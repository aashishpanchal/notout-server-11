package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_soccer_customer_contests database table.
 * 
 */
@Entity
@Table(name = "tbl_soccer_customer_contests")
@NamedQuery(name = "TblSoccerCustomerContest.findAll", query = "SELECT t FROM TblSoccerCustomerContest t")
public class TblSoccerCustomerContest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "cash_bonus_wallet")
	private float cashBonusWallet;

	private BigInteger created;

	@Column(name = "customer_id")
	private int customerId;

	@Column(name = "customer_team_id")
	private int customerTeamId;

	@Column(name = "given_by_user_amount")
	private float givenByUserAmount;

	@Column(name = "given_by_admin_amount")
	private float givenByAdminAmount;

	@Column(name = "deposite_wallet")
	private float depositeWallet;

	@Column(name = "entry_fees")
	private float entryFees;

	@Column(name = "match_contest_id")
	private int matchContestId;

	@Column(name = "match_unique_id")
	private int matchUniqueId;

	@Column(name = "series_id")
	private int seriesId;

	@Column(name = "new_points")
	private float newPoints;

	@Column(name = "new_rank")
	private int newRank;

	@Column(name = "old_points")
	private float oldPoints;

	@Column(name = "old_rank")
	private int oldRank;

	@Column(name = "refund_amount")
	private float refundAmount;

	@Column(name = "tax_amount")
	private float taxAmount;

	@Lob
	@Column(name = "tax_json")
	private String taxJson;

	@Column(name = "tax_percent")
	private float taxPercent;

	private BigInteger updated;

	@Column(name = "win_amount")
	private float winAmount;

	@Column(name = "win_gadget")
	private String winGadget;

	@Column(name = "winning_wallet")
	private float winningWallet;
	
	@Column(name = "is_abondant")
	private String isAbondant;

	@Transient
	double totalInvestment;

	@Transient
	double totalWinning;

	public double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public double getTotalWinning() {
		return totalWinning;
	}

	public void setTotalWinning(double totalWinning) {
		this.totalWinning = totalWinning;
	}

	public TblSoccerCustomerContest() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getCashBonusWallet() {
		return this.cashBonusWallet;
	}

	public void setCashBonusWallet(float cashBonusWallet) {
		this.cashBonusWallet = cashBonusWallet;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
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

	public float getGivenByUserAmount() {
		return givenByUserAmount;
	}

	public void setGivenByUserAmount(float givenByUserAmount) {
		this.givenByUserAmount = givenByUserAmount;
	}

	public float getGivenByAdminAmount() {
		return givenByAdminAmount;
	}

	public void setGivenByAdminAmount(float givenByAdminAmount) {
		this.givenByAdminAmount = givenByAdminAmount;
	}

	public float getDepositeWallet() {
		return this.depositeWallet;
	}

	public void setDepositeWallet(float depositeWallet) {
		this.depositeWallet = depositeWallet;
	}

	public float getEntryFees() {
		return this.entryFees;
	}

	public void setEntryFees(float entryFees) {
		this.entryFees = entryFees;
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

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public float getNewPoints() {
		return this.newPoints;
	}

	public void setNewPoints(float newPoints) {
		this.newPoints = newPoints;
	}

	public int getNewRank() {
		return this.newRank;
	}

	public void setNewRank(int newRank) {
		this.newRank = newRank;
	}

	public float getOldPoints() {
		return this.oldPoints;
	}

	public void setOldPoints(float oldPoints) {
		this.oldPoints = oldPoints;
	}

	public int getOldRank() {
		return this.oldRank;
	}

	public void setOldRank(int oldRank) {
		this.oldRank = oldRank;
	}

	public float getRefundAmount() {
		return this.refundAmount;
	}

	public void setRefundAmount(float refundAmount) {
		this.refundAmount = refundAmount;
	}

	public float getTaxAmount() {
		return this.taxAmount;
	}

	public void setTaxAmount(float taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getTaxJson() {
		return this.taxJson;
	}

	public void setTaxJson(String taxJson) {
		this.taxJson = taxJson;
	}

	public float getTaxPercent() {
		return this.taxPercent;
	}

	public void setTaxPercent(float taxPercent) {
		this.taxPercent = taxPercent;
	}

	public BigInteger getUpdated() {
		return this.updated;
	}

	public void setUpdated(BigInteger updated) {
		this.updated = updated;
	}

	public float getWinAmount() {
		return this.winAmount;
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

	public float getWinningWallet() {
		return this.winningWallet;
	}

	public void setWinningWallet(float winningWallet) {
		this.winningWallet = winningWallet;
	}
	
	public void setIsAbondant(String isAbondant) {
		this.isAbondant = isAbondant;
	}
	
	public String getIsAbondant() {
		return isAbondant;
	}

	public static TblSoccerCustomerContest getInstance() {
		TblSoccerCustomerContest tblSoccerCustomerContest = new TblSoccerCustomerContest();

		tblSoccerCustomerContest.customerId = 0;
		tblSoccerCustomerContest.matchUniqueId = 0;
		tblSoccerCustomerContest.matchContestId = 0;
		tblSoccerCustomerContest.customerTeamId = 0;
		tblSoccerCustomerContest.entryFees = 0;
		tblSoccerCustomerContest.oldRank = 0;
		tblSoccerCustomerContest.newRank = 0;
		tblSoccerCustomerContest.oldPoints = 0;
		tblSoccerCustomerContest.newPoints = 0;
		tblSoccerCustomerContest.winAmount = 0;
		tblSoccerCustomerContest.winGadget = "";
		tblSoccerCustomerContest.refundAmount = 0;
		tblSoccerCustomerContest.taxAmount = 0;
		tblSoccerCustomerContest.taxPercent = 0;
		tblSoccerCustomerContest.created = BigInteger.valueOf(0);
		tblSoccerCustomerContest.updated = BigInteger.valueOf(0);
		tblSoccerCustomerContest.cashBonusWallet = 0;
		tblSoccerCustomerContest.depositeWallet = 0;
		tblSoccerCustomerContest.winningWallet = 0;
		tblSoccerCustomerContest.givenByUserAmount = 0;
		tblSoccerCustomerContest.givenByAdminAmount = 0;
		tblSoccerCustomerContest.isAbondant = "N";

		return tblSoccerCustomerContest;
	}

}