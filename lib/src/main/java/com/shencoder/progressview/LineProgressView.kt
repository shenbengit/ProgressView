package com.shencoder.progressview

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.*
import androidx.annotation.IntRange

/**
 * 直线进度条自定义view
 * LineProgressView
 *
 * @author  ShenBen
 * @date    2021/05/17 10:18
 * @email   714081644@qq.com
 */
class LineProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        private const val TAG = "LineProgressView"
        private const val DEFAULT_MAX_PROGRESS = 100
        private val DEFAULT_REACHED_COLOR = Color.parseColor("#2BB4FF")
        private val DEFAULT_UNREACHED_COLOR = Color.parseColor("#C9C9C9")
        private const val DEFAULT_LINE_CORNER_ENABLE = true
        private const val DEFAULT_PROGRESS_TEXT_VISIBILITY = true
        private const val DEFAULT_PROGRESS_TEXT_PREFIX = ""
        private const val DEFAULT_PROGRESS_TEXT_SUFFIX = "%"
        private const val ZERO_PERCENT = 0
        private const val ONE_HUNDRED_PERCENT = 100

    }

    /**
     * current progress
     * Default:0
     */
    private var mProgress: Int

    /**
     * max progress
     * Default:[DEFAULT_MAX_PROGRESS]
     */
    private var mMaxProgress: Int

    /**
     * the color of line reached progress
     * Default:[DEFAULT_REACHED_COLOR]
     */
    @ColorInt
    private var mLineReachedColor: Int

    /**
     * the color of line unreached progress
     * Default:[DEFAULT_UNREACHED_COLOR]
     */
    @ColorInt
    private var mLineUnreachedColor: Int

    /**
     * the height of line
     * Default:[R.dimen.lpv_default_line_height] 2dp
     */
    @Px
    private var mLineHeight: Int

    /**
     * line corner
     * Default:[DEFAULT_LINE_CORNER_ENABLE]
     */
    private var mLineCornerEnable: Boolean

    /**
     * the visibility of progress text
     * true:VISIBLE
     * false:GONE
     * Default:[DEFAULT_PROGRESS_TEXT_VISIBILITY]
     */
    private var mProgressTextVisibility: Boolean

    /**
     * the text size of progress text
     * Default:[R.dimen.lpv_default_text_size] 14sp
     */
    private var mProgressTextSize: Int

    /**
     * the text color of progress text
     * Default:[DEFAULT_REACHED_COLOR]
     */
    private var mProgressTextColor: Int

    /**
     * the prefix of progress text
     * Default:[DEFAULT_PROGRESS_TEXT_PREFIX]
     */
    private var mProgressTextPrefix: String

    /**
     * the suffix of progress text
     * Default:[DEFAULT_PROGRESS_TEXT_SUFFIX]
     */
    private var mProgressTextSuffix: String

    /**
     * reached area paint
     */
    private val mReachedPaint = Paint()

    /**
     * unreached area paint
     */
    private val mUnreachedPaint = Paint()

    /**
     * text paint
     */
    private val mTextPaint = Paint()

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LineProgressView)
        val maxProgress =
            typedArray.getInt(R.styleable.LineProgressView_lpv_max, DEFAULT_MAX_PROGRESS)
        mMaxProgress = if (maxProgress > 0) maxProgress else DEFAULT_MAX_PROGRESS
        val progress = typedArray.getInt(R.styleable.LineProgressView_lpv_progress, 0)
        mProgress = when {
            progress < 0 -> 0
            progress > mMaxProgress -> mMaxProgress
            else -> progress
        }
        mLineReachedColor = typedArray.getColor(
            R.styleable.LineProgressView_lpv_line_reached_color,
            DEFAULT_REACHED_COLOR
        )
        mLineUnreachedColor = typedArray.getColor(
            R.styleable.LineProgressView_lpv_line_unreached_color,
            DEFAULT_UNREACHED_COLOR
        )
        mLineHeight =
            typedArray.getDimensionPixelSize(
                R.styleable.LineProgressView_lpv_line_height,
                getDimensionPixelSize(R.dimen.lpv_default_line_height)
            )
        mLineCornerEnable =
            typedArray.getBoolean(
                R.styleable.LineProgressView_lpv_line_corner_enable,
                DEFAULT_LINE_CORNER_ENABLE
            )
        mProgressTextVisibility = typedArray.getBoolean(
            R.styleable.LineProgressView_lpv_progress_text_visibility,
            DEFAULT_PROGRESS_TEXT_VISIBILITY
        )
        mProgressTextSize = typedArray.getDimensionPixelSize(
            R.styleable.LineProgressView_lpv_progress_text_size,
            getDimensionPixelSize(R.dimen.lpv_default_text_size)
        )
        mProgressTextColor = typedArray.getColor(
            R.styleable.LineProgressView_lpv_progress_text_color,
            DEFAULT_REACHED_COLOR
        )
        mProgressTextPrefix =
            typedArray.getString(R.styleable.LineProgressView_lpv_progress_text_prefix)
                ?: DEFAULT_PROGRESS_TEXT_PREFIX
        mProgressTextSuffix =
            typedArray.getString(R.styleable.LineProgressView_lpv_progress_text_suffix)
                ?: DEFAULT_PROGRESS_TEXT_SUFFIX

        typedArray.recycle()
        initPaint()
    }

    private fun initPaint() {
        mReachedPaint.run {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            color = mLineReachedColor
        }
        mUnreachedPaint.run {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            color = mLineUnreachedColor
        }
        mTextPaint.run {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            color = mProgressTextColor
            textSize = mProgressTextSize.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = measureSize(widthMeasureSpec, true)
        val heightSize = measureSize(heightMeasureSpec, false)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.i(TAG, "onSizeChanged: w:$w,h:$h")
        Log.i(TAG, "onSizeChanged: w:$width,h:$height")
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        val percentage = mProgress * 100 / mMaxProgress
        if (percentage != ZERO_PERCENT) {
            drawReachedArea(canvas)
        }
        drawProgressText(canvas)

        if (percentage != ONE_HUNDRED_PERCENT) {
            drawUnreachedArea(canvas)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }

    /**
     * set progress
     * @param progress should be in [0..mMaxProgress]
     */
    @MainThread
    fun setProgress(@IntRange(from = 0) progress: Int) {
        if (mProgress == progress) {
            return
        }
        mProgress = when {
            progress < 0 -> 0
            progress > mMaxProgress -> mMaxProgress
            else -> progress
        }
        invalidate()
    }

    fun getProgress() = mProgress

    /**
     * set max progress
     * @param maxProgress should be greater than 0
     */
    @UiThread
    fun setMaxProgress(@IntRange(from = 0) maxProgress: Int) {
        if (mMaxProgress == maxProgress) {
            return
        }
        mMaxProgress = if (maxProgress > 0) maxProgress else DEFAULT_MAX_PROGRESS
        invalidate()
    }

    fun getMaxProgress() = mMaxProgress

    @UiThread
    fun setLineReachedColor(@ColorInt color: Int) {
        mLineReachedColor = color
        invalidate()
    }

    fun getLineReachedColor() = mLineReachedColor

    @UiThread
    fun setLineUnreachedColor(@ColorInt color: Int) {
        mLineUnreachedColor = color
        invalidate()
    }

    fun getLineUnreachedColor() = mLineUnreachedColor

    /**
     * set height of line
     * @param height integer pixels
     */
    @UiThread
    fun setLineHeight(@Px height: Int) {
        mLineHeight = height
        requestLayout()
        invalidate()
    }

    fun getLineHeight() = mLineHeight

    /**
     * set visibility of progress text
     *
     * @param isVisible true : visible
     *                  false : gone
     */
    @UiThread
    fun setProgressTextVisibility(isVisible: Boolean) {
        if (mProgressTextVisibility != isVisible) {
            mProgressTextVisibility = isVisible
            invalidate()
        }
    }

    fun getProgressTextVisibility() = mProgressTextVisibility

    /**
     * the text size of progress text
     * @param textSize integer pixels
     */
    @UiThread
    fun setProgressTextSize(@Px textSize: Int) {
        if (mProgressTextSize != textSize) {
            mProgressTextSize = textSize
            mTextPaint.textSize = textSize.toFloat()

            requestLayout()
            invalidate()
        }
    }

    fun setProgressText(typeface: Typeface) {
        mTextPaint.typeface = typeface
        invalidate()
    }

    fun getProgressTextSize() = mProgressTextSize

    @UiThread
    fun setProgressTextColor(@ColorInt textColor: Int) {
        mProgressTextColor = textColor
        mTextPaint.color = textColor

        invalidate()
    }

    fun getProgressTextColor() = mProgressTextColor

    @UiThread
    fun setProgressTextPrefix(prefix: String?) {
        mProgressTextPrefix = if (prefix.isNullOrBlank()) "" else prefix
        requestLayout()
        invalidate()
    }

    fun getProgressTextPrefix() = mProgressTextPrefix

    @UiThread
    fun setProgressTextSuffix(suffix: String?) {
        mProgressTextSuffix = if (suffix.isNullOrBlank()) "" else suffix
        requestLayout()
        invalidate()
    }

    fun getProgressTextSuffix() = mProgressTextSuffix


    /**
     * measure size
     * @param measureSpec
     * @param isWidth true : measure width
     *                false : measure height
     */
    private fun measureSize(measureSpec: Int, isWidth: Boolean): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            val padding = if (isWidth) paddingStart + paddingEnd else paddingTop + paddingBottom
            (if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight).plus(padding)
        }
    }

    override fun getSuggestedMinimumWidth(): Int {
        val text = "$mProgressTextPrefix$ONE_HUNDRED_PERCENT$mProgressTextSuffix"
        val measureText = mTextPaint.measureText(text)
        return measureText.toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        val text = "$mProgressTextPrefix$ONE_HUNDRED_PERCENT$mProgressTextSuffix"
        val rect = Rect()
        mTextPaint.getTextBounds(text, 0, text.length, rect)
        return rect.height().coerceAtLeast(mLineHeight)
    }

    private fun drawReachedArea(canvas: Canvas) {

    }

    private fun drawProgressText(canvas: Canvas) {

    }

    private fun drawUnreachedArea(canvas: Canvas) {

    }


    private fun dp2px(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    private fun sp2px(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }

    private fun getDimensionPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)
}