package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets

import android.os.Bundle
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
        val walletMargin = 10

        for (wallet in WalletRepository.wallets) {
            val walletView = layoutInflater.inflate(R.layout.wallet_item, layout, false)
            walletView.findViewById<TextView>(R.id.wallet_name_text).text = wallet.walletType
            walletView.findViewById<View>(R.id.wallet_color_block).background =
                ContextCompat.getDrawable(requireContext(), wallet.walletGradient)

            val imageView: ImageView = walletView.findViewById(R.id.wallet_image)
            imageView.setImageResource(wallet.walletImage)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(walletMargin, walletMargin, walletMargin, walletMargin)
            }
            walletView.layoutParams = params
            layout.addView(walletView)
        }
        layout.requestLayout()
    }

    private fun saveWallet(selectedCoin: String, selectedGradient: String) {

            val walletImage = R.drawable.amazon_icon

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
                WalletModel(selectedCoin, "0%", "0", walletImage, gradientResId, gradientResId)
            )
            Toast.makeText(context, "Wallet Saved", Toast.LENGTH_SHORT).show()

    }

}
