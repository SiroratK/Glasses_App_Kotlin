package com.example.bitamirshafiee.ml_kit_skeleton


import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

/** Graphic instance for rendering face contours graphic overlay view.  */
class FaceContourGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {
    var previewWidth: Int? = null
    // The preview height
    var previewHeight: Int? = null
    private var widthScaleFactor = 1.0f
    private var heightScaleFactor = 1.0f
    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint






    @Volatile
    private var firebaseVisionFace: FirebaseVisionFace? = null

    init {

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    /**
     * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
     * portions of the overlay to trigger a redraw.
     */
    fun updateFace(face: FirebaseVisionFace) {
        firebaseVisionFace = face
        postInvalidate()
    }



    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())

        //by me
        val leftE = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
        val rightE = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
        var glassPoint :Int? = null
        if (leftE != null && rightE != null) {
            val eyeDistance = rightE.position.x - leftE.position.x
            val delta = (widthScaleFactor * eyeDistance / 2).toInt()
            val glassesRect = Rect(
                translateX(leftE.position.x).toInt()-delta,
                translateY(leftE.position.y).toInt()-delta,
                translateX(rightE.position.x).toInt()+delta,
                translateY(rightE.position.y).toInt()+delta)
            Log.e("check","eye distance = $eyeDistance")
            val glassesBitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.waterdrop_glasses)
            canvas.drawBitmap(glassesBitmap, null, glassesRect, null)

        }


        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
        canvas.drawText("id: " + face.trackingId, x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)


        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, boxPaint)


        val contour = face.getContour(FirebaseVisionFaceContour.FACE)
        for (point in contour.points) {
            val px = translateX(point.x!!)
            val py = translateY(point.y!!)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
        }


//
//
//        val contour2 = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE)
//        for (point in contour2.points) {
//            val px = translateX(point.x!!)
//            val py = translateY(point.y!!)
//            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
//        }

//        val contour2 = face.getContour(FirebaseVisionFaceContour.ALL_POINTS)
//        for (point in contour2.points) {
//            val px = translateX(point.x!!)
//            val py = translateY(point.y!!)
//            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
//        }



        //    if (face.getSmilingProbability() >= 0) {
        //      canvas.drawText(
        //          "happiness: " + String.format("%.2f", face.getSmilingProbability()),
        //          x + ID_X_OFFSET * 3,
        //          y - ID_Y_OFFSET,
        //          idPaint);
        //    }

        //    if (face.getRightEyeOpenProbability() >= 0) {
        //      canvas.drawText(
        //          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
        //          x - ID_X_OFFSET,
        //          y,
        //          idPaint);
        //    }
        //    if (face.getLeftEyeOpenProbability() >= 0) {
        //      canvas.drawText(
        //          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
        //          x + ID_X_OFFSET * 6,
        //          y,
        //          idPaint);
        //    }
        val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
        if (leftEye != null && leftEye.position != null) {
            canvas.drawCircle(
                translateX(leftEye.position.x!!),
                translateY(leftEye.position.y!!),
                FACE_POSITION_RADIUS,
                facePositionPaint
            )
        }
        val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
        if (rightEye != null && rightEye.position != null) {
            canvas.drawCircle(
                translateX(rightEye.position.x!!),
                translateY(rightEye.position.y!!),
                FACE_POSITION_RADIUS,
                facePositionPaint
            )
        }

        val leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)
        if (leftCheek != null && leftCheek.position != null) {
            canvas.drawCircle(
                translateX(leftCheek.position.x!!),
                translateY(leftCheek.position.y!!),
                FACE_POSITION_RADIUS,
                facePositionPaint
            )
        }
        val rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)
        if (rightCheek != null && rightCheek.position != null) {
            canvas.drawCircle(
                translateX(rightCheek.position.x!!),
                translateY(rightCheek.position.y!!),
                FACE_POSITION_RADIUS,
                facePositionPaint
            )
        }
    }

    companion object {

        private val FACE_POSITION_RADIUS = 10.0f
        private val ID_TEXT_SIZE = 70.0f
        private val ID_Y_OFFSET = 80.0f
        private val ID_X_OFFSET = -70.0f
        private val BOX_STROKE_WIDTH = 5.0f


        private val COLOR_CHOICES = intArrayOf(
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
        )
        private var currentColorIndex = 0
    }
}
