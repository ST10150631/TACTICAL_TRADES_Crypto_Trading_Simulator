package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem

class WatchListAdapter : RecyclerView.Adapter<WatchListAdapter.ViewHolder>() {

    private val dataList = mutableListOf<StockItem>()

    fun submitList(items: List<StockItem>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_watchlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        Glide.with(holder.itemView.context)
            .load(item.imageRes)
            .placeholder(R.drawable.logoregister)
            .error(R.drawable.logoregister)
            .into(holder.stockIcon)

        holder.stockId.text = item.stockId
        holder.stockName.text = item.name


        holder.currentPrice.text = item.currentPrice

        holder.priceDifference.text = item.priceDifference
        // will navigate to stock
        holder.stockBtn.setOnClickListener {
            val navController = findNavController(holder.itemView)
            val bundle = Bundle()
            bundle.putString("coinData",item.stockId) // Pass your data here
            navController.navigate(za.co.varsitycollege.opsc7312_poe_tactical_trades.R.id.navigation_coinViewFragment, bundle)
        }

        val colorRes = if (item.upDown) R.color.green else R.color.red
        holder.priceDifference.setTextColor(holder.itemView.context.getColor(colorRes))
        holder.stockGraph.setImageResource(if (item.upDown) R.drawable.stock_up_vector else R.drawable.stock_down_vector)
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockIcon: ImageView = itemView.findViewById(R.id.stockIcon)
        val stockId: TextView = itemView.findViewById(R.id.TxtViewID)
        val stockName: TextView = itemView.findViewById(R.id.TxtViewName)
        val currentPrice: TextView = itemView.findViewById(R.id.TxtViewCurrent)
        val priceDifference: TextView = itemView.findViewById(R.id.TxtViewDifference)
        val stockGraph: ImageView = itemView.findViewById(R.id.stockGraph)
        val stockBtn:ImageButton = itemView.findViewById(R.id.ImgBtnStock)
    }
}
