package com.epg.mobile.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.epg.mobile.dto.ScheduledJob;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.jobs.RetentionJob;
import com.epg.mobile.jobs.SMSJob;
import com.epg.mobile.jobs.TestsJob;
import com.epg.mobile.jobs.TestsJobListener;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.TestUtils;
import com.epg.mobile.utils.XMLUtils;

@Service
public class SchedulerService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
	
	private static final String TESTS_GROUP = "tests-group";
	private static final String SYSTEM_GROUP = "system-group";
	
	@Autowired
	private ApplicationContext appContext;
	
	private static SchedulerService _this;

	private Scheduler scheduler;
	
	@PostConstruct
	public void init() throws SchedulerException, IOException {
		_this = this.appContext.getBean(SchedulerService.class);
		if (this.scheduler != null) {
			this.scheduler.shutdown();
			this.scheduler = null;
		}
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.getListenerManager().addJobListener(
	    		new TestsJobListener(), GroupMatcher.jobGroupEquals(TESTS_GROUP));
		this.scheduler.start();
		this.scheduleSystemJob();
		this.reScheduleAllTests();
	}
	
	public void reStartSystemJob() throws SchedulerException, IOException {
		this.unScheduleSystemJob();
		this.scheduleSystemJob();
	}
	
	public void scheduleSystemJob() throws IOException, SchedulerException {
		JobDetail retentionJob = newJob(RetentionJob.class)
			      .withIdentity(AppConstants.RESULTS_RETENTION_JOB_ID, SYSTEM_GROUP)
			      .build();
		Trigger retentionJobTrigger = newTrigger()
			      .withIdentity(AppConstants.RESULTS_RETENTION_JOB_ID, SYSTEM_GROUP)
			      .withSchedule(cronSchedule(PropertiesUtils.getProperty(AppConstants.RESULTS_RETENTION_CRON)))
			      .forJob(AppConstants.RESULTS_RETENTION_JOB_ID, SYSTEM_GROUP)
			      .build();
		this.scheduler.addJob(retentionJob, true, true);
		Date retentionJobScheduledTime = this.scheduler.scheduleJob(retentionJobTrigger);
		LOG.info("Scheduling system job - {} @ {}", AppConstants.RESULTS_RETENTION_JOB_ID, retentionJobScheduledTime.toString());
		
		JobDetail smsJob = newJob(SMSJob.class)
			      .withIdentity(AppConstants.SMS_JOB_ID, SYSTEM_GROUP)
			      .build();
		Trigger smsJobTrigger = newTrigger()
			      .withIdentity(AppConstants.SMS_JOB_ID, SYSTEM_GROUP)
			      .withSchedule(cronSchedule(PropertiesUtils.getProperty(AppConstants.SMS_JOB_CRON)))
			      .forJob(AppConstants.SMS_JOB_ID, SYSTEM_GROUP)
			      .build();
		this.scheduler.addJob(smsJob, true, true);
		Date smsJobScheduledTime = this.scheduler.scheduleJob(smsJobTrigger);
		LOG.info("Scheduling system job - {} @ {}", AppConstants.RESULTS_RETENTION_JOB_ID, smsJobScheduledTime.toString());
	}
	
	@SuppressWarnings("unchecked")
	public void unScheduleSystemJob() throws SchedulerException {
		Set<JobKey> jobKeys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SYSTEM_GROUP));
		for (JobKey jobKey : jobKeys) {
			Object triggersObject = scheduler.getTriggersOfJob(jobKey);
			if (triggersObject != null) {
				List<Trigger> triggers = (List<Trigger>) triggersObject;
				if (!triggers.isEmpty()) {
					for (Trigger trigger : triggers) {
						this.scheduler.unscheduleJob(trigger.getKey());
						LOG.info("Unscheduling system job - {} @ {}", jobKey.getName(), trigger.getKey());
					}
				}
			}
		}
	}
	
	public void scheduleTestCase(TestCase tc) throws SchedulerException {
		if (StringUtils.isNotEmpty(tc.getSchedule())) {
			JobDataMap jobData = new JobDataMap();
			jobData.put(tc.getId(), tc);
			JobDetail job = newJob(TestsJob.class)
				      .withIdentity(tc.getId(), TESTS_GROUP)
				      .usingJobData(jobData)
				      .build();
			Trigger trigger = newTrigger()
				      .withIdentity(tc.getId(), TESTS_GROUP)
				      .usingJobData(jobData)
				      .withSchedule(cronSchedule(tc.getSchedule()))
				      .forJob(tc.getId(), TESTS_GROUP)
				      .build();
			this.scheduler.addJob(job, true, true);
			Date scheduledTime = this.scheduler.scheduleJob(trigger);
			LOG.info("Scheduling job - {} @ {}", tc.getId(), scheduledTime.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void unScheduleTestCase(String jobId) throws SchedulerException {
		Set<JobKey> jobKeys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TESTS_GROUP));
		for (JobKey jobKey : jobKeys) {
			if (jobId.equalsIgnoreCase(jobKey.getName())) {
				Object triggersObject = scheduler.getTriggersOfJob(jobKey);
				if (triggersObject != null) {
					List<Trigger> triggers = (List<Trigger>) triggersObject;
					if (!triggers.isEmpty()) {
						for (Trigger trigger : triggers) {
							this.scheduler.unscheduleJob(trigger.getKey());
							LOG.info("Unscheduling job - {} @ {}", jobKey.getName(), trigger.getKey());
						}
					}
				}
			}			
		}
	}

	@SuppressWarnings("unchecked")
	public List<ScheduledJob> getAllScheduledJobs() throws SchedulerException, IOException, TestsXMLException {
		List<ScheduledJob> scheduledJobs = new ArrayList<ScheduledJob>();
		TestCases testCases = TestUtils.getHealthCheckTestCases();
		if (testCases != null && testCases.getTestCases() != null 
				&& !testCases.getTestCases().isEmpty()) {
			for (TestCase tc : testCases.getTestCases()) {
				ScheduledJob scheduledJob = new ScheduledJob();
				scheduledJob.setTestCaseId(tc.getId());
				scheduledJob.setTestCase(tc);
				scheduledJobs.add(scheduledJob);
				Set<JobKey> jobKeys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TESTS_GROUP));
				for (JobKey jobKey : jobKeys) {
					if (tc.getId().equalsIgnoreCase(jobKey.getName())) {
						Object triggersObject = scheduler.getTriggersOfJob(jobKey);
						if (triggersObject != null) {
							List<Trigger> triggers = (List<Trigger>) triggersObject;
							if (!triggers.isEmpty()) {
								for (Trigger trigger : triggers) {
									scheduledJob.setNextRun(trigger.getNextFireTime().getTime());
								}
							}
						}
						break;
					}					
				}
			}
		}
		return scheduledJobs;
	}

	public void reScheduleAllTests() {
		try {
			XMLUtils.adjustCronAndReCreateXML();
			TestCases testCases = TestUtils.getHealthCheckTestCases();		
			List<TestCase> testCasesList = testCases.getTestCases();
			for (TestCase tc : testCasesList) {
				runNowAndScheduleTest(tc);
			}
		} catch(TestsXMLException | IOException | SchedulerException e) {
			LOG.error("Error occured while adjusting xml", e);
		}
		
	}
	
	public void runNowMultiple(List<TestCase> testCases) throws SchedulerException {
		if (testCases != null && !testCases.isEmpty()) {
			Long timeStamp = (new Date()).getTime();
			for (TestCase tc : testCases) {
				SimpleDateFormat sdf = new SimpleDateFormat("ss-mm-HH-dd-MM");
				timeStamp += 1000L;
				String _now = sdf.format(new Date(timeStamp));
				LOG.info("Test case - {} scheduled @ {} (Run now) ", tc.getId(), _now);
				String[] _now_arr = _now.split("-");
				String cron = _now_arr[0]+" "+_now_arr[1]+" "+_now_arr[2]+" "+_now_arr[3]+" "+_now_arr[4]+" ?";
				this.unScheduleTestCase(tc.getId());
				tc.setSchedule(cron);
				this.scheduleTestCase(tc);
			}
		}
	}
	
	public void runNowAndScheduleTest(TestCase testCase) throws SchedulerException {
		String originalCron = testCase.getSchedule();
		String currentCron = runNow(testCase);
		String[] originalCronParts = originalCron.split(" ");
		String[] currentCronParts = currentCron.split(" ");
		String[] latestCronParts = new String[originalCronParts.length];
		for (int i = 0; i<originalCronParts.length; i++) {
			String originalCronPart = originalCronParts[i];
			if (originalCronPart.contains("*") || originalCronPart.contains("/")) {
				latestCronParts[i] = originalCronPart;
			} else {
				latestCronParts[i] = currentCronParts[i];
			}
		}
		String latestCron = StringUtils.join(latestCronParts, " ");
		testCase.setSchedule(latestCron);
		unScheduleTestCase(testCase.getId());
		scheduleTestCase(testCase);
	}
	
	public String runNow(TestCase testCase) throws SchedulerException {
		SimpleDateFormat sdf = new SimpleDateFormat("ss-mm-HH-dd-MM");
		String _now = sdf.format(new Date());
		LOG.info("Test case - {} scheduled @ {} (Run now) ", testCase.getId(), _now);
		String[] _now_arr = _now.split("-");
		String cron = _now_arr[0]+" "+_now_arr[1]+" "+_now_arr[2]+" "+_now_arr[3]+" "+_now_arr[4]+" ?";
		this.unScheduleTestCase(testCase.getId());
		testCase.setSchedule(cron);
		this.scheduleTestCase(testCase);
		return cron;
	}
	
	public Map<String, Boolean> getCurrentlyRunningJobs() throws SchedulerException {
		Map<String, Boolean> runningTests = new HashMap<String, Boolean>();
		List<JobExecutionContext> executingJobs = this.scheduler.getCurrentlyExecutingJobs();
		if (executingJobs != null && !executingJobs.isEmpty()) {
			for (JobExecutionContext je : executingJobs) {
				if (je != null && je.getJobDetail() != null && 
						je.getJobDetail().getKey() != null &&
						TESTS_GROUP.equalsIgnoreCase(je.getJobDetail().getKey().getGroup()) &&
						StringUtils.isNotEmpty(je.getJobDetail().getKey().getName())) {
					String testCaseId = je.getJobDetail().getKey().getName();
					if (StringUtils.isNotEmpty(testCaseId)) {
						if (testCaseId.endsWith("_now")){
							testCaseId = testCaseId.replace("_now", "");
						}
						runningTests.put(testCaseId, true);
					}
				}					
			}
		}
		return runningTests;
	}
	

	public static SchedulerService getInstance() {
		return _this;
	}
	
	public Scheduler getScheduler() {
		return this.scheduler;
	}

}
