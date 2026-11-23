package com.example.ogym_kmp.feature.settings

import app.cash.turbine.test
import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.settings.domain.model.ThemeMode
import com.example.ogym_kmp.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    // Runs BEFORE each test: replace Main with a test dispatcher.
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    // Runs AFTER each test: restore Main, so one test cannot affect the next.
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun themeMode_emitsSavedTheme() = runTest {
        val fakeRepo = FakeSettingsRepository(ThemeMode.DARK)
        val viewModel = SettingsViewModel(fakeRepo)

        assertNull(viewModel.themeMode.value)

        viewModel.themeMode.test {
            assertEquals(ThemeMode.DARK, awaitItem())
        }
    }

    @Test
    fun onThemeModeSelected_updatesThemeMode() = runTest {
        val fakeRepo = FakeSettingsRepository(ThemeMode.DARK)
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.themeMode.test {
            assertEquals(ThemeMode.DARK, awaitItem())
            viewModel.onThemeModeSelected(ThemeMode.LIGHT)
            assertEquals(ThemeMode.LIGHT, awaitItem())
        }
    }

    @Test
    fun weightUnit_emitsSavedWeightUnit() = runTest {
        val fakeRepo = FakeSettingsRepository(initialWeightUnit = WeightUnit.KG)
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.weightUnit.test {
            assertEquals(WeightUnit.KG, awaitItem())
        }
    }

    @Test
    fun onWeightUnitSelected_updatesWeightUnit() = runTest {
        val fakeRepo = FakeSettingsRepository(initialWeightUnit = WeightUnit.LB)
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.weightUnit.test {
            assertEquals(WeightUnit.LB, awaitItem())
            viewModel.onWeightUnitSelected(WeightUnit.KG)
            assertEquals(WeightUnit.KG, awaitItem())
        }
    }
}