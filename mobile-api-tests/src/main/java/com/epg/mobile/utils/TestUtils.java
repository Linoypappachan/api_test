package com.epg.mobile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;

public class TestUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);
	
	public static String uploadXMLFile(InputStream in, String fileName) throws IOException {
		String responseText = "";
		ByteArrayOutputStream baos = Utils.getCopyOutputStream(in); 
		StringWriter sw = new StringWriter();
		IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), sw, "UTF-8");
		String xmlData = sw.toString();
		boolean isValid = XMLUtils.isValidXML(xmlData);
		if (isValid) {			
			Utils.createFile(fileName, new ByteArrayInputStream(baos.toByteArray()));
			LOG.info("Test cases XML file successfully created: {}", fileName);
		} else {
			responseText = "In-valid XML";
		}
		return responseText;
	}
	
	public static InputStream getSampleXML(String fileName) throws IOException {
		String filePath = TestUtils.class.getClassLoader()
				.getResource("sample-data-xmls/"+fileName).getPath();
		File xmlFile = new File(filePath);
		InputStream in = new FileInputStream(xmlFile);
		return in;
	}
	
	public static InputStream getUploadedXML(String fileName) throws IOException {
		File file = Utils.getFile(fileName);
		InputStream in = new ByteArrayInputStream((new String("")).getBytes());
		if (file.exists()) {			
			in = new FileInputStream(file);
		}
		return in;
	}
	
	public static String getUploadedFileInfo(String fileName) throws IOException {
		String fileInfo = "";
		File file = Utils.getFile(fileName);
		if (file != null && file.exists()) {
			fileInfo = ""+file.lastModified();
		}
		return fileInfo;
	}
	
	public static TestCases getHealthCheckTestCases() throws TestsXMLException, IOException {
		TestCases testCases = new TestCases();
		File file = Utils.getFile(AppConstants.TEST_XMLS_FILE_NAME);
		if (file.exists()) {
			testCases = XMLUtils.deSerialize(FileUtils.readFileToString(file, "UTF-8"));
		}
		return testCases;
	}
	
	public static void cleanupOldResults() throws IOException {
		File completed_info_file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		if (completed_info_file.exists()) {
			FileUtils.deleteQuietly(completed_info_file);
		}
		File xmlsFile = Utils.getFile(AppConstants.TEST_XMLS_FILE_NAME);
		if (xmlsFile.exists()) {
			FileUtils.deleteQuietly(xmlsFile);
		}
		String outDir = PropertiesUtils.getProperty(AppConstants.OUT_DIR)
    			+System.getProperty("file.separator")
    			+AppConstants.RESULTS_FOLDER_NAME;
		File outDirFolder = new File(outDir);
		if (outDirFolder.exists() && outDirFolder.isDirectory()) {			
			FileUtils.deleteDirectory(outDirFolder);
		}
	}
	
	public static String getTestCaseResultLocation(String testCaseId, Date date) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
		return PropertiesUtils.getProperty(AppConstants.OUT_DIR)
				+ AppConstants.SYSTEM_SEPARATOR
				+ AppConstants.RESULTS_DIR_NAME
				+ AppConstants.SYSTEM_SEPARATOR
				+ testCaseId
				+ "_"+sdf.format(date);
	}
	
	public static Map<String, TestCase> getHealthCheckTestCasesMap() throws TestsXMLException, IOException {
		Map<String, TestCase> map = new HashMap<String, TestCase>();
		TestCases testCases = new TestCases();
		File file = Utils.getFile(AppConstants.TEST_XMLS_FILE_NAME);
		if (file.exists()) {
			testCases = XMLUtils.deSerialize(FileUtils.readFileToString(file, "UTF-8"));
		}
		if (testCases != null) {
			List<TestCase> testCaseList = testCases.getTestCases();
			if (testCaseList != null && !testCaseList.isEmpty()) {
				for (TestCase tc : testCaseList) {
					map.put(tc.getId(), tc);
				}
			}
		}
		return map;
	}
}
