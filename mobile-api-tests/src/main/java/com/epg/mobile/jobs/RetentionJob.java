package com.epg.mobile.jobs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.ReportingUtils;
import com.epg.mobile.utils.Utils;

public class RetentionJob implements Job {
	

	private static final Logger LOG = LoggerFactory.getLogger(RetentionJob.class);
	
	public void retentionJob (JobExecutionContext context) {
		try {
			if (StringUtils.isNotEmpty(PropertiesUtils.getProperty(AppConstants.OUT_DIR))
				&& StringUtils.isNotEmpty(PropertiesUtils.getProperty(AppConstants.RESULTS_RETENTION_DURATION))) {
				String outDir = PropertiesUtils.getProperty(AppConstants.OUT_DIR)
	        			+System.getProperty("file.separator")
	        			+AppConstants.RESULTS_FOLDER_NAME;
				Date startDate = getStartDate();
				Object o = context.getScheduler().getContext().get(AppConstants.RESULTS_RETENTION_LAST_RUN_TIME);
				if (o != null) {
					startDate = (Date) o;
				}
				Date timeLimit = Utils.getCurrentTimeWithReducedDuration(startDate, PropertiesUtils.getProperty(AppConstants.RESULTS_RETENTION_DURATION));
				Utils.deleteFoldersOlderThan(timeLimit, outDir);
				ReportingUtils.retrieveRecordsLaterThan(timeLimit);
				context.getScheduler().getContext().put(AppConstants.RESULTS_RETENTION_LAST_RUN_TIME, startDate);
			}	
		} catch (IOException | SchedulerException e) {
			LOG.error("Error occured while executing results retention job", e);
		}
	}

	private Date getStartDate() {
		Date startDate = new Date();
		String startDateStr = PropertiesUtils.getProperty(AppConstants.RESULTS_RETENTION_START_DATE);
		if (StringUtils.isNotEmpty(startDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			try {
				startDate = sdf.parse(startDateStr);
			} catch (ParseException e) {
				LOG.error("Couldn't parse retention job start date", e);
			}
		}
		return startDate;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.retentionJob(context);
	}

	

}
