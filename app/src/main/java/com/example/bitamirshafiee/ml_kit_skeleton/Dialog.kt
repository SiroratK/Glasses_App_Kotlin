package com.example.bitamirshafiee.ml_kit_skeleton

import android.app.ProgressDialog
import android.os.Bundle
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity

class Dialog :AppCompatActivity(){
    val progressDialog = ProgressDialog(this)

    fun start(){
        progressDialog.setMessage("Dowloading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun stop(){
        progressDialog.dismiss()
    }
}