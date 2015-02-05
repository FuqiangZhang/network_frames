package net.httpstacks;

import net.base.Request;
import net.base.Response;

public interface HttpStack {
	/**
	 * execute the network request
	 * @param request
	 * @return response
	 * */
	public Response performRequest(Request<?> request);  
}
