package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_settings database table.
 * 
 */
@Entity
@Table(name="tbl_settings")
@NamedQuery(name="TblSetting.findAll", query="SELECT t FROM TblSetting t")
public class TblSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="created_at")
	private int createdAt;

	@Column(name="created_by")
	private int createdBy;

	private int height;

	private String key;

	private String label;

	@Column(name="updated_at")
	private int updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	private String value;

	private int width;

	public TblSetting() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(int updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}