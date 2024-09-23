package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat

class DrawableUtils
{
    object DrawableUtils {
        fun createWalletBackground(
            context: Context,
            backgroundColor: Int,
            borderColor: Int,
            borderWidth: Int,
            cornerRadius: Float
        ): GradientDrawable {
            return GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(ContextCompat.getColor(context, backgroundColor))
                setStroke(borderWidth, ContextCompat.getColor(context, borderColor))
                this.cornerRadius = cornerRadius
            }
        }
    }
}