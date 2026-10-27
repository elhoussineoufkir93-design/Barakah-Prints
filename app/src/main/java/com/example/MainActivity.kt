package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.PreviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.MotionCraftTheme
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleLight
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MotionCraftViewModel
import com.example.viewmodel.StudioTab

class MainActivity : ComponentActivity() {

    private val viewModel: MotionCraftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionCraftTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MotionCraftViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept hardware/gesture back press when not on home/Studio tab
    BackHandler(enabled = selectedTab != StudioTab.STUDIO) {
        viewModel.selectTab(StudioTab.STUDIO)
    }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialThemeColor(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            MotionCraftBottomNav(
                currentTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                StudioTab.STUDIO -> StudioScreen(viewModel = viewModel)
                StudioTab.PREVIEW -> PreviewScreen(viewModel = viewModel)
                StudioTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                StudioTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MaterialThemeColor(): Color {
    return SurfaceDark
}

@Composable
fun MotionCraftBottomNav(
    currentTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDark,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        NavigationBar(
            containerColor = SurfaceDark,
            tonalElevation = 8.dp,
            windowInsets = WindowInsets.navigationBars,
            modifier = Modifier.testTag("bottom_nav_bar")
        ) {
            val tabs = listOf(
                Triple(StudioTab.STUDIO, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
                Triple(StudioTab.PREVIEW, Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle),
                Triple(StudioTab.HISTORY, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary),
                Triple(StudioTab.SETTINGS, Icons.Filled.Tune, Icons.Outlined.Tune)
            )

            tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                            contentDescription = tab.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurpleLight,
                        selectedTextColor = PrimaryPurpleLight,
                        indicatorColor = Color(0x338B5CF6),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                )
            }
        }
    }
}
