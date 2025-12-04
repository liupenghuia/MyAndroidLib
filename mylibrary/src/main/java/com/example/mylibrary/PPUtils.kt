package com.example.mylibrary

import android.content.Context
import android.widget.Toast

object PPUtils {
    private var appContext: Context? = null

    fun init(context: Context) {
        this.appContext = context.applicationContext
    }

    fun showToast(text: String) {
        appContext?.let {
            Toast.makeText(it, text, Toast.LENGTH_SHORT).show()
        } ?: error("AppUtils 未初始化，请调用 AppUtils.init(context)")
    }
}