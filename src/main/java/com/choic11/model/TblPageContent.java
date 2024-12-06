package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tbl_page_contents database table.
 * 
 */
@Entity
@Table(name="tbl_page_contents")
@NamedQuery(name="TblPageContent.findAll", query="SELECT t FROM TblPageContent t")
public class TblPageContent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="app_type")
	private String appType;

	@Lob
	private String content;

	private int created;

	private int createdby;

	@Column(name="meta_description")
	private String metaDescription;

	@Column(name="meta_keywords")
	private String metaKeywords;

	@Column(name="meta_title")
	private String metaTitle;

	@Column(name="page_name")
	private String pageName;

	private String platform;

	private String title;

	private int updated;

	private int updatedby;

	public TblPageContent() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppType() {
		return this.appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getMetaDescription() {
		return this.metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeywords() {
		return this.metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaTitle() {
		return this.metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getUpdated() {
		return this.updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}

	public int getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(int updatedby) {
		this.updatedby = updatedby;
	}

}