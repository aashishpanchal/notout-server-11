package com.choic11.repository.basketball;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.TblCountry;
import com.choic11.model.TblTemplate;
import com.choic11.model.basketball.*;
import com.choic11.model.customer.TblCustomerWalletHistory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Repository("BasketballCronRepository")
public class CronRepository {

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

    @Transactional
    public LinkedHashMap<Integer, Object> matchProgressCron() {

        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId, tcm.seriesId as seriesId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.matchProgress='F' AND tcm.closeDate <= :currentTime";

        Query queryy = getSession().createQuery(hqln).setParameter("currentTime", BigInteger.valueOf(currentTime));

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.isEmpty()) {
            return data;
        }
        List<Integer> matchUniqueIdData = new ArrayList<Integer>();
        for (HashMap<String, Object> hashMap : result) {

            int matchUniqueId = (int) hashMap.get("uniqueId");
            matchUniqueIdData.add(matchUniqueId);

            LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
            match.put("uniqueId", hashMap.get("uniqueId"));
            match.put("seriesId", hashMap.get("seriesId"));

            data.put(matchUniqueId, match);
        }

        if (!matchUniqueIdData.isEmpty()) {

            hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.matchProgress = 'L' "
                    + "WHERE tcm.uniqueId IN (:matchUniqueIdData)";

            queryy = getSession().createQuery(hqln).setParameterList("matchUniqueIdData", matchUniqueIdData);

            queryy.executeUpdate();
        }

