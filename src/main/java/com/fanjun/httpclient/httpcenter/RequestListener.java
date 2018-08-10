package com.fanjun.httpclient.httpcenter;

import java.io.File;

/**
 * 请求助手
 * @author Administrator
 *
 */
public abstract class RequestListener<T extends Response>{
	public abstract void response(T response);
	public void download(long current, long size, File file){};
	public void upload(long current, long size, File file) { }
}
