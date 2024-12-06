package com.choic11.testing;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.choic11.GlobalConstant.FirebaseConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.fcm.FcmUtil;
import com.choic11.model.BaseRequest;
import com.choic11.model.TblTemplate;
import com.choic11.model.response.BaseResponse;
import com.choic11.sms.SmsUtil;
import com.choic11.smtp.SmtpUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TestService {

    public BaseResponse testEmail(BaseRequest baseRequest) {

        String subject = "TEST JAVA MAIL";
        String message = "TEST JAVA MAIL";
        String receiver_email = "manish.kumar.bluestk@gmail.com";
        String receiver_name = "Manish";

        if (baseRequest.hasParam("subject")) {
            subject = baseRequest.getParam("subject");
        }

        if (baseRequest.hasParam("message")) {
            message = baseRequest.getParam("message");
        }

        if (baseRequest.hasParam("receiver_email")) {
            receiver_email = baseRequest.getParam("receiver_email");
        }

        if (baseRequest.hasParam("receiver_name")) {
            receiver_name = baseRequest.getParam("receiver_name");
        }

        String sendSmtpmail = SmtpUtil.sendSmtpmail(subject, message, receiver_email, receiver_name);
        if (sendSmtpmail.equals("CURRENT_SMTP_PROVIDER")) {
            return new BaseResponse(0, true, "CURRENT_SMTP_PROVIDER is blank", null);
        } else if (sendSmtpmail.equals("CURRENT_SMTP_PROVIDER_NOT_MATCH")) {
            return new BaseResponse(0, true, "CURRENT_SMTP_PROVIDER_NOT_MATCH is not match", null);
        } else if (sendSmtpmail.equals("SMTP_SERVER")) {
            return new BaseResponse(0, true, "CURRENT_SMTP_PROVIDER SMTP_SERVER is blank", null);
        } else if (sendSmtpmail.equals("FAILED")) {
            return new BaseResponse(0, true, "FAILED", null);
        } else {
            return new BaseResponse(0, true, "SUCCESS", null);
        }

    }

    public BaseResponse testSms(BaseRequest baseRequest) {
        String country_code = "";
        String to = "8561887456";
        String otp = "5678";
        String message = "Welcome to Trump11 ! Verification PIN for verifying your account is " + otp + ".";

        if (baseRequest.hasParam("country_code")) {
            country_code = baseRequest.getParam("country_code");
        }

        if (baseRequest.hasParam("to")) {
            to = baseRequest.getParam("to");
        }

        if (baseRequest.hasParam("message")) {
            message = baseRequest.getParam("message");
        }

        HashMap<String, Object> data = new HashMap();
        data.put("otp", otp);

        TblTemplate tblTemplate = new TblTemplate();
        tblTemplate.setContent(message);

        String sendSms = SmsUtil.sendSms(tblTemplate, to, country_code, data);
        if (sendSms.equals("CURRENT_SMS_PROVIDER")) {
            return new BaseResponse(0, true, "CURRENT_SMS_PROVIDER is blank", null);
        } else if (sendSms.equals("CURRENT_SMS_PROVIDER_NOT_MATCH")) {
            return new BaseResponse(0, true, "CURRENT_SMS_PROVIDER_NOT_MATCH is not match", null);
        } else if (sendSms.equals("SMS_SENDER_NAME")) {
            return new BaseResponse(0, true, "CURRENT_SMS_PROVIDER SMS_SENDER_NAME is blank", null);
        } else if (sendSms.equals("FAILED")) {
            return new BaseResponse(0, true, "FAILED", null);
        } else {
            return new BaseResponse(0, true, "SUCCESS", sendSms);
        }
    }

    public BaseResponse testNotification(BaseRequest baseRequest) {
        JSONObject data = new JSONObject();
        List<String> tokens = new ArrayList<String>();
        String title = "JAVA TITLE";
        String alert_message = "JAVA NOTIFICATION MESSAGE";
        String noti_type = "TEST_TYPE";
        String device_type = "A";
        String fcm_key = FirebaseConstant.FCM_KEY;

        if (baseRequest.hasParam("fcm_key")) {
            fcm_key = baseRequest.getParam("fcm_key");
        }

        if (baseRequest.hasParam("token")) {
            tokens.clear();
            tokens.add(baseRequest.getParam("token"));
        }
        if (baseRequest.hasParam("title")) {
            title = baseRequest.getParam("title");
            data.put("title", title);
        }
        if (baseRequest.hasParam("alert_message")) {
            alert_message = baseRequest.getParam("alert_message");
            data.put("message", alert_message);
        }
        if (baseRequest.hasParam("noti_type")) {
            noti_type = baseRequest.getParam("noti_type");
        }
        if (baseRequest.hasParam("device_type")) {
            device_type = baseRequest.getParam("device_type");
        }
        if (baseRequest.hasParam("noti_thumb")) {
            String noti_thumb = baseRequest.getParam("noti_thumb");
            data.put("noti_thumb", noti_thumb);
        }

        String sendNotification = FcmUtil.sendNotification(data, tokens, alert_message, noti_type, device_type);
        if (sendNotification.equals("NO_TOKEN")) {
            return new BaseResponse(0, true, "NO_TOKEN for send notification", null);
        } else if (sendNotification.equals("FAILED")) {
            return new BaseResponse(0, true, "FAILED", null);
        } else {
            return new BaseResponse(0, true, "SUCCESS", sendNotification);
        }
    }

    public BaseResponse testPdf(BaseRequest baseRequest) {
        try {
            XvfbConfig xc = new XvfbConfig();
            xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

            String findExecutable = WrapperConfig.findExecutable();
            WrapperConfig wc = new WrapperConfig(findExecutable);
            wc.setXvfbConfig(xc);
            Pdf pdf = new Pdf(wc);
            pdf.addParam(new Param("--orientation", "landscape"), new Param("--margin-left", "1mm"),
                    new Param("--margin-right", "1mm"), new Param("--margin-top", "1mm"),
                    new Param("--margin-bottom", "1mm"), new Param("--enable-javascript"));
            pdf.addPageFromString(
                    "<html><body><table><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr><tr><td>A</td><td>B</td></tr></table></body></html>");
            pdf.saveAs(GlobalConstant.getContestPdfLocalDirectory() + "output2.pdf");
            pdf.setAllowMissingAssets();
            return new BaseResponse(0, false, "output2.pdf", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(0, true, e.getMessage(), null);
        }

    }

}
