package com.epg.tests.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.jobs.TestsJob;
import com.epg.mobile.service.FTLService;
import com.epg.mobile.service.NotificationService;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.ReportingUtils;
import com.epg.mobile.utils.TestUtils;
import com.epg.mobile.utils.Utils;

import freemarker.template.TemplateException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tests-spring.xml" })
public class TestApp {
	private static final Logger LOG = LoggerFactory.getLogger(TestApp.class);
	
	@Autowired
	private FTLService ftlService;
	
	@Before
	public void init() throws IOException {
		Utils.createWorkspace();
		// load notification templates
		ftlService.loadAllTemplates();
		String location = "C:\\Users\\muqsith.abdul\\Downloads\\urls-few.xml";
		//String location = "/Users/muqsithirfan/Development/office/java_projects/api-tests/automated_tests_config/urls.xml";
		/*
		InputStream in = TestApp.class.getClassLoader()
				.getResourceAsStream("testng-sample-xmls/urls2.xml");
			*/
		InputStream in = new FileInputStream(new File(location));
		TestUtils.uploadXMLFile(in, "urls.xml");	
	}
	
	@Test
	public void simpleTest() throws TestsXMLException, IOException, SchedulerException, TemplateException {
		LOG.info("Simple test...");
		TestCases testCases = TestUtils.getHealthCheckTestCases();
		List<TestCase> testCasesList = testCases.getTestCases();
		boolean overallstatus = true;
		for (int i=0; i<testCasesList.size(); i++) {
			try {
				TestCase tc = testCasesList.get(i);
				String testCaseId = tc.getId();
				String out_dir = TestUtils.getTestCaseResultLocation(testCaseId, new Date());
				TestsJob.runTest(testCaseId, tc, out_dir);
				CompletedTest testCaseResult = ReportingUtils.getCompletedInfo(out_dir);
				List<CompletedTest> failedTestCases = ReportingUtils.getFailedTestCasesForSMS();
				NotificationService.getInstance().sendEmail(tc, out_dir);
				NotificationService.getInstance().sendSMS(true, failedTestCases);
				overallstatus = overallstatus && "PASS".equalsIgnoreCase(testCaseResult.getStatus());
			} catch (Throwable t) {
				
			}
		}	
		Assert.assertTrue(overallstatus);
	}
	
	//@Test
	public void testRefreshSMSGUID() {
		String oldGuid = PropertiesUtils
				.getProperty(AppConstants.SMS_SOURCE_GUID);
		NotificationService.refreshSMSGuid();
		String latestGuid = PropertiesUtils
				.getProperty(AppConstants.SMS_SOURCE_GUID);
		Assert.assertTrue(StringUtils.isNotEmpty(oldGuid)
				&& StringUtils.isNotEmpty(latestGuid)
				&& !oldGuid.equalsIgnoreCase(latestGuid));
	}
	
}
