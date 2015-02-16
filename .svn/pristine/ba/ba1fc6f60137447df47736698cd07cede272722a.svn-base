package com.example.top32show;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.util.Log;


/**
 * This class downloads pictures by http connection.
 * @author jennyfang
 *
 */
public class HttpUtils {
	private static String TAG = "Jenny";

	/**
	 * Download the picture by the unique image url.
	 * Store the image to certain folder in sdcard.
	 * @param imgUrl
	 * @param dir
	 * @param fileName
	 */
	public static void downloadSaveImg(String imgUrl, String dir, String fileName){  
		InputStream in = null;
        URL url;
        HttpURLConnection conn;
		try {
			url = new URL(imgUrl);
			conn = (HttpURLConnection) url.openConnection();
			in = conn.getInputStream();
			if (null != in) {
				ImageFileCache.saveBitmap(in, dir, fileName);
				in.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
    }  
}
