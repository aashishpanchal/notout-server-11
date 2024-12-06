package com.choic11.repository.basketball;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.Util;
import com.choic11.model.basketball.TblBasketballCustomerContest;
import com.choic11.model.basketball.TblBasketballCustomerMatchInfo;
import com.choic11.model.basketball.TblBasketballMatch;
import com.choic11.model.basketball.TblBasketballSlider;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository("BasketballMatchesRepository")
public class MatchesRepository {

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
    public List<TblBasketballSlider> getSlider() {

        String hqlQuery = "SELECT tcs FROM TblBasketballSlider tcs WHERE tcs.status =:status AND tcs.isDeleted =:isDeleted ORDER BY tcs.sliderOrder ASC";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        List<TblBasketballSlider> sliders = queryObj.getResultList();

//        Criteria createCriteria = getSession().createCriteria(TblBasketballSlider.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.addOrder(Order.asc("sliderOrder"));
//        List<TblBasketballSlider> sliders = createCriteria.list();


        for (TblBasketballSlider tblSlider : sliders) {
            if (tblSlider.getMatchUniqueId() > 0) {
                Hibernate.initialize(tblSlider.getTblBasketballMatch());
            }
        }

        return sliders;
    }

    @Transactional(readOnly = true)
    public List<TblBasketballMatch> getMatches(String matchProgress,int customerId) {
        Session session = getSession();
        String hqlQuery;
        Query queryObj = null;
        if (matchProgress.equals("R")) {
            hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.matchProgress IN('R','AB') AND tcm.status =:status AND tcm.isDeleted =:isDeleted ORDER BY tcm.pointsUpdatedAt DESC";
            queryObj = session.createQuery(hqlQuery);
            queryObj.setParameter("status", "A");
            queryObj.setParameter("isDeleted", "N");
            queryObj.setFirstResult(0).setMaxResults(100);
        } else if (matchProgress.equals("L")) {
            hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.matchProgress IN('L','IR') AND tcm.status =:status AND tcm.isDeleted =:isDeleted ORDER BY tcm.closeDate DESC";
            queryObj = session.createQuery(hqlQuery);
            queryObj.setParameter("status", "A");
            queryObj.setParameter("isDeleted", "N");
        } else {
            if (matchProgress.equals("F")) {
                hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.matchProgress IN(:matchProgress) AND tcm.status =:status AND tcm.isDeleted =:isDeleted ORDER BY tcm.orderPos ASC";
                queryObj = session.createQuery(hqlQuery);
                queryObj.setParameter("matchProgress", matchProgress);
                queryObj.setParameter("status", "A");
                queryObj.setParameter("isDeleted", "N");
            }else{
                hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.matchProgress IN(:matchProgress) AND tcm.status =:status AND tcm.isDeleted =:isDeleted ORDER BY tcm.closeDate DESC";
                queryObj = session.createQuery(hqlQuery);
                queryObj.setParameter("matchProgress", matchProgress);
                queryObj.setParameter("status", "A");
                queryObj.setParameter("isDeleted", "N");
            }
        }
        List<TblBasketballMatch> matches = queryObj.getResultList();
        List<Integer> matchIds = new ArrayList<>();
        LinkedHashMap<Integer, TblBasketballMatch> matchesList = new LinkedHashMap<>();
        for (TblBasketballMatch tblBasketballMatch : matches){
            matchIds.add(tblBasketballMatch.getUniqueId());
            matchesList.put(tblBasketballMatch.getUniqueId(),tblBasketballMatch);
        }

        if (matchProgress.equals("L")) {
            String hqln = "SELECT " +
                    "tccmi.matchUniqueId as matchUniqueId, " +
                    "COALESCE(tccmi.contestCount,0) as contestCount, " +
                    "COALESCE(tccmi.abContestCount,0) as abContestCount, " +
                    "COALESCE(tccmi.teamCount,0) as teamCount " +
                    "FROM TblBasketballCustomerMatchInfo tccmi " +
                    "WHERE tccmi.matchUniqueId IN(:matchUniqueIds) AND tccmi.customerId=:customerId";
            Query query = session.createQuery(hqln);
            query.setParameterList("matchUniqueIds", matchIds);
            query.setParameter("customerId", customerId);
            query.setResultTransformer(new AliasToBeanResultTransformer(TblBasketballCustomerMatchInfo.class));
            List<TblBasketballCustomerMatchInfo> list = query.getResultList();
            for (TblBasketballCustomerMatchInfo tblBasketballCustomerMatchInfo : list) {
                if (matchesList.containsKey(tblBasketballCustomerMatchInfo.getMatchUniqueId())) {
                    TblBasketballMatch tblBasketballMatch = matchesList.get(tblBasketballCustomerMatchInfo.getMatchUniqueId());
                    tblBasketballMatch.setContestCount(tblBasketballCustomerMatchInfo.getContestCount()- tblBasketballCustomerMatchInfo.getAbContestCount());
                    tblBasketballMatch.setTeamCount(tblBasketballCustomerMatchInfo.getTeamCount());
                }
            }
        }

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//
//        if (matchProgress.equals("R")) {
//            createCriteria.add(Restrictions.in("matchProgress", "R", "AB"));
//        } else if (matchProgress.equals("L")) {
//            createCriteria.add(Restrictions.in("matchProgress", "L", "IR"));
//        } else {
//            createCriteria.add(Restrictions.in("matchProgress", matchProgress));
//        }
//
//        if (matchProgress.equals("F")) {
//            createCriteria.addOrder(Order.asc("orderPos"));
//        } else if (matchProgress.equals("R")) {
//            createCriteria.addOrder(Order.desc("pointsUpdatedAt"));
//
//            createCriteria.setFirstResult(0);
//            createCriteria.setMaxResults(100);
//        } else {
//            createCriteria.addOrder(Order.desc("closeDate"));
//        }
//        List<TblBasketballMatch> matches = createCriteria.list();

        return matches;

    }

