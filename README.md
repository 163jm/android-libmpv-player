# Android LibMpv Player

基于 **libmpv** 的 Android 本机视频播放器。

- **内核**: [dev.jdtech.mpv:libmpv](https://central.sonatype.com/artifact/dev.jdtech.mpv/libmpv) `1.0.0`
- **ABI**: 仅 arm64-v8a
- **Min / Target SDK**: 26 / 36

## 功能

- 本机视频浏览（MediaStore 扫描，无文件选择器）
- 刷新 / 下拉刷新
- 点击播放（`hwdec` 可配置）
- **设置页**：解码方式、播放方向、循环、精确跳转、反交错、缓存、音量等
- 可选网络 URL / 外部 `VIEW` Intent

## 构建

```bash
./gradlew assembleDebug
```
