# Pre-Commit Hooks Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Wire ktlint, detekt, taplo, and SwiftLint into the build system and enforce them via plain `.githooks/` scripts with no external hook manager.

**Architecture:** ktlint and detekt are added as Gradle plugins via the existing convention plugin system (`KotlinMultiplatform.kt` covers all KMP library modules; `CmpApplicationConventionPlugin` covers `composeApp`). Taplo and SwiftLint are standalone binaries invoked directly in the pre-commit shell script. Git hooks live in `.githooks/` (version-controlled) and are activated by a `installGitHooks` Gradle task that runs `git config core.hooksPath .githooks`.

**Tech Stack:** ktlint-gradle `12.3.0`, detekt `1.23.8`, taplo (brew), SwiftLint (brew), plain POSIX shell hooks.

---

## File Map

| File | Action | Responsibility |
|---|---|---|
| `gradle/libs.versions.toml` | Modify | Add ktlint-gradle + detekt versions, libraries, plugins |
| `build.gradle.kts` | Modify | Declare ktlint + detekt plugins `apply false`; add `installGitHooks` task |
| `build-logic/convention/build.gradle.kts` | Modify | Add ktlint-gradle + detekt as `compileOnly` classpath deps |
| `build-logic/convention/src/main/kotlin/com/ruialves/chirp/convention/KotlinMultiplatform.kt` | Modify | Apply + configure ktlint and detekt for all KMP library modules |
| `build-logic/convention/src/main/kotlin/CmpApplicationConventionPlugin.kt` | Modify | Apply + configure ktlint and detekt for `composeApp` |
| `.editorconfig` | Create | ktlint formatting rules |
| `config/detekt/detekt.yml` | Create | detekt rule overrides (builds on default config) |
| `.githooks/pre-commit` | Create | Shell script: ktlintCheck, detekt, taplo, swiftlint |
| `.githooks/commit-msg` | Create | Shell script: Conventional Commits regex |
| `.swiftlint.yml` | Create | SwiftLint rules for `iosApp/` |
| `taplo.toml` | Create | Taplo formatting config |
| `CLAUDE.md` | Modify | Add setup instructions |

---

## Task 1: Wire ktlint into the build system

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `build-logic/convention/build.gradle.kts`
- Modify: `build-logic/convention/src/main/kotlin/com/ruialves/chirp/convention/KotlinMultiplatform.kt`
- Modify: `build-logic/convention/src/main/kotlin/CmpApplicationConventionPlugin.kt`
- Create: `.editorconfig`

- [ ] **Step 1: Add ktlint-gradle version, library, and plugin to `gradle/libs.versions.toml`**

In the `[versions]` section, after the `# Third party` block add:
```toml
# Code quality
ktlint-gradle = "12.3.0"
```

In the `[libraries]` section, before the `[plugins]` section add:
```toml
ktlint-gradlePlugin = { group = "org.jlleitschuh.gradle", name = "ktlint-gradle", version.ref = "ktlint-gradle" }
```

In the `[plugins]` section, after `android-lint`:
```toml
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlint-gradle" }
```

- [ ] **Step 2: Declare ktlint plugin with `apply false` in root `build.gradle.kts`**

Add to the `plugins { }` block (after `android-lint`):
```kotlin
alias(libs.plugins.ktlint) apply false
```

- [ ] **Step 3: Add ktlint-gradle to the build-logic classpath in `build-logic/convention/build.gradle.kts`**

Add to the `dependencies { }` block:
```kotlin
compileOnly(libs.ktlint.gradlePlugin)
```

- [ ] **Step 4: Apply ktlint in `KotlinMultiplatform.kt`**

The full file after changes:
```kotlin
package com.ruialves.chirp.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform() {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    extensions.configure<LibraryExtension> {
        namespace = this@configureKotlinMultiplatform.pathToPackageName()
    }

    configureAndroidTarget()

    extensions.configure<KotlinMultiplatformExtension> {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = this@configureKotlinMultiplatform.pathToFrameworkName()
            }
        }
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }
}
```

- [ ] **Step 5: Apply ktlint in `CmpApplicationConventionPlugin.kt`**

The full file after changes:
```kotlin
import com.ruialves.chirp.convention.configureAndroidTarget
import com.ruialves.chirp.convention.configureIosTarget
import com.ruialves.chirp.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class CmpApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.ruialves.convention.android.application.compose")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jlleitschuh.gradle.ktlint")
            }

            configureAndroidTarget()
            configureIosTarget()

            dependencies {
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}
```

- [ ] **Step 6: Create `.editorconfig` at the project root**

