package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.coinview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.CoinAPIHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.SQLiteHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.LoggedInUser
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.OHLCV
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinviewTestBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.concurrent.thread

class CoinViewTestFragment : Fragment() {

    private var _binding: FragmentCoinviewTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var coin: CoinAsset
    private lateinit var OHLCVData: List<OHLCV>
    private lateinit var graph :com.db.williamchart.view.LineChartView
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
       setUpGraph(coinData.toString())


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

       // val time = getSixMonthsAgo()
        //thread {
            //val data = coin.assetId?.let { CoinAPIHelper().getOHLCVData(it,time) }
            //if (data != null) {
                    //activity?.runOnUiThread {
                //
        //}




        return root
    }
    private fun setGraphTheme(theme:String) {

        if (theme == "CyberSpace") {
            // chart
            binding.LineGraph.lineColor = Color.parseColor("#C800FF")
            binding.LineGraph.gradientFillColors = intArrayOf(
                Color.parseColor("#8551B2"),
                Color.TRANSPARENT
            )
        } else if (theme =="Unicorn") {
            binding.LineGraph.lineColor = Color.parseColor("#FF1E00")
            binding.LineGraph.gradientFillColors = intArrayOf(
                Color.parseColor("#FF7700"),
                Color.parseColor("#FF0066")
            )
        }
        else if (theme =="Deep Ocean(colorblind red/green)") {
            binding.LineGraph.lineColor = Color.parseColor("#00FFFB")
            binding.LineGraph.gradientFillColors = intArrayOf(
                Color.parseColor("#168DF6"),
                Color.parseColor("#00264C")
            )
        }
        else if (theme =="Pandora Green(colorblind blue/yellow)") {
            binding.LineGraph.lineColor = Color.parseColor("#00FF2F")
            binding.LineGraph.gradientFillColors = intArrayOf(
                Color.parseColor("#16F659"),
                Color.parseColor("#152E0F")
            )
        }
    }

    private fun setUpGraph(ID: String) {
        var data = mutableListOf<Pair<String, Float>>()
        binding.LineGraph.animation.duration = 1000L
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid
        var theme = "CyberSpace"
        if (userId != null) {
             FirebaseHelper.getGraphTheme(userId) { graphTheme, error ->
                if (graphTheme != null) {
                    theme=graphTheme
                }
             }
        }
        setGraphTheme(theme)
        thread {
            OHLCVData = try {
                CoinAPIHelper().getOHLCVData(ID, getDateAYearAgo())
            } catch (e: Exception) {
                return@thread
            }
            if (OHLCVData.isNotEmpty()) {
                activity?.runOnUiThread {
                    for (i in 0 until OHLCVData.size) {
                        data.add(Pair(OHLCVData[i].timeClose, OHLCVData[i].priceClose.toFloat()))

                    }
                    binding.LineGraph.animate(data)
                    binding.LineGraph.onDataPointTouchListener = {index, _, _ ->
                        val isoDateString = data.toList()[index].first
                        val formattedDate = formatDate(isoDateString)
                        binding.dateText.text = formattedDate
                        binding.priceText.text ="$"+data.toList()[index].second.toString()
                    }
                    if (OHLCVData[OHLCVData.size - 1].priceOpen - OHLCVData[OHLCVData.size - 1].priceClose < 0) {
                        binding.TxtViewDifference.text = " ${OHLCVData[OHLCVData.size - 1].priceOpen - OHLCVData[OHLCVData.size - 1].priceClose}"
                        binding.TxtViewDifference.setTextColor(Color.parseColor("#D90429"))
                        binding.LineGraph.animation.duration = 1000L

                    } else {
                        binding.TxtViewDifference.text = "+ ${OHLCVData[OHLCVData.size - 1].priceOpen - OHLCVData[OHLCVData.size - 1].priceClose}"
                        binding.TxtViewDifference.setTextColor(Color.parseColor("#21BF73"))

                    }

                }
            }
            else {
                data = mutableListOf<Pair<String, Float>>()
                data.add(Pair("JAN", 4.5F))
                data.add(Pair("FEB", 5F))
                data.add(Pair("MAR", 12F))
                data.add(Pair("APR", 2F))
                data.add(Pair("MAY", 6F))
                data.add(Pair("JUN", 6F))
                data.add(Pair("JUL", 3F))
                data.add(Pair("AUG", 4F))
                data.add(Pair("SEPT", 6F))
                data.add(Pair("OCT", 9F))
                data.add(Pair("NOV", 12F))
                data.add(Pair("DEC", 15F))
                binding.LineGraph.animate(data)
            }
        }


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

    private fun formatDate(data: String): String {
        return try {
            // Updated input format to handle sub-second precision
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.getDefault())
            inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC") // Ensure UTC is set

            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Parse the date string and format it
            val date = inputFormat.parse(data)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            // Return a default value or an error indicator if parsing fails
            "Invalid date"
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
                    val dbHelper = SQLiteHelper(requireContext())


                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        dbHelper.addWatchlistItem(stockItem,currentUser.uid)
                        addStockToUserWatchlist(stockItem, currentUser.uid)
                    } else {
                        LoggedInUser.LoggedInUser.userId?.let {
                            dbHelper.addWatchlistItem(stockItem,
                                it
                            )
                        }
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
    fun getDateAYearAgo(): String {
        // Get the current date and time in UTC
        val now = OffsetDateTime.now(ZoneOffset.UTC)

        // Subtract a year
        val aYearAgo = now.minusYears(1)

        // Format the result to ISO 8601 string in UTC
        return aYearAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
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
