package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Intent
import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.LoginActivity


class LoginActivityTest {

    @Mock
    lateinit var mockFirebaseAuth: FirebaseAuth

    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun loginUser_validCredentials_shouldCallFirebaseAuth() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"

        // Act
        mockFirebaseAuth.signInWithEmailAndPassword(email, password)

        // Assert
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
    }
}