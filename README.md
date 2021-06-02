# ProgressView
自定义ProgressView，包括：直线进度条

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

## 效果展示
<img src="https://github.com/shenbengit/ProgressView/blob/master/screenshots/LineProgressView.gif" alt="动图演示效果" width="250px">

## 使用事例
> LineProgressView    

布局事例
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
代码事例
```kotlin
    val lpv: LineProgressView = findViewById(R.id.lpv)
    lpv.setProgress(progress: Int)//设置当前进度
    lpv.setMaxProgress(maxProgress: Int)//设置最大进度
    lpv.setLineReachedColor(color: Int)//进度条进度部分显示的颜色
    lpv.setLineUnreachedColor(color: Int)//进度条剩余部分显示的颜色
    lpv.setLineHeight(height: Int)//进度条线的高度
    lpv.setLineCornerUsed(lineCornerUsed: Boolean)//true:带圆角,false:不带圆角
    lpv.setProgressTextVisibility(isVisible: Boolean)//true:显示进度条上文字,false:不显示进度条上文字
    lpv.setProgressTextSize(textSize: Int)//文字textSize
    lpv.setProgressTextTypeface(typeface: Typeface?)//
    lpv.setProgressTextColor(textColor: Int)//文字textSize
    lpv.setProgressTextPrefix(prefix: String?)//文字前缀
    lpv.setProgressTextSuffix(suffix: String?)//文字后缀
    lpv.getReachedPaint()//获取画笔
    lpv.getUnreachedPaint()/获取画笔
    lpv.getTextPaint()/获取画笔
    //添加进度回调监听
    lpv.setOnProgressListener(object : OnProgressListener {
       override fun onProgressChanged(current: Int, max: Int, percentage: Int) {
                
       }
    })
    //如果使用Kotlin
    lpv1.setOnProgressListener { current, max, percentage ->

    }
```

# [License](https://github.com/shenbengit/ProgressView/blob/master/LICENSE)
