package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

class FaceDetectionActivity:AppCompatActivity() {

    private fun detectFaces(image:FirebaseVisionImage){
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        val result = detector.detectInImage(image)
            .addOnSuccessListener { faces ->
                for (face in faces){
                    val bounds = face.boundingBox
                    val rotY = face.headEulerAngleY
                    val rotZ = face.headEulerAngleZ

                    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                    leftEar?.let{
                        val leftEarPos = leftEar.position
                    }

                    if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY){
                        val smileProb = face.smilingProbability
                    }

                    if(face.trackingId != FirebaseVisionFace.INVALID_ID){
                        val id = face.trackingId
                    }
                }
            }
    }

    private fun faceOptionsExample(){
        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
    }
    private fun processFaceList(faces: List<FirebaseVisionFace>){
        for (face in faces){
            val bound = face.boundingBox
            val rotY = face.headEulerAngleY
            val rotZ = face.headEulerAngleZ

            val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
            }

            val leftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
            val upperLipBottumContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points

            if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY){
                val smileProb = face.smilingProbability
            }
            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY){
                val rightEyeOpenProb = face.rightEyeOpenProbability
            }

            if(face.trackingId != FirebaseVisionFace.INVALID_ID){
                val id = face.trackingId
            }
        }
    }
}