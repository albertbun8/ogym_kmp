package com.example.ogym_kmp

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBarItem
import com.example.ogym_kmp.core.designsystem.component.OGymTopBar
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.navigation.HomeRoute
import com.example.ogym_kmp.core.navigation.ProfileRoute
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutScreen
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeUiAction
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionUiState
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionViewModel
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorScreen
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

private enum class MainDestination(val label: String, val route: Any, val icon: ImageVector) {
    Home("Home", HomeRoute, Icons.Outlined.Home),
    Profile("Profile", ProfileRoute, Icons.Outlined.Person),
}

private sealed interface AppSheet {
    data object None : AppSheet
    data class Workout(val activeWorkoutDraft: ActiveWorkoutDraft) : AppSheet
    data class TemplateEditor(val templateId: String?) : AppSheet
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    val homeViewModel = koinViewModel<HomeViewModel>()
    val sessionViewModel = koinViewModel<ActiveWorkoutSessionViewModel>()

    val navController = rememberNavController()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    var templateEditorOpen by rememberSaveable { mutableStateOf(false) }
    var editedTemplateId by rememberSaveable { mutableStateOf<String?>(null) }
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    if (sessionState is ActiveWorkoutSessionUiState.Loading) return

    OGymTheme(darkTheme = darkTheme) {
        val workout = (sessionState as? ActiveWorkoutSessionUiState.Active)?.workout
        val sheet: AppSheet = when {
            workout != null -> AppSheet.Workout(workout)
            templateEditorOpen -> AppSheet.TemplateEditor(editedTemplateId)
            else -> AppSheet.None
        }

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val showSheetBlockedMessage: () -> Unit = {
            val message = when (sheet) {
                is AppSheet.Workout -> "Finish your current workout first"
                is AppSheet.TemplateEditor -> "Close the template editor first"
                AppSheet.None -> null
            }
            if (message != null) {
                if (snackbarHostState.currentSnackbarData == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                            withDismissAction = true
                        )
                    }
                }
            }
        }

        val openTemplateEditor: (templateId: String?) -> Unit = { templateId ->
            if (sheet == AppSheet.None) {
                editedTemplateId = templateId
                templateEditorOpen = true
            } else {
                showSheetBlockedMessage()
            }
        }

        val closeTemplateEditor: () -> Unit = {
            editedTemplateId = null
            templateEditorOpen = false
        }

        val startWorkout: (draft: ActiveWorkoutDraft) -> Unit = { draft ->
            if (sheet == AppSheet.None) {
                sessionViewModel.onWorkoutStarted(draft)
            } else {
                showSheetBlockedMessage()
            }
        }

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = MainDestination.entries.firstOrNull { destination ->
            backStackEntry?.destination?.hasRoute(destination.route::class) == true
        } ?: MainDestination.Home

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { OGymTopBar() },
            bottomBar = {
                OGymBottomBar(
                    items = MainDestination.entries.map { OGymBottomBarItem(it.label, it.icon) },
                    selectedLabel = currentDestination.label,
                    onItemSelected = { selectedLabel ->
                        val selected = MainDestination.entries.first { it.label == selectedLabel }

                        navController.navigate(selected.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            },
        ) { innerPadding ->
            val currentSheet by rememberUpdatedState(sheet)

            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberBottomSheetState(
                    initialValue = SheetValue.Hidden,
                    confirmValueChange = { newValue ->
                        newValue != SheetValue.Hidden || currentSheet == AppSheet.None
                    },
                ),
            )

            LaunchedEffect(sheet) {
                when (sheet) {
                    AppSheet.None -> scaffoldState.bottomSheetState.hide()
                    else -> scaffoldState.bottomSheetState.expand()
                }
            }

            // The floating bottom bar draws over the content, so only the top padding is applied here;
            // the bottom padding goes to the scrollable content (Home, Profile and the sheets), so they
            // scroll behind the bar, and is added to the sheet peek height.
            val bottomBarPadding = innerPadding.calculateBottomPadding()

            BottomSheetScaffold(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                scaffoldState = scaffoldState,
                sheetPeekHeight = 96.dp + bottomBarPadding,
                sheetDragHandle = { OGymSheetDragHandle() },
                containerColor = MaterialTheme.colorScheme.background,
                sheetContainerColor = MaterialTheme.colorScheme.background,
                sheetContent = {
                    when (sheet) {
                        AppSheet.None -> Unit
                        is AppSheet.Workout -> {
                            val activeWorkoutViewModel =
                                koinViewModel<ActiveWorkoutViewModel>(key = sheet.activeWorkoutDraft.id.toString()) {
                                    parametersOf(sheet.activeWorkoutDraft)
                                }

                            NavigationBackHandler(
                                state = rememberNavigationEventState(NavigationEventInfo.None),
                                isBackEnabled = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                                onBackCompleted = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    }
                                },
                            )

                            ActiveWorkoutScreen(
                                viewModel = activeWorkoutViewModel,
                                contentPadding = PaddingValues(bottom = bottomBarPadding),
                                onClosed = { sessionViewModel.onWorkoutEnded() },
                                onFinished = {
                                    sessionViewModel.onWorkoutEnded()
                                    homeViewModel.refreshSessions()
                                },
                            )
                        }

                        is AppSheet.TemplateEditor -> {
                            val editorViewModel =
                                koinViewModel<TemplateEditorViewModel>(key = "template-editor-${sheet.templateId ?: "new"}") {
                                    parametersOf(sheet.templateId?.let(Uuid::parse))
                                }

                            NavigationBackHandler(
                                state = rememberNavigationEventState(NavigationEventInfo.None),
                                onBackCompleted = { closeTemplateEditor() },
                            )

                            TemplateEditorScreen(
                                viewModel = editorViewModel,
                                contentPadding = PaddingValues(bottom = bottomBarPadding),
                            ) { saved ->
                                closeTemplateEditor()
                                if (saved) homeViewModel.onAction(
                                    HomeUiAction.RetryTemplates,
                                )
                            }
                        }
                    }
                },
            ) {
                MainContent(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    activeWorkout = workout,
                    onWorkoutStarted = startWorkout,
                    onCreateTemplate = { openTemplateEditor(null) },
                    onEditTemplate = { openTemplateEditor(it.id.toString()) },
                    canStartWorkout = sheet == AppSheet.None,
                    onStartBlocked = showSheetBlockedMessage,
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { darkTheme = it },
                    contentPadding = PaddingValues(bottom = bottomBarPadding),
                )
            }
        }
    }
}

@Composable
private fun OGymSheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 2.dp)
            .size(width = 36.dp, height = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(50),
            ),
    )
}

@Composable
private fun MainContent(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    activeWorkout: ActiveWorkoutDraft?,
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
    onCreateTemplate: () -> Unit,
    onEditTemplate: (WorkoutTemplate) -> Unit,
    canStartWorkout: Boolean,
    onStartBlocked: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable<HomeRoute> {
            HomeScreen(
                viewModel = homeViewModel,
                activeWorkout = activeWorkout,
                onWorkoutStarted = onWorkoutStarted,
                onCreateTemplate = onCreateTemplate,
                onEditTemplate = onEditTemplate,
                canStartWorkout = canStartWorkout,
                onStartBlocked = onStartBlocked,
                contentPadding = contentPadding,
            )
        }
        composable<ProfileRoute> {
            ProfilePlaceholder(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }

}

@Composable
private fun ProfilePlaceholder(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Profile — next UI slice")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Dark mode")
            Switch(
                checked = darkTheme,
                onCheckedChange = onDarkThemeChanged,
            )
        }
    }
}
