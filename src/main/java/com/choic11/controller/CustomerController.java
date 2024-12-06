package com.choic11.controller;

import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {
    @Autowired
    CustomerService customerService;

    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);

        BaseResponse response = customerService.logOut(baseRequest.authUserId, baseRequest.HEADER_deviceid,
                baseRequest.HEADER_devicetype);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("add_bankdetail")
    public ResponseEntity<Object> addBankDetail(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("account_number", "ifsc", "name", "image");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.addBankDetail(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("add_pancard")
    public ResponseEntity<Object> addPanCard(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("image", "number", "name", "dob", "state");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.addPanCard(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("wallet_recharge_coupons")
    public ResponseEntity<Object> walletRechargeCoupons(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.walletRechargeCoupons(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("wallet_recharge")
    public ResponseEntity<Object> walletRecharge(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("amount");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        Object response = customerService.walletRecharge(baseRequest);
        if (response instanceof BaseResponse) {
            return echoRespose(response, HttpStatus.OK);
        } else {
            return echoResposeString(response, HttpStatus.OK);
        }

    }
    @PostMapping("wallet_recharge_sdk")
    public ResponseEntity<Object> walletRechargeSdk(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("amount");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        Object response = customerService.walletRechargeSdk(baseRequest);
        if (response instanceof BaseResponse) {
            return echoRespose(response, HttpStatus.OK);
        } else {
            return echoRespose(response, HttpStatus.OK);
        }

    }

    @PostMapping("apply_promocode")
    public ResponseEntity<Object> applyPromoCode(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("promocode", "amount");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.applyPromoCode(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("change_password")
    public ResponseEntity<Object> changePassword(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("old_password", "password");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.changePassword(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("change_profile_picture")
    public ResponseEntity<Object> changeProfilePicture(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.changeProfilePicture(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("/create_customer_enquiry")
    public ResponseEntity<Object> createCustomerEnquiry(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("subject", "message");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.createCustomerEnquiry(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("customer_team_name_update")
    public ResponseEntity<Object> customerTeamNameUpdate(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("team_name");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.customerTeamNameUpdate(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("/get_customer_profile")
    public ResponseEntity<Object> getCustomerProfile(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("customer_id");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.getCustomerProfile(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("/get_customer_recent_series_leaderboard")
    public ResponseEntity<Object> getCustomerRecentSeriesLeaderboard(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getCustomerRecentSeriesLeaderboard(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("/get_playing_history")
    public ResponseEntity<Object> getPlayingHistory(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getPlayingHistory(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("get_profile")
    public ResponseEntity<Object> getProfile(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getProfile(baseRequest.authUserId);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("get_profile_pictures")
    public ResponseEntity<Object> getProfilePictures(HttpServletRequest request) {

        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getProfilePictures(baseRequest);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("get_refer_earn")
    public ResponseEntity<Object> getReferEarn(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);

        BaseResponse response = customerService.getReferEarn(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("get_refer_earn_detail")
    public ResponseEntity<Object> getReferEarnDetail(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);

        BaseResponse response = customerService.getReferEarnDetail(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("send_otp_mobile")
    public ResponseEntity<Object> sendOtpMobile(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("country_mobile_code", "phone", "type");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        if (baseRequest.getParam("type").equals("PT")) {
            BaseResponse response = customerService.sendOtpMobilePaytm(baseRequest);
            return echoRespose(response, HttpStatus.OK);
        } else if (baseRequest.getParam("type").equals("SP")) {
            BaseResponse response = customerService.sendOtpMobile(baseRequest);
            return echoRespose(response, HttpStatus.OK);
        }

        BaseResponse response = new BaseResponse(0, true, "Unable to proceed.", null);
        return echoRespose(response, HttpStatus.OK);

    }


    @PostMapping("send_otp_mobile_paytm")
    public ResponseEntity<Object> sendOtpMobilePaytm(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("country_mobile_code", "phone");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.sendOtpMobilePaytm(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("update_profile")
    public ResponseEntity<Object> updateProfile(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("firstname", "country_mobile_code", "phone",
                "email");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        BaseResponse response = customerService.updateProfile(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("update_verify_email")
    public ResponseEntity<Object> eupdateVerifyEmail(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("email", "is_social");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        if (baseRequest.getParam("is_social").equals("Y")) {

            ArrayList<String> errorFieldss = baseRequest.verifyRequiredParams("social_type");
            if (errorFieldss.size() > 0) {
                return echoRespose(baseRequest.generateRequiredParamsResponse(errorFieldss), HttpStatus.OK);
            }

        }

        BaseResponse response = customerService.updateVerifyEmail(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("update_verify_mobile")
    public ResponseEntity<Object> updateVerifyMobile(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("otp", "type", "country_mobile_code", "phone");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }
        if (baseRequest.getParam("type").equals("PT")) {
            BaseResponse response = customerService.updateVerifyMobilePaytm(baseRequest);
            return echoRespose(response, HttpStatus.OK);
        } else {
            BaseResponse response = customerService.updateVerifyMobile(baseRequest);
            return echoRespose(response, HttpStatus.OK);
        }


    }


    @PostMapping("update_verify_mobile_paytm")
    public ResponseEntity<Object> updateVerifyMobilePaytm(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("otp", "type", "country_mobile_code", "phone");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.updateVerifyMobilePaytm(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }


    @PostMapping("get_customer_wallet_detail")
    public ResponseEntity<Object> getCustomerWalletDetail(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getCustomerWalletDetail(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("get_customer_wallet_history_filter/{page_no}/{type}")
    public ResponseEntity<Object> getCustomerWalletHistoryFilter(HttpServletRequest request,
                                                                 @PathVariable("page_no") int page_no, @PathVariable("type") String type) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getCustomerWalletHistoryFilter(baseRequest, page_no, type);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("customer_withdraw_amount")
    public ResponseEntity<Object> customerWithdrawAmount(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("amount");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.customerWithdrawAmount(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("get_customer_withdraw_history/{page_no}")
    public ResponseEntity<Object> getCustomerWithdrawHistory(HttpServletRequest request,
                                                             @PathVariable("page_no") int page_no) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getCustomerWithdrawHistory(baseRequest, page_no);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("/get_notifications/{page_no}")
    public ResponseEntity<Object> getNotifications(HttpServletRequest request, @PathVariable("page_no") int page_no) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getNotifications(baseRequest, page_no);
        return echoRespose(response, HttpStatus.OK);

    }

    @PostMapping("/get_affiliate_history")
    public ResponseEntity<Object> getAffiliateHistory(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getAffiliateHistory(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("coupon_list/{page_no}")
    public ResponseEntity<Object> couponList(HttpServletRequest request,
                                             @PathVariable("page_no") int page_no) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.getCouponList(baseRequest, page_no);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("redeem_coupon")
    public ResponseEntity<Object> redeemCoupon(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        ArrayList<String> errorFields = baseRequest.verifyRequiredParams("coupon");
        if (errorFields.size() > 0) {
            return echoRespose(baseRequest.generateRequiredParamsResponse(errorFields), HttpStatus.OK);
        }

        BaseResponse response = customerService.redeemCoupon(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping("wallet_recharge_promocodes")
    public ResponseEntity<Object> walletRechargePromocodes(HttpServletRequest request) {
        BaseRequest baseRequest = new BaseRequest(request);
        BaseResponse response = customerService.walletRechargePromocodes(baseRequest);
        return echoRespose(response, HttpStatus.OK);
    }

    @PostMapping(path = "/get_withdraw_slab")
    public ResponseEntity<Object> getWithdrawSlab(HttpServletRequest request) {
        BaseResponse response = customerService.getWithdrawSlab();
        return echoRespose(response, HttpStatus.OK);
    }
}
