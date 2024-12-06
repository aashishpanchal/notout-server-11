package com.choic11.repository.cricket;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.cricket.TblCricketCustomerTeam;
import com.choic11.model.cricket.TblCricketMatch;
import com.choic11.service.cricket.MatchesService;
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

@Repository
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
        String hqln = "SELECT id as id FROM  TblCricketCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", userId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result == null ? 0 : result.size();

    }

    @Transactional(readOnly = true)
    public int checkCustomerTeamIdExist(Integer userId, int matchUniqueId, int teamId) {
        String hqln = "SELECT id as id   FROM  TblCricketCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId AND id=:id";

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
                    + "FROM  TblCricketMatch tcm "
                    + "LEFT JOIN TblCricketCustomerTeam tcct ON (tcct.customerId=:customerId AND tcct.matchUniqueId=tcm.uniqueId) "
                    + "LEFT JOIN TblCricketCustomerTeam tcct1 ON (tcct1.customerId=:customerId1 AND tcct1.matchUniqueId=tcm.uniqueId AND tcct1.playerUniqueIdsMultiplers=:playerIdsMultipliers)  "
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

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblCricketCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND  customerTeamName =:customerTeamName AND customerId != :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "CUSTOMER_TEAM_NAME_ALREADY_EXIST";
                }

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblCricketCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId  AND customerTeamName=:customerTeamName  AND moreName = :moreName AND customerId = :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("moreName", moreName)
                        .setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "TEAM_NAME_ALREADY_EXIST";
                }
            }

            String hqln1 = "SELECT " + "name as name " + "from TblCricketCustomerTeam as tcct " + "where "
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

            TblCricketCustomerTeam tblCricketCustomerTeam = new TblCricketCustomerTeam();
            tblCricketCustomerTeam.setCreated(time);
            tblCricketCustomerTeam.setCustomerId(userId);
            tblCricketCustomerTeam.setCustomerTeamName(customerTeamName);
            tblCricketCustomerTeam.setMatchUniqueId(matchUniqueId);
            tblCricketCustomerTeam.setMoreName(moreName);
            tblCricketCustomerTeam.setName(new_team_name);
            tblCricketCustomerTeam.setPlayerUniqueIds(playerIds);
            tblCricketCustomerTeam.setPlayerMultiplers(playerMultipliers);
            tblCricketCustomerTeam.setPlayerUniqueIdsMultiplers(playerIdsMultipliers);
            tblCricketCustomerTeam.setUpdated(time);

            tx = session.beginTransaction();

            int newCustomerTeamId = (int) session.save(tblCricketCustomerTeam);

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

                String mysqlQuery = "INSERT " + "INTO tbl_cricket_customer_team_plyers "
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
                String updateNormalPlayerHQL = "UPDATE " + "TblCricketMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:oldNormalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblCricketMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1, selectedAsVccaptionCount = selectedAsVccaptionCount-1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblCricketMatchPlayer "
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
                matchString = "UPDATE " + "TblCricketMatch " + "SET totalCustomerTeam = totalCustomerTeam+1 " + "WHERE "
                        + "uniqueId =:matchUniqueId";
            }

            if (playingSquadUpdated.equals("N")) {
                tx = session.beginTransaction();
                String updateNormalPlayerHQL = "UPDATE " + "TblCricketMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:normalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblCricketMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1, selectedAsVccaptionCount = selectedAsVccaptionCount+1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblCricketMatchPlayer "
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
                                     int customerTeamId, String isUpdateAdmin) {

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
                    + "FROM  TblCricketMatch tcm " + "WHERE "
                    + "tcm.uniqueId = :uniqueId AND tcm.status = 'A' AND tcm.isDeleted='N'";

            Query queryy = session.createQuery(hqln).setParameter("uniqueId", matchUniqueId);

            HashMap<String, Object> matchData = (HashMap<String, Object>) queryy
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

            if (matchData.get("uniqueId") == null) {
                return "NO_MATCH_FOUND";
            }

            if (!isUpdateAdmin.equals("1")) {
                if (!matchData.get("matchProgress").toString().equals("F")) {
                    return "INVALID_MATCH";
                }
            }
            String playingSquadUpdated=(matchData.get("playingSquadUpdated")!=null? (String) matchData.get("playingSquadUpdated") :"N");


            String hqlQuery = "SELECT tcct FROM TblCricketCustomerTeam tcct WHERE tcct.id =:id";
            Query queryObj = session.createQuery(hqlQuery);
            queryObj.setParameter("id", customerTeamId);
            TblCricketCustomerTeam customerTeam = (TblCricketCustomerTeam) queryObj.getSingleResult();

            if (customerTeam.getMatchUniqueId() != matchUniqueId) {
                return "INVALID_MATCH";
            }

//            Criteria createCriteria = session.createCriteria(TblCricketCustomerTeam.class);
//            createCriteria.add(Restrictions.eq("id", customerTeamId));
//            TblCricketCustomerTeam customerTeam = (TblCricketCustomerTeam) createCriteria.uniqueResult();

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
                        String updatePlayers = " UPDATE TblCricketCustomerTeamPlyer SET multiplier = :multiplier WHERE playerUniqueId= :playerUniqueId AND customerTeamId =:customerTeamId";
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

                    String updatePlayers = " UPDATE " + "TblCricketCustomerTeamPlyer "
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
            String updateTeam = " UPDATE " + "TblCricketCustomerTeam "
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
        String hqln = "SELECT " + "COUNT(id) as id  " + "FROM  TblCricketCustomerTeam  " + "WHERE "
                + "customerId = :customerId AND matchUniqueId = :matchUniqueId";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return (long) result.get(0).get("id");

    }

    @Transactional(readOnly = true)
    public Object getCustomerMatchTeam(int UserId, int matchUniqueId, MatchesService matchesService) {
        TblCricketMatch cricketMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);

        if (cricketMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(cricketMatch);

        String hql = "SELECT " + "tcct.id AS team_id,tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position,tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblCricketCustomerTeamPlyer tcctp "
                + "LEFT JOIN  TblCricketMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblCricketPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblCricketSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId) "
                + "LEFT JOIN TblCricketCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerId = :UserId AND  tcctp.matchUniqueId = :matchUniqueId ORDER BY tcctp.customerTeamId ASC ";

        Query queryy = getSession().createQuery(hql).setParameter("UserId", UserId)
                .setParameter("seriesId", cricketMatch.getSeriesId())
                .setParameter("gameTypeId", cricketMatch.getGameTypeId()).setParameter("matchUniqueId", matchUniqueId);

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
            player.put("playing_squad_updated", cricketMatch.getPlayingSquadUpdated());
            player.put("position", hashMap.get("playingRole"));
            player.put("total_points", hashMap.get("series_total_points"));
            player.put("is_in_substitute_squad", hashMap.get("isInSubstituteSquad"));

            String position = player.get("position").toString();
            if (!position.isEmpty()) {

                List<HashMap<String, Object>> batsmans = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> bowlers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> wicketkeapers = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> allrounders = new ArrayList<HashMap<String, Object>>();

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
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeams.put((int) team.get("id"), team);

                }

                if (position.equalsIgnoreCase("wicketkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("batsman")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("allrounder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("bowler")) {
                    bowlers.add(player);
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

                if (player_teamId == cricketMatch.getTeam1Id()) {
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

        String getMatchUniqueId = "SELECT " + "tcct.matchUniqueId as matchUniqueId, tcct.customerId as customerId " + "FROM TblCricketCustomerTeam tcct " + "WHERE " + "id = :customerTeamId";
        Query queryMatchUniqueId = getSession().createQuery(getMatchUniqueId).setParameter("customerTeamId",
                customerTeamId);

        HashMap<String, Object> matchResult = (HashMap<String, Object>) queryMatchUniqueId
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int matchUniqueId = Integer.parseInt(matchResult.get("matchUniqueId").toString());
        int customerId = Integer.parseInt(matchResult.get("customerId").toString());

        TblCricketMatch cricketMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);
        if (cricketMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(cricketMatch);

        String hql = "SELECT " + "tcct.id AS team_id, tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position, tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad,tcmp.selectedByCount as selectedByCount,tcmp.selectedAsCaptionCount as selectedAsCaptionCount,tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tcmp.selectedAsTrumpCount as selectedAsTrumpCount, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblCricketCustomerTeamPlyer  tcctp "
                + "LEFT JOIN  TblCricketMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblCricketPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblCricketSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "LEFT JOIN TblCricketCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("seriesId", cricketMatch.getSeriesId())
                .setParameter("gameTypeId", cricketMatch.getGameTypeId())
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
            if (cricketMatch.getTotalCustomerTeam() > 0) {
                selectedByPer = (((float) selected_by) / cricketMatch.getTotalCustomerTeam()) * 100f;
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
            player.put("playing_squad_updated", cricketMatch.getPlayingSquadUpdated());
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
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeam = team;

                }

                if (position.equalsIgnoreCase("wicketkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("batsman")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("allrounder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("bowler")) {
                    bowlers.add(player);
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

                if (player_teamId == cricketMatch.getTeam1Id()) {
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

        TblCricketMatch cricketMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueid);
        if (cricketMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(cricketMatch);

        String hql = "SELECT " + "0 AS team_id, 'dream team' AS team_name, "
                + "tcmp.playerUniqueId AS playerUniqueId, tcmp.dreamTeamPlayer AS dreamTeamPlayer, tcmp.teamId AS player_teamId, tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, "
                + "tcsp.points AS series_total_points "
                + "FROM TblCricketMatchPlayer tcmp "
                + "LEFT JOIN  TblCricketPlayer tcp ON tcp.uniqueId = tcmp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblCricketSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcmp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "WHERE " + "tcmp.dreamTeamPlayer = 'Y' AND tcmp.matchUniqueId=:matchUniqueId "
                + "ORDER BY tcmp.points DESC";

        Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueid)
                .setParameter("seriesId", cricketMatch.getSeriesId())
                .setParameter("gameTypeId", cricketMatch.getGameTypeId());

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> customerTeams = null;
        int playerPosition = 0;
        Float[] multiplier_array = GlobalConstant.MULTIPLIER_ARRAY;
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
            player.put("playing_squad_updated", cricketMatch.getPlayingSquadUpdated());
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
                    team.put("team_total_points", 0f);
                    team.put("team_total_credits", 0f);
                    team.put("team1_selected_player", 0);
                    team.put("team2_selected_player", 0);

                    customerTeams = team;

                }

                if (position.equalsIgnoreCase("wicketkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("batsman")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("allrounder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("bowler")) {
                    bowlers.add(player);
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

                if (player_teamId == cricketMatch.getTeam1Id()) {
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
        String hqln = "SELECT " + "id as id, name as name " + "FROM TblCricketCustomerTeam " + "WHERE "
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
                + "tct.fullName as fullName, tct.sortName as sortName, tct.id as playerTeamId, tct.logo as logo, "
                + "tcmps.beingPartOfEleven as Being_Part_Of_Eleven, tcmps.beingPartOfElevenValue as Being_Part_Of_Eleven_Value, tcmps.everyRunScored as Every_Run_Scored, tcmps.everyRunScoredValue as Every_Run_Scored_Value, tcmps.everyBoundaryHit as Every_Boundary_Hit, tcmps.everyBoundaryHitValue as Every_Boundary_Hit_Value, tcmps.everySixHit as Every_Six_Hit, tcmps.everySixHitValue as Every_Six_Hit_Value, tcmps.halfCentury as Half_Century, tcmps.halfCenturyValue as Half_Century_Value, tcmps.century as Century, tcmps.centuryValue as Century_Value, tcmps.dismissForADuck as Dismiss_For_A_Duck, tcmps.dismissForADuckValue as Dismiss_For_A_Duck_Value, tcmps.wicket as Wicket, tcmps.wicketValue as Wicket_Value, tcmps.fiveWicket as Five_Wicket, tcmps.fiveWicketValue as Five_Wicket_Value, tcmps.fourWicket as Four_Wicket, tcmps.fourWicketValue as Four_Wicket_Value, tcmps.maidenOver as Maiden_Over, tcmps.maidenOverValue as Maiden_Over_Value, tcmps.catch1 as Catch, tcmps.catchValue as Catch_Value, tcmps.catchAndBowled as Catch_And_Bowled, tcmps.catchAndBowledValue as Catch_And_Bowled_Value, tcmps.stumping as Stumping, tcmps.stumpingValue as Stumping_Value, tcmps.runOut as Run_Out, tcmps.runOutValue as Run_Out_Value, tcmps.strikeRate as Strike_Rate, tcmps.strikeRateValue as Strike_Rate_Value, tcmps.economyRate as Economy_Rate, tcmps.economyRateValue as Economy_Rate_Value, tcmps.thirtyRuns as Thirty_Runs, tcmps.thirtyRunsValue as Thirty_Runs_Value, tcmps.threeWicket as Three_Wicket, tcmps.threeWicketValue as Three_Wicket_Value, tcmps.twoWicket as Two_Wicket, tcmps.twoWicketValue as Two_Wicket_Value, tcmps.runOutCatcher as Run_Out_Catcher, tcmps.runOutCatcherValue as Run_Out_Catcher_Value, tcmps.runOutThrower as Run_Out_Thrower, tcmps.runOutThrowerValue as Run_Out_Thrower_Value, tcmps.threeCatch as Three_Catch, tcmps.threeCatchValue as Three_Catch_Value, tcmps.lbwBowled as Lbw_Bowled, tcmps.lbwBowledValue as Lbw_Bowled_Value, "
                + "tcct.id AS team_id,tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.matchUniqueId as matchUniqueId, "
                + "tcmp.points AS points,tcmp.credits AS credits,tcmp.isInPlayingSquad AS isInPlayingSquad,tcmp.playingRole AS playingRole,tcmp.image AS image,tcmp.isInSubstituteSquad AS isInSubstituteSquad,tcmp.selectedByCount as selectedByCount,tcmp.selectedAsCaptionCount as selectedAsCaptionCount,tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tcmp.selectedAsTrumpCount as selectedAsTrumpCount,tcmp.dreamTeamPlayer, "
                + "tcp.name AS name " + "FROM TblCricketCustomerTeamPlyer tcctp "
                + "LEFT JOIN  TblCricketMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblCricketPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN TblCricketCustomerTeam tcct ON tcct.id = tcctp.customerTeamId "
                + "LEFT JOIN TblCricketMatchPlayersStat tcmps ON (tcmps.playerUniqueId=tcctp.playerUniqueId AND tcmps.matchUniqueId = tcctp.matchUniqueId) "
                + "LEFT JOIN TblCricketTeam tct ON tct.id=tcctp.teamId "
                + "WHERE tcctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("customerTeamId", customerTeamId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        int totalCustomerTeam = 0;
        if (result.size() > 0) {

            HashMap<String, Object> hashMap = result.get(0);
            int matchUniqueId = (int) hashMap.get("matchUniqueId");

            String hqlMtch = "SELECT tcm.totalCustomerTeam as totalCustomerTeam FROM TblCricketMatch tcm where tcm.uniqueId=:uniqueId";

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

            if (hashMap.get("Being_Part_Of_Eleven_Value") != null
                    && (float) hashMap.get("Being_Part_Of_Eleven_Value") == 1) {

                HashMap<String, Object> event = new HashMap<String, Object>();
                event.put("key", "Starting 11");
                event.put("points", hashMap.get("Being_Part_Of_Eleven"));
                event.put("value", (float) hashMap.get("Being_Part_Of_Eleven_Value") == 1 ? "YES" : "NO");

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Runs");
                event.put("points", hashMap.get("Every_Run_Scored"));
                event.put("value", hashMap.get("Every_Run_Scored_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "4's");
                event.put("points", hashMap.get("Every_Boundary_Hit"));
                event.put("value", hashMap.get("Every_Boundary_Hit_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "6's");
                event.put("points", hashMap.get("Every_Six_Hit"));
                event.put("value", hashMap.get("Every_Six_Hit_Value"));

                playerEvents.add(event);

                float halfCenturyValue = (float) hashMap.get("Half_Century");
                float century = (float) hashMap.get("Century");
                float thirty_Runs = (float) hashMap.get("Thirty_Runs");

                int runBonusGiven = 0;
                event = new HashMap<String, Object>();
                if (thirty_Runs > 0) {
                    event.put("key", "30");
                    event.put("points", hashMap.get("Thirty_Runs"));
                    event.put("value", hashMap.get("Thirty_Runs_Value"));
                    runBonusGiven = 1;
                }

                if (halfCenturyValue > 0) {
                    event.put("key", "50");
                    event.put("points", hashMap.get("Half_Century"));
                    event.put("value", hashMap.get("Half_Century_Value"));
                    runBonusGiven = 1;
                }

                if (century > 0) {
                    event.put("key", "100");
                    event.put("points", hashMap.get("Century"));
                    event.put("value", hashMap.get("Century_Value"));
                    runBonusGiven = 1;
                }

                if (runBonusGiven == 0) {
                    event.put("key", "50");
                    event.put("points", 0);
                    event.put("value", 0);
                }

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Duck");
                event.put("points", hashMap.get("Dismiss_For_A_Duck"));
                event.put("value", ((float) hashMap.get("Dismiss_For_A_Duck_Value")) == 1 ? "YES" : "NO");

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Wkts");
                event.put("points", hashMap.get("Wicket"));
                event.put("value", hashMap.get("Wicket_Value"));

                playerEvents.add(event);

                float TwoWktValue = (float) hashMap.get("Two_Wicket");
                float ThreeWktValue = (float) hashMap.get("Three_Wicket");
                float FourWktValue = (float) hashMap.get("Four_Wicket");
                float FiveWktValue = (float) hashMap.get("Five_Wicket");

                int wktBonusGiven = 0;
                event = new HashMap<String, Object>();
                if (TwoWktValue > 0) {
                    event.put("key", "2 Wkts");
                    event.put("points", hashMap.get("Two_Wicket"));
                    event.put("value", hashMap.get("Two_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (ThreeWktValue > 0) {
                    event.put("key", "3 Wkts");
                    event.put("points", hashMap.get("Three_Wicket"));
                    event.put("value", hashMap.get("Three_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (FourWktValue > 0) {
                    event.put("key", "4 Wkts");
                    event.put("points", hashMap.get("Four_Wicket"));
                    event.put("value", hashMap.get("Four_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (FiveWktValue > 0) {
                    event.put("key", "5 Wkts");
                    event.put("points", hashMap.get("Five_Wicket"));
                    event.put("value", hashMap.get("Five_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (wktBonusGiven == 0) {
                    event.put("key", "4 Wkts");
                    event.put("points", 0);
                    event.put("value", 0);
                }
                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Maiden Over");
                event.put("points", hashMap.get("Maiden_Over"));
                event.put("value", hashMap.get("Maiden_Over_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Catch");
                event.put("points", hashMap.get("Catch"));
                event.put("value", hashMap.get("Catch_Value"));

                playerEvents.add(event);


                event = new HashMap<String, Object>();
                event.put("key", "3 Catch Bonus");
                event.put("points", hashMap.get("Three_Catch"));
                event.put("value", hashMap.get("Three_Catch_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "LBW/Bowled");
                event.put("points", hashMap.get("Lbw_Bowled"));
                event.put("value", hashMap.get("Lbw_Bowled_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Stumping");
                event.put("points", hashMap.get("Stumping"));
                event.put("value", hashMap.get("Stumping_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Run Out");
                event.put("points", ((float) hashMap.get("Run_Out")) + ((float) hashMap.get("Run_Out_Thrower"))
                        + ((float) hashMap.get("Run_Out_Catcher")));
                event.put("value",
                        ((float) hashMap.get("Run_Out_Value")) + ((float) hashMap.get("Run_Out_Thrower_Value"))
                                + ((float) hashMap.get("Run_Out_Catcher_Value")));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Strike Rate");
                event.put("points", hashMap.get("Strike_Rate"));
                event.put("value", hashMap.get("Strike_Rate_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Economy Rate");
                event.put("points", hashMap.get("Economy_Rate"));
                event.put("value", hashMap.get("Economy_Rate_Value"));

                playerEvents.add(event);

            }
            player.put("player_events", playerEvents);

            players.add(player);
        }

        return customerTeams;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchDreamTeamStats(Integer authUserId, int matchUniqueId) {

        String hqlMtch = "SELECT tcm.totalCustomerTeam as totalCustomerTeam FROM TblCricketMatch tcm where tcm.uniqueId=:uniqueId";

        Query queryyM = getSession().createQuery(hqlMtch).setParameter("uniqueId", matchUniqueId);

        HashMap<String, Object> resultM = (HashMap<String, Object>) queryyM
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int totalCustomerTeam = (int) resultM.get("totalCustomerTeam");

        String hql = "SELECT "
                + "tct.fullName as fullName, tct.sortName as sortName, tct.id as playerTeamId, tct.logo as logo, "
                + "tcmps.beingPartOfEleven as Being_Part_Of_Eleven, tcmps.beingPartOfElevenValue as Being_Part_Of_Eleven_Value, tcmps.everyRunScored as Every_Run_Scored, tcmps.everyRunScoredValue as Every_Run_Scored_Value, tcmps.everyBoundaryHit as Every_Boundary_Hit, tcmps.everyBoundaryHitValue as Every_Boundary_Hit_Value, tcmps.everySixHit as Every_Six_Hit, tcmps.everySixHitValue as Every_Six_Hit_Value, tcmps.halfCentury as Half_Century, tcmps.halfCenturyValue as Half_Century_Value, tcmps.century as Century, tcmps.centuryValue as Century_Value, tcmps.dismissForADuck as Dismiss_For_A_Duck, tcmps.dismissForADuckValue as Dismiss_For_A_Duck_Value, tcmps.wicket as Wicket, tcmps.wicketValue as Wicket_Value, tcmps.fiveWicket as Five_Wicket, tcmps.fiveWicketValue as Five_Wicket_Value, tcmps.fourWicket as Four_Wicket, tcmps.fourWicketValue as Four_Wicket_Value, tcmps.maidenOver as Maiden_Over, tcmps.maidenOverValue as Maiden_Over_Value, tcmps.catch1 as Catch, tcmps.catchValue as Catch_Value, tcmps.catchAndBowled as Catch_And_Bowled, tcmps.catchAndBowledValue as Catch_And_Bowled_Value, tcmps.stumping as Stumping, tcmps.stumpingValue as Stumping_Value, tcmps.runOut as Run_Out, tcmps.runOutValue as Run_Out_Value, tcmps.strikeRate as Strike_Rate, tcmps.strikeRateValue as Strike_Rate_Value, tcmps.economyRate as Economy_Rate, tcmps.economyRateValue as Economy_Rate_Value, tcmps.thirtyRuns as Thirty_Runs, tcmps.thirtyRunsValue as Thirty_Runs_Value, tcmps.threeWicket as Three_Wicket, tcmps.threeWicketValue as Three_Wicket_Value, tcmps.twoWicket as Two_Wicket, tcmps.twoWicketValue as Two_Wicket_Value, tcmps.runOutCatcher as Run_Out_Catcher, tcmps.runOutCatcherValue as Run_Out_Catcher_Value, tcmps.runOutThrower as Run_Out_Thrower, tcmps.runOutThrowerValue as Run_Out_Thrower_Value, tcmps.threeCatch as Three_Catch, tcmps.threeCatchValue as Three_Catch_Value, tcmps.lbwBowled as Lbw_Bowled, tcmps.lbwBowledValue as Lbw_Bowled_Value, "
                + "0 AS team_id, 'dream team' AS team_name, " + "tcmp.playerUniqueId AS playerUniqueId, "
                + "tcmp.points AS points,tcmp.credits AS credits,tcmp.isInPlayingSquad AS isInPlayingSquad,tcmp.playingRole AS playingRole,tcmp.image AS image,tcmp.isInSubstituteSquad AS isInSubstituteSquad,tcmp.selectedByCount as selectedByCount,tcmp.selectedAsCaptionCount as selectedAsCaptionCount,tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tcmp.selectedAsTrumpCount as selectedAsTrumpCount,tcmp.dreamTeamPlayer, "
                + "tcp.name AS name " + "FROM TblCricketMatchPlayer tcmp "
                + "LEFT JOIN  TblCricketPlayer tcp ON tcp.uniqueId = tcmp.playerUniqueId "
                + "LEFT JOIN TblCricketMatchPlayersStat tcmps ON (tcmps.playerUniqueId=tcmp.playerUniqueId AND tcmps.matchUniqueId = tcmp.matchUniqueId) "
                + "LEFT JOIN TblCricketTeam tct ON tct.id=tcmp.teamId "
                + "WHERE tcmp.matchUniqueId = :matchUniqueId AND tcmp.dreamTeamPlayer = 'Y' ";

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

            if ((float) hashMap.get("Being_Part_Of_Eleven_Value") == 1) {

                HashMap<String, Object> event = new HashMap<String, Object>();
                event.put("key", "Starting 11");
                event.put("points", hashMap.get("Being_Part_Of_Eleven"));
                event.put("value", (float) hashMap.get("Being_Part_Of_Eleven_Value") == 1 ? "YES" : "NO");

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Runs");
                event.put("points", hashMap.get("Every_Run_Scored"));
                event.put("value", hashMap.get("Every_Run_Scored_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "4's");
                event.put("points", hashMap.get("Every_Boundary_Hit"));
                event.put("value", hashMap.get("Every_Boundary_Hit_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "6's");
                event.put("points", hashMap.get("Every_Six_Hit"));
                event.put("value", hashMap.get("Every_Six_Hit_Value"));

                playerEvents.add(event);

                float halfCenturyValue = (float) hashMap.get("Half_Century");
                float century = (float) hashMap.get("Century");
                float thirty_Runs = (float) hashMap.get("Thirty_Runs");

                int runBonusGiven = 0;
                event = new HashMap<String, Object>();
                if (thirty_Runs > 0) {
                    event.put("key", "30");
                    event.put("points", hashMap.get("Thirty_Runs"));
                    event.put("value", hashMap.get("Thirty_Runs_Value"));
                    runBonusGiven = 1;
                }

                if (halfCenturyValue > 0) {
                    event.put("key", "50");
                    event.put("points", hashMap.get("Half_Century"));
                    event.put("value", hashMap.get("Half_Century_Value"));
                    runBonusGiven = 1;
                }

                if (century > 0) {
                    event.put("key", "100");
                    event.put("points", hashMap.get("Century"));
                    event.put("value", hashMap.get("Century_Value"));
                    runBonusGiven = 1;
                }

                if (runBonusGiven == 0) {
                    event.put("key", "50");
                    event.put("points", 0);
                    event.put("value", 0);
                }

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Duck");
                event.put("points", hashMap.get("Dismiss_For_A_Duck"));
                event.put("value", ((float) hashMap.get("Dismiss_For_A_Duck_Value")) == 1 ? "YES" : "NO");

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Wkts");
                event.put("points", hashMap.get("Wicket"));
                event.put("value", hashMap.get("Wicket_Value"));

                playerEvents.add(event);

                float TwoWktValue = (float) hashMap.get("Two_Wicket");
                float ThreeWktValue = (float) hashMap.get("Three_Wicket");
                float FourWktValue = (float) hashMap.get("Four_Wicket");
                float FiveWktValue = (float) hashMap.get("Five_Wicket");

                int wktBonusGiven = 0;
                event = new HashMap<String, Object>();
                if (TwoWktValue > 0) {
                    event.put("key", "2 Wkts");
                    event.put("points", hashMap.get("Two_Wicket"));
                    event.put("value", hashMap.get("Two_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (ThreeWktValue > 0) {
                    event.put("key", "3 Wkts");
                    event.put("points", hashMap.get("Three_Wicket"));
                    event.put("value", hashMap.get("Three_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (FourWktValue > 0) {
                    event.put("key", "4 Wkts");
                    event.put("points", hashMap.get("Four_Wicket"));
                    event.put("value", hashMap.get("Four_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (FiveWktValue > 0) {
                    event.put("key", "5 Wkts");
                    event.put("points", hashMap.get("Five_Wicket"));
                    event.put("value", hashMap.get("Five_Wicket_Value"));
                    wktBonusGiven = 1;
                }

                if (wktBonusGiven == 0) {
                    event.put("key", "4 Wkts");
                    event.put("points", 0);
                    event.put("value", 0);
                }
                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Maiden Over");
                event.put("points", hashMap.get("Maiden_Over"));
                event.put("value", hashMap.get("Maiden_Over_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Catch");
                event.put("points", hashMap.get("Catch"));
                event.put("value", hashMap.get("Catch_Value"));

                playerEvents.add(event);


                event = new HashMap<String, Object>();
                event.put("key", "3 Catch Bonus");
                event.put("points", hashMap.get("Three_Catch"));
                event.put("value", hashMap.get("Three_Catch_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "LBW/Bowled");
                event.put("points", hashMap.get("Lbw_Bowled"));
                event.put("value", hashMap.get("Lbw_Bowled_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Stumping");
                event.put("points", hashMap.get("Stumping"));
                event.put("value", hashMap.get("Stumping_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Run Out");
                event.put("points", ((float) hashMap.get("Run_Out")) + ((float) hashMap.get("Run_Out_Thrower"))
                        + ((float) hashMap.get("Run_Out_Catcher")));
                event.put("value",
                        ((float) hashMap.get("Run_Out_Value")) + ((float) hashMap.get("Run_Out_Thrower_Value"))
                                + ((float) hashMap.get("Run_Out_Catcher_Value")));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Strike Rate");
                event.put("points", hashMap.get("Strike_Rate"));
                event.put("value", hashMap.get("Strike_Rate_Value"));

                playerEvents.add(event);

                event = new HashMap<String, Object>();
                event.put("key", "Economy Rate");
                event.put("points", hashMap.get("Economy_Rate"));
                event.put("value", hashMap.get("Economy_Rate_Value"));

                playerEvents.add(event);

            }
            player.put("player_events", playerEvents);

            players.add(player);
        }

        return customerTeams;
    }

    @Transactional
    public void updateCustomerMatchTeamInfo(int matchUniqueId, Integer customerId) {
        String hqln = "SELECT COUNT(tcct.id) AS cnt FROM TblCricketCustomerTeam tcct WHERE tcct.matchUniqueId =:matchUniqueId AND tcct.customerId =:customerId";
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
            String mysqlQuery1 = "INSERT " + "INTO tbl_cricket_customer_match_info "
                    + "SET match_unique_id =" + matchUniqueId + ", "
                    + "customer_id =" + customerId + ", "
                    + "team_count =" + count + " "
                    + "ON DUPLICATE KEY UPDATE team_count =" + count;
            Query queryyy = getSession().createSQLQuery(mysqlQuery1);
            queryyy.executeUpdate();
        }
    }

}
