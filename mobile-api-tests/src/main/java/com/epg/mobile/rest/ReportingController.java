package com.epg.mobile.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.dto.ResponseStatus;
import com.epg.mobile.service.FTLService;
import com.epg.mobile.utils.ReportingUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rs")
public class ReportingController {
	
	@Autowired
	private FTLService ftlService;
	
	@RequestMapping(value="/completed-by-tc", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public List<CompletedTest> getCompletedHistory(@RequestParam(value="id") String id) throws IOException {
		return ReportingUtils.getTestCaseHistory(id);
	}
	
	@RequestMapping(value="/alerts", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public List<CompletedTest> getAlerts() throws IOException {
		return ReportingUtils.getAlerts();
	}
	
	/**
	 *
	 * @param startDate dd-mm-yyyy 
	 * @param endDate dd-mm-yyyy
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	@RequestMapping(value="/daily_alerts", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public Map<String, List<CompletedTest>> getDailyAlerts(@RequestParam(value="start_date") String startDate,
				@RequestParam(value="end_date") String endDate) throws IOException, ParseException {
		return ReportingUtils.getDailyAlerts(startDate, endDate);
	}
	
	@RequestMapping(value="/test_details", method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus getTestDetails(@RequestParam(value="location") String location) throws IOException {
		return new ResponseStatus(ResponseStatus.SUCCESS, ReportingUtils.getXMLReportAsJSON(location));
	}
	
	
	@RequestMapping(value="/upload-template", method=RequestMethod.POST, 
			produces = "application/json", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseStatus uploadTemplate(@RequestParam(value="file") MultipartFile file,
			@RequestParam(value="name") String templateName) throws IOException {
		ftlService.uploadTemplateFile(file.getInputStream(), templateName);
		return new ResponseStatus(ResponseStatus.SUCCESS, "");
	}
	
	@RequestMapping(value="/save-template", method=RequestMethod.POST, 
			produces = "application/json")
	public ResponseStatus saveTemplate(@RequestParam(value="data") String data, 
				@RequestParam(value="name") String templateName) throws IOException {
		ftlService.saveTemplateString(data, templateName);
		return new ResponseStatus(ResponseStatus.SUCCESS, "");
	}
	
	@RequestMapping(value="/template", method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseStatus getTemplate(@RequestParam(value="name") String templateName) {
		return new ResponseStatus(ResponseStatus.SUCCESS, ftlService.getTemplate(templateName));
	}
	
	@RequestMapping(value="/download-template", method=RequestMethod.GET)
	public void downloadTemplateFile(@RequestParam(value="name") String templateName, 
				HttpServletResponse response) throws IOException {
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename=\""+templateName+".ftl\"");
		ftlService.downloadTemplate(templateName, response.getOutputStream());
	}
	
	@RequestMapping(value="/last-run", method=RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CompletedTest> getTestCasesLastRun() throws IOException {
		return ReportingUtils.getTestCasesLastRun();
	}
}
