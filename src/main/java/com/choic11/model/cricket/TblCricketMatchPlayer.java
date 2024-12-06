package com.choic11.model.cricket;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_cricket_match_players database table.
 * 
 */
@Entity
@Table(name = "tbl_cricket_match_players")
@NamedQuery(name = "TblCricketMatchPlayer.findAll", query = "SELECT t FROM TblCricketMatchPlayer t")
public class TblCricketMatchPlayer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "match_unique_id")
	private Integer matchUniqueId;

	@Column(name = "player_unique_id")
	private Integer playerUniqueId;

	@Column(name = "created_at")
	private BigInteger createdAt;

	@Column(name = "created_by")
	private int createdBy;

	private float credits;

	@Column(name = "dream_team_player")
	private String dreamTeamPlayer;

	private String image;

	@Column(name = "is_deleted")
	private String isDeleted;

	@Column(name = "is_in_playing_squad")
	private String isInPlayingSquad;

	@Column(name = "is_in_substitute_squad")
	private String isInSubstituteSquad;

	@Column(name = "playing_role")
	private String playingRole;

	private float points;

	@Column(name = "selected_as_caption")
	private float selectedAsCaption;

	@Column(name = "selected_as_trump")
	private float selectedAsTrump;

	@Column(name = "selected_as_vccaption")
	private float selectedAsVccaption;

	@Column(name = "selected_by")
	private float selectedBy;

	@Column(name = "selected_as_caption_count")
	private int selectedAsCaptionCount;

	@Column(name = "selected_as_trump_count")
	private int selectedAsTrumpCount;

	@Column(name = "selected_as_vccaption_count")
	private int selectedAsVccaptionCount;

	@Column(name = "selected_by_count")
	private int selectedByCount;

	private String status;

	@Column(name = "team_id")
	private int teamId;

	@Column(name = "updated_at")
	private BigInteger updatedAt;

	@Column(name = "updated_by")
	private int updatedBy;

	@OneToOne
	@JoinColumn(name = "player_unique_id", referencedColumnName = "uniqueid", insertable = false, updatable = false)
	TblCricketPlayer tblCricketPlayer;

	public TblCricketMatchPlayer() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getMatchUniqueId() {
		return matchUniqueId;
	}

	public void setMatchUniqueId(Integer matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public Integer getPlayerUniqueId() {
		return playerUniqueId;
	}

	public void setPlayerUniqueId(Integer playerUniqueId) {
		this.playerUniqueId = playerUniqueId;
	}

	public BigInteger getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(BigInteger createdAt) {
		this.createdAt = createdAt;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public float getCredits() {
		return this.credits;
	}

	public void setCredits(float credits) {
		this.credits = credits;
	}

	public String getDreamTeamPlayer() {
		return this.dreamTeamPlayer;
	}

	public void setDreamTeamPlayer(String dreamTeamPlayer) {
		this.dreamTeamPlayer = dreamTeamPlayer;
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

	public String getIsInPlayingSquad() {
		return this.isInPlayingSquad;
	}

	public void setIsInPlayingSquad(String isInPlayingSquad) {
		this.isInPlayingSquad = isInPlayingSquad;
	}

	public String getIsInSubstituteSquad() {
		return this.isInSubstituteSquad;
	}

	public void setIsInSubstituteSquad(String isInSubstituteSquad) {
		this.isInSubstituteSquad = isInSubstituteSquad;
	}

	public String getPlayingRole() {
		return this.playingRole;
	}

	public void setPlayingRole(String playingRole) {
		this.playingRole = playingRole;
	}

	public float getPoints() {
		return this.points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public float getSelectedAsCaption() {
		return this.selectedAsCaption;
	}

	public void setSelectedAsCaption(float selectedAsCaption) {
		this.selectedAsCaption = selectedAsCaption;
	}

	public float getSelectedAsTrump() {
		return this.selectedAsTrump;
	}

	public void setSelectedAsTrump(float selectedAsTrump) {
		this.selectedAsTrump = selectedAsTrump;
	}

	public float getSelectedAsVccaption() {
		return this.selectedAsVccaption;
	}

	public void setSelectedAsVccaption(float selectedAsVccaption) {
		this.selectedAsVccaption = selectedAsVccaption;
	}

	public float getSelectedBy() {
		return this.selectedBy;
	}

	public void setSelectedBy(float selectedBy) {
		this.selectedBy = selectedBy;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTeamId() {
		return this.teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public BigInteger getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(BigInteger updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public TblCricketPlayer getTblCricketPlayer() {
		return tblCricketPlayer;
	}

	public void setTblCricketPlayer(TblCricketPlayer tblCricketPlayer) {
		this.tblCricketPlayer = tblCricketPlayer;
	}

	public int getSelectedAsCaptionCount() {
		return selectedAsCaptionCount;
	}

	public void setSelectedAsCaptionCount(int selectedAsCaptionCount) {
		this.selectedAsCaptionCount = selectedAsCaptionCount;
	}

	public int getSelectedAsTrumpCount() {
		return selectedAsTrumpCount;
	}

	public void setSelectedAsTrumpCount(int selectedAsTrumpCount) {
		this.selectedAsTrumpCount = selectedAsTrumpCount;
	}

	public int getSelectedAsVccaptionCount() {
		return selectedAsVccaptionCount;
	}

	public void setSelectedAsVccaptionCount(int selectedAsVccaptionCount) {
		this.selectedAsVccaptionCount = selectedAsVccaptionCount;
	}

	public int getSelectedByCount() {
		return selectedByCount;
	}

	public void setSelectedByCount(int selectedByCount) {
		this.selectedByCount = selectedByCount;
	}

}