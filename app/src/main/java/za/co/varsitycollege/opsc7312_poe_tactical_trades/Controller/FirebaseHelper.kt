package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.User
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
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


    fun initializeDatabaseFromFirebase(context: Context) {
        val sqliteHelper = SQLiteHelper(context)

        // Clear existing user data in SQLite
        sqliteHelper.clearUsers()

        // Fetch users from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                    val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
                    val totalBalance = userSnapshot.child("totalBalance").getValue(Double::class.java) ?: 0.0
                    val notificationsEnabled = userSnapshot.child("notificationsEnabled").getValue(Boolean::class.java) ?: false
                    val profilePictureUrl = userSnapshot.child("profilePictureUrl").getValue(String::class.java) ?: ""
                    val graphTheme = userSnapshot.child("graphTheme").getValue(String::class.java) ?: ""
                    val language = userSnapshot.child("language").getValue(String::class.java) ?: ""

                    sqliteHelper.addUser(userId, email, name, username, totalBalance, notificationsEnabled, profilePictureUrl, graphTheme, language)


                    val walletsSnapshot = userSnapshot.child("wallets")
                    for (walletSnapshot in walletsSnapshot.children) {
                        val wallet = WalletModel(
                            walletType = walletSnapshot.child("walletType").getValue(String::class.java),
                            amountInCoin = walletSnapshot.child("amountInCoin").getValue(String::class.java),
                            color = walletSnapshot.child("color").getValue(Int::class.java),
                            percentage = walletSnapshot.child("percentage").getValue(String::class.java),
                            walletGradient = walletSnapshot.child("walletGradient").getValue(Int::class.java),
                            walletImage = walletSnapshot.child("walletImage").getValue(Int::class.java)
                        )
                        sqliteHelper.addWallet(wallet, userId)
                    }

                    val watchlistSnapshot = userSnapshot.child("watchlist")
                    for (stockSnapshot in watchlistSnapshot.children) {
                        val stockItem = StockItem(
                            stockId = stockSnapshot.child("stockId").getValue(String::class.java) ?: "",
                            name = stockSnapshot.child("name").getValue(String::class.java) ?: "",
                            imageRes = stockSnapshot.child("imageRes").getValue(String::class.java) ?: "",
                            currentPrice = stockSnapshot.child("currentPrice").getValue(String::class.java) ?: "",
                            priceDifference = stockSnapshot.child("priceDifference").getValue(String::class.java) ?: "",
                            upDown = stockSnapshot.child("upDown").getValue(Boolean::class.java) ?: false
                        )
                        sqliteHelper.addWatchlistItem(stockItem, userId)
                    }


                }
                Log.d("FirebaseHelper", "Users successfully loaded from Firebase and stored in SQLite.")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseHelper", "Error loading users from Firebase: ${databaseError.message}")
            }
        })
    }


    fun updateTotalBalance(userId: String, amount: Double, isBuying: Boolean, onComplete: (Boolean, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("totalBalance")

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentBalance = task.result?.getValue(Double::class.java) ?: 0.0

                val newBalance = if (isBuying) {
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
        startValue: String?,
        theme: String?,
        graphTheme: String?,
        language: String?
    ) {
        val userReference = databaseReference.child(userId)

        userReference.get().addOnSuccessListener { snapshot ->
            val currentUser = snapshot.getValue(User::class.java)
            val totalBalance = startValue?.toDoubleOrNull()

            if (currentUser != null) {
                val updatedData = mutableMapOf<String, Any?>()

                if (!username.isNullOrEmpty()) updatedData["username"] = username
                if (!name.isNullOrEmpty()) updatedData["name"] = name
                updatedData["email"] = currentUser.email
                if (totalBalance != null) updatedData["totalBalance"] = totalBalance
                if (!theme.isNullOrEmpty()) updatedData["theme"] = theme
                if (!graphTheme.isNullOrEmpty()) updatedData["graphTheme"] = graphTheme
                if (!language.isNullOrEmpty()) updatedData["language"] = language
                updatedData["profilePictureUrl"] = currentUser.profilePictureUrl

                userReference.updateChildren(updatedData).addOnCompleteListener { task ->
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


    fun saveWalletToFirebase(userId: String, wallet: WalletModel, onComplete: (Boolean, String?) -> Unit) {
        databaseReference.child(userId).child("wallets").child(wallet.walletType.toString()).setValue(wallet)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun getWalletsFromFirebase(userId: String, onComplete: (List<WalletModel>?, String?) -> Unit) {
        val walletsRef = databaseReference.child(userId).child("wallets")
        walletsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val wallets = task.result?.children?.mapNotNull { it.getValue(WalletModel::class.java) }
                onComplete(wallets, null)
            } else {
                onComplete(null, task.exception?.message)
            }
        }
    }

    fun updateWalletAmount(userId: String, walletType: String, newAmountInCoin: Double, isBuying: Boolean, onComplete: (Boolean, String?) -> Unit) {
        val walletRef = databaseReference.child(userId).child("wallets").child(walletType)

        walletRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val wallet = task.result?.getValue(WalletModel::class.java)
                if (wallet != null) {
                    val currentAmountInCoin = wallet.amountInCoin?.toDoubleOrNull() ?: 0.0

                    wallet.amountInCoin = (if (isBuying) {
                        currentAmountInCoin + newAmountInCoin
                    } else {
                        currentAmountInCoin - newAmountInCoin
                    }).toString()

                    walletRef.setValue(wallet).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, updateTask.exception?.message)
                        }
                    }
                } else {
                    onComplete(false, "Wallet not found.")
                }
            } else {
                onComplete(false, task.exception?.message)
            }
        }
    }


    fun getaWalletFromFirebase(userId: String, walletType: String, onComplete: (WalletModel?) -> Unit) {
        val walletsRef = databaseReference.child(userId).child("wallets")
        walletsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val wallet = task.result?.children
                    ?.mapNotNull { it.getValue(WalletModel::class.java) }
                    ?.firstOrNull { it.walletType == walletType } // Assuming WalletModel has a 'type' property
                onComplete(wallet)
            } else {
                onComplete(null)
            }
        }
    }


}
