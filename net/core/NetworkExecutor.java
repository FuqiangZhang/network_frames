package net.core;

import java.util.concurrent.BlockingQueue;

import net.base.Request;
import net.base.Response;
import net.cache.Cache;
import net.cache.MyCache;
import net.httpstacks.HttpStack;

public final class NetworkExecutor extends Thread {

	/**
	 * request queue
	 * */
	private BlockingQueue<Request<?>> mRequestQueue;

	/**
	 * httpstack the real executor
	 * */
	private HttpStack mHttpStack;

	/**
	 * request cache
	 * */
	private static Cache<String, Response> mReqCache = new MyCache();

	public NetworkExecutor(BlockingQueue<Request<?>> mRequestQueue,
			HttpStack mHttpStack) {
		this.mRequestQueue = mRequestQueue;
		this.mHttpStack = mHttpStack;
	}

	/**
	 * whether has been stopped
	 * */
	private boolean isStop = false;

	/**
	 * start the task
	 * */
	@Override
	public void run() {
		while (!isStop) {
			try {
				final Request<?> mRequest = mRequestQueue.take();
				// the request has been canceled
				if (mRequest.isCanceled()) {
					continue;
				}

				Response response = null;
				if (isUseCache(mRequest)) {
					// get from the cache
					System.err.println("in cache");
					response = mReqCache.get(mRequest.getUrl());
				} else {
					// get from the Internet
					System.out.println("not in cache");
					response = mHttpStack.performRequest(mRequest);
//					if(response == null){
//						continue;
//					}
					// if the request need to be cache, then put the response in to the cache
					if (response !=null && mRequest.shouldCache() && isSuccess(response)) {
						System.err.println("put into cache");
						mReqCache.put(mRequest.getUrl(), response);
					}
				}
				mRequest.deliveryResponse(response);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	/**
	 * check whether the request has been execute successfully, and get the
	 * successful response
	 * */
	private boolean isSuccess(Response response) {
		return response != null && response.getStatusCode() == 200;
	}

	/**
	 * 
	 * */
	private boolean isUseCache(Request<?> request) {
		return request.shouldCache()
				&& mReqCache.get(request.getUrl()) != null;
	}

	/**
	 * quit the executor
	 * */
	public void quit() {
		isStop = true;
		this.interrupt();
	}
}
