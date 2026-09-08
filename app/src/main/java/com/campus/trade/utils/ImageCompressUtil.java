package com.campus.trade.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具（上传前将图片缩放到最长边不超过 1080、大小不超过 maxSize）
 */
public final class ImageCompressUtil {

    private ImageCompressUtil() {
    }

    /** 把相册 content Uri 拷贝到应用缓存子目录并压缩，返回本地文件路径；失败返回 null */
    public static File copyToCacheAndCompress(Context context, Uri uri, String subDirName, long maxSize) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            if (in == null) {
                return null;
            }
            String name = queryName(context, uri);
            if (TextUtils.isEmpty(name)) {
                name = "img_" + System.currentTimeMillis() + ".jpg";
            } else if (!name.contains(".")) {
                name = name + ".jpg";
            }
            File cacheDir = new File(context.getCacheDir(), subDirName);
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                return null;
            }
            File raw = new File(cacheDir, "raw_" + System.currentTimeMillis() + "_" + name);
            try (FileOutputStream out = new FileOutputStream(raw)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                in.close();
            }
            return compressToFile(raw.getAbsolutePath(), maxSize);
        } catch (IOException e) {
            return null;
        }
    }

    private static String queryName(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) {
                    return cursor.getString(idx);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static File compressToFile(String filePath, long maxSize) {
        File originalFile = new File(filePath);
        if (!originalFile.exists()) {
            return originalFile;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int sampleSize = 1;
        while (options.outWidth / sampleSize > 1080 || options.outHeight / sampleSize > 1080) {
            sampleSize *= 2;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null) {
            return originalFile;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        while (baos.toByteArray().length > maxSize && quality > 20) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        File compressedFile = new File(originalFile.getParent(), "compressed_" + System.currentTimeMillis() + "_" + originalFile.getName());
        try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
            fos.write(baos.toByteArray());
        } catch (IOException e) {
            return originalFile;
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return compressedFile;
    }
}
