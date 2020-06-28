package com.fanjun.httpclient.httpcenter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Response {
	/**
	 * 返回数据
	 */
	private String result;
	/**
	 * 返回码
	 */
	private int code;
	/**
	 * 返回的header
	 */
	private Map<String,List<String>> headers;
	/**
	 * 错误消息
	 */
	private String errorMsg;
	/**
	 * 下载的文件
	 */
	private File downloadFile;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public File getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(File downloadFile) {
		this.downloadFile = downloadFile;
	}
}
