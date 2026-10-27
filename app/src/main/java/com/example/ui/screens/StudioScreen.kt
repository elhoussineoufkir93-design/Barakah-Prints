package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.AiProvider
import com.example.model.CameraMotion
import com.example.model.GenerationStep
import com.example.model.PromptPresetsData
import com.example.model.VideoAspectRatio
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGradientEnd
import com.example.ui.theme.AccentGradientStart
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeBorder
import com.example.ui.theme.DemoBadgeText
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleLight
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MotionCraftViewModel
import com.example.viewmodel.StudioTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    viewModel: MotionCraftViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val prompt by viewModel.prompt.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val cameraMotion by viewModel.cameraMotion.collectAsState()
    val durationSeconds by viewModel.durationSeconds.collectAsState()
    val motionStrength by viewModel.motionStrength.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val activeJob by viewModel.activeJob.collectAsState()
    val selectedProvider by viewModel.settingsManager.selectedProvider.collectAsState()

    var isSettingsExpanded by remember { mutableStateOf(false) }

    // Android Photo Picker launcher (zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri.toString())
        }
    }

    val isGenerating = activeJob?.status in listOf(
        GenerationStep.ANALYZING_IMAGE,
        GenerationStep.PROMPT_ENHANCEMENT,
        GenerationStep.DIFFUSION_SYNTHESIS,
        GenerationStep.ENCODING_MP4
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(PrimaryPurple, SecondaryBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon_motioncraft_1793140875336),
                        contentDescription = "MotionCraft Logo",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column {
                    Text(
                        text = "MotionCraft AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Image-to-Video Studio",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Provider Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (selectedProvider.isFree) DemoBadgeBg else Color(0x333B82F6),
                border = BorderStroke(1.dp, if (selectedProvider.isFree) DemoBadgeBorder else SecondaryBlue),
                modifier = Modifier.clickable {
                    viewModel.selectTab(StudioTab.SETTINGS)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (selectedProvider.isFree) DemoBadgeText else AccentCyan)
                    )
                    Text(
                        text = if (selectedProvider.isFree) "FREE SANDBOX" else "CLOUD AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedProvider.isFree) DemoBadgeText else AccentCyan
                    )
                }
            }
        }

        // Live Generation Banner / Progress
        AnimatedVisibility(
            visible = isGenerating,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            activeJob?.let { job ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    border = BorderStroke(1.dp, PrimaryPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PrimaryPurple,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = job.status.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "${(job.progress * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurpleLight,
                                fontSize = 14.sp
                            )
                        }

                        val animatedProgress by animateFloatAsState(
                            targetValue = job.progress,
                            animationSpec = tween(400, easing = FastOutSlowInEasing),
                            label = "generation_progress"
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = PrimaryPurple,
                            trackColor = SurfaceBorder
                        )

                        Text(
                            text = job.statusMessage,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelGeneration() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                border = BorderStroke(1.dp, Color(0x66EF4444)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Image Source / Upload
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. Source Image",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "JPG, PNG, WEBP (Max 20MB)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Image Preview or Upload Dropzone
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Selected Source Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top buttons overlay
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x99000000)
                            ) {
                                Text(
                                    text = "Ready to Animate",
                                    fontSize = 11.sp,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1E293B)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Change", fontSize = 11.sp, color = TextPrimary)
                                }

                                IconButton(
                                    onClick = { viewModel.setImageUri(null) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xCCEF4444), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Image",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Empty Upload Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceElevated)
                            .border(BorderStroke(1.dp, Brush.linearGradient(listOf(PrimaryPurple, SecondaryBlue))), RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("upload_image_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Upload Icon",
                                tint = PrimaryPurpleLight,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Tap to Pick Photo from Device",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Supports JPG, JPEG, PNG, WEBP",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Showcase Samples for Instant Testing
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Or test instantly with sample scenes:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        viewModel.showcaseSamples.forEach { sample ->
                            val isSelected = selectedImageUri?.contains(sample.id) == true
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0x338B5CF6) else SurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else SurfaceBorder),
                                modifier = Modifier
                                    .clickable { viewModel.selectShowcase(sample) }
                                    .testTag("sample_${sample.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = sample.drawableResId),
                                        contentDescription = sample.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Column {
                                        Text(
                                            text = sample.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = sample.tag,
                                            fontSize = 10.sp,
                                            color = AccentCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: AI Prompt Editor & Presets
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Animation Prompt",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )

                    // Enhance with AI button
                    Button(
                        onClick = { viewModel.enhancePrompt() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x338B5CF6),
                            contentColor = PrimaryPurpleLight
                        ),
                        border = BorderStroke(1.dp, PrimaryPurple),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("enhance_prompt_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Enhance Prompt",
                                modifier = Modifier.size(14.dp)
                            )
                            Text("Enhance with AI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Prompt Input Box
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.setPrompt(it) },
                    placeholder = {
                        Text(
                            text = "Describe how to animate this image (e.g. slow cinematic push-in, natural movement, soft volumetric lighting...)",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prompt_text_field")
                )

                // Presets Horizontal Row
                Text(
                    text = "Quick Animation Presets:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PromptPresetsData.presets.forEach { preset ->
                        val isPresetActive = prompt.contains(preset.name) || prompt == preset.promptText
                        FilterChip(
                            selected = isPresetActive,
                            onClick = { viewModel.applyPreset(preset) },
                            label = { Text(preset.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceElevated,
                                labelColor = TextSecondary,
                                selectedContainerColor = Color(0x338B5CF6),
                                selectedLabelColor = PrimaryPurpleLight
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isPresetActive) PrimaryPurple else SurfaceBorder
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        }

        // Section 3: Video Settings & Controls (Collapsible)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSettingsExpanded = !isSettingsExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = SecondaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "3. Video Parameters",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${aspectRatio.id} • ${durationSeconds}s • ${cameraMotion.label}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Icon(
                            imageVector = if (isSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Settings",
                            tint = TextSecondary
                        )
                    }
                }

                if (isSettingsExpanded) {
                    // Aspect Ratio Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Aspect Ratio (Social & Shorts):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VideoAspectRatio.values().forEach { ratio ->
                                val isSelected = aspectRatio == ratio
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0x338B5CF6) else SurfaceElevated,
                                    border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else SurfaceBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setAspectRatio(ratio) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = ratio.id,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) PrimaryPurpleLight else TextPrimary
                                        )
                                        Text(
                                            text = ratio.subtitle.split(",").first(),
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Duration Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Video Duration:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(3, 5, 10).forEach { sec ->
                                val isSelected = durationSeconds == sec
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0x333B82F6) else SurfaceElevated,
                                    border = BorderStroke(1.dp, if (isSelected) SecondaryBlue else SurfaceBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setDuration(sec) }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${sec}s Clip",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) AccentCyan else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Camera Movement Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Camera Trajectory:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CameraMotion.values().forEach { motion ->
                                val isSelected = cameraMotion == motion
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setCameraMotion(motion) },
                                    label = { Text(motion.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SurfaceElevated,
                                        labelColor = TextSecondary,
                                        selectedContainerColor = Color(0x3306B6D4),
                                        selectedLabelColor = AccentCyan
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AccentCyan else SurfaceBorder
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    // Motion Intensity Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Motion Intensity:",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = when {
                                    motionStrength <= 3.0f -> "Subtle (${motionStrength.toInt()})"
                                    motionStrength <= 7.0f -> "Fluid Dynamic (${motionStrength.toInt()})"
                                    else -> "Cinematic Action (${motionStrength.toInt()})"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryPurpleLight
                            )
                        }

                        Slider(
                            value = motionStrength,
                            onValueChange = { viewModel.setMotionStrength(it) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryPurple,
                                activeTrackColor = PrimaryPurple,
                                inactiveTrackColor = SurfaceBorder
                            )
                        )
                    }
                }
            }
        }

        // Section 4: Primary Generate Video Button
        Button(
            onClick = { viewModel.startGeneration() },
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = SurfaceElevated
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    if (isGenerating) {
                        Brush.linearGradient(listOf(SurfaceBorder, SurfaceBorder))
                    } else {
                        Brush.linearGradient(listOf(AccentGradientStart, AccentGradientEnd))
                    },
                    shape = RoundedCornerShape(14.dp)
                )
                .testTag("generate_video_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Synthesizing Video...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Generate",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Generate Video (${aspectRatio.id})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
