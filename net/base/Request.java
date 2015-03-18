package net.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class Request<T> implements Comparable<Request<T>> {

	/**
	 * Request method enum. Here we only have GET, POST, PUT, DELETE
	 * */
	public static enum HttpMethod {
		GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

		private String mHttpMethod = "";

		private HttpMethod(String method) {
			this.mHttpMethod = method;
		}

		@Override
		public String toString() {
			return this.mHttpMethod;
		}
	}

	/**
	 * the enum of priority
	 * */
	public static enum Priority {
		LOW, NORMAL, HIGH, IMMEDIATE
	}

	/**
	 * default encoding for POST or GET.
	 * */
	private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
	
	/**
	 * Default Content-type
	 */
	public final static String HEADER_CONTENT_TYPE = "Content-Type";
	
	/**
	 * serial number of the request
	 * */
	protected int mSerialNum = 0;

	/**
	 * set the default priority to normal.
	 * */
	protected Priority mPriority = Priority.NORMAL;

	/**
	 * set whether the request is cancle.
	 * */
	protected boolean isCancle = false;

	/**
	 * set whether should be cached.
	 * */
	private boolean mSholudCache = true;

	/**
	 * request URL
	 * */
	private String mUrl = "";

	/**
	 * the request method
	 * */
	HttpMethod mHttpMethod = HttpMethod.GET;

	/**
	 * request header
	 * */
	private Map<String, String> mHeaders = new HashMap<String, String>();

	/**
	 * request body
	 * */
	private Map<String, String> mBodyParams = new HashMap<String, String>();

	/**
	 * request listener
	 * */
	protected RequestListener<T> mRequestListener;

	public Request(HttpMethod method, String url, RequestListener<T> listener) {
		this.mHttpMethod = method;
		this.mUrl = url;
		this.mRequestListener = listener;
	}

	/**
	 * parse the response
	 * */
	public abstract T parseResponse(Response response);

	/**
	 * handle the response
	 * 
	 * called in UI
	 * */
	public final void deliveryResponse(Response response) {
		T result = parseResponse(response);
		if (mRequestListener != null) {
			int stCode = response != null ? response.getStatusCode() : -1;
			String msg = response != null ? response.getMessage()
					: "Unknown error";
			mRequestListener.onComplete(stCode,result, msg);
		}
	}

	public int getSerialNum() {
		return mSerialNum;
	}

	public void setSerialNum(int mSerialNum) {
		this.mSerialNum = mSerialNum;
	}

	public Priority getPriority() {
		return mPriority;
	}

	public void setPriority(Priority mPriority) {
		this.mPriority = mPriority;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public String getBodyContentType() {
		return "application/x-www-form-urlencoded; charset="
				+ getParamsEncoding();
	}

	protected String getParamsEncoding() {
		return DEFAULT_PARAMS_ENCODING;
	}

	public HttpMethod getHttpMethod() {
		return this.mHttpMethod;
	}

	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	public Map<String, String> getParams() {
		return mBodyParams;
	}

	public void cancel() {
		isCancle = true;
	}

	public boolean isCanceled() {
		return isCancle;
	}

	public byte[] getBody() {
		Map<String, String> params = getParams();
		if (params != null && params.size() > 0) {
			return encodeParameters(params, getParamsEncoding());
		}
		return null;
	}
	
	public boolean isHttps() {
        return mUrl.startsWith("https");
    }

	private byte[] encodeParameters(Map<String, String> params,
			String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(),
						paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(),
						paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: "
					+ paramsEncoding, uee);
		}
	}

	/**
	 * sort the request by the priority
	 * */
	@Override
	public int compareTo(Request<T> another) {
		Priority myPriority = this.getPriority();
		Priority anotherPriority = another.getPriority();
		// if the priority are same, then execute them by serial number
		return myPriority.equals(another) ? this.getSerialNum()
				- another.getSerialNum() : myPriority.ordinal()
				- anotherPriority.ordinal();
	}

	public boolean shouldCache() {
		return mSholudCache;
	}

	public void setSholudCache(boolean mSholudCache) {
		this.mSholudCache = mSholudCache;
	}

	/**
	 * request listener, called in UI.
	 * */
	public static interface RequestListener<T> {
		public void onComplete(int stCode, T result, String errMsg);
	}
	
	
}
