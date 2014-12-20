package com.sample.flickr.client;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.sample.flickr.client.entities.BaseInfo;
import com.sample.flickr.client.entities.BaseInfo.StatusTypes;
import com.sample.flickr.utils.Constants;

public class ServerParser {

	/**
	 * The response received from the server.
	 * 
	 * @param context
	 * @param type
	 * @param response
	 * @param responseStatus
	 * @return
	 */
	public StatusTypes parse(Context context, BaseInfo info, String response, int responseStatus) {
		// Handle the success and failure of the server.
		if (responseStatus == 200) {
			return info.parseResponse(context, response);
		} else {
			return parseErrorMessage(response);
		}
	}

	/**
	 * Handle the error message response.
	 * 
	 * @param response
	 * @return
	 */
	private StatusTypes parseErrorMessage(String response) {
		try {
			JSONObject errorResponse = new JSONObject(response);
			String errorCode = errorResponse.getString("code");
			if ("100".equals(errorCode)) {
				return StatusTypes.INVALIDE_API_KEY;
			} else if ("105".equals(errorCode)) {
				return StatusTypes.SERVICE_UNAVAILABLE;
			} else if ("116".equals(errorCode)) {
				return StatusTypes.BAD_URL;
			}

			return StatusTypes.UNKOWN_ERROR;
		} catch (Exception e) {
			Log.e(Constants.TAG, "Error in parsing the error response with Exception " + e.getMessage());
			return StatusTypes.UNKOWN_ERROR;
		}
	}
}