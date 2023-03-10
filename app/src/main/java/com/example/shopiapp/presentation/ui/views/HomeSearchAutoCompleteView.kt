package com.example.shopiapp.presentation.ui.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class HomeSearchAutoCompleteView(
    context: Context,
    attributeSet: AttributeSet
) : MaterialAutoCompleteTextView(context, attributeSet) {

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super@HomeSearchAutoCompleteView.performFiltering(msg.obj as CharSequence, msg.arg1)
        }
    }
    private val messageCode = 0

    private var delay: Long = 0

    private var loadingIndicator: ProgressBar? = null

    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        loadingIndicator?.let { it.visibility = View.VISIBLE }
        handler.removeMessages(messageCode)
        handler.sendMessageDelayed(handler.obtainMessage(messageCode, text), delay)
    }

    override fun onFilterComplete(count: Int) {
        loadingIndicator?.let { it.visibility = View.GONE }
        super.onFilterComplete(count)
    }

    fun setDelay(delay: Long) {
        this.delay = delay
    }

    fun setLoadIndicator(progressBar: ProgressBar) {
        loadingIndicator = progressBar
    }
}