```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.{kt,kts}]
indent_style = space
indent_size = 4
max_line_length = 120
ktlint_standard_no-wildcard-imports = enabled
ktlint_standard_trailing-comma-on-call-site = enabled
ktlint_standard_trailing-comma-on-declaration-site = enabled
```

- [ ] **Step 7: Verify ktlint resolves**

Run:
```bash
./gradlew ktlintCheck --daemon 2>&1 | head -30
```

Expected: either `BUILD SUCCESSFUL` or specific formatting errors (not `Could not resolve org.jlleitschuh.gradle`).

If you see resolution errors, double-check that `gradlePluginPortal()` is present in `settings.gradle.kts` `pluginManagement.repositories` — it already is.

- [ ] **Step 8: Auto-fix all existing violations**

Run:
```bash
./gradlew ktlintFormat --daemon
```

Expected: `BUILD SUCCESSFUL`. This rewrites any files that were improperly formatted.

- [ ] **Step 9: Confirm clean check**

Run:
```bash
./gradlew ktlintCheck --daemon
```

Expected: `BUILD SUCCESSFUL` with no violations.

- [ ] **Step 10: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts build-logic/convention/build.gradle.kts \
  build-logic/convention/src/main/kotlin/com/ruialves/chirp/convention/KotlinMultiplatform.kt \
  build-logic/convention/src/main/kotlin/CmpApplicationConventionPlugin.kt \
  .editorconfig
git commit -m "build: add ktlint to all modules via convention plugins"
```

---

## Task 2: Wire detekt into the build system

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `build-logic/convention/build.gradle.kts`
- Modify: `build-logic/convention/src/main/kotlin/com/ruialves/chirp/convention/KotlinMultiplatform.kt`
- Modify: `build-logic/convention/src/main/kotlin/CmpApplicationConventionPlugin.kt`
- Create: `config/detekt/detekt.yml`

- [ ] **Step 1: Add detekt version, library, and plugin to `gradle/libs.versions.toml`**

In the `[versions]` section, after `ktlint-gradle`:
```toml
detekt = "1.23.8"
```

In the `[libraries]` section, after `ktlint-gradlePlugin`:
```toml
detekt-gradlePlugin = { group = "io.gitlab.arturbosch.detekt", name = "detekt-gradle-plugin", version.ref = "detekt" }
```

In the `[plugins]` section, after `ktlint`:
```toml
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
```

- [ ] **Step 2: Declare detekt plugin with `apply false` in root `build.gradle.kts`**

Add to the `plugins { }` block (after `ktlint`):
```kotlin
alias(libs.plugins.detekt) apply false
```

- [ ] **Step 3: Add detekt-gradle-plugin to build-logic classpath**

In `build-logic/convention/build.gradle.kts`, add to `dependencies { }`:
```kotlin
compileOnly(libs.detekt.gradlePlugin)
```

- [ ] **Step 4: Apply and configure detekt in `KotlinMultiplatform.kt`**

The full file after changes:
```kotlin
package com.ruialves.chirp.convention

import com.android.build.gradle.LibraryExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform() {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
        baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
        buildUponDefaultConfig = true
    }

    extensions.configure<LibraryExtension> {
        namespace = this@configureKotlinMultiplatform.pathToPackageName()
    }

    configureAndroidTarget()

    extensions.configure<KotlinMultiplatformExtension> {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = this@configureKotlinMultiplatform.pathToFrameworkName()
            }
        }
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }
}
```

- [ ] **Step 5: Apply and configure detekt in `CmpApplicationConventionPlugin.kt`**

The full file after changes:
```kotlin
import com.ruialves.chirp.convention.configureAndroidTarget
import com.ruialves.chirp.convention.configureIosTarget
import com.ruialves.chirp.convention.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class CmpApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.ruialves.convention.android.application.compose")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jlleitschuh.gradle.ktlint")
                apply("io.gitlab.arturbosch.detekt")
            }

            extensions.configure<DetektExtension> {
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
                baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
                buildUponDefaultConfig = true
            }

            configureAndroidTarget()
            configureIosTarget()

            dependencies {
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}
```

- [ ] **Step 6: Create `config/detekt/detekt.yml`**

```bash
mkdir -p config/detekt
```

Create `config/detekt/detekt.yml`:
```yaml
# Builds on top of detekt's default config.
# Only rules that deviate from defaults are listed here.

complexity:
  LongMethod:
    threshold: 60
  CyclomaticComplexMethod:
    threshold: 15
  LargeClass:
    threshold: 600
  TooManyFunctions:
    thresholdInFiles: 20
    thresholdInClasses: 15
    thresholdInInterfaces: 10
    thresholdInObjects: 15
    thresholdInEnums: 5

