package com.choic11.service.cricket;

import com.choic11.GlobalConstant.EntitySportsConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.choic11.model.BaseRequest;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class EntitySportsService {

    public Object getUpcomingMatchesSeries(BaseRequest baseRequest) {
        String url = EntitySportsConstant.ENTITY_CRICKET_MATCHES;
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
                                series.put("title", competition.get("title"));
                                series.put("abbr", competition.get("abbr"));
                                series.put("type", competition.get("type"));
                                series.put("season", competition.get("season"));

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
        String url = EntitySportsConstant.ENTITY_CRICKET_MATCHES;
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

                                LinkedHashMap<String, Object> series = null;
                                JSONObject competition = match.getJSONObject("competition");
                                if (!competition.isEmpty()) {
                                    series = new LinkedHashMap<String, Object>();
                                    series.put("unique_id", competition.get("cid"));
                                    series.put("title", competition.get("title"));
                                    series.put("abbr", competition.get("abbr"));
                                    series.put("type", competition.get("type"));
                                    series.put("season", competition.get("season"));
                                }

                                if (series == null || (!seriesId.equals("0")
                                        && !seriesId.equals(series.get("unique_id").toString()))) {
                                    continue;
                                }

                                LinkedHashMap<String, Object> matchR = new LinkedHashMap<String, Object>();
                                matchR.put("timestamp_start", match.get("timestamp_start"));
                                matchR.put("date", match.get("date_start"));
                                matchR.put("dateTimeGMT", match.get("date_start") + ".000Z");
                                matchR.put("matchStarted", false);
                                matchR.put("squad", true);
                                matchR.put("type", match.get("format_str"));
                                matchR.put("unique_id", match.get("match_id"));
                                matchR.put("title", match.get("title"));
                                matchR.put("short_title", match.get("short_title"));
                                matchR.put("subtitle", match.get("subtitle"));
                                matchR.put("team-1", match.getJSONObject("teama").get("name"));
                                matchR.put("team-1-short_name", match.getJSONObject("teama").get("short_name"));
                                matchR.put("team-2", match.getJSONObject("teamb").get("name"));
                                matchR.put("team-2-short_name", match.getJSONObject("teamb").get("short_name"));
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
        String url = EntitySportsConstant.ENTITY_CRICKET_MATCH_SQUAD_ROSTER;

        url = url.replace("{SERIES_ID}", seriesUniqueId);
        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("squads")) {
                            List<LinkedHashMap<String, Object>> teama_palyers = new ArrayList<LinkedHashMap<String, Object>>();
                            List<LinkedHashMap<String, Object>> teamb_palyers = new ArrayList<LinkedHashMap<String, Object>>();

                            String teama_name = "";
                            String teamb_name = "";

                            JSONArray jsonArray = response.getJSONArray("squads");

                            int teamNumber = 1;
                            for (Object squadData : jsonArray) {
                                JSONObject squad = (JSONObject) squadData;

                                JSONObject teamData = squad.getJSONObject("team");
                                JSONArray teamPlayersData = squad.getJSONArray("players");

                                if (teamNumber == 1) {
                                    teama_name = teamData.getString("title");
                                } else {
                                    teamb_name = teamData.getString("title");
                                }

                                for (Object playerData : teamPlayersData) {
                                    JSONObject player = (JSONObject) playerData;

                                    LinkedHashMap<String, Object> singlePlayerData = new LinkedHashMap<String, Object>();

                                    singlePlayerData.put("pid", player.get("pid"));
                                    singlePlayerData.put("name", player.get("title"));
                                    singlePlayerData.put("short_name", player.get("short_name"));
                                    singlePlayerData.put("battingStyle", player.get("batting_style"));
                                    singlePlayerData.put("bowlingStyle", player.get("bowling_style"));
                                    singlePlayerData.put("credits", player.get("fantasy_player_rating"));

                                    if (player.getString("playing_role").equals("bat")) {
                                        singlePlayerData.put("playingRole", "batsman");
                                    } else if (player.getString("playing_role").equals("bowl")) {
                                        singlePlayerData.put("playingRole", "bowler");
                                    } else if (player.getString("playing_role").equals("all")) {
                                        singlePlayerData.put("playingRole", "allrounder");
                                    } else if (player.getString("playing_role").equals("wk")
                                            || player.getString("playing_role").equals("wkbat")) {
                                        singlePlayerData.put("playingRole", "wicketkeeper");
                                    } else {
                                        singlePlayerData.put("playingRole", "");
                                    }

                                    String realBirthday = player.getString("birthdate");
                                    if (realBirthday == "0000-00-00") {
                                        realBirthday = "";
                                    } else {
                                        realBirthday = realBirthday + ",";
                                    }
                                    singlePlayerData.put("born", realBirthday);
                                    if (!Util.isEmpty(player.optString("nationality"))) {
                                        singlePlayerData.put("country", player.optString("nationality"));
                                    } else {
                                        singlePlayerData.put("country", "");
                                    }

                                    LinkedHashMap<String, Object> realPlayerData = new LinkedHashMap<String, Object>();
                                    realPlayerData.put("pid", singlePlayerData.get("pid"));
                                    realPlayerData.put("name", singlePlayerData.get("name"));
                                    realPlayerData.put("credits", singlePlayerData.get("credits"));
                                    realPlayerData.put("playingRole", singlePlayerData.get("playingRole"));
                                    realPlayerData.put("detail_data", singlePlayerData);


                                    if (teamNumber == 1) {
                                        teama_palyers.add(realPlayerData);
                                    } else {
                                        teamb_palyers.add(realPlayerData);
                                    }

                                }
                                teamNumber++;

                            }

                            Collections.sort(teama_palyers, new Comparator<LinkedHashMap<String, Object>>() {
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
                            });

                            Collections.sort(teamb_palyers, new Comparator<LinkedHashMap<String, Object>>() {
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
                            });

                            LinkedHashMap<String, Object> teamA = new LinkedHashMap<String, Object>();
                            teamA.put("name", teama_name);
                            teamA.put("players", teama_palyers);

                            LinkedHashMap<String, Object> teamB = new LinkedHashMap<String, Object>();
                            teamB.put("name", teamb_name);
                            teamB.put("players", teamb_palyers);

                            List<LinkedHashMap<String, Object>> squadResponseData = new ArrayList<LinkedHashMap<String, Object>>();
                            squadResponseData.add(teamB);
                            squadResponseData.add(teamA);

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
        String url = EntitySportsConstant.ENTITY_CRICKET_PLAYER_FINDER;

        url = url.replace("{PLAYER_NAME}", playerName);

        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        List<LinkedHashMap<String, Object>> playersData = new ArrayList<LinkedHashMap<String, Object>>();

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();


                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("items")) {

                            JSONArray jsonArray = response.getJSONArray("items");

                            for (Object itemsData : jsonArray) {
                                JSONObject item = (JSONObject) itemsData;

                                LinkedHashMap<String, Object> player = new LinkedHashMap<String, Object>();
                                player.put("pid", item.getInt("pid"));
                                player.put("fullName", item.optString("title", ""));
                                player.put("name", item.optString("title", ""));
                                player.put("battingStyle", item.optString("batting_style", ""));
                                player.put("bowlingStyle", item.optString("bowling_style", ""));

                                if (item.optString("playing_role", "").equals("bat")) {
                                    player.put("playingRole", "batsman");
                                } else if (item.optString("playing_role", "").equals("bowl")) {
                                    player.put("playingRole", "bowler");
                                } else if (item.optString("playing_role", "").equals("all")) {
                                    player.put("playingRole", "allrounder");
                                } else if (item.optString("playing_role", "").equals("wk")
                                        || item.optString("playing_role", "").equals("wkbat")) {
                                    player.put("playingRole", "wicketkeeper");
                                } else {
                                    player.put("playingRole", "");
                                }

                                String realBirthday = item.optString("birthdate", "0000-00-00");
                                if (realBirthday == "0000-00-00") {
                                    realBirthday = "";
                                } else {
                                    realBirthday = realBirthday + ",";
                                }
                                player.put("born", realBirthday);
                                player.put("country", item.optString("nationality", ""));

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
        String url = EntitySportsConstant.ENTITY_CRICKET_PLAYER_DETAIL;

        url = url.replace("{PLAYER_ID}", playerId);

        JSONObject jsonBody = null;
        Response executeCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        LinkedHashMap<String, Object> playerData = null;

        if (executeCurlRequest != null) {
            try {
                String responseData = executeCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("player")) {

                            JSONObject item = response.getJSONObject("player");

                            LinkedHashMap<String, Object> player = new LinkedHashMap<String, Object>();
                            player.put("pid", item.get("pid"));
                            player.put("name", item.get("title"));
                            player.put("short_name", item.get("short_name"));
                            player.put("battingStyle", item.get("batting_style"));
                            player.put("bowlingStyle", item.get("bowling_style"));

                            if (item.getString("playing_role").equals("bat")) {
                                player.put("playingRole", "batsman");
                            } else if (item.getString("playing_role").equals("bowl")) {
                                player.put("playingRole", "bowler");
                            } else if (item.getString("playing_role").equals("all")) {
                                player.put("playingRole", "allrounder");
                            } else if (item.getString("playing_role").equals("wk")
                                    || item.getString("playing_role").equals("wkbat")) {
                                player.put("playingRole", "wicketkeeper");
                            } else {
                                player.put("playingRole", "");
                            }

                            String realBirthday = item.getString("birthdate");
                            if (realBirthday.equals("0000-00-00")) {
                                realBirthday = "";
                            } else {
                                realBirthday = realBirthday + ",";
                            }
                            player.put("born", realBirthday);
                            player.put("country", item.get("nationality"));

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
        String url = EntitySportsConstant.ENTITY_CRICKET_MATCH_LINEUP;

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

                    if (jsonObject.has("response")) {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (response.has("teama")) {
                            JSONObject item = response.getJSONObject("teama");
                            JSONArray jsonArray = item.getJSONArray("squads");

                            for (Object itemSquadData : jsonArray) {
                                JSONObject squadData = (JSONObject) itemSquadData;
                                if (squadData.getBoolean("playing11")) {
                                    if (playersForMatchUniqueId == null) {
                                        players.add(Integer.parseInt(squadData.getString("player_id")));

                                        if (squadData.has("substitute") && squadData.getBoolean("substitute")) {
                                            substitute.add(Integer.parseInt(squadData.getString("player_id")));
                                        }
                                    } else {
                                        if (playersForMatchUniqueId.has(squadData.getString("player_id"))) {
                                            players.add(Integer.parseInt(squadData.getString("player_id")));

                                            if (squadData.has("substitute") && squadData.getBoolean("substitute")) {
                                                substitute.add(Integer.parseInt(squadData.getString("player_id")));
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        if (response.has("teamb")) {
                            JSONObject item = response.getJSONObject("teamb");
                            JSONArray jsonArray = item.getJSONArray("squads");

                            for (Object itemSquadData : jsonArray) {
                                JSONObject squadData = (JSONObject) itemSquadData;
                                if (squadData.getBoolean("playing11")) {
                                    if (playersForMatchUniqueId == null) {
                                        players.add(Integer.parseInt(squadData.getString("player_id")));

                                        if (squadData.has("substitute") && squadData.getBoolean("substitute")) {
                                            substitute.add(Integer.parseInt(squadData.getString("player_id")));
                                        }
                                    } else {
                                        if (playersForMatchUniqueId.has(squadData.getString("player_id"))) {
                                            players.add(Integer.parseInt(squadData.getString("player_id")));

                                            if (squadData.has("substitute") && squadData.getBoolean("substitute")) {
                                                substitute.add(Integer.parseInt(squadData.getString("player_id")));
                                            }
                                        }
                                    }

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
            Collections.sort(players, new Comparator<Integer>() {
                @Override
                public int compare(Integer valA, Integer valB) {
                    if (valA == valB) {
                        return 0;
                    } else if (valA < valB) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }

        if (!substitute.isEmpty()) {
            Collections.sort(substitute, new Comparator<Integer>() {
                @Override
                public int compare(Integer valA, Integer valB) {
                    if (valA == valB) {
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

        JSONObject api_data_array_short = null;
        JSONObject api_data_array_full = null;
        JSONArray api_data_array_full_innings = new JSONArray();

        String url = EntitySportsConstant.ENTITY_CRICKET_MATCH_SCOREBOARD_LIVE;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);
        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {
                    if (jsonObject.has("response")) {
                        api_data_array_short = jsonObject.getJSONObject("response");
                    }
                }
            } catch (Exception e) {

            }
        }

        url = EntitySportsConstant.ENTITY_CRICKET_MATCH_SCOREBOARD;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBodyFull = null;
        Response excuteCurlRequestFull = Curl.excuteCurlRequest(url, "GET", jsonBodyFull, null);
        if (excuteCurlRequestFull != null) {
            try {
                String responseData = excuteCurlRequestFull.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.getString("status").equals("ok")) {
                    if (jsonObject.has("response")) {
                        api_data_array_full = jsonObject.getJSONObject("response");

                        if (api_data_array_full.has("innings")) {
                            api_data_array_full_innings = api_data_array_full.getJSONArray("innings");
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        if (api_data_array_full != null) {
            String team1_id = "0";
            String team1_name = "0";
            String team1_short_name = "0";
            String team1_run = "0";
            String team1_wicket = "0";
            String team1_overs = "0";

            String team2_id = "0";
            String team2_name = "0";
            String team2_short_name = "0";
            String team2_run = "0";
            String team2_wicket = "0";
            String team2_overs = "0";

            String score_board_notes = "";

            score_board_notes = api_data_array_full.getString("status_note");

            if (api_data_array_full.has("teama")) {
                team1_id = api_data_array_full.getJSONObject("teama").get("team_id").toString();
                team1_name = api_data_array_full.getJSONObject("teama").getString("name");
                team1_short_name = api_data_array_full.getJSONObject("teama").getString("short_name");

                if (match != null) {
                    if (team1_id.equals(match.get("team1Id").toString())) {
                        team1_name = match.get("fullName").toString();
                        team1_short_name = match.get("sortName").toString();
                    } else if (team1_id.equals(match.get("team2Id").toString())) {
                        team1_name = match.get("fullName2").toString();
                        team1_short_name = match.get("sortName2").toString();
                    }
                }

                if (api_data_array_full.getJSONObject("teama").has("scores_full")) {
                    String scores_full = api_data_array_full.getJSONObject("teama").getString("scores_full");
                    if (!Util.isEmpty(scores_full)) {
                        String[] scores_full_array = scores_full.split("/");
                        String lastIndex = scores_full_array[scores_full_array.length - 1];

                        String[] last_index_array = StringUtils.split(lastIndex, "(");

                        team1_run = scores_full.replace("/" + lastIndex, "");
                        team1_wicket = last_index_array[0].trim();
                        team1_overs = last_index_array[1].trim().split(" ")[0];

                    }
                }
            }

            if (api_data_array_full.has("teamb")) {
                team2_id = api_data_array_full.getJSONObject("teamb").get("team_id").toString();
                team2_name = api_data_array_full.getJSONObject("teamb").getString("name");
                team2_short_name = api_data_array_full.getJSONObject("teamb").getString("short_name");

                if (match != null) {
                    if (team2_id.equals(match.get("team1Id").toString())) {
                        team2_name = match.get("fullName").toString();
                        team2_short_name = match.get("sortName").toString();
                    } else if (team2_id.equals(match.get("team2Id").toString())) {
                        team2_name = match.get("fullName2").toString();
                        team2_short_name = match.get("sortName2").toString();
                    }
                }

                if (api_data_array_full.getJSONObject("teamb").has("scores_full")) {
                    String scores_full = api_data_array_full.getJSONObject("teamb").getString("scores_full");
                    if (!Util.isEmpty(scores_full)) {
                        String[] scores_full_array = scores_full.split("/");
                        String lastIndex = scores_full_array[scores_full_array.length - 1];

                        String[] last_index_array = StringUtils.split(lastIndex, "(");

                        team2_run = scores_full.replace("/" + lastIndex, "");
                        team2_wicket = last_index_array[0].trim();
                        team2_overs = last_index_array[1].trim().split(" ")[0];

                    }
                }
            }

            JSONObject scorecardData = new JSONObject();

            scorecardData.put("team1_id", team1_id);
            scorecardData.put("team1_name", team1_name);
            scorecardData.put("team1_short_name", team1_short_name);
            scorecardData.put("team1_run", team1_run);
            scorecardData.put("team1_wicket", team1_wicket);
            scorecardData.put("team1_overs", team1_overs);

            scorecardData.put("team2_id", team2_id);
            scorecardData.put("team2_name", team2_name);
            scorecardData.put("team2_short_name", team2_short_name);
            scorecardData.put("team2_run", team2_run);
            scorecardData.put("team2_wicket", team2_wicket);
            scorecardData.put("team2_overs", team2_overs);

            scorecardData.put("score_board_notes", score_board_notes);

            JSONObject output = new JSONObject();
            output.put("api_data_array_full", api_data_array_full_innings);

            if (api_data_array_full_innings.length() > 0) {

                JSONArray updatedInnings = new JSONArray();

                for (Object inningData : api_data_array_full_innings) {
                    JSONObject singleInning = (JSONObject) inningData;

                    String batting_team_id = singleInning.get("batting_team_id").toString();
                    JSONObject team_data = null;
                    if (batting_team_id.equals(team1_id)) {
                        team_data = new JSONObject();
                        team_data.put("unique_id", team1_id);
                        team_data.put("name", team1_name);
                        team_data.put("sort_name", team1_short_name);
                    } else if (batting_team_id.equals(team2_id)) {
                        team_data = new JSONObject();
                        team_data.put("unique_id", team2_id);
                        team_data.put("name", team2_name);
                        team_data.put("sort_name", team2_short_name);
                    }

                    singleInning.put("app_team_data", team_data);

                    updatedInnings.put(singleInning);

                }

                output.put("api_data_array_full", updatedInnings);

            }

            JSONObject short_score_data = new JSONObject();
            short_score_data.put("batsmen", new JSONArray());
            short_score_data.put("bowlers", new JSONArray());
            short_score_data.put("running_over_balls", new JSONArray());
            short_score_data.put("running_over_bowler_id", "0");
            short_score_data.put("running_over_batsman_id", "0");

            if (api_data_array_short != null && api_data_array_short.has("batsmen")
                    && api_data_array_short.getJSONArray("batsmen").length() > 0) {
                short_score_data.put("batsmen", api_data_array_short.getJSONArray("batsmen"));
                short_score_data.put("bowlers", api_data_array_short.getJSONArray("bowlers"));

                if (api_data_array_short.has("commentaries")) {
                    JSONArray commentariesData = api_data_array_short.getJSONArray("commentaries");

                    int arrayCount = commentariesData.length();

                    JSONArray running_over_balls = new JSONArray();

                    if (arrayCount > 0) {
                        for (int i = commentariesData.length() - 1; i >= 0; i--) {
                            JSONObject event = commentariesData.getJSONObject(i);

                            if (event.getString("event").equals("overend") && i != (commentariesData.length() - 1)) {
                                break;
                            }

                            if (!event.getString("event").equals("overend")) {
                                running_over_balls.put(event);
                            }
                        }

                        if (running_over_balls.length() > 0) {
                            short_score_data.put("running_over_bowler_id",
                                    running_over_balls.getJSONObject(0).get("bowler_id").toString());
                            short_score_data.put("running_over_batsman_id",
                                    running_over_balls.getJSONObject(0).get("batsman_id").toString());
                        }
                    }

                    short_score_data.put("running_over_balls", running_over_balls);

                }
            } else {

                JSONArray innings = output.getJSONArray("api_data_array_full");

                if (innings.length() > 0) {

                    JSONArray batesman = innings.getJSONObject(innings.length() - 1).getJSONArray("batsmen");
                    JSONArray currentBats = new JSONArray();
                    for (Object iterable_element : batesman) {
                        JSONObject api_data_array_full_batsmen = (JSONObject) iterable_element;

                        if (api_data_array_full_batsmen.getString("how_out").equals("Not out")) {
                            currentBats.put(api_data_array_full_batsmen);
                        }

                        if (currentBats.length() == 2) {
                            break;
                        }
                    }
                    short_score_data.put("batsmen", currentBats);

                }

            }

            output.put("api_data_array_short", short_score_data);
            output.put("scorecard_data", scorecardData);

            return output;
        }

        return null;
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

        String url = EntitySportsConstant.ENTITY_CRICKET_MATCH_SCOREBOARD;

        url = url.replace("{MATCH_ID}", matchUniqueId);

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(url, "GET", jsonBody, null);

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                JSONObject jsonObject = new JSONObject(responseData);

                output.put("api_response", jsonObject);
                if (jsonObject.getString("status").equals("ok")) {
                    if (jsonObject.has("response")) {
                        output.put("api_response", api_response);

                        JSONObject api_data_array = jsonObject.getJSONObject("response");

                        String team1_run = "0";
                        String team1_wicket = "0";
                        String team1_overs = "0";

                        String team2_run = "0";
                        String team2_wicket = "0";
                        String team2_overs = "0";

                        String score_board_notes = "";

                        score_board_notes = api_data_array.getString("status_note");

                        if (api_data_array.has("teama")) {
                            if (api_data_array.getJSONObject("teama").has("scores_full")) {
                                String scores_full = api_data_array.getJSONObject("teama").getString("scores_full");
                                if (!Util.isEmpty(scores_full)) {
                                    String[] scores_full_array = scores_full.split("/");
                                    String lastIndex = scores_full_array[scores_full_array.length - 1];
                                    String[] last_index_array = StringUtils.split(lastIndex, "(");

                                    team1_run = scores_full.replace("/" + lastIndex, "");
                                    team1_wicket = last_index_array[0].trim();
                                    team1_overs = last_index_array[1].trim().split(" ")[0];

                                }
                            }
                        }

                        if (api_data_array.has("teamb")) {
                            if (api_data_array.getJSONObject("teamb").has("scores_full")) {
                                String scores_full = api_data_array.getJSONObject("teamb").getString("scores_full");
                                if (!Util.isEmpty(scores_full)) {
                                    String[] scores_full_array = scores_full.split("/");
                                    String lastIndex = scores_full_array[scores_full_array.length - 1];
                                    String[] last_index_array = StringUtils.split(lastIndex, "(");

                                    team2_run = scores_full.replace("/" + lastIndex, "");
                                    team2_wicket = last_index_array[0].trim();
                                    team2_overs = last_index_array[1].trim().split(" ")[0];

                                }
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
                        if (api_data_array.has("players") && api_data_array.getJSONArray("players").length() > 0
                                && game_type_point != null) {

                            for (Object iterable_element : api_data_array.getJSONArray("players")) {
                                JSONObject playing_players = (JSONObject) iterable_element;
                                if (playing_players.getString("role").equals("squad")) {
                                    continue;
                                }

                                JSONObject playerData = new JSONObject();
                                playerData.put("Being_Part_Of_Eleven",
                                        game_type_point.getFloat("Being_Part_Of_Eleven"));
                                playerData.put("Every_Run_Scored", 0f);
                                playerData.put("Dismiss_For_A_Duck", 0f);
                                playerData.put("Every_Boundary_Hit", 0f);
                                playerData.put("Every_Six_Hit", 0f);
                                playerData.put("Half_Century", 0f);
                                playerData.put("Century", 0f);
                                playerData.put("Thirty_Runs", 0f);
                                playerData.put("Wicket", 0f);
                                playerData.put("Maiden_Over", 0f);
                                playerData.put("Four_Wicket", 0f);
                                playerData.put("Five_Wicket", 0f);
                                playerData.put("Three_Wicket", 0f);
                                playerData.put("Two_Wicket", 0f);
                                playerData.put("Catch", 0f);
                                playerData.put("Three_Catch", 0f);
                                playerData.put("Lbw_Bowled", 0f);
                                playerData.put("Catch_And_Bowled", 0f);
                                playerData.put("Stumping", 0f);
                                playerData.put("Run_Out", 0f);
                                playerData.put("Run_Out_Catcher", 0f);
                                playerData.put("Run_Out_Thrower", 0f);
                                playerData.put("Strike_Rate", 0f);
                                playerData.put("Economy_Rate", 0f);
                                playerData.put("total_points", game_type_point.getFloat("Being_Part_Of_Eleven"));

                                playerData.put("Being_Part_Of_Eleven_Value", 1f);
                                playerData.put("Every_Run_Scored_Value", 0f);
                                playerData.put("Dismiss_For_A_Duck_Value", 0f);
                                playerData.put("Every_Boundary_Hit_Value", 0f);
                                playerData.put("Every_Six_Hit_Value", 0f);
                                playerData.put("Half_Century_Value", 0f);
                                playerData.put("Century_Value", 0f);
                                playerData.put("Thirty_Runs_Value", 0f);

                                playerData.put("Wicket_Value", 0f);
                                playerData.put("Maiden_Over_Value", 0f);
                                playerData.put("Four_Wicket_Value", 0f);
                                playerData.put("Five_Wicket_Value", 0f);
                                playerData.put("Three_Wicket_Value", 0f);
                                playerData.put("Two_Wicket_Value", 0f);

                                playerData.put("Catch_Value", 0f);
                                playerData.put("Three_Catch_Value", 0f);
                                playerData.put("Lbw_Bowled_Value", 0f);
                                playerData.put("Catch_And_Bowled_Value", 0f);
                                playerData.put("Stumping_Value", 0f);
                                playerData.put("Run_Out_Value", 0f);
                                playerData.put("Run_Out_Catcher_Value", 0f);
                                playerData.put("Run_Out_Thrower_Value", 0f);
                                playerData.put("Strike_Rate_Value", "");
                                playerData.put("Economy_Rate_Value", "");

                                players.put(playing_players.get("pid").toString(), playerData);

                            }

                            HashSet<String> strikerate_ignore_position_array = new HashSet<String>();
                            HashSet<String> duck_ignore_position_array = new HashSet<String>();
                            strikerate_ignore_position_array.add("bowler");
                            duck_ignore_position_array.add("bowler");

                            if (api_data_array.has("innings") && api_data_array.getJSONArray("innings").length() > 0) {

                                JSONArray innings = api_data_array.getJSONArray("innings");

                                for (Object inningsD : innings) {
                                    JSONObject innigsdarta = (JSONObject) inningsD;

                                    int inningsNumber = innigsdarta.getInt("number");

                                    if (innigsdarta.has("batsmen")
                                            && innigsdarta.getJSONArray("batsmen").length() > 0) {
                                        for (Object batting_scoresD : innigsdarta.getJSONArray("batsmen")) {
                                            JSONObject scores = (JSONObject) batting_scoresD;
                                            if (scores.optString("batsman_id", "").trim().isEmpty()
                                                    || !players.has(scores.optString("batsman_id", ""))) {
                                                continue;
                                            }

                                            if (!scores.has("sixes")
                                                    || scores.get("sixes").toString().trim().equals("")) {
                                                scores.put("sixes", 0);
                                            }

                                            if (!scores.has("fours")
                                                    || scores.get("fours").toString().trim().equals("")) {
                                                scores.put("fours", 0);
                                            }

                                            if (!scores.has("runs")
                                                    || scores.get("runs").toString().trim().equals("")) {
                                                scores.put("runs", 0);
                                            }

                                            if (!scores.has("strike_rate")
                                                    || scores.get("strike_rate").toString().trim().equals("")) {
                                                scores.put("strike_rate", 0);
                                            }

                                            if (!scores.has("balls_faced")
                                                    || scores.get("balls_faced").toString().trim().equals("")) {
                                                scores.put("balls_faced", 0);
                                            }

                                            float Every_Six_Hit_Points = game_type_point.getFloat("Every_Six_Hit")
                                                    * scores.getInt("sixes");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Six_Hit", Every_Six_Hit_Points);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Every_Six_Hit_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("sixes"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Six_Hit");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Six_Hit_Value");

                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Six_Hit", previousPoints + Every_Six_Hit_Points);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Six_Hit_Value", previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("sixes"))));

                                            }

                                            float Every_Boundary_Hit_Points = game_type_point
                                                    .getFloat("Every_Boundary_Hit") * scores.getInt("fours");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Boundary_Hit", Every_Boundary_Hit_Points);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Every_Boundary_Hit_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("fours"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Boundary_Hit");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Boundary_Hit_Value");

                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Every_Boundary_Hit",
                                                        previousPoints + Every_Boundary_Hit_Points);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Boundary_Hit_Value", previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("fours"))));

                                            }

                                            float Every_Run_Scored_Points = game_type_point.getFloat("Every_Run_Scored")
                                                    * scores.getInt("runs");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Run_Scored", Every_Run_Scored_Points);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Every_Run_Scored_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("runs"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Run_Scored");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Every_Run_Scored_Value");

                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Every_Run_Scored", previousPoints + Every_Run_Scored_Points);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Every_Run_Scored_Value", previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("runs"))));

                                            }

                                            float Strike_Rate_Points = 0f;

                                            if (match_players_array.has(scores.optString("batsman_id", ""))
                                                    && !strikerate_ignore_position_array.contains(match_players_array
                                                    .getJSONObject(scores.optString("batsman_id", ""))
                                                    .getString("playingRole"))) {

                                                if (scores.getInt("balls_faced") >= game_type_point
                                                        .getInt("Minimum_Balls_for_Strike_Rate")) {
                                                    JSONArray Strike_Rate_decode = game_type_point
                                                            .getJSONArray("Strike_Rate");

                                                    for (Object data : Strike_Rate_decode) {
                                                        JSONObject Strike_Rate_decode_value = (JSONObject) data;
                                                        if (scores.getFloat("strike_rate") >= Strike_Rate_decode_value
                                                                .getFloat("min")
                                                                && scores.getFloat(
                                                                "strike_rate") <= Strike_Rate_decode_value
                                                                .getFloat("max")) {
                                                            Strike_Rate_Points = Strike_Rate_decode_value
                                                                    .getFloat("val");
                                                            break;
                                                        }

                                                    }

                                                }

                                            }

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("batsman_id")).put("Strike_Rate",
                                                        Strike_Rate_Points);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Strike_Rate_Value",
                                                        String.valueOf(scores.getFloat("strike_rate")));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Strike_Rate");
                                                String previousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getString("Strike_Rate_Value");

                                                players.getJSONObject(scores.getString("batsman_id")).put("Strike_Rate",
                                                        previousPoints + Strike_Rate_Points);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Strike_Rate_Value",
                                                        previousValue + String.valueOf(scores.getFloat("strike_rate")));

                                            }

                                            float Century_Points = 0f;
                                            float Half_Century_Points = 0f;
                                            float Thirty_Runs_Points = 0f;

                                            if (inningsNumber <= 2) {

                                                int no_of_century = (int) scores.getInt("runs") / 100;

                                                float Century_Points_I = 2 * game_type_point.getFloat("Half_Century")
                                                        * no_of_century;

                                                players.getJSONObject(scores.getString("batsman_id")).put("Century",
                                                        Century_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Century_Value",
                                                        Float.parseFloat(String.valueOf(no_of_century)));

                                                if (game_type_point.getFloat("Half_Century") <= 0) {
                                                    no_of_century = 0;
                                                }

                                                int no_of_fifty = (int) scores.getInt("runs") / 50;
                                                if (no_of_century > 0) {
                                                    no_of_fifty = 0;
                                                }

                                                float Half_Century_Points_I = game_type_point.getFloat("Half_Century")
                                                        * no_of_fifty;

                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Half_Century", Half_Century_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Half_Century_Value",
                                                        Float.parseFloat(String.valueOf(no_of_fifty)));

                                                if (game_type_point.getFloat("Half_Century") <= 0) {
                                                    no_of_fifty = 0;
                                                }

                                                int no_of_thirty_runs = (int) scores.getInt("runs") / 30;
                                                if (no_of_century > 0 || no_of_fifty > 0) {
                                                    no_of_thirty_runs = 0;
                                                }

                                                float Thirty_Runs_Points_I = game_type_point.getFloat("Thirty_Runs")
                                                        * no_of_thirty_runs;

                                                players.getJSONObject(scores.getString("batsman_id")).put("Thirty_Runs",
                                                        Thirty_Runs_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id")).put(
                                                        "Thirty_Runs_Value",
                                                        Float.parseFloat(String.valueOf(no_of_thirty_runs)));

                                                if (game_type_point.getFloat("Thirty_Runs") <= 0) {
                                                    no_of_thirty_runs = 0;
                                                }

                                                Century_Points = 2 * game_type_point.getFloat("Half_Century")
                                                        * no_of_century;
                                                Half_Century_Points = game_type_point.getFloat("Half_Century")
                                                        * no_of_fifty;
                                                Thirty_Runs_Points = game_type_point.getFloat("Thirty_Runs")
                                                        * no_of_thirty_runs;

                                            } else {

                                                int no_of_century = (int) scores.getInt("runs") / 100;

                                                float Century_Points_I = 2 * game_type_point.getFloat("Half_Century")
                                                        * no_of_century;

                                                float cpreviousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Century");
                                                float cpreviousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Century_Value");

                                                players.getJSONObject(scores.getString("batsman_id")).put("Century",
                                                        cpreviousPoints + Century_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Century_Value", cpreviousValue
                                                                + Float.parseFloat(String.valueOf(no_of_century)));

                                                if (game_type_point.getFloat("Half_Century") <= 0) {
                                                    no_of_century = 0;
                                                }

                                                int no_of_fifty = (int) scores.getInt("runs") / 50;
                                                if (no_of_century > 0) {
                                                    no_of_fifty = 0;
                                                }

                                                float Half_Century_Points_I = game_type_point.getFloat("Half_Century")
                                                        * no_of_fifty;

                                                float hcpreviousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Half_Century");
                                                float hcpreviousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Half_Century_Value");

                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Half_Century", hcpreviousPoints + Half_Century_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Half_Century_Value", hcpreviousValue
                                                                + Float.parseFloat(String.valueOf(no_of_fifty)));

                                                if (game_type_point.getFloat("Half_Century") <= 0) {
                                                    no_of_fifty = 0;
                                                }

                                                int no_of_thirty_runs = (int) scores.getInt("runs") / 30;
                                                if (no_of_century > 0 || no_of_fifty > 0) {
                                                    no_of_thirty_runs = 0;
                                                }

                                                float Thirty_Runs_Points_I = game_type_point.getFloat("Thirty_Runs")
                                                        * no_of_thirty_runs;

                                                float trpreviousPoints = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Thirty_Runs");
                                                float trpreviousValue = players
                                                        .getJSONObject(scores.getString("batsman_id"))
                                                        .getFloat("Thirty_Runs_Value");

                                                players.getJSONObject(scores.getString("batsman_id")).put("Thirty_Runs",
                                                        trpreviousPoints + Thirty_Runs_Points_I);
                                                players.getJSONObject(scores.getString("batsman_id"))
                                                        .put("Thirty_Runs_Value", trpreviousValue
                                                                + Float.parseFloat(String.valueOf(no_of_thirty_runs)));

                                                if (game_type_point.getFloat("Thirty_Runs") <= 0) {
                                                    no_of_thirty_runs = 0;
                                                }

                                                Century_Points = 2 * game_type_point.getFloat("Half_Century")
                                                        * no_of_century;
                                                Half_Century_Points = game_type_point.getFloat("Half_Century")
                                                        * no_of_fifty;
                                                Thirty_Runs_Points = game_type_point.getFloat("Thirty_Runs")
                                                        * no_of_thirty_runs;

                                            }

                                            float Dismiss_For_A_Duck_Points = 0;

                                            if (inningsNumber <= 2) {
                                                if (scores.getInt("runs") == 0
                                                        && !scores.optString("dismissal", "").trim().isEmpty()) {
                                                    players.getJSONObject(scores.getString("batsman_id"))
                                                            .put("Dismiss_For_A_Duck_Value", 1f);
                                                    if (match_players_array.has(scores.optString("batsman_id", ""))
                                                            && !duck_ignore_position_array.contains(match_players_array
                                                            .getJSONObject(scores.optString("batsman_id", ""))
                                                            .getString("playingRole"))) {

                                                    Dismiss_For_A_Duck_Points = game_type_point
                                                            .getFloat("Dismiss_For_A_Duck");

                                                    players.getJSONObject(scores.getString("batsman_id"))
                                                            .put("Dismiss_For_A_Duck", Dismiss_For_A_Duck_Points);
                                                    }else{
                                                        Dismiss_For_A_Duck_Points = 0;
                                                    players.getJSONObject(scores.getString("batsman_id"))
                                                                .put("Dismiss_For_A_Duck", Dismiss_For_A_Duck_Points);
                                                    }
                                                }

                                            } else {
                                                if (scores.getInt("runs") == 0
                                                        && !scores.optString("dismissal", "").trim().isEmpty()) {
                                                    players.getJSONObject(scores.getString("batsman_id"))
                                                            .put("Dismiss_For_A_Duck_Value", 1f);
                                                    if (match_players_array.has(scores.optString("batsman_id", ""))
                                                            && !duck_ignore_position_array.contains(match_players_array
                                                            .getJSONObject(scores.optString("batsman_id", ""))
                                                            .getString("playingRole"))) {
                                                    float previousPoints = players
                                                            .getJSONObject(scores.getString("batsman_id"))
                                                            .getFloat("Dismiss_For_A_Duck");

                                                    Dismiss_For_A_Duck_Points = game_type_point
                                                            .getFloat("Dismiss_For_A_Duck");

                                                    players.getJSONObject(scores.getString("batsman_id")).put(
                                                            "Dismiss_For_A_Duck",
                                                            previousPoints + Dismiss_For_A_Duck_Points);
                                                    }else{
                                                        float previousPoints = 0;

                                                        Dismiss_For_A_Duck_Points = 0;

                                                        players.getJSONObject(scores.getString("batsman_id")).put(
                                                                "Dismiss_For_A_Duck",
                                                                previousPoints + Dismiss_For_A_Duck_Points);
                                                    }

                                                }
                                            }

                                            float previousPoints = players.getJSONObject(scores.getString("batsman_id"))
                                                    .getFloat("total_points");

                                            players.getJSONObject(scores.getString("batsman_id")).put("total_points",
                                                    previousPoints + Every_Six_Hit_Points + Every_Boundary_Hit_Points
                                                            + Every_Run_Scored_Points + Century_Points
                                                            + Half_Century_Points + Dismiss_For_A_Duck_Points
                                                            + Strike_Rate_Points + Thirty_Runs_Points);

                                        }

                                    }

                                    if (innigsdarta.has("bowlers")
                                            && innigsdarta.getJSONArray("bowlers").length() > 0) {
                                        for (Object bowling_scores : innigsdarta.getJSONArray("bowlers")) {
                                            JSONObject scores = (JSONObject) bowling_scores;
                                            if (scores.optString("bowler_id", "").trim().isEmpty()
                                                    || !players.has(scores.optString("bowler_id", ""))) {
                                                continue;
                                            }

                                            if (!scores.has("overs")
                                                    || scores.get("overs").toString().trim().equals("")) {
                                                scores.put("overs", 0f);
                                            }
                                            if (!scores.has("wickets")
                                                    || scores.get("wickets").toString().trim().equals("")) {
                                                scores.put("wickets", 0);
                                            }
                                            if (!scores.has("maidens")
                                                    || scores.get("maidens").toString().trim().equals("")) {
                                                scores.put("maidens", 0);
                                            }
                                            if (!scores.has("econ")
                                                    || scores.get("econ").toString().trim().equals("")) {
                                                scores.put("econ", 0f);
                                            }
                                            if (!scores.has("bowledcount")
                                                    || scores.get("bowledcount").toString().trim().equals("")) {
                                                scores.put("bowledcount", 0);
                                            }
                                            if (!scores.has("lbwcount")
                                                    || scores.get("lbwcount").toString().trim().equals("")) {
                                                scores.put("lbwcount", 0);
                                            }

                                            float Wicket_Points = game_type_point.getFloat("Wicket")
                                                    * scores.getInt("wickets");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Wicket",
                                                        Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put("Wicket_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("wickets"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Wicket");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Wicket_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Wicket",
                                                        previousPoints + Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put("Wicket_Value",
                                                        previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("wickets"))));

                                            }

                                            int lbwBowledCount = scores.getInt("bowledcount") + scores.getInt("lbwcount");
                                            float Lbw_Bowled_Points = game_type_point.getFloat("Lbw_Bowled")
                                                    * lbwBowledCount;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Lbw_Bowled",
                                                        Lbw_Bowled_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put("Lbw_Bowled_Value",
                                                        Float.parseFloat(String.valueOf(lbwBowledCount)));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Lbw_Bowled");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Lbw_Bowled_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Lbw_Bowled",
                                                        previousPoints + Lbw_Bowled_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put("Lbw_Bowled_Value",
                                                        previousValue + Float
                                                                .parseFloat(String.valueOf(lbwBowledCount)));

                                            }

                                            float Maiden_Over_Points = game_type_point.getFloat("Maiden_Over")
                                                    * scores.getInt("maidens");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Maiden_Over",
                                                        Maiden_Over_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Maiden_Over_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("maidens"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Maiden_Over");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Maiden_Over_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Maiden_Over",
                                                        previousPoints + Maiden_Over_Points);
                                                players.getJSONObject(scores.getString("bowler_id"))
                                                        .put("Maiden_Over_Value", previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("maidens"))));

                                            }

                                            int no_of_5w = (int) scores.getInt("wickets") / 5;

                                            float Five_Wicket_Points = game_type_point.getFloat("Five_Wicket")
                                                    * no_of_5w;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Five_Wicket",
                                                        Five_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Five_Wicket_Value",
                                                        Float.parseFloat(String.valueOf(no_of_5w)));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Five_Wicket");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Five_Wicket_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Five_Wicket",
                                                        previousPoints + Five_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Five_Wicket_Value",
                                                        previousValue + Float.parseFloat(String.valueOf(no_of_5w)));

                                            }

                                            if (game_type_point.getFloat("Five_Wicket") <= 0) {
                                                no_of_5w = 0;
                                            }

                                            int no_of_4w = (int) scores.getInt("wickets") / 4;

                                            if (no_of_5w > 0) {
                                                no_of_4w = 0;
                                            }

                                            float Four_Wicket_Points = game_type_point.getFloat("Four_Wicket")
                                                    * no_of_4w;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Four_Wicket",
                                                        Four_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Four_Wicket_Value",
                                                        Float.parseFloat(String.valueOf(no_of_4w)));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Four_Wicket");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Four_Wicket_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Four_Wicket",
                                                        previousPoints + Four_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Four_Wicket_Value",
                                                        previousValue + Float.parseFloat(String.valueOf(no_of_4w)));

                                            }

                                            if (game_type_point.getFloat("Four_Wicket") <= 0) {
                                                no_of_4w = 0;
                                            }

                                            int no_of_3w = (int) scores.getInt("wickets") / 3;

                                            if (no_of_5w > 0 || no_of_4w > 0) {
                                                no_of_3w = 0;
                                            }

                                            float Three_Wicket_Points = game_type_point.getFloat("Three_Wicket")
                                                    * no_of_3w;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Three_Wicket",
                                                        Three_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Three_Wicket_Value",
                                                        Float.parseFloat(String.valueOf(no_of_3w)));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Three_Wicket");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Three_Wicket_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Three_Wicket",
                                                        previousPoints + Three_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Three_Wicket_Value",
                                                        previousValue + Float.parseFloat(String.valueOf(no_of_3w)));

                                            }

                                            if (game_type_point.getFloat("Three_Wicket") <= 0) {
                                                no_of_3w = 0;
                                            }

                                            int no_of_2w = (int) scores.getInt("wickets") / 2;

                                            if (no_of_5w > 0 || no_of_4w > 0 || no_of_3w > 0) {
                                                no_of_2w = 0;
                                            }

                                            float Two_Wicket_Points = game_type_point.getFloat("Two_Wicket") * no_of_2w;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Two_Wicket",
                                                        Two_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Two_Wicket_Value", Float.parseFloat(String.valueOf(no_of_2w)));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Two_Wicket");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Two_Wicket_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Two_Wicket",
                                                        previousPoints + Two_Wicket_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Two_Wicket_Value",
                                                        previousValue + Float.parseFloat(String.valueOf(no_of_2w)));

                                            }

                                            if (game_type_point.getFloat("Two_Wicket") <= 0) {
                                                no_of_2w = 0;
                                            }

                                            JSONArray jsonArray = game_type_point.getJSONArray("Economy_Rate");
                                            float Economy_Rate_Points = 0f;

                                            if (scores.getFloat("overs") >= Float.parseFloat(String.valueOf(
                                                    game_type_point.getInt("Minimum_Overs_for_Economy_Rate")))) {

                                                for (Object Economy_RateD : jsonArray) {
                                                    JSONObject Economy_Rate_decode_value = (JSONObject) Economy_RateD;

                                                    if (scores.getFloat("econ") >= Economy_Rate_decode_value
                                                            .getFloat("min")) {
                                                        Economy_Rate_Points = Economy_Rate_decode_value.getFloat("val");

                                                    }

                                                }

                                            }

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("bowler_id")).put("Economy_Rate",
                                                        Economy_Rate_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Economy_Rate_Value", String.valueOf(scores.getFloat("econ")));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getFloat("Economy_Rate");
                                                String previousValue = players
                                                        .getJSONObject(scores.getString("bowler_id"))
                                                        .getString("Economy_Rate_Value");

                                                players.getJSONObject(scores.getString("bowler_id")).put("Economy_Rate",
                                                        previousPoints + Economy_Rate_Points);
                                                players.getJSONObject(scores.getString("bowler_id")).put(
                                                        "Economy_Rate_Value",
                                                        previousValue + String.valueOf(scores.getFloat("econ")));

                                            }

                                            float previousPoints = players.getJSONObject(scores.getString("bowler_id"))
                                                    .getFloat("total_points");

                                            players.getJSONObject(scores.getString("bowler_id")).put("total_points",
                                                    previousPoints + Wicket_Points + Lbw_Bowled_Points + Maiden_Over_Points
                                                            + Five_Wicket_Points + Four_Wicket_Points
                                                            + Economy_Rate_Points + Three_Wicket_Points
                                                            + Two_Wicket_Points);

                                        }
                                    }

                                    if (innigsdarta.has("fielder")
                                            && innigsdarta.getJSONArray("fielder").length() > 0) {
                                        for (Object fielding_scores : innigsdarta.getJSONArray("fielder")) {
                                            JSONObject scores = (JSONObject) fielding_scores;
                                            if (scores.optString("fielder_id", "").trim().isEmpty()
                                                    || !players.has(scores.optString("fielder_id", ""))) {
                                                continue;
                                            }

                                            if (!scores.has("catches")
                                                    || scores.get("catches").toString().trim().equals("")) {
                                                scores.put("catches", 0);
                                            }
                                            if (!scores.has("stumping")
                                                    || scores.get("stumping").toString().trim().equals("")) {
                                                scores.put("stumping", 0);
                                            }
                                            if (!scores.has("runout_direct_hit")
                                                    || scores.get("runout_direct_hit").toString().trim().equals("")) {
                                                scores.put("runout_direct_hit", 0);
                                            }
                                            if (!scores.has("runout_catcher")
                                                    || scores.get("runout_catcher").toString().trim().equals("")) {
                                                scores.put("runout_catcher", 0);
                                            }
                                            if (!scores.has("runout_thrower")
                                                    || scores.get("runout_thrower").toString().trim().equals("")) {
                                                scores.put("runout_thrower", 0);
                                            }

                                            int totalCatches = scores.getInt("catches");
                                            int threeCatchBonus = (int) totalCatches / 3;

                                            float Catch_Points = game_type_point.getFloat("Catch")
                                                    * scores.getInt("catches");

                                            float Three_Catch_Points = game_type_point.getFloat("Three_Catch")
                                                    * threeCatchBonus;

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("fielder_id")).put("Catch",
                                                        Catch_Points);
                                                players.getJSONObject(scores.getString("fielder_id")).put("Catch_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("catches"))));

                                                players.getJSONObject(scores.getString("fielder_id")).put("Three_Catch",
                                                        Three_Catch_Points);
                                                players.getJSONObject(scores.getString("fielder_id")).put("Three_Catch_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("catches"))));


                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Catch");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Catch_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put("Catch",
                                                        previousPoints + Catch_Points);
                                                players.getJSONObject(scores.getString("fielder_id")).put("Catch_Value",
                                                        previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("catches"))));


                                                float previousPointsThreeCatch = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Three_Catch");
                                                float previousValueThreeCatch = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Three_Catch_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put("Three_Catch",
                                                        previousPointsThreeCatch + Three_Catch_Points);
                                                players.getJSONObject(scores.getString("fielder_id")).put("Three_Catch_Value",
                                                        previousValueThreeCatch + Float
                                                                .parseFloat(String.valueOf(scores.getInt("catches"))));

                                            }

                                            float Stumping_Points = game_type_point.getFloat("Stumping")
                                                    * scores.getInt("stumping");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("fielder_id")).put("Stumping",
                                                        Stumping_Points);
                                                players.getJSONObject(scores.getString("fielder_id")).put(
                                                        "Stumping_Value",
                                                        Float.parseFloat(String.valueOf(scores.getInt("stumping"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Stumping");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Stumping_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put("Stumping",
                                                        previousPoints + Stumping_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Stumping_Value", previousValue + Float
                                                                .parseFloat(String.valueOf(scores.getInt("stumping"))));

                                            }

                                            float Run_Out_Points = game_type_point.getFloat("Run_Out")
                                                    * scores.getInt("runout_direct_hit");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("fielder_id")).put("Run_Out",
                                                        Run_Out_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Value", Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_direct_hit"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put("Run_Out",
                                                        previousPoints + Run_Out_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Value", previousValue + Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_direct_hit"))));

                                            }

                                            float Run_Out_Catcher_Points = game_type_point.getFloat("Run_Out_Catcher")
                                                    * scores.getInt("runout_catcher");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Catcher", Run_Out_Catcher_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Catcher_Value", Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_catcher"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out_Catcher");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out_Catcher_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put(
                                                        "Run_Out_Catcher", previousPoints + Run_Out_Catcher_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Catcher_Value", previousValue + Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_catcher"))));

                                            }

                                            float Run_Out_Thrower_Points = game_type_point.getFloat("Run_Out_Thrower")
                                                    * scores.getInt("runout_thrower");

                                            if (inningsNumber <= 2) {
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Thrower", Run_Out_Thrower_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Thrower_Value", Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_thrower"))));
                                            } else {
                                                float previousPoints = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out_Thrower");
                                                float previousValue = players
                                                        .getJSONObject(scores.getString("fielder_id"))
                                                        .getFloat("Run_Out_Thrower_Value");

                                                players.getJSONObject(scores.getString("fielder_id")).put(
                                                        "Run_Out_Thrower", previousPoints + Run_Out_Thrower_Points);
                                                players.getJSONObject(scores.getString("fielder_id"))
                                                        .put("Run_Out_Thrower_Value", previousValue + Float.parseFloat(
                                                                String.valueOf(scores.getInt("runout_thrower"))));

                                            }

                                            float previousPoints = players.getJSONObject(scores.getString("fielder_id"))
                                                    .getFloat("total_points");

                                            players.getJSONObject(scores.getString("fielder_id")).put("total_points",
                                                    previousPoints + Catch_Points + Three_Catch_Points + Stumping_Points + Run_Out_Points
                                                            + Run_Out_Catcher_Points + Run_Out_Thrower_Points);

                                        }

                                    }
                                }

                            }

                        }

                        output.put("players", players);
                        output.put("scorecard_data", scorecardData);

                        JSONObject man_of_the_match1 = null;
                        if (api_data_array.getBoolean("verified")
                                && (api_data_array.getInt("status") == 2 || api_data_array.getInt("status") == 4)) {
                            man_of_the_match1 = new JSONObject();
                            man_of_the_match1.put("pid", "");
                            man_of_the_match1.put("name", "");

                        }
                        output.put("man_of_the_match", man_of_the_match1);

                        if (api_data_array.getInt("status") == 2) {
                            output.put("match_completed_status", "R");
                        } else if (api_data_array.getInt("status") == 4) {
                            output.put("match_completed_status", "AB");
                        } else {
                            output.put("match_completed_status", "NA");
                        }
                        return output;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    public JSONObject setupDefaultPointsIfNotAvailable(JSONObject game_type_point) {

        if (game_type_point == null) {
            return game_type_point;
        }

        if (!game_type_point.has("Being_Part_Of_Eleven")) {
            game_type_point.put("Being_Part_Of_Eleven", 0f);
        }

        if (!game_type_point.has("Every_Six_Hit")) {
            game_type_point.put("Every_Six_Hit", 0f);
        }

        if (!game_type_point.has("Every_Boundary_Hit")) {
            game_type_point.put("Every_Boundary_Hit", 0f);
        }

        if (!game_type_point.has("Every_Run_Scored")) {
            game_type_point.put("Every_Run_Scored", 0f);
        }

        if (!game_type_point.has("Half_Century")) {
            game_type_point.put("Half_Century", 0f);
        }

        if (!game_type_point.has("Dismiss_For_A_Duck")) {
            game_type_point.put("Dismiss_For_A_Duck", 0f);
        }

        if (!game_type_point.has("Wicket")) {
            game_type_point.put("Wicket", 0f);
        }

        if (!game_type_point.has("Maiden_Over")) {
            game_type_point.put("Maiden_Over", 0f);
        }

        if (!game_type_point.has("Five_Wicket")) {
            game_type_point.put("Five_Wicket", 0f);
        }

        if (!game_type_point.has("Four_Wicket")) {
            game_type_point.put("Four_Wicket", 0f);
        }

        if (!game_type_point.has("Three_Wicket")) {
            game_type_point.put("Three_Wicket", 0f);
        }

        if (!game_type_point.has("Two_Wicket")) {
            game_type_point.put("Two_Wicket", 0f);
        }

        if (!game_type_point.has("Catch")) {
            game_type_point.put("Catch", 0f);
        }

        if (!game_type_point.has("Three_Catch")) {
            game_type_point.put("Three_Catch", 0f);
        }

        if (!game_type_point.has("Lbw_Bowled")) {
            game_type_point.put("Lbw_Bowled", 0f);
        }

        if (!game_type_point.has("Catch_And_Bowled")) {
            game_type_point.put("Catch_And_Bowled", 0f);
        }

        if (!game_type_point.has("Stumping")) {
            game_type_point.put("Stumping", 0f);
        }

        if (!game_type_point.has("Thirty_Runs")) {
            game_type_point.put("Thirty_Runs", 0f);
        }
        
        if (!game_type_point.has("Run_Out")) {
            game_type_point.put("Run_Out", 0f);
        }

        if (!game_type_point.has("Run_Out_Catcher")) {
            game_type_point.put("Run_Out_Catcher", 0f);
        }

        if (!game_type_point.has("Run_Out_Thrower")) {
            game_type_point.put("Run_Out_Thrower", 0f);
        }

        if (!game_type_point.has("Minimum_Balls_for_Strike_Rate")) {
            game_type_point.put("Minimum_Balls_for_Strike_Rate", 0f);
        }

        if (!game_type_point.has("Minimum_Overs_for_Economy_Rate")) {
            game_type_point.put("Minimum_Overs_for_Economy_Rate", 0f);
        }

        if (!game_type_point.has("Strike_Rate")) {
            game_type_point.put("Strike_Rate", new JSONArray());
        }

        if (!game_type_point.has("Economy_Rate")) {
            game_type_point.put("Economy_Rate", new JSONArray());
        }

        return game_type_point;

    }
}
