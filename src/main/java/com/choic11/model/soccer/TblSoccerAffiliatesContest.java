package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_soccer_affiliates_contest database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_affiliates_contest")
@NamedQuery(name="TblSoccerAffiliatesContest.findAll", query="SELECT t FROM TblSoccerAffiliatesContest t")
public class TblSoccerAffiliatesContest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="admin_commission_amount")
	private float adminCommissionAmount;

	@Column(name="affiliate_percent")
	private float affiliatePercent;

	@Column(name="bonus_amount")
	private float bonusAmount;

	@Column(name="created_at")
	private int createdAt;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="deposit_amount")
	private float depositAmount;

	private float earning;

	@Column(name="given_by_admin_amount")
	private float givenByAdminAmount;

	@Column(name="given_by_user_amount")
	private float givenByUserAmount;

	@Column(name="joined_team")
	private int joinedTeam;

	@Column(name="match_contest_id")
	private int matchContestId;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	@Column(name="total_win_contribute")
	private float totalWinContribute;

	@Column(name="winning_amount")
	private float winningAmount;

	public TblSoccerAffiliatesContest() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getAdminCommissionAmount() {
		return this.adminCommissionAmount;
	}

	public void setAdminCommissionAmount(float adminCommissionAmount) {
		this.adminCommissionAmount = adminCommissionAmount;
	}

	public float getAffiliatePercent() {
		return this.affiliatePercent;
	}

	public void setAffiliatePercent(float affiliatePercent) {
		this.affiliatePercent = affiliatePercent;
	}

	public float getBonusAmount() {
		return this.bonusAmount;
	}

	public void setBonusAmount(float bonusAmount) {
		this.bonusAmount = bonusAmount;
	}

	public int getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public float getDepositAmount() {
		return this.depositAmount;
	}

	public void setDepositAmount(float depositAmount) {
		this.depositAmount = depositAmount;
	}

	public float getEarning() {
		return this.earning;
	}

	public void setEarning(float earning) {
		this.earning = earning;
	}

	public float getGivenByAdminAmount() {
		return this.givenByAdminAmount;
	}

	public void setGivenByAdminAmount(float givenByAdminAmount) {
		this.givenByAdminAmount = givenByAdminAmount;
	}

	public float getGivenByUserAmount() {
		return this.givenByUserAmount;
	}

	public void setGivenByUserAmount(float givenByUserAmount) {
		this.givenByUserAmount = givenByUserAmount;
	}

	public int getJoinedTeam() {
		return this.joinedTeam;
	}

	public void setJoinedTeam(int joinedTeam) {
		this.joinedTeam = joinedTeam;
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

	public float getTotalWinContribute() {
		return this.totalWinContribute;
	}

	public void setTotalWinContribute(float totalWinContribute) {
		this.totalWinContribute = totalWinContribute;
	}

	public float getWinningAmount() {
		return this.winningAmount;
	}

	public void setWinningAmount(float winningAmount) {
		this.winningAmount = winningAmount;
	}

}