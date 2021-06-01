package com.shencoder.progressview

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
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
    private val mReachedPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * unreached area paint
     */
    private val mUnreachedPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * text paint
     */
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

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
            style = Paint.Style.FILL_AND_STROKE
            isDither = true
            color = mLineReachedColor
        }
        mUnreachedPaint.run {
            style = Paint.Style.FILL_AND_STROKE
            isDither = true
            color = mLineUnreachedColor
        }
        mTextPaint.run {
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.LEFT
            color = mProgressTextColor
            textSize = mProgressTextSize.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = measureSize(widthMeasureSpec, true)
        val heightSize = measureSize(heightMeasureSpec, false)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        val percentage = mProgress / mMaxProgress.toFloat()
        if (mLineCornerEnable) {
            //draw with corner
            drawWithCorner(canvas, percentage)
        } else {
            //draw without corner
            drawWithoutCorner(canvas, percentage)
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
        mReachedPaint.color = color
        invalidate()
    }

    fun getLineReachedColor() = mLineReachedColor

    @UiThread
    fun setLineUnreachedColor(@ColorInt color: Int) {
        mLineUnreachedColor = color
        mUnreachedPaint.color = color
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

    fun setProgressTextTypeface(typeface: Typeface?) {
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

    fun getReachedPaint() = mReachedPaint

    fun getUnreachedPaint() = mUnreachedPaint

    fun getTextPaint() = mTextPaint

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

    private fun measureTextWidth(text: String) = mTextPaint.measureText(text)

    private fun drawWithCorner(canvas: Canvas, percentage: Float) {
        val realWidth = getRealWidth()
        val realHeight = getRealHeight()
        val circleY = 0f

        val drawReachedAreaEnd = getStartAfterPadding() + realWidth * percentage

        // arc radius
        val radius = mLineHeight.toFloat() / 2

        val circleReachedX = 0f
        val needDrawReachedRectangle = drawReachedAreaEnd > radius
        val arcReachedAngle =
            if (needDrawReachedRectangle) 180f else 180f - (radius - drawReachedAreaEnd) / radius * 180f


        if (needDrawReachedRectangle) {

        }

        val circleUnreachedX = 0f
        val circleUnreachedY = 0f
        val arcUnreachedAngle = 0f
        val needDrawUnreachedRectangle = false

        if (needDrawUnreachedRectangle) {

        }

        if (mProgressTextVisibility) {

        } else {

        }
    }

    private fun drawWithoutCorner(canvas: Canvas, percentage: Float) {
        val realWidth = getRealWidth()
        val realHeight = getRealHeight()

        val drawAreaTop = paddingTop + (realHeight - mLineHeight).toFloat() / 2
        val drawAreBottom = drawAreaTop + mLineHeight

        val drawReachedAreaStart = getStartAfterPadding().toFloat()
        var drawReachedAreaEnd = drawReachedAreaStart + realWidth * percentage

        var drawTextStart = drawReachedAreaEnd
        var measureTextWidth = 0f
        var needDrawUnreachedArea = true

        if (mProgressTextVisibility) {
            val drawText =
                "$mProgressTextPrefix${(percentage * ONE_HUNDRED_PERCENT).toInt()}$mProgressTextSuffix"
            measureTextWidth = measureTextWidth(drawText)
            val baseLine =
                (drawAreaTop + mLineHeight / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)
            if (drawTextStart + measureTextWidth >= getEndAfterPadding()) {
                drawTextStart = getEndAfterPadding() - measureTextWidth
                drawReachedAreaEnd = drawTextStart
                needDrawUnreachedArea = false
            }
            //draw progress text
            canvas.drawText(drawText, drawTextStart, baseLine, mTextPaint)
        }

        //draw reached area
        canvas.drawRect(
            drawReachedAreaStart,
            drawAreaTop,
            drawReachedAreaEnd,
            drawAreBottom,
            mReachedPaint
        )
        //draw unreached area
        if (needDrawUnreachedArea) {
            canvas.drawRect(
                drawTextStart + measureTextWidth,
                drawAreaTop,
                getEndAfterPadding().toFloat(),
                drawAreBottom,
                mUnreachedPaint
            )
        }
    }

    private fun getRealWidth() = width - paddingStart - paddingEnd

    private fun getRealHeight() = height - paddingTop - paddingBottom

    private fun getStartAfterPadding(): Int {
        return paddingStart
    }

    private fun getEndAfterPadding(): Int {
        return width - paddingEnd
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