package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_affiliates_withdraw_requests database table.
 */
@Entity
@Table(name = "tbl_affiliates_withdraw_requests")
@NamedQuery(name = "TblAffiliatesWithdrawRequest.findAll", query = "SELECT t FROM TblAffiliatesWithdrawRequest t")
public class TblAffiliatesWithdrawRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "action_time")
    private BigInteger actionTime;

    private float amount;

    @Column(name = "created_at")
    private BigInteger createdAt;

    @Column(name = "customer_id")
    private int customerId;

    private float gst;

    @Column(name = "instant_fee")
    private float instantFee;

    @Lob
    @Column(name = "json_data")
    private String jsonData;

    @Lob
    private String reason;

    private String referenceId;

    @Column(name = "service_tax")
    private float serviceTax;

    private String status;

    @Column(name = "withdraw_type")
    private String withdrawType;

    @Column(name = "transaction_id")
    private String transactionId;

    private String type;

    @Column(name = "updated_at")
    private BigInteger updatedAt;

    @Column(name = "updated_by")
    private int updatedBy;

    private String utr;

    @Column(name = "withdrawals_amount_charges")
    private float withdrawalsAmountCharges;

    public TblAffiliatesWithdrawRequest() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigInteger getActionTime() {
        return this.actionTime;
    }

    public void setActionTime(BigInteger actionTime) {
        this.actionTime = actionTime;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public BigInteger getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(BigInteger createdAt) {
        this.createdAt = createdAt;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public float getGst() {
        return this.gst;
    }

    public void setGst(float gst) {
        this.gst = gst;
    }

    public float getInstantFee() {
        return this.instantFee;
    }

    public void setInstantFee(float instantFee) {
        this.instantFee = instantFee;
    }

    public String getJsonData() {
        return this.jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceId() {
        return this.referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public float getServiceTax() {
        return this.serviceTax;
    }

    public void setServiceTax(float serviceTax) {
        this.serviceTax = serviceTax;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUtr() {
        return this.utr;
    }

    public void setUtr(String utr) {
        this.utr = utr;
    }

    public float getWithdrawalsAmountCharges() {
        return this.withdrawalsAmountCharges;
    }

    public void setWithdrawalsAmountCharges(float withdrawalsAmountCharges) {
        this.withdrawalsAmountCharges = withdrawalsAmountCharges;
    }

    public static TblAffiliatesWithdrawRequest getInstance() {
        TblAffiliatesWithdrawRequest tblAffiliatesWithdrawRequest = new TblAffiliatesWithdrawRequest();
        tblAffiliatesWithdrawRequest.setStatus("P");
        tblAffiliatesWithdrawRequest.setWithdrawalsAmountCharges(0);
        tblAffiliatesWithdrawRequest.setType("NORMAL");
        tblAffiliatesWithdrawRequest.setWithdrawType("B");
        tblAffiliatesWithdrawRequest.setInstantFee(0);
        tblAffiliatesWithdrawRequest.setServiceTax(0);
        tblAffiliatesWithdrawRequest.setGst(0);
        tblAffiliatesWithdrawRequest.setUpdatedBy(0);
        tblAffiliatesWithdrawRequest.setCreatedAt(BigInteger.valueOf(0));
        tblAffiliatesWithdrawRequest.setUpdatedAt(BigInteger.valueOf(0));
        tblAffiliatesWithdrawRequest.setActionTime(BigInteger.valueOf(0));
        return tblAffiliatesWithdrawRequest;
    }

}