style:
  MagicNumber:
    active: true
    ignoreNumbers:
      - '-1'
      - '0'
      - '1'
      - '2'
    ignoreAnnotated:
      - 'Preview'
      - 'Composable'
  UnusedPrivateMember:
    active: true
  ForbiddenComment:
    active: true
    values:
      - reason: 'Use a tracked issue instead of a FIXME comment.'
        value: 'FIXME:'
  MaxLineLength:
    maxLineLength: 120
    excludeImportStatements: true
    excludeCommentStatements: true

potential-bugs:
  UnreachableCode:
    active: true
  LateinitUsage:
    active: true
    excludeAnnotatedProperties:
      - 'Inject'

performance:
  ArrayPrimitive:
    active: true
  ForEachOnRange:
    active: true

# Compose — relax rules that conflict with Compose conventions
naming:
  FunctionNaming:
    functionPattern: '[a-zA-Z][a-zA-Z0-9]*'
    excludes:
      - '**/commonMain/**'
      - '**/androidMain/**'
      - '**/iosMain/**'
```

- [ ] **Step 7: Verify detekt resolves and runs**

Run:
```bash
./gradlew detekt --daemon 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL` or a list of specific rule violations (not classpath errors).

- [ ] **Step 8: Generate baseline to suppress pre-existing violations**

Run:
```bash
./gradlew detektBaseline --daemon
```

Expected: `BUILD SUCCESSFUL`. This creates `config/detekt/baseline.xml` for each module. After this, `./gradlew detekt` should pass on the current codebase.

- [ ] **Step 9: Confirm clean detekt run**

Run:
```bash
./gradlew detekt --daemon
```

Expected: `BUILD SUCCESSFUL` with no violations.

- [ ] **Step 10: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts build-logic/convention/build.gradle.kts \
  build-logic/convention/src/main/kotlin/com/ruialves/chirp/convention/KotlinMultiplatform.kt \
  build-logic/convention/src/main/kotlin/CmpApplicationConventionPlugin.kt \
  config/detekt/
git commit -m "build: add detekt to all modules via convention plugins"
```

---

## Task 3: Create git hooks and `installGitHooks` task

**Files:**
- Modify: `build.gradle.kts`
- Create: `.githooks/pre-commit`
- Create: `.githooks/commit-msg`

- [ ] **Step 1: Add `installGitHooks` task to root `build.gradle.kts`**

Append after the `plugins { }` block:
```kotlin
tasks.register("installGitHooks") {
    description = "Configures git to use .githooks/ and makes hook scripts executable."
    group = "setup"
    doLast {
        exec {
            commandLine("git", "config", "core.hooksPath", ".githooks")
        }
        fileTree(".githooks").forEach { it.setExecutable(true) }
        println("✅ Git hooks installed. Hooks will run from .githooks/")
    }
}
```

- [ ] **Step 2: Create `.githooks/` directory and `pre-commit` script**

```bash
mkdir -p .githooks
```

Create `.githooks/pre-commit`:
```bash
#!/bin/sh
set -e

# ── Dependency checks ──────────────────────────────────────────────────────────
if ! command -v taplo > /dev/null 2>&1; then
  echo "❌ taplo not found. Run: brew install taplo"
  exit 1
fi

if ! command -v swiftlint > /dev/null 2>&1; then
  echo "❌ swiftlint not found. Run: brew install swiftlint"
  exit 1
fi

# ── Kotlin ─────────────────────────────────────────────────────────────────────
echo "→ ktlintCheck..."
./gradlew ktlintCheck --daemon --quiet
echo "→ detekt..."
./gradlew detekt --daemon --quiet

# ── TOML ───────────────────────────────────────────────────────────────────────
echo "→ taplo (libs.versions.toml)..."
taplo fmt --check gradle/libs.versions.toml

# ── Swift ──────────────────────────────────────────────────────────────────────
echo "→ swiftlint..."
swiftlint lint --path iosApp/ --quiet

echo "✅ All checks passed."
```

- [ ] **Step 3: Create `.githooks/commit-msg` script**

Create `.githooks/commit-msg`:
```bash
#!/bin/sh

MSG=$(cat "$1")
PATTERN='^(feat|fix|docs|style|refactor|test|chore|build|ci|perf|revert)(\(.+\))?: .+'

if ! echo "$MSG" | grep -qE "$PATTERN"; then
  echo ""
  echo "❌ Commit message does not follow Conventional Commits format."
  echo ""
  echo "   Format : <type>(<scope>): <description>"
  echo "   Types  : feat|fix|docs|style|refactor|test|chore|build|ci|perf|revert"
  echo "   Scope  : optional, e.g. auth, chat, core"
  echo ""
  echo "   Examples:"
  echo "     feat(auth): add login screen"
  echo "     fix(chat): resolve message ordering bug"
  echo "     chore: update dependencies"
  echo ""
  exit 1
fi
```

