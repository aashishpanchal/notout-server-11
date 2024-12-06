package com.choic11.testing;


import com.choic11.Util;
import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.repository.cricket.CronRepository;
import com.choic11.service.cricket.ContestService;
import com.choic11.service.cricket.CronService;
import com.choic11.service.cricket.MatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/testapis")
public class TestController extends BaseController {

    @Autowired
    TestService testService;

    @Autowired
    ContestService contestService;

    @Autowired
    MatchesService matchesService;

    @Autowired
    CronRepository cronRepository;

    @Autowired
    CronService cronService;

    @GetMapping("check_abondant_contest/{matchUniqueId}")
    public ResponseEntity<Object> checkAbondantContets(HttpServletRequest request,
                                                       @PathVariable("matchUniqueId") int matchUniqueId) {

        BaseRequest baseRequest = new BaseRequest(request);
        LinkedHashMap<String,Object> data=new LinkedHashMap<>();
        data.put("uniqueId",matchUniqueId);
//        cronService.startAbondantContestForMatchTest(matchUniqueId);
        return echoRespose(data, HttpStatus.OK);
    }

    @PostMapping("test_email")
    public ResponseEntity<Object> testEmail(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = testService.testEmail(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("test_sms")
    public ResponseEntity<Object> testSms(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = testService.testSms(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("test_notification")
    public ResponseEntity<Object> testNotification(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = testService.testNotification(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("callCreateDuplicateContest/{matchContestId}")
    public ResponseEntity<Object> callCreateDuplicateContest(HttpServletRequest request,
                                                             @PathVariable("matchContestId") int matchContestId) {

        BaseRequest baseRequest = new BaseRequest(request);
        contestService.callCreateDuplicateContest(matchContestId);
        return echoRespose(new BaseResponse(0, false, "callCreateDuplicateContest", null), HttpStatus.OK);
    }

    @PostMapping("test_pdf")
    public ResponseEntity<Object> testPdf(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = testService.testPdf(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("generate_weeks")
    public ResponseEntity<Object> generateWeeks(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);

        long from = Long.parseLong(baseRequest.getParam("from"));
        long to = Long.parseLong(baseRequest.getParam("to"));
        List<HashMap<String, Object>> generateWeeks = Util.generateWeeks(from, to);
        BaseResponse response = new BaseResponse(0, false, "Test", generateWeeks);
        return echoRespose(response, HttpStatus.OK);
    }

}
