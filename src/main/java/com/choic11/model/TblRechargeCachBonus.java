package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_recharge_cach_bonus database table.
 * 
 */
@Entity
@Table(name="tbl_recharge_cach_bonus")
@NamedQuery(name="TblRechargeCachBonus.findAll", query="SELECT t FROM TblRechargeCachBonus t")
public class TblRechargeCachBonus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="cach_bonus")
	private float cachBonus;

	@Column(name="cash_bonus_type")
	private String cashBonusType;

	private String code;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Column(name="end_date")
	private BigInteger endDate;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="is_use")
	private String isUse;

	@Column(name="is_use_max")
	private int isUseMax;

	@Column(name="max_recharge")
	private float maxRecharge;

	private float recharge;

	@Column(name="start_date")
	private BigInteger startDate;

	private String status;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblRechargeCachBonus() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getCachBonus() {
		return this.cachBonus;
	}

	public void setCachBonus(float cachBonus) {
		this.cachBonus = cachBonus;
	}

	public String getCashBonusType() {
		return this.cashBonusType;
	}

	public void setCashBonusType(String cashBonusType) {
		this.cashBonusType = cashBonusType;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public BigInteger getEndDate() {
		return this.endDate;
	}

	public void setEndDate(BigInteger endDate) {
		this.endDate = endDate;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsUse() {
		return this.isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public int getIsUseMax() {
		return this.isUseMax;
	}

	public void setIsUseMax(int isUseMax) {
		this.isUseMax = isUseMax;
	}

	public float getMaxRecharge() {
		return this.maxRecharge;
	}

	public void setMaxRecharge(float maxRecharge) {
		this.maxRecharge = maxRecharge;
	}

	public float getRecharge() {
		return this.recharge;
	}

	public void setRecharge(float recharge) {
		this.recharge = recharge;
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