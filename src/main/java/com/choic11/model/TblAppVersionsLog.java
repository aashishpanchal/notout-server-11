package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_app_versions_logs database table.
 * 
 */
@Entity
@Table(name="tbl_app_versions_logs")
@NamedQuery(name="TblAppVersionsLog.findAll", query="SELECT t FROM TblAppVersionsLog t")
public class TblAppVersionsLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int created;

	private int createdby;

	@Column(name="device_type")
	private String deviceType;

	@Column(name="update_type")
	private String updateType;

	@Column(name="version_code")
	private int versionCode;

	@Lob
	@Column(name="version_desc")
	private String versionDesc;

	@Column(name="version_name")
	private String versionName;

	public TblAppVersionsLog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCreated() {
		return this.created;
	}

	public void setCreated(int created) {
		this.created = created;
	}

	public int getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(int createdby) {
		this.createdby = createdby;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getUpdateType() {
		return this.updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public int getVersionCode() {
		return this.versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionDesc() {
		return this.versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

}