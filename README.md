# Tgram TV Unlimited

Android TV 专用 Telegram 客户端 - 支持无限播放功能

## 功能特点

- 📺 仅支持 Android TV（手机版开发中）
- 🔐 支持手机号登录、二维码登录
- 💬 聊天列表、群组、频道浏览
- 📝 文字、表情、贴纸支持
- 🖼️ 图片、文件、语音消息查看与下载
- 🎬 **ExoPlayer 视频/语音无限播放** - 无任何 15 秒或时长限制
- 🎮 TV 遥控器友好界面（大字体、焦点导航）
- 🌙 Material 3 深色主题

## 项目结构

```
TgramTVUnlimited/
├── app/
│   └── src/main/
│       ├── java/com/momoko/tgramtvunlimited/
│       │   ├── MainActivity.kt          # 主入口
│       │   ├── TgramTVApp.kt            # Application 类
│       │   ├── data/api/
│       │   │   └── TelegramApi.kt       # TDLib API 封装
│       │   ├── player/
│       │   │   ├── UnlimitedPlayer.kt   # 无限播放核心
│       │   │   └── PlaybackService.kt   # 媒体播放服务
│       │   └── ui/
│       │       ├── navigation/          # 导航系统
│       │       ├── screens/             # 界面
│       │       │   ├── chat/            # 聊天相关
│       │       │   ├── login/           # 登录界面
│       │       │   ├── media/           # 媒体播放
│       │       │   └── settings/        # 设置界面
│       │       └── theme/               # 主题
│       └── res/                         # 资源文件
├── build.gradle.kts                     # 项目构建配置
├── settings.gradle.kts                  # 项目设置
└── gradle/wrapper/                      # Gradle 包装器
```

## 技术栈

- **Kotlin** 1.9.20
- **Jetpack Compose** (BOM 2023.10.01)
- **Material 3** 设计
- **ExoPlayer (Media3)** 1.2.0 - 无限播放支持
- **TDLib** - Telegram 核心库
- **Kotlin Coroutines** & **Flow**
- **Coil** - 图片加载
- **DataStore** - 首选项存储

## 核心模块说明

### 1. TelegramApi.kt
TDLib 封装，处理：
- 登录/验证码/密码认证
- 聊天列表获取
- 消息收发
- 文件下载

### 2. UnlimitedPlayer.kt
ExoPlayer 无限播放核心：
- 支持任意时长视频/音频
- 播放完成自动循环
- 遥控器控制（快进/快退/播放速度）
- 关键代码片段：
```kotlin
// 无限播放：播放完成后重新开始
override fun onPlaybackStateChanged(state: Int) {
    if (state == Player.STATE_ENDED) {
        player?.seekTo(0)
        player?.play()
    }
}
```

## 环境要求

- **Android Studio** Arctic Fox (2020.3.1) 或更高
- **JDK** 17
- **Gradle** 8.2
- **Android SDK** 34 (API 34)
- **Android TV** 设备或模拟器 (API 24+)

## 编译步骤

### 1. 准备工作

```bash
# 克隆项目
git clone <project-url>
cd TgramTVUnlimited
```

### 2. 配置 Telegram API

在 `TelegramApi.kt` 中配置你的 Telegram API 凭据：

```kotlin
// 在 TelegramApi.kt 中
suspend fun initialize() {
    client = TdApi.Client { update ->
        handleUpdate(update)
    }
    
    // 设置你的 API ID 和 Hash
    client?.send(TdApi.SetTdlibParameters()) {
        // 配置参数
    }
}
```

获取 Telegram API 凭据：
1. 访问 https://my.telegram.org
2. 创建新应用
3. 获取 api_id 和 api_hash

### 3. 用 Android Studio 打开

```
File → Open → 选择 TgramTVUnlimited 目录
```

### 4. 同步项目

```
File → Sync Project with Gradle Files
```

### 5. 构建 Debug APK

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

或使用命令行：

```bash
./gradlew assembleDebug
```

### 6. 输出位置

APK 文件位置：
```
app/build/outputs/apk/debug/app-debug.apk
```

## 使用说明

### 安装 APK

1. 将 APK 传输到 Android TV
2. 在 TV 上打开文件管理器
3. 点击安装 APK
4. 授予必要权限

### 首次使用

1. 打开应用
2. 输入手机号或使用二维码登录
3. 授权应用
4. 开始使用

### 播放视频/语音

1. 进入聊天
2. 点击媒体消息
3. 使用遥控器控制播放
   - 方向键：快进/快退
 - 确认键：播放/暂停
 - 菜单键：调整速度

## 常见问题

### Q: 编译失败？
A: 确保 JDK 17 已安装，ANDROID_HOME 环境变量已设置

### Q: 登录失败？
A: 检查 Telegram API 凭据是否正确，网络是否正常

### Q: 视频无法播放？
A: 检查网络连接，确保 TV 可以访问视频 URL

### Q: 遥控器无法控制？
A: 确保应用获得焦点，使用方向键和确认键

## 更新日志

### v1.0.0
- 初始版本
- 基础聊天功能
- 无限播放支持
- TV 友好界面

## 许可证

MIT License

## 贡献

欢迎提交 Pull Request！

---

**注意**：本应用需要 Telegram API 授权。使用本应用即表示您同意 Telegram 的服务条款。