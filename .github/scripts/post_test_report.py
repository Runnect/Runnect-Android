#!/usr/bin/env python3
"""Parse JUnit XML test results and print a markdown report to stdout.

Each test line gets a stable anchor (<a id="t-{hash}">) so that other
documents (e.g. the PR description's verification matrix) can link
directly to a specific test's latest result.
"""
import glob
import hashlib
import sys
import xml.etree.ElementTree as ET


def anchor_id(classname: str, case_name: str) -> str:
    digest = hashlib.md5(f"{classname}::{case_name}".encode()).hexdigest()[:10]
    return f"t-{digest}"


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
    by_class: dict[str, list[str]] = {}

    for path in paths:
        root = ET.parse(path).getroot()
        for case in root.findall("testcase"):
            case_name = case.get("name")
            classname = case.get("classname", root.get("name", path))
            if case.find("failure") is not None or case.find("error") is not None:
                status = "❌"
                failed += 1
            elif case.find("skipped") is not None:
                status = "⏭️"
                skipped += 1
            else:
                status = "✅"
                passed += 1
            aid = anchor_id(classname, case_name)
            by_class.setdefault(classname, []).append(
                f'- <a id="{aid}"></a>{status} {case_name}'
            )

    total = passed + failed + skipped
    badge = "✅" if failed == 0 else "❌"
    print(f"## {badge} {title}\n")
    print(f"**{total}개 중 {passed} 통과 / {failed} 실패 / {skipped} 스킵**")
    for classname in sorted(by_class):
        print(f"\n### {classname}")
        print("\n".join(by_class[classname]))


if __name__ == "__main__":
    main()
