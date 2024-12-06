package com.choic11.repository.basketball;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.basketball.TblBasketballCustomerTeam;
import com.choic11.model.basketball.TblBasketballMatch;
import com.choic11.service.basketball.MatchesService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Repository("BasketballTeamsRepository")
public class TeamsRepository {

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
    public int getCustomerAlreadyCreatedTeamCount(Integer userId, int matchUniqueId) {
        String hqln = "SELECT id as id FROM  TblBasketballCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", userId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result == null ? 0 : result.size();

    }

    @Transactional(readOnly = true)
    public int checkCustomerTeamIdExist(Integer userId, int matchUniqueId, int teamId) {
        String hqln = "SELECT id as id   FROM  TblBasketballCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId AND id=:id";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", userId)
                .setParameter("matchUniqueId", matchUniqueId).setParameter("id", teamId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result.size();
    }

    public Object createCustomerTeam(Integer userId, int matchUniqueId, List<JSONObject> playersDataList,
                                     String customerTeamName, int moreName, int fromAdmin) {

        Session session = getNewSession();
        Transaction tx = null;
        try {
            String playerIds = "";
            String playerMultipliers = "";

            List<Integer> normalPlayers = new ArrayList();
            int captionPlayer = 0;
            int viceCaptionPlayer = 0;
            int trumpPlayer = 0;

            for (JSONObject jsonObject : playersDataList) {
                float playerMultiplier = Float.parseFloat(jsonObject.get("player_multiplier").toString());
                if (playerIds.isEmpty()) {
                    playerIds = jsonObject.get("player_id").toString();
                    playerMultipliers = jsonObject.get("player_multiplier").toString();
                } else {
                    playerIds += "," + jsonObject.get("player_id").toString();
                    playerMultipliers += "," + jsonObject.get("player_multiplier").toString();
                }

                if (playerMultiplier == 1.5 || playerMultiplier == 4.5) {
                    viceCaptionPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else if (playerMultiplier == 2 || playerMultiplier == 5) {
                    captionPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else if (playerMultiplier > 2.5) {
                    trumpPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else {
                    normalPlayers.add(Integer.parseInt(jsonObject.get("player_id").toString()));
                }
            }

            if (captionPlayer == 0 || viceCaptionPlayer == 0) {
                throw new IllegalStateException("Unable to proceed CVC");
            }

            String playerIdsMultipliers = playerIds + "+++" + playerMultipliers;

            String hqln = "SELECT "
                    + "tcm.uniqueId as uniqueId, tcm.matchProgress as matchProgress, tcm.matchLimit as matchLimit, tcm.playingSquadUpdated as playingSquadUpdated, "
                    + "count(tcct.id) as customerTeamCount, " + "tcct1.id as customerTeamId  "
                    + "FROM  TblBasketballMatch tcm "
                    + "LEFT JOIN TblBasketballCustomerTeam tcct ON (tcct.customerId=:customerId AND tcct.matchUniqueId=tcm.uniqueId) "
                    + "LEFT JOIN TblBasketballCustomerTeam tcct1 ON (tcct1.customerId=:customerId1 AND tcct1.matchUniqueId=tcm.uniqueId AND tcct1.playerUniqueIdsMultiplers=:playerIdsMultipliers)  "
                    + "WHERE " + "tcm.uniqueId = :uniqueId AND tcm.status = 'A' AND tcm.isDeleted='N'";

            Query queryy = session.createQuery(hqln).setParameter("customerId", userId)
                    .setParameter("customerId1", userId).setParameter("playerIdsMultipliers", playerIdsMultipliers)
                    .setParameter("uniqueId", matchUniqueId);

            HashMap<String, Object> matchData = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            if (matchData.get("uniqueId") == null) {
                return "NO_MATCH_FOUND";
            }

            if (!matchData.get("matchProgress").toString().equals("F")) {
                return "INVALID_MATCH";
            }

            int matchLimit = Integer.parseInt(matchData.get("matchLimit").toString());
            int customerTeamCount = Integer.parseInt(matchData.get("customerTeamCount").toString());

            if (customerTeamCount >= matchLimit) {
                return "TEAM_CREATION_LIMIT_EXEED";
            }

            if (matchData.get("customerTeamId") != null) {
                return "TEAM_ALREADY_EXIST";
            }

            String playingSquadUpdated = (matchData.get("playingSquadUpdated") != null ? (String) matchData.get("playingSquadUpdated") : "N");

            if (fromAdmin == 1) {
                String hqlAdmin = "SELECT " + "count(id) " + "FROM TblCustomer " + "WHERE "
                        + "teamName = :teamName AND id != :id";
                Query queryAdmin = session.createQuery(hqlAdmin).setParameter("teamName", customerTeamName)
                        .setParameter("id", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "CUSTOMER_TEAM_NAME_ALREADY_EXIST";
                }

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblBasketballCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND  customerTeamName =:customerTeamName AND customerId != :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "CUSTOMER_TEAM_NAME_ALREADY_EXIST";
                }

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblBasketballCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId  AND customerTeamName=:customerTeamName  AND moreName = :moreName AND customerId = :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("moreName", moreName)
                        .setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "TEAM_NAME_ALREADY_EXIST";
                }
            }

            String hqln1 = "SELECT " + "name as name " + "from TblBasketballCustomerTeam as tcct " + "where "
                    + "tcct.matchUniqueId=:matchUniqueId AND tcct.customerId=:customerId ORDER BY tcct.id DESC";
            Query queryAdmin = session.createQuery(hqln1).setParameter("matchUniqueId", matchUniqueId)
                    .setParameter("customerId", userId);
            queryAdmin.setMaxResults(1);

            List<HashMap<String, Object>> singleResult = (List<HashMap<String, Object>>) queryAdmin
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

            int new_team_name = 1;
            if (singleResult.size() > 0) {
                String lastTeamName = singleResult.get(0).get("name").toString();
                new_team_name = Integer.parseInt(lastTeamName) + 1;
            }

            BigInteger time = BigInteger.valueOf(Util.getCurrentTime());

            TblBasketballCustomerTeam tblBasketballCustomerTeam = new TblBasketballCustomerTeam();
            tblBasketballCustomerTeam.setCreated(time);
            tblBasketballCustomerTeam.setCustomerId(userId);
            tblBasketballCustomerTeam.setCustomerTeamName(customerTeamName);
            tblBasketballCustomerTeam.setMatchUniqueId(matchUniqueId);
            tblBasketballCustomerTeam.setMoreName(moreName);
            tblBasketballCustomerTeam.setName(new_team_name);
            tblBasketballCustomerTeam.setPlayerUniqueIds(playerIds);
            tblBasketballCustomerTeam.setPlayerMultiplers(playerMultipliers);
            tblBasketballCustomerTeam.setPlayerUniqueIdsMultiplers(playerIdsMultipliers);
            tblBasketballCustomerTeam.setUpdated(time);

            tx = session.beginTransaction();

            int newCustomerTeamId = (int) session.save(tblBasketballCustomerTeam);

            if (newCustomerTeamId > 0) {
                String params = "";

                for (JSONObject jsonObject : playersDataList) {
                    float playerMultiplier = Float.parseFloat(jsonObject.get("player_multiplier").toString());
                    if (!params.isEmpty()) {
                        params += ",";
                    }
                    params += "(" + userId + "," + newCustomerTeamId + "," + matchUniqueId + ","
                            + jsonObject.get("player_id").toString() + "," + jsonObject.get("team_id").toString() + ","
                            + jsonObject.get("player_multiplier").toString() + ","
                            + jsonObject.get("player_pos").toString() + "," + time + ")";

                }

                String mysqlQuery = "INSERT " + "INTO tbl_basketball_customer_team_plyers "
                        + "(customer_id,customer_team_id,match_unique_id,player_unique_id,team_id,multiplier,position,created)"
                        + " VALUES " + params;

                session.createSQLQuery(mysqlQuery).executeUpdate();
                tx.commit();

                LinkedHashMap<String, Object> teamData = new LinkedHashMap<>();
                teamData.put("id", newCustomerTeamId);

                updatePlayerSelectedByCount(matchUniqueId, captionPlayer, viceCaptionPlayer, trumpPlayer, normalPlayers,
                        false, playingSquadUpdated);

                return teamData;

            } else {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }
        return "UNABLE_TO_PROCEED";

    }

    @Async
    public void updatePlayerSelectedByCountForTeamUpdate(int matchUniqueId, int captionPlayer, int viceCaptionPlayer,
                                                         int trumpPlayer, List<Integer> normalPlayers, int oldCaptionPlayer, int oldViceCaptionPlayer, int oldTrumpPlayer, List<Integer> oldNormalPlayers, String playingSquadUpdated) {

        Session session = getNewSession();
        Transaction tx = null;
        try {

            if (playingSquadUpdated.equals("N")) {
                String updateNormalPlayerHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:oldNormalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1, selectedAsVccaptionCount = selectedAsVccaptionCount-1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1, selectedAsCaptionCount = selectedAsCaptionCount-1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :captionPlayer";

                tx = session.beginTransaction();

                int unqID = matchUniqueId;
                session.createQuery(updateNormalPlayerHQL).setParameter("matchUniqueId", unqID)
                        .setParameterList("oldNormalPlayers", oldNormalPlayers).executeUpdate();
                session.createQuery(updateViceCaptainHQL).setParameter("matchUniqueId", unqID)
                        .setParameter("viceCaptionPlayer", oldViceCaptionPlayer).executeUpdate();
                session.createQuery(updateCaptainPlayerHQL).setParameter("matchUniqueId", unqID)
                        .setParameter("captionPlayer", oldCaptionPlayer).executeUpdate();

                tx.commit();
            }
            updatePlayerSelectedByCount(matchUniqueId, captionPlayer, viceCaptionPlayer, trumpPlayer, normalPlayers,
                    true, playingSquadUpdated);

        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }

    }

    @Async
    public void updatePlayerSelectedByCount(int matchUniqueId, int captionPlayer, int viceCaptionPlayer,
                                            int trumpPlayer, List<Integer> normalPlayer, boolean isUpdate, String playingSquadUpdated) {
        Session session = getNewSession();
        Transaction tx = null;
        try {
            String matchString = "";
            if (!isUpdate) {
                matchString = "UPDATE " + "TblBasketballMatch " + "SET totalCustomerTeam = totalCustomerTeam+1 " + "WHERE "
                        + "uniqueId =:matchUniqueId";
            }

            if (playingSquadUpdated.equals("N")) {
                tx = session.beginTransaction();
                String updateNormalPlayerHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:normalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1, selectedAsVccaptionCount = selectedAsVccaptionCount+1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblBasketballMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1, selectedAsCaptionCount = selectedAsCaptionCount+1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :captionPlayer";

                if (!matchString.isEmpty()) {
                    session.createQuery(matchString).setParameter("matchUniqueId", matchUniqueId).executeUpdate();
                }
                int unqID = matchUniqueId;
                session.createQuery(updateNormalPlayerHQL).setParameter("matchUniqueId", unqID)
                        .setParameterList("normalPlayers", normalPlayer).executeUpdate();
                session.createQuery(updateViceCaptainHQL).setParameter("matchUniqueId", unqID)
                        .setParameter("viceCaptionPlayer", viceCaptionPlayer).executeUpdate();
                session.createQuery(updateCaptainPlayerHQL).setParameter("matchUniqueId", unqID)
                        .setParameter("captionPlayer", captionPlayer).executeUpdate();

                tx.commit();
            } else {
                if (!matchString.isEmpty()) {
                    tx = session.beginTransaction();
                    session.createQuery(matchString).setParameter("matchUniqueId", matchUniqueId).executeUpdate();
                    tx.commit();
                }
            }
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }

    }

    public Object updateCustomerTeam(Integer userId, int matchUniqueId, List<JSONObject> playersDataList,
                                     int customerTeamId) {

        Session session = getNewSession();
        Transaction tx = null;
        try {

            String playerIds = "";
            String playerMultipliers = "";

            List<Integer> normalPlayers = new ArrayList();
            int captionPlayer = 0;
            int viceCaptionPlayer = 0;
            int trumpPlayer = 0;
            HashMap<String, JSONObject> playerMap = new HashMap<>();
            for (JSONObject jsonObject : playersDataList) {
                playerMap.put(jsonObject.get("player_id").toString(), jsonObject);
                float playerMultiplier = Float.parseFloat(jsonObject.get("player_multiplier").toString());
                if (playerIds.isEmpty()) {
                    playerIds = jsonObject.get("player_id").toString();
                    playerMultipliers = jsonObject.get("player_multiplier").toString();
                } else {
                    playerIds += "," + jsonObject.get("player_id").toString();
                    playerMultipliers += "," + jsonObject.get("player_multiplier").toString();
                }

                if (playerMultiplier == 1.5 || playerMultiplier == 4.5) {
                    viceCaptionPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else if (playerMultiplier == 2 || playerMultiplier == 5) {
                    captionPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else if (playerMultiplier > 2.5) {
                    trumpPlayer = Integer.parseInt(jsonObject.get("player_id").toString());
                } else {
                    normalPlayers.add(Integer.parseInt(jsonObject.get("player_id").toString()));
                }
            }

            if (captionPlayer == 0 || viceCaptionPlayer == 0) {
                throw new IllegalStateException("Unable to proceed CVCT");
            }

            String playerIdsMultipliers = playerIds + "+++" + playerMultipliers;

            String hqln = "SELECT "
                    + "tcm.uniqueId as uniqueId, tcm.matchProgress as matchProgress, tcm.matchLimit as matchLimit, tcm.playingSquadUpdated as playingSquadUpdated "
                    + "FROM  TblBasketballMatch tcm " + "WHERE "
                    + "tcm.uniqueId = :uniqueId AND tcm.status = 'A' AND tcm.isDeleted='N'";

            Query queryy = session.createQuery(hqln).setParameter("uniqueId", matchUniqueId);

            HashMap<String, Object> matchData = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            if (matchData.get("uniqueId") == null) {
                return "NO_MATCH_FOUND";
            }

            if (!matchData.get("matchProgress").toString().equals("F")) {
                return "INVALID_MATCH";
            }
            String playingSquadUpdated=(matchData.get("playingSquadUpdated")!=null? (String) matchData.get("playingSquadUpdated") :"N");

            String hqlQuery = "SELECT tcct FROM TblBasketballCustomerTeam tcct WHERE tcct.id =:id";
            Query queryObj = session.createQuery(hqlQuery);
            queryObj.setParameter("id", customerTeamId);
            TblBasketballCustomerTeam customerTeam = (TblBasketballCustomerTeam) queryObj.getSingleResult();

            if (customerTeam.getMatchUniqueId() != matchUniqueId) {
                return "INVALID_MATCH";
            }

//            Criteria createCriteria = session.createCriteria(TblBasketballCustomerTeam.class);
//            createCriteria.add(Restrictions.eq("id", customerTeamId));
//            TblBasketballCustomerTeam customerTeam = (TblBasketballCustomerTeam) createCriteria.uniqueResult();

            if (customerTeam.getPlayerUniqueIdsMultiplers().equals(playerIdsMultipliers)) {
                return "TEAM_ALREADY_EXIST";
            }

            List<Integer> oldNormalPlayers = new ArrayList();
            int oldCaptionPlayer = 0;
            int oldViceCaptionPlayer = 0;
            int oldTrumpPlayer = 0;

            String oldPlayerData[] = customerTeam.getPlayerUniqueIdsMultiplers().split("\\+++");
            String oldPlayerIDs[] = oldPlayerData[0].split(",");
            String oldPlayerMultipler[] = oldPlayerData[1].split(",");
            List<String> unusedPlayers = new ArrayList<>();
            tx = session.beginTransaction();
            for (int i = 0; i < oldPlayerIDs.length; i++) {
                String string = oldPlayerIDs[i];
                String multi = oldPlayerMultipler[i];
                float playerMultiplier = Float.parseFloat(multi);
                if (playerMap.containsKey(string)) {
                    JSONObject jsonObject = playerMap.get(string);
                    if (!jsonObject.get("player_multiplier").toString().equals(multi)) {
                        String updatePlayers = " UPDATE TblBasketballCustomerTeamPlyer SET multiplier = :multiplier WHERE playerUniqueId= :playerUniqueId AND customerTeamId =:customerTeamId";
                        session.createQuery(updatePlayers).setParameter("playerUniqueId", Integer.parseInt(string))
                                .setParameter("multiplier",
                                        Float.parseFloat(jsonObject.get("player_multiplier").toString()))
                                .setParameter("customerTeamId", customerTeamId).executeUpdate();
                    }

                    playerMap.remove(string);

                } else {
                    unusedPlayers.add(string);
                }

                if (playerMultiplier == 1.5 || playerMultiplier == 4.5) {
                    oldViceCaptionPlayer = Integer.parseInt(string);
                } else if (playerMultiplier == 2 || playerMultiplier == 5) {
                    oldCaptionPlayer = Integer.parseInt(string);
                } else if (playerMultiplier > 2.5) {
                    oldTrumpPlayer = Integer.parseInt(string);
                } else {
                    oldNormalPlayers.add(Integer.parseInt(string));
                }

            }

            if (!playerMap.isEmpty()) {
                int i = 0;
                for (Map.Entry<String, JSONObject> entry : playerMap.entrySet()) {
                    String unUsed = unusedPlayers.get(i);

                    JSONObject jsonObject = entry.getValue();

                    String updatePlayers = " UPDATE " + "TblBasketballCustomerTeamPlyer "
                            + "SET multiplier = :multiplier, playerUniqueId =:playerUniqueId, teamId=:teamId "
                            + "WHERE " + "playerUniqueId= :oldPlayerUniqueId AND customerTeamId =:customerTeamId";
                    session.createQuery(updatePlayers)
                            .setParameter("playerUniqueId", Integer.parseInt(jsonObject.get("player_id").toString()))
                            .setParameter("multiplier",
                                    Float.parseFloat(jsonObject.get("player_multiplier").toString()))
                            .setParameter("teamId", Integer.parseInt(jsonObject.get("team_id").toString()))
                            .setParameter("oldPlayerUniqueId", Integer.parseInt(unUsed))
                            .setParameter("customerTeamId", customerTeamId).executeUpdate();
                    i++;

                }
            }
            BigInteger time = BigInteger.valueOf(Util.getCurrentTime());
            String updateTeam = " UPDATE " + "TblBasketballCustomerTeam "
                    + "SET playerUniqueIds = :playerUniqueIds, playerMultiplers = :playerMultiplers, playerUniqueIdsMultiplers = :playerUniqueIdsMultiplers, updated = :updated "
                    + "WHERE " + "id =:customerTeamId";
            session.createQuery(updateTeam).setParameter("playerUniqueIds", playerIds)
                    .setParameter("playerMultiplers", playerMultipliers)
                    .setParameter("playerUniqueIdsMultiplers", playerIdsMultipliers).setParameter("updated", time)
                    .setParameter("customerTeamId", customerTeamId).executeUpdate();
            LinkedHashMap<String, Object> teamData = new LinkedHashMap<>();
            teamData.put("id", customerTeamId);
            tx.commit();

            updatePlayerSelectedByCountForTeamUpdate(matchUniqueId, captionPlayer, viceCaptionPlayer, trumpPlayer,
                    normalPlayers, oldCaptionPlayer, oldViceCaptionPlayer, oldTrumpPlayer, oldNormalPlayers, playingSquadUpdated);

            return teamData;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null)
                tx.rollback();
        } finally {
            session.close();
        }
        return "UNABLE_TO_PROCEED";
    }

    @Transactional
    public long getMatchCustomerTeamCountByMatchUniqueId(Integer UserId, int matchUniqueId) {
        String hqln = "SELECT " + "COUNT(id) as id  " + "FROM  TblBasketballCustomerTeam  " + "WHERE "
                + "customerId = :customerId AND matchUniqueId = :matchUniqueId";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return (long) result.get(0).get("id");

    }

    @Transactional(readOnly = true)
    public Object getCustomerMatchTeam(int UserId, int matchUniqueId, MatchesService matchesService) {
        TblBasketballMatch basketballMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);

        if (basketballMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(basketballMatch);

        String hql = "SELECT " + "tcct.id AS team_id,tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position,tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblBasketballCustomerTeamPlyer tcctp "
                + "LEFT JOIN  TblBasketballMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblBasketballPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblBasketballSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId) "
                + "LEFT JOIN TblBasketballCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerId = :UserId AND  tcctp.matchUniqueId = :matchUniqueId ORDER BY tcctp.customerTeamId ASC ";

        Query queryy = getSession().createQuery(hql).setParameter("UserId", UserId)
                .setParameter("seriesId", basketballMatch.getSeriesId())
                .setParameter("gameTypeId", basketballMatch.getGameTypeId()).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        LinkedHashMap<Integer, Object> customerTeams = new LinkedHashMap<Integer, Object>();
        for (HashMap<String, Object> hashMap : result) {

            int player_teamId = (int) hashMap.get("player_teamId");

            HashMap<String, Object> country = new HashMap<String, Object>();
            country.put("id", hashMap.get("country_id"));
            country.put("name", hashMap.get("country_name"));

            HashMap<String, Object> player = new HashMap<String, Object>();

            player.put("player_id", hashMap.get("playerUniqueId"));
            player.put("player_pos", hashMap.get("position"));
            player.put("player_multiplier", hashMap.get("multiplier"));
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));

            player.put("name", Util.getPlayerFormattedName(hashMap.get("name").toString()));
            player.put("team_id", player_teamId);
            player.put("points", hashMap.get("points"));
            player.put("bat_type", hashMap.get("bets"));
            player.put("bowl_type", hashMap.get("bowls"));
            player.put("dob", hashMap.get("dob"));
            player.put("country", country.get("name"));
            player.put("credits", hashMap.get("credits"));
            player.put("is_in_playing_squad", hashMap.get("isInPlayingSquad"));
            player.put("playing_squad_updated", basketballMatch.getPlayingSquadUpdated());
            player.put("position", hashMap.get("playingRole"));
            player.put("total_points", hashMap.get("series_total_points"));
            player.put("is_in_substitute_squad", hashMap.get("isInSubstituteSquad"));

            String position = player.get("position").toString();
            if (!position.isEmpty()) {

                List<HashMap<String, Object>> batsmans = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> bowlers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> wicketkeapers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> allrounders = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> centers = new ArrayList<HashMap<String, Object>>();

                HashMap<String, Object> captain = null;
                HashMap<String, Object> vise_captain = null;
                HashMap<String, Object> trump = null;

                LinkedHashMap<String, Object> team1 = null;
                LinkedHashMap<String, Object> team2 = null;

                HashMap<String, Object> team = new HashMap<>();
                if (customerTeams.containsKey((int) hashMap.get("team_id"))) {
                    team = (HashMap<String, Object>) customerTeams
                            .get(Integer.parseInt(hashMap.get("team_id").toString()));
                    batsmans = (List<HashMap<String, Object>>) team.get("batsmans");
                    bowlers = (List<HashMap<String, Object>>) team.get("bowlers");
                    wicketkeapers = (List<HashMap<String, Object>>) team.get("wicketkeapers");
                    allrounders = (List<HashMap<String, Object>>) team.get("allrounders");
                    centers = (List<HashMap<String, Object>>) team.get("centers");

                    captain = (HashMap<String, Object>) team.get("captain");
                    vise_captain = (HashMap<String, Object>) team.get("vise_captain");
                    trump = (HashMap<String, Object>) team.get("trump");

                    team1 = (LinkedHashMap<String, Object>) team.get("team1");
                    team2 = (LinkedHashMap<String, Object>) team.get("team2");
                } else {
                    team1 = new LinkedHashMap<String, Object>((LinkedHashMap<String, Object>) modifiedMatchData.get("team1"));
                    team2 = new LinkedHashMap<String, Object>((LinkedHashMap<String, Object>) modifiedMatchData.get("team2"));

                    team1.put("player_counter", 0);
                    team2.put("player_counter", 0);

                    team.put("customer_id", UserId);
                    team.put("id", hashMap.get("team_id"));
                    team.put("name", hashMap.get("team_name"));
                    team.put("team1", team1);
                    team.put("team2", team2);
                    team.put("batsmans", batsmans);
                    team.put("bowlers", bowlers);
                    team.put("wicketkeapers", wicketkeapers);
                    team.put("allrounders", allrounders);
                    team.put("centers", centers);
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeams.put((int) team.get("id"), team);

                }

                if (position.equalsIgnoreCase("point guard")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("shooting guard")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("small forward")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("power forward")) {
                    bowlers.add(player);
                } else if (position.equalsIgnoreCase("center")) {
                    centers.add(player);
                }

                Float multi = Float.parseFloat(player.get("player_multiplier").toString());

                if (multi == 2 || multi == 5) {
                    captain = player;
                } else if (multi == 1.5 || multi == 4.5) {
                    vise_captain = player;
                }

                if (multi > 2.5) {
                    trump = player;
                }

                team.put("captain", captain);
                team.put("vise_captain", vise_captain);
                team.put("trump", trump);
                float previousPoints = (float) team.get("team_total_points");
                float previousCredits = (float) team.get("team_total_credits");
                int previousTeam1SelectedPlayer = (int) team.get("team1_selected_player");
                int previousTeam2SelectedPlayer = (int) team.get("team2_selected_player");

                previousPoints += (multi * Float.parseFloat(player.get("points").toString()));
                previousCredits += (Float.parseFloat(player.get("credits").toString()));

                if (player_teamId == basketballMatch.getTeam1Id()) {
                    previousTeam1SelectedPlayer++;
                } else {
                    previousTeam2SelectedPlayer++;
                }

                team1.put("player_counter", previousTeam1SelectedPlayer);
                team2.put("player_counter", previousTeam2SelectedPlayer);

                team.put("team_total_points", previousPoints);
                team.put("team_total_credits", previousCredits);
                team.put("team1_selected_player", previousTeam1SelectedPlayer);
                team.put("team2_selected_player", previousTeam2SelectedPlayer);
            }

        }

        return customerTeams.values();

    }

    @Transactional(readOnly = true)
    public Object getCustomerMatchTeamDetail(int UserId, int customerTeamId, MatchesService matchesService) {

        String getMatchUniqueId = "SELECT " + "tcct.matchUniqueId as matchUniqueId, tcct.customerId as customerId " + "FROM TblBasketballCustomerTeam tcct " + "WHERE " + "id = :customerTeamId";
        Query queryMatchUniqueId = getSession().createQuery(getMatchUniqueId).setParameter("customerTeamId",
                customerTeamId);

        HashMap<String, Object> matchResult = (HashMap<String, Object>) queryMatchUniqueId
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int matchUniqueId = Integer.parseInt(matchResult.get("matchUniqueId").toString());
        int customerId = Integer.parseInt(matchResult.get("customerId").toString());

        TblBasketballMatch basketballMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);
        if (basketballMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(basketballMatch);

        String hql = "SELECT " + "tcct.id AS team_id, tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position, tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad,tcmp.selectedByCount as selectedByCount,tcmp.selectedAsCaptionCount as selectedAsCaptionCount,tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tcmp.selectedAsTrumpCount as selectedAsTrumpCount, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblBasketballCustomerTeamPlyer  tcctp "
                + "LEFT JOIN  TblBasketballMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblBasketballPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblBasketballSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "LEFT JOIN TblBasketballCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("seriesId", basketballMatch.getSeriesId())
                .setParameter("gameTypeId", basketballMatch.getGameTypeId())
                .setParameter("customerTeamId", customerTeamId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> customerTeam = null;
        for (HashMap<String, Object> hashMap : result) {

            int selected_by = (int) hashMap.get("selectedByCount");
            int selected_as_caption = (int) hashMap.get("selectedAsCaptionCount");
            int selected_as_vccaption = (int) hashMap.get("selectedAsVccaptionCount");
            int selected_as_trump = (int) hashMap.get("selectedAsTrumpCount");

            float selectedByPer = 0;
            float selectedByCaptionPer = 0;
            float selectedByVCaptionPer = 0;
            float selectedByTrumpPer = 0;
            if (basketballMatch.getTotalCustomerTeam() > 0) {
                selectedByPer = (((float) selected_by) / basketballMatch.getTotalCustomerTeam()) * 100f;
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

            int player_teamId = (int) hashMap.get("player_teamId");

            HashMap<String, Object> country = new HashMap<String, Object>();
            country.put("id", hashMap.get("country_id"));
            country.put("name", hashMap.get("country_name"));

            HashMap<String, Object> player = new HashMap<String, Object>();

            player.put("player_id", hashMap.get("playerUniqueId"));
            player.put("player_pos", hashMap.get("position"));
            player.put("player_multiplier", hashMap.get("multiplier"));
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));
            player.put("name", Util.getPlayerFormattedName(hashMap.get("name").toString()));
            player.put("team_id", player_teamId);
            player.put("points", hashMap.get("points"));
            player.put("bat_type", hashMap.get("bets"));
            player.put("bowl_type", hashMap.get("bowls"));
            player.put("dob", hashMap.get("dob"));
            player.put("country", country.get("name"));
            player.put("credits", hashMap.get("credits"));
            player.put("is_in_playing_squad", hashMap.get("isInPlayingSquad"));
            player.put("playing_squad_updated", basketballMatch.getPlayingSquadUpdated());
            player.put("position", hashMap.get("playingRole"));
            player.put("total_points", hashMap.get("series_total_points"));
            player.put("is_in_substitute_squad", hashMap.get("isInSubstituteSquad"));
            player.put("selected_by", selectedByPer);
            player.put("selected_as_caption", selectedByCaptionPer);
            player.put("selected_as_vccaption", selectedByVCaptionPer);
            player.put("selected_as_trump", selectedByTrumpPer);

            String position = player.get("position").toString();
            if (!position.isEmpty()) {

                List<HashMap<String, Object>> batsmans = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> bowlers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> wicketkeapers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> allrounders = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> centers = new ArrayList<HashMap<String, Object>>();

                HashMap<String, Object> captain = null;
                HashMap<String, Object> vise_captain = null;
                HashMap<String, Object> trump = null;

                HashMap<String, Object> team = new HashMap<>();
                if (customerTeam != null) {
                    team = customerTeam;
                    batsmans = (List<HashMap<String, Object>>) team.get("batsmans");
                    bowlers = (List<HashMap<String, Object>>) team.get("bowlers");
                    wicketkeapers = (List<HashMap<String, Object>>) team.get("wicketkeapers");
                    allrounders = (List<HashMap<String, Object>>) team.get("allrounders");
                    centers = (List<HashMap<String, Object>>) team.get("centers");

                    captain = (HashMap<String, Object>) team.get("captain");
                    vise_captain = (HashMap<String, Object>) team.get("vise_captain");
                    trump = (HashMap<String, Object>) team.get("trump");
                } else {
                    team.put("customer_id", customerId);
                    team.put("id", hashMap.get("team_id"));
                    team.put("name", hashMap.get("team_name"));
                    team.put("team1", modifiedMatchData.get("team1"));
                    team.put("team2", modifiedMatchData.get("team2"));
                    team.put("batsmans", batsmans);
                    team.put("bowlers", bowlers);
                    team.put("wicketkeapers", wicketkeapers);
                    team.put("allrounders", allrounders);
                    team.put("centers", centers);
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeam = team;

                }

                if (position.equalsIgnoreCase("point guard")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("shooting guard")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("small forward")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("power forward")) {
                    bowlers.add(player);
                } else if (position.equalsIgnoreCase("center")) {
                    centers.add(player);
                }

                Float multi = Float.parseFloat(player.get("player_multiplier").toString());

                if (multi == 2 || multi == 5) {
                    captain = player;
                } else if (multi == 1.5 || multi == 4.5) {
                    vise_captain = player;
                }

                if (multi > 2.5) {
                    trump = player;
                }

                team.put("captain", captain);
                team.put("vise_captain", vise_captain);
                team.put("trump", trump);

                float previousPoints = (float) team.get("team_total_points");
                float previousCredits = (float) team.get("team_total_credits");
                int previousTeam1SelectedPlayer = (int) team.get("team1_selected_player");
                int previousTeam2SelectedPlayer = (int) team.get("team2_selected_player");

                previousPoints += (multi * Float.parseFloat(player.get("points").toString()));
                previousCredits += (Float.parseFloat(player.get("credits").toString()));

                if (player_teamId == basketballMatch.getTeam1Id()) {
                    previousTeam1SelectedPlayer++;
                } else {
                    previousTeam2SelectedPlayer++;
                }

                team.put("team_total_points", previousPoints);
                team.put("team_total_credits", previousCredits);
                team.put("team1_selected_player", previousTeam1SelectedPlayer);
                team.put("team2_selected_player", previousTeam2SelectedPlayer);

            }

        }

        return customerTeam;

    }

    @Transactional(readOnly = true)
    public Object getMatchDreamTeamDetail(int matchUniqueid, MatchesService matchesService) {

        TblBasketballMatch basketballMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueid);
        if (basketballMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(basketballMatch);

        String hql = "SELECT " + "0 AS team_id, 'dream team' AS team_name, "
                + "tcmp.playerUniqueId AS playerUniqueId, tcmp.dreamTeamPlayer AS dreamTeamPlayer, tcmp.teamId AS player_teamId, tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, "
                + "tcsp.points AS series_total_points "
                + "FROM TblBasketballMatchPlayer tcmp "
                + "LEFT JOIN  TblBasketballPlayer tcp ON tcp.uniqueId = tcmp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblBasketballSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcmp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "WHERE " + "tcmp.dreamTeamPlayer = 'Y' AND tcmp.matchUniqueId=:matchUniqueId "
                + "ORDER BY tcmp.points DESC";

        Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueid)
                .setParameter("seriesId", basketballMatch.getSeriesId())
                .setParameter("gameTypeId", basketballMatch.getGameTypeId());

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> customerTeams = null;
        int playerPosition = 0;
        Float[] multiplier_array = GlobalConstant.MULTIPLIER_ARRAY_BASKETBALL;
        for (HashMap<String, Object> hashMap : result) {

            int player_teamId = (int) hashMap.get("player_teamId");

            HashMap<String, Object> country = new HashMap<String, Object>();
            country.put("id", hashMap.get("country_id"));
            country.put("name", hashMap.get("country_name"));

            HashMap<String, Object> player = new HashMap<String, Object>();

            player.put("player_id", hashMap.get("playerUniqueId"));
            player.put("player_pos", playerPosition + 1);
            player.put("player_multiplier", multiplier_array[playerPosition]);
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));
            player.put("name", Util.getPlayerFormattedName(hashMap.get("name").toString()));
            player.put("team_id", player_teamId);
            player.put("points", hashMap.get("points"));
            player.put("bat_type", hashMap.get("bets"));
            player.put("bowl_type", hashMap.get("bowls"));
            player.put("dob", hashMap.get("dob"));
            player.put("country", country.get("name"));
            player.put("credits", hashMap.get("credits"));
            player.put("is_in_playing_squad", hashMap.get("isInPlayingSquad"));
            player.put("playing_squad_updated", basketballMatch.getPlayingSquadUpdated());
            player.put("dream_team_player", hashMap.get("dreamTeamPlayer"));
            player.put("position", hashMap.get("playingRole"));
            player.put("total_points", hashMap.get("series_total_points"));
            player.put("is_in_substitute_squad", hashMap.get("isInSubstituteSquad"));

            String position = player.get("position").toString();
            if (!position.isEmpty()) {

                List<HashMap<String, Object>> batsmans = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> bowlers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> wicketkeapers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> allrounders = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> centers = new ArrayList<HashMap<String, Object>>();


                HashMap<String, Object> captain = null;
                HashMap<String, Object> vise_captain = null;
                HashMap<String, Object> trump = null;

                HashMap<String, Object> team = new HashMap<>();
                if (customerTeams != null) {
                    team = customerTeams;

                    batsmans = (List<HashMap<String, Object>>) team.get("batsmans");
                    bowlers = (List<HashMap<String, Object>>) team.get("bowlers");
                    wicketkeapers = (List<HashMap<String, Object>>) team.get("wicketkeapers");
                    allrounders = (List<HashMap<String, Object>>) team.get("allrounders");
                    centers = (List<HashMap<String, Object>>) team.get("centers");

                    captain = (HashMap<String, Object>) team.get("captain");
                    vise_captain = (HashMap<String, Object>) team.get("vise_captain");
                    trump = (HashMap<String, Object>) team.get("trump");
                } else {
                    team.put("id", hashMap.get("team_id"));
                    team.put("name", hashMap.get("team_name"));
                    team.put("team1", modifiedMatchData.get("team1"));
                    team.put("team2", modifiedMatchData.get("team2"));
                    team.put("batsmans", batsmans);
                    team.put("bowlers", bowlers);
                    team.put("wicketkeapers", wicketkeapers);
                    team.put("allrounders", allrounders);
                    team.put("centers", centers);
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeams = team;

                }

                if (position.equalsIgnoreCase("point guard")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("shooting guard")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("small forward")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("power forward")) {
                    bowlers.add(player);
                } else if (position.equalsIgnoreCase("center")) {
                    centers.add(player);
                }

                Float multi = Float.parseFloat(player.get("player_multiplier").toString());

                if (multi == 2 || multi == 5) {
                    captain = player;
                } else if (multi == 1.5 || multi == 4.5) {
                    vise_captain = player;
                }

                if (multi > 2.5) {
                    trump = player;
                }

                team.put("captain", captain);
                team.put("vise_captain", vise_captain);
                team.put("trump", trump);

                float previousPoints = (float) team.get("team_total_points");
                float previousCredits = (float) team.get("team_total_credits");
                int previousTeam1SelectedPlayer = (int) team.get("team1_selected_player");
                int previousTeam2SelectedPlayer = (int) team.get("team2_selected_player");

                previousPoints += (multi * Float.parseFloat(player.get("points").toString()));
                previousCredits += (Float.parseFloat(player.get("credits").toString()));

                if (player_teamId == basketballMatch.getTeam1Id()) {
                    previousTeam1SelectedPlayer++;
                } else {
                    previousTeam2SelectedPlayer++;
                }

                team.put("team_total_points", previousPoints);
                team.put("team_total_credits", previousCredits);
                team.put("team1_selected_player", previousTeam1SelectedPlayer);
                team.put("team2_selected_player", previousTeam2SelectedPlayer);

            }

            playerPosition++;

        }

