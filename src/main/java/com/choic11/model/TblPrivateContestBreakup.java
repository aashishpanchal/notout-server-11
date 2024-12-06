package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_private_contest_breakups database table.
 * 
 */
@Entity
@Table(name="tbl_private_contest_breakups")
@NamedQuery(name="TblPrivateContestBreakup.findAll", query="SELECT t FROM TblPrivateContestBreakup t")
public class TblPrivateContestBreakup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Lob
	@Column(name="contest_json")
	private String contestJson;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="total_winners")
	private int totalWinners;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblPrivateContestBreakup() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContestJson() {
		return this.contestJson;
	}

	public void setContestJson(String contestJson) {
		this.contestJson = contestJson;
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

	public int getTotalWinners() {
		return this.totalWinners;
	}

	public void setTotalWinners(int totalWinners) {
		this.totalWinners = totalWinners;
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

}