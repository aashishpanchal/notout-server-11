package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_customer_wallet_payment_logs database table.
 * 
 */
@Entity
@Table(name="tbl_customer_wallet_payment_logs")
@NamedQuery(name="TblCustomerWalletPaymentLog.findAll", query="SELECT t FROM TblCustomerWalletPaymentLog t")
public class TblCustomerWalletPaymentLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private float amount;

	private BigInteger created;

	@Column(name="order_id")
	private String orderId;

	@Column(name="payment_from")
	private String paymentFrom;

	@Column(name="payment_gateway")
	private String paymentGateway;

	@Column(name="refrence_id")
	private String refrenceId;

	@Lob
	@Column(name="request_json")
	private String requestJson;

	@Lob
	@Column(name="response_json")
	private String responseJson;

	private String status;

	private BigInteger updated;

	
	
	@Column(name="customer_id")
	private int customerId;

	public TblCustomerWalletPaymentLog() {
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

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentFrom() {
		return this.paymentFrom;
	}

	public void setPaymentFrom(String paymentFrom) {
		this.paymentFrom = paymentFrom;
	}

	public String getPaymentGateway() {
		return this.paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public String getRefrenceId() {
		return this.refrenceId;
	}

	public void setRefrenceId(String refrenceId) {
		this.refrenceId = refrenceId;
	}

	public String getRequestJson() {
		return this.requestJson;
	}

	public void setRequestJson(String requestJson) {
		this.requestJson = requestJson;
	}

	public String getResponseJson() {
		return this.responseJson;
	}

	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigInteger getUpdated() {
		return this.updated;
	}

	public void setUpdated(BigInteger updated) {
		this.updated = updated;
	}

	public int getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

}