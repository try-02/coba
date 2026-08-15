package com.pos.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Basic launch/smoke tests.
 *
 * NOTE: If your MainActivity is in a different package, change the import below.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityLaunchTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_andSeedProductIsVisible() {
        composeRule.onNodeWithText("Kopi Hitam", substring = true)
            .assertIsDisplayed()
    }
}
