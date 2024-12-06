package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_leaderboard_matches database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_leaderboard_matches")
@NamedQuery(name="TblSoccerLeaderboardMatch.findAll", query="SELECT t FROM TblSoccerLeaderboardMatch t")
public class TblSoccerLeaderboardMatch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="customer_team_id")
	private int customerTeamId;

	@Column(name="series_id")
	private int seriesId;
	
	@Column(name="match_id")
	private int matchId;

	@Column(name="match_unique_id")
	private int matchUniqueId;
	
	@Column(name="match_date")
	private BigInteger matchDate;
	
	@Column(name="week_no")
	private String weekNo;

	@Column(name="new_point")
	private float newPoint;

	@Column(name="new_rank")
	private int newRank;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	public TblSoccerLeaderboardMatch() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getCustomerTeamId() {
		return this.customerTeamId;
	}

	public void setCustomerTeamId(int customerTeamId) {
		this.customerTeamId = customerTeamId;
	}
	
	public int getSeriesId() {
		return seriesId;
	}
	
	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public int getMatchId() {
		return this.matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}
	
	public BigInteger getMatchDate() {
		return matchDate;
	}
	
	public void setMatchDate(BigInteger matchDate) {
		this.matchDate = matchDate;
	}
	
	public String getWeekNo() {
		return weekNo;
	}
	
	public void setWeekNo(String weekNo) {
		this.weekNo = weekNo;
	}

	public float getNewPoint() {
		return this.newPoint;
	}

	public void setNewPoint(float newPoint) {
		this.newPoint = newPoint;
	}

	public int getNewRank() {
		return this.newRank;
	}

	public void setNewRank(int newRank) {
		this.newRank = newRank;
	}

	public BigInteger getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

}