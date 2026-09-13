# jing-trang

Modernized build baseline for `jing-trang`.

[![Build](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml)
[![Release](https://github.com/jurgenei/jing-trang/actions/workflows/release.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/release.yml)
[![Coverage CI](https://github.com/jurgenei/jing-trang/actions/workflows/coverage.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/coverage.yml)
[![CodeQL](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml)
[![Dependency Check](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml)
[![SpotBugs Security](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-025E8C?logo=dependabot)](https://github.com/jurgenei/jing-trang/security/dependabot)
[![Coverage](https://codecov.io/gh/jurgenei/jing-trang/graph/badge.svg?branch=main)](https://codecov.io/gh/jurgenei/jing-trang?branch=main)
[![Maven Central](https://img.shields.io/maven-central/v/name.jurgenei/jing-trang.svg)](https://search.maven.org/artifact/name.jurgenei/jing-trang/20260913/jar)
[![License](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg)](LICENCE.md)
[![Java](https://img.shields.io/badge/java-21+-green.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-9.5+-blue.svg)](https://gradle.org/)

## Requirements

- Git
- JDK 21+

## Project layout

- `util/`, `resolver/`, `datatype/`, `regex-gen/`, `regex/`, `xsd-datatype/`: migrated Gradle subprojects
- `jing/`: validator artifact packaging (`name.jurgenei:jing`)
- `trang/`: converter artifact packaging (`name.jurgenei:trang`)
- `src/main/legacy/mod/`: remaining legacy module sources used by split artifacts

## Maven coordinates

Current release version is `20260913`.

- Aggregator: `name.jurgenei:jing-trang:20260913`
- Validator: `name.jurgenei:jing:20260913`
- Converter: `name.jurgenei:trang:20260913`

All three artifacts are staged into one Maven Central bundle.

## Build and test

```bash
./gradlew clean build
./gradlew test
```

## Maven Central publishing

```bash
./gradlew clean packageCentralBundle
```

Bundle output:

- `build/central-bundle/*-central-bundle.zip`

Release workflow:

- `.github/workflows/release.yml`

Workflow uploads bundle to Sonatype Central Publisher API with namespace `name.jurgenei`.

## Documentation and references

- `jing-trang` fork: [https://github.com/jurgenei/jing-trang](https://github.com/jurgenei/jing-trang)
- upstream `jing-trang`: [https://github.com/relaxng/jing-trang](https://github.com/relaxng/jing-trang)
- RELAX NG home: [https://relaxng.org/](https://relaxng.org/)
- RELAX NG compact tutorial/spec: [https://relaxng.org/compact-tutorial-20030326.html](https://relaxng.org/compact-tutorial-20030326.html)
- OASIS RELAX NG specification: [https://www.oasis-open.org/committees/relax-ng/spec-20011203.html](https://www.oasis-open.org/committees/relax-ng/spec-20011203.html)


