package com.choic11.repository.soccer;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.TblTemplate;
import com.choic11.model.customer.TblCustomerWalletHistory;
import com.choic11.model.response.BaseResponse;
import com.choic11.model.soccer.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Repository("SoccerContestRepository")
public class ContestRepository {

    @Autowired
    private SessionFactory factory;

    private Session getNewSession() {

        return factory.openSession();

    }


    private Object  lockObject = new Object();

    private Session getSession() {
        Session session = factory.getCurrentSession();
        if (session == null) {
            return factory.openSession();
        }
        return session;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContest(int matchId, int matchUniqueId, Integer UserId) {

        String hqln = "SELECT "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "tccf.totalJoinedTeam as total_joined_team, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height, "
                + "tccc.id as cat_id, tccc.name as name, tccc.description as description, tccc.image as image, tccc.isDiscounted as is_discounted, tccc.orderPos as order_pos, tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, "
                + "tccf.id as id, tccf.totalTeam as total_team, tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow,tccf.contestId as masterContestId, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON (tccc.id=tccf.categoryId ) "
                + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "WHERE  tccf.matchUniqueId=:matchUniqueId AND tccf.status='A' AND tccf.isDeleted='N' AND tccf.isPrivate='N' AND tccf.isBeatTheExpert='N' AND (tccf.totalTeam-tccf.totalJoinedTeam)>0  ORDER BY tccc.orderPos ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getCustomerMatchContest(int matchId, int matchUniqueId, Integer UserId) {

        String hqln1 = "SELECT " + "tccct.matchContestId as matchContestId from TblSoccerCustomerContestTeams tccct "
                + "LEFT JOIN TblSoccerContestMatch tccm on tccm.id=tccct.matchContestId"
                + " WHERE tccm.status='A' AND tccm.isDeleted='N' AND tccm.isAbondant='N' AND tccm.isBeatTheExpert='N' AND tccct.matchUniqueId=:matchUniqueId AND tccct.customerId=:customerId";

        Query queryy1 = getSession().createQuery(hqln1).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("customerId", UserId);
        List<HashMap<String, Object>> customerContests = (List<HashMap<String, Object>>) queryy1
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        if (customerContests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> customerMatchContests = new ArrayList<Integer>();
        for (HashMap<String, Object> hashMap : customerContests) {
            customerMatchContests.add((int) hashMap.get("matchContestId"));
        }

        LinkedHashMap<Integer, Object> customerContestTeams = getCustomerContestTeams(UserId, customerMatchContests);

        String hqln = "SELECT "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height, "
                + "tccc.id as cat_id, tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, tccc.name as name, tccc.description as description, tccc.image as image, tccc.isDiscounted as is_discounted, tccc.orderPos as order_pos, "
                + "tccf.totalJoinedTeam as total_joined_team, tccf.id as id ,tccf.totalTeam as total_team,tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow,tccf.contestId as masterContestId, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON (tccc.id=tccf.categoryId ) "
                + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "WHERE tccf.id IN(:customerMatchContests)  ORDER BY tccc.orderPos ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId)
                .setParameterList("customerMatchContests", customerMatchContests);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        for (HashMap<String, Object> hashMap : result) {

            int matchContestId = (int) hashMap.get("id");

            if (customerContestTeams.containsKey(matchContestId)) {
                hashMap.put("contestTeams", customerContestTeams.get(matchContestId));
            }

        }

        return result;

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<Integer, Object> getCustomerContestTeams(int customerId, List<Integer> matchContestIds) {
        String hqln = "SELECT  "
                + "tccc.matchContestId as match_contest_id, tccc.customerTeamId as team_id, tccc.newRank as new_rank, tccc.newPoints as total_points, tccc.winAmount as win_amount, tccc.refundAmount as refund_amount, tccc.taxAmount as tax_amount, tccc.oldRank as old_rank, tccc.winGadget as win_gadget, "
                + "tcct.name as team_name " + "FROM  TblSoccerCustomerContest tccc "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON tcct.id=tccc.customerTeamId "
                + "WHERE tccc.customerId = :customerId AND tccc.matchContestId IN (:matchContestIds) "
                + " ORDER BY tccc.newRank ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", customerId)
                .setParameterList("matchContestIds", matchContestIds);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        LinkedHashMap<Integer, Object> data = new LinkedHashMap<Integer, Object>();

        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> contestTeams = null;
            if (data.containsKey((int) hashMap.get("match_contest_id"))) {
                contestTeams = (LinkedHashMap<String, Object>) data.get((int) hashMap.get("match_contest_id"));
                contestTeams.put(hashMap.get("team_id").toString(), hashMap);
            } else {
                contestTeams = new LinkedHashMap<String, Object>();
                contestTeams.put(hashMap.get("team_id").toString(), hashMap);
                data.put((int) hashMap.get("match_contest_id"), contestTeams);
            }
        }

        return data;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchCategoryContest(int matchId, int matchUniqueId, Integer UserId,
                                                                 int catId) {

        String hqln = "SELECT  "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, "
                + "tccf.id as id, tccf.totalTeam as total_team, tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.totalJoinedTeam as total_team_current_join, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height,tccf.contestId as masterContestId, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf " + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + " LEFT JOIN TblSoccerContestCategory tccc ON (tccf.categoryId=tccc.id ) "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "where  tccf.matchId=:matchId AND tccf.status='A' AND tccf.isDeleted='N' AND tccf.isPrivate='N' AND tccf.isBeatTheExpert='N' AND (tccf.totalTeam-tccf.totalJoinedTeam)>0 ";

        if (catId > 0) {
            hqln += "AND  tccf.categoryId=:categoryId";
        }

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchId",
                matchId);
        if (catId > 0) {
            queryy.setParameter("categoryId", catId);
        }
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getContestWinnerBreakup(int contestId) {
        String hqln = "SELECT id as id, contestJson as contest_json, gadgetDisclaimer as gadget_disclaimer from TblSoccerContestMatch where status='A' AND isDeleted='N' AND id=:id";

        Query queryy = getSession().createQuery(hqln).setParameter("id", contestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return (result.size() == 0) ? null : result.get(0);

    }


    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContestSDetail(int matchContestId, Integer UserId) {

        String hqln = "SELECT "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height, "
                + "tccc.id as cat_id, tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, tccc.name as name, tccc.description as description, tccc.image as image, tccc.isDiscounted as is_discounted, tccc.orderPos as order_pos, "
                + "tccf.maxEntryFees as max_entry_fees, tccf.entryFeeMultiplier as entry_fee_multiplier, tccf.isBeatTheExpert as is_beat_the_expert, tccf.totalJoinedTeam as total_joined_team, tccf.id as id , tccf.totalTeam as total_team, tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow,tccf.contestId as masterContestId, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON (tccc.id=tccf.categoryId ) "
                + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "WHERE tccf.id=:id AND tccf.status='A' AND tccf.isDeleted='N'";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("id",
                matchContestId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchPrivateContestDetail(String slug, Integer UserId, int matchUniqueId) {
        String hqln = "SELECT "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height, "
                + "tccc.id as cat_id, tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, tccc.name as name, tccc.description as description, tccc.image as image, tccc.isDiscounted as is_discounted, tccc.orderPos as order_pos, "
                + "tccf.matchUniqueId as match_unique_id, tccf.maxEntryFees as max_entry_fees, tccf.entryFeeMultiplier as entry_fee_multiplier, tccf.isBeatTheExpert as is_beat_the_expert, tccf.totalJoinedTeam as total_joined_team, tccf.id as id ,tccf.totalTeam as total_team, tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow,tccf.contestId as masterContestId, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON (tccc.id=tccf.categoryId ) "
                + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "WHERE  tccf.slug=:slug AND tccf.status='A' AND tccf.isDeleted='N'";

        if (matchUniqueId > 0) {
            hqln += " AND tccf.matchUniqueId=:matchUniqueId";
        }

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("slug", slug);
        if (matchUniqueId > 0) {
            queryy.setParameter("matchUniqueId", matchUniqueId);
        }
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchContestShareDetail(int matchContestId) {
        String hqln = "SELECT tccm.slug as slug, tccm.isBeatTheExpert as is_beat_the_expert, tcm.image as image,tcm.name as name from TblSoccerContestMatch tccm LEFT JOIN TblSoccerMatch tcm ON (tccm.matchId=tcm.id) where (tccm.id=:matchContestId) AND tccm.status='A' AND tccm.isDeleted='N' AND tcm.status='A' AND tcm.isDeleted='N'";

        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return (result.size() == 0) ? null : result.get(0);

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchContestPdf(int matchContestId) {

        String hqln = "SELECT COALESCE(pdf,'') as contest_pdf from TblSoccerContestMatch where id=:id";
        Query queryy = getSession().createQuery(hqln).setParameter("id", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return (result.size() == 0) ? null : result.get(0);

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getBeatTheExpertTeamId(int matchContestId) {
        String hqln = "SELECT teamId as team_id from TblSoccerContestMatch where id=:id";
        Query queryy = getSession().createQuery(hqln).setParameter("id", matchContestId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return (result.size() == 0) ? null : result.get(0);

    }

    @Transactional
    public int updateBeatTheExpertTeam(int matchContestId, int customerTeamId) {
        String hqln = "UPDATE TblSoccerContestMatch tccm set tccm.teamId=:teamId where tccm.id=:id";

        Query queryy = getSession().createQuery(hqln).setParameter("teamId", customerTeamId).setParameter("id",
                matchContestId);

        return queryy.executeUpdate();

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getContestTeams(int userId, int matchUniqueId, int matchContestId, int pageNo,
                                                         TblSoccerMatch matchDataOnly, int beatExpertTeamId) {
        List<HashMap<String, Object>> output = new ArrayList<HashMap<String, Object>>();

        if (pageNo == 1) {

            String hql = "SELECT "
                    + "tcct.name as name, tcct.moreName as more_name, tcct.customerTeamName as customer_team_name, "
                    + "tc.teamName as team_name, tc.firstname as firstname, tc.lastname as lastname, tc.image as image, tc.externalImage as externalImage, "
                    + " tccc.customerTeamId as customer_team_id, tccc.customerId as customer_id, tccc.oldRank as old_rank, tccc.newRank as new_rank, tccc.newPoints as new_points, tccc.winAmount as win_amount, tccc.winGadget as win_gadget, tccc.refundAmount as refund_amount, tccc.taxAmount as tax_amount, tccc.entryFees as entry_fees "
                    + "FROM TblSoccerCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON tccc.customerId=tc.id "
                    + "LEFT JOIN TblSoccerCustomerTeam tcct ON tcct.id=tccc.customerTeamId "
                    + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccc.matchContestId=:matchContestId AND tccc.customerId=:customerId";

            if (!matchDataOnly.getMatchProgress().equals("F")) {
                hql += " ORDER BY tccc.newRank ASC";
            } else {
                hql += "  ORDER BY tcct.id ASC";
            }

            Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueId)
                    .setParameter("matchContestId", matchContestId).setParameter("customerId", userId);
            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            for (HashMap<String, Object> hashMap : result) {
                HashMap<String, Object> teams = new HashMap<String, Object>();
                teams.put("customer_id", hashMap.get("customer_id"));
                teams.put("firstname", hashMap.get("firstname"));
                teams.put("lastname", hashMap.get("lastname"));
                teams.put("customer_team_name",
                        Util.isEmpty((String) hashMap.get("customer_team_name")) ? hashMap.get("team_name")
                                : hashMap.get("customer_team_name"));
                teams.put("team_name",
                        ((int) hashMap.get("more_name") == 0) ? hashMap.get("name") : hashMap.get("more_name"));
                teams.put("team_id", hashMap.get("customer_team_id"));
                teams.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
                if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
                    teams.put("image", hashMap.get("externalImage").toString());
                }
                teams.put("old_rank", hashMap.get("old_rank"));
                teams.put("new_rank", hashMap.get("new_rank"));
                teams.put("total_points", hashMap.get("new_points"));
                teams.put("win_amount", (Float) hashMap.get("win_amount") + (Float) hashMap.get("tax_amount"));
                teams.put("refund_amount", hashMap.get("refund_amount"));
                teams.put("tax_amount", hashMap.get("tax_amount"));
                teams.put("user_entry_fees", hashMap.get("entry_fees"));
                teams.put("win_gadget", hashMap.get("win_gadget"));
                output.add(teams);
            }

        }

        String hql1 = "SELECT "
                + "tcct.name as name, tcct.moreName as more_name, tcct.customerTeamName as customer_team_name, "
                + "tc.teamName as team_name, tc.firstname as firstname, tc.lastname as lastname, tc.image as image, tc.externalImage as externalImage, "
                + "tccc.customerTeamId as customer_team_id, tccc.customerId as customer_id, tccc.oldRank as old_rank, tccc.newRank as new_rank, tccc.newPoints as new_points, tccc.winAmount as win_amount, tccc.winGadget as win_gadget, tccc.refundAmount as refund_amount, tccc.taxAmount as tax_amount, tccc.entryFees as entry_fees "
                + "FROM TblSoccerCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON tccc.customerId=tc.id "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON tcct.id=tccc.customerTeamId "
                + "WHERE tccc.matchUniqueId=:matchUniqueId AND tccc.matchContestId=:matchContestId AND tccc.customerId!=:customerId AND tccc.customerTeamId!=:beatExpertTeamId";

        if (!matchDataOnly.getMatchProgress().equals("F")) {
            hql1 += " ORDER BY tccc.newRank ASC";
        } else {
            hql1 += "  ORDER BY tcct.id ASC";
        }

        Query queryyy = getSession().createQuery(hql1).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("matchContestId", matchContestId).setParameter("customerId", userId)
                .setParameter("beatExpertTeamId", beatExpertTeamId);

        if (pageNo > 0) {
            int limit = 70;
            int offset = (pageNo - 1) * limit;
            queryyy.setFirstResult(offset);
            queryyy.setMaxResults(limit);
        }
        List<HashMap<String, Object>> resultt = (List<HashMap<String, Object>>) queryyy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        for (HashMap<String, Object> hashMap : resultt) {
            HashMap<String, Object> teams = new HashMap<String, Object>();
            teams.put("customer_id", hashMap.get("customer_id"));
            teams.put("firstname", hashMap.get("firstname"));
            teams.put("lastname", hashMap.get("lastname"));
            teams.put("customer_team_name",
                    Util.isEmpty((String) hashMap.get("customer_team_name")) ? hashMap.get("team_name")
                            : hashMap.get("customer_team_name"));
            teams.put("team_name",
                    ((int) hashMap.get("more_name") == 0) ? hashMap.get("name") : hashMap.get("more_name"));
            teams.put("team_id", hashMap.get("customer_team_id"));
            teams.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

            if (hashMap.get("externalImage") != null && !Util.isEmpty(hashMap.get("externalImage").toString())) {
                teams.put("image", hashMap.get("externalImage").toString());
            }

            teams.put("old_rank", hashMap.get("old_rank"));
            teams.put("new_rank", hashMap.get("new_rank"));
            teams.put("total_points", hashMap.get("new_points"));
            teams.put("win_amount", (Float) hashMap.get("win_amount") + (Float) hashMap.get("tax_amount"));
            teams.put("refund_amount", hashMap.get("refund_amount"));
            teams.put("tax_amount", hashMap.get("tax_amount"));
            teams.put("user_entry_fees", hashMap.get("entry_fees"));
            teams.put("win_gadget", hashMap.get("win_gadget"));
            output.add(teams);
        }

        return output;
    }

    @Transactional(readOnly = true)
    public long getMatchCustomerContestCountByMatchUniqueId(Integer UserId, int matchUniqueId) {

        String hqln = "SELECT " + "COUNT (DISTINCT tccc.matchContestId ) as id  "
                + "FROM  TblSoccerCustomerContest tccc "
                + "RIGHT JOIN TblSoccerContestMatch tccm ON (tccm.id=tccc.matchContestId) " + "WHERE "
                + "tccc.customerId = :customerId AND tccc.matchUniqueId = :matchUniqueId  AND tccm.isAbondant='N' AND tccm.isDeleted='N'";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return (long) result.get(0).get("id");

    }

    @Transactional(readOnly = true)
    public int getContestTeamsCount(int matchUniqueId, int matchContestId) {

        String hqln = "SELECT " + "count(id) as id " + "from TblSoccerCustomerContest tccc " + "where "
                + "tccc.matchUniqueId=:matchUniqueId AND tccc.matchContestId=:matchContestId";
        Query queryy = getSession().createQuery(hqln).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("matchContestId", matchContestId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return Integer.parseInt(result.get(0).get("id").toString());
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getBeatExpertAdminTeam(int matchUniqueId, int matchContestId) {

        String hqln = "SELECT " + "tcccat.image as cat_image, "
                + "tcct.name as name, tcct.moreName as more_name, tcct.customerTeamName as customer_team_name, tcct.customerId as customer_id, "
                + "tc.teamName as team_name, tc.firstname as firstname, tc.lastname as lastname, tc.image as image, "
                + "tccc.customerTeamId as customer_team_id, tccc.customerId as customer_id, tccc.oldRank as old_rank, tccc.newRank as new_rank, tccc.newPoints as new_points, tccc.winAmount as win_amount, tccc.refundAmount as refund_amount, tccc.taxAmount as tax_amount, tccc.entryFees as entry_fees "
                + "from TblSoccerCustomerContest tccc " + "LEFT JOIN TblCustomer tc ON(tccc.customerId=tc.id) "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON(tcct.id=tccc.customerTeamId) "
                + "LEFT JOIN TblSoccerContestMatch tccm ON(tccm.id=:id) "
                + "LEFT JOIN TblSoccerContestCategory tcccat ON(tcccat.id=tccm.categoryId) " + "where "
                + "tccc.matchUniqueId=:matchUniqueId AND tccc.matchContestId=:matchContestId AND tccc.customerTeamId=tccm.teamId";
        Query queryy = getSession().createQuery(hqln).setParameter("id", matchContestId)
                .setParameter("matchUniqueId", matchUniqueId).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> teams = null;

        if (result.size() > 0) {
            HashMap<String, Object> hashMap = result.get(0);
            teams.put("customer_id", hashMap.get("customer_id"));
            teams.put("firstname", hashMap.get("firstname"));
            teams.put("lastname", hashMap.get("lastname"));
            teams.put("customer_team_name",
                    Util.isEmpty((String) hashMap.get("customer_team_name")) ? hashMap.get("team_name")
                            : hashMap.get("customer_team_name"));
            teams.put("team_name",
                    ((int) hashMap.get("more_name") == 0) ? hashMap.get("name") : hashMap.get("more_name"));
            teams.put("team_id", hashMap.get("customer_team_id"));
            teams.put("image",
                    !Util.isEmpty((String) hashMap.get("image"))
                            ? FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL + hashMap.get("image")
                            : FileUploadConstant.NO_IMG_URL);
            teams.put("old_rank", hashMap.get("old_rank"));
            teams.put("new_rank", hashMap.get("new_rank"));
            teams.put("total_points", hashMap.get("new_points"));
            teams.put("win_amount", (Float) hashMap.get("win_amount") + (Float) hashMap.get("tax_amount"));
            teams.put("refund_amount", hashMap.get("refund_amount"));
            teams.put("tax_amount", hashMap.get("tax_amount"));
            teams.put("user_entry_fees", hashMap.get("entry_fees"));
        }

        return teams;

    }

    @Transactional(readOnly = true)
    public int ifTeamAlreadyJoinInContest(Integer UserId, Integer[] customerTeamIds, int matchContestId) {
        if (customerTeamIds == null || customerTeamIds.length == 0) {
            return 0;
        }

        String hqln = "SELECT " + "id " + "FROM TblSoccerCustomerContest " + "WHERE "
                + "customerId =:customerId AND  customerTeamId IN(:customerTeamId) AND matchContestId=:matchContestId";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId)
                .setParameterList("customerTeamId", customerTeamIds).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result.size();

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> contestDetailForJoin(Integer UserId, int matchContestId) {
        String hqln = "SELECT "
                + "tcm.matchDate as match_date, tcm.name as match_name, tcm.uniqueId as unique_id, "
                + "tccc.cashBonusUsedValue as cat_cash_bonus_used_value, tccc.cashBonusUsedType as cat_cash_bonus_used_type, "
                + "tccm.id as id, tccm.multiTeamAllowed as multi_team_allowed, tccm.entryFees as entry_fees, tccm.maxEntryFees as max_entry_fees, tccm.isBeatTheExpert as is_beat_the_expert, tccm.totalTeam as total_team, tccm.perUserTeamAllowed as per_user_team_allowed, tccm.totalJoinedTeam as total_joined_teams_count, tccm.cashBonusUsedType as cash_bonus_used_type, tccm.cashBonusUsedValue as cash_bonus_used_value,  "
                + "tccct.customerTeamIds as customer_team_ids "
                + "FROM TblSoccerContestMatch tccm "
                + "LEFT JOIN TblSoccerMatch tcm ON(tcm.id=tccm.matchId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON(tccc.id=tccm.categoryId) "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccm.id=tccct.matchContestId AND tccct.customerId=:customerId)  "
                + "WHERE " + "tccm.id = :id AND tccm.status='A' AND tccm.isDeleted='N'";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("id",
                matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return (result.size() == 0) ? null : result.get(0);

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> contestDetailMini(int matchContestId) {
        String hqln = "SELECT "
                + "tccm.id as id, tccm.isBeatTheExpert as isBeatTheExpert, tccm.teamId as teamId, tccm.entryFees as entryFees "
                + "FROM TblSoccerContestMatch tccm " + "WHERE "
                + "tccm.id = :id AND tccm.status='A' AND tccm.isDeleted='N'";
        Query queryy = getSession().createQuery(hqln).setParameter("id", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return (result.size() == 0) ? null : result.get(0);

    }

    @Async
    @Transactional
    public void createDuplicateMatchContest(int matchContestId) {

        String hqln = "SELECT "
                + "tccf1.id as parent_match_contest_id, tccf1.duplicateCount as parent_duplicate_count, tccf1.duplicateCreatedCount as parent_duplicate_created_count, "
                + "tccm.matchUniqueId as match_unique_id, tccm.totalTeam as total_team, tccm.totalJoinedTeam as total_joined_team, tccm.duplicateCount as duplicate_count, tccm.duplicateCreatedCount as duplicate_created_count , tccm.isDuplicateAllow as is_duplicate_allow "
                + "FROM TblSoccerContestMatch tccm "
                + "LEFT JOIN TblSoccerContestMatch tccf1 ON(tccm.parentId=tccf1.id AND tccf1.status='A' AND tccf1.isDeleted='N' AND tccf1.isDuplicateAllow='Y' AND tccf1.isPrivate='N' AND tccf1.isBeatTheExpert='N' AND tccf1.duplicateCount>tccf1.duplicateCreatedCount) "
                + "where "
                + "tccm.status='A' AND tccm.isDeleted='N' AND tccm.isDuplicatedCreated='N' AND tccm.isPrivate='N' AND tccm.isBeatTheExpert='N' AND tccm.id=:matchContestId ";

        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (result.size() > 0) {

            HashMap<String, Object> outputData = result.get(0);
            int duplicateCount = (int) outputData.get("duplicate_count");
            int duplicateCreatedCount = (int) outputData.get("duplicate_created_count");
            int parentMatchContestId = matchContestId;
            String isDuplicateAllow = (String) outputData.get("is_duplicate_allow");

            if (outputData.get("parent_match_contest_id") != null) {
                duplicateCount = (int) outputData.get("parent_duplicate_count");
                duplicateCreatedCount = (int) outputData.get("parent_duplicate_created_count");
                parentMatchContestId = (int) outputData.get("parent_match_contest_id");
                isDuplicateAllow = "Y";
            }

            if (isDuplicateAllow.equals("Y") && duplicateCount > duplicateCreatedCount
                    && ((int) outputData.get("total_team") - (int) outputData.get("total_joined_team")) <= 0) {
                Long currentTime = Util.getCurrentTime();
                BigInteger time = BigInteger.valueOf(currentTime);
                int dCount = 0;
                isDuplicateAllow = "N";
                String status = "A";
                String isDeleted = "N";
                String slug = Util.getAlphaNumericString(12) + currentTime + parentMatchContestId + "_";
                String is_duplicated_created = "N";
                float oldTotalPrice = -1;
                String oldContestJson = null;
                String hql = "INSERT INTO " + "TblSoccerContestMatch "
                        + "(contestId, categoryId, matchId, matchUniqueId, seriesId, totalTeam, totalPrice, entryFees, actualEntryFees, moreEntryFees, maxEntryFees, perUserTeamAllowed, contestJson, confirmWin, multiTeamAllowed, confirmWinContestPercentage,compressionAllowPercentage, pdf, pdfProcess, status, userId, isPrivate, isBeatTheExpert, teamId, entryFeeMultiplier, isDuplicateAllow,isLeaderBoardAvaliable, duplicateCount, slug, isDuplicatedCreated, parentId, isDeleted, isAbondant, createdAt, createdBy, updatedAt, updatedBy,isCompressionAllow,oldTotalPrice,oldContestJson,cashBonusUsedType,cashBonusUsedValue ) "
                        + "SELECT contestId, categoryId, matchId, matchUniqueId,seriesId, totalTeam, totalPrice, entryFees, actualEntryFees, moreEntryFees, maxEntryFees, perUserTeamAllowed, contestJson, confirmWin, multiTeamAllowed, confirmWinContestPercentage,compressionAllowPercentage, pdf, pdfProcess, :status, userId, isPrivate, isBeatTheExpert, teamId, entryFeeMultiplier, :isDuplicateAllow,isLeaderBoardAvaliable, :dCount, :slug, :is_duplicated_created, :parentMatchContestId, :isDeleted, isAbondant, :createdAt , createdBy, :updatedAt, updatedBy, isCompressionAllow, :oldTotalPrice, :oldContestJson,cashBonusUsedType,cashBonusUsedValue "
                        + " FROM TblSoccerContestMatch where id=:parentMatchContestId1";

                Query query = getSession().createQuery(hql)
                        .setParameter("status", status)
                        .setParameter("isDuplicateAllow", isDuplicateAllow)
                        .setParameter("dCount", dCount).setParameter("slug", slug)
                        .setParameter("is_duplicated_created", is_duplicated_created)
                        .setParameter("parentMatchContestId", parentMatchContestId)
                        .setParameter("isDeleted", isDeleted)
                        .setParameter("createdAt", time)
                        .setParameter("updatedAt", time).setParameter("oldTotalPrice", oldTotalPrice)
                        .setParameter("oldContestJson", oldContestJson)
                        .setParameter("parentMatchContestId1", parentMatchContestId);

                int lastInsertedId = query.executeUpdate();

                String hqllll = "UPDATE " + "TblSoccerContestMatch "
                        + "SET duplicateCreatedCount=duplicateCreatedCount+1 ";

                if (parentMatchContestId == matchContestId) {
                    hqllll += ", isDuplicatedCreated='Y' ";
                }

                hqllll += "where " + "id=:parentMatchContestId";
                Query queryyyyl = getSession().createQuery(hqllll).setParameter("parentMatchContestId",
                        parentMatchContestId);
                queryyyyl.executeUpdate();

                if (parentMatchContestId != matchContestId) {
                    String hqll = "UPDATE " + "TblSoccerContestMatch " + "SET isDuplicatedCreated='Y' " + "where "
                            + "id=:matchContestId";
                    Query queryyy = getSession().createQuery(hqll).setParameter("matchContestId", matchContestId);
                    queryyy.executeUpdate();
                }

            }

        }

    }

    @Transactional(readOnly = true)
    public int selectContestTeamCount(int matchContestId) {

        String hqln = "SELECT " + "totalJoinedTeam as totalJoinedTeam " + "FROM TblSoccerContestMatch " + "WHERE "
                + "id=:matchContestId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return (int) result.get(0).get("totalJoinedTeam");

    }

    @Transactional
    public void incrementContestTeamCount(int matchContestId) {

        String hqln = "UPDATE " + "TblSoccerContestMatch " + "SET totalJoinedTeam=totalJoinedTeam+1 " + "WHERE "
                + "id=:matchContestId";

        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId);

        queryy.executeUpdate();

    }

    @Transactional
    public void updateCustomerContestTeams(Integer customerId, int matchUniqueId, int matchContestId) {
        String hqln = "SELECT " + "tcct.id as id, tcct.name as name " + "FROM TblSoccerCustomerContest tccc "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON (tcct.id=tccc.customerTeamId) " + "WHERE "
                + "tccc.matchContestId=:matchContestId AND tccc.matchUniqueId=:matchUniqueId AND tccc.customerId=:customerId";
        Query queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId)
                .setParameter("matchUniqueId", matchUniqueId).setParameter("customerId", customerId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        hqln = "SELECT " + "tccct.id as id " + "FROM TblSoccerCustomerContestTeams tccct " + "WHERE "
                + "tccct.matchUniqueId=:matchUniqueId AND tccct.matchContestId=:matchContestId AND tccct.customerId=:customerId";
        queryy = getSession().createQuery(hqln).setParameter("matchContestId", matchContestId)
                .setParameter("matchUniqueId", matchUniqueId).setParameter("customerId", customerId);

        int rowCount = ((List<HashMap<String, Object>>) queryy.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list()).size();

        if (result.size() > 0) {

            String customerTeamIds = "";
            String customerTeamNames = "";

            for (HashMap<String, Object> hashMap : result) {

                if (customerTeamIds.isEmpty()) {
                    customerTeamIds = String.valueOf(hashMap.get("id"));
                    customerTeamNames = String.valueOf(hashMap.get("name"));
                } else {
                    customerTeamIds += "," + String.valueOf(hashMap.get("id"));
                    customerTeamNames += "," + String.valueOf(hashMap.get("name"));
                }
            }

            if (rowCount > 0) {
                hqln = "UPDATE " + "TblSoccerCustomerContestTeams "
                        + "SET customerTeamIds=:customerTeamIds, customerTeamNames=:customerTeamNames " + "WHERE "
                        + "matchUniqueId=:matchUniqueId AND customerId=:customerId AND matchContestId=:matchContestId";

                queryy = getSession().createQuery(hqln).setParameter("customerTeamIds", customerTeamIds)
                        .setParameter("customerTeamNames", customerTeamNames)
                        .setParameter("matchUniqueId", matchUniqueId).setParameter("customerId", customerId)
                        .setParameter("matchContestId", matchContestId);

                queryy.executeUpdate();
            } else {
                TblSoccerCustomerContestTeams tblSoccerCustomerContestTeams = new TblSoccerCustomerContestTeams();
                tblSoccerCustomerContestTeams.setMatchContestId(matchContestId);
                tblSoccerCustomerContestTeams.setMatchUniqueId(matchUniqueId);
                tblSoccerCustomerContestTeams.setCustomerId(customerId);
                tblSoccerCustomerContestTeams.setCustomerTeamIds(customerTeamIds);
                tblSoccerCustomerContestTeams.setCustomerTeamNames(customerTeamNames);

                getSession().save(tblSoccerCustomerContestTeams);

            }

        }

    }

    @Transactional
    public void updateCustomerMatchContestInfo(int matchUniqueId,Integer customerId){
        String hqln = "SELECT COUNT(DISTINCT tccc.matchContestId) AS cnt FROM TblSoccerCustomerContest tccc WHERE tccc.matchUniqueId =:matchUniqueId AND tccc.customerId =:customerId";
        Query queryy = getSession().createQuery(hqln);
        queryy.setParameter("matchUniqueId",matchUniqueId);
        queryy.setParameter("customerId",customerId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        long count = 0;
        if (result != null && result.size() > 0){
            count = (long) result.get(0).get("cnt");
        }

        if(count > 0){
            String mysqlQuery1 = "INSERT " + "INTO tbl_soccer_customer_match_info "
                    +"SET match_unique_id = " + matchUniqueId +", "
                    +"customer_id = " + customerId +", "
                    +"contest_count = " + count +" "
                    +"ON DUPLICATE KEY UPDATE contest_count = " + count;
            Query queryyy = getSession().createSQLQuery(mysqlQuery1);
            queryyy.executeUpdate();
        }
    }


    @Transactional(readOnly = true)
    public HashMap<String, Object> getPrivateContestCategory() {
        String hqln = "SELECT "
                + "tccc.id as id, tccc.name as name, tccc.confirmWinContestPercentage as confirm_win_contest_percentage, tccc.confirmWin as confirm_win, tccc.cashBonusUsedValue as cash_bonus_used_value, tccc.cashBonusUsedType as cash_bonus_used_type, tccc.isCompressionAllow as is_compression_allow "
                + "FROM " + "TblSoccerContestCategory tccc " + "where " + "tccc.isPrivate='Y' AND tccc.isDeleted='N'";

        Query queryy = getSession().createQuery(hqln);
        queryy.setFirstResult(0);
        queryy.setMaxResults(1);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getPrivateContestBrackupIdsForContestSize(int contestSize) {
        if (contestSize > 1000) {
            String hqln = "SELECT " + "tpcb.id as id "
                    + "FROM TblPrivateContestBreakup tpcb " + "where "
                    + "tpcb.isDeleted='N' AND tpcb.totalWinners=1";

            Query queryy = getSession().createQuery(hqln);
            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if (result.isEmpty()) {
                return null;
            }
            String brackupIds = "";
            for (HashMap<String, Object> stringObjectHashMap : result) {
                if (brackupIds.isEmpty()) {
                    brackupIds = stringObjectHashMap.get("id").toString();
                } else {
                    brackupIds += "," + stringObjectHashMap.get("id").toString();
                }
            }
            HashMap<String, Object> returnData = new HashMap<>();
            returnData.put("breakupIds", brackupIds);
            return returnData;
        } else {
            String hqln = "SELECT " + "tpcbr.breakupIds as breakupIds " + "FROM TblPrivateContestBreakupRule tpcbr "
                    + "where "
                    + "tpcbr.minContestSize<=:minContestSize AND tpcbr.maxContestSize>=:maxContestSize AND tpcbr.isDeleted='N'";

            Query queryy = getSession().createQuery(hqln).setParameter("minContestSize", contestSize)
                    .setParameter("maxContestSize", contestSize);
            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if (result.isEmpty()) {
                return null;
            }

            return result.get(0);
        }

    }

    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> getPrivateContestBreakupsFromIds(List<Integer> breakupIds, float prizePool) {
        String hqln = "SELECT " + "tpcb.id as id, tpcb.contestJson as contestJson, tpcb.totalWinners as totalWinners "
                + "FROM TblPrivateContestBreakup tpcb " + "where "
                + "tpcb.isDeleted='N' AND tpcb.id IN(:breakupIds) ORDER BY tpcb.totalWinners DESC";

        Query queryy = getSession().createQuery(hqln).setParameterList("breakupIds", breakupIds);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (result.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

        for (HashMap<String, Object> hashMap : result) {
            LinkedHashMap<String, Object> breakupData = new LinkedHashMap<String, Object>();

            breakupData.put("id", hashMap.get("id"));
            breakupData.put("total_winners", hashMap.get("totalWinners"));

            JSONObject contestJson = new JSONObject(hashMap.get("contestJson").toString());
            JSONArray contestJsonPrice = contestJson.getJSONArray("per_price");

            JSONArray perPrice = new JSONArray();
            JSONArray perPercent = new JSONArray();
            for (Object price : contestJsonPrice) {
                float contestJsonPerPriceValue = Float.parseFloat(price.toString());
                float v = (contestJsonPerPriceValue / 100) * prizePool;
                v=Util.numberFormate(v,2);
                perPrice.put(String.valueOf(v));
                perPercent.put(price);
            }
            contestJson.put("per_price", perPrice);
            contestJson.put("per_percent", perPercent);

            breakupData.put("contest_json", contestJson.toMap());

            data.put(breakupData.get("id").toString(), breakupData);
        }

        return data;

    }

    @Transactional
    public TblSoccerContestMatch createPrivateContest(TblSoccerMatch matchDataOnly,
                                                       HashMap<String, Object> categoryData, LinkedHashMap<String, Object> privateContestBreakupsFromIds,
                                                       int authUserId, LinkedHashMap<String, Object> extraData) {

        float usedBonus = (float) extraData.get("usedBonus");
        float usedDeposit = (float) extraData.get("usedDeposit");
        float usedWinning = (float) extraData.get("usedWinning");
        float entryFee = (float) extraData.get("entryFee");
        float prizePool = (float) extraData.get("prizePool");
        int contestSize = (int) extraData.get("contestSize");
        String isMultiple = (String) extraData.get("isMultiple");
        String teamId = (String) extraData.get("teamId");

        Session newSession = getNewSession();
        Transaction tx = null;
        try {
            long time = Util.getCurrentTime();

            int perUserTeamAllowed = 1;
            if (isMultiple.equals("Y")) {
                perUserTeamAllowed = 10000;
            }

            tx = newSession.beginTransaction();

            TblSoccerContestMatch tblSoccerContestMatch = TblSoccerContestMatch.getInstance();

            tblSoccerContestMatch.setActualEntryFees(entryFee);
            tblSoccerContestMatch.setCategoryId((int) categoryData.get("id"));
            tblSoccerContestMatch.setIsCompressionAllow((String) categoryData.get("is_compression_allow"));
            tblSoccerContestMatch.setConfirmWin(categoryData.get("confirm_win").toString());
            tblSoccerContestMatch.setConfirmWinContestPercentage(
                    Float.parseFloat(categoryData.get("confirm_win_contest_percentage").toString()));
            tblSoccerContestMatch.setContestJson(
                    new JSONObject((Map<String, Object>) privateContestBreakupsFromIds.get("contest_json")).toString());
            tblSoccerContestMatch.setCreatedAt(BigInteger.valueOf(time));
            tblSoccerContestMatch.setEntryFees(entryFee);
            tblSoccerContestMatch.setIsPrivate("Y");
            tblSoccerContestMatch.setSeriesId(matchDataOnly.getSeriesId());
            tblSoccerContestMatch.setMatchId(matchDataOnly.getId());
            tblSoccerContestMatch.setMatchUniqueId(matchDataOnly.getUniqueId());
            tblSoccerContestMatch.setPerUserTeamAllowed(perUserTeamAllowed);
            tblSoccerContestMatch.setTotalJoinedTeam(1);
            tblSoccerContestMatch.setTotalPrice(prizePool);
            tblSoccerContestMatch.setTotalTeam(contestSize);
            tblSoccerContestMatch.setUpdatedAt(BigInteger.valueOf(time));
            tblSoccerContestMatch.setUserId(authUserId);

            newSession.save(tblSoccerContestMatch);

            int matchContestId = tblSoccerContestMatch.getId();

            String slug = Util.getAlphaNumericString(12) + matchContestId + "_";

            tblSoccerContestMatch.setSlug(slug);

            String hqln = "UPDATE TblSoccerContestMatch tccm set tccm.slug=:slug where tccm.id=:id";

            Query queryy = newSession.createQuery(hqln).setParameter("slug", slug).setParameter("id", matchContestId);

            int executeUpdate = queryy.executeUpdate();

            time = Util.getCurrentTime();

            TblSoccerCustomerContest tblSoccerCustomerContest = TblSoccerCustomerContest.getInstance();
            tblSoccerCustomerContest.setCashBonusWallet(usedBonus);
            tblSoccerCustomerContest.setCreated(BigInteger.valueOf(time));
            tblSoccerCustomerContest.setCustomerId(authUserId);
            tblSoccerCustomerContest.setCustomerTeamId(Integer.parseInt(teamId));
            tblSoccerCustomerContest.setDepositeWallet(usedDeposit);
            tblSoccerCustomerContest.setEntryFees(entryFee);
            tblSoccerCustomerContest.setMatchContestId(matchContestId);
            tblSoccerCustomerContest.setSeriesId(matchDataOnly.getSeriesId());
            tblSoccerCustomerContest.setMatchUniqueId(matchDataOnly.getUniqueId());
            tblSoccerCustomerContest.setUpdated(BigInteger.valueOf(time));
            tblSoccerCustomerContest.setWinningWallet(usedWinning);

            newSession.save(tblSoccerCustomerContest);

            int lastUserJoinedRowId = tblSoccerCustomerContest.getId();

            LinkedHashMap<String, Object> insertJoinContestInWalletHistory = insertJoinContestInWalletHistory(
                    newSession, matchDataOnly.getSeriesId(), matchDataOnly.getUniqueId(), matchContestId, authUserId,
                    Integer.parseInt(teamId), extraData);

            tx.commit();

            return tblSoccerContestMatch;

        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            newSession.close();
        }

        return null;
    }

    @Transactional
    public TblSoccerCustomerContest customerJoinContest(TblSoccerMatch matchDataOnly, int authUserId,
                                                         int matchContestId, LinkedHashMap<String, Object> extraData) {
        synchronized (lockObject) {
            int totalTeam = (int) extraData.get("totalTeam");
            String hqlnn = "SELECT " + "totalJoinedTeam as totalJoinedTeam " + "FROM TblSoccerContestMatch " + "WHERE "
                    + "id=:matchContestId";

            Query queryy1 = getSession().createQuery(hqlnn).setParameter("matchContestId", matchContestId);

            List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy1
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            int currentTotalJoinedTeam = (int) result.get(0).get("totalJoinedTeam");
            if (currentTotalJoinedTeam >= totalTeam) {
                return  null;
            }


            float usedBonus = (float) extraData.get("usedBonus");
            float usedDeposit = (float) extraData.get("usedDeposit");
            float usedWinning = (float) extraData.get("usedWinning");
            float entryFee = (float) extraData.get("entryFee");

            int teamId = (int) extraData.get("teamId");

            Session newSession = getNewSession();
            Transaction tx = null;
            try {
                long time = Util.getCurrentTime();

                tx = newSession.beginTransaction();

                TblSoccerCustomerContest tblSoccerCustomerContest = TblSoccerCustomerContest.getInstance();
                tblSoccerCustomerContest.setCashBonusWallet(usedBonus);
                tblSoccerCustomerContest.setCreated(BigInteger.valueOf(time));
                tblSoccerCustomerContest.setCustomerId(authUserId);
                tblSoccerCustomerContest.setCustomerTeamId(teamId);
                tblSoccerCustomerContest.setDepositeWallet(usedDeposit);
                tblSoccerCustomerContest.setEntryFees(entryFee);
                tblSoccerCustomerContest.setMatchContestId(matchContestId);
                tblSoccerCustomerContest.setSeriesId(matchDataOnly.getSeriesId());
                tblSoccerCustomerContest.setMatchUniqueId(matchDataOnly.getUniqueId());
                tblSoccerCustomerContest.setUpdated(BigInteger.valueOf(time));
                tblSoccerCustomerContest.setWinningWallet(usedWinning);

                newSession.save(tblSoccerCustomerContest);

                int lastUserJoinedRowId = tblSoccerCustomerContest.getId();

                // increment joined team count
                String hqln = "UPDATE " + "TblSoccerContestMatch " + "SET totalJoinedTeam=totalJoinedTeam+1 " + "WHERE "
                        + "id=:matchContestId";

                Query queryy = newSession.createQuery(hqln).setParameter("matchContestId", matchContestId);
                queryy.executeUpdate();


                LinkedHashMap<String, Object> insertJoinContestInWalletHistory = insertJoinContestInWalletHistory(
                        newSession, matchDataOnly.getSeriesId(), matchDataOnly.getUniqueId(), matchContestId, authUserId,
                        teamId, extraData);

                tx.commit();

                return tblSoccerCustomerContest;

            } catch (Exception e) {
                e.printStackTrace();
                if (tx != null) {
                    tx.rollback();
                }
            } finally {
                newSession.close();
            }
        }

        return null;
    }

    @Transactional
    public int customerSwitchTeam(int customerId, int matchUniqueId, int matchContestId, int customerTeamIdOld,
                                  int customerTeamIdNew) {

        String hql = "UPDATE " + "TblSoccerCustomerContest " + "SET customerTeamId=:customerTeamIdNew " + "where "
                + "customerId=:customerId AND matchUniqueId=:matchUniqueId AND matchContestId=:matchContestId AND customerTeamId=:customerTeamIdOld";
        Query query = getSession().createQuery(hql).setParameter("customerTeamIdNew", customerTeamIdNew)
                .setParameter("customerId", customerId).setParameter("matchUniqueId", matchUniqueId)
                .setParameter("matchContestId", matchContestId).setParameter("customerTeamIdOld", customerTeamIdOld);
        return query.executeUpdate();

    }

    @Transactional
    public int updateSwitchTeamInWalletHistory(int customerId, int matchUniqueId, int matchContestId,
                                               int customerTeamIdOld, int customerTeamIdNew) {

        Long time = Util.getCurrentTime();

        String description = customerId + " Join contest match_contest_id " + matchContestId + " with customer_team_id "
                + customerTeamIdNew + ".";
        String transactionId = "JCWALL" + time + customerId + "_" + matchContestId + "_" + customerTeamIdNew;

        String hql = "UPDATE " + "TblCustomerWalletHistory "
                + "SET description=:description, transactionId=:transactionId, teamId=:customerTeamIdNew " + "where "
                + "sportId=0 AND type='CUSTOMER_JOIN_CONTEST' AND customerId=:customerId AND teamId=:customerTeamIdOld AND matchContestId=:matchContestId";

        Query query = getSession().createQuery(hql).setParameter("description", description)
                .setParameter("transactionId", transactionId).setParameter("customerTeamIdNew", customerTeamIdNew)
                .setParameter("customerId", customerId).setParameter("customerTeamIdOld", customerTeamIdOld)
                .setParameter("matchContestId", matchContestId);
        return query.executeUpdate();

    }

    @Transactional
    private LinkedHashMap<String, Object> insertJoinContestInWalletHistory(Session newSession, int seriesId,
                                                                           int matchUniqueId, int matchContestId, int customerId, int teamId,
                                                                           LinkedHashMap<String, Object> extraData) {

        Long time = Util.getCurrentTime();

        String description = customerId + " Join contest match_contest_id " + matchContestId + " with customer_team_id "
                + teamId + ".";
        String transaction_id = "JCWALL" + time + customerId + "_" + matchContestId + "_" + teamId;

        float bonusWallet = (float) extraData.get("bonusWallet");
        float depositWallet = (float) extraData.get("depositWallet");
        float winningWallet = (float) extraData.get("winningWallet");
        float winningWalletActual = (float) extraData.get("winningWalletActual");

        float usedBonus = (float) extraData.get("usedBonus");
        float usedDeposit = (float) extraData.get("usedDeposit");
        float usedWinning = (float) extraData.get("usedWinning");

        if (usedBonus > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("bonus_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_SOCCER);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(bonusWallet);
            tblCustomerWalletHistory.setAmount(usedBonus);
            tblCustomerWalletHistory.setCurrentAmount(bonusWallet - usedBonus);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("DEBIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_JOIN_CONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedDeposit > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("deposit_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_SOCCER);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(depositWallet);
            tblCustomerWalletHistory.setAmount(usedDeposit);
            tblCustomerWalletHistory.setCurrentAmount(depositWallet - usedDeposit);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("DEBIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_JOIN_CONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedWinning > 0) {

            String walletName = GlobalConstant.WALLET_TYPE.get("winning_wallet");

            TblCustomerWalletHistory tblCustomerWalletHistory = TblCustomerWalletHistory.getInstance();
            tblCustomerWalletHistory.setSportId(GlobalConstant.SPORT_ID_SOCCER);
            tblCustomerWalletHistory.setSeriesId(seriesId);
            tblCustomerWalletHistory.setMatchUniqueId(matchUniqueId);
            tblCustomerWalletHistory.setMatchContestId(matchContestId);
            tblCustomerWalletHistory.setCustomerId(customerId);
            tblCustomerWalletHistory.setTeamId(teamId);
            tblCustomerWalletHistory.setPreviousAmount(winningWalletActual);
            tblCustomerWalletHistory.setAmount(usedWinning);
            tblCustomerWalletHistory.setCurrentAmount(winningWalletActual - usedWinning);
            tblCustomerWalletHistory.setWalletType(walletName);
            tblCustomerWalletHistory.setTransactionType("DEBIT");
            tblCustomerWalletHistory.setTransactionId(transaction_id);
            tblCustomerWalletHistory.setType("CUSTOMER_JOIN_CONTEST");
            tblCustomerWalletHistory.setDescription(description);
            tblCustomerWalletHistory.setCreated(BigInteger.valueOf(time));
            newSession.save(tblCustomerWalletHistory);
        }

        if (usedBonus > 0 || usedDeposit > 0 || usedWinning > 0) {
            String hqlll = "UPDATE " + "TblCustomer "
                    + "SET bonusWallet=bonusWallet-:usedBonus, depositWallet=depositWallet-:usedDeposit, winningWallet=winningWallet-:usedWinning "
                    + "where " + "id=:customerId";
            Query queryyyy = newSession.createQuery(hqlll).setParameter("usedBonus", usedBonus)
                    .setParameter("usedDeposit", usedDeposit).setParameter("usedWinning", usedWinning)
                    .setParameter("customerId", customerId);
            queryyyy.executeUpdate();
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("description", description);
        data.put("transaction_id", transaction_id);

        return data;

    }

    @Transactional
    public void saveJoinedContestMail(TblTemplate template, List<HashMap<String, Object>> mailData,
                                      TblSoccerMatch matchDataOnly, String toName, String toEmail) {

        if (Util.isEmpty(toEmail)) {
            return;
        }

        try {
            Long currentTime = Util.getCurrentTime();
            Session session = getSession();

            for (HashMap<String, Object> hashMap : mailData) {

                float entry_fees = (float) hashMap.get("amount");

                String message = "Welcome to the contest. You have successfully joined the contest with an entry fee of "
                        + GlobalConstant.CURRENCY_SYMBOL + entry_fees + " for " + matchDataOnly.getMatchFormattedDate()
                        + " " + matchDataOnly.getName() + ".";

                String content = template.getContent();
                String subject = template.getSubject();

                content = content.replace("{CUSTOMER_NAME}", toName);

                content = content.replace("{MESSAGE}", message);

                TblSoccerEmailCron tblSoccerEmailCron = TblSoccerEmailCron.getInstance();

                tblSoccerEmailCron.setSubject(subject);
                tblSoccerEmailCron.setMessage(content);
                tblSoccerEmailCron.setToemail(toEmail);
                tblSoccerEmailCron.setToname(toName);
                tblSoccerEmailCron.setCreatedat(BigInteger.valueOf(currentTime));
                tblSoccerEmailCron.setEmailType("JOIN_CONTEST");

                session.save(tblSoccerEmailCron);
            }
        } catch (Exception e) {
        }

    }

    @Transactional
    public BaseResponse updateContestFavorite(Integer UserId, int masterContestId) {
        Session newSession = getNewSession();
        Transaction tx = null;
        tx = newSession.beginTransaction();
        Long currentTime = Util.getCurrentTime();
        String message= "";
        try {
            String hqlQuery = "SELECT tcccf FROM TblSoccerCustomerContestFav tcccf WHERE tcccf.customerId =:customerId AND tcccf.contestId =:contestId";
            Query queryObj = newSession.createQuery(hqlQuery);
            queryObj.setParameter("customerId", UserId);
            queryObj.setParameter("contestId", masterContestId);
            TblSoccerCustomerContestFav tblSoccerCustomerContestFav = (TblSoccerCustomerContestFav) queryObj.uniqueResult();
            if (tblSoccerCustomerContestFav == null) {
                String mysqlQuery1 = "INSERT " + "INTO tbl_soccer_customer_contest_fav "
                        + "(customer_id,contest_id,created)"
                        + " VALUES " + "(" + UserId + "," + masterContestId + "," + currentTime + ")";
                newSession.createSQLQuery(mysqlQuery1).executeUpdate();
                message = "Added in favorite successfully.";
            } else {
                String hqln = "DELETE FROM TblSoccerCustomerContestFav tcccf WHERE tcccf.customerId =:customerId AND tcccf.contestId =:contestId";
                Query queryy = newSession.createQuery(hqln);
                queryy.setParameter("customerId", UserId);
                queryy.setParameter("contestId", masterContestId);
                queryy.executeUpdate();
                message = "Remove from favorite successfully.";
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
        return new BaseResponse(0, false, message, null);
    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchContestFav(int matchId, int matchUniqueId, Integer UserId) {

        String hqln = "SELECT "
                + "tccct.customerTeamIds as customer_team_ids, tccct.customerTeamNames as customer_team_names, "
                + "tccf.totalJoinedTeam as total_joined_team, "
                + "ts.value as discount_image, ts.width as discount_image_width, ts.height as discount_image_height, "
                + "tccc.id as cat_id, tccc.name as name, tccc.description as description, tccc.image as image, tccc.isDiscounted as is_discounted, tccc.orderPos as order_pos, tccc.cashBonusUsedType as cat_cash_bonus_used_type, tccc.cashBonusUsedValue as cat_cash_bonus_used_value, "
                + "tccf.id as id, tccf.totalTeam as total_team, tccf.totalPrice as total_price, tccf.entryFees as entry_fees, tccf.perUserTeamAllowed as per_user_team_allowed, tccf.contestJson as contest_json, tccf.confirmWin as confirm_win, tccf.slug as slug, tccf.moreEntryFees as more_entry_fees, tccf.multiTeamAllowed as multi_team_allowed, tccf.actualEntryFees as actual_entry_fees, tccf.isCompressionAllow as is_compression_allow,tccf.contestId as masterContestId, tccf.cashBonusUsedType as cash_bonus_used_type, tccf.cashBonusUsedValue as cash_bonus_used_value, "
                + "(CASE WHEN tcccf.id is null THEN 'N' ELSE 'Y' END) as isFavorite "
                + "FROM  TblSoccerContestMatch tccf "
                + "LEFT JOIN TblSoccerCustomerContestTeams tccct ON (tccf.id=tccct.matchContestId AND tccct.customerId=:customerId) "
                + "LEFT JOIN TblSoccerContestCategory tccc ON (tccc.id=tccf.categoryId ) "
                + "LEFT JOIN TblSetting ts ON(ts.key='DISCOUNTED_IMAGE') "
                + "LEFT JOIN TblSoccerCustomerContestFav tcccf ON tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId "
                + "WHERE  tccf.matchUniqueId=:matchUniqueId " +
                "AND tccf.status='A' " +
                "AND tccf.isDeleted='N' " +
                "AND tccf.isPrivate='N' " +
                "AND tccf.isBeatTheExpert='N' " +
                "AND (tccf.totalTeam-tccf.totalJoinedTeam)>0 AND tcccf.customerId=:customerId AND tcccf.contestId=tccf.contestId ORDER BY tccc.orderPos ASC";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result;

    }

}
