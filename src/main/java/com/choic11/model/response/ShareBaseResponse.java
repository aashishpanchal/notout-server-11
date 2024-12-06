package com.choic11.model.response;

public class ShareBaseResponse extends BaseResponse {
	String image;
	public ShareBaseResponse(int code, boolean error, String message, Object data, String image) {
		super(code, error, message, data);
		this.image=image;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}

}
