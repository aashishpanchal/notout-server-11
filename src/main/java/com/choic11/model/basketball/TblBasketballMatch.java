package com.choic11.model.basketball;

import com.choic11.Util;
import com.choic11.model.TblGame;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_basketball_matches database table.
 * 
 */
@Entity
@Table(name = "tbl_basketball_matches")
@NamedQuery(name = "TblBasketballMatch.findAll", query = "SELECT t FROM TblBasketballMatch t")
public class TblBasketballMatch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "active_contest_count")
	private int activeContestCount;

	@Column(name = "unique_id")
	private Integer uniqueId;

	@Column(name = "close_date")
	private BigInteger closeDate;

	@Column(name = "contest_joined_mail")
	private String contestJoinedMail;

	@Column(name = "contest_wining_mail")
	private String contestWiningMail;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	private String image;

	@Column(name = "is_referral_bonus_distribute")
	private String isReferralDistributed;

	@Column(name = "is_affiliate_distribute")
	private String isAffiliateDistribute;

	@Column(name = "is_dis_affi_per")
	private String isDisAffiPer;
	

	@Column(name = "is_contest_abondant_complete")
	private String isContestAbondantComplete;

	@Column(name = "match_result_running")
	private String matchResultRunning;

	@Column(name = "is_deleted")
	private String isDeleted;

	@Column(name = "is_leaderboard_created")
	private String isLeaderboardCreated;

	@Column(name = "match_date")
	private BigInteger matchDate;

	@Column(name = "match_limit")
	private int matchLimit;

	@Column(name = "match_progress")
	private String matchProgress;
	
	@Column(name = "match_completed_status")
	private String matchCompletedStatus;

	private String name;

	@Column(name = "order_pos")
	private int orderPos;

	@Column(name = "playing_squad_notification_at")
	private BigInteger playingSquadNotificationAt;

	@Column(name = "playing_squad_updated")
	private String playingSquadUpdated;
	
	@Column(name = "playing_squad_updated_mannual")
	private String playingSquadUpdatedMannual;

	@Column(name = "playing_squad_updated_at")
	private BigInteger playingSquadUpdatedAt;

	@Column(name = "points_updated_at")
	private BigInteger pointsUpdatedAt;

	
	@Column(name = "score_board_notes")
	private String scoreBoardNotes;
	
	@Column(name = "toss_message")
	private String matchMessage;

	@Column(name = "scorecard_updated_at")
	private BigInteger scorecardUpdatedAt;

	@Column(name = "short_title")
	private String shortTitle;

	private String status;

	private String subtitle;

	@Column(name = "team1_overs")
	private String team1Overs;

	@Column(name = "team1_run")
	private String team1Run;

	@Column(name = "team1_wicket")
	private String team1Wicket;

	@Column(name = "team2_overs")
	private String team2Overs;

	@Column(name = "team2_run")
	private String team2Run;

	@Column(name = "team2_wicket")
	private String team2Wicket;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	@Column(name = "team_1_id")
	private int team1Id;

	@Column(name = "team_2_id")
	private int team2Id;

	@Column(name = "game_type_id")
	private int gameTypeId;

	@Column(name = "series_id")
	private int seriesId;

	@Column(name = "total_customer_team")
	private int totalCustomerTeam;

	@Column(name = "lineup_before_second")
	private int lineupBeforeSecond;
	
	@Column(name = "is_affiliate_available_all")
	private String isAffiliateAvailableAll;
	
	@Column(name = "affiliate_per_for_all")
	private float affiliatePerForAll;

	@ManyToOne
	@JoinColumn(name = "series_id", insertable = false, updatable = false)
	private TblBasketballSeries tblBasketballSery;

	@ManyToOne
	@JoinColumn(name = "team_1_id", insertable = false, updatable = false)
	private TblBasketballTeam tblBasketballTeam;

	@ManyToOne
	@JoinColumn(name = "team_2_id", insertable = false, updatable = false)
	private TblBasketballTeam tblBasketballTeam2;

	@ManyToOne
	@JoinColumn(name = "game_type_id", insertable = false, updatable = false)
	private TblBasketballGameType tblGameType;

	@ManyToOne
	@JoinColumn(name = "game_id")
	private TblGame tblGame;

	@Transient
	String shortScore;

	@Transient
	int contestCount;

	@Transient
	int teamCount;

	@Transient
	double totalWinning;

	@Transient
	double totalInvestment;

	public String getShortScore() {
		return shortScore;
	}

	public void setShortScore(String shortScore) {
		this.shortScore = shortScore;
	}

	public TblBasketballMatch() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getActiveContestCount() {
		return this.activeContestCount;
	}

	public void setActiveContestCount(int activeContestCount) {
		this.activeContestCount = activeContestCount;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public BigInteger getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(BigInteger closeDate) {
		this.closeDate = closeDate;
	}

	public boolean isContestJoinedMailActive() {
		return getContestJoinedMail().equals("Y");
	}

	public String getContestJoinedMail() {
		return this.contestJoinedMail;
	}

	public void setContestJoinedMail(String contestJoinedMail) {
		this.contestJoinedMail = contestJoinedMail;
	}

	public boolean isContestWinMailActive() {
		return getContestWiningMail().equals("Y");
	}

	public String getContestWiningMail() {
		return this.contestWiningMail;
	}

	public void setContestWiningMail(String contestWiningMail) {
		this.contestWiningMail = contestWiningMail;
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

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIsAffiliateDistribute() {
		return this.isAffiliateDistribute;
	}

	public void setIsAffiliateDistribute(String isAffiliateDistribute) {
		this.isAffiliateDistribute = isAffiliateDistribute;
	}

	public String getIsReferralDistributed() {
		return isReferralDistributed;
	}

	public void setIsReferralDistributed(String isReferralDistributed) {
		this.isReferralDistributed = isReferralDistributed;
	}

	public String getIsDisAffiPer() {
		return isDisAffiPer;
	}

	public void setIsDisAffiPer(String isDisAffiPer) {
		this.isDisAffiPer = isDisAffiPer;
	}

	public String getIsContestAbondantComplete() {
		return this.isContestAbondantComplete;
	}

	public void setIsContestAbondantComplete(String isContestAbondantComplete) {
		this.isContestAbondantComplete = isContestAbondantComplete;
	}

	public String getMatchResultRunning() {
		return matchResultRunning;
	}

	public void setMatchResultRunning(String matchResultRunning) {
		this.matchResultRunning = matchResultRunning;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsLeaderboardCreated() {
		return this.isLeaderboardCreated;
	}

	public void setIsLeaderboardCreated(String isLeaderboardCreated) {
		this.isLeaderboardCreated = isLeaderboardCreated;
	}

	public BigInteger getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(BigInteger matchDate) {
		this.matchDate = matchDate;
	}

	public int getMatchLimit() {
		return this.matchLimit;
	}

	public void setMatchLimit(int matchLimit) {
		this.matchLimit = matchLimit;
	}

	public String getMatchProgress() {
		return this.matchProgress;
	}

	public void setMatchProgress(String matchProgress) {
		this.matchProgress = matchProgress;
	}
	
	public String getMatchCompletedStatus() {
		return matchCompletedStatus;
	}
	
	public void setMatchCompletedStatus(String matchCompletedStatus) {
		this.matchCompletedStatus = matchCompletedStatus;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrderPos() {
		return this.orderPos;
	}

	public void setOrderPos(int orderPos) {
		this.orderPos = orderPos;
	}

	public BigInteger getPlayingSquadNotificationAt() {
		return this.playingSquadNotificationAt;
	}

	public void setPlayingSquadNotificationAt(BigInteger playingSquadNotificationAt) {
		this.playingSquadNotificationAt = playingSquadNotificationAt;
	}

	public String getPlayingSquadUpdated() {
		return this.playingSquadUpdated;
	}

	public void setPlayingSquadUpdated(String playingSquadUpdated) {
		this.playingSquadUpdated = playingSquadUpdated;
	}
	
	public String getPlayingSquadUpdatedMannual() {
		return playingSquadUpdatedMannual;
	}
	
	public void setPlayingSquadUpdatedMannual(String playingSquadUpdatedMannual) {
		this.playingSquadUpdatedMannual = playingSquadUpdatedMannual;
	}

	public BigInteger getPlayingSquadUpdatedAt() {
		return this.playingSquadUpdatedAt;
	}

	public void setPlayingSquadUpdatedAt(BigInteger playingSquadUpdatedAt) {
		this.playingSquadUpdatedAt = playingSquadUpdatedAt;
	}

	public BigInteger getPointsUpdatedAt() {
		return this.pointsUpdatedAt;
	}

	public void setPointsUpdatedAt(BigInteger pointsUpdatedAt) {
		this.pointsUpdatedAt = pointsUpdatedAt;
	}

	public String getScoreBoardNotes() {
		return this.scoreBoardNotes;
	}

	public void setScoreBoardNotes(String scoreBoardNotes) {
		this.scoreBoardNotes = scoreBoardNotes;
	}

	public String getMatchMessage() {
		return matchMessage;
	}

	public void setMatchMessage(String matchMessage) {
		this.matchMessage = matchMessage;
	}

	public BigInteger getScorecardUpdatedAt() {
		return this.scorecardUpdatedAt;
	}

	public void setScorecardUpdatedAt(BigInteger scorecardUpdatedAt) {
		this.scorecardUpdatedAt = scorecardUpdatedAt;
	}

	public String getShortTitle() {
		return this.shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubtitle() {
		return this.subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTeam1Overs() {
		return this.team1Overs;
	}

	public void setTeam1Overs(String team1Overs) {
		this.team1Overs = team1Overs;
	}

	public String getTeam1Run() {
		return this.team1Run;
	}

	public void setTeam1Run(String team1Run) {
		this.team1Run = team1Run;
	}

	public String getTeam1Wicket() {
		return this.team1Wicket;
	}

	public void setTeam1Wicket(String team1Wicket) {
		this.team1Wicket = team1Wicket;
	}

	public String getTeam2Overs() {
		return this.team2Overs;
	}

	public void setTeam2Overs(String team2Overs) {
		this.team2Overs = team2Overs;
	}

	public String getTeam2Run() {
		return this.team2Run;
	}

	public void setTeam2Run(String team2Run) {
		this.team2Run = team2Run;
	}

	public String getTeam2Wicket() {
		return this.team2Wicket;
	}

	public void setTeam2Wicket(String team2Wicket) {
		this.team2Wicket = team2Wicket;
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

	public TblBasketballSeries getTblBasketballSery() {
		return this.tblBasketballSery;
	}

	public void setTblBasketballSery(TblBasketballSeries tblBasketballSery) {
		this.tblBasketballSery = tblBasketballSery;
	}

	public TblBasketballTeam getTblBasketballTeam() {
		return this.tblBasketballTeam;
	}

	public void setTblBasketballTeam(TblBasketballTeam tblBasketballTeam) {
		this.tblBasketballTeam = tblBasketballTeam;
	}

	public TblBasketballTeam getTblBasketballTeam2() {
		return tblBasketballTeam2;
	}

	public void setTblBasketballTeam2(TblBasketballTeam tblBasketballTeam2) {
		this.tblBasketballTeam2 = tblBasketballTeam2;
	}

	public TblBasketballGameType getTblGameType() {
		return this.tblGameType;
	}

	public void setTblGameType(TblBasketballGameType tblGameType) {
		this.tblGameType = tblGameType;
	}

	public TblGame getTblGame() {
		return this.tblGame;
	}

	public void setTblGame(TblGame tblGame) {
		this.tblGame = tblGame;
	}

	public int getTeam1Id() {
		return team1Id;
	}

	public void setTeam1Id(int team1Id) {
		this.team1Id = team1Id;
	}

	public int getTeam2Id() {
		return team2Id;
	}

	public void setTeam2Id(int team2Id) {
		this.team2Id = team2Id;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public int getGameTypeId() {
		return gameTypeId;
	}

	public void setGameTypeId(int gameTypeId) {
		this.gameTypeId = gameTypeId;
	}

	public boolean isFixtureMatch() {
		return getMatchProgress().equals("F");
	}

	public boolean isLiveMatch() {
		return getMatchProgress().equals("L");
	}

	public boolean isInreviewMatch() {
		return getMatchProgress().equals("IR");
	}

	public boolean isCompletedMatch() {
		return getMatchProgress().equals("R");
	}

	public boolean isAboundantMatch() {
		return getMatchProgress().equals("AB");
	}

	public boolean isPastMatch() {
		return isCompletedMatch() || isAboundantMatch();
	}

	public boolean isReferralDistributedRunning() {
		return getIsReferralDistributed().equals("R");
	}

	public boolean isReferralDistributed() {
		return getIsReferralDistributed().equals("Y");
	}

	public boolean isAffiliateDistributedRunning() {
		return getIsAffiliateDistribute().equals("R");
	}

	public boolean isAffiliateDistributed() {
		return getIsAffiliateDistribute().equals("Y");
	}

	public boolean isLeaderboardCreatedRunning() {
		return getIsLeaderboardCreated().equals("R");
	}

	public boolean isLeaderboardCreated() {
		return getIsLeaderboardCreated().equals("Y");
	}

	public boolean isMatchResultRunning() {
		return getMatchResultRunning().equals("R");
	}

	public boolean isMatchResultCompleted() {
		return getMatchResultRunning().equals("Y");
	}

	public int getTotalCustomerTeam() {
		return totalCustomerTeam;
	}

	public void setTotalCustomerTeam(int totalCustomerTeam) {
		this.totalCustomerTeam = totalCustomerTeam;
	}

	public int getLineupBeforeSecond() {
		return lineupBeforeSecond;
	}

	public void setLineupBeforeSecond(int lineupBeforeSecond) {
		this.lineupBeforeSecond = lineupBeforeSecond;
	}
	
	public String getIsAffiliateAvailableAll() {
		return isAffiliateAvailableAll;
	}

	public void setIsAffiliateAvailableAll(String isAffiliateAvailableAll) {
		this.isAffiliateAvailableAll = isAffiliateAvailableAll;
	}

	public float getAffiliatePerForAll() {
		return affiliatePerForAll;
	}

	public void setAffiliatePerForAll(float affiliatePerForAll) {
		this.affiliatePerForAll = affiliatePerForAll;
	}

	public int getContestCount() {
		return contestCount;
	}

	public void setContestCount(int contestCount) {
		this.contestCount = contestCount;
	}

	public int getTeamCount() {
		return teamCount;
	}

	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}

	public double getTotalWinning() {
		return totalWinning;
	}

	public void setTotalWinning(double totalWinning) {
		this.totalWinning = totalWinning;
	}

	public double getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(double totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public String getMatchFormattedDate() {
		BigInteger matchDate2 = getMatchDate();
		try {
			long parseLong = matchDate2.longValue();

			return Util.getFormatedDate(parseLong * 1000, "yyyy-MM-dd hh:mm a");
		} catch (Exception e) {

		}
		return "";
	}
	public String getMatchFormattedDateOnly() {
		BigInteger matchDate2 = getMatchDate();
		try {
			long parseLong = matchDate2.longValue();

			return Util.getFormatedDate(parseLong * 1000, "yyyy-MM-dd");
		} catch (Exception e) {

		}
		return "";
	}

}