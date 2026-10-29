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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.designsystem.component.OGymTopBar
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutScreen
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionUiState
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionViewModel
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorScreen
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

private enum class MainDestination(val label: String) {
    Home("Home"),
    Profile("Profile"),
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

    var destination by rememberSaveable { mutableStateOf(MainDestination.Home) }
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

        val openTemplateEditor: (templateId: String?) -> Unit = { templateId ->
            if (workout == null) {
                editedTemplateId = templateId
                templateEditorOpen = true
            }
        }

        val startWorkout: (draft: ActiveWorkoutDraft) -> Unit = { draft ->
            if (sheet == AppSheet.None) {
                sessionViewModel.onWorkoutStarted(draft)
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
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
            val currentSheet by rememberUpdatedState(sheet)

            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.Hidden,
                    skipHiddenState = false,
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

            BottomSheetScaffold(
                modifier = Modifier.padding(innerPadding),
                scaffoldState = scaffoldState,
                sheetPeekHeight = 96.dp,
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

                            ActiveWorkoutScreen(
                                viewModel = activeWorkoutViewModel,
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

                            TemplateEditorScreen(viewModel = editorViewModel) { saved ->
                                templateEditorOpen = false
                                editedTemplateId = null
                                if (saved) homeViewModel.onAction(
                                    com.example.ogym_kmp.feature.workout.presentation.home.HomeUiAction.RetryTemplates,
                                )
                            }
                        }
                    }
                },
            ) {
                MainContent(
                    destination = destination,
                    homeViewModel = homeViewModel,
                    activeWorkout = workout,
                    onWorkoutStarted = startWorkout,
                    onCreateTemplate = { openTemplateEditor(null) },
                    onEditTemplate = { openTemplateEditor(it.id.toString()) },
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { darkTheme = it },
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
