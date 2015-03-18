package net.core;

import net.httpstacks.HttpStack;

public final class SimpleNet {
	/**
	 * create a request queue
	 * 
	 * @return
	 */
	public static RequestQueue newRequestQueue() {
		return newRequestQueue(RequestQueue.DEFAULT_CORE_NUMS);
	}

	/**
	 * Create a request queue, and the number of NetworkExecutor set to coreNums
	 * 
	 * @param coreNums
	 * @return
	 */
	public static RequestQueue newRequestQueue(int coreNums) {
		return newRequestQueue(coreNums, null);
	}

	/**
	 * Create a request queue, and the number of NetworkExecutor set to coreNums
	 * and set the specific HttpStack
	 * *
	 * 
	 * @param coreNums
	 *            Threads number
	 * @param httpStack
	 *            
	 * @return
	 */
	public static RequestQueue newRequestQueue(int coreNums, HttpStack httpStack) {
		RequestQueue queue = new RequestQueue(Math.max(0, coreNums), httpStack);
		queue.start();
		return queue;
	}
}
