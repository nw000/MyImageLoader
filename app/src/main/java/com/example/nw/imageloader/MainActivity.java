package com.example.nw.imageloader;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv);
        imageView.setImageBitmap(decodeSampleBitmapFromResourse(getResources(), R.drawable.hotographer, 50, 50));
    }

    public Bitmap decodeSampleBitmapFromResourse(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;

        int inSampleSize = 1;
        if (outWidth > reqWidth || outHeight > reqHeight) {
            int widthRound = Math.round(outWidth / reqWidth);
            int heightRound = Math.round(outHeight / reqHeight);
            inSampleSize = widthRound > heightRound ? heightRound : widthRound;
        }
        return inSampleSize;
    }

    public void grid(View view) {
        startActivity(new Intent(this, GridActivity.class));
    }
}
