package com.pos.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.test.ExperimentalTestApi

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class MainActivityRegressionTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun seededCatalog_containsExpectedProducts() {
        // Cukup tunggu salah satu produk dasar muncul untuk memastikan katalog telah dimuat
        composeRule.waitUntilAtLeastOneExists(
            hasText("Kopi Hitam", substring = true),
            timeoutMillis = 5000
        )
        
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
