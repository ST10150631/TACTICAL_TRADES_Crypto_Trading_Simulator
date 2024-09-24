package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletDialogListener
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletRepository
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsFragment


class AddWalletFragment : Fragment(),WalletDialogListener {
    //val that assigns an image to the coin
     val coinImages = mapOf(
        "BTC" to R.drawable.bitcoin,
        "ETH" to R.drawable.ethereum,
         "USDT" to R.drawable.dollar
    )
    //Value that assigns each colour for the coin
     val coinColors = mapOf(
        "BTC" to R.color.green,
        "ETH" to R.color.red,
        "USDT" to R.color.purple_200
    )
    //---------------------------------------------------//
    companion object {
        fun newInstance() = AddWalletFragment()
    }
    //---------------------------------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //---------------------------------------------------//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_wallet, container, false)
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the Add Wallet button
        val buttonAdd: ImageButton = view.findViewById(R.id.imgButtonAddWallet)
        buttonAdd.setOnClickListener {
            val walletDialog = WalletDialogFragment.newInstance(coinImages, coinColors)
            walletDialog.setTargetFragment(this, 0) // Set the target fragment for callback
            walletDialog.show(parentFragmentManager, "WalletDialogFragment")
        }
    }
    //---------------------------------------------------//
    //Function that saves the wallet details
    override fun onWalletSaved(selectedCoin: String, selectedGradient: String) {
        val walletImage = coinImages[selectedCoin] ?: R.drawable.amazon_icon
        val walletColor = coinColors[selectedCoin] ?: R.color.green
        val gradientResId = when (selectedGradient) {
            "gradient_for_bitcoin" -> R.drawable.gradient_for_bitcoin
            "gradient_for_ethereum" -> R.drawable.gradient_for_ethereum
            "gradient_for_tether" -> R.drawable.gradient_for_tether
            "gradient_colour_four" -> R.drawable.gradient_for_wallet_4
            "gradient_colour_five" -> R.drawable.gradient_five
            "gradient_colour_six" -> R.drawable.gradient_six
            else -> R.drawable.default_gradient_for_wallet
        }
        WalletRepository.wallets.add(
            WalletModel(selectedCoin, "0%", "0", walletImage, walletColor, gradientResId)
        )
        Toast.makeText(context, "Wallet Saved", Toast.LENGTH_SHORT).show()
    }
    //---------------------------------------------------//

}
//------------------------------END OF FILE---------------------------------//