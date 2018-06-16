package com.epg.mobile.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.epg.mobile.dto.ResponseStatus;
import com.epg.mobile.dto.ScheduledJob;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.service.SchedulerService;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.XMLUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rs")
public class SchedulerController {

	@Autowired
	private SchedulerService schedulerService;
	
	@RequestMapping(value="/reschedule_all_tests", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus scheduleTestsJob() throws SchedulerException, TestsXMLException, IOException {
		this.schedulerService.reScheduleAllTests();
		return new ResponseStatus("SUCCESS", "");
	}
	
	@RequestMapping(value="/get_scheduled", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public List<ScheduledJob> getScheduledJobs() throws SchedulerException, IOException, TestsXMLException {
		return this.schedulerService.getAllScheduledJobs();
	}

	@RequestMapping(value="/update_tests", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
				produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus reScheduleAllTestsJob(@RequestBody List<TestCase> testCases) throws TestsXMLException, IOException, SchedulerException {
		XMLUtils.updateTestCasesInXML(testCases);
		return new ResponseStatus("SUCCESS", "");
	}
	
	@RequestMapping(value="/reschedule_test", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus reScheduleTestsJob(@RequestBody TestCase testCase) throws TestsXMLException, IOException, SchedulerException {
		List<TestCase> list = new ArrayList<TestCase>();
		list.add(testCase);
		XMLUtils.updateTestCasesInXML(list);
		this.schedulerService.runNowAndScheduleTest(testCase);
		return new ResponseStatus("SUCCESS", "");
	}
	
	@RequestMapping(value="/run_now", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus runNow(@RequestBody TestCase testCase) throws TestsXMLException, IOException, SchedulerException {
		this.schedulerService.runNow(testCase);
		return new ResponseStatus("SUCCESS", "");
	}
	
	@RequestMapping(value="/currently_executingjobs", 
			method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Boolean> getCurrentlyExecutingJobs() throws SchedulerException {
		return this.schedulerService.getCurrentlyRunningJobs();
	}
}
