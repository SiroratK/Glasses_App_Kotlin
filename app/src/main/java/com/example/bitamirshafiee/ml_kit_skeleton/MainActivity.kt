package com.example.bitamirshafiee.ml_kit_skeleton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

        fun faceDetection(view: View){
        startActivity(Intent(this@MainActivity, FaceDetection::class.java))
    }

//    fun cameraStart(){
//
//    }

}
