package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CameraMotion
import com.example.model.PromptPresetsData
import com.example.model.VideoAspectRatio
import com.example.service.PromptEnhancer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MotionCraft AI", appName)
    }

    @Test
    fun `verify all 8 prompt presets exist`() {
        assertEquals(8, PromptPresetsData.presets.size)
        val names = PromptPresetsData.presets.map { it.name }
        assertTrue(names.contains("Cinematic Camera"))
        assertTrue(names.contains("Nature & Landscape"))
        assertTrue(names.contains("Product Showcase"))
        assertTrue(names.contains("Islamic Background"))
        assertTrue(names.contains("Fantasy & Animation"))
        assertTrue(names.contains("Realistic Human Motion"))
        assertTrue(names.contains("Slow Zoom & Pan"))
        assertTrue(names.contains("Lighting & Particles"))
    }

    @Test
    fun `verify prompt enhancer includes camera cues and aspect ratio`() {
        val enhanced = PromptEnhancer.enhance(
            userPrompt = "Cyberpunk street with neon rain",
            cameraMotion = CameraMotion.PUSH_IN,
            aspectRatio = VideoAspectRatio.PORTRAIT_9_16,
            motionStrength = 6.0f
        )
        assertTrue(enhanced.contains("slow cinematic push-in"))
        assertTrue(enhanced.contains("9:16"))
        assertTrue(enhanced.contains("Cyberpunk street"))
    }
}
