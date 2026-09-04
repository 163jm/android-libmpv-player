# Android LibMpv Player

基于 **libmpv** 的 Android 本机视频播放器示例。

- **内核**: [dev.jdtech.mpv:libmpv](https://central.sonatype.com/artifact/dev.jdtech.mpv/libmpv) `1.0.0`
- **ABI**: 仅 arm64-v8a
- **Min / Target SDK**: 26 / 36

## 功能

- **本机视频浏览**：申请媒体权限后，扫描 MediaStore 持久化存储中的视频
- **刷新 / 下拉刷新**：重新扫描本机视频列表
- **点击播放**：使用 libmpv（`hwdec=auto` 硬解优先）
- 可选网络 URL 播放
- 支持其它应用通过 `VIEW` Intent 打开视频

> 不使用系统文件选择器，列表来自 MediaStore 扫描结果。

## 构建

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

需要 JDK 17+。
