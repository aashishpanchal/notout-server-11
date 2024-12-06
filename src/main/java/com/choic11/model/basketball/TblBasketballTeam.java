package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_basketball_teams database table.
 * 
 */
@Entity
@Table(name = "tbl_basketball_teams")
@NamedQuery(name = "TblBasketballTeam.findAll", query = "SELECT t FROM TblBasketballTeam t")
public class TblBasketballTeam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "is_deleted")
	private String isDeleted;

	private String logo;

	private String name;

	@Column(name = "sort_name")
	private String sortName;

	private String status;

	@Column(name = "unique_id")
	private Integer uniqueId;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	public TblBasketballTeam() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortName() {
		return this.sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getUniqueId() {
		return this.uniqueId;
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

}