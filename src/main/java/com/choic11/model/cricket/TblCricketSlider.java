package com.choic11.model.cricket;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_sliders database table.
 * 
 */
@Entity
@Table(name="tbl_sliders")
@NamedQuery(name="TblCricketSlider.findAll", query="SELECT t FROM TblCricketSlider t")
public class TblCricketSlider implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Lob
	private String content;

	@Column(name="created_at")
	private BigInteger createdAt;

	@Column(name="created_by")
	private int createdBy;

	private String image;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="is_match")
	private String isMatch;
	
	@Column(name="redirecting_links_type")
	private String redirectingLinksType;
	
	@Column(name="redirecting_url")
	private String redirectingUrl;
	
	@Column(name = "match_unique_id")
	private int matchUniqueId;
	
	@OneToOne(fetch=FetchType.LAZY)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	@JoinColumn(name="match_unique_id", referencedColumnName="unique_id", updatable = false, insertable = false)
	private TblCricketMatch tblCricketMatch;


	@Column(name="slider_order")
	private int sliderOrder;

	private String status;

	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Column(name="updated_by")
	private int updatedBy;

	public TblCricketSlider() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsMatch() {
		return this.isMatch;
	}

	public void setIsMatch(String isMatch) {
		this.isMatch = isMatch;
	}
	
	public String getRedirectingLinksType() {
		return redirectingLinksType;
	}
	
	public void setRedirectingLinksType(String redirectingLinksType) {
		this.redirectingLinksType = redirectingLinksType;
	}
	
	public String getRedirectingUrl() {
		return redirectingUrl;
	}
	
	public void setRedirectingUrl(String redirectingUrl) {
		this.redirectingUrl = redirectingUrl;
	}
	
	public int getMatchUniqueId() {
		return matchUniqueId;
	}
	
	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}
	
	public void setTblCricketMatch(TblCricketMatch tblCricketMatch) {
		this.tblCricketMatch = tblCricketMatch;
	}
	
	public TblCricketMatch getTblCricketMatch() {
		return tblCricketMatch;
	}

	public int getSliderOrder() {
		return this.sliderOrder;
	}

	public void setSliderOrder(int sliderOrder) {
		this.sliderOrder = sliderOrder;
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