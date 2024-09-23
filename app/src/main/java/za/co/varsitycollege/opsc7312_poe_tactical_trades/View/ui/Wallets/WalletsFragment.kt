package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets

import android.graphics.drawable.GradientDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet.AddWalletFragment

class WalletsFragment : Fragment() {
    //---------------------------------------------------//
    //Companion object that receives the wallet data from the add wallets fragment
    companion object {
        fun newInstance() = WalletsFragment()
        private const val ARG_WALLET_NAME = "wallet_name"
        private const val ARG_WALLET_IMAGE = "wallet_image"
        fun newInstance(walletName: String,walletImage: Int): WalletsFragment {
            val fragment = WalletsFragment()
            val args = Bundle().apply {
                putString(ARG_WALLET_NAME, walletName)
                putInt(ARG_WALLET_IMAGE, walletImage)
            }
            fragment.arguments = args
            return fragment
        }
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
        return inflater.inflate(R.layout.fragment_wallets, container, false)
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton(view)
        if (AddWalletFragment.WalletRepository.wallets.isEmpty()) {
            Toast.makeText(context, "No wallets added yet", Toast.LENGTH_SHORT).show()
        } else {
            refreshWallets(view)
        }
    }
    //---------------------------------------------------//
    //Method that itterates through the list of wallets and adds them to the view
    private fun refreshWallets(view: View) {
        val layout: LinearLayout = view.findViewById(R.id.new_wallet)
        layout.removeAllViews()
        val cryptoGradients = mapOf(
            "BTC" to R.drawable.gradient_for_bitcoin,
            "ETH" to R.drawable.gradient_for_ethereum,
            "USDT" to R.drawable.gradient_for_tether

        )
        for (wallet in AddWalletFragment.WalletRepository.wallets) {
            val walletView = layoutInflater.inflate(R.layout.wallet_item, layout, false)
            walletView.findViewById<TextView>(R.id.wallet_name_text).text = wallet.walletType
            val gradientResId = cryptoGradients[wallet.walletType] ?: R.drawable.default_gradient_for_wallet // Fallback drawable
            walletView.findViewById<View>(R.id.wallet_color_block).background =
                ContextCompat.getDrawable(requireContext(), gradientResId)
            val imageView: ImageView = walletView.findViewById(R.id.wallet_image)
            imageView.setImageResource(wallet.walletImage)
            layout.addView(walletView)
        }
    }
    //---------------------------------------------------//
    //Method that sends the user back to the add wallets screen
    private fun setupBackButton(view: View)
    {
        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackWallet)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    //---------------------------------------------------//
}
//-------------------------------------END OF FILE---------------------------------//