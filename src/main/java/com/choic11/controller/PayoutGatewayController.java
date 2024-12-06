package com.choic11.controller;


import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.paymentgateway.Paytm;
import com.choic11.service.CustomerService;
import com.choic11.service.admin.AdminService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/payoutwebhook")
public class PayoutGatewayController extends BaseController {

	@Autowired
	CustomerService customerService;

	@Autowired
	AdminService adminService;

	@PostMapping("payu_payout_webhook")
	public ResponseEntity<Object> payuPayoutHook(HttpServletRequest request) {
		
		   String receivedJson="";
		   StringBuilder stringBuilder = new StringBuilder();
		   BufferedReader bufferedReader = null;
		   try {
		     InputStream inputStream = request.getInputStream();
		     if (inputStream != null) {
		       bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		       char[] charBuffer = new char[128];
		       int bytesRead = -1;
		       while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		         stringBuilder.append(charBuffer, 0, bytesRead);
		       }
		     } else {
		       stringBuilder.append("");
		     }
		     receivedJson=stringBuilder.toString().trim();
		   } catch (IOException ex) {
		       ex.printStackTrace();
		   } finally {
		     if (bufferedReader != null) {
		       try {
		         bufferedReader.close();
		       } catch (IOException ex) {
		    	   ex.printStackTrace();
		       }
		     }
		   }

		JSONObject jsonFromPayuData = new JSONObject();
		if(!Util.isEmpty(receivedJson)) {
			jsonFromPayuData=new JSONObject(receivedJson);
		}

		String merchantReferenceId = jsonFromPayuData.optString("merchantReferenceId", "");
		if (Util.isEmpty(merchantReferenceId)) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO merchantReferenceId", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}

		String jsonFromPayu = jsonFromPayuData.toString();

		String[] split = merchantReferenceId.split("_");
		boolean isAffiliate = split.length == 4;

		HashMap<String, Object> withdrawEntryIdByReferenceId = adminService.getAdminRepository()
				.getWithdrawEntryIdByReferenceId(merchantReferenceId, isAffiliate);
		if (withdrawEntryIdByReferenceId == null) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO withdrawEntryIdByReferenceId", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}

		String status = (String) jsonFromPayuData.get("event");
		String payoutId = jsonFromPayuData.getString("payuRefId");

		int entryId = (int) withdrawEntryIdByReferenceId.get("id");
		int customerId = (int) withdrawEntryIdByReferenceId.get("customerId");
		float amount = (float) withdrawEntryIdByReferenceId.get("amount");

		adminService.getAdminRepository().insertPayoutLog(customerId, entryId, merchantReferenceId, jsonFromPayu,
				isAffiliate);

