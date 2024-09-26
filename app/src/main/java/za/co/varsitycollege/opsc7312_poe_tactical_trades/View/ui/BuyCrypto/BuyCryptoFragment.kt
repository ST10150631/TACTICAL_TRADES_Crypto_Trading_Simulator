package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.BuyCrypto

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
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.SellCrypto.SellCryptoFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsFragment

class BuyCryptoFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_buy_crypto, container, false)
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Buy BTC")
            }
        }

        val confirmPurchase: ImageButton = view.findViewById(R.id.imgBtnConfirmPurchase)
        confirmPurchase.setOnClickListener()
        {
            Toast.makeText(context, "Crypto Purchased", Toast.LENGTH_SHORT).show()
        }
        val swapToSellCrypto: ImageButton = view.findViewById(R.id.imgSwapToSell)
        swapToSellCrypto.setOnClickListener()
        {
            val sellCryptoFragment = SellCryptoFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, sellCryptoFragment)
                .addToBackStack(null)
                .commit()
        }
        val imgViewAmountOfMoneyToPay = view.findViewById<ImageView>(R.id.imgViewAmountOfMoneyToPay)
        imgViewAmountOfMoneyToPay.setOnClickListener {
            openAmountInputDialog()
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
            updateAmountOfBitcoins(enteredAmount)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    //---------------------------------------------------//
    //Function that gets the amount of dollars that the user enters and
    // then divides it by 63498.70 to get the amount of bitcoins
    private fun updateAmountOfBitcoins(amount: String) {
        val txtAmountOfBitcoins = view?.findViewById<TextView>(R.id.txtAmountOfBitcoin)
        val dollarAmount = amount.toDoubleOrNull()
        if (dollarAmount != null && dollarAmount > 0) {
            val bitcoinAmount = dollarAmount / 63498.70
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