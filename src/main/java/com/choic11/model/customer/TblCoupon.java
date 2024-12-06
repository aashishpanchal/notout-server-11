package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_coupons database table.
 * 
 */
@Entity
@Table(name="tbl_coupons")
@NamedQuery(name="TblCoupon.findAll", query="SELECT t FROM TblCoupon t")
public class TblCoupon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private float bonus;

	@Column(name="coupon_code")
	private String couponCode;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Lob
	@Column(name="customer_ids")
	private String customerIds;

	private float deposite;

	@Lob
	private String description;

	@Column(name="end_date")
	private BigInteger endDate;

	private String image;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="start_date")
	private BigInteger startDate;

	private String status;

	private String title;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblCoupon() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getBonus() {
		return this.bonus;
	}

	public void setBonus(float bonus) {
		this.bonus = bonus;
	}

	public String getCouponCode() {
		return this.couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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

	public String getCustomerIds() {
		return this.customerIds;
	}

	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
	}

	public float getDeposite() {
		return this.deposite;
	}

	public void setDeposite(float deposite) {
		this.deposite = deposite;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getEndDate() {
		return this.endDate;
	}

	public void setEndDate(BigInteger endDate) {
		this.endDate = endDate;
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

	public BigInteger getStartDate() {
		return this.startDate;
	}

	public void setStartDate(BigInteger startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
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