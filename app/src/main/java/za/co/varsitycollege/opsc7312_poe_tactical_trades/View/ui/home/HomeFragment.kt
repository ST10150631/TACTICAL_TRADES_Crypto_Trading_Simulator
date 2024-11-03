package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.LoggedInUser
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).setHeaderTitle("Home")

        loadProfilePicture()
        loadTotalBalance()

        return root
    }
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun loadTotalBalance() {
        if (!isConnected(requireContext())) {
            binding.TxtBalance.text = LoggedInUser.LoggedInUser.totalBalance.toString()
            return
        }
        // Get current user's ID
        var userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            userId = LoggedInUser.LoggedInUser?.userId ?: ""
        }

        // Initial balance
        var initialBalance = 0.00

        FirebaseHelper.getStartValue(userId) { startValue, error ->
            if (error != null) {
                Toast.makeText(context, "Error fetching initial balance: $error", Toast.LENGTH_LONG).show()
            } else {
                initialBalance = startValue ?: 0.0
            }
        }


        // Fetch total balance
        FirebaseHelper.getTotalBalance(userId) { balance, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Error fetching balance: $error", Toast.LENGTH_LONG).show()
                binding.TxtBalance.text = "$0.00"
                binding.TxtDifference.text = "$0.00"
            } else {
                balance?.let { currentBalance ->
                    val formattedBalance = String.format("$%,.2f", currentBalance)
                    binding.TxtBalance.text = formattedBalance

                    val difference = currentBalance - initialBalance
                    val formattedDifference = String.format("$%,.2f", difference)

                    FirebaseHelper.updateDifference(userId, difference)

                    binding.TxtDifference.apply {
                        text = formattedDifference
                        setTextColor(
                            if (difference >= 0) ContextCompat.getColor(requireContext(), R.color.green)
                            else ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    }

                    val stockGraph = view?.findViewById<ImageView>(R.id.stockGraph)
                    stockGraph?.setImageResource(if (difference > 0) R.drawable.stock_up_vector else R.drawable.stock_down_vector)

                } ?: run {
                    Toast.makeText(requireContext(), "Balance is null", Toast.LENGTH_SHORT).show()
                    binding.TxtBalance.text = "$0.00"
                    binding.TxtDifference.text = "$0.00"
                }
            }
        }

    }

    private fun loadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.getProfilePictureUrl(it.uid) { url, message ->
                if (url != null) {
                    Glide.with(binding.ivProfile.context).load(url).apply(
                        RequestOptions()
                            .placeholder(R.drawable.profile_image)
                            .error(R.drawable.profile_image)
                    ).into(binding.ivProfile)

                } else {
                    Toast.makeText(requireContext(), "Failed to load profile picture: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
