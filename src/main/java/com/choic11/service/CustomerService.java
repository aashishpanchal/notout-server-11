package com.choic11.service;

import com.choic11.AsyncConfiguration;
import com.choic11.GlobalConstant.AWSConstant;
import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.GlobalConstant.PaymentGatewayConstant;
import com.choic11.Util;
import com.choic11.fcm.FcmUtil;
import com.choic11.jwt.JwtTokenUtil;
import com.choic11.model.*;
import com.choic11.model.customer.*;
import com.choic11.model.response.BaseResponse;
import com.choic11.model.response.WithdrawalSlabBaseResponse;
import com.choic11.paymentgateway.PaymentUtil;
import com.choic11.repository.CustomerRepository;
import com.choic11.sms.SmsUtil;
import com.choic11.smtp.SmtpUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public BaseResponse checkUser(String username, String type) {
        TblCustomer customer = customerRepository.checkUser(username, type);
        if (customer == null) {
            return new BaseResponse(1, true, "No record Found.", null);
        }
        if (customer.isDeactive()) {
            return new BaseResponse(2, true, "User account is deactivated Please contact to admin.", null);
        }

        HashMap<String, Object> response = new HashMap();
        response.put("type", type);
        if (type.equals("M")) {
            String otpCode = sendOtp(customer.getPhone(), "L", customer.getCountryMobileCode(), "");
            response.put("otp", otpCode);
            response.put("phone", customer.getPhone());
            response.put("country_mobile_code", customer.getCountryMobileCode());

        } else {
            response.put("email", username);
        }
        return new BaseResponse(0, false, "User Detail.", response);

    }

    public BaseResponse newUser(TblCustomer customer) {

        TblCustomer alreadySavedCustomer = customerRepository.getCustomerIdByEmail(customer.getEmail(), 0);
        if (alreadySavedCustomer != null) {
            return new BaseResponse(1, true, "Email is already exist.", null);
        }

        alreadySavedCustomer = customerRepository.getCustomerIdByMobileNo(customer.getCountryMobileCode(),
                customer.getPhone(), 0);
        if (alreadySavedCustomer != null) {
            return new BaseResponse(1, true, "Phone number already exists.", null);
        }

        if (!Util.isEmpty(customer.getUsedReferralCode())) {

            alreadySavedCustomer = customerRepository.getUsedReferralCustomerId(customer.getUsedReferralCode());

            if (alreadySavedCustomer == null) {
                return new BaseResponse(1, true, "Referral code is invalid.", null);
            } else {
                customer.setUsedReferralUserId(alreadySavedCustomer.getId());
            }
        }

        String json = new Gson().toJson(customer);
        String otpCode = sendOtp(customer.getPhone(), "V", customer.getCountryMobileCode(), json);
        HashMap<String, Object> response = new HashMap();
        response.put("otp", otpCode);
        response.put("phone", customer.getPhone());
        response.put("country_mobile_code", customer.getCountryMobileCode());
        return new BaseResponse(0, false,
                "Verification code sent to " + customer.getCountryMobileCode() + customer.getPhone() + ".",
                response);
    }

    public BaseResponse verifyOtp(BaseRequest baseRequest) {

        String updateAndDeleteOtp = updateAndDeleteOtp(baseRequest.getParam("country_mobile_code"), baseRequest.getParam("phone"));

        if (!Util.isEmpty(updateAndDeleteOtp) && updateAndDeleteOtp.equals("EXPIRED")) {
            return new BaseResponse(1, true, "Otp expired due to invalid attemps.", null);
        }
        String type = baseRequest.getParam("type");

        int updateCount = customerRepository.updateTempCustomerToVerified(baseRequest.getParam("otp"), type,
                baseRequest.getParam("country_mobile_code"),
                type.equals("FE") ? baseRequest.getParam("email") : baseRequest.getParam("phone"));

        if (updateCount <= 0) {
            return new BaseResponse(1, true, "Invalid verification code.", null);
        }

        if (type.equals("F")) {
            // check email is verify or not
            TblCustomer customerPreviousDetail = customerRepository.getCustomerById(0, baseRequest.getParam("phone"), "");

            String md5Password = Util.convertToMD5(baseRequest.getParam("password"));
            customerRepository.updateCustomerPasswordByMobile(baseRequest.getParam("country_mobile_code"),
                    baseRequest.getParam("phone"), md5Password);

            // entry in customer update logs
            String action = "Update password by mobile.";
            customerRepository.saveCustomerUpdateLogs(0, baseRequest.getParam("phone"), "", customerPreviousDetail, action);

            return new BaseResponse(0, false, "Password has successfully changed.", null);
        } else if (type.equals("FE")) {
            // check email is verify or not
            TblCustomer customerPreviousDetail = customerRepository.getCustomerById(0, "", baseRequest.getParam("email"));

            String md5Password = Util.convertToMD5(baseRequest.getParam("password"));
            customerRepository.updateCustomerPasswordByEmail(baseRequest.getParam("email"), md5Password);

            // entry in customer update logs
            String action = "Update password by email.";
            customerRepository.saveCustomerUpdateLogs(0, "", baseRequest.getParam("email"), customerPreviousDetail, action);

            return new BaseResponse(0, false, "Password has successfully changed.", null);
        } else if (type.equals("L")) {

            TblCustomer alreadySavedCustomer = customerRepository.getCustomerIdByMobileNo(
                    baseRequest.getParam("country_mobile_code"), baseRequest.getParam("phone"), 0);
            if (alreadySavedCustomer == null) {
                return new BaseResponse(1, true, "Unable to proceed.", null);
            }

            customerRepository.saveCustomerDetailsToCustomerLogins(alreadySavedCustomer.getId(),
                    baseRequest.HEADER_deviceid, baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress);

            customerRepository.saveCustomerDetailsToCustomerLogs(alreadySavedCustomer.getId(),
                    baseRequest.HEADER_deviceid, baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress,
                    baseRequest.HEADER_deviceinfo, baseRequest.HEADER_appinfo);

            HashMap<String, Object> customerData = getUpdatedProfileData(alreadySavedCustomer.getId());

            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId((int) customerData.get("id"));
            String jwtToken = jwtTokenUtil.generateToken(tblCustomer);
            customerData.put("jwtToken", jwtToken);
            return new BaseResponse(0, false, "Customer has successfully login.", customerData);

        } else if (type.equals("V")) {
            TblTempcustomer dataByMobile = customerRepository.getDataByMobileFromTempCustomer(
                    baseRequest.getParam("phone"), baseRequest.getParam("country_mobile_code"));
            if (dataByMobile == null) {
                return new BaseResponse(1, true, "Unable to proceed.", null);
            }
            TblCustomer fromJson = new Gson().fromJson(dataByMobile.getCustomerData(), TblCustomer.class);
            String md5Password = Util.convertToMD5(fromJson.getPassword());
            fromJson.setPassword(md5Password);
            fromJson.setCreated(BigInteger.valueOf(Util.getCurrentTime()));
            fromJson.setIsPhoneVerified("Y");

            int usedReferralUserId = fromJson.getUsedReferralUserId();

            HashMap<String, String> referCashBonus = getReferCashBonus();
            Float usedReferralAmount = (float) 0;
            Float usedReferralAmountDeposit = (float) 0;
            Float applierReferralAmount = (float) 0;
            Float applierReferralAmountDeposit = (float) 0;
            if (!referCashBonus.isEmpty()) {
                applierReferralAmount = Float.parseFloat(referCashBonus.get("NEW_REGISTRATION"));
                applierReferralAmountDeposit = Float.parseFloat(referCashBonus.get("NEW_REGISTRATION_DEPOSIT"));
                if (usedReferralUserId > 0) {
                    usedReferralAmount = Float.parseFloat(referCashBonus.get("REFERRER"));
                    usedReferralAmountDeposit = Float.parseFloat(referCashBonus.get("REFERRER_DEPOSIT"));
                    applierReferralAmount = Float.parseFloat(referCashBonus.get("REGISTER_WITH_REFERRAL_CODE_(applier)"));
                    applierReferralAmountDeposit = Float.parseFloat(referCashBonus.get("REGISTER_WITH_REFERRAL_CODE_DEPOSIT_(applier)"));
                }
            }
            fromJson.setUsedRefferalAmount(usedReferralAmount);
            fromJson.setUsedRefferalAmountDeposit(usedReferralAmountDeposit);

            int customerId = customerRepository.saveCustomer(fromJson);

            if (customerId == 0) {
                return new BaseResponse(1, true, "Unable to proceed.", null);
            }

            customerRepository.saveCustomerDetailsToCustomerLogins(customerId, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress);

            if (applierReferralAmount > 0 || applierReferralAmountDeposit > 0) {
                updateCustomerWalletRegisterCashBonus(customerId, applierReferralAmount, applierReferralAmountDeposit);
            }

            customerRepository.saveCustomerDetailsToCustomerLogs(customerId, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress, baseRequest.HEADER_deviceinfo,
                    baseRequest.HEADER_appinfo);

            if (!Util.isEmpty(fromJson.getEmail())) {
                HashMap<String, Object> data = new HashMap<String, Object>();
                sendTemplatesInMail("customer_welcome", fromJson.getFirstname(), fromJson.getEmail(), data);
            }

            HashMap<String, Object> customerData = getUpdatedProfileData(customerId);
            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId((int) customerData.get("id"));
            String jwtToken = jwtTokenUtil.generateToken(tblCustomer);
            customerData.put("jwtToken", jwtToken);
            return new BaseResponse(0, false, "Customer has successfully registered.", customerData);
        }

        return null;
    }

    public BaseResponse forgotPassword(BaseRequest baseRequest) {

        TblCustomer alreadySavedCustomer = customerRepository
                .getCustomerIdByMobileNo(baseRequest.getParam("country_mobile_code"), baseRequest.getParam("phone"), 0);
        if (alreadySavedCustomer == null) {
            return new BaseResponse(1, true, "Mobile number does not exist.", null);
        }

        String otpCode = sendOtp(baseRequest.getParam("phone"), "F", baseRequest.getParam("country_mobile_code"), "");
        LinkedHashMap<String, String> response = new LinkedHashMap<String, String>();
        response.put("otp", otpCode);
        response.put("phone", baseRequest.getParam("phone"));
        response.put("country_mobile_code", baseRequest.getParam("country_mobile_code"));
        return new BaseResponse(0, false, "Verification code sent to " + baseRequest.getParam("country_mobile_code")
                + baseRequest.getParam("phone") + ".", response);

    }

    public BaseResponse login(BaseRequest baseRequest) {

        Long time = Util.getCurrentTime();
        String md5password = Util.convertToMD5(baseRequest.getParam("password"));
        TblCustomer loginDetail = customerRepository.loginDetail(baseRequest.getParam("email"));
        if (loginDetail == null) {
            return new BaseResponse(1, true, "Invalid Email Address.", null);
        }

        if (loginDetail.getRepeatCount() >= 5 && time <= (loginDetail.getWrongAttempCreate().longValue() + (1 * 60))) {
            return new BaseResponse(1, true, "Customer Account Locked for some time.", null);
        }
        int newRepeatCount = 0;
        if (!md5password.equals(loginDetail.getPassword())) {

            if (loginDetail.getRepeatCount() >= 5) {
                newRepeatCount = 1;
            } else {
                newRepeatCount = loginDetail.getRepeatCount() + 1;
            }
            customerRepository.updateWrongPasswordCount(baseRequest.getParam("email"), newRepeatCount);

            return new BaseResponse(1, true, "Invalid Password.", null);
        }
        if (!loginDetail.getStatus().equals("A")) {
            return new BaseResponse(1, true, "Customer Account Deactivated.", null);
        } else {
            customerRepository.updateWrongPasswordCount(baseRequest.getParam("email"), newRepeatCount);

            customerRepository.saveCustomerDetailsToCustomerLogins(loginDetail.getId(), baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress);
            customerRepository.saveCustomerDetailsToCustomerLogs(loginDetail.getId(), baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress, baseRequest.HEADER_deviceinfo,
                    baseRequest.HEADER_appinfo);

            HashMap<String, Object> updatedProfileData = getUpdatedProfileData(loginDetail.getId());
            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId((int) updatedProfileData.get("id"));
            String jwtToken = jwtTokenUtil.generateToken(tblCustomer);
            updatedProfileData.put("jwtToken", jwtToken);
            return new BaseResponse(0, false, "Login successfully.", updatedProfileData);
        }

    }

    public BaseResponse logOut(Integer authUserId, String headerDeviceId, String headerDeviceType) {
        String logOut = customerRepository.logOut(authUserId, headerDeviceId, headerDeviceType);
        if (logOut.equals("INVALID_USER_ACCESS")) {
            return new BaseResponse(1, false, "You have loggedout successfully.", null);
        }
        return new BaseResponse(0, false, "You have loggedout successfully.", null);

    }

    public BaseResponse updateToken(BaseRequest baseRequest) {
        customerRepository.updateToken(baseRequest.HEADER_deviceid, baseRequest.HEADER_devicetype,
                baseRequest.getParam("device_token"), baseRequest.HEADER_deviceinfo, baseRequest.HEADER_appinfo);
        return new BaseResponse(0, false, "Customer Token updated successfully.", null);
    }

    private String sendOtp(String mobileNo, String type, String mobile_code, String jsonString) {
        customerRepository.removeMobileFromTemp(mobileNo, mobile_code);
        String otp = "" + ((int) (Math.random() * 9000) + 1000);

        String template = "";
        if (type.equals("V")) {
            template = "verification";
        } else if (type.equals("L")) {
            template = "verification";
        } else if (type.equals("F")) {
            template = "verification";
        } else if (type.equals("SP")) {
            template = "verification";
        } else if (type.equals("PT")) {
            template = "verification";
        }
        customerRepository.insertInToTemp(mobileNo, type, mobile_code, jsonString, otp);
        HashMap<String, Object> data = new HashMap();
        data.put("otp", otp);
        sendTemplatesInSms(template, data, mobileNo, mobile_code);

        if (GlobalConstant.isProjectTypeProd()) {
            return "";
        } else {
            return otp;
        }
    }

    private String sendOtpEmail(String email, String type, String jsonstring) {

        customerRepository.removeEmailFromTemp(email);
        String otp = "" + ((int) (Math.random() * 9000) + 1000);

        String template = "";
        if (type.equals("FE")) {
            template = "forgot_password";
        }
        customerRepository.insertInToTemp(email, type, "", jsonstring, otp);
        HashMap<String, Object> data = new HashMap();
        data.put("otp", otp);
        sendTemplatesInMail(template, "", email, data);
        return otp;
    }

    private void sendTemplatesInMail(String templateTitle, String toName, String toEmail, HashMap<String, Object> data) {

        AsyncConfiguration.getEmailSendexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    TblTemplate template = customerRepository.getTemplate("E", templateTitle);
                    if (template != null) {
                        String content = template.getContent();
                        String subject = template.getSubject();
                        content = content.replace("{CUSTOMER_NAME}", toName);
                        if (data.containsKey("link")) {
                            content = content.replace("{LINK}", data.get("link").toString());
                        }

                        if (data.containsKey("fee")) {
                            content = content.replace("{FEE}", data.get("fee").toString());
                        }

                        if (data.containsKey("match_date")) {
                            content = content.replace("{MATCH_DATE}", data.get("match_date").toString());
                        }

                        if (data.containsKey("match_name")) {
                            content = content.replace("{MATCH_NAME}", data.get("match_name").toString());
                        }

                        if (data.containsKey("otp")) {
                            content = content.replace("{OTP}", data.get("otp").toString());
                        }

                        if (data.containsKey("message")) {
                            content = content.replace("{MESSAGE}", data.get("message").toString());
                        }
                        SmtpUtil.sendSmtpmail(subject, content, toEmail, toName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void sendTemplatesInSms(String templateTitle, HashMap<String, Object> data, String mobileno, String mobile_code) {
        AsyncConfiguration.getSmsSendexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    TblTemplate template = customerRepository.getTemplate("S", templateTitle);
                    if (template != null) {
                        String content = template.getContent();
                        if (data.containsKey("otp")) {
                            content = content.replace("{OTP}", data.get("otp").toString());
                            template.setContent(content);
                        }
                        SmsUtil.sendSms(template, mobileno, mobile_code, data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public HashMap<String, Object> getUpdatedProfileData(int id) {
        TblCustomer updatedProfileData = customerRepository.getUpdatedProfileData(id);

        HashMap<String, Object> response = new HashMap();

        response.put("slug", Util.encodeData(updatedProfileData.getSlug()));
        response.put("id", updatedProfileData.getId());
        response.put("is_affiliate", updatedProfileData.getIsAffiliate());
        response.put("created", updatedProfileData.getCreated().longValue());

        response.put("firstname", updatedProfileData.getFirstname());
        response.put("lastname", updatedProfileData.getLastname());
        response.put("fullname", updatedProfileData.getFirstname());
        if (!Util.isEmpty(updatedProfileData.getLastname())) {
            response.put("fullname", updatedProfileData.getFirstname() + " " + updatedProfileData.getLastname());
        }
        response.put("team_name", updatedProfileData.getTeamName());
        response.put("team_change", updatedProfileData.getTeamChange());
        response.put("email", updatedProfileData.getEmail());
        response.put("is_social", updatedProfileData.getIsSocial());
        response.put("social_type", updatedProfileData.getSocialType());
        response.put("social_id", updatedProfileData.getSocialId());
        response.put("country_mobile_code", updatedProfileData.getCountryMobileCode());
        response.put("phone", updatedProfileData.getPhone());
        response.put("paytmphone", updatedProfileData.getPaytmphone());
        response.put("follower_count", updatedProfileData.getFollowerCount());
        response.put("following_count", updatedProfileData.getFollowingCount());
        response.put("post_count", updatedProfileData.getPostCount());

        response.put("referral_code", updatedProfileData.getReferralCode());
        response.put("is_phone_verified", updatedProfileData.getIsPhoneVerified());
        response.put("is_email_verified", updatedProfileData.getIsEmailVerified());
        response.put("image", Util.generateImageUrl(updatedProfileData.getImage(), FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
        if (!Util.isEmpty(updatedProfileData.getExternalImage())) {
            response.put("image", updatedProfileData.getExternalImage());
        }
        response.put("dob", updatedProfileData.getDob());
        response.put("addressline1", updatedProfileData.getAddressline1());
        response.put("addressline2", updatedProfileData.getAddressline2());
        response.put("pincode", updatedProfileData.getPincode());
        response.put("country", null);
        if (updatedProfileData.getTblCountry() != null) {
            TblCountry tblCountry = updatedProfileData.getTblCountry();
            HashMap<String, Object> country = new HashMap();
            country.put("id", tblCountry.getId());
            country.put("name", tblCountry.getName());
            response.put("country", country);
        }
        response.put("state", null);
        if (updatedProfileData.getTblState() != null) {
            TblState tblState = updatedProfileData.getTblState();
            HashMap<String, Object> state = new HashMap();
            state.put("id", tblState.getId());
            state.put("name", tblState.getName());
            response.put("state", state);

        }
        response.put("pancard", null);
        if (updatedProfileData.getTblCustomerPaincard() != null) {
            TblCustomerPaincard tblCustomerPaincard = updatedProfileData.getTblCustomerPaincard();
            HashMap<String, Object> panCard = new HashMap();
            panCard.put("id", tblCustomerPaincard.getId());
            panCard.put("number", tblCustomerPaincard.getPainNumber());
            panCard.put("name", tblCustomerPaincard.getName());
            panCard.put("dob", tblCustomerPaincard.getDob());
            panCard.put("status", tblCustomerPaincard.getStatus());
            panCard.put("reason", tblCustomerPaincard.getReason());
            panCard.put("image", Util.generateImageUrl(tblCustomerPaincard.getImage(), FileUploadConstant.PANCARD_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));
            if (tblCustomerPaincard.getTblState() != null) {
                TblState tblState = tblCustomerPaincard.getTblState();
                HashMap<String, Object> pancardstate = new HashMap();
                pancardstate.put("id", tblState.getId());
                pancardstate.put("name", tblState.getName());
                panCard.put("state", pancardstate);
            } else {
                panCard.put("state", null);
            }
            response.put("pancard", panCard);
        }

        response.put("bankdetail", null);
        if (updatedProfileData.getTblCustomerBankdetail() != null) {

            TblCustomerBankdetail tblCustomerBankdetail = updatedProfileData.getTblCustomerBankdetail();
            HashMap<String, Object> bankDetail = new HashMap();
            bankDetail.put("id", tblCustomerBankdetail.getId());
            bankDetail.put("account_number", tblCustomerBankdetail.getAccountNumber());
            bankDetail.put("account_holder_name", tblCustomerBankdetail.getName());
            bankDetail.put("ifsc", tblCustomerBankdetail.getIfsc());
            bankDetail.put("status", tblCustomerBankdetail.getStatus());
            bankDetail.put("reason", tblCustomerBankdetail.getReason());
            bankDetail.put("image", Util.generateImageUrl(tblCustomerBankdetail.getImage(), FileUploadConstant.BANK_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL));
            response.put("bankdetail", bankDetail);
        }

        HashMap<String, Object> city = new HashMap();
        city.put("id", 0);
        city.put("name", updatedProfileData.getCity());
        response.put("city", city);

        Float actualWinningBalance = updatedProfileData.getWinningWallet() - updatedProfileData.getPendingWidWallet();
        if (actualWinningBalance < 0) {
            actualWinningBalance = 0f;
        }
        HashMap<String, Object> wallet = new HashMap();
        wallet.put("winning_wallet", actualWinningBalance);
        wallet.put("bonus_wallet", updatedProfileData.getBonusWallet());
        wallet.put("deposit_wallet", updatedProfileData.getDepositWallet());
        wallet.put("pending_wid_amount", updatedProfileData.getPendingWidWallet());
        response.put("wallet", wallet);

        HashMap<String, String> awsConstant = AWSConstant.getAwsConstant();

        HashMap<String, Object> aws = new HashMap();
        aws.put("AWS_REGION", awsConstant.get("AWS_REGION"));
        aws.put("AWS_BUCKET", awsConstant.get("AWS_BUCKET"));
        aws.put("AWS_KEY_NEW", Util.encodeData(awsConstant.get("AWS_KEY")));
        aws.put("AWS_SECRET_NEW", Util.encodeData(awsConstant.get("AWS_SECRET")));
        aws.put("PANCARD_IMAGE_PATH", FileUploadConstant.PANCARD_IMAGE_PATH);
        aws.put("BANK_IMAGE_PATH", FileUploadConstant.BANK_IMAGE_PATH);
        aws.put("CUSTOMER_IMAGE_PATH", FileUploadConstant.CUSTOMER_IMAGE_PATH);
        aws.put("CUSTOMERGALLERY_IMAGE_PATH", FileUploadConstant.CUSTOMERGALLERY_IMAGE_PATH);
        response.put("aws", aws);
        response.put("notification_counter", 0);
        HashMap<String, String> settingData = getSettingData();

        float MIN_WITHDRAWALS = Float.parseFloat(settingData.get("MIN_WITHDRAWALS"));
        float MAX_WITHDRAWALS = Float.parseFloat(settingData.get("MAX_WITHDRAWALS"));

        float INSTANT_MIN_WITHDRAWALS = Float.parseFloat(settingData.get("INSTANT_MIN_WITHDRAWALS"));
        float INSTANT_MAX_WITHDRAWALS = Float.parseFloat(settingData.get("INSTANT_MAX_WITHDRAWALS"));
        float INSTANT_SERVICE_TAX = Float.parseFloat(settingData.get("INSTANT_SERVICE_TAX"));
        float INSTANT_GST = Float.parseFloat(settingData.get("INSTANT_GST"));
        String INSTANT_WITHDRAWAL_IS_AVAILABLE = settingData.get("INSTANT_WITHDRAWAL_IS_AVAILABLE");

        float PAYTM_MIN_WITHDRAWALS = Float.parseFloat(settingData.get("PAYTM_MIN_WITHDRAWALS"));
        float PAYTM_MAX_WITHDRAWALS = Float.parseFloat(settingData.get("PAYTM_MAX_WITHDRAWALS"));
        float PAYTM_SERVICE_TAX = Float.parseFloat(settingData.get("PAYTM_SERVICE_TAX"));
        float PAYTM_GST = Float.parseFloat(settingData.get("PAYTM_GST"));
        String PAYTM_WITHDRAWAL_IS_AVAILABLE = settingData.get("PAYTM_WITHDRAWAL_IS_AVAILABLE");

        String COMPANY = settingData.get("COMPANY");
        String COMPANY_EMAIL = settingData.get("COMPANY_EMAIL");
        String COMPANY_ADDRESS = settingData.get("COMPANY_ADDRESS");
        String COMPANY_FACEBOOK_URL = settingData.get("COMPANY_FACEBOOK_URL");
        String COMPANY_INSTAGRAM_URL = settingData.get("COMPANY_INSTAGRAM_URL");
        String COMPANY_TWITTER_URL = settingData.get("COMPANY_TWITTER_URL");
        String COMPANY_WHATSAPP_URL = settingData.get("COMPANY_WHATSAPP_URL");
        String COMPANY_TELEGRAM_URL = settingData.get("COMPANY_TELEGRAM_URL");

        HashMap<String, Object> totalTaxPercent = getTotalTaxPercent();
        Float totalTax = (Float) totalTaxPercent.get("total_tax");

        String WINNING_BREAKUP_MESSAGE = GlobalConstant.WINNING_BREAKUP_MESSAGE.replace("{TOTAL_TAX}", totalTax + "");

        String JOIN_CONTEST_MESSAGE = GlobalConstant.JOIN_CONTEST_MESSAGE;

        String PROFILE_UPDATE_MESSAGE = GlobalConstant.PROFILE_UPDATE_MESSAGE;

        String WITHDRAW_AMOUNT_MSG = "Min " + MIN_WITHDRAWALS + " & Max " + MAX_WITHDRAWALS + " allowed per day.";
        String WITHDRAW_AMOUNT_MSG_INSTANT = "Min " + INSTANT_MIN_WITHDRAWALS + " & Max " + INSTANT_MAX_WITHDRAWALS + " allowed per day.";

        String WITHDRAW_AMOUNT_MSG_PAYTM = "Min " + PAYTM_MIN_WITHDRAWALS + " & Max " + PAYTM_MAX_WITHDRAWALS + " allowed per day.";

        String INSTANT_AMOUNT_MSG = "Service fee " + INSTANT_SERVICE_TAX + "% +" + INSTANT_GST + "% GST will be deducted.";
        String INSTANT_TOOLTIP_MSG = "Withdraw amount Min. " + GlobalConstant.CURRENCY_SYMBOL + INSTANT_MIN_WITHDRAWALS + " & Max. " + GlobalConstant.CURRENCY_SYMBOL + INSTANT_MAX_WITHDRAWALS + " allowed per day & Only single transaction can be made during working hours. Instant withdraw facility available from 9 AM to 9 PM on working days. On Sunday & Bank Holidays Instant withdraw facility will not be available. T&C apply.";

        String PAYTM_AMOUNT_MSG = "Service fee " + PAYTM_SERVICE_TAX + "% +" + PAYTM_GST + "% GST will be deducted.";

        String PAYTM_TOOLTIP_MSG = "Withdraw amount Min. " + GlobalConstant.CURRENCY_SYMBOL + PAYTM_MIN_WITHDRAWALS + " & Max. " + GlobalConstant.CURRENCY_SYMBOL + PAYTM_MAX_WITHDRAWALS + " allowed per day & Only single transaction can be made during working hours. Paytm withdraw facility available from 9 AM to 9 PM on working days. T&C apply.";

        String PAYTM_WITHDRAWL_MESSAGE = "Note : In case of Paytm,Maximum withdraw of Rs." + PAYTM_MAX_WITHDRAWALS + " is allowed per day.\nFor any Paytm withdraw " + PAYTM_SERVICE_TAX + "% + " + PAYTM_GST + "% transaction charges will be levied on the withdrawn amount.";

        HashMap<String, Object> settings = new HashMap();
        settings.put("WITHDRAW_AMOUNT_MIN", MIN_WITHDRAWALS);
        settings.put("WITHDRAW_AMOUNT_MAX", MAX_WITHDRAWALS);
        settings.put("WINNING_BREAKUP_MESSAGE", WINNING_BREAKUP_MESSAGE);
        settings.put("JOIN_CONTEST_MESSAGE", JOIN_CONTEST_MESSAGE);
        settings.put("PROFILE_UPDATE_MESSAGE", PROFILE_UPDATE_MESSAGE);
        settings.put("COMPANY", COMPANY);
        settings.put("COMPANY_EMAIL", COMPANY_EMAIL);
        settings.put("COMPANY_ADDRESS", COMPANY_ADDRESS);
        settings.put("COMPANY_FACEBOOK_URL", COMPANY_FACEBOOK_URL);
        settings.put("COMPANY_INSTAGRAM_URL", COMPANY_INSTAGRAM_URL);
        settings.put("COMPANY_TWITTER_URL", COMPANY_TWITTER_URL);
        settings.put("COMPANY_WHATSAPP_URL", COMPANY_WHATSAPP_URL);
        settings.put("COMPANY_TELEGRAM_URL", COMPANY_TELEGRAM_URL);
        settings.put("INSTANT_WITHDRAW_AMOUNT_MIN", INSTANT_MIN_WITHDRAWALS);
        settings.put("INSTANT_WITHDRAW_AMOUNT_MAX", INSTANT_MAX_WITHDRAWALS);
        settings.put("INSTANT_SERVICE_TAX", INSTANT_SERVICE_TAX);
        settings.put("INSTANT_GST", INSTANT_GST);
        settings.put("PAYTM_MIN_WITHDRAWALS", PAYTM_MIN_WITHDRAWALS);
        settings.put("PAYTM_MAX_WITHDRAWALS", PAYTM_MAX_WITHDRAWALS);
        settings.put("PAYTM_SERVICE_TAX", PAYTM_SERVICE_TAX);
        settings.put("PAYTM_GST", PAYTM_GST);
        settings.put("WITHDRAW_AMOUNT_MSG", WITHDRAW_AMOUNT_MSG);
        settings.put("WITHDRAW_AMOUNT_MSG_INSTANT", WITHDRAW_AMOUNT_MSG_INSTANT);
        settings.put("WITHDRAW_AMOUNT_MSG_PAYTM", WITHDRAW_AMOUNT_MSG_PAYTM);
        settings.put("INSTANT_AMONUT_MSG", INSTANT_AMOUNT_MSG);
        settings.put("INSTANT_TOOLTIP_MSG", INSTANT_TOOLTIP_MSG);
        settings.put("PAYTM_AMONUT_MSG", PAYTM_AMOUNT_MSG);
        settings.put("PAYTM_TOOLTIP_MSG", PAYTM_TOOLTIP_MSG);
        settings.put("INSTANT_WITHDRAWAL_IS_AVAILABLE", INSTANT_WITHDRAWAL_IS_AVAILABLE);
        settings.put("PAYTM_WITHDRAWAL_IS_AVAILABLE", PAYTM_WITHDRAWAL_IS_AVAILABLE);
        settings.put("PAYTM_WITHDRAWL_MESSAGE", PAYTM_WITHDRAWL_MESSAGE);
        response.put("settings", settings);

        return response;
    }

    public HashMap<String, String> getSettingData() {
        List<TblSetting> settingData2 = customerRepository.getSettingData();

        HashMap<String, String> settingData = new HashMap();
        for (TblSetting tblSetting : settingData2) {
            settingData.put(tblSetting.getKey(), tblSetting.getValue());
        }
        return settingData;

    }

    public HashMap<String, Object> getTotalTaxPercent() {
        List<TblTaxes> getTotalTaxPercent = customerRepository.getTotalTaxPercent();
        List<HashMap<String, Object>> response = new ArrayList<HashMap<String, Object>>();
        float totalTax = 0;
        for (TblTaxes tblTaxes : getTotalTaxPercent) {
            HashMap<String, Object> taxes = new HashMap();
            taxes.put("name", tblTaxes.getName());
            taxes.put("value", tblTaxes.getValue());
            totalTax += tblTaxes.getValue();
            response.add(taxes);
        }

        HashMap<String, Object> returnArray = new HashMap();
        returnArray.put("total_tax", totalTax);
        returnArray.put("taxes", response);

        return returnArray;
    }

    public void updateCustomerWalletRegisterCashBonus(int customerId, float amount, float amountDeposit) {
        Long time = Util.getCurrentTime();

        if (amount > 0) {
            String walletType = "bonus_wallet";
            String TransactionType = "CREDIT";
            String Type = "REGISTER_CASH_BONUS";
            String transaction_id = "RECBWALL" + time + customerId;
            String description = customerId + " Received Register cash bonus amount " + amount + ".";

            String walletName = GlobalConstant.WALLET_TYPE.get(walletType);

            Float previousAmount = customerRepository.getCustomerWalletAmount(customerId, walletType);
            Float currentAmount = previousAmount + amount;
            customerRepository.updateWallet(customerId, amount, walletType, TransactionType);

            TblCustomerWalletHistory instance = TblCustomerWalletHistory.getInstance();
            instance.setCustomerId(customerId);
            instance.setWalletType(walletName);
            instance.setPreviousAmount(previousAmount);
            instance.setAmount(amount);
            instance.setCurrentAmount(currentAmount);
            instance.setTransactionType(TransactionType);
            instance.setType(Type);
            instance.setTransactionId(transaction_id);
            instance.setDescription(description);
            instance.setStatus("S");
            instance.setSportId(-1);
            instance.setCreated(BigInteger.valueOf(time));

            Integer insertWalletHistory = customerRepository.insertWalletHistory(instance);

            JSONObject notificationData = new JSONObject();
            notificationData.put("noti_type", "register_cash_bonus");
            String alertMessage = "Woohoo! Got " + GlobalConstant.CURRENCY_SYMBOL + amount + " Cash Bonus.";
            sendNotificationAndSave(notificationData, alertMessage, true, new Integer[]{customerId});
        }

        if (amountDeposit > 0) {
            String walletType = "deposit_wallet";
            String TransactionType = "CREDIT";
            String Type = "REGISTER_CASH_BONUS";
            String transaction_id = "RECDWALL" + time + customerId;
            String description = customerId + " Received Register deposit amount " + amountDeposit + ".";

            String walletName = GlobalConstant.WALLET_TYPE.get(walletType);

            Float previousAmount = customerRepository.getCustomerWalletAmount(customerId, walletType);
            Float currentAmount = previousAmount + amountDeposit;
            customerRepository.updateWallet(customerId, amountDeposit, walletType, TransactionType);

            TblCustomerWalletHistory instance = TblCustomerWalletHistory.getInstance();
            instance.setCustomerId(customerId);
            instance.setWalletType(walletName);
            instance.setPreviousAmount(previousAmount);
            instance.setAmount(amountDeposit);
            instance.setCurrentAmount(currentAmount);
            instance.setTransactionType(TransactionType);
            instance.setType(Type);
            instance.setTransactionId(transaction_id);
            instance.setDescription(description);
            instance.setStatus("S");
            instance.setSportId(-1);
            instance.setCreated(BigInteger.valueOf(time));

            Integer insertWalletHistory = customerRepository.insertWalletHistory(instance);

            JSONObject notificationData = new JSONObject();
            notificationData.put("noti_type", "register_cash_bonus");
            String alertMessage = "Woohoo! Got " + GlobalConstant.CURRENCY_SYMBOL + amount + " Deposit amount.";
            sendNotificationAndSave(notificationData, alertMessage, true, new Integer[]{customerId});
        }
    }

    public void callFcm(JSONObject notificationData, String alertMessage, Integer[] customerId) {
        List<TblCustomerLogin> getAndroidTokens = customerRepository.GetAndroidTokens(customerId);
        if (getAndroidTokens != null) {
            List<List<String>> androidTokens = new ArrayList<List<String>>();
            List<String> chunkedAndroidTokens = new ArrayList<String>();
            for (TblCustomerLogin tblCustomerLogin : getAndroidTokens) {
                if (chunkedAndroidTokens.size() == 900) {
                    androidTokens.add(chunkedAndroidTokens);
                    chunkedAndroidTokens = new ArrayList<String>();
                }

                chunkedAndroidTokens.add(tblCustomerLogin.getDeviceToken());
            }

            if (chunkedAndroidTokens.size() > 0) {
                androidTokens.add(chunkedAndroidTokens);
            }

            for (List<String> registrationIds : androidTokens) {
                FcmUtil.sendNotification(notificationData, registrationIds, alertMessage,
                        notificationData.get("noti_type").toString(), "A");
            }
        }


        List<TblCustomerLogin> getIosTokens = customerRepository.GetIosTokens(customerId);
        if (getIosTokens != null) {
            List<List<String>> iosTokens = new ArrayList<List<String>>();
            List<String> chunkedIosTokens = new ArrayList<String>();
            for (TblCustomerLogin tblCustomerLogin : getIosTokens) {
                if (chunkedIosTokens.size() == 900) {
                    iosTokens.add(chunkedIosTokens);
                    chunkedIosTokens = new ArrayList<String>();
                }

                chunkedIosTokens.add(tblCustomerLogin.getDeviceToken());
            }

            if (chunkedIosTokens.size() > 0) {
                iosTokens.add(chunkedIosTokens);
            }

            for (List<String> registrationIds : iosTokens) {
                FcmUtil.sendNotification(notificationData, registrationIds, alertMessage,
                        notificationData.get("noti_type").toString(), "I");
            }
        }

    }

    public void sendNotificationAndSave(JSONObject notificationData, String alertMessage, boolean dbsave, Integer[] customerId) {

        if (dbsave) {

            String userIds = Arrays.toString(customerId);
            userIds = userIds.substring(1, userIds.length() - 1);

            TblNotification tblNotification = new TblNotification();
            tblNotification.setTitle(GlobalConstant.APP_NAME);
            tblNotification.setUsersId(userIds);
            tblNotification.setNotification(alertMessage);
            tblNotification.setSenderType("APP");
            tblNotification.setCreated(BigInteger.valueOf(Util.getCurrentTime()));
            tblNotification.setIsDeleted("N");
            tblNotification.setIsPromotional("0");
            tblNotification.setSenderId(0);
            customerRepository.saveNotification(tblNotification);

        }

        AsyncConfiguration.getNotificationSendexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    callFcm(notificationData, alertMessage, customerId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendNotificationAndSaveCron(List<HashMap<String, Object>> matchLineup) {

        AsyncConfiguration.getNotificationSendexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    for (HashMap<String, Object> hashMap : matchLineup) {
                        int id = (int) hashMap.get("id");
                        String alertMessage = (String) hashMap.get("alertMessage");
                        String message = (String) hashMap.get("message");
                        String userIds = (String) hashMap.get("userIds");
                        String notiType = (String) hashMap.get("notiType");
                        String[] split = userIds.split(",");
                        Integer[] customerids = new Integer[split.length];
                        for (int i = 0; i < split.length; i++) {
                            customerids[i] = Integer.parseInt(split[i]);
                        }
                        boolean needSaveInDb = false;
                        if (notiType.equals("win_contest") ||  notiType.equals("win_leaderboard") ||
                                notiType.equals("contest_ab")
                                || notiType.equals("referral_cash_bonus") || notiType.equals("customer_rmaff")) {
                            needSaveInDb = true;
                        }
                        if (needSaveInDb) {
                            TblNotification tblNotification = new TblNotification();
                            tblNotification.setTitle(GlobalConstant.APP_NAME);
                            tblNotification.setUsersId(userIds);
                            tblNotification.setNotification(alertMessage);
                            tblNotification.setSenderType("APP");
                            tblNotification.setCreated(BigInteger.valueOf(Util.getCurrentTime()));
                            tblNotification.setIsDeleted("N");
                            tblNotification.setIsPromotional("0");
                            tblNotification.setSenderId(0);
                            customerRepository.saveNotification(tblNotification);
                        }
                        callFcm(new JSONObject(message), alertMessage, customerids);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public HashMap<String, String> getReferCashBonus() {
        List<TblReferralCashBonus> referCashBonus = customerRepository.getReferCashBonus();

        HashMap<String, String> output = new HashMap();
        for (TblReferralCashBonus tblReferralCashBonus : referCashBonus) {
            output.put(tblReferralCashBonus.getKey(), tblReferralCashBonus.getValue());

        }
        return output;
    }

    private String updateAndDeleteOtp(String country_mobile_code, String phone) {

        TblTempcustomer updateAndDeleteOtp = customerRepository.updateAndDeleteOtp(country_mobile_code, phone);
        if (updateAndDeleteOtp != null) {

            int repeatCount = updateAndDeleteOtp.getRepeatCount();
            if (repeatCount > 6) {
                return "EXPIRED";
            }
        }
        return "";

    }

    public BaseResponse getCustomerWalletDetail(BaseRequest baseRequest) {
        LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerRepository
                .getUpdatedWalletData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Customer wallet detail.", updatedWalletData);

    }

    public BaseResponse getCustomerWalletHistoryFilter(BaseRequest baseRequest, int page_no, String type) {
        HashSet<String> array = new HashSet<String>();
        if (type.equals("All")) {
            array.add("CUSTOMER_JOIN_CONTEST");
            array.add("CUSTOMER_WIN_CONTEST");
            array.add("CUSTOMER_WIN_LEADERBOARD");
            array.add("CUSTOMER_MR_RECEIVED");
            array.add("CUSTOMER_REFUND_ABCONTEST");
            array.add("CUSTOMER_REFUND_CONTEST");
            array.add("CUSTOMER_WALLET_RECHARGE");
            array.add("CUSTOMER_RECEIVED_REFCB");
            array.add("CUSTOMER_RECEIVED_RCB");
            array.add("CUSTOMER_RECEIVED_RCBD");
            array.add("REGISTER_CASH_BONUS");
            array.add("WALLET_WITHDRAW_ADMIN");
            array.add("WALLET_RECHARGE_ADMIN");
        } else if (type.equals("Join")) {
            array.add("CUSTOMER_JOIN_CONTEST");
        } else if (type.equals("Win")) {
            array.add("CUSTOMER_WIN_CONTEST");
            array.add("CUSTOMER_WIN_LEADERBOARD");
            array.add("CUSTOMER_MR_RECEIVED");
            array.add("WALLET_WITHDRAW_ADMIN");
            array.add("WALLET_RECHARGE_ADMIN");
        } else if (type.equals("Refund")) {
            array.add("CUSTOMER_REFUND_ABCONTEST");
            array.add("CUSTOMER_REFUND_CONTEST");

        } else if (type.equals("Deposit")) {
            array.add("CUSTOMER_WALLET_RECHARGE");
            array.add("WALLET_WITHDRAW_ADMIN");
            array.add("WALLET_RECHARGE_ADMIN");
            array.add("CUSTOMER_RECEIVED_RCBD");
            array.add("REGISTER_CASH_BONUS");
            array.add("CUSTOMER_RECEIVED_REFCB");
        } else if (type.equals("Bonus")) {
            array.add("CUSTOMER_RECEIVED_REFCB");
            array.add("CUSTOMER_RECEIVED_RCB");
            array.add("REGISTER_CASH_BONUS");
            array.add("WALLET_WITHDRAW_ADMIN");
            array.add("WALLET_RECHARGE_ADMIN");
        } else if (type.equals("Withdraw")) {
            array.add("WALLET_WITHDRAW_ADMIN");
        } else {
            array.add("CUSTOMER_JOIN_CONTEST");
            array.add("CUSTOMER_WIN_CONTEST");
            array.add("CUSTOMER_WIN_LEADERBOARD");
            array.add("CUSTOMER_MR_RECEIVED");
            array.add("CUSTOMER_REFUND_ABCONTEST");
            array.add("CUSTOMER_REFUND_CONTEST");
            array.add("CUSTOMER_WALLET_RECHARGE");
            array.add("CUSTOMER_RECEIVED_REFCB");
            array.add("CUSTOMER_RECEIVED_RCB");
            array.add("CUSTOMER_RECEIVED_RCBD");
            array.add("REGISTER_CASH_BONUS");
            array.add("WALLET_WITHDRAW_ADMIN");
            array.add("WALLET_RECHARGE_ADMIN");
        }

        List<HashMap<String, Object>> customerWalletHistoryFilter = customerRepository
                .getCustomerWalletHistoryFilter(array, baseRequest.authUserId, page_no, type);
        return new BaseResponse(0, false, "Customer wallet history.", customerWalletHistoryFilter);
    }

    public Object walletRecharge(BaseRequest baseRequest) {
        String paymentmethod = PaymentGatewayConstant.CURRENT_PAYMENT_METHOD;
        String promocode = "";
        if (baseRequest.hasParam("promocode")) {
            promocode = baseRequest.getParam("promocode");
        }
        if (!Util.isEmpty(promocode)) {
            HashMap<String, Object> applyPromocode = applyPromoCode(baseRequest.authUserId, promocode,
                    Float.parseFloat(baseRequest.getParam("amount")));
            if (!Util.isEmpty(applyPromocode.get("message").toString())) {
                return new BaseResponse(1, true, (String) applyPromocode.get("message"), null);
            }
        }
        String paymentCallBackBaseUrl = baseRequest.REQUEST_DOMAIN;
        if (!Util.isEmpty(GlobalConstant.getProjectSubDirectory())) {
            paymentCallBackBaseUrl += "/" + GlobalConstant.getProjectSubDirectory();
        }
        paymentCallBackBaseUrl += "/paymentcallback/";

        float amount = Float.parseFloat(baseRequest.getParam("amount"));
        LinkedHashMap<String, Object> walletRecharge = PaymentUtil.walletRecharge(this, baseRequest.authUserId, amount, "", promocode, paymentCallBackBaseUrl, paymentmethod);

        return walletRecharge.get("HTML").toString();

    }
    public Object walletRechargeSdk(BaseRequest baseRequest) {
        String paymentmethod = PaymentGatewayConstant.CURRENT_PAYMENT_METHOD;
        String promocode = "";
        if (baseRequest.hasParam("promocode")) {
            promocode = baseRequest.getParam("promocode");
        }
        if (!Util.isEmpty(promocode)) {
            HashMap<String, Object> applyPromocode = applyPromoCode(baseRequest.authUserId, promocode,
                    Float.parseFloat(baseRequest.getParam("amount")));
            if (!Util.isEmpty(applyPromocode.get("message").toString())) {
                return new BaseResponse(1, true, (String) applyPromocode.get("message"), null);
            }
        }
        String paymentCallBackBaseUrl = baseRequest.REQUEST_DOMAIN;
        if (!Util.isEmpty(GlobalConstant.getProjectSubDirectory())) {
            paymentCallBackBaseUrl += "/" + GlobalConstant.getProjectSubDirectory();
        }
        paymentCallBackBaseUrl += "/paymentcallback/";

        float amount = Float.parseFloat(baseRequest.getParam("amount"));

        LinkedHashMap<String, Object> walletRecharge = PaymentUtil.walletRechargeSdk(this, baseRequest.authUserId, amount, "", promocode, paymentCallBackBaseUrl, paymentmethod);
        if ((Boolean) walletRecharge.get("error")){
            return new BaseResponse(1, true, (String) walletRecharge.get("message"), null);
        }
        return new BaseResponse(0, false, "Sdk data", walletRecharge.get("data"));
    }

    public BaseResponse walletRechargeCoupons(BaseRequest baseRequest) {

        List<HashMap<String, Object>> availablePromoCodes = customerRepository
                .getAvailablePromoCodes(baseRequest.authUserId);
        if (availablePromoCodes.size() == 0) {
            return new BaseResponse(0, false, "No coupons available.", availablePromoCodes);
        }

        List<HashMap<String, Object>> availableCodes = new ArrayList<HashMap<String, Object>>();
        for (HashMap<String, Object> hashMap : availablePromoCodes) {
            String isUse = (String) hashMap.get("isUse");
            int isUseMax = (int) hashMap.get("isUseMax");
            int alreadyUse = (int) hashMap.get("alreadyUse");
            if ((isUse.equals("S") && alreadyUse > 0) || (isUse.equals("M") && alreadyUse >= isUseMax)) {
                continue;
            }

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("id", hashMap.get("id"));
            data.put("recharge", hashMap.get("recharge"));
            data.put("cach_bonus", hashMap.get("cachBonus"));
            data.put("is_use", hashMap.get("isUse"));
            data.put("is_use_max", hashMap.get("isUseMax"));
            data.put("max_recharge", hashMap.get("maxRecharge"));
            data.put("cash_bonus_type", hashMap.get("cashBonusType"));
            data.put("code", hashMap.get("code"));
            data.put("start_date", hashMap.get("startDate"));
            data.put("end_date", hashMap.get("endDate"));
            data.put("already_use", hashMap.get("alreadyUse"));

            availableCodes.add(data);
        }

        return new BaseResponse(0, false, "Available Coupons", availableCodes);
    }

    public BaseResponse customerWithdrawAmount(BaseRequest baseRequest) {
        float amount = Float.parseFloat(baseRequest.getParam("amount"));
        String withdrawType_BANK = "B";
        String withdrawType_PAYTM = "P";

        if (amount <= 0) {
            return new BaseResponse(1, true, "Invalid amount.", null);
        }

        String type = "NORMAL";

        if (baseRequest.hasParam("type")) {
            HashSet<String> availableTypes = new HashSet<String>();
            availableTypes.add("NORMAL");
            availableTypes.add("INSTANT");

            type = baseRequest.getParam("type");

            if (!availableTypes.contains(type)) {
                return new BaseResponse(0, true, "type should be in NORMAL, INSTANT", null);
            }
        }
        String withdrawType = withdrawType_BANK;
        if (baseRequest.hasParam("withdraw_type")) {
            HashSet<String> availableTypes = new HashSet<String>();
            availableTypes.add(withdrawType_BANK);
            availableTypes.add(withdrawType_PAYTM);

            withdrawType = baseRequest.getParam("withdraw_type");

            if (!availableTypes.contains(withdrawType)) {
                return new BaseResponse(0, true, "withdraw_type should be in B, P", null);
            }
        }

        if (withdrawType.equals(withdrawType_PAYTM)) {
            type = "INSTANT";
        }

        HashMap<String, String> settingData = getSettingData();

        Float WITHDRAW_AMOUNT_MIN = Float.parseFloat(settingData.get("MIN_WITHDRAWALS"));
        Float WITHDRAW_AMOUNT_MAX = Float.parseFloat(settingData.get("MAX_WITHDRAWALS"));

        float INSTANT_MAX_COUNT_DAY = Float.parseFloat(settingData.containsKey("INSTANT_MAX_COUNT_DAY") ? settingData.get("INSTANT_MAX_COUNT_DAY") : "0");
        float WITHDRAW_AMOUNT_MIN_INSTANT = Float.parseFloat(settingData.containsKey("INSTANT_MIN_WITHDRAWALS") ? settingData.get("INSTANT_MIN_WITHDRAWALS") : "0");
        float WITHDRAW_AMOUNT_MAX_INSTANT = Float.parseFloat(settingData.containsKey("INSTANT_MAX_WITHDRAWALS") ? settingData.get("INSTANT_MAX_WITHDRAWALS") : "0");
        String INSTANT_WITHDRAWAL_IS_AVAILABLE = settingData.containsKey("INSTANT_WITHDRAWAL_IS_AVAILABLE") ? settingData.get("INSTANT_WITHDRAWAL_IS_AVAILABLE") : "N";



        Float WITHDRAW_AMOUNT_MIN_PAYTM = Float.parseFloat(settingData.get("PAYTM_MIN_WITHDRAWALS"));
        Float WITHDRAW_AMOUNT_MAX_PAYTM = Float.parseFloat(settingData.get("PAYTM_MAX_WITHDRAWALS"));
        Float PAYTM_SERVICE_TAX = Float.parseFloat(settingData.get("PAYTM_SERVICE_TAX"));
        Float PAYTM_GST = Float.parseFloat(settingData.get("PAYTM_GST"));
//        int PAYTM_TAX_FREE_TRANSACTION = Integer.parseInt(settingData.get("PAYTM_TAX_FREE_TRANSACTION"));
        String PAYTM_WITHDRAWAL_IS_AVAILABLE = settingData.get("PAYTM_WITHDRAWAL_IS_AVAILABLE");

        if (withdrawType.equals(withdrawType_BANK) && type.equals("INSTANT") && INSTANT_WITHDRAWAL_IS_AVAILABLE.equals("N")) {
            return new BaseResponse(0, true, "Currently instant withdrawal is not available.", null);
        } else if (withdrawType.equals(withdrawType_PAYTM) && PAYTM_WITHDRAWAL_IS_AVAILABLE.equals("N")) {
            return new BaseResponse(0, true, "Currently paytm withdrawal is not available.", null);
        }

        float appliedInstantFee = 0;
        float appliedInstantServiceTax = 0;
        float appliedInstantGst = 0;

        if (type.equals("INSTANT")) {
            if (withdrawType.equals(withdrawType_BANK)) {
                HashMap<String, Object> withdrawSlabCharges = customerRepository.getWithdrawSlabCharges(amount);
                if (withdrawSlabCharges == null) {
                    return new BaseResponse(0, true, "Instant withdrawal is not valid.", null);
                }
                appliedInstantFee = (float) withdrawSlabCharges.get("charges");
                if ((amount - appliedInstantFee) <= 0) {
                    return new BaseResponse(1, true, "Invalid amount.", null);
                }
            } else if (withdrawType.equals(withdrawType_PAYTM)) {
                appliedInstantServiceTax = PAYTM_SERVICE_TAX;
                appliedInstantGst = PAYTM_GST;

                float appliedInstantServiceTaxValue = (appliedInstantServiceTax / 100) * amount;

                float gstValue = 0;

                if (appliedInstantServiceTaxValue > 0) {
                    gstValue = (appliedInstantGst / 100) * appliedInstantServiceTaxValue;
                }
                appliedInstantFee = appliedInstantServiceTaxValue + gstValue;
            }

        }

        if (withdrawType.equals(withdrawType_BANK)) {
            if (type.equals("NORMAL") && (amount < WITHDRAW_AMOUNT_MIN || amount > WITHDRAW_AMOUNT_MAX)) {
                return new BaseResponse(1, true,
                        "min " + WITHDRAW_AMOUNT_MIN + " & max " + WITHDRAW_AMOUNT_MAX + " allowed per day.", null);
            }

            if (type.equals("INSTANT")
                    && (amount < WITHDRAW_AMOUNT_MIN_INSTANT || amount > WITHDRAW_AMOUNT_MAX_INSTANT)) {
                return new BaseResponse(1, true, "Min " + WITHDRAW_AMOUNT_MIN_INSTANT + " & Max "
                        + WITHDRAW_AMOUNT_MAX_INSTANT + " allowed per day", null);
            }
        } else if (withdrawType.equals(withdrawType_PAYTM)) {

            if ((amount < WITHDRAW_AMOUNT_MIN_PAYTM || amount > WITHDRAW_AMOUNT_MAX_PAYTM)) {
                return new BaseResponse(1, true,
                        "Min " + WITHDRAW_AMOUNT_MIN_PAYTM + " & Max " + WITHDRAW_AMOUNT_MAX_PAYTM + " allowed per day",
                        null);
            }
        }

        if (withdrawType.equals(withdrawType_BANK) && type.equals("INSTANT")) {
            int todayCustomerWithdraw = customerRepository.getTodayCustomerWithdrawByInstant(baseRequest.authUserId);
            if (todayCustomerWithdraw >= (int) INSTANT_MAX_COUNT_DAY) {
                return new BaseResponse(1, true, "Maximum " + (int) INSTANT_MAX_COUNT_DAY + " instant withdrawal allowed per day.", null);
            }
        } else if (withdrawType.equals(withdrawType_PAYTM)) {
            int todayCustomerWithdraw = customerRepository.getTodayCustomerWithdrawByPaytm(baseRequest.authUserId);
            if (todayCustomerWithdraw > 0) {
                return new BaseResponse(1, true, "Paytm withdrawal allowed once per day.", null);
            }
        }
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        if (withdrawType.equals(withdrawType_BANK)) {
            if (updatedProfileData.get("pancard") == null || updatedProfileData.get("bankdetail") == null
                    || !((HashMap<String, Object>) updatedProfileData.get("pancard")).get("status").toString()
                    .equals("A")
                    || !((HashMap<String, Object>) updatedProfileData.get("bankdetail")).get("status").toString()
                    .equals("A")) {
                return new BaseResponse(1, true, "Documents not uploaded or approved.", null);
            }
        } else if (withdrawType.equals(withdrawType_PAYTM)) {
            if (updatedProfileData.get("pancard") == null || !((HashMap<String, Object>) updatedProfileData.get("pancard")).get("status").toString().equals("A")) {
                return new BaseResponse(1, true, "Documents not uploaded or approved.", null);
            }

            String paytmPhone = (String) updatedProfileData.get("paytmphone");
            if (Util.isEmpty(paytmPhone)) {
                return new BaseResponse(1, true, "Invalid paytm number..", null);
            }

        }

        if (amount > (float) ((HashMap<String, Object>) updatedProfileData.get("wallet")).get("winning_wallet")) {
            return new BaseResponse(1, true, "Insufficient amount.", null);
        }

        boolean insertWithdrawRequest = customerRepository.insertWithdrawRequest(amount, baseRequest.authUserId, type,
                withdrawType, appliedInstantFee, appliedInstantServiceTax, appliedInstantGst);
        if (insertWithdrawRequest) {
            if (withdrawType.equals(withdrawType_BANK)) {
                if (type.equals("INSTANT")) {
                    return new BaseResponse(0, false, "Withdraw Request submitted successfully. The Withdrawal will be processed with in 60 Mins.", null);
                } else {
                    return new BaseResponse(0, false, "Withdraw Request submitted successfully. The Withdrawal will be processed with in 24 Hours.", null);
                }
            } else {
                return new BaseResponse(0, false, "Withdraw Request submitted successfully.", null);
            }
        } else {
            return new BaseResponse(0, true, "Unable to proceed.", null);
        }
    }

    public BaseResponse getCustomerWithdrawHistory(BaseRequest baseRequest, int page_no) {
        List<HashMap<String, Object>> getCustomerWithdrawHistory = customerRepository
                .getCustomerWithdrawHistory(baseRequest.authUserId, page_no);
        return new BaseResponse(0, false, "Customer withdraw history.", getCustomerWithdrawHistory);
    }

    public BaseResponse getProfile(Integer UserId) {
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(UserId);
        return new BaseResponse(0, false, "User profile.", updatedProfileData);
    }

    public BaseResponse updateProfile(BaseRequest baseRequest) {
        String countryMobileCode = baseRequest.getParam("country_mobile_code");
        String firstName = baseRequest.getParam("firstname");
        String lastName = "";
        if (baseRequest.hasParam("lastname")) {
            lastName = baseRequest.getParam("lastname");
        }

        long dob = 0;
        if (baseRequest.hasParam("dob")) {
            try {
                dob = Long.parseLong(baseRequest.getParam("dob"));
            } catch (Exception e) {
            }

        }

        String addressLine1 = "";
        if (baseRequest.hasParam("addressline1")) {
            addressLine1 = baseRequest.getParam("addressline1");
        }

        String addressLine2 = "";
        if (baseRequest.hasParam("addressline2")) {
            addressLine2 = baseRequest.getParam("addressline2");
        }

        int country = 0;
        if (baseRequest.hasParam("country")) {
            try {
                country = Integer.parseInt(baseRequest.getParam("country"));
            } catch (Exception e) {
            }
        }

        int state = 0;
        if (baseRequest.hasParam("state")) {
            try {
                state = Integer.parseInt(baseRequest.getParam("state"));
            } catch (Exception e) {
            }
        }
        String city = "";
        if (baseRequest.hasParam("city")) {
            city = baseRequest.getParam("city");
        }

        String pinCode = "";
        if (baseRequest.hasParam("pincode")) {
            pinCode = baseRequest.getParam("pincode");
        }
        String phone = baseRequest.getParam("phone");
        String email = baseRequest.getParam("email");

        TblCustomer customerIdByMobileNo = customerRepository.getCustomerIdByMobileNo(countryMobileCode, phone,
                baseRequest.authUserId);
        if (customerIdByMobileNo != null) {
            return new BaseResponse(1, true, "Phone already exist.", null);
        }

        TblCustomer customerIdByEmail = customerRepository.getCustomerIdByEmail(email, baseRequest.authUserId);
        if (customerIdByEmail != null) {
            return new BaseResponse(1, true, "Email already exist.", null);
        }

        // entry in customer update logs
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");

        customerRepository.UpdateProfileData(baseRequest.authUserId, firstName, lastName, email, countryMobileCode, dob,
                phone, country, state, city, addressLine1, addressLine2, pinCode);

        // entry in customer update logs
        String action = "Update profile.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);

        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Customer successfully updated.", updatedProfileData);

    }

    public BaseResponse updateVerifyEmail(BaseRequest baseRequest) {
        // check email is verify or not
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        if (customerPreviousDetail.getIsEmailVerified().equals("Y")) {
            return new BaseResponse(1, true, "Unable to proceed.", null);
        }


        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("F");
        hashSet.add("G");
        String socialType = "";
        if (baseRequest.getParam("is_social").equals("Y")) {
            if (!hashSet.contains(baseRequest.getParam("social_type"))) {
                return new BaseResponse(1, true, "Invalid social_type.", null);
            }
            socialType = baseRequest.getParam("social_type");
        }

        TblCustomer customerIdByEmail = customerRepository.getCustomerIdByEmail(baseRequest.getParam("email"),
                baseRequest.authUserId);
        if (customerIdByEmail != null) {
            return new BaseResponse(1, true, "Email already exist with another account.", null);
        }

        String emailToken = Util.getAlphaNumericString(6) + Util.convertToMD5(baseRequest.authUserId.toString());
        BigInteger time = BigInteger.valueOf(Util.getCurrentTime());

        String isEmailVerified = "N";

        if (baseRequest.getParam("is_social").equals("Y")) {

            isEmailVerified = "Y";
        }

        customerRepository.updateVerifyEmail(baseRequest.getParam("email"), baseRequest.getParam("is_social"),
                socialType, isEmailVerified, emailToken, time, baseRequest.authUserId);
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);

        // entry in customer update logs
        String action = "Update verify email.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);

        HashMap<String, Object> data = new HashMap<String, Object>();
        if (GlobalConstant.isProjectTypeProd()){
            data.put("link", GlobalConstant.ADMIN_URL + "email_verification/index/" + emailToken);
        }else{
            data.put("link", GlobalConstant.ADMIN_URL_STAG + "email_verification/index/" + emailToken);
        }
        String fullName = updatedProfileData.get("firstname") + " " + updatedProfileData.get("lastname");

        sendTemplatesInMail("email_verification_link", fullName, baseRequest.getParam("email"), data);
        return new BaseResponse(0, false, "Verification link send successfully..", updatedProfileData);

    }

    public BaseResponse sendOtpMobile(BaseRequest baseRequest) {
        TblCustomer customerIdByMobileNo = customerRepository.getCustomerIdByMobileNo(
                baseRequest.getParam("country_mobile_code"), baseRequest.getParam("phone"), baseRequest.authUserId);
        if (customerIdByMobileNo != null) {
            return new BaseResponse(1, true, "Phone number already exists with another account.", null);
        }
        String otpCode = sendOtp(baseRequest.getParam("phone"), "SP", baseRequest.getParam("country_mobile_code"), "");
        HashMap<String, Object> response = new HashMap();
        response.put("otp", otpCode);
        response.put("phone", baseRequest.getParam("phone"));
        response.put("country_mobile_code", baseRequest.getParam("country_mobile_code"));
        return new BaseResponse(0, false, "Verification code sent to " + baseRequest.getParam("country_mobile_code")
                + baseRequest.getParam("phone") + ".", response);
    }

    public BaseResponse updateVerifyMobile(BaseRequest baseRequest) {
        // check mobile is verify or not
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        if (customerPreviousDetail.getIsPhoneVerified().equals("Y")) {
            return new BaseResponse(1, true, "Unable to proceed.", null);
        }

        TblCustomer customerIdByMobileNo = customerRepository.getCustomerIdByMobileNo(
                baseRequest.getParam("country_mobile_code"), baseRequest.getParam("phone"), baseRequest.authUserId);
        if (customerIdByMobileNo != null) {
            return new BaseResponse(1, true, "Phone number already exists with another account.", null);
        }

        String updateAndDeleteOtp = updateAndDeleteOtp(baseRequest.getParam("country_mobile_code"),
                baseRequest.getParam("phone"));
        if (!Util.isEmpty(updateAndDeleteOtp) && updateAndDeleteOtp.equals("EXPIRED")) {
            return new BaseResponse(1, true, "Otp expired due to invalid attemps.", null);
        }

        int updateCount = customerRepository.updateTempCustomerToVerified(baseRequest.getParam("otp"),
                baseRequest.getParam("type"), baseRequest.getParam("country_mobile_code"),
                baseRequest.getParam("phone"));

        if (updateCount <= 0) {
            return new BaseResponse(1, true, "Invalid verification code.", null);
        }

        customerRepository.updateVerifyMobile(baseRequest);

        // entry in customer update logs
        String action = "Update verify mobile.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);

        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Mobile No. verified successfully.", updatedProfileData);

    }

    public BaseResponse sendOtpMobilePaytm(BaseRequest baseRequest) {
        TblCustomer customerIdByMobileNo = customerRepository
                .getCustomerIdByPaytmMobileNo(baseRequest.getParam("phone"), baseRequest.authUserId);
        if (customerIdByMobileNo != null) {
            return new BaseResponse(1, true, "Phone number already exists with another account.", null);
        }
        String otpCode = sendOtp(baseRequest.getParam("phone"), "PT", baseRequest.getParam("country_mobile_code"), "");
        HashMap<String, Object> response = new HashMap();
        response.put("otp", otpCode);
        response.put("phone", baseRequest.getParam("phone"));
        response.put("country_mobile_code", baseRequest.getParam("country_mobile_code"));
        return new BaseResponse(0, false, "Verification code sent to " + baseRequest.getParam("country_mobile_code")
                + baseRequest.getParam("phone") + ".", response);
    }

    public BaseResponse updateVerifyMobilePaytm(BaseRequest baseRequest) {
        // check mobile paytm is verify or not
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        if (!Util.isEmpty(customerPreviousDetail.getPaytmphone())) {
            return new BaseResponse(1, true, "Unable to proceed.", null);
        }

        TblCustomer customerIdByMobileNo = customerRepository
                .getCustomerIdByPaytmMobileNo(baseRequest.getParam("phone"), baseRequest.authUserId);
        if (customerIdByMobileNo != null) {
            return new BaseResponse(1, true, "Phone number already exists with another account.", null);
        }

        String updateAndDeleteOtp = updateAndDeleteOtp(baseRequest.getParam("country_mobile_code"),
                baseRequest.getParam("phone"));
        if (!Util.isEmpty(updateAndDeleteOtp) && updateAndDeleteOtp.equals("EXPIRED")) {
            return new BaseResponse(1, true, "Otp expired due to invalid attemps.", null);
        }

        int updateCount = customerRepository.updateTempCustomerToVerified(baseRequest.getParam("otp"),
                baseRequest.getParam("type"), baseRequest.getParam("country_mobile_code"),
                baseRequest.getParam("phone"));

        if (updateCount <= 0) {
            return new BaseResponse(1, true, "Invalid verification code.", null);
        }

        customerRepository.updateVerifyMobilePaytm(baseRequest);
        // entry in customer update logs
        String action = "Update verify mobile paytm.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);

        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Mobile No. verified successfully.", updatedProfileData);

    }

    public BaseResponse getProfilePictures(BaseRequest baseRequest) {

        List<TblCustomerAvatar> profilePictures = customerRepository.getProfilePictures();

        if (profilePictures == null) {
            return new BaseResponse(1, true, "No picture available.", null);
        }
        HashSet<String> response = new HashSet<String>();
        for (TblCustomerAvatar tblCustomerAvatar : profilePictures) {
            response.add(FileUploadConstant.CUSTOMER_IMAGE_THUMB_URL + tblCustomerAvatar.getImage());
        }
        return new BaseResponse(0, false, "Profile picture  List.", response);

    }

    public BaseResponse changeProfilePicture(BaseRequest baseRequest) {
        String image = "";
        String fbImage = "";
        if (baseRequest.hasParam("image") && !Util.isEmpty(baseRequest.getParam("image"))) {
            image = baseRequest.getParam("image");

            String[] split = image.split("/");
            image = split[split.length - 1];
        }

        if (baseRequest.hasParam("fb_image") && !Util.isEmpty(baseRequest.getParam("fb_image"))) {
            fbImage = baseRequest.getParam("fb_image");
        }

        if (baseRequest.hasParam("g_image") && !Util.isEmpty(baseRequest.getParam("g_image"))) {
            fbImage = baseRequest.getParam("g_image");
        }
        if (Util.isEmpty(image) && Util.isEmpty(fbImage)) {
            return new BaseResponse(1, true, "image,fb_image,g_image atleast send one param.", null);
        }

        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        customerRepository.changeProfilePicture(baseRequest);
        // entry in customer update logs
        String action = "Update profile picture.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Profile Picture Changed successfully.", updatedProfileData);
    }

    public BaseResponse changePassword(BaseRequest baseRequest) {
        TblCustomer customerByIdForChangePassword = customerRepository.getCustomerByIdForChangePassword(baseRequest.authUserId);

        if (customerByIdForChangePassword == null) {
            return new BaseResponse(1, true, "Invalid user access.", null);
        }
        String oldPassword = baseRequest.getParam("old_password");
        oldPassword = Util.convertToMD5(oldPassword);
        if (!oldPassword.equals(customerByIdForChangePassword.getPassword())) {
            return new BaseResponse(1, true, "Invalid old password.", null);
        }
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        customerRepository.updatePassword(baseRequest);
        // entry in customer update logs
        String action = "Update password.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);
        return new BaseResponse(0, false, "Password successfully changed.", null);

    }

    public BaseResponse getReferEarn(BaseRequest baseRequest) {

        HashMap<String, String> referCashBonus = getReferCashBonus();

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("title", "Kick off your friend's\nchoic11 Journey!");
        output.put("subtitle", "For every friend that plays, you get 100");
        output.put("image", Util.generateImageUrl(referCashBonus.get("REFERRAL_EARN_IMAGE"), FileUploadConstant.REFER_EARN_IMAGE_LARGE_URL, ""));

        String usedRefferalAmount = referCashBonus.get("REFERRER");
        String applierReffrelAmount = referCashBonus.get("REGISTER_WITH_REFERRAL_CODE_(applier)");

        output.put("title", "Kick off your friend's\nchoic11 Journey!");
        output.put("subtitle",
                "For every friend that plays, you get " + GlobalConstant.CURRENCY_SYMBOL + usedRefferalAmount);
        output.put("share_content",
                "Think you can challenge me on choic11? Tap " + GlobalConstant.APK_DOWNLOAD_URL
                        + " \nto download the app & use my invite code %s to get Cash Bonus of Rs."
                        + applierReffrelAmount + "! Let the game begin");

        HashMap<String, Object> referEarn = customerRepository.getReferEarn(baseRequest.authUserId);

        output.put("team_count", referEarn.get("team_count"));
        output.put("team_earn", referEarn.get("team_earn"));
        output.put("total_received_amount", referEarn.get("total_received_amount"));
        output.put("total_received_amount_aff", referEarn.get("total_received_amount_aff"));

        String[] userRef = referEarn.get("user_ref").toString().split("-------");
        String dynamicLink = userRef[0];
        String referralCode = userRef[1];
        if (dynamicLink.equals("0")) {
            dynamicLink = getFirebaseDynamicLinkForUser(referralCode, baseRequest.authUserId);
        }

        if (!Util.isEmpty(dynamicLink)) {
            dynamicLink+="?refcode="+referralCode;
            output.put("share_content",
                    "Think you can challenge me on choic11? Tap " + dynamicLink
                            + " \nto download the app & use my invite code %s to get Cash Bonus of Rs."
                            + applierReffrelAmount + "! Let the game begin");
        }

        return new BaseResponse(0, false, "Refer earn data.", output);

    }

    public HashMap<String, Object> getCustomerDynamicLink(Integer authUserId) {

        HashMap<String, Object> customerDynamicLink = customerRepository.getCustomerDynamicLink(authUserId);
        if (customerDynamicLink == null) {
            return null;
        }

        String dynamicLink = (String) customerDynamicLink.get("dynamic_link");
        String referralCode = (String) customerDynamicLink.get("referral_code");
        if (dynamicLink.equals("0")) {
            dynamicLink = getFirebaseDynamicLinkForUser(referralCode, authUserId);
        }
        dynamicLink+="?refcode="+referralCode;
        customerDynamicLink.put("dynamic_link", dynamicLink);

        return customerDynamicLink;

    }

    public String getFirebaseDynamicLinkForUser(String referralCode, Integer authUserId) {
        String generateDynamicLink = FcmUtil.generateDynamicLink(referralCode);
        if (!Util.isEmpty(generateDynamicLink)) {
            customerRepository.updateCustomerDynamicLink(authUserId, generateDynamicLink);
        }
        return generateDynamicLink;
    }

    public BaseResponse getReferEarnDetail(BaseRequest baseRequest) {
        int pageNo = 0;
        if (baseRequest.hasParam("page_no")) {
            pageNo = Integer.parseInt(baseRequest.getParam("page_no"));
        }
        BaseResponse referEarn = null;
        if (pageNo == 0 || pageNo == 1) {
            referEarn = getReferEarn(baseRequest);
        }

        HashMap<String, Object> referEarnDetail = customerRepository.getReferEarnDetail(baseRequest.authUserId, pageNo);
        LinkedHashMap<String, Object> output_first = null;
        if (referEarn != null) {
            output_first = (LinkedHashMap<String, Object>) referEarn.getData();
            output_first.put("total_received_amount", referEarnDetail.get("refer_data"));
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("user_refer_data", referEarnDetail.get("user_refer_data"));
        output.put("refer_data", output_first);
        return new BaseResponse(0, false, "Refer earn detail.", output);
    }

    public BaseResponse applyPromoCode(BaseRequest baseRequest) {

        String promoCode = baseRequest.getParam("promocode");
        float amount = Float.parseFloat(baseRequest.getParam("amount"));

        HashMap<String, Object> applyPromoCode = applyPromoCode(baseRequest.authUserId, promoCode, amount);
        if (!Util.isEmpty(applyPromoCode.get("message").toString())) {
            return new BaseResponse((int) applyPromoCode.get("code"), true, (String) applyPromoCode.get("message"),
                    null);
        }

        return new BaseResponse((int) applyPromoCode.get("code"), false, "Promocode applied successfully.",
                applyPromoCode.get("data"));

    }

    private HashMap<String, Object> applyPromoCode(Integer UserId, String promocode, Float amount) {
        Long time = Util.getCurrentTime();
        HashMap<String, Object> promoCodeDetailByPromoCodeCustom = customerRepository.getPromoCodeDetailByPromoCodeCustom(UserId, promocode);
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("code", 0);
        output.put("message", "");

        if (promoCodeDetailByPromoCodeCustom == null) {
            output.put("code", 1);
            output.put("message", "Invalid Promocode.");
            return output;
        }
        if (amount < (float) promoCodeDetailByPromoCodeCustom.get("recharge")
                || amount > (float) promoCodeDetailByPromoCodeCustom.get("maxRecharge")) {
            output.put("code", 1);
            output.put("message",
                    "Invalid amount. Amount should be between " + promoCodeDetailByPromoCodeCustom.get("recharge")
                            + " to " + promoCodeDetailByPromoCodeCustom.get("maxRecharge"));
            return output;

        }

        if (time < ((BigInteger) promoCodeDetailByPromoCodeCustom.get("startDate")).longValue()
                || time > ((BigInteger) promoCodeDetailByPromoCodeCustom.get("endDate")).longValue()) {
            if (time > ((BigInteger) promoCodeDetailByPromoCodeCustom.get("endDate")).longValue()) {
                output.put("code", 1);
                output.put("message", "Promocode expired.");
                return output;
            }
            output.put("code", 1);
            output.put("message", "Invalid Promocode.");
            return output;
        }
        String isUse = (String) promoCodeDetailByPromoCodeCustom.get("isUse");
        int isUseMax = (int) promoCodeDetailByPromoCodeCustom.get("isUseMax");
        int alreadyUse = (int) promoCodeDetailByPromoCodeCustom.get("alreadyUse");
        if ((isUse.equals("S") && alreadyUse > 0) || (isUse.equals("M") && alreadyUse >= isUseMax)) {
            output.put("code", 1);
            output.put("message", "Promocode already used.");
            return output;
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", promoCodeDetailByPromoCodeCustom.get("id"));
        data.put("recharge", promoCodeDetailByPromoCodeCustom.get("recharge"));
        data.put("cach_bonus", promoCodeDetailByPromoCodeCustom.get("cachBonus"));
        data.put("is_use", promoCodeDetailByPromoCodeCustom.get("isUse"));
        data.put("is_use_max", promoCodeDetailByPromoCodeCustom.get("isUseMax"));
        data.put("max_recharge", promoCodeDetailByPromoCodeCustom.get("maxRecharge"));
        data.put("cash_bonus_type", promoCodeDetailByPromoCodeCustom.get("cashBonusType"));
        data.put("code", promoCodeDetailByPromoCodeCustom.get("code"));
        data.put("start_date", promoCodeDetailByPromoCodeCustom.get("startDate"));
        data.put("end_date", promoCodeDetailByPromoCodeCustom.get("endDate"));
        data.put("already_use", promoCodeDetailByPromoCodeCustom.get("alreadyUse"));

        output.put("data", data);
        output.put("message", "");
        return output;

    }

    public BaseResponse customerTeamNameUpdate(BaseRequest baseRequest) {
        boolean teamNameExists = customerRepository.isTeamNameExists(baseRequest.getParam("team_name"),
                baseRequest.authUserId);
        if (teamNameExists) {
            return new BaseResponse(1, true, "Team name already exists.", null);
        }

        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        int updateTeamName = customerRepository.updateTeamName(baseRequest);
        // entry in customer update logs
        String action = "Update team name.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);

        if (updateTeamName > 0) {
            return new BaseResponse(0, false, "Team Name successfully updated.", null);
        }
        return new BaseResponse(1, true, "Team name can't change now.", null);

    }

    public BaseResponse addPanCard(BaseRequest baseRequest) {

        int selectPanCard = customerRepository.selectPanCard(baseRequest.getParam("number"), baseRequest.authUserId);
        if (selectPanCard > 0) {
            return new BaseResponse(1, true, "Pan Number already exist with another account.", null);
        }
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        customerRepository.insertPanCard(baseRequest);
        // entry in customer update logs
        String action = "Update pan card.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Pan card added successfully.", updatedProfileData);
    }

    public BaseResponse addBankDetail(BaseRequest baseRequest) {
        int selectBankDetail = customerRepository.selectBankDetail(baseRequest.getParam("account_number"),
                baseRequest.authUserId);

        if (selectBankDetail > 0) {
            return new BaseResponse(1, true, "Account Number already exist with another account.", null);
        }
        TblCustomer customerPreviousDetail = customerRepository.getCustomerById(baseRequest.authUserId, "", "");
        customerRepository.insertBankDetail(baseRequest);
        // entry in customer update logs
        String action = "Update bank details.";
        customerRepository.saveCustomerUpdateLogs(baseRequest.authUserId, "", "", customerPreviousDetail, action);
        HashMap<String, Object> updatedProfileData = getUpdatedProfileData(baseRequest.authUserId);
        return new BaseResponse(0, false, "Bank detail added successfully.", updatedProfileData);
    }

    public BaseResponse getPlayingHistory(BaseRequest baseRequest) {
        LinkedHashMap<String, Object> playingHistory = customerRepository.getPlayingHistory(baseRequest.authUserId);

        return new BaseResponse(0, false, "Playing history data.", playingHistory);
    }

    public BaseResponse getCustomerRecentSeriesLeaderboard(BaseRequest baseRequest) {
        Collection<Object> customerRecentSeriesLeaderboard = customerRepository
                .getCustomerRecentSeriesLeaderboard(baseRequest.authUserId);

        return new BaseResponse(0, false, "Recent Series List", customerRecentSeriesLeaderboard);
    }

    public BaseResponse getCustomerProfile(BaseRequest baseRequest) {
        Object customerProfile = customerRepository.getCustomerProfile(baseRequest);
        if (customerProfile instanceof String) {
            String error = (String) customerProfile;
            return new BaseResponse(0, true, "unable to proceed.", null);
        } else {
            return new BaseResponse(0, false, "Customer profile.", customerProfile);
        }
    }

    public BaseResponse createCustomerEnquiry(BaseRequest baseRequest) {
        int customerId = baseRequest.authUserId;
        String message = baseRequest.getParam("message");
        String subject = baseRequest.getParam("subject");
        LinkedHashMap<String, Object> ticketData = customerRepository.createCustomerEnquiry(customerId, subject,
                message);

        return new BaseResponse(0, false, "Query submitted successfully. Ticket No is " + ticketData.get("ticket_id"),
                ticketData);
    }

    public BaseResponse forgotPasswordEmail(BaseRequest baseRequest) {

        TblCustomer alreadySavedCustomer = customerRepository.getCustomerIdByEmail(baseRequest.getParam("email"), 0);
        if (alreadySavedCustomer == null) {
            return new BaseResponse(1, true, "Email address does not exist.", null);
        }
        String sendotpEmail = sendOtpEmail(baseRequest.getParam("email"), "FE", "");

        LinkedHashMap<String, String> response = new LinkedHashMap<String, String>();
        response.put("otp", sendotpEmail);
        response.put("email", baseRequest.getParam("email"));
        return new BaseResponse(0, false, "Verification code sent to " + baseRequest.getParam("email") + ".", response);

    }

    public BaseResponse socialLogin(BaseRequest baseRequest) {
        String email = baseRequest.getParam("email");
        String social_id = baseRequest.getParam("social_id");
        String social_type = baseRequest.getParam("social_type");
        if (Util.isEmpty(email) && Util.isEmpty(social_id)) {
            return new BaseResponse(0, true, "Unable to proceed.", null);
        }
        TblCustomer socialCustomerDetail = null;

        if (!Util.isEmpty(social_id)) {
            TblCustomer socialCustomerDetailById = customerRepository.getSocialCustomerDetail(social_id, null);

            if (socialCustomerDetailById != null) {
                socialCustomerDetail = socialCustomerDetailById;
            }
        }
        if (socialCustomerDetail == null && !Util.isEmpty(email)) {
            TblCustomer socialCustomerDetailByEmail = customerRepository.getSocialCustomerDetail(null, email);
            if (socialCustomerDetailByEmail != null) {
                socialCustomerDetail = socialCustomerDetailByEmail;
            }
        }

        Long currentTime = Util.getCurrentTime();
        if (socialCustomerDetail == null) {
            String firstname = baseRequest.getParam("firstname");
            String lastname = baseRequest.getParam("lastname");

            String used_referral_code = baseRequest.getParam("referral_code");

            String country_mobile_code = "";
            String phone = "";
            String real_password = email + firstname;
            String secure_password = Util.convertToMD5(real_password);

            TblCustomer customer = TblCustomer.getInstance();
            customer.setCountryMobileCode(country_mobile_code);
            customer.setFirstname(firstname);
            customer.setLastname(lastname);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setPassword(secure_password);
            customer.setSocialType(social_type);
            customer.setSocialId(social_id);
            customer.setCreated(BigInteger.valueOf(currentTime));
            if (Util.isEmpty(email)) {
                customer.setIsEmailVerified("N");
            } else {
                customer.setIsEmailVerified("Y");
            }

            customer.setIsSocial("Y");

            if (!Util.isEmpty(used_referral_code)) {

                TblCustomer usedReferralCustomerId = customerRepository.getUsedReferralCustomerId(used_referral_code);

                if (usedReferralCustomerId != null) {
                    customer.setUsedReferralCode(used_referral_code);
                    customer.setUsedReferralUserId(usedReferralCustomerId.getId());
                }
            }

            HashMap<String, String> referCashBonus = getReferCashBonus();
            Float usedReferralAmount = (float) 0;
            Float usedReferralAmountDeposit = (float) 0;
            Float applierReferralAmount = (float) 0;
            Float applierReferralAmountDeposit = (float) 0;
            if (!referCashBonus.isEmpty()) {
                applierReferralAmount = Float.parseFloat(referCashBonus.get("NEW_REGISTRATION"));
                applierReferralAmountDeposit = Float.parseFloat(referCashBonus.get("NEW_REGISTRATION_DEPOSIT"));
                if (customer.getUsedReferralUserId() > 0) {
                    usedReferralAmount = Float.parseFloat(referCashBonus.get("REFERRER"));
                    usedReferralAmountDeposit = Float.parseFloat(referCashBonus.get("REFERRER_DEPOSIT"));
                    applierReferralAmount = Float.parseFloat(referCashBonus.get("REGISTER_WITH_REFERRAL_CODE_(applier)"));
                    applierReferralAmountDeposit = Float.parseFloat(referCashBonus.get("REGISTER_WITH_REFERRAL_CODE_DEPOSIT_(applier)"));
                }
            }
            customer.setUsedRefferalAmount(usedReferralAmount);
            customer.setUsedRefferalAmountDeposit(usedReferralAmountDeposit);

            int customerId = customerRepository.saveCustomer(customer);

            if (customerId == 0) {
                return new BaseResponse(1, true, "Unable to proceed.", null);
            }

            customerRepository.saveCustomerDetailsToCustomerLogins(customerId, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress);


            if (applierReferralAmount > 0 || applierReferralAmountDeposit > 0) {
                updateCustomerWalletRegisterCashBonus(customerId, applierReferralAmount, applierReferralAmountDeposit);
            }

            customerRepository.saveCustomerDetailsToCustomerLogs(customerId, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress, baseRequest.HEADER_deviceinfo,
                    baseRequest.HEADER_appinfo);

            if (!Util.isEmpty(customer.getEmail())) {
                HashMap<String, Object> data = new HashMap<String, Object>();
                sendTemplatesInMail("customer_welcome", customer.getFirstname(), customer.getEmail(), data);
            }

            HashMap<String, Object> customerData = getUpdatedProfileData(customerId);
            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId((int) customerData.get("id"));
            String jwtToken = jwtTokenUtil.generateToken(tblCustomer);
            customerData.put("jwtToken", jwtToken);
            return new BaseResponse(0, false, "Login successfully.", customerData);

        } else {
            if (socialCustomerDetail.getStatus().equals("D")) {
                return new BaseResponse(0, false, "User account is deactivated Please contact to admin.", null);
            }

            int saved_customer_id = socialCustomerDetail.getId();
            String saved_email = socialCustomerDetail.getEmail();
            String saved_social_id = socialCustomerDetail.getSocialId();
            String is_email_verified = socialCustomerDetail.getIsEmailVerified();

            if (is_email_verified.equals("N") && !Util.isEmpty(email)) {
                if (Util.isEmpty(saved_email) || saved_email.equals(email)) {
                    customerRepository.updateCustomerSocialDetail(saved_customer_id, social_type, social_id, email);
                }
            }
            if (Util.isEmpty(saved_social_id) && !Util.isEmpty(social_id)) {
                customerRepository.updateCustomerSocialId(saved_customer_id, social_type, social_id);
            }

            customerRepository.saveCustomerDetailsToCustomerLogins(saved_customer_id, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress);

            customerRepository.saveCustomerDetailsToCustomerLogs(saved_customer_id, baseRequest.HEADER_deviceid,
                    baseRequest.HEADER_devicetype, baseRequest.HEADER_ipAddress, baseRequest.HEADER_deviceinfo,
                    baseRequest.HEADER_appinfo);

            HashMap<String, Object> customerData = getUpdatedProfileData(saved_customer_id);
            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId((int) customerData.get("id"));
            String jwtToken = jwtTokenUtil.generateToken(tblCustomer);
            customerData.put("jwtToken", jwtToken);

            return new BaseResponse(0, false, "Login successfully.", customerData);

        }

    }

    public BaseResponse getNotifications(BaseRequest baseRequest, int page_no) {
        List<HashMap<String, Object>> notifications = customerRepository.getNotifications(baseRequest.authUserId,
                page_no);

        List<HashMap<String, Object>> output = new ArrayList<HashMap<String, Object>>();

        for (HashMap<String, Object> value : notifications) {

            HashMap<String, Object> innerOutput = new HashMap<String, Object>();
            innerOutput.put("title", value.get("title"));
            innerOutput.put("notification", value.get("notification"));
            innerOutput.put("sender_type", value.get("sender_type"));
            innerOutput.put("created", value.get("created"));
            innerOutput.put("image_thumb", Util.generateImageUrl((String) value.get("image"), FileUploadConstant.NOTIFICATION_IMAGE_THUMB_URL, ""));
            innerOutput.put("image_large", Util.generateImageUrl((String) value.get("image"), FileUploadConstant.NOTIFICATION_IMAGE_LARGE_URL, ""));
            output.add(innerOutput);
        }
        return new BaseResponse(0, false, "notification list.", output);
    }

    public BaseResponse getAffiliateHistory(BaseRequest baseRequest) {
        LinkedHashMap<String, Object> affiliateHistory = customerRepository.getAffiliateHistory(baseRequest.authUserId);

        return new BaseResponse(0, false, "Affiliate history data.", affiliateHistory);
    }

    public BaseResponse getCouponList(BaseRequest baseRequest, int page_no) {
        List<HashMap<String, Object>> couponList = customerRepository.getCouponList(baseRequest.authUserId, page_no);
        if (couponList == null || couponList.size() == 0) {
            return new BaseResponse(0, false, "No Coupon found.", new ArrayList<>());
        }

        for (HashMap<String, Object> stringObjectHashMap : couponList) {
            String image = (String) stringObjectHashMap.get("image");

            stringObjectHashMap.put("image_thumb", Util.generateImageUrl(image, FileUploadConstant.NOTIFICATION_IMAGE_THUMB_URL, ""));

            stringObjectHashMap.put("image_large", Util.generateImageUrl(image, FileUploadConstant.NOTIFICATION_IMAGE_LARGE_URL, ""));

            stringObjectHashMap.remove("image");

        }
        return new BaseResponse(0, false, "Coupon list.", couponList);
    }

    public BaseResponse redeemCoupon(BaseRequest baseRequest) {
        String coupon = baseRequest.getParam("coupon");

        HashMap<String, Object> couponByCode = customerRepository.getCouponByCode(coupon);
        if (couponByCode == null) {
            return new BaseResponse(0, true, "Invalid Coupon.", null);
        }

        Long currentTime = Util.getCurrentTime();

        String title = (String) couponByCode.get("title");
        String description = (String) couponByCode.get("description");
        BigInteger start_date = (BigInteger) couponByCode.get("start_date");
        BigInteger end_date = (BigInteger) couponByCode.get("end_date");
        float bonus_amount = (float) couponByCode.get("bonus");
        float deposite_amount = (float) couponByCode.get("deposite");

        int couponId = (int) couponByCode.get("id");
        String customer_ids = (String) couponByCode.get("customer_ids");

        if (currentTime < start_date.longValue() || currentTime > end_date.longValue()) {
            if (currentTime > end_date.longValue()) {
                return new BaseResponse(0, true, "Coupon expired.", null);
            }
            return new BaseResponse(0, true, "Invalid Coupon.", null);
        }

        if (!customer_ids.equals("0")) {
            boolean userFound = false;
            String[] split = customer_ids.split(",");
            for (String s : split) {
                if (baseRequest.authUserId == Integer.parseInt(s)) {
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                return new BaseResponse(0, true, "Invalid Coupon.", null);
            }
        }

        int customerUseCouponCount = customerRepository.getCustomerUseCouponCount(baseRequest.authUserId, couponId);

        if (customerUseCouponCount > 0) {
            return new BaseResponse(0, true, "Coupon already used.", null);
        }

        boolean isAllSuccess = customerRepository.updateCustomerCouponRedeem(baseRequest.authUserId, couponId, deposite_amount, bonus_amount, coupon);

        if (!isAllSuccess) {
            return new BaseResponse(0, true, "Unable to proceed.", null);
        }

        String msg = "";
        String msg1 = "";
        if (deposite_amount > 0) {
            msg = GlobalConstant.CURRENCY_SYMBOL + deposite_amount + "  credited to your Deposit wallet";
        }
        if (bonus_amount > 0) {
            if (!msg.isEmpty()) {
                msg1 = "and ";
            }
            msg1 += GlobalConstant.CURRENCY_SYMBOL + bonus_amount + "  credited to your Bonus wallet";
        }

        JSONObject notificationData = new JSONObject();
        notificationData.put("noti_type", "redeem_coupon");
        notificationData.put("title", title);
        String alertMessage = description + "\nWoohoo! " + coupon + " redeemed " + msg + " " + msg1;
        sendNotificationAndSave(notificationData, alertMessage, true, new Integer[]{baseRequest.authUserId});

        LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerRepository.getUpdatedWalletData(baseRequest.authUserId);

        return new BaseResponse(0, false, "Coupon redeem successfully.", updatedWalletData.get("wallet"));

    }


    public BaseResponse getReferralSettings() {
        HashMap<String, String> referCashbonus = getReferCashBonus();
        return new BaseResponse(0, false, "Settings.", referCashbonus);

    }

    public BaseResponse getAppCustomIcons() {
        LinkedHashMap<String, Object> appCustomIcons = customerRepository.getAppCustomIcons();
        if (appCustomIcons.isEmpty()) {
            return new BaseResponse(0, true, "No custom icon found", null);
        } else {
            return new BaseResponse(0, false, "Custom icons", appCustomIcons);
        }
    }

    public BaseResponse getQuotations(BaseRequest baseRequest) {
        Collection<Object> quotations = customerRepository.getQuotations();
        if (quotations.isEmpty()) {
            return new BaseResponse(0, true, "No Quotations found", null);
        } else {
            return new BaseResponse(0, false, "Quotations images", quotations);
        }
    }

    public BaseResponse getGames(BaseRequest baseRequest) {
        Collection<Object> quotations = customerRepository.getGames();
        if (quotations.isEmpty()) {
            return new BaseResponse(0, true, "No Games found", null);
        } else {
            return new BaseResponse(0, false, "Games list", quotations);
        }
    }

    public BaseResponse checkAppVersionNew(BaseRequest baseRequest) {
        try {
            String version_code = baseRequest.getParam("version_code");
            int versionCode = Integer.parseInt(version_code);
            String HEADER_devicetype = baseRequest.HEADER_devicetype;
            Object checkAppVersion = customerRepository.checkAppVersionNew(versionCode, HEADER_devicetype);
            if (checkAppVersion instanceof String) {
                String message = (String) checkAppVersion;
                if (message.equals("APP_ALREADY_UPDATED")) {
                    return new BaseResponse(0, true, "APP ALREADY UPDATED", null);
                }
                return new BaseResponse(0, true, message, null);
            } else {
                return new BaseResponse(0, false, "App Detail.", checkAppVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(0, true, e.getMessage(), null);
        }

    }

    public HashMap<String, String> getReferCashbonus() {
        List<TblReferralCashBonus> referCashbonus = customerRepository.getReferCashbonus();

        HashMap<String, String> output = new HashMap();
        for (TblReferralCashBonus tblReferralCashBonus : referCashbonus) {
            output.put(tblReferralCashBonus.getKey(), tblReferralCashBonus.getValue());

        }
        return output;
    }

    public void updateCustomerWalletRecharge(int customerId, float amount, String refrence_id, String jsonData, String paymentMethod, String paymentFrom, String promocode) {
        Long time = Util.getCurrentTime();

        String walletType = "deposit_wallet";
        String TransactionType = "CREDIT";
        String Type = "CUSTOMER_WALLET_RECHARGE";
        String transaction_id = "WALL" + time;
        String description = customerId + " Recharge his wallet.";

        String walletName = GlobalConstant.WALLET_TYPE.get(walletType);

        Float previousAmount = customerRepository.getCustomerWalletAmount(customerId, walletType);
        Float currentAmount = previousAmount + amount;
        customerRepository.updateWallet(customerId, amount, walletType, TransactionType);

        TblCustomerWalletHistory instance = TblCustomerWalletHistory.getInstance();
        instance.setCustomerId(customerId);
        instance.setWalletType(walletName);
        instance.setPreviousAmount(previousAmount);
        instance.setAmount(amount);
        instance.setCurrentAmount(currentAmount);
        instance.setTransactionType(TransactionType);
        instance.setType(Type);
        instance.setTransactionId(transaction_id);
        instance.setDescription(description);
        instance.setRefrenceId(refrence_id);
        instance.setJsonData(jsonData);
        instance.setPaymentMethod(paymentMethod);
        instance.setPaymentFrom(paymentFrom);
        instance.setStatus("S");
        instance.setSportId(-1);
        instance.setCreated(BigInteger.valueOf(time));

        Integer insertWallethistory = customerRepository.insertWalletHistory(instance);

        if (!Util.isEmpty(promocode)) {

            HashMap<String, Object> applyPromocode = applyPromoCode(customerId, promocode, amount);
            if (Util.isEmpty(applyPromocode.get("message").toString())) {

                LinkedHashMap<String, Object> promocodeDetailByPromocodeCustom = (LinkedHashMap<String, Object>) applyPromocode.get("data");

                int newrcbId = (int) promocodeDetailByPromocodeCustom.get("id");
                float cachBonus = (float) promocodeDetailByPromocodeCustom.get("cach_bonus");
                String cashBonusType = (String) promocodeDetailByPromocodeCustom.get("cash_bonus_type");

                if (cashBonusType.equals("F")) {
                    cachBonus = cachBonus;
                } else if (cashBonusType.equals("P")) {
                    cachBonus = (cachBonus / 100) * amount;
                }

                cachBonus = Util.numberFormate(cachBonus, 2);

                if (cachBonus > 0) {

                    String newDescription = customerId + " Get Cash Bonus " + cachBonus + " due to recharge " + amount
                            + ".";
                    String newTransactionId = "CBWALL" + time + customerId;
                    String newWalletType = "bonus_wallet";
                    String newWalletName = GlobalConstant.WALLET_TYPE.get(newWalletType);

                    Float newPreviousAmount = customerRepository.getCustomerWalletAmount(customerId, newWalletType);
                    Float newCurrentAmount = newPreviousAmount + cachBonus;
                    customerRepository.updateWallet(customerId, cachBonus, newWalletType, TransactionType);

                    TblCustomerWalletHistory instance2 = TblCustomerWalletHistory.getInstance();
                    instance2.setCustomerId(customerId);
                    instance2.setWalletType(newWalletName);
                    instance2.setPreviousAmount(newPreviousAmount);
                    instance2.setAmount(cachBonus);
                    instance2.setCurrentAmount(newCurrentAmount);
                    instance2.setTransactionType(TransactionType);
                    instance2.setType("CUSTOMER_RECEIVED_RCB");
                    instance2.setTransactionId(newTransactionId);
                    instance2.setDescription(newDescription);
                    instance2.setRcbId(newrcbId);
                    instance2.setRefCwhId(insertWallethistory);
                    instance2.setStatus("S");
                    instance2.setSportId(-1);
                    instance2.setCreated(BigInteger.valueOf(time));

                    Integer insertWallethistory2 = customerRepository.insertWalletHistory(instance2);

                    JSONObject notificationData = new JSONObject();
                    notificationData.put("noti_type", "recharge_cash_bonus");
                    String alertMessage = "Congratulations! Got " + GlobalConstant.CURRENCY_SYMBOL + cachBonus
                            + " Cash Bonus.";
                    sendNotificationAndSave(notificationData, alertMessage, true, new Integer[]{customerId});
                }
                customerRepository.updateCachBonusForCustomer(customerId, newrcbId, amount, cachBonus, 0);

            }
        }

    }

    public BaseResponse walletRechargePromocodes(BaseRequest baseRequest) {
        return customerRepository.walletRechargePromocodes(baseRequest.authUserId);
    }

    public BaseResponse getWithdrawSlab() {
        List<HashMap<String, Object>> withdrawSlabList = customerRepository.getWithdrawSlab();
        HashMap<String, String> settingData = getSettingData();
        String INSTANT_WITHDRAWAL_IS_AVAILABLE = "N";
        String INSTANT_WITHDRAWAL_ADMIN_MESSAGE = "";
        if (settingData != null) {
            INSTANT_WITHDRAWAL_IS_AVAILABLE = settingData.containsKey("INSTANT_WITHDRAWAL_IS_AVAILABLE") ? settingData.get("INSTANT_WITHDRAWAL_IS_AVAILABLE") : "N";
            INSTANT_WITHDRAWAL_ADMIN_MESSAGE = settingData.containsKey("INSTANT_WITHDRAWAL_ADMIN_MESSAGE") ? settingData.get("INSTANT_WITHDRAWAL_ADMIN_MESSAGE") : "";
        }

        return new WithdrawalSlabBaseResponse(0, false, "Instant withdraw slab list.", withdrawSlabList, INSTANT_WITHDRAWAL_IS_AVAILABLE, INSTANT_WITHDRAWAL_ADMIN_MESSAGE);
    }
}
