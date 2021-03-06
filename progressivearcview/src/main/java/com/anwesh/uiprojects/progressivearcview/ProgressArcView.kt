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
    val fontSize : Float = size / 4
    val tx : String = "${Math.floor(100.0 * sf1).toInt()}"
    val tw : Float = paint.measureText(tx)
    paint.textSize = fontSize
    save()
    translate(w / 2, h / 2)
    paint.style = Paint.Style.STROKE
    drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), -90f, 360f * sf1, false, paint)
    if (sf1 > 0) {
        paint.style = Paint.Style.FILL
        drawText(tx, -tw / 2, fontSize / 4, paint)
    }
    restore()
}

fun Canvas.drawPANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawProgressArc(scale, w, h, paint)
}

class ProgressArcView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class PANode(var i : Int, val state : State = State()) {

        private var next : PANode? = null
        private var prev : PANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = PANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawPANode(i, state.scale, paint)
        }

        fun update(cb : (Float) ->Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : PANode {
            var curr : PANode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    class ProgressArc(var i : Int, val state : State = State()) {

        private var curr : PANode = PANode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : View, var animated : Boolean = false) {

        private val pa : ProgressArc = ProgressArc(0)
        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            pa.draw(canvas, paint)
            animator.animate {
                pa.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            pa.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : ProgressArcView {
            val view : ProgressArcView = ProgressArcView(activity)
            activity.setContentView(view)
            return view
        }
    }
}