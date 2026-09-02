package com.example.ogym_kmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import com.example.ogym_kmp.core.designsystem.component.OGymBottomBar
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.feature.workout.presentation.home.HomeScreen
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel

private enum class MainDestination(val label: String) {
    Home("Home"),
    Profile("Profile"),
}

@Composable
fun App(dependencies: AppDependencies) {
    val homeViewModel = viewModel {
        HomeViewModel(
            getWorkoutTemplates = dependencies.getWorkoutTemplates,
            getWorkoutSessions = dependencies.getWorkoutSessions,
        )
    }
    var destination by rememberSaveable { mutableStateOf(MainDestination.Home) }

    OGymTheme {
        Scaffold(
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
            when (destination) {
                MainDestination.Home -> HomeScreen(
                    viewModel = homeViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
                MainDestination.Profile -> ProfilePlaceholder(
                    modifier = Modifier.padding(innerPadding),
                )
            }
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
