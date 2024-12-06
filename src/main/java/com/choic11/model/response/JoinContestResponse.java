package com.choic11.model.response;

public class JoinContestResponse extends BaseResponse {
	int match_contest_id;
	public JoinContestResponse(int code, boolean error, String message, Object data, int match_contest_id) {
		super(code, error, message, data);
		this.match_contest_id=match_contest_id;
	}
	public int getMatch_contest_id() {
		return match_contest_id;
	}
	public void setMatch_contest_id(int match_contest_id) {
		this.match_contest_id = match_contest_id;
	}
	


}
