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
class MainNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun openMenu() {
        // Menunggu tombol menu/label "Kasir" siap di layar
        composeRule.waitUntilAtLeastOneExists(
            hasText("Kasir"),
            timeoutMillis = 5000
        )
        composeRule.onNodeWithText("Kasir").assertIsDisplayed()
    }

    @Test
    fun pos_isVisibleAfterLaunch() {
        composeRule.waitUntilAtLeastOneExists(
            hasText("Kopi Hitam", substring = true),
            timeoutMillis = 5000
        )
        composeRule.onNodeWithText("Kopi Hitam", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun inventoryLabel_isReachable() {
        openMenu()
        // Jika menu membutuhkan animasi transisi saat dibuka, kita tunggu labelnya muncul
        composeRule.waitUntilAtLeastOneExists(
            hasText("Inventaris", substring = true),
            timeoutMillis = 3000
        )
        composeRule.onNodeWithText("Inventaris", substring = true)
            .assertIsDisplayed()
    }
}
