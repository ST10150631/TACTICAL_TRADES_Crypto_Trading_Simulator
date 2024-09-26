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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.BuyCrypto.BuyCryptoFragment

class SellCryptoFragment : Fragment() {
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

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Sell BTC")
            }
        }

        val confirmPurchase: ImageButton = view.findViewById(R.id.imgBtnConfirmSale)
        confirmPurchase.setOnClickListener()
        {
            Toast.makeText(context, "Crypto Sold", Toast.LENGTH_SHORT).show()
        }
        val swapToBuyCrypto: ImageButton = view.findViewById(R.id.btnImgSwapToBuyBTC)
        swapToBuyCrypto.setOnClickListener()
        {
            val buyCryptoFragment = BuyCryptoFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, buyCryptoFragment)
                .addToBackStack(null)
                .commit()
        }
        val imgBtnAddBTCToSell = view.findViewById<ImageButton>(R.id.imgBtnAddBTCToSell)
        imgBtnAddBTCToSell.setOnClickListener {
            openBitcoinInputDialog()
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
    //by 63498.70 and then updates the amount of dollars to the calculated amount
    private fun updateAmountOfDollarsReceived(bitcoinAmount: String) {
        val txtBitcoinAvailable = view?.findViewById<TextView>(R.id.txtBitcoinAvailable)
        val txtAmountOfDollarsReceivedForSale = view?.findViewById<TextView>(R.id.txtAmountOfDollarsReceivedForSale)
        val bitcoin = bitcoinAmount.toDoubleOrNull()
        if (bitcoin != null && bitcoin > 0) {
            txtBitcoinAvailable?.text = bitcoinAmount
            val dollarAmount = bitcoin * 63498.70
            txtAmountOfDollarsReceivedForSale?.text = String.format("%.2f", dollarAmount)
        } else {
            txtBitcoinAvailable?.text = "0.0"
            txtAmountOfDollarsReceivedForSale?.text = "0.00"
        }
    }
    //---------------------------------------------------//
}
//----------------------------------END OF FILE----------------------------------------//