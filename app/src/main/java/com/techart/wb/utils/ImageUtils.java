package com.techart.wb.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import com.techart.wb.R;

/**
 * Class for working with images
 * Created by Kelvin on 17/09/2017.
 */

public final class ImageUtils {

    private ImageUtils() {
    }

    public static int getStoryUrl(String category) {
        switch (category) {
            case "Action":
                return R.drawable.action;
            case "Comedy":
                return R.drawable.comedy;
            case "Drama":
                return R.drawable.drama;
            case "Fiction":
                return R.drawable.fiction;
            case "Horror":
                return R.drawable.horror;
            case "Romance":
                return R.drawable.romance;
            case "Sci-Fi":
                return R.drawable.scifi;
            case "Tradition":
                return R.drawable.tradition;
            case "Traditional":
                return R.drawable.tradition;
            case "Thriller":
                return R.drawable.horror;
            default: return R.drawable.fiction;
        }
    }

    public static int getPoemUrl() {
        return R.drawable.poem;
    }

    public static int getDevotionUrl() {
        return R.drawable.devotion;
    }

    public static String getRealPathFromUrl(Context context, Uri imageUrl) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(imageUrl,projection,null,null,null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
