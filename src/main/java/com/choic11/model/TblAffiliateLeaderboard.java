package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="tbl_affiliate_leaderboard")
@NamedQuery(name="TblAffiliateLeaderboard.findAll", query="SELECT t FROM TblAffiliateLeaderboard t")
public class TblAffiliateLeaderboard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="start_date")
	private BigInteger startDate;

	@Column(name="end_date")
	private BigInteger endDate;

	@Column(name="criteria")
	private String criteria;

	@Column(name="total_price")
	private float totalPrice;

	@Lob
	@Column(name="price_json")
	private String priceJson;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="result_declared")
	private String resultDeclared;

	@Column(name="status")
	private String status;

	@Column(name="is_deleted")
	private String isDeleted;

	@Lob
	@Column(name="tnc")
	private String tnc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getStartDate() {
		return startDate;
	}

	public void setStartDate(BigInteger startDate) {
		this.startDate = startDate;
	}

	public BigInteger getEndDate() {
		return endDate;
	}

	public void setEndDate(BigInteger endDate) {
		this.endDate = endDate;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPriceJson() {
		return priceJson;
	}

	public void setPriceJson(String priceJson) {
		this.priceJson = priceJson;
	}

	public BigInteger getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public BigInteger getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getResultDeclared() {
		return resultDeclared;
	}

	public void setResultDeclared(String resultDeclared) {
		this.resultDeclared = resultDeclared;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getTnc() {
		return tnc;
	}

	public void setTnc(String tnc) {
		this.tnc = tnc;
	}
}