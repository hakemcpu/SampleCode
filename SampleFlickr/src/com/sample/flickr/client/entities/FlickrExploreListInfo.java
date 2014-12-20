package com.sample.flickr.client.entities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.sample.flickr.client.ServerCommunicationTypes;
import com.sample.flickr.ui.entities.FlickrPhoto;
import com.sample.flickr.utils.Constants;

public class FlickrExploreListInfo extends BaseInfo {
	public int mPage;

	public FlickrExploreListInfo(int page) {
		this.mPage = page;
		this.mCommType = ServerCommunicationTypes.GET_FLICKR_EXPLORE_LIST;
	}

	/**
	 * Get the request parameters.
	 */
	@Override
	public ArrayList<Pair<String, String>> getKeysValuePairs() {
		ArrayList<Pair<String, String>> pairsList = new ArrayList<Pair<String, String>>();
		pairsList.add(new Pair<String, String>("method", "flickr.interestingness.getList"));
		pairsList.add(new Pair<String, String>("page", String.valueOf(mPage)));
		pairsList.add(new Pair<String, String>("per_page", "12"));
		pairsList.add(new Pair<String, String>("nojsoncallback", "1"));
		pairsList.add(new Pair<String, String>("format", "json"));
		return pairsList;
	}

	/**
	 * Parse the response that was returned from the API.
	 */
	@Override
	public StatusTypes parseResponse(Context context, String response) {
		try {
			// Parse the response from the flickr API.
			JSONObject responseJSON = new JSONObject(response);
			JSONObject photos = responseJSON.getJSONObject("photos");
			JSONArray photosArray = photos.getJSONArray("photo");

			// List that contains the result of the data.
			mResultPhotosList = new ArrayList<FlickrPhoto>();
			int failedCounter = 0;
			for (int i = 0; i < photosArray.length(); i++) {
				try {
					// Parse the photo and add it to the list of photos.
					FlickrPhoto flickrPhoto = parsePhoto(photosArray.getJSONObject(i));
					mResultPhotosList.add(flickrPhoto);
				} catch (Exception e) {
					failedCounter++;
					Log.e(Constants.TAG, "Failed in parsing photo");
				}
			}

			// If parsing all the photos failed then return parsing error status.
			if (failedCounter == photosArray.length())
				return StatusTypes.PARSING_ERROR;

			return StatusTypes.SUCCESS;
		} catch (Exception e) {
			Log.e(Constants.TAG, "Error in parsing the response and saving it with Exception " + e.getMessage());
			return StatusTypes.PARSING_ERROR;
		}
	}

	/**
	 * Parse a single photo from the json object. 
	 * 
	 * @param photoObject
	 * @return
	 * @throws JSONException
	 */
	private FlickrPhoto parsePhoto(JSONObject photoObject) throws JSONException {
		// A Photo is generated from multiple parts as shown in the below
		// example:
		// https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
		String photoURLFormate = "https://farm%s.staticflickr.com/%s/%s_%s.jpg";

		String farmId = photoObject.getString("farm");
		String serverId = photoObject.getString("server");
		String photoId = photoObject.getString("id");
		String secret = photoObject.getString("secret");
		String title = photoObject.getString("title");

		String photoUrl = String.format(photoURLFormate, farmId, serverId, photoId, secret);
		return new FlickrPhoto(photoUrl, title, mPage);
	}
}
