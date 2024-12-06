package com.choic11.controller.basketball;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.basketball.TeamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/basketball")
@Controller("BasketballTeamsController")
public class TeamsController extends BaseController {

	@Autowired
	TeamsService teamsService;

	@PostMapping(path = "/get_already_created_team_count/{matchUniqueId}")
	public ResponseEntity<Object> getAlreadyCreatedTeamCount(HttpServletRequest request,
			@PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = teamsService.getAlreadyCreatedTeamCount(baseRequest.authUserId, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/create_customer_team")
	public ResponseEntity<Object> createCustomerTeam(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);

		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "player_json");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}
		baseRequest.setParam("customer_team_name", "");
		baseRequest.setParam("team_name", "0");
		baseRequest.setParam("fromadmin", "0");

		BaseResponse match = teamsService.createCustomerTeam(baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/update_customer_team")
	public ResponseEntity<Object> updateCustomerTeam(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);

		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "customer_team_id",
				"player_json");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse match = teamsService.updateCustomerTeam(baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping("/get_customer_match_teams/{match_unique_id}")
	public ResponseEntity<Object> getCustomerMatchTeam(HttpServletRequest request,
			@PathVariable("match_unique_id") int match_unique_id) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = teamsService.getCustomerMatchTeam(baseRequest.authUserId, match_unique_id);
		return echoRespose(response, HttpStatus.OK);

	}

	@PostMapping("/get_customer_match_team_detail/{customer_team_id}")
	public ResponseEntity<Object> getCustomerMatchTeamDetail(HttpServletRequest request,
			@PathVariable("customer_team_id") int customerTeamId) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = teamsService.getCustomerMatchTeamDetail(baseRequest.authUserId, customerTeamId);
		return echoRespose(response, HttpStatus.OK);
	}


	@PostMapping("/get_match_dream_team_detail/{match_unique_id}")
	public ResponseEntity<Object> getMatchDreamTeamDetail(HttpServletRequest request,
			@PathVariable("match_unique_id") int matchUniqueid) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = teamsService.getMatchDreamTeamDetail(baseRequest, matchUniqueid);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_customer_match_team_stats/{customer_team_id}")
	public ResponseEntity<Object> getCustomerMatchTeamStats(HttpServletRequest request,
			@PathVariable("customer_team_id") int customerTeamId) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = teamsService.getCustomerMatchTeamStats(baseRequest, customerTeamId);
		return echoRespose(response, HttpStatus.OK);
	}
	
	@PostMapping("/get_match_dream_team_stats/{match_unique_id}")
	public ResponseEntity<Object> getMatchDreamTeamStats(HttpServletRequest request,
			@PathVariable("match_unique_id") String matchUniqueid) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = teamsService.getMatchDreamTeamStats(baseRequest, matchUniqueid);
		return echoRespose(response, HttpStatus.OK);
	}

}
