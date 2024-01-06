package com.jh.presentation.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun convertImage(byteArray: ByteArray): ImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()