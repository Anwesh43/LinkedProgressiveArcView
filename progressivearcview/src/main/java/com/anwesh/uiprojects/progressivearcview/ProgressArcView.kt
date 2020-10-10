package com.anwesh.uiprojects.progressivearcview

/**
 * Created by anweshmishra on 11/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#673AB7",
        "#4CAF50",
        "#2196F3",
        "#4CAF50"
).map({Color.parseColor(it)}).toTypedArray()
val parts : Int = 2
val sizeFactor : Float = 6.9f
val strokeFactor : Float = 90f
val scGap : Float = 0.02f / parts
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawProgressArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val size : Float = Math.min(w, h) / sizeFactor
    val fontSize : Float = size / 5
    val tx : String = "${Math.floor(100.0 * scale).toInt()}"
    val tw : Float = paint.measureText(tx) / 2
    paint.textSize = fontSize
    paint.style = Paint.Style.STROKE
    save()
    translate(w / 2, h / 2)
    drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), -90f, 360f * sf1, false, paint)
    drawText(tx, -tw / 2, -fontSize / 4, paint)
    restore()
}

fun Canvas.drawPANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawProgressArc(scale, w, h, paint)
}
