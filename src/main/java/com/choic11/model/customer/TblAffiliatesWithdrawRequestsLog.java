package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_affiliates_withdraw_requests_logs database
 * table.
 * 
 */
@Entity
@Table(name = "tbl_affiliates_withdraw_requests_logs")
@NamedQuery(name = "TblAffiliatesWithdrawRequestsLog.findAll", query = "SELECT t FROM TblAffiliatesWithdrawRequestsLog t")
public class TblAffiliatesWithdrawRequestsLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name = "customer_id")
	private int customerId;

	@Column(name = "entry_id")
	private int entryId;

	@Lob
	private String json;

	@Column(name = "payout_id")
	private String payoutId;

	public TblAffiliatesWithdrawRequestsLog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getEntryId() {
		return this.entryId;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public String getJson() {
		return this.json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getPayoutId() {
		return this.payoutId;
	}

	public void setPayoutId(String payoutId) {
		this.payoutId = payoutId;
	}

	public static TblAffiliatesWithdrawRequestsLog getInstance() {
		return new TblAffiliatesWithdrawRequestsLog();
	}

}