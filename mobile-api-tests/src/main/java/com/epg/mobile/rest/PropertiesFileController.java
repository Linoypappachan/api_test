package com.epg.mobile.rest;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epg.mobile.dto.ResponseStatus;
import com.epg.mobile.service.SchedulerService;
import com.epg.mobile.utils.PropertiesUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rs")
public class PropertiesFileController {
	
	@Autowired
	private SchedulerService schedulerService;
		
	@RequestMapping(value="/set-properties-file", method=RequestMethod.POST, 
			produces = "application/json", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseStatus createProperties(@RequestParam(value="file") MultipartFile file) throws IOException, SchedulerException {
		PropertiesUtils.uploadProperties(file.getInputStream());
		this.schedulerService.init();
		return new ResponseStatus(ResponseStatus.SUCCESS, "");
	}
	
	@RequestMapping(value="/download-properties", method=RequestMethod.GET)
	public void downloadPropertiesFile(HttpServletResponse response) throws IOException {
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename=\"app.properties\"");
		PropertiesUtils.getPropertiesFile(response.getOutputStream());
	}
	
	@RequestMapping(value="/get-property", method=RequestMethod.GET)
	public ResponseStatus getProperty(@RequestParam(value="key") String key) {
		return new ResponseStatus(ResponseStatus.SUCCESS, PropertiesUtils.getProperty(key));
	}
	
	@RequestMapping(value="/set-property", method=RequestMethod.POST)
	public ResponseStatus setProperty(@RequestBody String jsonString) throws JSONException, IOException {
		JSONObject json = new JSONObject(jsonString);
		PropertiesUtils.setProperty(json.getString("key"), json.getString("value"));
		return new ResponseStatus(ResponseStatus.SUCCESS, json.getString("value"));
	}
	
	@RequestMapping(value="/get-all-properties", method=RequestMethod.GET)
	public Set<Entry<Object, Object>> getAllProperties() throws IOException {
		return PropertiesUtils.getAllProperties();
	}
}
