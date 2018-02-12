package com.techart.writersblock.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;

import com.techart.writersblock.R;

/**
 * Class for working with images
 * Created by Kelvin on 17/09/2017.
 */

public final class ImageUtils {
    private ImageUtils()
    {

    }

    public static int getStoryUrl(String category, String title) {
        switch (category)
        {
            case "Action":
                return R.drawable.action;
            case "Drama":
                return getDramaImage(title);
            case "Fiction":
                return getFictionImage(title);
            case "Romance":
                return getRomanceImage(title);
            default: return R.drawable.fiction;
        }
    }

    private static int getFictionImage(String title) {
        switch (title)
        {
            case "The Tumans":
                return R.drawable.fiction;
            case "JUSTICE MUST BE SERVED":
                return R.drawable.fiction1;
            case "HALLELUJAH; You Are Home":
                return R.drawable.fiction2;
            case "A PRAYER AND A DOLLAR":
                return R.drawable.adollar;
            case "SILENCE":
                return R.drawable.fiction3;
            default: return R.drawable.fiction4;
        }
    }

    private static int getDramaImage(String title) {
        switch (title)
        {
            case "A PRAYER AND A DOLLAR":
                return R.drawable.adollar;
            default: return R.drawable.drama;
        }
    }

    private static int getRomanceImage(String title) {
        switch (title)
        {
            case "EMOTIONALLY DETACHED":
                return R.drawable.romance;
            default: return R.drawable.romance1;
        }
    }


    public static int getPoemUrl(int category) {
        switch (category)
        {
            case 0:
                return R.drawable.poem1;
            case 1:
                return R.drawable.poem2;
            case 2:
                return R.drawable.poem3;
            case 3:
                return R.drawable.poem4;
            case 4:
                return R.drawable.poem5;
            default: return R.drawable.poem6;
        }
    }

    public static int getDevotionUrl(int category) {
        switch (category)
        {
            case 0:
                return R.drawable.devotion;
            case 1:
                return R.drawable.devotion1;
            case 2:
                return R.drawable.devotion2;
            default: return R.drawable.devotion3;
        }
    }


    public static String getRealPathFromUrl(Context context, Uri imageUrl)
    {
        Cursor curseo = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            curseo = context.getContentResolver().query(imageUrl,proj,null,null,null);
            int coIumnndex = curseo.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            curseo.moveToFirst();
            return curseo.getString(coIumnndex);
        }
        finally {
            if (curseo != null)
            {
                curseo.close();
            }
        }
    }
    public static Bitmap scaleDown(Bitmap realImage,Context context)
    {
        Bitmap newImage = Bitmap.createBitmap(250,250, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newImage);
        Matrix matrix = new Matrix();
        matrix.setScale((float)250/realImage.getWidth(),(float)250/realImage.getHeight());
        canvas.drawBitmap(realImage,matrix,new Paint());
        return newImage;
    }
}
