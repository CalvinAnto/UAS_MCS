package id.binusian.uas_mcs;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import id.binusian.uas_mcs.objects.Movie;

@Database(entities = Movie.class, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase instance;

    public abstract MovieDao movieDao();

    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) instance = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, "movie_database")
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateAsyncTask(instance).execute();
        }
    };

    private static class PopulateAsyncTask extends AsyncTask<Void, Void, Void> {

        MovieDao movieDao;

        private PopulateAsyncTask(MovieDatabase db) {
            movieDao = db.movieDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
