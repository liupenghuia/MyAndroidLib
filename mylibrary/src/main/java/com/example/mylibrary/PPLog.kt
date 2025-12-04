package com.example.mylibrary

object PPLog {
    private const val TAG = "PPPPLG"

    fun d(message: String) {
        println("DEBUG: [$TAG] $message")
    }

    fun e(message: String, throwable: Throwable? = null) {
        println("ERROR: [$TAG] $message")
        throwable?.printStackTrace()
    }

}