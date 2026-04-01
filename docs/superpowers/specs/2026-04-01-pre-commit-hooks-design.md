# Pre-Commit Hooks Design

**Date:** 2026-04-01
**Status:** Approved

## Goal

Add automated code quality enforcement to the Chirp KMP project via pre-commit hooks. Every commit must pass:
- Kotlin formatting (ktlint) and static analysis (detekt)
- TOML formatting (taplo) for `libs.versions.toml`
- Swift linting (SwiftLint) for the iOS wrapper code
- Conventional Commits format on commit messages

Config is version-controlled — no hook manager required (solo project).

---

## Hook Delivery: Plain `.githooks/`

Git supports a custom hooks directory via `git config core.hooksPath .githooks`. Scripts in `.githooks/` are committed to the repo, so hooks are version-controlled with zero external hook-manager dependencies.

**One-time developer setup:**
```bash
./gradlew installGitHooks
```

This Gradle task runs `git config core.hooksPath .githooks` and ensures all hook scripts are executable.

Two hook scripts:

| Script | Trigger | Checks |
|---|---|---|
| `.githooks/pre-commit` | `git commit` | ktlintCheck, detekt, taplo, swiftlint |
| `.githooks/commit-msg` | After message typed | Conventional Commits regex |

---

## Checks in `pre-commit`

### 1. ktlint (Kotlin Formatting)

**Plugin:** `org.jlleitschuh.gradle.ktlint` (version `12.3.0`)

Applied inside `configureKotlinMultiplatform()` in `KotlinMultiplatform.kt` — covers all KMP modules automatically.
Also applied inside `configureKotlinAndroid()` in `KotlinAndroid.kt` for the Android app module.

Tasks added per module:
- `ktlintCheck` — fails on formatting violations (used in hook)
- `ktlintFormat` — auto-fixes formatting in place (developer use)

**Config:** `.editorconfig` at project root.
```ini
[*.{kt,kts}]
indent_size = 4
max_line_length = 120
ktlint_standard_no-wildcard-imports = enabled
ktlint_standard_trailing-comma-on-call-site = enabled
ktlint_standard_trailing-comma-on-declaration-site = enabled
```

---

### 2. detekt (Kotlin Static Analysis)

**Plugin:** `io.gitlab.arturbosch.detekt` (version `1.23.8`)

Applied inside `configureKotlinMultiplatform()` alongside ktlint.

**Config:** `config/detekt/detekt.yml` at project root. Key rules:
- **Complexity:** `LongMethod` (threshold 60), `CyclomaticComplexMethod` (threshold 15), `LargeClass` (threshold 600), `TooManyFunctions`
- **Style:** `MagicNumber`, `UnusedPrivateMember`, `ForbiddenComment` (blocks `TODO:` / `FIXME:` in committed code)
- **Potential bugs:** `UnreachableCode`, `LateinitUsage`, `UnsafeCallOnNullableType`
- **Performance:** `ArrayPrimitive`, `ForEachOnRange`

**Baseline:** `./gradlew detektBaseline` is run once after integration to generate `config/detekt/baseline.xml`, snapshotting pre-existing violations so only new code is gated.

---

### 3. Taplo (TOML Formatting)

**Tool:** [taplo](https://taplo.tamasfe.dev/) — fast TOML formatter/linter, installed via `brew install taplo`.

**Scope:** `gradle/libs.versions.toml` only — the single most-edited non-Kotlin file in the project.

**Hook command:**
```bash
taplo fmt --check gradle/libs.versions.toml
```

**Fix command (developer use):**
```bash
taplo fmt gradle/libs.versions.toml
```

**Config:** `taplo.toml` at project root with sensible defaults (column width 120, align values in tables).

If `taplo` is not installed, the hook prints an install hint and exits 1 (blocks commit).

---

### 4. SwiftLint (Swift Linting)

**Tool:** [SwiftLint](https://github.com/realm/SwiftLint) — standard Swift linter, installed via `brew install swiftlint`.

**Scope:** `iosApp/` directory (2 files today: `iOSApp.swift`, `ContentView.swift`).

**Hook command:**
```bash
swiftlint lint --path iosApp/ --quiet
```

**Config:** `.swiftlint.yml` at project root. Minimal ruleset appropriate for thin iOS wrapper files:
```yaml
disabled_rules:
  - trailing_whitespace
opt_in_rules:
  - empty_count
  - closure_spacing
included:
  - iosApp
excluded:
  - iosApp/iosApp.xcodeproj
line_length: 120
```

If `swiftlint` is not installed, the hook prints an install hint (`brew install swiftlint`) and exits 1 (blocks commit).

---

## Commit Message: `commit-msg`

Validated via a shell regex. No extra tooling required.

**Pattern:**
```
^(feat|fix|docs|style|refactor|test|chore|build|ci|perf|revert)(\(.+\))?: .{1,}$
```

**Valid examples:**
```
feat(auth): add login screen
fix(chat): resolve message ordering bug
chore: update dependencies
```

On violation the script exits 1 with a message showing the expected format.

---

## Files Changed / Created

| File | Action | Purpose |
|---|---|---|
| `.githooks/pre-commit` | Create | Runs ktlintCheck, detekt, taplo, swiftlint |
| `.githooks/commit-msg` | Create | Validates Conventional Commits format |
| `.editorconfig` | Create | ktlint formatting rules |
| `.swiftlint.yml` | Create | SwiftLint rules for iosApp/ |
| `taplo.toml` | Create | Taplo formatting config |
| `config/detekt/detekt.yml` | Create | detekt rule configuration |
| `gradle/libs.versions.toml` | Edit | Add ktlint-gradle and detekt versions + plugin entries |
| `build-logic/convention/build.gradle.kts` | Edit | Add ktlint-gradle + detekt as classpath dependencies; register `installGitHooks` task |
| `build-logic/convention/src/main/kotlin/.../KotlinMultiplatform.kt` | Edit | Apply ktlint + detekt to all KMP modules |
| `build-logic/convention/src/main/kotlin/.../KotlinAndroid.kt` | Edit | Apply ktlint + detekt to Android app module |
| `CLAUDE.md` | Edit | Add setup instructions |

---

## Developer Setup (after this change)

```bash
# Prerequisites (one-time, macOS)
brew install swiftlint taplo

# Install git hooks (one-time per clone)
./gradlew installGitHooks

# Auto-fix Kotlin formatting before committing
./gradlew ktlintFormat

# Auto-fix TOML formatting before committing
taplo fmt gradle/libs.versions.toml

# Generate detekt baseline (run once after first integration)
./gradlew detektBaseline
```

---

## Dependency Versions

| Tool | Version |
|---|---|
| ktlint-gradle plugin | `12.3.0` |
| detekt plugin | `1.23.8` |
| taplo | latest via brew |
| SwiftLint | latest via brew |
