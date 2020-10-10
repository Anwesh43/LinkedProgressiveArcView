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

