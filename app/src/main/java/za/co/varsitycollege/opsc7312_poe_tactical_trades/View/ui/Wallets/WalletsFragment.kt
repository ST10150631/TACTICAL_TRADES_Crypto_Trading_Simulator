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
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
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

        refreshWallets(view)
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
            val gradientId = RbtnGroup.checkedRadioButtonId.toString()
            saveWallet(coinName, gradientId)
            refreshWallets(view)
            addWalletSection.visibility = View.GONE
            btnAddWallet.visibility = View.VISIBLE
        }
    }

    private fun refreshWallets(view: View) {
        val layout: LinearLayout = view.findViewById(R.id.new_wallet)
        layout.removeAllViews()

        // Convert 10dp to pixels
        val walletMargin = (10 * resources.displayMetrics.density).toInt()

        for (wallet in WalletRepository.wallets) {
            val walletView = layoutInflater.inflate(R.layout.wallet_item, layout, false)
            walletView.findViewById<TextView>(R.id.wallet_name_text).text = wallet.walletType
            walletView.findViewById<View>(R.id.wallet_color_block).background =
                ContextCompat.getDrawable(requireContext(), wallet.walletGradient)

            val imageView: ImageView = walletView.findViewById(R.id.wallet_image)
            wallet.walletImage?.let { imageView.setImageResource(it) }

            // Set ImageView size to 20dp by 20dp


            // Set layout params for walletView with margin and center gravity
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


    private fun saveWallet(selectedCoin: String, selectedGradient: String) {

            val walletImage:Int? = assetLogoIdMap[selectedCoin]
            val selectedCoinCode =assetIdMap[selectedCoin]

            val gradientResId = when (selectedGradient) {
                "2131362046" -> R.drawable.walletbg_1
                "2131362047" -> R.drawable.walletbg_2
                "2131362048" -> R.drawable.walletbg_3
                "2131362049" -> R.drawable.walletbg_4
                "2131362050" -> R.drawable.walletbg_5
                "2131362051" -> R.drawable.walletbg_6
                else -> R.drawable.default_gradient_for_wallet // Fallback drawable
            }

            WalletRepository.wallets.add(
                WalletModel(selectedCoinCode, "0%", "0", walletImage, gradientResId, gradientResId)
            )
            Toast.makeText(context, "Wallet Saved", Toast.LENGTH_SHORT).show()

    }

}
