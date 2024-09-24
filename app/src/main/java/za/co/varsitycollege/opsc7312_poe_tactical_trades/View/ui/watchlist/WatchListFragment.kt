package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings.SettingsFragment

class WatchListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WatchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
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

        val settingsButton: ImageButton = view.findViewById(R.id.BtnSettings)
        settingsButton.setOnClickListener {
            redirectToSettingsFragment()
        }

    }

    private fun redirectToSettingsFragment() {
        val fragment = SettingsFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_watchlist, fragment)
            .addToBackStack(null)
            .commit()


    }

}
