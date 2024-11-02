package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem

class LeaderboardAdapter(private val users: List<Map<String, Any>>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user["username"] as String

        val difference = user["difference"] as? Double ?: 0.0
        holder.amountMade.text = "$%.2f".format(difference)

        val url = user["profilePictureUrl"] as? String
        if (!url.isNullOrEmpty()) {
            Glide.with(holder.profile.context).load(url).apply(
                RequestOptions()
                    .placeholder(R.drawable.profile_image)
                    .error(R.drawable.profile_image)
            ).into(holder.profile)
        } else {
            holder.profile.setImageResource(R.drawable.profile_image)
        }
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.TxtViewUsername)
        val amountMade: TextView = itemView.findViewById(R.id.TxtViewHowMuchTheyHaveMade)
        val profile: ImageView = itemView.findViewById(R.id.ivProfile)
    }
}
