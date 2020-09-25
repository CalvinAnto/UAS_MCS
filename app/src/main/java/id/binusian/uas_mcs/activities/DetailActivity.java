package id.binusian.uas_mcs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import id.binusian.uas_mcs.MovieViewModel;
import id.binusian.uas_mcs.R;
import id.binusian.uas_mcs.fragments.SavedFragment;
import id.binusian.uas_mcs.objects.Movie;
import id.binusian.uas_mcs.sqlite.MovieContract;
import id.binusian.uas_mcs.sqlite.MovieHelper;

public class DetailActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    ArrayList<Movie> movies;
    Movie movie;

    MovieViewModel movieViewModel;

    MovieHelper helper;
    SQLiteDatabase db;

    ImageView poster;
    TextView title, year, id;
    Button button;

    Boolean saved;

    String sYear, sTitle, sPoster, sImdbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        movieViewModel.getAllMovies().observe(DetailActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> moviesList) {
                movies = new ArrayList<>(moviesList);
                for (Movie m : movies) {
                    if (m.getImbdId().equals(sImdbId)) {
                        movie = m;
                        break;
                    }
                }
            }
        });

        helper = new MovieHelper(this);
        db = helper.getWritableDatabase();

        toolbar = findViewById(R.id.detail_tool_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button = findViewById(R.id.detail_button);

        poster = findViewById(R.id.detail_poster);
        title = findViewById(R.id.detail_title);
        year = findViewById(R.id.detail_year);
        id = findViewById(R.id.detail_id);

        final Intent intent = getIntent();

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        sPoster = intent.getStringExtra("Poster");
        sYear = intent.getStringExtra("Year");
        sImdbId = intent.getStringExtra("ImdbId");
        sTitle = intent.getStringExtra("Title");

        Glide.with(this)
                .load(sPoster)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.notavail)
                .into(poster);

        title.setText(sTitle);
        year.setText(sYear);
        id.setText(sImdbId);

//        for (Movie m : movies) {
//            if (m.getImbdId().equals(sImdbId)) {
//                movie = m;
//                break;
//            }
//        }

        String selection = MovieContract.MovieTable.COLUMN_IMDB_ID +  " = ?";
        String[] selectionArgs = { sImdbId };

        Cursor cursor = db.query(
                MovieContract.MovieTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.getCount() != 0)
            saved = true;
        else
            saved = false;

        if (saved) {
            button.setText("Delete");
        } else {
            button.setText("Save");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved) {
                    String selection = MovieContract.MovieTable.COLUMN_IMDB_ID +  " = ?";
                    String[] selectionArgs = { sImdbId };
                    db.delete(MovieContract.MovieTable.TABLE_NAME, selection, selectionArgs);

//                    movieViewModel.delete(movie);

                    button.setText("Save");
                    saved = false;
                    movie = null;
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieTable.COLUMN_IMDB_ID, sImdbId);
                    cv.put(MovieContract.MovieTable.COLUMN_TITLE, sTitle);
                    cv.put(MovieContract.MovieTable.COLUMN_POSTER, sPoster);
                    cv.put(MovieContract.MovieTable.COLUMN_YEAR, sYear);

                    db.insert(MovieContract.MovieTable.TABLE_NAME, null, cv);
//                    movie = new Movie(sTitle, sPoster, sYear, sImdbId);
//                    movieViewModel.insert(movie);

                    button.setText("Delete");
                    saved = true;
                }
            }
        });

    }
}