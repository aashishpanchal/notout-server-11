package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_private_contest_breakup_rules database table.
 * 
 */
@Entity
@Table(name="tbl_private_contest_breakup_rules")
@NamedQuery(name="TblPrivateContestBreakupRule.findAll", query="SELECT t FROM TblPrivateContestBreakupRule t")
public class TblPrivateContestBreakupRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="breakup_ids")
	private String breakupIds;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="max_contest_size")
	private int maxContestSize;

	@Column(name="min_contest_size")
	private int minContestSize;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblPrivateContestBreakupRule() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBreakupIds() {
		return this.breakupIds;
	}

	public void setBreakupIds(String breakupIds) {
		this.breakupIds = breakupIds;
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

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getMaxContestSize() {
		return this.maxContestSize;
	}

	public void setMaxContestSize(int maxContestSize) {
		this.maxContestSize = maxContestSize;
	}

	public int getMinContestSize() {
		return this.minContestSize;
	}

	public void setMinContestSize(int minContestSize) {
		this.minContestSize = minContestSize;
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

}