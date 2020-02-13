package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.image_face_detection.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FaceDetection : AppCompatActivity() {
    var currentPhotoPath: String? = null

    companion object {
        private const val TAG = "Face detection"
        private const val TAKE_PICTURE = 0
        private const val SELECT_PICTURE = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)
    }

    fun cameraFaceDetection(view: View) {
        dispatchTakePictureIntent()
    }

    fun galleryFaceDetection(view: View) {
        val selectPicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(selectPicture, SELECT_PICTURE)
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val photoFlie: File? = try {
            createImageFile()
        } catch (e: Exception) {
            Log.e(TAG, "exception for camera $e")
            null
        }

        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.cameraview.fileprovider",
            photoFlie!!)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, TAKE_PICTURE)
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
            if (requestCode == TAKE_PICTURE) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath,options)

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
                imageFaceDetection.setImageBitmap(rotatedBitmap)
            } else if (requestCode == SELECT_PICTURE) {
                val selectedPicture = data?.data
                val selectedPictureBitmap =
                    BitmapFactory.decodeStream(contentResolver.openInputStream(selectedPicture!!))
                imageFaceDetection.setImageBitmap(selectedPictureBitmap)
            }

        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}
