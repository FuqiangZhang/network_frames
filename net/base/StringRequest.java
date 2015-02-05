package net.base;

public class StringRequest extends Request<String>{

	public StringRequest(HttpMethod method, String url, RequestListener<String> listener) {
		super(method, url, listener);
	}


	@Override
	public String parseResponse(Response response) {
		if(response == null)
			return "";
		return new String(response.getRawData());
	}
}
