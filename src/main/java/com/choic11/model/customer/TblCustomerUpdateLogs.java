package com.choic11.model.customer;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the tbl_customers database table.
 */
@Entity
@Table(name = "tbl_customer_update_logs")
@NamedQuery(name = "TblCustomerUpdateLogs.findAll", query = "SELECT t FROM TblCustomerUpdateLogs t")
public class TblCustomerUpdateLogs implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_id")
    private int customerId;

    @Lob
    @Column(name = "action")
    private String action;

    @Lob
    @Column(name = "previous_data")
    private String previousData;

    @Lob
    @Column(name = "new_data")
    private String newData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousData() {
        return previousData;
    }

    public void setPreviousData(String previousData) {
        this.previousData = previousData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}