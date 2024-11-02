package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
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

    private fun loadTotalBalance() {
        // Get current user's ID
        val userId = FirebaseHelper.firebaseAuth.currentUser?.uid ?: ""

        // Fetch total balance
        FirebaseHelper.getTotalBalance(userId) { balance, error ->
            if (error != null) {
                Toast.makeText(context, "Error fetching balance: $error", Toast.LENGTH_LONG).show()
                // Use safe call for binding
                binding?.TxtBalance?.text = "$0.00"
            } else {
                balance?.let {
                    val formattedBalance = String.format("$%,.2f", it)
                    binding?.TxtBalance?.text = formattedBalance
                } ?: run {
                    Toast.makeText(context, "Balance is null", Toast.LENGTH_SHORT).show()
                    binding?.TxtBalance?.text = "$0.00"
                }
            }
        }
    }

    private fun loadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.getProfilePictureUrl(it.uid) { url, message ->
                if (url != null) {
                    Glide.with(this)
                        .load(url)
                        .apply(RequestOptions().transform(RoundedCorners(16)))
                        .into(binding.myImageView)
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
