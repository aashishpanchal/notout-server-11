package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_customer_bankdetail database table.
 * 
 */
@Entity
@Table(name="tbl_customer_bankdetail")
@NamedQuery(name="TblCustomerBankdetail.findAll", query="SELECT t FROM TblCustomerBankdetail t")
public class TblCustomerBankdetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="account_number")
	private String accountNumber;

	@Column(name="approved_at")
	private BigInteger approvedAt;

	@Column(name="created_by")
	private int createdBy;

	private BigInteger createdat;

	@Column(name="customer_id")
	private int customerId;

	private String ifsc;

	private String image;

	private String name;

	@Lob
	private String reason;

	@Column(name="rejected_at")
	private BigInteger rejectedAt;

	private String status;

	@Column(name="updated_by")
	private int updatedBy;

	private BigInteger updatedat;

	

	public TblCustomerBankdetail() {
	}

	public TblCustomerBankdetail(int id) {
		this.id=id;
	}
	
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigInteger getApprovedAt() {
		return this.approvedAt;
	}

	public void setApprovedAt(BigInteger approvedAt) {
		this.approvedAt = approvedAt;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public BigInteger getCreatedat() {
		return this.createdat;
	}

	public void setCreatedat(BigInteger createdat) {
		this.createdat = createdat;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getIfsc() {
		return this.ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public BigInteger getRejectedAt() {
		return this.rejectedAt;
	}

	public void setRejectedAt(BigInteger rejectedAt) {
		this.rejectedAt = rejectedAt;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public BigInteger getUpdatedat() {
		return this.updatedat;
	}

	public void setUpdatedat(BigInteger updatedat) {
		this.updatedat = updatedat;
	}
	
	public static TblCustomerBankdetail getInstance() {
		TblCustomerBankdetail tblCustomerBankdetail = new TblCustomerBankdetail();

		tblCustomerBankdetail.status = "P";
		tblCustomerBankdetail.createdat = BigInteger.valueOf(0);
		tblCustomerBankdetail.updatedat = BigInteger.valueOf(0);
		tblCustomerBankdetail.approvedAt = BigInteger.valueOf(0);
		tblCustomerBankdetail.rejectedAt = BigInteger.valueOf(0);
		tblCustomerBankdetail.createdBy = 0;
		tblCustomerBankdetail.updatedBy = 0;

		return tblCustomerBankdetail;
	}

	

}