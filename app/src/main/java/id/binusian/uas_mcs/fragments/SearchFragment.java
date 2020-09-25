package id.binusian.uas_mcs.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.binusian.uas_mcs.GridSpacingItemDecoration;
import id.binusian.uas_mcs.MovieAdapter;
import id.binusian.uas_mcs.R;
import id.binusian.uas_mcs.activities.DetailActivity;
import id.binusian.uas_mcs.objects.Movie;
import id.binusian.uas_mcs.sqlite.MovieContract;
import id.binusian.uas_mcs.sqlite.MovieHelper;

public class SearchFragment extends Fragment {

    private String url = "https://www.omdbapi.com/?apikey=403166c0&s=";
    private int page = 1;
    private String search;
    private boolean loading;

    private ArrayList<Movie> movies;

    RecyclerView mRecyclerView;
    MovieAdapter movieAdapter;
    GridLayoutManager layoutManager;
    ProgressBar progressBar;

    TextInputEditText textInputEditText;
    RequestQueue requestQueue;
    LinearLayout container;
    SwipeRefreshLayout refreshLayout;

    MovieHelper helper;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new MovieHelper(getContext());
        db = helper.getWritableDatabase();

        movies = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(getContext());

        container = view.findViewById(R.id.search_container);

        mRecyclerView = view.findViewById(R.id.search_recycler);
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

                final String selection = MovieContract.MovieTable.COLUMN_IMDB_ID +  " = ?";
                final String[] selectionArgs = { movie.getImbdId() };

                Cursor cursor = db.query(
                        MovieContract.MovieTable.TABLE_NAME,
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
                                ContentValues cv = new ContentValues();
                                cv.put(MovieContract.MovieTable.COLUMN_IMDB_ID, movie.getImbdId());
                                cv.put(MovieContract.MovieTable.COLUMN_TITLE, movie.getTitle());
                                cv.put(MovieContract.MovieTable.COLUMN_POSTER, movie.getPoster());
                                cv.put(MovieContract.MovieTable.COLUMN_YEAR, movie.getYear());

                                db.insert(MovieContract.MovieTable.TABLE_NAME, null, cv);
                                return true;
                            case R.id.menu_delete:
                                db.delete(MovieContract.MovieTable.TABLE_NAME, selection, selectionArgs);
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

        layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (movieAdapter.getItemViewType(position)) {
                    case MovieAdapter.VIEW_ITEM:
                        return 1;
                    case MovieAdapter.VIEW_PROGRESS:
                        return 2;
                    default:
                        return 0;
                }
            }
        });

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(movieAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(30, 2));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!loading && layoutManager.findLastVisibleItemPosition() > layoutManager.getItemCount() - 4) {
                    loading = true;
                    movies.add(null);
                    movieAdapter.notifyItemInserted(movies.size()-1);
                    moreData();
                }
            }
        });

        textInputEditText = view.findViewById(R.id.search_search_edit_text);
        textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search = textInputEditText.getText().toString();

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputEditText.getWindowToken(), 0);

                    requestData();
                    return true;
                }
                return false;
            }
        });

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getContext());
        circularProgressDrawable.setStrokeWidth(10);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        progressBar = view.findViewById(R.id.search_progress);
        progressBar.setProgressDrawable(circularProgressDrawable);

        refreshLayout = view.findViewById(R.id.search_swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

    }

    private void requestData() {

        if (movies.size() > 0) mRecyclerView.scrollToPosition(0);

        container.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.GONE);
        page = 1;
        loading = true;

        movies.clear();
        movieAdapter.notifyDataSetChanged();

        JsonObjectRequest request = new JsonObjectRequest(url + search + "&page=" + page, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading = false;
                refreshLayout.setRefreshing(false);
                container.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                try {
                    String resp = response.getString("Response");
                    if (resp.equals("True")) {
                        insertData(response.getJSONArray("Search"));
                    } else {
                        loading = true;
                        String error = response.getString("Error");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+error, Toast.LENGTH_SHORT).show();
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    void insertData(JSONArray response) throws Exception{
        for (int i = 0; i < response.length(); i++) {
            JSONObject object = response.getJSONObject(i);
            String title = object.getString("Title");
            String poster = object.getString("Poster");
            String imbdId = object.getString("imdbID");
            String year = object.getString("Year");

            movies.add(new Movie(title, poster, year, imbdId));
        }

        if (movies.size() < 10) loading = true;

        movieAdapter.notifyDataSetChanged();
    }

    void insertMore(JSONArray response) throws Exception {
        if (response.length() < 10) loading = true;
        else loading = false;
        for (int i = 0; i < response.length(); i++) {
            JSONObject object = response.getJSONObject(i);
            String title = object.getString("Title");
            String poster = object.getString("Poster");
            String imbdId = object.getString("imdbID");
            String year = object.getString("Year");

            movieAdapter.addItem(new Movie(title, poster, year, imbdId));
        }
    }

    void moreData() {
        page++;

        JsonObjectRequest request = new JsonObjectRequest(url + search + "&page=" + page, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                movies.remove(movies.size()-1);
                movieAdapter.notifyItemRemoved(movies.size());
                try {
                    String resp = response.getString("Response");
                    if (resp.equals("True")) {
                        insertMore(response.getJSONArray("Search"));
                    } else {
                        String error = response.getString("Error");
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        request.setShouldCache(false);
        requestQueue.add(request);

    }
}
