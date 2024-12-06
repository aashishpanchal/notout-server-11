package com.choic11.model.basketball;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_basketball_match_players_stats database table.
 */
@Entity
@Table(name = "tbl_basketball_match_players_stats")
@NamedQuery(name = "TblBasketballMatchPlayersStat.findAll", query = "SELECT t FROM TblBasketballMatchPlayersStat t")
public class TblBasketballMatchPlayersStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "match_unique_id")
    private Integer matchUniqueId;

    @Column(name = "player_unique_id")
    private Integer playerUniqueId;

    @Column(name = "Points_Scored")
    private float pointsScored;

    @Column(name = "Points_Scored_Value")
    private float pointsScoredValue;

    @Column(name = "Rebounds")
    private float rebounds;

    @Column(name = "Rebounds_Value")
    private float reboundsValue;

    @Column(name = "Assists")
    private float assists;

    @Column(name = "Assists_Value")
    private float assistsValue;

    @Column(name = "Steals")
    private float steals;

    @Column(name = "Steals_Value")
    private float stealsValue;

    @Column(name = "Blocks")
    private float blocks;

    @Column(name = "Blocks_Value")
    private float blocksValue;

    @Column(name = "Turn_Overs")
    private float turnOvers;

    @Column(name = "Turn_Overs_Value")
    private float turnOversValue;

    @Column(name = "updated")
    private BigInteger updated;

    public int getId() {
        return id;
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

    public float getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(float pointsScored) {
        this.pointsScored = pointsScored;
    }

    public float getPointsScoredValue() {
        return pointsScoredValue;
    }

    public void setPointsScoredValue(float pointsScoredValue) {
        this.pointsScoredValue = pointsScoredValue;
    }

    public float getRebounds() {
        return rebounds;
    }

    public void setRebounds(float rebounds) {
        this.rebounds = rebounds;
    }

    public float getReboundsValue() {
        return reboundsValue;
    }

    public void setReboundsValue(float reboundsValue) {
        this.reboundsValue = reboundsValue;
    }

    public float getAssists() {
        return assists;
    }

    public void setAssists(float assists) {
        this.assists = assists;
    }

    public float getAssistsValue() {
        return assistsValue;
    }

    public void setAssistsValue(float assistsValue) {
        this.assistsValue = assistsValue;
    }

    public float getSteals() {
        return steals;
    }

    public void setSteals(float steals) {
        this.steals = steals;
    }

    public float getStealsValue() {
        return stealsValue;
    }

    public void setStealsValue(float stealsValue) {
        this.stealsValue = stealsValue;
    }

    public float getBlocks() {
        return blocks;
    }

    public void setBlocks(float blocks) {
        this.blocks = blocks;
    }

    public float getBlocksValue() {
        return blocksValue;
    }

    public void setBlocksValue(float blocksValue) {
        this.blocksValue = blocksValue;
    }

    public float getTurnOvers() {
        return turnOvers;
    }

    public void setTurnOvers(float turnOvers) {
        this.turnOvers = turnOvers;
    }

    public float getTurnOversValue() {
        return turnOversValue;
    }

    public void setTurnOversValue(float turnOversValue) {
        this.turnOversValue = turnOversValue;
    }

    public BigInteger getUpdated() {
        return updated;
    }

    public void setUpdated(BigInteger updated) {
        this.updated = updated;
    }

    public void updateStatsData(JSONObject playerData) {
        setPointsScored(playerData.getFloat("Points_Scored"));
        setPointsScoredValue(playerData.getFloat("Points_Scored_Value"));
        setRebounds(playerData.getFloat("Rebounds"));
        setReboundsValue(playerData.getFloat("Rebounds_Value"));
        setAssists(playerData.getFloat("Assists"));
        setAssistsValue(playerData.getFloat("Assists_Value"));
        setSteals(playerData.getFloat("Steals"));
        setStealsValue(playerData.getFloat("Steals_Value"));
        setBlocks(playerData.getFloat("Blocks"));
        setBlocksValue(playerData.getFloat("Blocks_Value"));
        setTurnOvers(playerData.getFloat("Turn_Overs"));
        setTurnOversValue(playerData.getFloat("Turn_Overs_Value"));
    }

    public boolean checkSameStats(JSONObject playerData) {
        return
                getPointsScored() == playerData.getFloat("Points_Scored") &&
                        getPointsScoredValue() == playerData.getFloat("Points_Scored_Value") &&
                        getRebounds() == playerData.getFloat("Rebounds") &&
                        getReboundsValue() == playerData.getFloat("Rebounds_Value") &&
                        getAssists() == playerData.getFloat("Assists") &&
                        getAssistsValue() == playerData.getFloat("Assists_Value") &&
                        getSteals() == playerData.getFloat("Steals") &&
                        getStealsValue() == playerData.getFloat("Steals_Value") &&
                        getBlocks() == playerData.getFloat("Blocks") &&
                        getBlocksValue() == playerData.getFloat("Blocks_Value") &&
                        getTurnOvers() == playerData.getFloat("Turn_Overs") &&
                        getTurnOversValue() == playerData.getFloat("Turn_Overs_Value");
    }

}