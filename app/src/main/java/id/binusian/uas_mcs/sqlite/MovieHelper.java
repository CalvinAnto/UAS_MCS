package id.binusian.uas_mcs.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import id.binusian.uas_mcs.sqlite.MovieContract.*;

public class MovieHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Uas.db";

    public MovieHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
            "CREATE TABLE " + MovieTable.TABLE_NAME + " (" +
                MovieTable.COLUMN_IMDB_ID + " TEXT PRIMARY KEY," +
                MovieTable.COLUMN_TITLE + " TEXT," +
                MovieTable.COLUMN_POSTER + " TEXT," +
                MovieTable.COLUMN_YEAR + " TEXT)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + MovieTable.TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
}
