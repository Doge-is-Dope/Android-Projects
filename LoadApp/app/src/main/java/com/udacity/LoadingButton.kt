package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textColor = 0
    private var buttonColor = 0

    private var animatedProgressValue = 0.0f
    var rectProgress = RectF()

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (buttonState) {
            ButtonState.Clicked -> startAnimation()
            ButtonState.Completed -> stopAnimator()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = textColor
        textAlign = Paint.Align.CENTER
        textSize = 20.0f * resources.displayMetrics.density
    }


    init {
        isClickable = true

        val defTextColor = ContextCompat.getColor(context, R.color.design_default_color_on_primary)
        val defButtonColor = ContextCompat.getColor(context, R.color.design_default_color_primary)

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, defTextColor)
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, defButtonColor)
        }

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(buttonColor)

        when (buttonState) {
            ButtonState.Loading, ButtonState.Clicked -> {
                drawLoading(canvas)
            }
            ButtonState.Completed -> {
                drawText(canvas, resources.getString(R.string.button_download))
            }
        }

    }

    private fun drawLoading(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)

        canvas.drawRect(0.0f, 0.0f, animatedProgressValue, heightSize.toFloat(), paint)

        val buttonText = resources.getString(R.string.button_loading)

        drawText(canvas, buttonText)

        val textWidth = getTextWidth(buttonText, paint)
        val textHeight = getTextHeight(buttonText, paint)
        val circleLeft = (width.toFloat() + textWidth + 20) / 2
        val circleTop = (height.toFloat() - textHeight) / 2
        val circleRight = circleLeft + 60.0f
        val circleBottom = circleTop + 60.0f
        val sweepAngle = (animatedProgressValue / width) * 360

        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(
            circleLeft, circleTop, circleRight, circleBottom,
            0.0f, sweepAngle, true, paint
        )
    }

    private fun drawText(canvas: Canvas, text: String) {
        paint.color = textColor
        val metricsInt = paint.fontMetricsInt
        val posX = widthSize / 2.0f
        val textHeight = metricsInt.descent - metricsInt.ascent
        val posY = (heightSize - textHeight) / 2.0f - metricsInt.ascent
        canvas.drawText(
            text,
            posX,
            posY,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun performClick(): Boolean {
        // Enables accessibility events as well as calls onClickListener()
        super.performClick()

        buttonState = ButtonState.Clicked
        invalidate()

        return true
    }

    fun getTextWidth(text: String, paint: Paint): Int {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.left + bounds.width()
    }

    fun getTextHeight(text: String, paint: Paint): Int {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.bottom + bounds.height()
    }

    private fun startAnimation() {
        val rectMultiplier = widthSize.toFloat() / 100

        valueAnimator.apply {
            setFloatValues(0f, 100f)
            duration = 2500
            addUpdateListener {
                animatedProgressValue = valueAnimator.animatedValue as Float * rectMultiplier
                buttonState = ButtonState.Loading
                invalidate()

            }
            addListener({
                buttonState = ButtonState.Completed
                invalidate()
            })
        }

        valueAnimator.start()
    }

    private fun stopAnimator() {
        valueAnimator.cancel()
        invalidate()
    }

}