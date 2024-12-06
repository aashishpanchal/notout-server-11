package com.choic11.model.response;

public class SeriesLeaderBoardResponse extends BaseResponse {
	Object data_self;

	public SeriesLeaderBoardResponse(int code, boolean error, String message, Object data, Object data_self) {
		super(code, error, message, data);
		this.data_self = data_self;
	}

	public Object getData_self() {
		return data_self;
	}

	public void setData_self(Object data_self) {
		this.data_self = data_self;
	}

}
