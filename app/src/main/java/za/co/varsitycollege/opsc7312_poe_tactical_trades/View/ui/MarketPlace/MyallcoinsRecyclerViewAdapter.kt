package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset

import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace.placeholder.PlaceholderContent.PlaceholderItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyallcoinsRecyclerViewAdapter(
    private val values: List<CoinAsset>
) : RecyclerView.Adapter<MyallcoinsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.assetId
        holder.nameView.text = item.name
        holder.priceView.text = item.priceUsd.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCoinBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.tvID
        val nameView: TextView = binding.tvName
        val priceView: TextView = binding.tvPrice

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}