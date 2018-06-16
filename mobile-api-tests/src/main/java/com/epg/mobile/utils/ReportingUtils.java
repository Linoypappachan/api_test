package com.epg.mobile.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.dto.FailedTestDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportingUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportingUtils.class);
	
	private static Map<String, CompletedTest> oldAlerts = new HashMap<String, CompletedTest>();
	
	public static String getXMLReportAsJSON(String out_dir) throws IOException {
		String json = "{}";
		String filePath = out_dir+System.getProperty("file.separator")+"testng-results.xml";
		File xmlFile = new File(filePath);
		if (xmlFile.exists()) {
			InputStream in = new FileInputStream(xmlFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = ""; 
			StringBuilder xml_string = new StringBuilder("");
			while ((line = reader.readLine()) != null) {
				xml_string.append(line);
			}
			reader.close();
			JSONObject jsonObject = XML.toJSONObject(xml_string.toString());
			json = jsonObject.toString();
		} else {
			LOG.error("Result file not found at give file path {1}", filePath);
		}
		return json;
	}
	
	public static CompletedTest getCompletedTestObject(String line) throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObj = new JSONObject(line);
		ObjectMapper objectMapper = new ObjectMapper();
		CompletedTest completedJob = objectMapper.readValue(jsonObj.toString(), CompletedTest.class);
		return completedJob;
	}
	
	public static synchronized void updateCompletedInfo(CompletedTest completedInfo) throws IOException {
		File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		if (!file.exists()) {
			file.createNewFile();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(completedInfo);
		Utils.appendDataToFile(file, json+"\n");
	}

	public static List<CompletedTest> getAlerts() throws IOException {
		Map<String, CompletedTest> alertsMap = getAlertsMap();
		List<CompletedTest> alerts = new ArrayList<CompletedTest>();
		for (String testId : alertsMap.keySet()) {
			alerts.add(alertsMap.get(testId));
		}
		return alerts;
	}
	
	public static Map<String, CompletedTest> getAlertsMap() throws IOException {
		File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		Map<String, CompletedTest> alertsMap = new HashMap<String, CompletedTest>();
		LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				CompletedTest job = getCompletedTestObject(line);
				CompletedTest _job = alertsMap.get(job.getTestId());
				if (_job == null || (job.getEndTimestamp() > _job.getEndTimestamp())) {
					alertsMap.put(job.getTestId(), job);
				}
			}
		} finally {
			LineIterator.closeQuietly(lineIterator);
		}
				
		Map<String, CompletedTest> copy = new HashMap<String, CompletedTest>(alertsMap);
		
		for (String testId : copy.keySet()) {
			CompletedTest _job_now = alertsMap.get(testId+"_now");
			CompletedTest _job = alertsMap.get(testId);
			if (_job_now != null) {
				if (_job.getEndTimestamp() > _job_now.getEndTimestamp()) {
					alertsMap.remove(testId+"_now");
				} else {
					alertsMap.remove(testId);
				}
			}
		}
		
		copy = new HashMap<String, CompletedTest>(alertsMap);
		for (String testId : copy.keySet()) {
			if (!AppConstants.FAILED.equalsIgnoreCase(copy.get(testId).getStatus())) {
				alertsMap.remove(testId);
			}
		}
		
		return alertsMap;
	}

	public static Map<String, List<CompletedTest>> getDailyAlerts(String startDateStr, String endDateStr) throws IOException, ParseException {
		Map<String, List<CompletedTest>> map = new LinkedHashMap<String, List<CompletedTest>>();
		if (StringUtils.isNotEmpty(startDateStr) && StringUtils.isNotEmpty(endDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(endDateStr);
			endDate = new Date(endDate.getTime() + (24 * 60 * 60 * 1000) -1);
			File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
			LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
			try {
				while (lineIterator.hasNext()) {
					String line = lineIterator.next();
					CompletedTest j = getCompletedTestObject(line);
					if (!j.getTestId().contains("_now") && 
							AppConstants.FAILED.equalsIgnoreCase(j.getStatus())
							&& startDate.getTime() < j.getEndTimestamp()
							&& endDate.getTime() > j.getEndTimestamp()) {
						Date _dt = new Date(j.getEndTimestamp());
						String _dtStr = sdf.format(_dt);
						List<CompletedTest> dayJobs = map.get(_dtStr);
						if (dayJobs == null) {
							dayJobs = new ArrayList<CompletedTest>();
							map.put(_dtStr, dayJobs);
						}
						dayJobs.add(j);			
					}
				}
			} finally {
				LineIterator.closeQuietly(lineIterator);
			}
			for (String day : map.keySet()) {
				List<CompletedTest> dailyJobs = map.get(day);
				Collections.sort(dailyJobs);
			}
		}
		return map;
	}
	
	public static CompletedTest getCompletedInfo(String resultLocation) throws IOException {
		CompletedTest completedInfo = new CompletedTest();
		String jsonString = getXMLReportAsJSON(resultLocation);
		if (StringUtils.isNotEmpty(jsonString)) {
			completedInfo.setResultLocation(resultLocation);
			JSONObject jsonObject = new JSONObject(jsonString);
			try {
				JSONObject testMethodObject = null;
				try { 
					testMethodObject = jsonObject.getJSONObject("testng-results")
							.getJSONObject("suite")
							.getJSONObject("test")
							.getJSONObject("class")
							.getJSONObject("test-method")
							;
				} catch (Throwable t1) {}
	
				if (testMethodObject == null) {
					try {
						JSONArray jsonMethodArray = jsonObject.getJSONObject("testng-results")
								.getJSONObject("suite")
								.getJSONObject("test")
								.getJSONObject("class")
								.getJSONArray("test-method")
								;
						for (int i=0; i<jsonMethodArray.length(); i++) {
							JSONObject _json = jsonMethodArray.getJSONObject(i);
							if ("test".equalsIgnoreCase(_json.getString("name"))) {
								testMethodObject = _json;
							}
						}
					} catch(Throwable t2) {}        	
				}
				completedInfo.setStatus(testMethodObject.getString("status"));
				completedInfo.setEndTime(testMethodObject.getString("finished-at"));
				completedInfo.setStartTime(testMethodObject.getString("started-at"));
				completedInfo.setTestDuration(testMethodObject.get("duration-ms").toString());
			} catch (Throwable t) {
				LOG.error("Failed to extract status", t);
			}
		}
		return completedInfo;
	}
	
	public static FailedTestDetails getFailedTestDetails(String testCaseId, String resultLocation) throws IOException {
		FailedTestDetails failedTestDetails = new FailedTestDetails();
		failedTestDetails.setTestId(testCaseId);
		String jsonString = getXMLReportAsJSON(resultLocation);
		try {
			JSONObject j = (new JSONObject(jsonString)).getJSONObject("testng-results").getJSONObject("suite")
					.getJSONObject("test").getJSONObject("class").getJSONObject("test-method")
					.getJSONObject("exception");
			failedTestDetails.setFailedException(j.getString("full-stacktrace"));
			failedTestDetails.setFailedMessage(j.getString("message"));
		} catch(Throwable t) {
			LOG.error("Error occured while parsing json", t);
		}
		return failedTestDetails;
	}

	public static Map<String, CompletedTest> getTestCasesLastRun() throws IOException {
		Map<String, CompletedTest> alertsMap = new HashMap<String, CompletedTest>();
		File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				CompletedTest job = getCompletedTestObject(line);
				if (job.getTestId().contains("_now")) {
					job.setTestId(job.getTestId().replace("_now", ""));
				}
				CompletedTest _job = alertsMap.get(job.getTestId());
				if (_job == null || (job.getEndTimestamp() > _job.getEndTimestamp())) {				
					alertsMap.put(job.getTestId(), job);
				}
			}
		} finally {
			LineIterator.closeQuietly(lineIterator);
		}
		return alertsMap;
	}

	public static List<CompletedTest> getTestCaseHistory(String id) throws IOException {
		List<CompletedTest> history = new ArrayList<CompletedTest>();
		File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				CompletedTest job = getCompletedTestObject(line);
				if (id.equalsIgnoreCase(job.getTestId())
						|| (id+"_now").equalsIgnoreCase(job.getTestId())) {
					history.add(job);
				}
			}
		} finally {
			LineIterator.closeQuietly(lineIterator);
		}
		
		Collections.sort(history);
		
		return history;
	}
	
	
	public static synchronized List<CompletedTest> getFailedTestCasesForSMS() throws IOException {
		Map<String, CompletedTest> currentAlerts = ReportingUtils.getAlertsMap();
		
		Map<String, CompletedTest> alertsToBeSent = new HashMap<String, CompletedTest>();
		
		List<CompletedTest> result = new ArrayList<CompletedTest>();
		
		if (currentAlerts.isEmpty()) {
			oldAlerts.clear();
		} else {
			
			Map<String, CompletedTest> oldAlertsCopy = new HashMap<String, CompletedTest>(oldAlerts);
			
			// clear passed alerts and remove previously sent alerts else add new one
			for (String key : oldAlertsCopy.keySet()) {
				CompletedTest current = currentAlerts.get(key);
				CompletedTest previous = oldAlerts.get(key);
				if (current == null) {
					// test case has passed
					oldAlerts.remove(key);
				} else if (current.getEndTimestamp() > previous.getEndTimestamp()) {
					oldAlerts.put(key, current);
					alertsToBeSent.put(key, current);
				}
			}
			
			// add newly failed alerts
			for (String key: currentAlerts.keySet()) {
				if (oldAlerts.get(key) == null) {
					oldAlerts.put(key, currentAlerts.get(key));
					alertsToBeSent.put(key, currentAlerts.get(key));
				}
			}
		}	
		
		for (String key : alertsToBeSent.keySet()){
			result.add(alertsToBeSent.get(key));
		}
		
		return result;
	}

	public static void retrieveRecordsLaterThan(Date timeLimit) throws JsonParseException, JsonMappingException, IOException {
		List<CompletedTest> latest_records = new ArrayList<CompletedTest>();
		File file = Utils.getFile(AppConstants.COMPLETED_JOB_INFO_FILE_NAME);
		LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				CompletedTest job = getCompletedTestObject(line);
				if (timeLimit.getTime() > job.getEndTimestamp()) {					
					latest_records.add(job);
				}
			}
		} finally {
			LineIterator.closeQuietly(lineIterator);
		}
		for (CompletedTest c : latest_records) {
			updateCompletedInfo(c);;
		}
	}
}
