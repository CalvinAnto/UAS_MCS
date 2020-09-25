package id.binusian.uas_mcs.objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_table")
public class Movie {

    @NonNull
    @PrimaryKey
    private String imbdId;

    private String title;
    private String poster;
    private String year;

    public Movie(String title, String poster, String year, String imbdId) {
        this.title = title;
        this.poster = poster;
        this.year = year;
        this.imbdId = imbdId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImbdId() {
        return imbdId;
    }

    public void setImbdId(String imbdId) {
        this.imbdId = imbdId;
    }
}
