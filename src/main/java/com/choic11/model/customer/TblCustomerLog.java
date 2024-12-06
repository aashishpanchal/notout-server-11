package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_customer_logs database table.
 * 
 */
@Entity
@Table(name="tbl_customer_logs")
@NamedQuery(name="TblCustomerLog.findAll", query="SELECT t FROM TblCustomerLog t")
public class TblCustomerLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="app_info")
	private String appInfo;

	private BigInteger created;

	@Column(name="device_id")
	private String deviceId;

	@Column(name="device_info")
	private String deviceInfo;

	@Column(name="device_token")
	private String deviceToken;

	@Column(name="device_type")
	private String deviceType;

	@Column(name="ip_address")
	private String ipAddress;

	@Column(name="login_time")
	private BigInteger loginTime;

	@Column(name="logout_time")
	private BigInteger logoutTime;
	@Column(name="customer_id")
	private Integer customerId;
	@ManyToOne
	@JoinColumn(name="customer_id",referencedColumnName="id",insertable = false,updatable = false)
	private TblCustomer tblCustomer;

	public TblCustomerLog() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppInfo() {
		return this.appInfo;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceInfo() {
		return this.deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getDeviceToken() {
		return this.deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public BigInteger getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(BigInteger loginTime) {
		this.loginTime = loginTime;
	}

	public BigInteger getLogoutTime() {
		return this.logoutTime;
	}

	public void setLogoutTime(BigInteger logoutTime) {
		this.logoutTime = logoutTime;
	}

	public TblCustomer getTblCustomer() {
		return this.tblCustomer;
	}

	public void setTblCustomer(TblCustomer tblCustomer) {
		this.tblCustomer = tblCustomer;
	}

}