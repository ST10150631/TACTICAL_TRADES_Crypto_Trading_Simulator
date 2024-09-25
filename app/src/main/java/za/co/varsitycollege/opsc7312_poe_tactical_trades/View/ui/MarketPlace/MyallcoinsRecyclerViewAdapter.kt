package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace.placeholder.PlaceholderContent.PlaceholderItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentCoinBinding
import java.text.DecimalFormat


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
        holder.ivLogo.setImageResource(item.logo)
        holder.idView.text = item.assetId
        holder.nameView.text = item.name
        val price = DecimalFormat("#,###.00").format(item.priceUsd)
        holder.priceView.text = price.toString()
        holder.btnViewCoin.setOnClickListener {
            val navController = findNavController(holder.itemView)
            val bundle = Bundle()
            bundle.putString("coinData",item.assetId) // Pass your data here
            navController.navigate(za.co.varsitycollege.opsc7312_poe_tactical_trades.R.id.navigation_coinViewFragment, bundle)
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCoinBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivLogo = binding.ivLogo
        val idView: TextView = binding.tvID
        val nameView: TextView = binding.tvName
        val priceView: TextView = binding.tvPrice
        val  btnViewCoin: ImageButton = binding.btnViewCoin


        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}