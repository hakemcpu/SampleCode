package com.sample.flickr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	/**
	 * Checks WiFi connection
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWiFiConnected(Context context) {
		boolean isConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkinfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkinfo != null) {
				isConnected = networkinfo.isConnectedOrConnecting();
			}
		}
		return isConnected;
	}

	/**
	 * Checks mobile connection
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		boolean isConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkinfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (networkinfo != null) {
				isConnected = networkinfo.isConnected();
			}
		}
		return isConnected;
	}

	/**
	 * Checks connection without checking the preferences
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		return (isWiFiConnected(context) || isMobileConnected(context));
	}
}
