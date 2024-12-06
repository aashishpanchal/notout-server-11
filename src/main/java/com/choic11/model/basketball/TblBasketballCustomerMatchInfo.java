package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_basketball_affiliates database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_customer_match_info")
@NamedQuery(name="TblBasketballCustomerMatchInfo.findAll", query="SELECT t FROM TblBasketballCustomerMatchInfo t")
public class TblBasketballCustomerMatchInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="contest_count")
	private int contestCount;

	@Column(name="ab_contest_count")
	private int abContestCount;

	@Column(name="team_count")
	private int teamCount;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatchUniqueId() {
		return matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getContestCount() {
		return contestCount;
	}

	public void setContestCount(int contestCount) {
		this.contestCount = contestCount;
	}

	public int getAbContestCount() {
		return abContestCount;
	}

	public void setAbContestCount(int abContestCount) {
		this.abContestCount = abContestCount;
	}

	public int getTeamCount() {
		return teamCount;
	}

	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}
}