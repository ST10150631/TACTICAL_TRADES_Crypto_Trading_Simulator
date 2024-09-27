package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.coinview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletRepository.wallets
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinviewTestBinding
import java.io.ByteArrayOutputStream

class CoinViewTestFragment : Fragment() {

    private var _binding: FragmentCoinviewTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var coin: CoinAsset
    private val storageRef = FirebaseStorage.getInstance().reference
    var hasWallet = false

    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinviewTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val coinData = arguments?.getString("coinData")
        if (coinData != null) {
            try {
                coin = coins.find { it.assetId == coinData } ?: coins[0]
                updateUI(coin)
            }catch (e: Exception){
                Toast.makeText(context, "Failed to load coin: $e", Toast.LENGTH_SHORT).show()
            }

        }

        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid
        if (userId != null) {
            getWallet(userId)
        }

        binding.BtnAddToWatchList.setOnClickListener {
            if (coinData != null) {
                coin = coins.find { it.assetId == coinData } ?: coins[0]
                addToWatchList(coin)
            }
        }
        binding.btnGoToBuyCoin.setOnClickListener{
            if (hasWallet)
            {
                val bundle = Bundle()
                bundle.putString("coinData",coin.assetId)
                val navController = findNavController()
                navController.navigate(R.id.navigation_buyCrypto,bundle)
            }
            else{
                Toast.makeText(this.context, "No wallet found for this coin.",  Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnGoToSellCoin.setOnClickListener {
            if (hasWallet)
            {
                val bundle = Bundle()
                bundle.putString("coinData",coin.assetId)
                val navController = findNavController()
                navController.navigate(R.id.navigation_SellCrypto, bundle)
            }
            else{
                Toast.makeText(this.context, "No wallet found for this coin.",  Toast.LENGTH_SHORT).show()
            }

        }

        return root
    }


    private fun getWallet(userId: String) {
        FirebaseHelper.getWalletsFromFirebase(userId) { wallets, error ->
            if (wallets != null) {
                displaySpecificWallet(wallets)
            } else {
                Toast.makeText(context, "Failed to load wallets: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displaySpecificWallet(wallets: List<WalletModel>) {
        val specificWallet =
            wallets.firstOrNull { it.walletType.equals(coin.assetId, ignoreCase = true) }

        if (specificWallet != null) {
            binding.txtWalletLabel.text = coin.name
            binding.txtWalletBalance.text = specificWallet.amountInCoin.toString()
            binding.txtPercentageChange.text = "+ ${specificWallet.percentage}%"
            binding.imgEthLogo.setImageResource(specificWallet.walletImage!!)
            binding.WalletGradient.setBackgroundResource(specificWallet.walletGradient!!)
            hasWallet = true
        } else {
            Toast.makeText(context, "No wallet found for this coin.", Toast.LENGTH_SHORT).show()
            binding.txtWalletLabel.text = ""
            binding.txtWalletBalance.text = ""
            binding.txtPercentageChange.text = ""
            binding.imgEthLogo.setImageResource(R.drawable.rounded_box)
            binding.WalletGradient.setBackgroundResource(R.drawable.rounded_box)
        }
    }

    override fun onResume() {
        super.onResume()
        val coinData = arguments?.getString("coinData")
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid

        if (coinData != null && userId != null) {
            coin = coins.find { it.assetId == coinData } ?: coins[0]
            updateUI(coin)
            getWallet(userId)
        }
    }

    private fun updateUI(coinAsset: CoinAsset) {
        // Set coin logo
        binding.CoinIconImage.setImageResource(coinAsset.logo)

        val assetId = coinAsset.assetId.toString()
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle(assetId)
            }
        }
        binding.TxtViewName.text = coinAsset.name
        val formattedPrice = String.format("%.2f", coinAsset.priceUsd)
        binding.TxtViewCurrent.text = "$" + formattedPrice
        binding.txtWalletBalance.text = "$" + formattedPrice
    }


    private fun addToWatchList(coinAsset: CoinAsset) {
        val coinId = coinAsset.assetId.toString()
        val coinName = binding.TxtViewName.text.toString()
        val coinPrice = binding.TxtViewCurrent.text.toString()
        val priceChange = binding.TxtViewDifference.text.toString()
        val upDown: Boolean = priceChange.startsWith("+")

        val imageResId = coinAsset.logo

        val bitmap = getBitmapFromDrawable(imageResId)

        if (bitmap != null) {
            uploadImageToFirebase(bitmap) { imageUrl ->
                if (imageUrl != null) {
                    val stockItem = StockItem(
                        name = coinName,
                        currentPrice = coinPrice,
                        priceDifference = priceChange,
                        upDown = upDown,
                        imageRes = imageUrl,
                        stockId = coinId
                    )

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        addStockToUserWatchlist(stockItem, currentUser.uid)
                    } else {
                        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve coin image", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap, callback: (String?) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        val uploadTask = imageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }.addOnFailureListener {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
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

    private fun getBitmapFromDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null
        // Create a bitmap with a smaller size
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        // Set a max size to avoid OutOfMemoryError
        val maxWidth = 200  // Adjust as necessary
        val maxHeight = 200 // Adjust as necessary
        val scale = Math.min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()

        val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, scaledWidth, scaledHeight)
        drawable.draw(canvas)

        return bitmap
    }


    private fun getImageResIdFromDrawable(bitmap: Bitmap): Int {
        val drawable = BitmapDrawable(resources, bitmap)
        return resources.getIdentifier(
            binding.CoinIconImage.getTag().toString(),
            "drawable",
            requireActivity().packageName
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