    @Transactional(readOnly = true)
    public Object getCustomerMatches(int customerId, String matchProgress) {

        String matchProgressIn = "";

        if (matchProgress.equals("R")) {
            matchProgressIn = " AND tcm.matchProgress IN ('R','AB')";
        } else if (matchProgress.equals("L")) {
            matchProgressIn = " AND tcm.matchProgress IN ('L','IR')";
        } else {
            matchProgressIn = " AND tcm.matchProgress IN ('" + matchProgress + "')";
        }

        String hqln = "SELECT "
                + "tccc.matchUniqueId as matchUniqueId, count(DISTINCT tccc.matchContestId) as contestCount  "
                + "FROM TblBasketballCustomerContest tccc "
                + "LEFT JOIN TblBasketballMatch tcm on (tcm.uniqueId = tccc.matchUniqueId) " + "WHERE "
                + "tccc.customerId=:customerId " + "AND tcm.status='A' AND tcm.isDeleted='N' " + matchProgressIn
                + " GROUP BY tccc.matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", customerId);
        List<HashMap<String, Object>> customerContests = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        if (customerContests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> customerMatches = new ArrayList<Integer>();
        LinkedHashMap<String, Object> customerMatchContest = new LinkedHashMap<>();
        for (HashMap<String, Object> hashMap : customerContests) {
            customerMatches.add((int) hashMap.get("matchUniqueId"));

            customerMatchContest.put(hashMap.get("matchUniqueId").toString(), hashMap.get("contestCount"));
        }

        String hqlQuery;
        Query queryObj = null;
        if (matchProgress.equals("F")) {
            hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.uniqueId IN(:uniqueId) ORDER BY tcm.orderPos ASC";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("uniqueId", customerMatches);
        } else if (matchProgress.equals("R")) {
            hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.uniqueId IN(:uniqueId) ORDER BY tcm.pointsUpdatedAt DESC";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setFirstResult(0);
            queryObj.setMaxResults(100);
            queryObj.setParameter("uniqueId", customerMatches);
        } else {
            hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.uniqueId IN(:uniqueId) ORDER BY tcm.closeDate DESC ";
            queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("uniqueId", customerMatches);
        }
        List<TblBasketballMatch> matches = queryObj.getResultList();

        List<Integer> matchIds = new ArrayList<>();
        LinkedHashMap<Integer, TblBasketballMatch> matchesList = new LinkedHashMap<>();
        for (TblBasketballMatch tblBasketballMatch : matches){
            matchIds.add(tblBasketballMatch.getUniqueId());
            matchesList.put(tblBasketballMatch.getUniqueId(),tblBasketballMatch);
        }

        if (matchProgress.equals("R")) {

            String hqlnnn = "SELECT " +
                    "tccc.matchUniqueId as matchUniqueId, " +
                    "COALESCE(SUM(tccc.winAmount),0) as totalWinning " +
                    "FROM TblBasketballCustomerContest tccc " +
                    "WHERE tccc.matchUniqueId IN(:matchUniqueIds) AND tccc.customerId=:customerId GROUP BY tccc.matchUniqueId,tccc.customerId";
            Query queryyy = getSession().createQuery(hqlnnn);
            queryyy.setParameterList("matchUniqueIds", matchIds);
            queryyy.setParameter("customerId", customerId);
            queryyy.setResultTransformer(new AliasToBeanResultTransformer(TblBasketballCustomerContest.class));
            List<TblBasketballCustomerContest> list2 = queryyy.getResultList();
            for (TblBasketballCustomerContest tblBasketballCustomerContest : list2) {
                if (matchesList.containsKey(tblBasketballCustomerContest.getMatchUniqueId())) {
                    TblBasketballMatch tblBasketballMatch = matchesList.get(tblBasketballCustomerContest.getMatchUniqueId());
                    tblBasketballMatch.setTotalWinning(tblBasketballCustomerContest.getTotalWinning());
                }
            }
        }



        String hqlnn = "SELECT " +
                "tccmi.matchUniqueId as matchUniqueId, " +
                "COALESCE(tccmi.contestCount,0) as contestCount, " +
                "COALESCE(tccmi.abContestCount,0) as abContestCount, " +
                "COALESCE(tccmi.teamCount,0) as teamCount " +
                "FROM TblBasketballCustomerMatchInfo tccmi " +
                "WHERE tccmi.matchUniqueId IN(:matchUniqueIds) AND tccmi.customerId=:customerId";
        Query query = getSession().createQuery(hqlnn);
        query.setParameterList("matchUniqueIds", matchIds);
        query.setParameter("customerId",customerId);
        query.setResultTransformer(new AliasToBeanResultTransformer(TblBasketballCustomerMatchInfo.class));
        List<TblBasketballCustomerMatchInfo> list = query.getResultList();
        for (TblBasketballCustomerMatchInfo tblBasketballCustomerMatchInfo : list){
            if (matchesList.containsKey(tblBasketballCustomerMatchInfo.getMatchUniqueId())){
                TblBasketballMatch tblBasketballMatch = matchesList.get(tblBasketballCustomerMatchInfo.getMatchUniqueId());
                tblBasketballMatch.setContestCount(tblBasketballCustomerMatchInfo.getContestCount() - tblBasketballCustomerMatchInfo.getAbContestCount());
                tblBasketballMatch.setTeamCount(tblBasketballCustomerMatchInfo.getTeamCount());
            }
        }


//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.in("uniqueId", customerMatches));
//
//        if (matchProgress.equals("F")) {
//            createCriteria.addOrder(Order.asc("orderPos"));
//        } else if (matchProgress.equals("R")) {
//            createCriteria.addOrder(Order.desc("pointsUpdatedAt"));
//
//            createCriteria.setFirstResult(0);
//            createCriteria.setMaxResults(100);
//        } else {
//            createCriteria.addOrder(Order.desc("closeDate"));
//        }
//        List<TblBasketballMatch> matches = createCriteria.list();

        return new Object[]{customerMatchContest, matches};

    }

    @Transactional(readOnly = true)
    public TblBasketballMatch getMatchData(int uniqueId) {
        String hqlQuery = "SELECT tcm FROM TblBasketballMatch tcm WHERE tcm.status =:status AND tcm.isDeleted =:isDeleted AND tcm.uniqueId =:uniqueId";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        queryObj.setParameter("uniqueId", uniqueId);
        return  (TblBasketballMatch) queryObj.uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.add(Restrictions.eq("uniqueId", uniqueId));
//        return (TblBasketballMatch) createCriteria.uniqueResult();

    }

