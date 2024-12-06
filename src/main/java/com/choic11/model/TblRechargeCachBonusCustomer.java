package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the tbl_recharge_cach_bonus_customers database
 * table.
 * 
 */
@Entity
@Table(name = "tbl_recharge_cach_bonus_customers")
@NamedQuery(name = "TblRechargeCachBonusCustomer.findAll", query = "SELECT t FROM TblRechargeCachBonusCustomer t")
public class TblRechargeCachBonusCustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private float amount;

	@Column(name = "cach_bonus")
	private float cachBonus;

	@Column(name = "cash_back")
	private float cashBack;

	@Column(name = "customer_id")
	private int customerId;

	@Column(name = "rcb_id")
	private int rcbId;

	@Column(name = "used_count")
	private int usedCount;

	public TblRechargeCachBonusCustomer() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getCachBonus() {
		return this.cachBonus;
	}

	public void setCachBonus(float cachBonus) {
		this.cachBonus = cachBonus;
	}

	public float getCashBack() {
		return this.cashBack;
	}

	public void setCashBack(float cashBack) {
		this.cashBack = cashBack;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getRcbId() {
		return this.rcbId;
	}

	public void setRcbId(int rcbId) {
		this.rcbId = rcbId;
	}

	public int getUsedCount() {
		return this.usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public static TblRechargeCachBonusCustomer getInstance() {
		TblRechargeCachBonusCustomer tblRechargeCachBonusCustomer = new TblRechargeCachBonusCustomer();

		return tblRechargeCachBonusCustomer;
	}

}