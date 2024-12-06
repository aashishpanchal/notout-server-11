package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_lineup_noti_cron database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_lineup_noti_cron")
@NamedQuery(name="TblSoccerLineupNotiCron.findAll", query="SELECT t FROM TblSoccerLineupNotiCron t")
public class TblSoccerLineupNotiCron implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Lob
	@Column(name="alert_message")
	private String alertMessage;

	private BigInteger created;

	@Column(name="is_send")
	private String isSend;
	
	@Column(name="noti_type")
	private String notiType;

	@Lob
	private String message;

	@Lob
	@Column(name="user_ids")
	private String userIds;

	public TblSoccerLineupNotiCron() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlertMessage() {
		return this.alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public String getIsSend() {
		return this.isSend;
	}

	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}
	
	public String getNotiType() {
		return notiType;
	}
	
	public void setNotiType(String notiType) {
		this.notiType = notiType;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUserIds() {
		return this.userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

}