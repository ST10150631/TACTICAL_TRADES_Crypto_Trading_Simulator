package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.AddWallet

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets.WalletsViewModel

class AddWalletFragment : Fragment() {
    //Object that saves the wallets to a list so that they can be displayed in the wallets fragment
    object WalletRepository {
        val wallets = mutableListOf<WalletModel>()
    }
    //val that assigns an image to the coin
    private val coinImages = mapOf(
        "BTC" to R.drawable.btc_logo,
        "ETH" to R.drawable.eth_logo,
         "USDT" to R.drawable.usdt_logo
    )
    //Value that assigns each colour for the coin
    private val coinColors = mapOf(
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
        setUpSpinner(view)
        addWalletBlockToWalletScreen(view)
    }
    //---------------------------------------------------//
    //Method that populates the spinner and adds its layout
    private fun setUpSpinner(view: View)
    {
        val spinner: Spinner = view.findViewById(R.id.spnWalletType)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_itemsForWalletType,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    //---------------------------------------------------//
    //Method that saves the wallet data
    private fun addWalletBlockToWalletScreen(view:View)
    {
        setUpSpinner(view)
        val buttonAdd: ImageButton = view.findViewById(R.id.imgButtonAddWallet)
        buttonAdd.setOnClickListener {
            val spinner: Spinner = view.findViewById(R.id.spnWalletType)
            val selectedWallet = spinner.selectedItem.toString()
            val walletImage = coinImages[selectedWallet] ?: R.drawable.amazon_icon
            val walletColor = coinColors[selectedWallet] ?: R.color.green
            WalletRepository.wallets.add(WalletModel(selectedWallet, "0%", "0", walletImage, walletColor))
            Toast.makeText(context, "Wallet Added", Toast.LENGTH_SHORT).show()
            val walletFragment = WalletsFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, walletFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
//------------------------------END OF FILE---------------------------------//