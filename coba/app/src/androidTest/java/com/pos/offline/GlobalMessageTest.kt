package com.pos.offline

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Global-message UI test placeholder.
 *
 * The controller itself is fully covered by GlobalMessageControllerTest.
 * UI-level message triggering is intentionally not guessed here because the
 * exact production events/labels vary by screen. Once the concrete trigger
 * is identified, add an end-to-end test using the real user action.
 */
@RunWith(AndroidJUnit4::class)
class GlobalMessageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_canHostGlobalMessageLayer() {
        // Launching MainActivity verifies the global CompositionLocal/pill integration
        // does not prevent the root composition from starting.
        composeRule.waitForIdle()
    }
}
