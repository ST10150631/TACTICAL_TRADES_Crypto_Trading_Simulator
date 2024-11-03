package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R

const val notificationID = 1
const val channelID = "TacticalTrades"
const val messageExtra = "message"
class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: "No message"

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.logoregister)
            .setContentTitle("Tactical Trades")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationID, notification)
    }
}