package com.example.nw.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nw on 17/4/25.
 */

public class WallAdapter extends ArrayAdapter<String> implements AbsListView.OnScrollListener {


    private final LruCache<String, Bitmap> mLruCache;


    private final GridView gridView;
    private int mFirstVisibleTtem;
    private int mVisibleItemCount;

    private Set<BitmapTask> setCollection ;

    private boolean isFirstEnter = true;

    public WallAdapter(Context context, int resource, String[] objects, GridView gridView) {
        super(context, resource, objects);
        this.gridView = gridView;
        setCollection = new HashSet<>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        gridView.setOnScrollListener(this);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String url = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.photo);
        imageView.setTag(url);
        setImageView(url, imageView);
        return view;
    }

    private void setImageView(String url, ImageView imageView) {
        Bitmap bitmap = getBitmapFromCacheMemory(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.hotographer);
        }
    }

    private Bitmap getBitmapFromCacheMemory(String url) {
        return mLruCache.get(url);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            loaderImages(mFirstVisibleTtem,mVisibleItemCount);
        } else {
            cancellAllTasks();
        }
    }

    private void cancellAllTasks() {

    }

    private void loaderImages(int mFirstVisibleTtem, int mVisibleItemCount) {
        for (int i = mFirstVisibleTtem; i< mFirstVisibleTtem + mVisibleItemCount;i ++) {
            String url = getItem(i);
            Bitmap bitmap = getBitmapFromCacheMemory(url);
            if (bitmap == null) {
                BitmapTask bitmapTask = new BitmapTask();
                bitmapTask.execute(url);
                setCollection.add(bitmapTask);
            } else {
                ImageView imageView = (ImageView) gridView.findViewWithTag(url);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleTtem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        if (isFirstEnter && visibleItemCount != 0) {
            loaderImages(firstVisibleItem,visibleItemCount);
            isFirstEnter = false;
        }

    }

    class  BitmapTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }
}
