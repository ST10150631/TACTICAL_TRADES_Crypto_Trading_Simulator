package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper.firebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val editTxtUsername : EditText = findViewById(R.id.editTxtUsername)
        val editTxtName : EditText = findViewById(R.id.editTxtName)
        val editTxtEmailAddress : EditText = findViewById(R.id.editTxtEmailAddress)
        val editTxtPassword : EditText = findViewById(R.id.editTxtPassword)
        val editTxtConfirmPassword : EditText = findViewById(R.id.editTxtConfirmPassword)

        val btnRegister : ImageButton = findViewById(R.id.btnRegister)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignOut: Button = findViewById(R.id.btnSignOut)

        btnRegister.setOnClickListener {
            val username = editTxtUsername.text.toString().trim()
            val name = editTxtName.text.toString().trim()
            val email = editTxtEmailAddress.text.toString().trim()
            val password = editTxtPassword.text.toString().trim()
            val confirmPassword = editTxtConfirmPassword.text.toString().trim()

            if (validateInput(username, name, email, password, confirmPassword)) {
                registerUserWithEmailAndPassword(username, name, email, password)
            }
        }

        btnLogin.setOnClickListener {
            navigateToLoginActivity()
        }

        btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            // Navigate to LoginActivity after signing out
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(
        username: String,
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        val passwordPattern =
            "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+\$).{8,}".toRegex()
        if (!passwordPattern.matches(password)) {
            Toast.makeText(this, "Password must meet complexity requirements", Toast.LENGTH_LONG)
                .show()
            return false
        }

        return true
    }

    private fun registerUserWithEmailAndPassword(username: String, name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registered successfully
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        storeUserData(userId, username, name, email)
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun storeUserData(userId: String, username: String, name: String, email: String) {
        // Create a User object with the provided details
        val user = User(username, name, email)

        val userReference = FirebaseHelper.databaseReference.child(userId)

        userReference.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Data saved successfully
                Toast.makeText(
                    this,
                    "User data saved successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToLoginActivity()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Failed to save user data: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
