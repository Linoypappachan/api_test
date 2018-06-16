package com.epg.mobile.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.dto.Email;
import com.epg.mobile.dto.FailedTestDetails;
import com.epg.mobile.dto.SMSMessage;
import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.ReportingUtils;
import com.epg.mobile.utils.TestUtils;
import com.epg.mobile.utils.Utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class FTLService {
	private static final Logger LOG = LoggerFactory.getLogger(FTLService.class);
	
	private static FTLService _this;
	
	private Configuration cfg;
	
	private Map<String, String> templates = new HashMap<String, String>();
	
	@Autowired
	private ApplicationContext appContext;
	
	@PostConstruct
	public void init() {
		this.cfg = new Configuration(Configuration.VERSION_2_3_25);
		_this = this.appContext.getBean(FTLService.class);
	}
	
	public void loadAllTemplates() throws IOException {
		// load templates
		loadTemplate(AppConstants.EMAIL_BODY_TEMPLATE_NAME);
		loadTemplate(AppConstants.EMAIL_SUBJECT_TEMPLATE_NAME);
		loadTemplate(AppConstants.SMS_TEMPLATE_NAME);
	}
	
	private void loadTemplate(String template) throws IOException {
		File templateFile = Utils.getFile(template+".ftl");
		if (templateFile.exists()) {
			try {
				this.loadTemplate(new FileInputStream(templateFile), template);
			} catch (IOException e) {
				LOG.error("Failed to read {} template from out_dir", template, e);
			}
		}
		
		if (StringUtils.isEmpty(templates.get(template))) {
			InputStream in = this.getClass()
						.getClassLoader()
						.getResourceAsStream(template+".ftl");
			this.uploadTemplateFile(in, template);
		}
	}
	
	private void loadTemplate(InputStream in, String template) {
		try {
			String templateStr = IOUtils.toString(in, Consts.UTF_8);
			this.templates.put(template, templateStr);
		} catch (IOException e) {
			LOG.error("Failed to read {} from input stream", template, e);
		}
	}
	
	public void uploadTemplateFile(InputStream in, String template) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(in, baos);
		Utils.createFile(template+".ftl", new ByteArrayInputStream(baos.toByteArray()));
		this.loadTemplate(new ByteArrayInputStream(baos.toByteArray()), template);
	}
	
	public void saveTemplateString(String templateData, String template) throws IOException {
		InputStream in = new ByteArrayInputStream(templateData.getBytes());
		uploadTemplateFile(in, template);
	}
	
	public String getTemplate(String template) {
		String templateStr = this.templates.get(template);
		if (StringUtils.isEmpty(templateStr)) {
			templateStr = "";
		}
		return templateStr;
	}
	
	public Email getEmail(TestCase tc, String resultLocation) throws IOException, TemplateException {
		CompletedTest testCaseResult = ReportingUtils.getCompletedInfo(resultLocation);
		FailedTestDetails failedDetails = new FailedTestDetails();
		if (AppConstants.FAILED.equalsIgnoreCase(testCaseResult.getStatus())) {
			failedDetails = ReportingUtils.getFailedTestDetails(tc.getId(), resultLocation);
		}
		testCaseResult.setResultLocation(Utils.encodeURIComponent(resultLocation));
		Email email = new Email();
		email.setTo(PropertiesUtils.getProperty(AppConstants.EMAIL_TO_NAME));
		// adding CC also to the TO list
		String ccList = PropertiesUtils.getProperty(AppConstants.EMAIL_CC_NAME);
		if (StringUtils.isNotEmpty(ccList)) {			
			if (StringUtils.isNotEmpty(email.getTo())) {
				email.setTo(email.getTo()+","+ccList);
			} else {
				email.setTo(ccList);				
			}
		}
		email.setSubject(Utils.getUtf8String(getEmailSubject(tc, testCaseResult).trim()));
		email.setBody(Utils.getUtf8String(getEmailBody(tc, testCaseResult, failedDetails).trim()));
		email.setFrom(PropertiesUtils.getProperty(AppConstants.EMAIL_FROM_USER_NAME));
		return email;
	}

	private String getEmailBody(TestCase tc, CompletedTest testCaseResult, FailedTestDetails failedDetails) throws IOException, TemplateException {
		Template t = new Template(AppConstants.EMAIL_BODY_TEMPLATE_NAME,
					new StringReader(
							this.getTemplate(
									AppConstants.EMAIL_BODY_TEMPLATE_NAME))
						, cfg);
		Writer out = new StringWriter();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("tc", tc);
		model.put("testCaseResult", testCaseResult);
		model.put("failedDetails", failedDetails);
		t.process(model, out);
		return out.toString();
	}

	private String getEmailSubject(TestCase tc, CompletedTest testCaseResult) throws IOException, TemplateException {
		Template t = new Template(AppConstants.EMAIL_SUBJECT_TEMPLATE_NAME,
				new StringReader(this.getTemplate(
						AppConstants.EMAIL_SUBJECT_TEMPLATE_NAME)), 
					cfg);
		Writer out = new StringWriter();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(AppConstants.TEST_CASE_OBJECT_NAME, tc);
		model.put(AppConstants.TEST_CASE_RESULT_OBJECT_NAME, testCaseResult);
		t.process(model, out);
		return out.toString();
	}
	
	public SMSMessage getSMS(List<CompletedTest> completedJobs) throws JSONException, IOException, TemplateException, TestsXMLException {
		SMSMessage sms = new SMSMessage();
		sms.setMessage(getSMSMessage(completedJobs));
		sms.setFrom(PropertiesUtils.getProperty("sms_from"));
		sms.setNumbers(PropertiesUtils.getProperty(AppConstants.SMS_NUMBERS_NAME));
		return sms;
	}
	
	private String getSMSMessage(List<CompletedTest> completedJobs) throws IOException, TemplateException, TestsXMLException {
		Map<String, TestCase> testCasesMap = TestUtils.getHealthCheckTestCasesMap();
		Template t = new Template(AppConstants.SMS_TEMPLATE_NAME,
				new StringReader(this.getTemplate(
						AppConstants.SMS_TEMPLATE_NAME)), 
					cfg);
		Writer out = new StringWriter();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("completed_jobs_count", completedJobs.size());
		String tc_names = "";
		if (completedJobs.size() < 3) {
			for (int i=0; i<completedJobs.size(); i++) {
				TestCase testCaseObject = testCasesMap.get(completedJobs.get(i).getTestId());
				if (testCaseObject != null) {
					tc_names += testCaseObject.getName();
					if (i<completedJobs.size()-1) {			
						tc_names += ", ";
					}
				}				
			}
		}
		model.put("tc_names", tc_names);
		model.put("time", (new Date()).toString());
		t.process(model, out);
		String message = out.toString();
		LOG.info("SMS: {}", message);
		return message;
	}

	public static FTLService getInstance() {
		return _this;
	}

	public void downloadTemplate(String template, ServletOutputStream outputStream) throws FileNotFoundException, IOException {
		File file = Utils.getFile(template+".ftl");
		IOUtils.copy(new FileInputStream(file), outputStream);
	}
}
