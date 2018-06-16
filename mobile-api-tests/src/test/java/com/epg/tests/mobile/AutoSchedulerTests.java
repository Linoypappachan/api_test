package com.epg.tests.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCases;
import com.epg.mobile.utils.CronUtils;
import com.epg.mobile.utils.TestUtils;
import com.epg.mobile.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tests-spring.xml" })
public class AutoSchedulerTests {
	
	@Before
	public void init() throws IOException {
		Utils.createWorkspace();
		//String location = "C:\\Users\\muqsith.abdul\\Downloads\\urls-few.xml";
		String location = "/Users/muqsithirfan/Development/office/java_projects/api-tests/automated_tests_config/urls.xml";
		InputStream in = new FileInputStream(new File(location));
		TestUtils.uploadXMLFile(in, "urls.xml");	
	}
	
	@Test
	public void test() throws TestsXMLException, IOException {
		TestCases testCases = TestUtils.getHealthCheckTestCases();
		CronUtils.adjustCron(testCases);
	}

}
