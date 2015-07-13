package com.clarkington.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    private FetchArtistsTask fetchArtistsTask;
    private StreamerArtistAdapter streamerArtistAdapter;
    private ListView artistsListView;
    private ProgressBar artistsProgressBar;
    private TextView noArtistsTextView;
    private SearchView artistSearchView;
    private String searchText;
    private Boolean isIconified = true;

    public ArtistSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_artist_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search_view_artist);
        artistSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        artistSearchView.setQueryHint(getString(R.string.search_artist));
        if (searchText != null) {
            artistSearchView.setQuery(searchText, false);
            artistSearchView.setIconified(isIconified);
        }
        artistSearchView.setIconified(isIconified);
        artistSearchView.clearFocus();

        artistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                updateArtists(query);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);

        streamerArtistAdapter = new StreamerArtistAdapter(getActivity(), new ArrayList<StreamerArtist>());
        artistsListView = (ListView)rootView.findViewById(R.id.list_view_artists);
        artistsListView.setAdapter(streamerArtistAdapter);
        artistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getActivity();
                StreamerArtist artist = streamerArtistAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), TopTracks.class);
                intent.putExtra(Intent.EXTRA_TEXT, artist.getId());
                startActivity(intent);
            }
        });

        artistsProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar_artists);
        noArtistsTextView = (TextView)rootView.findViewById(R.id.text_view_no_artists);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("search")) {
                searchText = savedInstanceState.getString("search");
            }
            if (savedInstanceState.containsKey("isIconified")) {
                isIconified = savedInstanceState.getBoolean("isIconified");
            }
            if (savedInstanceState.containsKey("artists")) {
                ArrayList<StreamerArtist> savedArtists = savedInstanceState.getParcelableArrayList("artists");
                if (savedArtists.size() > 0) {
                    for (StreamerArtist streamerArtist : savedArtists) {
                        streamerArtistAdapter.add(streamerArtist);
                    }
                    artistsListView.setVisibility(View.VISIBLE);
                    artistsProgressBar.setVisibility(View.GONE);
                    noArtistsTextView.setVisibility(View.GONE);
                } else {
                    artistsListView.setVisibility(View.GONE);
                    artistsProgressBar.setVisibility(View.GONE);
                    noArtistsTextView.setVisibility(View.VISIBLE);
                }
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (streamerArtistAdapter != null) {
            ArrayList<StreamerArtist> artistsToSave = new ArrayList<StreamerArtist>();
            for (int i = 0; i < streamerArtistAdapter.getCount(); i++)
            {
                artistsToSave.add(streamerArtistAdapter.getItem(i));
            }
            outState.putParcelableArrayList("artists", artistsToSave);
            outState.putString("search", artistSearchView.getQuery().toString());
            outState.putBoolean("isIconified", artistSearchView.isIconified());
        }
        super.onSaveInstanceState(outState);
    }

    private void updateArtists(String artistSearchText) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (fetchArtistsTask == null) {
                fetchArtistsTask = new FetchArtistsTask();
            } else {
                fetchArtistsTask.cancel(false);
            }
            fetchArtistsTask = new FetchArtistsTask();
            fetchArtistsTask.execute(artistSearchText);
        }
        else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet_message, Toast.LENGTH_LONG );
            toast.show();
        }

    }

    public class FetchArtistsTask extends AsyncTask<String, Void, ArtistsPager>
    {
        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();
        private final int IMAGE_WIDTH = 64;

        @Override
        protected void onPreExecute() {
            artistsListView.setVisibility(View.GONE);
            artistsProgressBar.setVisibility(View.VISIBLE);
            noArtistsTextView.setVisibility(View.GONE);
        }

        @Override
        protected ArtistsPager doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            if (params[0].length() == 0) {
                return null;
            }

            try {
                String artistSearchText = params[0];
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(artistSearchText);
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
        protected void onPostExecute(ArtistsPager result) {

            if (result == null || result.artists.items.size() == 0) {
                streamerArtistAdapter.clear();
                artistsListView.setVisibility(View.GONE);
                artistsProgressBar.setVisibility(View.GONE);
                noArtistsTextView.setVisibility(View.VISIBLE);
            }
            else {
                streamerArtistAdapter.clear();
                for (Artist artist : result.artists.items) {
                    Image sizedImage = null;
                    StreamerArtist streamerArtist;
                    if (artist.images.size() > 0) {
                        sizedImage = artist.images.get(0);
                    }
                    for (Image image :artist.images) {
                        if (image.width < IMAGE_WIDTH) {
                            break;
                        }
                        sizedImage = image;
                    }
                    if (sizedImage != null) {
                        streamerArtist = new StreamerArtist(artist.name, artist.id, sizedImage.url);
                    }
                    else {
                        streamerArtist = new StreamerArtist(artist.name, artist.id, null);
                    }
                    streamerArtistAdapter.add(streamerArtist);
                }
                artistsListView.setVisibility(View.VISIBLE);
                artistsProgressBar.setVisibility(View.GONE);
                noArtistsTextView.setVisibility(View.GONE);
                artistSearchView.clearFocus();
            }
        }
    }
}

