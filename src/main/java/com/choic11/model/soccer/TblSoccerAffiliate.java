package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_soccer_affiliates database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_affiliates")
@NamedQuery(name="TblSoccerAffiliate.findAll", query="SELECT t FROM TblSoccerAffiliate t")
public class TblSoccerAffiliate implements Serializable {
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

	@Column(name="contest_count")
	private int contestCount;

	@Column(name="created_at")
	private int createdAt;

	@Column(name="customer_id")
	private int customerId;
	
	@Column(name="series_id")
	private int seriesId;

	@Column(name="deposit_amount")
	private float depositAmount;

	private float earning;

	@Column(name="is_affiliate_distribute")
	private String isAffiliateDistribute;

	@Column(name="joined_team")
	private int joinedTeam;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	@Column(name="winning_amount")
	private float winningAmount;

	public TblSoccerAffiliate() {
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

	public int getContestCount() {
		return this.contestCount;
	}

	public void setContestCount(int contestCount) {
		this.contestCount = contestCount;
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
	
	public int getSeriesId() {
		return seriesId;
	}
	
	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
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

	public String getIsAffiliateDistribute() {
		return this.isAffiliateDistribute;
	}

	public void setIsAffiliateDistribute(String isAffiliateDistribute) {
		this.isAffiliateDistribute = isAffiliateDistribute;
	}

	public int getJoinedTeam() {
		return this.joinedTeam;
	}

	public void setJoinedTeam(int joinedTeam) {
		this.joinedTeam = joinedTeam;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public float getWinningAmount() {
		return this.winningAmount;
	}

	public void setWinningAmount(float winningAmount) {
		this.winningAmount = winningAmount;
	}

}