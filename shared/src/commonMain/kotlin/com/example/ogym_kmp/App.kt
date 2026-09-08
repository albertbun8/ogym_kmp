package com.example.ogym_kmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutScreen
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
import androidx.compose.ui.unit.dp

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
        )
    }
    var destination by rememberSaveable { mutableStateOf(MainDestination.Home) }
    var activeWorkout by remember { mutableStateOf<ActiveWorkoutDraft?>(null) }

    OGymTheme {
        val workout = activeWorkout
        if (workout == null) {
            MainScaffold(
                destination = destination,
                onDestinationChanged = { destination = it },
                homeViewModel = homeViewModel,
                onWorkoutStarted = { activeWorkout = it },
            )
        } else {
            val activeWorkoutViewModel = viewModel(key = workout.id.toString()) {
                ActiveWorkoutViewModel(
                    workout = workout,
                    discardActiveWorkout = dependencies.discardActiveWorkout,
                    getExercises = dependencies.getExercises,
                    addExerciseToActiveWorkout = dependencies.addExerciseToActiveWorkout,
                    addSetToActiveWorkout = dependencies.addSetToActiveWorkout,
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
                scaffoldState = sheetState,
                sheetPeekHeight = 72.dp,
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
                MainScaffold(
                    destination = destination,
                    onDestinationChanged = { destination = it },
                    homeViewModel = homeViewModel,
                    onWorkoutStarted = { activeWorkout = it },
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(
    destination: MainDestination,
    onDestinationChanged: (MainDestination) -> Unit,
    homeViewModel: HomeViewModel,
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
) {
    Scaffold(
            bottomBar = {
                OGymBottomBar(
                    items = MainDestination.entries.map { it.label },
                    selectedItem = destination.label,
                    onItemSelected = { selectedLabel ->
                        onDestinationChanged(MainDestination.entries.first { it.label == selectedLabel })
                    },
                )
            },
        ) { innerPadding ->
            when (destination) {
                MainDestination.Home -> HomeScreen(
                    viewModel = homeViewModel,
                    onWorkoutStarted = onWorkoutStarted,
                    modifier = Modifier.padding(innerPadding),
                )
                MainDestination.Profile -> ProfilePlaceholder(
                    modifier = Modifier.padding(innerPadding),
                )
            }
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
