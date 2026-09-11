# jing-trang

Modernized build baseline for `jing-trang`.

[![CI](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml)
[![CodeQL](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml)
[![OWASP Dependency-Check](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml)
[![SpotBugs + FindSecBugs](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml)

## Build status

CI and security automation run in GitHub Actions:

- `CI`: `.github/workflows/ci.yml`
- `CodeQL`: `.github/workflows/codeql.yml`
- `OWASP Dependency-Check`: `.github/workflows/dependency-check.yml`
- `SpotBugs + FindSecBugs`: `.github/workflows/spotbugs-security.yml`
- `Dependabot`: `.github/dependabot.yml`

## Requirements

- Git
- JDK 21+

## Build

```bash
./gradlew clean build
```

## Test

```bash
./gradlew test
```

## Security scans

```bash
./gradlew spotbugsMain spotbugsTest
```

## Release commands

`release.py` functionality moved into Gradle tasks:

```bash
./gradlew release
./gradlew releaseBuild
./gradlew publishRelease
./gradlew releaseSnapshot
```

Optional explicit snapshot flag:

```bash
./gradlew publish -Psnapshot=true
```

## Notes

- This migration slice removes Travis usage and switches project automation to GitHub Actions.
- Full Ant/Maven/GCJ decommission and full module migration continue in following slices.