- [ ] **Step 4: Install hooks**

Run:
```bash
./gradlew installGitHooks
```

Expected output:
```
✅ Git hooks installed. Hooks will run from .githooks/
BUILD SUCCESSFUL
```

- [ ] **Step 5: Verify hooks are executable**

Run:
```bash
ls -la .githooks/
```

Expected: both `pre-commit` and `commit-msg` have `-rwxr-xr-x` permissions.

- [ ] **Step 6: Smoke-test `commit-msg` hook**

Run:
```bash
echo "bad message" | .githooks/commit-msg /dev/stdin 2>&1 || true
```

Expected: prints the error message about Conventional Commits format and exits 1.

Run:
```bash
echo "feat(auth): add login" | .githooks/commit-msg /dev/stdin
```

Expected: exits 0 silently.

- [ ] **Step 7: Commit**

```bash
git add build.gradle.kts .githooks/
git commit -m "build: add git hooks and installGitHooks task"
```

---

## Task 4: Add Taplo config

**Files:**
- Create: `taplo.toml`

- [ ] **Step 1: Verify taplo is installed**

Run:
```bash
taplo --version
```

Expected: `taplo 0.x.x`. If not found, run `brew install taplo` first.

- [ ] **Step 2: Create `taplo.toml` at the project root**

```toml
[formatting]
column_width = 120
indent_string = "  "
trailing_newline = true
reorder_keys = false
allowed_blank_lines = 1
```

- [ ] **Step 3: Verify taplo passes on the current TOML file**

Run:
```bash
taplo fmt --check gradle/libs.versions.toml
```

Expected: `BUILD SUCCESSFUL` (exits 0, no output). If it reports formatting issues, run `taplo fmt gradle/libs.versions.toml` to fix them, then re-check.

- [ ] **Step 4: Commit**

```bash
git add taplo.toml gradle/libs.versions.toml
git commit -m "build: add taplo config for TOML formatting"
```

---

## Task 5: Add SwiftLint config

**Files:**
- Create: `.swiftlint.yml`

- [ ] **Step 1: Verify SwiftLint is installed**

Run:
```bash
swiftlint version
```

Expected: `0.x.x`. If not found, run `brew install swiftlint` first.

- [ ] **Step 2: Create `.swiftlint.yml` at the project root**

```yaml
disabled_rules:
  - trailing_whitespace     # handled by .editorconfig

opt_in_rules:
  - empty_count
  - closure_spacing
  - explicit_init

included:
  - iosApp

excluded:
  - iosApp/iosApp.xcodeproj

line_length:
  warning: 120
  error: 150

type_body_length:
  warning: 300
  error: 400

file_length:
  warning: 400
  error: 500
```

- [ ] **Step 3: Verify SwiftLint passes on the current iOS code**

Run:
```bash
swiftlint lint --path iosApp/ --quiet
```

Expected: exits 0 with no output (the two wrapper files are minimal and should pass cleanly).

If violations are reported, fix them in `iosApp/iosApp/iOSApp.swift` and `iosApp/iosApp/ContentView.swift`.

- [ ] **Step 4: Commit**

```bash
git add .swiftlint.yml
git commit -m "build: add SwiftLint config for iOS wrapper"
```

---

## Task 6: End-to-end verification and CLAUDE.md update

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: Run the full pre-commit hook manually**

Run:
```bash
.githooks/pre-commit
```

Expected:
```
→ ktlintCheck...
→ detekt...
→ taplo (libs.versions.toml)...
→ swiftlint...
✅ All checks passed.
```

- [ ] **Step 2: Test that a bad commit message is rejected**

Run:
```bash
git commit --allow-empty -m "wip stuff"
```

Expected: hook fires and prints the Conventional Commits error. Commit is blocked.

- [ ] **Step 3: Test that a good commit message is accepted**

Run:
```bash
git commit --allow-empty -m "chore: verify hooks end-to-end"
```

Expected: hooks pass, empty commit is created.

Clean up the test commit:
```bash
git reset HEAD~1
```

- [ ] **Step 4: Update `CLAUDE.md` — add setup section**

In the `## Build & Run` section, add below it:

```markdown
## Developer Setup (one-time)

```bash
# Install hook dependencies (macOS)
brew install swiftlint taplo

# Activate git hooks
./gradlew installGitHooks

# Auto-fix Kotlin formatting
./gradlew ktlintFormat

# Regenerate detekt baseline after resolving violations
./gradlew detektBaseline
```
```

- [ ] **Step 5: Final commit**

```bash
git add CLAUDE.md
git commit -m "docs: add developer setup instructions for pre-commit hooks"
```
