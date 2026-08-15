package com.pos.offline

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class InventoryGlobalMessageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    companion object {
        private const val TEST_PRODUCT_NAME = "TEST Global Message Product"
        private const val ADDED_MESSAGE = "Produk ditambahkan."
        private const val DELETED_MESSAGE =
            "Produk \"$TEST_PRODUCT_NAME\" dihapus."
    }

    private fun waitForSeededPos() {
        composeRule.waitUntilAtLeastOneExists(
            hasText("Kopi Hitam", substring = true),
            timeoutMillis = 5_000,
        )
    }

    private fun navigateToInventory() {
        waitForSeededPos()

        composeRule
            .onNode(
                hasContentDescription("Buka menu"),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodes(
                    hasText("Inventaris", substring = true),
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 3. SOLUSI UTAMA: Cari kontainer menu yang memiliki aksi klik (hasClickAction) 
        //    dan di dalamnya mengandung teks "Inventaris". Ini akan otomatis mengklik area ikon Anda.
        composeRule
            .onNode(
                hasAnyDescendant(hasText("Inventaris", substring = true)) and hasClickAction(),
                useUnmergedTree = true
            )
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntilAtLeastOneExists(
            hasText("Inventaris", substring = true),
            timeoutMillis = 5_000,
        )
    }

    @Test
    fun addAndDeleteProduct_usesGlobalMessagePill() {
        navigateToInventory()

        // Open the real "Tambah Produk" flow.
        composeRule
            .onNode(
                hasContentDescription("Tambah Produk"),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNode(
                hasText("Tambah Produk", substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()

        // In ProductFormDialog the first editable field is "Nama Produk".
        composeRule
            .onAllNodes(
                hasSetTextAction(),
                useUnmergedTree = true,
            )
            .get(0)
            .performTextInput(TEST_PRODUCT_NAME)

        composeRule
            .onNode(
                hasText("Simpan", substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        // Verify the actual InventoryViewModel -> messages -> GlobalMessageController path.
        composeRule.waitUntilAtLeastOneExists(
            hasText(ADDED_MESSAGE, substring = true),
            timeoutMillis = 5_000,
        )

        composeRule
            .onNode(
                hasText(ADDED_MESSAGE, substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()

        // Wait until the newly-created product is visible.
        composeRule.waitUntilAtLeastOneExists(
            hasText(TEST_PRODUCT_NAME, substring = true),
            timeoutMillis = 5_000,
        )

        // Clean up the test data using the real UI.
        composeRule
            .onNode(
                hasContentDescription("Edit $TEST_PRODUCT_NAME"),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNode(
                hasText("Hapus", substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNode(
                hasText("Hapus Produk?", substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()

        // Confirmation dialog also contains the actual delete button.
        composeRule
            .onNode(
                hasText("Hapus", substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
            .performClick()

        // Verify the second real message event from InventoryViewModel.
        composeRule.waitUntilAtLeastOneExists(
            hasText(DELETED_MESSAGE, substring = true),
            timeoutMillis = 5_000,
        )

        composeRule
            .onNode(
                hasText(DELETED_MESSAGE, substring = true),
                useUnmergedTree = true,
            )
            .assertIsDisplayed()
    }
}
