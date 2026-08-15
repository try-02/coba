package com.pos.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Conservative regression smoke tests.
 *
 * These deliberately avoid guessing implementation-specific controls. They verify
 * that the root screen and seeded catalog remain functional after MainActivity refactor.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityRegressionTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun seededCatalog_containsExpectedProducts() {
        composeRule.onNodeWithText("Kopi Hitam", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Kopi Susu", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Es Teh Manis", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun rootComposition_reachesIdleWithoutCrash() {
        composeRule.waitForIdle()
    }
}
