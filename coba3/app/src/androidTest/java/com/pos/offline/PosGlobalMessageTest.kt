package com.pos.offline

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class) // Ditambahkan di sini agar berlaku untuk seluruh isi kelas
class PosGlobalMessageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForSeededPos() {
        composeRule.waitUntilAtLeastOneExists(
            hasText("Kopi Hitam", substring = true),
            timeoutMillis = 5_000,
        )
    }

    @Test
    fun openingCashDrawerWithoutPrinter_showsGlobalMessage() {
        waitForSeededPos()

        // Pastikan tombol laci kasir tersedia di POS.
        composeRule
            .onNode(
                hasContentDescription("Buka laci kasir"),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntilAtLeastOneExists(
            hasText(
                "Printer belum diatur. Atur printer default di tab Pengaturan.",
                substring = true,
            ),
            timeoutMillis = 5_000,
        )

        composeRule
            .onNode(
                hasText(
                    "Printer belum diatur. Atur printer default di tab Pengaturan.",
                    substring = true,
                ),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
    }

    @Test
    fun globalMessage_canBeDismissedByClickingPill() {
        waitForSeededPos()

        composeRule
            .onNode(
                hasContentDescription("Buka laci kasir"),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        val messageMatcher = hasText(
            "Printer belum diatur. Atur printer default di tab Pengaturan.",
            substring = true,
        )

        composeRule.waitUntilAtLeastOneExists(
            messageMatcher,
            timeoutMillis = 5_000,
        )

        composeRule
            .onNode(
                messageMatcher,
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        // Setelah dismiss, pill seharusnya tidak lagi terlihat.
        composeRule.waitUntil(
            timeoutMillis = 2_000,
        ) {
            composeRule
                .onAllNodes(
                    messageMatcher,
                    useUnmergedTree = true,
                )
                .fetchSemanticsNodes()
                .isEmpty()
        }
    }
}
