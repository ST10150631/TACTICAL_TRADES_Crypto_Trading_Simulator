package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://opsc7312-poe-tactical-trades-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")
    }
}
