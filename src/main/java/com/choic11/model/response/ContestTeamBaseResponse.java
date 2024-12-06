package com.choic11.model.response;

public class ContestTeamBaseResponse extends BaseResponse {
	
	int total_teams;
	Object admin_data;
	public ContestTeamBaseResponse(int code, boolean error, String message, Object data, int total_teams, Object admin_data) {
		super(code, error, message, data);
		this.total_teams=total_teams;
		this.admin_data=admin_data;
	}
	
	public Object getAdmin_data() {
		return admin_data;
	}
	public void setAdmin_data(Object admin_data) {
		this.admin_data = admin_data;
	}
	public int getTotal_teams() {
		return total_teams;
	}
	
	public void setTotal_teams(int total_teams) {
		this.total_teams = total_teams;
	}

}
