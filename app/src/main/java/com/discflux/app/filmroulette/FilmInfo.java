package com.discflux.app.filmroulette;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phil on 02/05/2016.
 */
public class FilmInfo implements Parcelable{

    private String title;
    private String description;
    private String year;
    private String posterUrl;
    private double rating;

    public FilmInfo(String title, String description, String posterUrl, double rating, String year) {
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getRating() {
        return rating;
    }

    // Allow updating of user ratings
    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getYear() {
        return year;
    }

    @Override
    public String toString() {
        return getTitle() + " (" + getYear() + ")";
    }

    protected FilmInfo(Parcel in) {
        title = in.readString();
        description = in.readString();
        posterUrl = in.readString();
        rating = in.readDouble();
        year = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(posterUrl);
        dest.writeDouble(rating);
        dest.writeString(year);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FilmInfo> CREATOR = new Parcelable.Creator<FilmInfo>() {
        @Override
        public FilmInfo createFromParcel(Parcel in) {
            return new FilmInfo(in);
        }

        @Override
        public FilmInfo[] newArray(int size) {
            return new FilmInfo[size];
        }
    };
}
