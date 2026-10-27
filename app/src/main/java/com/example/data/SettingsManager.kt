package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AiProvider
import com.example.model.VideoAspectRatio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("motioncraft_settings", Context.MODE_PRIVATE)

    private val _selectedProvider = MutableStateFlow(loadProvider())
    val selectedProvider: StateFlow<AiProvider> = _selectedProvider.asStateFlow()

    private val _replicateApiKey = MutableStateFlow(prefs.getString(KEY_REPLICATE_KEY, "") ?: "")
    val replicateApiKey: StateFlow<String> = _replicateApiKey.asStateFlow()

    private val _falApiKey = MutableStateFlow(prefs.getString(KEY_FAL_KEY, "") ?: "")
    val falApiKey: StateFlow<String> = _falApiKey.asStateFlow()

    private val _hfToken = MutableStateFlow(prefs.getString(KEY_HF_TOKEN, "") ?: "")
    val hfToken: StateFlow<String> = _hfToken.asStateFlow()

    private val _defaultAspectRatio = MutableStateFlow(loadAspectRatio())
    val defaultAspectRatio: StateFlow<VideoAspectRatio> = _defaultAspectRatio.asStateFlow()

    private val _motionStrength = MutableStateFlow(prefs.getFloat(KEY_MOTION_STRENGTH, 5.0f))
    val motionStrength: StateFlow<Float> = _motionStrength.asStateFlow()

    private fun loadProvider(): AiProvider {
        val id = prefs.getString(KEY_PROVIDER, AiProvider.DEMO_SANDBOX.id)
        return AiProvider.values().firstOrNull { it.id == id } ?: AiProvider.DEMO_SANDBOX
    }

    private fun loadAspectRatio(): VideoAspectRatio {
        val id = prefs.getString(KEY_ASPECT_RATIO, VideoAspectRatio.PORTRAIT_9_16.id)
        return VideoAspectRatio.values().firstOrNull { it.id == id } ?: VideoAspectRatio.PORTRAIT_9_16
    }

    fun setProvider(provider: AiProvider) {
        prefs.edit().putString(KEY_PROVIDER, provider.id).apply()
        _selectedProvider.value = provider
    }

    fun setReplicateApiKey(key: String) {
        prefs.edit().putString(KEY_REPLICATE_KEY, key.trim()).apply()
        _replicateApiKey.value = key.trim()
    }

    fun setFalApiKey(key: String) {
        prefs.edit().putString(KEY_FAL_KEY, key.trim()).apply()
        _falApiKey.value = key.trim()
    }

    fun setHfToken(token: String) {
        prefs.edit().putString(KEY_HF_TOKEN, token.trim()).apply()
        _hfToken.value = token.trim()
    }

    fun setAspectRatio(ratio: VideoAspectRatio) {
        prefs.edit().putString(KEY_ASPECT_RATIO, ratio.id).apply()
        _defaultAspectRatio.value = ratio
    }

    fun setMotionStrength(strength: Float) {
        prefs.edit().putFloat(KEY_MOTION_STRENGTH, strength).apply()
        _motionStrength.value = strength
    }

    companion object {
        private const val KEY_PROVIDER = "selected_provider"
        private const val KEY_REPLICATE_KEY = "replicate_api_key"
        private const val KEY_FAL_KEY = "fal_api_key"
        private const val KEY_HF_TOKEN = "hf_token"
        private const val KEY_ASPECT_RATIO = "default_aspect_ratio"
        private const val KEY_MOTION_STRENGTH = "motion_strength"
    }
}
