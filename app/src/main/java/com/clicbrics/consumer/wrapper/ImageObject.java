package com.clicbrics.consumer.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 17/1/17.
 */
public class ImageObject implements Parcelable {
    public String image_url; //if video this will be thumbnail's url
    public boolean isVideo;
    public String videoUrl;

    public ImageObject(){}

    protected ImageObject(Parcel in) {
        image_url = in.readString();
        isVideo = in.readByte() != 0;
        videoUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image_url);
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeString(videoUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageObject> CREATOR = new Creator<ImageObject>() {
        @Override
        public ImageObject createFromParcel(Parcel in) {
            return new ImageObject(in);
        }

        @Override
        public ImageObject[] newArray(int size) {
            return new ImageObject[size];
        }
    };
}
