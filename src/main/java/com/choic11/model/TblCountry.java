package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_countries database table.
 * 
 */
@Entity
@Table(name = "tbl_countries")
@NamedQuery(name = "TblCountry.findAll", query = "SELECT t FROM TblCountry t")
public class TblCountry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "is_deleted")
	private String isDeleted;

	private String name;

	private String status;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	public TblCountry() {
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

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public static TblCountry getInstance() {
		TblCountry tblCountry = new TblCountry();
		tblCountry.setCreatedAt(BigInteger.valueOf(0));
		tblCountry.setCreatedBy(0);
		tblCountry.setUpdatedAt(BigInteger.valueOf(0));
		tblCountry.setUpdatedBy(0);
		tblCountry.setIsDeleted("N");
		tblCountry.setStatus("A");

		return tblCountry;
	}

}