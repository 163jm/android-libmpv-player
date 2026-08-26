# Android LibMpv Player

A complete, minimal Android video player that uses **libmpv** as the playback core.

- **Core**: [dev.jdtech.mpv:libmpv](https://central.sonatype.com/artifact/dev.jdtech.mpv/libmpv) (Maven Central)
- **ABI**: **arm64-v8a only**
- **Min SDK**: 24
- **UI**: Material 3 + SurfaceView

## Features

- Play local video / audio files (Storage Access Framework)
- Play network streams / URLs (http, https, etc.)
- Hardware decoding (`hwdec=auto`)
- Play / Pause / Seek controls
- Progress bar & time display
- Open via `VIEW` intent from other apps

## Requirements

- Android Studio Ladybug (or newer) / AGP 8.7+
- JDK 17
- Device or emulator with **arm64-v8a** (most modern phones)

## Build locally

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Release:

```bash
./gradlew assembleRelease
```

## GitHub Actions

The workflow `.github/workflows/build.yml` builds **arm64-v8a** debug & release APKs on every push to `main` and on tags, then uploads them as artifacts.

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
