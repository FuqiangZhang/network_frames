package test;

import net.base.Request;
import net.base.Request.RequestListener;
import net.base.StringRequest;
import net.core.RequestQueue;
import net.core.SimpleNet;

public class MainTest {
	public static void main(String[] args) {
		final RequestQueue mrq = SimpleNet.newRequestQueue();
		final String url = "https://www.baidu0.com";
		mrq.start();
		boolean flag = true;
		while (flag) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Request<String> r = new StringRequest(Request.HttpMethod.GET, url,
					new RequestListener<String>() {
						@Override
						public void onComplete(int stCode, String result,
								String errMsg) {
							System.out.println(errMsg);
						}
					});
			r.setSholudCache(true);
			mrq.addRequest(r);
		}
	}
}
