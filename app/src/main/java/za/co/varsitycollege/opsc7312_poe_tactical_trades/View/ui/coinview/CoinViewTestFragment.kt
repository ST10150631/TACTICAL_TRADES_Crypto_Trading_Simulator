package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.coinview

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinviewTestBinding

class CoinViewTestFragment : Fragment() {

    private var _binding: FragmentCoinviewTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var coin: CoinAsset

    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinviewTestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupBackButton(root)
        val coinData = arguments?.getString("coinData")
        if (coinData != null) {
            coin = coins.find { it.assetId == coinData } ?: coins[0]
            updateUI(coin)
        }
        binding.BtnAddToWatchList.setOnClickListener {
            addToWatchList()
        }


        return root
    }

    private fun updateUI(coinAsset: CoinAsset) {
        binding.CoinIconImage.setImageResource(coinAsset.logo)
        binding.TxtViewID.text = coinAsset.assetId
        binding.TxtViewName.text = coinAsset.name
        binding.txtEthLabel.text = coinAsset.name
        binding.TxtViewCurrent.text = coinAsset.priceUsd.toString()
        binding.txtWalletBalance.text = coinAsset.priceUsd.toString()
        binding.txtEthLabel.text = coinAsset.name
        binding.txtCurrency.text = coinAsset.assetId
        binding.txtPercentageChange.text = coinAsset.volume1dayUsd.toString()

    }

    private fun addToWatchList() {
        val coinId = binding.TxtViewID.text.toString()
        val coinName = binding.TxtViewName.text.toString()
        val coinPrice = binding.TxtViewCurrent.text.toString()
        val priceChange = binding.TxtViewDifference.text.toString()

        val imageResId = (binding.CoinIconImage.drawable as? BitmapDrawable)?.bitmap?.let {
            getImageResIdFromDrawable(it)
        } ?: R.drawable.logoregister


        val upDown: Boolean = priceChange.startsWith("+")

        // Create a StockItem instance
        val stockItem = StockItem(
            name = coinName,
            currentPrice = coinPrice,
            priceDifference = priceChange,
            upDown = upDown,
            imageRes = imageResId,
            stockId = coinId
        )

        val currentUser = auth.currentUser
        if (currentUser != null) {
            addStockToUserWatchlist(stockItem, currentUser.uid)
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStockToUserWatchlist(stockItem: StockItem, userId: String) {
        if (userId.isNotEmpty()) {
            val userWatchlistRef = FirebaseHelper.databaseReference.child(userId).child("watchlist")

            val stockIdRef = userWatchlistRef.child(stockItem.stockId)

            val watchListData = hashMapOf(
                "name" to stockItem.name,
                "currentPrice" to stockItem.currentPrice,
                "priceDifference" to stockItem.priceDifference,
                "upDown" to stockItem.upDown,
                "imageRes" to stockItem.imageRes,
                "stockId" to stockItem.stockId
            )

            stockIdRef.setValue(watchListData)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Stock item added to watchlist successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to add stock item: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                requireContext(),
                "User ID is empty, cannot add stock item",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    private fun getImageResIdFromDrawable(bitmap: Bitmap): Int {
        val drawable = BitmapDrawable(resources, bitmap)
        return resources.getIdentifier(binding.CoinIconImage.getTag().toString(), "drawable", requireActivity().packageName)
    }

    //Method that sends the user back to the add wallets screen
    private fun setupBackButton(view: View)
    {
        val backButton: ImageButton = view.findViewById(R.id.BtnBack)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
