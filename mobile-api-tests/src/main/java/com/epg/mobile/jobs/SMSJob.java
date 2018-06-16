package com.epg.mobile.jobs;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.service.NotificationService;
import com.epg.mobile.utils.ReportingUtils;

import freemarker.template.TemplateException;

public class SMSJob implements Job {
	
	private static final Logger LOG = LoggerFactory.getLogger(SMSJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			List<CompletedTest> failedTestCases = ReportingUtils.getFailedTestCasesForSMS();
			NotificationService.getInstance().sendSMS(true, failedTestCases);
		} catch (JSONException | IOException | TemplateException | TestsXMLException e) {
			LOG.error("Error occured while sending SMS", e);
		}
	}
	
}
