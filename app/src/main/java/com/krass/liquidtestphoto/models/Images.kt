package com.krass.liquidtestphoto.models

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import java.io.File

class Images private constructor() {
    private lateinit var images: SnapshotStateList<Uri>
    companion object {

        @Volatile private var instance: Images? = null

        fun getInstance() =
            Images.instance ?: synchronized(this) { // synchronized to avoid concurrency problem
                Images.instance ?: Images().also { Images.instance = it }
            }

        fun getStoredImages(context: Context){
            getInstance().getImages().clear()
            val directory = getFolderDirectory(context)

            if (directory.exists()) {
                val allFiles: Array<out File>? = directory.listFiles()
                if (allFiles != null) {
                    for (file in allFiles) {
                        val uri = Uri.fromFile(file)
                        if(uri.path?.split("/")?.last()?.contains("_") == true) {
                            getInstance().getImages().add(Uri.fromFile(file))
                            Log.d("file", file.name)
                        }
                    }
                }
            }
        }

        fun createZipFileWithZip4j(uris: SnapshotStateList<Uri>, outputZipFile: File): File {
            val zipFile = ZipFile(outputZipFile)
            val parameters = ZipParameters().apply {
                compressionMethod = Zip4jConstants.COMP_DEFLATE
                compressionLevel = Zip4jConstants.DEFLATE_LEVEL_ULTRA
            }
            val files = ArrayList<File>()
            uris.forEach {
                uri -> files.add(uri.path?.let { File(it) }!!)
            }
            zipFile.addFiles(files, parameters)
            return outputZipFile
        }

        fun getFolderDirectory(context: Context): File {
            val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val folder = sharedPref.getInt("folder", 0)
            //Generating a file name
            return File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Samples $folder")
        }
    }

    fun getImages(): SnapshotStateList<Uri> {
        if (!this::images.isInitialized) {
            images = mutableStateListOf<Uri>()
        }
        return images
    }

}