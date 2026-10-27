package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AppDatabase
import com.example.data.SettingsManager
import com.example.data.VideoProjectEntity
import com.example.model.AiProvider
import com.example.model.CameraMotion
import com.example.model.GenerationJob
import com.example.model.GenerationStep
import com.example.model.PromptPreset
import com.example.model.PromptPresetsData
import com.example.model.VideoAspectRatio
import com.example.service.PromptEnhancer
import com.example.service.VideoExportManager
import com.example.service.VideoGenerationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StudioTab(val title: String, val icon: String) {
    STUDIO("Create", "MovieFilter"),
    PREVIEW("Player", "PlayCircle"),
    HISTORY("Projects", "VideoLibrary"),
    SETTINGS("API & Guide", "Tune")
}

data class ShowcaseSample(
    val id: String,
    val name: String,
    val tag: String,
    val drawableResId: Int,
    val defaultPrompt: String,
    val presetMotion: CameraMotion
)

class MotionCraftViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val settingsManager = SettingsManager(application)
    private val generationEngine = VideoGenerationEngine(application, database, settingsManager)

    // Current Navigation Tab
    private val _selectedTab = MutableStateFlow(StudioTab.STUDIO)
    val selectedTab: StateFlow<StudioTab> = _selectedTab.asStateFlow()

    // Showcase Sample Images for Instant Tryout
    val showcaseSamples = listOf(
        ShowcaseSample(
            id = "cyberpunk",
            name = "Cyberpunk Supercar",
            tag = "Tokyo Rain",
            drawableResId = R.drawable.sample_cyberpunk_car_1793140934131,
            defaultPrompt = "Animate this cyberpunk supercar with slow cinematic camera push-in, headlights beaming in wet Tokyo rain, reflection ripples in puddles, and smooth camera glide.",
            presetMotion = CameraMotion.PUSH_IN
        ),
        ShowcaseSample(
            id = "waterfall",
            name = "Alpine Cascade",
            tag = "Nature & Mist",
            drawableResId = R.drawable.sample_nature_waterfall_1793140947375,
            defaultPrompt = "Crystalline emerald waterfall cascading into turquoise pool, gentle mist floating upwards, sunbeams piercing through mossy forest trees, soft breeze.",
            presetMotion = CameraMotion.TILT_DOWN
        )
    )

    // Input States
    private val _selectedImageUri = MutableStateFlow<String?>(
        "android.resource://${application.packageName}/${R.drawable.sample_cyberpunk_car_1793140934131}"
    )
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _prompt = MutableStateFlow(
        "Animate this image with a slow cinematic camera push-in, realistic natural movement, soft volumetric lighting, subtle environmental motion, and smooth transitions."
    )
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _aspectRatio = MutableStateFlow(VideoAspectRatio.PORTRAIT_9_16)
    val aspectRatio: StateFlow<VideoAspectRatio> = _aspectRatio.asStateFlow()

    private val _cameraMotion = MutableStateFlow(CameraMotion.PUSH_IN)
    val cameraMotion: StateFlow<CameraMotion> = _cameraMotion.asStateFlow()

    private val _durationSeconds = MutableStateFlow(5)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _motionStrength = MutableStateFlow(5.0f)
    val motionStrength: StateFlow<Float> = _motionStrength.asStateFlow()

    private val _selectedPreset = MutableStateFlow<PromptPreset?>(PromptPresetsData.presets.first())
    val selectedPreset: StateFlow<PromptPreset?> = _selectedPreset.asStateFlow()

    // Active Generation State
    private val _activeJob = MutableStateFlow<GenerationJob?>(null)
    val activeJob: StateFlow<GenerationJob?> = _activeJob.asStateFlow()

    private var generationJobScope: Job? = null

    // Active Preview Video
    private val _activePreview = MutableStateFlow<VideoProjectEntity?>(null)
    val activePreview: StateFlow<VideoProjectEntity?> = _activePreview.asStateFlow()

    // Toast/Feedback message
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Project History from Database
    val projects: StateFlow<List<VideoProjectEntity>> = database.projectDao()
        .getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: StudioTab) {
        _selectedTab.value = tab
    }

    fun setImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun selectShowcase(sample: ShowcaseSample) {
        val app = getApplication<Application>()
        _selectedImageUri.value = "android.resource://${app.packageName}/${sample.drawableResId}"
        _prompt.value = sample.defaultPrompt
        _cameraMotion.value = sample.presetMotion
    }

    fun setPrompt(text: String) {
        _prompt.value = text
    }

    fun applyPreset(preset: PromptPreset) {
        _selectedPreset.value = preset
        _prompt.value = preset.promptText
    }

    fun enhancePrompt() {
        val enhanced = PromptEnhancer.enhance(
            userPrompt = _prompt.value,
            cameraMotion = _cameraMotion.value,
            aspectRatio = _aspectRatio.value,
            motionStrength = _motionStrength.value
        )
        _prompt.value = enhanced
        _feedbackMessage.value = "Prompt enhanced with cinematic cues!"
    }

    fun setAspectRatio(ratio: VideoAspectRatio) {
        _aspectRatio.value = ratio
    }

    fun setCameraMotion(motion: CameraMotion) {
        _cameraMotion.value = motion
    }

    fun setDuration(seconds: Int) {
        _durationSeconds.value = seconds
    }

    fun setMotionStrength(strength: Float) {
        _motionStrength.value = strength
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun startGeneration() {
        if (_activeJob.value?.status in listOf(
                GenerationStep.ANALYZING_IMAGE,
                GenerationStep.PROMPT_ENHANCEMENT,
                GenerationStep.DIFFUSION_SYNTHESIS,
                GenerationStep.ENCODING_MP4
            )
        ) {
            return
        }

        generationJobScope?.cancel()
        generationJobScope = viewModelScope.launch {
            val provider = settingsManager.selectedProvider.value
            generationEngine.generateVideo(
                imageUriString = _selectedImageUri.value,
                prompt = _prompt.value,
                aspectRatio = _aspectRatio.value,
                cameraMotion = _cameraMotion.value,
                durationSeconds = _durationSeconds.value,
                motionStrength = _motionStrength.value,
                provider = provider
            ).collect { job ->
                _activeJob.value = job
                if (job.status == GenerationStep.COMPLETE && job.outputVideoUri != null) {
                    val entity = VideoProjectEntity(
                        id = job.id,
                        title = job.prompt.take(40),
                        prompt = job.prompt,
                        aspectRatio = job.aspectRatio.id,
                        durationSeconds = job.durationSeconds,
                        videoUri = job.outputVideoUri,
                        thumbnailUri = job.thumbnailUri,
                        providerName = job.provider.displayName,
                        isDemo = job.isDemo,
                        timestamp = job.timestamp
                    )
                    _activePreview.value = entity
                    _selectedTab.value = StudioTab.PREVIEW
                }
            }
        }
    }

    fun cancelGeneration() {
        generationJobScope?.cancel()
        _activeJob.value = null
        _feedbackMessage.value = "Generation cancelled."
    }

    fun openPreview(project: VideoProjectEntity) {
        _activePreview.value = project
        _selectedTab.value = StudioTab.PREVIEW
    }

    fun saveToDevice(project: VideoProjectEntity) {
        viewModelScope.launch {
            _feedbackMessage.value = "Saving video to Movies/MotionCraft..."
            val result = VideoExportManager.saveVideoToMovies(
                context = getApplication(),
                sourceUriString = project.videoUri,
                filenamePrefix = "MotionCraft_${project.id.take(6)}"
            )
            result.onSuccess {
                _feedbackMessage.value = "Saved to Gallery / Movies folder!"
            }.onFailure { err ->
                _feedbackMessage.value = "Export failed: ${err.message}"
            }
        }
    }

    fun shareProject(project: VideoProjectEntity) {
        VideoExportManager.shareVideo(
            context = getApplication(),
            videoUriString = project.videoUri,
            promptTitle = project.title
        )
    }

    fun deleteProject(project: VideoProjectEntity) {
        viewModelScope.launch {
            database.projectDao().deleteProject(project)
            if (_activePreview.value?.id == project.id) {
                _activePreview.value = null
            }
            _feedbackMessage.value = "Project removed."
        }
    }

    fun clearAllProjects() {
        viewModelScope.launch {
            database.projectDao().clearAll()
            _activePreview.value = null
            _feedbackMessage.value = "All projects cleared."
        }
    }
}
