package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.Article
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

class NewsAdapter(private var dataList: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // Function to update the list
    fun submitList(items: List<Article>) {
        dataList = items // Update the list
        notifyDataSetChanged() // Notify the adapter of the change
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = dataList[position]

        holder.titleTextView.text = newsItem.title
        holder.dateTextView.text = newsItem.date
        holder.informationTextView.text = newsItem.body
    }

    override fun getItemCount(): Int = dataList.size

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.TxtTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.TxtDate)
        val informationTextView: TextView = itemView.findViewById(R.id.TxtInformation)
    }
}
