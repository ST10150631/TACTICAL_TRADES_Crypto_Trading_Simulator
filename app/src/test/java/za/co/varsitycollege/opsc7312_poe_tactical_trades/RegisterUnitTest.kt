package za.co.varsitycollege.opsc7312_poe_tactical_trades

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.RegisterActivity

@RunWith(AndroidJUnit4::class)
class RegisterUnitTest {
    private val activity = RegisterActivity()

    @Test
    fun `validateInput should return false when any field is empty`() {
        assertFalse(activity.TestvalidateInput("", "Name", "test@example.com", "Password123!", "Password123!"))
        assertFalse(activity.TestvalidateInput("Username", "", "test@example.com", "Password123!", "Password123!"))
        assertFalse(activity.TestvalidateInput("Username", "Name", "", "Password123!", "Password123!"))
        assertFalse(activity.TestvalidateInput("Username", "Name", "test@example.com", "", "Password123!"))
    }

    @Test
    fun `validateInput should return false for invalid email`() {
        assertFalse(activity.TestvalidateInput("Username", "Name", "invalid-email", "Password123!", "Password123!"))
    }

    @Test
    fun `validateInput should return false when passwords do not match`() {
        assertFalse(activity.TestvalidateInput("Username", "Name", "test@example.com", "Password123!", "DifferentPassword!"))
    }

    @Test
    fun `validateInput should return false when password does not meet complexity requirements`() {
        assertFalse(activity.TestvalidateInput("Username", "Name", "test@example.com", "simple", "simple"))
    }

    @Test
    fun `validateInput should return true for valid inputs`() {
        assertTrue(activity.TestvalidateInput("Username", "Name", "test134@example.com", "@Password123", "@Password123"))
    }
}