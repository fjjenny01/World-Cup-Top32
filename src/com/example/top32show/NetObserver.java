package com.example.top32show;

/**
 * Main Activity extends this interface. So the receiver can get access
 * to the notifyDataChanged() of Adapter
 * @author jennyfang
 *
 */
public interface NetObserver {
	public void notifyDataChanged();
}
