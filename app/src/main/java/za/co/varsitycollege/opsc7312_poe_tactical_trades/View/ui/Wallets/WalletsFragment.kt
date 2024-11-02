package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletRepository
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet.AddWalletFragment

class WalletsFragment : Fragment() {

    private lateinit var btnAddWallet: ImageButton
    private lateinit var addWalletSection: LinearLayout
    private lateinit var btnSaveWallet: Button
    private lateinit var spinner: Spinner
    private lateinit var RbtnGroup :RadioGroup
    val assetLogoIdMap = mapOf(
        "Bitcoin" to R.drawable.btc_logo,
        "Ethereum" to R.drawable.eth_logo,
        "Tether" to R.drawable.usdt_logo,
        "Binance Coin" to R.drawable.bnb_logo,
        "Solana" to R.drawable.sol_logo,
        "USD Coin" to R.drawable.usdc_logo,
        "XRP" to R.drawable.xrp_logo,
        "Dogecoin" to R.drawable.doge_logo,
        "TRON" to R.drawable.trx_logo,
        "Cardano" to R.drawable.ada_logo,
        "Avalanche" to R.drawable.avax_logo,
        "Litecoin" to R.drawable.ltc_logo,
        "Chainlink" to R.drawable.link_logo,
        "Bitcoin Cash" to R.drawable.bch_logo,
        "Polkadot" to R.drawable.dot_logo,
        "LEO Token" to R.drawable.leo_logo,
        "Dai" to R.drawable.dai_logo,
        "Uniswap" to R.drawable.uni_logo,
        "Wrapped Bitcoin" to R.drawable.wbtc_logo,
        "Lido Staked Ether" to R.drawable.steth_logo,
        "NEAR Protocol" to R.drawable.near_logo,
        "Shiba Inu" to R.drawable.shib_logo,
        "Toncoin" to R.drawable.ton_logo
    )
    val assetIdMap = mapOf(
        "Bitcoin" to "BTC",
        "Ethereum" to "ETH",
        "Tether" to "USDT",
        "Binance Coin" to "BNB",
        "Solana" to "SOL",
        "USD Coin" to "USDC",
        "XRP" to "XRP",
        "Dogecoin" to "DOGE",
        "TRON" to "TRX",
        "Cardano" to "ADA",
        "Avalanche" to "AVAX",
        "Litecoin" to "LTC",
        "Chainlink" to "LINK",
        "Bitcoin Cash" to "BCH",
        "Polkadot" to "DOT",
        "LEO Token" to "LEO",
        "Dai" to "DAI",
        "Uniswap" to "UNI",
        "Wrapped Bitcoin" to "WBTC",
        "Lido Staked Ether" to "STETH",
        "NEAR Protocol" to "NEAR",
        "Shiba Inu" to "SHIB",
        "Toncoin" to "TON"
    )
    companion object {
        private const val ARG_WALLET_NAME = "wallet_name"
        private const val ARG_WALLET_IMAGE = "wallet_image"

        fun newInstance(walletName: String, walletImage: Int): WalletsFragment {
            val fragment = WalletsFragment()
            val args = Bundle().apply {
                putString(ARG_WALLET_NAME, walletName)
                putInt(ARG_WALLET_IMAGE, walletImage)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_wallets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Wallets")
            }
        }

        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: return

        fetchAndDisplayWallets(view, userId)

        btnAddWallet = view.findViewById(R.id.imgBtnAddWallet)
        addWalletSection = view.findViewById(R.id.AddWalletSection)

        btnAddWallet.setOnClickListener {
            addWalletSection.visibility = View.VISIBLE
            btnAddWallet.visibility = View.GONE
        }

        RbtnGroup = view.findViewById(R.id.gradientSelectorGroup)
        btnSaveWallet = view.findViewById(R.id.btnSaveWallet)
        spinner = view.findViewById(R.id.spinnerCoinName)

        btnSaveWallet.setOnClickListener {
            val coinName = spinner.selectedItem.toString()
            val gradientId = RbtnGroup.checkedRadioButtonId
            val selectedRadioButton = view.findViewById<RadioButton>(gradientId)
            saveWalletToFirebase(coinName, selectedRadioButton.text.toString(), userId)
            fetchAndDisplayWallets(view, userId)
            addWalletSection.visibility = View.GONE
            btnAddWallet.visibility = View.VISIBLE
        }
    }
    private fun fetchAndDisplayWallets(view: View, userId: String) {
        FirebaseHelper.getWalletsFromFirebase(userId) { wallets, error ->
            if (wallets != null) {
                displayWallets(view, wallets)
            } else {
                Toast.makeText(context, "Failed to load wallets: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayWallets(view: View, wallets: List<WalletModel>) {
        val layout: LinearLayout = view.findViewById(R.id.new_wallet)
        layout.removeAllViews()

        val walletMargin = (10 * resources.displayMetrics.density).toInt()

        for (wallet in wallets) {
            val walletView = layoutInflater.inflate(R.layout.wallet_item, layout, false)
            walletView.findViewById<TextView>(R.id.wallet_name_text).text = wallet.walletType

            val amountInCoin = wallet.amountInCoin

            val formattedAmount = amountInCoin?.toDoubleOrNull()?.let { amount ->
                String.format("%.2f", amount)
            } ?: "0.00"

            walletView.findViewById<TextView>(R.id.txtwallet_value).text = formattedAmount

            walletView.findViewById<View>(R.id.wallet_color_block).background =
                wallet.walletGradient?.let { getDrawable(requireContext(), it) }

            val imageView: ImageView = walletView.findViewById(R.id.wallet_image)
            wallet.walletImage.let { it?.let { it1 -> imageView.setImageResource(it1) } }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(walletMargin, 0, walletMargin, 0)
            }

            walletView.layoutParams = params
            layout.addView(walletView)
        }
        layout.requestLayout()
    }

    private fun saveWalletToFirebase(selectedCoin: String, selectedGradient: String, userId: String) {
        val walletImage: Int = assetLogoIdMap[selectedCoin]!!
        val selectedCoinCode = assetIdMap[selectedCoin]

        val gradientResId = when (selectedGradient) {
            "1" -> R.drawable.walletbg_1
            "2" -> R.drawable.walletbg_2
            "3" -> R.drawable.walletbg_3
            "4" -> R.drawable.walletbg_4
            "5" -> R.drawable.walletbg_5
            "6" -> R.drawable.walletbg_6
            else -> R.drawable.default_gradient_for_wallet // Fallback drawable
        }

        val newWallet = WalletModel(selectedCoinCode, "0%", "0", walletImage, gradientResId, gradientResId)

        FirebaseHelper.saveWalletToFirebase(userId, newWallet) { success, error ->
            if (success) {
                Toast.makeText(context, "Wallet saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error saving wallet: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
