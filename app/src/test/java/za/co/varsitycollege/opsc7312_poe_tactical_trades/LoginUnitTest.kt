import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.LoginActivity

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LoginUnitTest {

    private lateinit var loginActivity: LoginActivity

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockTask: Task<AuthResult>


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginActivity = LoginActivity()
        loginActivity.TestfirebaseAuth = mockFirebaseAuth
    }

    @Test
    fun testLoginUser_Success() {
        val email = "test@example.com"
        val password = "ValidPassword123!"

        // Mocking successful login
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)

        // Simulate success for the task
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockTask.exception).thenReturn(null)

        loginActivity.TestloginUser(email, password)

        // Verify that a successful login results in starting MainActivity
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
        // You might want to verify additional behavior like Toast messages or activity transitions
    }

    @Test
    fun testLoginUser_Failure() {
        val email = "test@example.com"
        val password = "InvalidPassword"

        // Mocking failed login
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)

        // Simulate failure for the task
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(Exception("Login failed"))

        loginActivity.TestloginUser(email, password)

        // Verify that the login failed
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
        // Add more verifications for the Toast message if necessary
    }
}
