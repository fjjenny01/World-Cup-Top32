package com.example.top32show;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * This class extends BroadcastReceiver and observes network connectivity change.
 * If the receiver receives the message, it will call observer's method to notify
 * the change.
 * @author jennyfang
 *
 */

public class NetworkChangeReceiver extends BroadcastReceiver{

	private static final String TAG = "Jenny3";  
    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";  
    private NetObserver observer; 
	
	public void registerNetObserver(NetObserver observer)
    {
    	this.observer = observer;
    }
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)){  
			Log.i(TAG, "network status changed");
           
            if(NetworkHelper.isNetworkConnected(context)){  
            	Log.i(TAG, "network connected");
            	observer.notifyDataChanged();
            
            }
           
        }     
	}
	

}
