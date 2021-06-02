package com.shencoder.progressviewdemo

import android.graphics.Color
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.shencoder.progressview.LineProgressView

class MainActivity : AppCompatActivity() {
    private companion object {
        private const val TIME_TAG = 101
    }

    private lateinit var lpv1: LineProgressView
    private lateinit var lpv2: LineProgressView
    private lateinit var lpv3: LineProgressView
    private lateinit var lpv4: LineProgressView

    private var currentProgress = 0

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == TIME_TAG) {
                if (currentProgress >= 100) {
                    return
                }
                ++currentProgress
                println("设置进度:$currentProgress")
                lpv1.setProgress(currentProgress)
                lpv2.setProgress(currentProgress)
                lpv3.setProgress(currentProgress)
                lpv4.setProgress(currentProgress)
                sendEmptyMessageDelayed(TIME_TAG, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lpv1 = findViewById(R.id.lpv1)
        lpv2 = findViewById(R.id.lpv2)
        lpv3 = findViewById(R.id.lpv3)
        lpv4 = findViewById(R.id.lpv4)
        lpv1.setOnProgressListener { current, max, percentage ->
            println("当前进度:$percentage")
        }
        findViewById<Button>(R.id.btn).setOnClickListener {
            currentProgress = 80
            lpv1.setLineCornerUsed(lpv1.getLineCornerUsed().not())
            lpv2.setLineCornerUsed(lpv1.getLineCornerUsed().not())
            lpv1.setProgress(currentProgress)
            lpv1.setLineReachedColor(Color.RED)
            lpv1.setProgressTextSuffix("$")
            lpv1.setProgressTextSize(15)
            lpv1.setLineHeight(20)
            lpv2.setProgress(currentProgress)
            lpv2.setLineReachedColor(Color.YELLOW)
            lpv2.setProgressTextSuffix("@@")
            lpv1.setProgressTextSize(20)
            lpv1.setLineHeight(30)
        }
//        handler.sendEmptyMessageDelayed(TIME_TAG, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}