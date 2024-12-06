package com.choic11.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_notifications database table.
 * 
 */
@Entity
@Table(name="tbl_notifications")
@NamedQuery(name="TblNotification.findAll", query="SELECT t FROM TblNotification t")
public class TblNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	private String image;

	@Column(name="is_deleted")
	private String isDeleted;

	@Column(name="is_promotional")
	private String isPromotional;

	@Lob
	private String notification;

	@Column(name="sender_id")
	private int senderId;

	@Column(name="sender_ip")
	private String senderIp;

	@Column(name="sender_type")
	private String senderType;

	private String title;

	@Lob
	@Column(name="users_id")
	private String usersId;

	public TblNotification() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
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

	public String getIsPromotional() {
		return this.isPromotional;
	}

	public void setIsPromotional(String isPromotional) {
		this.isPromotional = isPromotional;
	}

	public String getNotification() {
		return this.notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public int getSenderId() {
		return this.senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public String getSenderIp() {
		return this.senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public String getSenderType() {
		return this.senderType;
	}

	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUsersId() {
		return this.usersId;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

}