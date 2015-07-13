package com.clarkington.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rclark on 7/8/2015.
 */
public class StreamerArtistAdapter extends ArrayAdapter<StreamerArtist> {
    private static final String LOG_TAG = StreamerArtistAdapter.class.getSimpleName();

    ArtistViewHolder artistViewHolder;

    public StreamerArtistAdapter (Activity context, List<StreamerArtist> streamerArtists) {
        super(context, 0, streamerArtists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StreamerArtist streamerArtist = getItem(position);
        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);
            artistViewHolder = new ArtistViewHolder();
            artistViewHolder.imageViewArtist = (ImageView) convertView.findViewById(R.id.image_view_artist);
            artistViewHolder.textViewArtist = (TextView) convertView.findViewById(R.id.text_view_artist);
            convertView.setTag(artistViewHolder);
        }
        else {
            artistViewHolder = (ArtistViewHolder) convertView.getTag();
        }

        if (streamerArtist.getImageUrl() != null && streamerArtist.getImageUrl().length() > 0 && Patterns.WEB_URL.matcher(streamerArtist.getImageUrl()).matches()) {
            Picasso.with(context).load(streamerArtist.getImageUrl()).into(artistViewHolder.imageViewArtist);
        }

        artistViewHolder.textViewArtist.setText(streamerArtist.getName());
        return convertView;
    }

    static class ArtistViewHolder {
        ImageView imageViewArtist;
        TextView textViewArtist;
    }
}
