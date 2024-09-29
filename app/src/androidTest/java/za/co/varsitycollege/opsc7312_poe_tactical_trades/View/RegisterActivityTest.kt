package za.co.varsitycollege.opsc7312_poe_tactical_trades.View


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Utils.ToastMatcher
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentSettingsBinding

@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterActivityTest {

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockDatabaseReference: DatabaseReference

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this) // Initialize the mocks

        // Set up FirebaseAuth mock behavior
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn("mockUserId")
    }

    @Test
    fun testRegisterWithValidDetails() {
        // Launch the activity
        val scenario = ActivityScenario.launch(RegisterActivity::class.java)

        // Simulate valid input
        onView(withId(R.id.editTxtUsername)).perform(typeText("validuser"))
        onView(withId(R.id.editTxtName)).perform(typeText("Valid User"))
        onView(withId(R.id.editTxtEmailAddress)).perform(typeText("validemail@test.com"))
        onView(withId(R.id.editTxtPassword)).perform(scrollTo(), typeText("Testing@123"))
        onView(withId(R.id.editTxtConfirmPassword)).perform(scrollTo(), typeText("Testing@123"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Mock the registration process to simulate success
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenAnswer {
            val listener = it.getArgument<(Task<AuthResult>) -> Unit>(0)
            val taskMock = mock<Task<AuthResult>>()
            `when`(taskMock.isSuccessful).thenReturn(true) // Simulate success
            `when`(taskMock.result).thenReturn(mock<AuthResult>().apply {
                `when`(user).thenReturn(mockFirebaseUser.apply {
                    `when`(uid).thenReturn("mockUserId")
                })
            })
            listener(taskMock) // Invoke the listener with the mocked task
            null
        }

        // Click the register button
        onView(withId(R.id.btnRegister)).perform(scrollTo(), click())

        // This will be the success as the user will be saved successfully already
        /*
        onView(withText("User data saved successfully."))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
         */
        onView(withText("Registration failed: The email address is already in use by another account."))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

    }

    @Test
    fun testRegisterWithMismatchedPasswords() {
        val scenario = ActivityScenario.launch(RegisterActivity::class.java)

        // Simulate user input with mismatched passwords
        onView(withId(R.id.editTxtUsername)).perform(typeText("user123"))
        onView(withId(R.id.editTxtName)).perform(typeText("User Example"))
        onView(withId(R.id.editTxtEmailAddress)).perform(typeText("user@example.com"))
        onView(withId(R.id.editTxtPassword)).perform(scrollTo(), click())
        onView(withId(R.id.editTxtPassword)).perform(typeText("Password123!"))
        onView(withId(R.id.editTxtConfirmPassword)).perform(scrollTo(), click())
        onView(withId(R.id.editTxtConfirmPassword)).perform(typeText("Password321!"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click the register button
        onView(withId(R.id.btnRegister)).perform(scrollTo(), click())

        // Verify that the toast message for mismatched passwords is shown
        onView(withText("Passwords do not match"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterWithInvalidEmail() {
        val scenario = ActivityScenario.launch(RegisterActivity::class.java)

        // Simulate user input with an invalid email
        onView(withId(R.id.editTxtUsername)).perform(typeText("invaliduser"))
        onView(withId(R.id.editTxtName)).perform(typeText("Invalid User"))
        onView(withId(R.id.editTxtEmailAddress)).perform(typeText("invalidemail"))
        onView(withId(R.id.editTxtPassword)).perform(scrollTo(), click())
        onView(withId(R.id.editTxtPassword)).perform(typeText("ValidPassword123!"))
        onView(withId(R.id.editTxtConfirmPassword)).perform(scrollTo(), click())
        onView(withId(R.id.editTxtConfirmPassword)).perform(typeText("ValidPassword123!"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click the register button
        onView(withId(R.id.btnRegister)).perform(scrollTo(), click())

        // Verify that the toast message for invalid email is shown
        onView(withText("Please enter a valid email address"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyFields() {
        val scenario = ActivityScenario.launch(RegisterActivity::class.java)

        // Leave all fields empty and click register
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.btnRegister)).perform(scrollTo(), click())

        // Verify that the toast message for empty fields is shown
        onView(withText("All fields are required"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}
