package com.lazard.nyapp.picturetaker

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File

class PictureTakerFileProvider: FileProvider()

fun File.getFileProviderUri(context:Context) = FileProvider.getUriForFile(context,"${context.packageName}.picturetaker.fileprovider",this)