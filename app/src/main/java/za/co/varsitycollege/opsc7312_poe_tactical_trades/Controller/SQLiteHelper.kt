package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TacticalTrades.db"
        private const val DATABASE_VERSION = 5


        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NAME = "name"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_TOTAL_BALANCE = "total_balance"
        const val COLUMN_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val COLUMN_PROFILE_PICTURE_URL = "profile_picture_url"
        const val COLUMN_GRAPH_THEME = "graph_theme"
        const val COLUMN_LANGUAGE = "language"

        const val TABLE_WALLETS = "wallets"
        const val COLUMN_WALLET_ID = "wallet_id"
        const val COLUMN_WALLET_USER_ID = "user_id"
        const val COLUMN_WALLET_TYPE = "wallet_type"
        const val COLUMN_WALLET_AMOUNT = "amount_in_coin"
        const val COLUMN_WALLET_COLOR = "color"
        const val COLUMN_WALLET_PERCENTAGE = "percentage"
        const val COLUMN_WALLET_GRADIENT = "wallet_gradient"
        const val COLUMN_WALLET_IMAGE = "wallet_image"


        const val TABLE_WATCHLIST = "watchlist"
        const val COLUMN_WATCHLIST_ID = "watchlist_id"
        const val COLUMN_WATCHLIST_USER_ID = "user_id"
        const val COLUMN_STOCK_ID = "stock_id"
        const val COLUMN_STOCK_NAME = "name"
        const val COLUMN_STOCK_IMAGE = "image_res"
        const val COLUMN_CURRENT_PRICE = "current_price"
        const val COLUMN_PRICE_DIFFERENCE = "price_difference"
        const val COLUMN_UP_DOWN = "up_down"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Create(db)
    }

    fun Create(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID TEXT PRIMARY KEY,
                $COLUMN_EMAIL TEXT,
                $COLUMN_NAME TEXT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_TOTAL_BALANCE REAL,
                $COLUMN_NOTIFICATIONS_ENABLED INTEGER,
                $COLUMN_PROFILE_PICTURE_URL TEXT,
                $COLUMN_GRAPH_THEME TEXT,
                $COLUMN_LANGUAGE TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // Create Wallets Table

        val createWalletsTable = """
        CREATE TABLE $TABLE_WALLETS (
            $COLUMN_WALLET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WALLET_USER_ID TEXT,
            $COLUMN_WALLET_TYPE TEXT,
            $COLUMN_WALLET_AMOUNT TEXT,
            $COLUMN_WALLET_COLOR INTEGER,
            $COLUMN_WALLET_PERCENTAGE TEXT,
            $COLUMN_WALLET_GRADIENT INTEGER,
            $COLUMN_WALLET_IMAGE INTEGER,
            FOREIGN KEY($COLUMN_WALLET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
        )
    """.trimIndent()
        db.execSQL(createWalletsTable)

        // Create Watchlist Table
        val createWatchlistTable = """
        CREATE TABLE $TABLE_WATCHLIST (
            $COLUMN_WATCHLIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WATCHLIST_USER_ID TEXT,
            $COLUMN_STOCK_ID TEXT,
            $COLUMN_STOCK_NAME TEXT,
            $COLUMN_STOCK_IMAGE TEXT,
            $COLUMN_CURRENT_PRICE TEXT,
            $COLUMN_PRICE_DIFFERENCE TEXT,
            $COLUMN_UP_DOWN INTEGER,
            FOREIGN KEY($COLUMN_WATCHLIST_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
        )
    """.trimIndent()
        db.execSQL(createWatchlistTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WATCHLIST")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WALLETS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(
        userId: String,
        email: String,
        name: String,
        username: String,
        totalBalance: Double,
        notificationsEnabled: Boolean,
        profilePictureUrl: String,
        graphTheme: String,
        language: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_EMAIL, email)
            put(COLUMN_NAME, name)
            put(COLUMN_USERNAME, username)
            put(COLUMN_TOTAL_BALANCE, totalBalance)
            put(COLUMN_NOTIFICATIONS_ENABLED, if (notificationsEnabled) 1 else 0)
            put(COLUMN_PROFILE_PICTURE_URL, profilePictureUrl)
            put(COLUMN_GRAPH_THEME, graphTheme)
            put(COLUMN_LANGUAGE, language)
        }
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun clearUsers() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_USERS")
            Log.d("SQLiteHelper", "Users table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing users: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun addWallet(wallet: WalletModel, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET_USER_ID, userId)
            put(COLUMN_WALLET_TYPE, wallet.walletType)
            put(COLUMN_WALLET_AMOUNT, wallet.amountInCoin)
            put(COLUMN_WALLET_COLOR, wallet.color)
            put(COLUMN_WALLET_PERCENTAGE, wallet.percentage)
            put(COLUMN_WALLET_GRADIENT, wallet.walletGradient)
            put(COLUMN_WALLET_IMAGE, wallet.walletImage)
        }
        db.insert(TABLE_WALLETS, null, values)
        db.close()
    }

    fun clearWallets() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_WALLETS")
            Log.d("SQLiteHelper", "Wallets table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing wallets: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun addWatchlistItem(stockItem: StockItem, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WATCHLIST_USER_ID, userId)
            put(COLUMN_STOCK_ID, stockItem.stockId)
            put(COLUMN_STOCK_NAME, stockItem.name)
            put(COLUMN_STOCK_IMAGE, stockItem.imageRes)
            put(COLUMN_CURRENT_PRICE, stockItem.currentPrice)
            put(COLUMN_PRICE_DIFFERENCE, stockItem.priceDifference)
            put(COLUMN_UP_DOWN, if (stockItem.upDown) 1 else 0)
        }
        db.insert(TABLE_WATCHLIST, null, values)
        db.close()
    }

    fun clearWatchlist() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_WATCHLIST")
            Log.d("SQLiteHelper", "Watchlist table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing watchlist: ${e.message}")
        } finally {
            db.close()
        }
    }
    }
