package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_basketball_contest_matches database table.
 * 
 */
@Entity
@Table(name = "tbl_basketball_contest_matches")
@NamedQuery(name = "TblBasketballContestMatch.findAll", query = "SELECT t FROM TblBasketballContestMatch t")
public class TblBasketballContestMatch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "duplicate_created_count")
	private int duplicateCreatedCount;

	@Column(name = "total_real_money_received")
	private float totalRealMoneyReceived;

	@Column(name = "total_winning_distributed")
	private float totalWinningDistributed;

	@Column(name = "actual_entry_fees")
	private float actualEntryFees;

	@Column(name = "category_id")
	private int categoryId;

	@Column(name = "series_id")
	private int seriesId;

	@Column(name = "confirm_win")
	private String confirmWin;
	
	@Column(name = "is_compression_allow")
	private String isCompressionAllow;

	@Column(name = "compression_allow_percentage")
	private float compressionAllowPercentage;

	@Column(name = "confirm_win_contest_percentage")
	private float confirmWinContestPercentage;

	@Column(name = "contest_id")
	private int contestId;

	@Lob
	@Column(name = "gadget_disclaimer")
	private String gadgetDisclaimer;

	@Lob
	@Column(name = "contest_json")
	private String contestJson;

	@Lob
	@Column(name = "contest_json_old")
	private String oldContestJson;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "duplicate_count")
	private int duplicateCount;

	@Column(name = "entry_fee_multiplier")
	private float entryFeeMultiplier;

	@Column(name = "entry_fees")
	private float entryFees;

	@Column(name = "is_abondant")
	private String isAbondant;

	@Column(name = "is_result")
	private String isResult;

	@Column(name = "is_affiliate_earn_allow")
	private String isAffiliateEarnAllow;

	@Column(name = "is_beat_the_expert")
	private String isBeatTheExpert;


	@Column(name = "is_deleted")
	private String isDeleted;

	@Column(name = "is_duplicate_allow")
	private String isDuplicateAllow;

	@Column(name = "is_leader_board_avaliable")
	private String isLeaderBoardAvaliable;

	@Column(name = "is_duplicated_created")
	private String isDuplicatedCreated;

	@Column(name = "is_private")
	private String isPrivate;

	@Column(name = "match_id")
	private int matchId;

	@Column(name = "match_unique_id")
	private int matchUniqueId;

	@Column(name = "max_entry_fees")
	private float maxEntryFees;

	@Column(name = "more_entry_fees")
	private float moreEntryFees;

	@Column(name = "multi_team_allowed")
	private String multiTeamAllowed;

	@Column(name = "parent_id")
	private int parentId;

	private String pdf;

	@Column(name = "pdf_process")
	private String pdfProcess;

	@Column(name = "per_user_team_allowed")
	private int perUserTeamAllowed;

	private String slug;

	private String status;

	@Column(name = "team_id")
	private int teamId;

	@Column(name = "total_teams_joined")
	private int totalJoinedTeam;

	@Column(name = "total_price")
	private float totalPrice;

	@Column(name = "total_price_old")
	private float oldTotalPrice;

	@Column(name = "total_team")
	private int totalTeam;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	@Column(name = "user_id")
	private int userId;


	@Column(name="cash_bonus_used_type")
	private String cashBonusUsedType;

	@Column(name="cash_bonus_used_value")
	private float cashBonusUsedValue;

	public TblBasketballContestMatch() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuplicateCreatedCount() {
		return duplicateCreatedCount;
	}

	public void setDuplicateCreatedCount(int duplicateCreatedCount) {
		this.duplicateCreatedCount = duplicateCreatedCount;
	}

	public float getTotalRealMoneyReceived() {
		return totalRealMoneyReceived;
	}

	public void setTotalRealMoneyReceived(float totalRealMoneyReceived) {
		this.totalRealMoneyReceived = totalRealMoneyReceived;
	}

	public float getTotalWinningDistributed() {
		return totalWinningDistributed;
	}

	public void setTotalWinningDistributed(float totalWinningDistributed) {
		this.totalWinningDistributed = totalWinningDistributed;
	}

	public float getActualEntryFees() {
		return this.actualEntryFees;
	}

	public void setActualEntryFees(float actualEntryFees) {
		this.actualEntryFees = actualEntryFees;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public String getConfirmWin() {
		return this.confirmWin;
	}

	public void setConfirmWin(String confirmWin) {
		this.confirmWin = confirmWin;
	}

	public String getIsCompressionAllow() {
		return isCompressionAllow;
	}

	public void setIsCompressionAllow(String isCompressionAllow) {
		this.isCompressionAllow = isCompressionAllow;
	}

	public float getConfirmWinContestPercentage() {
		return this.confirmWinContestPercentage;
	}

	public void setConfirmWinContestPercentage(float confirmWinContestPercentage) {
		this.confirmWinContestPercentage = confirmWinContestPercentage;
	}

	public int getContestId() {
		return this.contestId;
	}

	public void setContestId(int contestId) {
		this.contestId = contestId;
	}

	public String getGadgetDisclaimer() {
		return gadgetDisclaimer;
	}

	public void setGadgetDisclaimer(String gadgetDisclaimer) {
		this.gadgetDisclaimer = gadgetDisclaimer;
	}

	public String getContestJson() {
		return this.contestJson;
	}

	public void setContestJson(String contestJson) {
		this.contestJson = contestJson;
	}

	public String getOldContestJson() {
		return oldContestJson;
	}

	public void setOldContestJson(String oldContestJson) {
		this.oldContestJson = oldContestJson;
	}

	public BigInteger getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getDuplicateCount() {
		return this.duplicateCount;
	}

	public void setDuplicateCount(int duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public float getEntryFeeMultiplier() {
		return this.entryFeeMultiplier;
	}

	public void setEntryFeeMultiplier(float entryFeeMultiplier) {
		this.entryFeeMultiplier = entryFeeMultiplier;
	}

	public float getEntryFees() {
		return this.entryFees;
	}

	public void setEntryFees(float entryFees) {
		this.entryFees = entryFees;
	}

	public String getIsAbondant() {
		return this.isAbondant;
	}

	public void setIsAbondant(String isAbondant) {
		this.isAbondant = isAbondant;
	}

	public String getIsResult() {
		return isResult;
	}

	public void setIsResult(String isResult) {
		this.isResult = isResult;
	}

	public String getIsAffiliateEarnAllow() {
		return isAffiliateEarnAllow;
	}

	public void setIsAffiliateEarnAllow(String isAffiliateEarnAllow) {
		this.isAffiliateEarnAllow = isAffiliateEarnAllow;
	}

	public String getIsBeatTheExpert() {
		return this.isBeatTheExpert;
	}

	public void setIsBeatTheExpert(String isBeatTheExpert) {
		this.isBeatTheExpert = isBeatTheExpert;
	}


	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsDuplicateAllow() {
		return this.isDuplicateAllow;
	}

	public void setIsDuplicateAllow(String isDuplicateAllow) {
		this.isDuplicateAllow = isDuplicateAllow;
	}

	public String getIsDuplicatedCreated() {
		return this.isDuplicatedCreated;
	}

	public void setIsDuplicatedCreated(String isDuplicatedCreated) {
		this.isDuplicatedCreated = isDuplicatedCreated;
	}

	public String getIsPrivate() {
		return this.isPrivate;
	}

	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
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

	public float getMaxEntryFees() {
		return this.maxEntryFees;
	}

	public void setMaxEntryFees(float maxEntryFees) {
		this.maxEntryFees = maxEntryFees;
	}

	public float getMoreEntryFees() {
		return this.moreEntryFees;
	}

	public void setMoreEntryFees(float moreEntryFees) {
		this.moreEntryFees = moreEntryFees;
	}

	public String getMultiTeamAllowed() {
		return this.multiTeamAllowed;
	}

	public void setMultiTeamAllowed(String multiTeamAllowed) {
		this.multiTeamAllowed = multiTeamAllowed;
	}

	public int getParentId() {
		return this.parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getPdf() {
		return this.pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getPdfProcess() {
		return this.pdfProcess;
	}

	public void setPdfProcess(String pdfProcess) {
		this.pdfProcess = pdfProcess;
	}

	public int getPerUserTeamAllowed() {
		return this.perUserTeamAllowed;
	}

	public void setPerUserTeamAllowed(int perUserTeamAllowed) {
		this.perUserTeamAllowed = perUserTeamAllowed;
	}

	public String getSlug() {
		return this.slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTeamId() {
		return this.teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getTotalJoinedTeam() {
		return this.totalJoinedTeam;
	}

	public void setTotalJoinedTeam(int totalJoinedTeam) {
		this.totalJoinedTeam = totalJoinedTeam;
	}

	public float getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public float getOldTotalPrice() {
		return oldTotalPrice;
	}

	public void setOldTotalPrice(float oldTotalPrice) {
		this.oldTotalPrice = oldTotalPrice;
	}

	public int getTotalTeam() {
		return this.totalTeam;
	}

	public void setTotalTeam(int totalTeam) {
		this.totalTeam = totalTeam;
	}

	public BigInteger getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getIsLeaderBoardAvaliable() {
		return isLeaderBoardAvaliable;
	}

	public void setIsLeaderBoardAvaliable(String isLeaderBoardAvaliable) {
		this.isLeaderBoardAvaliable = isLeaderBoardAvaliable;
	}

	public String getCashBonusUsedType() {
		return cashBonusUsedType;
	}

	public void setCashBonusUsedType(String cashBonusUsedType) {
		this.cashBonusUsedType = cashBonusUsedType;
	}

	public float getCashBonusUsedValue() {
		return cashBonusUsedValue;
	}

	public void setCashBonusUsedValue(float cashBonusUsedValue) {
		this.cashBonusUsedValue = cashBonusUsedValue;
	}

	public float getCompressionAllowPercentage() {
		return compressionAllowPercentage;
	}

	public void setCompressionAllowPercentage(float compressionAllowPercentage) {
		this.compressionAllowPercentage = compressionAllowPercentage;
	}

	public static TblBasketballContestMatch getInstance() {
		TblBasketballContestMatch tblBasketballContestMatch = new TblBasketballContestMatch();
		tblBasketballContestMatch.contestId = 0;
		tblBasketballContestMatch.actualEntryFees = 0;
		tblBasketballContestMatch.moreEntryFees = 0;
		tblBasketballContestMatch.maxEntryFees = 0;
		tblBasketballContestMatch.userId = 0;
		tblBasketballContestMatch.teamId = 0;
		tblBasketballContestMatch.entryFeeMultiplier = 0;
		tblBasketballContestMatch.duplicateCount = 0;
		tblBasketballContestMatch.parentId = 0;
		tblBasketballContestMatch.createdAt = BigInteger.valueOf(0);
		tblBasketballContestMatch.createdBy = 0;
		tblBasketballContestMatch.updatedAt = BigInteger.valueOf(0);
		tblBasketballContestMatch.updatedBy = 0;
		tblBasketballContestMatch.totalJoinedTeam = 0;
		tblBasketballContestMatch.duplicateCreatedCount = 0;
		tblBasketballContestMatch.confirmWin = "N";
		tblBasketballContestMatch.isCompressionAllow = "N";
		tblBasketballContestMatch.multiTeamAllowed = "N";
		tblBasketballContestMatch.pdfProcess = "N";
		tblBasketballContestMatch.status = "A";
		tblBasketballContestMatch.isPrivate = "N";
		tblBasketballContestMatch.isBeatTheExpert = "N";
		tblBasketballContestMatch.isDuplicateAllow = "N";
		tblBasketballContestMatch.isDuplicatedCreated = "N";
		tblBasketballContestMatch.isDeleted = "N";
		tblBasketballContestMatch.isAbondant = "N";
		tblBasketballContestMatch.isResult = "N";
		tblBasketballContestMatch.isAffiliateEarnAllow = "Y";
		tblBasketballContestMatch.confirmWinContestPercentage = 60;
		tblBasketballContestMatch.oldTotalPrice = -1;
		tblBasketballContestMatch.cashBonusUsedType = "F";
		tblBasketballContestMatch.cashBonusUsedValue = 0;
		tblBasketballContestMatch.isLeaderBoardAvaliable = "N";
		return tblBasketballContestMatch;
	}

}