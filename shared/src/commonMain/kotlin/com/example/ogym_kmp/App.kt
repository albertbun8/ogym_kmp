package com.example.ogym_kmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.designsystem.component.OGymTopBar
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutScreen
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionUiState
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorScreen
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorViewModel

private enum class MainDestination(val label: String) {
    Home("Home"),
    Profile("Profile"),
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App(dependencies: AppDependencies) {

    val homeViewModel = viewModel {
        dependencies.workout.createHomeViewModel()
    }

    val sessionViewModel = viewModel {
        dependencies.workout.createActiveWorkoutSessionViewModel()
    }

    var destination by rememberSaveable { mutableStateOf(MainDestination.Home) }
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    var templateEditorOpen by rememberSaveable { mutableStateOf(false) }
    var editedTemplate by remember { mutableStateOf<WorkoutTemplate?>(null) }
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    if (sessionState is ActiveWorkoutSessionUiState.Loading) return

    OGymTheme(darkTheme = darkTheme) {
        val workout = (sessionState as? ActiveWorkoutSessionUiState.Active)?.workout
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = { OGymTopBar() },
            bottomBar = {
                OGymBottomBar(
                    items = MainDestination.entries.map { it.label },
                    selectedItem = destination.label,
                    onItemSelected = { selectedLabel ->
                        destination = MainDestination.entries.first { it.label == selectedLabel }
                    },
                )
            },
        ) { innerPadding ->
            if (workout == null && !templateEditorOpen) {
                MainContent(
                    destination = destination,
                    homeViewModel = homeViewModel,
                    activeWorkout = null,
                    onWorkoutStarted = { sessionViewModel.onWorkoutStarted(it) },
                    onCreateTemplate = {
                        editedTemplate = null
                        templateEditorOpen = true
                    },
                    onEditTemplate = {
                        editedTemplate = it
                        templateEditorOpen = true
                    },
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { darkTheme = it },
                    modifier = Modifier.padding(innerPadding),
                )
            } else if (workout != null) {
                val activeWorkoutViewModel = viewModel(key = workout.id.toString()) {
                    dependencies.workout.createActiveWorkoutViewModel(workout)
                }

                val sheetState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberStandardBottomSheetState(
                        initialValue = SheetValue.Expanded,
                        skipHiddenState = true,
                    ),
                )
                BottomSheetScaffold(
                    modifier = Modifier.padding(innerPadding),
                    scaffoldState = sheetState,
                    sheetPeekHeight = 96.dp,
                    sheetDragHandle = { OGymSheetDragHandle() },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    sheetContainerColor = MaterialTheme.colorScheme.background,
                    sheetContentColor = MaterialTheme.colorScheme.onBackground,
                    sheetContent = {
                        ActiveWorkoutScreen(
                            viewModel = activeWorkoutViewModel,
                            onClosed = { sessionViewModel.onWorkoutEnded() },
                            onFinished = {
                                sessionViewModel.onWorkoutEnded()
                                homeViewModel.refreshSessions()
                            },
                        )
                    },
                ) {
                    MainContent(
                        destination = destination,
                        homeViewModel = homeViewModel,
                        activeWorkout = workout,
                        onWorkoutStarted = { sessionViewModel.onWorkoutStarted(it) },
                        onCreateTemplate = {
                            editedTemplate = null
                            templateEditorOpen = true
                        },
                        onEditTemplate = {
                            editedTemplate = it
                            templateEditorOpen = true
                        },
                        darkTheme = darkTheme,
                        onDarkThemeChanged = { darkTheme = it },
                    )
                }
            } else {
                val editorViewModel =
                    viewModel(key = "template-editor-${editedTemplate?.id ?: "new"}") {
                        dependencies.workout.createTemplateEditorViewModel(editedTemplate)
                    }

                BottomSheetScaffold(
                    modifier = Modifier.padding(innerPadding),
                    scaffoldState = rememberBottomSheetScaffoldState(
                        bottomSheetState = rememberStandardBottomSheetState(
                            initialValue = SheetValue.Expanded,
                            skipHiddenState = true,
                        ),
                    ),
                    sheetPeekHeight = 96.dp,
                    sheetDragHandle = { OGymSheetDragHandle() },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    sheetContainerColor = MaterialTheme.colorScheme.background,
                    sheetContentColor = MaterialTheme.colorScheme.onBackground,
                    sheetContent = {
                        TemplateEditorScreen(viewModel = editorViewModel) { saved ->
                            templateEditorOpen = false
                            editedTemplate = null
                            if (saved) homeViewModel.onAction(
                                com.example.ogym_kmp.feature.workout.presentation.home.HomeUiAction.RetryTemplates,
                            )
                        }
                    },
                ) {
                    MainContent(
                        destination = destination,
                        homeViewModel = homeViewModel,
                        activeWorkout = null,
                        onWorkoutStarted = { sessionViewModel.onWorkoutStarted(it) },
                        onCreateTemplate = {
                            editedTemplate = null
                            templateEditorOpen = true
                        },
                        onEditTemplate = {
                            editedTemplate = it
                            templateEditorOpen = true
                        },
                        darkTheme = darkTheme,
                        onDarkThemeChanged = { darkTheme = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun OGymSheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 2.dp)
            .size(width = 32.dp, height = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(50),
            ),
    )
}

@Composable
private fun MainContent(
    destination: MainDestination,
    homeViewModel: HomeViewModel,
    activeWorkout: ActiveWorkoutDraft?,
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
    onCreateTemplate: () -> Unit,
    onEditTemplate: (WorkoutTemplate) -> Unit,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (destination) {
        MainDestination.Home -> HomeScreen(
            viewModel = homeViewModel,
            activeWorkout = activeWorkout,
            onWorkoutStarted = onWorkoutStarted,
            onCreateTemplate = onCreateTemplate,
            onEditTemplate = onEditTemplate,
            modifier = modifier,
        )

        MainDestination.Profile -> ProfilePlaceholder(
            darkTheme = darkTheme,
            onDarkThemeChanged = onDarkThemeChanged,
            modifier = modifier,
        )
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
