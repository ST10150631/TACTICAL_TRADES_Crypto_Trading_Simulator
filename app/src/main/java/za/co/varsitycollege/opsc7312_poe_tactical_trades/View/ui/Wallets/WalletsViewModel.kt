package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Wallets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel

class WalletsViewModel : ViewModel() {
    val wallets = MutableLiveData<MutableList<WalletModel>>(mutableListOf())
}
//------------------------------END OF FILE-------------------//