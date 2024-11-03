package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Report

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.SQLiteHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ReportFragment : Fragment() {
    // Parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        // Fetch and display the difference
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid
        if (userId != null) {
            fetchAndDisplayDifference(userId, view)
            fetchAndDisplayWallets(view, userId)
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Report")
            }
        }

    }

    private fun fetchAndDisplayDifference(userId: String, view: View) {
        FirebaseHelper.getDifference(userId) { difference, error ->
            if (error != null) {
                Toast.makeText(context, "Error fetching difference: $error", Toast.LENGTH_LONG).show()
                return@getDifference
            }

            // Display the difference
            val differenceTextView: TextView = view.findViewById(R.id.tv_DisplayDifference)

            // Format the difference to 2 decimal places
            val balanceDifference = difference ?: 0.0
            val formattedDifference = String.format("%.2f", balanceDifference)
            differenceTextView.text = "$$formattedDifference"

            // Change the text color based on the difference
            if (balanceDifference < 0) {
                differenceTextView.setTextColor(Color.RED) // Negative difference
            } else {
                differenceTextView.setTextColor(Color.GREEN) // Positive difference
            }
        }
    }

    private fun fetchAndDisplayWallets(view: View, userId: String) {
        FirebaseHelper.getWalletsFromFirebase(userId) { wallets, error ->
            if (wallets != null) {
                displayWallets(view, wallets)

            } else {
                val dbHelper = SQLiteHelper(requireContext())
                val wallet = dbHelper.getWalletsByUserId(userId)
                displayWallets(view, wallet)
                Toast.makeText(context, "Loading Offline Wallets: ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayWallets(view: View, wallets: List<WalletModel>) {
        val layout: LinearLayout = view.findViewById(R.id.report_wallet)
        layout.removeAllViews()

        val walletMargin = (10 * resources.displayMetrics.density).toInt()

        for (wallet in wallets) {
            val walletView = layoutInflater.inflate(R.layout.wallet_item, layout, false)
            walletView.findViewById<TextView>(R.id.wallet_name_text).text = wallet.walletType

            val amountInCoin = wallet.amountInCoin

            val formattedAmount = amountInCoin?.toDoubleOrNull()?.let { amount ->
                String.format("%.2f", amount)
            } ?: "0.00"

            walletView.findViewById<TextView>(R.id.txtwallet_value).text = formattedAmount

            walletView.findViewById<View>(R.id.wallet_color_block).background =
                wallet.walletGradient?.let { getDrawable(requireContext(), it) }

            val imageView: ImageView = walletView.findViewById(R.id.wallet_image)
            wallet.walletImage?.let { imageView.setImageResource(it) }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(walletMargin, 0, walletMargin, 0)
            }

            walletView.layoutParams = params
            layout.addView(walletView)
        }
        layout.requestLayout()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}