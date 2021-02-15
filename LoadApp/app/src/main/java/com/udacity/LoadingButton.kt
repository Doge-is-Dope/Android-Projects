package com.udacity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var textColor = 0
    private var buttonColor = 0


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(
                R.styleable.LoadingButton_textColor, 0
            )
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
        }

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)

        drawText(canvas)
    }


    private fun drawBackground(canvas: Canvas) {
        paint.color = buttonColor
        canvas.drawRect(
            0.0f,
            0.0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            paint
        )
    }


    private fun drawText(canvas: Canvas) {
        paint.apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            textSize = 24.0f * resources.displayMetrics.density
        }
        val metricsInt = paint.fontMetricsInt
        val posX = widthSize / 2.0f
        val textHeight = metricsInt.descent - metricsInt.ascent
        val posY = (heightSize - textHeight) / 2.0f - metricsInt.ascent
        canvas.drawText(
            resources.getString(R.string.button_download),
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

    override fun performClick(): Boolean {
        // Enables accessibility events as well as calls onClickListener()
        if (super.performClick()) return true

        // Invalidates the entire view, forcing a call to onDraw() to redraw the view
        invalidate()
        return true
    }

}