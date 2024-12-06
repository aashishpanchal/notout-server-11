package com.choic11.paymentgateway;

import com.choic11.GlobalConstant.PayoutGatewayConstant;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PayoutUtil {

	public static LinkedHashMap<String, Object> customerWithdrawAmountFromBank(HashMap<String, Object> withdrawEntryId,
                                                                               boolean isAffiliate, String otp, String payoutGateway) {

		if (payoutGateway.equals(PayoutGatewayConstant.PAYOUT_METHOD_PAYU)) {
			return PAYU.customerWithdrawAmountFromBank(withdrawEntryId, otp, isAffiliate);
		}else if (payoutGateway.equals(PayoutGatewayConstant.PAYOUT_METHOD_PAYTM)) {
			return Paytm.customerWithdrawAmountFromBank(withdrawEntryId, otp, isAffiliate);
		}else if (payoutGateway.equals(PayoutGatewayConstant.PAYOUT_METHOD_CASHFREE)) {
			return CASHFREE.customerWithdrawAmountFromBank(withdrawEntryId, otp, isAffiliate);
		} else {
			LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("errorCode", 0);
			data.put("message", "Invalid payout method");
			return data;
		}

	}

}
