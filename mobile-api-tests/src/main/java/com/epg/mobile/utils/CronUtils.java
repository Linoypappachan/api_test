package com.epg.mobile.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;

public class CronUtils {
	
	public static void adjustCron(TestCases testCases) throws TestsXMLException, IOException {
		Set<String> cronSet = new HashSet<String>();
		for (TestCase tc : testCases.getTestCases()) {
			String nextCron = tc.getSchedule();
			while(cronSet.contains(nextCron)) {
				nextCron = getNextCron(nextCron);
			}
			cronSet.add(nextCron);
			tc.setSchedule(nextCron);
		}
		
	}

	private static String getNextCron(String cron) {
		String next = cron.toString();
		String[] parts = cron.split(" ");
		if (parts.length > 0) {
			String secondsCurrent = parts[0];
			String minutesCurrent = parts[1];
			String hoursCurrent = parts[2];
			String secondsNew = secondsCurrent;
			String minutesNew = minutesCurrent;
			String hoursNew = hoursCurrent;
			
			secondsNew = getNextSecond(secondsCurrent);
			if ("0".equals(secondsNew)) {
				minutesNew = getNextMinute(minutesCurrent);
				if ("0/0".equals(minutesNew)) {
					hoursNew = getNextHour(hoursCurrent);
				}
			}
			next = secondsNew+" "+minutesNew+" "+hoursNew +" "
					+ StringUtils.join(Arrays.copyOfRange(parts, 3, parts.length), " ");
		}
		return next;
	}



	private static String getNextHour(String hoursStr) {
		String hour = (!"*".equals(hoursStr)) ? hoursStr : "0";
		String offset_hour = "";
		if (hoursStr.contains("/")) {
			offset_hour = hoursStr.split("/")[0];
			hour = hoursStr.split("/")[1];
		}
		int h = Integer.parseInt(hour);
		h = (h < 23) ? (h+1) : 0;
		if (StringUtils.isEmpty(offset_hour)) {
			offset_hour = "0";
		}
		return offset_hour +"/"+Integer.toString(h);
	}



	private static String getNextMinute(String minutesStr) {
		String returnVal = minutesStr;
		String min = (!"*".equals(minutesStr)) ? minutesStr : "0";
		String offset_min = "";
		if (minutesStr.contains("/")) {
			offset_min = minutesStr.split("/")[0];
			min = minutesStr.split("/")[1];
		}
		int m = Integer.parseInt(min);
		m = (m < 59) ? (m+1) : 0;
		if (StringUtils.isEmpty(offset_min)) {
			returnVal = Integer.toString(m);
		} else {
			returnVal = offset_min + "/" + Integer.toString(m);
		}
		return returnVal;
	}



	private static String getNextSecond(String secondsStr) {
		int s = Integer.parseInt(secondsStr);
		s = (s < 59) ? (s+1) : 0;
		return Integer.toString(s);
	}
}
