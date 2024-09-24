package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings.SettingsFragment

class MarketFragment : Fragment() {
    private lateinit var settingsButton: ImageButton

    companion object {
        fun newInstance() = MarketFragment()
    }

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_market, container, false)
        setupBackButton(rootView)
        settingsButton = rootView.findViewById(R.id.ImgBtnSettings)
        settingsButton.setOnClickListener {
            // Navigate to the SettingsFragment using the Bottom Navigation
            val navController = (requireActivity() as MainActivity).navController
            navController.navigate(R.id.navigateToSettingsFragment)
        }

        return rootView
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
