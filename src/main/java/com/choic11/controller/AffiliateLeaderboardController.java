package com.choic11.controller;

import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.AffiliateLeaderboardService;
import com.choic11.service.ProjectCronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/adminapis")
public class AffiliateLeaderboardController extends BaseController {

    @Autowired
    AffiliateLeaderboardService affiliateLeaderboardService;

    @PostMapping(path = "/get_affiliate_leaderboard")
    public ResponseEntity<Object> getAffiliateLeaderboard(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("leaderboard_id");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse baseResponse = affiliateLeaderboardService.getAffiliateLeaderboard(baseRequest);
        return echoRespose(baseResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/get_result_for_affiliate_leaderboard")
    public ResponseEntity<Object> getResultForAffiliateLeaderboard(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("leaderboard_id");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse baseResponse = null;
        try {
            baseResponse = affiliateLeaderboardService.getResultForAffiliateLeaderboardDeposit(baseRequest);
        } catch (Exception e) {
            return echoRespose(new BaseResponse(1, true, e.getMessage(), null), HttpStatus.OK);
        }
        return echoRespose(baseResponse, HttpStatus.OK);
    }

    @Autowired
    ProjectCronService projectCronService;

    @GetMapping(path = "/update_affiliate_leaderboard_rank/{leaderboard_id}")
    public ResponseEntity<Object> updateAffiliateLeaderboardRank(HttpServletRequest request, @PathVariable(name = "leaderboard_id") int leaderboardId) {
        BaseRequest baseRequest = new BaseRequest(request);
        projectCronService.updateAffiliateLeaderboardRank(leaderboardId);
        return echoRespose(null, HttpStatus.OK);
    }
}
