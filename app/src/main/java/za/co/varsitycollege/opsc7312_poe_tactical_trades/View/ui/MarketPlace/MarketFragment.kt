package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

class MarketFragment : Fragment() {

    companion object {
        fun newInstance() = MarketFragment()
    }

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_market, container, false)
    }
}