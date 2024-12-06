package com.choic11.controller.soccer;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.soccer.EntitySportsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entitysp_soccer")
@Controller("SoccerEntitySportsController")
public class EntitySportsController extends BaseController {

    @Autowired
    EntitySportsService entitySportsService;

    @GetMapping(path = "/get_upcoming_matches_series")
    public ResponseEntity<Object> getUpcomingMatchesSeries(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        Object entityResponse = entitySportsService.getUpcomingMatchesSeries(baseRequest);

        if (entityResponse instanceof JSONObject) {
            Map<String, Object> stringObjectMap = ((JSONObject) entityResponse).toMap();
            BaseResponse response = new BaseResponse(0, true, "Series list.", stringObjectMap);
            return echoRespose(response, HttpStatus.OK);
        } else {
            LinkedHashMap<String, Object> upcomingSeries = (LinkedHashMap<String, Object>) entityResponse;
            LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
            output.put("matches", upcomingSeries.values());

            BaseResponse response = new BaseResponse(0, false, "Series list.", output);
            return echoRespose(response, HttpStatus.OK);
        }


    }

    @GetMapping(path = "/get_upcoming_matches/{series_id}")
    public ResponseEntity<Object> getUpcomingMatchesBySeries(HttpServletRequest request,
                                                             @PathVariable("series_id") String seriesId) {
        BaseRequest baseRequest = new BaseRequest(request);
        LinkedHashMap<String, Object> upcomingMatches = entitySportsService.getUpcomingMatchesBySeries(baseRequest,
                seriesId);

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("matches", upcomingMatches.values());

        BaseResponse response = new BaseResponse(0, false, "Matches list.", output);

        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/get_upcoming_matches")
    public ResponseEntity<Object> getUpcomingMatchesBySeries(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        LinkedHashMap<String, Object> upcomingMatches = entitySportsService.getUpcomingMatchesBySeries(baseRequest,
                "0");

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("matches", upcomingMatches.values());

        BaseResponse response = new BaseResponse(0, false, "Matches list.", output);
        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/get_match_squade/{series_unique_id}/{match_unique_id}")
    public ResponseEntity<Object> getMatchSquad(HttpServletRequest request,
                                                @PathVariable("series_unique_id") String seriesUniqueId,
                                                @PathVariable("match_unique_id") String matchUniqueId) {
        BaseRequest baseRequest = new BaseRequest(request);
        List<LinkedHashMap<String, Object>> squadResponseData = entitySportsService.getMatchSquad(baseRequest,
                seriesUniqueId, matchUniqueId);

        if (squadResponseData == null) {
            BaseResponse response = new BaseResponse(0, true, "Unable to proceed", null);
            return echoRespose(response, HttpStatus.OK);
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("squad", squadResponseData);

        BaseResponse response = new BaseResponse(0, false, "Squad list.", output);
        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/get_match_lineup/{match_unique_id}")
    public ResponseEntity<Object> getMatchLineup(HttpServletRequest request,
                                                 @PathVariable("match_unique_id") String matchUniqueId) {
        BaseRequest baseRequest = new BaseRequest(request);
        LinkedHashMap<String, Object> output = entitySportsService.getMatchLineup(baseRequest, matchUniqueId, null);

        BaseResponse response = new BaseResponse(0, false, "Lineup Players list.", output);

        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/get_match_scorecard/{match_unique_id}")
    public ResponseEntity<Object> getMatchScoreCard(HttpServletRequest request,
                                                    @PathVariable("match_unique_id") String matchUniqueId) {
        BaseRequest baseRequest = new BaseRequest(request);
        JSONObject output = entitySportsService.getMatchScoreCard(baseRequest, matchUniqueId, null);

        if (output == null) {
            BaseResponse response = new BaseResponse(0, true, "Scoreboard not found.", null);

            return echoRespose(response, HttpStatus.OK);
        }

        BaseResponse response = new BaseResponse(0, false, "Scoreboard data.", output.toMap());

        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/player_finder/{player_name}")
    public ResponseEntity<Object> playerFinder(HttpServletRequest request,
                                               @PathVariable("player_name") String playerName) {
        BaseRequest baseRequest = new BaseRequest(request);
        List<LinkedHashMap<String, Object>> playersData = entitySportsService.playerFinder(baseRequest, playerName);

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("data", playersData);

        BaseResponse response = new BaseResponse(0, false, "Players list.", output);

        return echoRespose(response, HttpStatus.OK);
    }

    @GetMapping(path = "/player_detail/{player_id}")
    public ResponseEntity<Object> playerDetail(HttpServletRequest request, @PathVariable("player_id") String playerId) {
        BaseRequest baseRequest = new BaseRequest(request);
        LinkedHashMap<String, Object> playerData = entitySportsService.playerDetail(baseRequest, playerId);

        if (playerData == null) {
            BaseResponse response = new BaseResponse(0, true, "Players detail not found.", null);

            return echoRespose(response, HttpStatus.OK);
        }

        BaseResponse response = new BaseResponse(0, false, "Players Detail.", playerData);

        return echoRespose(response, HttpStatus.OK);
    }

}
