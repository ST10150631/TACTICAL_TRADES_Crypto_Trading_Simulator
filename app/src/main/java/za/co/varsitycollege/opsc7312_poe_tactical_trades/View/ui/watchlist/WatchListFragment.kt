package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity

class WatchListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WatchListAdapter
    private lateinit var settingsButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        setupBackButton(view)
        settingsButton = view.findViewById(R.id.ImgBtnSettings)
        settingsButton.setOnClickListener {
            // Navigate to the SettingsFragment using the Bottom Navigation
            val navController = (requireActivity() as MainActivity).navController
            navController.navigate(R.id.navigateToSettingsFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WatchListAdapter()
        recyclerView.adapter = adapter

        val viewModel = ViewModelProvider(this).get(WatchListViewModel::class.java)

        viewModel.watchList.observe(viewLifecycleOwner, { items ->
            adapter.submitList(items)
        })


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
