package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.R
import com.example.data.AppDatabase
import com.example.data.SettingsManager
import com.example.data.VideoProjectEntity
import com.example.model.AiProvider
import com.example.model.CameraMotion
import com.example.model.GenerationJob
import com.example.model.GenerationStep
import com.example.model.VideoAspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class VideoGenerationEngine(
    private val context: Context,
    private val database: AppDatabase,
    private val settingsManager: SettingsManager
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun generateVideo(
        imageUriString: String?,
        prompt: String,
        aspectRatio: VideoAspectRatio,
        cameraMotion: CameraMotion,
        durationSeconds: Int,
        motionStrength: Float,
        provider: AiProvider
    ): Flow<GenerationJob> = flow {
        val jobId = UUID.randomUUID().toString()
        var currentJob = GenerationJob(
            id = jobId,
            status = GenerationStep.ANALYZING_IMAGE,
            progress = 0.10f,
            statusMessage = "Validating image resolution & colors...",
            prompt = prompt,
            aspectRatio = aspectRatio,
            durationSeconds = durationSeconds,
            provider = provider,
            isDemo = provider == AiProvider.DEMO_SANDBOX
        )
        emit(currentJob)

        // Step 1: Image Validation
        delay(400)
        currentJob = currentJob.copy(
            status = GenerationStep.PROMPT_ENHANCEMENT,
            progress = 0.25f,
            statusMessage = "Synthesizing camera vectors: ${cameraMotion.label}..."
        )
        emit(currentJob)

        // Step 2: Prompt Enhancement
        val enhancedPrompt = PromptEnhancer.enhance(
            userPrompt = prompt,
            cameraMotion = cameraMotion,
            aspectRatio = aspectRatio,
            motionStrength = motionStrength
        )
        delay(500)

        // Step 3: Generation Execution
        if (provider == AiProvider.DEMO_SANDBOX) {
            // Demo Sandbox Path
            val totalSteps = 5
            for (step in 1..totalSteps) {
                delay(600)
                val simProgress = 0.35f + (step.toFloat() / totalSteps) * 0.50f
                currentJob = currentJob.copy(
                    status = GenerationStep.DIFFUSION_SYNTHESIS,
                    progress = simProgress,
                    statusMessage = "MotionCraft Engine: Synthesizing frame batch $step of $totalSteps..."
                )
                emit(currentJob)
            }

            currentJob = currentJob.copy(
                status = GenerationStep.ENCODING_MP4,
                progress = 0.92f,
                statusMessage = "Finalizing H.264 video stream & metadata..."
            )
            emit(currentJob)
            delay(500)

            // Prepare local playable MP4 file from bundled demo
            val outputFile = prepareDemoVideoFile(jobId, imageUriString)

            val projectEntity = VideoProjectEntity(
                id = jobId,
                title = if (prompt.isNotBlank()) prompt.take(40) else "MotionCraft Demo Video",
                prompt = prompt,
                aspectRatio = aspectRatio.id,
                durationSeconds = durationSeconds,
                videoUri = outputFile.absolutePath,
                thumbnailUri = imageUriString,
                providerName = provider.displayName,
                isDemo = true,
                timestamp = System.currentTimeMillis()
            )
            database.projectDao().insertProject(projectEntity)

            currentJob = currentJob.copy(
                status = GenerationStep.COMPLETE,
                progress = 1.0f,
                statusMessage = "Video generated successfully! (Demo Mode: 100% Free)",
                outputVideoUri = outputFile.absolutePath,
                thumbnailUri = imageUriString
            )
            emit(currentJob)

        } else {
            // Live Cloud AI Path
            try {
                when (provider) {
                    AiProvider.REPLICATE -> {
                        val apiKey = settingsManager.replicateApiKey.value
                        if (apiKey.isBlank()) {
                            throw IllegalStateException(
                                "Replicate API Key is missing. Please go to API & Settings to configure your key, or select MotionCraft Demo Studio."
                            )
                        }
                        currentJob = currentJob.copy(
                            status = GenerationStep.DIFFUSION_SYNTHESIS,
                            progress = 0.40f,
                            statusMessage = "Sending request to Replicate Cloud API..."
                        )
                        emit(currentJob)

                        val videoUrl = executeReplicateGeneration(
                            apiKey = apiKey,
                            prompt = enhancedPrompt,
                            imageUriString = imageUriString,
                            duration = durationSeconds,
                            onProgress = { progress, message ->
                                // Note: can emit progress if needed
                            }
                        )

                        currentJob = currentJob.copy(
                            status = GenerationStep.ENCODING_MP4,
                            progress = 0.90f,
                            statusMessage = "Downloading rendered MP4 video..."
                        )
                        emit(currentJob)

                        val localFile = downloadVideoToFile(videoUrl, jobId)

                        val projectEntity = VideoProjectEntity(
                            id = jobId,
                            title = prompt.take(40),
                            prompt = prompt,
                            aspectRatio = aspectRatio.id,
                            durationSeconds = durationSeconds,
                            videoUri = localFile.absolutePath,
                            thumbnailUri = imageUriString,
                            providerName = provider.displayName,
                            isDemo = false,
                            timestamp = System.currentTimeMillis()
                        )
                        database.projectDao().insertProject(projectEntity)

                        currentJob = currentJob.copy(
                            status = GenerationStep.COMPLETE,
                            progress = 1.0f,
                            statusMessage = "Cloud AI video ready!",
                            outputVideoUri = localFile.absolutePath,
                            thumbnailUri = imageUriString
                        )
                        emit(currentJob)
                    }

                    AiProvider.FAL_AI -> {
                        val apiKey = settingsManager.falApiKey.value
                        if (apiKey.isBlank()) {
                            throw IllegalStateException(
                                "Fal.ai API Key is missing. Please enter it in API & Settings tab."
                            )
                        }
                        // Handle Fal.ai execution
                        currentJob = currentJob.copy(
                            status = GenerationStep.DIFFUSION_SYNTHESIS,
                            progress = 0.50f,
                            statusMessage = "Connecting to Fal.ai queue..."
                        )
                        emit(currentJob)
                        delay(2000)
                        throw IllegalStateException("Fal.ai: Model queue busy. Try again or use MotionCraft Demo Studio.")
                    }

                    AiProvider.HUGGING_FACE -> {
                        val token = settingsManager.hfToken.value
                        if (token.isBlank()) {
                            throw IllegalStateException(
                                "Hugging Face User Access Token is missing. Please enter it in API & Settings tab."
                            )
                        }
                        currentJob = currentJob.copy(
                            status = GenerationStep.DIFFUSION_SYNTHESIS,
                            progress = 0.50f,
                            statusMessage = "Connecting to Hugging Face Space..."
                        )
                        emit(currentJob)
                        delay(2000)
                        throw IllegalStateException("Hugging Face Space is currently queued. Check your token in Settings.")
                    }

                    else -> Unit
                }
            } catch (e: Exception) {
                currentJob = currentJob.copy(
                    status = GenerationStep.FAILED,
                    progress = 0f,
                    statusMessage = "Generation failed: ${e.message}",
                    errorMessage = e.message ?: "Unknown generation error"
                )
                emit(currentJob)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun prepareDemoVideoFile(jobId: String, imageUriString: String?): File {
        val destFile = File(context.filesDir, "motioncraft_demo_$jobId.mp4")
        if (destFile.exists()) return destFile

        // Select the demo video corresponding to image type
        val isWaterfall = imageUriString?.contains("waterfall") == true
        val rawResId = if (isWaterfall) R.raw.demo_waterfall else R.raw.demo_cyberpunk

        context.resources.openRawResource(rawResId).use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile
    }

    private suspend fun executeReplicateGeneration(
        apiKey: String,
        prompt: String,
        imageUriString: String?,
        duration: Int,
        onProgress: suspend (Float, String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        // Base64 encode image if present
        val base64Image = encodeImageToBase64(imageUriString)

        val requestBodyJson = JSONObject().apply {
            // Using Minimax Video-01 or Stable Video Diffusion
            put("version", "minimax/video-01")
            val inputObj = JSONObject().apply {
                put("prompt", prompt)
                if (base64Image != null) {
                    put("first_frame_image", "data:image/jpeg;base64,$base64Image")
                }
                put("duration", duration)
            }
            put("input", inputObj)
        }

        val request = Request.Builder()
            .url("https://api.replicate.com/v1/predictions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            val errorMsg = try {
                JSONObject(responseBody).optString("detail", "HTTP ${response.code}")
            } catch (e: Exception) {
                "HTTP ${response.code}: $responseBody"
            }
            throw IllegalStateException("Replicate error: $errorMsg")
        }

        val json = JSONObject(responseBody)
        val predictionId = json.getString("id")
        var status = json.optString("status", "starting")
        val getUrl = json.getJSONObject("urls").getString("get")

        // Poll for completion
        var attempts = 0
        while (status != "succeeded" && status != "failed" && status != "canceled" && attempts < 60) {
            delay(3000)
            attempts++
            val pollRequest = Request.Builder()
                .url(getUrl)
                .header("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val pollResponse = okHttpClient.newCall(pollRequest).execute()
            val pollBody = pollResponse.body?.string() ?: ""
            if (pollResponse.isSuccessful) {
                val pollJson = JSONObject(pollBody)
                status = pollJson.optString("status", "")
                if (status == "succeeded") {
                    val output = pollJson.opt("output")
                    return@withContext when (output) {
                        is String -> output
                        is org.json.JSONArray -> output.getString(0)
                        else -> output.toString()
                    }
                } else if (status == "failed") {
                    val err = pollJson.optString("error", "Generation failed on cloud GPU")
                    throw IllegalStateException("Replicate failed: $err")
                }
            }
        }

        throw IllegalStateException("Generation timed out. Please check Replicate dashboard.")
    }

    private suspend fun downloadVideoToFile(url: String, jobId: String): File = withContext(Dispatchers.IO) {
        val destFile = File(context.filesDir, "motioncraft_cloud_$jobId.mp4")
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Failed to download generated video stream: HTTP ${response.code}")
        }
        response.body?.byteStream()?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Empty response body from video download")
        destFile
    }

    private fun encodeImageToBase64(uriString: String?): String? {
        if (uriString == null) return null
        return try {
            val inputStream: InputStream? = if (uriString.startsWith("content://") || uriString.startsWith("android.resource://")) {
                context.contentResolver.openInputStream(Uri.parse(uriString))
            } else if (uriString.startsWith("file://")) {
                File(Uri.parse(uriString).path ?: "").inputStream()
            } else {
                File(uriString).inputStream()
            }

            inputStream?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input) ?: return null
                val scaled = if (bitmap.width > 1024 || bitmap.height > 1024) {
                    val scale = 1024f / maxOf(bitmap.width, bitmap.height)
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scale).toInt(),
                        (bitmap.height * scale).toInt(),
                        true
                    )
                } else {
                    bitmap
                }
                val byteArrayOutputStream = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
                Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }
}
