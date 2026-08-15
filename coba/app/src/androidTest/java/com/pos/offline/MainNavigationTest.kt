package com.pos.offline

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class MainNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun openMenu() {
        // 1. Tunggu hingga tombol FAB dengan deskripsi "Buka menu" siap di layar
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription("Buka menu"),
            timeoutMillis = 5000
        )
        // 2. Klik tombol tersebut untuk membuka/mengekspand menu
        composeRule.onNodeWithContentDescription("Buka menu").performClick()
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
        // Panggil fungsi untuk mengklik "Buka menu"
        openMenu()
        
        // 3. Setelah menu terbuka, tunggu hingga opsi "Inventaris" muncul di layar
        composeRule.waitUntilAtLeastOneExists(
            hasText("Inventaris", substring = true),
            timeoutMillis = 3000
        )
        composeRule.onNodeWithText("Inventaris", substring = true)
            .assertIsDisplayed()
    }
}
