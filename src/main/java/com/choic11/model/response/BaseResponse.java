package com.choic11.model.response;

import com.choic11.Util;

public class BaseResponse {

	int code;
	boolean error;
	String message;
	Object data;
	Long server_date;
	
	public BaseResponse(int code, boolean error, String message, Object data) {
		this.code=code;
		this.error=error;
		this.message=message;
		this.data=data;
		this.server_date= Util.getCurrentTime();
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public Long getServer_date() {
		return server_date;
	}
	
	public void setServer_date(Long server_date) {
		this.server_date = server_date;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	
	
}
