package net.cache;

import java.util.HashMap;
import java.util.Map;

import net.base.Response;

public class MyCache implements Cache<String,Response> {
	
	private Map<String,Response> mCache = new HashMap<String,Response>();
	
	@Override
	public Response get(String key) {
		return mCache.get(key);
	}

	@Override
	public void put(String key, Response value) {
		mCache.put(key,value);
	}

	@Override
	public void remove(String key) {
		mCache.remove(key);
	}
}