        return customerTeams;

    }

    @Transactional(readOnly = true)
    public List<HashMap<String, Object>> getMatchCustomerTeamCount(Integer[] customerTeamIds, Integer UserId) {
        if (customerTeamIds == null || customerTeamIds.length == 0) {
            return new ArrayList<HashMap<String, Object>>();
        }
        String hqln = "SELECT " + "id as id, name as name " + "FROM TblBasketballCustomerTeam " + "WHERE "
                + "id IN (:customerTeamIds) AND customerId=:customerId";
        Query queryy = getSession().createQuery(hqln).setParameterList("customerTeamIds", customerTeamIds)
                .setParameter("customerId", UserId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getCustomerMatchTeamStats(int UserId, int customerTeamId) {

        String hql = "SELECT "
                + "tct.fullName as fullName, tct.sortName as sortName, tct.id as playerTeamId, tct.logo as logo, " +
                "tsmps.pointsScored as Points_Scored, tsmps.pointsScoredValue as Points_Scored_Value, tsmps.rebounds as Rebounds, tsmps.reboundsValue as Rebounds_Value, tsmps.assists as Assists, tsmps.assistsValue as Assists_Value, tsmps.steals as Steals, tsmps.stealsValue as Steals_Value, tsmps.blocks as Blocks, tsmps.blocksValue as Blocks_Value, tsmps.turnOvers as Turn_Overs, tsmps.turnOversValue as Turn_Overs_Value, "
                + "tsct.id AS team_id,tsct.name AS team_name, "
                + "tsctp.playerUniqueId AS playerUniqueId, tsctp.matchUniqueId as matchUniqueId, "
                + "tsmp.points AS points,tsmp.credits AS credits,tsmp.isInPlayingSquad AS isInPlayingSquad,tsmp.playingRole AS playingRole,tsmp.image AS image,tsmp.isInSubstituteSquad AS isInSubstituteSquad,tsmp.selectedByCount as selectedByCount,tsmp.selectedAsCaptionCount as selectedAsCaptionCount,tsmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tsmp.selectedAsTrumpCount as selectedAsTrumpCount,tsmp.dreamTeamPlayer, "
                + "tsp.name AS name " + "FROM TblBasketballCustomerTeamPlyer tsctp "
                + "LEFT JOIN  TblBasketballMatchPlayer tsmp ON (tsmp.playerUniqueId = tsctp.playerUniqueId AND tsmp.matchUniqueId=tsctp.matchUniqueId) "
                + "LEFT JOIN  TblBasketballPlayer tsp ON tsp.uniqueId = tsctp.playerUniqueId "
                + "LEFT JOIN TblBasketballCustomerTeam tsct ON tsct.id = tsctp.customerTeamId "
                + "LEFT JOIN TblBasketballMatchPlayersStat tsmps ON (tsmps.playerUniqueId=tsctp.playerUniqueId AND tsmps.matchUniqueId = tsctp.matchUniqueId) "
                + "LEFT JOIN TblBasketballTeam tct ON tct.id=tsctp.teamId "
                + "WHERE tsctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("customerTeamId", customerTeamId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        int totalCustomerTeam = 0;
        if (result.size() > 0) {

            HashMap<String, Object> hashMap = result.get(0);
            int matchUniqueId = (int) hashMap.get("matchUniqueId");

            String hqlMtch = "SELECT tsm.totalCustomerTeam as totalCustomerTeam FROM TblBasketballMatch tsm where tsm.uniqueId=:uniqueId";

            Query queryyM = getSession().createQuery(hqlMtch).setParameter("uniqueId", matchUniqueId);

            HashMap<String, Object> resultM = (HashMap<String, Object>) queryyM
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            totalCustomerTeam = (int) resultM.get("totalCustomerTeam");
        }

        HashMap<String, Object> customerTeams = new HashMap<String, Object>();
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

            HashMap<String, Object> team = customerTeams;
            if (team.isEmpty()) {
                team.put("id", hashMap.get("team_id"));
                team.put("name", hashMap.get("team_name"));
                team.put("players_stats", new ArrayList<HashMap<String, Object>>());
            }

            List<HashMap<String, Object>> players = (List<HashMap<String, Object>>) team.get("players_stats");

            HashMap<String, Object> playerTeam = new HashMap<String, Object>();
            playerTeam.put("name", hashMap.get("fullName"));
            playerTeam.put("sort_name", hashMap.get("sortName"));
            playerTeam.put("id", hashMap.get("playerTeamId"));
            playerTeam.put("image", Util.generateImageUrl((String) hashMap.get("logo"), FileUploadConstant.TEAMCRICKET_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_TEAM));

            HashMap<String, Object> player = new HashMap<String, Object>();

            player.put("match_team_count", 0);
            player.put("player_team_count", 0);
            player.put("team_data", playerTeam);
            player.put("position", hashMap.get("playingRole"));
            player.put("selected_by", selectedByPer);
            player.put("selected_as_caption", selectedByCaptionPer);
            player.put("selected_as_vccaption", selectedByVCaptionPer);
            player.put("selected_as_trump", selectedByTrumpPer);
            player.put("player_unique_id", hashMap.get("playerUniqueId"));
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));
            player.put("name", hashMap.get("name"));
            player.put("points", hashMap.get("points"));
            player.put("credits", hashMap.get("credits"));
            player.put("dream_team_player", hashMap.get("dreamTeamPlayer"));

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

            players.add(player);
        }

        return customerTeams;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchDreamTeamStats(Integer authUserId, int matchUniqueId) {

        String hqlMtch = "SELECT tsm.totalCustomerTeam as totalCustomerTeam FROM TblBasketballMatch tsm where tsm.uniqueId=:uniqueId";

        Query queryyM = getSession().createQuery(hqlMtch).setParameter("uniqueId", matchUniqueId);

        HashMap<String, Object> resultM = (HashMap<String, Object>) queryyM
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int totalCustomerTeam = (int) resultM.get("totalCustomerTeam");

        String hql = "SELECT "
                + "tst.fullName as fullName, tst.sortName as sortName, tst.id as playerTeamId, tst.logo as logo, " +
                "tsmps.pointsScored as Points_Scored, tsmps.pointsScoredValue as Points_Scored_Value, tsmps.rebounds as Rebounds, tsmps.reboundsValue as Rebounds_Value, tsmps.assists as Assists, tsmps.assistsValue as Assists_Value, tsmps.steals as Steals, tsmps.stealsValue as Steals_Value, tsmps.blocks as Blocks, tsmps.blocksValue as Blocks_Value, tsmps.turnOvers as Turn_Overs, tsmps.turnOversValue as Turn_Overs_Value, "
                + "0 AS team_id, 'dream team' AS team_name, " + "tsmp.playerUniqueId AS playerUniqueId, "
                + "tsmp.points AS points,tsmp.credits AS credits,tsmp.isInPlayingSquad AS isInPlayingSquad,tsmp.playingRole AS playingRole,tsmp.image AS image,tsmp.isInSubstituteSquad AS isInSubstituteSquad,tsmp.selectedByCount as selectedByCount,tsmp.selectedAsCaptionCount as selectedAsCaptionCount,tsmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tsmp.selectedAsTrumpCount as selectedAsTrumpCount,tsmp.dreamTeamPlayer, "
                + "tsp.name AS name " + "FROM TblBasketballMatchPlayer tsmp "
                + "LEFT JOIN  TblBasketballPlayer tsp ON tsp.uniqueId = tsmp.playerUniqueId "
                + "LEFT JOIN TblBasketballMatchPlayersStat tsmps ON (tsmps.playerUniqueId=tsmp.playerUniqueId AND tsmps.matchUniqueId = tsmp.matchUniqueId) "
                + "LEFT JOIN TblBasketballTeam tst ON tst.id=tsmp.teamId "
                + "WHERE tsmp.matchUniqueId = :matchUniqueId AND tsmp.dreamTeamPlayer = 'Y' ";

        Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> customerTeams = new HashMap<String, Object>();
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

            HashMap<String, Object> team = customerTeams;
            if (team.isEmpty()) {
                team.put("id", hashMap.get("team_id"));
                team.put("name", hashMap.get("team_name"));
                team.put("players_stats", new ArrayList<HashMap<String, Object>>());
            }

            List<HashMap<String, Object>> players = (List<HashMap<String, Object>>) team.get("players_stats");

            HashMap<String, Object> playerTeam = new HashMap<String, Object>();
            playerTeam.put("name", hashMap.get("fullName"));
            playerTeam.put("sort_name", hashMap.get("sortName"));
            playerTeam.put("id", hashMap.get("playerTeamId"));
            playerTeam.put("image", Util.generateImageUrl((String) hashMap.get("logo"), FileUploadConstant.TEAMCRICKET_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_TEAM));

            HashMap<String, Object> player = new HashMap<String, Object>();

            player.put("match_team_count", 0);
            player.put("player_team_count", 0);
            player.put("team_data", playerTeam);
            player.put("position", hashMap.get("playingRole"));
            player.put("selected_by", selectedByPer);
            player.put("selected_as_caption", selectedByCaptionPer);
            player.put("selected_as_vccaption", selectedByVCaptionPer);
            player.put("selected_as_trump", selectedByTrumpPer);
            player.put("player_unique_id", hashMap.get("playerUniqueId"));
            player.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));
            player.put("name", hashMap.get("name"));
            player.put("points", hashMap.get("points"));
            player.put("credits", hashMap.get("credits"));
            player.put("dream_team_player", hashMap.get("dreamTeamPlayer"));


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

            players.add(player);
        }

        return customerTeams;
    }

    @Transactional
    public void updateCustomerMatchTeamInfo(int matchUniqueId, Integer customerId) {
        String hqln = "SELECT COUNT(tcct.id) AS cnt FROM TblBasketballCustomerTeam tcct WHERE tcct.matchUniqueId =:matchUniqueId AND tcct.customerId =:customerId";
        Query queryy = getSession().createQuery(hqln);
        queryy.setParameter("matchUniqueId", matchUniqueId);
        queryy.setParameter("customerId", customerId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        long count = 0;
        if (result != null && result.size() > 0) {
            count = (long) result.get(0).get("cnt");
        }

        if (count > 0) {
            String mysqlQuery1 = "INSERT " + "INTO tbl_basketball_customer_match_info "
                    + "SET match_unique_id =" + matchUniqueId + ", "
                    + "customer_id =" + customerId + ", "
                    + "team_count =" + count + " "
                    + "ON DUPLICATE KEY UPDATE team_count =" + count;
            Query queryyy = getSession().createSQLQuery(mysqlQuery1);
            queryyy.executeUpdate();
        }
    }

}