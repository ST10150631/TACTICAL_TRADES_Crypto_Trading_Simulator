package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.coinview

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinviewTestBinding

class CoinViewTestFragment : Fragment() {

    private var _binding: FragmentCoinviewTestBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinviewTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.BtnAddToWatchList.setOnClickListener {
            addToWatchList()
        }

        return root
    }

    private fun addToWatchList() {
        val coinId = binding.TxtViewID.text.toString()
        val coinName = binding.TxtViewName.text.toString()
        val coinPrice = binding.TxtViewCurrent.text.toString()
        val priceChange = binding.TxtViewDifference.text.toString()

        val imageResId = (binding.stockIconImage.drawable as? BitmapDrawable)?.bitmap?.let {
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
        return resources.getIdentifier(binding.stockIconImage.getTag().toString(), "drawable", requireActivity().packageName)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
