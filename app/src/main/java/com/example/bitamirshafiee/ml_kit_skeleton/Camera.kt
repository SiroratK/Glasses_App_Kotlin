package com.example.bitamirshafiee.ml_kit_skeleton

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.image_face_detection.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Camera : AppCompatActivity() {

    var currentPhotoPath: String? = null
    var bottomSheetBehavior : BottomSheetBehavior<*>? = null

    companion object{
        private const val TAG = "FaceDetection"
        private const val TAKE_PICTURE = 0
        private const val SELECT_PICTURE = 1

        private const val PERMISSION_CAMERA = 1
        private const val PERMISSION_WRITE = 2
        private const val PERMISSION_READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    fun cameraFaceDetection(view: View){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            //if not request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),PERMISSION_CAMERA
            )

            //if yes take the picture
        }else{
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        //check if we have access gallery permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //write permission is not granted lets request
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),PERMISSION_WRITE
            )
        }else{
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
            Log.d(TAG,"exception: $ex")
            null

        }

        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.bitamirshafiee.ml_kit_skeleton.fileprovider",
            photoFile!!
        )
        Log.d(TAG,"photo uri: $photoURI")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())

        val storageDirectory : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("image_$timeStamp",".jpg", storageDirectory).apply {
            currentPhotoPath = this.absolutePath
        }
    }
}
