package com.clarkington.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rclark on 7/8/2015.
 */
public class StreamerArtist implements Parcelable {

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String imageUrl;
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StreamerArtist(String name, String id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    protected StreamerArtist(Parcel in) {
        name = in.readString();
        id = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(imageUrl);
    }

    public static final Creator<StreamerArtist> CREATOR = new Creator<StreamerArtist>() {
        @Override
        public StreamerArtist createFromParcel(Parcel in) {
            return new StreamerArtist(in);
        }

        @Override
        public StreamerArtist[] newArray(int size) {
            return new StreamerArtist[size];
        }
    };
}
