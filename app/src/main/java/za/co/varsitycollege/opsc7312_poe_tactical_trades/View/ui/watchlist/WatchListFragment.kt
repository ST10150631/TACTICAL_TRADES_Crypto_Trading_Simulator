package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.CoinAPIHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.SQLiteHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.LoggedInUser
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace.MyallcoinsRecyclerViewAdapter
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings.SettingsFragment
import kotlin.concurrent.thread

class WatchListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WatchListAdapter
    private lateinit var settingsButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
       // setupBackButton(view)
        thread {
            val CoinList = try {
                CoinAPIHelper().top25FromAPI()
            } catch (e: Exception) {
                return@thread
            }
        }

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle(getString(R.string.watchlist))
            }
        }

        return view
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun getOfflineWatchList(): List<StockItem> {
       val dbHelper = SQLiteHelper(requireContext())
         val watchList = LoggedInUser.LoggedInUser.userId?.let {
             dbHelper.getWatchlistItemsByUserId(
                 it
             )
         }
        return watchList?: emptyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WatchListAdapter()
        recyclerView.adapter = adapter

        val viewModel = ViewModelProvider(this).get(WatchListViewModel::class.java)

        if (isConnected(requireContext())) {
            viewModel.watchList.observe(viewLifecycleOwner) { items ->
                adapter.submitList(items)
            }
        } else {
            val items = getOfflineWatchList()
            adapter.submitList(items)
        }

    }

    //---------------------------------------------------//

}
