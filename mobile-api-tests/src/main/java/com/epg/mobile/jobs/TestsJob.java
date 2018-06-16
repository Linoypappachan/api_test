package com.epg.mobile.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.collections.Lists;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.service.TestDataQueue;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.ReportingUtils;
import com.epg.mobile.utils.TestUtils;

public class TestsJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(TestsJob.class);


	public static void runTest(String testCaseId, TestCase tc, String out_dir) {
		try {
			TestDataQueue.getObject().enqueue(tc);

			LOG.info("Enqueued testcase object {}", tc.getId());

			File resultsDir = new File(PropertiesUtils.getProperty(AppConstants.OUT_DIR)
					+ AppConstants.SYSTEM_SEPARATOR
					+ AppConstants.RESULTS_DIR_NAME);
			if (!resultsDir.exists()) {
				resultsDir.mkdirs();
			}

			String testNgXML = TestsJob.class.getClassLoader()
					.getResource("testng-xmls/health-check.xml").getPath();
			TestNG testng = new TestNG();
			List<String> suites = Lists.newArrayList();
			suites.add(testNgXML);
			testng.setTestSuites(suites);
			testng.setOutputDirectory(out_dir);
			testng.run();

			CompletedTest completedInfo = ReportingUtils.getCompletedInfo(out_dir);
			completedInfo.setEndTimestamp((new Date()).getTime());
			completedInfo.setTestId(tc.getId());
			ReportingUtils.updateCompletedInfo(completedInfo);
		} catch (IOException e) {
			LOG.error("Error occured while saving completed info", e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String testCaseId = context.getJobDetail().getKey().getName();
		try {
			TestCase tc = (TestCase) context.getMergedJobDataMap().get(testCaseId);
			if (tc != null && (StringUtils.isEmpty(tc.getActive()) || "true".equalsIgnoreCase(tc.getActive()))) {
				LOG.info("Job {} is now started executing", testCaseId);
				String out_dir = TestUtils.getTestCaseResultLocation(testCaseId, new Date());
				context.getMergedJobDataMap().put(AppConstants.TEST_RESULT_LOCATION_KEY, out_dir);
				runTest(testCaseId, tc, out_dir);
			}
		} catch (IOException e) {
			LOG.error("Error occured while saving completed info", e);
		}
	}

}
