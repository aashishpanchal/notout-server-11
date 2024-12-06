package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_team_setting database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_team_setting")
@NamedQuery(name="TblSoccerTeamSetting.findAll", query="SELECT t FROM TblSoccerTeamSetting t")
public class TblSoccerTeamSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	private String key;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	private float value;

	public TblSoccerTeamSetting() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public BigInteger getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}