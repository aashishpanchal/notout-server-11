package com.choic11.service.basketball;

import com.choic11.GlobalConstant.EntitySportsConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.choic11.model.BaseRequest;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service("BasketballEntitySportsService")
public class EntitySportsService {

    public Object getUpcomingMatchesSeries(BaseRequest baseRequest) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_MATCHES;
        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> upcomingSeries = new LinkedHashMap<String, Object>();

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items")) {
                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object matchData : jsonArray) {
                                JSONObject match = (JSONObject) matchData;

                                JSONObject competition = match.getJSONObject("competition");
                                if (Util.isEmpty(competition.get("cid").toString())
                                        || upcomingSeries.containsKey(competition.get("cid").toString())) {
                                    continue;
                                }

                                LinkedHashMap<String, Object> series = new LinkedHashMap<String, Object>();
                                series.put("unique_id", competition.get("cid"));
                                series.put("title", competition.get("cname"));
                                series.put("abbr", "");
                                series.put("type", "");
                                series.put("season", competition.get("year"));

                                upcomingSeries.put(series.get("unique_id").toString(), series);
                            }

                        } else {
                            return jsonObject;
                        }

                    } else {
                        return jsonObject;
                    }

                } else {
                    return jsonObject;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return upcomingSeries;
    }

    public LinkedHashMap<String, Object> getUpcomingMatchesBySeries(BaseRequest baseRequest, String seriesId) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_MATCHES;
        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> upcomingMatches = new LinkedHashMap<String, Object>();

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items")) {
                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object matchData : jsonArray) {
                                JSONObject match = (JSONObject) matchData;
                                boolean presquad= Boolean.parseBoolean(match.getString("presquad"));

                                LinkedHashMap<String, Object> series = null;
                                JSONObject competition = match.getJSONObject("competition");
                                if (!competition.isEmpty()) {
                                    series = new LinkedHashMap<String, Object>();
                                    series.put("unique_id", competition.get("cid"));
                                    series.put("title", competition.get("cname"));
                                    series.put("abbr", "");
                                    series.put("type", "");
                                    series.put("season", competition.get("year"));
                                }

                                if (series == null || (!seriesId.equals("0")
                                        && !seriesId.equals(series.get("unique_id").toString()))) {
                                    continue;
                                }

                                String teamA = match.getJSONObject("teams").getJSONObject("home").getString("tname");
                                String teamAAbbr = match.getJSONObject("teams").getJSONObject("home").getString("abbr");
                                String teamB = match.getJSONObject("teams").getJSONObject("away").getString("tname");
                                String teamBAbbr = match.getJSONObject("teams").getJSONObject("away").getString("abbr");

                                LinkedHashMap<String, Object> matchR = new LinkedHashMap<String, Object>();
                                matchR.put("timestamp_start", match.get("timestampstart"));
                                matchR.put("date", match.get("datestart"));
                                matchR.put("dateTimeGMT", match.get("datestart") + ".000Z");
                                matchR.put("matchStarted", false);
                                matchR.put("squad", true);
                                matchR.put("presquad", presquad);
                                matchR.put("team-1", teamA);
                                matchR.put("team-1-short_name", teamAAbbr);
                                matchR.put("team-2", teamB);
                                matchR.put("team-2-short_name", teamBAbbr);
                                matchR.put("type", "BASKETBALL");
                                matchR.put("unique_id", match.get("mid"));
                                matchR.put("title", teamA + " vs " + teamB);
                                matchR.put("short_title", teamAAbbr + " vs " + teamBAbbr);
                                matchR.put("subtitle", "");
                                matchR.put("series_data", series);

                                upcomingMatches.put(matchR.get("unique_id").toString(), matchR);
                            }

                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return upcomingMatches;
    }

    public List<LinkedHashMap<String, Object>> getMatchSquad(BaseRequest baseRequest, String seriesUniqueId,
                                                             String matchUniqueId) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_MATCH_SQUAD_ROSTER;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items") && response.get("items") instanceof JSONObject) {
                            JSONObject items = response.getJSONObject("items");

                            List<LinkedHashMap<String, Object>> teama_palyers = new ArrayList<LinkedHashMap<String, Object>>();
                            List<LinkedHashMap<String, Object>> teamb_palyers = new ArrayList<LinkedHashMap<String, Object>>();

                            String teama_name = items.getJSONObject("match_info").getJSONObject("teams").getJSONObject("home").getString("tname");
                            String teamb_name = items.getJSONObject("match_info").getJSONObject("teams").getJSONObject("away").getString("tname");

                            if (items.has("fantasy_squad") && items.getJSONObject("fantasy_squad").has("home")) {
                                JSONArray teamPlayersData = items.getJSONObject("fantasy_squad").getJSONArray("home");
                                for (Object playerData : teamPlayersData) {
                                    JSONObject player = (JSONObject) playerData;

                                    LinkedHashMap<String, Object> singlePlayerData = new LinkedHashMap<String, Object>();

                                    singlePlayerData.put("pid", Integer.parseInt(player.get("pid").toString()));
                                    singlePlayerData.put("name", player.get("name"));
                                    singlePlayerData.put("short_name", player.get("name"));
                                    singlePlayerData.put("battingStyle", " ");
                                    singlePlayerData.put("bowlingStyle", " ");
                                    singlePlayerData.put("credits", player.get("rating"));
                                    singlePlayerData.put("playingRole", player.get("rolename"));
                                    singlePlayerData.put("born", "");
                                    if (player.has("nationality")) {
                                        singlePlayerData.put("country", player.getJSONObject("nationality").get("name"));
                                    } else {
                                        singlePlayerData.put("country", "");
                                    }

                                    LinkedHashMap<String, Object> realPlayerData = new LinkedHashMap<String, Object>();
                                    realPlayerData.put("pid", singlePlayerData.get("pid"));
                                    realPlayerData.put("name", singlePlayerData.get("name"));
                                    realPlayerData.put("credits", singlePlayerData.get("credits"));
                                    realPlayerData.put("playingRole", singlePlayerData.get("playingRole"));
                                    realPlayerData.put("detail_data", singlePlayerData);

                                    teama_palyers.add(realPlayerData);
                                }
                            }

                            if (items.has("fantasy_squad") && items.getJSONObject("fantasy_squad").has("away")) {
                                JSONArray teamPlayersData = items.getJSONObject("fantasy_squad").getJSONArray("away");
                                for (Object playerData : teamPlayersData) {
                                    JSONObject player = (JSONObject) playerData;

                                    LinkedHashMap<String, Object> singlePlayerData = new LinkedHashMap<String, Object>();

                                    singlePlayerData.put("pid", Integer.parseInt(player.get("pid").toString()));
                                    singlePlayerData.put("name", player.get("name"));
                                    singlePlayerData.put("short_name", player.get("name"));
                                    singlePlayerData.put("battingStyle", " ");
                                    singlePlayerData.put("bowlingStyle", " ");
                                    singlePlayerData.put("credits", player.get("rating"));
                                    singlePlayerData.put("playingRole", player.get("rolename"));
                                    singlePlayerData.put("born", "");
                                    if (player.has("nationality")) {
                                        singlePlayerData.put("country", player.getJSONObject("nationality").get("name"));
                                    } else {
                                        singlePlayerData.put("country", "");
                                    }

                                    LinkedHashMap<String, Object> realPlayerData = new LinkedHashMap<String, Object>();
                                    realPlayerData.put("pid", singlePlayerData.get("pid"));
                                    realPlayerData.put("name", singlePlayerData.get("name"));
                                    realPlayerData.put("credits", singlePlayerData.get("credits"));
                                    realPlayerData.put("playingRole", singlePlayerData.get("playingRole"));
                                    realPlayerData.put("detail_data", singlePlayerData);

                                    teamb_palyers.add(realPlayerData);
                                }
                            }

                            Comparator<LinkedHashMap<String, Object>> comparator = new Comparator<>() {
                                private static final String KEY_NAME = "pid";

                                @Override
                                public int compare(LinkedHashMap<String, Object> a, LinkedHashMap<String, Object> b) {

                                    int valA = (int) a.get(KEY_NAME);
                                    int valB = (int) b.get(KEY_NAME);

                                    if (valA == valB) {
                                        return 0;
                                    } else if (valA < valB) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            };

                            teama_palyers.sort(comparator);
                            teamb_palyers.sort(comparator);

                            LinkedHashMap<String, Object> teamA = new LinkedHashMap<String, Object>();
                            teamA.put("name", teama_name);
                            teamA.put("players", teama_palyers);

                            LinkedHashMap<String, Object> teamB = new LinkedHashMap<String, Object>();
                            teamB.put("name", teamb_name);
                            teamB.put("players", teamb_palyers);

                            List<LinkedHashMap<String, Object>> squadResponseData = new ArrayList<LinkedHashMap<String, Object>>();
                            squadResponseData.add(teamA);
                            squadResponseData.add(teamB);

                            return squadResponseData;

                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<LinkedHashMap<String, Object>> playerFinder(BaseRequest baseRequest, String playerName) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_PLAYER_FINDER;

        url = url.replace("{PLAYER_NAME}", playerName);

        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        List<LinkedHashMap<String, Object>> playersData = new ArrayList<LinkedHashMap<String, Object>>();

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();


                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items") && response.get("items") instanceof JSONArray) {

                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object itemsData : jsonArray) {
                                JSONObject item = (JSONObject) itemsData;

                                LinkedHashMap<String, Object> player = new LinkedHashMap<String, Object>();
                                player.put("pid", item.getInt("pid"));
                                player.put("fullName", item.optString("fullname", ""));
                                player.put("name", item.optString("fullname", ""));
                                player.put("battingStyle", item.optString("height", ""));
                                player.put("bowlingStyle", item.optString("weight", ""));
                                player.put("playingRole", item.optString("primarypositionname", ""));

                                String realBirthday = item.optString("birthdate", "0000-00-00");
                                if (realBirthday.equals("0000-00-00")) {
                                    realBirthday = "";
                                } else {
                                    realBirthday = realBirthday + ",";
                                }
                                player.put("born", realBirthday);
                                player.put("country", (item.has("nationality") && item.get("nationality") instanceof JSONObject) ?
                                        item.getJSONObject("nationality").optString("name", "") : "");

                                playersData.add(player);
                            }

                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return playersData;

    }

    public LinkedHashMap<String, Object> playerDetail(BaseRequest baseRequest, String playerId) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_PLAYER_DETAIL;

        url = url.replace("{PLAYER_ID}", playerId);

        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> playerData = null;

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items")
                                && response.get("items") instanceof JSONObject
                                && response.getJSONObject("items").has("player_info")
                                && response.getJSONObject("items").get("player_info") instanceof JSONObject) {

                            JSONObject item = response.getJSONObject("items").getJSONObject("player_info");

                            LinkedHashMap<String, Object> player = new LinkedHashMap<String, Object>();
                            player.put("pid", item.get("pid"));
                            player.put("fullName", item.optString("fullname", ""));
                            player.put("name", item.optString("fullname", ""));
                            player.put("battingStyle", item.get("height").toString());
                            player.put("bowlingStyle", item.get("weight").toString());
                            player.put("playingRole", item.get("primarypositionname").toString());

                            String realBirthday = item.getString("birthdate");
                            if (realBirthday.equals("0000-00-00")) {
                                realBirthday = "";
                            } else {
                                realBirthday = realBirthday + ",";
                            }
                            player.put("born", realBirthday);
                            player.put("country", (item.has("nationality") && item.get("nationality") instanceof JSONObject) ?
                                    item.getJSONObject("nationality").optString("name", "") : "");

                            playerData = player;

                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return playerData;

    }

    public LinkedHashMap<String, Object> getMatchLineup(BaseRequest baseRequest, String matchUniqueId,
                                                        JSONObject playersForMatchUniqueId) {
        String url = EntitySportsConstant.ENTITY_BASKETBALL_MATCH_LINEUP;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        List<Integer> players = new ArrayList<Integer>();
        List<Integer> substitute = new ArrayList<Integer>();

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items") && response.get("items") instanceof JSONObject) {
                            JSONObject items = response.getJSONObject("items");
                            if (items.has("lineup")
                                    && items.get("lineup") instanceof JSONObject
                                    && items.getJSONObject("lineup").has("players")
                                    && items.getJSONObject("lineup").get("players") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("players").has("home")
                                    && items.getJSONObject("lineup").getJSONObject("players").get("home") instanceof JSONArray) {
                                JSONArray jsonArray = items.getJSONObject("lineup").getJSONObject("players").getJSONArray("home");
                                for (Object itemSquadData : jsonArray) {
                                    JSONObject squadData = (JSONObject) itemSquadData;
                                    players.add(Integer.parseInt(squadData.get("pid").toString()));
                                }
                            }

                            if (items.has("lineup")
                                    && items.get("lineup") instanceof JSONObject
                                    && items.getJSONObject("lineup").has("players")
                                    && items.getJSONObject("lineup").get("players") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("players").has("away")
                                    && items.getJSONObject("lineup").getJSONObject("players").get("away") instanceof JSONArray) {
                                JSONArray jsonArray = items.getJSONObject("lineup").getJSONObject("players").getJSONArray("away");
                                for (Object itemSquadData : jsonArray) {
                                    JSONObject squadData = (JSONObject) itemSquadData;
                                    players.add(Integer.parseInt(squadData.get("pid").toString()));
                                }
                            }

                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!players.isEmpty()) {
            players.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer valA, Integer valB) {
                    if (Objects.equals(valA, valB)) {
                        return 0;
                    } else if (valA < valB) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("players", players);
        output.put("substitute", substitute);

        return output;
    }

    public JSONObject getMatchScoreCard(BaseRequest baseRequest, String matchUniqueId,
                                        LinkedHashMap<String, Object> match) {

        return getMatchFantasySummary(matchUniqueId, null, null);
    }

    public JSONObject getMatchFantasySummary(String matchUniqueId, JSONObject game_type_point,
                                             JSONObject match_players_array) {

        JSONObject output = new JSONObject();

        JSONObject scorecard_data = null;
        JSONObject api_response = null;
        JSONObject man_of_the_match = null;
        JSONObject players1 = null;

        output.put("players", players1);
        output.put("scorecard_data", scorecard_data);
        output.put("api_response", api_response);
        output.put("man_of_the_match", man_of_the_match);

        String url = EntitySportsConstant.ENTITY_BASKETBALL_MATCH_SCOREBOARD;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();


                JSONObject jsonObject = new JSONObject(responseData);

                output.put("api_response", jsonObject);
                if (jsonObject.getString("status").equals("ok")) {
                    if (jsonObject.has("response")
                            && jsonObject.get("response") instanceof JSONObject
                            && jsonObject.getJSONObject("response").has("items")
                            && jsonObject.getJSONObject("response").get("items") instanceof JSONObject) {

                        JSONObject api_data_array = jsonObject.getJSONObject("response").getJSONObject("items");

                        int home_team_id = 0;
                        String home_team_name = "";
                        int away_team_id = 0;
                        String away_team_name = "";
                        JSONObject matchInfo = null;
                        if (api_data_array.has("match_info") && api_data_array.get("match_info") instanceof JSONObject) {
                            matchInfo = api_data_array.getJSONObject("match_info");
                            home_team_id = Integer.parseInt(matchInfo.getJSONObject("teams").getJSONObject("home").get("tid").toString());
                            home_team_name = matchInfo.getJSONObject("teams").getJSONObject("home").get("tname").toString();

                            away_team_id = Integer.parseInt(matchInfo.getJSONObject("teams").getJSONObject("away").get("tid").toString());
                            away_team_name = matchInfo.getJSONObject("teams").getJSONObject("away").get("tname").toString();
                        }

                        if (home_team_id == 0 || away_team_id == 0) {
                            return output;
                        }


                        String team1_run = "0";
                        String team1_wicket = "0";
                        String team1_overs = "0";

                        String team2_run = "0";
                        String team2_wicket = "0";
                        String team2_overs = "0";

                        String score_board_notes = "";

                        if (matchInfo.has("result")) {
                            if(matchInfo.getJSONObject("result").get("home")!=null){
                                team1_run = matchInfo.getJSONObject("result").get("home").toString();
                                if (team1_run == null) {
                                    team1_run = "0";
                                }
                            }

                            if(matchInfo.getJSONObject("result").get("away")!=null) {
                                team2_run = matchInfo.getJSONObject("result").get("away").toString();
                                if (team2_run == null) {
                                    team2_run = "0";
                                }
                            }

                            String matchWinner = (String) matchInfo.getJSONObject("result").get("winner");
                            if (matchInfo.has("status") &&
                                    (matchInfo.get("status").toString().equals("2") || matchInfo.get("status").toString().equals("4"))) {
                                score_board_notes = matchWinner;
                            }
                        }

                        if (score_board_notes.isEmpty()) {
                            Object time = matchInfo.get("time");
                            if (time != null) {
                                score_board_notes = "Played " + time + " Minutes";
                            }
                        } else {
                            if (score_board_notes.equals("home")) {
                                score_board_notes = "Winner " + home_team_name;
                            } else if (score_board_notes.equals("away")) {
                                score_board_notes = "Winner " + away_team_name;
                            }
                        }

                        JSONObject scorecardData = new JSONObject();

                        scorecardData.put("team1_run", team1_run);
                        scorecardData.put("team1_wicket", team1_wicket);
                        scorecardData.put("team1_overs", team1_overs);

                        scorecardData.put("team2_run", team2_run);
                        scorecardData.put("team2_wicket", team2_wicket);
                        scorecardData.put("team2_overs", team2_overs);

                        scorecardData.put("score_board_notes", score_board_notes);

                        game_type_point = setupDefaultPointsIfNotAvailable(game_type_point);

                        JSONObject players = new JSONObject();

                        if (game_type_point != null && api_data_array.has("match_player_stats")
                                && api_data_array.get("match_player_stats") instanceof JSONObject
                                && api_data_array.getJSONObject("match_player_stats").has("home")
                                && api_data_array.getJSONObject("match_player_stats").get("home") instanceof JSONArray) {
                            for (Object iterable_element : api_data_array.getJSONObject("match_player_stats")
                                    .getJSONArray("home")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty() || pid
                                        .toString().trim().equals("0")) {
                                    continue;
                                }

                                if (playing_players.get("points") == null ||
                                        playing_players.get("points").toString().trim().isEmpty()) {
                                    playing_players.put("points", 0);
                                }

                                if (playing_players.get("rebound") == null ||
                                        playing_players.get("rebound").toString().trim().isEmpty()) {
                                    playing_players.put("rebound", 0);
                                }


                                if (playing_players.get("assists") == null ||
                                        playing_players.get("assists").toString().trim().isEmpty()) {
                                    playing_players.put("assists", 0);
                                }

                                if (playing_players.get("steals") == null ||
                                        playing_players.get("steals").toString().trim().isEmpty()) {
                                    playing_players.put("steals", 0);
                                }

                                if (playing_players.get("blocks") == null ||
                                        playing_players.get("blocks").toString().trim().isEmpty()) {
                                    playing_players.put("blocks", 0);
                                }

                                if (playing_players.get("turnovers") == null ||
                                        playing_players.get("turnovers").toString().trim().isEmpty()) {
                                    playing_players.put("turnovers", 0);
                                }

                                JSONObject playerData = new JSONObject();
                                playerData.put("Points_Scored", 0f);
                                playerData.put("Rebounds", 0f);
                                playerData.put("Assists", 0f);
                                playerData.put("Steals", 0f);
                                playerData.put("Blocks", 0f);
                                playerData.put("Turn_Overs", 0f);
                                playerData.put("total_points", 0f);

                                playerData.put("Points_Scored_Value", 0f);
                                playerData.put("Rebounds_Value", 0f);
                                playerData.put("Assists_Value", 0f);
                                playerData.put("Steals_Value", 0f);
                                playerData.put("Blocks_Value", 0f);
                                playerData.put("Turn_Overs_Value", 0f);

                                float points = Float.parseFloat(playing_players.get("points").toString());
                                float rebound = Float.parseFloat(playing_players.get("rebound").toString());
                                float assists = Float.parseFloat(playing_players.get("assists").toString());
                                float steals = Float.parseFloat(playing_players.get("steals").toString());
                                float blocks = Float.parseFloat(playing_players.get("blocks").toString());
                                float turnovers = Float.parseFloat(playing_players.get("turnovers").toString());
                                float totalPoints = 0;
                                if (points != 0) {
                                    totalPoints += points * game_type_point.getFloat("Points_Scored");
                                    playerData.put("Points_Scored", points * game_type_point.getFloat("Points_Scored"));
                                    playerData.put("Points_Scored_Value", points);
                                }
                                if (rebound != 0) {
                                    totalPoints += rebound * game_type_point.getFloat("Rebounds");
                                    playerData.put("Rebounds", rebound * game_type_point.getFloat("Rebounds"));
                                    playerData.put("Rebounds_Value", rebound);
                                }
                                if (assists != 0) {
                                    totalPoints += assists * game_type_point.getFloat("Assists");
                                    playerData.put("Assists", assists * game_type_point.getFloat("Assists"));
                                    playerData.put("Assists_Value", assists);
                                }
                                if (steals != 0) {
                                    totalPoints += steals * game_type_point.getFloat("Steals");
                                    playerData.put("Steals", steals * game_type_point.getFloat("Steals"));
                                    playerData.put("Steals_Value", steals);
                                }
                                if (blocks != 0) {
                                    totalPoints += blocks * game_type_point.getFloat("Blocks");
                                    playerData.put("Blocks", blocks * game_type_point.getFloat("Blocks"));
                                    playerData.put("Blocks_Value", blocks);
                                }
                                if (turnovers != 0) {
                                    totalPoints += turnovers * game_type_point.getFloat("Turn_Overs");
                                    playerData.put("Turn_Overs", turnovers * game_type_point.getFloat("Turn_Overs"));
                                    playerData.put("Turn_Overs_Value", turnovers);
                                }
                                playerData.put("total_points", totalPoints);


                                players.put(playing_players.get("pid").toString(), playerData);
                            }
                        }

                        if (game_type_point != null && api_data_array.has("match_player_stats")
                                && api_data_array.get("match_player_stats") instanceof JSONObject
                                && api_data_array.getJSONObject("match_player_stats").has("away")
                                && api_data_array.getJSONObject("match_player_stats").get("away") instanceof JSONArray) {
                            for (Object iterable_element : api_data_array.getJSONObject("match_player_stats")
                                    .getJSONArray("away")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty()) {
                                    continue;
                                }

                                if (playing_players.get("points") == null ||
                                        playing_players.get("points").toString().trim().isEmpty()) {
                                    playing_players.put("points", 0);
                                }

                                if (playing_players.get("rebound") == null ||
                                        playing_players.get("rebound").toString().trim().isEmpty()) {
                                    playing_players.put("rebound", 0);
                                }


                                if (playing_players.get("assists") == null ||
                                        playing_players.get("assists").toString().trim().isEmpty()) {
                                    playing_players.put("assists", 0);
                                }

                                if (playing_players.get("steals") == null ||
                                        playing_players.get("steals").toString().trim().isEmpty()) {
                                    playing_players.put("steals", 0);
                                }

                                if (playing_players.get("blocks") == null ||
                                        playing_players.get("blocks").toString().trim().isEmpty()) {
                                    playing_players.put("blocks", 0);
                                }

                                if (playing_players.get("turnovers") == null ||
                                        playing_players.get("turnovers").toString().trim().isEmpty()) {
                                    playing_players.put("turnovers", 0);
                                }

                                JSONObject playerData = new JSONObject();
                                playerData.put("Points_Scored", 0f);
                                playerData.put("Rebounds", 0f);
                                playerData.put("Assists", 0f);
                                playerData.put("Steals", 0f);
                                playerData.put("Blocks", 0f);
                                playerData.put("Turn_Overs", 0f);
                                playerData.put("total_points", 0f);

                                playerData.put("Points_Scored_Value", 0f);
                                playerData.put("Rebounds_Value", 0f);
                                playerData.put("Assists_Value", 0f);
                                playerData.put("Steals_Value", 0f);
                                playerData.put("Blocks_Value", 0f);
                                playerData.put("Turn_Overs_Value", 0f);

                                float points = Float.parseFloat(playing_players.get("points").toString());
                                float rebound = Float.parseFloat(playing_players.get("rebound").toString());
                                float assists = Float.parseFloat(playing_players.get("assists").toString());
                                float steals = Float.parseFloat(playing_players.get("steals").toString());
                                float blocks = Float.parseFloat(playing_players.get("blocks").toString());
                                float turnovers = Float.parseFloat(playing_players.get("turnovers").toString());
                                float totalPoints = 0;
                                if (points != 0) {
                                    totalPoints += points * game_type_point.getFloat("Points_Scored");
                                    playerData.put("Points_Scored", points * game_type_point.getFloat("Points_Scored"));
                                    playerData.put("Points_Scored_Value", points);
                                }
                                if (rebound != 0) {
                                    totalPoints += rebound * game_type_point.getFloat("Rebounds");
                                    playerData.put("Rebounds", rebound * game_type_point.getFloat("Rebounds"));
                                    playerData.put("Rebounds_Value", rebound);
                                }
                                if (assists != 0) {
                                    totalPoints += assists * game_type_point.getFloat("Assists");
                                    playerData.put("Assists", assists * game_type_point.getFloat("Assists"));
                                    playerData.put("Assists_Value", assists);
                                }
                                if (steals != 0) {
                                    totalPoints += steals * game_type_point.getFloat("Steals");
                                    playerData.put("Steals", steals * game_type_point.getFloat("Steals"));
                                    playerData.put("Steals_Value", steals);
                                }
                                if (blocks != 0) {
                                    totalPoints += blocks * game_type_point.getFloat("Blocks");
                                    playerData.put("Blocks", blocks * game_type_point.getFloat("Blocks"));
                                    playerData.put("Blocks_Value", blocks);
                                }
                                if (turnovers != 0) {
                                    totalPoints += turnovers * game_type_point.getFloat("Turn_Overs");
                                    playerData.put("Turn_Overs", turnovers * game_type_point.getFloat("Turn_Overs"));
                                    playerData.put("Turn_Overs_Value", turnovers);
                                }
                                playerData.put("total_points", totalPoints);

                                players.put(playing_players.get("pid").toString(), playerData);
                            }
                        }

                        output.put("players", players);
                        output.put("scorecard_data", scorecardData);

                        JSONObject man_of_the_match1 = null;
                        if (matchInfo.getBoolean("verified")
                                && (matchInfo.getInt("status") == 2 || matchInfo.getInt("status") == 4)) {
                            man_of_the_match1 = new JSONObject();
                            man_of_the_match1.put("pid", "");
                            man_of_the_match1.put("name", "");

                        }
                        output.put("man_of_the_match", man_of_the_match1);

                        if (matchInfo.getInt("status") == 2) {
                            output.put("match_completed_status", "R");
                        } else if (matchInfo.getInt("status") == 4) {
                            output.put("match_completed_status", "AB");
                        } else {
                            output.put("match_completed_status", "NA");
                        }
                        return output;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    public JSONObject setupDefaultPointsIfNotAvailable(JSONObject game_type_point) {

        if (game_type_point == null) {
            game_type_point = new JSONObject();
        }

        if (!game_type_point.has("Points_Scored")) {
            game_type_point.put("Points_Scored", 0f);
        }

        if (!game_type_point.has("Rebounds")) {
            game_type_point.put("Rebounds", 0f);
        }

        if (!game_type_point.has("Assists")) {
            game_type_point.put("Assists", 0f);
        }

        if (!game_type_point.has("Steals")) {
            game_type_point.put("Steals", 0f);
        }

        if (!game_type_point.has("Blocks")) {
            game_type_point.put("Blocks", 0f);
        }

        if (!game_type_point.has("Turn_Overs")) {
            game_type_point.put("Turn_Overs", 0f);
        }

        return game_type_point;

    }
}
