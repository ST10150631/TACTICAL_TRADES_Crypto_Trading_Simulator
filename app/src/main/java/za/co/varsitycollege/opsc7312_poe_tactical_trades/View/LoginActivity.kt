package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.NetworkUtils
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.SQLiteHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var TestfirebaseAuth: FirebaseAuth
    lateinit var Email: String
    lateinit var Password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sqliteHelper = SQLiteHelper(this)
        sqliteHelper.clearUsers()
        FirebaseHelper.initializeDatabaseFromFirebase(this)

        firebaseAuth = FirebaseAuth.getInstance()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val emailEditText: EditText = findViewById(R.id.EditTxtEmail)
        val passwordEditText: EditText = findViewById(R.id.EditTxtPassword)
        val loginButton: ImageButton = findViewById(R.id.BtnLogin)
        val registerButton: Button = findViewById(R.id.BtnRegister)
        Password = passwordEditText.text.toString().trim()
        Email = emailEditText.text.toString().trim()

        loginButton.setOnClickListener {
            val loginDetail = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(loginDetail).matches())
            {
                val email = loginDetail
                if (email.isNotEmpty() && password.isNotEmpty()) {

                    if (NetworkUtils.isNetworkAvailable(this)) {
                        firebaseLogin(email, password)
                    } else {
                        sqliteLogin(email, password)
                    }

                    //loginUser(email, password)
                } else {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }

        }


        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        if (NetworkUtils.isNetworkAvailable(this) == true) {
            checkUser()
        } else {
            FirebaseHelper.signOut()
        }
    }
    fun TestloginUser(email: String, password: String) {
        TestfirebaseAuth.signInWithEmailAndPassword(email, password)
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

    //old method
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

    private fun firebaseLogin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseHelper.initializeDatabaseFromFirebase(this) // Sync data with SQLite
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sqliteLogin(email: String, password: String) {
        val user = SQLiteHelper(this).getUserDataByEmail(email)
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Offline authentication failed", Toast.LENGTH_SHORT).show()
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
