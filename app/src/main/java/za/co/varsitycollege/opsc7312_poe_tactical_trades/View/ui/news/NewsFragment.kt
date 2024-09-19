package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.NewsItem

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyNews = listOf(
            NewsItem("TESLA CEO ELON MUSK ARRESTED FOR TAX FRAUD", "Today", "Tesla CEO Elon Musk arrested on allegations of tax fraud and fraudulent transactions...read more", 2131230840),
            NewsItem("Mark Zuckerberg announces plans to use Ethereum as Meta Currency", "Yesterday", "Mark Zuckerberg announces plans to use Ethereum as Meta Currency...read more", 2131230840),
        )

        adapter = NewsAdapter()
        adapter.submitList(dummyNews)
        recyclerView.adapter = adapter

        return view
    }
}
