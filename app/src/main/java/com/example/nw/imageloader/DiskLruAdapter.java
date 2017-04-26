package com.example.nw.imageloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import disklrucache.DiskLruCache;

/**
 * Created by nw on 17/4/26.
 */

public class DiskLruAdapter extends ArrayAdapter<String> {


    private final GridView gridView;
    /**
     * 内存缓存的初始化
     */
    private LruCache<String, Bitmap> mLruCache;


    /**
     * 磁盘缓存的初始化
     */
    private DiskLruCache mDiskLruCache;


    public DiskLruAdapter(Context context, int resource, String[] objects, GridView gridView) {
        super(context, resource, objects);
        this.gridView = gridView;

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheMemory = maxMemory / 8;

        mLruCache = new LruCache<String, Bitmap>(mCacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        try {
            File cacheDir = getDiskCacheDir(context, "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        loadBitmaps(url, imageView);
        return view;
    }

    private void loadBitmaps(String url, ImageView imageView) {
        Bitmap bitmap = getBitmapFromLruMemory(url);
        if (bitmap == null) {

        } else {
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    class DiskBitmapTask extends AsyncTask<String ,Void ,Bitmap> {

        String url;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                url = params[0];
                String key = hashKeyForDisk(url);
                DiskLruCache.Value value = mDiskLruCache.get(key);
                DiskLruCache.Editor edit = value.edit();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


    }



    private void addBitmapToLruMemory(String url, Bitmap bitmap) {
        if (getBitmapFromLruMemory(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    private Bitmap getBitmapFromLruMemory(String url) {
        return mLruCache.get(url);
    }

    private File getDiskCacheDir(Context context, String thumb) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + thumb);
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