        return data;

    }

    @Transactional
    public LinkedHashMap<Integer, Object> distributeAffiliatePercentageCron(int matchUniqueId) {

        Session session = getSession();

        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT "
                + "tcm.uniqueId as uniqueId, tcm.seriesId as seriesId, tcm.isAffiliateAvailableAll as isAffiliateAvailableAll, tcm.affiliatePerForAll as affiliatePerForAll "
                + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.isDisAffiPer='N' AND tcm.isContestAbondantComplete='Y'";
        if (matchUniqueId > 0) {
            hqln += " AND tcm.uniqueId=:matchUniqueId";
        } else {
            hqln += " AND tcm.matchProgress='L'";
        }

        Query queryy = session.createQuery(hqln);

        if (matchUniqueId > 0) {
            queryy.setParameter("matchUniqueId", matchUniqueId);
        }

        queryy.setFirstResult(0);
        queryy.setMaxResults(1);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.isEmpty()) {
            return data;
        }
        for (HashMap<String, Object> hashMap : result) {

            int matchUniqueIdData = (int) hashMap.get("uniqueId");

            int updateIsDisAffiPerOnMatch = updateIsDisAffiPerOnMatch(session, matchUniqueIdData, "R");

            if (updateIsDisAffiPerOnMatch > 0) {
                LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
                match.put("uniqueId", hashMap.get("uniqueId"));
                match.put("seriesId", hashMap.get("seriesId"));
                match.put("isAffiliateAvailableAll", hashMap.get("isAffiliateAvailableAll"));
                match.put("affiliatePerForAll", hashMap.get("affiliatePerForAll"));

                data.put(matchUniqueIdData, match);
            }

        }

        return data;
    }

    @Transactional
    public int updateIsDisAffiPerOnMatch(Session session, int matchUniqueId, String value) {

        if (session == null) {
            session = getSession();
        }
        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.isDisAffiPer = :value "
                + "WHERE tcm.uniqueId = :matchUniqueId";

        Query queryy = session.createQuery(hqln).setParameter("value", value).setParameter("matchUniqueId",
                matchUniqueId);

        int executeUpdate = queryy.executeUpdate();

        return executeUpdate;

    }

    public boolean distributeAffiliatePercentage(LinkedHashMap<String, Object> match) {

        Session session = getNewSession();

        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();
            int matchUniqueId = (int) match.get("uniqueId");
            int seriesId = (int) match.get("seriesId");

            String hqln = "SELECT " + "DISTINCT (tc1.id) as id, tc1.affiliatePercent as affiliatePercent "
                    + "FROM TblBasketballCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON tc.id=tccc.customerId "
                    + "LEFT JOIN TblCustomer tc1 ON tc1.id=tc.usedReferralUserId "
                    + "WHERE tccc.isAbondant='N' AND tccc.matchUniqueId=:matchUniqueId AND tc1.status = 'A' AND tc1.isDeleted = 'N' AND tc1.isAffiliate='1' AND tc1.affiliatePercent>0";

            Query queryy = session.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            if (result.isEmpty()) {
                return true;
            }
            tx = session.beginTransaction();
            String paramsAffiliate = "";

            for (HashMap<String, Object> hasMap : result) {
                int customerid = (int) hasMap.get("id");
                float affiliatePercent = (float) hasMap.get("affiliatePercent");
                if (affiliatePercent > 0) {
                    if (!paramsAffiliate.isEmpty()) {
                        paramsAffiliate += ", ";
                    }
                    paramsAffiliate += "(" + seriesId + "," + matchUniqueId + "," + customerid + "," + affiliatePercent
                            + "," + currentTime + ")";
                }
            }

            if (!paramsAffiliate.isEmpty()) {
                String mysqlQuery = "INSERT " + "INTO tbl_basketball_affiliates "
                        + "(series_id, match_unique_id, customer_id, affiliate_percent, created_at)" + " VALUES "
                        + paramsAffiliate;

                session.createSQLQuery(mysqlQuery).executeUpdate();
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }

        return false;
    }

    public boolean distributeCustomerAffiliatePercentage(LinkedHashMap<String, Object> match) {

        Session session = getNewSession();

        Transaction tx = null;
        try {
            Long currentTime = Util.getCurrentTime();
            int matchUniqueId = (int) match.get("uniqueId");
            int seriesId = (int) match.get("seriesId");
            String isAffiliateAvailableAll = (String) match.get("isAffiliateAvailableAll");
            float affiliatePerForAll = (float) match.get("affiliatePerForAll");

            if (isAffiliateAvailableAll.equals("N") || affiliatePerForAll <= 0) {
                return true;
            }

            String hqln = "SELECT " + "DISTINCT (tc1.id) as id, tc1.affiliatePercent as affiliatePercent "
                    + "FROM TblBasketballCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON tc.id=tccc.customerId "
                    + "LEFT JOIN TblCustomer tc1 ON tc1.id=tc.usedReferralUserId "
                    + "WHERE tccc.isAbondant='N' AND tccc.matchUniqueId=:matchUniqueId AND tc1.status = 'A' AND tc1.isDeleted = 'N' AND tc1.isAffiliate='0'";

            Query queryy = session.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            if (result.isEmpty()) {
                return true;
            }
            tx = session.beginTransaction();
            String paramsAffiliate = "";

            for (HashMap<String, Object> hasMap : result) {
                int customerid = (int) hasMap.get("id");
                float affiliatePercent = affiliatePerForAll;
                if (affiliatePercent > 0) {
                    if (!paramsAffiliate.isEmpty()) {
                        paramsAffiliate += ", ";
                    }
                    paramsAffiliate += "(" + seriesId + "," + matchUniqueId + "," + customerid + "," + affiliatePercent
                            + "," + currentTime + ")";
                }
            }

            if (!paramsAffiliate.isEmpty()) {
                String mysqlQuery = "INSERT " + "INTO tbl_basketball_customer_affiliates "
                        + "(series_id, match_unique_id, customer_id, affiliate_percent, created_at)" + " VALUES "
                        + paramsAffiliate;

                session.createSQLQuery(mysqlQuery).executeUpdate();
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }

        return false;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<Integer, Object> matchLineupCron(int matchUniqueId) {

        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT "
                + "tcm.uniqueId as uniqueId, tcm.playingSquadNotificationAt as playingSquadNotificationAt, tcm.playingSquadUpdatedMannual as playingSquadUpdatedMannual, "
                + "tcml.playerUniqueIds as playerUniqueIds, tcml.subsituteUniqueIds as subsituteUniqueIds "
                + "FROM TblBasketballMatch tcm "
                + "LEFT JOIN TblBasketballMatchesLineup tcml ON tcml.matchUniqueId=tcm.uniqueId "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        } else {
            hqln += "AND tcm.playingSquadUpdatedMannual='N' AND tcm.matchProgress='F' AND (tcm.closeDate - :currentTime) <= tcm.lineupBeforeSecond AND (tcm.closeDate - :currentTime) >= (60) "
                    + "ORDER BY tcm.closeDate ASC";
        }

        Query queryy = getSession().createQuery(hqln);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        } else {
            queryy.setParameter("currentTime", BigInteger.valueOf(currentTime));
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.isEmpty()) {
            return data;
        }
        for (HashMap<String, Object> hashMap : result) {
            int uniqueId = (int) hashMap.get("uniqueId");
            LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
            match.put("uniqueId", uniqueId);
            match.put("playingSquadUpdatedMannual", hashMap.get("playingSquadUpdatedMannual"));
            match.put("playingSquadNotificationAt", hashMap.get("playingSquadNotificationAt"));
            match.put("playerUniqueIds", hashMap.get("playerUniqueIds") == null ? "" : hashMap.get("playerUniqueIds"));
            match.put("subsituteUniqueIds",
                    hashMap.get("subsituteUniqueIds") == null ? "" : hashMap.get("subsituteUniqueIds"));
            data.put(uniqueId, match);
        }

        return data;

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<Integer, Object> matchScorecardCron(int matchUniqueId) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId,tcm.team1Id as team1Id,tcm.team2Id as team2Id, "
                + "tct_one.fullName as fullName, tct_one.name as name, tct_one.sortName as sortName, "
                + "tct_two.fullName as fullName2, tct_two.name as name2, tct_two.sortName as sortName2 "
                + "FROM TblBasketballMatch tcm " + "LEFT JOIN TblBasketballTeam tct_one ON tct_one.id=tcm.team1Id "
                + "LEFT JOIN TblBasketballTeam tct_two ON tct_two.id=tcm.team2Id "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        } else {
            hqln += "AND tcm.matchProgress='L' " + "ORDER BY tcm.scorecardUpdatedAt ASC";
        }

        Query queryy = getSession().createQuery(hqln);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        }
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.isEmpty()) {
            return data;
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
            match.put("uniqueId", hashMap.get("uniqueId"));
            match.put("team1Id", hashMap.get("team1Id"));
            match.put("fullName", hashMap.get("fullName"));
            match.put("name", hashMap.get("name"));
            match.put("sortName", hashMap.get("sortName"));
            match.put("team2Id", hashMap.get("team2Id"));
            match.put("fullName2", hashMap.get("fullName2"));
            match.put("name2", hashMap.get("name2"));
            match.put("sortName2", hashMap.get("sortName2"));
            data.put((int) match.get("uniqueId"), match);
        }

        return data;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<Integer, Object> matchAbondantContestCron(int matchUniqueId) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.isContestAbondantComplete='N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        } else {
            hqln += "AND tcm.matchProgress='L' ";
        }

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.isEmpty()) {
            return data;
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
            match.put("uniqueId", hashMap.get("uniqueId"));
            data.put((int) match.get("uniqueId"), match);
        }

        return data;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> distributeReferralCashbonusCron(int matchUniqueId) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.matchProgress='R' AND tcm.matchResultRunning='Y' AND tcm.isReferralDistributed='N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        }

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> distributeAffiliateAmountCron(int matchUniqueId) {

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.matchProgress='R' AND tcm.matchResultRunning='Y' AND tcm.isReferralDistributed='Y' AND tcm.isAffiliateDistribute='N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        }

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> generateMatchLeaderboardCron(int matchUniqueId) {
        String hqln = "SELECT " + "tcm.uniqueId as uniqueId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.matchProgress='R' AND tcm.matchResultRunning='Y' AND tcm.isReferralDistributed='Y' AND tcm.isLeaderboardCreated='N' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        } else {
            hqln += "ORDER BY tcm.pointsUpdatedAt ASC";
        }

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> liveMatchUpdateRankingCron(int matchUniqueId) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcm.id as id, tcm.gameTypeId as gameTypeId, tcm.uniqueId as uniqueId "
                + "FROM TblBasketballMatch tcm " + "WHERE "
                + "tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.matchProgress='L' AND tcm.isContestAbondantComplete='Y' ";
        if (matchUniqueId != 0) {
            hqln += "AND tcm.uniqueId=:uniqueId";
        } else {
            hqln += "ORDER BY tcm.pointsUpdatedAt ASC";
        }

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        if (matchUniqueId != 0) {
            queryy.setParameter("uniqueId", matchUniqueId);
        } else {
            queryy.setFirstResult(0);
            queryy.setMaxResults(1);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getPlayerDetailCron() {
        String hqln = "SELECT " + "tcp.uniqueId as uniqueId " + "FROM TblBasketballPlayer tcp " + "WHERE "
                + "tcp.status = 'A' AND tcp.isDeleted = 'N' AND tcp.isSummaryUpdated='N' " + "ORDER BY tcp.id DESC";
        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(10);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;

    }

    @Transactional(readOnly = true)
    public HashMap<Integer, String> getPlayersByMatch(int matchUniqueId) {

        String hqln = "SELECT " + "tcmp.playerUniqueId as playerUniqueId, " + "tcp.name as name "
                + "FROM  TblBasketballMatchPlayer tcmp "
                + "LEFT JOIN TblBasketballPlayer tcp ON (tcp.uniqueId=tcmp.playerUniqueId) " + "WHERE "
                + "tcmp.matchUniqueId = :matchUniqueId AND tcmp.status = 'A' AND tcmp.isDeleted='N'";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        HashMap<Integer, String> playersData = new HashMap<Integer, String>();
        for (HashMap<String, Object> hashMap : result) {
            int playerUniqueId = (int) hashMap.get("playerUniqueId");
            String name = (String) hashMap.get("name");
            playersData.put(playerUniqueId, name);
        }
        return playersData;
    }

    @Transactional
    public int updateContestPdfProgress(int matchContestId, String value, String pdfFileName) {

        String hqln = "UPDATE TblBasketballContestMatch tccm " + "SET tccm.pdfProcess=:value ";

        if (!Util.isEmpty(pdfFileName)) {
            hqln += ", tccm.pdf=:pdfFileName ";
        }
        hqln += "WHERE tccm.id = :matchContestId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", value).setParameter("matchContestId",
                matchContestId);
        if (!Util.isEmpty(pdfFileName)) {
            queryy.setParameter("pdfFileName", pdfFileName);
        }

        return queryy.executeUpdate();

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> generateContestPdfCron(int matchContestId) {

        Session session = getSession();

        String hqln = "SELECT "
                + "tccm.id as id, tccm.matchUniqueId as matchUniqueId, tccm.totalTeam as totalTeam, tccm.entryFees as entryFees, tccm.totalPrice as totalPrice, tccm.slug as slug, "
                + "tcm.name as matchName " + "FROM TblBasketballContestMatch tccm "
                + "LEFT JOIN TblBasketballMatch tcm on tcm.uniqueId=tccm.matchUniqueId " + "WHERE ";
        if (matchContestId == 0) {
            hqln += "tccm.status = 'A' AND tccm.isDeleted = 'N' AND tccm.isAbondant = 'N' AND tccm.pdfProcess = 'N' AND tcm.matchProgress='L' AND tcm.isContestAbondantComplete='Y' "
                    + "ORDER BY tccm.id ASC";
        } else {
            hqln += "tccm.id=:matchContestId";
        }
        Query queryy = session.createQuery(hqln);
        if (matchContestId == 0) {
            queryy.setFirstResult(0);
            queryy.setMaxResults(3);
        } else {
            queryy.setParameter("matchContestId", matchContestId);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result == null || result.size() == 0) {
            return result;
        }
        HashMap<Integer, Object> matchesPlayersData = new HashMap<Integer, Object>();

        for (HashMap<String, Object> hashMap : result) {

            int selectedMatchContestId = (int) hashMap.get("id");
            int matchUniqueId = (int) hashMap.get("matchUniqueId");

            if (!matchesPlayersData.containsKey(matchUniqueId)) {
                HashMap<Integer, String> playersByMatch = getPlayersByMatch(matchUniqueId);
                matchesPlayersData.put(matchUniqueId, playersByMatch);
            }
            HashMap<Integer, String> playersByMatch = (HashMap<Integer, String>) matchesPlayersData.get(matchUniqueId);

            hqln = "SELECT "
                    + "tct.id as id, tct.moreName as moreName, tct.name as name, tct.playerUniqueIds as playerUniqueIds, tct.playerMultiplers as playerMultiplers, tct.customerTeamName as customerTeamName, "
                    + "tc.teamName as teamName " + "FROM TblBasketballCustomerContest tccc "
                    + "LEFT JOIN TblBasketballCustomerTeam tct on tct.id=tccc.customerTeamId "
                    + "LEFT JOIN TblCustomer tc on tc.id=tccc.customerId " + "WHERE "
                    + "tccc.matchContestId = :selectedMatchContestId " + "ORDER BY tccc.id ASC";
            queryy = session.createQuery(hqln);

            queryy.setParameter("selectedMatchContestId", selectedMatchContestId);

            List<HashMap<String, Object>> resultTeams = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            List<JSONObject> contestTeams = new ArrayList<JSONObject>();
            for (HashMap<String, Object> hashMap2 : resultTeams) {

                String playerUniqueIds = (String) hashMap2.get("playerUniqueIds");
                String playerMultiplers = (String) hashMap2.get("playerMultiplers");
                String[] split = playerUniqueIds.split(",");
                String[] split2 = playerMultiplers.split(",");

                String customerTeamName = (String) hashMap2.get("customerTeamName");
                if (Util.isEmpty(customerTeamName)) {
                    customerTeamName = (String) hashMap2.get("teamName");
                }

                int name = (int) hashMap2.get("moreName");
                if (name == 0) {
                    name = (int) hashMap2.get("name");
                }

                List<JSONObject> teamPlayers = new ArrayList<JSONObject>();
                for (int i = 0; i < split.length; i++) {
                    int playerUniqueId = Integer.parseInt(split[i]);
                    float playerMultiplier = Float.parseFloat(split2[i]);
                    String playerName = playersByMatch.get(playerUniqueId);
                    if (Util.isEmpty(playerName)) {
                        playerName = "";
                    }
                    JSONObject player = new JSONObject();
                    player.put("name", playerName);
                    player.put("multiplier", playerMultiplier);
                    teamPlayers.add(player);
                }

                Collections.sort(teamPlayers, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        float valA = a.getFloat("multiplier");
                        float valB = b.getFloat("multiplier");

                        if (valA == valB) {
                            return 0;
                        } else if (valA < valB) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                JSONObject customerTeam = new JSONObject();
                customerTeam.put("id", hashMap2.get("id"));
                customerTeam.put("teamName", customerTeamName);
                customerTeam.put("name", name);
                customerTeam.put("players", teamPlayers);

                contestTeams.add(customerTeam);

            }
            hashMap.put("teams", contestTeams);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getPointSystemForGameTypeId(int gameTypeId) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcp.metaKey as metaKey, tcp.metaValue as metaValue " + "FROM TblBasketballPoint tcp "
                + "WHERE " + "tcp.status = 'A' AND tcp.isDeleted = 'N' AND tcp.gameTypeId=:gameTypeId";

        Query queryy = getSession().createQuery(hqln).setParameter("gameTypeId", gameTypeId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

        for (HashMap<String, Object> hashMap : result) {
            data.put(hashMap.get("metaKey").toString(), hashMap.get("metaValue"));
        }
        return data;

    }

    @Transactional(readOnly = true)
    public JSONObject getPlayersForMatchUniqueId(int matchUniqueId) {

        String hqln = "SELECT " + "tcmp.playerUniqueId as playerUniqueId, tcmp.playingRole as playingRole "
                + "FROM TblBasketballMatchPlayer tcmp " + "WHERE "
                + "tcmp.status = 'A' AND tcmp.isDeleted = 'N' AND tcmp.matchUniqueId=:matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        JSONObject data = new JSONObject();

        for (HashMap<String, Object> hashMap : result) {
            int playerUniqueId = (int) hashMap.get("playerUniqueId");
            String playingRole = (String) hashMap.get("playingRole");

            JSONObject player = new JSONObject();
            player.put("playerUniqueId", playerUniqueId);
            player.put("playingRole", playingRole);

            data.put(String.valueOf(playerUniqueId), player);
        }
        return data;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getPlayersForDreamTeam(int matchUniqueId) {

        String hqln = "SELECT "
                + "tcmp.playerUniqueId as playerUniqueId, tcmp.points as points, tcmp.credits as credits, tcmp.playingRole as playingRole, tcmp.teamId as teamId  "
                + "FROM TblBasketballMatchPlayer tcmp " + "WHERE "
                + "tcmp.status = 'A' AND tcmp.isDeleted = 'N' AND tcmp.matchUniqueId=:matchUniqueId "
                + "ORDER BY tcmp.points DESC";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, TblBasketballMatchPlayersStat> getPlayersStatsForMatchUniqueId(int matchUniqueId) {

        String hqlQuery = "SELECT " +
                "tsmps.id as id," +
                "tsmps.matchUniqueId as matchUniqueId," +
                "tsmps.playerUniqueId as playerUniqueId," +
                "tsmps.pointsScored as pointsScored," +
                "tsmps.pointsScoredValue as pointsScoredValue," +
                "tsmps.rebounds as rebounds," +
                "tsmps.reboundsValue as reboundsValue," +
                "tsmps.assists as assists," +
                "tsmps.assistsValue as assistsValue," +
                "tsmps.steals as steals," +
                "tsmps.stealsValue as stealsValue," +
                "tsmps.blocks as blocks," +
                "tsmps.blocksValue as blocksValue," +
                "tsmps.turnOvers as turnOvers," +
                "tsmps.turnOversValue as turnOversValue," +
                "tsmps.updated as updated " +
                "FROM TblBasketballMatchPlayersStat tsmps WHERE tsmps.matchUniqueId =:matchUniqueId";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("matchUniqueId", matchUniqueId);
        List<TblBasketballMatchPlayersStat> list = queryObj
                .setResultTransformer(new AliasToBeanResultTransformer(TblBasketballMatchPlayersStat.class))
                .getResultList();

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatchPlayersStat.class);
//        createCriteria.add(Restrictions.eq("matchUniqueId", matchUniqueId));
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("beingPartOfEleven"), "beingPartOfEleven");
//        projectionList.add(Projections.property("beingPartOfElevenValue"), "beingPartOfElevenValue");
//        projectionList.add(Projections.property("catch1"), "catch1");
//        projectionList.add(Projections.property("catchAndBowled"), "catchAndBowled");
//        projectionList.add(Projections.property("catchAndBowledValue"), "catchAndBowledValue");
//        projectionList.add(Projections.property("century"), "century");
//        projectionList.add(Projections.property("centuryValue"), "centuryValue");
//        projectionList.add(Projections.property("dismissForADuck"), "dismissForADuck");
//        projectionList.add(Projections.property("dismissForADuckValue"), "dismissForADuckValue");
//        projectionList.add(Projections.property("economyRate"), "economyRate");
//        projectionList.add(Projections.property("economyRateValue"), "economyRateValue");
//        projectionList.add(Projections.property("everyBoundaryHit"), "everyBoundaryHit");
//        projectionList.add(Projections.property("everyBoundaryHitValue"), "everyBoundaryHitValue");
//        projectionList.add(Projections.property("everyRunScored"), "everyRunScored");
//        projectionList.add(Projections.property("everyRunScoredValue"), "everyRunScoredValue");
//        projectionList.add(Projections.property("everySixHit"), "everySixHit");
//        projectionList.add(Projections.property("everySixHitValue"), "everySixHitValue");
//        projectionList.add(Projections.property("fiveWicket"), "fiveWicket");
//        projectionList.add(Projections.property("fiveWicketValue"), "fiveWicketValue");
//        projectionList.add(Projections.property("fourWicket"), "fourWicket");
//        projectionList.add(Projections.property("fourWicketValue"), "fourWicketValue");
//        projectionList.add(Projections.property("halfCentury"), "halfCentury");
//        projectionList.add(Projections.property("halfCenturyValue"), "halfCenturyValue");
//        projectionList.add(Projections.property("maidenOver"), "maidenOver");
//        projectionList.add(Projections.property("maidenOverValue"), "maidenOverValue");
//        projectionList.add(Projections.property("matchUniqueId"), "matchUniqueId");
//        projectionList.add(Projections.property("playerUniqueId"), "playerUniqueId");
//        projectionList.add(Projections.property("runOut"), "runOut");
//        projectionList.add(Projections.property("runOutCatcher"), "runOutCatcher");
//        projectionList.add(Projections.property("runOutCatcherValue"), "runOutCatcherValue");
//        projectionList.add(Projections.property("runOutThrower"), "runOutThrower");
//        projectionList.add(Projections.property("runOutThrowerValue"), "runOutThrowerValue");
//        projectionList.add(Projections.property("runOutValue"), "runOutValue");
//        projectionList.add(Projections.property("strikeRate"), "strikeRate");
//        projectionList.add(Projections.property("strikeRateValue"), "strikeRateValue");
//        projectionList.add(Projections.property("stumping"), "stumping");
//        projectionList.add(Projections.property("stumpingValue"), "stumpingValue");
//        projectionList.add(Projections.property("thirtyRuns"), "thirtyRuns");
//        projectionList.add(Projections.property("thirtyRunsValue"), "thirtyRunsValue");
//        projectionList.add(Projections.property("threeWicket"), "threeWicket");
//        projectionList.add(Projections.property("threeWicketValue"), "threeWicketValue");
//        projectionList.add(Projections.property("twoWicket"), "twoWicket");
//        projectionList.add(Projections.property("twoWicketValue"), "twoWicketValue");
//        projectionList.add(Projections.property("updated"), "updated");
//        projectionList.add(Projections.property("wicket"), "wicket");
//        projectionList.add(Projections.property("wicketValue"), "wicketValue");
//
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblBasketballMatchPlayersStat.class));
//        List<TblBasketballMatchPlayersStat> list = createCriteria.list();

        LinkedHashMap<String, TblBasketballMatchPlayersStat> data = new LinkedHashMap<String, TblBasketballMatchPlayersStat>();
        if (list != null && list.size() > 0) {
            for (TblBasketballMatchPlayersStat tblBasketballMatchPlayersStat : list) {

                data.put(String.valueOf(tblBasketballMatchPlayersStat.getPlayerUniqueId()), tblBasketballMatchPlayersStat);
            }
        }

        return data;
    }

    public boolean updateMatchLineup(int matchUniqueId, List<Integer> players, List<Integer> oldPlayers,
                                     List<Integer> substitute, List<Integer> oldSubstitute, String playerUniqueIds, String newPlayerUniqueIds,
                                     String newSubsituteUniqueIds, boolean fromMannual) {

        Session session = getNewSession();
        Transaction tx = null;
        try {
            boolean needUpdatePlayers = false;
            boolean needUpdateSubsitude = false;

            if (!oldPlayers.isEmpty()) {
                if (tx == null) {
                    tx = session.beginTransaction();
                }
                needUpdatePlayers = true;
                String isInPlayingSquad = "N";
                String hqln = "UPDATE TblBasketballMatchPlayer tcmp " + "SET tcmp.isInPlayingSquad = :isInPlayingSquad "
                        + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.playerUniqueId IN (:playerUniqueId)";

                Query queryy = session.createQuery(hqln).setParameter("isInPlayingSquad", isInPlayingSquad)
                        .setParameter("matchUniqueId", matchUniqueId).setParameterList("playerUniqueId", oldPlayers);

                queryy.executeUpdate();
            }

            if (!oldSubstitute.isEmpty()) {
                if (tx == null) {
                    tx = session.beginTransaction();
                }
                needUpdateSubsitude = true;
                String isInPlayingSquad = "N";
                String isInSubstituteSquad = "N";
                String hqln = "UPDATE TblBasketballMatchPlayer tcmp "
                        + "SET tcmp.isInPlayingSquad = :isInPlayingSquad, tcmp.isInSubstituteSquad = :isInSubstituteSquad "
                        + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.playerUniqueId IN (:playerUniqueId)";

                Query queryy = session.createQuery(hqln).setParameter("isInPlayingSquad", isInPlayingSquad)
                        .setParameter("isInSubstituteSquad", isInSubstituteSquad)
                        .setParameter("matchUniqueId", matchUniqueId).setParameterList("playerUniqueId", oldSubstitute);

                queryy.executeUpdate();
            }

            if (!players.isEmpty()) {

                if (tx == null) {
                    tx = session.beginTransaction();
                }
                needUpdatePlayers = true;
                String isInPlayingSquad = "Y";
                String hqln = "UPDATE TblBasketballMatchPlayer tcmp " + "SET tcmp.isInPlayingSquad = :isInPlayingSquad "
                        + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.playerUniqueId IN (:playerUniqueId)";

                Query queryy = session.createQuery(hqln).setParameter("isInPlayingSquad", isInPlayingSquad)
                        .setParameter("matchUniqueId", matchUniqueId).setParameterList("playerUniqueId", players);

                queryy.executeUpdate();
            }


            if (!substitute.isEmpty()) {
                if (tx == null) {
                    tx = session.beginTransaction();
                }
                needUpdateSubsitude = true;
                String isInPlayingSquad = "Y";
                String isInSubstituteSquad = "Y";
                String hqln = "UPDATE TblBasketballMatchPlayer tcmp "
                        + "SET tcmp.isInPlayingSquad = :isInPlayingSquad, tcmp.isInSubstituteSquad = :isInSubstituteSquad "
                        + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.playerUniqueId IN (:playerUniqueId)";

                Query queryy = session.createQuery(hqln).setParameter("isInPlayingSquad", isInPlayingSquad)
                        .setParameter("isInSubstituteSquad", isInSubstituteSquad)
                        .setParameter("matchUniqueId", matchUniqueId).setParameterList("playerUniqueId", substitute);

                queryy.executeUpdate();
            }


            if (needUpdatePlayers || needUpdateSubsitude) {

                Long currentTime = Util.getCurrentTime();

                if (playerUniqueIds.isEmpty()) {
                    TblBasketballMatchesLineup tblBasketballMatchesLineup = new TblBasketballMatchesLineup();
                    tblBasketballMatchesLineup.setMatchUniqueId(matchUniqueId);
                    tblBasketballMatchesLineup.setPlayerUniqueIds(newPlayerUniqueIds);
                    tblBasketballMatchesLineup.setSubsituteUniqueIds(newSubsituteUniqueIds);
                    tblBasketballMatchesLineup.setUpdatedAt(BigInteger.valueOf(currentTime));

                    session.save(tblBasketballMatchesLineup);

                    String playingSquadUpdated = "Y";
                    String playingSquadUpdatedMannual = "Y";
                    String hqln = "UPDATE TblBasketballMatch tcm "
                            + "SET tcm.playingSquadUpdated=:playingSquadUpdated, tcm.playingSquadNotificationAt = :playingSquadNotificationAt ";
                    if (fromMannual) {
                        hqln += ", tcm.playingSquadUpdatedMannual=:playingSquadUpdatedMannual ";
                    }
                    hqln += "WHERE tcm.uniqueId = :matchUniqueId";

                    Query queryy = session.createQuery(hqln).setParameter("playingSquadUpdated", playingSquadUpdated)
                            .setParameter("playingSquadNotificationAt", BigInteger.valueOf(currentTime))
                            .setParameter("matchUniqueId", matchUniqueId);
                    if (fromMannual) {
                        queryy.setParameter("playingSquadUpdatedMannual", playingSquadUpdatedMannual);
                    }

                    queryy.executeUpdate();

                } else {

                    String hqln = "UPDATE TblBasketballMatchesLineup tcml "
                            + "SET tcml.playerUniqueIds=:newPlayerUniqueIds, tcml.subsituteUniqueIds=:newSubsituteUniqueIds, tcml.updatedAt=:currentTime "
                            + "WHERE tcml.matchUniqueId = :matchUniqueId";

                    Query queryy = session.createQuery(hqln).setParameter("newPlayerUniqueIds", newPlayerUniqueIds)
                            .setParameter("newSubsituteUniqueIds", newSubsituteUniqueIds)
                            .setParameter("currentTime", BigInteger.valueOf(currentTime))
                            .setParameter("matchUniqueId", matchUniqueId);

                    queryy.executeUpdate();

                    if (fromMannual) {
                        String playingSquadUpdatedMannual = "Y";
                        String hqln1 = "UPDATE TblBasketballMatch tcm "
                                + "SET tcm.playingSquadUpdatedMannual=:playingSquadUpdatedMannual "
                                + "WHERE tcm.uniqueId = :matchUniqueId";

                        Query queryy1 = session.createQuery(hqln1)
                                .setParameter("playingSquadUpdatedMannual", playingSquadUpdatedMannual)
                                .setParameter("matchUniqueId", matchUniqueId);

                        queryy1.executeUpdate();
                    }

                }

            }
            if (tx != null) {
                tx.commit();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }

        return false;

    }

    @Transactional
    public void saveLineupNotificationForCustomers(int matchUniqueId) {

        Session session = getSession();

        Long currentTime = Util.getCurrentTime();

        long customerActiveAfter = currentTime - (3 * 24 * 60 * 60);

        String hqln = "SELECT " + "tcm.name as name, " + "tcs.name as seriesName " + "FROM TblBasketballMatch tcm "
                + "LEFT JOIN TblBasketballSeries tcs ON tcs.id=tcm.seriesId "
                + "WHERE tcm.status = 'A' AND tcm.isDeleted = 'N' AND tcm.uniqueId=:matchUniqueId";

        Query queryy = session.createQuery(hqln);
        queryy.setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return;
        }
        HashMap<String, Object> matchData = result.get(0);

        hqln = "SELECT " + "DISTINCT customerId as customerId " + "FROM TblCustomerLogin "
                + "WHERE customerId>0 AND created > :customerActiveAfter";

        queryy = session.createQuery(hqln);
        queryy.setParameter("customerActiveAfter", BigInteger.valueOf(customerActiveAfter));

        result = (List<HashMap<String, Object>>) queryy.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return;
        }

        String customerIds = "";
        for (HashMap<String, Object> hashMap : result) {
            if (!customerIds.isEmpty()) {
                customerIds += ",";
            }
            customerIds += hashMap.get("customerId").toString();
        }

        String noti_type = "lineup_out";
        JSONObject data = new JSONObject();
        data.put("noti_type", noti_type);
        data.put("title", matchData.get("seriesName").toString());
        String alertMessage = "Here are the Line ups for " + matchData.get("name").toString() + "!";

        TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
        tblBasketballLineupNotiCron.setIsSend("N");
        tblBasketballLineupNotiCron.setMessage(data.toString());
        tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
        tblBasketballLineupNotiCron.setUserIds(customerIds);
        tblBasketballLineupNotiCron.setNotiType(noti_type);
        tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));

        session.save(tblBasketballLineupNotiCron);

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> sendLineupNotificationCron() {

        Session session = getSession();

        String hqln = "SELECT "
                + "tclnc.id as id, tclnc.alertMessage as alertMessage, tclnc.message as message, tclnc.userIds as userIds, tclnc.notiType as notiType "
                + "FROM TblBasketballLineupNotiCron tclnc " + "WHERE tclnc.isSend = 'N'";

        Query queryy = session.createQuery(hqln);

        queryy.setFirstResult(0);
        queryy.setMaxResults(10);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> sendMailCron() {

        Session session = getSession();

        String hqln = "SELECT "
                + "tce.id as id, tce.subject as subject, tce.message as message, tce.toemail as toemail, tce.toname as toname "
                + "FROM TblBasketballEmailCron tce " + "WHERE tce.isSend = 'N'";

        Query queryy = session.createQuery(hqln);

        queryy.setFirstResult(0);
        queryy.setMaxResults(10);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }


    @Transactional
    public void deleteLineupNotification(int id) {

        Session session = getSession();

        String hqln = "DELETE FROM TblBasketballLineupNotiCron tclnc "

                + "WHERE tclnc.id = :id";

        Query queryy = session.createQuery(hqln).setParameter("id", id);

        queryy.executeUpdate();
    }

    @Transactional
    public void deleteLineupNotificationMulti(List<Integer> ids) {

        Session session = getSession();

        String hqln = "DELETE FROM TblBasketballLineupNotiCron tblnc "

                + "WHERE tblnc.id IN(:id)";

        Query queryy = session.createQuery(hqln)
                .setParameterList("id", ids);

        queryy.executeUpdate();
    }

    @Transactional
    public void deleteEmailCron(int id) {

        Session session = getSession();

        String hqln = "DELETE FROM TblBasketballEmailCron tclnc "

                + "WHERE tclnc.id = :id";

        Query queryy = session.createQuery(hqln).setParameter("id", id);

        queryy.executeUpdate();
    }


    @Transactional
    public void deleteEmailCronMulti(List<Integer> ids) {

        Session session = getSession();

        String hqln = "DELETE FROM TblBasketballEmailCron tblnc "

                + "WHERE tblnc.id IN(:id)";

        Query queryy = session.createQuery(hqln)
                .setParameterList("id", ids);

        queryy.executeUpdate();
    }

    @Transactional
    public boolean updateMatchScoreCard(int matchUniqueId, JSONObject scorecard_data) {

        if (scorecard_data == null) {
            return false;
        }

        Long currentTime = Util.getCurrentTime();

        Session session = getNewSession();

        Transaction tx = null;

        try {
            if (!scorecard_data.isEmpty()) {
                tx = session.beginTransaction();

                String hqln = "UPDATE TblBasketballMatch tsm "
                        + "SET tsm.team1Run=:team1Run, tsm.team2Run=:team2Run, tsm.scoreBoardNotes=:scoreBoardNotes, tsm.scorecardUpdatedAt=:scorecardUpdatedAt "
                        + "WHERE tsm.uniqueId = :uniqueId";

                Query queryy = session.createQuery(hqln).setParameter("team1Run", scorecard_data.get("team1_run"))
                        .setParameter("team2Run", scorecard_data.get("team2_run"))
                        .setParameter("scoreBoardNotes", scorecard_data.get("score_board_notes"))
                        .setParameter("scorecardUpdatedAt", BigInteger.valueOf(currentTime))
                        .setParameter("uniqueId", matchUniqueId);
                queryy.executeUpdate();
                tx.commit();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
        return false;
    }

    public boolean updatePlayerDetail(int playerUniqueId, LinkedHashMap<String, Object> playerDetail) {

        if (playerDetail != null && !playerDetail.isEmpty()) {
            Session newSession = getNewSession();
            Transaction tx = null;
            try {
                Long currentTime = Util.getCurrentTime();
                String countryName = playerDetail.get("country") == null ? "" : playerDetail.get("country").toString();
                String dob = playerDetail.get("born") == null ? "" : playerDetail.get("born").toString().trim();
                String bets = playerDetail.get("battingStyle") == null ? ""
                        : playerDetail.get("battingStyle").toString();
                String bowls = playerDetail.get("bowlingStyle") == null ? ""
                        : playerDetail.get("bowlingStyle").toString();
                String position = playerDetail.get("playingRole") == null ? ""
                        : playerDetail.get("playingRole").toString();

                if (bets.trim().isEmpty()) {
                    bets = " ";
                }
                if (bowls.trim().isEmpty()) {
                    bowls = " ";
                }
                if (position.trim().isEmpty()) {
                    position = " ";
                }

                if (dob.trim().length() > 0) {
                    String[] split = dob.split(",");
                    if (split.length >= 1) {
                        LocalDate parse1 = LocalDate.parse(split[0].trim());
                        int year = parse1.getYear();
                        int monthValue = parse1.getMonthValue();
                        int dayOfMonth = parse1.getDayOfMonth();
                        String yearString = String.valueOf(year);
                        String monthString = monthValue > 9 ? String.valueOf(monthValue) : "0" + String.valueOf(monthValue);
                        String dayString = dayOfMonth > 9 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth);

                        dob = yearString + "-" + monthString + "-" + dayString;
                    } else {
                        dob = "";
                    }
                }


                int playerCountryId = 0;
                if (!countryName.trim().isEmpty()) {
                    String hqln = "SELECT " + "tc.id as id " + "FROM TblCountry tc " + "WHERE tc.name = :countryName";

                    Query queryy = newSession.createQuery(hqln).setParameter("countryName", countryName);

                    List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                    if (result != null && result.size() > 0) {
                        playerCountryId = (int) result.get(0).get("id");
                    }

                    if (playerCountryId == 0) {
                        tx = newSession.beginTransaction();
                        TblCountry instance = TblCountry.getInstance();
                        instance.setName(countryName);
                        instance.setCreatedAt(BigInteger.valueOf(currentTime));
                        instance.setUpdatedAt(BigInteger.valueOf(currentTime));
                        newSession.save(instance);
                        playerCountryId = instance.getId();
                    }
                }

                if (tx == null) {
                    tx = newSession.beginTransaction();
                }
                String summary = new JSONObject(playerDetail).toString();
                String isSummaryUpdated = "Y";
                String hqln = "UPDATE TblBasketballPlayer tcp "
                        + "SET tcp.bets=:bets, tcp.bowls=:bowls, tcp.position=:position, tcp.dob=:dob, tcp.countryId=:countryId, tcp.updatedAt=:updatedAt, tcp.summary=:summary, tcp.isSummaryUpdated=:isSummaryUpdated  "
                        + "WHERE tcp.uniqueId = :uniqueId";

                Query queryy = newSession.createQuery(hqln).setParameter("bets", bets).setParameter("bowls", bowls)
                        .setParameter("position", position).setParameter("dob", dob)
                        .setParameter("countryId", playerCountryId)
                        .setParameter("updatedAt", BigInteger.valueOf(currentTime)).setParameter("summary", summary)
                        .setParameter("isSummaryUpdated", isSummaryUpdated).setParameter("uniqueId", playerUniqueId);

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

        }

        return false;
    }

    @Transactional
    public int updateMatchDreamTeamPlayers(int matchUniqueId, List<Integer> playerUniqueIds) {
        String dreamTeamPlayer = "Y";
        String hqln = "UPDATE TblBasketballMatchPlayer tcmp " + "SET tcmp.dreamTeamPlayer=:dreamTeamPlayer "
                + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.playerUniqueId IN (:playerUniqueIds)";

        Query queryy = getSession().createQuery(hqln).setParameter("dreamTeamPlayer", dreamTeamPlayer)
                .setParameter("matchUniqueId", matchUniqueId).setParameterList("playerUniqueIds", playerUniqueIds);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchReferralDistributed(int uniqueId, String value) {

        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.isReferralDistributed=:value "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", value).setParameter("uniqueId", uniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchAffiliateDistributed(int uniqueId, String value) {

        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.isAffiliateDistribute=:value "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", value).setParameter("uniqueId", uniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchLeaderboardCreated(int uniqueId, String value) {

        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.isLeaderboardCreated=:value "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", value).setParameter("uniqueId", uniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchResultStart(int uniqueId, String result) {
        Long currentTime = Util.getCurrentTime();

        String matchProgress = result;
        String matchResultRunning = "R";
        String hqln = "UPDATE TblBasketballMatch tcm "
                + "SET tcm.matchProgress=:matchProgress, tcm.matchResultRunning=:matchResultRunning, tcm.pointsUpdatedAt=:pointsUpdatedAt "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchProgress", matchProgress)
                .setParameter("matchResultRunning", matchResultRunning)
                .setParameter("pointsUpdatedAt", BigInteger.valueOf(currentTime)).setParameter("uniqueId", uniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchResultCompleted(int uniqueId) {

        String matchResultRunning = "Y";
        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.matchResultRunning=:matchResultRunning "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchResultRunning", matchResultRunning)
                .setParameter("uniqueId", uniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchContestAbondant(int matchUniqueId, String value) {

        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.isContestAbondantComplete=:value "
                + "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", value).setParameter("uniqueId",
                matchUniqueId);

        return queryy.executeUpdate();

    }

    @Transactional
    public int updateMatchPointsUpdatedAt(int matchUniqueId, String matchProgress, String matchCompletedStatus) {

        Long currentTime = Util.getCurrentTime();

        String hqln = "UPDATE TblBasketballMatch tcm " + "SET tcm.pointsUpdatedAt=:value ";
        if (matchProgress != null && !matchProgress.isEmpty()) {
            hqln += ", tcm.matchProgress=:matchProgress ";
        }
        if (matchCompletedStatus != null && !matchCompletedStatus.isEmpty()) {
            hqln += ", tcm.matchCompletedStatus=:matchCompletedStatus ";
        }
        hqln += "WHERE tcm.uniqueId = :uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("value", BigInteger.valueOf(currentTime));
        if (matchProgress != null && !matchProgress.isEmpty()) {
            queryy.setParameter("matchProgress", matchProgress);
        }
        if (matchCompletedStatus != null && !matchCompletedStatus.isEmpty()) {
            queryy.setParameter("matchCompletedStatus", matchCompletedStatus);
        }
        queryy.setParameter("uniqueId", matchUniqueId);

        return queryy.executeUpdate();

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContestForAbondant(int matchUniqueId) {

        String hqln = "SELECT "
                + "tccm.id as id, tccm.categoryId as categoryId, tccm.contestJson as contestJson, tccm.totalTeam as totalTeam, tccm.totalJoinedTeam as totalJoinedTeam, tccm.confirmWin as confirmWin, tccm.totalPrice as totalPrice, tccm.actualEntryFees as actualEntryFees, tccm.entryFees as entryFees, tccm.confirmWinContestPercentage as confirmWinContestPercentage, tccm.seriesId as seriesId, tccm.matchUniqueId as matchUniqueId, tccm.isCompressionAllow as isCompressionAllow,tccm.compressionAllowPercentage as compressionAllowPercentage, "
                + "(SELECT COUNT(DISTINCT playerUniqueIdsMultiplers) FROM TblBasketballCustomerTeam where id IN(SELECT customerTeamId FROM TblBasketballCustomerContest WHERE matchContestId=tccm.id)) as clashcount "
                + "FROM TblBasketballContestMatch tccm "
                + "WHERE tccm.isDeleted='N' AND tccm.status='A' AND tccm.isAbondant='N' AND tccm.matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result != null && result.size() > 0) {
            List<HashMap<String, Object>> resultFinal = new ArrayList<>();
            for (HashMap<String, Object> contest : result) {
                long clashcount = (long) contest.get("clashcount");
                int totalTeams = (int) contest.get("totalTeam");
                int joinedTeams = (int) contest.get("totalJoinedTeam");
                if (totalTeams - joinedTeams > 0 || clashcount == 1) {
                    resultFinal.add(contest);
                }

            }
            return resultFinal;
        }

        return result;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContestForResult(int matchUniqueId) {

        String hqln = "SELECT "
                + "tccm.id as id, tccm.categoryId as categoryId, tccm.contestJson as contestJson, tccm.totalTeam as totalTeam, tccm.totalJoinedTeam as totalJoinedTeam, tccm.confirmWin as confirmWin, tccm.totalPrice as totalPrice, tccm.actualEntryFees as actualEntryFees, tccm.entryFees as entryFees, tccm.confirmWinContestPercentage as confirmWinContestPercentage, tccm.seriesId as seriesId, tccm.matchUniqueId as matchUniqueId, tccm.isCompressionAllow as isCompressionAllow  "
                + "FROM TblBasketballContestMatch tccm "
                + "WHERE tccm.isDeleted='N' AND tccm.status='A' AND tccm.isAbondant='N' AND tccm.isResult='N' AND tccm.matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchContestAdminAbondant(int matchContestId) {

        String hqln = "SELECT "
                + "tccm.id as id, tccm.categoryId as categoryId, tccm.contestJson as contestJson, tccm.totalTeam as totalTeam, tccm.totalJoinedTeam as totalJoinedTeam, tccm.confirmWin as confirmWin, tccm.totalPrice as totalPrice, tccm.actualEntryFees as actualEntryFees, tccm.entryFees as entryFees, tccm.confirmWinContestPercentage as confirmWinContestPercentage, tccm.seriesId as seriesId, tccm.matchUniqueId as matchUniqueId, tccm.isCompressionAllow as isCompressionAllow, tccm.isAbondant as isAbondant " + "FROM TblBasketballContestMatch tccm "
                + "WHERE tccm.isDeleted='N' AND tccm.status='A' AND tccm.isResult='N' AND tccm.id = :matchContestId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result == null || result.size() == 0) {
            return null;
        }

        return result.get(0);

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContestForRanking(int matchUniqueId) {

        String hqln = "SELECT " + "tccm.id as id, tccm.matchUniqueId as matchUniqueId  "
                + "FROM TblBasketballContestMatch tccm "
                + "WHERE tccm.isDeleted='N' AND tccm.status='A' AND tccm.isAbondant='N' AND tccm.isResult='N' AND tccm.matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;

    }

    public HashMap<String, Object> abodentContest(HashMap<String, Object> contest) {

        int matchContestId = (int) contest.get("id");
        int seriesId = (int) contest.get("seriesId");
        int matchUniqueId = (int) contest.get("matchUniqueId");
        float entryFees = (float) contest.get("entryFees");
        String matchName = "";

        boolean fromClash = false;
        if (contest.containsKey("fromClash")) {
            fromClash = (boolean) contest.get("fromClash");
        }

        boolean fromMatchAB = false;
        if (contest.containsKey("fromMatchAB")) {
            fromMatchAB = (boolean) contest.get("fromMatchAB");
            matchName = (String) contest.get("matchName");
        }

        HashMap<String, Object> output = new HashMap<String, Object>();

        Session newSession = getNewSession();
        Transaction tx = null;
        try {

            tx = newSession.beginTransaction();

            String hqln = "SELECT "
                    + "tccc.id as id, tccc.customerId as customerId, tccc.customerTeamId as customerTeamId, tccc.cashBonusWallet as cashBonusWallet, tccc.depositeWallet as depositeWallet, tccc.winningWallet as winningWallet "
                    + "FROM TblBasketballCustomerContest tccc " + "WHERE tccc.matchContestId=:matchContestId";

            Query queryy = newSession.createQuery(hqln).setParameter("matchContestId", matchContestId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            HashMap<Integer, Float> customers = new HashMap<Integer, Float>();

            if (result.size() == 0) {
                hqln = "UPDATE TblBasketballContestMatch tccm " + "SET tccm.isAbondant='Y' " + "WHERE tccm.id = :id";

                queryy = newSession.createQuery(hqln).setParameter("id", matchContestId);

                queryy.executeUpdate();
            } else {
                for (HashMap<String, Object> hashMap : result) {

                    int id = (int) hashMap.get("id");
                    int authUserId = (int) hashMap.get("customerId");
                    int teamId = (int) hashMap.get("customerTeamId");
                    float cashBonusWallet = (float) hashMap.get("cashBonusWallet");
                    float depositeWallet = (float) hashMap.get("depositeWallet");
                    float winningWallet = (float) hashMap.get("winningWallet");


                    float refundAmount = cashBonusWallet + depositeWallet + winningWallet;


                    LinkedHashMap<String, Object> extraData = new LinkedHashMap<String, Object>();
                    extraData.put("usedBonus", cashBonusWallet);
                    extraData.put("usedDeposit", depositeWallet);
                    extraData.put("usedWinning", winningWallet);

                    LinkedHashMap<String, Object> insertAbondantContestInWalletHistory = insertAbondantContestInWalletHistory(
                            newSession, seriesId, matchUniqueId, matchContestId, authUserId, teamId, extraData);

                    if (refundAmount > 0) {
                        hqln = "UPDATE TblBasketballCustomerContest tccc " + "SET tccc.refundAmount=:refundAmount "
                                + "WHERE tccc.id = :id";

                        queryy = newSession.createQuery(hqln).setParameter("refundAmount", refundAmount)
                                .setParameter("id", id);

                        queryy.executeUpdate();

                        if (customers.containsKey(authUserId)) {
                            float previousAmount = customers.get(authUserId);
                            customers.put(authUserId, refundAmount + previousAmount);
                        } else {
                            customers.put(authUserId, refundAmount);
                        }
                    }

                }

                hqln = "UPDATE TblBasketballCustomerContest tccc " + "SET tccc.isAbondant='Y' "
                        + "WHERE tccc.matchContestId = :matchContestId";

                queryy = newSession.createQuery(hqln).setParameter("matchContestId", matchContestId);

                queryy.executeUpdate();

                hqln = "UPDATE TblBasketballContestMatch tccm " + "SET tccm.isAbondant='Y' " + "WHERE tccm.id = :id";

                queryy = newSession.createQuery(hqln).setParameter("id", matchContestId);

                queryy.executeUpdate();
            }

            tx.commit();
            output.put("customers", customers);
            output.put("completed", true);
        } catch (Exception e) {
            e.printStackTrace();
            output.clear();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        if (!output.isEmpty()) {

            Long currentTime = Util.getCurrentTime();

            HashMap<Integer, Float> customers = (HashMap<Integer, Float>) output.get("customers");
            Set<Integer> keySet = customers.keySet();
            HashMap<Float, String> customersByAmount = new HashMap<Float, String>();
            for (Integer customerId : keySet) {
                Float amount = customers.get(customerId);
                if (customersByAmount.containsKey(amount)) {
                    String previous = customersByAmount.get(amount);
                    customersByAmount.put(amount, previous + "," + String.valueOf(customerId));
                } else {
                    customersByAmount.put(amount, String.valueOf(customerId));
                }
            }

            Set<Float> keySet2 = customersByAmount.keySet();
            String noti_type = "contest_ab";

            Session newSession2 = getNewSession();
            Transaction tx2 = null;
            try {

                tx2 = newSession2.beginTransaction();

                for (Float amount : keySet2) {

                    String customerIds = customersByAmount.get(amount);

                    JSONObject data = new JSONObject();
                    data.put("noti_type", noti_type);

                    String alertMessage = "Your entry fee has been refunded for the " + GlobalConstant.CURRENCY_SYMBOL + entryFees + " contest. The contest you joined had to be cancelled as required slots were not filled.";

                    if (fromMatchAB) {
                        alertMessage = "Your entry fee has been refunded for the " + GlobalConstant.CURRENCY_SYMBOL + entryFees + " contest. Due to " + matchName + " abandoned.";
                    }

                    if (fromClash) {
                        alertMessage = "PROTECTION AGAINST CLASH : Your entry fee has been refunded for the " + GlobalConstant.CURRENCY_SYMBOL + entryFees + " contest. The contest you joined was cancelled as all the teams joined were same.";
                    }

                    TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
                    tblBasketballLineupNotiCron.setIsSend("N");
                    tblBasketballLineupNotiCron.setMessage(data.toString());
                    tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
                    tblBasketballLineupNotiCron.setUserIds(customerIds);
                    tblBasketballLineupNotiCron.setNotiType(noti_type);
                    tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));

                    newSession2.save(tblBasketballLineupNotiCron);

                }
                tx2.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx2 != null) {
                    tx2.rollback();
                }
            } finally {
                newSession2.close();
            }

        }

        return output;

    }

    public HashMap<String, Object> compressContest(HashMap<String, Object> contest, boolean forTesting) {
        Session newSession = null;
        Transaction tx = null;
        HashMap<String, Object> output = new HashMap<String, Object>();
        try {
            int matchContestId = (int) contest.get("id");
            int total_team = (int) contest.get("totalTeam");
            int joined_teams = (int) contest.get("totalJoinedTeam");
            float contest_entry_fees = (float) contest.get("entryFees");
            float total_price = (float) contest.get("totalPrice");
            JSONObject contestJsonData = new JSONObject(contest.get("contestJson").toString());

            if (contest_entry_fees == 0 || total_price == 0) {
                output.put("totalPrice", total_price);
                output.put("contestJson", contestJsonData.toString());
                output.put("completed", true);
                return output;
            }

            float need_earning = total_team * contest_entry_fees;
            float prozepool_per = 0;
            if (need_earning > 0) {
                prozepool_per = (total_price / need_earning);
            }

            float total_distribute = joined_teams * contest_entry_fees;
            float updated_prize_pool = total_distribute * prozepool_per;
            updated_prize_pool = Util.numberFormate(updated_prize_pool, 2);
            updated_prize_pool = Float.parseFloat(String.valueOf(Math.floor(updated_prize_pool)));

            JSONArray per_min_p = contestJsonData.getJSONArray("per_min_p");
            JSONArray per_max_p = contestJsonData.getJSONArray("per_max_p");
            JSONArray per_price = contestJsonData.getJSONArray("per_price");
            JSONArray per_gadget = new JSONArray();
            if (contestJsonData.has("gadget")) {
                per_gadget = contestJsonData.getJSONArray("gadget");
            }

            int previousTotalWinners = Integer.parseInt(per_max_p.getString(per_max_p.length() - 1));
            float winnerPer = (((float) previousTotalWinners) / total_team);

            float newTotalWinnersByPer = joined_teams * winnerPer;
            newTotalWinnersByPer = Util.numberFormate(newTotalWinnersByPer, 2);
            int newTotalWinners = (int) Math.floor(newTotalWinnersByPer);
            if (newTotalWinners == 0) {
                newTotalWinners = 1;
            }

            JSONArray updatedPriceByRank = new JSONArray();
            for (int i = 0; i < per_min_p.length(); i++) {
                int minP = Integer.parseInt(per_min_p.getString(i));
                int maxP = Integer.parseInt(per_max_p.getString(i));
                float price = Float.parseFloat(per_price.getString(i));

                float totalPriceOnRank = ((maxP - minP) + 1) * price;

                float amountt_per = 0;
                if (total_price > 0) {
                    amountt_per = (totalPriceOnRank / total_price);
                }

                float new_price = updated_prize_pool * amountt_per;
                new_price = new_price / ((maxP - minP) + 1);
                for (int j = 0; j < ((maxP - minP) + 1); j++) {
                    updatedPriceByRank.put(new_price);
                }
            }

            JSONObject updatedPriceByNewWinners = new JSONObject();
            float extraAmountInAdminHand = 0;
            for (int i = 0; i < updatedPriceByRank.length(); i++) {
                float newPrice = updatedPriceByRank.getFloat(i);
                if (i + 1 > newTotalWinners) {
                    extraAmountInAdminHand += newPrice;
                } else {
                    updatedPriceByNewWinners.put(String.valueOf(i + 1), newPrice);
                }
            }

            float extraAmountForPerWinner = extraAmountInAdminHand / updatedPriceByNewWinners.length();
            extraAmountForPerWinner = Util.numberFormate(extraAmountForPerWinner, 2);

            JSONArray new_per_min_p = new JSONArray();
            JSONArray new_per_max_p = new JSONArray();
            JSONArray new_per_price = new JSONArray();
            JSONArray new_per_gadget = new JSONArray();

            float newPricePool = 0;
            int incrementRank = 1;
            float lastAmountInAdminHand = 0;
            for (int i = 0; i < per_min_p.length(); i++) {
                int minP = Integer.parseInt(per_min_p.getString(i));
                int maxP = Integer.parseInt(per_max_p.getString(i));
                float price = Float.parseFloat(per_price.getString(i));
                String gadget = "";
                if (!per_gadget.isEmpty()) {
                    gadget = per_gadget.getString(i);
                }

                if (minP > newTotalWinners) {
                    break;
                }

                if (maxP > newTotalWinners) {
                    maxP = newTotalWinners;
                }

                float totalPriceOnRankBlock = 0;
                for (int j = 0; j < ((maxP - minP) + 1); j++) {
                    float float1 = updatedPriceByNewWinners.getFloat(String.valueOf(incrementRank));
                    float1 += extraAmountForPerWinner;
                    totalPriceOnRankBlock += float1;
                    incrementRank++;
                }

                float priceForPerWinnerInBlock = totalPriceOnRankBlock / ((maxP - minP) + 1);
                float extraPricePerWinner = 0;
                if (priceForPerWinnerInBlock > price) {
                    extraPricePerWinner = priceForPerWinnerInBlock - price;
                    priceForPerWinnerInBlock = price;
                } else {
                    priceForPerWinnerInBlock = Float.parseFloat(String.valueOf(Math.round(priceForPerWinnerInBlock)));
                }

                if (!gadget.equals("") || priceForPerWinnerInBlock > 0) {
                    totalPriceOnRankBlock = priceForPerWinnerInBlock * ((maxP - minP) + 1);

                    lastAmountInAdminHand += extraPricePerWinner * ((maxP - minP) + 1);

                    new_per_min_p.put(String.valueOf(minP));
                    new_per_max_p.put(String.valueOf(maxP));
                    new_per_price.put(String.valueOf(priceForPerWinnerInBlock));
                    new_per_gadget.put(gadget);

                    newPricePool += totalPriceOnRankBlock;
                } else {
                    break;
                }
            }

            lastAmountInAdminHand = Math.round(lastAmountInAdminHand);
            if (lastAmountInAdminHand > 0) {
                int newLastRank = Integer.parseInt(new_per_max_p.getString(new_per_max_p.length() - 1));

                while (lastAmountInAdminHand > 0) {
                    float givenAmount = 0;
                    for (int i = 0; i < per_min_p.length(); i++) {
                        int minP = Integer.parseInt(per_min_p.getString(i));
                        int maxP = Integer.parseInt(per_max_p.getString(i));
                        float price = Float.parseFloat(per_price.getString(i));

                        if (minP <= (newLastRank + 1) && maxP >= (newLastRank + 1)) {
                            if (lastAmountInAdminHand >= price) {
                                givenAmount = price;
                            } else {
                                givenAmount = lastAmountInAdminHand;
                            }
                            break;
                        }
                    }
                    if (givenAmount == 0) {
                        break;
                    } else {
                        new_per_min_p.put(String.valueOf(newLastRank + 1));
                        new_per_max_p.put(String.valueOf(newLastRank + 1));
                        new_per_price.put(String.valueOf(givenAmount));
                        new_per_gadget.put("");
                        lastAmountInAdminHand -= givenAmount;
                        newPricePool += givenAmount;
                        newLastRank++;
                    }

                }

            }

            JSONObject newContestJsonData = new JSONObject();
            newContestJsonData.put("per_min_p", new_per_min_p);
            newContestJsonData.put("per_max_p", new_per_max_p);
            newContestJsonData.put("per_price", new_per_price);
            newContestJsonData.put("gadget", new_per_gadget);

            if (!forTesting) {
                newSession = getNewSession();
                tx = newSession.beginTransaction();

                String hqln = "UPDATE TblBasketballContestMatch tccm "
                        + "SET tccm.contestJson=:contestJson, tccm.oldContestJson=:oldContestJson, tccm.totalPrice=:totalPrice, tccm.oldTotalPrice=:oldTotalPrice "
                        + "WHERE tccm.id = :id";

                Query queryy = newSession.createQuery(hqln).setParameter("contestJson", newContestJsonData.toString())
                        .setParameter("oldContestJson", contestJsonData.toString())
                        .setParameter("totalPrice", newPricePool).setParameter("oldTotalPrice", total_price)
                        .setParameter("id", matchContestId);

                queryy.executeUpdate();

                tx.commit();
            }

            output.put("totalPrice", newPricePool);
            output.put("contestJson", newContestJsonData.toString());
            output.put("completed", true);

            return output;
        } catch (Exception e) {
            e.printStackTrace();
            output.clear();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (newSession != null) {
                newSession.close();
            }
        }
        return output;
    }

    public HashMap<String, Object> saveResultForContest(HashMap<String, Object> contest,
                                                        HashMap<String, Object> totalTaxPercent, TblTemplate winningTamplate) {
        String prizeBrackupData = (String) contest.get("contestJson");
        JSONObject prizeBrackup = new JSONObject(prizeBrackupData);
        JSONArray minRanks = prizeBrackup.getJSONArray("per_min_p");
        JSONArray maxRanks = prizeBrackup.getJSONArray("per_max_p");
        JSONArray rankPrice = prizeBrackup.getJSONArray("per_price");
        JSONArray totalGadget = prizeBrackup.has("gadget") ? prizeBrackup.getJSONArray("gadget") : null;
        int lastPosition = Integer.parseInt(maxRanks.get(maxRanks.length() - 1).toString());

        int matchContestId = (int) contest.get("id");
        int seriesId = (int) contest.get("seriesId");
        int matchUniqueId = (int) contest.get("matchUniqueId");
        String matchName = (String) contest.get("matchName");

        HashMap<String, Object> output = new HashMap<String, Object>();

        Session newSession = getNewSession();
        Transaction tx = null;
        try {

            tx = newSession.beginTransaction();

            String hqln = "SELECT " + "tccc.id as id, tccc.customerId as customerId, tccc.newRank as newRank, "
                    + "tc.firstname as firstname, tc.lastname as lastname, tc.email as email "
                    + "FROM TblBasketballCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON tc.id=tccc.customerId "
                    + "WHERE tccc.matchContestId=:matchContestId AND tccc.newRank<=:lastPosition "
                    + " ORDER BY tccc.newRank ASC";

            Query queryy = newSession.createQuery(hqln).setParameter("matchContestId", matchContestId)
                    .setParameter("lastPosition", lastPosition);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            LinkedHashMap<Integer, Object> blankArray = new LinkedHashMap<Integer, Object>();
            HashMap<Integer, Object> customersData = new HashMap<Integer, Object>();
            if (result.size() == 0) {

            } else {
                int currentTotalWinners = 0;

                for (HashMap<String, Object> hashMap : result) {

                    int id = (int) hashMap.get("id");
                    int authUserId = (int) hashMap.get("customerId");
                    int newRank = (int) hashMap.get("newRank");
                    String firstname = (String) hashMap.get("firstname");
                    String lastname = (String) hashMap.get("lastname");
                    String email = (String) hashMap.get("email");

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
                        customerIds.add(authUserId);

                        rankData.put("customerCount", customerIds.size());

                        if (!customersData.containsKey(authUserId)) {
                            HashMap<String, Object> customerData = new HashMap<String, Object>();
                            customerData.put("firstname", firstname);
                            customerData.put("lastname", lastname);
                            customerData.put("email", email);
                            customersData.put(authUserId, customerData);
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
                        customerIds.add(authUserId);

                        rankData.put("customerCount", customerIds.size());
                        rankData.put("amounts", amounts);
                        rankData.put("gadgets", gadgets);
                        rankData.put("customerIds", customerIds);

                        blankArray.put(newRank, rankData);

                        if (!customersData.containsKey(authUserId)) {
                            HashMap<String, Object> customerData = new HashMap<String, Object>();
                            customerData.put("firstname", firstname);
                            customerData.put("lastname", lastname);
                            customerData.put("email", email);
                            customersData.put(authUserId, customerData);
                        }
                    }

                }
            }

            Set<Integer> keySet = blankArray.keySet();
            Long resultTime = Util.getCurrentTime();
            for (Integer rank : keySet) {
                LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) blankArray.get(rank);
                List<Float> amounts = (List<Float>) rankData.get("amounts");
                List<String> gadgets = (List<String>) rankData.get("gadgets");
                List<Integer> customerIds = (List<Integer>) rankData.get("customerIds");

                float sum = 0;
                for (Float d : amounts) {
                    sum += d;
                }
                String totalGadgets = "";
                for (String d : gadgets) {
                    if (!totalGadgets.isEmpty()) {
                        totalGadgets += ",";
                    }
                    totalGadgets += d;
                }

                float update_amount = sum / amounts.size();
                update_amount = Util.numberFormate(update_amount, 2);
                float tax_amount = 0;
                float tax_percent = 0;
                JSONArray tax_json_data = new JSONArray();

                if (update_amount > 10000) {
                    List<HashMap<String, Object>> taxes = (List<HashMap<String, Object>>) totalTaxPercent.get("taxes");

                    if (taxes.size() > 0) {
                        for (HashMap<String, Object> tax : taxes) {
                            JSONObject data = new JSONObject();
                            data.put("name", tax.get("name"));
                            data.put("value", tax.get("value"));
                            float calculatedTaxAmount = (((float) tax.get("value")) / 100) * update_amount;
                            calculatedTaxAmount = Util.numberFormate(calculatedTaxAmount, 2);
                            data.put("amount", calculatedTaxAmount);
                            tax_amount += calculatedTaxAmount;
                            tax_percent += (float) tax.get("value");

                            tax_json_data.put(data);
                        }
                        update_amount = update_amount - tax_amount;
                    }
                }

                rankData.put("winningAmount", update_amount);
                rankData.put("taxAmount", tax_amount);
                rankData.put("taxPercent", tax_percent);
                rankData.put("totalGadgets", totalGadgets);

                String hqln1 = "UPDATE TblBasketballCustomerContest tccc "
                        + "SET tccc.winAmount=:winAmount, tccc.taxAmount=:taxAmount, tccc.taxPercent=:taxPercent, tccc.taxJson=:taxJson, tccc.winGadget=:totalGadgets "
                        + "WHERE tccc.newRank = :newRank AND tccc.matchContestId=:matchContestId";

                Query queryy1 = newSession.createQuery(hqln1).setParameter("winAmount", update_amount)
                        .setParameter("taxAmount", tax_amount).setParameter("taxPercent", tax_percent)
                        .setParameter("taxJson", tax_json_data.toString())
                        .setParameter("totalGadgets", totalGadgets)
                        .setParameter("newRank", rank)
                        .setParameter("matchContestId", matchContestId);

                queryy1.executeUpdate();

                for (Integer customer : customerIds) {
                    insertWinContestInWallethistory(newSession, seriesId, matchUniqueId, matchContestId, customer,
                            update_amount, tax_amount, tax_percent, rank, totalGadgets, resultTime);

                }

            }

            hqln = "UPDATE TblBasketballContestMatch tccm " + "SET tccm.isResult='Y' " + "WHERE tccm.id = :id";

            queryy = newSession.createQuery(hqln).setParameter("id", matchContestId);

            queryy.executeUpdate();

            tx.commit();
            output.put("customers", blankArray);
            output.put("customersData", customersData);
            output.put("completed", true);
        } catch (Exception e) {
            e.printStackTrace();
            output.clear();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        if (!output.isEmpty()) {

            Long currentTime = Util.getCurrentTime();

            HashMap<Integer, Object> customers = (HashMap<Integer, Object>) output.get("customers");
            HashMap<Integer, Object> customersData = (HashMap<Integer, Object>) output.get("customersData");

            Set<Integer> keySet2 = customers.keySet();
            String noti_type = "win_contest";

            Session newSession2 = getNewSession();
            Transaction tx2 = null;
            try {

                tx2 = newSession2.beginTransaction();

                for (Integer rank : keySet2) {

                    LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) customers.get(rank);

                    List<Integer> customerIds = (List<Integer>) rankData.get("customerIds");
                    float winningAmount = (float) rankData.get("winningAmount");
                    float taxAmount = (float) rankData.get("taxAmount");
                    float taxPercent = (float) rankData.get("taxPercent");
                    String totalGadgets = (String) rankData.get("totalGadgets");

                    String customerIdsString = "";
                    for (Integer customer : customerIds) {
                        if (!customerIdsString.isEmpty()) {
                            customerIdsString += ",";
                        }
                        customerIdsString += String.valueOf(customer);
                    }

                    JSONObject data = new JSONObject();
                    data.put("noti_type", noti_type);

                    String alertMessage = "";
                    String winAmountMail = "";
                    if (winningAmount > 0) {
                        String ga_des = "";
                        if (!Util.isEmpty(totalGadgets)) {
                            ga_des = " and " + totalGadgets;
                        }
                        alertMessage = "Congratulations! You have won " + GlobalConstant.CURRENCY_SYMBOL
                                + winningAmount + ga_des + "  in the " + matchName;

                        winAmountMail = GlobalConstant.CURRENCY_SYMBOL + winningAmount + ga_des;

                        if (taxAmount > 0) {
                            alertMessage = "Congratulations! You have won " + GlobalConstant.CURRENCY_SYMBOL + winningAmount
                                    + ga_des + ", after deducting " + taxPercent + "% tax from your winning amount in the "
                                    + matchName + " TNC apply.";
                        }

                    } else if (!Util.isEmpty(totalGadgets)) {
                        alertMessage = "Congratulations! You have won "
                                + totalGadgets + "  in the " + matchName;

                        winAmountMail = totalGadgets;

                        if (taxAmount > 0) {
                            alertMessage = "Congratulations! You have won " + totalGadgets
                                    + ", after deducting " + taxPercent + "% tax from your winning amount in the "
                                    + matchName + " TNC apply.";
                        }
                    }

                    if (alertMessage.isEmpty()) {
                        continue;
                    }


                    TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
                    tblBasketballLineupNotiCron.setIsSend("N");
                    tblBasketballLineupNotiCron.setMessage(data.toString());
                    tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
                    tblBasketballLineupNotiCron.setUserIds(customerIdsString);
                    tblBasketballLineupNotiCron.setNotiType(noti_type);
                    tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));

                    newSession2.save(tblBasketballLineupNotiCron);

                    if (winningTamplate != null) {

                        String subject = winningTamplate.getSubject();

                        for (Integer customer : customerIds) {

                            if (customersData.containsKey(customer)) {

                                HashMap<String, Object> object = (HashMap<String, Object>) customersData.get(customer);

                                String firstname = (String) object.get("firstname");
                                String lastname = (String) object.get("lastname");
                                String email = (String) object.get("email");
                                String fullName = firstname;
                                if (!Util.isEmpty(lastname)) {
                                    fullName += " " + lastname;
                                }

                                String content = winningTamplate.getContent();

                                content = content.replace("{WINNING_AMOUNT}", winAmountMail);

                                TblBasketballEmailCron tblBasketballEmailCron = TblBasketballEmailCron.getInstance();

                                tblBasketballEmailCron.setSubject(subject);
                                tblBasketballEmailCron.setMessage(content);
                                tblBasketballEmailCron.setToemail(email);
                                tblBasketballEmailCron.setToname(fullName);
                                tblBasketballEmailCron.setEmailType("WIN_CONTEST");

                                newSession2.save(tblBasketballEmailCron);

                            }

                        }

                    }

                }
                tx2.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx2 != null) {
                    tx2.rollback();
                }
            } finally {
                newSession2.close();
            }

        }

        return output;

    }

    public HashMap<String, Object> saveResultForSeriesLeaderboard(TblBasketballSeries tblBasketballSeries,
                                                                  HashMap<String, Object> totalTaxPercent, TblTemplate winningTamplate) {
        String prizeBrackupData = tblBasketballSeries.getPrizeJson();
        JSONObject prizeBrackup = new JSONObject(prizeBrackupData);
        JSONArray minRanks = prizeBrackup.getJSONArray("per_min_p");
        JSONArray maxRanks = prizeBrackup.getJSONArray("per_max_p");
        JSONArray rankPrice = prizeBrackup.getJSONArray("per_price");
        JSONArray totalGadget = prizeBrackup.has("gadget") ? prizeBrackup.getJSONArray("gadget") : null;
        int lastPosition = Integer.parseInt(maxRanks.get(maxRanks.length() - 1).toString());

        int seriesId = tblBasketballSeries.getId();

        HashMap<String, Object> output = new HashMap<String, Object>();

        Session newSession = getNewSession();
        Transaction tx = null;
        try {

            tx = newSession.beginTransaction();

            String hqln = "SELECT " + "tcls.id as id, tcls.customerId as customerId, tcls.newRank as newRank, "
                    + "tc.firstname as firstname, tc.lastname as lastname, tc.email as email "
                    + "FROM TblBasketballLeaderboardSeries tcls " + "LEFT JOIN TblCustomer tc ON tc.id=tcls.customerId "
                    + "WHERE tcls.seriesId=:seriesId AND tcls.newRank<=:lastPosition "
                    + " ORDER BY tcls.newRank ASC";

            Query queryy = newSession.createQuery(hqln).setParameter("seriesId", tblBasketballSeries.getId())
                    .setParameter("lastPosition", lastPosition);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            LinkedHashMap<Integer, Object> blankArray = new LinkedHashMap<Integer, Object>();
            HashMap<Integer, Object> customersData = new HashMap<Integer, Object>();
            if (result.size() == 0) {

            } else {
                int currentTotalWinners = 0;

                for (HashMap<String, Object> hashMap : result) {

                    int id = (int) hashMap.get("id");
                    int authUserId = (int) hashMap.get("customerId");
                    int newRank = (int) hashMap.get("newRank");
                    String firstname = (String) hashMap.get("firstname");
                    String lastname = (String) hashMap.get("lastname");
                    String email = (String) hashMap.get("email");

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
                        customerIds.add(authUserId);

                        rankData.put("customerCount", customerIds.size());

                        if (!customersData.containsKey(authUserId)) {
                            HashMap<String, Object> customerData = new HashMap<String, Object>();
                            customerData.put("firstname", firstname);
                            customerData.put("lastname", lastname);
                            customerData.put("email", email);
                            customersData.put(authUserId, customerData);
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
                        customerIds.add(authUserId);

                        rankData.put("customerCount", customerIds.size());
                        rankData.put("amounts", amounts);
                        rankData.put("gadgets", gadgets);
                        rankData.put("customerIds", customerIds);

                        blankArray.put(newRank, rankData);

                        if (!customersData.containsKey(authUserId)) {
                            HashMap<String, Object> customerData = new HashMap<String, Object>();
                            customerData.put("firstname", firstname);
                            customerData.put("lastname", lastname);
                            customerData.put("email", email);
                            customersData.put(authUserId, customerData);
                        }
                    }

                }
            }

            Set<Integer> keySet = blankArray.keySet();
            Long resultTime = Util.getCurrentTime();
            for (Integer rank : keySet) {
                LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) blankArray.get(rank);
                List<Float> amounts = (List<Float>) rankData.get("amounts");
                List<String> gadgets = (List<String>) rankData.get("gadgets");
                List<Integer> customerIds = (List<Integer>) rankData.get("customerIds");

                float sum = 0;
                for (Float d : amounts) {
                    sum += d;
                }
                String totalGadgets = "";
                for (String d : gadgets) {
                    if (!totalGadgets.isEmpty()) {
                        totalGadgets += ",";
                    }
                    totalGadgets += d;
                }

                float update_amount = sum / amounts.size();
                update_amount = Util.numberFormate(update_amount, 2);
                float tax_amount = 0;
                float tax_percent = 0;
                JSONArray tax_json_data = new JSONArray();

                if (update_amount > 10000) {
                    List<HashMap<String, Object>> taxes = (List<HashMap<String, Object>>) totalTaxPercent.get("taxes");

                    if (taxes.size() > 0) {
                        for (HashMap<String, Object> tax : taxes) {
                            JSONObject data = new JSONObject();
                            data.put("name", tax.get("name"));
                            data.put("value", tax.get("value"));
                            float calculatedTaxAmount = (((float) tax.get("value")) / 100) * update_amount;
                            calculatedTaxAmount = Util.numberFormate(calculatedTaxAmount, 2);
                            data.put("amount", calculatedTaxAmount);
                            tax_amount += calculatedTaxAmount;
                            tax_percent += (float) tax.get("value");

                            tax_json_data.put(data);
                        }
                        update_amount = update_amount - tax_amount;
                    }
                }

                rankData.put("winningAmount", update_amount);
                rankData.put("taxAmount", tax_amount);
                rankData.put("taxPercent", tax_percent);
                rankData.put("totalGadgets", totalGadgets);

                String hqln1 = "UPDATE TblBasketballLeaderboardSeries tcls "
                        + "SET tcls.winAmount=:winAmount, tcls.taxAmount=:taxAmount, tcls.taxPercent=:taxPercent, tcls.taxJson=:taxJson, tcls.winGadget=:totalGadgets "
                        + "WHERE tcls.newRank = :newRank AND tcls.seriesId=:seriesId";

                Query queryy1 = newSession.createQuery(hqln1).setParameter("winAmount", update_amount)
                        .setParameter("taxAmount", tax_amount).setParameter("taxPercent", tax_percent)
                        .setParameter("taxJson", tax_json_data.toString())
                        .setParameter("totalGadgets", totalGadgets)
                        .setParameter("newRank", rank)
                        .setParameter("seriesId", tblBasketballSeries.getId());

                queryy1.executeUpdate();

                for (Integer customer : customerIds) {
                    insertWinSeriesLeaderboardInWalletHistory(newSession, seriesId, customer,
                            update_amount, tax_amount, tax_percent, rank, totalGadgets, resultTime);
                }

            }

            hqln = "UPDATE TblBasketballSeries tcs "
                    + "SET tcs.isResultDeclared='Y' "
                    + "WHERE tcs.id = :id";

            queryy = newSession.createQuery(hqln).setParameter("id", tblBasketballSeries.getId());

            queryy.executeUpdate();

            tx.commit();
            output.put("customers", blankArray);
            output.put("customersData", customersData);
            output.put("completed", true);
        } catch (Exception e) {
            e.printStackTrace();
            output.clear();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }



        if (!output.isEmpty()) {

            Long currentTime = Util.getCurrentTime();

            HashMap<Integer, Object> customers = (HashMap<Integer, Object>) output.get("customers");
            HashMap<Integer, Object> customersData = (HashMap<Integer, Object>) output.get("customersData");

            Set<Integer> keySet2 = customers.keySet();
            String noti_type = "win_leaderboard";

            Session newSession2 = getNewSession();
            Transaction tx2 = null;
            try {

                tx2 = newSession2.beginTransaction();

                for (Integer rank : keySet2) {

                    LinkedHashMap<String, Object> rankData = (LinkedHashMap<String, Object>) customers.get(rank);

                    List<Integer> customerIds = (List<Integer>) rankData.get("customerIds");
                    float winningAmount = (float) rankData.get("winningAmount");
                    float taxAmount = (float) rankData.get("taxAmount");
                    float taxPercent = (float) rankData.get("taxPercent");
                    String totalGadgets = (String) rankData.get("totalGadgets");

                    String customerIdsString = "";
                    for (Integer customer : customerIds) {
                        if (!customerIdsString.isEmpty()) {
                            customerIdsString += ",";
                        }
                        customerIdsString += String.valueOf(customer);
                    }

                    JSONObject data = new JSONObject();
                    data.put("noti_type", noti_type);

                    String alertMessage = "";
                    String winAmountMail = "";
                    if (winningAmount > 0) {
                        String ga_des = "";
                        if (!Util.isEmpty(totalGadgets)) {
                            ga_des = " / " + totalGadgets;
                        }
                        alertMessage = "Congratulations! You have won " + GlobalConstant.CURRENCY_SYMBOL
                                + winningAmount + ga_des + "  in the leaderboard " + tblBasketballSeries.getName();

                        winAmountMail = GlobalConstant.CURRENCY_SYMBOL + winningAmount + ga_des;

                        if (taxAmount > 0) {
                            alertMessage = "Congratulations! You have won " + GlobalConstant.CURRENCY_SYMBOL + winningAmount
                                    + ga_des + ", after deducting " + taxPercent + "% tax from your winning amount in the leaderboard "
                                    + tblBasketballSeries.getName() + " TNC apply.";
                        }

                    } else if (!Util.isEmpty(totalGadgets)) {
                        alertMessage = "Congratulations! You have won "
                                + totalGadgets + "  in the leaderboard " + tblBasketballSeries.getName();

                        winAmountMail = totalGadgets;

                        if (taxAmount > 0) {
                            alertMessage = "Congratulations! You have won " + totalGadgets
                                    + ", after deducting " + taxPercent + "% tax from your winning amount in the leaderboard "
                                    + tblBasketballSeries.getName() + " TNC apply.";
                        }
                    }

                    if (alertMessage.isEmpty()) {
                        continue;
                    }


                    TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
                    tblBasketballLineupNotiCron.setIsSend("N");
                    tblBasketballLineupNotiCron.setMessage(data.toString());
                    tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
                    tblBasketballLineupNotiCron.setUserIds(customerIdsString);
                    tblBasketballLineupNotiCron.setNotiType(noti_type);
                    tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));

                    newSession2.save(tblBasketballLineupNotiCron);

                    if (winningTamplate != null) {

                        String subject = winningTamplate.getSubject();

                        for (Integer customer : customerIds) {

                            if (customersData.containsKey(customer)) {

                                HashMap<String, Object> object = (HashMap<String, Object>) customersData.get(customer);

                                String firstname = (String) object.get("firstname");
                                String lastname = (String) object.get("lastname");
                                String email = (String) object.get("email");
                                String fullName = firstname;
                                if (!Util.isEmpty(lastname)) {
                                    fullName += " " + lastname;
                                }

                                String content = winningTamplate.getContent();

                                content = content.replace("{WINNING_AMOUNT}", winAmountMail);

                                TblBasketballEmailCron tblBasketballEmailCron = TblBasketballEmailCron.getInstance();

                                tblBasketballEmailCron.setSubject(subject);
                                tblBasketballEmailCron.setMessage(content);
                                tblBasketballEmailCron.setToemail(email);
                                tblBasketballEmailCron.setToname(fullName);
                                tblBasketballEmailCron.setEmailType("WIN_LEADERBOARD");

                                newSession2.save(tblBasketballEmailCron);

                            }

                        }

                    }

                }
                tx2.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx2 != null) {
                    tx2.rollback();
                }
            } finally {
                newSession2.close();
            }

        }

        return output;

    }

    @Transactional
    private void insertReferralCashBonusInWallethistory(Session newSession, int seriesId, int matchUniqueId,
                                                        int matchContestId, int parentCustomerId, int customerId, float amount, String type) {

        if (amount <= 0) {
            return;
        }
        Long currentTime = Util.getCurrentTime();
        if (type.equals("BONUS")) {
            String description = parentCustomerId + " Received Referral cash bonus amount " + amount;
            String transaction_id = "REFCBWALL" + currentTime + parentCustomerId + customerId + matchContestId;
            String walletName = GlobalConstant.WALLET_TYPE.get("bonus_wallet");

            String hqln = "SELECT bonusWallet as bonusWallet " + "FROM TblCustomer where id=:id";
            Query queryy = newSession.createQuery(hqln).setParameter("id", parentCustomerId);
            HashMap<String, Object> result = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            float bonusWallet = (float) result.get("bonusWallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(parentCustomerId);
            tblCustomerWalletHistory.setTeamId(0);
            tblCustomerWalletHistory.setPreviousAmount(bonusWallet);
            tblCustomerWalletHistory.setAmount(amount);
            tblCustomerWalletHistory.setCurrentAmount(bonusWallet + amount);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_RECEIVED_REFCB");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setRefrenceId(String.valueOf(customerId));
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
            newSession.save(tblCustomerWalletHistory);

            if (amount > 0) {
                String hqlll = "UPDATE " + "TblCustomer " + "SET bonusWallet=bonusWallet+:amount " + "where "
                        + "id=:customerId";
                Query queryyyy = newSession.createQuery(hqlll).setParameter("amount", amount).setParameter("customerId",
                        parentCustomerId);
                queryyyy.executeUpdate();

                String hqlll1 = "UPDATE " + "TblCustomer " + "SET usedRefferalGivenAmount=usedRefferalGivenAmount+:amount1 "
                        + "where " + "id=:customerId1";
                Query queryyyy1 = newSession.createQuery(hqlll1).setParameter("amount1", amount).setParameter("customerId1",
                        customerId);
                queryyyy1.executeUpdate();
            }
        } else if (type.equals("DEPOSIT")) {
            String description = parentCustomerId + " Received Referral deposit amount " + amount;
            String transaction_id = "REFCDWALL" + currentTime + parentCustomerId + customerId + matchContestId;
            String walletName = GlobalConstant.WALLET_TYPE.get("deposit_wallet");

            String hqln = "SELECT depositWallet as depositWallet " + "FROM TblCustomer where id=:id";
            Query queryy = newSession.createQuery(hqln).setParameter("id", parentCustomerId);
            HashMap<String, Object> result = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            float depositWallet = (float) result.get("depositWallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(parentCustomerId);
            tblCustomerWalletHistory.setTeamId(0);
            tblCustomerWalletHistory.setPreviousAmount(depositWallet);
            tblCustomerWalletHistory.setAmount(amount);
            tblCustomerWalletHistory.setCurrentAmount(depositWallet + amount);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_RECEIVED_REFCB");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setRefrenceId(String.valueOf(customerId));
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
            newSession.save(tblCustomerWalletHistory);

            if (amount > 0) {
                String hqlll = "UPDATE " + "TblCustomer " + "SET depositWallet=depositWallet+:amount " + "where "
                        + "id=:customerId";
                Query queryyyy = newSession.createQuery(hqlll).setParameter("amount", amount).setParameter("customerId",
                        parentCustomerId);
                queryyyy.executeUpdate();

                String hqlll1 = "UPDATE " + "TblCustomer " + "SET usedRefferalGivenAmountDeposit=usedRefferalGivenAmountDeposit+:amount1 "
                        + "where " + "id=:customerId1";
                Query queryyyy1 = newSession.createQuery(hqlll1).setParameter("amount1", amount).setParameter("customerId1",
                        customerId);
                queryyyy1.executeUpdate();
            }
        }
    }

    @Transactional
    private void insertWinContestInWallethistory(Session newSession, int seriesId, int matchUniqueId,
                                                 int matchContestId, int customerId, float amount, float taxAmount, float taxPercent, int rank, String totalGadgets, long resultTime) {

        Long currentTime = Util.getCurrentTime();
        String description = "";
        if (amount > 0) {
            String ga_des = "";
            if (!Util.isEmpty(totalGadgets)) {
                ga_des = " and " + totalGadgets;
            }

            description = customerId + " Win amount " + amount + ga_des + " on match_contest_id " + matchContestId
                    + " With Rank " + rank;

            if (taxAmount > 0) {
                description = customerId + " Win amount " + amount + ga_des + " after deducting " + taxPercent
                        + "% tax on match_contest_id " + matchContestId + " With Rank " + rank;
            }

            String transaction_id = "WINWALL" + resultTime + matchContestId + customerId;

            String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

            String hqln = "SELECT winningWallet as winningWallet " + "FROM TblCustomer where id=:id";
            Query queryy = newSession.createQuery(hqln).setParameter("id", customerId);
            HashMap<String, Object> result = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            float winningWallet = (float) result.get("winningWallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(0);
            tblCustomerWalletHistory.setPreviousAmount(winningWallet);
            tblCustomerWalletHistory.setAmount(amount);
            tblCustomerWalletHistory.setCurrentAmount(winningWallet + amount);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_WIN_CONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
            newSession.save(tblCustomerWalletHistory);

            if (amount > 0) {
                String hqlll = "UPDATE " + "TblCustomer " + "SET winningWallet=winningWallet+:amount " + "where "
                        + "id=:customerId";
                Query queryyyy = newSession.createQuery(hqlll).setParameter("amount", amount).setParameter("customerId",
                        customerId);
                queryyyy.executeUpdate();
            }
        } else if (!Util.isEmpty(totalGadgets)) {


        }
    }

    @Transactional
    private void insertWinSeriesLeaderboardInWalletHistory(Session newSession, int seriesId, int customerId, float amount, float taxAmount, float taxPercent, int rank, String totalGadgets, long resultTime) {

        Long currentTime = Util.getCurrentTime();
        String description = "";
        if (amount > 0) {
            String ga_des = "";
            if (!Util.isEmpty(totalGadgets)) {
                ga_des = " / " + totalGadgets;
            }

            description = customerId + " Win amount " + amount + ga_des + " on series_id " + seriesId
                    + " With Rank " + rank;

            if (taxAmount > 0) {
                description = customerId + " Win amount " + amount + ga_des + " after deducting " + taxPercent
                        + "% tax on series_id " + seriesId + " With Rank " + rank;
            }

            String transaction_id = "WINSWALL" + resultTime + seriesId + customerId;

            String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

            String hqln = "SELECT winningWallet as winningWallet " + "FROM TblCustomer where id=:id";
            Query queryy = newSession.createQuery(hqln).setParameter("id", customerId);
            HashMap<String, Object> result = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            float winningWallet = (float) result.get("winningWallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(0);
            tblCustomerWalletHistory.setMatchContestId(0);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(0);
            tblCustomerWalletHistory.setPreviousAmount(winningWallet);
            tblCustomerWalletHistory.setAmount(amount);
            tblCustomerWalletHistory.setCurrentAmount(winningWallet + amount);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_WIN_LEADERBOARD");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
            newSession.save(tblCustomerWalletHistory);

            if (amount > 0) {
                String hqlll = "UPDATE " + "TblCustomer " + "SET winningWallet=winningWallet+:amount " + "where "
                        + "id=:customerId";
                Query queryyyy = newSession.createQuery(hqlll).setParameter("amount", amount).setParameter("customerId",
                        customerId);
                queryyyy.executeUpdate();
            }
        } else if (!Util.isEmpty(totalGadgets)) {


        }
    }

    @Transactional
    private LinkedHashMap<String, Object> insertAbondantContestInWalletHistory(Session newSession, int seriesId,
                                                                               int matchUniqueId, int matchContestId, int customerId, int teamId,
                                                                               LinkedHashMap<String, Object> extraData) {

        Long time = Util.getCurrentTime();

        String description = customerId + " Abodent contest refund on match_contest_id " + matchContestId;
        String transaction_id = "RABCWALL" + time + customerId + "_" + matchContestId + "_" + teamId;

        float usedBonus = (float) extraData.get("usedBonus");
        float usedDeposit = (float) extraData.get("usedDeposit");
        float usedWinning = (float) extraData.get("usedWinning");

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("description", description);
        data.put("transaction_id", transaction_id);

        if (usedBonus == 0 && usedDeposit == 0 && usedWinning == 0) {
            return data;
        }

        String hqln = "SELECT winningWallet as winningWallet, depositWallet as depositWallet, bonusWallet as bonusWallet, pendingWidWallet as pendingWidWallet "
                + "FROM TblCustomer where id=:id";
        Query queryy = newSession.createQuery(hqln).setParameter("id", customerId);
        HashMap<String, Object> result = (HashMap<String, Object>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        float bonusWallet = (float) result.get("bonusWallet");
        float depositWallet = (float) result.get("depositWallet");
        float winningWallet = (float) result.get("winningWallet");

        if (usedBonus > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("bonus_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(bonusWallet);
            tblCustomerWalletHistory.setAmount(usedBonus);
            tblCustomerWalletHistory.setCurrentAmount(bonusWallet + usedBonus);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_REFUND_ABCONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedDeposit > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("deposit_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(depositWallet);
            tblCustomerWalletHistory.setAmount(usedDeposit);
            tblCustomerWalletHistory.setCurrentAmount(depositWallet + usedDeposit);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_REFUND_ABCONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedWinning > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(winningWallet);
            tblCustomerWalletHistory.setAmount(usedWinning);
            tblCustomerWalletHistory.setCurrentAmount(winningWallet + usedWinning);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("CREDIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_REFUND_ABCONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedBonus > 0 || usedDeposit > 0 || usedWinning > 0) {
            String hqlll = "UPDATE " + "TblCustomer "
                    + "SET bonusWallet=bonusWallet+:usedBonus, depositWallet=depositWallet+:usedDeposit, winningWallet=winningWallet+:usedWinning "
                    + "where " + "id=:customerId";
            Query queryyyy = newSession.createQuery(hqlll).setParameter("usedBonus", usedBonus)
                    .setParameter("usedDeposit", usedDeposit).setParameter("usedWinning", usedWinning)
                    .setParameter("customerId", customerId);
            queryyyy.executeUpdate();
        }

        return data;

    }

    public boolean saveOrUpdatePlayerStats(int matchUniqueId, JSONObject players,
                                           LinkedHashMap<String, TblBasketballMatchPlayersStat> playersStatsForMatchUniqueId,
                                           JSONObject playersForMatchUniqueId) {

        boolean needUpdateRanking = false;
        Long currentTime = Util.getCurrentTime();

        Session newSession = getNewSession();

        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();

            Set<String> keySet = players.keySet();
            for (String playerId : keySet) {
                if (playersForMatchUniqueId.has(playerId)) {
                    JSONObject playerData = players.getJSONObject(playerId);
                    float totalPoints = playerData.getFloat("total_points");
                    TblBasketballMatchPlayersStat tblBasketballMatchPlayersStat = null;
                    boolean needUpdateInsert = false;
                    if (playersStatsForMatchUniqueId.containsKey(playerId)) {
                        tblBasketballMatchPlayersStat = playersStatsForMatchUniqueId.get(playerId);
                        boolean checkSameStats = tblBasketballMatchPlayersStat.checkSameStats(playerData);
                        if (!checkSameStats) {
                            needUpdateInsert = true;
                            tblBasketballMatchPlayersStat.updateStatsData(playerData);
                            tblBasketballMatchPlayersStat.setUpdated(BigInteger.valueOf(currentTime));
                        }
                    } else {
                        needUpdateInsert = true;
                        tblBasketballMatchPlayersStat = new TblBasketballMatchPlayersStat();
                        tblBasketballMatchPlayersStat.setId(0);
                        tblBasketballMatchPlayersStat.setMatchUniqueId(matchUniqueId);
                        tblBasketballMatchPlayersStat.setPlayerUniqueId(Integer.parseInt(playerId));
                        tblBasketballMatchPlayersStat.setUpdated(BigInteger.valueOf(currentTime));
                        tblBasketballMatchPlayersStat.updateStatsData(playerData);
                    }

                    if (needUpdateInsert) {
                        if (tblBasketballMatchPlayersStat.getId() == 0) {
                            newSession.save(tblBasketballMatchPlayersStat);
                        } else {
                            newSession.update(tblBasketballMatchPlayersStat);
                        }

                        needUpdateRanking = true;

                        String hqlll = "UPDATE " + "TblBasketballMatchPlayer " + "SET points=:totalPoints " + "where "
                                + "matchUniqueId=:matchUniqueId AND playerUniqueId=:playerUniqueId";
                        Query queryyyy = newSession.createQuery(hqlll).setParameter("totalPoints", totalPoints)
                                .setParameter("matchUniqueId", matchUniqueId)
                                .setParameter("playerUniqueId", Integer.parseInt(playerId));
                        queryyyy.executeUpdate();
                    }
                    playersForMatchUniqueId.getJSONObject(playerId).put("points", totalPoints);
                }
            }

            Set<String> keySet2 = playersStatsForMatchUniqueId.keySet();
            List<Integer> needRemovePlayers = new ArrayList<Integer>();
            for (String playerId : keySet2) {
                if (!players.has(playerId)) {
                    needRemovePlayers.add(Integer.parseInt(playerId));
                }
            }

            if (!needRemovePlayers.isEmpty()) {
                String hqlll = "DELETE FROM " + "TblBasketballMatchPlayersStat " + "WHERE "
                        + "matchUniqueId=:matchUniqueId AND playerUniqueId IN(:playerUniqueId)";
                Query queryyyy = newSession.createQuery(hqlll).setParameter("matchUniqueId", matchUniqueId)
                        .setParameterList("playerUniqueId", needRemovePlayers);
                queryyyy.executeUpdate();

                float points = 0;
                hqlll = "UPDATE " + "TblBasketballMatchPlayer " + "SET points=:points " + "where "
                        + "matchUniqueId=:matchUniqueId AND playerUniqueId IN(:playerUniqueId)";
                queryyyy = newSession.createQuery(hqlll).setParameter("points", points)
                        .setParameter("matchUniqueId", matchUniqueId)
                        .setParameterList("playerUniqueId", needRemovePlayers);
                queryyyy.executeUpdate();

            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            needUpdateRanking = false;
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        return needUpdateRanking;

    }

    public void updateMatchContestRanking(HashMap<String, Object> contest, JSONObject playersForMatchUniqueId) {

        int matchContestId = (int) contest.get("id");

        Session newSession = getNewSession();

        Transaction tx = null;

        String hqln = "SELECT "
                + "tccc.id as id, tccc.customerTeamId as customerTeamId, tccc.oldPoints as oldPoints, tccc.newPoints as newPoints, tccc.oldRank as oldRank, tccc.newRank as newRank, "
                + "tcct.playerUniqueIds as playerUniqueIds, tcct.playerMultiplers as playerMultiplers "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballCustomerTeam tcct ON tcct.id = tccc.customerTeamId "
                + "WHERE tccc.matchContestId = :matchContestId";

        Query queryy = newSession.createQuery(hqln).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() > 0) {

            List<JSONObject> customerTeams = new ArrayList<JSONObject>();

            for (HashMap<String, Object> hashMap : result) {

                String playerUniqueIds = (String) hashMap.get("playerUniqueIds");
                String playerMultiplers = (String) hashMap.get("playerMultiplers");

                String[] split = playerUniqueIds.split(",");
                String[] split2 = playerMultiplers.split(",");

                float teamTotalPoints = 0;
                for (int i = 0; i < split.length; i++) {
                    String playerId = split[i];
                    float multiple = Float.parseFloat(split2[i].trim());
                    if (playersForMatchUniqueId.has(playerId)) {
                        float playerPoints = playersForMatchUniqueId.getJSONObject(playerId).optFloat("points", 0f);

                        teamTotalPoints += (playerPoints * multiple);
                    }
                }
                teamTotalPoints=Util.numberFormate(teamTotalPoints,2);
                hashMap.put("teamTotalPoints", teamTotalPoints);
                customerTeams.add(new JSONObject(hashMap));
            }

            Collections.sort(customerTeams, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject a, JSONObject b) {

                    float valA = a.getFloat("teamTotalPoints");
                    float valB = b.getFloat("teamTotalPoints");

                    if (valA == valB) {
                        return 0;
                    } else if (valA < valB) {
                        return 1;
                    } else {
                        return -1;
                    }

                }
            });

            try {
                tx = newSession.beginTransaction();
                HashMap<Float, Integer> pointsWiseRank = new HashMap<Float, Integer>();
                for (int i = 0; i < customerTeams.size(); i++) {
                    int newRank = i + 1;
                    JSONObject jsonObject = customerTeams.get(i);
                    int rowId = jsonObject.getInt("id");
                    int previousOldRank = jsonObject.getInt("oldRank");
                    int previousNewRank = jsonObject.getInt("newRank");
                    float previousOldPoints = jsonObject.getFloat("oldPoints");
                    float previousNewPoints = jsonObject.getFloat("newPoints");
                    float newPoints = jsonObject.getFloat("teamTotalPoints");

                    int newRankForCustomer = newRank;
                    if (pointsWiseRank.containsKey(newPoints)) {
                        newRankForCustomer = pointsWiseRank.get(newPoints);
                    } else {
                        pointsWiseRank.put(newPoints, newRank);
                    }

                    int oldRank = previousOldRank;
                    if (previousOldRank == 0) {
                        oldRank = newRankForCustomer;
                    } else if (previousNewRank != newRankForCustomer) {
                        oldRank = previousNewRank;
                    }

                    float oldPoints = previousOldPoints;
                    if (previousOldPoints == 0) {
                        oldPoints = newPoints;
                    } else if (previousNewPoints != newPoints) {
                        oldPoints = previousNewPoints;
                    }

                    String hqlll = "UPDATE " + "TblBasketballCustomerContest "
                            + "SET oldRank=:oldRank, newRank=:newRank, oldPoints=:oldPoints, newPoints=:newPoints "
                            + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll).setParameter("oldRank", oldRank)
                            .setParameter("newRank", newRankForCustomer).setParameter("oldPoints", oldPoints)
                            .setParameter("newPoints", newPoints).setParameter("id", rowId);
                    queryyyy.executeUpdate();

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

    public boolean startReferralDistributeForMatch(TblBasketballMatch matchDataOnly,
                                                   HashMap<String, String> referCashbonus) {

        boolean isAllDistributed = true;
        int matchUniqueId = matchDataOnly.getUniqueId();
        Session newSession = getNewSession();

        String hqln = "SELECT "
                + "tccc.matchContestId as matchContestId, tccc.customerId as customerId, tccc.entryFees as entryFees, "
                + "tc.usedReferralUserId as usedReferralUserId, tc.usedRefferalAmount as usedRefferalAmount,tc.usedRefferalAmountDeposit as usedRefferalAmountDeposit, "
                + "tc1.isAffiliate as isAffiliate "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballContestMatch tccm ON (tccc.matchContestId = tccm.id) "
                + "LEFT JOIN TblCustomer tc ON (tccc.customerId = tc.id) "
                + "LEFT JOIN TblCustomer tc1 ON (tc1.id = tc.usedReferralUserId) "
                + "WHERE tccc.entryFees > 0 AND tccc.matchUniqueId = :matchUniqueId AND tccm.isAbondant='N' AND tc.usedReferralUserId > 0 AND tc.usedRefferalAmount > tc.usedRefferalGivenAmount ";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() > 0) {
            HashMap<Float, String> customers = new HashMap<Float, String>();
            Transaction tx = null;
            try {
                for (HashMap<String, Object> contestData : result) {
                    String isAffiliate = (String) contestData.get("isAffiliate");
                    if (isAffiliate.equals("1")) {
                        continue;
                    }

                    int matchContestId = (int) contestData.get("matchContestId");
                    int customerId = (int) contestData.get("customerId");
                    int usedReferralUserId = (int) contestData.get("usedReferralUserId");
                    float usedRefferalAmount = (float) contestData.get("usedRefferalAmount");
                    float usedRefferalAmountDeposit = (float) contestData.get("usedRefferalAmountDeposit");
                    float entryFees = (float) contestData.get("entryFees");
                    if (entryFees <= 0) {
                        continue;
                    }


                    String hqln1 = "SELECT " + "tc.usedRefferalGivenAmount as usedRefferalGivenAmount,tc.usedRefferalGivenAmountDeposit as usedRefferalGivenAmountDeposit "
                            + "FROM TblCustomer tc " + "WHERE tc.id = :id";
                    Query queryy1 = newSession.createQuery(hqln1).setParameter("id", customerId);
                    List<HashMap<String, Object>> result1 = (List<HashMap<String, Object>>) queryy1
                            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                    if (result1.size() > 0) {
                        HashMap<String, Object> customerData = result1.get(0);

                        float usedRefferalGivenAmount = (float) customerData.get("usedRefferalGivenAmount");
                        float usedRefferalGivenAmountDeposit = (float) customerData.get("usedRefferalGivenAmountDeposit");

                        if (usedRefferalGivenAmount < usedRefferalAmount) {

                            float amount = 0;

                            if (referCashbonus.get("CONTEST_BASED").toString().equals("N")) {
                                amount = usedRefferalAmount - usedRefferalGivenAmount;
                            } else {
                                float percent = Float
                                        .parseFloat(referCashbonus.get("PERCENTAGE_OF_ENTRY_FEES").toString());

                                float remaining_amount = usedRefferalAmount - usedRefferalGivenAmount;
                                float percent_amount = (percent / 100) * entryFees;

                                if (percent_amount > remaining_amount) {
                                    amount = remaining_amount;
                                } else {
                                    amount = percent_amount;
                                }
                            }

                            if (amount > 0) {
                                if (tx == null) {
                                    tx = newSession.beginTransaction();
                                }
                                amount = Util.numberFormate(amount, 2);

                                insertReferralCashBonusInWallethistory(newSession, matchDataOnly.getSeriesId(),
                                        matchUniqueId, matchContestId, usedReferralUserId, customerId, amount, "BONUS");
                                if (customers.containsKey(amount)) {
                                    String previousUsers = customers.get(amount);
                                    customers.put(amount, previousUsers + "," + String.valueOf(usedReferralUserId));
                                } else {
                                    customers.put(amount, String.valueOf(usedReferralUserId));
                                }
                            }

                        }

                        if (usedRefferalGivenAmountDeposit < usedRefferalAmountDeposit) {

                            float amount = 0;

                            if (referCashbonus.get("CONTEST_BASED_DEPOSIT").toString().equals("N")) {
                                amount = usedRefferalAmountDeposit - usedRefferalGivenAmountDeposit;
                            } else {
                                float percent = Float
                                        .parseFloat(referCashbonus.get("PERCENTAGE_OF_ENTRY_FEES_DEPOSIT").toString());

                                float remaining_amount = usedRefferalAmountDeposit - usedRefferalGivenAmountDeposit;
                                float percent_amount = (percent / 100) * entryFees;

                                if (percent_amount > remaining_amount) {
                                    amount = remaining_amount;
                                } else {
                                    amount = percent_amount;
                                }
                            }

                            if (amount > 0) {
                                if (tx == null) {
                                    tx = newSession.beginTransaction();
                                }
                                amount = Util.numberFormate(amount, 2);

                                insertReferralCashBonusInWallethistory(newSession, matchDataOnly.getSeriesId(),
                                        matchUniqueId, matchContestId, usedReferralUserId, customerId, amount, "DEPOSIT");
                                if (customers.containsKey(amount)) {
                                    String previousUsers = customers.get(amount);
                                    customers.put(amount, previousUsers + "," + String.valueOf(usedReferralUserId));
                                } else {
                                    customers.put(amount, String.valueOf(usedReferralUserId));
                                }
                            }

                        }
                    }
                }
                if (tx != null) {
                    tx.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                customers.clear();
                isAllDistributed = false;
                if (tx != null) {
                    tx.rollback();
                }
            } finally {
                newSession.close();
            }

            if (!customers.isEmpty()) {

                Session newSession2 = getNewSession();
                Transaction tx2 = null;
                try {
                    tx2 = newSession2.beginTransaction();

                    String noti_type = "referral_cash_bonus";
                    Long currentTime = Util.getCurrentTime();
                    for (Float amount : customers.keySet()) {
                        String customerIds = customers.get(amount);
                        JSONObject data = new JSONObject();
                        data.put("noti_type", noti_type);
                        String alertMessage = "Woohoo! Go for glory with your " + GlobalConstant.CURRENCY_SYMBOL
                                + amount + " Cash Bonus.";

                        TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
                        tblBasketballLineupNotiCron.setIsSend("N");
                        tblBasketballLineupNotiCron.setMessage(data.toString());
                        tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
                        tblBasketballLineupNotiCron.setUserIds(customerIds);
                        tblBasketballLineupNotiCron.setNotiType(noti_type);
                        tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));
                        newSession2.save(tblBasketballLineupNotiCron);
                    }
                    tx2.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (tx2 != null) {
                        tx2.rollback();
                    }
                } finally {
                    newSession2.close();
                }
            }

        }

        return isAllDistributed;

    }

    public boolean startAffiliateDistributeForMatch(TblBasketballMatch matchDataOnly) {
        boolean isAllDistributed = true;
        int matchUniqueId = matchDataOnly.getUniqueId();
        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();
            settledAffiliateAmountOnCustomerContest(newSession, matchUniqueId);

            String hqln = "SELECT "
                    + "tca.id as id, tca.affiliatePercent as affiliatePercent, tca.customerId as customerId "
                    + "FROM TblBasketballAffiliate tca "
                    + "WHERE tca.isAffiliateDistribute = 'N' AND tca.matchUniqueId=:matchUniqueId "
                    + "ORDER BY tca.id ASC";

            Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            if (result.size() == 0) {
                tx.commit();
                return isAllDistributed;
            }

            LinkedHashMap<Integer, Object> settledAmountFromCustomerContest = getSettledAmountFromCustomerContest(
                    newSession, matchUniqueId);

            for (HashMap<String, Object> hashMap : result) {
                int affiliate_id = (int) hashMap.get("id");
                int customerId = (int) hashMap.get("customerId");
                float affiliatePercent = (float) hashMap.get("affiliatePercent");
                if (affiliatePercent == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();
                    continue;
                }
                String hqln1 = "SELECT " + "tc.id as id " + "FROM TblCustomer tc "
                        + "WHERE tc.isDeleted = 'N' AND tc.status='A' AND tc.usedReferralUserId=:usedReferralUserId";

                Query queryy1 = newSession.createQuery(hqln1).setParameter("usedReferralUserId", customerId);

                List<HashMap<String, Object>> result1 = (List<HashMap<String, Object>>) queryy1
                        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                if (result1.size() == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();

                    continue;
                }
                List<Integer> myChilds = new ArrayList<Integer>();
                for (HashMap<String, Object> customer : result1) {
                    myChilds.add((int) customer.get("id"));
                }

                String hqln2 = "SELECT "
                        + "count(tccc.id) as joinedteams, tccc.matchContestId as matchContestId, SUM(tccc.cashBonusWallet) as cashBonusWallet, SUM(tccc.depositeWallet) as depositeWallet, SUM(tccc.winningWallet) as winningWallet, SUM(tccc.givenByUserAmount) as givenByUserAmount, SUM(tccc.givenByAdminAmount) as givenByAdminAmount, "
                        + "tccm.contestJson as contestJson, tccm.isAffiliateEarnAllow as isAffiliateEarnAllow "
                        + "FROM TblBasketballCustomerContest tccc "
                        + "LEFT JOIN TblBasketballContestMatch tccm ON tccm.id=tccc.matchContestId "
                        + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccc.customerId IN(:myChilds) AND tccm.isAbondant='N' AND tccm.isDeleted='N' "
                        + "GROUP BY tccc.matchContestId";

                Query queryy2 = newSession.createQuery(hqln2).setParameter("matchUniqueId", matchUniqueId)
                        .setParameterList("myChilds", myChilds);

                List<HashMap<String, Object>> result2 = (List<HashMap<String, Object>>) queryy2
                        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                if (result2.size() == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();

                    continue;
                }

                int contest_count = result2.size();
                int joined_team = 0;
                float deposit_amount = 0;
                float winning_amount = 0;
                float bonus_amount = 0;
                float admin_commission_amount = 0;
                float earning = 0;

                for (HashMap<String, Object> contestData : result2) {
                    JSONObject jsonObject1 = new JSONObject(contestData.get("contestJson").toString());

                    String isAffiliateEarnAllow = contestData.get("isAffiliateEarnAllow").toString();

                    boolean isGadgetContest = false;
                    if (isAffiliateEarnAllow.equals("N")) {
                        isGadgetContest = true;
                    }
                    if (!isGadgetContest) {
                        if (jsonObject1.has("gadget")) {
                            JSONArray gadgetArray = jsonObject1.getJSONArray("gadget");
                            for (Object o : gadgetArray) {
                                if (!Util.isEmpty((String) o)) {
                                    isGadgetContest = true;
                                    break;
                                }
                            }
                        }
                    }


                    int match_contest_id = (int) contestData.get("matchContestId");

                    int contestJoinedTeam = Integer.parseInt(String.valueOf(contestData.get("joinedteams")));
                    float contestdeposite_wallet = Float.parseFloat(String.valueOf(contestData.get("depositeWallet")));
                    float contestwinning_wallet = Float.parseFloat(String.valueOf(contestData.get("winningWallet")));
                    float contestcash_bonus_wallet = Float
                            .parseFloat(String.valueOf(contestData.get("cashBonusWallet")));
                    float given_by_user_amount = Float.parseFloat(String.valueOf(contestData.get("givenByUserAmount")));
                    float given_by_admin_amount = Float
                            .parseFloat(String.valueOf(contestData.get("givenByAdminAmount")));
                    float contest_admin_commission_amount = 0;
                    float contest_earning = 0;

                    joined_team += contestJoinedTeam;
                    deposit_amount += contestdeposite_wallet;
                    winning_amount += contestwinning_wallet;
                    bonus_amount += contestcash_bonus_wallet;

                    HashMap<String, Object> contestDataMain = (HashMap<String, Object>) settledAmountFromCustomerContest
                            .get(match_contest_id);

                    float total_given_by_user_amount = Float
                            .parseFloat(String.valueOf(contestDataMain.get("givenByUserAmount")));
                    float total_given_by_admin_amount = Float
                            .parseFloat(String.valueOf(contestDataMain.get("givenByAdminAmount")));
                    float total_Recived = (float) contestDataMain.get("totalRealMoneyReceived");
                    float total_win_amount = (float) contestDataMain.get("totalWinningDistributed");

                    float total_given_by_admin_and_user_amount = total_given_by_user_amount
                            + total_given_by_admin_amount;

                    if (!isGadgetContest && total_Recived > 0 && (total_win_amount) < total_Recived) {

                        float totalCommOfAdmin = total_Recived - total_win_amount;

                        float adminwinnigComm = 0;
                        if ((given_by_user_amount - given_by_admin_amount) > 0) {
                            adminwinnigComm = given_by_user_amount / total_given_by_admin_and_user_amount;
                        }

                        float WinningcOMM = totalCommOfAdmin * adminwinnigComm;
                        contest_admin_commission_amount = WinningcOMM;
                        admin_commission_amount += WinningcOMM;

                        float YouEarnedContest = (WinningcOMM * affiliatePercent * 0.01f);
                        contest_earning = YouEarnedContest;
                        earning += YouEarnedContest;
                    }
                    saveOrUpdateAffiliateContest(newSession, contestJoinedTeam, contestdeposite_wallet,
                            contestwinning_wallet, contestcash_bonus_wallet, contest_admin_commission_amount,
                            contest_earning, affiliatePercent, given_by_user_amount, given_by_admin_amount,
                            total_given_by_admin_and_user_amount, matchUniqueId, match_contest_id, customerId);

                }
                String isAffiliateDistribute = "Y";
                String hqlll = "UPDATE " + "TblBasketballAffiliate "
                        + "SET isAffiliateDistribute=:isAffiliateDistribute, contestCount=:contest_count, joinedTeam=:joined_team,  adminCommissionAmount=:admin_commission_amount, bonusAmount=:bonus_amount, depositAmount=:deposit_amount, earning=:earning, winningAmount=:winning_amount "
                        + "WHERE " + "id=:affiliate_id";
                Query queryyyy = newSession.createQuery(hqlll)
                        .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                        .setParameter("contest_count", contest_count).setParameter("joined_team", joined_team)
                        .setParameter("admin_commission_amount", admin_commission_amount)
                        .setParameter("bonus_amount", bonus_amount).setParameter("deposit_amount", deposit_amount)
                        .setParameter("earning", earning).setParameter("winning_amount", winning_amount)
                        .setParameter("affiliate_id", affiliate_id);
                queryyyy.executeUpdate();

            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            isAllDistributed = false;
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        return isAllDistributed;
    }

    public boolean startCustomerAffiliateDistributeForMatch(TblBasketballMatch matchDataOnly) {
        boolean isAllDistributed = true;
        int seriesId = matchDataOnly.getSeriesId();
        int matchUniqueId = matchDataOnly.getUniqueId();
        Session newSession = getNewSession();
        Transaction tx = null;
        HashMap<Float, String> customersEariningData = new HashMap<Float, String>();
        try {

            String hqln = "SELECT "
                    + "tca.id as id, tca.affiliatePercent as affiliatePercent, tca.customerId as customerId "
                    + "FROM TblBasketballCustomerAffiliate tca "
                    + "WHERE tca.isAffiliateDistribute = 'N' AND tca.matchUniqueId=:matchUniqueId "
                    + "ORDER BY tca.id ASC";

            Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if (result.size() == 0) {
                return isAllDistributed;
            }

            tx = newSession.beginTransaction();

            LinkedHashMap<Integer, Object> settledAmountFromCustomerContest = getSettledAmountFromCustomerContest(
                    newSession, matchUniqueId);

            for (HashMap<String, Object> hashMap : result) {
                int affiliate_id = (int) hashMap.get("id");
                int customerId = (int) hashMap.get("customerId");
                float affiliatePercent = (float) hashMap.get("affiliatePercent");
                if (affiliatePercent == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballCustomerAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();
                    continue;
                }
                String hqln1 = "SELECT " + "tc.id as id " + "FROM TblCustomer tc "
                        + "WHERE tc.isDeleted = 'N' AND tc.status='A' AND tc.usedReferralUserId=:usedReferralUserId";

                Query queryy1 = newSession.createQuery(hqln1).setParameter("usedReferralUserId", customerId);

                List<HashMap<String, Object>> result1 = (List<HashMap<String, Object>>) queryy1
                        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                if (result1.size() == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballCustomerAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();

                    continue;
                }
                List<Integer> myChilds = new ArrayList<Integer>();
                for (HashMap<String, Object> customer : result1) {
                    myChilds.add((int) customer.get("id"));
                }

                String hqln2 = "SELECT "
                        + "count(tccc.id) as joinedteams, tccc.matchContestId as matchContestId, SUM(tccc.cashBonusWallet) as cashBonusWallet, SUM(tccc.depositeWallet) as depositeWallet, SUM(tccc.winningWallet) as winningWallet, SUM(tccc.givenByUserAmount) as givenByUserAmount, SUM(tccc.givenByAdminAmount) as givenByAdminAmount, "
                        + "tccm.contestJson as contestJson "
                        + "FROM TblBasketballCustomerContest tccc "
                        + "LEFT JOIN TblBasketballContestMatch tccm ON tccm.id=tccc.matchContestId "
                        + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccc.customerId IN(:myChilds) AND tccm.isAbondant='N' AND tccm.isDeleted='N' "
                        + "GROUP BY tccc.matchContestId";

                Query queryy2 = newSession.createQuery(hqln2).setParameter("matchUniqueId", matchUniqueId)
                        .setParameterList("myChilds", myChilds);

                List<HashMap<String, Object>> result2 = (List<HashMap<String, Object>>) queryy2
                        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
                if (result2.size() == 0) {
                    String isAffiliateDistribute = "Y";
                    String hqlll = "UPDATE " + "TblBasketballCustomerAffiliate "
                            + "SET isAffiliateDistribute=:isAffiliateDistribute " + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll)
                            .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                            .setParameter("id", affiliate_id);
                    queryyyy.executeUpdate();

                    continue;
                }

                int contest_count = result2.size();
                int joined_team = 0;
                float deposit_amount = 0;
                float winning_amount = 0;
                float bonus_amount = 0;
                float admin_commission_amount = 0;
                float earning = 0;

                for (HashMap<String, Object> contestData : result2) {
                    JSONObject jsonObject1 = new JSONObject(contestData.get("contestJson").toString());

                    boolean isGadgetContest = false;
                    if (jsonObject1.has("gadget")) {
                        JSONArray gadgetArray = jsonObject1.getJSONArray("gadget");
                        for (Object o : gadgetArray) {
                            if (!Util.isEmpty((String) o)) {
                                isGadgetContest = true;
                                break;
                            }
                        }
                    }

                    int match_contest_id = (int) contestData.get("matchContestId");

                    int contestJoinedTeam = Integer.parseInt(String.valueOf(contestData.get("joinedteams")));
                    float contestdeposite_wallet = Float.parseFloat(String.valueOf(contestData.get("depositeWallet")));
                    float contestwinning_wallet = Float.parseFloat(String.valueOf(contestData.get("winningWallet")));
                    float contestcash_bonus_wallet = Float
                            .parseFloat(String.valueOf(contestData.get("cashBonusWallet")));
                    float given_by_user_amount = Float.parseFloat(String.valueOf(contestData.get("givenByUserAmount")));
                    float given_by_admin_amount = Float
                            .parseFloat(String.valueOf(contestData.get("givenByAdminAmount")));
                    float contest_admin_commission_amount = 0;
                    float contest_earning = 0;

                    joined_team += contestJoinedTeam;
                    deposit_amount += contestdeposite_wallet;
                    winning_amount += contestwinning_wallet;
                    bonus_amount += contestcash_bonus_wallet;

                    HashMap<String, Object> contestDataMain = (HashMap<String, Object>) settledAmountFromCustomerContest
                            .get(match_contest_id);

                    float total_given_by_user_amount = Float
                            .parseFloat(String.valueOf(contestDataMain.get("givenByUserAmount")));
                    float total_given_by_admin_amount = Float
                            .parseFloat(String.valueOf(contestDataMain.get("givenByAdminAmount")));
                    float total_Recived = (float) contestDataMain.get("totalRealMoneyReceived");
                    float total_win_amount = (float) contestDataMain.get("totalWinningDistributed");

                    float total_given_by_admin_and_user_amount = total_given_by_user_amount
                            + total_given_by_admin_amount;

                    if (!isGadgetContest && total_Recived > 0 && (total_win_amount) < total_Recived) {

                        float totalCommOfAdmin = total_Recived - total_win_amount;

                        float adminwinnigComm = 0;
                        if ((given_by_user_amount - given_by_admin_amount) > 0) {
                            adminwinnigComm = given_by_user_amount / total_given_by_admin_and_user_amount;
                        }

                        float WinningcOMM = totalCommOfAdmin * adminwinnigComm;
                        contest_admin_commission_amount = WinningcOMM;
                        admin_commission_amount += WinningcOMM;

                        float YouEarnedContest = (WinningcOMM * affiliatePercent * 0.01f);
                        contest_earning = YouEarnedContest;
                        earning += YouEarnedContest;
                    }
                    saveOrUpdateCustomerAffiliateContest(newSession, contestJoinedTeam, contestdeposite_wallet,
                            contestwinning_wallet, contestcash_bonus_wallet, contest_admin_commission_amount,
                            contest_earning, affiliatePercent, given_by_user_amount, given_by_admin_amount,
                            total_given_by_admin_and_user_amount, matchUniqueId, match_contest_id, customerId);

                }
                String isAffiliateDistribute = "Y";
                String hqlll = "UPDATE " + "TblBasketballCustomerAffiliate "
                        + "SET isAffiliateDistribute=:isAffiliateDistribute, contestCount=:contest_count, joinedTeam=:joined_team,  adminCommissionAmount=:admin_commission_amount, bonusAmount=:bonus_amount, depositAmount=:deposit_amount, earning=:earning, winningAmount=:winning_amount "
                        + "WHERE " + "id=:affiliate_id";
                Query queryyyy = newSession.createQuery(hqlll)
                        .setParameter("isAffiliateDistribute", isAffiliateDistribute)
                        .setParameter("contest_count", contest_count).setParameter("joined_team", joined_team)
                        .setParameter("admin_commission_amount", admin_commission_amount)
                        .setParameter("bonus_amount", bonus_amount).setParameter("deposit_amount", deposit_amount)
                        .setParameter("earning", earning).setParameter("winning_amount", winning_amount)
                        .setParameter("affiliate_id", affiliate_id);
                queryyyy.executeUpdate();

                insertCustomerAffiliatInWallethistory(newSession, seriesId, matchUniqueId, customerId, earning);

                String customersString = "";
                if (customersEariningData.containsKey(earning)) {
                    customersString = customersEariningData.get(earning);
                    customersString += "," + String.valueOf(customerId);
                } else {
                    customersString = String.valueOf(customerId);
                }
                customersEariningData.put(earning, customersString);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            isAllDistributed = false;
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        if (isAllDistributed && !customersEariningData.isEmpty()) {
            String noti_type = "mr_received";

            Session newSession2 = getNewSession();
            Transaction tx2 = null;
            try {
                Long currentTime = Util.getCurrentTime();
                tx2 = newSession2.beginTransaction();
                Set<Float> keySet = customersEariningData.keySet();
                for (Float amount : keySet) {

                    String customerIds = customersEariningData.get(amount);

                    JSONObject data = new JSONObject();
                    data.put("noti_type", noti_type);

                    String alertMessage = "Congratulations! You have received referral earning " + GlobalConstant.CURRENCY_SYMBOL + amount + " in the " + matchDataOnly.getName() + ".";

                    TblBasketballLineupNotiCron tblBasketballLineupNotiCron = new TblBasketballLineupNotiCron();
                    tblBasketballLineupNotiCron.setIsSend("N");
                    tblBasketballLineupNotiCron.setMessage(data.toString());
                    tblBasketballLineupNotiCron.setAlertMessage(alertMessage);
                    tblBasketballLineupNotiCron.setUserIds(customerIds);
                    tblBasketballLineupNotiCron.setNotiType(noti_type);
                    tblBasketballLineupNotiCron.setCreated(BigInteger.valueOf(currentTime));

                    newSession2.save(tblBasketballLineupNotiCron);

                }
                tx2.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx2 != null) {
                    tx2.rollback();
                }
            } finally {
                newSession2.close();
            }
        }

        return isAllDistributed;
    }

    @Transactional
    private void insertCustomerAffiliatInWallethistory(Session newSession, int seriesId, int matchUniqueId,
                                                       int customerId, float amount) {

        Long currentTime = Util.getCurrentTime();
        String description = customerId + " received referral earning " + amount + " on match " + matchUniqueId;

        String transaction_id = "WINWALLMR" + currentTime + customerId;

        String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

        String hqln = "SELECT winningWallet as winningWallet " + "FROM TblCustomer where id=:id";
        Query queryy = newSession.createQuery(hqln).setParameter("id", customerId);
        HashMap<String, Object> result = (HashMap<String, Object>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        float winningWallet = (float) result.get("winningWallet");

        TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
        tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_BASKETBALL);
        tblCustomerWalletHistory.setSeriesId(seriesId);
        tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
        tblCustomerWalletHistory.setMatchContestId(0);
        tblCustomerWalletHistory.setCustomerId(customerId);
        tblCustomerWalletHistory.setTeamId(0);
        tblCustomerWalletHistory.setPreviousAmount(winningWallet);
        tblCustomerWalletHistory.setAmount(amount);
        tblCustomerWalletHistory.setCurrentAmount(winningWallet + amount);
        tblCustomerWalletHistory.setWalletType(walletName);
        tblCustomerWalletHistory.setTransactionType("CREDIT");
        tblCustomerWalletHistory.setTransactionId(transaction_id);
        tblCustomerWalletHistory.setType("CUSTOMER_MR_RECEIVED");
        tblCustomerWalletHistory.setDescription(description);
        tblCustomerWalletHistory.setCreated(BigInteger.valueOf(currentTime));
        newSession.save(tblCustomerWalletHistory);

        if (amount > 0) {
            String hqlll = "UPDATE " + "TblCustomer " + "SET winningWallet=winningWallet+:amount " + "where "
                    + "id=:customerId";
            Query queryyyy = newSession.createQuery(hqlll).setParameter("amount", amount).setParameter("customerId",
                    customerId);
            queryyyy.executeUpdate();
        }
    }

    public boolean startLeaderboardCreatingForMatch(TblBasketballMatch matchDataOnly) {
        boolean isAllDistributed = true;

        long matchDate = matchDataOnly.getMatchDate().longValue();

        List<HashMap<String, Object>> generateWeeks = Util.generateWeeks(matchDate, matchDate);
        String weekNo = (String) generateWeeks.get(0).get("searchdate");

        int matchUniqueId = matchDataOnly.getUniqueId();
        Session newSession = getNewSession();

//        String hqln1 = "SELECT " + "tcsl.categoryId as categoryId "
//                + "FROM TblBasketballSeriesLeaderboard tcsl "
//                + "WHERE tcsl.seriesId =:matchSeriesId";
//
//        Query queryy1 = newSession.createQuery(hqln1).setParameter("matchSeriesId", matchDataOnly.getSeriesId());
//
//        List<HashMap<String, Object>> result1 = (List<HashMap<String, Object>>) queryy1
//                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//        if (result1.size() == 0) {
//            newSession.close();
//            return isAllDistributed;
//        }
//        List<Integer> leaderboardCategories = new ArrayList<Integer>();
//        for (HashMap<String, Object> cat : result1) {
//            leaderboardCategories.add((int) cat.get("categoryId"));
//        }

        String hqln = "SELECT "
                + "tccc.customerId as customerId, tccc.customerTeamId as customerTeamId, tccc.newPoints as newPoints "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballContestMatch tccm on tccm.id=tccc.matchContestId "
                + "WHERE tccc.isAbondant = 'N' AND tccc.matchUniqueId=:matchUniqueId AND tccm.isLeaderBoardAvaliable = 'Y'"
                + "ORDER BY tccc.newPoints DESC";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() == 0) {
            newSession.close();
            return isAllDistributed;
        }

        Transaction tx = null;

        try {
            Long currentTime = Util.getCurrentTime();

            String params = "";
            int newRank = 1;
            LinkedHashMap<Integer, Object> matchLeaderBoardData = new LinkedHashMap<Integer, Object>();
            LinkedHashMap<Integer, Object> matchLeaderBoardDataWeek = new LinkedHashMap<Integer, Object>();
            for (HashMap<String, Object> hashMap : result) {

                int customerId = (int) hashMap.get("customerId");
                if (matchLeaderBoardData.containsKey(customerId)) {
                    continue;
                }
                int customerTeamId = (int) hashMap.get("customerTeamId");
                float newPoints = (float) hashMap.get("newPoints");
                if (!params.isEmpty()) {
                    params += ",";
                }
                params += "(" + matchDataOnly.getId() + "," + matchDataOnly.getSeriesId() + "," + matchUniqueId + ","
                        + customerId + "," + customerTeamId + "," + newRank + "," + newPoints + "," + currentTime + ","
                        + currentTime + ",'" + weekNo + "'," + matchDate + ")";

                HashMap<String, Object> customerData = new HashMap<String, Object>();
                customerData.put("customerId", customerId);
                customerData.put("newPoint", newPoints);

                matchLeaderBoardData.put(customerId, customerData);
                matchLeaderBoardDataWeek.put(customerId, customerData);

                newRank++;
            }
            tx = newSession.beginTransaction();

            String mysqlQuery = "INSERT " + "INTO tbl_basketball_leaderboard_matches "
                    + "(match_id,series_id,match_unique_id,customer_id,customer_team_id,new_rank,new_point,created_at,updated_at,week_no,match_date)"
                    + " VALUES " + params;

            newSession.createSQLQuery(mysqlQuery).executeUpdate();

            hqln = "SELECT "
                    + "tcls.id as id, tcls.customerId as customerId, tcls.newPoint as newPoint, tcls.oldPoint as oldPoint, tcls.newRank as newRank, tcls.oldRank as oldRank "
                    + "FROM TblBasketballLeaderboardSeries tcls " + "WHERE tcls.seriesId = :seriesId ";

            queryy = newSession.createQuery(hqln).setParameter("seriesId", matchDataOnly.getSeriesId());

            List<HashMap<String, Object>> resultSeries = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            hqln = "SELECT "
                    + "tcls.id as id, tcls.customerId as customerId, tcls.newPoint as newPoint, tcls.oldPoint as oldPoint, tcls.newRank as newRank, tcls.oldRank as oldRank "
                    + "FROM TblBasketballLeaderboardSeriesWeek tcls "
                    + "WHERE tcls.seriesId = :seriesId AND tcls.weekNo = :weekNo";

            queryy = newSession.createQuery(hqln).setParameter("seriesId", matchDataOnly.getSeriesId())
                    .setParameter("weekNo", weekNo);

            List<HashMap<String, Object>> resultSeriesWeek = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            List<JSONObject> customerSeriesData = new ArrayList<JSONObject>();
            for (HashMap<String, Object> data : resultSeries) {
                int customeridSeries = (int) data.get("customerId");
                float newPoint = (float) data.get("newPoint");
                if (matchLeaderBoardData.containsKey(customeridSeries)) {
                    HashMap<String, Object> object = (HashMap<String, Object>) matchLeaderBoardData
                            .get(customeridSeries);

                    data.put("oldNewPoint", newPoint);
                    data.put("newPoint", newPoint + (float) object.get("newPoint"));
                    customerSeriesData.add(new JSONObject(data));
                    matchLeaderBoardData.remove(customeridSeries);
                } else {
                    data.put("oldNewPoint", newPoint);
                    data.put("newPoint", newPoint);
                    customerSeriesData.add(new JSONObject(data));
                }
            }

            List<JSONObject> customerSeriesDataWeek = new ArrayList<JSONObject>();
            for (HashMap<String, Object> data : resultSeriesWeek) {
                int customeridSeries = (int) data.get("customerId");
                float newPoint = (float) data.get("newPoint");
                if (matchLeaderBoardDataWeek.containsKey(customeridSeries)) {
                    HashMap<String, Object> object = (HashMap<String, Object>) matchLeaderBoardDataWeek
                            .get(customeridSeries);

                    data.put("oldNewPoint", newPoint);
                    data.put("newPoint", newPoint + (float) object.get("newPoint"));
                    customerSeriesDataWeek.add(new JSONObject(data));
                    matchLeaderBoardDataWeek.remove(customeridSeries);
                } else {
                    data.put("oldNewPoint", newPoint);
                    data.put("newPoint", newPoint);
                    customerSeriesDataWeek.add(new JSONObject(data));
                }
            }

            if (!matchLeaderBoardData.isEmpty()) {
                Set<Integer> keySet = matchLeaderBoardData.keySet();

                for (Integer integer : keySet) {
                    customerSeriesData.add(new JSONObject((HashMap<String, Object>) matchLeaderBoardData.get(integer)));
                }
            }

            if (!matchLeaderBoardDataWeek.isEmpty()) {
                Set<Integer> keySet = matchLeaderBoardDataWeek.keySet();

                for (Integer integer : keySet) {
                    customerSeriesDataWeek
                            .add(new JSONObject((HashMap<String, Object>) matchLeaderBoardDataWeek.get(integer)));
                }
            }

            Collections.sort(customerSeriesData, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject a, JSONObject b) {

                    float valA = a.getFloat("newPoint");
                    float valB = b.getFloat("newPoint");

                    if (valA == valB) {
                        return 0;
                    } else if (valA < valB) {
                        return 1;
                    } else {
                        return -1;
                    }

                }
            });

            Collections.sort(customerSeriesDataWeek, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject a, JSONObject b) {

                    float valA = a.getFloat("newPoint");
                    float valB = b.getFloat("newPoint");

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
            for (JSONObject jsonObject : customerSeriesData) {

                int customerId = jsonObject.getInt("customerId");
                float newPoint = jsonObject.getFloat("newPoint");

                if (!jsonObject.has("id")) {
                    if (!paramsSeries.isEmpty()) {
                        paramsSeries += ",";
                    }
                    paramsSeries += "(" + matchDataOnly.getSeriesId() + "," + customerId + "," + newRankSeries + ","
                            + newRankSeries + "," + newPoint + "," + newPoint + "," + currentTime + "," + currentTime
                            + ")";
                } else {
                    int rowId = jsonObject.getInt("id");
                    int previousOldRank = jsonObject.getInt("oldRank");
                    int previousNewRank = jsonObject.getInt("newRank");
                    float previousOldPoints = jsonObject.getFloat("oldPoint");
                    float previousNewPoints = jsonObject.getFloat("oldNewPoint");
                    float newPoints = jsonObject.getFloat("newPoint");

                    int oldRank = previousOldRank;
                    if (previousOldRank == 0) {
                        oldRank = newRank;
                    } else if (previousNewRank != newRankSeries) {
                        oldRank = previousNewRank;
                    }

                    float oldPoints = previousOldPoints;
                    if (previousOldPoints == 0) {
                        oldPoints = newPoints;
                    } else if (previousNewPoints != newPoints) {
                        oldPoints = previousNewPoints;
                    }

                    String hqlll = "UPDATE " + "TblBasketballLeaderboardSeries "
                            + "SET oldRank=:oldRank, newRank=:newRank, oldPoint=:oldPoints, newPoint=:newPoints, updatedAt=:updatedAt  "
                            + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll).setParameter("oldRank", oldRank)
                            .setParameter("newRank", newRankSeries).setParameter("oldPoints", oldPoints)
                            .setParameter("newPoints", newPoints)
                            .setParameter("updatedAt", BigInteger.valueOf(currentTime)).setParameter("id", rowId);
                    queryyyy.executeUpdate();
                }

                newRankSeries++;

            }

            if (!paramsSeries.isEmpty()) {
                String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_leaderboard_series "
                        + "(series_id,customer_id,old_rank,new_rank,old_point,new_point,created_at,updated_at)"
                        + " VALUES " + paramsSeries;

                newSession.createSQLQuery(mysqlQuery1).executeUpdate();
            }

            String paramsSeriesWeek = "";
            int newRankSeriesWeek = 1;
            for (JSONObject jsonObject : customerSeriesDataWeek) {

                int customerId = jsonObject.getInt("customerId");
                float newPoint = jsonObject.getFloat("newPoint");

                if (!jsonObject.has("id")) {
                    if (!paramsSeriesWeek.isEmpty()) {
                        paramsSeriesWeek += ",";
                    }
                    paramsSeriesWeek += "(" + matchDataOnly.getSeriesId() + ",'" + weekNo + "'," + customerId + ","
                            + newRankSeriesWeek + "," + newRankSeriesWeek + "," + newPoint + "," + newPoint + ","
                            + currentTime + "," + currentTime + ")";
                } else {
                    int rowId = jsonObject.getInt("id");
                    int previousOldRank = jsonObject.getInt("oldRank");
                    int previousNewRank = jsonObject.getInt("newRank");
                    float previousOldPoints = jsonObject.getFloat("oldPoint");
                    float previousNewPoints = jsonObject.getFloat("oldNewPoint");
                    float newPoints = jsonObject.getFloat("newPoint");

                    int oldRank = previousOldRank;
                    if (previousOldRank == 0) {
                        oldRank = newRank;
                    } else if (previousNewRank != newRankSeriesWeek) {
                        oldRank = previousNewRank;
                    }

                    float oldPoints = previousOldPoints;
                    if (previousOldPoints == 0) {
                        oldPoints = newPoints;
                    } else if (previousNewPoints != newPoints) {
                        oldPoints = previousNewPoints;
                    }

                    String hqlll = "UPDATE " + "TblBasketballLeaderboardSeriesWeek "
                            + "SET oldRank=:oldRank, newRank=:newRank, oldPoint=:oldPoints, newPoint=:newPoints, updatedAt=:updatedAt  "
                            + "WHERE " + "id=:id";
                    Query queryyyy = newSession.createQuery(hqlll).setParameter("oldRank", oldRank)
                            .setParameter("newRank", newRankSeriesWeek).setParameter("oldPoints", oldPoints)
                            .setParameter("newPoints", newPoints)
                            .setParameter("updatedAt", BigInteger.valueOf(currentTime)).setParameter("id", rowId);
                    queryyyy.executeUpdate();
                }

                newRankSeriesWeek++;
            }

            if (!paramsSeriesWeek.isEmpty()) {
                String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_leaderboard_series_week "
                        + "(series_id,week_no,customer_id,old_rank,new_rank,old_point,new_point,created_at,updated_at)"
                        + " VALUES " + paramsSeriesWeek;

                newSession.createSQLQuery(mysqlQuery1).executeUpdate();
            }

            tx.commit();

        } catch (Exception e) {
            e.printStackTrace();
            isAllDistributed = false;
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        return isAllDistributed;
    }

    public void saveOrUpdateAffiliateContest(Session newSession, int contestJoinedTeam, float contestdeposite_wallet,
                                             float contestwinning_wallet, float contestcash_bonus_wallet, float contest_admin_commission_amount,
                                             float contest_earning, float affiliate_percent, float given_by_user_amount, float given_by_admin_amount,
                                             float total_given_by_admin_and_user_amount, int matchUniqueId, int matchContestId, int customerId) {

        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcac.id as id " + "FROM TblBasketballAffiliatesContest tcac "
                + "WHERE tcac.matchUniqueId=:matchUniqueId AND tcac.matchContestId=:matchContestId AND tcac.customerId=:customerId ";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("matchContestId", matchContestId).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() == 0) {
            TblBasketballAffiliatesContest tblBasketballAffiliatesContest = new TblBasketballAffiliatesContest();
            tblBasketballAffiliatesContest.setAdminCommissionAmount(contest_admin_commission_amount);
            tblBasketballAffiliatesContest.setAffiliatePercent(affiliate_percent);
            tblBasketballAffiliatesContest.setBonusAmount(contestcash_bonus_wallet);
            tblBasketballAffiliatesContest.setCreatedAt(currentTime.intValue());
            tblBasketballAffiliatesContest.setCustomerId(customerId);
            tblBasketballAffiliatesContest.setDepositAmount(contestdeposite_wallet);
            tblBasketballAffiliatesContest.setEarning(contest_earning);
            tblBasketballAffiliatesContest.setGivenByAdminAmount(given_by_admin_amount);
            tblBasketballAffiliatesContest.setGivenByUserAmount(given_by_user_amount);
            tblBasketballAffiliatesContest.setJoinedTeam(contestJoinedTeam);
            tblBasketballAffiliatesContest.setMatchContestId(matchContestId);
            tblBasketballAffiliatesContest.setMatchUniqueId(matchUniqueId);
            tblBasketballAffiliatesContest.setTotalWinContribute(total_given_by_admin_and_user_amount);
            tblBasketballAffiliatesContest.setWinningAmount(contestwinning_wallet);

            newSession.save(tblBasketballAffiliatesContest);

        } else {
            HashMap<String, Object> hashMap = result.get(0);
            int rowId = (int) hashMap.get("id");

            String hqlll = "UPDATE " + "TblBasketballAffiliatesContest "
                    + "SET adminCommissionAmount=:adminCommissionAmount, affiliatePercent=:affiliatePercent, bonusAmount=:bonusAmount, createdAt=:createdAt, depositAmount=:depositAmount, earning=:earning, givenByAdminAmount=:givenByAdminAmount, givenByUserAmount=:givenByUserAmount, joinedTeam=:joinedTeam, totalWinContribute=:totalWinContribute, winningAmount=:winningAmount "
                    + "WHERE " + "id=:rowId";
            Query queryyyy = newSession.createQuery(hqlll)
                    .setParameter("adminCommissionAmount", contest_admin_commission_amount)
                    .setParameter("affiliatePercent", affiliate_percent)
                    .setParameter("bonusAmount", contestcash_bonus_wallet)
                    .setParameter("createdAt", currentTime.intValue())
                    .setParameter("depositAmount", contestdeposite_wallet).setParameter("earning", contest_earning)
                    .setParameter("givenByAdminAmount", given_by_admin_amount)
                    .setParameter("givenByUserAmount", given_by_user_amount)
                    .setParameter("joinedTeam", contestJoinedTeam)
                    .setParameter("totalWinContribute", total_given_by_admin_and_user_amount)
                    .setParameter("winningAmount", contestwinning_wallet).setParameter("rowId", rowId);
            queryyyy.executeUpdate();

        }

    }

    public void saveOrUpdateCustomerAffiliateContest(Session newSession, int contestJoinedTeam,
                                                     float contestdeposite_wallet, float contestwinning_wallet, float contestcash_bonus_wallet,
                                                     float contest_admin_commission_amount, float contest_earning, float affiliate_percent,
                                                     float given_by_user_amount, float given_by_admin_amount, float total_given_by_admin_and_user_amount,
                                                     int matchUniqueId, int matchContestId, int customerId) {

        Long currentTime = Util.getCurrentTime();

        String hqln = "SELECT " + "tcac.id as id " + "FROM TblBasketballCustomerAffiliatesContest tcac "
                + "WHERE tcac.matchUniqueId=:matchUniqueId AND tcac.matchContestId=:matchContestId AND tcac.customerId=:customerId ";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("matchContestId", matchContestId).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() == 0) {
            TblBasketballCustomerAffiliatesContest tblBasketballAffiliatesContest = new TblBasketballCustomerAffiliatesContest();
            tblBasketballAffiliatesContest.setAdminCommissionAmount(contest_admin_commission_amount);
            tblBasketballAffiliatesContest.setAffiliatePercent(affiliate_percent);
            tblBasketballAffiliatesContest.setBonusAmount(contestcash_bonus_wallet);
            tblBasketballAffiliatesContest.setCreatedAt(currentTime.intValue());
            tblBasketballAffiliatesContest.setCustomerId(customerId);
            tblBasketballAffiliatesContest.setDepositAmount(contestdeposite_wallet);
            tblBasketballAffiliatesContest.setEarning(contest_earning);
            tblBasketballAffiliatesContest.setGivenByAdminAmount(given_by_admin_amount);
            tblBasketballAffiliatesContest.setGivenByUserAmount(given_by_user_amount);
            tblBasketballAffiliatesContest.setJoinedTeam(contestJoinedTeam);
            tblBasketballAffiliatesContest.setMatchContestId(matchContestId);
            tblBasketballAffiliatesContest.setMatchUniqueId(matchUniqueId);
            tblBasketballAffiliatesContest.setTotalWinContribute(total_given_by_admin_and_user_amount);
            tblBasketballAffiliatesContest.setWinningAmount(contestwinning_wallet);

            newSession.save(tblBasketballAffiliatesContest);

        } else {
            HashMap<String, Object> hashMap = result.get(0);
            int rowId = (int) hashMap.get("id");

            String hqlll = "UPDATE " + "TblBasketballCustomerAffiliatesContest "
                    + "SET adminCommissionAmount=:adminCommissionAmount, affiliatePercent=:affiliatePercent, bonusAmount=:bonusAmount, createdAt=:createdAt, depositAmount=:depositAmount, earning=:earning, givenByAdminAmount=:givenByAdminAmount, givenByUserAmount=:givenByUserAmount, joinedTeam=:joinedTeam, totalWinContribute=:totalWinContribute, winningAmount=:winningAmount "
                    + "WHERE " + "id=:rowId";
            Query queryyyy = newSession.createQuery(hqlll)
                    .setParameter("adminCommissionAmount", contest_admin_commission_amount)
                    .setParameter("affiliatePercent", affiliate_percent)
                    .setParameter("bonusAmount", contestcash_bonus_wallet)
                    .setParameter("createdAt", currentTime.intValue())
                    .setParameter("depositAmount", contestdeposite_wallet).setParameter("earning", contest_earning)
                    .setParameter("givenByAdminAmount", given_by_admin_amount)
                    .setParameter("givenByUserAmount", given_by_user_amount)
                    .setParameter("joinedTeam", contestJoinedTeam)
                    .setParameter("totalWinContribute", total_given_by_admin_and_user_amount)
                    .setParameter("winningAmount", contestwinning_wallet).setParameter("rowId", rowId);
            queryyyy.executeUpdate();

        }

    }

    public void settledAffiliateAmountOnCustomerContest(Session newSession, int matchUniqueId) {
        String hqln = "SELECT "
                + "count(tccc.id) as joinedteams, tccc.matchContestId as matchContestId, SUM(tccc.winAmount) as prizepool, SUM(tccc.depositeWallet) as depositeWallet, SUM(tccc.winningWallet) as winningWallet "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballContestMatch tccm ON tccm.id=tccc.matchContestId "
                + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccm.isAbondant='N' AND tccm.isDeleted='N' "
                + "GROUP BY tccc.matchContestId";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() == 0) {
            return;
        }

        for (HashMap<String, Object> contestData : result) {

            int match_contest_id = (int) contestData.get("matchContestId");
            long joinedteams = (long) contestData.get("joinedteams");
            float prizepool = Float.parseFloat(String.valueOf(contestData.get("prizepool")));
            float winningWallet = Float.parseFloat(String.valueOf(contestData.get("winningWallet")));
            float depositeWallet = Float.parseFloat(String.valueOf(contestData.get("depositeWallet")));

            float perUserWinnContri = prizepool / joinedteams;

            perUserWinnContri = Util.numberFormate(perUserWinnContri, 2);

            String hqlll = "UPDATE " + "TblBasketballCustomerContest tccc "
                    + "SET tccc.givenByUserAmount=(CASE WHEN (tccc.depositeWallet+tccc.winningWallet)>:perUserWinnContri THEN (tccc.depositeWallet+tccc.winningWallet-:perUserWinnContri) ELSE 0 END), tccc.givenByAdminAmount=(CASE WHEN (tccc.depositeWallet+tccc.winningWallet) < :perUserWinnContri THEN (:perUserWinnContri-tccc.depositeWallet-tccc.winningWallet) ELSE 0 END) "
                    + "WHERE " + "tccc.matchContestId=:matchContestId";
            Query queryyyy = newSession.createQuery(hqlll).setParameter("perUserWinnContri", perUserWinnContri)
                    .setParameter("matchContestId", match_contest_id);
            queryyyy.executeUpdate();

            String hqlll1 = "UPDATE " + "TblBasketballContestMatch "
                    + "SET totalRealMoneyReceived=:totalRealMoneyReceived, totalWinningDistributed=:totalWinningDistributed "
                    + "WHERE " + "id=:id";
            Query queryyyy1 = newSession.createQuery(hqlll1)
                    .setParameter("totalRealMoneyReceived", depositeWallet + winningWallet)
                    .setParameter("totalWinningDistributed", prizepool).setParameter("id", match_contest_id);
            queryyyy1.executeUpdate();
        }
    }

    public LinkedHashMap<Integer, Object> getSettledAmountFromCustomerContest(Session newSession, int matchUniqueId) {
        String hqln = "SELECT "
                + "tccc.matchContestId as matchContestId, SUM(tccc.givenByUserAmount) as givenByUserAmount, SUM(tccc.givenByAdminAmount) as givenByAdminAmount, "
                + "tccm.totalRealMoneyReceived as totalRealMoneyReceived, tccm.totalWinningDistributed as totalWinningDistributed "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballContestMatch tccm ON tccm.id=tccc.matchContestId "
                + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccm.isAbondant='N' AND tccm.isDeleted='N' "
                + "GROUP BY tccc.matchContestId";

        Query queryy = newSession.createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();
        if (result.size() == 0) {
            return data;
        }

        for (HashMap<String, Object> contestData : result) {
            data.put((int) contestData.get("matchContestId"), contestData);
        }

        return data;
    }

    public void updatePlayerPointsInSeries(int seriesId, int gameTypeId) {
        Session newSession = getNewSession();

        String hqln = "SELECT " + "tcm.uniqueId as uniqueId " + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.seriesId=:seriesId AND tcm.gameTypeId=:gameTypeId AND tcm.matchResultRunning='Y'";

        Query queryy = newSession.createQuery(hqln).setParameter("seriesId", seriesId).setParameter("gameTypeId",
                gameTypeId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.size() == 0) {
            newSession.close();
            return;
        }

        List<Integer> matches = new ArrayList<Integer>();
        for (HashMap<String, Object> data : result) {
            int uniqueId = (int) data.get("uniqueId");
            matches.add(uniqueId);
        }

        hqln = "SELECT " + "tcmp.playerUniqueId as playerUniqueId, SUM(tcmp.points) as points "
                + "FROM TblBasketballMatchPlayer tcmp "
                + "WHERE tcmp.matchUniqueId IN(:matches) AND tcmp.isDeleted='N' AND tcmp.status='A' AND tcmp.points!=0 "
                + "GROUP BY tcmp.playerUniqueId";

        queryy = newSession.createQuery(hqln).setParameterList("matches", matches);

        List<HashMap<String, Object>> result2 = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result2.size() == 0) {
            newSession.close();
            return;
        }
        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();
            HashMap<Integer, Float> seriesPlayersData = new HashMap<Integer, Float>();
            for (HashMap<String, Object> data : result2) {
                int playerUniqueId = (int) data.get("playerUniqueId");
                float points = Float.parseFloat(String.valueOf(data.get("points")));
                seriesPlayersData.put(playerUniqueId, points);
            }

            hqln = "SELECT " + "tcsp.id as id, tcsp.playerUniqueId as playerUniqueId "
                    + "FROM TblBasketballSeriesPlayers tcsp "
                    + "WHERE tcsp.seriesId=:seriesId AND tcsp.gameTypeId=:gameTypeId";

            queryy = newSession.createQuery(hqln).setParameter("seriesId", seriesId).setParameter("gameTypeId",
                    gameTypeId);

            List<HashMap<String, Object>> result3 = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            Long currentTime = Util.getCurrentTime();

            if (result3.size() > 0) {
                for (HashMap<String, Object> hashMap : result3) {
                    int rowId = (int) hashMap.get("id");
                    int playerUniqueId = (int) hashMap.get("playerUniqueId");

                    if (seriesPlayersData.containsKey(playerUniqueId)) {
                        Float points = seriesPlayersData.get(playerUniqueId);

                        String hqlll = "UPDATE " + "TblBasketballSeriesPlayers " + "SET points=:points, updated=:updated "
                                + "WHERE " + "id=:id";
                        Query queryyyy = newSession.createQuery(hqlll).setParameter("points", points)
                                .setParameter("updated", BigInteger.valueOf(currentTime)).setParameter("id", rowId);
                        queryyyy.executeUpdate();

                        seriesPlayersData.remove(playerUniqueId);
                    }
                }
            }

            if (!seriesPlayersData.isEmpty()) {

                String paramsSeries = "";

                Set<Integer> keySet = seriesPlayersData.keySet();
                for (Integer playerUniqueId : keySet) {
                    float points = seriesPlayersData.get(playerUniqueId);
                    if (!paramsSeries.isEmpty()) {
                        paramsSeries += ",";
                    }
                    paramsSeries += "(" + seriesId + "," + playerUniqueId + "," + gameTypeId + "," + points + ","
                            + currentTime + "," + currentTime + ")";
                }

                String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_series_players "
                        + "(series_id,player_unique_id,game_type_id,points,created,updated)" + " VALUES "
                        + paramsSeries;

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
    public LinkedHashMap<String, Object> getOurSystemRunningMatches() {
        String hqln = "SELECT "
                + "tcm.id as id, tcm.uniqueId as uniqueId, tcm.closeDate as closeDate, tcm.matchDate as matchDate "
                + "FROM TblBasketballMatch tcm "
                + "WHERE tcm.isDeleted='N' AND (tcm.matchProgress='F' OR tcm.matchProgress='L' OR tcm.matchProgress='AB') ";

        Query queryy = getSession().createQuery(hqln);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        LinkedHashMap<String, Object> matches = new LinkedHashMap<String, Object>();

        if (result == null || result.size() == 0) {
            return matches;
        }

        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();
            match.put("id", hashMap.get("id"));
            match.put("uniqueId", hashMap.get("uniqueId"));
            match.put("closeDate", Long.parseLong(hashMap.get("closeDate").toString()));
            match.put("matchDate", Long.parseLong(hashMap.get("matchDate").toString()));

            matches.put(match.get("uniqueId").toString(), match);
        }

        return matches;

    }

    public void updateNewAvailableMatchCount(List<LinkedHashMap<String, Object>> newMatchAvailable,
                                             List<LinkedHashMap<String, Object>> matchTimeChanged) {
        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();

            int newMatchesCount = newMatchAvailable.size();
            int updatedMatchesCount = matchTimeChanged.size();

            int basketballSportsId = GlobalConstant.SPORT_ID_BASKETBALL;
            String hqlll = "UPDATE " + "TblGame " + "SET newMatchCount=:newMatchesCount " + "WHERE " + "id=:id";
            Query queryyyy = newSession.createQuery(hqlll).setParameter("newMatchesCount", newMatchesCount)
                    .setParameter("id", basketballSportsId);
            queryyyy.executeUpdate();

            if (updatedMatchesCount > 0) {
                for (LinkedHashMap<String, Object> match : matchTimeChanged) {
                    int unique_id = Integer.parseInt(match.get("unique_id").toString());
                    long updatedMatchTime = (long) match.get("updatedMatchTime");

                    hqlll = "UPDATE " + "TblBasketballMatch " + "SET matchDate=:matchDate, closeDate=:closeDate " + "WHERE "
                            + "uniqueId=:uniqueId";
                    queryyyy = newSession.createQuery(hqlll).setParameter("matchDate", BigInteger.valueOf(updatedMatchTime)).setParameter("closeDate", BigInteger.valueOf(updatedMatchTime))
                            .setParameter("uniqueId", unique_id);
                    queryyyy.executeUpdate();
                }
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


    @Transactional
    public void updateCustomerMatchAbondantContestInfo(int matchUniqueId, int matchContestId) {
        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            tx = newSession.beginTransaction();
            String hqln = "SELECT COUNT(DISTINCT tccc.matchContestId) AS cnt, tccc.customerId as customerId FROM TblBasketballCustomerContest tccc WHERE tccc.matchUniqueId =:matchUniqueId AND tccc.isAbondant='Y' GROUP BY tccc.customerId";
            if (matchContestId > 0) {
                hqln = "SELECT COUNT(DISTINCT tccc.matchContestId) AS cnt, tccc.customerId as customerId FROM TblBasketballCustomerContest tccc WHERE tccc.matchUniqueId =:matchUniqueId AND tccc.matchContestId =:matchContestId AND tccc.isAbondant='Y' GROUP BY tccc.customerId";
            }

            Query queryy = newSession.createQuery(hqln);
            queryy.setParameter("matchUniqueId", matchUniqueId);
            if (matchContestId > 0) {
                queryy.setParameter("matchContestId", matchContestId);
            }
            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            if (result != null && result.size() > 0) {
                for (HashMap<String, Object> data : result) {
                    long count = (long) data.get("cnt");
                    int customerId = (int) data.get("customerId");

                    if (matchContestId > 0) {
                        String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_customer_match_info "
                                + "SET match_unique_id = " + matchUniqueId + ", "
                                + "customer_id = " + customerId + ", "
                                + "ab_contest_count = " + count + " "
                                + "ON DUPLICATE KEY UPDATE ab_contest_count += " + count;
                        Query queryyy = newSession.createSQLQuery(mysqlQuery1);
                        queryyy.executeUpdate();
                    } else {
                        String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_customer_match_info "
                                + "SET match_unique_id = " + matchUniqueId + ", "
                                + "customer_id = " + customerId + ", "
                                + "ab_contest_count = " + count + " "
                                + "ON DUPLICATE KEY UPDATE ab_contest_count = " + count;
                        Query queryyy = newSession.createSQLQuery(mysqlQuery1);
                        queryyy.executeUpdate();
                    }
                }
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
    public TblBasketballSeries getSeriesData(int id) {
        String hqlQuery = "SELECT tcs FROM TblBasketballSeries tcs WHERE tcs.status =:status AND tcs.isDeleted =:isDeleted AND tcs.id =:id";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        queryObj.setParameter("id", id);
        return  (TblBasketballSeries) queryObj.uniqueResult();
    }

    @Transactional
    public int updateSeriesLeaderboardResultStart(int id, String result) {
        Long currentTime = Util.getCurrentTime();

        String hqln = "UPDATE TblBasketballSeries tcs "
                + "SET tcs.isResultDeclared=:isResultDeclared, tcs.updatedAt=:updatedAt "
                + "WHERE tcs.id = :id";
        if(result.equals("R")){
            hqln+=" AND tcs.isResultDeclared='N'";
        }

        Query queryy = getSession().createQuery(hqln)
                .setParameter("isResultDeclared", result)
                .setParameter("updatedAt", BigInteger.valueOf(currentTime))
                .setParameter("id", id);

        return queryy.executeUpdate();

    }

    @Transactional
    public List<HashMap<String, Object>> generatePlayerSelectedCount(int matchUniqueId) {
        Session session = getNewSession();
        Transaction tx = null;
        try {
            String hql2 = "SELECT "
                    + "tcctp.playerUniqueId as playerUniqueId, "
                    + "count(tcctp.id) as selectedByCount, "
                    + "SUM(CASE WHEN tcctp.multiplier=2 THEN 1 ELSE 0 END) as selectedAsCaptionCount, "
                    + "SUM(CASE WHEN tcctp.multiplier=1.5 THEN 1 ELSE 0 END) as selectedAsVcCaptionCount "
                    + "FROM TblBasketballCustomerTeamPlyer tcctp " +
                    "WHERE tcctp.matchUniqueId = :matchUniqueId GROUP BY tcctp.playerUniqueId";
            Query queryy2 = session.createQuery(hql2)
                    .setParameter("matchUniqueId", matchUniqueId);

            List<HashMap<String, Object>> result2 = (List<HashMap<String, Object>>) queryy2
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if (result2 != null && result2.size() > 0) {
                tx = session.beginTransaction();
                for (HashMap<String, Object> hashMap : result2) {
                    int playerUniqueId = (int) hashMap.get("playerUniqueId");
                    Long selectedByCount = (Long) hashMap.get("selectedByCount");
                    Long selectedAsCaptionCount = (Long) hashMap.get("selectedAsCaptionCount");
                    Long selectedAsVcCaptionCount = (Long) hashMap.get("selectedAsVcCaptionCount");


                    String updatePlayer = "UPDATE "
                            + "TblBasketballMatchPlayer "
                            + "SET selectedByCount = :selectedByCount, selectedAsCaptionCount = :selectedAsCaptionCount, selectedAsVccaptionCount = :selectedAsVcCaptionCount "
                            + "WHERE "
                            + "matchUniqueId = :matchUniqueId AND playerUniqueId = :playerUniqueId";
                    session.createQuery(updatePlayer)
                            .setParameter("matchUniqueId", matchUniqueId)
                            .setParameter("playerUniqueId", playerUniqueId)
                            .setParameter("selectedByCount", selectedByCount.intValue())
                            .setParameter("selectedAsCaptionCount", selectedAsCaptionCount.intValue())
                            .setParameter("selectedAsVcCaptionCount", selectedAsVcCaptionCount.intValue())
                            .executeUpdate();
                }
                String updateMatch = "UPDATE "
                        + "TblBasketballMatch "
                        + "SET totalCustomerTeam = (SELECT count(id) FROM TblBasketballCustomerTeam WHERE matchUniqueId=:matchUniqueId) "
                        + "WHERE "
                        + "uniqueId = :matchUniqueId";
                session.createQuery(updateMatch)
                        .setParameter("matchUniqueId", matchUniqueId)
                        .executeUpdate();
                tx.commit();
            }
            return result2;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

}