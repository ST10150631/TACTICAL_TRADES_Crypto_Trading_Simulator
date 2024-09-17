package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.editTxtUsername)
        val passwordEditText: EditText = findViewById(R.id.EditTxtPassword)
        val loginButton: ImageButton = findViewById(R.id.BtnLogin)
        val registerButton: Button = findViewById(R.id.BtnRegister)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        checkUser()
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
