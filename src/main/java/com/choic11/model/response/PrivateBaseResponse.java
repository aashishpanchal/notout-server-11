package com.choic11.model.response;

public class PrivateBaseResponse extends BaseResponse {
	Object match_detail;
	Object winner_breakup;
	int favorite_contest_count;
	public PrivateBaseResponse(int code, boolean error, String message, Object data, Object match_detail, Object winner_breakup,int totalFavoriteContestCount) {
		super(code, error, message, data);
		this.match_detail=match_detail;
		this.winner_breakup=winner_breakup;
		this.favorite_contest_count = totalFavoriteContestCount;
	}
	
	public Object getMatch_detail() {
		return match_detail;
	}
	
	public void setMatch_detail(Object match_detail) {
		this.match_detail = match_detail;
	}
	
	public Object getWinner_breakup() {
		return winner_breakup;
	}
	
	public void setWinner_breakup(Object winner_breakup) {
		this.winner_breakup = winner_breakup;
	}

	public int getFavorite_contest_count() {
		return favorite_contest_count;
	}

	public void setFavorite_contest_count(int favorite_contest_count) {
		this.favorite_contest_count = favorite_contest_count;
	}
}
