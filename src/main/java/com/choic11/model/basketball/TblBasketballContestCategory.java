package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_basketball_contest_categories database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_contest_categories")
@NamedQuery(name="TblBasketballContestCategory.findAll", query="SELECT t FROM TblBasketballContestCategory t")
public class TblBasketballContestCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="cash_bonus_used_type")
	private String cashBonusUsedType;

	@Column(name="cash_bonus_used_value")
	private float cashBonusUsedValue;

	@Column(name="confirm_win")
	private String confirmWin;

	@Column(name="is_compression_allow")
	private String isCompressionAllow;

	@Column(name="confirm_win_contest_percentage")
	private float confirmWinContestPercentage;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	@Lob
	private String description;

	@Column(name="duplicate_count")
	private int duplicateCount;

	@Column(name="entry_fee_multiplier")
	private float entryFeeMultiplier;

	private String image;

	@Column(name="is_beat_the_expert")
	private String isBeatTheExpert;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="is_discounted")
	private String isDiscounted;

	@Column(name="is_duplicate_allow")
	private String isDuplicateAllow;

	@Column(name="is_private")
	private String isPrivate;

	private String name;

	@Column(name="order_pos")
	private int orderPos;

	private String status;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblBasketballContestCategory() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCashBonusUsedType() {
		return this.cashBonusUsedType;
	}

	public void setCashBonusUsedType(String cashBonusUsedType) {
		this.cashBonusUsedType = cashBonusUsedType;
	}

	public float getCashBonusUsedValue() {
		return this.cashBonusUsedValue;
	}

	public void setCashBonusUsedValue(float cashBonusUsedValue) {
		this.cashBonusUsedValue = cashBonusUsedValue;
	}

	public String getConfirmWin() {
		return this.confirmWin;
	}

	public void setConfirmWin(String confirmWin) {
		this.confirmWin = confirmWin;
	}

	public String getIsCompressionAllow() {
		return isCompressionAllow;
	}

	public void setIsCompressionAllow(String isCompressionAllow) {
		this.isCompressionAllow = isCompressionAllow;
	}

	public float getConfirmWinContestPercentage() {
		return this.confirmWinContestPercentage;
	}

	public void setConfirmWinContestPercentage(float confirmWinContestPercentage) {
		this.confirmWinContestPercentage = confirmWinContestPercentage;
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuplicateCount() {
		return this.duplicateCount;
	}

	public void setDuplicateCount(int duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public float getEntryFeeMultiplier() {
		return this.entryFeeMultiplier;
	}

	public void setEntryFeeMultiplier(float entryFeeMultiplier) {
		this.entryFeeMultiplier = entryFeeMultiplier;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIsBeatTheExpert() {
		return this.isBeatTheExpert;
	}

	public void setIsBeatTheExpert(String isBeatTheExpert) {
		this.isBeatTheExpert = isBeatTheExpert;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsDiscounted() {
		return this.isDiscounted;
	}

	public void setIsDiscounted(String isDiscounted) {
		this.isDiscounted = isDiscounted;
	}

	public String getIsDuplicateAllow() {
		return this.isDuplicateAllow;
	}

	public void setIsDuplicateAllow(String isDuplicateAllow) {
		this.isDuplicateAllow = isDuplicateAllow;
	}

	public String getIsPrivate() {
		return this.isPrivate;
	}

	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrderPos() {
		return this.orderPos;
	}

	public void setOrderPos(int orderPos) {
		this.orderPos = orderPos;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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