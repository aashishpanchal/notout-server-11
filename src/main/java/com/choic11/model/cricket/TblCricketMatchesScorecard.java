package com.choic11.model.cricket;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_cricket_matches_scorecard database table.
 * 
 */
@Entity
@Table(name = "tbl_cricket_matches_scorecard")
@NamedQuery(name = "TblCricketMatchesScorecard.findAll", query = "SELECT t FROM TblCricketMatchesScorecard t")
public class TblCricketMatchesScorecard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private BigInteger created;

	@Lob
	@Column(name = "full_score")
	private String fullScore;

	@Column(name = "match_unique_id")
	private Integer matchUniqueId;

	@Lob
	@Column(name = "short_score")
	private String shortScore;

	private BigInteger updated;
	

	public TblCricketMatchesScorecard() {
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

	public String getFullScore() {
		return this.fullScore;
	}

	public void setFullScore(String fullScore) {
		this.fullScore = fullScore;
	}

	public Integer getMatchUniqueId() {
		return this.matchUniqueId;
	}

	public void setMatchUniqueId(Integer matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public String getShortScore() {
		return this.shortScore;
	}

	public void setShortScore(String shortScore) {
		this.shortScore = shortScore;
	}

	public BigInteger getUpdated() {
		return this.updated;
	}

	public void setUpdated(BigInteger updated) {
		this.updated = updated;
	}


}