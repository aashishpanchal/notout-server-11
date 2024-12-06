package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_basketball_leaderboard_series database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_leaderboard_series")
@NamedQuery(name="TblBasketballLeaderboardSeries.findAll", query="SELECT t FROM TblBasketballLeaderboardSeries t")
public class TblBasketballLeaderboardSeries implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="new_point")
	private float newPoint;

	@Column(name="new_rank")
	private int newRank;

	@Column(name="old_point")
	private float oldPoint;

	@Column(name="old_rank")
	private int oldRank;

	@Column(name="series_id")
	private int seriesId;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	public TblBasketballLeaderboardSeries() {
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

	public float getOldPoint() {
		return this.oldPoint;
	}

	public void setOldPoint(float oldPoint) {
		this.oldPoint = oldPoint;
	}

	public int getOldRank() {
		return this.oldRank;
	}

	public void setOldRank(int oldRank) {
		this.oldRank = oldRank;
	}

	public int getSeriesId() {
		return this.seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public BigInteger getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

}