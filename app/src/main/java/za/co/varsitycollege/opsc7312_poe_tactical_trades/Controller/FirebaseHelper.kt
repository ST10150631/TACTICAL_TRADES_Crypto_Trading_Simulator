package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.User
import java.util.UUID
import android.widget.*

object FirebaseHelper {
    // Firebase Authentication instance
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Firebase Database reference
    val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://opsc7312-poe-tactical-trades-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")
    }

    // Firebase Storage reference
    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    fun signOut() {
        firebaseAuth.signOut()
    }


    fun uploadProfilePicture(userId: String, imageUri: Uri, onComplete: (String?, String?) -> Unit) {
        val fileName = UUID.randomUUID().toString()
        val fileReference = storageReference.child("profile_pictures/$userId/$fileName")

        fileReference.putFile(imageUri).addOnSuccessListener {
            fileReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                onComplete(downloadUrl.toString(), null)
            }.addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
        }.addOnFailureListener { exception ->
            onComplete(null, exception.message)
        }
    }

    fun getProfilePictureUrl(userId: String, onComplete: (String?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("profilePictureUrl")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val profilePictureUrl = task.result?.getValue(String::class.java)
                onComplete(profilePictureUrl, null)
            } else {
                onComplete(null, task.exception?.message)
            }
        }
    }

    fun updateUserData(
        context: Context,
        userId: String,
        username: String?,
        name: String?,
        email: String,
        theme: String?,
        graphTheme: String?,
        language: String?
    ) {
        val userReference = databaseReference.child(userId)

        userReference.get().addOnSuccessListener { snapshot ->
            val currentUser = snapshot.getValue(User::class.java)

            if (currentUser != null) {
                val updatedUser = User(
                    username = if (!username.isNullOrEmpty()) username else currentUser.username,
                    name = if (!name.isNullOrEmpty()) name else currentUser.name,
                    email = currentUser.email,
                    theme = if (!theme.isNullOrEmpty()) theme else currentUser.theme,
                    graphTheme = if (!graphTheme.isNullOrEmpty()) graphTheme else currentUser.graphTheme,
                    language = if (!language.isNullOrEmpty()) language else currentUser.language,
                    profilePictureUrl = currentUser.profilePictureUrl
                )

                userReference.setValue(updatedUser).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "User data updated successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "User not found.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch user data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun storeUserData(
        context: Context,
        userId: String,
        username: String?,
        name: String?,
        email: String,
        theme: String?,
        graphTheme: String?,
        language: String?
    ) {
        signOut()

        val userReference = databaseReference.child(userId)

        userReference.get().addOnSuccessListener { snapshot ->
            val currentUser = snapshot.getValue(User::class.java)

            if (currentUser == null) {
                // No user found, create a new user entry
                val newUser = User(
                    username = username ?: "",
                    name = name ?: "",
                    email = email,
                    theme = theme ?: "",
                    graphTheme = graphTheme ?: "",
                    language = language ?: "",
                    profilePictureUrl = ""
                )

                // Store the new user data in the database
                userReference.setValue(newUser).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "User data stored successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to store user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // User already exists
                Toast.makeText(context, "User already exists.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

}
