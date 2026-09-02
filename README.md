# Android LibMpv Player

A complete, minimal Android video player that uses **libmpv** as the playback core.

- **Core**: [dev.jdtech.mpv:libmpv](https://central.sonatype.com/artifact/dev.jdtech.mpv/libmpv) (Maven Central)
- **ABI**: **arm64-v8a only**
- **Min SDK**: 26
- **Compile / Target SDK**: 36
- **AGP / Gradle**: 8.13.2 / 8.13
- **UI**: Material 3 + SurfaceView

## Features

- Play local video / audio files (Storage Access Framework)
- Play network streams / URLs (http, https, etc.)
- Hardware decoding (`hwdec=auto`)
- Play / Pause / Seek controls
- Progress bar & time display
- Open via `VIEW` intent from other apps

## Requirements

- Android Studio Meerkat / Narwhal or newer (AGP 8.13+)
- JDK 17
- **Gradle 8.13** (AGP 8.13 is **not** compatible with Gradle 9.6+)
- Device or emulator with **arm64-v8a**

## Build locally

```bash
# Use Gradle 8.13 (not 9.x)
gradle wrapper --gradle-version 8.13   # once, if you have no wrapper yet
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Release:

```bash
./gradlew assembleRelease
```

> **Important:** Do not build with system Gradle 9.6+. You will get:
> `Plugin relies on InternalProblems, a Gradle internal API that was removed in Gradle 9.6.0`.

## GitHub Actions

The workflow pins **Gradle 8.13** via `gradle/actions/setup-gradle` and builds arm64-v8a debug/release APKs as artifacts.

## Project structure

```
app/
  src/main/
    java/com/example/libmpvplayer/
      MainActivity.kt      # File / URL picker
      PlayerActivity.kt    # Fullscreen libmpv player
    res/
    AndroidManifest.xml
  build.gradle.kts
```

## License

MIT (same as the underlying libmpv-android bindings).

libmpv / mpv itself are under LGPL / GPL – see [mpv license](https://github.com/mpv-player/mpv).
