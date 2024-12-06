package com.choic11.repository;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.*;
import com.choic11.model.customer.*;
import com.choic11.model.response.BaseResponse;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

@Repository
public class CustomerRepository {

    @Autowired
    private SessionFactory factory;

    @Autowired
    private EntityManager entityManager;


    private Session getNewSession() {

        return factory.openSession();

    }

    private Session getSession() {
        Session session = factory.getCurrentSession();
        if (session == null) {
            session = factory.openSession();
        }
        return session;
    }

    @Transactional(readOnly = true)
    public TblCustomer checkUser(String username, String type) {
        Query query = null;
        if (type.equals("M")) {
            String hqlQuery = "SELECT " +
                    "tc.id as id, " +
                    "tc.slug as slug, " +
                    "tc.status as status, " +
                    "tc.phone as phone, " +
                    "tc.countryMobileCode as countryMobileCode " +
                    "FROM TblCustomer tc WHERE tc.fullPhone =:fullPhone AND tc.isDeleted =:isDeleted";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("fullPhone", username);
            query.setParameter("isDeleted", "N");
        } else if (type.equals("E")) {
            String hqlQuery = "SELECT " +
                    "tc.id as id, " +
                    "tc.slug as slug, " +
                    "tc.status as status, " +
                    "tc.phone as phone, " +
                    "tc.countryMobileCode as countryMobileCode " +
                    "FROM TblCustomer tc WHERE tc.email =:email AND tc.isDeleted =:isDeleted";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("email", username);
            query.setParameter("isDeleted", "N");
        }
        return (TblCustomer) query.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        if (type.equals("M")) {
//            createCriteria.add(Restrictions.eq("fullPhone", username));
//            createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//            createCriteria
//                    .setProjection(Projections.projectionList()
//                            .add(Projections.property("id"), "id")
//                            .add(Projections.property("slug"), "slug")
//                            .add(Projections.property("status"), "status")
//                            .add(Projections.property("phone"), "phone")
//                            .add(Projections.property("countryMobileCode"), "countryMobileCode"))
//                    .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        } else if (type.equals("E")) {
//            createCriteria.add(Restrictions.eq("email", username));
//            createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//            createCriteria
//                    .setProjection(Projections.projectionList()
//                            .add(Projections.property("id"), "id")
//                            .add(Projections.property("slug"), "slug")
//                            .add(Projections.property("status"), "status"))
//                    .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        }
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();
//
//        return customer;
    }

    @Transactional
    public int updateTempCustomerToVerified(String otp, String type, String countryMobileCode, String mobileNo) {

        String isVerified = "YES";
        String hqlQuery = "UPDATE TblTempcustomer SET isverified=:isVerified WHERE otp=:otp AND type=:type AND countryMobileCode=:countryMobileCode AND mobileno=:mobileno AND isverified='NO'";
        Query query = getSession().createQuery(hqlQuery)
                .setParameter("isVerified", isVerified)
                .setParameter("otp", otp)
                .setParameter("type", type)
                .setParameter("countryMobileCode", countryMobileCode)
                .setParameter("mobileno", mobileNo);
        return query.executeUpdate();
    }

    @Transactional
    public int updateCustomerPasswordByMobile(String countryMobileCode, String phone, String password) {

        String hqlQuery = "UPDATE TblCustomer SET password=:password WHERE countryMobileCode=:countryMobileCode AND phone=:phone";

        Query query = getSession().createQuery(hqlQuery).setParameter("password", password)
                .setParameter("countryMobileCode", countryMobileCode).setParameter("phone", phone);
        return query.executeUpdate();
    }

    @Transactional
    public int updateCustomerPasswordByEmail(String email, String password) {
        String hqlQuery = "UPDATE TblCustomer SET password=:password WHERE email=:email";
        Query query = getSession().createQuery(hqlQuery).setParameter("password", password).setParameter("email", email);
        return query.executeUpdate();
    }

    @Transactional(readOnly = true)
    public TblCustomer getCustomerIdByMobileNo(String countryMobileCode, String phone, int customerId) {
        Query query = null;
        String hqlQuery = "";
        if (customerId != 0) {
            hqlQuery = "SELECT " +
                    "tc.id as id " +
                    "FROM TblCustomer tc " +
                    "WHERE " +
                    "tc.countryMobileCode =:countryMobileCode AND " +
                    "tc.phone =:phone AND " +
                    "tc.isDeleted =:isDeleted AND " +
                    "tc.id !=:id";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("countryMobileCode", countryMobileCode);
            query.setParameter("phone", phone);
            query.setParameter("isDeleted", "N");
            query.setParameter("id", customerId);
        } else {
            hqlQuery = "SELECT " +
                    "tc.id as id " +
                    "FROM TblCustomer tc " +
                    "WHERE " +
                    "tc.countryMobileCode =:countryMobileCode AND " +
                    "tc.phone =:phone AND " +
                    "tc.isDeleted =:isDeleted";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("countryMobileCode", countryMobileCode);
            query.setParameter("phone", phone);
            query.setParameter("isDeleted", "N");
        }
        return (TblCustomer) query.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//
//        createCriteria.add(Restrictions.eq("countryMobileCode", countryMobileCode));
//        createCriteria.add(Restrictions.eq("phone", phone));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        if (customerId != 0) {
//            createCriteria.add(Restrictions.ne("id", customerId));
//        }
//
//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();
//
//        return customer;

    }

    @Transactional
    public void saveCustomerDetailsToCustomerLogins(int customer_id, String deviceId, String deviceType, String ipAddress) {

        BigInteger loginTime = BigInteger.valueOf(Util.getCurrentTime());
        BigInteger created = loginTime;

        String hqlQuery = "SELECT tcl FROM TblCustomerLogin tcl WHERE tcl.deviceId =:deviceId AND tcl.deviceType =:deviceType";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("deviceId", deviceId);
        queryObj.setParameter("deviceType", deviceType);

        TblCustomerLogin tblCustomerLogin = (TblCustomerLogin) queryObj.uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerLogin.class);
//        createCriteria.add(Restrictions.eq("deviceId", deviceId));
//        createCriteria.add(Restrictions.eq("deviceType", deviceType));
//
//        TblCustomerLogin tblCustomerLogin = (TblCustomerLogin) createCriteria.uniqueResult();

        if (tblCustomerLogin != null) {
            tblCustomerLogin.setCustomerId(customer_id);
            tblCustomerLogin.setIpAddress(ipAddress);
            tblCustomerLogin.setLoginTime(loginTime);
            tblCustomerLogin.setCreated(created);
            getSession().update(tblCustomerLogin);
        } else {
            tblCustomerLogin = new TblCustomerLogin();
            tblCustomerLogin.setCustomerId(customer_id);
            tblCustomerLogin.setIpAddress(ipAddress);
            tblCustomerLogin.setDeviceId(deviceId);
            tblCustomerLogin.setDeviceType(deviceType);
            tblCustomerLogin.setLoginTime(loginTime);
            tblCustomerLogin.setCreated(created);
            getSession().save(tblCustomerLogin);
        }

    }

    @Transactional
    public void saveCustomerDetailsToCustomerLogs(int customer_id, String deviceId, String deviceType, String ipAddress, String deviceInfo, String appInfo) {

        BigInteger loginTime = BigInteger.valueOf(Util.getCurrentTime());
        BigInteger created = loginTime;

        TblCustomerLog tblCustomerLog = new TblCustomerLog();

        tblCustomerLog.setCustomerId(customer_id);
        tblCustomerLog.setIpAddress(ipAddress);
        tblCustomerLog.setDeviceId(deviceId);
        tblCustomerLog.setDeviceType(deviceType);
        tblCustomerLog.setLoginTime(loginTime);
        tblCustomerLog.setCreated(created);
        tblCustomerLog.setDeviceInfo(deviceInfo);
        tblCustomerLog.setAppInfo(appInfo);

        getSession().save(tblCustomerLog);

    }

    @Transactional(readOnly = true)
    public TblTempcustomer getDataByMobileFromTempCustomer(String phone, String countryMobileCode) {

        String hqlQuery = "SELECT ttc FROM TblTempcustomer ttc WHERE ttc.countryMobileCode =:countryMobileCode AND ttc.mobileno =:mobileno";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("countryMobileCode", countryMobileCode);
        queryObj.setParameter("mobileno", phone);

        TblTempcustomer TblTempcustomer = (TblTempcustomer) queryObj.uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblTempcustomer.class);
//        createCriteria.add(Restrictions.eq("countryMobileCode", countryMobileCode));
//        createCriteria.add(Restrictions.eq("mobileno", phone));
//        TblTempcustomer TblTempcustomer = (TblTempcustomer) createCriteria.uniqueResult();

