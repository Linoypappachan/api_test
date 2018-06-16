package com.epg.mobile.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.Param;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class XMLUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(XMLUtils.class);
	
	public static String serialize(TestCases testCases) {
		String xml = null;
		if (testCases != null) {			
			XStream xstream = new XStream(new StaxDriver());
			xstream.processAnnotations(TestCases.class);
			xml = xstream.toXML(testCases);
		}
		return xml;
	}
	
	public static TestCases deSerialize(String xml) throws TestsXMLException {
		TestCases testCases = null;
		if (StringUtils.isNotEmpty(xml)) {
			try {
				XStream xstream = new XStream(new StaxDriver());
				XStream.setupDefaultSecurity(xstream);
				xstream.allowTypeHierarchy(TestCases.class);
				xstream.allowTypeHierarchy(TestCase.class);
				xstream.allowTypeHierarchy(Param.class);
				xstream.processAnnotations(TestCases.class);
				testCases = (TestCases) xstream.fromXML(xml);
			} catch(Throwable t) {
				LOG.error("Invalid XML", t);
				throw new TestsXMLException("Invalid XML", t);
			}			
		}	
		return testCases;
	}
	
	public static boolean isValidXML(String xml) {
		boolean isValid = false;
		try {			
			TestCases testCases = deSerialize(xml);
			isValid = testCases != null;
		} catch(TestsXMLException e) {
			LOG.error("Error occured while validating xml", e);
		}
		return isValid;
	}
	
	public static synchronized void updateTestCasesInXML(List<TestCase> _testCases) throws TestsXMLException, IOException {
		TestCases testCases = TestUtils.getHealthCheckTestCases();
		List<TestCase> testCasesList = testCases.getTestCases();
		for (TestCase tc : _testCases) {
			for(TestCase testCase : testCasesList) {
				if (testCase.getId().equalsIgnoreCase(tc.getId())) {
					// update fields -- keep adding here as per requirement
					testCase.setSchedule(tc.getSchedule());
					testCase.setActive(tc.getActive());
				}
			}
		}
		adjustCronAndReCreateXML(testCases);
	}
	
	public static void adjustCronAndReCreateXML(TestCases testCases) throws TestsXMLException, IOException {
		CronUtils.adjustCron(testCases);
		reCreateTestCasesXML(testCases);
	}
	
	public static void adjustCronAndReCreateXML() throws TestsXMLException, IOException {
		TestCases testCases = TestUtils.getHealthCheckTestCases();
		CronUtils.adjustCron(testCases);
		reCreateTestCasesXML(testCases);
	}
	
	public static void reCreateTestCasesXML (TestCases testCases) throws IOException {
		String xmlData = serialize(testCases);
		Utils.createFile(AppConstants.TEST_XMLS_FILE_NAME, 
					new ByteArrayInputStream(xmlData.getBytes()));
	}
}
