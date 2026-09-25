#!/bin/bash
# 러닝 화면 GPS 시뮬레이터 E2E 시나리오 (debug 빌드 전용)
#
# 사용법: 폰에서 러닝 화면(RunActivity)에 막 들어온 상태에서 실행 (목표 페이스는 '설정 안 함')
#   bash scripts/gps-sim-scenarios.sh [결과 저장 폴더]
#
# 앱의 러닝 서비스가 1초마다 남기는 logcat(RunSimState)으로 상태를, dumpsys로 진동/상단 알림 발생을 확인해
# TC별 PASS/FAIL을 판정하고, 판정 시점 스크린샷과 화면 녹화를 결과 폴더에 저장한다.
# 알림은 종류별 60초 쿨다운이 있어 TC 순서에 의존한다 — 순서를 바꾸면 기대값이 달라질 수 있다.

PKG=com.runnect.runnect
ADB=${ADB:-$(command -v adb || echo "$ANDROID_HOME/platform-tools/adb")}
[ -x "$ADB" ] || ADB="$HOME/Library/Android/sdk/platform-tools/adb"
OUT=${1:-./gps-sim-results/$(date +%Y%m%d-%H%M%S)}
mkdir -p "$OUT"

PASS=0; FAIL=0; RESULTS=()
REQUIRE_FOREGROUND=1 # 백그라운드 TC 구간에서는 0
REC_PART=0

cleanup() {
    "$ADB" shell svc power stayon false
    "$ADB" shell pkill -INT screenrecord 2>/dev/null
}

ensure_foreground() { # 러닝 화면을 벗어나면(앱 나감, 화면 꺼짐) 결과가 무의미하므로 FAIL이 아니라 중단으로 끝낸다
    [ "$REQUIRE_FOREGROUND" = 1 ] || return 0
    if ! "$ADB" shell dumpsys activity activities | grep -m1 topResumedActivity | grep -q "run.RunActivity"; then
        echo "[ABORTED] 러닝 화면을 벗어나 테스트를 중단합니다 (앱 이탈 또는 화면 꺼짐)"
        cleanup; exit 2
    fi
}

sim() { "$ADB" shell am broadcast -a com.runnect.debug.GPS_SIM -p $PKG --es cmd "$@" >/dev/null; }
# 러닝 화면 디버그 패널의 시나리오 버튼과 같은 상황을 만든다(RunSimScenario)
scenario() { sim scenario --es id "$1"; }
latest_state() { "$ADB" logcat -d -s RunSimState:I | grep RunSimState | tail -1 | sed 's/.*RunSimState: //'; }
state_field() { latest_state | tr ' ' '\n' | grep "^$1=" | cut -d= -f2; }
last_vibration() { "$ADB" shell dumpsys vibrator_manager | grep "$PKG" | awk '{print $1, $2}' | sort | tail -1; }
last_vibration_ms() { "$ADB" shell dumpsys vibrator_manager | grep "$PKG" | sort | tail -1 | sed -E 's/.*duration: *([0-9]+)ms.*/\1/'; }
# 현재 떠 있는 알림만 본다 — dumpsys에는 지워진 알림 보관함(mArchive)과 시스템 자동 그룹 요약(AUTOGROUP_SUMMARY)도 섞여 나온다.
has_alert_notification() {
    "$ADB" shell dumpsys notification --noredact | sed '/mArchive=/,$d' | grep "channel=run_alert" | grep -vq "AUTOGROUP_SUMMARY"
}
shot() { "$ADB" shell screencap -p /sdcard/tc.png && "$ADB" pull -q /sdcard/tc.png "$OUT/$1.png"; }

start_recording() { # screenrecord는 최대 180초라 구간별로 나눠 녹화한다
    "$ADB" shell pkill -INT screenrecord 2>/dev/null; sleep 1
    REC_PART=$((REC_PART+1))
    "$ADB" shell screenrecord --time-limit 180 /sdcard/tc_part$REC_PART.mp4 &
}

record() { # $1=TC id, $2=설명, $3=PASS|FAIL, $4=근거
    RESULTS+=("| $1 | $2 | $3 | \`$4\` |")
    if [ "$3" = PASS ]; then PASS=$((PASS+1)); else FAIL=$((FAIL+1)); fi
    echo "[$3] $1 $2 — $4"
    shot "$1"
}

# 판정은 반복 횟수가 아니라 실제 경과 시간(초) 기준 — adb 호출 자체에 시간이 걸려 횟수로 세면 대기 시간이 늘어난다.

