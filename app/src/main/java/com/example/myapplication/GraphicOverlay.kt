package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource
import com.otaliastudios.cameraview.controls.Facing
import java.util.HashSet

class GraphicOverlay(context: Context, atrrs: AttributeSet) : View(context, atrrs) {
    private val mLock = Any()
    private var mPreviewWidth: Int = 0
    private var mWidthScaleFactor = 1.0f
    private var mPreviwHeight: Int = 0
    private var mHeightScaleFactor = 1.0f
    private var mFacing = CameraSource.CAMERA_FACING_BACK
    private val mGraphic = HashSet<Graphic>()

    abstract class Graphic(val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas)

        fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.mWidthScaleFactor
        }

        fun scaleY(vertical: Float):Float{
            return vertical * overlay.mHeightScaleFactor
        }

        fun translateX(x: Float):Float{
            return if (overlay.mFacing == CameraSource.CAMERA_FACING_FRONT){
                overlay.width - scaleX(x)
            } else{
                scaleX(x)
            }
        }

        fun translateY(y: Float):Float{
            return scaleY(y)
        }

        fun postInvalidate(){
            overlay.postInvalidate()
        }
    }

    fun clear(){
        synchronized(mLock){
            mGraphic.clear()
        }
        postInvalidate()
    }

    fun add(graphic: Graphic){
        synchronized(mLock){
            mGraphic.add(graphic)
        }
        postInvalidate()
    }

    fun remove(graphic: Graphic){
        synchronized(mLock){
            mGraphic.remove(graphic)
        }
        postInvalidate()
    }

    fun setCamera(previewWidth:Int, previewHeight:Int ,facing: Int){
        synchronized(mLock){
            mPreviewWidth = previewWidth
            mPreviwHeight = previewHeight
            mFacing = facing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(mLock){
            if(mPreviewWidth != 0 && mPreviwHeight != 0){
                mWidthScaleFactor = canvas.width.toFloat()/ mPreviewWidth.toFloat()
                mHeightScaleFactor = canvas.height.toFloat()/mPreviwHeight.toFloat()
            }
            for(graphic in mGraphic){
                graphic.draw(canvas)
            }
        }
    }
}