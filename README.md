# Android LibMpv Player

Minimal Android video player powered by **libmpv**.

- **Core**: [dev.jdtech.mpv:libmpv](https://central.sonatype.com/artifact/dev.jdtech.mpv/libmpv) `1.0.0`
- **ABI**: arm64-v8a only
- **Min / Target SDK**: 26 / 36

## Features

- **Local video browser**: request media permission \u2192 scan MediaStore \u2192 list videos
- **Refresh** button to rescan persistent storage
- Tap a video to play with libmpv (hardware decode `hwdec=auto`)
- Optional network URL playback
- Open via `VIEW` intent from other apps

## Build

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Requires JDK 17+ and Gradle 8.13 (see CI workflow).
