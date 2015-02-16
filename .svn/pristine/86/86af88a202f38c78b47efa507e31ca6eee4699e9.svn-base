package com.example.top32show;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageFileCache {
	private static String TAG = "Jenny";
	/** 从缓存中获取图片 **/
    public static Bitmap getImage(String dir, String fileName, int reqHeight, int reqWidth) {    
        String path = dir + "/" + fileName;
    	File file = new File(path);
        if (file.exists()) {
        	Bitmap bmp = null;
        	BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inJustDecodeBounds = true;
        	BitmapFactory.decodeFile(path, options);
        	options.inSampleSize = PictureOptimizer.calculateInSampleSize(options, reqHeight, reqWidth);
        	options.inJustDecodeBounds = false;
        	try
        	{
        		bmp = BitmapFactory.decodeFile(path, options);
        	}catch(OutOfMemoryError e)
        	{
        		Log.i(TAG, "out of memory");
        	}
        	

            if (bmp == null) {
                file.delete();
            } else {
                return bmp;
            }
        }
        return null;
    }
    /**
     * Save the bitmap by writing the inputStream to file.
     * @param inputStream
     * @param dir
     * @param fileName
     */
    public static void saveBitmap(InputStream inputStream, String dir, String fileName) {
        if (inputStream == null) {
            return;
        }

        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dirFile, fileName + ".tmp");
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
        
       int ch = 0;
        try {
			while ((ch = inputStream.read())!=-1)
			{
				fos.write(ch);
			}
			fos.flush();
			file.renameTo(new File(dirFile, fileName));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
       finally{
            try {
				fos.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        } 
    }
    
}
