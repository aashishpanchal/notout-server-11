package com.choic11.smtp;

import com.choic11.GlobalConstant.SmtpConstant;
import com.choic11.Util;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SmtpUtil {

	public static String sendSmtpmail(String subject, String message, String receiver_email, String receiver_name) {

		if (Util.isEmpty(SmtpConstant.CURRENT_SMTP_PROVIDER)) {
			return "CURRENT_SMTP_PROVIDER";
		}

		if (SmtpConstant.CURRENT_SMTP_PROVIDER.equals(SmtpConstant.SMTP_PROVIDER_AWS)) {
			return sendSmtpMailAWS(subject, message, receiver_email, receiver_name);
		}
		return "CURRENT_SMTP_PROVIDER_NOT_MATCH";

	}

	private static String sendSmtpMailAWS(String subject, String message, String receiver_email, String receiver_name) {

		if (Util.isEmpty(SmtpConstant.SMTP_SERVER_AWS)) {
			return "SMTP_SERVER";
		}

		try {
			Properties properties = new Properties();
			properties.setProperty("mail.debug", "false");
			properties.setProperty("mail.smtp.auth", "true");
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.smtp.connectiontimeout", "5000");
			properties.setProperty("mail.smtp.timeout", "5000");
			properties.setProperty("mail.smtp.writetimeout", "5000");

			JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
			javaMailSender.setJavaMailProperties(properties);
			javaMailSender.setHost(SmtpConstant.SMTP_SERVER_AWS);
			javaMailSender.setUsername(SmtpConstant.SMTP_USERNAME_AWS);
			javaMailSender.setPassword(SmtpConstant.SMTP_PASSWORD_AWS);
			javaMailSender.setPort(SmtpConstant.SMTP_PORT_AWS);
			javaMailSender.setProtocol("smtp");

			MimeMessage msg = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(msg, true,"UTF-8");

			helper.setFrom(SmtpConstant.SMTP_FROM_EMAIL_AWS, SmtpConstant.SMTP_FROM_NAME_AWS);
			helper.setTo(receiver_email);

			helper.setSubject(subject);

			helper.setText(message, true);

			javaMailSender.send(msg);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "FAILED";

	}

}
