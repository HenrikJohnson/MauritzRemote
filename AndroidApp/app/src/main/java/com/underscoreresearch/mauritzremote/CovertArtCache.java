package com.underscoreresearch.mauritzremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.WeakHashMap;

/**
 * Created by henri on 1/30/2017.
 */

public class CovertArtCache {
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static WeakHashMap<String, Bitmap> existingBitmaps = new WeakHashMap<>();

    public static void loadBitmap(Context context, String url, final Response.Listener<Bitmap> listener) {
        final String key = md5(url);
        Bitmap bitmap = existingBitmaps.get(key);
        if (bitmap != null) {
            listener.onResponse(bitmap);
            return;
        }

        final File file = new File(context.getCacheDir() + "/" + key);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                existingBitmaps.put(key, bitmap);
                listener.onResponse(bitmap);
                return;
            }
        }

        RemoteService.loadData(url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(response);
                    fos.close();
                } catch (IOException e) {
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                if (bitmap != null) {
                    existingBitmaps.put(key, bitmap);
                    listener.onResponse(bitmap);
                }
            }
        });
    }
}