    @Transactional(readOnly = true)
    public TblBasketballMatch getCustomerMatchData(int uniqueId,int customerId) {
        Session session = getSession();
        TblBasketballMatch tblBasketballMatch = new TblBasketballMatch();
        // get totalContestCount and teamCount
        String hqln = "SELECT " +
                "COALESCE(tccmi.contestCount,0) as contestCount, " +
                "COALESCE(tccmi.abContestCount,0) as abContestCount, " +
                "COALESCE(tccmi.teamCount,0) as teamCount " +
                "FROM TblBasketballCustomerMatchInfo tccmi " +
                "WHERE tccmi.matchUniqueId=:matchUniqueId AND tccmi.customerId=:customerId";
        Query query = session.createQuery(hqln);
        query.setParameter("matchUniqueId", uniqueId);
        query.setParameter("customerId",customerId);
        query.setResultTransformer(new AliasToBeanResultTransformer(TblBasketballCustomerMatchInfo.class));
        TblBasketballCustomerMatchInfo tblBasketballCustomerMatchInfo = (TblBasketballCustomerMatchInfo)query.uniqueResult();
        if (tblBasketballCustomerMatchInfo != null){
        tblBasketballMatch.setContestCount(tblBasketballCustomerMatchInfo.getContestCount() - tblBasketballCustomerMatchInfo.getAbContestCount());
        tblBasketballMatch.setTeamCount(tblBasketballCustomerMatchInfo.getTeamCount());
        }


        String hqlnn = "SELECT " +
                "COALESCE(SUM(tccc.entryFees),0) as totalInvestment, " +
                "COALESCE(SUM(tccc.winAmount),0) as totalWinning " +
                "FROM TblBasketballCustomerContest tccc " +
                "WHERE tccc.matchUniqueId=:matchUniqueId AND tccc.customerId=:customerId AND tccc.isAbondant='N'";
        Query queryy = session.createQuery(hqlnn);
        queryy.setParameter("matchUniqueId", uniqueId);
        queryy.setParameter("customerId",customerId);
        queryy.setResultTransformer(new AliasToBeanResultTransformer(TblBasketballCustomerContest.class));
        TblBasketballCustomerContest tblBasketballCustomerContest = (TblBasketballCustomerContest)queryy.uniqueResult();
        if (tblBasketballCustomerContest != null) {
            tblBasketballMatch.setTotalInvestment(tblBasketballCustomerContest.getTotalInvestment());
            tblBasketballMatch.setTotalWinning(tblBasketballCustomerContest.getTotalWinning());
        }
        return  tblBasketballMatch;

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.add(Restrictions.eq("uniqueId", uniqueId));
//        return (TblBasketballMatch) createCriteria.uniqueResult();

    }