expect_within() { # $1=초 $2=패턴 $3=TC id $4=설명 — 제한 시간 안에 최신 상태가 패턴과 일치하면 PASS
    local state start=$SECONDS
    while [ $((SECONDS - start)) -lt "$1" ]; do
        sleep 1; ensure_foreground; state=$(latest_state)
        if echo "$state" | grep -Eq "$2"; then record "$3" "$4" PASS "$state"; return; fi
    done
    record "$3" "$4" FAIL "$state"
}

expect_never() { # $1=초 $2=패턴 $3=TC id $4=설명 — 지정 시간 동안 한 번이라도 패턴과 일치하면 FAIL
    local state start=$SECONDS
    while [ $((SECONDS - start)) -lt "$1" ]; do
        sleep 1; ensure_foreground; state=$(latest_state)
        if echo "$state" | grep -Eq "$2"; then record "$3" "$4" FAIL "$state"; return; fi
    done
    record "$3" "$4" PASS "$state"
}

expect_after() { # $1=since $2=min초 $3=max초 $4=패턴 $5=TC id $6=설명 — 처음 나타난 시점이 min~max초 사이면 PASS
    local state elapsed
    while [ $((SECONDS - $1)) -le "$3" ]; do
        sleep 1; ensure_foreground; state=$(latest_state)
        if echo "$state" | grep -Eq "$4"; then
            elapsed=$((SECONDS - $1))
            if [ "$elapsed" -ge "$2" ]; then record "$5" "$6" PASS "${elapsed}초 후: $state"
            else record "$5" "$6" FAIL "${elapsed}초 만에 발생(너무 빠름): $state"; fi
            return
        fi
    done
    record "$5" "$6" FAIL "$3초 안에 발생 안 함: $state"
}

expect_vibrated_since() { # $1=기준 진동 기록 $2=최소ms $3=최대ms $4=TC id $5=설명
    local now_vib ms
    now_vib=$(last_vibration); ms=$(last_vibration_ms)
    if [ -n "$now_vib" ] && [ "$now_vib" != "$1" ] && [ "$ms" -ge "$2" ] && [ "$ms" -le "$3" ]; then
        record "$4" "$5" PASS "진동 $now_vib (${ms}ms)"
    else
        record "$4" "$5" FAIL "마지막 진동: ${now_vib:-없음} (${ms:-?}ms)"
    fi
}

top=$("$ADB" shell dumpsys activity activities | grep -m1 topResumedActivity)
if ! echo "$top" | grep -q "run.RunActivity"; then
    echo "러닝 화면(RunActivity)을 띄운 상태에서 실행하세요. 현재: $top"; exit 1
fi

"$ADB" shell appops set $PKG android:mock_location allow
"$ADB" shell pm grant $PKG android.permission.POST_NOTIFICATIONS 2>/dev/null # 상단 알림 TC용
"$ADB" shell svc power stayon true # 테스트 중 화면이 꺼지면 결과가 틀어지지 않게
"$ADB" logcat -c
start_recording

# 준비: 출발점에서 보통 속도, 목표 페이스 없음, 코스 위
sim resume; sim target --ei sec 0; sim rewind; scenario normal
sleep 5 # 가짜 위치로 기준점이 옮겨질 시간

# ── 포그라운드: 정상 주행 / 페이스 ──
expect_within 30 "pace=[0-9]+ alert=NONE paused=false" TC-01 "코스 위 정상 주행 시 거리/페이스 표시, 알림 없음"
expect_never  15 "alert=(OFF_ROUTE|PACE_DROP)"          TC-02 "코스 위 주행 중 오탐 알림 없음"

scenario faster            # 목표 5'30", 실제 약 4'46"
expect_never  15 "alert=PACE_DROP" TC-03 "목표보다 빠르면 페이스 저하 알림 없음"

scenario slightly_slower   # 약 5'57" — 목표보다 느리지만 20% 미만(6'36" 기준)
expect_never  20 "alert=PACE_DROP" TC-04 "목표보다 20% 미만으로 느리면 알림 없음"

VIB=$(last_vibration)
scenario pace_drop         # 목표 5'00", 실제 약 7'35" — 20% 넘게 느림
expect_within 35 "alert=PACE_DROP" TC-05 "목표보다 20% 넘게 10초 이상 느리면 페이스 저하 알림"
sleep 2; expect_vibrated_since "$VIB" 400 700 TC-06 "페이스 저하 알림 시 길게 한 번(500ms) 진동"

scenario pace_recover
expect_within 25 "alert=NONE" TC-07 "페이스를 회복하면 페이스 저하 알림 사라짐"
sim target --ei sec 0; scenario normal

# ── 포그라운드: 코스 이탈 ──
scenario short_detour      # 6초만 벗어났다 돌아옴 — 이탈 상태가 8초를 못 채움
expect_never  15 "alert=OFF_ROUTE" TC-08 "잠깐(8초 미만) 벗어났다 돌아오면 이탈 알림 없음"

