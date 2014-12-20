package com.sample.flickr.ui.activities;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.sample.flickr.R;
import com.sample.flickr.client.ServerHandler;
import com.sample.flickr.client.ServerHandler.OnFinishedListener;
import com.sample.flickr.client.entities.FlickrExploreListInfo;
import com.sample.flickr.client.entities.BaseInfo.StatusTypes;
import com.sample.flickr.ui.adapters.FlickrAdapter;
import com.sample.flickr.ui.adapters.InfiniteScrollAdapter;
import com.sample.flickr.ui.entities.FlickrPhoto;
import com.sample.flickr.utils.Constants;
import com.sample.flickr.utils.Utils;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class MainActivity extends Activity implements InfiniteScrollAdapter.InfiniteScrollListener,
		OnItemClickListener, OnRefreshListener {
	// Variable views to be used in this activity.
	private SwipeRefreshLayout mPullToRefreshLayout;
	private GridView mGridView;
	private InfiniteScrollAdapter<FlickrAdapter> mAdapter;

	// Grid size constants.
	private static final int GRID_ITEM_HEIGHT = 165;
	private static final int GRID_ITEM_WIDTH = 165;

	// The current API page, it is set to 1 to get the first page in our first
	// request.
	private int mCurrentPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set the action bar background color.
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.action_bar_background_color))));

		// Setup the action bar with pull to refresh layout
		mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mPullToRefreshLayout.setOnRefreshListener(this);
		mPullToRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mPullToRefreshLayout.setRefreshing(true);

		mGridView = (GridView) findViewById(R.id.flickr_gridview);
		mGridView.setOnItemClickListener(this);
		// Make GridView use your custom selector drawable
		mGridView.setDrawSelectorOnTop(true);
		mGridView.setSelector(getResources().getDrawable(R.drawable.list_selector));

		// Handle the first run update.
		handleInitialUpdate();
	}

	@Override
	public void onRefresh() {
		// On the pull to refresh started call the API with the initial page and
		// reload the gridview.
		handleInitialUpdate();
	}

	@Override
	public void onInfiniteScrolled() {
		// Check for the connectivity before doing requests.
		if (!Utils.isConnected(this)) {
			Toast.makeText(this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
			mPullToRefreshLayout.setRefreshing(false);
			mAdapter.handledRefresh();
			return;
		}
		
		// Call API with the next page.
		FlickrExploreListInfo info = new FlickrExploreListInfo(++mCurrentPage);
		ServerHandler handler = new ServerHandler(this, info, new OnFinishedListener() {
			@Override
			public void onFinished(StatusTypes status, ArrayList<FlickrPhoto> flickrPhotosList) {
				Log.e(Constants.TAG, "status :" + status);

				// Handle the loading of the gridview in case of success.
				if (status == StatusTypes.SUCCESS) {
					mAdapter.getAdapter().addFlickrPhotos(flickrPhotosList);
					mAdapter.handledRefresh();
				} else {
					Toast.makeText(getApplicationContext(), "API request failed with status " + status,
							Toast.LENGTH_SHORT).show();
				}

				// Notify PullToRefreshLayout that the refresh has finished.
				mPullToRefreshLayout.setRefreshing(false);
			}
		});
		handler.start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Handle the click of the gridview items.
		if (arg0.getAdapter() instanceof SwingBottomInAnimationAdapter) {
			SwingBottomInAnimationAdapter adapter = (SwingBottomInAnimationAdapter) arg0.getAdapter();
			if (adapter.getItem(arg2) instanceof FlickrPhoto) {
				FlickrPhoto photo = (FlickrPhoto) adapter.getItem(arg2);
				Toast.makeText(this, "Clicked " + photo.getImageTitle(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Initiate the API request and handle the loading of the gridview.
	 */
	private void handleInitialUpdate() {
		// Check for the connectivity before doing requests.
		if (!Utils.isConnected(this)) {
			Toast.makeText(this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
			mPullToRefreshLayout.setRefreshing(false);
			return;
		}

		// Reset the current page to be the initial page.
		mCurrentPage = 1;

		// Call API with the initial page.
		FlickrExploreListInfo info = new FlickrExploreListInfo(mCurrentPage);
		ServerHandler handler = new ServerHandler(this, info, new OnFinishedListener() {
			@Override
			public void onFinished(StatusTypes status, ArrayList<FlickrPhoto> flickrPhotosList) {
				Log.e(Constants.TAG, "API status is " + status);

				// Handle the loading of the gridview in case of success.
				if (status == StatusTypes.SUCCESS) {
					mAdapter = new InfiniteScrollAdapter<FlickrAdapter>(MainActivity.this, new FlickrAdapter(
							MainActivity.this, flickrPhotosList), GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT);
					mAdapter.addListener(MainActivity.this);

					SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
							mAdapter);
					swingBottomInAnimationAdapter.setAbsListView(mGridView);
					swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

					mGridView.setAdapter(swingBottomInAnimationAdapter);
				} else {
					Toast.makeText(getApplicationContext(), "API request failed with status " + status,
							Toast.LENGTH_SHORT).show();
				}

				// Notify PullToRefreshLayout that the refresh has finished.
				mPullToRefreshLayout.setRefreshing(false);
			}
		});
		handler.start();
	}
}
