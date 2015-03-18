package net.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.base.Request;
import net.httpstacks.HttpStack;
import net.httpstacks.HttpStackFactory;

public final class RequestQueue {

	/**
	 * request queue thread safe
	 * */
	private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<Request<?>>();

	/**
	 * request serial number generator
	 * */
	private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

	/**
	 * default core number
	 * */
	public static int DEFAULT_CORE_NUMS = Runtime.getRuntime()
			.availableProcessors() + 1;

	/**
	 * number of dispatcher
	 * */
	private int mDispatcherNums = DEFAULT_CORE_NUMS;

	/**
	 * threads of network executor
	 * */
	private NetworkExecutor[] mDispatchers = null;

	/**
	 * the real executor of network request
	 * */
	private HttpStack mHttpStack;

	/**
	 * @param coreNums
	 *            : the number of thread cores
	 * */
	protected RequestQueue(int coreNums, HttpStack httpStack) {
		mDispatcherNums = coreNums;
		mHttpStack = httpStack != null ? httpStack : HttpStackFactory
				.createHttpStack();
	}

	private final void startNetworkExecutors() {
		mDispatchers = new NetworkExecutor[mDispatcherNums];
		for (int i = 0; i < mDispatcherNums; i++) {
			mDispatchers[i] = new NetworkExecutor(mRequestQueue, mHttpStack);
			mDispatchers[i].start();
		}
	}

	/**
	 * start
	 * */
	public void start() {
		stop();
		startNetworkExecutors();
	}

	/**
	 * stop
	 * */
	public void stop() {
		if (mDispatchers != null && mDispatchers.length > 0) {
			for (int i = 0; i < mDispatchers.length; i++) {
				mDispatchers[i].quit();
			}
		}
	}

	/**
	 * add a request
	 * */
	public void addRequest(Request<?> request) {
		if (!mRequestQueue.contains(request)) {
			request.setSerialNum(this.generateSerialNumber());
			mRequestQueue.add(request);
		} else {
			System.err.println("Already in the queue!");
		}
	}

	/**
	 * get all the requests
	 * */
	public BlockingQueue<Request<?>> getAllRequests() {
		return mRequestQueue;
	}
	
	/**
	 * clear all
	 * */
	public void clear() {
		mRequestQueue.clear();
	}

	/**
	 * generate a serial number for the request
	 * */
	private int generateSerialNumber() {
		return mSerialNumGenerator.incrementAndGet();
	}
}
