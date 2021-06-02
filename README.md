# ProgressView
自定义ProgressView，包括：直线进度条

## 效果展示
<img src="https://github.com/shenbengit/ProgressView/blob/master/screenshots/LineProgressView.gif" alt="动图演示效果" width="250px">

## 引入

### 将JitPack存储库添加到您的项目中(项目根目录下build.gradle文件)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### 添加依赖
[![](https://jitpack.io/v/shenbengit/ProgressView.svg)](https://jitpack.io/#shenbengit/ProgressView)
```gradle
dependencies {
    implementation 'com.github.shenbengit:ProgressView:Tag'
}
```

## 使用事例

```xml
    <com.shencoder.progressview.LineProgressView
        android:id="@+id/lpv2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:lpv_progress="0"
        app:lpv_max="100"
        app:lpv_line_reached_color="#2BB4FF"
        app:lpv_line_unreached_color="#C9C9C9"
        app:lpv_line_height="5dp"
        app:lpv_line_corner_used="true|false"
        app:lpv_progress_text_visibility="true|false"
        app:lpv_progress_text_size="12sp"
        app:lpv_progress_text_color="#2BB4FF"
        app:lpv_progress_text_prefix=""
        app:lpv_progress_text_suffix="%" />
```
