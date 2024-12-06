package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the
 * tbl_affiliate_withdraw_request_approve_reject_logs database table.
 * 
 */
@Entity
@Table(name = "tbl_affiliate_withdraw_request_approve_reject_logs")
@NamedQuery(name = "TblAffiliateWithdrawRequestApproveRejectLog.findAll", query = "SELECT t FROM TblAffiliateWithdrawRequestApproveRejectLog t")
public class TblAffiliateWithdrawRequestApproveRejectLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String action;

	@Column(name = "admin_id")
	private int adminId;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "entry_id")
	private int entryId;

	@Column(name = "ip_address")
	private String ipAddress;

	private String remark;

	public TblAffiliateWithdrawRequestApproveRejectLog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getAdminId() {
		return this.adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public BigInteger getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public int getEntryId() {
		return this.entryId;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static TblAffiliateWithdrawRequestApproveRejectLog getInstance() {
		TblAffiliateWithdrawRequestApproveRejectLog tblAffiliateWithdrawRequestApproveRejectLog = new TblAffiliateWithdrawRequestApproveRejectLog();
		return tblAffiliateWithdrawRequestApproveRejectLog;
	}

}