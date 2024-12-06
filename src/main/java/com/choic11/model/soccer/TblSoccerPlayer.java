package com.choic11.model.soccer;

import com.choic11.model.TblCountry;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_soccer_players database table.
 * 
 */
@Entity
@Table(name = "tbl_soccer_players")
@NamedQuery(name = "TblSoccerPlayer.findAll", query = "SELECT t FROM TblSoccerPlayer t")
public class TblSoccerPlayer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String bets;

	private String bowls;

	@Column(name = "country_id")
	private int countryId;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	private String dob;

	@Column(name = "is_deleted")
	private String isDeleted;
	
	@Column(name = "is_summary_updated")
	private String isSummaryUpdated;

	private String name;

	private String position;

	@Column(name = "short_name")
	private String shortName;

	private String status;

	@Lob
	private String summary;

	@Column(name = "uniqueid")
	private Integer uniqueId;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	@OneToOne
	@JoinColumn(name = "country_id", referencedColumnName = "id", insertable = false, updatable = false)
	TblCountry tblCountry;

	public TblSoccerPlayer() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBets() {
		return this.bets;
	}

	public void setBets(String bets) {
		this.bets = bets;
	}

	public String getBowls() {
		return this.bowls;
	}

	public void setBowls(String bowls) {
		this.bowls = bowls;
	}

	public int getCountryId() {
		return this.countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
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

	public String getDob() {
		return this.dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getIsSummaryUpdated() {
		return isSummaryUpdated;
	}
	
	public void setIsSummaryUpdated(String isSummaryUpdated) {
		this.isSummaryUpdated = isSummaryUpdated;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
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

	public TblCountry getTblCountry() {
		return tblCountry;
	}

	public void setTblCountry(TblCountry tblCountry) {
		this.tblCountry = tblCountry;
	}

}