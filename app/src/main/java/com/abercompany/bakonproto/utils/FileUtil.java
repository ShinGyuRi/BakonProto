package com.abercompany.bakonproto.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import java.io.File;

public class FileUtil {

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 마운트가 됬는지 안됬는지 확인하는 메소드
     *
     * @return
     */
    public static boolean externalMemoryAvailable() {
        boolean isExternalMemory = false;
        try {
            isExternalMemory = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception err) {

        }
        return isExternalMemory;
    }

    /**
     * 외장 SD카드 남은 용량
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) { // ����Ʈ�� �Ǿ��ִ��� Ȯ���ϱ�.
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            path = null;
            stat = null;
            return availableBlocks * blockSize;
        } else {
            return 1;
        }
    }
}
