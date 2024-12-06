package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_basketball_email_cron database table.
 * 
 */
@Entity
@Table(name = "tbl_basketball_email_cron")
@NamedQuery(name = "TblBasketballEmailCron.findAll", query = "SELECT t FROM TblBasketballEmailCron t")
public class TblBasketballEmailCron implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private BigInteger createdat;

	@Column(name = "email_type")
	private String emailType;

	@Column(name = "is_send")
	private String isSend;

	@Lob
	private String message;

	@Lob
	private String subject;

	private String toemail;

	private String toname;

	public TblBasketballEmailCron() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreatedat() {
		return this.createdat;
	}

	public void setCreatedat(BigInteger createdat) {
		this.createdat = createdat;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getIsSend() {
		return this.isSend;
	}

	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToemail() {
		return this.toemail;
	}

	public void setToemail(String toemail) {
		this.toemail = toemail;
	}

	public String getToname() {
		return this.toname;
	}

	public void setToname(String toname) {
		this.toname = toname;
	}

	public static TblBasketballEmailCron getInstance() {
		TblBasketballEmailCron tblBasketballEmailCron = new TblBasketballEmailCron();

		tblBasketballEmailCron.setIsSend("N");

		return tblBasketballEmailCron;
	}

}