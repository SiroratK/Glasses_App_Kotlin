package com.example.myapplication

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.CameraView

class FaceProcessor(private val cameraView: CameraView, private val overayView: CameraView) {
    // Initailiza the face detection option ,and we need all the face landmarks
    private val options = FirebaseVisionFaceDetectorOptions.Builder()
        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
        .build()

    //obtain Line FIrebaseVisionFaceDetector instance
    private val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

    fun startProcessing(){
        cameraView.addFrameProcessor{frame ->
            if(frame.size != null){
                val rotation = frame.rotation/90
                if(rotation/2==0){
                    overlayView.
                }
            }
        }
    }
}