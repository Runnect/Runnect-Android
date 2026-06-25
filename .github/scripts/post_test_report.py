#!/usr/bin/env python3
"""Parse JUnit XML test results and print a markdown report to stdout."""
import glob
import sys
import xml.etree.ElementTree as ET


def main() -> None:
    title = sys.argv[1]
    patterns = sys.argv[2:]

    paths = []
    for pattern in patterns:
        paths.extend(sorted(glob.glob(pattern, recursive=True)))

    if not paths:
        print(f"## {title}\n\n⚠️ 테스트 결과 파일을 찾을 수 없습니다.")
        return

    passed = failed = skipped = 0
    suite_lines = []

    for path in paths:
        root = ET.parse(path).getroot()
        suite_name = root.get("name", path)
        cases = root.findall("testcase")
        suite_lines.append(f"\n### {suite_name}")
        for case in cases:
            case_name = case.get("name")
            if case.find("failure") is not None or case.find("error") is not None:
                status = "❌"
                failed += 1
            elif case.find("skipped") is not None:
                status = "⏭️"
                skipped += 1
            else:
                status = "✅"
                passed += 1
            suite_lines.append(f"- {status} {case_name}")

    total = passed + failed + skipped
    badge = "✅" if failed == 0 else "❌"
    print(f"## {badge} {title}\n")
    print(f"**{total}개 중 {passed} 통과 / {failed} 실패 / {skipped} 스킵**")
    print("".join(line + "\n" for line in suite_lines))


if __name__ == "__main__":
    main()
