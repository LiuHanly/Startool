# Star Tool

Android 设备硬件功能测试工具（GloryStar），用于产线/质检环节对安卓一体机进行全面的硬件功能测试与老化测试，并生成测试报告。

## 功能概览

- **设备信息**：设备型号、系统信息等展示（竖屏/横屏布局适配）
- **接口与外设测试**
  - 网络：WiFi、RJ45 以太网、蓝牙
  - 存储与接口：SD 卡、USB 接口、HDMI、GPIO、韦根（Wiegand）
  - 射频识别：RFID、HID RFID（支持 Zebra SE2707 / SE4107 等模块）
  - 扫码：一维/二维条码扫描模块
- **多媒体测试**：摄像头（单摄/双摄/多摄）、屏幕显示、喇叭/音频、视频播放
- **传感器测试**：陀螺仪、按键、灯带、继电器
- **性能与稳定性测试**：CPU 测试、DDR 内存测试（stressapptest 32/64 位）、存储测试、视频压力测试
- **老化测试（Aging Test）**：预老化测试、老化测试执行与老化测试报告
- **测试报告**：测试结果汇总与报告生成
- **机型适配**：A733、SM6255、NES150、NEB101/156、VOD101、VID101、JAR101、ALU100/150/210-A133、Vega 等多机型配置

## 技术栈

- **语言**：Kotlin / Java，C++（Native，CMake）
- **构建**：Gradle 8.13，Android Gradle Plugin 8.12.2，Kotlin 1.8.22
- **SDK**：compileSdk 34，minSdk 24，targetSdk 29
- **JVM 目标**：Java 17
- **其他**：ViewBinding、机器学习模型（TFLite，物体/鸟类识别）

## 构建

需要 Android Studio（建议最新稳定版）和 JDK 17。

```bash
# Windows
gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```

首次构建前请确认 `local.properties` 中已配置本机 Android SDK 路径（Android Studio 会自动生成）。

## 项目结构

```
app/src/main/
├── java/com/glorystar/startool/
│   ├── config/          # 应用配置、基类（BaseActivity、MyApplication）
│   ├── ui/
│   │   ├── activity/    # 各硬件测试 Activity
│   │   ├── testactivity/# 新版测试流程 Activity
│   │   └── fragment/    # CPU/DDR/存储/视频测试 Fragment
│   └── utils/           # 工具类
├── cpp/                 # Native 代码（CMake）
├── assets/              # 测试二进制（stressapptest）、TFLite 模型
└── res/                 # 布局（含横竖屏）、资源
```

## 文档

- 详细功能说明见仓库根目录 `StarToolDocumentation.html`（浏览器打开）

## 仓库

- GitHub（主仓库）：https://github.com/LiuHanly/Startool
