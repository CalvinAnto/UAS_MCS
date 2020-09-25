package id.binusian.uas_mcs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import id.binusian.uas_mcs.objects.Movie;

@Dao
public interface MovieDao {

    @Insert
    void insert(Movie movie);

    @Update
    void update(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("SELECT * FROM movie_table")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM  movie_table WHERE imbdId = :imdbId")
    List<Movie> getMoviesById(String imdbId);
}
