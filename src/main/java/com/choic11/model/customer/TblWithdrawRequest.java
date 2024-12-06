package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_withdraw_requests database table.
 */
@Entity
@Table(name = "tbl_withdraw_requests")
@NamedQuery(name = "TblWithdrawRequest.findAll", query = "SELECT t FROM TblWithdrawRequest t")
public class TblWithdrawRequest implements Serializable {
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

    @Lob
    @Column(name = "json_data")
    private String jsonData;

    @Lob
    private String reason;

    private String referenceId;

    private String status;

    @Column(name = "withdraw_type")
    private String withdrawType;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "updated_at")
    private BigInteger updatedAt;

    @Column(name = "updated_by")
    private int updatedBy;

    private String utr;

    private String type;

    @Column(name = "withdrawals_amount_charges")
    private float withdrawalsAmountCharges;

    @Column(name = "instant_fee")
    private float instantFee;

    @Column(name = "service_tax")
    private float serviceTax;

    private float gst;

    public TblWithdrawRequest() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWithdrawalsAmountCharges() {
        return this.withdrawalsAmountCharges;
    }

    public void setWithdrawalsAmountCharges(float withdrawalsAmountCharges) {
        this.withdrawalsAmountCharges = withdrawalsAmountCharges;
    }

    public float getInstantFee() {
        return instantFee;
    }

    public void setInstantFee(float instantFee) {
        this.instantFee = instantFee;
    }

    public float getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(float serviceTax) {
        this.serviceTax = serviceTax;
    }

    public float getGst() {
        return gst;
    }

    public void setGst(float gst) {
        this.gst = gst;
    }

    public static TblWithdrawRequest getInstance() {
        TblWithdrawRequest tblWithdrawRequest = new TblWithdrawRequest();
        tblWithdrawRequest.setStatus("P");
        tblWithdrawRequest.setWithdrawalsAmountCharges(0);
        tblWithdrawRequest.setType("NORMAL");
        tblWithdrawRequest.setWithdrawType("B");
        tblWithdrawRequest.setInstantFee(0);
        tblWithdrawRequest.setServiceTax(0);
        tblWithdrawRequest.setGst(0);
        tblWithdrawRequest.setUpdatedBy(0);
        tblWithdrawRequest.setCreatedAt(BigInteger.valueOf(0));
        tblWithdrawRequest.setUpdatedAt(BigInteger.valueOf(0));
        tblWithdrawRequest.setActionTime(BigInteger.valueOf(0));
        return tblWithdrawRequest;
    }

}