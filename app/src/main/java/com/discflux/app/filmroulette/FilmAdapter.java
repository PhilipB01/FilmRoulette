package com.discflux.app.filmroulette;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Phil on 02/05/2016.
 */
public class FilmAdapter extends ArrayAdapter<FilmInfo> {

    private static final String LOG_TAG = FilmAdapter.class.getSimpleName();
    private Context mContext;

    public FilmAdapter(Context context, int resourceId, List<FilmInfo> items) {
        super(context, resourceId, items);
        this.mContext = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        //TextView textView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        FilmInfo film = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_film, null);
            holder = new ViewHolder();
            //holder.textView = (TextView) convertView.findViewById(R.id.list_item_film_textview);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_film_imageview);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        //holder.textView.setText(film.getTitle());
        if (!film.getPosterUrl().equals("")) {
            // debug picasso image loading indicators
            //Picasso.with(mContext).setIndicatorsEnabled(false);
            Picasso.with(mContext)
                    .load(film.getPosterUrl())
                    .into(holder.imageView);
        } else {
            Picasso.with(mContext)
                    .load(R.mipmap.ic_launcher)
                    .into(holder.imageView);
        }

        return convertView;
    }
}
