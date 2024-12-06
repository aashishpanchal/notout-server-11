package com.choic11.controller;

import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.TblState;
import com.choic11.model.customer.TblCustomer;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.CustomerService;
import com.choic11.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/apis")
public class UnauthenticateController extends BaseController {

    @Autowired
    StateService stateService;
    @Autowired
    CustomerService customerService;

    @RequestMapping(path = "/states/{countryId}")
    public ResponseEntity<Object> list(HttpServletRequest request, @PathVariable("countryId") int countryId) {

        BaseRequest baseRequest = new BaseRequest(request);
        List<TblState> abc = stateService.listAllState(countryId);
        if (abc.size() == 0) {
            return echoRespose(new BaseResponse(0, false, "No record Found.", abc), HttpStatus.OK);
        }

        return echoRespose(new BaseResponse(0, false, "States list", abc), HttpStatus.OK);

    }

    @PostMapping("check_user")
    public ResponseEntity<Object> list(HttpServletRequest request, @RequestPart(required = false) String username,
                                       @RequestPart(required = false) String type) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("username", "type");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.checkUser(username, type);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("newuser")
    public ResponseEntity<Object> newuser(HttpServletRequest request, @RequestPart(required = false) String firstname,
                                          @RequestPart(required = false) String country_mobile_code, @RequestPart(required = false) String phone,
                                          @RequestPart(required = false) String email, @RequestPart(required = false) String password,
                                          @RequestPart(required = false) String referral_code) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("firstname", "country_mobile_code", "phone",
                "email", "password");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        TblCustomer customer = TblCustomer.getInstance();
        customer.setCountryMobileCode(country_mobile_code);
        customer.setFirstname(firstname);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setUsedReferralCode(referral_code);

        BaseResponse response = customerService.newUser(customer);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("verifyotp")
    public ResponseEntity<Object> verifyotp(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("otp", "type");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        if (baseRequest.getParam("type").equals("F")) {

            errorFields = baseRequest.verifyRequiredParams("country_mobile_code", "phone", "password");
            if (errorFields.size() > 0) {
                return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
            }

        } else if (baseRequest.getParam("type").equals("FE")) {

            errorFields = baseRequest.verifyRequiredParams("email", "password");
            if (errorFields.size() > 0) {
                return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
            }

            baseRequest.setParam("country_mobile_code", "");

        } else {

            errorFields = baseRequest.verifyRequiredParams("country_mobile_code", "phone");
            if (errorFields.size() > 0) {
                return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
            }

        }

        BaseResponse response = customerService.verifyOtp(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("forgotpassword")
    public ResponseEntity<Object> forgotpassword(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("country_mobile_code", "phone");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.forgotPassword(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("login")
    public ResponseEntity<Object> login(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("email", "password");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.login(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("update_token")
    public ResponseEntity<Object> updateToken(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("device_token");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.updateToken(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("forgotpassword_email")
    public ResponseEntity<Object> forgotPasswordEmail(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("email");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.forgotPasswordEmail(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("social_login")
    public ResponseEntity<Object> socialLogin(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        String email = baseRequest.getParam("email");

        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("social_id", "firstname", "social_type");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }


        String social_type = baseRequest.getParam("social_type");

        HashSet<String> allowedSocialTypes = new HashSet<String>();
        allowedSocialTypes.add("F");
        allowedSocialTypes.add("G");

        if (!allowedSocialTypes.contains(social_type)) {
            return echoRespose(new BaseResponse(0, false, "Invalid social_type.", null), HttpStatus.OK);
        }

        if (!Util.isEmpty(email) && !Util.isValidEmail(email)) {
            return echoRespose(new BaseResponse(0, false, "Invalid email.", null), HttpStatus.OK);
        }

        BaseResponse response = customerService.socialLogin(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("get_referral_settings")
    public ResponseEntity<Object> getReferralSettings(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getReferralSettings();
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("get_app_custom_icons")
    public ResponseEntity<Object> getAppCustomIcons(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getAppCustomIcons();
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("/get_quotations")
    public ResponseEntity<Object> getQuotations(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);

        BaseResponse response = customerService.getQuotations(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("/get_games")
    public ResponseEntity<Object> getGames(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);

        BaseResponse response = customerService.getGames(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("check_app_version_new")
    public ResponseEntity<Object> checkAppVersionNew(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("version_code");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.checkAppVersionNew(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

}
