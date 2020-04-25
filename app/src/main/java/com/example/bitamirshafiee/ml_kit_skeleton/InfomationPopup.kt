package com.example.bitamirshafiee.ml_kit_skeleton

import android.os.Bundle
import android.view.*
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_heart.*
import kotlinx.android.synthetic.main.layout_popup.*

class HeartPopUp : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.heart_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}

class RoundPopUp : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.round_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}

class OblongPopUp : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.oblong_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}
class SqurePopUp : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.square_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}

class OvalPopUp : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.oval_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}

class NodetectPopup : DialogFragment(){
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width,height)

        val textCancel = dialog.findViewById<TextView>(R.id.textOk)
        textCancel.setOnClickListener { dialog.dismiss() }
    }
    override  fun onCreateView(inflater:LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.layout_popup,container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(R.color.colorTransparent)
        setStyle(DialogFragment.STYLE_NO_INPUT, android.R.style.Theme)
    }
}
