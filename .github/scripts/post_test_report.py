#!/usr/bin/env python3
"""Parse JUnit XML test results and print failed tests to stdout.

Prints nothing when all tests pass. Prints only failed tests when failures exist,
so the caller can decide whether to post a PR comment based on whether stdout is non-empty.
"""
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
        return

    failed_by_class: dict[str, list[str]] = {}
    total = failed = 0

    for path in paths:
        root = ET.parse(path).getroot()
        for case in root.findall("testcase"):
            total += 1
            if case.find("failure") is not None or case.find("error") is not None:
                failed += 1
                classname = case.get("classname", root.get("name", path))
                failed_by_class.setdefault(classname, []).append(
                    f"- ❌ {case.get('name')}"
                )

    if failed == 0:
        return

    print(f"## ❌ {title} — {total}개 중 {failed}개 실패\n")
    for classname in sorted(failed_by_class):
        print(f"### {classname}")
        print("\n".join(failed_by_class[classname]))
        print()


if __name__ == "__main__":
    main()
