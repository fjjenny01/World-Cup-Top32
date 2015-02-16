package com.example.top32show;

import java.util.LinkedList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

/**
 * 异步加载图片 Imageloader创建一个带有两个线程的线程池。
 * 
 * @author jennyfang
 * 
 */

public class ImageLoader {
	private final static String TAG = "ImageLoader";

	public LinkedList<Task> waitingQueue = new LinkedList<Task>();
	public LinkedList<Task> downloadingQueue = new LinkedList<Task>(); // 现在只有一个
	private static ImageLoader instance;

	private Handler handler;
	private Context context;
	private final int MAX_THREAD_SUM = 2;
	private DoTaskThread[] mDoTasks;

	public ImageLoader(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;

		mDoTasks = new DoTaskThread[MAX_THREAD_SUM];
		for (int i = 0; i < mDoTasks.length; i++) {
			mDoTasks[i] = new DoTaskThread();
			mDoTasks[i].start();
		}
	}

	public static ImageLoader getInstance(Context context, Handler handler) {
		if (instance == null)
			instance = new ImageLoader(context, handler);
		return instance;
	}

	/**
	 * 添加任务
	 */
	public void addTask(String url, String dir, String fileName) {
		Log.i(TAG, "addTask start");
		synchronized (waitingQueue) {
			if (isTaskExist(url)) {
				return;
			}
			
			waitingQueue.add(new Task(url, dir, fileName));
			Log.i(TAG, "addTask " + fileName);
			waitingQueue.notify();
			Log.i(TAG, "waitingQueue.notify()");
		}
	}
	
	/**
	 * 完成一个任务
	 * @return
	 */
	private Task take() {
		synchronized (waitingQueue) {
			if (!waitingQueue.isEmpty()) {
				return waitingQueue.removeLast();
			}
		}
		return null;
	}
	
	/**
	 *  检查任务是否在waitingQueue里
	 * @param url
	 * @return
	 */
	public boolean isTaskExist(String url) {
		for (Task task : waitingQueue) {
			if (task.url.equals(url)) {
				return true;
			}
		}
		
		for (Task task : downloadingQueue) {
			if (task.url.equals(url)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 检查当前网络状态是否为wifi
	 * @return
	 */
	public boolean isWifi() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager
				.getActiveNetworkInfo();
		if (mNetworkInfo != null
				&& mNetworkInfo.isAvailable()
				&& mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		
		return false;
	}

	
	/**
	 * 实现图片异步加载的线程
	 * @author jennyfang
	 *
	 */
	public class DoTaskThread extends Thread {

		@Override
		public void run() {
			Log.i(TAG, "Thread run");
			while (true) {
				synchronized (waitingQueue) {
					if (waitingQueue.isEmpty()) {
						try {
							waitingQueue.wait();
							Log.i(TAG, "waitingQueue.wait()");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if (isWifi()) {
					Task t = take();
					downloadingQueue.add(t);
					Log.i(TAG, "before save");
					HttpUtils.downloadSaveImg(t.url, t.dir, t.fileName);
					downloadingQueue.remove(t);
					Log.i(TAG, "After save");
					handler.obtainMessage(0, null).sendToTarget();
				}
				
				
			}
		}

	}

}