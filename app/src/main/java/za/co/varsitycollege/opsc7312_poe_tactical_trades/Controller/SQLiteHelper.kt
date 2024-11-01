package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TacticalTrades.db"
        private const val DATABASE_VERSION = 4

        // Users Table
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(userId: String, email: String, name: String, username: String, totalBalance: Double, notificationsEnabled: Boolean, profilePictureUrl: String, graphTheme: String, language: String) {
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
}
