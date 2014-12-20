package com.sample.flickr.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.sample.flickr.client.ServerCommunicationTypes.RequestType;
import com.sample.flickr.client.entities.BaseInfo;
import com.sample.flickr.client.entities.BaseInfo.StatusTypes;
import com.sample.flickr.ui.entities.FlickrPhoto;
import com.sample.flickr.utils.Constants;

/**
 * Class that handles sending server requests in a separate thread and
 * communicating with the parser and returning the result to the calling
 * component.
 * 
 * @author hzaied
 */
public class ServerHandler implements Runnable {
	private static final String API_KEY = "5f412b6f21aa9b6978c979c1ca806375";

	private static final String API_HOST_URL = "https://api.flickr.com";
	private static final String API_KEY_PARAM = "?api_key=" + API_KEY + "&";

	private BaseInfo mBaseInfo;

	private String mResponse;
	private int mResponseStatus;
	private StatusTypes mHandlerStatus;

	private Context mContext;
	private Handler mHandler;
	private OnFinishedListener mListener;

	public ServerHandler(Context context, BaseInfo baseInfo, OnFinishedListener listener) {
		mBaseInfo = baseInfo;
		mListener = listener;
		mContext = context;
	}

	/**
	 * Initialize and start the thread and handle the on finish callback.
	 */
	public void start() {
		mHandler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message arg0) {
				// Notify the calling component with the finish status and result.
				if (mListener != null) {
					mListener.onFinished(mHandlerStatus, mBaseInfo.mResultPhotosList);
				}
				return false;
			}
		});
		new Thread(this).start();
	}

	@Override
	public void run() {
		// Send the requests.
		if (mBaseInfo.mCommType.mType == RequestType.GET) {
			sendGetRequest();
		} else {
			sendPostRequest();
		}

		// Handle parsing of the response.
		ServerParser parser = new ServerParser();
		mHandlerStatus = parser.parse(mContext, mBaseInfo, mResponse, mResponseStatus);
		// Notify the calling component with the finish status and result
		mHandler.sendEmptyMessage(0);
	}

	/**
	 * Handle sending the post request.
	 * 
	 * @return
	 */
	public boolean sendPostRequest() {
		// Handle the Post request if needed.
		return false;
	}

	/**
	 * Handle sending the GET request.
	 * 
	 * @return
	 */
	public boolean sendGetRequest() {
		// Create a new HttpClient.
		HttpClient httpclient = new DefaultHttpClient();

		String url = API_HOST_URL + mBaseInfo.mCommType.mURLPath;

		InputStream instream = null;
		try {
			// Add the GET parameters to the request.
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			ArrayList<Pair<String, String>> pair = mBaseInfo.getKeysValuePairs();

			for (int i = 0; i < pair.size(); i++) {
				if ("".equals(pair.get(i).first)) {
					url += pair.get(i).second;
				} else {
					nameValuePairs.add(new BasicNameValuePair(pair.get(i).first, pair.get(i).second));
				}
			}
			String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
			url += API_KEY_PARAM + paramsString;
			HttpGet httpget = new HttpGet(url);
			Log.v(Constants.TAG, "Request: " + url);

			HttpParams httpParameters = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 30 * 1000);
			HttpConnectionParams.setSoTimeout(httpParameters, 60 * 1000);

			// Execute HTTP GET Request
			HttpResponse httpResponse = httpclient.execute(httpget);
			mResponseStatus = httpResponse.getStatusLine().getStatusCode();
			Log.v(Constants.TAG, "Server status: " + mResponseStatus);

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				instream = entity.getContent();
				// Read all the text returned by the server.
				BufferedReader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

				// Append all the response in the StringBuilder and return it.
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					stringBuilder.append(line + "\n");
				}
				in.close();

				mResponse = stringBuilder.toString();

				Log.v(Constants.TAG, "Server response: " + mResponse);

				// Closing the input stream will trigger connection release
				instream.close();
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				instream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Interface to handle the result and status.
	 */
	public interface OnFinishedListener {
		void onFinished(StatusTypes status, ArrayList<FlickrPhoto> flickrPhotosList);
	}
}
