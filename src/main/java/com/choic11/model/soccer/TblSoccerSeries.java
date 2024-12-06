package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_series database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_series")
@NamedQuery(name="TblSoccerSeries.findAll", query="SELECT t FROM TblSoccerSeries t")
public class TblSoccerSeries implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String abbr;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="is_leaderboard_available")
	private String isLeaderboardAvailable;

	@Column(name="is_show_in_leaderboard")
	private String isShowInLeaderboard;

	@Column(name="is_result_declared")
	private String isResultDeclared;

	@Lob
	@Column(name="tnc")
	private String tnc;

	@Lob
	@Column(name="prize_json")
	private String prizeJson;

	@Column(name="total_price")
	private float totalPrice;

	private String name;

	private String season;

	private String status;

	private String type;

	private Integer uniqueid;

	@Column(name="order_pos")
	private Integer leaderboardOrder;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;


	public TblSoccerSeries() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAbbr() {
		return this.abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
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

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsLeaderboardAvailable() {
		return isLeaderboardAvailable;
	}

	public void setIsLeaderboardAvailable(String isLeaderboardAvailable) {
		this.isLeaderboardAvailable = isLeaderboardAvailable;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getUniqueid() {
		return this.uniqueid;
	}

	public void setUniqueid(Integer uniqueid) {
		this.uniqueid = uniqueid;
	}

	public Integer getLeaderboardOrder() {
		return leaderboardOrder;
	}

	public void setLeaderboardOrder(Integer leaderboardOrder) {
		this.leaderboardOrder = leaderboardOrder;
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

	public String getIsShowInLeaderboard() {
		return isShowInLeaderboard;
	}

	public void setIsShowInLeaderboard(String isShowInLeaderboard) {
		this.isShowInLeaderboard = isShowInLeaderboard;
	}

	public String getIsResultDeclared() {
		return isResultDeclared;
	}

	public void setIsResultDeclared(String isResultDeclared) {
		this.isResultDeclared = isResultDeclared;
	}

	public String getTnc() {
		return tnc;
	}

	public void setTnc(String tnc) {
		this.tnc = tnc;
	}

	public String getPrizeJson() {
		return prizeJson;
	}

	public void setPrizeJson(String prizeJson) {
		this.prizeJson = prizeJson;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean isResultDeclared(){
		return isResultDeclared.equals("Y");
	}

	public boolean isResultRunning(){
		return isResultDeclared.equals("R");
	}
}