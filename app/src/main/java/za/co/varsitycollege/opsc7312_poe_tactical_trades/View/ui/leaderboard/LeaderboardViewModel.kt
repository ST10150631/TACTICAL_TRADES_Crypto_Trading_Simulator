package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem

class LeaderboardViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }
    private val databaseReference = FirebaseHelper.databaseReference

    fun fetchTopListItemsFromFirebase(onComplete: (List<Map<String, Any>>, String?) -> Unit) {
        FirebaseHelper.databaseReference.orderByChild("difference").limitToLast(5).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val topUsers = mutableListOf<Map<String, Any>>()

                for (userSnapshot in dataSnapshot.children) {
                    val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
                    val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                    val difference = userSnapshot.child("difference").getValue(Double::class.java) ?: 0.0
                    val profilePictureUrl = userSnapshot.child("profilePictureUrl").getValue(String::class.java)  ?: ""

                    topUsers.add(mapOf("username" to username, "email" to email, "difference" to difference,"profilePictureUrl" to  profilePictureUrl))
                }

                topUsers.sortByDescending { it["difference"] as Double }

                onComplete(topUsers.take(5), null)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(emptyList(), databaseError.message)
            }
        })
    }
}
