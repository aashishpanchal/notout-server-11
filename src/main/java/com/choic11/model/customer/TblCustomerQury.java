package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_customer_quries database table.
 * 
 */
@Entity
@Table(name="tbl_customer_quries")
@NamedQuery(name="TblCustomerQury.findAll", query="SELECT t FROM TblCustomerQury t")
public class TblCustomerQury implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name="is_deleted")
	private String isDeleted;

	@Lob
	private String message;

	@Lob
	@Column(name="response_message")
	private String responseMessage;

	@Column(name="response_time")
	private BigInteger responseTime;

	private String status;

	@Lob
	private String subject;

	@Column(name="ticket_id")
	private String ticketId;
	
	@Column(name="customer_id")
	private int customerId;

	public TblCustomerQury() {
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

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResponseMessage() {
		return this.responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public BigInteger getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(BigInteger responseTime) {
		this.responseTime = responseTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTicketId() {
		return this.ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	public int getCustomerId() {
		return customerId;
	}
	
	public static TblCustomerQury getInstance() {
		TblCustomerQury tblCustomerQury=new TblCustomerQury();
		tblCustomerQury.isDeleted="N";
		tblCustomerQury.status="P";
		tblCustomerQury.responseTime=BigInteger.valueOf(0);
		return tblCustomerQury;
		
	}

}