package com.choic11.controller.admin;

import com.choic11.controller.BaseController;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/adminapis")
public class AdminApiController extends BaseController {

    @Autowired
    AdminService adminService;

    @PostMapping(path = "/customer_withdraw_amount_from_bank")
    public ResponseEntity<Object> customerWithdrawAmountFromBank(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("entry_id", "action", "otp", "user_id", "affiliate");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = adminService.customerWithdrawAmountFromBank(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }
}
