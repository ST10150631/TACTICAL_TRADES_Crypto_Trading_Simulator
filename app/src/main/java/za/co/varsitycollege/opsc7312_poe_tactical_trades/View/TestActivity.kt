package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet.AddWalletFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.BuyCrypto.BuyCryptoFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsFragment

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        if (savedInstanceState == null) {
            // Initialize with AddWalletFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddWalletFragment.newInstance())
                .commit()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}