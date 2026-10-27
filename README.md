# MotionCraft AI — Free AI Image-to-Video Studio

**MotionCraft AI** is a professional, mobile-first Android application that transforms user-uploaded still images into short, cinematic AI-generated videos using text prompts and motion physics.

Designed with a **Free-First Strategy**, MotionCraft AI includes an offline **Demo Sandbox Studio** that generates real, playable, and downloadable MP4 animations with zero setup, alongside direct integrations with leading cloud image-to-video diffusion APIs (**Replicate**, **Fal.ai**, and **Hugging Face**).

---

## 🚀 Key Features

1. **Flexible Image Ingestion**:
   - Tap-to-pick from device using Android zero-permission Photo Picker (`PickVisualMedia`).
   - Supports JPG, JPEG, PNG, and WEBP formats up to 20MB.
   - Built-in showcased 8K scenes (*Cyberpunk Supercar* and *Alpine Cascade*) for 1-tap instant testing.
   - Image preview with resolution details, replace, and removal options.

2. **AI Prompt Generator & Presets**:
   - Multi-line cinematic prompt editor with real-time prompt enhancement.
   - **8 Curated Animation Presets**:
     - 🎬 *Cinematic Camera Movement*: Slow push-in with soft volumetric lighting and natural motion.
     - 🌿 *Nature and Landscape Animation*: Foliage flutter, crystalline water ripples, and twilight mist.
     - 📦 *Product Advertisement*: 360-degree commercial showcase rotation with spotlight glints.
     - 🕌 *Islamic Background Animation*: Shimmering gold particle dust and spiritual lantern illumination.
     - ✨ *Fantasy and Cartoon Animation*: Vibrant magical auras and fluid cel-animation dynamics.
     - 👤 *Realistic Human Motion*: Micro-expressions, subtle eye blinking, and natural breathing.
     - 🔍 *Slow Zoom and Camera Pan*: Ultra-smooth 24fps panning with rack-focus depth of field.
     - ⚡ *Lighting and Particle Effects*: Anamorphic lens flares, floating embers, and god rays.
   - **One-Tap "Enhance with AI"**: Automatically expands prompts with camera kinetics, temporal stability cues, physical lighting parameters, and subject preservation rules.

3. **Multi-Aspect Ratio & Motion Controls**:
   - **9:16 Vertical**: Optimized for YouTube Shorts, TikTok, Instagram Reels, and Facebook Reels.
   - **16:9 Widescreen**: Designed for YouTube and desktop presentations.
   - **1:1 Square**: Perfect for Instagram feeds and social carousels.
   - Adjustable duration (3s, 5s, 10s), motion dynamics slider (1–10), and camera trajectory controls (Push-In, Pull-Out, Pan, Tilt, Orbit, Static).

4. **Multi-Provider Architecture & Free-First Strategy**:
   - **MotionCraft Demo Studio (Free Sandbox)**: 100% free and offline-ready. Synthesizes real H.264 MP4 videos directly on device to test UI, pipeline, and export workflows without needing an account or credit card.
   - **Replicate API**: Cloud GPU inference supporting Minimax Video-01, Kling, and Stable Video Diffusion (SVD).
   - **Fal.ai**: Low-latency video generation via LTX-Video and Fast-SVD queues.
   - **Hugging Face**: Open-source image-to-video community spaces.

5. **Video Player & Real MP4 Export**:
   - HTML5-style Android Video Player with play/pause, seekbar, time codes, loop toggle, and aspect ratio container.
   - **Download MP4**: Directly writes to Android device `Movies/MotionCraft` directory via `MediaStore.Video.Media` without requiring sensitive storage permissions.
   - **Share Video**: Native Android Share Sheet via secure `FileProvider` to WhatsApp, TikTok, Instagram Reels, etc.

6. **Local Project History**:
   - Powered by Room Database for fast, durable local persistence of generated videos, prompts, dates, and thumbnails.

---

## 🎨 UI/UX Design

- **Dark Creative Suite Aesthetic**: Deep cosmic background (`#090A10`), charcoal surfaces (`#11131E`, `#171B2B`), and vibrant neon purple/cyan accents (`#8B5CF6`, `#06B6D4`).
- **Responsive & Accessible**: Strict Material 3 component guidelines, minimum 48dp touch targets, full-bleed edge-to-edge support with safe drawing insets.
- **Glassy Cards & Micro-interactions**: Smooth animated progress indicators, state transitions, and responsive feedback.

---

## 🔑 Obtaining & Configuring API Credentials

### 1. MotionCraft Demo Sandbox (Default — No Keys Required)
The app works out of the box in **Demo Sandbox** mode. No account, sign-in, or payment is ever required to generate, play, or download videos.

### 2. Replicate (Optional Live Cloud Diffusion)
1. Sign up at [replicate.com](https://replicate.com).
2. Navigate to your account settings at [replicate.com/account/api-tokens](https://replicate.com/account/api-tokens).
3. Copy your API token (starts with `r8_...`).
4. In MotionCraft AI, open the **API & Guide** tab and paste your token under *Replicate API Token*.
5. Tap **Save Token** and select **Replicate AI** as your active provider.

### 3. Fal.ai (Optional Fast Diffusion)
1. Register at [fal.ai](https://fal.ai).
2. Go to [fal.ai/dashboard/keys](https://fal.ai/dashboard/keys) and generate a new key.
3. Paste the key in the **API & Guide** tab in MotionCraft AI.

### 4. Hugging Face (Optional Open Source Models)
1. Create a free account at [huggingface.co](https://huggingface.co).
2. Go to [huggingface.co/settings/tokens](https://huggingface.co/settings/tokens) and create a **Read** token (starts with `hf_...`).
3. Paste into MotionCraft AI settings.

---

## 🛠️ Technical Stack & Architecture

- **Platform**: Android (Kotlin, Jetpack Compose, Material 3)
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 15 (API 36)
- **Architecture**: Clean MVVM (Model-View-ViewModel) with Kotlin Coroutines & Flow
- **Local Persistence**: Android Room Database (`AppDatabase`, `ProjectDao`, `VideoProjectEntity`)
- **Image & Thumbnail Loading**: Coil Compose (`AsyncImage`)
- **Video Playback**: Android `VideoView` / `MediaPlayer` wrapped in Compose `AndroidView`
- **Networking**: OkHttp 4 + Retrofit for cloud polling and asynchronous jobs
- **Export & Storage**: Android `MediaStore.Video.Media` & `FileProvider`

---

## 📱 Build & Local Run Instructions

To build the APK:
```bash
gradle :app:assembleDebug
```

To run unit and robolectric tests:
```bash
gradle :app:testDebugUnitTest
```
