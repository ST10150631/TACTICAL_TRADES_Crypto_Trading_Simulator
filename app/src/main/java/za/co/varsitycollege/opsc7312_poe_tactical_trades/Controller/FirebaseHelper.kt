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

    /*

    so this will be to use that update total balance in the firebase
    //when adding (selling)
    FirebaseHelper.updateTotalBalance(FirebaseHelper.firebaseAuth.currentUser?.uid ?: "", 50.0, true) { success, error ->
        if (success) {
            Toast.makeText(context, "Balance updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error updating balance: $error", Toast.LENGTH_LONG).show()
        }
    }
    //when substracting (buying)
    FirebaseHelper.updateTotalBalance(FirebaseHelper.firebaseAuth.currentUser?.uid ?: "", 20.0, false) { success, error ->
        if (success) {
            Toast.makeText(context, "Balance updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error updating balance: $error", Toast.LENGTH_LONG).show()
        }
    }

    */

    fun updateTotalBalance(userId: String, amount: Double, isAddition: Boolean, onComplete: (Boolean, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("totalBalance")

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentBalance = task.result?.getValue(Double::class.java) ?: 0.0

                val newBalance = if (isAddition) {
                    currentBalance + amount
                } else {
                    currentBalance - amount
                }

                userRef.setValue(newBalance).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, updateTask.exception?.message)
                    }
                }
            } else {
                onComplete(false, task.exception?.message)
            }
        }
    }


    fun getTotalBalance(userId: String, onComplete: (Double?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("totalBalance")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val totalBalance = task.result?.getValue(Double::class.java)
                onComplete(totalBalance, null)
            } else {
                onComplete(null, task.exception?.message)
            }
        }
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

    fun getTheme(userId: String, onComplete: (String?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("theme")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val theme = task.result?.getValue(String::class.java)
                onComplete(theme, null)
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
        startValue : String?,
        theme: String?,
        graphTheme: String?,
        language: String?
    ) {
        val userReference = databaseReference.child(userId)

        userReference.get().addOnSuccessListener { snapshot ->
            val currentUser = snapshot.getValue(User::class.java)
            val totalBalance = startValue?.toDoubleOrNull()

            if (currentUser != null) {
                val updatedUser = User(
                    username = if (!username.isNullOrEmpty()) username else currentUser.username,
                    name = if (!name.isNullOrEmpty()) name else currentUser.name,
                    email = currentUser.email,
                    totalBalance = totalBalance,
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
}
