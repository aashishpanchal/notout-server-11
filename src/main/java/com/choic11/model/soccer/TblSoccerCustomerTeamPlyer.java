package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_customer_team_plyers database table.
 * 
 */
@Entity
@Table(name="tbl_soccer_customer_team_plyers")
@NamedQuery(name="TblSoccerCustomerTeamPlyer.findAll", query="SELECT t FROM TblSoccerCustomerTeamPlyer t")
public class TblSoccerCustomerTeamPlyer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="customer_team_id")
	private int customerTeamId;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	private float multiplier;

	@Column(name="player_unique_id")
	private int playerUniqueId;

	private int position;

	@Column(name="team_id")
	private int teamId;

	public TblSoccerCustomerTeamPlyer() {
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

	public int getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getCustomerTeamId() {
		return this.customerTeamId;
	}

	public void setCustomerTeamId(int customerTeamId) {
		this.customerTeamId = customerTeamId;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public float getMultiplier() {
		return this.multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public int getPlayerUniqueId() {
		return this.playerUniqueId;
	}

	public void setPlayerUniqueId(int playerUniqueId) {
		this.playerUniqueId = playerUniqueId;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getTeamId() {
		return this.teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

}