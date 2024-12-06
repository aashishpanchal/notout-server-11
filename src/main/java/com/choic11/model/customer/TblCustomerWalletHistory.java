package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_customer_wallet_histories database table.
 * 
 */
@Entity
@Table(name = "tbl_customer_wallet_histories")
@NamedQuery(name = "TblCustomerWalletHistory.findAll", query = "SELECT t FROM TblCustomerWalletHistory t")
public class TblCustomerWalletHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Float amount;

	private BigInteger created;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "current_amount")
	private Float currentAmount;

	@Lob
	private String description;

	@Lob
	@Column(name = "json_data")
	private String jsonData;

	@Column(name = "match_contest_id")
	private int matchContestId;

	@Column(name = "payment_from")
	private String paymentFrom;

	@Column(name = "payment_method")
	private String paymentMethod;

	@Column(name = "previous_amount")
	private Float previousAmount;

	@Column(name = "rcb_id")
	private int rcbId;

	@Column(name = "ref_cwh_id")
	private int refCwhId;

	@Column(name = "refrence_id")
	private String refrenceId;

	private String status;

	@Column(name = "team_id")
	private int teamId;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "transaction_type")
	private String transactionType;

	private String type;

	@Column(name = "wallet_type")
	private String walletType;

	@Column(name = "sport_id")
	private Integer sportId;

	@Column(name = "customer_id")
	private Integer customerId;

	@Column(name = "series_id")
	private Integer seriesId;

	@Column(name = "match_unique_id")
	private Integer matchUniqueId;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getSportId() {
		return sportId;
	}
	
	public void setSportId(Integer sportId) {
		this.sportId = sportId;
	}
	
	public Integer getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(Integer seriesId) {
		this.seriesId = seriesId;
	}
	
	public Integer getMatchUniqueId() {
		return matchUniqueId;
	}
	
	public void setMatchUniqueId(Integer matchUniqueId) {
		this.matchUniqueId = matchUniqueId;
	}

	public TblCustomerWalletHistory() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getAmount() {
		return this.amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public BigInteger getCreated() {
		return this.created;
	}

	public void setCreated(BigInteger created) {
		this.created = created;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Float getCurrentAmount() {
		return this.currentAmount;
	}

	public void setCurrentAmount(Float currentAmount) {
		this.currentAmount = currentAmount;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJsonData() {
		return this.jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public int getMatchContestId() {
		return this.matchContestId;
	}

	public void setMatchContestId(int matchContestId) {
		this.matchContestId = matchContestId;
	}

	public String getPaymentFrom() {
		return this.paymentFrom;
	}

	public void setPaymentFrom(String paymentFrom) {
		this.paymentFrom = paymentFrom;
	}

	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Float getPreviousAmount() {
		return this.previousAmount;
	}

	public void setPreviousAmount(Float previousAmount) {
		this.previousAmount = previousAmount;
	}

	public int getRcbId() {
		return this.rcbId;
	}

	public void setRcbId(int rcbId) {
		this.rcbId = rcbId;
	}

	public int getRefCwhId() {
		return this.refCwhId;
	}

	public void setRefCwhId(int refCwhId) {
		this.refCwhId = refCwhId;
	}

	public String getRefrenceId() {
		return this.refrenceId;
	}

	public void setRefrenceId(String refrenceId) {
		this.refrenceId = refrenceId;
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

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWalletType() {
		return this.walletType;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}
	
	public static TblCustomerWalletHistory getInstance() {
		
		TblCustomerWalletHistory tblCustomerWalletHistory=new TblCustomerWalletHistory();
		tblCustomerWalletHistory.customerId=0;
		tblCustomerWalletHistory.sportId=0;
		tblCustomerWalletHistory.seriesId=0;
		tblCustomerWalletHistory.matchUniqueId=0;
		tblCustomerWalletHistory.matchContestId=0;
		tblCustomerWalletHistory.rcbId=0;
		tblCustomerWalletHistory.refCwhId=0;
		tblCustomerWalletHistory.teamId=0;
		tblCustomerWalletHistory.status="S";
		tblCustomerWalletHistory.createdBy=0;
		
		return tblCustomerWalletHistory;
		
		
	}

}