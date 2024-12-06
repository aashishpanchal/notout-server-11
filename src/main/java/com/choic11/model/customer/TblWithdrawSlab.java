package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_withdraw_requests database table.
 */
@Entity
@Table(name = "tbl_withdraw_slab")
@NamedQuery(name = "TblWithdrawSlab.findAll", query = "SELECT t FROM TblWithdrawSlab t")
public class TblWithdrawSlab implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "min")
    private float min;

    @Column(name = "max")
    private float max;

    @Column(name = "charges")
    private float charges;

    @Column(name = "created_at")
    private BigInteger createdAt;

    @Column(name = "update_at")
    private BigInteger updateAt;

    @Column(name="is_deleted")
    private String isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getCharges() {
        return charges;
    }

    public void setCharges(float charges) {
        this.charges = charges;
    }

    public BigInteger getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(BigInteger createdAt) {
        this.createdAt = createdAt;
    }

    public BigInteger getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(BigInteger updateAt) {
        this.updateAt = updateAt;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}