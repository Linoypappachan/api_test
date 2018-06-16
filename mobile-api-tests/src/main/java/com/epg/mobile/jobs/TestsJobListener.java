package com.epg.mobile.jobs;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.service.NotificationService;
import com.epg.mobile.service.SchedulerService;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.AppConstants;

import freemarker.template.TemplateException;

public class TestsJobListener implements JobListener{
	
	public static final Logger LOG = LoggerFactory.getLogger(TestsJobListener.class);
	
	private static final String LISTENER_NAME = "tests-job-listener";

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// method to use just before job is being executed
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// method to use when a job is dismissed (or) unscheduled
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		// method to use after a job is executed
		String jobId = context.getJobDetail().getKey().getName();
		TestCase tc = (TestCase) context.getMergedJobDataMap().get(jobId);
		String testResultLocation = context.getMergedJobDataMap()
					.getString(AppConstants.TEST_RESULT_LOCATION_KEY);
		if (jobId.endsWith(AppConstants.RUN_NOW)) {
			try {
				SchedulerService.getInstance().unScheduleTestCase(jobId);
				LOG.info("Successfully unscheduled immediate job for test case {}", jobId);
			} catch (SchedulerException e) {
				LOG.error("Error occured while unscheduling the immediate job for test case {}", jobId, e);
			}
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("Sending email - job - {}", jobId);
				try {
					NotificationService.getInstance().sendEmail(tc, testResultLocation);
					//EmailAPIService.getInstance().sendSMS(tc, testResultLocation, true);
				} catch (IOException | TemplateException e1) {
					LOG.error("Error occured while sending email", e1);
				}
				LOG.info("Sending message - job - {}", jobId);		
			}
			
		});
		t.start();
	}

}
