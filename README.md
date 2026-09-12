# jing-trang

Modernized build baseline for `jing-trang`.

[![CI](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/ci.yml)
[![CodeQL](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/codeql.yml)
[![OWASP Dependency-Check](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/dependency-check.yml)
[![SpotBugs + FindSecBugs](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/spotbugs-security.yml)
[![Coverage](https://github.com/jurgenei/jing-trang/actions/workflows/coverage.yml/badge.svg)](https://github.com/jurgenei/jing-trang/actions/workflows/coverage.yml)
[![codecov](https://codecov.io/gh/jurgenei/jing-trang/graph/badge.svg)](https://codecov.io/gh/jurgenei/jing-trang)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-025E8C?logo=dependabot)](https://github.com/jurgenei/jing-trang/network/updates)

## Build status

CI and security automation run in GitHub Actions:

- `CI`: `.github/workflows/ci.yml`
- `CodeQL`: `.github/workflows/codeql.yml`
- `OWASP Dependency-Check`: `.github/workflows/dependency-check.yml`
- `SpotBugs + FindSecBugs`: `.github/workflows/spotbugs-security.yml`
- `Coverage + Codecov`: `.github/workflows/coverage.yml`
- `Dependabot`: `.github/dependabot.yml`

## Requirements

- Git
- JDK 21+

## Project layout

- `util/`, `resolver/`, `datatype/`, `regex-gen/`, `regex/`, `xsd-datatype/`: migrated Gradle subprojects using standard layout (`src/main/java`, `src/main/resources`, `src/test/java`, `src/test/resources`).
- `samples/`: runnable sample assets and examples.
- `src/main/legacy/mod/`: remaining modules pending migration into top-level subprojects.

Legacy TestNG usage has been removed from migrated modules; tests run on JUnit Jupiter.

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

## Maven Central publishing (bundle flow)

`jing-trang` now supports same Central bundle flow used by `xml-sax-sexpr`:

```bash
./gradlew clean packageCentralBundle
```

Bundle output:

- `build/central-bundle/*-central-bundle.zip`

Release workflow:

- `.github/workflows/release.yml`

### Release checklist

1. Push branch so CI + Codecov recompute coverage.

```bash
git push origin <branch>
```

2. Configure GitHub repository secrets for Maven Central + signing.

- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `SIGNING_KEY`
- `SIGNING_PASSWORD`
- optional: `SIGNING_KEY_ID`

3. Publish release from tag (`v*`) or `workflow_dispatch`.

```bash
git tag v<version>
git push origin v<version>
```

Workflow builds signed bundle and uploads to Sonatype Central Publisher API (`nameSpace=org.relaxng`).

## Documentation and references

- `jing-trang` fork: [https://github.com/jurgenei/jing-trang](https://github.com/jurgenei/jing-trang)
- upstream `jing-trang`: [https://github.com/relaxng/jing-trang](https://github.com/relaxng/jing-trang)
- RELAX NG home: [https://relaxng.org/](https://relaxng.org/)
- RELAX NG compact tutorial/spec: [https://relaxng.org/compact-tutorial-20030326.html](https://relaxng.org/compact-tutorial-20030326.html)
- OASIS RELAX NG specification: [https://www.oasis-open.org/committees/relax-ng/spec-20011203.html](https://www.oasis-open.org/committees/relax-ng/spec-20011203.html)
- Jing docs (James Clark): [https://www.thaiopensource.com/relaxng/jing.html](https://www.thaiopensource.com/relaxng/jing.html)

## Notes

- This migration slice removes Travis usage and switches project automation to GitHub Actions.
- Full Ant/Maven/GCJ decommission and full module migration continue in following slices.

