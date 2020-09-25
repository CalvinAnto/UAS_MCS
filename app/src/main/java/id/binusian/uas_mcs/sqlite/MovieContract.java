package id.binusian.uas_mcs.sqlite;

import android.provider.BaseColumns;

public class MovieContract {

    private MovieContract() {}

    public static class MovieTable implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_IMDB_ID = "imbd_id";
    }

}
