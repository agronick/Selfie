package com.kyle.selfie.selfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kyle on 10/11/14.
 */
public class EndlessAdapter extends BaseAdapter {

    public static final String INSTAURL = "https://api.instagram.com/v1/tags/selfie/media/recent?client_id=f4824e7ec5bc472193b76ea085e87881&count=100";

    private Context mContext;
    private GridView view;
    private ArrayList<String> imageLoc ;
    private ArrayList<Drawable> images = new ArrayList<Drawable>();
    private TextView output;

    private int hasDownloaded = 0;
    private int toDownload = 0;

    public EndlessAdapter(Context c, GridView gridview, TextView status) {
        mContext = c;
        view = gridview;
        output = status;
        getInstaContent(0 ,0);
    }

    private void getInstaContent(int min, int max) {
        output.setText("Fetching pictures");
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = null;
                            url = new URL(INSTAURL);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            url.openStream()));

                            String inputLine;
                            String output = "";

                            while ((inputLine = in.readLine()) != null) {
                                output += inputLine;
                            }

                            JSONObject jObject = new JSONObject(output);
                            JSONArray jArray = jObject.getJSONArray("data");

                            imageLoc = new ArrayList<String>();
                            toDownload = jArray.length();
                            hasDownloaded = 0;

                            for(int i = 0; i < jArray.length(); i++)
                            {
                                String download = jArray.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail").getString("url");

                                new DownloadImage().execute(download);
                            }


                        }catch(Exception e){
                            output.setText(e.getLocalizedMessage());
                        }
                    }
                }
        ).start();
    }

    public int getCount() {
        return images.size();
    }

    public Drawable getItem(int position) {
        return images.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        //You guys asked in the email for a pattern with big, small, small
        boolean big = ((position % 3) == 0);

        ImageView imageView;

        if(convertView != null) {
            imageView = (ImageView) convertView;
        }else{
            imageView = new ImageView(mContext);
        }

        imageView.setLayoutParams(new GridView.LayoutParams(big ? 175 : 150, big ? 175 : 150));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int pad = big ? 0 : 12;
        imageView.setPadding(pad, pad, pad, pad);


        imageView.setImageDrawable(images.get(position));
        return imageView;
    }

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            return downloadImage(arg0[0]);
        }

        protected void onPostExecute(Drawable image)
        {
            images.add(image);
            hasDownloaded++;

            Log.d(Selfie.TAG, "Downloaded " + hasDownloaded);

            output.setText("Downloaded " + hasDownloaded + "/" + toDownload);

            if(hasDownloaded == toDownload)
            {
                output.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        }


        private Drawable downloadImage(String _url)
        {
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            try {
                url = new URL(_url);
                in = url.openStream();

                buf = new BufferedInputStream(in);

                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }

    }

}
