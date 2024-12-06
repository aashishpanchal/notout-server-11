package com.choic11.model.customer;

import com.choic11.model.TblState;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_customer_paincard database table.
 * 
 */
@Entity
@Table(name = "tbl_customer_paincard")
@NamedQuery(name = "TblCustomerPaincard.findAll", query = "SELECT t FROM TblCustomerPaincard t")
public class TblCustomerPaincard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "approved_at")
	private BigInteger approvedAt;

	@Column(name = "created_by")
	private int createdBy;

	private BigInteger createdat;

	@Column(name = "customer_id")
	private int customerId;

	private String dob;

	private String image;

	private String name;

	@Column(name = "pain_number")
	private String painNumber;

	@Lob
	private String reason;

	@Column(name = "rejected_at")
	private BigInteger rejectedAt;

	@Column(name = "state")
	private int state;

	@ManyToOne
	@JoinColumn(name = "state", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private TblState tblState;

	private String status;

	@Column(name = "updated_by")
	private int updatedBy;

	private BigInteger updatedat;

	public TblCustomerPaincard() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getDob() {
		return this.dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
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

	public String getPainNumber() {
		return this.painNumber;
	}

	public void setPainNumber(String painNumber) {
		this.painNumber = painNumber;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public TblState getTblState() {
		return tblState;
	}

	public void setTblState(TblState tblState) {
		this.tblState = tblState;
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

	public static TblCustomerPaincard getInstance() {
		TblCustomerPaincard tblCustomerPaincard = new TblCustomerPaincard();

		tblCustomerPaincard.status = "P";
		tblCustomerPaincard.createdat = BigInteger.valueOf(0);
		tblCustomerPaincard.updatedat = BigInteger.valueOf(0);
		tblCustomerPaincard.approvedAt = BigInteger.valueOf(0);
		tblCustomerPaincard.rejectedAt = BigInteger.valueOf(0);
		tblCustomerPaincard.createdBy = 0;
		tblCustomerPaincard.updatedBy = 0;

		return tblCustomerPaincard;
	}

}