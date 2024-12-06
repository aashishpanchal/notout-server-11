package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_basketball_customer_contest_teams database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_customer_contest_teams")
@NamedQuery(name="TblBasketballCustomerContestTeams.findAll", query="SELECT t FROM TblBasketballCustomerContestTeams t")
public class TblBasketballCustomerContestTeams implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="customer_id")
	private int customerId;

	@Lob
	@Column(name="customer_team_ids")
	private String customerTeamIds;

	@Lob
	@Column(name="customer_team_names")
	private String customerTeamNames;

	@Column(name="match_contest_id")
	private int matchContestId;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	public TblBasketballCustomerContestTeams() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerTeamIds() {
		return this.customerTeamIds;
	}

	public void setCustomerTeamIds(String customerTeamIds) {
		this.customerTeamIds = customerTeamIds;
	}

	public String getCustomerTeamNames() {
		return this.customerTeamNames;
	}

	public void setCustomerTeamNames(String customerTeamNames) {
		this.customerTeamNames = customerTeamNames;
	}

	public int getMatchContestId() {
		return this.matchContestId;
	}

	public void setMatchContestId(int matchContestId) {
		this.matchContestId = matchContestId;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

}