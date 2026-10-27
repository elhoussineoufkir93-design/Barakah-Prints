package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiProvider
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeBorder
import com.example.ui.theme.DemoBadgeText
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

@Composable
fun SettingsScreen(
    viewModel: MotionCraftViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedProvider by viewModel.settingsManager.selectedProvider.collectAsState()
    val replicateApiKey by viewModel.settingsManager.replicateApiKey.collectAsState()
    val falApiKey by viewModel.settingsManager.falApiKey.collectAsState()
    val hfToken by viewModel.settingsManager.hfToken.collectAsState()

    var repKeyInput by remember(replicateApiKey) { mutableStateOf(replicateApiKey) }
    var falKeyInput by remember(falApiKey) { mutableStateOf(falApiKey) }
    var hfTokenInput by remember(hfToken) { mutableStateOf(hfToken) }

    var isRepKeyVisible by remember { mutableStateOf(false) }
    var isFalKeyVisible by remember { mutableStateOf(false) }
    var isHfTokenVisible by remember { mutableStateOf(false) }

    var repSavedToast by remember { mutableStateOf(false) }
    var falSavedToast by remember { mutableStateOf(false) }
    var hfSavedToast by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "AI Video Providers & Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Configure live image-to-video diffusion engines or use the built-in free sandbox.",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Section 1: Provider Selection
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Active Generation Provider",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                AiProvider.values().forEach { provider ->
                    val isSelected = selectedProvider == provider
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0x228B5CF6) else SurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else SurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.settingsManager.setProvider(provider) }
                            .testTag("provider_option_${provider.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isSelected) PrimaryPurple else TextMuted, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryPurple)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = provider.displayName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (provider.isFree) DemoBadgeBg else Color(0x333B82F6)
                                    ) {
                                        Text(
                                            text = provider.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (provider.isFree) DemoBadgeText else AccentCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = provider.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )

                                Text(
                                    text = "Quota: ${provider.quotaInfo}",
                                    fontSize = 11.sp,
                                    color = if (provider.isFree) SuccessGreen else AccentCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: API Keys Management
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Keys",
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "API Key Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                // Replicate Token
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replicate API Token (r8_...)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://replicate.com/account/api-tokens"))
                                )
                            }
                        ) {
                            Text("Get Token", fontSize = 11.sp, color = AccentCyan)
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = "Open Link",
                                tint = AccentCyan,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = repKeyInput,
                        onValueChange = { repKeyInput = it },
                        placeholder = { Text("Enter r8_...", color = TextMuted, fontSize = 12.sp) },
                        visualTransformation = if (isRepKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isRepKeyVisible = !isRepKeyVisible }) {
                                Icon(
                                    imageVector = if (isRepKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("replicate_key_input")
                    )

                    Button(
                        onClick = {
                            viewModel.settingsManager.setReplicateApiKey(repKeyInput)
                            repSavedToast = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x338B5CF6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(34.dp)
                    ) {
                        Text(
                            text = if (repSavedToast) "Saved!" else "Save Token",
                            fontSize = 12.sp,
                            color = PrimaryPurpleLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Fal.ai Key
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fal.ai API Key",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://fal.ai/dashboard/keys"))
                                )
                            }
                        ) {
                            Text("Get Key", fontSize = 11.sp, color = AccentCyan)
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = "Open Link",
                                tint = AccentCyan,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = falKeyInput,
                        onValueChange = { falKeyInput = it },
                        placeholder = { Text("Enter Fal.ai Key", color = TextMuted, fontSize = 12.sp) },
                        visualTransformation = if (isFalKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isFalKeyVisible = !isFalKeyVisible }) {
                                Icon(
                                    imageVector = if (isFalKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fal_key_input")
                    )

                    Button(
                        onClick = {
                            viewModel.settingsManager.setFalApiKey(falKeyInput)
                            falSavedToast = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x333B82F6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(34.dp)
                    ) {
                        Text(
                            text = if (falSavedToast) "Saved!" else "Save Key",
                            fontSize = 12.sp,
                            color = AccentCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Hugging Face Token
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hugging Face User Access Token (hf_...)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://huggingface.co/settings/tokens"))
                                )
                            }
                        ) {
                            Text("Free Token", fontSize = 11.sp, color = SuccessGreen)
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = "Open Link",
                                tint = SuccessGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = hfTokenInput,
                        onValueChange = { hfTokenInput = it },
                        placeholder = { Text("Enter hf_...", color = TextMuted, fontSize = 12.sp) },
                        visualTransformation = if (isHfTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isHfTokenVisible = !isHfTokenVisible }) {
                                Icon(
                                    imageVector = if (isHfTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hf_token_input")
                    )

                    Button(
                        onClick = {
                            viewModel.settingsManager.setHfToken(hfTokenInput)
                            hfSavedToast = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3310B981)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(34.dp)
                    ) {
                        Text(
                            text = if (hfSavedToast) "Saved!" else "Save Token",
                            fontSize = 12.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Section 3: Free-First Transparency & Quota Guide
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Pricing and Quotas",
                        tint = PrimaryPurpleLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Free-First Transparency & Quotas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "• MotionCraft Demo Studio: 100% Free and works completely offline without API accounts. Great for testing prompt enhancements, aspect ratios, and export workflows.\n" +
                            "• Replicate: Gives free GPU trial on account creation. Afterwards, pricing is based on seconds of GPU compute (~$0.05 per 5s video).\n" +
                            "• Fal.ai: Free developer balance on sign-up.\n" +
                            "• Hugging Face: Free inference tier for open source models.\n" +
                            "• Security: Your API keys are stored locally on your device and are never uploaded to any third party server.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
