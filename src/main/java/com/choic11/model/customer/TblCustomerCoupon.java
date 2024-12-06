package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the tbl_customer_coupons database table.
 */
@Entity
@Table(name = "tbl_customer_coupons")
@NamedQuery(name = "TblCustomerCoupon.findAll", query = "SELECT t FROM TblCustomerCoupon t")
public class TblCustomerCoupon implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "coupon_id")
    private int couponId;

    @Column(name = "created_at")
    private BigInteger createdAt;

    @Column(name = "customer_id")
    private int customerId;

    public TblCustomerCoupon() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCouponId() {
        return this.couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
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


    public static TblCustomerCoupon getInstance() {
        return new TblCustomerCoupon();
    }

}