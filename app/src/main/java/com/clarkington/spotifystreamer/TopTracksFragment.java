package com.clarkington.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private FetchTracksTask fetchTracksTask;
    private StreamerTrackAdapter streamerTrackAdapter;
    private ListView tracksListView;
    private ProgressBar tracksProgressBar;
    private TextView noTracksTextView;

    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        streamerTrackAdapter = new StreamerTrackAdapter(getActivity(), new ArrayList<StreamerTrack>());
        tracksListView = (ListView)rootView.findViewById(R.id.list_view_tracks);
        tracksListView.setAdapter(streamerTrackAdapter);

        tracksProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar_tracks);
        noTracksTextView = (TextView)rootView.findViewById(R.id.text_view_no_tracks);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("tracks")) {
                ArrayList<StreamerTrack> savedTracks = savedInstanceState.getParcelableArrayList("tracks");
                if (savedTracks.size() > 0) {
                    for (StreamerTrack streamerTrack : savedTracks) {
                        streamerTrackAdapter.add(streamerTrack);
                    }
                    tracksListView.setVisibility(View.VISIBLE);
                    tracksProgressBar.setVisibility(View.GONE);
                    noTracksTextView.setVisibility(View.GONE);
                } else {
                    tracksListView.setVisibility(View.GONE);
                    tracksProgressBar.setVisibility(View.GONE);
                    noTracksTextView.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
                updateTracks(artistId);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (streamerTrackAdapter != null) {
            ArrayList<StreamerTrack> tracksToSave = new ArrayList<StreamerTrack>();
            for (int i = 0; i < streamerTrackAdapter.getCount(); i++)
            {
                tracksToSave.add(streamerTrackAdapter.getItem(i));
            }
            outState.putParcelableArrayList("tracks", tracksToSave);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateTracks(String artistId) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (fetchTracksTask == null) {
                fetchTracksTask = new FetchTracksTask();
            } else {
                fetchTracksTask.cancel(false);
            }
            fetchTracksTask = new FetchTracksTask();
            fetchTracksTask.execute(artistId);
        }
        else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet_message, Toast.LENGTH_LONG );
            toast.show();
        }
    }

    public class FetchTracksTask extends AsyncTask<String, Void, Tracks>
    {
        private final String LOG_TAG = FetchTracksTask.class.getSimpleName();
        private final int SMALL_IMAGE_WIDTH = 200;
        private final int LARGE_IMAGE_WIDTH = 640;

        @Override
        protected void onPreExecute() {
            tracksListView.setVisibility(View.GONE);
            tracksProgressBar.setVisibility(View.VISIBLE);
            noTracksTextView.setVisibility(View.GONE);
        }

        @Override
        protected Tracks doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            if (params[0].length() == 0) {
                return null;
            }

            try {
                String artistId = params[0];
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                Map countryMap = new HashMap<String, Object>();
                countryMap.put("country", "US");
                Tracks results = spotify.getArtistTopTrack(artistId, countryMap);
                return results;
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tracks result) {

            if (result == null || result.tracks.size() == 0) {
                streamerTrackAdapter.clear();
                tracksListView.setVisibility(View.GONE);
                tracksProgressBar.setVisibility(View.GONE);
                noTracksTextView.setVisibility(View.VISIBLE);
            }
            else {
                streamerTrackAdapter.clear();
                for (int i = 0; i < result.tracks.size() && i < 10; i++) {
                    Track track = result.tracks.get(i);
                    Image sizedImage = null;
                    StreamerTrack streamerTrack;
                    if (track.album.images.size() > 0) {
                        sizedImage = track.album.images.get(0);
                    }
                    for (Image image : track.album.images) {
                        if (image.width < SMALL_IMAGE_WIDTH) {
                            break;
                        }
                        sizedImage = image;
                    }
                    String smallImageUrl = null;
                    if (sizedImage != null) {
                        smallImageUrl = sizedImage.url;
                    }

                    sizedImage = null;
                    if (track.album.images.size() > 0) {
                        sizedImage = track.album.images.get(0);
                    }
                    for (Image image : track.album.images) {
                        if (image.width < LARGE_IMAGE_WIDTH) {
                            break;
                        }
                        sizedImage = image;
                    }
                    String largeImageUrl = null;
                    if (sizedImage != null) {
                        largeImageUrl = sizedImage.url;
                    }

                    streamerTrack = new StreamerTrack(track.name, track.album.name, smallImageUrl, largeImageUrl, track.preview_url);
                    streamerTrackAdapter.add(streamerTrack);
                }
                tracksListView.setVisibility(View.VISIBLE);
                tracksProgressBar.setVisibility(View.GONE);
                noTracksTextView.setVisibility(View.GONE);
            }
        }
    }
}
