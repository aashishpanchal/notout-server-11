package com.choic11.model.response;

public class ContestBaseResponse extends BaseResponse {
	Object practice;
	Object detail;
	Object beat_the_expert;
	Object match_data;
	int favorite_contest_count;
	public ContestBaseResponse(int code, boolean error, String message, Object data, Object practice, Object detail, Object beat_the_expert, Object match_data,int totalFavoriteContestCount) {
		super(code, error, message, data);
		this.practice=practice;
		this.detail=detail;
		this.beat_the_expert=beat_the_expert;
		this.match_data=match_data;
		this.favorite_contest_count= totalFavoriteContestCount;
	}

	public Object getPractice() {
		return practice;
	}

	public void setPractice(Object practice) {
		this.practice = practice;
	}

	public Object getDetail() {
		return detail;
	}

	public void setDetail(Object detail) {
		this.detail = detail;
	}

	public Object getBeat_the_expert() {
		return beat_the_expert;
	}

	public void setBeat_the_expert(Object beat_the_expert) {
		this.beat_the_expert = beat_the_expert;
	}

	public Object getMatch_data() {
		return match_data;
	}

	public void setMatch_data(Object match_data) {
		this.match_data = match_data;
	}

    public int getFavorite_contest_count() {
        return favorite_contest_count;
    }

    public void setFavorite_contest_count(int favorite_contest_count) {
        this.favorite_contest_count = favorite_contest_count;
    }
}
