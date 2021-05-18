package com.shencoder.progressview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * 圆形进度条自定义view
 *
 * @author  ShenBen
 * @date    2021/05/17 10:18
 * @email   714081644@qq.com
 */
class CircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        private const val DEFAULT_MAX_PROGRESS = 100

    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)

        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

    }

    override fun onDraw(canvas: Canvas?) {

    }
}