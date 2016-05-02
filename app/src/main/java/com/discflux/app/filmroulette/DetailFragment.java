package com.discflux.app.filmroulette;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Phil on 02/05/2016.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private ImageView mBoxArt;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mYearTextView;
    private TextView mRatingTextView;
    private FilmInfo filmDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (getArguments() != null) {
            filmDetails = getArguments().getParcelable(GridFragment.FILM_EXTRA);
            setupView(rootView);
        }
        return rootView;
    }

    private void setupView(View view) {
        mBoxArt = (ImageView) view.findViewById(R.id.detail_boxart_imageview);
        mTitleTextView = (TextView) view.findViewById(R.id.detail_title_textview);
        mDescriptionTextView = (TextView) view.findViewById(R.id.detail_description_textview);
        mYearTextView = (TextView) view.findViewById(R.id.detail_year_textview);
        mRatingTextView = (TextView) view.findViewById(R.id.detail_rating_textview);

        Log.d(LOG_TAG, filmDetails.toString());

        Picasso.with(getActivity()).load(filmDetails.getPosterUrl()).into(mBoxArt);
        mTitleTextView.setText(filmDetails.getTitle());
        mDescriptionTextView.setText(filmDetails.getDescription());
        mYearTextView.setText(filmDetails.getYear());
        Double rating = filmDetails.getRating();
        mRatingTextView.setText(rating.toString() + "/10");

    }
}