    @Transactional(readOnly = true)
    public TblBasketballMatch getMatchDataOnly(int uniqueId) {

        String hqlQuery = "SELECT " +
                "tcm.id as id," +
                "tcm.uniqueId as uniqueId," +
                "tcm.name as name," +
                "tcm.closeDate as closeDate," +
                "tcm.contestJoinedMail as contestJoinedMail," +
                "tcm.contestWiningMail as contestWiningMail," +
                "tcm.isAffiliateDistribute as isAffiliateDistribute," +
                "tcm.matchResultRunning as matchResultRunning," +
                "tcm.isReferralDistributed as isReferralDistributed," +
                "tcm.isContestAbondantComplete as isContestAbondantComplete," +
                "tcm.isLeaderboardCreated as isLeaderboardCreated," +
                "tcm.matchDate as matchDate," +
                "tcm.matchLimit as matchLimit," +
                "tcm.matchProgress as matchProgress," +
                "tcm.matchCompletedStatus as matchCompletedStatus," +
                "tcm.playingSquadNotificationAt as playingSquadNotificationAt," +
                "tcm.playingSquadUpdated as playingSquadUpdated," +
                "tcm.isAffiliateAvailableAll as isAffiliateAvailableAll," +
                "tcm.affiliatePerForAll as affiliatePerForAll," +
                "tcm.shortTitle as shortTitle," +
                "tcm.subtitle as subtitle," +
                "tcm.team1Id as team1Id," +
                "tcm.team2Id as team2Id," +
                "tcm.gameTypeId as gameTypeId," +
                "tcm.seriesId as seriesId," +
                "tcm.totalCustomerTeam as totalCustomerTeam," +
                "tcm.matchMessage as matchMessage " +
                "FROM TblBasketballMatch tcm WHERE tcm.status =:status AND tcm.isDeleted =:isDeleted AND tcm.uniqueId =:uniqueId";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        queryObj.setParameter("uniqueId", uniqueId);
        return (TblBasketballMatch) queryObj.setResultTransformer(Transformers.aliasToBean(TblBasketballMatch.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.add(Restrictions.eq("uniqueId", uniqueId));
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("uniqueId"), "uniqueId");
//        projectionList.add(Projections.property("name"), "name");
//        projectionList.add(Projections.property("closeDate"), "closeDate");
//        projectionList.add(Projections.property("contestJoinedMail"), "contestJoinedMail");
//        projectionList.add(Projections.property("contestWiningMail"), "contestWiningMail");
//        projectionList.add(Projections.property("isAffiliateDistribute"), "isAffiliateDistribute");
//        projectionList.add(Projections.property("matchResultRunning"), "matchResultRunning");
//        projectionList.add(Projections.property("isReferralDistributed"), "isReferralDistributed");
//        projectionList.add(Projections.property("isContestAbondantComplete"), "isContestAbondantComplete");
//        projectionList.add(Projections.property("isLeaderboardCreated"), "isLeaderboardCreated");
//        projectionList.add(Projections.property("matchDate"), "matchDate");
//        projectionList.add(Projections.property("matchLimit"), "matchLimit");
//        projectionList.add(Projections.property("matchProgress"), "matchProgress");
//        projectionList.add(Projections.property("matchCompletedStatus"), "matchCompletedStatus");
//        projectionList.add(Projections.property("playingSquadNotificationAt"), "playingSquadNotificationAt");
//        projectionList.add(Projections.property("playingSquadUpdated"), "playingSquadUpdated");
//        projectionList.add(Projections.property("isAffiliateAvailableAll"), "isAffiliateAvailableAll");
//        projectionList.add(Projections.property("affiliatePerForAll"), "affiliatePerForAll");
//        projectionList.add(Projections.property("shortTitle"), "shortTitle");
//        projectionList.add(Projections.property("subtitle"), "subtitle");
//        projectionList.add(Projections.property("team1Id"), "team1Id");
//        projectionList.add(Projections.property("team2Id"), "team2Id");
//        projectionList.add(Projections.property("gameTypeId"), "gameTypeId");
//        projectionList.add(Projections.property("seriesId"), "seriesId");
//        projectionList.add(Projections.property("totalCustomerTeam"), "totalCustomerTeam");
//        projectionList.add(Projections.property("matchMessage"), "matchMessage");
//
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblBasketballMatch.class));
//        return (TblBasketballMatch) createCriteria.uniqueResult();

    }

    @Transactional(readOnly = true)
    public TblBasketballMatch getMatchMiniDataOnly(int uniqueId) {
        String hqlQuery = "SELECT " +
                "tcm.id as id," +
                "tcm.uniqueId as uniqueId," +
                "tcm.closeDate as closeDate," +
                "tcm.matchDate as matchDate," +
                "tcm.matchMessage as matchMessage," +
                "tcm.matchProgress as matchProgress " +
                "FROM TblBasketballMatch tcm WHERE tcm.status =:status AND tcm.isDeleted =:isDeleted AND tcm.uniqueId =:uniqueId";
        Query queryObj = getSession().createQuery(hqlQuery);
        queryObj.setParameter("status", "A");
        queryObj.setParameter("isDeleted", "N");
        queryObj.setParameter("uniqueId", uniqueId);
        return (TblBasketballMatch) queryObj.setResultTransformer(Transformers.aliasToBean(TblBasketballMatch.class)).uniqueResult();

//        Criteria createCriteria = getSession().createCriteria(TblBasketballMatch.class);
//        createCriteria.add(Restrictions.eq("status", "A"));
//        createCriteria.add(Restrictions.eq("isDeleted", "N"));
//        createCriteria.add(Restrictions.eq("uniqueId", uniqueId));
//
//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.property("id"), "id");
//        projectionList.add(Projections.property("uniqueId"), "uniqueId");
//        projectionList.add(Projections.property("closeDate"), "closeDate");
//        projectionList.add(Projections.property("matchDate"), "matchDate");
//        projectionList.add(Projections.property("matchMessage"), "matchMessage");
//        projectionList.add(Projections.property("matchProgress"), "matchProgress");
//
//        createCriteria.setProjection(projectionList)
//                .setResultTransformer(new AliasToBeanResultTransformer(TblBasketballMatch.class));
//
//        return (TblBasketballMatch) createCriteria.uniqueResult();

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchScore(int matchUniqueId) {

        String hqln = "SELECT " +
                "tcm.uniqueId as unique_id," +
                "tcm.team1Id as team_1_id," +
                "tcm.team2Id as team_2_id," +
                "tcm.team1Run as team1_run," +
                "tcm.team1Wicket as team1_wicket," +
                "tcm.team1Overs as team1_overs," +
                "tcm.team2Run as team2_run," +
                "tcm.team2Wicket as team2_wicket," +
                "tcm.team2Overs as team2_overs," +
                "tcm.scoreBoardNotes as score_board_notes," +
                "tct.fullName as team_1_name," +
                "tct.sortName as team_1_sort_name," +
                "tct1.fullName as team_2_name," +
                "tct1.sortName as team_2_sort_name," +
                "tcms.shortScore as short_score  " +
                "FROM " +
                "TblBasketballMatch tcm " +
                "LEFT JOIN TblBasketballMatchesScorecard tcms ON (tcms.matchUniqueId=tcm.uniqueId) " +
                "LEFT JOIN TblBasketballTeam tct ON (tcm.team1Id=tct.id) " +
                "LEFT JOIN TblBasketballTeam tct1 ON (tcm.team2Id=tct1.id) " +
                "WHERE " +
                "tcm.uniqueId=:uniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("uniqueId", matchUniqueId);
        HashMap<String, Object> result = (HashMap<String, Object>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        return result;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchFullScore(int matchUniqueId) {
        String hqln = "SELECT " + "shortScore as short_score, fullScore as full_score "
                + "FROM  TblBasketballMatchesScorecard  " + "WHERE " + "matchUniqueId=:matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId);
        HashMap<String, Object> result = (HashMap<String, Object>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getPlayersByMatch(int seriesId, int gameTypeId, int matchUniqueId) {

        String hqln = "SELECT " +
                "tcmp.teamId as team_id," +
                "tcmp.playingRole as playing_role," +
                "tcmp.credits as credits," +
                "tcmp.isInPlayingSquad as is_in_playing_squad," +
                "tcmp.isInSubstituteSquad as is_in_substitute_squad," +
                "tcmp.image as image," +
                "tcmp.selectedByCount as selectedByCount," +
                "tcmp.selectedAsCaptionCount as selectedAsCaptionCount," +
                "tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount," +
                "tcmp.selectedAsTrumpCount as selectedAsTrumpCount, " +
                "tcsp.points AS series_total_points, " +
                "tc.name as country_name," +
                "tcp.name as name," +
                "tcp.uniqueId as uniqueId," +
                "tcp.bets as bets," +
                "tcp.bowls as bowls," +
                "tcp.dob as dob " +
                "FROM  " +
                "TblBasketballMatchPlayer tcmp " +
                "LEFT JOIN TblBasketballPlayer tcp ON (tcmp.playerUniqueId=tcp.uniqueId) " +
                "LEFT JOIN TblCountry tc ON(tcp.countryId=tc.id) " +
                "LEFT JOIN TblBasketballSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcmp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) " +
                "WHERE " +
                "tcmp.matchUniqueId = :matchUniqueId " +
                "AND tcmp.status = 'A' " +
                "AND tcmp.isDeleted='N' " +
                "ORDER BY coalesce(tcmp.playingRole) ASC , FIELD(tcmp.playingRole, 'Point guard','Shooting guard','Small forward','Power forward','Center',' ','') ASC";
        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("seriesId", seriesId).setParameter("gameTypeId", gameTypeId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;

    }

    @Transactional(readOnly = true)
    public Object getSeriesByPlayerStatistics(int matchUniqueId, int playerUniqueId) {

        TblBasketballMatch matchDataOnly = getMatchDataOnly(matchUniqueId);

        int seriesId = matchDataOnly.getSeriesId();
        int gameTypeId = matchDataOnly.getGameTypeId();

        String hqln = "SELECT "
                + "tcm.uniqueId as uniqueId, tcm.name as matchName, tcm.shortTitle as shortTitle, tcm.matchDate as matchDate, tcm.totalCustomerTeam as totalCustomerTeam, "
                + "tcmp.playerUniqueId as playerUniqueId, tcmp.points as points, tcmp.credits as credits, tcmp.selectedByCount as selectedByCount, tcmp.selectedAsCaptionCount as selectedAsCaptionCount, tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount, tcmp.selectedAsTrumpCount as selectedAsTrumpCount, tcmp.dreamTeamPlayer as dreamTeamPlayer "
                + "FROM TblBasketballMatchPlayer tcmp "
                + "LEFT JOIN TblBasketballMatch tcm ON tcm.uniqueId=tcmp.matchUniqueId " + "WHERE "
                + "tcmp.playerUniqueId=:playerUniqueId AND tcmp.matchUniqueId IN (SELECT tcm.uniqueId from TblBasketballMatch tcm where tcm.seriesId=:seriesId AND tcm.gameTypeId=:gameTypeId AND tcm.status='A' and tcm.isDeleted='N') "
                + "ORDER BY tcm.matchDate DESC";

        Query queryy = getSession().createQuery(hqln).setParameter("playerUniqueId", playerUniqueId)
                .setParameter("gameTypeId", gameTypeId).setParameter("seriesId", seriesId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return "NO_RECORD";
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        for (HashMap<String, Object> hashMap : result) {

            int selected_by = (int) hashMap.get("selectedByCount");
            int selected_as_caption = (int) hashMap.get("selectedAsCaptionCount");
            int selected_as_vccaption = (int) hashMap.get("selectedAsVccaptionCount");
            int selected_as_trump = (int) hashMap.get("selectedAsTrumpCount");
            int totalCustomerTeam = (int) hashMap.get("totalCustomerTeam");

            float selectedByPer = 0;
            float selectedByCaptionPer = 0;
            float selectedByVCaptionPer = 0;
            float selectedByTrumpPer = 0;
            if (totalCustomerTeam > 0) {
                selectedByPer = (((float) selected_by) / totalCustomerTeam) * 100f;
                selectedByPer = Util.numberFormate(selectedByPer, 2);

                if (selected_by > 0) {
                    selectedByCaptionPer = (((float) selected_as_caption) / selected_by) * 100f;
                    selectedByCaptionPer = Util.numberFormate(selectedByCaptionPer, 2);

                    selectedByVCaptionPer = (((float) selected_as_vccaption) / selected_by) * 100f;
                    selectedByVCaptionPer = Util.numberFormate(selectedByVCaptionPer, 2);

                    selectedByTrumpPer = (((float) selected_as_trump) / selected_by) * 100f;
                    selectedByTrumpPer = Util.numberFormate(selectedByTrumpPer, 2);
                }

            }

            LinkedHashMap<String, Object> playerStats = new LinkedHashMap<>();
            playerStats.put("match_unique_id", hashMap.get("uniqueId"));
            playerStats.put("match_name", hashMap.get("matchName"));
            playerStats.put("short_title", hashMap.get("shortTitle"));
            playerStats.put("match_date", hashMap.get("matchDate"));
            playerStats.put("player_unique_id", hashMap.get("playerUniqueId"));
            playerStats.put("points", hashMap.get("points"));
            playerStats.put("credits", hashMap.get("credits"));
            playerStats.put("match_team_count", 0);
            playerStats.put("player_team_count", 0);
            playerStats.put("dream_team_player", hashMap.get("dreamTeamPlayer"));
            playerStats.put("selected_by", selectedByPer);
            playerStats.put("selected_as_vccaption", selectedByVCaptionPer);
            playerStats.put("selected_as_caption", selectedByCaptionPer);
            playerStats.put("selected_as_trump", selectedByTrumpPer);

            data.put(playerStats.get("match_unique_id").toString(), playerStats);

        }

        return data;
    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getTeamSettings() {
        String hqln = "SELECT " + "tcts.key as key, tcts.value as value " + "FROM TblBasketballTeamSetting tcts "
                + "ORDER BY tcts.id ASC";

        Query queryy = getSession().createQuery(hqln);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data;
        }
        for (HashMap<String, Object> hashMap : result) {
            data.put(hashMap.get("key").toString(), hashMap.get("value"));
        }

        return data;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchPlayersStats(int customerId, int matchUniqueId) {

        String hqlMtch = "SELECT tsm.totalCustomerTeam as totalCustomerTeam FROM TblBasketballMatch tsm where tsm.uniqueId=:uniqueId";

        Query queryyM = getSession().createQuery(hqlMtch).setParameter("uniqueId", matchUniqueId);

        HashMap<String, Object> resultM = (HashMap<String, Object>) queryyM
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int totalCustomerTeam = (int) resultM.get("totalCustomerTeam");

        String hql = "SELECT " +
                "tst.fullName as fullName, " +
                "tst.sortName as sortName, " +
                "tst.id as playerTeamId, " +
                "tst.logo as logo, " +
                "tsmps.pointsScored as Points_Scored, " +
                "tsmps.pointsScoredValue as Points_Scored_Value, " +
                "tsmps.rebounds as Rebounds, " +
                "tsmps.reboundsValue as Rebounds_Value, " +
                "tsmps.assists as Assists, " +
                "tsmps.assistsValue as Assists_Value, " +
                "tsmps.steals as Steals, " +
                "tsmps.stealsValue as Steals_Value, " +
                "tsmps.blocks as Blocks, " +
                "tsmps.blocksValue as Blocks_Value, " +
                "tsmps.turnOvers as Turn_Overs, " +
                "tsmps.turnOversValue as Turn_Overs_Value, " +
                "tsmp.playerUniqueId AS playerUniqueId, " +
                "tsmp.points AS points," +
                "tsmp.credits AS credits," +
                "tsmp.isInPlayingSquad AS isInPlayingSquad," +
                "tsmp.playingRole AS playingRole," +
                "tsmp.image AS image," +
                "tsmp.isInSubstituteSquad AS isInSubstituteSquad," +
                "tsmp.selectedByCount as selectedByCount," +
                "tsmp.selectedAsCaptionCount as selectedAsCaptionCount," +
                "tsmp.selectedAsVccaptionCount as selectedAsVccaptionCount," +
                "tsmp.selectedAsTrumpCount as selectedAsTrumpCount," +
                "tsmp.dreamTeamPlayer as dreamTeamPlayer, " +
                "tsp.name AS name " +

                "FROM TblBasketballMatchPlayersStat tsmps "
                + "INNER JOIN TblBasketballMatchPlayer tsmp ON (tsmps.playerUniqueId=tsmp.playerUniqueId AND tsmps.matchUniqueId = tsmp.matchUniqueId) "
                + "LEFT JOIN  TblBasketballPlayer tsp ON tsp.uniqueId = tsmps.playerUniqueId "
                + "LEFT JOIN TblBasketballTeam tst ON tst.id=tsmp.teamId "
                + "WHERE tsmps.matchUniqueId = :matchUniqueId ";

        Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<Integer, Object> myPlayers = new HashMap<>();
        if (result != null && result.size() > 0) {
            String hql2 = "SELECT "
                    + "DISTINCT tcctp.playerUniqueId as playerUniqueId "
                    + "FROM TblBasketballCustomerTeamPlyer tcctp " +
                    "WHERE tcctp.matchUniqueId = :matchUniqueId AND tcctp.customerId=:customerId";
            Query queryy2 = getSession().createQuery(hql2)
                    .setParameter("matchUniqueId", matchUniqueId)
                    .setParameter("customerId", customerId);

            List<HashMap<String, Object>> result2 = (List<HashMap<String, Object>>) queryy2
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if (result2 != null && result2.size() > 0) {
                for (HashMap<String, Object> hashMap : result2) {
                    myPlayers.put((int) hashMap.get("playerUniqueId"), hashMap.get("playerUniqueId"));
                }
            }
        }

        HashMap<String, Object> players = new HashMap<String, Object>();
        for (HashMap<String, Object> hashMap : result) {

            int selected_by = (int) hashMap.get("selectedByCount");
            int selected_as_caption = (int) hashMap.get("selectedAsCaptionCount");
            int selected_as_vccaption = (int) hashMap.get("selectedAsVccaptionCount");
            int selected_as_trump = (int) hashMap.get("selectedAsTrumpCount");

            float selectedByPer = 0;
            float selectedByCaptionPer = 0;
            float selectedByVCaptionPer = 0;
            float selectedByTrumpPer = 0;
            if (totalCustomerTeam > 0) {
                selectedByPer = (((float) selected_by) / totalCustomerTeam) * 100f;
                selectedByPer = Util.numberFormate(selectedByPer, 2);

                if (selected_by > 0) {
                    selectedByCaptionPer = (((float) selected_as_caption) / selected_by) * 100f;
                    selectedByCaptionPer = Util.numberFormate(selectedByCaptionPer, 2);

                    selectedByVCaptionPer = (((float) selected_as_vccaption) / selected_by) * 100f;
                    selectedByVCaptionPer = Util.numberFormate(selectedByVCaptionPer, 2);

                    selectedByTrumpPer = (((float) selected_as_trump) / selected_by) * 100f;
                    selectedByTrumpPer = Util.numberFormate(selectedByTrumpPer, 2);
                }

            }

            HashMap<String, Object> playerTeam = new HashMap<String, Object>();
            playerTeam.put("name", hashMap.get("fullName"));
            playerTeam.put("sort_name", hashMap.get("sortName"));
            playerTeam.put("id", hashMap.get("playerTeamId"));
            playerTeam.put("image", Util.generateImageUrl((String) hashMap.get("logo"), FileUploadConstant.TEAMCRICKET_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_TEAM));


            HashMap<String, Object> player = new HashMap<String, Object>();
            player.put("player_unique_id", hashMap.get("playerUniqueId"));
            player.put("name", hashMap.get("name"));
            player.put("position", hashMap.get("playingRole"));
            player.put("credits", hashMap.get("credits"));
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));

            player.put("points", hashMap.get("points"));
            player.put("dream_team_player", hashMap.get("dreamTeamPlayer"));
            if (myPlayers.containsKey((int) hashMap.get("playerUniqueId"))) {
                player.put("is_my_player", "Y");
            } else {
                player.put("is_my_player", "N");
            }
            player.put("selected_by", selectedByPer);
            player.put("selected_as_caption", selectedByCaptionPer);
            player.put("selected_as_vccaption", selectedByVCaptionPer);
            player.put("selected_as_trump", selectedByTrumpPer);
            player.put("match_team_count", 0);
            player.put("player_team_count", 0);
            player.put("team_data", playerTeam);

            List<HashMap<String, Object>> playerEvents = new ArrayList<HashMap<String, Object>>();

            HashMap<String, Object> event = new HashMap<String, Object>();
            event.put("key", "Points Scored");
            event.put("points", hashMap.get("Points_Scored"));
            event.put("value", hashMap.get("Points_Scored_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Rebounds");
            event.put("points", hashMap.get("Rebounds"));
            event.put("value", hashMap.get("Rebounds_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Assists");
            event.put("points", hashMap.get("Assists"));
            event.put("value", hashMap.get("Assists_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Steals");
            event.put("points", hashMap.get("Steals"));
            event.put("value", hashMap.get("Steals_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Blocks");
            event.put("points", hashMap.get("Blocks"));
            event.put("value", hashMap.get("Blocks_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Turn Overs");
            event.put("points", hashMap.get("Turn_Overs"));
            event.put("value", hashMap.get("Turn_Overs_Value"));

            playerEvents.add(event);

            player.put("player_events", playerEvents);

            players.put(player.get("player_unique_id").toString(), player);
        }

        return players;
    }

    @Transactional(readOnly = true)
    public Collection<Object> getSeries() {
        String hqln = "SELECT " + "tcs.id as id, tcs.name as name, tcs.abbr as abbr, tcs.season as season "
                + "FROM TblBasketballLeaderboardSeries tcls " + "LEFT JOIN TblBasketballSeries tcs on tcs.id=tcls.seriesId "
                + "WHERE tcs.status = 'A' AND tcs.isDeleted = 'N' " + "GROUP BY tcs.id ORDER BY tcs.leaderboardOrder ASC,tcs.id DESC";

        Query queryy = getSession().createQuery(hqln);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }
        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
            series.put("id", hashMap.get("id"));
            series.put("name", hashMap.get("name"));
            series.put("abbr", hashMap.get("abbr"));
            series.put("season", hashMap.get("season"));

            data.put(series.get("id").toString(), series);
        }

        return data.values();
    }
    @Transactional(readOnly = true)
    public Collection<Object> getSeriesForLeaderboard() {
        String hqln = "SELECT " + "tcs.id as id, tcs.name as name, tcs.abbr as abbr, tcs.season as season, " +
                "tcs.isResultDeclared as isResultDeclared, "+
                "tcs.tnc as tnc, "+
                "tcs.prizeJson as prizeJson, "+
                "tcs.totalPrice as totalPrice "
                + "FROM TblBasketballLeaderboardSeries tcls " + "LEFT JOIN TblBasketballSeries tcs on tcs.id=tcls.seriesId "
                + "WHERE tcs.status = 'A' AND tcs.isDeleted = 'N' AND tcs.isShowInLeaderboard = 'Y' " + "GROUP BY tcs.id ORDER BY tcs.leaderboardOrder ASC,tcs.id DESC";

        Query queryy = getSession().createQuery(hqln);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }
        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
            series.put("id", hashMap.get("id"));
            series.put("name", hashMap.get("name"));
            series.put("abbr", hashMap.get("abbr"));
            series.put("season", hashMap.get("season"));
            series.put("is_result_declared", hashMap.get("isResultDeclared"));
            series.put("tnc", hashMap.get("tnc"));

            if (!Util.isEmpty((String)hashMap.get("prizeJson"))) {
                JSONObject jsonObject1 = new JSONObject((String)hashMap.get("prizeJson"));
                series.put("prize_json", jsonObject1.toMap());
            }else{
                series.put("prize_json", null);
            }
            series.put("total_price", hashMap.get("totalPrice"));

            data.put(series.get("id").toString(), series);
        }

        return data.values();
    }

    @Transactional(readOnly = true)
    public Collection<Object> getSeriesLeaderboard(int customerId, int seriesId, int pageNo) {

        String hqln = "SELECT "
                + "tcls.id as id, tcls.oldRank as oldRank, tcls.newRank as newRank, tcls.oldPoint as oldPoint, tcls.newPoint as newPoint, tcls.customerId as customerId, "
                + "tc.firstname as firstname, tc.lastname as lastname, tc.teamName as teamName, tc.image as image, tc.externalImage as externalImage "
                + "FROM TblBasketballLeaderboardSeries tcls " + "LEFT JOIN TblCustomer tc on tc.id=tcls.customerId "
                + "WHERE tcls.seriesId = :seriesId AND tcls.customerId != :customerId " + "ORDER BY tcls.newRank ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId).setParameter("customerId",
                customerId);

        if (pageNo > 0) {
            int limit = 30;
            int offset = (pageNo - 1) * limit;
            queryy.setMaxResults(limit);
            queryy.setFirstResult(offset);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> userDetail = new LinkedHashMap<String, Object>();
            userDetail.put("id", hashMap.get("customerId"));
            userDetail.put("firstname", hashMap.get("firstname"));
            userDetail.put("lastname", hashMap.get("lastname"));
            userDetail.put("team_name", hashMap.get("teamName"));
            userDetail.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

            if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
                userDetail.put("image", hashMap.get("externalImage").toString());
            }

            LinkedHashMap<String, Object> team = new LinkedHashMap<String, Object>();
            team.put("id", hashMap.get("id"));
            team.put("old_rank", hashMap.get("oldRank"));
            team.put("new_rank", hashMap.get("newRank"));
            team.put("old_point", hashMap.get("oldPoint"));
            team.put("new_point", hashMap.get("newPoint"));
            team.put("user_detail", userDetail);

            data.put(team.get("id").toString(), team);
        }

        return data.values();
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getSeriesLeaderboardSelf(int customerId, int seriesId) {

        String hqln = "SELECT "
                + "tcls.id as id, tcls.oldRank as oldRank, tcls.newRank as newRank, tcls.oldPoint as oldPoint, tcls.newPoint as newPoint, tcls.customerId as customerId, "
                + "tc.firstname as firstname, tc.lastname as lastname, tc.teamName as teamName, tc.image as image, tc.externalImage as externalImage "
                + "FROM TblBasketballLeaderboardSeries tcls " + "LEFT JOIN TblCustomer tc on tc.id=tcls.customerId "
                + "WHERE tcls.seriesId = :seriesId AND tcls.customerId = :customerId ";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId).setParameter("customerId",
                customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return null;
        }

        HashMap<String, Object> hashMap = result.get(0);

        LinkedHashMap<String, Object> userDetail = new LinkedHashMap<String, Object>();
        userDetail.put("id", hashMap.get("customerId"));
        userDetail.put("firstname", hashMap.get("firstname"));
        userDetail.put("lastname", hashMap.get("lastname"));
        userDetail.put("team_name", hashMap.get("teamName"));
        userDetail.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

        if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
            userDetail.put("image", hashMap.get("externalImage").toString());
        }

        LinkedHashMap<String, Object> team = new LinkedHashMap<String, Object>();
        team.put("id", hashMap.get("id"));
        team.put("old_rank", hashMap.get("oldRank"));
        team.put("new_rank", hashMap.get("newRank"));
        team.put("old_point", hashMap.get("oldPoint"));
        team.put("new_point", hashMap.get("newPoint"));
        team.put("user_detail", userDetail);

        return team;
    }

    @Transactional(readOnly = true)
    public Collection<Object> getSeriesLeaderboardByWeek(int customerId, int seriesId, int pageNo, String searchDate,boolean isAffiliate) {
        String affiliateCondition = isAffiliate ? "AND tcls.customerId IN(select tc.id from TblCustomer tc where tc.usedReferralUserId =:customerId AND tc.isAffiliate ='0') " : "";
        String hqln = "SELECT "
                + "tcls.id as id, tcls.oldRank as oldRank, tcls.newRank as newRank, tcls.oldPoint as oldPoint, tcls.newPoint as newPoint, tcls.customerId as customerId, "
                + "tc.firstname as firstname, tc.lastname as lastname, tc.teamName as teamName, tc.image as image, tc.externalImage as externalImage "
                + "FROM TblBasketballLeaderboardSeriesWeek tcls " + "LEFT JOIN TblCustomer tc on tc.id=tcls.customerId "
                + "WHERE tcls.seriesId = :seriesId " +
                "AND tcls.customerId != :customerId " + affiliateCondition +
                "AND tcls.weekNo = :weekNo "
                + "ORDER BY tcls.newRank ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId)
                .setParameter("customerId", customerId).setParameter("weekNo", searchDate);

        if (pageNo > 0) {
            int limit = 30;
            int offset = (pageNo - 1) * limit;
            queryy.setMaxResults(limit);
            queryy.setFirstResult(offset);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> userDetail = new LinkedHashMap<String, Object>();
            userDetail.put("id", hashMap.get("customerId"));
            userDetail.put("firstname", hashMap.get("firstname"));
            userDetail.put("lastname", hashMap.get("lastname"));
            userDetail.put("team_name", hashMap.get("teamName"));
            userDetail.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

            if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
                userDetail.put("image", hashMap.get("externalImage").toString());
            }

            LinkedHashMap<String, Object> team = new LinkedHashMap<String, Object>();
            team.put("id", hashMap.get("id"));
            team.put("old_rank", hashMap.get("oldRank"));
            team.put("new_rank", hashMap.get("newRank"));
            team.put("old_point", hashMap.get("oldPoint"));
            team.put("new_point", hashMap.get("newPoint"));
            team.put("user_detail", userDetail);

            data.put(team.get("id").toString(), team);
        }

        return data.values();
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getSeriesLeaderboardSelfByWeek(int customerId, int seriesId, String searchDate) {

        String hqln = "SELECT "
                + "tcls.id as id, tcls.oldRank as oldRank, tcls.newRank as newRank, tcls.oldPoint as oldPoint, tcls.newPoint as newPoint, tcls.customerId as customerId, "
                + "tc.firstname as firstname, tc.lastname as lastname, tc.teamName as teamName, tc.image as image, tc.externalImage as externalImage "
                + "FROM TblBasketballLeaderboardSeriesWeek tcls " + "LEFT JOIN TblCustomer tc on tc.id=tcls.customerId "
                + "WHERE tcls.seriesId = :seriesId AND tcls.customerId = :customerId AND tcls.weekNo = :weekNo";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId)
                .setParameter("customerId", customerId).setParameter("weekNo", searchDate);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return null;
        }

        HashMap<String, Object> hashMap = result.get(0);

        LinkedHashMap<String, Object> userDetail = new LinkedHashMap<String, Object>();
        userDetail.put("id", hashMap.get("customerId"));
        userDetail.put("firstname", hashMap.get("firstname"));
        userDetail.put("lastname", hashMap.get("lastname"));
        userDetail.put("team_name", hashMap.get("teamName"));
        userDetail.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

        if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
            userDetail.put("image", hashMap.get("externalImage").toString());
        }

        LinkedHashMap<String, Object> team = new LinkedHashMap<String, Object>();
        team.put("id", hashMap.get("id"));
        team.put("old_rank", hashMap.get("oldRank"));
        team.put("new_rank", hashMap.get("newRank"));
        team.put("old_point", hashMap.get("oldPoint"));
        team.put("new_point", hashMap.get("newPoint"));
        team.put("user_detail", userDetail);

        return team;
    }

    @Transactional(readOnly = true)
    public Collection<Object> getSeriesLeaderboardCustomerMatches(int authUserId, int seriesId, int customerId,
                                                                  String weekNo) {
        String hqln = "SELECT "
                + "tclm.id as id, tclm.customerTeamId as customerTeamId, tclm.newRank as newRank, tclm.newPoint as newPoint, tclm.matchId as matchId, tclm.matchUniqueId as matchUniqueId, "
                + "tcm.name as matchName, tcm.shortTitle as matchShortTitle, tcm.subtitle as matchSubtitle, tcm.matchDate as matchDate "
                + "FROM TblBasketballLeaderboardMatch tclm "
                + "LEFT JOIN TblBasketballMatch tcm on tcm.uniqueId=tclm.matchUniqueId "
                + "WHERE tclm.seriesId = :seriesId AND tclm.customerId = :customerId ";

        if (!Util.isEmpty(weekNo)) {
            hqln += " AND tclm.weekNo=:weekNo ";
        }

        hqln += "ORDER BY tclm.id DESC";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId).setParameter("customerId",
                customerId);

        if (!Util.isEmpty(weekNo)) {
            queryy.setParameter("weekNo", weekNo);
        }

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }

        for (HashMap<String, Object> hashMap : result) {

            LinkedHashMap<String, Object> matchDetail = new LinkedHashMap<String, Object>();
            matchDetail.put("id", hashMap.get("matchId"));
            matchDetail.put("unique_id", hashMap.get("matchUniqueId"));
            matchDetail.put("name", hashMap.get("matchName"));
            matchDetail.put("short_title", hashMap.get("matchShortTitle"));
            matchDetail.put("subtitle", hashMap.get("matchSubtitle"));
            matchDetail.put("match_date", hashMap.get("matchDate"));

            LinkedHashMap<String, Object> leaderboard = new LinkedHashMap<String, Object>();
            leaderboard.put("id", hashMap.get("id"));
            leaderboard.put("customer_team_id", hashMap.get("customerTeamId"));
            leaderboard.put("new_rank", hashMap.get("newRank"));
            leaderboard.put("new_point", hashMap.get("newPoint"));
            leaderboard.put("match_detail", matchDetail);

            data.put(leaderboard.get("id").toString(), leaderboard);
        }

        return data.values();
    }

    @Transactional(readOnly = true)
    public Collection<Object> getSeriesForWeeklyLeaderboard() {
        String hqln = "SELECT " + "tcs.id as id, tcs.name as name, tcs.abbr as abbr, tcs.season as season "
                + "FROM TblBasketballLeaderboardSeriesWeek tclsw "
                + "LEFT JOIN TblBasketballSeries tcs on tcs.id=tclsw.seriesId "
                + "WHERE tcs.status = 'A' AND tcs.isDeleted = 'N' " + "GROUP BY tcs.id ORDER BY tcs.leaderboardOrder ASC, tcs.id DESC";

        Query queryy = getSession().createQuery(hqln);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (result.isEmpty()) {
            return data.values();
        }
        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
            series.put("id", hashMap.get("id"));
            series.put("name", hashMap.get("name"));
            series.put("abbr", hashMap.get("abbr"));
            series.put("season", hashMap.get("season"));

            data.put(series.get("id").toString(), series);
        }

        return data.values();
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getSeriesWeeklyLeaderboardWeek(int seriesId) {
        String hqln = "SELECT " + "MAX(matchDate) as maxDate, MIN(matchDate) as minDate "
                + "FROM TblBasketballLeaderboardMatch tclm " + "WHERE tclm.seriesId = :seriesId ";

        Query queryy = getSession().createQuery(hqln).setParameter("seriesId", seriesId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return null;
        }
        HashMap<String, Object> hashMap = result.get(0);
        if (hashMap.get("maxDate") == null) {
            return null;
        }
        return hashMap;
    }

}
