package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.coinview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoinViewTestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is coin view test Fragment"
    }
    val text: LiveData<String> = _text
}
