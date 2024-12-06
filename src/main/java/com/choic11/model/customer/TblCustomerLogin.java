package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_customer_logins database table.
 * 
 */
@Entity
@Table(name = "tbl_customer_logins")

public class TblCustomerLogin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Lob
	@Column(name = "app_info")
	private String appInfo;

	private BigInteger created;

	@Column(name = "device_id")
	private String deviceId;

	@Lob
	@Column(name = "device_info")
	private String deviceInfo;

	@Column(name = "device_token")
	private String deviceToken;

	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "login_time")
	private BigInteger loginTime;

	@Column(name = "customer_id")
	private Integer customerId;

	public TblCustomerLogin() {
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

}