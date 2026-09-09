package com.example.ogym_kmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutScreen
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
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
        HomeViewModel(
            getWorkoutTemplates = dependencies.getWorkoutTemplates,
            getWorkoutSessions = dependencies.getWorkoutSessions,
            startEmptyWorkout = dependencies.startEmptyWorkout,
            startWorkoutFromTemplate = dependencies.startWorkoutFromTemplate,
            getExercises = dependencies.getExercises,
        )
    }
    var destination by rememberSaveable { mutableStateOf(MainDestination.Home) }
    var activeWorkout by remember { mutableStateOf<ActiveWorkoutDraft?>(null) }
    var templateEditorOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(dependencies) {
        activeWorkout = dependencies.getActiveWorkout()
    }

    OGymTheme {
        val workout = activeWorkout
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
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
                    onWorkoutStarted = { activeWorkout = it },
                    onCreateTemplate = { templateEditorOpen = true },
                    modifier = Modifier.padding(innerPadding),
                )
            } else if (workout != null) {
                val activeWorkoutViewModel = viewModel(key = workout.id.toString()) {
                    ActiveWorkoutViewModel(
                        workout = workout,
                        discardActiveWorkout = dependencies.discardActiveWorkout,
                        getExercises = dependencies.getExercises,
                        addExerciseToActiveWorkout = dependencies.addExerciseToActiveWorkout,
                        addSetToActiveWorkout = dependencies.addSetToActiveWorkout,
                        reorderActiveWorkoutExercise = dependencies.reorderActiveWorkoutExercise,
                        removeExerciseFromActiveWorkout = dependencies.removeExerciseFromActiveWorkout,
                        removeSetFromActiveWorkout = dependencies.removeSetFromActiveWorkout,
                        updateActiveWorkoutSet = dependencies.updateActiveWorkoutSet,
                        setActiveWorkoutSetCompletion = dependencies.setActiveWorkoutSetCompletion,
                        getPreviousExercisePerformances = dependencies.getPreviousExercisePerformances,
                        finishActiveWorkout = dependencies.finishActiveWorkout,
                    )
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
                            onClosed = { activeWorkout = null },
                            onFinished = {
                                activeWorkout = null
                                homeViewModel.refreshSessions()
                            },
                        )
                    },
                ) {
                    MainContent(
                        destination = destination,
                        homeViewModel = homeViewModel,
                        onWorkoutStarted = { activeWorkout = it },
                        onCreateTemplate = { templateEditorOpen = true },
                    )
                }
            } else {
                val editorViewModel = viewModel(key = "template-editor") {
                    TemplateEditorViewModel(
                        getExercises = dependencies.getExercises,
                        createWorkoutTemplate = dependencies.createWorkoutTemplate,
                    )
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
                            if (saved) homeViewModel.onAction(
                                com.example.ogym_kmp.feature.workout.presentation.home.HomeUiAction.RetryTemplates,
                            )
                        }
                    },
                ) {
                    MainContent(
                        destination = destination,
                        homeViewModel = homeViewModel,
                        onWorkoutStarted = { activeWorkout = it },
                        onCreateTemplate = { templateEditorOpen = true },
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
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
    onCreateTemplate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (destination) {
        MainDestination.Home -> HomeScreen(
            viewModel = homeViewModel,
            onWorkoutStarted = onWorkoutStarted,
            onCreateTemplate = onCreateTemplate,
            modifier = modifier,
        )
        MainDestination.Profile -> ProfilePlaceholder(modifier = modifier)
    }
}

@Composable
private fun ProfilePlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Profile — next UI slice")
    }
}
