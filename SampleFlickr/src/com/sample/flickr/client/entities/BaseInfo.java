package com.sample.flickr.client.entities;

import java.util.ArrayList;

import android.content.Context;
import android.util.Pair;

import com.sample.flickr.client.ServerCommunicationTypes;
import com.sample.flickr.ui.entities.FlickrPhoto;

/**
 * Abstract class to handle different types of requests. 
 * 
 * @author hzaied
 */
public abstract class BaseInfo {
	public ServerCommunicationTypes mCommType;
	public ArrayList<FlickrPhoto> mResultPhotosList;
	
	public abstract ArrayList<Pair<String, String>> getKeysValuePairs();
	public StatusTypes parseResponse(Context context, String response) {
		return StatusTypes.UNKOWN_ERROR;
	}
	
	public enum StatusTypes {
		SUCCESS, UNKOWN_ERROR, PARSING_ERROR, INVALIDE_API_KEY, SERVICE_UNAVAILABLE, BAD_URL, 
	}
}
