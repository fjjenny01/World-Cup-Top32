package com.example.top32show;

import android.graphics.BitmapFactory;

/**
 * 考虑到大图从sdcard里读取会造成内存不够，所以当需要显示的是小图而原图很多大时，
 * 采用压缩的方法，优化读取图片的过程
 * @author jennyfang
 *
 */
public class PictureOptimizer {
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth)
		{
			final int heightRatio  = height / reqHeight;
			final int widthRatio = width / reqWidth;
			inSampleSize = heightRatio < widthRatio? heightRatio: widthRatio;
		}
		return inSampleSize;
	}

}
