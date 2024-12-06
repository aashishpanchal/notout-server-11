package com.choic11.model.soccer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_countries database table.
 * 
 */
@Entity
@Table(name = "tbl_soccer_series_players")
public class TblSoccerSeriesPlayers implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    

    @Column(name = "created")
    private BigInteger created;

    @Column(name = "updated")
    private BigInteger updated;

    @Column(name = "series_id")
    private Integer seriesId;
    
    @Column(name = "game_type_id")
    private Integer gameTypeId;

    @Column(name = "player_unique_id")
    private Integer playerUniqueId;

    @Column(name = "points")
    private Float points;

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

    public BigInteger getUpdated() {
        return updated;
    }

    public void setUpdated(BigInteger updated) {
        this.updated = updated;
    }

    public Integer getSeriesId() {  
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }
    
    public Integer getGameTypeId() {
		return gameTypeId;
	}
    
    public void setGameTypeId(Integer gameTypeId) {
		this.gameTypeId = gameTypeId;
	}

    public Integer getPlayerUniqueId() {
        return playerUniqueId;
    }

    public void setPlayerUniqueId(Integer playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }


}