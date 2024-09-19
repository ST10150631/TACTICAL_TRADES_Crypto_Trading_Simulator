package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.NewsItem

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val dataList = mutableListOf<NewsItem>()

    fun submitList(items: List<NewsItem>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
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
        holder.informationTextView.text = newsItem.information
        holder.stockIconImageView.setImageResource(newsItem.imageRes)

    }

    override fun getItemCount(): Int = dataList.size

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.TxtTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.TxtDate)
        val informationTextView: TextView = itemView.findViewById(R.id.TxtInformation)
        val stockIconImageView: ImageView = itemView.findViewById(R.id.stockIcon)
    }
}