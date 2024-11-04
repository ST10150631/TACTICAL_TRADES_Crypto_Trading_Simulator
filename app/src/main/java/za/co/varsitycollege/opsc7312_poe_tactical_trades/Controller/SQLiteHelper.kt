package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.User
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TacticalTrades.db"
        private const val DATABASE_VERSION = 9


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
        db.execSQL("PRAGMA foreign_keys = ON;")

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

        val createWalletsTable = """
    CREATE TABLE $TABLE_WALLETS (
        $COLUMN_WALLET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_WALLET_USER_ID TEXT NOT NULL,
        $COLUMN_WALLET_TYPE TEXT NOT NULL,
        $COLUMN_WALLET_AMOUNT TEXT NOT NULL,  -- Keeping it as TEXT based on your model
        $COLUMN_WALLET_COLOR INTEGER,
        $COLUMN_WALLET_PERCENTAGE TEXT,       -- Keeping it as TEXT since percentage in the model is String?
        $COLUMN_WALLET_GRADIENT INTEGER,
        $COLUMN_WALLET_IMAGE INTEGER,
        FOREIGN KEY($COLUMN_WALLET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
    )
""".trimIndent()
        db.execSQL(createWalletsTable)

        val createWatchlistTable = """
    CREATE TABLE $TABLE_WATCHLIST (
        $COLUMN_WATCHLIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_WATCHLIST_USER_ID TEXT,
        $COLUMN_STOCK_ID TEXT,
        $COLUMN_STOCK_NAME TEXT,
        $COLUMN_STOCK_IMAGE TEXT,
        $COLUMN_CURRENT_PRICE TEXT,
        $COLUMN_PRICE_DIFFERENCE TEXT,
        $COLUMN_UP_DOWN INTEGER, -- This will be changed to INTEGER to represent a Boolean value
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

    // CRUD operations for Users
    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, user.userId)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_NAME, user.name)
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_TOTAL_BALANCE, user.totalBalance)
            put(COLUMN_NOTIFICATIONS_ENABLED, user.notificationsEnabled)
            put(COLUMN_PROFILE_PICTURE_URL, user.profilePictureUrl)
            put(COLUMN_GRAPH_THEME, user.graphTheme)
            put(COLUMN_LANGUAGE, user.language)
        }
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        var user: User? = null
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            user = User(
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                totalBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_BALANCE)),
                notificationsEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_ENABLED)) == 1,
                profilePictureUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE_URL)),
                graphTheme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_THEME)),
                language = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_NAME, user.name)
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_TOTAL_BALANCE, user.totalBalance)
            put(COLUMN_NOTIFICATIONS_ENABLED, if (user.notificationsEnabled == true) 1 else 0)
            put(COLUMN_PROFILE_PICTURE_URL, user.profilePictureUrl)
            put(COLUMN_GRAPH_THEME, user.graphTheme)
            put(COLUMN_LANGUAGE, user.language)
        }

        // Updating row and returning the number of rows affected
        val rowsUpdated = db.update(
            TABLE_USERS,
            values,
            "$COLUMN_USER_ID = ?",
            arrayOf(user.userId)
        )
        db.close()
        return rowsUpdated
    }



    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    totalBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_BALANCE)),
                    notificationsEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_ENABLED)) == 1,
                    profilePictureUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE_URL)),
                    graphTheme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRAPH_THEME)),
                    language = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userList
    }


    // CRUD operations for Wallets
    fun addWallet(wallet: WalletModel, userId: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET_USER_ID, userId) // Storing userId in the wallet entry
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

    fun getWalletsByUserId(userId: String): List<WalletModel> {
        val walletList = mutableListOf<WalletModel>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_WALLETS,
            null,
            "$COLUMN_WALLET_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val wallet = WalletModel(
                    walletType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WALLET_TYPE)),
                    amountInCoin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WALLET_AMOUNT)),
                    color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WALLET_COLOR)),
                    percentage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WALLET_PERCENTAGE)),
                    walletGradient = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WALLET_GRADIENT)),
                    walletImage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WALLET_IMAGE))
                )
                walletList.add(wallet)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return walletList
    }

    fun updateWallet(wallet: WalletModel, walletId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET_USER_ID, wallet.walletType) // Assuming you want to keep userId
            put(COLUMN_WALLET_TYPE, wallet.walletType)
            put(COLUMN_WALLET_AMOUNT, wallet.amountInCoin)
            put(COLUMN_WALLET_COLOR, wallet.color)
            put(COLUMN_WALLET_PERCENTAGE, wallet.percentage)
            put(COLUMN_WALLET_GRADIENT, wallet.walletGradient)
            put(COLUMN_WALLET_IMAGE, wallet.walletImage)
        }
        db.update(TABLE_WALLETS, values, "$COLUMN_WALLET_ID = ?", arrayOf(walletId.toString()))
        db.close()
    }

    fun deleteWallet(walletId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_WALLETS, "$COLUMN_WALLET_ID = ?", arrayOf(walletId.toString()))
        db.close()
    }


    fun addWatchlistItem(stockItem: StockItem, userId: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WATCHLIST_USER_ID, userId)
            put(COLUMN_STOCK_ID, stockItem.stockId)
            put(COLUMN_STOCK_NAME, stockItem.name)
            put(COLUMN_STOCK_IMAGE, stockItem.imageRes)
            put(COLUMN_CURRENT_PRICE, stockItem.currentPrice)
            put(COLUMN_PRICE_DIFFERENCE, stockItem.priceDifference)
            put(COLUMN_UP_DOWN, if (stockItem.upDown) 1 else 0) // Store as 1 for true and 0 for false
        }
        db.insert(TABLE_WATCHLIST, null, values)
        db.close()
    }


    fun getWatchlistItemsByUserId(userId: String): List<StockItem> {
        val watchlistItems = mutableListOf<StockItem>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_WATCHLIST,
            null,
            "$COLUMN_WATCHLIST_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val stockItem = StockItem(
                    stockId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOCK_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOCK_NAME)),
                    imageRes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOCK_IMAGE)),
                    upDown = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UP_DOWN)) == 1, // Convert 1 or 0 to Boolean
                    currentPrice = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE)),
                    priceDifference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE_DIFFERENCE))
                )
                watchlistItems.add(stockItem)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return watchlistItems
    }

}

