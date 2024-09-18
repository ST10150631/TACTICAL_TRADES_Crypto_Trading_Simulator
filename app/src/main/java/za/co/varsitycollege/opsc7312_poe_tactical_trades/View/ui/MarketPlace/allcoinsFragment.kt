package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.MarketPlace

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.CoinAPIHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinsAssets
import kotlin.concurrent.thread

/**
 * A fragment representing a list of Items.
 */
class allcoinsFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_coins_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                thread {
                    val CoinList = try {
                        CoinAPIHelper().top25FromAPI()
                    } catch (e: Exception) {
                        return@thread
                    }
                    if (CoinList.isNotEmpty()) {
                        activity?.runOnUiThread {
                            adapter =
                                MyallcoinsRecyclerViewAdapter(CoinList)
                        }
                    }


                }
                /*
                returns all assets too many assets to display
                thread {
                    val coinJSON = try {
                        CoinAPIHelper().buildURLforCoin("fd0612a2-3ef6-48aa-824a-1c025b0e12e9")
                            ?.readText()
                    } catch (e: Exception) {
                        return@thread
                    }
                    if (coinJSON != null) {
                        val gson = Gson()
                        val coinData: List<CoinAsset> = gson.fromJson(coinJSON, object : TypeToken<List<CoinAsset>>() {}.type)

                        activity?.runOnUiThread {
                            adapter =
                                MyallcoinsRecyclerViewAdapter(coinData)
                        }
                    }


                }
                 */

            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            allcoinsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}