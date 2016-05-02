package com.discflux.app.filmroulette;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phil on 02/05/2016.
 */
public class GridFragment extends Fragment {

    private static final String LOG_TAG = GridFragment.class.getSimpleName();
    public static final String FILM_EXTRA = "film details";

    private FilmAdapter mFilmAdapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gridfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshGrid();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        refreshGrid();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        mFilmAdapter = new FilmAdapter(
                getActivity(),
                R.layout.list_item_film,
                new ArrayList<FilmInfo>());

        mGridView = (GridView) rootView.findViewById(R.id.film_grid);
        mGridView.setAdapter(mFilmAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                FilmInfo selected = mFilmAdapter.getItem(position);
                Log.d(LOG_TAG, "Packaging " + selected.toString() + " in Intent...");
                intent.putExtra(FILM_EXTRA, selected);
                startActivity(intent);
            }
        });

        refreshGrid();
        return rootView;
    }

    private void refreshGrid() {
        FetchFilmsTask filmsTask = new FetchFilmsTask();
        filmsTask.execute();
    }

    /**
     * Async background task to load in Films
     */
    public class FetchFilmsTask extends AsyncTask<String, Void, List<FilmInfo>> {

        private final String LOG_TAG = FetchFilmsTask.class.getSimpleName();

        @Override
        protected List<FilmInfo> doInBackground(String... params) {

            // An API_KEY for The Movie DB is required to use this app which requires a user account
            final String API_KEY = "";

            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String KEY_PARAM = "api_key";
            final String NUM_PARAM = "page";

            String jsonStr = "";
            String numPages = "3";
            String sortType;

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean sortMode = sharedPrefs.getBoolean(getString(R.string.sortmode_key_popular), true);

            if (sortMode) {
                sortType = "/popular";
            } else {
                sortType = "/top_rated";
            }
            String baseUrl = MOVIE_BASE_URL + sortType;

            try {

                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .appendQueryParameter(NUM_PARAM, numPages)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Log.d(LOG_TAG, jsonStr);

            List<FilmInfo> films = null;
            try {
                films = getFilmList(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return films;
        }

        private List<FilmInfo> getFilmList(String jsonStr) throws JSONException {
            final String FILM_TITLE = "title";
            final String FILM_DESCRIPTION = "overview";
            final String FILM_RELEASE = "release_date";
            final String FILM_POSTER = "poster_path";
            final String FILM_RATING = "vote_average";

            List<FilmInfo> list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject filmJSON = resultsArray.getJSONObject(i);

                String title = filmJSON.getString(FILM_TITLE);
                String description = filmJSON.getString(FILM_DESCRIPTION);
                String posterUrl = "http://image.tmdb.org/t/p/w342" + filmJSON.getString(FILM_POSTER);
                double rating = filmJSON.getDouble(FILM_RATING);
                String date = filmJSON.getString(FILM_RELEASE);
                //date = date.substring(0, 4);

                //Log.d(LOG_TAG, posterUrl);

                FilmInfo info = new FilmInfo(title, description, posterUrl, rating, date);
                list.add(info);
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<FilmInfo> films) {
            mFilmAdapter.clear();

            if (films != null) {
                mFilmAdapter.addAll(films);
            }
        }
    }
}
