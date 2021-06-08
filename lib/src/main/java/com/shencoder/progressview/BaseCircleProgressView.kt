package com.shencoder.progressview

import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

/**
 * 圆形进度条自定义view
 *
 * @author  ShenBen
 * @date    2021/05/17 10:18
 * @email   714081644@qq.com
 */
abstract class BaseCircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_MAX_PROGRESS = 100
        private const val DEFAULT_PROGRESS_VIEW_STYLE = CircleProgressViewStyle.RING
    }

    /**
     * progress view style
     *
     */
    @CircleProgressViewStyle
    private var mProgressViewStyle: Int = DEFAULT_PROGRESS_VIEW_STYLE

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BaseCircleProgressView)

        val progressViewStyle = typedArray.getInt(
            R.styleable.BaseCircleProgressView_cpv_progress_view_style,
            DEFAULT_PROGRESS_VIEW_STYLE
        )
        setProgressViewStyle(progressViewStyle)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun getSuggestedMinimumWidth(): Int {
        return super.getSuggestedMinimumWidth()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return super.getSuggestedMinimumHeight()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

    }

    override fun onDraw(canvas: Canvas?) {

    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()

    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

    }

    /**
     * set progress view style
     *
     * @param style [CircleProgressViewStyle.RING] or [CircleProgressViewStyle.SCALE] or [CircleProgressViewStyle.WAVE]
     */
    fun setProgressViewStyle(@CircleProgressViewStyle style: Int) {
        if (style !in arrayOf(
                CircleProgressViewStyle.RING,
                CircleProgressViewStyle.SCALE,
                CircleProgressViewStyle.WAVE
            )
        ) {
            throw IllegalArgumentException("invalid style:$style")
        }

        mProgressViewStyle = style

    }

    @CircleProgressViewStyle
    fun getProgressViewStyle() = mProgressViewStyle
}