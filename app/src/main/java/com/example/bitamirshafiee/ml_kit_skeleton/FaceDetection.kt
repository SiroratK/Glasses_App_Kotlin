package com.example.bitamirshafiee.ml_kit_skeleton

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.content.FileProvider
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.bottom_sheet_face_detection.*
import kotlinx.android.synthetic.main.image_detect_mlkit.*
import kotlinx.android.synthetic.main.image_face_detection.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FaceDetection : AppCompatActivity() {

    companion object {
        private const val TAG = "FaceDetection"
        private const val TAKE_PICTURE = 0
        private const val SELECT_PICTURE = 1

        private const val PERMISSION_CAMERA = 1
        private const val PERMISSION_WRITE = 2
        private const val PERMISSION_READ = 3
    }

    var currentPhotoPath: String? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var result: String? = null

    // Max width (portrait mode)
    private var mImageMaxWidth: Int? = null
    // Max height (portrait mode)
    private var mImageMaxHeight: Int? = null

    private var classifier: Classifier? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)


        classifier = Classifier(applicationContext, "converted_Xception_model.tflite")


    }

    fun cameraFaceDetection(view: View) {
        //check if we have camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //if not request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )

            //if yes take the picture
        } else {
            dispatchTakePictureIntent()

        }
    }


    private fun dispatchTakePictureIntent() {
        //check if we have access gallery permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //write permission is not granted lets request
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE
            )
        } else {
            //if yes take the picture
            writeOnFile()
        }

    }

    private fun writeOnFile() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        intent.resolveActivity(packageManager)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.d(TAG, "exception: $ex")
            null

        }

        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.bitamirshafiee.ml_kit_skeleton.fileprovider",
            photoFile!!
        )
        Log.d(TAG, "photo uri: $photoURI")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    fun galleryFaceDetection(view: View) {
        //request permission read if not granted for gallery
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ
            )
        } else {
            val selectPicture =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(selectPicture, SELECT_PICTURE)
        }
    }


    private fun createImageFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())

        val storageDirectory: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("image_$timeStamp", ".jpg", storageDirectory).apply {
            currentPhotoPath = this.absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            if (requestCode == SELECT_PICTURE) {
                val selectedImage = data?.data
                val selectedImageBitmap =
                    BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage))
                image_face_detection.setImageBitmap(selectedImageBitmap)
                runFaceDetection(imageProcess(selectedImageBitmap))
            } else if (requestCode == TAKE_PICTURE) {

                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)

                val ei = ExifInterface(currentPhotoPath)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                var rotatedBitmap: Bitmap? = null

                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
                    else -> bitmap
                }

                image_face_detection.setImageBitmap(rotatedBitmap)
                runFaceDetection(imageProcess(rotatedBitmap!!))
            }
        }
    }

    private fun runTFlite(bitmap: Bitmap,faces: MutableList<FirebaseVisionFace>) {

        val prediction = classifier
        if(prediction != null){
            result = prediction.predict(bitmap)
        }

        if (result == "oblong") {
            Log.e(TAG, "oblong")
            setContentView(R.layout.activity_diamond)
            image_face_detection_ml.setImageBitmap(bitmap)

            //run face detection after click only!!

        } else if (result == "heart") {
            Log.e(TAG, "heart")
            setContentView(R.layout.activity_heart)
            image_face_detection_ml.setImageBitmap(bitmap)


        }else if (result == "oval"){
            Log.e(TAG,"oval")
            setContentView(R.layout.activity_oval)
            image_face_detection_ml.setImageBitmap(bitmap)


        }else if (result == "square"){
            Log.e(TAG,"square")
            setContentView(R.layout.activity_rec)
            image_face_detection_ml.setImageBitmap(bitmap)

        }

        else if (result == "round")
        {   Log.e(TAG,"round")
            setContentView(R.layout.activity_round)
            image_face_detection_ml.setImageBitmap(bitmap)

        }
        processFaceDetection(faces)
    }

    private fun runFaceDetection(bitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS).build()

        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Dowloading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        detector.detectInImage(image)
            .addOnSuccessListener {
                //have pic
                if (it.size == 0){
                    Toast.makeText(this, "No face detected please try again", Toast.LENGTH_SHORT).show()
                    //popup no face detection result please select new Image
//                    setContentView(R.layout.activity_face_detection)
                    progressDialog.dismiss()
                }
                else{
                    runTFlite(bitmap,it)
                    progressDialog.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "process Failed", Toast.LENGTH_SHORT).show()
                //have no pic error
                progressDialog.dismiss()
            }
    }


    private fun processFaceDetection(faces: MutableList<FirebaseVisionFace>) {

        graphic_overlay2.clear()
        var glassesBitmap: Bitmap

        if (faces.size == 0) {
            Toast.makeText(this, "No face detected result", Toast.LENGTH_SHORT).show()
            return
        }
        for (i in faces.indices) {
            val face = faces[i]
            if (result == "oblong") {

                val btn1 = findViewById<CircularImageView>(R.id.diamond_1)
                btn1.setOnClickListener {
                    // your code to perform when the user clicks on the button
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.round_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn2 = findViewById<CircularImageView>(R.id.diamond_2)
                btn2.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.cat_eye1
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn3 = findViewById<CircularImageView>(R.id.diamond_3)
                btn3.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.cat_eye_2
                    )
                    setGrapphic(glassesBitmap, face)
                }
            } else if (result == "round") {
                val btn1 = findViewById<CircularImageView>(R.id.round_1)
                btn1.setOnClickListener {
                    // your code to perform when the user clicks on the button
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.water_cat
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn2 = findViewById<CircularImageView>(R.id.round_2)
                btn2.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.rec_glasses
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn3 = findViewById<CircularImageView>(R.id.round_3)
                btn3.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.hex_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
            } else if (result == "oval") {
                val btn1 = findViewById<CircularImageView>(R.id.oval_1)
                btn1.setOnClickListener {
                    // your code to perform when the user clicks on the button
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.waterdrop_glasses
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn2 = findViewById<CircularImageView>(R.id.oval_2)
                btn2.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.rec_glasses
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn3 = findViewById<CircularImageView>(R.id.oval_3)
                btn3.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.round_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
            } else if (result == "square") {
                val btn1 = findViewById<CircularImageView>(R.id.rec_1)
                btn1.setOnClickListener {
                    // your code to perform when the user clicks on the button
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.waterdrop_glasses
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn2 = findViewById<CircularImageView>(R.id.rec_2)
                btn2.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.oval_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn3 = findViewById<CircularImageView>(R.id.rec_3)
                btn3.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.round_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
            } else if (result == "heart") {
                val btn1 = findViewById<CircularImageView>(R.id.heart_1)
                btn1.setOnClickListener {
                    // your code to perform when the user clicks on the button
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.round_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn2 = findViewById<CircularImageView>(R.id.heart_2)
                btn2.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.cat_eye_2
                    )
                    setGrapphic(glassesBitmap, face)
                }
                val btn3 = findViewById<CircularImageView>(R.id.heart_3)
                btn3.setOnClickListener {
                    glassesBitmap = BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.oval_rec
                    )
                    setGrapphic(glassesBitmap, face)
                }
            } else {
                Toast.makeText(this, "result error", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this, "$result", Toast.LENGTH_SHORT).show()
//            val smilingProb = face.smilingProbability
//
//            smiling += "face $i : $smilingProb \n"

//            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//            text_view_image_labeling.text = smiling
        }
    }

    private fun setGrapphic(glassesBitmap: Bitmap, face: FirebaseVisionFace) {
        graphic_overlay2.clear()
        val faceGraphic = FaceContourGraphic(graphic_overlay2, glassesBitmap)
        graphic_overlay2.add(faceGraphic)
        faceGraphic.updateFace(face)
    }

    private fun imageProcess(bitmap: Bitmap): Bitmap {

        val targetedSize = getTargetedWidthHeight()

        val targetWidth = targetedSize.first
        val maxHeight = targetedSize.second

        // Determine how much to scale down the image
        val scaleFactor = Math.max(
            bitmap.getWidth().toFloat() / targetWidth.toFloat(),
            bitmap.getHeight().toFloat() / maxHeight.toFloat()
        )

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.getWidth() / scaleFactor).toInt(),
            (bitmap.getHeight() / scaleFactor).toInt(),
            true
        )

        return resizedBitmap

    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = getImageMaxWidth()!!
        val maxHeightForPortraitMode = getImageMaxHeight()!!
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxWidth(): Int? {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = image_face_detection.getWidth()
        }

        return mImageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxHeight(): Int? {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight = image_face_detection.getHeight()
        }

        return mImageMaxHeight
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CAMERA -> {
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //camera permission is granted
                    dispatchTakePictureIntent()
                }
            }
            PERMISSION_WRITE -> {
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeOnFile()
                }
            }
            PERMISSION_READ -> {
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val selectPicture =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(selectPicture, SELECT_PICTURE)
                }
            }
        }
    }
}
