package com.choic11.fcm;

import com.choic11.GlobalConstant.FirebaseConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

public class FcmUtil {

    public static String sendNotification(JSONObject message, List<String> registrationIds, String alertMessage,
                                          String notiType, String deviceType) {

        if (registrationIds == null || registrationIds.isEmpty()) {
            return "NO_TOKEN";
        }

        String sound = "default";

        String notiTitle = GlobalConstant.APP_NAME;
        if (notiType.equals("adminalert") || notiType.equals("lineup_out") || notiType.equals("redeem_coupon")) {

            notiTitle = message.get("title").toString();
        }

        message.put("noti_time", Util.getCurrentTime());
        message.put("message", alertMessage);
        message.put("title", notiTitle);

        JSONObject notification = new JSONObject();
        notification.put("title", message.get("title").toString());
        notification.put("body", message.get("message").toString());
        notification.put("sound", sound);

        JSONObject fields = new JSONObject();
        fields.put("registration_ids", new JSONArray(registrationIds));
        fields.put("data", message);
        fields.put("notification", notification);

        if (message.has("noti_thumb") && !Util.isEmpty(message.get("noti_thumb").toString())) {
            fields.put("content_available", true);
            fields.put("mutable_content", true);
            message.put("image", message.get("noti_large"));
            notification.put("image", message.get("noti_large"));
        }

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        headers.put("Authorization", "key=" + FirebaseConstant.FCM_KEY);
        headers.put("Content-Type", "application/json");

        Response excuteCurlRequest = Curl.excuteCurlRequest(FirebaseConstant.FIRE_BASE_URL, "POST", fields, headers);
        if (excuteCurlRequest != null) {
            try {
                return excuteCurlRequest.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "FAILED";

    }

    public static String generateDynamicLink(String referralCode) {

        JSONObject suffix = new JSONObject();
        suffix.put("option", "UNGUESSABLE");

        JSONObject desktopInfo = new JSONObject();
        desktopInfo.put("desktopFallbackLink", FirebaseConstant.FCM_DYNAMIC_desktopFallbackLink);

        JSONObject socialMetaTagInfo = new JSONObject();
        socialMetaTagInfo.put("socialTitle", FirebaseConstant.FCM_DYNAMIC_socialTitle);
        socialMetaTagInfo.put("socialDescription", FirebaseConstant.FCM_DYNAMIC_socialDescription);
        socialMetaTagInfo.put("socialImageLink", FirebaseConstant.FCM_DYNAMIC_socialImageLink);

        JSONObject androidInfo = new JSONObject();
        androidInfo.put("androidPackageName", FirebaseConstant.FCM_DYNAMIC_androidPackageName);
        androidInfo.put("androidFallbackLink", FirebaseConstant.FCM_DYNAMIC_androidFallbackLink + referralCode);

        JSONObject dynamicLinkInfo = new JSONObject();
        dynamicLinkInfo.put("domainUriPrefix", FirebaseConstant.FCM_DYNAMIC_domainUriPrefix);
        dynamicLinkInfo.put("link", FirebaseConstant.FCM_DYNAMIC_link + referralCode);
        dynamicLinkInfo.put("androidInfo", androidInfo);
        dynamicLinkInfo.put("socialMetaTagInfo", socialMetaTagInfo);
        dynamicLinkInfo.put("desktopInfo", desktopInfo);

        JSONObject send_json = new JSONObject();

        send_json.put("dynamicLinkInfo", dynamicLinkInfo);
        send_json.put("suffix", suffix);

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", "application/json");

        Response excuteCurlRequest = Curl.excuteCurlRequest(FirebaseConstant.FIREBASE_SHORTLINK_URL, "POST", send_json,
                headers);
        if (excuteCurlRequest != null) {
            try {
                String response = excuteCurlRequest.body().string();
                if (!Util.isEmpty(response)) {
                    JSONObject responsedata = new JSONObject(response);
                    if (responsedata.has("shortLink")) {
                        return responsedata.getString("shortLink");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

}
