package com.sample.flickr.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.sample.flickr.R;
import com.sample.flickr.ui.entities.FlickrPhoto;

public class FlickrAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<FlickrPhoto> mFlickrPhotosList;
	private AQuery mAQuery;

	public FlickrAdapter(Context context, ArrayList<FlickrPhoto> flickrPhotosList) {
		mFlickrPhotosList = flickrPhotosList;
		mInflater = LayoutInflater.from(context);
		mAQuery = new AQuery(context);
	}
	
	/**
	 * Add a new set of photos to the current list.
	 * 
	 * @param flickrPhotosList
	 */
	public void addFlickrPhotos(ArrayList<FlickrPhoto> flickrPhotosList) {
		mFlickrPhotosList.addAll(flickrPhotosList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFlickrPhotosList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFlickrPhotosList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = new ViewHolder();

		if (view == null) {
			view = mInflater.inflate(R.layout.flickr_photo_list_item, null);

			holder.mFlickrPhoto = (ImageView) view.findViewById(R.id.flickr_image);
			holder.mPhotoTitle = (TextView) view.findViewById(R.id.image_title_textview);

			view.setTag(holder);
		} else
			holder = (ViewHolder) view.getTag();

		if (mFlickrPhotosList.get(position) != null) {
			String url = mFlickrPhotosList.get(position).getImageUrl();

			// Download and load image.
	        mAQuery = new AQuery(view);
	        mAQuery.id(holder.mFlickrPhoto).image(url, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f);
	        
			holder.mPhotoTitle.setText(mFlickrPhotosList.get(position).getImageTitle());
		}

		return view;
	}

	static class ViewHolder {
		ImageView mFlickrPhoto;
		TextView mPhotoTitle;
	}

}