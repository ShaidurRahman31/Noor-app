package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.ui.components.TasbihCounterDialog
import com.example.ui.screens.AdminReviewScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CategoryListScreen
import com.example.ui.screens.DuaDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

sealed class AppDestination {
    object Home : AppDestination()
    object Categories : AppDestination()
    object Bookmarks : AppDestination()
    object Settings : AppDestination()
    object Admin : AppDestination()
    data class CategoryDetail(val category: DuaCategory) : AppDestination()
    data class DuaDetail(val dua: DuaItem) : AppDestination()
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NoorApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NoorApp(viewModel: MainViewModel) {
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }
    var backStack by remember { mutableStateOf<List<AppDestination>>(emptyList()) }
    var activeTasbihDua by remember { mutableStateOf<DuaItem?>(null) }

    val tasbihCount by viewModel.tasbihCount.collectAsState()
    val tasbihTarget by viewModel.tasbihTarget.collectAsState()

    fun navigateTo(destination: AppDestination) {
        backStack = backStack + currentDestination
        currentDestination = destination
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentDestination = backStack.last()
            backStack = backStack.dropLast(1)
        } else {
            currentDestination = AppDestination.Home
        }
    }

    // Handle system back gesture
    BackHandler(enabled = currentDestination !is AppDestination.Home || backStack.isNotEmpty()) {
        navigateBack()
    }

    val showBottomNav = currentDestination is AppDestination.Home ||
            currentDestination is AppDestination.Categories ||
            currentDestination is AppDestination.Bookmarks ||
            currentDestination is AppDestination.Settings

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    val isHome = currentDestination is AppDestination.Home
                    val isCategories = currentDestination is AppDestination.Categories
                    val isBookmarks = currentDestination is AppDestination.Bookmarks
                    val isSettings = currentDestination is AppDestination.Settings

                    // Tab 1: Home
                    NavigationBarItem(
                        selected = isHome,
                        onClick = {
                            if (!isHome) {
                                backStack = emptyList()
                                currentDestination = AppDestination.Home
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = stringResource(R.string.nav_home)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_home),
                                fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Tab 2: Categories (All 20 Categories)
                    NavigationBarItem(
                        selected = isCategories,
                        onClick = {
                            if (!isCategories) {
                                backStack = emptyList()
                                currentDestination = AppDestination.Categories
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isCategories) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = stringResource(R.string.nav_categories)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_categories),
                                fontWeight = if (isCategories) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Tab 3: Bookmarks
                    NavigationBarItem(
                        selected = isBookmarks,
                        onClick = {
                            if (!isBookmarks) {
                                backStack = emptyList()
                                currentDestination = AppDestination.Bookmarks
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isBookmarks) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = stringResource(R.string.nav_bookmarks)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_bookmarks),
                                fontWeight = if (isBookmarks) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Tab 4: Settings (with Owner Secret Door)
                    NavigationBarItem(
                        selected = isSettings,
                        onClick = {
                            if (!isSettings) {
                                backStack = emptyList()
                                currentDestination = AppDestination.Settings
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSettings) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.nav_settings)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_settings),
                                fontWeight = if (isSettings) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "destination_transition"
            ) { destination ->
                when (destination) {
                    is AppDestination.Home -> HomeScreen(
                        viewModel = viewModel,
                        onCategoryClick = { category ->
                            navigateTo(AppDestination.CategoryDetail(category))
                        },
                        onDuaClick = { dua ->
                            navigateTo(AppDestination.DuaDetail(dua))
                        },
                        onTasbihClick = { dua ->
                            viewModel.resetTasbih(if (dua.recommendedRepeats > 1) dua.recommendedRepeats else 33)
                            activeTasbihDua = dua
                        }
                    )
                    is AppDestination.Categories -> CategoriesScreen(
                        viewModel = viewModel,
                        onCategoryClick = { category ->
                            navigateTo(AppDestination.CategoryDetail(category))
                        }
                    )
                    is AppDestination.CategoryDetail -> CategoryListScreen(
                        category = destination.category,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onDuaClick = { dua ->
                            navigateTo(AppDestination.DuaDetail(dua))
                        },
                        onTasbihClick = { dua ->
                            viewModel.resetTasbih(if (dua.recommendedRepeats > 1) dua.recommendedRepeats else 33)
                            activeTasbihDua = dua
                        }
                    )
                    is AppDestination.DuaDetail -> DuaDetailScreen(
                        dua = destination.dua,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onOpenTasbih = {
                            viewModel.resetTasbih(if (destination.dua.recommendedRepeats > 1) destination.dua.recommendedRepeats else 33)
                            activeTasbihDua = destination.dua
                        }
                    )
                    is AppDestination.Bookmarks -> BookmarksScreen(
                        viewModel = viewModel,
                        onDuaClick = { dua ->
                            navigateTo(AppDestination.DuaDetail(dua))
                        },
                        onTasbihClick = { dua ->
                            viewModel.resetTasbih(if (dua.recommendedRepeats > 1) dua.recommendedRepeats else 33)
                            activeTasbihDua = dua
                        }
                    )
                    is AppDestination.Settings -> SettingsScreen(
                        viewModel = viewModel,
                        onOpenAdmin = {
                            navigateTo(AppDestination.Admin)
                        }
                    )
                    is AppDestination.Admin -> AdminReviewScreen(
                        viewModel = viewModel
                    )
                }
            }

            // Tasbih Dialog
            activeTasbihDua?.let { dua ->
                TasbihCounterDialog(
                    dua = dua,
                    currentCount = tasbihCount,
                    targetCount = tasbihTarget,
                    onIncrement = { viewModel.incrementTasbih() },
                    onReset = { viewModel.resetTasbih() },
                    onSelectTarget = { viewModel.setTasbihTarget(it) },
                    onDismiss = { activeTasbihDua = null }
                )
            }
        }
    }
}
