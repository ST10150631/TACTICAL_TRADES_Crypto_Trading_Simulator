package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.BuyCrypto

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
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
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.Notification
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.channelID
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.notificationID
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity

class BuyCryptoFragment : Fragment() {
    private lateinit var coin: CoinAsset

    companion object {
        fun newInstance() = BuyCryptoFragment()
    }
    private val viewModel: BuyCryptoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //---------------------------------------------------//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_buy_crypto, container, false)

        return view
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val coinData = arguments?.getString("coinData")
        if (coinData != null) {
            coin = coins.find { it.assetId == coinData } ?: coins[0]

            updateUI(coin)
        }
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Buy Crypto")
            }
        }


        val confirmPurchase: ImageButton = view.findViewById(R.id.imgBtnConfirmPurchase)

        confirmPurchase.setOnClickListener {
            val dollarAmount =
                view.findViewById<TextView>(R.id.txtAmountOfDollarsAdded).text.toString()
            val coinAmount = view.findViewById<TextView>(R.id.txtAmountOfBitcoin).text.toString().replace(",", ".")

            if (dollarAmount.isEmpty()) {
                Toast.makeText(context, "Please enter an amount in dollars.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val dollars = dollarAmount.toDouble()
            val coins = coinAmount.toDouble()

            val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""
            FirebaseHelper.getTotalBalance(userId) { totalBalance, errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(
                        context,
                        "Error fetching balance: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                    return@getTotalBalance
                }

                if (dollars == null || dollars <= 0 || totalBalance == null || dollars > totalBalance) {
                    Toast.makeText(
                        context,
                        "Please enter a valid dollar amount. The amount can't be more than your total balance.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@getTotalBalance
                }
                createNotificationChannel()
                scheduleNotification()
                performPurchase(dollars, coins!!) { success, errorMessage ->
                    if (success) {
                        Toast.makeText(context, "Crypto Purchased Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Purchase failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }

                view.findViewById<TextView>(R.id.txtAmountOfDollarsAdded).text = "1.00"
                view.findViewById<TextView>(R.id.txtAmountOfBitcoin).text = "0.00"

            }
        }

        val swapToSellCrypto: ImageButton = view.findViewById(R.id.imgSwapToSell)
        swapToSellCrypto.setOnClickListener()
        {
            val bundle = Bundle()
            bundle.putString("coinData",coin.assetId)
            val navController = findNavController()
            navController.navigate(R.id.navigation_SellCrypto,bundle)
        }
        val imgViewAmountOfMoneyToPay = view.findViewById<ImageView>(R.id.imgViewAmountOfMoneyToPay)
        imgViewAmountOfMoneyToPay.setOnClickListener {
            openAmountInputDialog()
        }
    }

    private fun performPurchase(dollars: Double, coins: Double, callback: (Boolean, String?) -> Unit) {
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""

        FirebaseHelper.updateTotalBalance(userId, dollars, 0.00,false) { success, error ->
            if (!success) {
                callback(false, error ?: "Error updating balance.")
                return@updateTotalBalance
            }
        }
        val walletType = coin.assetId.toString()

        val newAmountInCoin = coins


        FirebaseHelper.updateWalletAmount(userId, walletType, newAmountInCoin, true) { success, errorMessage ->
            if (success) {
                Toast.makeText(context, "Wallet updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update wallet: ${errorMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //---------------------------------------------------//
    //Function that opens up a menu that allows the user to enter amount of dollars they wish to spend
    private fun openAmountInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Amount")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredAmount = input.text.toString()
            updateAmountOfcoins(enteredAmount)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    private fun scheduleNotification() {
        val intent = Intent(requireContext(), Notification::class.java)
        val message = "You have successfully purchased ${coin.name}"
        intent.putExtra("message", message)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val  alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
        val time = System.currentTimeMillis() +1000
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        AlertDialog.Builder(requireContext()).setTitle("Notification Scheduled").setMessage(
            "Message: $message"
        ).setPositiveButton("Okay"){ _, _ ->}.show()
    }
    private fun createNotificationChannel(){
        val name = "Tactical Trades Channel"
        val descriptionText = "Channel for Tactical Trades notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = descriptionText
        val notificationManager = requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    //Updates UI based on coin passed
    private fun updateUI(coinAsset: CoinAsset) {

        val txtViewName = view?.findViewById<TextView>(R.id.txtCurrency)
        txtViewName?.text = coinAsset.name

        val coinLogo = view?.findViewById<ImageView>(R.id.imgViewBTCLogoForSell)
        if (coinLogo != null) {
            coinLogo.setImageResource(coin.logo)
        }
    }
    //---------------------------------------------------//
    private fun updateAmountOfcoins(amount: String) {
        val txtAmountOfBitcoins = view?.findViewById<TextView>(R.id.txtAmountOfBitcoin)
        val dollarAmount = amount.toDoubleOrNull()
        if (dollarAmount != null && dollarAmount > 0) {
            val bitcoinAmount = dollarAmount / coin.priceUsd!!
            txtAmountOfBitcoins?.text = String.format("%.8f", bitcoinAmount)
        } else {
            txtAmountOfBitcoins?.text = "0.00000000"
        }
        val txtAmountOfDollarsAdded = view?.findViewById<TextView>(R.id.txtAmountOfDollarsAdded)
        txtAmountOfDollarsAdded?.text = amount
    }
    //---------------------------------------------------//
}
//--------------------------------END OF FILE----------------------//