        return TblTempcustomer;

    }

    @Transactional
    public int saveCustomer(TblCustomer fromJson) {

        Session newSession = getNewSession();
        Transaction tx = null;
        try {

            tx = newSession.beginTransaction();

            newSession.save(fromJson);
            int id = fromJson.getId();

            String myReferralCode = (Util.getAlphaNumericString(8) + id).toUpperCase();
            String myTeamName = (fromJson.getFirstname().trim().length() > 5 ? fromJson.getFirstname().trim().substring(0, 5).toUpperCase() : fromJson.getFirstname().trim().toUpperCase()) + id;

            String slug = Util.getCurrentTime() + "" + id;
            String hqlQuery = "UPDATE TblCustomer SET slug=:slug, referralCode=:myReferralCode, teamName=:myTeamName  WHERE id=:id";
            Query query = newSession.createQuery(hqlQuery).setParameter("slug", slug)
                    .setParameter("myReferralCode", myReferralCode).setParameter("myTeamName", myTeamName)
                    .setParameter("id", id);
            query.executeUpdate();

            tx.commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        return 0;

    }

    @Transactional
    public void insertInToTemp(String mobileno, String type, String country_mobile_code, String customer_data, String otp) {
        TblTempcustomer tempCustomer = new TblTempcustomer();
        tempCustomer.setCountryMobileCode(country_mobile_code);
        tempCustomer.setMobileno(mobileno);
        tempCustomer.setOtp(otp);
        tempCustomer.setType(type);
        tempCustomer.setCustomerData(customer_data);
        getSession().save(tempCustomer);
    }

    @Transactional(readOnly = true)
    public TblCustomer getCustomerIdByEmail(String email, int id) {
        Query query = null;
        String hqlQuery = "";
        if (id != 0) {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.email =:email AND tc.isDeleted =:isDeleted AND tc.id !=:id";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("email", email);
            query.setParameter("isDeleted", "N");
            query.setParameter("id", id);
        } else {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.email =:email AND tc.isDeleted =:isDeleted";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("email", email);
            query.setParameter("isDeleted", "N");
        }
        return (TblCustomer) query.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//
//        createCriteria.add(Restrictions.eq("email", email));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        if (id != 0) {
//            createCriteria.add(Restrictions.ne("id", id));
//        }
//
//        createCriteria.setProjection(Projections.projectionList()
//                        .add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();
//
//        return customer;

    }

    @Transactional(readOnly = true)
    public TblCustomer getUsedReferralCustomerId(String referralCode) {

        Query query = null;
        String hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.referralCode =:referralCode AND tc.isDeleted =:isDeleted";
        query = getSession().createQuery(hqlQuery);
        query.setParameter("referralCode", referralCode);
        query.setParameter("isDeleted", "N");
        return (TblCustomer) query.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//
//        createCriteria.add(Restrictions.eq("referralCode", referralCode));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();
//
//        return customer;

    }

    @Transactional(readOnly = true)
    public TblCustomer loginDetail(String email) {

        String hqlQuery = "SELECT " +
                "tc.id as id," +
                "tc.status as status," +
                "tc.password as password," +
                "tc.wrongAttempCreate as wrongAttempCreate," +
                "tc.repeatCount as repeatCount " +
                "FROM TblCustomer tc WHERE (tc.email =:email OR tc.fullPhone =:fullPhone) AND tc.isDeleted =:isDeleted";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("email", email);
        queryObj.setParameter("fullPhone", email);
        queryObj.setParameter("isDeleted", "N");
        return (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.add(Restrictions.or(Restrictions.eq("email", email), Restrictions.eq("fullPhone", email)));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("status"), "status");
//        projectionList.add(Projections.property("password"), "password");
//        projectionList.add(Projections.property("wrongAttempCreate"), "wrongAttempCreate");
//        projectionList.add(Projections.property("repeatCount"), "repeatCount");
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//        return (TblCustomer) createCriteria.uniqueResult();

    }

    @Transactional
    public void updateWrongPasswordCount(String email, int newRepeatCount) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        String hqlQuery = "UPDATE " +
                "TblCustomer " +
                "SET " +
                "wrongAttempCreate=:wrongAttempCreate," +
                "repeatCount=:repeatCount " +
                "WHERE " +
                "email=:email";
        Query query = getSession().createQuery(hqlQuery).setParameter("wrongAttempCreate", time)
                .setParameter("repeatCount", newRepeatCount).setParameter("email", email);
        query.executeUpdate();

    }

    private Object lockObject = new Object();

    @Transactional
    public void updateToken(String deviceId, String deviceType, String deviceToken, String DeviceInfo, String AppInfo) {
        synchronized (lockObject) {
            BigInteger loginTime = BigInteger.valueOf(Util.getCurrentTime());
            BigInteger created = loginTime;

            Session newSession = getNewSession();
            Transaction tx = null;

            try {
                tx = newSession.beginTransaction();

                String hqlQuery = "SELECT tcl FROM TblCustomerLogin tcl WHERE tcl.deviceId =:deviceId AND tcl.deviceType =:deviceType";
                Query queryObj = newSession.createQuery(hqlQuery);
                queryObj.setParameter("deviceId", deviceId);
                queryObj.setParameter("deviceType", deviceType);

                TblCustomerLogin tblCustomerLogin = (TblCustomerLogin) queryObj.uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerLogin.class);
//        createCriteria.add(Restrictions.eq("deviceId", deviceId));
//        createCriteria.add(Restrictions.eq("deviceType", deviceType));
//
//        TblCustomerLogin tblCustomerLogin = (TblCustomerLogin) createCriteria.uniqueResult();

                if (tblCustomerLogin != null) {

                    tblCustomerLogin.setDeviceToken(deviceToken);
                    tblCustomerLogin.setDeviceInfo(DeviceInfo);
                    tblCustomerLogin.setAppInfo(AppInfo);
                    tblCustomerLogin.setCreated(created);

                    newSession.update(tblCustomerLogin);
                    tx.commit();
                } else {

                    tblCustomerLogin = new TblCustomerLogin();
                    tblCustomerLogin.setDeviceToken(deviceToken);
                    tblCustomerLogin.setDeviceId(deviceId);
                    tblCustomerLogin.setDeviceType(deviceType);
                    tblCustomerLogin.setCreated(created);
                    tblCustomerLogin.setDeviceInfo(DeviceInfo);
                    tblCustomerLogin.setAppInfo(AppInfo);
                    tblCustomerLogin.setCustomerId(0);
                    newSession.save(tblCustomerLogin);
                    tx.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (tx != null) {
                    tx.rollback();
                }

            } finally {
                newSession.close();
            }
        }

    }

    @Transactional(readOnly = true)
    public TblTemplate getTemplate(String type, String title) {

        String hqlQuery = "SELECT " +
                "tt.id as id," +
                "tt.messageId as messageId," +
                "tt.content as content," +
                "tt.subject as subject " +
                "FROM TblTemplate tt WHERE tt.type =:type AND tt.title =:title";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("type", type);
        queryObj.setParameter("title", title);
        return (TblTemplate) queryObj.setResultTransformer(Transformers.aliasToBean(TblTemplate.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblTemplate.class);
//        createCriteria.add(Restrictions.eq("type", type));
//        createCriteria.add(Restrictions.eq("title", title));
//
//        createCriteria.setProjection(Projections.projectionList()
//                        .add(Projections.property("id"), "id")
//                        .add(Projections.property("messageId"), "messageId")
//                        .add(Projections.property("content"), "content")
//                        .add(Projections.property("subject"), "subject"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblTemplate.class));
//
//        TblTemplate template = (TblTemplate) createCriteria.uniqueResult();
//
//        return template;
    }


    @Transactional(readOnly = true)
    public TblCustomer getUpdatedProfileData(int id) {

        String hqlQuery = "SELECT tc FROM TblCustomer tc WHERE tc.id =:id";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("id", id);
        return (TblCustomer) queryObj.uniqueResult();
//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.add(Restrictions.eq("id", id));
//        TblCustomer tblCustomer = (TblCustomer) createCriteria.uniqueResult();
//        return tblCustomer;

    }

    @Transactional(readOnly = true)
    public List<TblSetting> getSettingData() {
        String hqlQuery = "SELECT ts FROM TblSetting ts";
        return getSession().createQuery(hqlQuery).getResultList();
//        return getSession().createCriteria(TblSetting.class).list();

    }

    @Transactional(readOnly = true)
    public List<TblTaxes> getTotalTaxPercent() {

        String hqlQuery = "SELECT tt FROM TblTaxes tt WHERE tt.status =:status AND tt.isDeleted =:isDeleted";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        return queryObj.getResultList();

//        Criteria createCriteria = getSession().createCriteria(TblTaxes.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        return createCriteria.list();
    }

    @Transactional
    public void removeMobileFromTemp(String mobileNo, String mobile_code) {

        String hqlQuery = "DELETE from TblTempcustomer where mobileno=:mobileno AND countryMobileCode=:countryMobileCode";
        getSession().createQuery(hqlQuery).setParameter("mobileno", mobileNo).setParameter("countryMobileCode", mobile_code)
                .executeUpdate();
    }

    @Transactional
    public void removeEmailFromTemp(String email) {

        String hqlQuery = "DELETE FROM TblTempcustomer WHERE mobileno=:mobileno";
        getSession().createQuery(hqlQuery).setParameter("mobileno", email).executeUpdate();

    }

    @Transactional
    public void updateWallet(int customerId, float amount, String walletType, String trnType) {

        String wallet = "";
        if (walletType.equals("bonus_wallet")) {
            wallet = "bonusWallet";
        } else if (walletType.equals("winning_wallet")) {
            wallet = "winningWallet";
        } else if (walletType.equals("deposit_wallet")) {
            wallet = "depositWallet";
        }

        if (trnType.equals("CREDIT")) {
            String hqlQuery = "UPDATE TblCustomer SET " + wallet + "=" + wallet + "+:amount WHERE id=:id";
            Query query = getSession().createQuery(hqlQuery).setParameter("amount", amount).setParameter("id", customerId);
            query.executeUpdate();
        } else {
            String hqlQuery = "UPDATE TblCustomer SET " + wallet + "=" + wallet + "-:amount WHERE id=:id";
            Query query = getSession().createQuery(hqlQuery).setParameter("amount", amount).setParameter("id", customerId);
            query.executeUpdate();
        }
    }

    public void updateWallet(Session session, int customerId, float amount, String walletType, String trnType) {

        String wallet = "";
        if (walletType.equals("bonus_wallet")) {
            wallet = "bonusWallet";
        } else if (walletType.equals("winning_wallet")) {
            wallet = "winningWallet";
        } else if (walletType.equals("deposit_wallet")) {
            wallet = "depositWallet";
        }

        if (trnType.equals("CREDIT")) {
            String hqlQuery = "UPDATE TblCustomer SET " + wallet + "=" + wallet + "+:amount WHERE id=:id";
            Query query = session.createQuery(hqlQuery).setParameter("amount", amount).setParameter("id", customerId);
            query.executeUpdate();
        } else {
            String hqlQuery = "UPDATE TblCustomer SET " + wallet + "=" + wallet + "-:amount WHERE id=:id";
            Query query = session.createQuery(hqlQuery).setParameter("amount", amount).setParameter("id", customerId);
            query.executeUpdate();
        }
    }

    @Transactional(readOnly = true)
    public Float getCustomerWalletAmount(int customerId, String walletType) {
        String getWallet = "";

        if (walletType.equals("bonus_wallet")) {
            getWallet = "bonusWallet";
        } else if (walletType.equals("winning_wallet")) {
            getWallet = "winningWallet";
        } else if (walletType.equals("deposit_wallet")) {
            getWallet = "depositWallet";
        }

        String hqlQuery = "SELECT tc.id as id,tc." + getWallet + " as " + getWallet + " FROM TblCustomer tc WHERE tc.id =:id";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("id", customerId);
        TblCustomer tblCustomer = (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();
//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.add(Restrictions.eq("id", customerId));
//        createCriteria
//                .setProjection(Projections.projectionList()
//                        .add(Projections.property("id"), "id")
//                        .add(Projections.property(getWallet), getWallet))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//        TblCustomer tblCustomer = (TblCustomer) createCriteria.uniqueResult();

        if (walletType.equals("bonus_wallet")) {
            return tblCustomer.getBonusWallet();
        } else if (walletType.equals("winning_wallet")) {
            return tblCustomer.getWinningWallet();
        } else if (walletType.equals("deposit_wallet")) {
            return tblCustomer.getDepositWallet();
        }

        return (float) 0.00;
    }

    @Transactional
    public Integer insertWalletHistory(TblCustomerWalletHistory tblCustomerWalletHistory) {
        getSession().save(tblCustomerWalletHistory);

        return tblCustomerWalletHistory.getId();
    }

    @Transactional
    public void saveNotification(TblNotification tblNotification) {
        getSession().save(tblNotification);

    }

    @Transactional(readOnly = true)
    public List<TblCustomerLogin> GetAndroidTokens(Integer[] customerId) {

        String hqlQuery = "SELECT tcl.deviceToken as deviceToken FROM TblCustomerLogin tcl WHERE tcl.customerId IN(:customerId) AND tcl.deviceType =:deviceType";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameterList("customerId", customerId);
        queryObj.setParameter("deviceType", "A");
        queryObj.setResultTransformer(new AliasToBeanResultTransformer(TblCustomerLogin.class));
        List resultList = queryObj.getResultList();
        if (resultList != null && resultList.size() > 0) {
            return resultList;
        } else {
            return null;
        }

//        Criteria createCriteria = getSession().createCriteria(TblCustomerLogin.class);
//        createCriteria.add(Restrictions.in("customerId", customerId));
//        createCriteria.add(Restrictions.eq("deviceType", "A"));
//
//        createCriteria
//                .setProjection(Projections.projectionList().add(Projections.property("deviceToken"), "deviceToken"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomerLogin.class));
//        return createCriteria.list();

    }

    @Transactional(readOnly = true)
    public List<TblCustomerLogin> GetIosTokens(Integer[] customerId) {

        String hqlQuery = "SELECT tcl.deviceToken as deviceToken FROM TblCustomerLogin tcl WHERE tcl.customerId IN(:customerId) AND tcl.deviceType =:deviceType";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameterList("customerId", customerId);
        queryObj.setParameter("deviceType", "I");
        queryObj.setResultTransformer(new AliasToBeanResultTransformer(TblCustomerLogin.class));
        List resultList = queryObj.getResultList();
        if (resultList != null && resultList.size() > 0) {
            return resultList;
        } else {
            return null;
        }

//        Criteria createCriteria = getSession().createCriteria(TblCustomerLogin.class);
//        createCriteria.add(Restrictions.in("customerId", customerId));
//        createCriteria.add(Restrictions.eq("deviceType", "I"));
//
//        createCriteria
//                .setProjection(Projections.projectionList().add(Projections.property("deviceToken"), "deviceToken"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomerLogin.class));
//        return createCriteria.list();

    }

    @Transactional(readOnly = true)
    public List<TblReferralCashBonus> getReferCashBonus() {
        String hqlQuery = "SELECT trcb FROM TblReferralCashBonus trcb";
        return getSession().createQuery(hqlQuery).getResultList();
//        return getSession().createCriteria(TblReferralCashBonus.class).list();

    }

    @Transactional
    public TblTempcustomer updateAndDeleteOtp(String countryMobileCode, String phone) {

        String hqlQuery = "UPDATE TblTempcustomer SET repeatCount=repeatCount+1 WHERE countryMobileCode=:countryMobileCode AND mobileno=:mobileno";
        Query query = getSession().createQuery(hqlQuery).setParameter("countryMobileCode", countryMobileCode)
                .setParameter("mobileno", phone);
        int executeUpdate = query.executeUpdate();

        String hql1 = "SELECT ttc.id as id, ttc.repeatCount as repeatCount FROM TblTempcustomer ttc WHERE ttc.countryMobileCode =:countryMobileCode AND ttc.mobileno =:mobileno";
        Query query1 = getSession().createQuery(hql1);
        query1.setParameter("countryMobileCode", countryMobileCode);
        query1.setParameter("mobileno", phone);
        return (TblTempcustomer) query1.setResultTransformer(Transformers.aliasToBean(TblTempcustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblTempcustomer.class);
//        createCriteria.add(Restrictions.eq("countryMobileCode", countryMobileCode));
//        createCriteria.add(Restrictions.eq("mobileno", phone));
//        createCriteria
//                .setProjection(Projections.projectionList()
//                        .add(Projections.property("id"), "id")
//                        .add(Projections.property("repeatCount"), "repeatCount"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblTempcustomer.class));
//
//        TblTempcustomer tempCustomer = (TblTempcustomer) createCriteria.uniqueResult();
//
//        return tempCustomer;

    }

    @Transactional
    public String logOut(Integer UserId, String deviceId, String deviceType) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());

        String hql = "SELECT tcl FROM TblCustomerLog tcl WHERE tcl.deviceId =:deviceId AND tcl.deviceType =:deviceType AND tcl.customerId =:customerId";
        Query queryObj = getSession().createQuery(hql);
        queryObj.setParameter("deviceId", deviceId);
        queryObj.setParameter("deviceType", deviceType);
        queryObj.setParameter("customerId", UserId);
        Integer totalCount = queryObj.getResultList().size();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerLog.class);
//        createCriteria.add(Restrictions.eq("deviceId", deviceId));
//        createCriteria.add(Restrictions.eq("deviceType", deviceType));
//        createCriteria.add(Restrictions.eq("customerId", UserId));
//        Integer totalCount = createCriteria.setProjection(Projections.rowCount()).uniqueResult().hashCode();
        if (totalCount > 0) {

            String hqlQuery = "UPDATE TblCustomerLog SET logoutTime=:logoutTime WHERE deviceId=:deviceId AND deviceType=:deviceType AND customerId=:customerId ";
            Query query = getSession().createQuery(hqlQuery).setParameter("logoutTime", time)
                    .setParameter("deviceId", deviceId).setParameter("deviceType", deviceType)
                    .setParameter("customerId", UserId);
            query.executeUpdate();

            String hqlQueryNew = "UPDATE TblCustomerLogin SET customerId=:customerId1 WHERE deviceId=:deviceId AND deviceType=:deviceType AND customerId=:customerId ";
            Query queryNew = getSession().createQuery(hqlQueryNew).setParameter("customerId1", 0)
                    .setParameter("deviceId", deviceId).setParameter("deviceType", deviceType)
                    .setParameter("customerId", UserId);
            queryNew.executeUpdate();

        } else {

            return "INVALID_USER_ACCESS";
        }

        return "SUCCESS";

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, LinkedHashMap<String, Object>> getUpdatedWalletData(Integer UserId) {

        String hqlQuery = "SELECT " +
                "winningWallet as winningWallet, " +
                "depositWallet as depositWallet, " +
                "bonusWallet as bonusWallet, " +
                "pendingWidWallet as pendingWidWallet " +
                "FROM " +
                "TblCustomer " +
                "where " +
                "id=:id";
        Query query = getSession().createQuery(hqlQuery).setParameter("id", UserId);
        HashMap<String, Object> result = (HashMap<String, Object>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        LinkedHashMap<String, LinkedHashMap<String, Object>> output = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> innerResult = new LinkedHashMap<String, Object>();
        float realWinningAmount = (float) result.get("winningWallet") - (float) result.get("pendingWidWallet");
        if (realWinningAmount < 0) {
            realWinningAmount = 0;
        }
        innerResult.put("winning_wallet", realWinningAmount);
        innerResult.put("bonus_wallet", result.get("bonusWallet"));
        innerResult.put("deposit_wallet", result.get("depositWallet"));
        innerResult.put("pending_wid_amount", result.get("pendingWidWallet"));
        innerResult.put("winning_wallet_actual", result.get("winningWallet"));
        output.put("wallet", innerResult);

        return output;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getCustomerWalletHistoryFilter(HashSet<String> array, Integer UserId, int page_no, String type) {

        String hqlQuery = "SELECT " +
                "id as id, " +
                "customerId as customerId," +
                "matchContestId as matchContestId," +
                "walletType as walletType," +
                "transactionType as transactionType," +
                "transactionId as transactionId," +
                "type as type," +
                "sum(previousAmount) as previousAmount," +
                "sum(amount) as  amount ," +
                "sum(currentAmount) as  currentAmount," +
                "description as description," +
                "status as status," +
                "rcbId as rcbId," +
                "refCwhId as refCwhId," +
                "createdBy as createdBy," +
                "created as created " +
                "from " +
                "TblCustomerWalletHistory " +
                "where " +
                "customerId=:customerId  " +
                "AND type IN(:type) " +
                "GROUP BY transactionId " +
                "ORDER BY id DESC";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", UserId).setParameter("type", array);

        if (page_no > 0) {
            int limit = 20;
            int offset = (page_no - 1) * limit;
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        List<HashMap<String, Object>> output = new ArrayList<HashMap<String, Object>>();
        if (result != null) {
            String WALLET_TYPE_DEPOSIT = "Deposit Wallet";
            String WALLET_TYPE_WINNING = "Winning Wallet";
            String WALLET_TYPE_BONUS = "Bonus Wallet";
            for (HashMap<String, Object> historyData : result) {
                String historyType = (String) historyData.get("type");
                String historyWalletType = (String) historyData.get("walletType");
                HashMap<String, Object> history = new HashMap<String, Object>();
                history.put("wallet_type", historyWalletType);
                history.put("type", historyType);

                if (type.equals("Win")) {
                    if (historyType.equals("WALLET_WITHDRAW_ADMIN") || historyType.equals("WALLET_RECHARGE_ADMIN")) {
                        if (!historyWalletType.equals(WALLET_TYPE_WINNING)) {
                            continue;
                        }
                    }
                } else if (type.equals("Deposit")) {
                    if (historyType.equals("WALLET_WITHDRAW_ADMIN")
                            || historyType.equals("WALLET_RECHARGE_ADMIN")
                            || historyType.equals("CUSTOMER_RECEIVED_REFCB")
                            || historyType.equals("REGISTER_CASH_BONUS")) {
                        if (!historyWalletType.equals(WALLET_TYPE_DEPOSIT)) {
                            continue;
                        }
                    }
                } else if (type.equals("Bonus")) {
                    if (historyType.equals("WALLET_WITHDRAW_ADMIN")
                            || historyType.equals("WALLET_RECHARGE_ADMIN")
                            || historyType.equals("CUSTOMER_RECEIVED_REFCB")
                            || historyType.equals("REGISTER_CASH_BONUS")) {
                        if (!historyWalletType.equals(WALLET_TYPE_BONUS)) {
                            continue;
                        }
                    }
                }

                history.put("transaction_type", historyData.get("transactionType"));
                history.put("transaction_id", historyData.get("transactionId"));
                history.put("previous_amount", historyData.get("previousAmount"));
                history.put("amount", historyData.get("amount"));
                history.put("current_amount", historyData.get("currentAmount"));
                history.put("description", historyData.get("description"));
                history.put("status", historyData.get("status"));
                history.put("created", historyData.get("created"));

                switch (historyType) {
                    case "CUSTOMER_WALLET_RECHARGE": {
                        history.put("title_text", "Deposit Cash");
                        history.put("description", "Deposit wallet recharged.");

                    }
                    break;
                    case "WALLET_RECHARGE_ADMIN": {
                        if (historyWalletType.equals(WALLET_TYPE_WINNING)) {
                            history.put("title_text", "Admin Recharge Winning Wallet");
                        } else if (historyWalletType.equals(WALLET_TYPE_DEPOSIT)) {
                            history.put("title_text", "Admin Recharge Deposit Wallet");
                        } else if (historyWalletType.equals(WALLET_TYPE_BONUS)) {
                            history.put("title_text", "Admin Recharge Bonus Wallet");
                        } else {
                            history.put("title_text", "Deposit Cash (Admin)");
                        }
                    }
                    break;
                    case "WALLET_WITHDRAW_ADMIN": {
                        if (historyWalletType.equals(WALLET_TYPE_WINNING)) {
                            history.put("title_text", "Admin Withdraw Winning Wallet");
                        } else if (historyWalletType.equals(WALLET_TYPE_DEPOSIT)) {
                            history.put("title_text", "Admin Withdraw Deposit Wallet");
                        } else if (historyWalletType.equals(WALLET_TYPE_BONUS)) {
                            history.put("title_text", "Admin Withdraw Bonus Wallet");
                        } else {
                            history.put("title_text", "Withdraw Cash (Admin)");
                        }
                    }
                    break;
                    case "CUSTOMER_WIN_CONTEST": {
                        history.put("title_text", "Won A Contest");
                        history.put("description", "choic11 cash prize payout");
                    }
                    break;
                    case "CUSTOMER_WIN_LEADERBOARD": {
                        history.put("title_text", "Won A Leaderboard");
                        history.put("description", "choic11 cash prize payout");
                    }
                    break;
                    case "CUSTOMER_MR_RECEIVED": {
                        history.put("title_text", "Received referral earning");
                        history.put("description", "choic11 referral earning");
                    }
                    break;
                    case "CUSTOMER_JOIN_CONTEST": {
                        history.put("title_text", "Joined A Contest");
                        history.put("description", "Join choic11 contest with cash");
                    }
                    break;
                    case "CUSTOMER_RECEIVED_RCB": {
                        history.put("title_text", "Cash Bonus Received");
                        history.put("description", "Recharge cash bonus received");
                    }
                    break;
                    case "CUSTOMER_RECEIVED_RCBD": {
                        history.put("title_text", "Cash Back Received");
                        history.put("description", "Recharge cash back received");
                    }
                    break;
                    case "CUSTOMER_RECEIVED_REFCB": {
                        if (historyWalletType.equals(WALLET_TYPE_DEPOSIT)) {
                            history.put("title_text", "Referral Cash Deposit");
                            history.put("description", "Referral cash deposit received");
                        } else{
                            history.put("title_text", "Referral Cash Bonus");
                            history.put("description", "Referral cash bonus received");
                        }
                    }
                    break;
                    case "REGISTER_CASH_BONUS": {
                        if (historyWalletType.equals(WALLET_TYPE_DEPOSIT)) {
                            history.put("title_text", "Register Cash Deposit");
                            history.put("description", "Register cash deposit received");
                        } else{
                            history.put("title_text", "Register Cash Bonus");
                            history.put("description", "Register cash bonus received");
                        }

                    }
                    break;
                    case "CUSTOMER_REFUND_CONTEST": {
                        history.put("title_text", "Refund Contest");
                        history.put("description", "Contest entry fee refund");
                    }
                    break;
                    case "CUSTOMER_REFUND_ABCONTEST": {
                        history.put("title_text", "Refund Contest");
                        history.put("description", "Contest entry fee refund");
                    }
                    break;

                }

                output.add(history);
            }
        }

        return output;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getAvailablePromoCodes(Integer UserId) {

        long currentTime = Util.getCurrentTime();

        String hqlQuery = "SELECT " +
                "trcb.id as id, " +
                "trcb.recharge as recharge, " +
                "trcb.cachBonus as cachBonus, " +
                "trcb.isUse as isUse, " +
                "trcb.isUseMax as isUseMax, " +
                "trcb.maxRecharge as maxRecharge, " +
                "trcb.cashBonusType as cashBonusType, " +
                "trcb.code as code, " +
                "trcb.startDate as startDate, " +
                "trcb.endDate as endDate, " +
                "COALESCE(trcbc.usedCount,0) as alreadyUse " +
                "FROM " +
                "TblRechargeCachBonus trcb " +
                "LEFT JOIN TblRechargeCachBonusCustomer trcbc ON (trcbc.customerId=:customerId AND trcbc.rcbId=trcb.id) " +
                "WHERE  " +
                "trcb.status='A' " +
                "AND trcb.isDeleted='N' " +
                "AND trcb.endDate > :endDate " +
                "ORDER BY trcb.createdAt DESC";
        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", UserId).setParameter("endDate", BigInteger.valueOf(currentTime));
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return result;

    }

    public boolean insertWithdrawRequest(float amount, int customerId, String type, String withdrawType, float instantFee, float instantServiceTax, float instantGst) {

        Session newSession = getNewSession();

        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();

            BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
            String transactionId = "WIDWWALL-" + time + customerId;
            TblWithdrawRequest tblWithdrawRequest = TblWithdrawRequest.getInstance();
            tblWithdrawRequest.setCustomerId(customerId);
            tblWithdrawRequest.setAmount(amount);
            tblWithdrawRequest.setTransactionId(transactionId);
            tblWithdrawRequest.setCreatedAt(time);
            tblWithdrawRequest.setType(type);
            tblWithdrawRequest.setWithdrawType(withdrawType);
            tblWithdrawRequest.setInstantFee(instantFee);
            tblWithdrawRequest.setServiceTax(instantServiceTax);
            tblWithdrawRequest.setGst(instantGst);
            newSession.save(tblWithdrawRequest);

            float pendingWidWallet = getCustomerPendingWithdrawAmount(newSession, customerId);

            String hqlQuery = "UPDATE TblCustomer SET pendingWidWallet=:pendingWidWallet WHERE id=:id";

            Query query = newSession.createQuery(hqlQuery).setParameter("pendingWidWallet", pendingWidWallet)
                    .setParameter("id", customerId);
            query.executeUpdate();

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

    @Transactional
    public float getCustomerPendingWithdrawAmount(Session newSession, int customerId) {

        if (newSession == null) {
            newSession = getSession();
        }

        String hqlQuery = "SELECT COALESCE(SUM(amount),0) as amount from TblWithdrawRequest where customerId=:customerId AND (status='P' OR status='RP' OR status='H')";

        Query query = newSession.createQuery(hqlQuery).setParameter("customerId", customerId);

        List resultList = query.getResultList();
        if (resultList != null && resultList.size() > 0) {
            Object object = resultList.get(0);
            if (object != null) {
                return Float.parseFloat(String.valueOf((double) object));
            }
        }
        return 0f;
    }

    @Transactional(readOnly = true)
    public int getTodayCustomerWithdrawByInstant(Integer authUserId) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        long timeInMillis = instance.getTimeInMillis() / 1000;

        String hqlQuery = "SELECT " + "tcwh.id as id " + "FROM TblWithdrawRequest tcwh " + "WHERE "
                + "tcwh.customerId = :customerId AND tcwh.status!='R' AND tcwh.createdAt >= :created AND tcwh.withdrawType = 'B' AND tcwh.type='INSTANT'";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", authUserId).setParameter("created",
                BigInteger.valueOf(timeInMillis));

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result.size();

    }

    @Transactional(readOnly = true)
    public int getTodayCustomerWithdrawByPaytm(Integer authUserId) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        long timeInMillis = instance.getTimeInMillis() / 1000;

        String hqlQuery = "SELECT " + "tcwh.id as id " + "FROM TblWithdrawRequest tcwh " + "WHERE "
                + "tcwh.customerId = :customerId AND tcwh.status!='R' AND tcwh.createdAt >= :created AND tcwh.withdrawType = 'P'";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", authUserId).setParameter("created",
                BigInteger.valueOf(timeInMillis));

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result.size();

    }

    @Transactional(readOnly = true)
    public int getTodayCustomerPaytmWithdraw(Integer authUserId) throws ParseException {

        int withdrawType = 1;
        TimeZone userTimezone = TimeZone.getTimeZone("Asia/Kolkata");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);

        int offset = userTimezone.getOffset(calendar.getTimeInMillis());

        long userCurrentTime = calendar.getTimeInMillis() + offset;

        String userTimezoneDate = Util.getFormatedDate(userCurrentTime, "yyyy-MM-dd");

        DateFormat dateInstance = DateFormat.getDateInstance();

        Date fromTimeObject = Util.getFormattedDateObject(userTimezoneDate + " 00:00:00", "yyyy-MM-dd hh:mm:ss");
        Date toTimeObject = Util.getFormattedDateObject(userTimezoneDate + " 23:59:59", "yyyy-MM-dd hh:mm:ss");

        long fromTime = fromTimeObject.getTime() - offset;
        long toTime = toTimeObject.getTime() - offset;


        fromTime = Math.round(fromTime / 1000f);
        toTime = Math.round(toTime / 1000f);

        String hqlQuery = "SELECT " + "tcwh.id as id " + "FROM TblWithdrawRequest tcwh " + "WHERE "
                + "tcwh.customerId = :customerId AND tcwh.createdAt >=:fromTime AND tcwh.createdAt <= :toTime AND tcwh.withdrawType=:withdrawType";

        Query query = getSession().createQuery(hqlQuery)
                .setParameter("customerId", authUserId)
                .setParameter("fromTime", BigInteger.valueOf(fromTime))
                .setParameter("toTime", BigInteger.valueOf(toTime))
                .setParameter("withdrawType", withdrawType);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result == null ? 0 : result.size();
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getCustomerWithdrawHistory(Integer UserId, int page_no) {
        String hqlQuery = "SELECT " +
                "id as id," +
                "customerId as customer_id," +
                "transactionId as transaction_id," +
                "amount as amount," +
                "status as status," +
                "reason as reason," +
                "referenceId as referenceId," +
                "utr as utr," +
                "createdAt as created_at," +
                "updatedAt as updated_at," +
                "actionTime as action_time," +
                "updatedBy as updated_by," +
                "jsonData as json_data," +
                "withdrawalsAmountCharges as withdrawals_amount_charges " +
                "from " +
                "TblWithdrawRequest " +
                "where " +
                "customerId=:customerId " +
                "ORDER BY id desc";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", UserId);

        if (page_no > 0) {
            int limit = 20;
            int offset = (page_no - 1) * limit;
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;

    }

    @Transactional(readOnly = true)
    public TblCustomer getCustomerIdByPaytmMobileNo(String phone, int customerId) {

        String hqlQuery;
        Query query = null;
        if (customerId != 0) {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.paytmphone =:paytmphone AND tc.isDeleted =:isDeleted AND tc.id !=:id";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("paytmphone", phone);
            query.setParameter("isDeleted", "N");
            query.setParameter("id", customerId);
        } else {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.paytmphone =:paytmphone AND tc.isDeleted =:isDeleted";
            query = getSession().createQuery(hqlQuery);
            query.setParameter("paytmphone", phone);
            query.setParameter("isDeleted", "N");
        }
        return (TblCustomer) query.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();
//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//
//        createCriteria.add(Restrictions.eq("paytmphone", phone));
//        createCriteria.add(Restrictions.eq("paytmVerified", paytmVerified));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        if (customerId != 0) {
//            createCriteria.add(Restrictions.ne("id", customerId));
//        }

//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();
//
//        return customer;

    }

    @Transactional
    public int updateCustomerDynamicLink(Integer authUserId, String generateDynamicLink) {
        String hqlQuery = "UPDATE TblCustomer SET dynamicLink=:dynamicLink WHERE id=:id";
        Query query = getSession().createQuery(hqlQuery).setParameter("dynamicLink", generateDynamicLink).setParameter("id",
                authUserId);
        return query.executeUpdate();
    }

    @Transactional
    public void UpdateProfileData(Integer authUserId, String firstName, String lastName, String email,
                                  String countryMobileCode, long dob, String phone, int country, int state, String city, String addressLine1,
                                  String addressLine2, String pincode) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        String hqlQuery = "UPDATE TblCustomer SET firstname=:firstname,lastname=:lastname,modified=:modified,dob=:dob,country=:country,state=:state,city=:city,addressline1=:addressline1,addressline2=:addressline2,pincode=:pincode WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery).setParameter("firstname", firstName)
                .setParameter("lastname", lastName).setParameter("modified", time)
                .setParameter("dob", BigInteger.valueOf(dob)).setParameter("country", country)
                .setParameter("state", state).setParameter("city", city).setParameter("addressline1", addressLine1)
                .setParameter("addressline2", addressLine2).setParameter("pincode", pincode)
                .setParameter("id", authUserId);
        query.executeUpdate();

    }

    @Transactional
    public void updateVerifyEmail(String email, String isSocial, String socialType, String isEmailVerified,
                                  String emailToken, BigInteger time, Integer authUserId) {
        String hqlQuery = "UPDATE TblCustomer SET email=:email,isSocial=:isSocial,socialType=:socialType,isEmailVerified=:isEmailVerified,modified=:modified,emailToken=:emailToken,emailTokenAt=:emailTokenAt WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery).setParameter("email", email).setParameter("isSocial", isSocial)
                .setParameter("socialType", socialType).setParameter("isEmailVerified", isEmailVerified)
                .setParameter("modified", time).setParameter("emailToken", emailToken)
                .setParameter("emailTokenAt", time).setParameter("id", authUserId);
        query.executeUpdate();

    }

    @Transactional
    public void updateVerifyMobile(BaseRequest baseRequest) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        String hqlQuery = "UPDATE TblCustomer SET countryMobileCode=:countryMobileCode,phone=:phone,isPhoneVerified=:isPhoneVerified,modified=:modified WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery)
                .setParameter("countryMobileCode", baseRequest.getParam("country_mobile_code"))
                .setParameter("phone", baseRequest.getParam("phone")).setParameter("isPhoneVerified", "Y")
                .setParameter("modified", time).setParameter("id", baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional
    public void updateVerifyMobilePaytm(BaseRequest baseRequest) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        String hqlQuery = "UPDATE TblCustomer SET paytmphone=:paytmphone, modified=:modified WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery).setParameter("paytmphone", baseRequest.getParam("phone"))
                .setParameter("modified", time).setParameter("id", baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional(readOnly = true)
    public List<TblCustomerAvatar> getProfilePictures() {
        String hqlQuery = "SELECT tca FROM TblCustomerAvatar tca WHERE tca.status =:status AND tca.isDeleted =:isDeleted";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        return queryObj.getResultList();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerAvatar.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        return createCriteria.list();
    }

    @Transactional
    public void changeProfilePicture(BaseRequest baseRequest) {

        String image = "";
        String fbImage = "";
        if (baseRequest.hasParam("image") && !Util.isEmpty(baseRequest.getParam("image"))) {
            image = baseRequest.getParam("image");

            String[] split = image.split("/");
            image = split[split.length - 1];
        }

        if (baseRequest.hasParam("fb_image") && !Util.isEmpty(baseRequest.getParam("fb_image"))) {
            fbImage = baseRequest.getParam("fb_image");
        }

        if (baseRequest.hasParam("g_image") && !Util.isEmpty(baseRequest.getParam("g_image"))) {
            fbImage = baseRequest.getParam("g_image");
        }

        String hqlQuery = "UPDATE TblCustomer SET image=:image, externalImage=:fbImage WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery).setParameter("image", image).setParameter("fbImage", fbImage)
                .setParameter("id", baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional(readOnly = true)
    public TblCustomer getCustomerByIdForChangePassword(int CustomerId) {

        String hqlQuery = "SELECT tc.password as password,tc.status as status,tc.isDeleted as isDeleted FROM TblCustomer tc WHERE tc.id =:id AND tc.isDeleted =:isDeleted AND tc.status =:status";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("id", CustomerId);
        queryObj.setParameter("isDeleted", "N");
        queryObj.setParameter("status", "A");
        return (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.add(Restrictions.eq("id", CustomerId));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.add(Restrictions.eq("status", "A"));

//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("password"), "password");
//        projectionList.add(Projections.property("status"), "status");
//        projectionList.add(Projections.property("isDeleted"), "isDeleted");
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//        return (TblCustomer) createCriteria.uniqueResult();

    }

    @Transactional
    public void updatePassword(BaseRequest baseRequest) {
        String Password = baseRequest.getParam("password");
        Password = Util.convertToMD5(Password);
        String hqlQuery = "UPDATE TblCustomer SET password=:password WHERE id=:id";

        Query query = getSession().createQuery(hqlQuery).setParameter("password", Password).setParameter("id",
                baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional
    public HashMap<String, Object> getCustomerDynamicLink(Integer authUserId) {
        String hqlQuery = "SELECT "
                + "COALESCE(dynamicLink,'0') as dynamic_link, referralCode as referral_code "
                + "FROM TblCustomer WHERE id=:authUserId";

        Query query = getSession().createQuery(hqlQuery).setParameter("authUserId", authUserId);

        return (HashMap<String, Object>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getSingleResult();

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getReferEarn(Integer UserId) {

        String hqlQuery = "SELECT "
                + "count(id) as team_count, COALESCE(sum(usedRefferalAmount),0) as team_earn, COALESCE(sum(usedRefferalGivenAmount),0) as total_received_amount, "
                + "(SELECT COALESCE(sum(earning),0) FROM TblCricketCustomerAffiliate where customerId=:used_referral_user_id AND isAffiliateDistribute='Y') as total_received_amount_aff, "
                + "(SELECT CONCAT(COALESCE(dynamicLink,'0'),'-------', referralCode) as data from TblCustomer where id=:id) as user_ref FROM TblCustomer WHERE usedReferralUserId=:used_referral_user_id";

        Query query = getSession().createQuery(hqlQuery).setParameter("id", UserId).setParameter("used_referral_user_id",
                UserId);

        return (HashMap<String, Object>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getSingleResult();

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getReferEarnDetail(Integer UserId, int pageNo) {

        String hqlQuery = "SELECT firstname as firstname, lastname as lastname, teamName as teamName, COALESCE(image,'') as image, COALESCE(externalImage,'') as externalImage, usedRefferalAmount as usedRefferalAmount, usedRefferalGivenAmount as receivedReferralAmount FROM TblCustomer tc WHERE usedReferralUserId=:usedReferralUserId";

        Query query = getSession().createQuery(hqlQuery).setParameter("usedReferralUserId", UserId);
        if (pageNo > 0) {
            int limit = 20;
            int offset = (pageNo - 1) * limit;

            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<HashMap<String, Object>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<HashMap<String, Object>> matchesData = new ArrayList<HashMap<String, Object>>();
        Float totalReceivedAmount = (float) 0.00;
        for (HashMap<String, Object> object : result) {
            HashMap<String, Object> innerResult = new HashMap<String, Object>();
            innerResult.put("firstname", object.get("firstname"));
            innerResult.put("lastname", object.get("lastname"));
            innerResult.put("team_name", object.get("teamName"));
            innerResult.put("image", Util.generateImageUrl(object.get("image").toString(), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

            if (object.get("externalImage") != null && !Util.isEmpty(object.get("externalImage").toString())) {
                innerResult.put("image", object.get("externalImage").toString());
            }

            innerResult.put("used_refferal_amount", object.get("usedRefferalAmount"));
            innerResult.put("received_referral_amount", object.get("receivedReferralAmount"));

            totalReceivedAmount = totalReceivedAmount + (float) object.get("receivedReferralAmount");
            matchesData.add(innerResult);
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("user_refer_data", matchesData);
        output.put("refer_data", totalReceivedAmount);

        return output;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getPromoCodeDetailByPromoCodeCustom(Integer UserId, String promocode) {

        String hqlQuery = "SELECT " +
                "trcb.id as id, " +
                "trcb.recharge as recharge, " +
                "trcb.cachBonus as cachBonus, " +
                "trcb.isUse as isUse, " +
                "trcb.isUseMax as isUseMax, " +
                "trcb.maxRecharge as maxRecharge, " +
                "trcb.cashBonusType as cashBonusType, " +
                "trcb.code as code, " +
                "trcb.startDate as startDate, " +
                "trcb.endDate as endDate, " +
                "COALESCE(trcbc.usedCount,0) as alreadyUse " +
                "FROM " +
                "TblRechargeCachBonus trcb " +
                "LEFT JOIN TblRechargeCachBonusCustomer trcbc ON (trcbc.customerId=:customerId AND trcbc.rcbId=trcb.id) " +
                "WHERE  " +
                "trcb.status='A' " +
                "AND trcb.isDeleted='N' " +
                "AND trcb.code=:code";
        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", UserId).setParameter("code",
                promocode);

        query.setFirstResult(0);
        query.setMaxResults(1);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        if (result.size() == 0) {
            return null;
        }

        return result.get(0);

    }

    @Transactional(readOnly = true)
    public boolean isTeamNameExists(String teamName, Integer UserId) {

        String hqlQuery;
        Query queryObj;
        if (UserId != 0) {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.teamName =:teamName AND tc.id !=:id";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("teamName", teamName);
            queryObj.setParameter("id", UserId);
        } else {
            hqlQuery = "SELECT tc.id as id FROM TblCustomer tc WHERE tc.teamName =:teamName";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("teamName", teamName);
        }

        TblCustomer customer = (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();
//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.add(Restrictions.eq("teamName", teamName));
//
//        if (UserId != 0) {
//            createCriteria.add(Restrictions.ne("id", UserId));
//        }
//
//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//
//        TblCustomer customer = (TblCustomer) createCriteria.uniqueResult();

        if (customer != null) {
            return true;
        } else {
            return false;
        }

    }

    @Transactional
    public int updateTeamName(BaseRequest baseRequest) {

        String teamName = baseRequest.getParam("team_name");

        String hqlQuery = "UPDATE TblCustomer SET teamName=:teamName, teamChange='Y'  WHERE id=:id AND teamChange='N'";

        Query query = getSession().createQuery(hqlQuery).setParameter("teamName", teamName).setParameter("id",
                baseRequest.authUserId);
        return query.executeUpdate();

    }

    @Transactional(readOnly = true)
    public int selectPanCard(String number, Integer UserId) {

        String hqlQuery = "SELECT tcp.id as id FROM TblCustomerPaincard tcp WHERE tcp.painNumber =:painNumber AND tcp.status !=:status";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("painNumber", number);
        queryObj.setParameter("status", "R");
        List list = queryObj.getResultList();
        return list == null ? 0 : list.size();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerPaincard.class);
//        createCriteria.add(Restrictions.eq("painNumber", number));
//        createCriteria.add(Restrictions.ne("status", "R"));
//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomerPaincard.class));
//        List list = createCriteria.list();
//        return list == null ? 0 : list.size();
    }

    @Transactional
    public void insertPanCard(BaseRequest baseRequest) {

        String[] fullPath = baseRequest.getParam("image").split("/");
        String image = fullPath[fullPath.length - 1];

        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        TblCustomerPaincard tblCustomerPaincard = TblCustomerPaincard.getInstance();
        tblCustomerPaincard.setCustomerId(baseRequest.authUserId);
        tblCustomerPaincard.setImage(image);
        tblCustomerPaincard.setPainNumber(baseRequest.getParam("number"));
        tblCustomerPaincard.setName(baseRequest.getParam("name"));
        tblCustomerPaincard.setDob(baseRequest.getParam("dob"));
        tblCustomerPaincard.setState(Integer.parseInt(baseRequest.getParam("state")));
        tblCustomerPaincard.setCreatedat(time);
        int pId = (int) getSession().save(tblCustomerPaincard);

        String hqlQuery = "UPDATE TblCustomer SET paincardId=:paincardId WHERE id=:id";
        Query query = getSession().createQuery(hqlQuery).setParameter("paincardId", pId).setParameter("id",
                baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional(readOnly = true)
    public int selectBankDetail(String accountNumber, Integer UserId) {

        String hqlQuery = "SELECT tcb.id as id FROM TblCustomerBankdetail tcb WHERE tcb.accountNumber =:accountNumber AND tcb.status !=:status";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("accountNumber", accountNumber);
        queryObj.setParameter("status", "R");
        List list = queryObj.getResultList();
        return list == null ? 0 : list.size();

//        Criteria createCriteria = getSession().createCriteria(TblCustomerBankdetail.class);
//        createCriteria.add(Restrictions.eq("accountNumber", accountNumber));
//        createCriteria.add(Restrictions.ne("status", "R"));
//        createCriteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"))
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomerBankdetail.class));
//        List list = createCriteria.list();
//        return list == null ? 0 : list.size();

    }

    @Transactional
    public void insertBankDetail(BaseRequest baseRequest) {
        String[] fullPath = baseRequest.getParam("image").split("/");
        String image = fullPath[fullPath.length - 1];

        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        TblCustomerBankdetail tblCustomerBankdetail = TblCustomerBankdetail.getInstance();
        tblCustomerBankdetail.setCustomerId(baseRequest.authUserId);
        tblCustomerBankdetail.setImage(image);
        tblCustomerBankdetail.setAccountNumber(baseRequest.getParam("account_number"));
        tblCustomerBankdetail.setName(baseRequest.getParam("name"));
        tblCustomerBankdetail.setIfsc(baseRequest.getParam("ifsc"));
        tblCustomerBankdetail.setCreatedat(time);
        int bId = (int) getSession().save(tblCustomerBankdetail);

        String hqlQuery = "UPDATE TblCustomer SET bankdetailId=:bankdetailId WHERE id=:id";
        Query query = getSession().createQuery(hqlQuery).setParameter("bankdetailId", bId).setParameter("id", baseRequest.authUserId);
        query.executeUpdate();

    }

    @Transactional(readOnly = true)
    public Object getCustomerProfile(BaseRequest baseRequest) {
        int customerId = Integer.parseInt(baseRequest.getParam("customer_id"));

        String hqlQuery = "SELECT "
                + "tc.teamName as teamName, tc.id as id, tc.followerCount as followerCount, tc.followingCount as followingCount, tc.postCount as postCount, tc.firstname as firstname, tc.lastname as lastname, tc.email as email, tc.image as image, tc.externalImage as externalImage,  tc.countryMobileCode as countryMobileCode, tc.phone as phone, tc.dob as dob, tc.addressline1 as addressline1, tc.addressline2 as addressline2, tc.pincode as pincode, tc.city as city_name, tc.created as created, "
                + "tcc.id as countryId, tcc.name as countryName, "
                + "tcs.id as stateId, tcs.name as stateName, "
                + "count(tcf.id) as isFollow, count(tcff.id) as isFollowing "
                + "FROM TblCustomer tc "
                + "LEFT JOIN TblCountry tcc ON tcc.id=tc.country "
                + "LEFT JOIN TblState tcs ON tcs.id=tc.state "
                + "LEFT JOIN TblFollow tcf ON (tcf.followerId=:authUserId AND tcf.followingId=:customerId) "
                + "LEFT JOIN TblFollow tcff ON (tcff.followerId=:customerId AND tcff.followingId=:authUserId) "
                + "WHERE " + "tc.id=:customerId";

        Query query = getSession().createQuery(hqlQuery).setParameter("authUserId", baseRequest.authUserId)
                .setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.size() != 1) {
            return "UNABLE_TO_PROCEED";
        }
        HashMap<String, Object> resultData = result.get(0);

        LinkedHashMap<String, Object> customerCountry = null;
        if (resultData.get("countryId") != null) {
            customerCountry = new LinkedHashMap<String, Object>();
            customerCountry.put("id", resultData.get("countryId"));
            customerCountry.put("name", resultData.get("countryName"));
        }

        LinkedHashMap<String, Object> customerState = null;

        if (resultData.get("stateId") != null) {
            customerState = new LinkedHashMap<String, Object>();
            customerState.put("id", resultData.get("stateId"));
            customerState.put("name", resultData.get("stateName"));
        }

        LinkedHashMap<String, Object> customerData = new LinkedHashMap<String, Object>();
        customerData.put("id", resultData.get("id"));
        customerData.put("created", resultData.get("created"));
        customerData.put("team_name", resultData.get("teamName"));
        customerData.put("is_follow", ((long) resultData.get("isFollow")) > 0 ? "Y" : "N");
        customerData.put("is_following", ((long) resultData.get("isFollowing")) > 0 ? "Y" : "N");
        customerData.put("firstname", resultData.get("firstname"));
        customerData.put("lastname", resultData.get("lastname"));
        customerData.put("email", resultData.get("email"));
        customerData.put("country_mobile_code", resultData.get("countryMobileCode"));
        String phoneNumber = (String) resultData.get("phone");
        if (!Util.isEmpty(phoneNumber)) {
            phoneNumber = phoneNumber.trim();
            if (phoneNumber.length() >= 10) {
                phoneNumber = phoneNumber.charAt(0) + "XXXXXXX" + phoneNumber.substring(phoneNumber.length() - 2);
            } else {
                phoneNumber = "XXXXXXXXXX";
            }
        }
        customerData.put("phone", phoneNumber);

        customerData.put("follower_count", resultData.get("followerCount"));
        customerData.put("following_count", resultData.get("followingCount"));
        customerData.put("post_count", resultData.get("postCount"));
        customerData.put("image", Util.generateImageUrl((String) resultData.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

        if (resultData.get("externalImage") != null && !Util.isEmpty(resultData.get("externalImage").toString())) {
            customerData.put("image", resultData.get("externalImage").toString());
        }

        customerData.put("country", customerCountry);
        customerData.put("state", customerState);
        customerData.put("playing_history", getPlayingHistory(customerId));
        customerData.put("series_leaderboard", getCustomerRecentSeriesLeaderboard(customerId));

        return customerData;

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getPlayingHistory(int customerId) {

        LinkedHashMap<String, Object> cricketPlayingHistory = getCricketPlayingHistory(customerId);
        LinkedHashMap<String, Object> soccerPlayingHistory = getSoccerPlayingHistory(customerId);

        LinkedHashMap<String, Object> playingData = new LinkedHashMap<String, Object>();

        long playingContests= (long) cricketPlayingHistory.get("contests") + (long) soccerPlayingHistory.get("contests");

        long playingMatches= (long) cricketPlayingHistory.get("matches") + (long) soccerPlayingHistory.get("matches");

        long playingSeries= (long) cricketPlayingHistory.get("series") + (long) soccerPlayingHistory.get("series");

        long winContests= (long) cricketPlayingHistory.get("wins") + (long) soccerPlayingHistory.get("wins") ;

        double totalWinAmount= (double) cricketPlayingHistory.get("win_amount") + (double) soccerPlayingHistory.get("win_amount");

        playingData.put("contests", playingContests);
        playingData.put("matches", playingMatches);
        playingData.put("series", playingSeries);
        playingData.put("wins", winContests);
        playingData.put("win_amount", totalWinAmount);
        playingData.put("league_created", 0);

        return playingData;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getCricketPlayingHistory(int customerId) {
        String hqlQuery = "SELECT " +
                "count(tccc.matchContestId) as playingContests, " +
                "count(DISTINCT tccc.matchUniqueId) as playingMatches, " +
                "count(DISTINCT tccc.seriesId) as playingSeries, " +
                "COALESCE(SUM(tccc.winAmount),0) as totalWinAmount, " +
                "COALESCE(SUM(CASE tccc.winAmount WHEN 0 THEN 0  ELSE 1 END),0) AS winContests " +
                "FROM " +
                "TblCricketCustomerContest tccc " +
                "WHERE " +
                "tccc.customerId=:customerId AND tccc.isAbondant='N'";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> hashMap = result.get(0);

        LinkedHashMap<String, Object> cricketPlayingData = new LinkedHashMap<String, Object>();

        cricketPlayingData.put("contests", hashMap.get("playingContests"));
        cricketPlayingData.put("matches", hashMap.get("playingMatches"));
        cricketPlayingData.put("series", hashMap.get("playingSeries"));
        cricketPlayingData.put("wins", hashMap.get("winContests"));
        cricketPlayingData.put("win_amount", hashMap.get("totalWinAmount"));
        cricketPlayingData.put("league_created", 0);

        return cricketPlayingData;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getSoccerPlayingHistory(int customerId) {
        String hqlQuery = "SELECT " +
                "count(tccc.matchContestId) as playingContests, " +
                "count(DISTINCT tccc.matchUniqueId) as playingMatches, " +
                "count(DISTINCT tccc.seriesId) as playingSeries, " +
                "COALESCE(SUM(tccc.winAmount),0) as totalWinAmount, " +
                "COALESCE(SUM(CASE tccc.winAmount WHEN 0 THEN 0  ELSE 1 END),0) AS winContests " +
                "FROM " +
                "TblSoccerCustomerContest tccc " +
                "WHERE " +
                "tccc.customerId=:customerId AND tccc.isAbondant='N'";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> hashMap = result.get(0);

        LinkedHashMap<String, Object> cricketPlayingData = new LinkedHashMap<String, Object>();

        cricketPlayingData.put("contests", hashMap.get("playingContests"));
        cricketPlayingData.put("matches", hashMap.get("playingMatches"));
        cricketPlayingData.put("series", hashMap.get("playingSeries"));
        cricketPlayingData.put("wins", hashMap.get("winContests"));
        cricketPlayingData.put("win_amount", hashMap.get("totalWinAmount"));
        cricketPlayingData.put("league_created", 0);

        return cricketPlayingData;
    }

    @Transactional(readOnly = true)
    public Collection<Object> getCustomerRecentSeriesLeaderboard(int customerId) {
        String hqlQuery = "SELECT " +
                "tcs.id as id, " +
                "tcs.name as name, " +
                "tcls.newPoint as newPoint, " +
                "tcls.newRank as newRank " +
                "FROM " +
                "TblCricketLeaderboardSeries tcls " +
                "LEFT JOIN TblCricketSeries tcs ON tcs.id=tcls.seriesId " +
                "WHERE " +
                "tcls.customerId=:customerId " +
                "ORDER BY tcls.updatedAt DESC";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);
        query.setMaxResults(4);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        LinkedHashMap<String, Object> recentSeriesData = new LinkedHashMap<String, Object>();

        for (HashMap<String, Object> hashMap2 : result) {

            LinkedHashMap<String, Object> seriesData = new LinkedHashMap<String, Object>();

            seriesData.put("id", hashMap2.get("id"));
            seriesData.put("name", hashMap2.get("name"));
            seriesData.put("new_point", hashMap2.get("newPoint"));
            seriesData.put("new_rank", hashMap2.get("newRank"));

            recentSeriesData.put(seriesData.get("id").toString(), seriesData);
        }

        return recentSeriesData.values();

    }

    @Transactional
    public LinkedHashMap<String, Object> createCustomerEnquiry(int customerId, String subject, String message) {
        Long currentTime = Util.getCurrentTime();

        String ticketNo = "#TICKET-" + GlobalConstant.APP_NAME + "-" + currentTime + customerId;

        TblCustomerQury tblCustomerQury = TblCustomerQury.getInstance();
        tblCustomerQury.setTicketId(ticketNo);
        tblCustomerQury.setCustomerId(customerId);
        tblCustomerQury.setSubject(subject);
        tblCustomerQury.setMessage(message);
        tblCustomerQury.setCreated(BigInteger.valueOf(currentTime));

        getSession().save(tblCustomerQury);

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ticket_id", ticketNo);

        return data;

    }


    @Transactional(readOnly = true)
    public TblCustomer getSocialCustomerDetail(String social_id, String email) {

        String hqlQuery;
        Query queryObj = null;
        if (!Util.isEmpty(social_id)) {
            hqlQuery = "SELECT tc.id as id," +
                    "tc.status as status," +
                    "tc.email as email," +
                    "tc.isEmailVerified as isEmailVerified," +
                    "tc.socialId as socialId " +
                    "FROM TblCustomer tc WHERE tc.socialId =:socialId AND tc.isDeleted =:isDeleted";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("socialId", social_id);
            queryObj.setParameter("isDeleted", "N");
        } else if (!Util.isEmpty(email)) {
            hqlQuery = "SELECT tc.id as id,tc.status as status,tc.email as email,tc.isEmailVerified as isEmailVerified,tc.socialId as socialId FROM TblCustomer tc WHERE tc.email =:email AND tc.isDeleted =:isDeleted";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("email", email);
            queryObj.setParameter("isDeleted", "N");
        }
        return (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        if (!Util.isEmpty(social_id)) {
//            createCriteria.add(Restrictions.eq("socialId", social_id));
//            createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        } else if (!Util.isEmpty(email)) {
//            createCriteria.add(Restrictions.eq("email", email));
//            createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        }
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("status"), "status");
//        projectionList.add(Projections.property("email"), "email");
//        projectionList.add(Projections.property("isEmailVerified"), "isEmailVerified");
//        projectionList.add(Projections.property("socialId"), "socialId");
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//        return (TblCustomer) createCriteria.uniqueResult();
    }

    @Transactional
    public void updateCustomerSocialDetail(int customerId, String socialType, String socialId, String email) {
        String isSocial = "Y";
        String isEmailVerified = "Y";
        Long currentTime = Util.getCurrentTime();

        String hqlQuery = "UPDATE TblCustomer SET socialType=:socialType, socialId=:socialId, email=:email, isSocial=:isSocial, isEmailVerified=:isEmailVerified, modified=:modified  WHERE id=:customerId";
        Query query = getSession().createQuery(hqlQuery).setParameter("socialType", socialType)
                .setParameter("socialId", socialId).setParameter("email", email).setParameter("isSocial", isSocial)
                .setParameter("isEmailVerified", isEmailVerified)
                .setParameter("modified", BigInteger.valueOf(currentTime)).setParameter("customerId", customerId);
        query.executeUpdate();
    }

    @Transactional
    public void updateCustomerSocialId(int customerId, String socialType, String socialId) {
        String isSocial = "Y";
        Long currentTime = Util.getCurrentTime();

        String hqlQuery = "UPDATE TblCustomer SET socialType=:socialType, socialId=:socialId, isSocial=:isSocial, modified=:modified WHERE id=:customerId";
        Query query = getSession().createQuery(hqlQuery).setParameter("socialType", socialType)
                .setParameter("socialId", socialId).setParameter("isSocial", isSocial)
                .setParameter("modified", BigInteger.valueOf(currentTime)).setParameter("customerId", customerId);
        query.executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getNotifications(Integer UserId, int page_no) {

        String hqlQuery = "SELECT title as title,notification as notification,image as image,senderType as sender_type,created as created FROM TblNotification tn WHERE isPromotional='0' AND FIND_IN_SET(:customerId,usersId)>0 ORDER BY created DESC";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", String.valueOf(UserId));

        if (page_no > 0) {
            int limit = 20;
            int offset = (page_no - 1) * limit;
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getAffiliateHistory(int customerId) {
        String hqlQuery = "SELECT " + "COALESCE(sum(tca.earning),0) as totalearning, " + "tc.isAffiliate as isAffiliate "
                + "FROM TblCustomer tc " + "LEFT JOIN TblCricketAffiliate tca on tc.id=tca.customerId " + "WHERE "
                + "tc.id=:customerId";
        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        HashMap<String, Object> hashMap = result.get(0);

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        String isAffiliate = (String) hashMap.get("isAffiliate");
        float totalEarning = 0f;
        float totalWithdraw = 0f;

        if (isAffiliate.equals("1")) {
            totalEarning = Float.parseFloat(String.valueOf(hashMap.get("totalearning")));

            if (totalEarning > 0) {
                hqlQuery = "SELECT " + "COALESCE(sum(tawr.amount),0) as totalwithdraw "
                        + "FROM TblAffiliatesWithdrawRequest tawr " + "WHERE " + "tawr.customerId=:customerId";
                query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);
                result = (List<HashMap<String, Object>>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                        .list();
                hashMap = result.get(0);
                totalWithdraw = Float.parseFloat(String.valueOf(hashMap.get("totalwithdraw")));
            }
        }

        float currentEarning = totalEarning - totalWithdraw;
        if (currentEarning < 0) {
            currentEarning = 0;
        }

        output.put("you_earned", totalEarning);
        output.put("pending_amount", totalWithdraw);
        output.put("current_amount", currentEarning);
        output.put("is_affiliate", isAffiliate);

        return output;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getAppCustomIcons() {
        String hqlQuery = "SELECT " + "tag as tag, name as name, image as image " + "FROM TblAppIconCustomize " + "WHERE "
                + "status = 'A'";

        Query query = getSession().createQuery(hqlQuery);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> customIcons = new LinkedHashMap<String, Object>();
        if (result.size() == 0) {
            return customIcons;
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> icon = new LinkedHashMap<String, Object>();
            icon.put("tag", hashMap.get("tag"));
            icon.put("name", hashMap.get("name"));
            icon.put("image", Util.generateImageUrl((String) hashMap.get("image"),
                    FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));

            customIcons.put(icon.get("tag").toString(), icon);

        }

        return customIcons;
    }

    @Transactional(readOnly = true)
    public Collection<Object> getQuotations() {
        String hqlQuery = "SELECT " + "gameId as gameId, width as width, height as height, link as link, image as image "
                + "FROM TblQuotation " + "WHERE " + "status = 'A'";

        Query query = getSession().createQuery(hqlQuery);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> quotations = new LinkedHashMap<String, Object>();
        if (result.size() == 0) {
            return quotations.values();
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> icon = new LinkedHashMap<String, Object>();
            icon.put("game_id", hashMap.get("gameId"));
            icon.put("width", hashMap.get("width"));
            icon.put("height", hashMap.get("height"));
            icon.put("link", hashMap.get("link"));
            icon.put("image", Util.generateImageUrl((String) hashMap.get("image"),
                    FileUploadConstant.QUOTATIONS_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));

            quotations.put(icon.get("game_id").toString(), icon);
        }

        return quotations.values();
    }


    @Transactional(readOnly = true)
    public Collection<Object> getGames() {
        Long currentTime = Util.getCurrentTime();

        String hqlQuery = "SELECT " + "tg.id as id, tg.name as name, tg.image as image, "
                + "tq.gameId as gameId, tq.width as width, tq.height as height, tq.image as q_image, tq.link as link " + "FROM TblGame tg " + "LEFT JOIN TblQuotation tq on (tq.gameId=tg.id AND tq.status='A' and tq.expiryDate>:currentTime) "
                + "WHERE " + "tg.status = 'A' AND tg.isDeleted='N' " + "ORDER BY tg.orderno ASC";

        Query query = getSession().createQuery(hqlQuery).setParameter("currentTime", BigInteger.valueOf(currentTime));

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> games = new LinkedHashMap<String, Object>();
        if (result.size() == 0) {
            return games.values();
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> quatation = null;

            if (hashMap.get("q_image") != null && !Util.isEmpty(hashMap.get("q_image").toString())) {
                quatation = new LinkedHashMap<String, Object>();

                quatation.put("game_id", hashMap.get("gameId"));
                quatation.put("width", hashMap.get("width"));
                quatation.put("height", hashMap.get("height"));
                quatation.put("link", hashMap.get("link"));
                quatation.put("image", Util.generateImageUrl((String) hashMap.get("q_image"),
                        FileUploadConstant.QUOTATIONS_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));

            }

            LinkedHashMap<String, Object> icon = new LinkedHashMap<String, Object>();
            icon.put("id", hashMap.get("id"));
            icon.put("name", hashMap.get("name"));
            icon.put("image", Util.generateImageUrl((String) hashMap.get("image"),
                    FileUploadConstant.GAME_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));

            icon.put("quotation", quatation);

            games.put(icon.get("id").toString(), icon);

        }

        return games.values();
    }


    @Transactional(readOnly = true)
    public Object checkAppVersionNew(int versionCode, String deviceType) {
        String hqlQuery = "SELECT "
                + "tav.versionName as version_name, tav.versionCode as version_code, tav.versionDesc as version_desc, tav.updateType as update_type "
                + "FROM TblAppVersion tav " + "WHERE "
                + "tav.versionCode > :versionCode AND tav.deviceType = :deviceType";

        Query query = getSession().createQuery(hqlQuery).setParameter("versionCode", versionCode)
                .setParameter("deviceType", deviceType);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return "APP_ALREADY_UPDATED";
        }

        String updatePageName = "app_versions_update";
        String hqlNew = "SELECT content as content FROM TblPageContent where pageName=:updatePageName";
        Query queryNew = getSession().createQuery(hqlNew).setParameter("updatePageName", updatePageName);
        List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) queryNew
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        String updateContent = "";
        if (list.size() > 0) {
            HashMap<String, Object> hashMap = list.get(0);
            updateContent = (String) hashMap.get("content");
        }

        HashMap<String, Object> data = result.get(0);

        if (!Util.isEmpty(updateContent)) {
            data.put("version_desc", updateContent);
        }

        data.put("title", "Update Is Available");
        data.put("app_link", GlobalConstant.APK_DOWNLOAD_URL);
        data.put("app_download_link", GlobalConstant.APKURL);

        if (data.get("update_type").toString().equals("F")) {
            return data;
        }

        hqlQuery = "SELECT " + "count(tavl.id) as data " + "FROM TblAppVersionsLog tavl " + "WHERE "
                + "tavl.versionCode > :versionCode AND tavl.deviceType = :deviceType and tavl.updateType = 'F'";

        query = getSession().createQuery(hqlQuery).setParameter("versionCode", versionCode).setParameter("deviceType",
                deviceType);

        result = (List<HashMap<String, Object>>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() > 0) {
            HashMap<String, Object> hashMap = result.get(0);
            if ((long) hashMap.get("data") > 0) {
                data.put("update_type", "F");
            }
        }

        return data;
    }

    @Transactional(readOnly = true)
    public List<TblReferralCashBonus> getReferCashbonus() {
        String hqlQuery = "SELECT trcb FROM TblReferralCashBonus trcb";
        Query queryObj = getSession().createQuery(hqlQuery);
        return queryObj.getResultList();
//        return getSession().createCriteria(TblReferralCashBonus.class).list();

    }


    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getCustomerProfileData(int customerId) {
        String hqlQuery = "SELECT "
                + "tc.teamName as teamName, tc.id as id, tc.firstname as firstname, tc.lastname as lastname, tc.email as email,  tc.countryMobileCode as countryMobileCode, tc.phone as phone, tc.isAdmin as isAdmin, tc.isFake as isFake "
                + "FROM TblCustomer tc " + "WHERE " + "tc.id=:customerId";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> resultData = result.get(0);

        LinkedHashMap<String, Object> customerData = new LinkedHashMap<String, Object>();
        customerData.put("id", resultData.get("id"));
        customerData.put("team_name", resultData.get("teamName"));
        customerData.put("firstname", resultData.get("firstname"));
        customerData.put("lastname", resultData.get("lastname"));
        customerData.put("email", resultData.get("email"));
        customerData.put("country_mobile_code", resultData.get("countryMobileCode"));
        customerData.put("phone", resultData.get("phone"));
        customerData.put("isAdmin", resultData.get("isAdmin"));
        customerData.put("isFake", resultData.get("isFake"));

        return customerData;
    }


    //****************************************************************************************************************************************************************************************************

    //
    @Transactional(readOnly = true)
    public TblCustomer getCustomerByIdForAuth(int CustomerId, String deviceId) {

        String hqlQuery = "SELECT tc.id as id," +
                "tc.status as status," +
                "tc.isDeleted as isDeleted," +
                "tcl.id as customerLoginId " +
                "FROM TblCustomer tc "+
                "LEFT JOIN TblCustomerLogin tcl ON tcl.customerId=tc.id AND tcl.deviceId=:deviceId "+
                "WHERE tc.id =:id AND tc.isDeleted =:isDeleted";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("id", CustomerId);
        queryObj.setCacheable(true);
        queryObj.setParameter("deviceId", deviceId);
        queryObj.setParameter("isDeleted", "N");
        return (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblCustomer.class);
//        createCriteria.setCacheable(true);
//        createCriteria.add(Restrictions.eq("id", CustomerId));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("status"), "status");
//        projectionList.add(Projections.property("isDeleted"), "isDeleted");
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblCustomer.class));
//        return (TblCustomer) createCriteria.uniqueResult();

    }

    @Transactional
    public void insertPaymentLog(Integer userId, String oRDER_ID, String logRefrenceId, String logStatus,
                                 float tXN_AMOUNT, String logPaymentGateway, String logRequestJson, String logResponseJson) {
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
        TblCustomerWalletPaymentLog tblCustomerWalletPaymentLog = new TblCustomerWalletPaymentLog();
        tblCustomerWalletPaymentLog.setCustomerId(userId);
        tblCustomerWalletPaymentLog.setOrderId(oRDER_ID);
        tblCustomerWalletPaymentLog.setRefrenceId(logRefrenceId);
        tblCustomerWalletPaymentLog.setStatus(logStatus);
        tblCustomerWalletPaymentLog.setAmount(tXN_AMOUNT);
        tblCustomerWalletPaymentLog.setPaymentGateway(logPaymentGateway);
        tblCustomerWalletPaymentLog.setRequestJson(logRequestJson);
        tblCustomerWalletPaymentLog.setResponseJson(logResponseJson);
        tblCustomerWalletPaymentLog.setCreated(time);
        tblCustomerWalletPaymentLog.setUpdated(time);
        getSession().save(tblCustomerWalletPaymentLog);

    }

    @Transactional(readOnly = true)
    public int checkTransactionIdAlreadyExist(int customerId, String trnId) {
        String hqlQuery = "SELECT " + "tcwh.id as id " + "FROM TblCustomerWalletHistory tcwh " + "WHERE "
                + "tcwh.customerId = :customerId AND tcwh.refrenceId = :trnId";

        Query query = getSession().createQuery(hqlQuery).setParameter("customerId", customerId).setParameter("trnId",
                trnId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result.size();
    }

    @Transactional
    public void updateCachBonusForCustomer(int customerId, int newrcbId, float amount, float cachBonus,
                                           float cashBack) {

        Session session = getSession();

        String hqlQuery1 = "SELECT " + "trcbc.id as id " + "FROM TblRechargeCachBonusCustomer trcbc " + "WHERE "
                + "trcbc.customerId = :customerId AND trcbc.rcbId=:rcbId";

        Query query1 = session.createQuery(hqlQuery1).setParameter("customerId", customerId).setParameter("rcbId", newrcbId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result != null && result.size() > 0) {

            HashMap<String, Object> hashMap = result.get(0);

            int rowId = (int) hashMap.get("id");

            int usedCount = 1;

            String hqlQuery = "UPDATE TblRechargeCachBonusCustomer SET amount=amount+:amount, cachBonus=cachBonus+:cachBonus, cashBack=cashBack+:cashBack, usedCount=usedCount+:usedCount WHERE id=:id";
            Query query = session.createQuery(hqlQuery).setParameter("amount", amount).setParameter("cachBonus", cachBonus).setParameter("cashBack", cashBack).setParameter("usedCount", usedCount).setParameter("id", rowId);
            query.executeUpdate();
        } else {
            TblRechargeCachBonusCustomer instance = TblRechargeCachBonusCustomer.getInstance();
            instance.setCustomerId(customerId);
            instance.setRcbId(newrcbId);
            instance.setAmount(amount);
            instance.setCachBonus(cachBonus);
            instance.setCashBack(cashBack);
            instance.setUsedCount(1);

            session.save(instance);
        }

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getCouponList(Integer authUserId, int page_no) {
        Long currentTime = Util.getCurrentTime();
        Session session = getSession();

        String hqlQuery1 = "SELECT "
                + "tc.title as title, tc.couponCode as coupon_code, tc.description as description, tc.startDate as start_date, tc.endDate as end_date, tc.deposite as deposite, tc.bonus as bonus, tc.createdAt as created, tc.image as image "
                + "FROM TblCoupon tc " + "WHERE "
                + "FIND_IN_SET(:customerId,tc.customerIds)>0 AND tc.isDeleted='N' AND tc.status='A' AND tc.startDate<=:time AND tc.endDate>=:time AND tc.id NOT IN(SELECT tcc.couponId FROM TblCustomerCoupon tcc WHERE tcc.couponId=tc.id and tcc.customerId=:customerId1) "
                + "ORDER BY tc.createdAt DESC";

        Query query1 = session.createQuery(hqlQuery1).setParameter("customerId", String.valueOf(authUserId)).setParameter("customerId1", authUserId).setParameter("time", BigInteger.valueOf(currentTime));

        if (page_no > 0) {
            int limit = 20;
            int offset = (page_no - 1) * limit;
            query1.setFirstResult(offset);
            query1.setMaxResults(limit);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getCouponByCode(String couponCode) {
        Session session = getSession();

        String hqlQuery1 = "SELECT "
                + "tc.title as title, tc.couponCode as coupon_code, tc.description as description, tc.startDate as start_date, tc.endDate as end_date, tc.deposite as deposite, tc.bonus as bonus, tc.createdAt as created, tc.image as image, tc.customerIds as customer_ids, tc.id as id "
                + "FROM TblCoupon tc "
                + "WHERE "
                + " tc.isDeleted='N' AND tc.status='A' AND tc.couponCode=:couponCode";

        Query query1 = session.createQuery(hqlQuery1).setParameter("couponCode", couponCode);


        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result == null || result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    @Transactional(readOnly = true)
    public int getCustomerUseCouponCount(int customerId, int couponId) {
        Session session = getSession();

        String hqlQuery1 = "SELECT tcc.couponId FROM TblCustomerCoupon tcc WHERE tcc.couponId=:couponId and tcc.customerId=:customerId";

        Query query1 = session.createQuery(hqlQuery1).setParameter("couponId", couponId)
                .setParameter("customerId", customerId);


        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result == null || result.size() == 0) {
            return 0;
        }
        return result.size();
    }

    @Transactional
    public boolean updateCustomerCouponRedeem(Integer authUserId, int couponId, float depositeAmount, float bonusAmount, String coupon) {

        boolean isAllSuccess = false;
        Long time = Util.getCurrentTime();

        Session newSession = getNewSession();
        Transaction tx = null;
        try {

            tx = newSession.beginTransaction();

            TblCustomerCoupon tblCustomerCoupon = TblCustomerCoupon.getInstance();
            tblCustomerCoupon.setCouponId(couponId);
            tblCustomerCoupon.setCustomerId(authUserId);
            tblCustomerCoupon.setCreatedAt(BigInteger.valueOf(time));

            newSession.save(tblCustomerCoupon);

            String TransactionType = "CREDIT";

            if (depositeAmount > 0) {
                String newDescription = authUserId + " Redeem coupon " + coupon;
                String newTransactionId = "COUPONWALL" + time;
                String newWalletType = "deposit_wallet";
                String newWalletName = GlobalConstant.WALLET_TYPE.get(newWalletType);

                Float newPreviousAmount = getCustomerWalletAmount(authUserId, newWalletType);
                Float newCurrentAmount = newPreviousAmount + depositeAmount;
                updateWallet(newSession, authUserId, depositeAmount, newWalletType, TransactionType);

                TblCustomerWalletHistory instance2 = TblCustomerWalletHistory.getInstance();
                instance2.setCustomerId(authUserId);
                instance2.setWalletType(newWalletName);
                instance2.setPreviousAmount(newPreviousAmount);
                instance2.setAmount(depositeAmount);
                instance2.setCurrentAmount(newCurrentAmount);
                instance2.setTransactionType(TransactionType);
                instance2.setType("CUSTOMER_REDEEM_COUPON");
                instance2.setTransactionId(newTransactionId);
                instance2.setDescription(newDescription);
                instance2.setRcbId(0);
                instance2.setRefCwhId(0);
                instance2.setStatus("S");
                instance2.setSportId(-1);
                instance2.setCreated(BigInteger.valueOf(time));

                newSession.save(instance2);
            }

            if (bonusAmount > 0) {
                String newDescription = authUserId + " Redeem coupon " + coupon;
                String newTransactionId = "COUPONWALL" + time;
                String newWalletType = "bonus_wallet";
                String newWalletName = GlobalConstant.WALLET_TYPE.get(newWalletType);

                Float newPreviousAmount = getCustomerWalletAmount(authUserId, newWalletType);
                Float newCurrentAmount = newPreviousAmount + bonusAmount;
                updateWallet(newSession, authUserId, bonusAmount, newWalletType, TransactionType);

                TblCustomerWalletHistory instance2 = TblCustomerWalletHistory.getInstance();
                instance2.setCustomerId(authUserId);
                instance2.setWalletType(newWalletName);
                instance2.setPreviousAmount(newPreviousAmount);
                instance2.setAmount(bonusAmount);
                instance2.setCurrentAmount(newCurrentAmount);
                instance2.setTransactionType(TransactionType);
                instance2.setType("CUSTOMER_REDEEM_COUPON");
                instance2.setTransactionId(newTransactionId);
                instance2.setDescription(newDescription);
                instance2.setRcbId(0);
                instance2.setRefCwhId(0);
                instance2.setStatus("S");
                instance2.setSportId(-1);
                instance2.setCreated(BigInteger.valueOf(time));

                newSession.save(instance2);
            }
            tx.commit();
            isAllSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }
        return isAllSuccess;
    }

    @Transactional(readOnly = true)
    public TblCustomer getCustomerById(int CustomerId, String mobile, String email) {
        String whereCondition = "";
        if (CustomerId > 0) {
            whereCondition = "tc.id =:id";
        } else if (!Util.isEmpty(mobile)) {
            whereCondition = "tc.email =:id";
        } else if (!Util.isEmpty(email)) {
            whereCondition = "tc.phone =:id";
        }

        String hqlQuery = "SELECT " +
                "tc.id as id," +
                "tc.firstname as firstname," +
                "tc.lastname as lastname," +
                "tc.email as email," +
                "tc.phone as phone," +
                "tc.paytmphone as paytmphone," +
                "tc.countryMobileCode as countryMobileCode," +
                "tc.dob as dob," +
                "tc.country as country," +
                "tc.state as state," +
                "tc.city as city," +
                "tc.addressline1 as addressline1," +
                "tc.addressline2 as addressline2," +
                "tc.pincode as pincode, " +
                "tc.dynamicLink as dynamicLink, " +
                "tc.teamName as teamName," +
                "tc.paincardId as paincardId," +
                "tc.bankdetailId as bankdetailId, " +
                "tc.image as image, " +
                "tc.externalImage as externalImage, " +
                "tc.isEmailVerified as isEmailVerified, " +
                "tc.isPhoneVerified as isPhoneVerified " +
                "FROM TblCustomer tc WHERE " + whereCondition;
        Query queryObj = getSession().createQuery(hqlQuery);
        if (CustomerId > 0) {
            queryObj.setParameter("id", CustomerId);
        } else if (!Util.isEmpty(mobile)) {
            queryObj.setParameter("id", mobile);
        } else if (!Util.isEmpty(email)) {
            queryObj.setParameter("id", email);
        }
        queryObj.setCacheable(true);
        return (TblCustomer) queryObj.setResultTransformer(Transformers.aliasToBean(TblCustomer.class)).uniqueResult();
    }

    @Transactional
    public void saveCustomerUpdateLogs(int customerId, String mobile, String email, TblCustomer customerPreviousDetail, String action) {
        TblCustomer customerNewDetail = getCustomerById(customerId, mobile, email);
        String previousData = new Gson().toJson(customerPreviousDetail);
        String newData = new Gson().toJson(customerNewDetail);
        TblCustomerUpdateLogs tblCustomerUpdateLogs = new TblCustomerUpdateLogs();
        tblCustomerUpdateLogs.setCustomerId(customerId);
        tblCustomerUpdateLogs.setAction(action);
        tblCustomerUpdateLogs.setPreviousData(previousData);
        tblCustomerUpdateLogs.setNewData(newData);
        getSession().save(tblCustomerUpdateLogs);
    }

    @Transactional(readOnly = true)
    public BaseResponse walletRechargePromocodes(int authUserId) {
        Long currentTime = Util.getCurrentTime();
        String hqlQuery = "SELECT " +
                "trcb.id as id, " +
                "trcb.recharge as recharge, " +
                "trcb.cachBonus as cach_bonus, " +
                "trcb.isUse as is_use, " +
                "trcb.isUseMax as is_use_max, " +
                "trcb.maxRecharge as max_recharge, " +
                "trcb.cashBonusType as cash_bonus_type, " +
                "trcb.code as code, " +
                "trcb.startDate as start_date, " +
                "trcb.endDate as end_date, " +
                "trcbc.usedCount as already_use " +
                "FROM " +
                "TblRechargeCachBonus trcb " +
                "LEFT JOIN TblRechargeCachBonusCustomer trcbc ON trcbc.rcbId = trcb.id AND trcbc.customerId =:customerId " +
                "WHERE  " +
                "trcb.status='A' AND trcb.isDeleted='N' AND trcb.endDate >:endDate  " +
                "ORDER BY trcb.createdAt DESC";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("customerId", authUserId);
        queryObj.setParameter("endDate", BigInteger.valueOf(currentTime));
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryObj.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result == null || result.size() == 0) {
            new BaseResponse(0, false, "No promocodes.", new ArrayList<>());
        }
        return new BaseResponse(0, false, "Promocode list.", result);
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getWithdrawSlab() {
        String hqlQuery = "SELECT tws.min as min,tws.max as max,tws.charges as charges FROM TblWithdrawSlab tws WHERE tws.isDeleted ='N' ORDER BY tws.min ASC";
        Query query = getSession().createQuery(hqlQuery);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getWithdrawSlabCharges(float amount) {
        String hqlQuery = "SELECT tws.min as min,tws.max as max,tws.charges as charges FROM TblWithdrawSlab tws WHERE tws.isDeleted ='N' AND tws.min <= :amount AND tws.max >= :amount";
        Query query = getSession().createQuery(hqlQuery).setParameter("amount", amount);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result == null || result.size() == 0) {
            return null;
        }
        return result.get(0);
    }
}
