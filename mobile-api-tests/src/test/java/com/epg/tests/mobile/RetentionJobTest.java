package com.epg.tests.mobile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epg.mobile.jobs.RetentionJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tests-spring.xml" })
public class RetentionJobTest {

	private static final Logger LOG = LoggerFactory.getLogger(RetentionJobTest.class);
	
	private JobExecutionContext jobExecutionCtx;
	
	@Before
	public void init() throws SchedulerException {
		jobExecutionCtx = Mockito.mock(JobExecutionContext.class);
		Mockito.when(jobExecutionCtx.getScheduler()).thenReturn(StdSchedulerFactory.getDefaultScheduler());
	}
	
	@Test
	public void testRetentionJob() throws JobExecutionException {
		RetentionJob retentionJob = new RetentionJob();
		retentionJob.execute(jobExecutionCtx);
		LOG.info("Successfully executed retention job");
		Assert.assertTrue(true);
	}
}
