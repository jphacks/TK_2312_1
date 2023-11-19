package com.atssystem.model

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val warnings: Int,
    val appName: String,
    val icon: Drawable?,
    val time: Long,
    val url: String?
)
