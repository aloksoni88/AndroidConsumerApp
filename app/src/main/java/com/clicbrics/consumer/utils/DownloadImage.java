package com.clicbrics.consumer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by Alok on 30-11-2017.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    public DownloadImage(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap mIcon11 = null;
        try {
        String decodedURL = URLDecoder.decode(urls[0],"UTF-8");
        URL url = new URL(decodedURL);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        String urldisplay = uri.toString();
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Download Image Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(result != null) {
            imageView.setImageBitmap(result);
        }
    }
}
