package com.epg.mobile.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.quartz.SchedulerException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epg.mobile.dto.ResponseStatus;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.testxml.dto.TestCases;
import com.epg.mobile.utils.TestUtils;

@RestController
@CrossOrigin(origins = "*", methods={ RequestMethod.GET, 
		RequestMethod.POST, RequestMethod.PUT }, allowedHeaders="*")
@RequestMapping("/rs")
public class TestsConfigurationController {
	@RequestMapping(value="/get_tests", method=RequestMethod.GET, produces = "application/json")
	public TestCases getTests() throws IOException, TestsXMLException {
		return TestUtils.getHealthCheckTestCases();
	}
		
	@RequestMapping(value="/upload-xml-file", method=RequestMethod.POST, 
			produces = "application/json", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseStatus uploadXML(@RequestParam(value="file") MultipartFile file,
			@RequestParam(value="fileName") String fileName) throws IOException, SchedulerException, TestsXMLException {
		TestUtils.uploadXMLFile(file.getInputStream(), fileName);
		return new ResponseStatus("SUCCESS", "SUCCESS");
	}
	
	@RequestMapping(value="/download-sample-file", method=RequestMethod.GET)
	public void downloadSampleFile(@RequestParam(value="file") String fileName,
				HttpServletResponse response) throws IOException {
		InputStream in = TestUtils.getSampleXML(fileName);
		response.setContentType(MediaType.APPLICATION_XML_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
		IOUtils.copy(in, response.getOutputStream());
	}
	
	@RequestMapping(value="/download-recent-file", method=RequestMethod.GET)
	public void downloadUploadedFile(@RequestParam(value="file") String fileName,
			HttpServletResponse response) throws IOException {
		InputStream in = TestUtils.getUploadedXML(fileName);
		response.setContentType(MediaType.APPLICATION_XML_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
		IOUtils.copy(in, response.getOutputStream());
	}
	
	@RequestMapping(value="/uploaded-file-time", method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public String getUploadedFileInfo(@RequestParam(value="file") String fileName) throws IOException {
		return TestUtils.getUploadedFileInfo(fileName);
	}
	
	
	@RequestMapping(value="/testcases-map", method=RequestMethod.GET)
	public  Map<String, TestCase> getTestCasesMap() throws TestsXMLException, IOException {
		return TestUtils.getHealthCheckTestCasesMap();
	}
	
}
