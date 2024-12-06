package com.choic11.controller.cricket;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.cricket.MatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/cricket")
public class MatchesController extends BaseController {

	@Autowired
	MatchesService matchesService;

	@PostMapping(path = "/get_slider")

	public ResponseEntity<Object> getSlider(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		List<HashMap<String, Object>> sliders = matchesService.getSlider(baseRequest);

		if (sliders.size() == 0) {
			return echoRespose(new BaseResponse(0, false, "Stay Tunned", sliders), HttpStatus.OK);
		}

		return echoRespose(new BaseResponse(0, false, "Slider list.", sliders), HttpStatus.OK);

	}

	@PostMapping(path = "/get_matches/{matchProgress}")
	public ResponseEntity<Object> getMatches(HttpServletRequest request,
			@PathVariable("matchProgress") String matchProgress) {

		HashSet<String> matchProgressArray = new HashSet<String>();
		matchProgressArray.add("F");
		matchProgressArray.add("L");
		matchProgressArray.add("R");

		if (!matchProgressArray.contains(matchProgress)) {
			return echoRespose(new BaseResponse(0, false, "Invalid Match progress flag.", null), HttpStatus.OK);
		}

		BaseRequest baseRequest = new BaseRequest(request);
		List<HashMap<String, Object>> matches = matchesService.getMatches(baseRequest, matchProgress);

		if (matches.size() == 0) {
			return echoRespose(new BaseResponse(0, false, "No matches Found.", matches), HttpStatus.OK);
		}

		return echoRespose(new BaseResponse(0, false, "matches List.", matches), HttpStatus.OK);
	}

	@PostMapping(path = "/get_customer_matches/{matchProgress}")
	public ResponseEntity<Object> getCustomerMatches(HttpServletRequest request,
			@PathVariable("matchProgress") String matchProgress) {

		HashSet<String> matchProgressArray = new HashSet<String>();
		matchProgressArray.add("F");
		matchProgressArray.add("L");
		matchProgressArray.add("R");

		if (!matchProgressArray.contains(matchProgress)) {
			return echoRespose(new BaseResponse(0, false, "Invalid Match progress flag.", null), HttpStatus.OK);
		}

		BaseRequest baseRequest = new BaseRequest(request);
		List<HashMap<String, Object>> matches = matchesService.getCustomerMatches(baseRequest, matchProgress);

		if (matches.size() == 0) {
			return echoRespose(new BaseResponse(0, false, "No matches Found.", matches), HttpStatus.OK);
		}

		return echoRespose(new BaseResponse(0, false, "matches List.", matches), HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_score/{matchUniqueId}")
	public ResponseEntity<Object> getMatchScore(HttpServletRequest request,
			@PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = matchesService.getMatchScore(baseRequest, matchUniqueId);

		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_full_score/{matchUniqueId}")
	public ResponseEntity<Object> getMatchFullScore(HttpServletRequest request,
			@PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = matchesService.getMatchFullScore(baseRequest, matchUniqueId);

		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_players/{matchUniqueId}")
	public ResponseEntity<Object> getMatchPlayers(HttpServletRequest request,
			@PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = matchesService.getMatchPlayers(baseRequest, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}
	
	@PostMapping(path = "/get_match_players_stats/{matchUniqueId}")
	public ResponseEntity<Object> getMatchPlayersStats(HttpServletRequest request,
			@PathVariable("matchUniqueId") String matchUniqueId) {
		
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = matchesService.getMatchPlayersStats(baseRequest, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping("/get_series_by_player_statistics/{match_unique_id}/{player_unique_id}")
	public ResponseEntity<Object> getSeriesByPlayerStatistics(HttpServletRequest request,
			@PathVariable("match_unique_id") int matchUniqueId, @PathVariable("player_unique_id") int playerUniqueId) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = matchesService.getSeriesByPlayerStatistics(baseRequest, matchUniqueId, playerUniqueId);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_series")
	public ResponseEntity<Object> getSeries(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = matchesService.getSeries(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("/get_series_for_leaderboard")
	public ResponseEntity<Object> getSeriesForLeaderboard(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = matchesService.getSeriesForLeaderboard(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("/get_series_leaderboard")
	public ResponseEntity<Object> getSeriesLeaderboard(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("page_no", "series_id");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}
		BaseResponse response = matchesService.getSeriesLeaderboard(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_series_leaderboard_customer_matches")
	public ResponseEntity<Object> getSeriesLeaderboardCustomerMatches(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("customer_id", "series_id");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}
		BaseResponse response = matchesService.getSeriesLeaderboardCustomerMatches(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_series_for_weekly_leaderboard")
	public ResponseEntity<Object> getSeriesForWeeklyLeaderboard(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = matchesService.getSeriesForWeeklyLeaderboard(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_series_weekly_leaderboard_week")
	public ResponseEntity<Object> getSeriesWeeklyLeaderboardWeek(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("series_id");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}
		
		BaseResponse response = matchesService.getSeriesWeeklyLeaderboardWeek(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_series_leaderboard_by_week")
	public ResponseEntity<Object> getSeriesLeaderboardByWeek(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("page_no","series_id","searchdate");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}
		
		BaseResponse response = matchesService.getSeriesLeaderboardByWeek(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

}
