package com.choic11.model.soccer;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_soccer_match_players_stats database table.
 */
@Entity
@Table(name = "tbl_soccer_match_players_stats")
@NamedQuery(name = "TblSoccerMatchPlayersStat.findAll", query = "SELECT t FROM TblSoccerMatchPlayersStat t")
public class TblSoccerMatchPlayersStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "match_unique_id")
    private Integer matchUniqueId;

    @Column(name = "player_unique_id")
    private Integer playerUniqueId;

    @Column(name = "Goal")
    private float goal;

    @Column(name = "Goal_Value")
    private float goalValue;

    @Column(name = "Assist")
    private float assist;

    @Column(name = "Assist_Value")
    private float assistValue;

    @Column(name = "Shot_On_Target")
    private float shotOnTarget;

    @Column(name = "Shot_On_Target_Value")
    private float shotOnTargetValue;

    @Column(name = "Chance_Created")
    private float chanceCreated;

    @Column(name = "Chance_Created_Value")
    private float chanceCreatedValue;

    @Column(name = "5_Passes_Completed")
    private float fivePassesCompleted;

    @Column(name = "5_Passes_Completed_Value")
    private float fivePasses_Completed_Value;

    @Column(name = "Tackle_Won")
    private float tackleWon;

    @Column(name = "Tackle_Won_Value")
    private float tackleWonValue;

    @Column(name = "Interception_Won")
    private float interceptionWon;

    @Column(name = "Interception_Won_Value")
    private float interceptionWonValue;

    @Column(name = "Blocked_Shot")
    private float blockedShot;

    @Column(name = "Blocked_Shot_Value")
    private float blockedShotValue;

    @Column(name = "Clearance")
    private float clearance;

    @Column(name = "Clearance_Value")
    private float clearanceValue;

    @Column(name = "Saves")
    private float saves;

    @Column(name = "Saves_Value")
    private float savesValue;

    @Column(name = "Penalty_Saved")
    private float penaltySaved;

    @Column(name = "Penalty_Saved_Value")
    private float penaltySavedValue;

    @Column(name = "Clean_Sheet")
    private float cleanSheet;

    @Column(name = "Clean_Sheet_Value")
    private float cleanSheetValue;

    @Column(name = "In_Starting_11")
    private float inStarting11;

    @Column(name = "In_Starting_11_Value")
    private float inStarting11Value;

    @Column(name = "Coming_on_as_a_Substitute")
    private float comingOnAsASubstitute;

    @Column(name = "Coming_on_as_a_Substitute_Value")
    private float comingOnAsASubstituteValue;

    @Column(name = "Yellow_Card")
    private float yellowCard;

    @Column(name = "Yellow_Card_Value")
    private float yellowCardValue;

    @Column(name = "Red_Card")
    private float redCard;

    @Column(name = "Red_Card_Value")
    private float redCardValue;

    @Column(name = "Own_goal")
    private float ownGoal;

    @Column(name = "Own_goal_Value")
    private float ownGoalValue;

    @Column(name = "Goals_Conceded")
    private float goalsConceded;

    @Column(name = "Goals_Conceded_Value")
    private float goalsConcededValue;

    @Column(name = "Penalty_Missed")
    private float penaltyMissed;

    @Column(name = "Penalty_Missed_Value")
    private float penaltyMissedValue;

    @Column(name = "updated")
    private BigInteger updated;

    public TblSoccerMatchPlayersStat() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getMatchUniqueId() {
        return matchUniqueId;
    }

    public void setMatchUniqueId(Integer matchUniqueId) {
        this.matchUniqueId = matchUniqueId;
    }

    public Integer getPlayerUniqueId() {
        return playerUniqueId;
    }

    public void setPlayerUniqueId(Integer playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public float getGoal() {
        return goal;
    }

    public void setGoal(float goal) {
        this.goal = goal;
    }

    public float getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(float goalValue) {
        this.goalValue = goalValue;
    }

    public float getAssist() {
        return assist;
    }

    public void setAssist(float assist) {
        this.assist = assist;
    }

    public float getAssistValue() {
        return assistValue;
    }

    public void setAssistValue(float assistValue) {
        this.assistValue = assistValue;
    }

    public float getShotOnTarget() {
        return shotOnTarget;
    }

    public void setShotOnTarget(float shotOnTarget) {
        this.shotOnTarget = shotOnTarget;
    }

    public float getShotOnTargetValue() {
        return shotOnTargetValue;
    }

    public void setShotOnTargetValue(float shotOnTargetValue) {
        this.shotOnTargetValue = shotOnTargetValue;
    }

    public float getChanceCreated() {
        return chanceCreated;
    }

    public void setChanceCreated(float chanceCreated) {
        this.chanceCreated = chanceCreated;
    }

    public float getChanceCreatedValue() {
        return chanceCreatedValue;
    }

    public void setChanceCreatedValue(float chanceCreatedValue) {
        this.chanceCreatedValue = chanceCreatedValue;
    }

    public float getFivePassesCompleted() {
        return fivePassesCompleted;
    }

    public void setFivePassesCompleted(float fivePassesCompleted) {
        this.fivePassesCompleted = fivePassesCompleted;
    }

    public float getFivePasses_Completed_Value() {
        return fivePasses_Completed_Value;
    }

    public void setFivePasses_Completed_Value(float fivePasses_Completed_Value) {
        this.fivePasses_Completed_Value = fivePasses_Completed_Value;
    }

    public float getTackleWon() {
        return tackleWon;
    }

    public void setTackleWon(float tackleWon) {
        this.tackleWon = tackleWon;
    }

    public float getTackleWonValue() {
        return tackleWonValue;
    }

    public void setTackleWonValue(float tackleWonValue) {
        this.tackleWonValue = tackleWonValue;
    }

    public float getInterceptionWon() {
        return interceptionWon;
    }

    public void setInterceptionWon(float interceptionWon) {
        this.interceptionWon = interceptionWon;
    }

    public float getInterceptionWonValue() {
        return interceptionWonValue;
    }

    public void setInterceptionWonValue(float interceptionWonValue) {
        this.interceptionWonValue = interceptionWonValue;
    }

    public float getBlockedShot() {
        return blockedShot;
    }

    public void setBlockedShot(float blockedShot) {
        this.blockedShot = blockedShot;
    }

    public float getBlockedShotValue() {
        return blockedShotValue;
    }

    public void setBlockedShotValue(float blockedShotValue) {
        this.blockedShotValue = blockedShotValue;
    }

    public float getClearance() {
        return clearance;
    }

    public void setClearance(float clearance) {
        this.clearance = clearance;
    }

    public float getClearanceValue() {
        return clearanceValue;
    }

    public void setClearanceValue(float clearanceValue) {
        this.clearanceValue = clearanceValue;
    }

    public float getSaves() {
        return saves;
    }

    public void setSaves(float saves) {
        this.saves = saves;
    }

    public float getSavesValue() {
        return savesValue;
    }

    public void setSavesValue(float savesValue) {
        this.savesValue = savesValue;
    }

    public float getPenaltySaved() {
        return penaltySaved;
    }

    public void setPenaltySaved(float penaltySaved) {
        this.penaltySaved = penaltySaved;
    }

    public float getPenaltySavedValue() {
        return penaltySavedValue;
    }

    public void setPenaltySavedValue(float penaltySavedValue) {
        this.penaltySavedValue = penaltySavedValue;
    }

    public float getCleanSheet() {
        return cleanSheet;
    }

    public void setCleanSheet(float cleanSheet) {
        this.cleanSheet = cleanSheet;
    }

    public float getCleanSheetValue() {
        return cleanSheetValue;
    }

    public void setCleanSheetValue(float cleanSheetValue) {
        this.cleanSheetValue = cleanSheetValue;
    }

    public float getInStarting11() {
        return inStarting11;
    }

    public void setInStarting11(float inStarting11) {
        this.inStarting11 = inStarting11;
    }

    public float getInStarting11Value() {
        return inStarting11Value;
    }

    public void setInStarting11Value(float inStarting11Value) {
        this.inStarting11Value = inStarting11Value;
    }

    public float getComingOnAsASubstitute() {
        return comingOnAsASubstitute;
    }

    public void setComingOnAsASubstitute(float comingOnAsASubstitute) {
        this.comingOnAsASubstitute = comingOnAsASubstitute;
    }

    public float getComingOnAsASubstituteValue() {
        return comingOnAsASubstituteValue;
    }

    public void setComingOnAsASubstituteValue(float comingOnAsASubstituteValue) {
        this.comingOnAsASubstituteValue = comingOnAsASubstituteValue;
    }

    public float getYellowCard() {
        return yellowCard;
    }

    public void setYellowCard(float yellowCard) {
        this.yellowCard = yellowCard;
    }

    public float getYellowCardValue() {
        return yellowCardValue;
    }

    public void setYellowCardValue(float yellowCardValue) {
        this.yellowCardValue = yellowCardValue;
    }

    public float getRedCard() {
        return redCard;
    }

    public void setRedCard(float redCard) {
        this.redCard = redCard;
    }

    public float getRedCardValue() {
        return redCardValue;
    }

    public void setRedCardValue(float redCardValue) {
        this.redCardValue = redCardValue;
    }

    public float getOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(float ownGoal) {
        this.ownGoal = ownGoal;
    }

    public float getOwnGoalValue() {
        return ownGoalValue;
    }

    public void setOwnGoalValue(float ownGoalValue) {
        this.ownGoalValue = ownGoalValue;
    }

    public float getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(float goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public float getGoalsConcededValue() {
        return goalsConcededValue;
    }

    public void setGoalsConcededValue(float goalsConcededValue) {
        this.goalsConcededValue = goalsConcededValue;
    }

    public float getPenaltyMissed() {
        return penaltyMissed;
    }

    public void setPenaltyMissed(float penaltyMissed) {
        this.penaltyMissed = penaltyMissed;
    }

    public float getPenaltyMissedValue() {
        return penaltyMissedValue;
    }

    public void setPenaltyMissedValue(float penaltyMissedValue) {
        this.penaltyMissedValue = penaltyMissedValue;
    }

    public BigInteger getUpdated() {
        return updated;
    }

    public void setUpdated(BigInteger updated) {
        this.updated = updated;
    }

    public void updateStatsData(JSONObject playerData) {
        setGoal(playerData.getFloat("Goal"));
        setGoalValue(playerData.getFloat("Goal_Value"));
        setAssist(playerData.getFloat("Assist"));
        setAssistValue(playerData.getFloat("Assist_Value"));
        setShotOnTarget(playerData.getFloat("Shot_On_Target"));
        setShotOnTargetValue(playerData.getFloat("Shot_On_Target_Value"));
        setChanceCreated(playerData.getFloat("Chance_Created"));
        setChanceCreatedValue(playerData.getFloat("Chance_Created_Value"));
        setFivePassesCompleted(playerData.getFloat("5_Passes_Completed"));
        setFivePasses_Completed_Value(playerData.getFloat("5_Passes_Completed_Value"));
        setTackleWon(playerData.getFloat("Tackle_Won"));
        setTackleWonValue(playerData.getFloat("Tackle_Won_Value"));
        setInterceptionWon(playerData.getFloat("Interception_Won"));
        setInterceptionWonValue(playerData.getFloat("Interception_Won_Value"));
        setBlockedShot(playerData.getFloat("Blocked_Shot"));
        setBlockedShotValue(playerData.getFloat("Blocked_Shot_Value"));
        setClearance(playerData.getFloat("Clearance"));
        setClearanceValue(playerData.getFloat("Clearance_Value"));
        setSaves(playerData.getFloat("Saves"));
        setSavesValue(playerData.getFloat("Saves_Value"));
        setPenaltySaved(playerData.getFloat("Penalty_Saved"));
        setPenaltySavedValue(playerData.getFloat("Penalty_Saved_Value"));
        setCleanSheet(playerData.getFloat("Clean_Sheet"));
        setCleanSheetValue(playerData.getFloat("Clean_Sheet_Value"));
        setInStarting11(playerData.getFloat("In_Starting_11"));
        setInStarting11Value(playerData.getFloat("In_Starting_11_Value"));
        setComingOnAsASubstitute(playerData.getFloat("Coming_on_as_a_Substitute"));
        setComingOnAsASubstituteValue(playerData.getFloat("Coming_on_as_a_Substitute_Value"));
        setYellowCard(playerData.getFloat("Yellow_Card"));
        setYellowCardValue(playerData.getFloat("Yellow_Card_Value"));
        setRedCard(playerData.getFloat("Red_Card"));
        setRedCardValue(playerData.getFloat("Red_Card_Value"));
        setOwnGoal(playerData.getFloat("Own_goal"));
        setOwnGoalValue(playerData.getFloat("Own_goal_Value"));
        setGoalsConceded(playerData.getFloat("Goals_Conceded"));
        setGoalsConcededValue(playerData.getFloat("Goals_Conceded_Value"));
        setPenaltyMissed(playerData.getFloat("Penalty_Missed"));
        setPenaltyMissedValue(playerData.getFloat("Penalty_Missed_Value"));
    }

    public boolean checkSameStats(JSONObject playerData) {
        return
                getGoal() == playerData.getFloat("Goal") &&
                        getGoalValue() == playerData.getFloat("Goal_Value") &&
                        getAssist() == playerData.getFloat("Assist") &&
                        getAssistValue() == playerData.getFloat("Assist_Value") &&
                        getShotOnTarget() == playerData.getFloat("Shot_On_Target") &&
                        getShotOnTargetValue() == playerData.getFloat("Shot_On_Target_Value") &&
                        getChanceCreated() == playerData.getFloat("Chance_Created") &&
                        getChanceCreatedValue() == playerData.getFloat("Chance_Created_Value") &&
                        getFivePassesCompleted() == playerData.getFloat("5_Passes_Completed") &&
                        getFivePasses_Completed_Value() == playerData.getFloat("5_Passes_Completed_Value") &&
                        getTackleWon() == playerData.getFloat("Tackle_Won") &&
                        getTackleWonValue() == playerData.getFloat("Tackle_Won_Value") &&
                        getInterceptionWon() == playerData.getFloat("Interception_Won") &&
                        getInterceptionWonValue() == playerData.getFloat("Interception_Won_Value") &&
                        getBlockedShot() == playerData.getFloat("Blocked_Shot") &&
                        getBlockedShotValue() == playerData.getFloat("Blocked_Shot_Value") &&
                        getClearance() == playerData.getFloat("Clearance") &&
                        getClearanceValue() == playerData.getFloat("Clearance_Value") &&
                        getSaves() == playerData.getFloat("Saves") &&
                        getSavesValue() == playerData.getFloat("Saves_Value") &&
                        getPenaltySaved() == playerData.getFloat("Penalty_Saved") &&
                        getPenaltySavedValue() == playerData.getFloat("Penalty_Saved_Value") &&
                        getCleanSheet() == playerData.getFloat("Clean_Sheet") &&
                        getCleanSheetValue() == playerData.getFloat("Clean_Sheet_Value") &&
                        getInStarting11() == playerData.getFloat("In_Starting_11") &&
                        getInStarting11Value() == playerData.getFloat("In_Starting_11_Value") &&
                        getComingOnAsASubstitute() == playerData.getFloat("Coming_on_as_a_Substitute") &&
                        getComingOnAsASubstituteValue() == playerData.getFloat("Coming_on_as_a_Substitute_Value") &&
                        getYellowCard() == playerData.getFloat("Yellow_Card") &&
                        getYellowCardValue() == playerData.getFloat("Yellow_Card_Value") &&
                        getRedCard() == playerData.getFloat("Red_Card") &&
                        getRedCardValue() == playerData.getFloat("Red_Card_Value") &&
                        getOwnGoal() == playerData.getFloat("Own_goal") &&
                        getOwnGoalValue() == playerData.getFloat("Own_goal_Value") &&
                        getGoalsConceded() == playerData.getFloat("Goals_Conceded") &&
                        getGoalsConcededValue() == playerData.getFloat("Goals_Conceded_Value") &&
                        getPenaltyMissed() == playerData.getFloat("Penalty_Missed") &&
                        getPenaltyMissedValue() == playerData.getFloat("Penalty_Missed_Value");

    }

}