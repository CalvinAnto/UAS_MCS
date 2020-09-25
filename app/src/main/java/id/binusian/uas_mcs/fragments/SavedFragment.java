package id.binusian.uas_mcs.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.binusian.uas_mcs.GridSpacingItemDecoration;
import id.binusian.uas_mcs.MovieAdapter;
import id.binusian.uas_mcs.MovieViewModel;
import id.binusian.uas_mcs.R;
import id.binusian.uas_mcs.activities.DetailActivity;
import id.binusian.uas_mcs.objects.Movie;
import id.binusian.uas_mcs.sqlite.MovieContract;
import id.binusian.uas_mcs.sqlite.MovieHelper;
import id.binusian.uas_mcs.sqlite.MovieContract.MovieTable;

public class SavedFragment extends Fragment {

    private MovieViewModel movieViewModel;

    ArrayList<Movie> movies;

    MovieAdapter movieAdapter;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;

    MovieHelper movieHelper;
    SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        movieViewModel = new ViewModelProvider(getActivity()).get(MovieViewModel.class);
//        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
//            @Override
//            public void onChanged(List<Movie> movies) {
//                movieAdapter.setMovies(movies);
//            }
//        });

        movieHelper = new MovieHelper(getContext());
        db = movieHelper.getWritableDatabase();

        recyclerView = view.findViewById(R.id.saved_recycler);

        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), movies);

        movieAdapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie, ImageView poster) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("Title", movie.getTitle());
                intent.putExtra("Poster", movie.getPoster());
                intent.putExtra("Year", movie.getYear());
                intent.putExtra("ImdbId", movie.getImbdId());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), poster, "card_transition");
                startActivity(intent, options.toBundle());
            }
        });

        movieAdapter.setOnLongItemClickListener(new MovieAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(final Movie movie, final int position, View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                final String selection = MovieTable.COLUMN_IMDB_ID +  " = ?";
                final String[] selectionArgs = { movie.getImbdId() };

                Cursor cursor = db.query(
                        MovieTable.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                if (cursor.getCount() != 0) {
                    menuInflater.inflate(R.menu.menu_delete, popupMenu.getMenu());
                } else {
                    menuInflater.inflate(R.menu.menu_save, popupMenu.getMenu());
                }

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_save:
                                // Add to sqlite
                                ContentValues cv = new ContentValues();
                                cv.put(MovieTable.COLUMN_IMDB_ID, movie.getImbdId());
                                cv.put(MovieTable.COLUMN_TITLE, movie.getTitle());
                                cv.put(MovieTable.COLUMN_POSTER, movie.getPoster());
                                cv.put(MovieTable.COLUMN_YEAR, movie.getYear());

                                db.insert(MovieTable.TABLE_NAME, null, cv);
                                movies.add(movie);
                                movieAdapter.notifyItemInserted(movies.size()-1);
                                return true;
                            case R.id.menu_delete:
                                db.delete(MovieTable.TABLE_NAME, selection, selectionArgs);
                                movies.remove(position);
                                movieAdapter.notifyItemRemoved(position);
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

        layoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(30, 2));

//        getData();
    }

    public void getData() {
        Cursor cursor = db.query(
          MovieTable.TABLE_NAME,
          null,
          null,
          null,
          null,
          null,
          null
        );

        movies.clear();
        movieAdapter.notifyDataSetChanged();

        while(cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieTable.COLUMN_TITLE));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(MovieTable.COLUMN_POSTER));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(MovieTable.COLUMN_YEAR));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(MovieTable.COLUMN_IMDB_ID));

            movies.add(new Movie(title, poster, year, id));
            movieAdapter.notifyItemInserted(movies.size()-1);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}