package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the tbl_tempcustomers database table.
 * 
 */
@Entity
@Table(name="tbl_tempcustomers")
@NamedQuery(name="TblTempcustomer.findAll", query="SELECT t FROM TblTempcustomer t")
public class TblTempcustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	private Timestamp addedat;

	@Column(name="country_mobile_code")
	private String countryMobileCode;

	@Lob
	@Column(name="customer_data")
	private String customerData;
	@Column(name="isverified",nullable = false )
	private String isverified="NO";

	private String mobileno;

	private String otp;

	@Column(name="repeat_count")
	private int repeatCount;

	private String type;

	public TblTempcustomer() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getAddedat() {
		return this.addedat;
	}

	public void setAddedat(Timestamp addedat) {
		this.addedat = addedat;
	}

	public String getCountryMobileCode() {
		return this.countryMobileCode;
	}

	public void setCountryMobileCode(String countryMobileCode) {
		this.countryMobileCode = countryMobileCode;
	}

	public String getCustomerData() {
		return this.customerData;
	}

	public void setCustomerData(String customerData) {
		this.customerData = customerData;
	}

	public String getIsverified() {
		return this.isverified;
	}

	public void setIsverified(String isverified) {
		this.isverified = isverified;
	}

	public String getMobileno() {
		return this.mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getOtp() {
		return this.otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public int getRepeatCount() {
		return this.repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}