package com.choic11.repository;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.TblAffiliateLeaderboard;
import com.choic11.model.TblAffiliateLeaderboardResult;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.*;

@Repository
public class AffiliateLeaderboardRepository {

    @Autowired
    private SessionFactory factory;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

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

    @Transactional
    public void updateAffiliateLeaderboardRank(int leaderboardId) {
        Session newSession = getNewSession();
        Transaction tx = null;
        tx = newSession.beginTransaction();
        try {
            Query query = newSession.createQuery("SELECT tal FROM TblAffiliateLeaderboard tal WHERE tal.id =:id");
            query.setParameter("id", leaderboardId);
            TblAffiliateLeaderboard tblAffiliateLeaderboard = (TblAffiliateLeaderboard) query.uniqueResult();
            long startDate = tblAffiliateLeaderboard.getStartDate().longValue();
            long endDate = tblAffiliateLeaderboard.getEndDate().longValue();

            String hqln;
            if (tblAffiliateLeaderboard.getCriteria().equals("DEPOSIT")) {
                hqln = "CALL get_affiliate_leaderboard_deposit_amount(:start_date,:end_date,:start_count,:page_size)";
            } else {
                hqln = "CALL get_affiliate_leaderboard_paid_amount(:start_date,:end_date,:start_count,:page_size)";
            }
            Query queryy = newSession.createSQLQuery(hqln);
            queryy.setParameter("start_date", startDate);
            queryy.setParameter("end_date", endDate);
            queryy.setParameter("start_count", 0);
            queryy.setParameter("page_size", -1);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            LinkedHashMap<Integer, Object> finalData = new LinkedHashMap<>();

            for (HashMap<String, Object> hashMap : result) {
                HashMap<String, Object> object = new HashMap<String, Object>();
                int customerId = (Integer) hashMap.get("customer_id");
                String customerName = (String) hashMap.get("customer_name");
                float totalAmount = ((Double) hashMap.get("total_amount")).floatValue();
                object.put("customerId", customerId);
                object.put("customerName", customerName);
                object.put("newAmount", totalAmount);
                finalData.put(customerId, object);
            }

            Query query1 = newSession.createQuery("SELECT talr.id as id,talr.customerId as customerId,talr.newAmount as newAmount,talr.oldAmount as oldAmount,talr.newRank as newRank,talr.oldRank as oldRank FROM TblAffiliateLeaderboardResult talr WHERE talr.leaderboardId = " + leaderboardId + "");
            List<HashMap<String, Object>> resultSeries = (List<HashMap<String, Object>>) query1.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();


            List<JSONObject> customerData = new ArrayList<JSONObject>();
            for (HashMap<String, Object> data : resultSeries) {
                int customerId = ((BigInteger) data.get("customerId")).intValue();
                float newAmount = (float) data.get("newAmount");
                if (finalData.containsKey(customerId)) {
                    HashMap<String, Object> object = (HashMap<String, Object>) finalData.get(customerId);

                    data.put("oldNewAmount", newAmount);
                    data.put("newAmount", (float) object.get("newAmount"));
                    customerData.add(new JSONObject(data));
                    finalData.remove(customerId);
                } else {
                    data.put("oldNewAmount", newAmount);
                    data.put("newAmount", newAmount);
                    customerData.add(new JSONObject(data));
                }
            }

            if (!finalData.isEmpty()) {
                Set<Integer> keySet = finalData.keySet();

                for (Integer integer : keySet) {
                    customerData.add(new JSONObject((HashMap<String, Object>) finalData.get(integer)));
                }
            }

            Collections.sort(customerData, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject a, JSONObject b) {

                    float valA = a.getFloat("newAmount");
                    float valB = b.getFloat("newAmount");

                    if (valA == valB) {
                        return 0;
                    } else if (valA < valB) {
                        return 1;
                    } else {
                        return -1;
                    }

                }
            });

