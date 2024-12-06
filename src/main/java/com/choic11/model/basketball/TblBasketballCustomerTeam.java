package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_soccer_customer_teams database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_customer_teams")
@NamedQuery(name="TblBasketballCustomerTeam.findAll", query="SELECT t FROM TblBasketballCustomerTeam t")
public class TblBasketballCustomerTeam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Column(name="customer_id")
	private int customerId;

	@Column(name="customer_team_name")
	private String customerTeamName;

	@Column(name="match_unique_id")
	private int matchUniqueId;

	@Column(name="more_name")
	private int moreName;

	private int name;
	
	@Column(name = "player_unique_ids")
	private String playerUniqueIds;
	
	@Column(name = "player_multipliers")
	private String playerMultiplers;
	
	@Column(name = "player_unique_ids_multiplers")
	private String playerUniqueIdsMultiplers;

	private BigInteger updated;

	public TblBasketballCustomerTeam() {
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

	public String getCustomerTeamName() {
		return this.customerTeamName;
	}

	public void setCustomerTeamName(String customerTeamName) {
		this.customerTeamName = customerTeamName;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public int getMoreName() {
		return this.moreName;
	}

	public void setMoreName(int moreName) {
		this.moreName = moreName;
	}

	public int getName() {
		return this.name;
	}

	public void setName(int name) {
		this.name = name;
	}
	
	

	public String getPlayerUniqueIds() {
		return playerUniqueIds;
	}

	public void setPlayerUniqueIds(String playerUniqueIds) {
		this.playerUniqueIds = playerUniqueIds;
	}

	public String getPlayerMultiplers() {
		return playerMultiplers;
	}

	public void setPlayerMultiplers(String playerMultiplers) {
		this.playerMultiplers = playerMultiplers;
	}

	public String getPlayerUniqueIdsMultiplers() {
		return playerUniqueIdsMultiplers;
	}

	public void setPlayerUniqueIdsMultiplers(String playerUniqueIdsMultiplers) {
		this.playerUniqueIdsMultiplers = playerUniqueIdsMultiplers;
	}

	public BigInteger getUpdated() {
		return this.updated;
	}

	public void setUpdated(BigInteger updated) {
		this.updated = updated;
	}

}