		if (!status.equals("TRANSFER_SUCCESS")) {

			String reason = jsonFromPayuData.optString("msg", "");

			boolean updateWithdrawEntryStatusRejected = adminService.getAdminRepository()
					.updateWithdrawEntryStatusRejected(customerService, entryId, customerId, isAffiliate, reason);
			if (!updateWithdrawEntryStatusRejected) {
				BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 2.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
			try {
				String noti_type = "withdraw_request_rejected";
				JSONObject data = new JSONObject();
				data.put("noti_type", noti_type);
				String alertMessage = reason;
				customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[] { customerId });
			} catch (Exception e) {
				e.printStackTrace();
			}
			BaseResponse baseResponse = new BaseResponse(0, false, "Transaction rejected successfully.", null);
			return echoRespose(baseResponse, HttpStatus.OK);
		} else {

			boolean updateWithdrawEntryStatusApproved = adminService.getAdminRepository()
					.updateWithdrawEntryStatusApproved(customerService, entryId, customerId, isAffiliate,
							merchantReferenceId, payoutId, jsonFromPayu, amount);
			if (!updateWithdrawEntryStatusApproved) {
				BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 3.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
			try {
				String noti_type = "withdraw_request_approved";
				JSONObject data = new JSONObject();
				data.put("noti_type", noti_type);
				String alertMessage = "Congratulations! Your withdraw request has been approved";
				customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[] { customerId });
			} catch (Exception e) {
				e.printStackTrace();
			}
			BaseResponse baseResponse = new BaseResponse(0, false, "Transaction approved successfully.", null);
			return echoRespose(baseResponse, HttpStatus.OK);
		}
	}


	@GetMapping("paytm_payout_status/{orderId}")
	public ResponseEntity<Object> declareMatchResult(HttpServletRequest request,
													 @PathVariable(name = "orderId") String orderId) {

		BaseRequest baseRequest = new BaseRequest(request);
		LinkedHashMap<String, Object> payoutOrderIdStatusData= Paytm.getPayoutStatusByOrderId(orderId);
		BaseResponse response = new BaseResponse(0, false, "data",payoutOrderIdStatusData);
		return echoRespose(response, HttpStatus.OK);
	}

	@PostMapping("paytm_payout_webhook")
	public ResponseEntity<Object> paytmPayoutHook(HttpServletRequest request) {
		
		   String receivedJson="";
		   StringBuilder stringBuilder = new StringBuilder();
		   BufferedReader bufferedReader = null;
		   try {
		     InputStream inputStream = request.getInputStream();
		     if (inputStream != null) {
		       bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		       char[] charBuffer = new char[128];
		       int bytesRead = -1;
		       while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		         stringBuilder.append(charBuffer, 0, bytesRead);
		       }
		     } else {
		       stringBuilder.append("");
		     }
		     receivedJson=stringBuilder.toString().trim();
		   } catch (IOException ex) {
		       ex.printStackTrace();
		   } finally {
		     if (bufferedReader != null) {
		       try {
		         bufferedReader.close();
		       } catch (IOException ex) {
		    	   ex.printStackTrace();
		       }
		     }
		   }

		JSONObject jsonFromPaytmData = new JSONObject();
		if(!Util.isEmpty(receivedJson)) {
			jsonFromPaytmData=new JSONObject(receivedJson);
		}

		String merchantReferenceId = "";
		if(jsonFromPaytmData.has("result")) {
			merchantReferenceId=jsonFromPaytmData.getJSONObject("result").getString("orderId");
		}
		if (Util.isEmpty(merchantReferenceId)) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO orderId found", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}
		
		String jsonFromPaytm = jsonFromPaytmData.toString();
		
		String[] split = merchantReferenceId.split("_");
		boolean isAffiliate = split.length == 3;

		HashMap<String, Object> withdrawEntryIdByReferenceId = adminService.getAdminRepository()
				.getWithdrawEntryIdByReferenceId(merchantReferenceId, isAffiliate);
		if (withdrawEntryIdByReferenceId == null) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO withdrawEntryIdByReferenceId", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}
		
		int entryId = (int) withdrawEntryIdByReferenceId.get("id");
		int customerId = (int) withdrawEntryIdByReferenceId.get("customerId");
		float amount = (float) withdrawEntryIdByReferenceId.get("amount");

		adminService.getAdminRepository().insertPayoutLog(customerId, entryId, merchantReferenceId, jsonFromPaytm,
				isAffiliate);
		
		LinkedHashMap<String, Object> payoutOrderIdStatusData= Paytm.getPayoutStatusByOrderId(merchantReferenceId);
		if (!payoutOrderIdStatusData.containsKey("completed")) {
			int errorCode=(int)payoutOrderIdStatusData.get("errorCode");
			if(errorCode == 6) {
				String reason = (String) payoutOrderIdStatusData.get("message");

				boolean updateWithdrawEntryStatusRejected = adminService.getAdminRepository()
						.updateWithdrawEntryStatusRejected(customerService, entryId, customerId, isAffiliate, reason);
				if (!updateWithdrawEntryStatusRejected) {
					BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 2.", null);
					return echoRespose(baseResponse, HttpStatus.OK);
				}
				try {
					String noti_type = "withdraw_request_rejected";
					JSONObject data = new JSONObject();
					data.put("noti_type", noti_type);
					String alertMessage = reason;
					customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[] { customerId });
				} catch (Exception e) {
					e.printStackTrace();
				}
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction rejected successfully.", null);
				return echoRespose(baseResponse, HttpStatus.OK);	
			}else {
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction hold successfully.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
		}else {
			
			String jsonData=(String) payoutOrderIdStatusData.get("jsonData");
			String payoutId=(String) payoutOrderIdStatusData.get("utr");
			
			boolean updateWithdrawEntryStatusApproved = adminService.getAdminRepository()
					.updateWithdrawEntryStatusApproved(customerService, entryId, customerId, isAffiliate,
							merchantReferenceId, payoutId, jsonData, amount);
			if (!updateWithdrawEntryStatusApproved) {
				BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 3.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
			try {
				String noti_type = "withdraw_request_approved";
				JSONObject data = new JSONObject();
				data.put("noti_type", noti_type);
				String alertMessage = "Congratulations! Your withdraw request has been approved";
				customerService.sendNotificationAndSave(data, alertMessage, true, new Integer[] { customerId });
			} catch (Exception e) {
				e.printStackTrace();
			}
			BaseResponse baseResponse = new BaseResponse(0, false, "Transaction approved successfully.", null);
			return echoRespose(baseResponse, HttpStatus.OK);
			
		}
	}

	@PostMapping("cashfree_payout_webhook")
	public ResponseEntity<Object> cashfreePayoutHook(HttpServletRequest request) {

		JSONObject jsonFromCashfreeData = new JSONObject();

		Map<String, String[]> parameters = request.getParameterMap();
		Set<String> keySet = parameters.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			jsonFromCashfreeData.put(key, parameters.get(key)[0]);
		}


		String merchantReferenceId = jsonFromCashfreeData.optString("transferId", "");
		if (Util.isEmpty(merchantReferenceId)) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO merchantReferenceId", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}

		String jsonFromCashfree = jsonFromCashfreeData.toString();

		String[] split = merchantReferenceId.split("_");
		boolean isAffiliate = split.length == 4;

		HashMap<String, Object> withdrawEntryIdByReferenceId = adminService.getAdminRepository()
				.getWithdrawEntryIdByReferenceId(merchantReferenceId, isAffiliate);
		if (withdrawEntryIdByReferenceId == null) {
			BaseResponse baseResponse = new BaseResponse(0, true, "NO withdrawEntryIdByReferenceId", null);

			return echoRespose(baseResponse, HttpStatus.OK);
		}

		String status = (String) jsonFromCashfreeData.get("event");
		String payoutId = jsonFromCashfreeData.has("utr")?jsonFromCashfreeData.getString("utr"):"";
		String failedReason = jsonFromCashfreeData.has("reason")?jsonFromCashfreeData.getString("reason"):"";

		int entryId = (int) withdrawEntryIdByReferenceId.get("id");
		int customerId = (int) withdrawEntryIdByReferenceId.get("customerId");
		float amount = (float) withdrawEntryIdByReferenceId.get("amount");
		String dbStatus = (String) withdrawEntryIdByReferenceId.get("status");

		adminService.getAdminRepository().insertPayoutLog(customerId, entryId, merchantReferenceId, jsonFromCashfree,
				isAffiliate);

		if (status.equals("TRANSFER_REVERSED") || status.equals("TRANSFER_FAILED")) {

			if(!dbStatus.equals("R")) {
				String reason = "Transaction faild or reversed.";
				if(!Util.isEmpty(failedReason)){
					reason=failedReason;
				}

				boolean updateWithdrawEntryStatusRejected = adminService.getAdminRepository()
						.updateWithdrawEntryStatusRejected(customerService, entryId, customerId, isAffiliate, reason);
				if (!updateWithdrawEntryStatusRejected) {
					BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 2.", null);
					return echoRespose(baseResponse, HttpStatus.OK);
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
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction rejected successfully.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}else{
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction already rejected.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
		} else if(status.equals("TRANSFER_SUCCESS")) {

			if(!dbStatus.equals("C")) {
				boolean updateWithdrawEntryStatusApproved = adminService.getAdminRepository()
						.updateWithdrawEntryStatusApproved(customerService, entryId, customerId, isAffiliate,
								merchantReferenceId, payoutId, jsonFromCashfree, amount);
				if (!updateWithdrawEntryStatusApproved) {
					BaseResponse baseResponse = new BaseResponse(0, true, "Unable to proceed 3.", null);
					return echoRespose(baseResponse, HttpStatus.OK);
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
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction approved successfully.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}else{
				BaseResponse baseResponse = new BaseResponse(0, false, "Transaction already approved.", null);
				return echoRespose(baseResponse, HttpStatus.OK);
			}
		}else{
			BaseResponse baseResponse = new BaseResponse(0, false, "Status not handled.", null);
			return echoRespose(baseResponse, HttpStatus.OK);
		}
	}
}
