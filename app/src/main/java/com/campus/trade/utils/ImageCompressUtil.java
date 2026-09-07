package com.campus.trade.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩工具（上传前将图片缩放到最长边不超过 1080、大小不超过 maxSize）
 */
public final class ImageCompressUtil {

    private ImageCompressUtil() {
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
