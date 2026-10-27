package com.example.service

import com.example.model.CameraMotion
import com.example.model.VideoAspectRatio

object PromptEnhancer {

    fun enhance(
        userPrompt: String,
        cameraMotion: CameraMotion,
        aspectRatio: VideoAspectRatio,
        motionStrength: Float
    ): String {
        val trimmed = userPrompt.trim()
        val motionDescription = when (cameraMotion) {
            CameraMotion.PUSH_IN -> "slow cinematic push-in tracking towards central subject"
            CameraMotion.PULL_OUT -> "slow cinematic camera pull-out revealing wider ambient scenery"
            CameraMotion.PAN_RIGHT -> "smooth horizontal camera pan from left to right"
            CameraMotion.PAN_LEFT -> "smooth horizontal camera pan from right to left"
            CameraMotion.TILT_DOWN -> "smooth vertical tilt movement flowing gently downward"
            CameraMotion.ORBIT -> "subtle orbital camera parallax arc around focal point"
            CameraMotion.STATIC_MOTION -> "tripod locked shot with natural organic motion inside frame"
        }

        val dynamics = when {
            motionStrength <= 3.0f -> "subtle, gentle natural movement, high temporal stability"
            motionStrength <= 7.0f -> "balanced dynamic motion, fluid physical animation, smooth transitions"
            else -> "energetic expressive motion, pronounced cinematic movement, high temporal action"
        }

        val compositionCue = when (aspectRatio) {
            VideoAspectRatio.PORTRAIT_9_16 -> "vertical 9:16 composition optimized for Reels and TikTok, sharp foreground subject depth"
            VideoAspectRatio.LANDSCAPE_16_9 -> "cinematic 16:9 widescreen composition, expansive depth of field"
            VideoAspectRatio.SQUARE_1_1 -> "balanced 1:1 square frame focus, symmetrical lighting"
        }

        val baseText = if (trimmed.isNotBlank()) trimmed else "Animate this scene with lifelike realistic motion"

        return "$baseText. Camera: $motionDescription. Motion dynamics: $dynamics. Lighting: volumetric ambient illumination, soft shadows, detailed textures. $compositionCue. Preserving original subject integrity and colors."
    }
}
