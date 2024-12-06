package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_quotations database table.
 * 
 */
@Entity
@Table(name="tbl_quotations")
@NamedQuery(name="TblQuotation.findAll", query="SELECT t FROM TblQuotation t")
public class TblQuotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Column(name="expiry_date")
	private BigInteger expiryDate;

	@Column(name="game_id")
	private int gameId;

	private String height;

	private String image;

	@Column(name="is_deleted")
	private String isDeleted;

	private String link;

	private String status;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	private String width;

	public TblQuotation() {
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

	public BigInteger getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(BigInteger expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getGameId() {
		return this.gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
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

	public String getWidth() {
		return this.width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}