package com.sample.flickr.ui.entities;

public class FlickrPhoto {
	private String mImageUrl;
	private String mImageTitle;
	private int mPage;

	public FlickrPhoto(String imageUrl, String imageTitle, int page) {
		super();
		mImageUrl = imageUrl;
		mImageTitle = imageTitle;
		mPage = page;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}

	public String getImageTitle() {
		return mImageTitle;
	}

	public void setImageTitle(String imageTitle) {
		mImageTitle = imageTitle;
	}

	public int getPage() {
		return mPage;
	}

	public void setPage(int page) {
		mPage = page;
	}

}
