package com.choic11.model.cricket;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_cricket_customer_contests database table.
 * 
 */
@Entity
@Table(name = "tbl_cricket_customer_contest_fav")
@NamedQuery(name = "TblCricketCustomerContestFav.findAll", query = "SELECT t FROM TblCricketCustomerContestFav t")
public class TblCricketCustomerContestFav implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name = "customer_id")
	private int customerId;

	@Column(name = "contest_id")
	private int contestId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCreated() {
		return created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getContestId() {
		return contestId;
	}

	public void setContestId(int contestId) {
		this.contestId = contestId;
	}
}