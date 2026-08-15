package com.pos.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Navigation smoke tests based on the visible labels used by ExpandableMenuFab.
 *
 * These tests intentionally use text semantics rather than screen coordinates.
 */
@RunWith(AndroidJUnit4::class)
class MainNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun openMenu() {
        // The main FAB may have no text/contentDescription in the current implementation.
        // If this test fails here, add a stable test tag/contentDescription to ExpandableMenuFab
        // rather than relying on coordinates.
        composeRule.onNodeWithText("Kasir").assertIsDisplayed()
    }

    @Test
    fun pos_isVisibleAfterLaunch() {
        composeRule.onNodeWithText("Kopi Hitam", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun inventoryLabel_isReachable() {
        // This is intentionally a semantic probe. If the menu is collapsed, the label may
        // not exist in the composition. In that case, the production FAB needs a stable
        // contentDescription/testTag for deterministic automation.
        openMenu()
        composeRule.onNodeWithText("Inventaris", substring = true)
            .assertIsDisplayed()
    }
}
