package com.choic11.service.admin;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.GlobalConstant.PayoutGatewayConstant;
import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.paymentgateway.PayoutUtil;
import com.choic11.repository.admin.AdminRepository;
import com.choic11.service.CustomerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Service
public class AdminService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    private SessionFactory factory;

    private Session getNewSession() {

        return factory.openSession();

    }

    private Session getSession() {
        Session session = factory.getCurrentSession();
        if (session == null) {
            return factory.openSession();
        }
        return session;
    }

    public AdminRepository getAdminRepository() {
        return adminRepository;
    }

    public BaseResponse customerWithdrawAmountFromBank(BaseRequest baseRequest) {
        String action = baseRequest.getParam("action");
        String reason = baseRequest.getParam("reason");
        if (reason == null) {
            reason = "";
        }
        String otp = baseRequest.getParam("otp");
        String adminUserId = baseRequest.getParam("user_id");

        HashSet<String> availableOptions = new HashSet<String>();
        availableOptions.add("A");
        availableOptions.add("R");
        if (!availableOptions.contains(action)) {
            return new BaseResponse(0, true, "Action must be A or R.", null);
        }

        boolean isAffiliate = Boolean.parseBoolean(baseRequest.getParam("affiliate"));

        int entryId = Integer.parseInt(baseRequest.getParam("entry_id"));

        HashMap<String, Object> withdrawEntryId = adminRepository.getWithdrawEntryId(entryId, isAffiliate);
        if (withdrawEntryId == null) {
            return new BaseResponse(0, true, "Entry detail not found.", withdrawEntryId);
        }
        float amount = (float) withdrawEntryId.get("amount");
        float instantFee = (float) withdrawEntryId.get("instantFee");
        String withdrawType = (String) withdrawEntryId.get("withdrawType");

        String withdrawType_BANK = "B";
        String withdrawType_PAYTM = "P";

        if (action.equals("A")) {
            if (amount <= 1 || (amount - instantFee) <= 1) {
                return new BaseResponse(0, true, "Invalid withdraw amount.", null);
            }
            float winningWallet = (float) withdrawEntryId.get("winningWallet");
            float pendingWidWallet = (float) withdrawEntryId.get("pendingWidWallet");

            if (!isAffiliate && amount > winningWallet) {
                return new BaseResponse(0, true, "Insufficient amount in customer wallet.", null);
            }
            if (!isAffiliate) {
                if (withdrawType.equals(withdrawType_BANK)) {
                    int paincardId = (int) withdrawEntryId.get("paincardId");
                    int bankdetailId = (int) withdrawEntryId.get("bankdetailId");

                    if (paincardId == 0 || bankdetailId == 0) {
                        return new BaseResponse(0, true, "Pan or Bank detail not proper or not approved.", null);
                    }

                    String panStatus = (String) withdrawEntryId.get("panStatus");
                    String bankStatus = (String) withdrawEntryId.get("bankStatus");

                    if (!panStatus.equals("A") || !bankStatus.equals("A")) {
                        return new BaseResponse(0, true, "Pan or Bank detail not proper or not approved.", null);
                    }

                } else if (withdrawType.equals(withdrawType_PAYTM)) {
                    int paincardId = (int) withdrawEntryId.get("paincardId");
                    if (paincardId == 0) {
                        return new BaseResponse(0, true, "Pan detail not proper or not approved.", null);
                    }

                    String panStatus = (String) withdrawEntryId.get("panStatus");

                    if (!panStatus.equals("A")) {
                        return new BaseResponse(0, true, "Pan detail not proper or not approved.", null);
                    }

                    String customerPaytmNumber = (String) withdrawEntryId.get("paytmphone");
                    if (Util.isEmpty(customerPaytmNumber)) {
                        return new BaseResponse(0, true, "Paytm number is not valid.", null);
                    }
                }
            }
        }

        int customerId = (int) withdrawEntryId.get("customerId");
        adminRepository.insertWithdrawRequestApproveRejectLogs(adminUserId, entryId, action, isAffiliate,
                baseRequest.HEADER_ipAddress);

        boolean updateWithdrawEntryStatus = adminRepository.updateWithdrawEntryStatus(entryId, isAffiliate, "RP");
        if (!updateWithdrawEntryStatus) {
            return new BaseResponse(0, true, "Unable to proceed 1.", null);
        }

        if (action.equals("R")) {
            boolean updateWithdrawEntryStatusRejected = adminRepository
                    .updateWithdrawEntryStatusRejected(customerService, entryId, customerId, isAffiliate, reason);
            if (!updateWithdrawEntryStatusRejected) {
                adminRepository.updateWithdrawEntryStatus(entryId, isAffiliate, "P");
                return new BaseResponse(0, true, "Unable to proceed 2.", null);
            }

            try {
                String noti_type = "withdraw_request_rejected";
                JSONObject data = new JSONObject();
                data.put("noti_type", noti_type);
                String alertMessage = reason;
                customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[]{customerId});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new BaseResponse(0, false, "Transaction rejected successfully.", null);
        } else if (action.equals("A")) {

            if (PayoutGatewayConstant.CURRENT_PAYOUT_METHOD.equals(PayoutGatewayConstant.PAYOUT_METHOD_DIRECT)) {
                String referenceId = "direct";
                String utr = "";
                String json_data = new JSONObject().toString();
                adminRepository.insertPayoutLog(customerId, entryId, referenceId, json_data, isAffiliate);
                boolean updateWithdrawEntryStatusApproved = adminRepository.updateWithdrawEntryStatusApproved(customerService, entryId, customerId, isAffiliate, referenceId, utr, json_data, amount);
                if (!updateWithdrawEntryStatusApproved) {
                    adminRepository.updateWithdrawEntryStatus(entryId, isAffiliate, "P");
                    return new BaseResponse(0, true, "Unable to proceed 3.", null);
                }
                try {
                    String noti_type = "withdraw_request_approved";
                    JSONObject data = new JSONObject();
                    data.put("noti_type", noti_type);
                    String alertMessage = "Congratulations! Your withdraw request has been approved";
                    customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[]{customerId});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new BaseResponse(0, false, "Transaction approved successfully.", null);
            } else {
                String payoutGateway = PayoutGatewayConstant.CURRENT_PAYOUT_METHOD;
                if (withdrawType.equals("P")) {
                    payoutGateway = PayoutGatewayConstant.PAYOUT_METHOD_PAYTM;

                    String paytmPayoutCallBackUrl = baseRequest.REQUEST_DOMAIN;
                    if (!Util.isEmpty(GlobalConstant.getProjectSubDirectory())) {
                        paytmPayoutCallBackUrl += "/" + GlobalConstant.getProjectSubDirectory();
                    }
                    paytmPayoutCallBackUrl += "/payoutwebhook/";

                    withdrawEntryId.put("paytmPayoutCallBackUrl", paytmPayoutCallBackUrl);
                }
                LinkedHashMap<String, Object> customerWithdrawAmountFromBank = PayoutUtil
                        .customerWithdrawAmountFromBank(withdrawEntryId, isAffiliate, otp, payoutGateway);
                if (!customerWithdrawAmountFromBank.containsKey("completed")) {
                    adminRepository.updateWithdrawEntryStatus(entryId, isAffiliate, "P");
                    return new BaseResponse((int) customerWithdrawAmountFromBank.get("errorCode"), true,
                            (String) customerWithdrawAmountFromBank.get("message"), null);
                } else {

                    String referenceId = (String) customerWithdrawAmountFromBank.get("merchantRefId");
                    String utr = (String) customerWithdrawAmountFromBank.get("utr");
                    String json_data = (String) customerWithdrawAmountFromBank.get("jsonData");

                    adminRepository.insertPayoutLog(customerId, entryId, referenceId, json_data, isAffiliate);

                    boolean updateWithdrawEntryStatusHold = adminRepository.updateWithdrawEntryStatusHold(entryId,
                            isAffiliate, referenceId, utr, json_data);
                    if (!updateWithdrawEntryStatusHold) {
                        return new BaseResponse(0, true, "Unable to proceed 3.", null);
                    }
                    if(customerWithdrawAmountFromBank.containsKey("finalStatus") &&
                            ((String)customerWithdrawAmountFromBank.get("finalStatus")).equals("C")){

                    }
                    return new BaseResponse(0, false, (String) customerWithdrawAmountFromBank.get("message"), null);
                }
            }
        } else {
            return new BaseResponse(0, true, "Action must be A or R.", null);
        }
    }

}