start_recording
VIB=$(last_vibration)
scenario off_route
expect_within 20 "alert=OFF_ROUTE" TC-09 "코스에서 30m 넘게 8초 이상 벗어나면 이탈 알림"
OFF_ROUTE_AT=$SECONDS
sleep 2; expect_vibrated_since "$VIB" 650 900 TC-10 "이탈 알림 시 짧게 두 번(300+150+300ms) 진동"

scenario back_on_route
expect_within 10 "alert=NONE" TC-11 "코스로 돌아오면 이탈 알림 사라짐"

# ── 포그라운드: 자동 일시정지 ──
scenario halt; HALT_AT=$SECONDS
# 기준 20초. 로그가 1초 간격이라 판정 오차를 감안해 18~25초 사이면 정상으로 본다.
expect_after $HALT_AT 18 25 "paused=true" TC-12 "제자리에 멈추면 약 20초 뒤 자동 일시정지(그 전엔 안 걸림)"

scenario resume
expect_within 5  "paused=false" TC-13 "재개하면 일시정지 해제"
expect_never  20 "paused=true"  TC-14 "재개 후 달리는 동안 다시 자동 일시정지 안 됨"

# ── 백그라운드(홈으로 나감): 화면을 안 보고 뛰는 상황 ──
start_recording
REQUIRE_FOREGROUND=0
"$ADB" shell input keyevent KEYCODE_HOME; sleep 2
DIST_BEFORE=$(state_field distM) # 화면 표시값(0.1km 단위)은 15초 이동량(약 40m)을 못 보여줘서 미터로 비교
sleep 15
DIST_AFTER=$(state_field distM)
if [ "$DIST_AFTER" -gt $((DIST_BEFORE + 20)) ]; then
    record TC-15 "백그라운드에서도 거리가 계속 쌓임" PASS "${DIST_BEFORE}m → ${DIST_AFTER}m"
else
    record TC-15 "백그라운드에서도 거리가 계속 쌓임" FAIL "${DIST_BEFORE}m → ${DIST_AFTER}m"
fi

# 이탈 알림 60초 쿨다운이 끝난 뒤에 백그라운드 이탈을 검증한다
WAIT=$((60 - (SECONDS - OFF_ROUTE_AT)))
[ "$WAIT" -gt 0 ] && sleep "$WAIT"
VIB=$(last_vibration)
scenario off_route
expect_within 20 "alert=OFF_ROUTE" TC-16 "백그라운드에서도 이탈 알림 판정"
sleep 2
expect_vibrated_since "$VIB" 650 900 TC-17 "백그라운드에서도 이탈 진동"
if has_alert_notification; then record TC-18 "백그라운드면 상단 알림(헤드업)으로 표시" PASS "run_alert 알림 게시됨"
else record TC-18 "백그라운드면 상단 알림(헤드업)으로 표시" FAIL "run_alert 알림 없음"; fi

scenario back_on_route
expect_within 10 "alert=NONE" TC-19 "백그라운드에서 코스 복귀 시 알림 해제"
sleep 2
if has_alert_notification; then record TC-20 "알림 해제 시 상단 알림도 제거" FAIL "run_alert 알림이 남아 있음"
else record TC-20 "알림 해제 시 상단 알림도 제거" PASS "run_alert 알림 없음"; fi

# ── 앱으로 복귀: 기록이 그대로 이어지는지 ──
DIST_BG=$(state_field distM)
"$ADB" shell monkey -p $PKG -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1; sleep 3
REQUIRE_FOREGROUND=1
DIST_FG=$(state_field distM)
if "$ADB" shell dumpsys activity activities | grep -m1 topResumedActivity | grep -q "run.RunActivity" &&
    [ "$DIST_FG" -ge "$DIST_BG" ]; then
    record TC-21 "앱으로 돌아오면 러닝 화면과 기록이 그대로 이어짐" PASS "${DIST_BG}m → ${DIST_FG}m"
else
    record TC-21 "앱으로 돌아오면 러닝 화면과 기록이 그대로 이어짐" FAIL "${DIST_BG}m → ${DIST_FG}m"
fi

sim stop
cleanup
sleep 2
for i in $(seq $REC_PART); do "$ADB" pull -q /sdcard/tc_part$i.mp4 "$OUT/tc_part$i.mp4"; done
"$ADB" logcat -d -s RunSimState:I > "$OUT/run_sim_state.log"

{
    echo "| TC | 시나리오 | 결과 | 판정 근거 |"
    echo "| --- | --- | --- | --- |"
    printf '%s\n' "${RESULTS[@]}"
    echo
    echo "PASS $PASS / FAIL $FAIL"
} > "$OUT/result.md"
cat "$OUT/result.md"
[ "$FAIL" -eq 0 ]
