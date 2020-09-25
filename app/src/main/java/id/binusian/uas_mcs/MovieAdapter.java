package id.binusian.uas_mcs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import id.binusian.uas_mcs.activities.DetailActivity;
import id.binusian.uas_mcs.activities.MainActivity;
import id.binusian.uas_mcs.fragments.SavedFragment;
import id.binusian.uas_mcs.objects.Movie;
import id.binusian.uas_mcs.sqlite.MovieHelper;
import id.binusian.uas_mcs.sqlite.MovieContract.MovieTable;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROGRESS = 0;

    private Context mContext;
//    private List<Movie> movies = new ArrayList<>();
    private ArrayList<Movie> movies;

    private MovieHelper movieHelper;
    private SQLiteDatabase db;

    private OnItemClickListener listener;
    private OnLongItemClickListener longListener;

    public MovieAdapter(Context context) {
        this.mContext = context;
    }

    public MovieAdapter(Context mContext, ArrayList<Movie> movies) {
        this.mContext = mContext;
        this.movies = movies;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view;

        if (viewType == VIEW_ITEM) {
            view = inflater.inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.view_progress, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder hol, int position) {

        if (hol instanceof MovieViewHolder) {

            movieHelper = new MovieHelper(mContext);
            db = movieHelper.getWritableDatabase();

            final MovieViewHolder holder = (MovieViewHolder) hol;

            final Movie movie = movies.get(position);

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
            circularProgressDrawable.setStrokeWidth(10);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            Glide.with(mContext)
                    .load(movie.getPoster())
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.notavail)
                    .into(holder.poster);

            holder.title.setText(movie.getTitle());

//            holder.card.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {

//                    PopupMenu popupMenu = new PopupMenu(mContext, v);
//                    MenuInflater menuInflater = popupMenu.getMenuInflater();
//
//                    final String selection = MovieTable.COLUMN_IMDB_ID +  " = ?";
//                    final String[] selectionArgs = { movie.getImbdId() };
//
//                    Cursor cursor = db.query(
//                            MovieTable.TABLE_NAME,
//                            null,
//                            selection,
//                            selectionArgs,
//                            null,
//                            null,
//                            null
//                    );
//
//                    if (cursor.getCount() != 0) {
//                        menuInflater.inflate(R.menu.menu_delete, popupMenu.getMenu());
//                    } else {
//                        menuInflater.inflate(R.menu.menu_save, popupMenu.getMenu());
//                    }
//
//                    popupMenu.show();
//
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.menu_save:
//                                    // Add to sqlite
//                                    ContentValues cv = new ContentValues();
//                                    cv.put(MovieTable.COLUMN_IMDB_ID, movie.getImbdId());
//                                    cv.put(MovieTable.COLUMN_TITLE, movie.getTitle());
//                                    cv.put(MovieTable.COLUMN_POSTER, movie.getPoster());
//                                    cv.put(MovieTable.COLUMN_YEAR, movie.getYear());
//
//                                    db.insert(MovieTable.TABLE_NAME, null, cv);
////                                    SavedFragment.movies.add(new Movie(movie.getTitle(), movie.getPoster(), movie.getYear(), movie.getImbdId()));
////                                    SavedFragment.movieAdapter.notifyItemInserted(SavedFragment.movies.size()-1);
//
////                                    Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
//                                    return true;
//                                case R.id.menu_delete:
//                                    // Remove from sqlite
//                                    db.delete(MovieTable.TABLE_NAME, selection, selectionArgs);
//
////                                    int i = 0;
////                                    for (i = 0; i < SavedFragment.movies.size(); i++) {
////                                        if (SavedFragment.movies.get(i).getImbdId().equals(movie.getImbdId())) {
////                                            SavedFragment.movies.remove(i);
////                                            break;
////                                        }
////
////                                    }
//
////                                    Toast.makeText(mContext, "Removed", Toast.LENGTH_SHORT).show();
////                                    SavedFragment.movieAdapter.notifyItemRemoved(i);
//                                    return true;
//                            }
//                            return false;
//                        }
//                    });
//
//                    return true;
//                }
//            });

//            holder.card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, DetailActivity.class);
//                    intent.putExtra("Title", movie.getTitle());
//                    intent.putExtra("Poster", movie.getPoster());
//                    intent.putExtra("Year", movie.getYear());
//                    intent.putExtra("ImdbId", movie.getImbdId());
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.poster, "card_transition");
//                    mContext.startActivity(intent, options.toBundle());
//                }
//            });
        } else {
            ProgressViewHolder holder = (ProgressViewHolder) hol;

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
            circularProgressDrawable.setStrokeWidth(10);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            holder.bar.setProgressDrawable(circularProgressDrawable);

            holder.bar.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemViewType(int position) {
        return movies.get(position) == null ? VIEW_PROGRESS : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void addItem(Movie movie) {
        int lastPos = movies.size() - 1;
        movies.add(movie);
//        notifyItemRangeInserted(lastPos, movies.size());
        notifyItemChanged(movies.size());
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView card;
        ImageView poster;
        TextView title;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.movie_card);
            poster = itemView.findViewById(R.id.movie_poster);
            title = itemView.findViewById(R.id.movie_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(movies.get(position), poster);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAdapterPosition();
                    if (longListener != null && position != RecyclerView.NO_POSITION) {
                        longListener.onLongItemClick(movies.get(position), position, v);
                    }

                    return true;
                }
            });

        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar bar;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            bar = itemView.findViewById(R.id.view_progress_bar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie, ImageView poster);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(Movie movie, int position, View view);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longListener) {
        this.longListener = longListener;
    }

}
