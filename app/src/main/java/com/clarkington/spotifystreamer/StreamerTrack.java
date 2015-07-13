package com.clarkington.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rclark on 7/10/2015.
 */
public class StreamerTrack implements Parcelable {

    private String trackName;
    public String getTrackName() {
        return trackName;
    }
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    private String albumName;
    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    private String smallImageThumbnailUrl;
    public String getSmallImageThumbnailUrl() { return smallImageThumbnailUrl; }
    public void setSmallImageThumbnailUrl(String smallImageThumbnailUrl) { this.smallImageThumbnailUrl = smallImageThumbnailUrl; }

    private String largeImageThumbnailUrl;
    public String getLargeImageThumbnailUrl() { return largeImageThumbnailUrl; }
    public void setLargeImageThumbnailUrl(String largeImageThumbnailUrl) { this.largeImageThumbnailUrl = largeImageThumbnailUrl; }

    private String previewUrl;
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

    protected StreamerTrack(Parcel in) {
        trackName = in.readString();
        albumName = in.readString();
        smallImageThumbnailUrl = in.readString();
        largeImageThumbnailUrl = in.readString();
        previewUrl = in.readString();
    }

    public StreamerTrack(String trackName, String albumName, String smallImageThumbnailUrl, String largeImageThumbnailUrl, String previewUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.smallImageThumbnailUrl = smallImageThumbnailUrl;
        this.largeImageThumbnailUrl = largeImageThumbnailUrl;
        this.previewUrl = previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(smallImageThumbnailUrl);
        dest.writeString(largeImageThumbnailUrl);
        dest.writeString(previewUrl);
    }

    public static final Creator<StreamerTrack> CREATOR = new Creator<StreamerTrack>() {
        @Override
        public StreamerTrack createFromParcel(Parcel in) {
            return new StreamerTrack(in);
        }

        @Override
        public StreamerTrack[] newArray(int size) {
            return new StreamerTrack[size];
        }
    };
}
