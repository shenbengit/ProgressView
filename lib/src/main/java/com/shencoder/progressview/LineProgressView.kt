package com.shencoder.progressview

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.AbsSavedState
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
    private var mLineCornerUsed: Boolean

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
     * used when [mLineCornerUsed] is true
     */
    private val mReachedArcRectF by lazy { RectF() }

    /**
     * used when [mLineCornerUsed] is true
     */
    private val mUnreachedArcRectF by lazy { RectF() }

    private val mReachedRectF = RectF()

    private val mUnreachedRectF = RectF()

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

    private val mValueAnimator = ValueAnimator().apply {
        addUpdateListener {
            val animatedValue = it.animatedValue
            if (animatedValue is Int) {
                setProgress(animatedValue, true)
            }
        }
    }

    private var mListener: OnProgressListener? = null

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
        mLineCornerUsed =
            typedArray.getBoolean(
                R.styleable.LineProgressView_lpv_line_corner_used,
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

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        val percentage = mProgress / mMaxProgress.toFloat()

        if (mProgressTextVisibility) {
            drawWithProgressText(canvas, percentage)
        } else {
            drawWithoutProgressText(canvas, percentage)
        }
        mListener?.onProgressChanged(
            mProgress,
            mMaxProgress,
            (percentage * ONE_HUNDRED_PERCENT).toInt()
        )
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState() ?: AbsSavedState.EMPTY_STATE
        val savedState = SavedState(superState)
        savedState.mProgress = mProgress
        savedState.mMaxProgress = mMaxProgress
        savedState.mLineReachedColor = mLineReachedColor
        savedState.mLineUnreachedColor = mLineUnreachedColor
        savedState.mLineHeight = mLineHeight
        savedState.mLineCornerUsed = mLineCornerUsed
        savedState.mProgressTextVisibility = mProgressTextVisibility
        savedState.mProgressTextSize = mProgressTextSize
        savedState.mProgressTextColor = mProgressTextColor
        savedState.mProgressTextPrefix = mProgressTextPrefix
        savedState.mProgressTextSuffix = mProgressTextSuffix
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            mProgress = state.mProgress
            mMaxProgress = state.mMaxProgress
            mLineReachedColor = state.mLineReachedColor
            mLineUnreachedColor = state.mLineUnreachedColor
            mLineHeight = state.mLineHeight
            mLineCornerUsed = state.mLineCornerUsed
            mProgressTextVisibility = state.mProgressTextVisibility
            mProgressTextSize = state.mProgressTextSize
            mProgressTextColor = state.mProgressTextColor
            mProgressTextPrefix = state.mProgressTextPrefix
            mProgressTextSuffix = state.mProgressTextSuffix

            mReachedPaint.color = mLineReachedColor
            mUnreachedPaint.color = mLineUnreachedColor
            mTextPaint.textSize = mProgressTextSize.toFloat()
            mTextPaint.color = mProgressTextColor
            invalidate()
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mValueAnimator.run {
            removeAllUpdateListeners()
            removeAllListeners()
            cancel()
        }
    }

    fun setOnProgressListener(listener: OnProgressListener?) {
        mListener = listener
    }

    inline fun setOnProgressListener(crossinline listener: (current: Int, max: Int, percentage: Int) -> Unit) {
        setOnProgressListener(object : OnProgressListener {
            override fun onProgressChanged(current: Int, max: Int, percentage: Int) {
                listener.invoke(current, max, percentage)
            }
        })
    }

    /**
     * set progress with animation
     * @param progress progress
     * @param duration The length of the animation, in milliseconds. This value cannot be negative.
     * @param timeInterpolator the interpolator to be used by this animation.
     * @see [setProgress]
     */
    fun setProgressWithAnimation(
        @IntRange(from = 0) progress: Int,
        @IntRange(from = 0) duration: Long = 1000L,
        timeInterpolator: TimeInterpolator? = null
    ) {
        if (mProgress == progress) {
            return
        }
        mValueAnimator.run {
            if (isRunning) {
                cancel()
            }
            interpolator = timeInterpolator
            setIntValues(mProgress, progress)
            this.duration = duration
            start()
        }
    }

    fun cancelProgressAnimation() {
        mValueAnimator.cancel()
    }

    fun resumeProgressAnimation() {
        mValueAnimator.resume()
    }

    fun pauseProgressAnimation() {
        mValueAnimator.pause()
    }

    fun isProgressAnimatorRunning() = mValueAnimator.isRunning

    fun isProgressAnimatorPaused() = mValueAnimator.isPaused

    /**
     * set progress
     * when [ValueAnimator.isRunning] is true ,This method will not work.
     *
     * @param progress should be in [0..mMaxProgress]
     * @see mValueAnimator
     */
    @UiThread
    fun setProgress(@IntRange(from = 0) progress: Int) {
        setProgress(progress, false)
    }

    @UiThread
    private fun setProgress(@IntRange(from = 0) progress: Int, isFromAnimator: Boolean) {
        if (mProgress == progress) {
            return
        }
        if (isFromAnimator.not() && mValueAnimator.isRunning) {
            Log.w(TAG, "setProgress failed, because valueAnimator isRunning.")
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
     * set line corner
     * @param lineCornerUsed is used corner
     */
    @UiThread
    fun setLineCornerUsed(lineCornerUsed: Boolean) {
        if (mLineCornerUsed != lineCornerUsed) {
            mLineCornerUsed = lineCornerUsed
            invalidate()
        }
    }

    fun getLineCornerUsed() = mLineCornerUsed

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

            requestLayout()
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

    private fun drawWithProgressText(canvas: Canvas, percentage: Float) {
        val realWidth = getRealWidth()
        val realHeight = getRealHeight()
        val reachedWidth = realWidth * percentage
        val middle = paddingTop + realHeight.toFloat() / 2
        val drawAreaTop = middle - mLineHeight.toFloat() / 2
        val drawAreBottom = drawAreaTop + mLineHeight
        var drawReachedAreaEnd = getStartAfterPadding() + reachedWidth

        val baseLine = middle - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)
        val drawText =
            "$mProgressTextPrefix${(percentage * ONE_HUNDRED_PERCENT).toInt()}$mProgressTextSuffix"
        val progressTextWidth = mTextPaint.measureText(drawText)
        var drawTextStart = drawReachedAreaEnd

        var needDrawUnreachedArea = true

        if (mLineCornerUsed) {
            val radius = mLineHeight.toFloat() / 2
            mReachedArcRectF.set(
                getStartAfterPadding().toFloat(),
                middle - radius,
                getStartAfterPadding() + radius * 2,
                middle + radius
            )
            mUnreachedArcRectF.set(
                getEndAfterPadding() - radius * 2,
                middle - radius,
                getEndAfterPadding().toFloat(),
                middle + radius
            )
            val needDrawReachedRectangle = reachedWidth > radius
            val arcReachedAngle =
                if (needDrawReachedRectangle) 180f else 180f - (radius - reachedWidth) / radius * 180f

            canvas.drawArc(
                mReachedArcRectF,
                180f - arcReachedAngle / 2,
                arcReachedAngle,
                false,
                mReachedPaint
            )

            if (drawTextStart + progressTextWidth >= getEndAfterPadding()) {
                drawTextStart = getEndAfterPadding() - progressTextWidth
                drawReachedAreaEnd = drawTextStart
                needDrawUnreachedArea = false
            }
            if (needDrawReachedRectangle) {
                mReachedRectF.set(
                    getStartAfterPadding() + radius,
                    drawAreaTop,
                    drawReachedAreaEnd,
                    drawAreBottom
                )
                canvas.drawRect(mReachedRectF, mReachedPaint)
            }
            canvas.drawText(drawText, drawTextStart, baseLine, mTextPaint)
            if (needDrawUnreachedArea) {
                val needDrawUnreachedRectangle =
                    getEndAfterPadding() - (drawTextStart + progressTextWidth) > radius

                if (needDrawUnreachedRectangle) {
                    canvas.drawRect(
                        drawTextStart + progressTextWidth,
                        drawAreaTop,
                        getEndAfterPadding() - radius,
                        drawAreBottom,
                        mUnreachedPaint
                    )
                }
                val arcUnreachedAngle =
                    if (needDrawUnreachedRectangle) 180f else 180f - (radius - (getEndAfterPadding() - (drawTextStart + progressTextWidth))) / radius * 180f

                canvas.drawArc(
                    mUnreachedArcRectF,
                    0f - arcUnreachedAngle / 2,
                    arcUnreachedAngle,
                    false,
                    mUnreachedPaint
                )
            }
        } else {
            if (drawTextStart + progressTextWidth >= getEndAfterPadding()) {
                drawTextStart = getEndAfterPadding() - progressTextWidth
                drawReachedAreaEnd = drawTextStart
                needDrawUnreachedArea = false
            }
            mReachedRectF.set(
                getStartAfterPadding().toFloat(),
                drawAreaTop,
                drawReachedAreaEnd,
                drawAreBottom
            )
            canvas.drawRect(mReachedRectF, mReachedPaint)
            canvas.drawText(drawText, drawTextStart, baseLine, mTextPaint)
            if (needDrawUnreachedArea) {
                mUnreachedRectF.set(
                    drawTextStart + progressTextWidth,
                    drawAreaTop,
                    getEndAfterPadding().toFloat(),
                    drawAreBottom
                )
                canvas.drawRect(mUnreachedRectF, mUnreachedPaint)
            }
        }
    }

    private fun drawWithoutProgressText(canvas: Canvas, percentage: Float) {
        val realWidth = getRealWidth()
        val realHeight = getRealHeight()
        val reachedWidth = realWidth * percentage
        val middle = paddingTop + realHeight.toFloat() / 2
        val drawAreaTop = middle - mLineHeight.toFloat() / 2
        val drawAreBottom = drawAreaTop + mLineHeight
        val drawReachedAreaEnd = getStartAfterPadding() + reachedWidth

        mReachedRectF.set(
            getStartAfterPadding().toFloat(),
            drawAreaTop,
            drawReachedAreaEnd,
            drawAreBottom
        )
        if (mLineCornerUsed) {
            mUnreachedRectF.set(
                getStartAfterPadding().toFloat(),
                drawAreaTop,
                getEndAfterPadding().toFloat(),
                drawAreBottom
            )
            val radius = mLineHeight.toFloat() / 2
            canvas.drawRoundRect(mUnreachedRectF, radius, radius, mUnreachedPaint)
            canvas.drawRoundRect(mReachedRectF, radius, radius, mReachedPaint)
        } else {
            mUnreachedRectF.set(
                mReachedRectF.right,
                drawAreaTop,
                getEndAfterPadding().toFloat(),
                drawAreBottom
            )
            canvas.drawRect(mUnreachedRectF, mUnreachedPaint)
            canvas.drawRect(mReachedRectF, mReachedPaint)
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

    private fun getDimensionPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

    /**
     * [onSaveInstanceState]
     * [onRestoreInstanceState]
     */
    internal class SavedState : BaseSavedState {
        var mProgress: Int = 0
        var mMaxProgress: Int = 0

        @ColorInt
        var mLineReachedColor: Int = 0

        @ColorInt
        var mLineUnreachedColor: Int = 0

        @Px
        var mLineHeight: Int = 0
        var mLineCornerUsed: Boolean = false
        var mProgressTextVisibility: Boolean = false
        var mProgressTextSize: Int = 0
        var mProgressTextColor: Int = 0
        var mProgressTextPrefix: String = ""
        var mProgressTextSuffix: String = ""

        constructor(superState: Parcelable?) : super(superState)

        private constructor(source: Parcel) : super(source) {
            mProgress = source.readInt()
            mMaxProgress = source.readInt()
            mLineReachedColor = source.readInt()
            mLineUnreachedColor = source.readInt()
            mLineHeight = source.readInt()
            mLineCornerUsed = source.readInt() != 0
            mProgressTextVisibility = source.readInt() != 0
            mProgressTextSize = source.readInt()
            mProgressTextColor = source.readInt()
            mProgressTextPrefix = source.readString() ?: ""
            mProgressTextSuffix = source.readString() ?: ""
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(mProgress)
            parcel.writeInt(mMaxProgress)
            parcel.writeInt(mLineReachedColor)
            parcel.writeInt(mLineUnreachedColor)
            parcel.writeInt(mLineHeight)
            parcel.writeInt(if (mLineCornerUsed) 1 else 0)
            parcel.writeInt(if (mProgressTextVisibility) 1 else 0)
            parcel.writeInt(mProgressTextSize)
            parcel.writeInt(mProgressTextColor)
            parcel.writeString(mProgressTextPrefix)
            parcel.writeString(mProgressTextSuffix)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}