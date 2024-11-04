package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.NewsAPIHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import kotlin.concurrent.thread

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var settingsButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle(getString(R.string.news))
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        /* Fetch articles in a separate thread
        thread {
            val newsList = try {
                NewsAPIHelper().getArticles()
            } catch (e: Exception) {
                // Handle exception and return an empty list
                e.printStackTrace()
                emptyList()
            }

            // Update UI on the main thread
            activity?.runOnUiThread {
                adapter = NewsAdapter(newsList)
                recyclerView.adapter = adapter // Set the adapter to RecyclerView
            }
        }*/
        return view
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
