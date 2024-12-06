package com.choic11.model.cricket;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_cricket_match_players_stats database table.
 */
@Entity
@Table(name = "tbl_cricket_match_players_stats")
@NamedQuery(name = "TblCricketMatchPlayersStat.findAll", query = "SELECT t FROM TblCricketMatchPlayersStat t")
public class TblCricketMatchPlayersStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Being_Part_Of_Eleven")
    private float beingPartOfEleven;

    @Column(name = "Being_Part_Of_Eleven_Value")
    private float beingPartOfElevenValue;

    @Column(name = "Catch")
    private float catch1;

    @Column(name = "Three_Catch")
    private float threeCatch;

    @Column(name = "Lbw_Bowled")
    private float lbwBowled;

    @Column(name = "Catch_And_Bowled")
    private float catchAndBowled;

    @Column(name = "Catch_And_Bowled_Value")
    private float catchAndBowledValue;

    @Column(name = "Catch_Value")
    private float catchValue;

    @Column(name = "Three_Catch_Value")
    private float threeCatchValue;

    @Column(name = "Lbw_Bowled_Value")
    private float lbwBowledValue;

    @Column(name = "Century")
    private float century;

    @Column(name = "Century_Value")
    private float centuryValue;

    @Column(name = "Dismiss_For_A_Duck")
    private float dismissForADuck;

    @Column(name = "Dismiss_For_A_Duck_Value")
    private float dismissForADuckValue;

    @Column(name = "Economy_Rate")
    private float economyRate;

    @Column(name = "Economy_Rate_Value")
    private String economyRateValue;

    @Column(name = "Every_Boundary_Hit")
    private float everyBoundaryHit;

    @Column(name = "Every_Boundary_Hit_Value")
    private float everyBoundaryHitValue;

    @Column(name = "Every_Run_Scored")
    private float everyRunScored;

    @Column(name = "Every_Run_Scored_Value")
    private float everyRunScoredValue;

    @Column(name = "Every_Six_Hit")
    private float everySixHit;

    @Column(name = "Every_Six_Hit_Value")
    private float everySixHitValue;

    @Column(name = "Five_Wicket")
    private float fiveWicket;

    @Column(name = "Five_Wicket_Value")
    private float fiveWicketValue;

    @Column(name = "Four_Wicket")
    private float fourWicket;

    @Column(name = "Four_Wicket_Value")
    private float fourWicketValue;

    @Column(name = "Half_Century")
    private float halfCentury;

    @Column(name = "Half_Century_Value")
    private float halfCenturyValue;

    @Column(name = "Maiden_Over")
    private float maidenOver;

    @Column(name = "Maiden_Over_Value")
    private float maidenOverValue;

    @Column(name = "match_unique_id")
    private int matchUniqueId;

    @Column(name = "player_unique_id")
    private int playerUniqueId;

    @Column(name = "Run_Out")
    private float runOut;

    @Column(name = "Run_Out_Catcher")
    private float runOutCatcher;

    @Column(name = "Run_Out_Catcher_Value")
    private float runOutCatcherValue;

    @Column(name = "Run_Out_Thrower")
    private float runOutThrower;

    @Column(name = "Run_Out_Thrower_Value")
    private float runOutThrowerValue;

    @Column(name = "Run_Out_Value")
    private float runOutValue;

    @Column(name = "Strike_Rate")
    private float strikeRate;

    @Column(name = "Strike_Rate_Value")
    private String strikeRateValue;

    @Column(name = "Stumping")
    private float stumping;

    @Column(name = "Stumping_Value")
    private float stumpingValue;

    @Column(name = "Thirty_Runs")
    private float thirtyRuns;

    @Column(name = "Thirty_Runs_Value")
    private float thirtyRunsValue;

    @Column(name = "Three_Wicket")
    private float threeWicket;

    @Column(name = "Three_Wicket_Value")
    private float threeWicketValue;

    @Column(name = "Two_Wicket")
    private float twoWicket;

    @Column(name = "Two_Wicket_Value")
    private float twoWicketValue;

    private BigInteger updated;

    @Column(name = "Wicket")
    private float wicket;

    @Column(name = "Wicket_Value")
    private float wicketValue;

    public TblCricketMatchPlayersStat() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getBeingPartOfEleven() {
        return beingPartOfEleven;
    }

    public void setBeingPartOfEleven(float beingPartOfEleven) {
        this.beingPartOfEleven = beingPartOfEleven;
    }

    public float getBeingPartOfElevenValue() {
        return beingPartOfElevenValue;
    }

    public void setBeingPartOfElevenValue(float beingPartOfElevenValue) {
        this.beingPartOfElevenValue = beingPartOfElevenValue;
    }

    public float getCatch1() {
        return catch1;
    }

    public void setCatch1(float catch1) {
        this.catch1 = catch1;
    }

    public float getThreeCatch() {
        return threeCatch;
    }

    public void setThreeCatch(float threeCatch) {
        this.threeCatch = threeCatch;
    }

    public float getLbwBowled() {
        return lbwBowled;
    }

    public void setLbwBowled(float lbwBowled) {
        this.lbwBowled = lbwBowled;
    }

    public float getCatchAndBowled() {
        return catchAndBowled;
    }

    public void setCatchAndBowled(float catchAndBowled) {
        this.catchAndBowled = catchAndBowled;
    }

    public float getCatchAndBowledValue() {
        return catchAndBowledValue;
    }

    public void setCatchAndBowledValue(float catchAndBowledValue) {
        this.catchAndBowledValue = catchAndBowledValue;
    }

    public float getCatchValue() {
        return catchValue;
    }

    public void setCatchValue(float catchValue) {
        this.catchValue = catchValue;
    }

    public float getThreeCatchValue() {
        return threeCatchValue;
    }

    public void setThreeCatchValue(float threeCatchValue) {
        this.threeCatchValue = threeCatchValue;
    }

    public float getLbwBowledValue() {
        return lbwBowledValue;
    }

    public void setLbwBowledValue(float lbwBowledValue) {
        this.lbwBowledValue = lbwBowledValue;
    }

    public float getCentury() {
        return century;
    }

    public void setCentury(float century) {
        this.century = century;
    }

    public float getCenturyValue() {
        return centuryValue;
    }

    public void setCenturyValue(float centuryValue) {
        this.centuryValue = centuryValue;
    }

    public float getDismissForADuck() {
        return dismissForADuck;
    }

    public void setDismissForADuck(float dismissForADuck) {
        this.dismissForADuck = dismissForADuck;
    }

    public float getDismissForADuckValue() {
        return dismissForADuckValue;
    }

    public void setDismissForADuckValue(float dismissForADuckValue) {
        this.dismissForADuckValue = dismissForADuckValue;
    }

    public float getEconomyRate() {
        return economyRate;
    }

    public void setEconomyRate(float economyRate) {
        this.economyRate = economyRate;
    }

    public String getEconomyRateValue() {
        return economyRateValue;
    }

    public void setEconomyRateValue(String economyRateValue) {
        this.economyRateValue = economyRateValue;
    }

    public float getEveryBoundaryHit() {
        return everyBoundaryHit;
    }

    public void setEveryBoundaryHit(float everyBoundaryHit) {
        this.everyBoundaryHit = everyBoundaryHit;
    }

    public float getEveryBoundaryHitValue() {
        return everyBoundaryHitValue;
    }

    public void setEveryBoundaryHitValue(float everyBoundaryHitValue) {
        this.everyBoundaryHitValue = everyBoundaryHitValue;
    }

    public float getEveryRunScored() {
        return everyRunScored;
    }

    public void setEveryRunScored(float everyRunScored) {
        this.everyRunScored = everyRunScored;
    }

    public float getEveryRunScoredValue() {
        return everyRunScoredValue;
    }

    public void setEveryRunScoredValue(float everyRunScoredValue) {
        this.everyRunScoredValue = everyRunScoredValue;
    }

    public float getEverySixHit() {
        return everySixHit;
    }

    public void setEverySixHit(float everySixHit) {
        this.everySixHit = everySixHit;
    }

    public float getEverySixHitValue() {
        return everySixHitValue;
    }

    public void setEverySixHitValue(float everySixHitValue) {
        this.everySixHitValue = everySixHitValue;
    }

    public float getFiveWicket() {
        return fiveWicket;
    }

    public void setFiveWicket(float fiveWicket) {
        this.fiveWicket = fiveWicket;
    }

    public float getFiveWicketValue() {
        return fiveWicketValue;
    }

    public void setFiveWicketValue(float fiveWicketValue) {
        this.fiveWicketValue = fiveWicketValue;
    }

    public float getFourWicket() {
        return fourWicket;
    }

    public void setFourWicket(float fourWicket) {
        this.fourWicket = fourWicket;
    }

    public float getFourWicketValue() {
        return fourWicketValue;
    }

    public void setFourWicketValue(float fourWicketValue) {
        this.fourWicketValue = fourWicketValue;
    }

    public float getHalfCentury() {
        return halfCentury;
    }

    public void setHalfCentury(float halfCentury) {
        this.halfCentury = halfCentury;
    }

    public float getHalfCenturyValue() {
        return halfCenturyValue;
    }

    public void setHalfCenturyValue(float halfCenturyValue) {
        this.halfCenturyValue = halfCenturyValue;
    }

    public float getMaidenOver() {
        return maidenOver;
    }

    public void setMaidenOver(float maidenOver) {
        this.maidenOver = maidenOver;
    }

    public float getMaidenOverValue() {
        return maidenOverValue;
    }

    public void setMaidenOverValue(float maidenOverValue) {
        this.maidenOverValue = maidenOverValue;
    }

    public int getMatchUniqueId() {
        return matchUniqueId;
    }

    public void setMatchUniqueId(int matchUniqueId) {
        this.matchUniqueId = matchUniqueId;
    }

    public int getPlayerUniqueId() {
        return playerUniqueId;
    }

    public void setPlayerUniqueId(int playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public float getRunOut() {
        return runOut;
    }

    public void setRunOut(float runOut) {
        this.runOut = runOut;
    }

    public float getRunOutCatcher() {
        return runOutCatcher;
    }

    public void setRunOutCatcher(float runOutCatcher) {
        this.runOutCatcher = runOutCatcher;
    }

    public float getRunOutCatcherValue() {
        return runOutCatcherValue;
    }

    public void setRunOutCatcherValue(float runOutCatcherValue) {
        this.runOutCatcherValue = runOutCatcherValue;
    }

    public float getRunOutThrower() {
        return runOutThrower;
    }

    public void setRunOutThrower(float runOutThrower) {
        this.runOutThrower = runOutThrower;
    }

    public float getRunOutThrowerValue() {
        return runOutThrowerValue;
    }

    public void setRunOutThrowerValue(float runOutThrowerValue) {
        this.runOutThrowerValue = runOutThrowerValue;
    }

    public float getRunOutValue() {
        return runOutValue;
    }

    public void setRunOutValue(float runOutValue) {
        this.runOutValue = runOutValue;
    }

    public float getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(float strikeRate) {
        this.strikeRate = strikeRate;
    }

    public String getStrikeRateValue() {
        return strikeRateValue;
    }

    public void setStrikeRateValue(String strikeRateValue) {
        this.strikeRateValue = strikeRateValue;
    }

    public float getStumping() {
        return stumping;
    }

    public void setStumping(float stumping) {
        this.stumping = stumping;
    }

    public float getStumpingValue() {
        return stumpingValue;
    }

    public void setStumpingValue(float stumpingValue) {
        this.stumpingValue = stumpingValue;
    }

    public float getThirtyRuns() {
        return thirtyRuns;
    }

    public void setThirtyRuns(float thirtyRuns) {
        this.thirtyRuns = thirtyRuns;
    }

    public float getThirtyRunsValue() {
        return thirtyRunsValue;
    }

    public void setThirtyRunsValue(float thirtyRunsValue) {
        this.thirtyRunsValue = thirtyRunsValue;
    }

    public float getThreeWicket() {
        return threeWicket;
    }

    public void setThreeWicket(float threeWicket) {
        this.threeWicket = threeWicket;
    }

    public float getThreeWicketValue() {
        return threeWicketValue;
    }

    public void setThreeWicketValue(float threeWicketValue) {
        this.threeWicketValue = threeWicketValue;
    }

    public float getTwoWicket() {
        return twoWicket;
    }

    public void setTwoWicket(float twoWicket) {
        this.twoWicket = twoWicket;
    }

    public float getTwoWicketValue() {
        return twoWicketValue;
    }

    public void setTwoWicketValue(float twoWicketValue) {
        this.twoWicketValue = twoWicketValue;
    }

    public BigInteger getUpdated() {
        return updated;
    }

    public void setUpdated(BigInteger updated) {
        this.updated = updated;
    }

    public float getWicket() {
        return wicket;
    }

    public void setWicket(float wicket) {
        this.wicket = wicket;
    }

    public float getWicketValue() {
        return wicketValue;
    }

    public void setWicketValue(float wicketValue) {
        this.wicketValue = wicketValue;
    }

    public void updateStatsData(JSONObject playerData) {
        setBeingPartOfEleven(playerData.getFloat("Being_Part_Of_Eleven"));
        setEveryRunScored(playerData.getFloat("Every_Run_Scored"));
        setDismissForADuck(playerData.getFloat("Dismiss_For_A_Duck"));
        setEveryBoundaryHit(playerData.getFloat("Every_Boundary_Hit"));
        setEverySixHit(playerData.getFloat("Every_Six_Hit"));
        setHalfCentury(playerData.getFloat("Half_Century"));
        setCentury(playerData.getFloat("Century"));
        setThirtyRuns(playerData.getFloat("Thirty_Runs"));
        setWicket(playerData.getFloat("Wicket"));
        setMaidenOver(playerData.getFloat("Maiden_Over"));
        setFourWicket(playerData.getFloat("Four_Wicket"));
        setFiveWicket(playerData.getFloat("Five_Wicket"));
        setThreeWicket(playerData.getFloat("Three_Wicket"));
        setTwoWicket(playerData.getFloat("Two_Wicket"));
        setCatch1(playerData.getFloat("Catch"));
        setThreeCatch(playerData.getFloat("Three_Catch"));
        setLbwBowled(playerData.getFloat("Lbw_Bowled"));
        setCatchAndBowled(playerData.getFloat("Catch_And_Bowled"));
        setStumping(playerData.getFloat("Stumping"));
        setRunOut(playerData.getFloat("Run_Out"));
        setRunOutCatcher(playerData.getFloat("Run_Out_Catcher"));
        setRunOutThrower(playerData.getFloat("Run_Out_Thrower"));
        setStrikeRate(playerData.getFloat("Strike_Rate"));
        setEconomyRate(playerData.getFloat("Economy_Rate"));

        setBeingPartOfElevenValue(playerData.getFloat("Being_Part_Of_Eleven_Value"));
        setEveryRunScoredValue(playerData.getFloat("Every_Run_Scored_Value"));
        setDismissForADuckValue(playerData.getFloat("Dismiss_For_A_Duck_Value"));
        setEveryBoundaryHitValue(playerData.getFloat("Every_Boundary_Hit_Value"));
        setEverySixHitValue(playerData.getFloat("Every_Six_Hit_Value"));
        setHalfCenturyValue(playerData.getFloat("Half_Century_Value"));
        setCenturyValue(playerData.getFloat("Century_Value"));
        setThirtyRunsValue(playerData.getFloat("Thirty_Runs_Value"));
        setWicketValue(playerData.getFloat("Wicket_Value"));
        setMaidenOverValue(playerData.getFloat("Maiden_Over_Value"));
        setFourWicketValue(playerData.getFloat("Four_Wicket_Value"));
        setFiveWicketValue(playerData.getFloat("Five_Wicket_Value"));
        setThreeWicketValue(playerData.getFloat("Three_Wicket_Value"));
        setTwoWicketValue(playerData.getFloat("Two_Wicket_Value"));
        setCatchValue(playerData.getFloat("Catch_Value"));
        setThreeCatchValue(playerData.getFloat("Three_Catch_Value"));
        setLbwBowledValue(playerData.getFloat("Lbw_Bowled_Value"));
        setCatchValue(playerData.getFloat("Catch_Value"));
        setCatchAndBowledValue(playerData.getFloat("Catch_And_Bowled_Value"));
        setStumpingValue(playerData.getFloat("Stumping_Value"));
        setRunOutValue(playerData.getFloat("Run_Out_Value"));
        setRunOutCatcherValue(playerData.getFloat("Run_Out_Catcher_Value"));
        setRunOutThrowerValue(playerData.getFloat("Run_Out_Thrower_Value"));
        setStrikeRateValue(playerData.getString("Strike_Rate_Value"));
        setEconomyRateValue(playerData.getString("Economy_Rate_Value"));
    }

    public boolean checkSameStats(JSONObject playerData) {

        return getBeingPartOfEleven() == playerData.getFloat("Being_Part_Of_Eleven")
                && getEveryRunScored() == playerData.getFloat("Every_Run_Scored")
                && getDismissForADuck() == playerData.getFloat("Dismiss_For_A_Duck")
                && getEveryBoundaryHit() == playerData.getFloat("Every_Boundary_Hit")
                && getEverySixHit() == playerData.getFloat("Every_Six_Hit")
                && getHalfCentury() == playerData.getFloat("Half_Century")
                && getCentury() == playerData.getFloat("Century")
                && getThirtyRuns() == playerData.getFloat("Thirty_Runs")
                && getWicket() == playerData.getFloat("Wicket")
                && getMaidenOver() == playerData.getFloat("Maiden_Over")
                && getFourWicket() == playerData.getFloat("Four_Wicket")
                && getFiveWicket() == playerData.getFloat("Five_Wicket")
                && getThreeWicket() == playerData.getFloat("Three_Wicket")
                && getTwoWicket() == playerData.getFloat("Two_Wicket")
                && getCatch1() == playerData.getFloat("Catch")
                && getThreeCatch() == playerData.getFloat("Three_Catch")
                && getLbwBowled() == playerData.getFloat("Lbw_Bowled")
                && getCatchAndBowled() == playerData.getFloat("Catch_And_Bowled")
                && getStumping() == playerData.getFloat("Stumping")
                && getRunOut() == playerData.getFloat("Run_Out")
                && getRunOutCatcher() == playerData.getFloat("Run_Out_Catcher")
                && getRunOutThrower() == playerData.getFloat("Run_Out_Thrower")
                && getStrikeRate() == playerData.getFloat("Strike_Rate")
                && getEconomyRate() == playerData.getFloat("Economy_Rate")

                && getBeingPartOfElevenValue() == playerData.getFloat("Being_Part_Of_Eleven_Value")
                && getEveryRunScoredValue() == playerData.getFloat("Every_Run_Scored_Value")
                && getDismissForADuckValue() == playerData.getFloat("Dismiss_For_A_Duck_Value")
                && getEveryBoundaryHitValue() == playerData.getFloat("Every_Boundary_Hit_Value")
                && getEverySixHitValue() == playerData.getFloat("Every_Six_Hit_Value")
                && getHalfCenturyValue() == playerData.getFloat("Half_Century_Value")
                && getCenturyValue() == playerData.getFloat("Century_Value")
                && getThirtyRunsValue() == playerData.getFloat("Thirty_Runs_Value")
                && getWicketValue() == playerData.getFloat("Wicket_Value")
                && getMaidenOverValue() == playerData.getFloat("Maiden_Over_Value")
                && getFourWicketValue() == playerData.getFloat("Four_Wicket_Value")
                && getFiveWicketValue() == playerData.getFloat("Five_Wicket_Value")
                && getThreeWicketValue() == playerData.getFloat("Three_Wicket_Value")
                && getTwoWicketValue() == playerData.getFloat("Two_Wicket_Value")
                && getCatchValue() == playerData.getFloat("Catch_Value")
                && getThreeCatchValue() == playerData.getFloat("Three_Catch_Value")
                && getLbwBowledValue() == playerData.getFloat("Lbw_Bowled_Value")
                && getCatchAndBowledValue() == playerData.getFloat("Catch_And_Bowled_Value")
                && getStumpingValue() == playerData.getFloat("Stumping_Value")
                && getRunOutValue() == playerData.getFloat("Run_Out_Value")
                && getRunOutCatcherValue() == playerData.getFloat("Run_Out_Catcher_Value")
                && getRunOutThrowerValue() == playerData.getFloat("Run_Out_Thrower_Value")
                && getStrikeRateValue().equals(playerData.getString("Strike_Rate_Value"))
                && getEconomyRateValue().equals(playerData.getString("Economy_Rate_Value"));

    }

}