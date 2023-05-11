package com.le.info.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();


    /**
     * scale image
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        //paint.setColor(0xff424242);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle) {
            bitMap.recycle();
        }
        return newBitMap;
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int h = options.outHeight;
        int w = options.outWidth;
        int inSampleSize = 0;
        if (h > reqHeight || w > reqWidth) {
            float ratioW = (float) w / reqWidth;
            float ratioH = (float) h / reqHeight;
            inSampleSize = (int) Math.ceil(Math.min(ratioH, ratioW));
        }
        inSampleSize = Math.max(1, inSampleSize);
        return inSampleSize;
    }

    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight,boolean needRotate) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(filePath, options);
        //rotation of the image
        if (needRotate) {
            int degrees = getBitmapDegree(filePath);
            if (degrees != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degrees);
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                        scaledBitmap.getHeight(), matrix, true);
            }
        }

        return scaledBitmap;
    }


    public static void compressBitmapAndSave(String filePath, String savePath, int reqWidth, int reqHeight, int maxSize) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        Bitmap smallBitmap = getSmallBitmap(filePath, reqWidth, reqHeight,false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        smallBitmap.compress(format, options, baos);
        while (baos.toByteArray().length / 1024 > maxSize) {
            // 重置baos即清空baos
            baos.reset();
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            smallBitmap.compress(format, options, baos);
        }

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(savePath));
            os.write(baos.toByteArray());
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 释放Bitmap
        if (!smallBitmap.isRecycled()) {
            smallBitmap.recycle();
            smallBitmap = null;
        }

    }

    public static void compressBitmapAndSave(String filePath, String savePath, int reqWidth, int reqHeight, int maxSize,boolean needRotate) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        Bitmap smallBitmap = getSmallBitmap(filePath, reqWidth, reqHeight,needRotate);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        smallBitmap.compress(format, options, baos);
        while (baos.toByteArray().length / 1024 > maxSize) {
            // 重置baos即清空baos
            baos.reset();
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            smallBitmap.compress(format, options, baos);
        }

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(savePath));
            os.write(baos.toByteArray());
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 释放Bitmap
        if (!smallBitmap.isRecycled()) {
            smallBitmap.recycle();
            smallBitmap = null;
        }

    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 通过Uri获取文件路径
     * 支持图片媒体,文件等
     * <p/>
     * Author qiujuer@live.cn
     *
     * @param uri     Uri
     * @param context Context
     * @return 文件路径
     */
    @SuppressLint({"NewApi", "Recycle"})
    public static String getImagePath(Uri uri, Context context) {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            String authority = uri.getAuthority();
            if ("com.android.externalstorage.documents".equals(authority)) {
                // isExternalStorageDocument
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // isDownloadsDocument
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // isMediaDocument
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

}
