package com.epg.mobile.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.dto.Email;
import com.epg.mobile.dto.SMSMessage;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;

import freemarker.template.TemplateException;

@Service
public class NotificationService {
	private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
	
	private static NotificationService _this;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private FTLService ftlService;
	
	@PostConstruct
	public void init() {
		_this = appContext.getBean(NotificationService.class);
	}
	
	public static NotificationService getInstance() {
		return _this;
	}
	
	public void sendEmail(TestCase tc, String resultLocation) throws IOException, TemplateException {
		LOG.info("Sending email for {} ", tc.getName());
		Email email = ftlService.getEmail(tc, resultLocation);
		String responseText = Request
				.Post(PropertiesUtils.getProperty("email.api"))
				.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
				.bodyForm(
						Form.form()
								.add("sender", email.getFrom())
								.add("recepientList", email.getTo())
								.add("subject", email.getSubject())
								.add("body", email.getBody())
								.build(),
						Consts.UTF_8).execute().returnContent().asString();
		
		LOG.info("Email service resonse {}", responseText);
	}
	
	public void sendSMS(boolean retry, final List<CompletedTest> failedTestCases) throws JSONException, IOException, TemplateException, TestsXMLException {
		if ("true".equalsIgnoreCase(PropertiesUtils.getProperty(AppConstants.SMS_ENABLE))
				&& !failedTestCases.isEmpty()) {
			LOG.info("Sending sms @ {}", new Date());
			SMSMessage sms = ftlService.getSMS(failedTestCases);
			String message = sms.getMessage();
			int sms_size = Integer.parseInt(PropertiesUtils.getProperty(AppConstants.SMS_MAX_SIZE));
			if (StringUtils.isNotEmpty(message)) {
				if (message.length() > sms_size) {
					message = message.substring(0, sms_size);
				}
			}
			String responseText = Request
					.Post(PropertiesUtils.getProperty("sms.api"))
					.addHeader(AppConstants.SMS_SOURCE_ID, PropertiesUtils.getProperty(AppConstants.SMS_SOURCE_ID))
					.addHeader(AppConstants.SMS_SOURCE_GUID, PropertiesUtils.getProperty(AppConstants.SMS_SOURCE_GUID))
					.bodyForm(
							Form.form()
									.add("sender", sms.getFrom())
									.add("recepientList", sms.getNumbers())
									.add("message", message)
									.build(),
							Consts.UTF_8).execute().returnContent().asString();
			
			if (retry) {
				if (StringUtils.isEmpty(responseText)
						|| (StringUtils.isNotEmpty(responseText) && responseText.contains("ERROR"))) {
					refreshSMSGuid();
					sendSMS(false, failedTestCases);
				}
			}
			LOG.info("SMS service resonse {}", responseText);
		}
	}

	public static void refreshSMSGuid() {
		try {
		String jsonString = Request.Get(PropertiesUtils
				.getProperty(AppConstants.SMS_AUTHTOKEN_API))
		.addHeader(AppConstants.SMS_SOURCE_ID,
				PropertiesUtils.getProperty(AppConstants.SMS_SOURCE_ID))
		.addHeader(AppConstants.SMS_AUTH_PASS, 
				PropertiesUtils.getProperty(AppConstants.SMS_AUTH_PASS))
		.execute().returnContent().asString();
			if (StringUtils.isNotEmpty(jsonString)) {
				JSONObject json = new JSONObject(jsonString);
				PropertiesUtils.setProperty(AppConstants.SMS_SOURCE_GUID, 
						json.getString(AppConstants.SMS_SOURCE_GUID));
			}
		} catch (Exception e) {
			LOG.error("Error occured while refreshing SMS GUID", e);
		}
	}
}
