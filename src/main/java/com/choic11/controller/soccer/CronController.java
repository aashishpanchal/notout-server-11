package com.choic11.controller.soccer;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.soccer.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/soccercron")
@Controller("SoccerCronController")
public class CronController extends BaseController {

    @Autowired
    CronService cronService;

    @PostMapping(path = "/check_contest_compress")
    public ResponseEntity<Object> checkContestCompress(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("id", "totalTeam", "totalJoinedTeam",
                "entryFees", "totalPrice", "contestJson");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        HashMap<String, Object> contestData = new HashMap<String, Object>();
        contestData.put("id", Integer.parseInt(baseRequest.getParam("id")));
        contestData.put("totalTeam", Integer.parseInt(baseRequest.getParam("totalTeam")));
        contestData.put("totalJoinedTeam", Integer.parseInt(baseRequest.getParam("totalJoinedTeam")));
        contestData.put("entryFees", Float.parseFloat(baseRequest.getParam("entryFees")));
        contestData.put("totalPrice", Float.parseFloat(baseRequest.getParam("totalPrice")));
        contestData.put("contestJson", baseRequest.getParam("contestJson"));

        BaseResponse match = cronService.checkContestCompress(contestData);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/declare_match_result/{match_unique_id}")
    public ResponseEntity<Object> declareMatchResult(HttpServletRequest request,
                                                     @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.declareMatchResult(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

//    @GetMapping(path = "/declare_match_result_test/{match_unique_id}")
//    public ResponseEntity<Object> declareMatchResultTest(HttpServletRequest request,
//                                                     @PathVariable(name = "match_unique_id") int match_unique_id) {
//
//        BaseRequest baseRequest = new BaseRequest(request);
//        BaseResponse match = cronService.declareMatchResultTest(match_unique_id);
//        return echoRespose(match, HttpStatus.OK);
//    }

    @GetMapping(path = "/declare_series_leaderboard_result/{series_id}")
    public ResponseEntity<Object> declareSeriesLeaderboardResult(HttpServletRequest request,
                                                                 @PathVariable(name = "series_id") int seriesId) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.declareSeriesLeaderboardResult(seriesId);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/abodent_match/{match_unique_id}")
    public ResponseEntity<Object> abodentMatch(HttpServletRequest request,
                                               @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.abodentMatch(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/abondant_match_contest/{match_contest_id}")
    public ResponseEntity<Object> abondantMatchContest(HttpServletRequest request,
                                                       @PathVariable(name = "match_contest_id") int matchContestId) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.abondantMatchContest(matchContestId);
        return echoRespose(match, HttpStatus.OK);
    }


    @GetMapping(path = "/update_customer_abcontest_info/{match_unique_id}/{match_contest_id}")
    public ResponseEntity<Object> abondantMatchContest(HttpServletRequest request,
                                                       @PathVariable(name = "match_unique_id") int match_unique_id,
                                                       @PathVariable(name = "match_contest_id") int matchContestId) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse baseResponse = cronService.updateCustomerMatchAbondantContestInfo(match_unique_id, matchContestId);
        return echoRespose(baseResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/create_customer_team_join_contest_admin")
    public ResponseEntity<Object> createCustomerTeamJoinContestAdmin(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "player_json", "user_id",
                "customer_team_name", "team_name", "match_contest_id", "is_update", "customer_team_id");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse match = cronService.createCustomerTeamJoinContestAdmin(baseRequest);
        return echoRespose(match, HttpStatus.OK);
    }

    @PostMapping(path = "/match_lineup_mannual")
    public ResponseEntity<Object> matchLineupMannual(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("match_unique_id", "players");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse match = cronService.matchLineupMannual(baseRequest);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/update_player_points_in_series/{series_id}/{game_type_id}")
    public ResponseEntity<Object> updatePlayerPointsInSeries(HttpServletRequest request,
                                                             @PathVariable(name = "series_id") int series_id, @PathVariable(name = "game_type_id") int game_type_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        cronService.updatePlayerPointsInSeries(series_id, game_type_id);
        return echoRespose(new BaseResponse(0, false, "Players points updatedby series successfully.", null),
                HttpStatus.OK);
    }

    @GetMapping(path = "/match_progress_cron")
    public ResponseEntity<Object> matchProgressCron(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.matchProgressCron();
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/match_distribute_affi_percentage_cron")
    public ResponseEntity<Object> distributeAffiliatePercentageCron(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.distributeAffiliatePercentageCron(0);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/match_distribute_affi_percentage_cron/{match_unique_id}")
    public ResponseEntity<Object> distributeAffiliatePercentageCron(HttpServletRequest request,
                                                                    @PathVariable(name = "match_unique_id") int matchUniqueId) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.distributeAffiliatePercentageCron(matchUniqueId);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/match_lineup_cron/{match_unique_id}")
    public ResponseEntity<Object> matchLineupCron(HttpServletRequest request,
                                                  @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.matchLineupCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/send_lineup_notification_cron")
    public ResponseEntity<Object> sendLineupNotificationCron(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.sendLineupNotificationCron();
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/send_mail_cron")
    public ResponseEntity<Object> sendMailCron(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.sendMailCron();
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/match_abondant_contest_cron/{match_unique_id}")
    public ResponseEntity<Object> matchAbondantContestCron(HttpServletRequest request,
                                                           @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.matchAbondantContestCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/live_match_update_ranking_cron/{match_unique_id}")
    public ResponseEntity<Object> liveMatchUpdateRankingCron(HttpServletRequest request,
                                                             @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.liveMatchUpdateRankingCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/distribute_referral_cashbonus_cron/{match_unique_id}")
    public ResponseEntity<Object> distributeReferralCashbonusCron(HttpServletRequest request,
                                                                  @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.distributeReferralCashbonusCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/distribute_affiliat_amount_cron/{match_unique_id}")
    public ResponseEntity<Object> distributeAffiliateAmountCron(HttpServletRequest request,
                                                                @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.distributeAffiliateAmountCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/generate_match_leaderboard_cron/{match_unique_id}")
    public ResponseEntity<Object> generateMatchLeaderboardCron(HttpServletRequest request,
                                                               @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.generateMatchLeaderboardCron(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/get_player_detail_cron")
    public ResponseEntity<Object> getPlayerDetailCron(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.getPlayerDetailCron();
        return echoRespose(match, HttpStatus.OK);
    }

    @GetMapping(path = "/update_new_available_match_count_cron")
    public ResponseEntity<Object> updateNewAvailableMatchCountCron(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.updateNewAvailableMatchCountCron();
        return echoRespose(match, HttpStatus.OK);
    }


    @GetMapping(path = "/generate_contest_pdf_cron/{match_contest_id}")
    public ResponseEntity<Object> generateContestPdfCron(HttpServletRequest request,
                                                         @PathVariable(name = "match_contest_id") int matchContestId) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse contestPdf = cronService.generateContestPdfCron(matchContestId);
        return echoRespose(contestPdf, HttpStatus.OK);
    }

    @GetMapping(path = "/generate_player_selected_count/{match_unique_id}")
    public ResponseEntity<Object> generatePlayerSelected(HttpServletRequest request,
                                                         @PathVariable(name = "match_unique_id") int match_unique_id) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse match = cronService.generatePlayerSelectedCount(match_unique_id);
        return echoRespose(match, HttpStatus.OK);
    }


}
