package com.runnect.runnect.util.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.runnect.runnect.BuildConfig
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * 마커 충전용 리워드 광고 로드/노출을 감싼다. 광고 시청 완료(onUserEarnedReward)는
 * AdMob이 정확히 한 번만 호출해주지만, 그 뒤 서버로 지급 요청을 보내는 과정에서
 * 네트워크 재시도가 붙을 수 있으므로 이 시청 1건을 식별하는 rewardTransactionId를
 * 여기서 발급해 호출부에 넘긴다 — 서버는 이 값으로 중복 지급을 막는다.
 */
class RewardedAdManager @Inject constructor() {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun load(context: Context) {
        if (isLoading || rewardedAd != null) return
        isLoading = true
        RewardedAd.load(
            context,
            BuildConfig.ADMOB_REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isLoading = false
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    rewardedAd = null
                    Timber.e("리워드 광고 로드 실패: ${error.message}")
                }
            }
        )
    }

    fun isReady(): Boolean = rewardedAd != null

    fun show(
        activity: Activity,
        onUserEarnedReward: (rewardTransactionId: String) -> Unit,
        onNotReady: () -> Unit = {},
    ) {
        val ad = rewardedAd
        if (ad == null) {
            onNotReady()
            load(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                load(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                Timber.e("리워드 광고 노출 실패: ${adError.message}")
                load(activity)
            }
        }

        ad.show(activity) {
            onUserEarnedReward(UUID.randomUUID().toString())
        }
    }
}
