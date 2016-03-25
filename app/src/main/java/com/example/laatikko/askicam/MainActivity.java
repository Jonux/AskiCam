package com.example.laatikko.askicam;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Bitmap image = null;
    private ImageView imageView = null;
    private TextView  infoTextView  = null;

    private ImageButton callButton = null;
    private ImageButton refreshButton = null;
    private Boolean imageLoading = false;

    private class LoadImageFromUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Hack: Using local time, when starting the image downloading
            SimpleDateFormat sFormat = new SimpleDateFormat("HH:mm:ss");
            String imageLoadedTime = sFormat.format(new Date());
            Resources res = getResources();

            try {
                URL url = new URL(res.getString(R.string.askiCamUrl));
                InputStream in = url.openConnection().getInputStream();
                image = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception ex) {
                return res.getString(R.string.imageLoadingFailedMsg);
            }
            return String.format(res.getString(R.string.imageLoadedSuccessfullyMsg), imageLoadedTime);
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
            imageView.setImageBitmap(image);
            infoTextView.setText(message);
            imageLoading = false;
        }
    }

    // Fetch the image
    private void refreshView(){
        imageLoading = true;
        new LoadImageFromUrl().execute("");
        infoTextView.setText(R.string.imageLoadingStartedMsg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView1);
        infoTextView = (TextView) findViewById(R.id.textView);

        // Screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Image size
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = size.x;
        layoutParams.height = size.y;
        imageView.setLayoutParams(layoutParams);

        /*
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(size.x, size.y, conf); // this creates a MUTABLE bitmap
        bmp.eraseColor(Color.GRAY);
        */

        refreshView();

        // Call button
        callButton = (ImageButton) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Resources res = getResources();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(res.getString(R.string.askiCamNumber)));
                startActivity(callIntent);
            }
        });

        // Refresh button
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!imageLoading) refreshView();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
