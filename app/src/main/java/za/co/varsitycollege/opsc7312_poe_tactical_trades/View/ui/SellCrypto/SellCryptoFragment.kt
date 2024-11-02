package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.SellCrypto

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.BuyCrypto.BuyCryptoFragment

class SellCryptoFragment : Fragment() {
    private lateinit var coin: CoinAsset

    companion object {
        fun newInstance() = SellCryptoFragment()
    }
    private val viewModel: SellCryptoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //---------------------------------------------------//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sell_crypto, container, false)
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val coinData = arguments?.getString("coinData")
        if (coinData != null) {
            coin = coins.find { it.assetId == coinData } ?: coins[0]
            updateUI(coin)
        }


        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Sell Crypto")
            }
        }

        val confirmPurchase: ImageButton = view.findViewById(R.id.imgBtnConfirmSale)
        confirmPurchase.setOnClickListener()
        {
            val dollarAmount = view.findViewById<TextView>(R.id.txtAmountOfDollarsReceivedForSale).text.toString().replace(",", ".")

            val coinAmount = view.findViewById<TextView>(R.id.txtBitcoinAvailable).text.toString()

            if (coinAmount.isEmpty()) {
                Toast.makeText(context, "Please enter a coin amount.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""
            val walletType = coin.assetId?.toString() ?: ""

            FirebaseHelper.getaWalletFromFirebase(userId, walletType) { wallet ->
                if (wallet != null) {
                    val amountInCoin = wallet.amountInCoin?.toDoubleOrNull()

                    val dollars = dollarAmount.toDouble()
                    val coins = coinAmount.toDouble()
                    val priceDifference = arguments?.getDouble("priceChange")

                    if (coins == null || coins <= 0 || amountInCoin == null || coins > amountInCoin) {
                        Toast.makeText(
                            context,
                            "Please enter a valid coin amount. The amount can't be more than your wallet balance.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@getaWalletFromFirebase
                    }

                    performPurchase(dollars!!, coins!!, priceDifference!!) { success, errorMessage ->
                        if (success) {
                            Toast.makeText(context, "Crypto Sold", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Purchase failed: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }

                    view.findViewById<TextView>(R.id.txtAmountOfDollarsReceivedForSale).text = "0.00"
                    view.findViewById<TextView>(R.id.txtBitcoinAvailable).text = "0.00"

                } else {
                    println("Wallet not found")
                }
            }
        }
        val swapToBuyCrypto: ImageButton = view.findViewById(R.id.btnImgSwapToBuyBTC)
        swapToBuyCrypto.setOnClickListener()
        {
            val bundle = Bundle()
            bundle.putString("coinData",coin.assetId)
            val navController = findNavController()
            navController.navigate(R.id.navigation_buyCrypto,bundle)
        }

        val imgBtnAddBTCToSell = view.findViewById<ImageButton>(R.id.imgViewAmountOfMoneyToPay)
        imgBtnAddBTCToSell.setOnClickListener {
            openBitcoinInputDialog()
        }
    }

    private fun performPurchase(dollars: Double, coins: Double, priceDifference: Double, callback: (Boolean, String?) -> Unit) {
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""

        FirebaseHelper.updateTotalBalance(userId, dollars, priceDifference,true ) { success, error ->
            if (!success) {
                callback(false, error ?: "Error updating balance.")
                return@updateTotalBalance
            }
        }
        val walletType = coin.assetId.toString()

        val newAmountInCoin = coins


        FirebaseHelper.updateWalletAmount(userId, walletType, newAmountInCoin, false) { success, errorMessage ->
            if (success) {
                Toast.makeText(context, "Wallet updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update wallet: ${errorMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(coinAsset: CoinAsset) {

        val txtViewName = view?.findViewById<TextView>(R.id.txtCurrency)
        txtViewName?.text = coinAsset.name

        val coinLogo = view?.findViewById<ImageView>(R.id.imgViewCurrencyImage)
        if (coinLogo != null) {
            coinLogo.setImageResource(coin.logo)
        }
    }
    //---------------------------------------------------//
    //Function that displays a user with an input option
    private fun openBitcoinInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Bitcoin Amount")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredBitcoinAmount = input.text.toString()
            updateAmountOfDollarsReceived(enteredBitcoinAmount)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    //---------------------------------------------------//
    //Function that gets the amount of bitcoin and then multiplies it
    private fun updateAmountOfDollarsReceived(bitcoinAmount: String) {
        val txtBitcoinAvailable = view?.findViewById<TextView>(R.id.txtBitcoinAvailable)
        val txtAmountOfDollarsReceivedForSale = view?.findViewById<TextView>(R.id.txtAmountOfDollarsReceivedForSale)
        val bitcoin = bitcoinAmount.toDoubleOrNull()
        if (bitcoin != null && bitcoin > 0) {
            txtBitcoinAvailable?.text = bitcoinAmount
            val dollarAmount = bitcoin * (coin.priceUsd?.toDouble() ?: 0.0)
            txtAmountOfDollarsReceivedForSale?.text = String.format("%.2f", dollarAmount)
        } else {
            txtBitcoinAvailable?.text = "0.0"
            txtAmountOfDollarsReceivedForSale?.text = "0.00"
        }
    }
    //---------------------------------------------------//
}
//----------------------------------END OF FILE----------------------------------------//