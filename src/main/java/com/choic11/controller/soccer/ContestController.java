package com.choic11.controller.soccer;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.soccer.ContestService;
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
@RequestMapping("/soccer")
@Controller("SoccerContestController")
public class ContestController extends BaseController {

	@Autowired
	ContestService contestService;

	@PostMapping(path = "/get_match_contest/{matchId}/{matchUniqueId}")
	public ResponseEntity<Object> getMatchContest(HttpServletRequest request, @PathVariable("matchId") int matchId,
			@PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchContest(baseRequest, matchId, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_customer_match_contest/{matchId}/{matchUniqueId}")
	public ResponseEntity<Object> getCustomerMatchContest(HttpServletRequest request,
			@PathVariable("matchId") int matchId, @PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getCustomerMatchContest(baseRequest, matchId, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_category_contest/{matchId}/{matchUniqueId}/{catId}")
	public ResponseEntity<Object> get_match_category_contest(HttpServletRequest request,
			@PathVariable("matchId") int matchId, @PathVariable("matchUniqueId") int matchUniqueId,
			@PathVariable("catId") int catId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchCategoryContest(baseRequest, matchId, matchUniqueId, catId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_contest_winner_breakup/{contestId}")
	public ResponseEntity<Object> get_contest_winner_breakup(HttpServletRequest request,
			@PathVariable("contestId") int contestId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getContestWinnerBreakup(contestId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_contest_detail/{matchContestId}/{matchUniqueId}")
	public ResponseEntity<Object> get_match_contest_detail(HttpServletRequest request,
			@PathVariable("matchContestId") int matchContestId, @PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchContestSDetail(matchContestId, baseRequest, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_private_contest_detail/{slug}/{matchUniqueId}")
	public ResponseEntity<Object> get_match_private_contest_detail(HttpServletRequest request,
			@PathVariable("slug") String slug, @PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchPrivateContestDetail(slug, baseRequest, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_contest_share_detail/{matchContestId}")
	public ResponseEntity<Object> get_match_contest_share_detail(HttpServletRequest request,
			@PathVariable("matchContestId") int matchContestId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchContestShareDetail(baseRequest,matchContestId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_contest_pdf/{matchContestId}/{matchUniqueId}")
	public ResponseEntity<Object> get_match_contest_pdf(HttpServletRequest request,
			@PathVariable("matchContestId") int matchContestId, @PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchContestPdf(matchContestId, matchUniqueId, baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_contest_teams/{matchUniqueId}/{matchContestId}/{pageNo}")
	public ResponseEntity<Object> get_contest_teams(HttpServletRequest request,
			@PathVariable("matchUniqueId") int matchUniqueId, @PathVariable("matchContestId") int matchContestId,
			@PathVariable("pageNo") int pageNo) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getContestTeams(baseRequest.authUserId, matchUniqueId, matchContestId,
				pageNo);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/customer_pre_join_contest")
	public ResponseEntity<Object> customer_pre_join_contest(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);

		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "match_contest_id",
				"customer_team_ids");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse match = contestService.customerPreJoinContest(baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/customer_join_contest")
	public ResponseEntity<Object> customer_join_contest(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);

		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "match_contest_id",
				"customer_team_id");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse match = contestService.customerJoinContestMulti(baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/customer_switch_team")
	public ResponseEntity<Object> customerSwitchTeam(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);

		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "match_contest_id",
				"customer_team_id_old", "customer_team_id_new");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse match = contestService.customerSwitchTeam(baseRequest);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping("/get_private_contest_settings")
	public ResponseEntity<Object> getPrivateContestSettings(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse response = contestService.getPrivateContestSettings(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("/get_private_contest_entry_fee")
	public ResponseEntity<Object> getPrivateContestEntryFee(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("contest_size", "prize_pool");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse response = contestService.getPrivateContestEntryFee(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("/get_private_contest_winning_breakup")
	public ResponseEntity<Object> getPrivateContestWinningBreakup(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("contest_size", "prize_pool");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse response = contestService.getPrivateContestWinningBreakup(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("/create_private_contest")
	public ResponseEntity<Object> createPrivateContest(HttpServletRequest request) {
		BaseRequest baseRequest = new BaseRequest(request);
		ArrayList<String> errorFields = baseRequest.verifyRequiredParams("contest_size", "prize_pool",
				"winning_breakup_id", "match_id", "match_unique_id", "is_multiple", "team_id", "pre_join");
		if (errorFields.size() > 0) {
			return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
		}

		BaseResponse response = contestService.createPrivateContest(baseRequest);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping(path = "/update_contest_favorite/{masterContestId}")
	public ResponseEntity<Object> updateContestFavorite(HttpServletRequest request, @PathVariable("masterContestId") int masterContestId) {
		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.updateContestFavorite(baseRequest, masterContestId);
		return echoRespose(match, HttpStatus.OK);
	}

	@PostMapping(path = "/get_match_contest_fav/{matchId}/{matchUniqueId}")
	public ResponseEntity<Object> getMatchContestFav(HttpServletRequest request, @PathVariable("matchId") int matchId,
												  @PathVariable("matchUniqueId") int matchUniqueId) {

		BaseRequest baseRequest = new BaseRequest(request);
		BaseResponse match = contestService.getMatchContest(baseRequest, matchId, matchUniqueId);
		return echoRespose(match, HttpStatus.OK);
	}
}
