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
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.LoggedInUser
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.User
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.SellCrypto.SellCryptoFragment
import java.util.UUID
import kotlin.concurrent.thread

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


    fun updateTotalBalance(userId: String, amount: Double,priceDifference:Double, isBuying: Boolean, onComplete: (Boolean, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("totalBalance")

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentBalance = task.result?.getValue(Double::class.java) ?: 0.0

                val newBalance = if (isBuying) {
                    currentBalance + amount + priceDifference
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
                try {
                    LoggedInUser.LoggedInUser.totalBalance?.let { onComplete(it, null) }
                }
                catch (e: Exception) {
                    onComplete(null, task.exception?.message)
                }
            }
        }
    }

    fun updateDifference(userId: String, difference: Double) {
        val userRef = databaseReference.child(userId).child("difference")
        userRef.setValue(difference).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseHelper", "Difference updated successfully.")
            } else {
                Log.e("FirebaseHelper", "Failed to update difference: ${task.exception?.message}")
            }
        }
    }

    fun getDifference(userId: String, onComplete: (Double?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("difference")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val difference = task.result?.getValue(Double::class.java)
                onComplete(difference, null)
            } else {
                onComplete(null, task.exception?.message)
            }
        }
    }

    fun getStartValue(userId: String, onComplete: (Double?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("startValue")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val startValue = task.result?.getValue(Double::class.java)
                onComplete(startValue, null)
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

    fun getGraphTheme(userId: String, onComplete: (String?, String?) -> Unit) {
        val userRef = databaseReference.child(userId).child("graphTheme")
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val theme = task.result?.getValue(String::class.java)
                onComplete(theme, null)
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
            val currentUser = firebaseAuth.currentUser

            val totalBalance = startValue?.toDoubleOrNull()

            val startValue = totalBalance

            if (totalBalance != null)
            {
                deleteUserWalletsAndResetDifference(currentUser?.uid.toString())
            }

            if (currentUser != null) {
                val updatedData = mutableMapOf<String, Any?>()

                if (!username.isNullOrEmpty()) updatedData["username"] = username
                if (!name.isNullOrEmpty()) updatedData["name"] = name
                updatedData["email"] = currentUser.email
                if (totalBalance != null) updatedData["totalBalance"] = totalBalance
                if (!theme.isNullOrEmpty()) updatedData["theme"] = theme
                if (!graphTheme.isNullOrEmpty()) updatedData["graphTheme"] = graphTheme
                if (!language.isNullOrEmpty()) updatedData["language"] = language
                if (startValue != null) updatedData["startValue"] = startValue

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


    fun deleteUserWalletsAndResetDifference(userId: String) {
        val userRef = databaseReference.child(userId)

        userRef.child("wallets").removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseHelper", "Successfully deleted wallets for user: $userId")


            } else {
                Log.e("FirebaseHelper", "Failed to delete wallets for user: $userId", task.exception)
            }
        }

        userRef.child("difference").setValue(0.00).addOnCompleteListener { resetTask ->
            if (resetTask.isSuccessful) {
                Log.d("FirebaseHelper", "Successfully reset difference for user: $userId")
            } else {
                Log.e("FirebaseHelper", "Failed to reset difference for user: $userId", resetTask.exception)
            }
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

    fun calculateBitcoinToDollar(bitcoinAmount: String, coinAssetId: String): Double {

        val bitcoin = bitcoinAmount.toDoubleOrNull()
        val coin = coins.find { it.assetId == coinAssetId }
        if (coin == null) {
            Log.e("HomeFragment", "Coin is null for assetId: $coinAssetId")
        }
        return if (bitcoin != null && bitcoin > 0 && coin != null) {
            bitcoin * (coin.priceUsd?.toDouble() ?: 0.0)
        } else {
            0.0
        }
    }

    fun getAllWalletsAndCalculateTotalUsdAmount(userId: String, onComplete: (Double) -> Unit) {
        val walletsRef = databaseReference.child(userId).child("wallets")

        walletsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalUsdAmount = 0.0

                for (walletSnapshot in snapshot.children) {
                    val wallet = walletSnapshot.getValue(WalletModel::class.java)
                    if (wallet != null) {
                        val bitcoinAmount = wallet.amountInCoin?.toDouble()
                        val dollarAmount = FirebaseHelper.calculateBitcoinToDollar(bitcoinAmount.toString(),  wallet.walletType.toString())

                        if (bitcoinAmount != null) {
                            totalUsdAmount += bitcoinAmount * dollarAmount
                        }
                    }
                }

                onComplete(totalUsdAmount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseHelper", "Error fetching wallets: ${error.message}")
                onComplete(0.0)
            }
        })
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
