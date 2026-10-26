# Echo

Echo is an offline-first mood journal for Android. It pairs a quiet, cinematic journal with simple
mood check-ins and insights made only from what you record. There is no account, backend, telemetry,
or network permission. Entries and check-ins live in a local Room database.

## Stack

- Kotlin 2.2.21, Jetpack Compose (BOM 2025.11.01), Material 3
- Navigation Compose 2.9.8, lifecycle-aware ViewModels with `StateFlow`
- Room 2.8.4 with exported schema
- AGP 8.13.2, Java 17, compile/target SDK 36, min SDK 26
- Package/application ID: `com.echo.journal`

## Build

The environment may need `JAVA_HOME` pointed at JDK 17:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Install and launch

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.echo.journal/.MainActivity
```

## Tests

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :app:testDebugUnitTest
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :app:connectedDebugAndroidTest
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :app:lintDebug
```

- `app/src/test/java/com/echo/journal/domain/DomainTest.kt` covers mood storage, search, titles,
  truncation, streak/insight math, home snapshots, prompts, and word counts.
- `app/src/androidTest/java/com/echo/journal/data/EchoPersistenceTest.kt` covers Room persistence,
  ordering, edits, deletes, and unknown mood keys across database reopen.
- `app/src/androidTest/java/com/echo/journal/ui/EchoUiTest.kt` covers selected-state semantics,
  navigation, and writes software-rendered screenshots of the primary screens to the app's files
  directory. It temporarily sets the system animator scale to zero so Compose tests can settle.

## Structure

- `data/local` - Room entities, DAO, database
- `data/repository` - mapping and persistence operations
- `domain` - models, mood definitions, search, dates, insights, prompts, text metrics
- `ui/screens` - Home, Journal, Reader, Editor, Insights
- `ui/components` - glass surfaces and controls, mood selector, cards, atmospheric background,
  floating navigation
- `ui/theme` - palette, typography, shapes, reduced-motion handling
- `ui/EchoApp.kt` - navigation graph, shared-element transition host, floating navigation visibility

## Design notes

- Palette: `#0B0B10`, `#12121B`, `#191925`, `#A78BFA`, `#C4B5FD`, `#A1A1B5`, `#F7F5FF`.
- True backdrop blur is not available to ordinary app composables on the target platform, so glass
  surfaces use layered translucent gradients, highlights, borders, and shadows as an approximation.
- Mood is always communicated by emoji and text as well as color. The selected mood has a check mark
  and a state description. Large font scales switch the mood grid and some insight layouts to fewer
  columns/lines instead of clipping.
- The floating navigation hides while the IME is visible, and content scrolls under a bottom fade.
- Motion respects the system animator duration scale; at zero, the atmospheric background stops.

## Known limitations

- Only the dark theme is provided.
- No import, export, share, or cloud backup. Local data survives app restarts but is removed by
  uninstalling/clearing app data.
- Room schema v1 has no migration history yet. Future schema changes must add migrations; destructive
  fallback is intentionally disabled.
- Emoji rendering varies by device font/OEM.
- Visual verification used software-rendered Compose screenshots on an API 36 AOSP ATD emulator,
  because that image's display compositor produced black `screencap`/`screenrecord` output. The app
  should still be checked on a physical device before release.
