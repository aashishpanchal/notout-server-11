package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="tbl_affiliate_leaderboard_result")
@NamedQuery(name="TblAffiliateLeaderboardResult.findAll", query="SELECT t FROM TblAffiliateLeaderboardResult t")
public class TblAffiliateLeaderboardResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="leaderboard_id")
	private BigInteger leaderboardId;

	@Column(name="customer_id")
	private BigInteger customerId;

	@Column(name="total_amount")
	private float totalAmount;

	@Column(name="new_amount")
	private float newAmount;

	@Column(name="old_amount")
	private float oldAmount;

	@Column(name="new_rank")
	private int newRank;

	@Column(name="old_rank")
	private int oldRank;

	@Column(name = "win_amount")
	private float winAmount;

	@Column(name = "win_gadget")
	private String winGadget;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getLeaderboardId() {
		return leaderboardId;
	}

	public void setLeaderboardId(BigInteger leaderboardId) {
		this.leaderboardId = leaderboardId;
	}

	public BigInteger getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BigInteger customerId) {
		this.customerId = customerId;
	}

	public float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public float getWinAmount() {
		return winAmount;
	}

	public void setWinAmount(float winAmount) {
		this.winAmount = winAmount;
	}

	public String getWinGadget() {
		return winGadget;
	}

	public void setWinGadget(String winGadget) {
		this.winGadget = winGadget;
	}

	public float getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(float newAmount) {
		this.newAmount = newAmount;
	}

	public float getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(float oldAmount) {
		this.oldAmount = oldAmount;
	}

	public int getNewRank() {
		return newRank;
	}

	public void setNewRank(int newRank) {
		this.newRank = newRank;
	}

	public int getOldRank() {
		return oldRank;
	}

	public void setOldRank(int oldRank) {
		this.oldRank = oldRank;
	}

	public BigInteger getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public BigInteger getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}
}