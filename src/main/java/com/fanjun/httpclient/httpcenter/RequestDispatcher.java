package com.fanjun.httpclient.httpcenter;


import com.fanjun.httpclient.interceptor.RequestInterceptor;

import java.util.concurrent.BlockingQueue;


/**
 * 请求执行
 * @author Administrator
 *
 */
public class RequestDispatcher extends Thread {
	private BlockingQueue<Request> requests = null;
	private Config config = null;
	private HttpClient httpUtils;
	public RequestDispatcher(BlockingQueue<Request> requests, HttpClient httpUtils) {
		super();
		this.requests = requests;
		this.httpUtils = httpUtils;
	}
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Request request = requests.take();
				if (request.isStop()) {
					continue;
				}
				if (config != null & config.getRequestInterceptors() != null) {
					for (RequestInterceptor requestInterceptor: config.getRequestInterceptors()) {
						Request request2 = requestInterceptor.paramsMachining(request);
						if(request2 != null) {
							request = request2;
						}
					}
				}
				if (request == null) {
					continue;
				}
				switch (request.getRequestType()) {
				case Request.POST:
					httpUtils.doPost(request);
					break;
				case Request.GET:
					httpUtils.doGet(request);
					break;
				case Request.UPLOAD:
					httpUtils.doUpload(request);
					break;
				case Request.DOWNLOAD:
					httpUtils.doDownload(request);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	
	
}
