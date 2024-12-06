package com.choic11.service.basketball;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.basketball.TblBasketballMatch;
import com.choic11.model.basketball.TblBasketballSlider;
import com.choic11.model.basketball.TblBasketballTeam;
import com.choic11.model.response.BaseResponse;
import com.choic11.model.response.SeriesLeaderBoardResponse;
import com.choic11.repository.basketball.MatchesRepository;
import com.choic11.service.CustomerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service("BasketballMatchesService")
public class MatchesService {

    @Autowired
    MatchesRepository matchesRepository;

    @Autowired
    CustomerService customerService;

    public MatchesRepository getMatchesRepository() {
        return matchesRepository;
    }

    public List<HashMap<String, Object>> getSlider(BaseRequest baseRequest) {
        List<TblBasketballSlider> sliders = matchesRepository.getSlider();

        List<HashMap<String, Object>> slidersData = new ArrayList<HashMap<String, Object>>();

        for (TblBasketballSlider tblSlider : sliders) {

            LinkedHashMap<String, Object> slider = new LinkedHashMap<String, Object>();

            slider.put("id", tblSlider.getId());
            slider.put("content", tblSlider.getContent());
            slider.put("redirecting_links_type", tblSlider.getRedirectingLinksType());
            slider.put("redirecting_url", tblSlider.getRedirectingUrl());
            slider.put("image_thumb", Util.generateImageUrl(tblSlider.getImage(), FileUploadConstant.SLIDER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
            slider.put("image_large", Util.generateImageUrl(tblSlider.getImage(), FileUploadConstant.SLIDER_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));

            if (tblSlider.getMatchUniqueId() > 0) {
                TblBasketballMatch tblBasketballMatch = tblSlider.getTblBasketballMatch();
                if(tblBasketballMatch!=null){
                    slider.put("match", getModifiedMatchData(tblBasketballMatch));
                }else {
                    slider.put("match", null);
                }
            } else {
                slider.put("match", null);
            }

            slidersData.add(slider);

        }

        return slidersData;
    }

    public List<HashMap<String, Object>> getMatches(BaseRequest baseRequest, String matchProgress) {

        List<TblBasketballMatch> matches = matchesRepository.getMatches(matchProgress,baseRequest.authUserId);

        List<HashMap<String, Object>> matchesData = new ArrayList<HashMap<String, Object>>();

        for (TblBasketballMatch tblBasketballMatch : matches) {
            matchesData.add(getModifiedMatchData(tblBasketballMatch));
        }

        return matchesData;

    }

    public List<HashMap<String, Object>> getCustomerMatches(BaseRequest baseRequest, String matchProgress) {

        Object result = matchesRepository.getCustomerMatches(baseRequest.authUserId, matchProgress);
        if (result instanceof List) {
            return (List) result;

        }
        Object[] resultData = (Object[]) result;
        LinkedHashMap<String, Object> customerMatchesContest = (LinkedHashMap<String, Object>) resultData[0];
        List<TblBasketballMatch> matches = (List<TblBasketballMatch>) resultData[1];

        List<HashMap<String, Object>> matchesData = new ArrayList<HashMap<String, Object>>();

        for (TblBasketballMatch tblBasketballMatch : matches) {
            LinkedHashMap<String, Object> modifiedMatchData = getModifiedMatchData(tblBasketballMatch);
//            long contestCount = (long) customerMatchesContest.get(modifiedMatchData.get("match_id").toString());
            modifiedMatchData.put("contest_count", tblBasketballMatch.getContestCount());
            matchesData.add(modifiedMatchData);
        }

        return matchesData;

    }

    public LinkedHashMap<String, Object> getModifiedMatchData(TblBasketballMatch tblBasketballMatch) {
        LinkedHashMap<String, Object> match = new LinkedHashMap<String, Object>();

        LinkedHashMap<String, Object> series = null;
        if (tblBasketballMatch.getTblBasketballSery() != null) {
            series = new LinkedHashMap<String, Object>();
            series.put("id", tblBasketballMatch.getTblBasketballSery().getId());
            series.put("name", tblBasketballMatch.getTblBasketballSery().getName());
            series.put("is_leaderboard_available", tblBasketballMatch.getTblBasketballSery().getIsLeaderboardAvailable());
        }

        LinkedHashMap<String, Object> gametype = null;
        if (tblBasketballMatch.getTblGameType() != null) {
            gametype = new LinkedHashMap<String, Object>();
            gametype.put("id", tblBasketballMatch.getTblGameType().getId());
            gametype.put("name", tblBasketballMatch.getTblGameType().getName());
        }

        LinkedHashMap<String, Object> team1 = null;
        if (tblBasketballMatch.getTblBasketballTeam() != null) {
            TblBasketballTeam tblBasketballTeam = tblBasketballMatch.getTblBasketballTeam();

            team1 = new LinkedHashMap<String, Object>();
            team1.put("id", tblBasketballTeam.getId());
            team1.put("name", tblBasketballTeam.getName());
            team1.put("sort_name", tblBasketballTeam.getSortName());
            team1.put("image", Util.generateImageUrl(tblBasketballTeam.getLogo(), FileUploadConstant.TEAMBASKETBALL_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_TEAM));
        }

        LinkedHashMap<String, Object> team2 = null;
        if (tblBasketballMatch.getTblBasketballTeam2() != null) {
            TblBasketballTeam tblBasketballTeam = tblBasketballMatch.getTblBasketballTeam2();
            team2 = new LinkedHashMap<String, Object>();
            team2.put("id", tblBasketballTeam.getId());
            team2.put("name", tblBasketballTeam.getName());
            team2.put("sort_name", tblBasketballTeam.getSortName());
            team2.put("image", Util.generateImageUrl(tblBasketballTeam.getLogo(), FileUploadConstant.TEAMBASKETBALL_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_TEAM));
        }

        match.put("id", tblBasketballMatch.getId());
        match.put("match_id", tblBasketballMatch.getUniqueId());
        match.put("name", tblBasketballMatch.getName());
        match.put("match_date", tblBasketballMatch.getMatchDate());
        match.put("close_date", tblBasketballMatch.getCloseDate());
        match.put("toss_message", tblBasketballMatch.getMatchMessage());
        match.put("match_progress", tblBasketballMatch.getMatchProgress());
        match.put("match_completed_status", tblBasketballMatch.getMatchCompletedStatus());

        match.put("server_date", Util.getCurrentTime());
        match.put("match_limit", tblBasketballMatch.getMatchLimit());
        match.put("contest_count", tblBasketballMatch.getActiveContestCount());
        match.put("playing_squad_updated", tblBasketballMatch.getPlayingSquadUpdated());
        match.put("is_affiliate_available_all", tblBasketballMatch.getIsAffiliateAvailableAll());
        match.put("affiliate_per_for_all", tblBasketballMatch.getAffiliatePerForAll());
        match.put("series", series);
        match.put("gametype", gametype);
        match.put("team1", team1);
        match.put("team2", team2);

        if(!tblBasketballMatch.isFixtureMatch()){
            match.put("contest_count" ,tblBasketballMatch.getContestCount());
        }
        match.put("total_team" ,tblBasketballMatch.getTeamCount());
        match.put("total_winning" ,tblBasketballMatch.getTotalWinning());
        match.put("total_investment" ,tblBasketballMatch.getTotalInvestment());
        return match;
    }

    public TblBasketballMatch getCustomerMatchData(int matchUniqueId,int customerId) {
        TblBasketballMatch tblBasketballMatch = matchesRepository.getCustomerMatchData(matchUniqueId,customerId);
        return tblBasketballMatch;
    }

    public BaseResponse getMatchScore(BaseRequest baseRequest, int matchUniqueId) {

        HashMap<String, Object> basketballMatchScore = matchesRepository.getMatchScore(matchUniqueId);
        if (basketballMatchScore == null) {
            return new BaseResponse(0, false, "No matches Found.", null);
        }

        HashMap<String, Object> team1 = new HashMap<String, Object>();
        team1.put("id", basketballMatchScore.get("team_1_id"));
        team1.put("name", basketballMatchScore.get("team_1_name"));
        team1.put("sort_name", basketballMatchScore.get("team_1_sort_name"));
        team1.put("team_run", basketballMatchScore.get("team1_run"));
        team1.put("team_wicket", basketballMatchScore.get("team1_wicket"));
        team1.put("team_overs", basketballMatchScore.get("team1_overs"));

        HashMap<String, Object> team2 = new HashMap<String, Object>();
        team2.put("id", basketballMatchScore.get("team_2_id"));
        team2.put("name", basketballMatchScore.get("team_2_name"));
        team2.put("sort_name", basketballMatchScore.get("team_2_sort_name"));
        team2.put("team_run", basketballMatchScore.get("team2_run"));
        team2.put("team_wicket", basketballMatchScore.get("team2_wicket"));
        team2.put("team_overs", basketballMatchScore.get("team2_overs"));

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();

        output.put("team1", team1);
        output.put("team2", team2);
        output.put("score_board_notes", basketballMatchScore.get("score_board_notes"));
        output.put("short_score", null);
        if (!Util.isEmpty((String) basketballMatchScore.get("short_score"))) {
            JSONObject jsonObject = new JSONObject(basketballMatchScore.get("short_score").toString());

            output.put("short_score", jsonObject.toMap());
        }
        return new BaseResponse(0, false, "matches scoreboard.", output);
    }

    public BaseResponse getMatchPlayers(BaseRequest baseRequest, int matchUniqueId) {

        TblBasketballMatch matchData = matchesRepository.getMatchData(matchUniqueId);

        if (matchData == null) {
            return new BaseResponse(0, false, "Unable to proceed.", matchData);
        }

        LinkedHashMap<String, Object> modifiedMatchData = getModifiedMatchData(matchData);

        List<HashMap<String, Object>> playersByMatchandTeam = matchesRepository
                .getPlayersByMatch(matchData.getSeriesId(), matchData.getGameTypeId(), matchUniqueId);

        List<LinkedHashMap<String, Object>> wkPlayers = null;
        List<LinkedHashMap<String, Object>> batPlayers = null;
        List<LinkedHashMap<String, Object>> allPlayers = null;
        List<LinkedHashMap<String, Object>> bowlPlayers = null;
        List<LinkedHashMap<String, Object>> centerPlayers = null;
        if (!playersByMatchandTeam.isEmpty()) {
            wkPlayers = new ArrayList<LinkedHashMap<String, Object>>();
            batPlayers = new ArrayList<LinkedHashMap<String, Object>>();
            allPlayers = new ArrayList<LinkedHashMap<String, Object>>();
            bowlPlayers = new ArrayList<LinkedHashMap<String, Object>>();
            centerPlayers = new ArrayList<LinkedHashMap<String, Object>>();

            for (HashMap<String, Object> playersByMatchandTeamValue : playersByMatchandTeam) {
                int selected_by = (int) playersByMatchandTeamValue.get("selectedByCount");
                int selected_as_caption = (int) playersByMatchandTeamValue.get("selectedAsCaptionCount");
                int selected_as_vccaption = (int) playersByMatchandTeamValue.get("selectedAsVccaptionCount");
                int selected_as_trump = (int) playersByMatchandTeamValue.get("selectedAsTrumpCount");

                float selectedByPer = 0;
                float selectedByCaptionPer = 0;
                float selectedByVCaptionPer = 0;
                float selectedByTrumpPer = 0;
                if (matchData.getTotalCustomerTeam() > 0) {
                    selectedByPer = (((float) selected_by) / matchData.getTotalCustomerTeam()) * 100f;
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
                LinkedHashMap<String, Object> playerData = new LinkedHashMap<String, Object>();
                playerData.put("team_id", playersByMatchandTeamValue.get("team_id"));
                playerData.put("credits", playersByMatchandTeamValue.get("credits"));
                playerData.put("position", playersByMatchandTeamValue.get("playing_role"));
                playerData.put("total_points", playersByMatchandTeamValue.get("series_total_points"));
                playerData.put("is_in_playing_squad", playersByMatchandTeamValue.get("is_in_playing_squad"));
                playerData.put("is_in_substitute_squad", playersByMatchandTeamValue.get("is_in_substitute_squad"));
                playerData.put("playing_squad_updated", modifiedMatchData.get("playing_squad_updated"));
                playerData.put("player_id", playersByMatchandTeamValue.get("uniqueId"));
                playerData.put("full_name", playersByMatchandTeamValue.get("name"));
                playerData.put("name", Util.getPlayerFormattedName(playersByMatchandTeamValue.get("name").toString()));
                playerData.put("bat_type", playersByMatchandTeamValue.get("bets"));
                playerData.put("bowl_type", playersByMatchandTeamValue.get("bowls"));
                playerData.put("country", playersByMatchandTeamValue.get("country_name"));
                String playDob = (String) playersByMatchandTeamValue.get("dob");
                playerData.put("dob", !Util.isEmpty(playDob) ? Util.getFormatedDate(playDob, "YYYY-MM-dd", "dd-MM-YYY") : "");
                playerData.put("selected_by", selectedByPer);
                playerData.put("selected_as_caption", selectedByCaptionPer);
                playerData.put("selected_as_vccaption", selectedByVCaptionPer);
                playerData.put("selected_as_trump", selectedByTrumpPer);
                playerData.put("image", Util.generateImageUrl((String) playersByMatchandTeamValue.get("image"), FileUploadConstant.PLAYER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL_PLAYER));

                if (!Util.isEmpty((String) playersByMatchandTeamValue.get("playing_role"))) {
                    String position = ((String) playersByMatchandTeamValue.get("playing_role")).toLowerCase();
                    if (position.contains("point guard")) {
                        wkPlayers.add(playerData);
                    } else if (position.contains("shooting guard")) {
                        batPlayers.add(playerData);
                    } else if (position.contains("small forward")) {
                        allPlayers.add(playerData);
                    } else if (position.contains("power forward")) {
                        bowlPlayers.add(playerData);
                    } else if (position.contains("center")) {
                        centerPlayers.add(playerData);
                    }
                }

            }

        }

        modifiedMatchData.put("batsmans", batPlayers);
        modifiedMatchData.put("bowlers", bowlPlayers);
        modifiedMatchData.put("centers", centerPlayers);
        modifiedMatchData.put("wicketkeapers", wkPlayers);
        modifiedMatchData.put("allrounders", allPlayers);
        modifiedMatchData.put("team_settings", matchesRepository.getTeamSettings());

        return new BaseResponse(0, false, "match Detail.", modifiedMatchData);
    }

    public BaseResponse getMatchPlayersStats(BaseRequest baseRequest, String matchUniqueId) {
        HashMap<String, Object> matchPlayersStats = matchesRepository.getMatchPlayersStats(baseRequest.authUserId,
                Integer.parseInt(matchUniqueId));
        if (matchPlayersStats.isEmpty()) {
            return new BaseResponse(0, false, "No Player Found.", new ArrayList<String>());
        } else {
            return new BaseResponse(0, false, "Player list.", matchPlayersStats.values());
        }
    }

    public BaseResponse getMatchFullScore(BaseRequest baseRequest, int matchUniqueId) {
        HashMap<String, Object> basketballMatchScore = matchesRepository.getMatchFullScore(matchUniqueId);

        if (basketballMatchScore == null) {
            return new BaseResponse(0, false, "Match Not Found.", null);
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("short_score", null);
        if (!Util.isEmpty((String) basketballMatchScore.get("short_score"))) {
            JSONObject jsonObject = new JSONObject(basketballMatchScore.get("short_score").toString());
            output.put("short_score", jsonObject.toMap());
        }
        output.put("full_score", null);
        if (!Util.isEmpty((String) basketballMatchScore.get("full_score"))) {
            JSONArray jsonObject1 = new JSONArray(basketballMatchScore.get("full_score").toString());
            output.put("full_score", jsonObject1.toList());
        }

        return new BaseResponse(0, false, "Match score.", output);
    }

    public BaseResponse getSeriesByPlayerStatistics(BaseRequest baseRequest, int matchUniqueId, int playerUniqueId) {

        Object playerData = matchesRepository.getSeriesByPlayerStatistics(matchUniqueId, playerUniqueId);

        if (playerData instanceof String) {
            String error = (String) playerData;
            if (error.equals("NO_RECORD")) {
                return new BaseResponse(0, false, "No Statistics Found.", new ArrayList<>());
            } else {
                return new BaseResponse(0, false, "Unable to proceed.", null);
            }
        }

        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) playerData;

        return new BaseResponse(0, false, "Player Statistics.", data.values());

    }

    public BaseResponse getSeries(BaseRequest baseRequest) {
        Collection<Object> series = matchesRepository.getSeries();
        return new BaseResponse(0, false, "Series list.", series);
    }

    public BaseResponse getSeriesForLeaderboard(BaseRequest baseRequest) {
        Collection<Object> series = matchesRepository.getSeriesForLeaderboard();
        return new BaseResponse(0, false, "Series list.", series);
    }

    public BaseResponse getSeriesForWeeklyLeaderboard(BaseRequest baseRequest) {
        Collection<Object> series = matchesRepository.getSeriesForWeeklyLeaderboard();
        return new BaseResponse(0, false, "Week Series list.", series);
    }

    public BaseResponse getSeriesLeaderboard(BaseRequest baseRequest) {
        int page_no = Integer.parseInt(baseRequest.getParam("page_no"));
        int series_id = Integer.parseInt(baseRequest.getParam("series_id"));

        Collection<Object> seriesLeaderboard = matchesRepository.getSeriesLeaderboard(baseRequest.authUserId, series_id,
                page_no);
        HashMap<String, Object> seriesLeaderboardSelf = matchesRepository
                .getSeriesLeaderboardSelf(baseRequest.authUserId, series_id);

        return new SeriesLeaderBoardResponse(0, false, "Series Leaderboard list.", seriesLeaderboard,
                seriesLeaderboardSelf);

    }

    public BaseResponse getSeriesLeaderboardByWeek(BaseRequest baseRequest) {
        int page_no = Integer.parseInt(baseRequest.getParam("page_no"));
        int series_id = Integer.parseInt(baseRequest.getParam("series_id"));
        String searchDate = baseRequest.getParam("searchdate");
        boolean isAffiliate = false;
        if(baseRequest.hasParam("is_affiliate")) {
            isAffiliate = Boolean.parseBoolean(baseRequest.getParam("is_affiliate"));
        }

        Collection<Object> seriesLeaderboard = matchesRepository.getSeriesLeaderboardByWeek(baseRequest.authUserId, series_id,
                page_no, searchDate,isAffiliate);
        HashMap<String, Object> seriesLeaderboardSelf = matchesRepository
                .getSeriesLeaderboardSelfByWeek(baseRequest.authUserId, series_id, searchDate);

        return new SeriesLeaderBoardResponse(0, false, "Series Leaderboard list.", seriesLeaderboard,
                seriesLeaderboardSelf);
    }

    public BaseResponse getSeriesLeaderboardCustomerMatches(BaseRequest baseRequest) {
        int customer_id = Integer.parseInt(baseRequest.getParam("customer_id"));
        int series_id = Integer.parseInt(baseRequest.getParam("series_id"));
        String weekNo = "";
        if (baseRequest.hasParam("week_no") && !Util.isEmpty(baseRequest.getParam("week_no"))) {
            weekNo = baseRequest.getParam("week_no");
        }


        Collection<Object> seriesLeaderboardCustomerMatches = matchesRepository
                .getSeriesLeaderboardCustomerMatches(baseRequest.authUserId, series_id, customer_id, weekNo);

        return new BaseResponse(0, false, "Series Leaderboard Customer Matches list.",
                seriesLeaderboardCustomerMatches);
    }

    public BaseResponse getSeriesWeeklyLeaderboardWeek(BaseRequest baseRequest) {
        int seriesId = Integer.parseInt(baseRequest.getParam("series_id"));
        HashMap<String, Object> seriesWeeklyLeaderboardWeek = matchesRepository.getSeriesWeeklyLeaderboardWeek(seriesId);
        if (seriesWeeklyLeaderboardWeek == null) {
            return new BaseResponse(0, false, "Weeks list.", new ArrayList<Object>());
        } else {
            long maxDate = ((BigInteger) seriesWeeklyLeaderboardWeek.get("maxDate")).longValue();
            long minDate = ((BigInteger) seriesWeeklyLeaderboardWeek.get("minDate")).longValue();

            List<HashMap<String, Object>> generateWeeks = Util.generateWeeks(minDate, maxDate);
            return new BaseResponse(0, false, "Weeks list.", generateWeeks);
        }
    }

}