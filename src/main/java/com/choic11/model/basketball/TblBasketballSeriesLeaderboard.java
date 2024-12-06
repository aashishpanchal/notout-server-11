package com.choic11.model.basketball;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_basketball_series_leaderboard database table.
 * 
 */
@Entity
@Table(name="tbl_basketball_series_leaderboard")
@NamedQuery(name="TblBasketballSeriesLeaderboard.findAll", query="SELECT t FROM TblBasketballSeriesLeaderboard t")
public class TblBasketballSeriesLeaderboard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="category_id")
	private int categoryId;

	private BigInteger created;

	@Column(name="series_id")
	private int seriesId;

	public TblBasketballSeriesLeaderboard() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public int getSeriesId() {
		return this.seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

}