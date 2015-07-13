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
 * Created by rclark on 7/10/2015.
 */
public class StreamerTrackAdapter extends ArrayAdapter<StreamerTrack> {
    private static final String LOG_TAG = StreamerArtistAdapter.class.getSimpleName();

    TrackViewHolder trackViewHolder;

    public StreamerTrackAdapter (Activity context, List<StreamerTrack> streamerTracks) {
        super(context, 0, streamerTracks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StreamerTrack streamerTrack = getItem(position);

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_track, parent, false);
            trackViewHolder = new TrackViewHolder();
            trackViewHolder.imageViewTrack = (ImageView) convertView.findViewById(R.id.image_view_track);
            trackViewHolder.textViewTrackName = (TextView) convertView.findViewById(R.id.text_view_track_name);
            trackViewHolder.textViewAlbumName = (TextView) convertView.findViewById(R.id.text_view_album_name);
            convertView.setTag(trackViewHolder);
        }
        else {
            trackViewHolder = (TrackViewHolder) convertView.getTag();
        }


        if (streamerTrack.getSmallImageThumbnailUrl() != null && streamerTrack.getSmallImageThumbnailUrl().length() > 0 && Patterns.WEB_URL.matcher(streamerTrack.getSmallImageThumbnailUrl()).matches()) {
            Picasso.with(context).load(streamerTrack.getSmallImageThumbnailUrl()).into(trackViewHolder.imageViewTrack);
        }

        trackViewHolder.textViewTrackName.setText(streamerTrack.getTrackName());
        trackViewHolder.textViewAlbumName.setText(streamerTrack.getAlbumName());

        return convertView;
    }

    static class TrackViewHolder {
        ImageView imageViewTrack;
        TextView textViewTrackName;
        TextView textViewAlbumName;
    }
}
