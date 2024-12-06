package com.choic11.repository.admin;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.customer.*;
import com.choic11.service.CustomerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Repository
public class AdminRepository {

    @Autowired
    private SessionFactory factory;

    private Session getNewSession() {

        return factory.openSession();

    }

    private Session getSession() {
        Session session = factory.getCurrentSession();
        if (session == null) {
            return factory.openSession();
        }
        return session;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getWithdrawEntryId(int entryId, boolean isAffiliate) {

        String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

        String hqln1 = "SELECT "
                + "tbr.id as id, tbr.amount as amount, tbr.instantFee as instantFee, tbr.customerId as customerId, tbr.withdrawType as withdrawType, "
                + "tc.rzContactId as rzContactId, tc.rzFundAccountId as rzFundAccountId, tc.winningWallet as winningWallet, tc.pendingWidWallet as pendingWidWallet, tc.firstname as firstname, tc.lastname as lastname, tc.email as email, tc.countryMobileCode as countryMobileCode, tc.phone as phone, tc.paincardId as paincardId, tc.bankdetailId as bankdetailId, tc.paytmphone as paytmphone, "
                + "tcp.status as panStatus, "
                + "tcbd.status as bankStatus, tcbd.accountNumber as bankAccountNumber, tcbd.name as bankHolderName, tcbd.ifsc as bankIfsc  "
                + "FROM " + withdrawClass + " tbr " + "LEFT JOIN TblCustomer tc on tc.id=tbr.customerId "
                + "LEFT JOIN TblCustomerPaincard tcp ON tcp.id=tc.paincardId "
                + "LEFT JOIN TblCustomerBankdetail tcbd ON tcbd.id=tc.bankdetailId " + "WHERE "
                + "tbr.status='P' AND tbr.id=:entryId";

        Query queryy1 = getSession().createQuery(hqln1).setParameter("entryId", entryId);
        List<HashMap<String, Object>> results = (List<HashMap<String, Object>>) queryy1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        if (results.size() == 0) {
            return null;
        }
        return results.get(0);

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getWithdrawEntryIdByReferenceId(String referenceId, boolean isAffiliate) {

        String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

        String hqln1 = "SELECT "
                + "tbr.id as id, tbr.amount as amount, tbr.instantFee as instantFee, tbr.customerId as customerId, tbr.status as status "
                + "FROM " + withdrawClass + " tbr " + "WHERE " + "tbr.referenceId=:referenceId";

        Query queryy1 = getSession().createQuery(hqln1).setParameter("referenceId", referenceId);
        List<HashMap<String, Object>> results = (List<HashMap<String, Object>>) queryy1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        if (results.size() == 0) {
            return null;
        }
        return results.get(0);

    }

    public boolean updateWithdrawEntryStatus(int entryId, boolean isAffiliate, String status) {
        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();

            String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

            String hqln = "UPDATE " + withdrawClass + " tbr set tbr.status=:status where tbr.id=:entryId";

            Query queryy = newSession.createQuery(hqln).setParameter("status", status).setParameter("entryId", entryId);

            queryy.executeUpdate();

            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;
    }

    public boolean updateWithdrawEntryStatusRejected(CustomerService customerService, int entryId, int customerId,
                                                     boolean isAffiliate, String reason) {

        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();

            String status = "R";
            tx = newSession.beginTransaction();

            String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

            String hqln = "UPDATE " + withdrawClass
                    + " tbr set tbr.status=:status, tbr.reason=:reason, tbr.actionTime=:actionTime where tbr.id=:entryId";

            Query queryy = newSession.createQuery(hqln).setParameter("status", status).setParameter("reason", reason)
                    .setParameter("actionTime", BigInteger.valueOf(currentTime)).setParameter("entryId", entryId);

            queryy.executeUpdate();

            if (!isAffiliate) {

                float pendingWidWallet = customerService.getCustomerRepository()
                        .getCustomerPendingWithdrawAmount(newSession, customerId);

                hqln = "UPDATE TblCustomer SET pendingWidWallet=:pendingWidWallet WHERE id=:id";

                queryy = newSession.createQuery(hqln).setParameter("pendingWidWallet", pendingWidWallet)
                        .setParameter("id", customerId);
                queryy.executeUpdate();

            }
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;
    }

    public boolean updateWithdrawEntryStatusApproved(CustomerService customerService, int entryId, int customerId,
                                                     boolean isAffiliate, String referenceId, String utr, String jsonData, float amount) {

        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();

            String status = "C";
            tx = newSession.beginTransaction();

            String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

            String hqln = "UPDATE " + withdrawClass
                    + " tbr set tbr.status=:status, tbr.referenceId=:referenceId, tbr.utr=:utr, tbr.jsonData=:jsonData, tbr.actionTime=:actionTime where tbr.id=:entryId";

            Query queryy = newSession.createQuery(hqln).setParameter("status", status)
                    .setParameter("referenceId", referenceId).setParameter("utr", utr)
                    .setParameter("jsonData", jsonData).setParameter("actionTime", BigInteger.valueOf(currentTime))
                    .setParameter("entryId", entryId);

            queryy.executeUpdate();

            if (amount > 0 && !isAffiliate) {

                hqln = "SELECT winningWallet as winningWallet " + "FROM TblCustomer where id=:id";
                queryy = newSession.createQuery(hqln).setParameter("id", customerId);
                HashMap<String, Object> result = (HashMap<String, Object>) queryy
                        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

                float winningWallet = (float) result.get("winningWallet");

                String transaction_id = "ADMIN-C-" + currentTime;
                String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

                TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
                tblCustomerWalletHistory.setSportId(-1);
                tblCustomerWalletHistory.setCustomerId(customerId);
                tblCustomerWalletHistory.setPreviousAmount(winningWallet);
                tblCustomerWalletHistory.setAmount(amount);
                tblCustomerWalletHistory.setCurrentAmount(winningWallet - amount);
                tblCustomerWalletHistory.setWalletType(walletName);
                tblCustomerWalletHistory.setTransactionType("DEBIT");
                tblCustomerWalletHistory.setTransactionId(transaction_id);
                tblCustomerWalletHistory.setType("WALLET_WITHDRAW_ADMIN");
                tblCustomerWalletHistory.setDescription("Paid By Admin");
                tblCustomerWalletHistory.setRefrenceId(utr);
                tblCustomerWalletHistory.setJsonData(jsonData);
                tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
                newSession.save(tblCustomerWalletHistory);

                float pendingWidWallet = customerService.getCustomerRepository()
                        .getCustomerPendingWithdrawAmount(newSession, customerId);

                hqln = "UPDATE TblCustomer SET winningWallet=winningWallet - :amount, pendingWidWallet=:pendingWidWallet WHERE id=:id";

                queryy = newSession.createQuery(hqln).setParameter("amount", amount)
                        .setParameter("pendingWidWallet", pendingWidWallet).setParameter("id", customerId);
                queryy.executeUpdate();
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;
    }

    public boolean updateWithdrawEntryStatusHold(int entryId, boolean isAffiliate, String referenceId, String utr,
                                                 String jsonData) {

        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();

            String status = "H";
            tx = newSession.beginTransaction();

            String withdrawClass = isAffiliate ? "TblAffiliatesWithdrawRequest" : "TblWithdrawRequest";

            String hqln = "UPDATE " + withdrawClass
                    + " tbr set tbr.status=:status, tbr.referenceId=:referenceId, tbr.utr=:utr, tbr.jsonData=:jsonData, tbr.actionTime=:actionTime where tbr.id=:entryId";

            Query queryy = newSession.createQuery(hqln).setParameter("status", status)
                    .setParameter("referenceId", referenceId).setParameter("utr", utr)
                    .setParameter("jsonData", jsonData).setParameter("actionTime", BigInteger.valueOf(currentTime))
                    .setParameter("entryId", entryId);

            queryy.executeUpdate();

            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;
    }

    public boolean insertWithdrawRequestApproveRejectLogs(String adminUserId, int entryId, String action,
                                                          boolean isAffiliate, String ipAddress) {

        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();
            tx = newSession.beginTransaction();

            if (isAffiliate) {
                TblAffiliateWithdrawRequestApproveRejectLog instance = TblAffiliateWithdrawRequestApproveRejectLog
                        .getInstance();

                instance.setAction(action);
                instance.setAdminId(Integer.parseInt(adminUserId));
                instance.setEntryId(entryId);
                instance.setIpAddress(ipAddress);
                instance.setCreatedAt(BigInteger.valueOf(currentTime));

                newSession.save(instance);
            } else {
                TblWithdrawRequestApproveRejectLog instance = TblWithdrawRequestApproveRejectLog.getInstance();

                instance.setAction(action);
                instance.setAdminId(Integer.parseInt(adminUserId));
                instance.setEntryId(entryId);
                instance.setIpAddress(ipAddress);
                instance.setCreatedAt(BigInteger.valueOf(currentTime));

                newSession.save(instance);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;
    }

    public boolean insertPayoutLog(int customerId, int entryId, String referenceId, String json_data,
                                   boolean isAffiliate) {
        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();
            tx = newSession.beginTransaction();

            if (isAffiliate) {
                TblAffiliatesWithdrawRequestsLog instance = TblAffiliatesWithdrawRequestsLog.getInstance();

                instance.setPayoutId(referenceId);
                instance.setEntryId(entryId);
                instance.setCustomerId(customerId);
                instance.setJson(json_data);
                instance.setCreated(BigInteger.valueOf(currentTime));

                newSession.save(instance);
            } else {
                TblWithdrawRequestsLog instance = TblWithdrawRequestsLog.getInstance();

                instance.setPayoutId(referenceId);
                instance.setEntryId(entryId);
                instance.setCustomerId(customerId);
                instance.setJson(json_data);
                instance.setCreated(BigInteger.valueOf(currentTime));

                newSession.save(instance);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return false;

    }
}
