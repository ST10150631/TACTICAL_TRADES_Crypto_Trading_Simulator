package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings.SettingsFragment
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentHomeBinding

class MarketFragment : Fragment() {
    private lateinit var settingsButton: ImageButton
    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }

    companion object {
        fun newInstance() = MarketFragment()
    }

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_market, container, false)

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle(getString(R.string.trade))
            }
        }

        loadProfilePicture()
        loadTotalBalance()

        return rootView
    }

    private fun loadTotalBalance() {
        FirebaseHelper.getTotalBalance(FirebaseHelper.firebaseAuth.currentUser?.uid ?: "") { balance, error ->
            if (error != null) {
                Toast.makeText(context, "Error fetching balance: $error", Toast.LENGTH_LONG).show()
                val formattedBalance = "$0.00"

                val txtBalance = view?.findViewById<TextView>(R.id.TxtBalance)
                txtBalance?.text = formattedBalance

            } else {
                balance?.let {
                    val formattedBalance = String.format("$%,.2f", it)

                    val txtBalance = view?.findViewById<TextView>(R.id.TxtBalance)
                    txtBalance?.text = formattedBalance

                } ?: run {
                    Toast.makeText(context, "Balance is null", Toast.LENGTH_SHORT).show()
                    val formattedBalance = "$0.00"

                    val txtBalance = view?.findViewById<TextView>(R.id.TxtBalance)
                    txtBalance?.text = formattedBalance
                }
            }
        }

        FirebaseHelper.getDifference(FirebaseAuth.getInstance().currentUser?.uid.toString()) { difference, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Error fetching difference: $error", Toast.LENGTH_LONG).show()
            } else {
                difference?.let {
                    val formattedDifference = String.format("$%,.2f", it)
                    val txtDifference = view?.findViewById<TextView>(R.id.TxtDifference)
                    txtDifference?.text = formattedDifference

                    txtDifference?.setTextColor(
                        ContextCompat.getColor(requireContext(), if (it >= 0) R.color.green else R.color.red)
                    )

                    val stockGraph = view?.findViewById<ImageView>(R.id.stockGraph)
                    stockGraph?.setImageResource(if (it >= 0) R.drawable.stock_up_vector else R.drawable.stock_down_vector)

                } ?: run {
                    Toast.makeText(requireContext(), "Difference is null", Toast.LENGTH_SHORT).show()
                    val txtDifference = view?.findViewById<TextView>(R.id.TxtDifference)
                    txtDifference?.text = "$0.00"
                }
            }
        }

    }



    private fun loadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.getProfilePictureUrl(it.uid) { url, message ->

                val imageView = view?.findViewById<ImageView>(R.id.ivProfile)

                if (imageView != null) {
                    if (url != null) {
                        Glide.with(imageView.context).load(url).apply(
                            RequestOptions()
                                .placeholder(R.drawable.profile_image)
                                .error(R.drawable.profile_image)
                        ).into(imageView)
                    }
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe ViewModel data here if necessary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up resources or observers if needed
    }

    //Method that sends the user back to the add wallets screen
    private fun setupBackButton(view: View)
    {
        val backButton: ImageButton = view.findViewById(R.id.BtnBack)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    //---------------------------------------------------//
}
