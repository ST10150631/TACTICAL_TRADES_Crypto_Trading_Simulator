package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletDialogListener
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletRepository
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsFragment

class WalletDialogFragment : DialogFragment() {
    private var listener: WalletDialogListener? = null
    private var selectedCoin: String? = null
    private var selectedGradient: String? = null // Changed to nullable
    private var coinImages: Map<String, Int>? = null
    private var coinColors: Map<String, Int>? = null
    //---------------------------------------------------//
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    //---------------------------------------------------//
    //Companion object
    companion object {
        fun newInstance(coinImages: Map<String, Int>, coinColors: Map<String, Int>): WalletDialogFragment {
            val fragment = WalletDialogFragment()
            val args = Bundle()
            args.putSerializable("coinImages", HashMap(coinImages))
            args.putSerializable("coinColors", HashMap(coinColors))
            fragment.arguments = args
            return fragment
        }
    }
    //---------------------------------------------------//
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as? WalletDialogListener
            ?: throw ClassCastException("$context must implement WalletDialogListener")
    }
    //---------------------------------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            coinImages = it.getSerializable("coinImages") as Map<String, Int>
            coinColors = it.getSerializable("coinColors") as Map<String, Int>
        }
    }
    //---------------------------------------------------//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_wallet_dialog, container, false)
    }
    //---------------------------------------------------//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner: Spinner = view.findViewById(R.id.spinnerCoinName)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_coin_names,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val gradient1: View = view.findViewById(R.id.gradient1)
        val gradient2: View = view.findViewById(R.id.gradient2)
        val gradient3: View = view.findViewById(R.id.gradient3)
        val gradient4: View = view.findViewById(R.id.gradient4)
        val gradient5: View = view.findViewById(R.id.gradient5)
        val gradient6: View = view.findViewById(R.id.gradient6)
        val allGradients = listOf(gradient1, gradient2, gradient3,gradient4,gradient5,gradient6)
        gradient1.setOnClickListener {
            selectedGradient = "gradient_for_bitcoin"
            highlightSelectedGradient(gradient1, allGradients)
        }
        gradient2.setOnClickListener {
            selectedGradient = "gradient_for_ethereum"
            highlightSelectedGradient(gradient2, allGradients)
        }
        gradient3.setOnClickListener {
            selectedGradient = "gradient_for_tether"
            highlightSelectedGradient(gradient3, allGradients)
        }
        gradient4.setOnClickListener {
            selectedGradient = "gradient_colour_four"
            highlightSelectedGradient(gradient4, allGradients)
        }
        gradient5.setOnClickListener {
            selectedGradient = "gradient_colour_five"
            highlightSelectedGradient(gradient5, allGradients)
        }
        gradient6.setOnClickListener {
            selectedGradient = "gradient_colour_six"
            highlightSelectedGradient(gradient6, allGradients)
        }
        val btnSave: Button = view.findViewById(R.id.btnSaveWallet)
        btnSave.setOnClickListener {
            selectedCoin = spinner.selectedItem.toString()
            if (!selectedCoin.isNullOrEmpty() && !selectedGradient.isNullOrEmpty()) {
                saveWallet(selectedCoin!!, selectedGradient!!) // Call the save method
                val walletFragment = WalletsFragment.newInstance()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, walletFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(context, "Please select both a coin and a gradient", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //---------------------------------------------------//
    //Function that highlights the selected colour for the wallet
    private fun highlightSelectedGradient(selectedView: View, allViews: List<View>) {
        allViews.forEach { it.setBackgroundResource(0) }  // Reset all gradients
        selectedView.setBackgroundResource(R.drawable.selected_gradient_border)  // Highlight selected gradient
    }
    //---------------------------------------------------//
    //Function that saves the wallets details
    private fun saveWallet(selectedCoin: String, selectedGradient: String) {
        val parentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? AddWalletFragment
        if (parentFragment != null) {
            val walletImage = parentFragment.coinImages[selectedCoin] ?: R.drawable.amazon_icon
            val walletColor = parentFragment.coinColors[selectedCoin] ?: R.color.green
            val gradientResId = when (selectedGradient) {
                "gradient_for_bitcoin" -> R.drawable.gradient_for_bitcoin
                "gradient_for_ethereum" -> R.drawable.gradient_for_ethereum
                "gradient_for_tether" -> R.drawable.gradient_for_tether
                "gradient_colour_four" -> R.drawable.gradient_for_wallet_4
                "gradient_colour_five" -> R.drawable.gradient_five
                "gradient_colour_six" -> R.drawable.gradient_six
                else -> R.drawable.default_gradient_for_wallet // Fallback drawable
            }
            WalletRepository.wallets.add(
                WalletModel(selectedCoin, "0%", "0", walletImage, walletColor, gradientResId)
            )
            Toast.makeText(context, "Wallet Saved", Toast.LENGTH_SHORT).show()
            dismiss() // Dismiss the dialog
        } else {
            Toast.makeText(context, "Error saving wallet: Parent fragment not found", Toast.LENGTH_SHORT).show()
        }
    }
}
//------------------------------END OF FILE-----------------------//