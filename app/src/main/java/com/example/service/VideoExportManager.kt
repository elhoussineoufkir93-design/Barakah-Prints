package com.example.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object VideoExportManager {

    suspend fun saveVideoToMovies(
        context: Context,
        sourceUriString: String,
        filenamePrefix: String = "MotionCraft_Video"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val fileName = "${filenamePrefix}_${System.currentTimeMillis()}.mp4"

            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/MotionCraft")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val targetUri = contentResolver.insert(collection, contentValues)
                ?: return@withContext Result.failure(Exception("Could not create MediaStore entry"))

            val inputStream = when {
                sourceUriString.startsWith("android.resource://") || sourceUriString.startsWith("content://") -> {
                    contentResolver.openInputStream(Uri.parse(sourceUriString))
                }
                sourceUriString.startsWith("file://") -> {
                    FileInputStream(File(Uri.parse(sourceUriString).path ?: sourceUriString))
                }
                else -> {
                    FileInputStream(File(sourceUriString))
                }
            } ?: return@withContext Result.failure(Exception("Unable to read source video file"))

            val outputStream = contentResolver.openOutputStream(targetUri)
                ?: return@withContext Result.failure(Exception("Unable to open output video stream"))

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                contentResolver.update(targetUri, contentValues, null, null)
            }

            Result.success(targetUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareVideo(context: Context, videoUriString: String, promptTitle: String) {
        try {
            val shareUri: Uri = when {
                videoUriString.startsWith("content://") -> Uri.parse(videoUriString)
                videoUriString.startsWith("file://") || !videoUriString.startsWith("http") -> {
                    val file = if (videoUriString.startsWith("file://")) {
                        File(Uri.parse(videoUriString).path ?: "")
                    } else {
                        File(videoUriString)
                    }
                    if (file.exists()) {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    } else {
                        Uri.parse(videoUriString)
                    }
                }
                else -> Uri.parse(videoUriString)
            }

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, shareUri)
                putExtra(Intent.EXTRA_TEXT, "Created with MotionCraft AI: $promptTitle")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Share AI Video via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
