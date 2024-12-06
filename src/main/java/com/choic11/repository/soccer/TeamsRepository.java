package com.choic11.repository.soccer;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.soccer.TblSoccerCustomerTeam;
import com.choic11.model.soccer.TblSoccerMatch;
import com.choic11.service.soccer.MatchesService;
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

@Repository("SoccerTeamsRepository")
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
        String hqln = "SELECT id as id FROM  TblSoccerCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId";

        Query queryy = getSession().createQuery(hqln).setParameter("customerId", userId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return result == null ? 0 : result.size();

    }

    @Transactional(readOnly = true)
    public int checkCustomerTeamIdExist(Integer userId, int matchUniqueId, int teamId) {
        String hqln = "SELECT id as id   FROM  TblSoccerCustomerTeam  WHERE customerId = :customerId AND matchUniqueId = :matchUniqueId AND id=:id";

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
                    + "FROM  TblSoccerMatch tcm "
                    + "LEFT JOIN TblSoccerCustomerTeam tcct ON (tcct.customerId=:customerId AND tcct.matchUniqueId=tcm.uniqueId) "
                    + "LEFT JOIN TblSoccerCustomerTeam tcct1 ON (tcct1.customerId=:customerId1 AND tcct1.matchUniqueId=tcm.uniqueId AND tcct1.playerUniqueIdsMultiplers=:playerIdsMultipliers)  "
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

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblSoccerCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND  customerTeamName =:customerTeamName AND customerId != :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "CUSTOMER_TEAM_NAME_ALREADY_EXIST";
                }

                hqlAdmin = "SELECT " + "count(id) " + "FROM TblSoccerCustomerTeam " + "WHERE "
                        + "matchUniqueId = :matchUniqueId  AND customerTeamName=:customerTeamName  AND moreName = :moreName AND customerId = :customerId";
                queryAdmin = session.createQuery(hqlAdmin).setParameter("matchUniqueId", matchUniqueId)
                        .setParameter("customerTeamName", customerTeamName).setParameter("moreName", moreName)
                        .setParameter("customerId", userId);
                if (Integer.parseInt(queryAdmin.list().get(0).toString()) > 0) {
                    return "TEAM_NAME_ALREADY_EXIST";
                }
            }

            String hqln1 = "SELECT " + "name as name " + "from TblSoccerCustomerTeam as tcct " + "where "
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

            TblSoccerCustomerTeam tblSoccerCustomerTeam = new TblSoccerCustomerTeam();
            tblSoccerCustomerTeam.setCreated(time);
            tblSoccerCustomerTeam.setCustomerId(userId);
            tblSoccerCustomerTeam.setCustomerTeamName(customerTeamName);
            tblSoccerCustomerTeam.setMatchUniqueId(matchUniqueId);
            tblSoccerCustomerTeam.setMoreName(moreName);
            tblSoccerCustomerTeam.setName(new_team_name);
            tblSoccerCustomerTeam.setPlayerUniqueIds(playerIds);
            tblSoccerCustomerTeam.setPlayerMultiplers(playerMultipliers);
            tblSoccerCustomerTeam.setPlayerUniqueIdsMultiplers(playerIdsMultipliers);
            tblSoccerCustomerTeam.setUpdated(time);

            tx = session.beginTransaction();

            int newCustomerTeamId = (int) session.save(tblSoccerCustomerTeam);

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

                String mysqlQuery = "INSERT " + "INTO tbl_soccer_customer_team_plyers "
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
                String updateNormalPlayerHQL = "UPDATE " + "TblSoccerMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:oldNormalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblSoccerMatchPlayer "
                        + "SET selectedByCount = selectedByCount-1, selectedAsVccaptionCount = selectedAsVccaptionCount-1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblSoccerMatchPlayer "
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
                matchString = "UPDATE " + "TblSoccerMatch " + "SET totalCustomerTeam = totalCustomerTeam+1 " + "WHERE "
                        + "uniqueId =:matchUniqueId";
            }

            if (playingSquadUpdated.equals("N")) {
                tx = session.beginTransaction();
                String updateNormalPlayerHQL = "UPDATE " + "TblSoccerMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1 " + "WHERE "
                        + "matchUniqueId = :matchUniqueId AND playerUniqueId IN(:normalPlayers)";

                String updateViceCaptainHQL = "UPDATE " + "TblSoccerMatchPlayer "
                        + "SET selectedByCount = selectedByCount+1, selectedAsVccaptionCount = selectedAsVccaptionCount+1 "
                        + "WHERE " + "matchUniqueId = :matchUniqueId AND playerUniqueId = :viceCaptionPlayer";

                String updateCaptainPlayerHQL = "UPDATE " + "TblSoccerMatchPlayer "
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
                    + "FROM  TblSoccerMatch tcm " + "WHERE "
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

            String hqlQuery = "SELECT tcct FROM TblSoccerCustomerTeam tcct WHERE tcct.id =:id";
            Query queryObj = session.createQuery(hqlQuery);
            queryObj.setParameter("id", customerTeamId);
            TblSoccerCustomerTeam customerTeam = (TblSoccerCustomerTeam) queryObj.getSingleResult();

            if (customerTeam.getMatchUniqueId() != matchUniqueId) {
                return "INVALID_MATCH";
            }

//            Criteria createCriteria = session.createCriteria(TblSoccerCustomerTeam.class);
//            createCriteria.add(Restrictions.eq("id", customerTeamId));
//            TblSoccerCustomerTeam customerTeam = (TblSoccerCustomerTeam) createCriteria.uniqueResult();

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
                        String updatePlayers = " UPDATE TblSoccerCustomerTeamPlyer SET multiplier = :multiplier WHERE playerUniqueId= :playerUniqueId AND customerTeamId =:customerTeamId";
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

                    String updatePlayers = " UPDATE " + "TblSoccerCustomerTeamPlyer "
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
            String updateTeam = " UPDATE " + "TblSoccerCustomerTeam "
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
        String hqln = "SELECT " + "COUNT(id) as id  " + "FROM  TblSoccerCustomerTeam  " + "WHERE "
                + "customerId = :customerId AND matchUniqueId = :matchUniqueId";
        Query queryy = getSession().createQuery(hqln).setParameter("customerId", UserId).setParameter("matchUniqueId",
                matchUniqueId);
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return (long) result.get(0).get("id");

    }

    @Transactional(readOnly = true)
    public Object getCustomerMatchTeam(int UserId, int matchUniqueId, MatchesService matchesService) {
        TblSoccerMatch soccerMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);

        if (soccerMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(soccerMatch);

        String hql = "SELECT " + "tcct.id AS team_id,tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position,tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblSoccerCustomerTeamPlyer tcctp "
                + "LEFT JOIN  TblSoccerMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblSoccerPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblSoccerSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId) "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerId = :UserId AND  tcctp.matchUniqueId = :matchUniqueId ORDER BY tcctp.customerTeamId ASC ";

        Query queryy = getSession().createQuery(hql).setParameter("UserId", UserId)
                .setParameter("seriesId", soccerMatch.getSeriesId())
                .setParameter("gameTypeId", soccerMatch.getGameTypeId()).setParameter("matchUniqueId", matchUniqueId);

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
            player.put("playing_squad_updated", soccerMatch.getPlayingSquadUpdated());
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

                if (position.equalsIgnoreCase("goalkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("defender")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("midfielder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("forward")) {
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

                if (player_teamId == soccerMatch.getTeam1Id()) {
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

        String getMatchUniqueId = "SELECT " + "tcct.matchUniqueId as matchUniqueId, tcct.customerId as customerId " + "FROM TblSoccerCustomerTeam tcct " + "WHERE " + "id = :customerTeamId";
        Query queryMatchUniqueId = getSession().createQuery(getMatchUniqueId).setParameter("customerTeamId",
                customerTeamId);

        HashMap<String, Object> matchResult = (HashMap<String, Object>) queryMatchUniqueId
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int matchUniqueId = Integer.parseInt(matchResult.get("matchUniqueId").toString());
        int customerId = Integer.parseInt(matchResult.get("customerId").toString());

        TblSoccerMatch soccerMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueId);
        if (soccerMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(soccerMatch);

        String hql = "SELECT " + "tcct.id AS team_id, tcct.name AS team_name, "
                + "tcctp.playerUniqueId AS playerUniqueId, tcctp.multiplier AS multiplier, tcctp.position AS position, tcctp.teamId AS player_teamId, "
                + "tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad,tcmp.selectedByCount as selectedByCount,tcmp.selectedAsCaptionCount as selectedAsCaptionCount,tcmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tcmp.selectedAsTrumpCount as selectedAsTrumpCount, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, " + "tcsp.points AS series_total_points "
                + "FROM TblSoccerCustomerTeamPlyer  tcctp "
                + "LEFT JOIN  TblSoccerMatchPlayer tcmp ON (tcmp.playerUniqueId = tcctp.playerUniqueId AND tcmp.matchUniqueId=tcctp.matchUniqueId) "
                + "LEFT JOIN  TblSoccerPlayer tcp ON tcp.uniqueId = tcctp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblSoccerSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcctp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "LEFT JOIN TblSoccerCustomerTeam tcct ON tcct.id = tcctp.customerTeamId  " + "WHERE "
                + "tcctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("seriesId", soccerMatch.getSeriesId())
                .setParameter("gameTypeId", soccerMatch.getGameTypeId())
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
            if (soccerMatch.getTotalCustomerTeam() > 0) {
                selectedByPer = (((float) selected_by) / soccerMatch.getTotalCustomerTeam()) * 100f;
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
            player.put("playing_squad_updated", soccerMatch.getPlayingSquadUpdated());
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

                if (position.equalsIgnoreCase("goalkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("defender")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("midfielder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("forward")) {
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

                if (player_teamId == soccerMatch.getTeam1Id()) {
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

        TblSoccerMatch soccerMatch = matchesService.getMatchesRepository().getMatchData(matchUniqueid);
        if (soccerMatch == null) {
            return "UNABLE_TO_PROCEED";
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(soccerMatch);

        String hql = "SELECT " + "0 AS team_id, 'dream team' AS team_name, "
                + "tcmp.playerUniqueId AS playerUniqueId, tcmp.dreamTeamPlayer AS dreamTeamPlayer, tcmp.teamId AS player_teamId, tcmp.points AS points, tcmp.credits AS credits, tcmp.isInPlayingSquad AS isInPlayingSquad, tcmp.playingRole AS playingRole, tcmp.image AS image, tcmp.isInSubstituteSquad AS isInSubstituteSquad, "
                + "tcp.name AS name, tcp.bets AS bets, tcp.bowls AS bowls, tcp.dob AS dob, "
                + "tc.id as country_id, tc.name as country_name, "
                + "tcsp.points AS series_total_points "
                + "FROM TblSoccerMatchPlayer tcmp "
                + "LEFT JOIN  TblSoccerPlayer tcp ON tcp.uniqueId = tcmp.playerUniqueId "
                + "LEFT JOIN  TblCountry tc ON tcp.countryId = tc.id "
                + "LEFT JOIN TblSoccerSeriesPlayers tcsp ON (tcsp.playerUniqueId = tcmp.playerUniqueId AND tcsp.seriesId = :seriesId AND tcsp.gameTypeId = :gameTypeId ) "
                + "WHERE " + "tcmp.dreamTeamPlayer = 'Y' AND tcmp.matchUniqueId=:matchUniqueId "
                + "ORDER BY tcmp.points DESC";

        Query queryy = getSession().createQuery(hql).setParameter("matchUniqueId", matchUniqueid)
                .setParameter("seriesId", soccerMatch.getSeriesId())
                .setParameter("gameTypeId", soccerMatch.getGameTypeId());

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        HashMap<String, Object> customerTeams = null;
        int playerPosition = 0;
        Float[] multiplier_array = GlobalConstant.MULTIPLIER_ARRAY_SOCCER;
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
            player.put("playing_squad_updated", soccerMatch.getPlayingSquadUpdated());
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

                if (position.equalsIgnoreCase("goalkeeper")) {
                    wicketkeapers.add(player);
                } else if (position.equalsIgnoreCase("defender")) {
                    batsmans.add(player);
                } else if (position.equalsIgnoreCase("midfielder")) {
                    allrounders.add(player);
                } else if (position.equalsIgnoreCase("forward")) {
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

                if (player_teamId == soccerMatch.getTeam1Id()) {
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
        String hqln = "SELECT " + "id as id, name as name " + "FROM TblSoccerCustomerTeam " + "WHERE "
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
                "tsmps.goal AS Goal," +
                "tsmps.goalValue AS Goal_Value," +
                "tsmps.assist AS Assist," +
                "tsmps.assistValue AS Assist_Value," +
                "tsmps.shotOnTarget AS Shot_On_Target," +
                "tsmps.shotOnTargetValue AS Shot_On_Target_Value," +
                "tsmps.chanceCreated AS Chance_Created," +
                "tsmps.chanceCreatedValue AS Chance_Created_Value," +
                "tsmps.fivePassesCompleted AS Five_Passes_Completed," +
                "tsmps.fivePasses_Completed_Value AS Five_Passes_Completed_Value," +
                "tsmps.tackleWon AS Tackle_Won," +
                "tsmps.tackleWonValue AS Tackle_Won_Value," +
                "tsmps.interceptionWon AS Interception_Won," +
                "tsmps.interceptionWonValue AS Interception_Won_Value," +
                "tsmps.blockedShot AS Blocked_Shot," +
                "tsmps.blockedShotValue AS Blocked_Shot_Value," +
                "tsmps.clearance AS Clearance," +
                "tsmps.clearanceValue AS Clearance_Value," +
                "tsmps.saves AS Saves," +
                "tsmps.savesValue AS Saves_Value," +
                "tsmps.penaltySaved AS Penalty_Saved," +
                "tsmps.penaltySavedValue AS Penalty_Saved_Value," +
                "tsmps.cleanSheet AS Clean_Sheet," +
                "tsmps.cleanSheetValue AS Clean_Sheet_Value," +
                "tsmps.inStarting11 AS In_Starting_11," +
                "tsmps.inStarting11Value AS In_Starting_11_Value," +
                "tsmps.comingOnAsASubstitute AS Coming_on_as_a_Substitute," +
                "tsmps.comingOnAsASubstituteValue AS Coming_on_as_a_Substitute_Value," +
                "tsmps.yellowCard AS Yellow_Card," +
                "tsmps.yellowCardValue AS Yellow_Card_Value," +
                "tsmps.redCard AS Red_Card," +
                "tsmps.redCardValue AS Red_Card_Value," +
                "tsmps.ownGoal AS Own_goal," +
                "tsmps.ownGoalValue AS Own_goal_Value," +
                "tsmps.goalsConceded AS Goals_Conceded," +
                "tsmps.goalsConcededValue AS Goals_Conceded_Value," +
                "tsmps.penaltyMissed AS Penalty_Missed," +
                "tsmps.penaltyMissedValue AS Penalty_Missed_Value, "
                + "tsct.id AS team_id,tsct.name AS team_name, "
                + "tsctp.playerUniqueId AS playerUniqueId, tsctp.matchUniqueId as matchUniqueId, "
                + "tsmp.points AS points,tsmp.credits AS credits,tsmp.isInPlayingSquad AS isInPlayingSquad,tsmp.playingRole AS playingRole,tsmp.image AS image,tsmp.isInSubstituteSquad AS isInSubstituteSquad,tsmp.selectedByCount as selectedByCount,tsmp.selectedAsCaptionCount as selectedAsCaptionCount,tsmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tsmp.selectedAsTrumpCount as selectedAsTrumpCount,tsmp.dreamTeamPlayer, "
                + "tsp.name AS name " + "FROM TblSoccerCustomerTeamPlyer tsctp "
                + "LEFT JOIN  TblSoccerMatchPlayer tsmp ON (tsmp.playerUniqueId = tsctp.playerUniqueId AND tsmp.matchUniqueId=tsctp.matchUniqueId) "
                + "LEFT JOIN  TblSoccerPlayer tsp ON tsp.uniqueId = tsctp.playerUniqueId "
                + "LEFT JOIN TblSoccerCustomerTeam tsct ON tsct.id = tsctp.customerTeamId "
                + "LEFT JOIN TblSoccerMatchPlayersStat tsmps ON (tsmps.playerUniqueId=tsctp.playerUniqueId AND tsmps.matchUniqueId = tsctp.matchUniqueId) "
                + "LEFT JOIN TblSoccerTeam tct ON tct.id=tsctp.teamId "
                + "WHERE tsctp.customerTeamId = :customerTeamId ";

        Query queryy = getSession().createQuery(hql).setParameter("customerTeamId", customerTeamId);

        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) queryy
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        int totalCustomerTeam = 0;
        if (result.size() > 0) {

            HashMap<String, Object> hashMap = result.get(0);
            int matchUniqueId = (int) hashMap.get("matchUniqueId");

            String hqlMtch = "SELECT tsm.totalCustomerTeam as totalCustomerTeam FROM TblSoccerMatch tsm where tsm.uniqueId=:uniqueId";

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
            event.put("key", "Goal");
            event.put("points", hashMap.get("Goal"));
            event.put("value", hashMap.get("Goal_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Assist");
            event.put("points", hashMap.get("Assist"));
            event.put("value", hashMap.get("Assist_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Shot On Target");
            event.put("points", hashMap.get("Shot_On_Target"));
            event.put("value", hashMap.get("Shot_On_Target_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Chance Created");
            event.put("points", hashMap.get("Chance_Created"));
            event.put("value", hashMap.get("Chance_Created_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "5 Passes Completed");
            event.put("points", hashMap.get("Five_Passes_Completed"));
            event.put("value", hashMap.get("Five_Passes_Completed_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Tackle Won");
            event.put("points", hashMap.get("Tackle_Won"));
            event.put("value", hashMap.get("Tackle_Won_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Interception Won");
            event.put("points", hashMap.get("Interception_Won"));
            event.put("value", hashMap.get("Interception_Won_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Blocked Shot");
            event.put("points", hashMap.get("Blocked_Shot"));
            event.put("value", hashMap.get("Blocked_Shot_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Clearance");
            event.put("points", hashMap.get("Clearance"));
            event.put("value", hashMap.get("Clearance_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Saves");
            event.put("points", hashMap.get("Saves"));
            event.put("value", hashMap.get("Saves_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Penalty Saved");
            event.put("points", hashMap.get("Penalty_Saved"));
            event.put("value", hashMap.get("Penalty_Saved_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Clean Sheet");
            event.put("points", hashMap.get("Clean_Sheet"));
            event.put("value", hashMap.get("Clean_Sheet_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "In Starting 11");
            event.put("points", hashMap.get("In_Starting_11"));
            event.put("value", hashMap.get("In_Starting_11_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Coming on as a Substitute");
            event.put("points", hashMap.get("Coming_on_as_a_Substitute"));
            event.put("value", hashMap.get("Coming_on_as_a_Substitute_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Yellow Card");
            event.put("points", hashMap.get("Yellow_Card"));
            event.put("value", hashMap.get("Yellow_Card_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Red Card");
            event.put("points", hashMap.get("Red_Card"));
            event.put("value", hashMap.get("Red_Card_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Own goal");
            event.put("points", hashMap.get("Own_goal"));
            event.put("value", hashMap.get("Own_goal_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Goals Conceded");
            event.put("points", hashMap.get("Goals_Conceded"));
            event.put("value", hashMap.get("Goals_Conceded_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Penalty Missed");
            event.put("points", hashMap.get("Penalty_Missed"));
            event.put("value", hashMap.get("Penalty_Missed_Value"));

            playerEvents.add(event);

            player.put("player_events", playerEvents);

            players.add(player);
        }

        return customerTeams;

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getMatchDreamTeamStats(Integer authUserId, int matchUniqueId) {

        String hqlMtch = "SELECT tsm.totalCustomerTeam as totalCustomerTeam FROM TblSoccerMatch tsm where tsm.uniqueId=:uniqueId";

        Query queryyM = getSession().createQuery(hqlMtch).setParameter("uniqueId", matchUniqueId);

        HashMap<String, Object> resultM = (HashMap<String, Object>) queryyM
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        int totalCustomerTeam = (int) resultM.get("totalCustomerTeam");

        String hql = "SELECT "
                + "tst.fullName as fullName, tst.sortName as sortName, tst.id as playerTeamId, tst.logo as logo, " +
                "tsmps.goal AS Goal," +
                "tsmps.goalValue AS Goal_Value," +
                "tsmps.assist AS Assist," +
                "tsmps.assistValue AS Assist_Value," +
                "tsmps.shotOnTarget AS Shot_On_Target," +
                "tsmps.shotOnTargetValue AS Shot_On_Target_Value," +
                "tsmps.chanceCreated AS Chance_Created," +
                "tsmps.chanceCreatedValue AS Chance_Created_Value," +
                "tsmps.fivePassesCompleted AS Five_Passes_Completed," +
                "tsmps.fivePasses_Completed_Value AS Five_Passes_Completed_Value," +
                "tsmps.tackleWon AS Tackle_Won," +
                "tsmps.tackleWonValue AS Tackle_Won_Value," +
                "tsmps.interceptionWon AS Interception_Won," +
                "tsmps.interceptionWonValue AS Interception_Won_Value," +
                "tsmps.blockedShot AS Blocked_Shot," +
                "tsmps.blockedShotValue AS Blocked_Shot_Value," +
                "tsmps.clearance AS Clearance," +
                "tsmps.clearanceValue AS Clearance_Value," +
                "tsmps.saves AS Saves," +
                "tsmps.savesValue AS Saves_Value," +
                "tsmps.penaltySaved AS Penalty_Saved," +
                "tsmps.penaltySavedValue AS Penalty_Saved_Value," +
                "tsmps.cleanSheet AS Clean_Sheet," +
                "tsmps.cleanSheetValue AS Clean_Sheet_Value," +
                "tsmps.inStarting11 AS In_Starting_11," +
                "tsmps.inStarting11Value AS In_Starting_11_Value," +
                "tsmps.comingOnAsASubstitute AS Coming_on_as_a_Substitute," +
                "tsmps.comingOnAsASubstituteValue AS Coming_on_as_a_Substitute_Value," +
                "tsmps.yellowCard AS Yellow_Card," +
                "tsmps.yellowCardValue AS Yellow_Card_Value," +
                "tsmps.redCard AS Red_Card," +
                "tsmps.redCardValue AS Red_Card_Value," +
                "tsmps.ownGoal AS Own_goal," +
                "tsmps.ownGoalValue AS Own_goal_Value," +
                "tsmps.goalsConceded AS Goals_Conceded," +
                "tsmps.goalsConcededValue AS Goals_Conceded_Value," +
                "tsmps.penaltyMissed AS Penalty_Missed," +
                "tsmps.penaltyMissedValue AS Penalty_Missed_Value, "
                + "0 AS team_id, 'dream team' AS team_name, " + "tsmp.playerUniqueId AS playerUniqueId, "
                + "tsmp.points AS points,tsmp.credits AS credits,tsmp.isInPlayingSquad AS isInPlayingSquad,tsmp.playingRole AS playingRole,tsmp.image AS image,tsmp.isInSubstituteSquad AS isInSubstituteSquad,tsmp.selectedByCount as selectedByCount,tsmp.selectedAsCaptionCount as selectedAsCaptionCount,tsmp.selectedAsVccaptionCount as selectedAsVccaptionCount,tsmp.selectedAsTrumpCount as selectedAsTrumpCount,tsmp.dreamTeamPlayer, "
                + "tsp.name AS name " + "FROM TblSoccerMatchPlayer tsmp "
                + "LEFT JOIN  TblSoccerPlayer tsp ON tsp.uniqueId = tsmp.playerUniqueId "
                + "LEFT JOIN TblSoccerMatchPlayersStat tsmps ON (tsmps.playerUniqueId=tsmp.playerUniqueId AND tsmps.matchUniqueId = tsmp.matchUniqueId) "
                + "LEFT JOIN TblSoccerTeam tst ON tst.id=tsmp.teamId "
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
            event.put("key", "Goal");
            event.put("points", hashMap.get("Goal"));
            event.put("value", hashMap.get("Goal_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Assist");
            event.put("points", hashMap.get("Assist"));
            event.put("value", hashMap.get("Assist_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Shot On Target");
            event.put("points", hashMap.get("Shot_On_Target"));
            event.put("value", hashMap.get("Shot_On_Target_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Chance Created");
            event.put("points", hashMap.get("Chance_Created"));
            event.put("value", hashMap.get("Chance_Created_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "5 Passes Completed");
            event.put("points", hashMap.get("Five_Passes_Completed"));
            event.put("value", hashMap.get("Five_Passes_Completed_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Tackle Won");
            event.put("points", hashMap.get("Tackle_Won"));
            event.put("value", hashMap.get("Tackle_Won_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Interception Won");
            event.put("points", hashMap.get("Interception_Won"));
            event.put("value", hashMap.get("Interception_Won_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Blocked Shot");
            event.put("points", hashMap.get("Blocked_Shot"));
            event.put("value", hashMap.get("Blocked_Shot_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Clearance");
            event.put("points", hashMap.get("Clearance"));
            event.put("value", hashMap.get("Clearance_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Saves");
            event.put("points", hashMap.get("Saves"));
            event.put("value", hashMap.get("Saves_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Penalty Saved");
            event.put("points", hashMap.get("Penalty_Saved"));
            event.put("value", hashMap.get("Penalty_Saved_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Clean Sheet");
            event.put("points", hashMap.get("Clean_Sheet"));
            event.put("value", hashMap.get("Clean_Sheet_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "In Starting 11");
            event.put("points", hashMap.get("In_Starting_11"));
            event.put("value", hashMap.get("In_Starting_11_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Coming on as a Substitute");
            event.put("points", hashMap.get("Coming_on_as_a_Substitute"));
            event.put("value", hashMap.get("Coming_on_as_a_Substitute_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Yellow Card");
            event.put("points", hashMap.get("Yellow_Card"));
            event.put("value", hashMap.get("Yellow_Card_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Red Card");
            event.put("points", hashMap.get("Red_Card"));
            event.put("value", hashMap.get("Red_Card_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Own goal");
            event.put("points", hashMap.get("Own_goal"));
            event.put("value", hashMap.get("Own_goal_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Goals Conceded");
            event.put("points", hashMap.get("Goals_Conceded"));
            event.put("value", hashMap.get("Goals_Conceded_Value"));

            playerEvents.add(event);

            event = new HashMap<String, Object>();
            event.put("key", "Penalty Missed");
            event.put("points", hashMap.get("Penalty_Missed"));
            event.put("value", hashMap.get("Penalty_Missed_Value"));

            playerEvents.add(event);

            player.put("player_events", playerEvents);

            players.add(player);
        }

        return customerTeams;
    }

    @Transactional
    public void updateCustomerMatchTeamInfo(int matchUniqueId, Integer customerId) {
        String hqln = "SELECT COUNT(tcct.id) AS cnt FROM TblSoccerCustomerTeam tcct WHERE tcct.matchUniqueId =:matchUniqueId AND tcct.customerId =:customerId";
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
            String mysqlQuery1 = "INSERT " + "INTO tbl_soccer_customer_match_info "
                    + "SET match_unique_id =" + matchUniqueId + ", "
                    + "customer_id =" + customerId + ", "
                    + "team_count =" + count + " "
                    + "ON DUPLICATE KEY UPDATE team_count =" + count;
            Query queryyy = getSession().createSQLQuery(mysqlQuery1);
            queryyy.executeUpdate();
        }
    }

}