            String paramsSeries = "";
            int newRankSeries = 1;
            int newRank = 1;
            Long currentTime = Util.getCurrentTime();
            for (JSONObject jsonObject : customerData) {

                int customerId = jsonObject.getInt("customerId");
                float newAmount = jsonObject.getFloat("newAmount");

                if (!jsonObject.has("id")) {
                    if (!paramsSeries.isEmpty()) {
                        paramsSeries += ",";
                    }
                    paramsSeries += "(" + leaderboardId + "," + customerId + "," + newAmount + "," + newRankSeries + ","
                            + newRankSeries + "," + newAmount + "," + newAmount + "," + currentTime + "," + currentTime
                            + ")";
                } else {
                    int rowId = jsonObject.getInt("id");
                    int previousOldRank = jsonObject.getInt("oldRank");
                    int previousNewRank = jsonObject.getInt("newRank");
                    float previousOldAmount = jsonObject.getFloat("oldAmount");
                    float previousNewAmount = jsonObject.getFloat("oldNewAmount");
                    float newAmounts = jsonObject.getFloat("newAmount");

                    int oldRank = previousOldRank;
                    if (previousOldRank == 0) {
                        oldRank = newRank;
                    } else if (previousNewRank != newRankSeries) {
                        oldRank = previousNewRank;
                    }

                    float oldAmount = previousOldAmount;
                    if (previousOldAmount == 0) {
                        oldAmount = newAmounts;
                    } else if (previousNewAmount != newAmounts) {
                        oldAmount = previousNewAmount;
                    }

                    String hqlll = "UPDATE " + "TblAffiliateLeaderboardResult "
                            + "SET oldRank=:oldRank, newRank=:newRank, oldAmount=:oldAmount, newAmount=:newAmount, updatedAt=:updatedAt  "
                            + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll).setParameter("oldRank", oldRank)
                            .setParameter("newRank", newRankSeries).setParameter("oldAmount", oldAmount)
                            .setParameter("newAmount", newAmounts)
                            .setParameter("updatedAt", BigInteger.valueOf(currentTime)).setParameter("id", rowId);
                    queryyyy.executeUpdate();
                }
                newRankSeries++;
                newRank++;
            }
            if (!paramsSeries.isEmpty()) {
                String mysqlQuery1 = "INSERT " + "INTO tbl_affiliate_leaderboard_result "
                        + "(leaderboard_id,customer_id,total_amount,old_rank,new_rank,old_amount,new_amount,created_at,updated_at)"
                        + " VALUES " + paramsSeries;

                newSession.createSQLQuery(mysqlQuery1).executeUpdate();
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

    }

    @Transactional(readOnly = true)
    public List<LinkedHashMap<String, Object>> getAffiliateLeaderboard(BaseRequest baseRequest) {

        int leaderboardId = Integer.parseInt(baseRequest.getParam("leaderboard_id"));
        int startCount = Integer.parseInt(baseRequest.getParam("start_count"));
        int pageSize = Integer.parseInt(baseRequest.getParam("page_size"));

        String hqln = "CALL get_affiliate_leaderboard_data(:leaderboardId,:start_count,:page_size)";
        Query queryy = getSession().createSQLQuery(hqln);
        queryy.setParameter("leaderboardId", leaderboardId);
        queryy.setParameter("start_count", startCount);
        queryy.setParameter("page_size", pageSize);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<LinkedHashMap<String, Object>> finalData = new ArrayList<>();

        if (result.isEmpty()) {
            return finalData;
        }
        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> object = new LinkedHashMap<String, Object>();
            int customerId = (int) hashMap.get("customerId");
            String customerName = (String) hashMap.get("customerName");
            String customerTeamName = (String) hashMap.get("customerTeamName");
            String image = (String) hashMap.get("image");
            String externalImage = (String) hashMap.get("externalImage");
            int newRank = (int) hashMap.get("newRank");
            int oldRank = (int) hashMap.get("oldRank");
            float newAmount = (float) hashMap.get("newAmount");
            float oldAmount = (float) hashMap.get("oldAmount");
            float winAmount = (float) hashMap.get("winAmount");
            String winGadget = (String) hashMap.get("winGadget");
            String referralCode = (String) hashMap.get("referralCode");

            object.put("customerId", customerId);
            object.put("customerName", customerName);
            object.put("customerTeamName", customerTeamName);
            object.put("image", Util.generateImageUrl(image, FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
            if (!Util.isEmpty(externalImage)) {
                object.put("image", externalImage);
            }
            object.put("newRank", newRank);
            object.put("oldRank", oldRank);
            object.put("newAmount", newAmount);
            object.put("oldAmount", oldAmount);
            object.put("winAmount", winAmount);
            object.put("winGadget", winGadget);
            object.put("referralCode", referralCode);
            finalData.add(object);
        }
        return finalData;
    }

    @Transactional(readOnly = false)
    public void getResultForAffiliateLeaderboard(BaseRequest baseRequest) throws Exception {
        Session newSession = getNewSession();
        Transaction tx = null;
        tx = newSession.beginTransaction();
        List<LinkedHashMap<String, Object>> finalData = new ArrayList<>();
        try {
            Long currentTime = Util.getCurrentTime();
            // get Leaderboard by id
            int leaderboardId = Integer.parseInt(baseRequest.getParam("leaderboard_id"));
            Query query = newSession.createQuery("SELECT tal FROM TblAffiliateLeaderboard tal WHERE tal.id =:id");
            query.setParameter("id", leaderboardId);
            TblAffiliateLeaderboard tblAffiliateLeaderboard = (TblAffiliateLeaderboard) query.uniqueResult();

            // check endDate
            if ((tblAffiliateLeaderboard.getEndDate().longValue() * 1000) > new Date().getTime()) {
                throw new Exception("Result not declared before endDate");
            }

            // update leaderboard affiliate table ResultDeclared = "R"
            if (tblAffiliateLeaderboard.getResultDeclared().equals("N")) {
                String hqlObj1 = "UPDATE TblAffiliateLeaderboard tal SET " +
                        "tal.resultDeclared =:resultDeclared WHERE tal.id =:id";
                Query queryy = newSession.createQuery(hqlObj1);
                queryy.setParameter("resultDeclared", "R");
                queryy.setParameter("id", leaderboardId);
                queryy.executeUpdate();
            }

            long startDate = tblAffiliateLeaderboard.getStartDate().longValue();
            long endDate = tblAffiliateLeaderboard.getEndDate().longValue();
            int startCount = Integer.parseInt(baseRequest.getParam("start_count"));
            int pageSize = Integer.parseInt(baseRequest.getParam("page_size"));

            // get affiliate leaderboard deposit data
            String hqln;
            if (tblAffiliateLeaderboard.getCriteria().equals("DEPOSIT")) {
                hqln = "CALL get_affiliate_leaderboard_deposit_amount(:start_date,:end_date,:start_count,:page_size)";
            } else {
                hqln = "CALL get_affiliate_leaderboard_paid_amount(:start_date,:end_date,:start_count,:page_size)";
            }
            Query queryy = newSession.createSQLQuery(hqln);
            queryy.setParameter("start_date", startDate);
            queryy.setParameter("end_date", endDate);
            queryy.setParameter("start_count", startCount);
            queryy.setParameter("page_size", pageSize);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();


            JSONObject prizeBrackup = new JSONObject(tblAffiliateLeaderboard.getPriceJson());
            JSONArray minRanks = prizeBrackup.getJSONArray("per_min_p");
            JSONArray maxRanks = prizeBrackup.getJSONArray("per_max_p");
            JSONArray rankPrice = prizeBrackup.getJSONArray("per_price");
            JSONArray totalGadget = prizeBrackup.has("gadget") ? prizeBrackup.getJSONArray("gadget") : null;
            int lastPosition = Integer.parseInt(maxRanks.get(maxRanks.length() - 1).toString());

            LinkedHashMap<Integer, Object> blankArray = new LinkedHashMap<Integer, Object>();
            HashMap<Integer, Object> customersData = new HashMap<Integer, Object>();
            int currentTotalWinners = 0;

            for (HashMap<String, Object> hashMap : result) {
                LinkedHashMap<String, Object> object = new LinkedHashMap<String, Object>();
                int customerId = (int) hashMap.get("customer_id");
                String customerName = (String) hashMap.get("customer_name");
                String customerEmail = (String) hashMap.get("customer_email");
                String customerPhone = (String) hashMap.get("customer_phone");
                double totalAmount = (double) hashMap.get("total_amount");

                object.put("customer_id", customerId);
                object.put("customer_name", customerName);
                object.put("customer_email", customerEmail);
                object.put("customer_phone", customerPhone);
                object.put("total_amount", totalAmount);
                finalData.add(object);


                int newRank = 1;
                int newRankForAmount = newRank;

                if (currentTotalWinners >= lastPosition) {
                    Object[] array = blankArray.keySet().toArray();
                    int blankArrayLastRank = (int) array[array.length - 1];
                    if (newRankForAmount > blankArrayLastRank) {
                        break;
                    }
                }

                if (blankArray.containsKey(newRank)) {
                    LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) blankArray
                            .get(newRank);
                    newRankForAmount = newRank + (int) rankData.get("customerCount");
                }

                float amount = 0;
                String gadget = "";
                for (int i = 0; i < maxRanks.length(); i++) {
                    int min = Integer.parseInt(minRanks.get(i).toString());
                    int max = Integer.parseInt(maxRanks.get(i).toString());
                    String gadgett = "";
                    if (totalGadget != null) {
                        gadgett = totalGadget.get(i).toString();
                    }

                    float amountt = Float.parseFloat(rankPrice.get(i).toString());
                    if (newRankForAmount >= min && newRankForAmount <= max) {
                        amount = amountt;
                        gadget = gadgett;
                        break;
                    }
                }

                if (blankArray.containsKey(newRank)) {
                    LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) blankArray
                            .get(newRank);
                    List<Float> amounts = (List<Float>) rankData.get("amounts");
                    List<String> gadgets = (List<String>) rankData.get("gadgets");
                    List<Integer> customerIds = (List<Integer>) rankData.get("customerIds");

                    amounts.add(amount);
                    if (!Util.isEmpty(gadget)) {
                        gadgets.add(gadget);
                    }
                    customerIds.add(customerId);

                    rankData.put("customerCount", customerIds.size());

                    if (!customersData.containsKey(customerId)) {
                        HashMap<String, Object> customerData = new HashMap<String, Object>();
                        customerData.put("name", customerName);
                        customerData.put("email", customerEmail);
                        customerData.put("phone", customerPhone);
                        customersData.put(customerId, customerData);
                    }

                } else {
                    LinkedHashMap<String, Object> rankData = new LinkedHashMap<String, Object>();
                    List<Float> amounts = new ArrayList<Float>();
                    List<String> gadgets = new ArrayList<String>();
                    List<Integer> customerIds = new ArrayList<Integer>();

                    amounts.add(amount);
                    if (!Util.isEmpty(gadget)) {
                        gadgets.add(gadget);
                    }
                    customerIds.add(customerId);

                    rankData.put("customerCount", customerIds.size());
                    rankData.put("amounts", amounts);
                    rankData.put("gadgets", gadgets);
                    rankData.put("customerIds", customerIds);

                    blankArray.put(newRank, rankData);

                    if (!customersData.containsKey(customerId)) {
                        HashMap<String, Object> customerData = new HashMap<String, Object>();
                        customerData.put("name", customerName);
                        customerData.put("email", customerEmail);
                        customerData.put("phone", customerPhone);
                        customersData.put(customerId, customerData);
                    }
                }


                // insert data in result table if result not declared
                if (tblAffiliateLeaderboard.getResultDeclared().equals("N")) {
                    Query query1 = newSession.createQuery("SELECT talr FROM TblAffiliateLeaderboardResult talr WHERE talr.leaderboardId = " + leaderboardId + " AND talr.customerId = " + customerId + "");
                    TblAffiliateLeaderboardResult tblAffiliateLeaderboardResult = (TblAffiliateLeaderboardResult) query1.uniqueResult();
                    if (tblAffiliateLeaderboardResult != null) {
                        String hqlll = "UPDATE " + "TblAffiliateLeaderboardResult "
                                + "SET winAmount=:winAmount, winGadget=:winGadget, updatedAt=:updatedAt  "
                                + "WHERE " + "id=:id";
                        Query queryyyy = newSession.createQuery(hqlll);
                        queryyyy.setParameter("winAmount", amount);
                        queryyyy.setParameter("winGadget", gadget);
                        queryyyy.setParameter("updatedAt", BigInteger.valueOf(currentTime));
                        queryyyy.setParameter("id", tblAffiliateLeaderboardResult.getId());
                        queryyyy.executeUpdate();
                    } else {
                        if (gadget.isEmpty()) {
                            String mysqlQuery1 = "INSERT " + "INTO tbl_affiliate_leaderboard_result "
                                    + "(win_amount,created_at,updated_at)"
                                    + " VALUES " + "(" + amount + "," + currentTime + "," + currentTime + ")";
                            newSession.createSQLQuery(mysqlQuery1).executeUpdate();
                        } else {
                            String mysqlQuery1 = "INSERT " + "INTO tbl_affiliate_leaderboard_result "
                                    + "(win_amount,win_gadget,created_at,updated_at)"
                                    + " VALUES " + "(" + amount + ",'" + gadget + "'," + currentTime + "," + currentTime + ")";
                            newSession.createSQLQuery(mysqlQuery1).executeUpdate();
                        }
                    }
                }
            }

            // update leaderboard affiliate table ResultDeclared = "Y"
            if (tblAffiliateLeaderboard.getResultDeclared().equals("R")) {
                String hqlObj1 = "UPDATE TblAffiliateLeaderboard tal SET " +
                        "tal.resultDeclared =:resultDeclared WHERE tal.id =:id";
                queryy = newSession.createQuery(hqlObj1);
                queryy.setParameter("resultDeclared", "Y");
                queryy.setParameter("id", leaderboardId);
                queryy.executeUpdate();
            }
            tx.commit();
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
