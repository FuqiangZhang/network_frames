package net.httpstacks;

public class HttpStackFactory {

	public static HttpStack createHttpStack() {
		return new HttpUrlConnStack();
	}
}
