package com.example.model

enum class VideoAspectRatio(
    val id: String,
    val title: String,
    val subtitle: String,
    val ratio: Float,
    val targetWidth: Int,
    val targetHeight: Int
) {
    PORTRAIT_9_16("9:16", "9:16 Vertical", "Shorts, Reels, TikTok", 9f / 16f, 720, 1280),
    LANDSCAPE_16_9("16:9", "16:9 Landscape", "YouTube, Desktop, Cinema", 16f / 9f, 1280, 720),
    SQUARE_1_1("1:1", "1:1 Square", "Instagram, Square Feed", 1f, 720, 720)
}

enum class CameraMotion(
    val id: String,
    val label: String,
    val promptCue: String
) {
    PUSH_IN("push_in", "Slow Push-In", "slow cinematic push-in camera movement towards subject"),
    PULL_OUT("pull_out", "Slow Pull-Out", "slow cinematic pull-out revealing wide environmental background"),
    PAN_RIGHT("pan_right", "Pan Right", "smooth horizontal camera pan from left to right"),
    PAN_LEFT("pan_left", "Pan Left", "smooth horizontal camera pan from right to left"),
    TILT_DOWN("tilt_down", "Tilt Down", "gentle vertical tilt motion flowing downward"),
    ORBIT("orbit", "Subtle Orbit", "subtle parallax orbital camera curve around central subject"),
    STATIC_MOTION("static", "Static Camera", "fixed tripod camera with lifelike subject movement and atmospheric physics")
}

data class PromptPreset(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val promptText: String
)

object PromptPresetsData {
    val presets = listOf(
        PromptPreset(
            id = "cinematic",
            name = "Cinematic Camera",
            iconName = "Movie",
            description = "Slow push-in with soft volumetric lighting and natural motion",
            promptText = "Animate this image with a slow cinematic camera push-in, realistic natural movement, soft volumetric lighting, subtle environmental motion, detailed textures, and smooth transitions. Preserve the original subject and composition."
        ),
        PromptPreset(
            id = "nature",
            name = "Nature & Landscape",
            iconName = "Park",
            description = "Foliage breeze, water ripples, mist, and sunset time-lapse",
            promptText = "Gentle breeze fluttering foliage and leaves, crystalline water ripples, rolling atmospheric mountain mist, soft golden-hour time-lapse sunlight drift, hyper-realistic physics."
        ),
        PromptPreset(
            id = "product",
            name = "Product Showcase",
            iconName = "ShoppingBag",
            description = "360-degree commercial spin, studio spotlight sweep",
            promptText = "Clean 360-degree commercial showcase rotation, studio spotlight sweep, glossy surface reflections, high-end commercial grade animation, 4k ultra-crisp motion."
        ),
        PromptPreset(
            id = "islamic",
            name = "Islamic Background",
            iconName = "Brightness3",
            description = "Spiritual twilight, golden particle dust, lantern glow",
            promptText = "Serene spiritual twilight atmosphere, shimmering golden particle dust drifting gently, soft ambient glow emanating from ornamental lantern geometry, peaceful sacred motion."
        ),
        PromptPreset(
            id = "fantasy",
            name = "Fantasy & Animation",
            iconName = "AutoAwesome",
            description = "Glowing mana aura, cel-shaded fluidity, floating sparkles",
            promptText = "Vibrant magical glowing aura particles floating in air, fluid animated motion, whimsical energetic camera tilt, enchanting fantasy aesthetics."
        ),
        PromptPreset(
            id = "human",
            name = "Realistic Human Motion",
            iconName = "Person",
            description = "Natural eye blink, breathing chest motion, wind in hair",
            promptText = "Subtle lifelike micro-expressions, gentle natural eye blinking, soft breathing chest motion, ambient wind softly swaying hair, photorealistic skin and fabric physics."
        ),
        PromptPreset(
            id = "slow_zoom",
            name = "Slow Zoom & Pan",
            iconName = "ZoomIn",
            description = "Ultra-slow pan, rack focus, depth-of-field separation",
            promptText = "Ultra-smooth 24fps cinematic pan across the frame with shallow depth-of-field rack focus, seamless parallax between foreground and background elements."
        ),
        PromptPreset(
            id = "particles",
            name = "Lighting & Particles",
            iconName = "Flare",
            description = "Anamorphic lens flares, floating embers, volumetric rays",
            promptText = "Dynamic anamorphic lens flares crossing the lens, luminous embers and dust motes suspended in volumetric god rays, high-contrast dramatic mood."
        )
    )
}

enum class AiProvider(
    val id: String,
    val displayName: String,
    val badge: String,
    val isFree: Boolean,
    val description: String,
    val quotaInfo: String,
    val docsUrl: String
) {
    DEMO_SANDBOX(
        id = "demo_sandbox",
        displayName = "MotionCraft Demo Studio",
        badge = "FREE / SANDBOX",
        isFree = true,
        description = "Guaranteed 100% working offline & sandbox engine. Transforms your image into a real playable, downloadable MP4 with cinematic motion curves.",
        quotaInfo = "Unlimited Free Generations (No API key needed)",
        docsUrl = "https://github.com/motioncraft-ai"
    ),
    REPLICATE(
        id = "replicate",
        displayName = "Replicate AI",
        badge = "CLOUD AI",
        isFree = false,
        description = "Runs top video generation models: Minimax Video-01, Kling 1.0, and Stable Video Diffusion (SVD).",
        quotaInfo = "Free trial credits on signup, then pay-per-second on cloud GPUs (~$0.05/video).",
        docsUrl = "https://replicate.com"
    ),
    FAL_AI(
        id = "fal_ai",
        displayName = "Fal.ai Video",
        badge = "FAST DIFFUSION",
        isFree = false,
        description = "High-throughput cloud inference for LTX-Video, Fast-SVD, and Kling models.",
        quotaInfo = "Free trial credit at fal.ai, low latency queue.",
        docsUrl = "https://fal.ai"
    ),
    HUGGING_FACE(
        id = "huggingface",
        displayName = "Hugging Face Spaces",
        badge = "OPEN SOURCE",
        isFree = true,
        description = "Connects to open-source image-to-video Gradio and Inference spaces.",
        quotaInfo = "Free user access tokens available from huggingface.co/settings/tokens.",
        docsUrl = "https://huggingface.co"
    )
}

enum class GenerationStep(val title: String, val detail: String) {
    IDLE("Ready", "Configure image and animation prompt"),
    ANALYZING_IMAGE("Validating & Optimizing Image", "Decoding resolution and aspect ratio"),
    PROMPT_ENHANCEMENT("Enhancing Animation Vectors", "Calculating camera dynamics and temporal cues"),
    DIFFUSION_SYNTHESIS("Synthesizing Motion Frames", "Running generative diffusion model"),
    ENCODING_MP4("Color Grading & MP4 Packaging", "Finalizing H.264 stream and metadata"),
    COMPLETE("Generation Succeeded", "Video ready for preview and export"),
    FAILED("Generation Failed", "An error occurred during generation")
}

data class GenerationJob(
    val id: String,
    val status: GenerationStep = GenerationStep.IDLE,
    val progress: Float = 0f,
    val statusMessage: String = "",
    val outputVideoUri: String? = null,
    val thumbnailUri: String? = null,
    val prompt: String = "",
    val aspectRatio: VideoAspectRatio = VideoAspectRatio.PORTRAIT_9_16,
    val durationSeconds: Int = 5,
    val provider: AiProvider = AiProvider.DEMO_SANDBOX,
    val isDemo: Boolean = true,
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
