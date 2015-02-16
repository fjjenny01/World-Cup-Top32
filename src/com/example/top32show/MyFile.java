package com.example.top32show;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.content.Context;


/**
 * This class performs some file read & write related work.
 * @author jennyfang
 *
 */
public class MyFile {
	

	/**
	 * 
	 * @param path 文件目录名
	 * @param fileName 文件名
	 * @return boolean if the certain file exists in the folder
	 */
	public static boolean  isFileExisting(String path, String fileName)
	{
		File dir = new File(path);
		if(!dir.exists())    
        {    
            dir.mkdirs();    
        }    
           
		File[] files = dir.listFiles();
		if (files.length !=0)
		{
			int i;
			for(i = 0; i < files.length; i++)
			{
				if(files[i].getName().equals(fileName))
				{
					break;
				}
			}
			if (i < files.length)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * delete all files in a folder
	 * @param dir the folder contains files need to be deleted
	 */
	public static void deleteAllFiles(String dir)
	{
		File folder = new File(dir);
		if (folder.exists() && folder.isDirectory())
		{
			
			File[] files = folder.listFiles();
			
			if (files.length !=0)
			{
				for (File file: files)
				{
					file.delete();		
				}
			
			}
		}
	}
	
	/**
	 * write the inputstream to a file
	 * @param from InputStream
	 * @param to FileOutputStream
	 */
	public static void copyFile(InputStream from, FileOutputStream to)
	{
		
		try {

			byte[] buffer = new byte[1024];  
			  
		    int length;  
		          
		    while ((length = from.read(buffer)) > 0) {  
		        to.write(buffer, 0, length);  
		     }  
		    from.close();  
		    to.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
