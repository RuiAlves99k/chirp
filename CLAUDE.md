# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:build

# Desktop (JVM)
./gradlew :composeApp:run

# iOS: open iosApp/ in Xcode and run from there
```

## Architecture

Chirp is a **Kotlin Multiplatform (Compose Multiplatform)** project targeting Android, iOS, and Desktop.

### Module Structure

```
chirp/
├── composeApp/          # App entry points per platform
├── core/
│   ├── domain/          # Result<D,E> type, DataError, ChirpLogger
│   ├── data/            # HttpClientFactory (Ktor, OkHttp/Darwin)
│   ├── presentation/    # Base presentation classes, one-time events
│   └── designsystem/    # ChirpTheme, ChirpButton, ChirpTextField
├── feature/
│   ├── auth/
│   │   ├── domain/
│   │   └── presentation/
│   └── chat/
│       ├── domain/
│       ├── data/
│       ├── database/    # Room/SQLite via KSP
│       └── presentation/
└── build-logic/convention/  # Gradle convention plugins
```

### Dependency Direction

`composeApp` → `feature:*:presentation` → `feature:*:domain` → `core:domain`
`feature:*:data` → `core:data`
All UI → `core:designsystem`

### Key Patterns

- **Error handling:** Functional `Result<out D, out E : Error>` sealed interface (not exceptions). `DataError.Remote` covers 13 HTTP error types; use `map()`, `onSuccess()`, `onFailure()` helpers.
- **DI:** Koin (KMP-compatible).
- **Platform split:** Each module has `commonMain/`, `androidMain/`, `iosMain/`. Use expect/actual for platform abstractions.
- **HTTP:** Ktor with OkHttp engine (Android) and Darwin engine (iOS), configured in `core:data`.
- **Database:** Room + KSP in `feature:chat:database`.

### Convention Plugins (build-logic)

New modules should apply the appropriate plugin:
- `convention-cmp-app` — Compose Multiplatform app
- `convention-cmp-feature` — Feature module (Compose)
- `convention-cmp-library` — Compose library
- `convention-kmp-library` — Pure KMP library (no Compose)
- `convention-room` — Adds Room/KSP setup
- `convention-buildkonfig` — Adds BuildKonfig (runtime config)

**ktlint** is applied to every module automatically via `KotlinMultiplatform.kt` (all KMP library modules) and `CmpApplicationConventionPlugin` (`composeApp`). You do not need to apply it manually in new modules.

**Gotcha — ktlint filter:** `KtlintExtension.filter { }` configured from within a convention plugin is silently ignored. Any source exclusions for ktlint (e.g. Compose resource generator output, BuildKonfig output) must be configured in the root `build.gradle.kts` using:
```kotlin
subprojects {
    plugins.withId("org.jlleitschuh.gradle.ktlint") {
        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            filter { exclude { ... } }
        }
    }
}
```

### Design System (`core:designsystem`)

**Buttons** (`ChirpButton`): styles `PRIMARY`, `SECONDARY`, `DESTRUCTIVE_PRIMARY`, `DESTRUCTIVE_SECONDARY`, `TEXT`. Also `ChirpIconButton`, `ChirpFloatingActionButton`.

**Text fields** (`ChirpTextField`, `ChirpPasswordTextField`): support title, placeholder, supporting text, error state, disabled state, focus tracking. Password variant includes visibility toggle.

**Theme:** `ChirpTheme` wraps Material3 with custom color schemes (light/dark) and typography.

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 2.2.0 | Language |
| Compose Multiplatform | 1.9.0-beta01 | UI |
| Ktor | 3.2.3 | HTTP client |
| Room | 2.7.2 | Local database |
| Koin | 4.1.0 | DI |
| KSP | 2.2.0-2.0.2 | Code generation |
| Kermit | 2.1.0 | Logging |
| Coil | 3.3.0 | Image loading |
| ktlint-gradle | 12.3.0 | Kotlin formatting (applied via convention plugins) |
| detekt | 1.23.8 | Kotlin static analysis (applied via convention plugins) |

All versions are managed in `gradle/libs.versions.toml`.
