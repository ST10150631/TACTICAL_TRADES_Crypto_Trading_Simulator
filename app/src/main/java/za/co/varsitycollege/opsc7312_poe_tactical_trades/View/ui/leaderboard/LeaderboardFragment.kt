package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.CoinAPIHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist.WatchListAdapter
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist.WatchListViewModel
import kotlin.concurrent.thread

class LeaderboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val viewModel = ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        viewModel.fetchTopListItemsFromFirebase { topUsers, error ->
            if (error == null) {
                adapter = LeaderboardAdapter(topUsers)
                recyclerView.adapter = adapter
            } else {
                // Handle error
            }
        }
    }
}
