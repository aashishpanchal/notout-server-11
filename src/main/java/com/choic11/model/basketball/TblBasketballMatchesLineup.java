package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_basketball_matches_lineup database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_matches_lineup")
@NamedQuery(name="TblBasketballMatchesLineup.findAll", query="SELECT t FROM TblBasketballMatchesLineup t")
public class TblBasketballMatchesLineup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="match_unique_id")
	private int matchUniqueId;
	
	@Column(name="updated_at")
	private BigInteger updatedAt;

	@Lob
	@Column(name="player_unique_ids")
	private String playerUniqueIds;

	@Lob
	@Column(name="subsitute_unique_ids")
	private String subsituteUniqueIds;

	public TblBasketballMatchesLineup() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(int matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}
	
	public BigInteger getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getPlayerUniqueIds() {
		return this.playerUniqueIds;
	}

	public void setPlayerUniqueIds(String playerUniqueIds) {
		this.playerUniqueIds = playerUniqueIds;
	}

	public String getSubsituteUniqueIds() {
		return this.subsituteUniqueIds;
	}

	public void setSubsituteUniqueIds(String subsituteUniqueIds) {
		this.subsituteUniqueIds = subsituteUniqueIds;
	}

}