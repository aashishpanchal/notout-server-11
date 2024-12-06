package com.choic11.model;

import com.choic11.model.customer.TblCustomer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_follow database table.
 * 
 */
@Entity
@Table(name="tbl_follow")
@NamedQuery(name="TblFollow.findAll", query="SELECT t FROM TblFollow t")
public class TblFollow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name="following_id")
	private int followingId;
	
	@Column(name="follower_id")
	private int followerId;

	@ManyToOne
	@JoinColumn(name = "follower_id", insertable = false, updatable = false)
	private TblCustomer tblCustomer;

	public TblFollow() {
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

	public int getFollowingId() {
		return this.followingId;
	}

	public void setFollowingId(int followingId) {
		this.followingId = followingId;
	}
	
	public int getFollowerId() {
		return followerId;
	}
	
	public void setFollowerId(int followerId) {
		this.followerId = followerId;
	}

	public TblCustomer getTblCustomer() {
		return this.tblCustomer;
	}

	public void setTblCustomer(TblCustomer tblCustomer) {
		this.tblCustomer = tblCustomer;
	}

}