package com.sample.flickr.client;

/**
 * Enum to handle the different types of server communication. It contains a
 * RequestType that differentiates between the GET and POST (currently the POST
 * request is not implemented). It also contains the path of the API request.
 * 
 * @author hzaied
 */
public enum ServerCommunicationTypes {
	// Add other types if needed.
	GET_FLICKR_EXPLORE_LIST(RequestType.GET, "/services/rest/");

	public RequestType mType;
	public String mURLPath;

	private ServerCommunicationTypes(RequestType type, String URLPath) {
		this.mType = type;
		this.mURLPath = URLPath;
	}

	enum RequestType {
		POST, GET
	}
}
