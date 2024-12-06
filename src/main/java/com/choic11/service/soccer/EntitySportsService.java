package com.choic11.service.soccer;

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

@Service("SoccerEntitySportsService")
public class EntitySportsService {

    public Object getUpcomingMatchesSeries(BaseRequest baseRequest) {
        String url = EntitySportsConstant.ENTITY_SOCCER_MATCHES;
        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> upcomingSeries = new LinkedHashMap<String, Object>();

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items") && response.get("items") instanceof JSONArray) {
                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object matchData : jsonArray) {
                                JSONObject match = (JSONObject) matchData;

                                if (match.get("datestart") == null || match.get("datestart").toString().trim().isEmpty()) {
                                    continue;
                                }

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
        String url = EntitySportsConstant.ENTITY_SOCCER_MATCHES;
        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> upcomingMatches = new LinkedHashMap<String, Object>();

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response") && jsonObject.get("response") instanceof JSONObject) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items") && response.get("items") instanceof JSONArray) {
                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object matchData : jsonArray) {
                                JSONObject match = (JSONObject) matchData;

                                if (match.get("datestart") == null || match.get("datestart").toString().trim().isEmpty()) {
                                    continue;
                                }

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
                                matchR.put("team-1", teamA);
                                matchR.put("team-1-short_name", teamAAbbr.isEmpty() ? teamA : teamAAbbr);
                                matchR.put("team-2", teamB);
                                matchR.put("team-2-short_name", teamBAbbr.isEmpty() ? teamB : teamBAbbr);
                                matchR.put("type", "FOOTBALL");
                                matchR.put("unique_id", match.get("mid"));
                                matchR.put("title", teamA + " vs " + teamB);
                                matchR.put("short_title", matchR.get("team-1-short_name") + " vs " + matchR.get("team-2-short_name"));
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
        String url = EntitySportsConstant.ENTITY_SOCCER_MATCH_SQUAD_ROSTER;

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

                            if (items.has("teams") && items.getJSONObject("teams").has("home")) {
                                JSONArray teamPlayersData = items.getJSONObject("teams").getJSONArray("home");
                                for (Object playerData : teamPlayersData) {
                                    JSONObject player = (JSONObject) playerData;

                                    LinkedHashMap<String, Object> singlePlayerData = new LinkedHashMap<String, Object>();

                                    String playerShortName = Util.getPlayerFormattedName((String) player.get("pname"));
                                    singlePlayerData.put("pid", Integer.parseInt(player.get("pid").toString()));
                                    singlePlayerData.put("name", player.get("pname"));
                                    singlePlayerData.put("short_name", playerShortName);
                                    singlePlayerData.put("battingStyle", " ");
                                    singlePlayerData.put("bowlingStyle", " ");
                                    singlePlayerData.put("credits", player.get("rating"));
                                    singlePlayerData.put("playingRole", player.get("role"));
                                    singlePlayerData.put("born", "");
                                    singlePlayerData.put("country", "");


                                    LinkedHashMap<String, Object> realPlayerData = new LinkedHashMap<String, Object>();
                                    realPlayerData.put("pid", singlePlayerData.get("pid"));
                                    realPlayerData.put("name", singlePlayerData.get("name"));
                                    realPlayerData.put("credits", singlePlayerData.get("credits"));
                                    realPlayerData.put("playingRole", singlePlayerData.get("playingRole"));
                                    realPlayerData.put("detail_data", singlePlayerData);

                                    teama_palyers.add(realPlayerData);
                                }
                            }

                            if (items.has("teams") && items.getJSONObject("teams").has("away")) {
                                JSONArray teamPlayersData = items.getJSONObject("teams").getJSONArray("away");
                                for (Object playerData : teamPlayersData) {
                                    JSONObject player = (JSONObject) playerData;

                                    LinkedHashMap<String, Object> singlePlayerData = new LinkedHashMap<String, Object>();

                                    String playerShortName = Util.getPlayerFormattedName((String) player.get("pname"));
                                    singlePlayerData.put("pid", Integer.parseInt(player.get("pid").toString()));
                                    singlePlayerData.put("name", player.get("pname"));
                                    singlePlayerData.put("short_name", playerShortName);
                                    singlePlayerData.put("battingStyle", " ");
                                    singlePlayerData.put("bowlingStyle", " ");
                                    singlePlayerData.put("credits", player.get("rating"));
                                    singlePlayerData.put("playingRole", player.get("role"));
                                    singlePlayerData.put("born", "");
                                    singlePlayerData.put("country", "");


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
        String url = EntitySportsConstant.ENTITY_SOCCER_PLAYER_FINDER;

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
                                player.put("bowlingStyle", item.optString("foot", ""));
                                player.put("playingRole", item.optString("positionname", ""));

                                String realBirthday = item.optString("birthdate", "0000-00-00");
                                if (realBirthday.split(" ").length > 1) {
                                    realBirthday = realBirthday.split(" ")[0];
                                }
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
        String url = EntitySportsConstant.ENTITY_SOCCER_PLAYER_DETAIL;

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
                            player.put("bowlingStyle", item.get("foot").toString());
                            player.put("playingRole", item.get("positionname").toString());

                            String realBirthday = item.getString("birthdate");
                            if (realBirthday.split(" ").length > 1) {
                                realBirthday = realBirthday.split(" ")[0];
                            }
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
        String url = EntitySportsConstant.ENTITY_SOCCER_MATCH_LINEUP;

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
                                    && items.getJSONObject("lineup").has("home")
                                    && items.getJSONObject("lineup").get("home") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("home").has("lineup")
                                    && items.getJSONObject("lineup").getJSONObject("home").get("lineup") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("home").getJSONObject("lineup").has("player")
                                    && items.getJSONObject("lineup").getJSONObject("home").getJSONObject("lineup").get("player") instanceof JSONArray) {
                                JSONArray jsonArray = items.getJSONObject("lineup").getJSONObject("home").getJSONObject("lineup").getJSONArray("player");
                                for (Object itemSquadData : jsonArray) {
                                    JSONObject squadData = (JSONObject) itemSquadData;
                                    players.add(Integer.parseInt(squadData.get("pid").toString()));
                                }
                            }

                            if (items.has("lineup")
                                    && items.get("lineup") instanceof JSONObject
                                    && items.getJSONObject("lineup").has("away")
                                    && items.getJSONObject("lineup").get("away") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("away").has("lineup")
                                    && items.getJSONObject("lineup").getJSONObject("away").get("lineup") instanceof JSONObject
                                    && items.getJSONObject("lineup").getJSONObject("away").getJSONObject("lineup").has("player")
                                    && items.getJSONObject("lineup").getJSONObject("away").getJSONObject("lineup").get("player") instanceof JSONArray) {
                                JSONArray jsonArray = items.getJSONObject("lineup").getJSONObject("away").getJSONObject("lineup").getJSONArray("player");
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

        String url = EntitySportsConstant.ENTITY_SOCCER_MATCH_SCOREBOARD;

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
                            team1_run = (String) matchInfo.getJSONObject("result").get("home");
                            if (team1_run == null) {
                                team1_run = "0";
                            }

                            team2_run = (String) matchInfo.getJSONObject("result").get("away");
                            if (team2_run == null) {
                                team2_run = "0";
                            }

                            String matchWinner = (String) matchInfo.getJSONObject("result").get("winner");
                            if (matchInfo.has("status") &&
                                    (matchInfo.get("status").toString().equals("2")
                                            || matchInfo.get("status").toString().equals("5")
                                            || matchInfo.get("status").toString().equals("6"))) {
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
                        JSONObject home_players_data = new JSONObject();
                        JSONObject away_players_data = new JSONObject();
                        if (api_data_array.has("teams") && api_data_array.get("teams") instanceof JSONObject
                                && api_data_array.getJSONObject("teams").has("home")
                                && api_data_array.getJSONObject("teams").get("home") instanceof JSONArray) {

                            for (Object iterable_element : api_data_array.getJSONObject("teams")
                                    .getJSONArray("home")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty() || pid
                                        .toString().trim().equals("0")) {
                                    continue;
                                }
                                home_players_data.put(playing_players.get("pid").toString(), playing_players);
                            }

                        }

                        if (api_data_array.has("teams") && api_data_array.get("teams") instanceof JSONObject
                                && api_data_array.getJSONObject("teams").has("away")
                                && api_data_array.getJSONObject("teams").get("away") instanceof JSONArray) {

                            for (Object iterable_element : api_data_array.getJSONObject("teams")
                                    .getJSONArray("away")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty() || pid
                                        .toString().trim().equals("0")) {
                                    continue;
                                }
                                away_players_data.put(playing_players.get("pid").toString(), playing_players);
                            }

                        }

                        if (game_type_point != null && api_data_array.has("playerstats")
                                && api_data_array.get("playerstats") instanceof JSONObject
                                && api_data_array.getJSONObject("playerstats").has("home")
                                && api_data_array.getJSONObject("playerstats").get("home") instanceof JSONArray) {
                            for (Object iterable_element : api_data_array.getJSONObject("playerstats")
                                    .getJSONArray("home")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty() || pid
                                        .toString().trim().equals("0")) {
                                    continue;
                                }
                                if (!home_players_data.has(pid.toString())) {
                                    continue;
                                }
                                playing_players.put("role", home_players_data.getJSONObject(pid.toString()).get("role"));

                                if (playing_players.get("goalscored") == null ||
                                        playing_players.get("goalscored").toString().trim().isEmpty()) {
                                    playing_players.put("goalscored", 0);
                                }

                                if (playing_players.get("assist") == null ||
                                        playing_players.get("assist").toString().trim().isEmpty()) {
                                    playing_players.put("assist", 0);
                                }


                                if (playing_players.get("shotsontarget") == null ||
                                        playing_players.get("shotsontarget").toString().trim().isEmpty()) {
                                    playing_players.put("shotsontarget", 0);
                                }

                                if (playing_players.get("chancecreated") == null ||
                                        playing_players.get("chancecreated").toString().trim().isEmpty()) {
                                    playing_players.put("chancecreated", 0);
                                }

                                if (playing_players.get("passes") == null ||
                                        playing_players.get("passes").toString().trim().isEmpty()) {
                                    playing_players.put("passes", 0);
                                }

                                if (playing_players.get("tacklesuccessful") == null ||
                                        playing_players.get("tacklesuccessful").toString().trim().isEmpty()) {
                                    playing_players.put("tacklesuccessful", 0);
                                }

                                if (playing_players.get("interceptionwon") == null ||
                                        playing_players.get("interceptionwon").toString().trim().isEmpty()) {
                                    playing_players.put("interceptionwon", 0);
                                }

                                if (playing_players.get("blockedshot") == null ||
                                        playing_players.get("blockedshot").toString().trim().isEmpty()) {
                                    playing_players.put("blockedshot", 0);
                                }

                                if (playing_players.get("clearance") == null ||
                                        playing_players.get("clearance").toString().trim().isEmpty()) {
                                    playing_players.put("clearance", 0);
                                }

                                if (playing_players.get("shotssaved") == null ||
                                        playing_players.get("shotssaved").toString().trim().isEmpty()) {
                                    playing_players.put("shotssaved", 0);
                                }

                                if (playing_players.get("penaltysaved") == null ||
                                        playing_players.get("penaltysaved").toString().trim().isEmpty()) {
                                    playing_players.put("penaltysaved", 0);
                                }

                                if (playing_players.get("cleansheet") == null ||
                                        playing_players.get("cleansheet").toString().trim().isEmpty()) {
                                    playing_players.put("cleansheet", 0);
                                }

                                if (playing_players.get("minutesplayed") == null ||
                                        playing_players.get("minutesplayed").toString().trim().isEmpty()) {
                                    playing_players.put("minutesplayed", 0);
                                }

                                if (playing_players.get("starting11") == null ||
                                        playing_players.get("starting11").toString().trim().isEmpty()) {
                                    playing_players.put("starting11", 0);
                                }

                                if (playing_players.get("substitute") == null ||
                                        playing_players.get("substitute").toString().trim().isEmpty()) {
                                    playing_players.put("substitute", 0);
                                }

                                if (playing_players.get("yellowcard") == null ||
                                        playing_players.get("yellowcard").toString().trim().isEmpty()) {
                                    playing_players.put("yellowcard", 0);
                                }

                                if (playing_players.get("redcard") == null ||
                                        playing_players.get("redcard").toString().trim().isEmpty()) {
                                    playing_players.put("redcard", 0);
                                }

                                if (playing_players.get("owngoal") == null ||
                                        playing_players.get("owngoal").toString().trim().isEmpty()) {
                                    playing_players.put("owngoal", 0);
                                }

                                if (playing_players.get("goalsconceded") == null ||
                                        playing_players.get("goalsconceded").toString().trim().isEmpty()) {
                                    playing_players.put("goalsconceded", 0);
                                }

                                if (playing_players.get("penaltymissed") == null ||
                                        playing_players.get("penaltymissed").toString().trim().isEmpty()) {
                                    playing_players.put("penaltymissed", 0);
                                }

                                JSONObject playerData = new JSONObject();
                                playerData.put("Goal", 0f);
                                playerData.put("Assist", 0f);
                                playerData.put("Shot_On_Target", 0f);
                                playerData.put("Chance_Created", 0f);
                                playerData.put("5_Passes_Completed", 0f);
                                playerData.put("Tackle_Won", 0f);
                                playerData.put("Interception_Won", 0f);
                                playerData.put("Blocked_Shot", 0f);
                                playerData.put("Clearance", 0f);
                                playerData.put("Saves", 0f);
                                playerData.put("Penalty_Saved", 0f);
                                playerData.put("Clean_Sheet", 0f);
                                playerData.put("In_Starting_11", 0f);
                                playerData.put("Coming_on_as_a_Substitute", 0f);
                                playerData.put("Yellow_Card", 0f);
                                playerData.put("Red_Card", 0f);
                                playerData.put("Own_goal", 0f);
                                playerData.put("Goals_Conceded", 0f);
                                playerData.put("Penalty_Missed", 0f);
                                playerData.put("total_points", 0f);

                                playerData.put("Goal_Value", 0f);
                                playerData.put("Assist_Value", 0f);
                                playerData.put("Shot_On_Target_Value", 0f);
                                playerData.put("Chance_Created_Value", 0f);
                                playerData.put("5_Passes_Completed_Value", 0f);
                                playerData.put("Tackle_Won_Value", 0f);
                                playerData.put("Interception_Won_Value", 0f);
                                playerData.put("Blocked_Shot_Value", 0f);
                                playerData.put("Clearance_Value", 0f);
                                playerData.put("Saves_Value", 0f);
                                playerData.put("Penalty_Saved_Value", 0f);
                                playerData.put("Clean_Sheet_Value", 0f);
                                playerData.put("In_Starting_11_Value", 0f);
                                playerData.put("Coming_on_as_a_Substitute_Value", 0f);
                                playerData.put("Yellow_Card_Value", 0f);
                                playerData.put("Red_Card_Value", 0f);
                                playerData.put("Own_goal_Value", 0f);
                                playerData.put("Goals_Conceded_Value", 0f);
                                playerData.put("Penalty_Missed_Value", 0f);

                                float goalscored = Float.parseFloat(playing_players.get("goalscored").toString());
                                float assist = Float.parseFloat(playing_players.get("assist").toString());
                                float shotsontarget = Float.parseFloat(playing_players.get("shotsontarget").toString());
                                float chancecreated = Float.parseFloat(playing_players.get("chancecreated").toString());
                                float passes = Float.parseFloat(playing_players.get("passes").toString());
                                float tacklesuccessful = Float.parseFloat(playing_players.get("tacklesuccessful").toString());
                                float interceptionwon = Float.parseFloat(playing_players.get("interceptionwon").toString());
                                float blockedshot = Float.parseFloat(playing_players.get("blockedshot").toString());
                                float clearance = Float.parseFloat(playing_players.get("clearance").toString());
                                float shotssaved = Float.parseFloat(playing_players.get("shotssaved").toString());
                                float penaltysaved = Float.parseFloat(playing_players.get("penaltysaved").toString());
                                float cleansheet = Float.parseFloat(playing_players.get("cleansheet").toString());
                                float minutesplayed = Float.parseFloat(playing_players.get("minutesplayed").toString());
                                float starting11 = Float.parseFloat(playing_players.get("starting11").toString());
                                float substitute = Float.parseFloat(playing_players.get("substitute").toString());
                                float yellowcard = Float.parseFloat(playing_players.get("yellowcard").toString());
                                float redcard = Float.parseFloat(playing_players.get("redcard").toString());
                                float owngoal = Float.parseFloat(playing_players.get("owngoal").toString());
                                float goalsconceded = Float.parseFloat(playing_players.get("goalsconceded").toString());
                                float penaltymissed = Float.parseFloat(playing_players.get("penaltymissed").toString());

                                float totalPoints = 0;
                                if (goalscored != 0) {
                                    float Goal_Scored_point = 0;
                                    if (playing_players.getString("role").equals("Goalkeeper") || playing_players.getString("role").equals("Defender")) {
                                        Goal_Scored_point = game_type_point.getFloat("Goal_GK_Defender");
                                    } else if (playing_players.getString("role").equals("Midfielder")) {
                                        Goal_Scored_point = game_type_point.getFloat("Goal_MidFielder");
                                    } else {
                                        Goal_Scored_point = game_type_point.getFloat("Goal");
                                    }
                                    totalPoints += goalscored * Goal_Scored_point;
                                    playerData.put("Goal", goalscored * Goal_Scored_point);
                                    playerData.put("Goal_Value", goalscored);
                                }
                                if (assist != 0) {
                                    totalPoints += assist * game_type_point.getFloat("Assist");
                                    playerData.put("Assist", assist * game_type_point.getFloat("Assist"));
                                    playerData.put("Assist_Value", assist);
                                }
                                if (shotsontarget != 0) {
                                    totalPoints += shotsontarget * game_type_point.getFloat("Shot_On_Target");
                                    playerData.put("Shot_On_Target", shotsontarget * game_type_point.getFloat("Shot_On_Target"));
                                    playerData.put("Shot_On_Target_Value", shotsontarget);
                                }
                                if (chancecreated != 0) {
                                    totalPoints += chancecreated * game_type_point.getFloat("Chance_Created");
                                    playerData.put("Chance_Created", chancecreated * game_type_point.getFloat("Chance_Created"));
                                    playerData.put("Chance_Created_Value", chancecreated);
                                }
                                if (passes != 0) {
                                    int every_5_passes = (int) (passes / 5);
                                    if (every_5_passes > 0) {
                                        totalPoints += every_5_passes * game_type_point.getFloat("5_Passes_Completed");
                                        playerData.put("5_Passes_Completed", every_5_passes * game_type_point.getFloat("5_Passes_Completed"));
                                    }

                                    playerData.put("5_Passes_Completed_Value", passes);
                                }
                                if (tacklesuccessful != 0) {
                                    totalPoints += tacklesuccessful * game_type_point.getFloat("Tackle_Won");
                                    playerData.put("Tackle_Won", tacklesuccessful * game_type_point.getFloat("Tackle_Won"));
                                    playerData.put("Tackle_Won_Value", tacklesuccessful);
                                }
                                if (interceptionwon != 0) {
                                    totalPoints += interceptionwon * game_type_point.getFloat("Interception_Won");
                                    playerData.put("Interception_Won", interceptionwon * game_type_point.getFloat("Interception_Won"));
                                    playerData.put("Interception_Won_Value", interceptionwon);
                                }
                                if (blockedshot != 0) {
                                    totalPoints += blockedshot * game_type_point.getFloat("Blocked_Shot");
                                    playerData.put("Blocked_Shot", blockedshot * game_type_point.getFloat("Blocked_Shot"));
                                    playerData.put("Blocked_Shot_Value", blockedshot);
                                }
                                if (clearance != 0) {
                                    totalPoints += clearance * game_type_point.getFloat("Clearance");
                                    playerData.put("Clearance", clearance * game_type_point.getFloat("Clearance"));
                                    playerData.put("Clearance_Value", clearance);
                                }
                                if (shotssaved != 0) {
                                    if (playing_players.getString("role").equals("Goalkeeper")) {
                                        totalPoints += shotssaved * game_type_point.getFloat("Saves_GK");
                                        playerData.put("Saves", shotssaved * game_type_point.getFloat("Saves_GK"));
                                    }
                                    playerData.put("Saves_Value", shotssaved);
                                }
                                if (penaltysaved != 0) {
                                    if (playing_players.getString("role").equals("Goalkeeper")) {
                                        totalPoints += penaltysaved * game_type_point.getFloat("Penalty_Saved_GK");
                                        playerData.put("Penalty_Saved", penaltysaved * game_type_point.getFloat("Penalty_Saved_GK"));
                                    }
                                    playerData.put("Penalty_Saved_Value", penaltysaved);
                                }
                                if (cleansheet != 0) {
                                    float Clean_Sheet = 0;
                                    if ((playing_players.getString("role").equals("Goalkeeper")
                                            || playing_players.getString("role").equals("Defender"))
                                            && minutesplayed > 55) {
                                        Clean_Sheet = game_type_point.getFloat("Clean_Sheet_GK_Defender");
                                    }
                                    if (Clean_Sheet > 0) {
                                        totalPoints += Clean_Sheet;
                                        playerData.put("Clean_Sheet", Clean_Sheet);
                                        playerData.put("Clean_Sheet_Value", 1);
                                    }
                                }

                                if (starting11 != 0) {
                                    totalPoints += starting11 * game_type_point.getFloat("In_Starting_11");
                                    playerData.put("In_Starting_11", starting11 * game_type_point.getFloat("In_Starting_11"));
                                    playerData.put("In_Starting_11_Value", starting11);
                                }
                                if (substitute != 0) {
                                    totalPoints += substitute * game_type_point.getFloat("Coming_on_as_a_Substitute");
                                    playerData.put("Coming_on_as_a_Substitute", substitute * game_type_point.getFloat("Coming_on_as_a_Substitute"));
                                    playerData.put("Coming_on_as_a_Substitute_Value", substitute);
                                }
                                if (yellowcard != 0) {
                                    totalPoints += yellowcard * game_type_point.getFloat("Yellow_Card");
                                    playerData.put("Yellow_Card", yellowcard * game_type_point.getFloat("Yellow_Card"));
                                    playerData.put("Yellow_Card_Value", yellowcard);
                                }
                                if (redcard != 0) {
                                    totalPoints += redcard * game_type_point.getFloat("Red_Card");
                                    playerData.put("Red_Card", redcard * game_type_point.getFloat("Red_Card"));
                                    playerData.put("Red_Card_Value", redcard);
                                }
                                if (owngoal != 0) {
                                    totalPoints += owngoal * game_type_point.getFloat("Own_goal");
                                    playerData.put("Own_goal", owngoal * game_type_point.getFloat("Own_goal"));
                                    playerData.put("Own_goal_Value", owngoal);
                                }
                                if (goalsconceded != 0) {
                                    if ((playing_players.getString("role").equals("Goalkeeper")
                                            || playing_players.getString("role").equals("Defender"))) {
                                        totalPoints += goalsconceded * game_type_point.getFloat("Goals_Conceded_Gk_Defender");
                                        playerData.put("Goals_Conceded", goalsconceded * game_type_point.getFloat("Goals_Conceded_Gk_Defender"));
                                    }
                                    playerData.put("Goals_Conceded_Value", goalsconceded);
                                }
                                if (penaltymissed != 0) {
                                    totalPoints += penaltymissed * game_type_point.getFloat("Penalty_Missed");
                                    playerData.put("Penalty_Missed", penaltymissed * game_type_point.getFloat("Penalty_Missed"));
                                    playerData.put("Penalty_Missed_Value", penaltymissed);
                                }

                                playerData.put("total_points", totalPoints);

                                players.put(playing_players.get("pid").toString(), playerData);
                            }
                        }

                        if (game_type_point != null && api_data_array.has("playerstats")
                                && api_data_array.get("playerstats") instanceof JSONObject
                                && api_data_array.getJSONObject("playerstats").has("away")
                                && api_data_array.getJSONObject("playerstats").get("away") instanceof JSONArray) {
                            for (Object iterable_element : api_data_array.getJSONObject("playerstats")
                                    .getJSONArray("away")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                Object pid = playing_players.get("pid");
                                if (pid == null || pid.toString().trim().isEmpty() || pid
                                        .toString().trim().equals("0")) {
                                    continue;
                                }
                                if (!away_players_data.has(pid.toString())) {
                                    continue;
                                }
                                playing_players.put("role", away_players_data.getJSONObject(pid.toString()).get("role"));

                                if (playing_players.get("goalscored") == null ||
                                        playing_players.get("goalscored").toString().trim().isEmpty()) {
                                    playing_players.put("goalscored", 0);
                                }

                                if (playing_players.get("assist") == null ||
                                        playing_players.get("assist").toString().trim().isEmpty()) {
                                    playing_players.put("assist", 0);
                                }


                                if (playing_players.get("shotsontarget") == null ||
                                        playing_players.get("shotsontarget").toString().trim().isEmpty()) {
                                    playing_players.put("shotsontarget", 0);
                                }

                                if (playing_players.get("chancecreated") == null ||
                                        playing_players.get("chancecreated").toString().trim().isEmpty()) {
                                    playing_players.put("chancecreated", 0);
                                }

                                if (playing_players.get("passes") == null ||
                                        playing_players.get("passes").toString().trim().isEmpty()) {
                                    playing_players.put("passes", 0);
                                }

                                if (playing_players.get("tacklesuccessful") == null ||
                                        playing_players.get("tacklesuccessful").toString().trim().isEmpty()) {
                                    playing_players.put("tacklesuccessful", 0);
                                }

                                if (playing_players.get("interceptionwon") == null ||
                                        playing_players.get("interceptionwon").toString().trim().isEmpty()) {
                                    playing_players.put("interceptionwon", 0);
                                }

                                if (playing_players.get("blockedshot") == null ||
                                        playing_players.get("blockedshot").toString().trim().isEmpty()) {
                                    playing_players.put("blockedshot", 0);
                                }

                                if (playing_players.get("clearance") == null ||
                                        playing_players.get("clearance").toString().trim().isEmpty()) {
                                    playing_players.put("clearance", 0);
                                }

                                if (playing_players.get("shotssaved") == null ||
                                        playing_players.get("shotssaved").toString().trim().isEmpty()) {
                                    playing_players.put("shotssaved", 0);
                                }

                                if (playing_players.get("penaltysaved") == null ||
                                        playing_players.get("penaltysaved").toString().trim().isEmpty()) {
                                    playing_players.put("penaltysaved", 0);
                                }

                                if (playing_players.get("cleansheet") == null ||
                                        playing_players.get("cleansheet").toString().trim().isEmpty()) {
                                    playing_players.put("cleansheet", 0);
                                }

                                if (playing_players.get("minutesplayed") == null ||
                                        playing_players.get("minutesplayed").toString().trim().isEmpty()) {
                                    playing_players.put("minutesplayed", 0);
                                }

                                if (playing_players.get("starting11") == null ||
                                        playing_players.get("starting11").toString().trim().isEmpty()) {
                                    playing_players.put("starting11", 0);
                                }

                                if (playing_players.get("substitute") == null ||
                                        playing_players.get("substitute").toString().trim().isEmpty()) {
                                    playing_players.put("substitute", 0);
                                }

                                if (playing_players.get("yellowcard") == null ||
                                        playing_players.get("yellowcard").toString().trim().isEmpty()) {
                                    playing_players.put("yellowcard", 0);
                                }

                                if (playing_players.get("redcard") == null ||
                                        playing_players.get("redcard").toString().trim().isEmpty()) {
                                    playing_players.put("redcard", 0);
                                }

                                if (playing_players.get("owngoal") == null ||
                                        playing_players.get("owngoal").toString().trim().isEmpty()) {
                                    playing_players.put("owngoal", 0);
                                }

                                if (playing_players.get("goalsconceded") == null ||
                                        playing_players.get("goalsconceded").toString().trim().isEmpty()) {
                                    playing_players.put("goalsconceded", 0);
                                }

                                if (playing_players.get("penaltymissed") == null ||
                                        playing_players.get("penaltymissed").toString().trim().isEmpty()) {
                                    playing_players.put("penaltymissed", 0);
                                }

                                JSONObject playerData = new JSONObject();
                                playerData.put("Goal", 0f);
                                playerData.put("Assist", 0f);
                                playerData.put("Shot_On_Target", 0f);
                                playerData.put("Chance_Created", 0f);
                                playerData.put("5_Passes_Completed", 0f);
                                playerData.put("Tackle_Won", 0f);
                                playerData.put("Interception_Won", 0f);
                                playerData.put("Blocked_Shot", 0f);
                                playerData.put("Clearance", 0f);
                                playerData.put("Saves", 0f);
                                playerData.put("Penalty_Saved", 0f);
                                playerData.put("Clean_Sheet", 0f);
                                playerData.put("In_Starting_11", 0f);
                                playerData.put("Coming_on_as_a_Substitute", 0f);
                                playerData.put("Yellow_Card", 0f);
                                playerData.put("Red_Card", 0f);
                                playerData.put("Own_goal", 0f);
                                playerData.put("Goals_Conceded", 0f);
                                playerData.put("Penalty_Missed", 0f);
                                playerData.put("total_points", 0f);

                                playerData.put("Goal_Value", 0f);
                                playerData.put("Assist_Value", 0f);
                                playerData.put("Shot_On_Target_Value", 0f);
                                playerData.put("Chance_Created_Value", 0f);
                                playerData.put("5_Passes_Completed_Value", 0f);
                                playerData.put("Tackle_Won_Value", 0f);
                                playerData.put("Interception_Won_Value", 0f);
                                playerData.put("Blocked_Shot_Value", 0f);
                                playerData.put("Clearance_Value", 0f);
                                playerData.put("Saves_Value", 0f);
                                playerData.put("Penalty_Saved_Value", 0f);
                                playerData.put("Clean_Sheet_Value", 0f);
                                playerData.put("In_Starting_11_Value", 0f);
                                playerData.put("Coming_on_as_a_Substitute_Value", 0f);
                                playerData.put("Yellow_Card_Value", 0f);
                                playerData.put("Red_Card_Value", 0f);
                                playerData.put("Own_goal_Value", 0f);
                                playerData.put("Goals_Conceded_Value", 0f);
                                playerData.put("Penalty_Missed_Value", 0f);

                                float goalscored = Float.parseFloat(playing_players.get("goalscored").toString());
                                float assist = Float.parseFloat(playing_players.get("assist").toString());
                                float shotsontarget = Float.parseFloat(playing_players.get("shotsontarget").toString());
                                float chancecreated = Float.parseFloat(playing_players.get("chancecreated").toString());
                                float passes = Float.parseFloat(playing_players.get("passes").toString());
                                float tacklesuccessful = Float.parseFloat(playing_players.get("tacklesuccessful").toString());
                                float interceptionwon = Float.parseFloat(playing_players.get("interceptionwon").toString());
                                float blockedshot = Float.parseFloat(playing_players.get("blockedshot").toString());
                                float clearance = Float.parseFloat(playing_players.get("clearance").toString());
                                float shotssaved = Float.parseFloat(playing_players.get("shotssaved").toString());
                                float penaltysaved = Float.parseFloat(playing_players.get("penaltysaved").toString());
                                float cleansheet = Float.parseFloat(playing_players.get("cleansheet").toString());
                                float minutesplayed = Float.parseFloat(playing_players.get("minutesplayed").toString());
                                float starting11 = Float.parseFloat(playing_players.get("starting11").toString());
                                float substitute = Float.parseFloat(playing_players.get("substitute").toString());
                                float yellowcard = Float.parseFloat(playing_players.get("yellowcard").toString());
                                float redcard = Float.parseFloat(playing_players.get("redcard").toString());
                                float owngoal = Float.parseFloat(playing_players.get("owngoal").toString());
                                float goalsconceded = Float.parseFloat(playing_players.get("goalsconceded").toString());
                                float penaltymissed = Float.parseFloat(playing_players.get("penaltymissed").toString());

                                float totalPoints = 0;
                                if (goalscored != 0) {
                                    float Goal_Scored_point = 0;
                                    if (playing_players.getString("role").equals("Goalkeeper") || playing_players.getString("role").equals("Defender")) {
                                        Goal_Scored_point = game_type_point.getFloat("Goal_GK_Defender");
                                    } else if (playing_players.getString("role").equals("Midfielder")) {
                                        Goal_Scored_point = game_type_point.getFloat("Goal_MidFielder");
                                    } else {
                                        Goal_Scored_point = game_type_point.getFloat("Goal");
                                    }
                                    totalPoints += goalscored * Goal_Scored_point;
                                    playerData.put("Goal", goalscored * Goal_Scored_point);
                                    playerData.put("Goal_Value", goalscored);
                                }
                                if (assist != 0) {
                                    totalPoints += assist * game_type_point.getFloat("Assist");
                                    playerData.put("Assist", assist * game_type_point.getFloat("Assist"));
                                    playerData.put("Assist_Value", assist);
                                }
                                if (shotsontarget != 0) {
                                    totalPoints += shotsontarget * game_type_point.getFloat("Shot_On_Target");
                                    playerData.put("Shot_On_Target", shotsontarget * game_type_point.getFloat("Shot_On_Target"));
                                    playerData.put("Shot_On_Target_Value", shotsontarget);
                                }
                                if (chancecreated != 0) {
                                    totalPoints += chancecreated * game_type_point.getFloat("Chance_Created");
                                    playerData.put("Chance_Created", chancecreated * game_type_point.getFloat("Chance_Created"));
                                    playerData.put("Chance_Created_Value", chancecreated);
                                }
                                if (passes != 0) {
                                    int every_5_passes = (int) (passes / 5);
                                    if (every_5_passes > 0) {
                                        totalPoints += every_5_passes * game_type_point.getFloat("5_Passes_Completed");
                                        playerData.put("5_Passes_Completed", every_5_passes * game_type_point.getFloat("5_Passes_Completed"));
                                    }

                                    playerData.put("5_Passes_Completed_Value", passes);
                                }
                                if (tacklesuccessful != 0) {
                                    totalPoints += tacklesuccessful * game_type_point.getFloat("Tackle_Won");
                                    playerData.put("Tackle_Won", tacklesuccessful * game_type_point.getFloat("Tackle_Won"));
                                    playerData.put("Tackle_Won_Value", tacklesuccessful);
                                }
                                if (interceptionwon != 0) {
                                    totalPoints += interceptionwon * game_type_point.getFloat("Interception_Won");
                                    playerData.put("Interception_Won", interceptionwon * game_type_point.getFloat("Interception_Won"));
                                    playerData.put("Interception_Won_Value", interceptionwon);
                                }
                                if (blockedshot != 0) {
                                    totalPoints += blockedshot * game_type_point.getFloat("Blocked_Shot");
                                    playerData.put("Blocked_Shot", blockedshot * game_type_point.getFloat("Blocked_Shot"));
                                    playerData.put("Blocked_Shot_Value", blockedshot);
                                }
                                if (clearance != 0) {
                                    totalPoints += clearance * game_type_point.getFloat("Clearance");
                                    playerData.put("Clearance", clearance * game_type_point.getFloat("Clearance"));
                                    playerData.put("Clearance_Value", clearance);
                                }
                                if (shotssaved != 0) {
                                    if (playing_players.getString("role").equals("Goalkeeper")) {
                                        totalPoints += shotssaved * game_type_point.getFloat("Saves_GK");
                                        playerData.put("Saves", shotssaved * game_type_point.getFloat("Saves_GK"));
                                    }
                                    playerData.put("Saves_Value", shotssaved);
                                }
                                if (penaltysaved != 0) {
                                    if (playing_players.getString("role").equals("Goalkeeper")) {
                                        totalPoints += penaltysaved * game_type_point.getFloat("Penalty_Saved_GK");
                                        playerData.put("Penalty_Saved", penaltysaved * game_type_point.getFloat("Penalty_Saved_GK"));
                                    }
                                    playerData.put("Penalty_Saved_Value", penaltysaved);
                                }
                                if (cleansheet != 0) {
                                    float Clean_Sheet = 0;
                                    if ((playing_players.getString("role").equals("Goalkeeper")
                                            || playing_players.getString("role").equals("Defender"))
                                            && minutesplayed > 55) {
                                        Clean_Sheet = game_type_point.getFloat("Clean_Sheet_GK_Defender");
                                    }
                                    if (Clean_Sheet > 0) {
                                        totalPoints += Clean_Sheet;
                                        playerData.put("Clean_Sheet", Clean_Sheet);
                                        playerData.put("Clean_Sheet_Value", 1);
                                    }
                                }

                                if (starting11 != 0) {
                                    totalPoints += starting11 * game_type_point.getFloat("In_Starting_11");
                                    playerData.put("In_Starting_11", starting11 * game_type_point.getFloat("In_Starting_11"));
                                    playerData.put("In_Starting_11_Value", starting11);
                                }
                                if (substitute != 0) {
                                    totalPoints += substitute * game_type_point.getFloat("Coming_on_as_a_Substitute");
                                    playerData.put("Coming_on_as_a_Substitute", substitute * game_type_point.getFloat("Coming_on_as_a_Substitute"));
                                    playerData.put("Coming_on_as_a_Substitute_Value", substitute);
                                }
                                if (yellowcard != 0) {
                                    totalPoints += yellowcard * game_type_point.getFloat("Yellow_Card");
                                    playerData.put("Yellow_Card", yellowcard * game_type_point.getFloat("Yellow_Card"));
                                    playerData.put("Yellow_Card_Value", yellowcard);
                                }
                                if (redcard != 0) {
                                    totalPoints += redcard * game_type_point.getFloat("Red_Card");
                                    playerData.put("Red_Card", redcard * game_type_point.getFloat("Red_Card"));
                                    playerData.put("Red_Card_Value", redcard);
                                }
                                if (owngoal != 0) {
                                    totalPoints += owngoal * game_type_point.getFloat("Own_goal");
                                    playerData.put("Own_goal", owngoal * game_type_point.getFloat("Own_goal"));
                                    playerData.put("Own_goal_Value", owngoal);
                                }
                                if (goalsconceded != 0) {
                                    if ((playing_players.getString("role").equals("Goalkeeper")
                                            || playing_players.getString("role").equals("Defender"))) {
                                        totalPoints += goalsconceded * game_type_point.getFloat("Goals_Conceded_Gk_Defender");
                                        playerData.put("Goals_Conceded", goalsconceded * game_type_point.getFloat("Goals_Conceded_Gk_Defender"));
                                    }
                                    playerData.put("Goals_Conceded_Value", goalsconceded);
                                }
                                if (penaltymissed != 0) {
                                    totalPoints += penaltymissed * game_type_point.getFloat("Penalty_Missed");
                                    playerData.put("Penalty_Missed", penaltymissed * game_type_point.getFloat("Penalty_Missed"));
                                    playerData.put("Penalty_Missed_Value", penaltymissed);
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

        if (!game_type_point.has("Goal")) {
            game_type_point.put("Goal", 0f);
        }

        if (!game_type_point.has("Assist")) {
            game_type_point.put("Assist", 0f);
        }

        if (!game_type_point.has("Shot_On_Target")) {
            game_type_point.put("Shot_On_Target", 0f);
        }

        if (!game_type_point.has("Chance_Created")) {
            game_type_point.put("Chance_Created", 0f);
        }

        if (!game_type_point.has("5_Passes_Completed")) {
            game_type_point.put("5_Passes_Completed", 0f);
        }

        if (!game_type_point.has("Tackle_Won")) {
            game_type_point.put("Tackle_Won", 0f);
        }

        if (!game_type_point.has("Interception_Won")) {
            game_type_point.put("Interception_Won", 0f);
        }

        if (!game_type_point.has("Blocked_Shot")) {
            game_type_point.put("Blocked_Shot", 0f);
        }

        if (!game_type_point.has("Clearance")) {
            game_type_point.put("Clearance", 0f);
        }

        if (!game_type_point.has("Saves_GK")) {
            game_type_point.put("Saves_GK", 0f);
        }

        if (!game_type_point.has("Penalty_Saved_GK")) {
            game_type_point.put("Penalty_Saved_GK", 0f);
        }

        if (!game_type_point.has("Clean_Sheet_GK_Defender")) {
            game_type_point.put("Clean_Sheet_GK_Defender", 0f);
        }

        if (!game_type_point.has("In_Starting_11")) {
            game_type_point.put("In_Starting_11", 0f);
        }

        if (!game_type_point.has("Coming_on_as_a_Substitute")) {
            game_type_point.put("Coming_on_as_a_Substitute", 0f);
        }

        if (!game_type_point.has("Yellow_Card")) {
            game_type_point.put("Yellow_Card", 0f);
        }

        if (!game_type_point.has("Red_Card")) {
            game_type_point.put("Red_Card", 0f);
        }

        if (!game_type_point.has("Own_goal")) {
            game_type_point.put("Own_goal", 0f);
        }

        if (!game_type_point.has("Goals_Conceded_Gk_Defender")) {
            game_type_point.put("Goals_Conceded_Gk_Defender", 0f);
        }

        if (!game_type_point.has("Penalty_Missed")) {
            game_type_point.put("Penalty_Missed", 0f);
        }

        if (!game_type_point.has("Goal_GK_Defender")) {
            game_type_point.put("Goal_GK_Defender", 0f);
        }

        if (!game_type_point.has("Goal_MidFielder")) {
            game_type_point.put("Goal_MidFielder", 0f);
        }

        return game_type_point;

    }
}
