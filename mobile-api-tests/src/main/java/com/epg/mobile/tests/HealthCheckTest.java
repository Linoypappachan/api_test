package com.epg.mobile.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.maven.wagon.util.IoUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.epg.mobile.exceptions.TestsXMLException;
import com.epg.mobile.service.TestDataQueue;
import com.epg.mobile.testxml.dto.Param;
import com.epg.mobile.testxml.dto.Params;
import com.epg.mobile.testxml.dto.TestCase;
import com.epg.mobile.utils.AppConstants;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.Utils;

public class HealthCheckTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthCheckTest.class);
	
	private String appSession;
	private String userSession;
	
	private static final String EXPECTED = "\"status_code\":200";
	
	private void loginUser(boolean proxyTrue) throws NumberFormatException, ClientProtocolException, IOException {
		String result = null, usersessiongen = null;
		Request req = Request
				.Post(PropertiesUtils.getProperty("user.session.url"))
				.addHeader(AppConstants.APP_SESSION_NAME, this.getAppSession());
		if (proxyTrue) {
			req = req.viaProxy(new HttpHost(
					PropertiesUtils.getProperty("proxy.host").toString(),
					Integer.parseInt(PropertiesUtils
							.getProperty("proxy.port").toString())));
		}
		result = req.bodyForm(
						Form.form().add(AppConstants.LOGIN_USER_NAME, 
								PropertiesUtils.getProperty("login.user").toString())
								.add(AppConstants.LOGIN_PASSWORD_NAME, 
										PropertiesUtils.getProperty("login.password").toString())
								.add("auth_type", "PWD_ONLY").build(),
						Consts.UTF_8).execute().returnContent().asString();
		
		String jsonStatusMessage = "";
		int jsonStatusCode = -1;
		
		if (StringUtils.isNotEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				jsonStatusMessage = jsonObject.getString("status_message");
				jsonStatusCode = jsonObject.getInt("status_code");
				JSONObject getdataval = (JSONObject) jsonObject.get("data");
				usersessiongen = getdataval.get("access_token").toString();
				usersessiongen = "Bearer " + usersessiongen;
			} catch (Throwable t) {
				LOG.error("Login api json exception", t);
			}			
		}
		this.setUserSession(usersessiongen);
		if (!result.contains(EXPECTED)) {
			String failMessage = "Login failed. ";
			if (jsonStatusCode != -1) {
				failMessage += "Login API response code: "+jsonStatusCode;
			}
			if (StringUtils.isNotEmpty(jsonStatusMessage)) {
				failMessage += ". Message: "+jsonStatusMessage;
			}
			Assert.fail(failMessage);
		}
	}

	private void createDeviceSession(boolean proxyTrue) throws NumberFormatException, ClientProtocolException, IOException {
		String result = null, appsessiongen = null;
		Request req = Request
				.Post(PropertiesUtils.getProperty("app.session.url"));
		if (proxyTrue) {
			req = req.viaProxy(new HttpHost(
					PropertiesUtils.getProperty("proxy.host").toString(), 
					Integer.parseInt(PropertiesUtils
							.getProperty("proxy.port").toString())));
		}
		result = req.bodyForm(
					Form.form().add("app_token", 
								PropertiesUtils.getProperty("apptoken").toString())
							.add("device_id", 
									PropertiesUtils.getProperty("device.id").toString())
							.add("device_name", 
									PropertiesUtils.getProperty("device.name").toString())
							.add("app_version",
									PropertiesUtils.getProperty("app.version").toString())
							.add("os", PropertiesUtils.getProperty("device.os").toString())
							.add("os_version", 
									PropertiesUtils.getProperty("device.os_version").toString())
							.build(), Consts.UTF_8)
			.execute().returnContent().asString();
		String jsonStatusMessage = "";
		int jsonStatusCode = -1;
		if (StringUtils.isNotEmpty(result)){
			try {
				JSONObject jsonObject = new JSONObject(result);
				jsonStatusMessage = jsonObject.getString("status_message");
				jsonStatusCode = jsonObject.getInt("status_code");
				JSONObject getdataval = (JSONObject) jsonObject.get("data");
				appsessiongen = getdataval.get("access_token").toString();
				appsessiongen = "Bearer " + appsessiongen;
				this.setAppSession(appsessiongen);
			} catch (Throwable t) {
				LOG.error("Session api json exception", t);
			}
		}
		
		if (!result.contains(EXPECTED)) {
			String failMessage = "Aplication Init failed. ";
			if (jsonStatusCode != -1) {
				failMessage += "Init API response code: "+jsonStatusCode;
			}
			if (StringUtils.isNotEmpty(jsonStatusMessage)) {
				failMessage += ". Message: "+jsonStatusMessage;
			}
			Assert.fail(failMessage);
		}
	}

	@DataProvider(name="tests_data_provider")
	public Object[][] testDataProvider() throws IOException, TestsXMLException {
		TestCase testCase = TestDataQueue.getObject().dequeue();
		LOG.info("Dequeued testcase object {}", testCase.getId());
		Object[][] data = new Object[1][1];
		data[0][0] = testCase;
		return data;
	}
	
	@Test(dataProvider="tests_data_provider")
	public void test(TestCase testCase) throws ClientProtocolException, IOException, URISyntaxException {
		LOG.info("Executing test case job: {}", testCase.getId());
		HttpResponse response = getUrlResponse(testCase);
		StringWriter sw = new StringWriter();
		JSONObject json = null;
		if (response != null && response.getEntity() != null) {
			InputStream in = response.getEntity().getContent();
			if (in != null) {
				IoUtils.copy(in, sw);
				try {					
					json = new JSONObject(sw.toString());
					if (json != null) {
						String jsonStatusMessage = json.getString("status_message");
						int jsonStatusCode = json.getInt("status_code");
						Assert.assertTrue(("Ok".equalsIgnoreCase(jsonStatusMessage))
								&& (jsonStatusCode == 200),"Status code recieved: "+jsonStatusCode);
					}
				} catch(Exception je) {
					LOG.error("Test url response is not json", je);
				}
			}
		}
		int status_code = response.getStatusLine().getStatusCode();
		Assert.assertTrue((status_code == 200), "Status code recieved: "+status_code);
	}

	private HttpResponse getUrlResponse(TestCase testCase) throws ClientProtocolException, IOException, URISyntaxException {
		Request request = null;
		String url = testCase.getUrl();
		boolean proxy = false, session = false, login = false;
		if ("true".equalsIgnoreCase(testCase.getProxy())) {
			proxy = true;
		}
		if ("true".equalsIgnoreCase(testCase.getSession())) {
			session = true;
		}
		if ("true".equalsIgnoreCase(testCase.getLogin())) {
			session = true;
			login = true;
		}
		
		Params  params = testCase.getParams();
		
		// URL Path params
		if (params != null && params.getUrl() != null && !params.getUrl().isEmpty()) {
			List<Param> urlParams = params.getUrl();
			for (Param param : urlParams) {
				url = url.replace("{"+param.getName().trim()+"}", param.getValue());
			}
		}		
		
		// URL Query params
		if (params != null && params.getQuery() != null && !params.getQuery().isEmpty()) {
			List<Param> queryParams = params.getQuery();
			URIBuilder uriBuilder = new URIBuilder(url);
			for (Param param : queryParams) {
				uriBuilder.setParameter(param.getName(), Utils.encodeURIComponent(param.getValue()));
			}
			url = uriBuilder.build().toString();
		}
		
		
		if (testCase.getVerb().equalsIgnoreCase(AppConstants.HTTP_VERB_GET)) {
			request = Request.Get(url);
		} else if (testCase.getVerb().equalsIgnoreCase(AppConstants.HTTP_VERB_POST)){
			request = Request.Post(url);
		}
		
		if (login) {
			createDeviceSession(proxy);
			loginUser(proxy);
			request = request
					.addHeader(AppConstants.APP_SESSION_NAME, appSession)
					.addHeader(AppConstants.USER_SESSION_NAME, userSession);
		} else if (session) {
			createDeviceSession(proxy);
			request = request
					.addHeader(AppConstants.APP_SESSION_NAME, appSession);
		}
		
		if (proxy) {
			request = request.viaProxy(new HttpHost(
					PropertiesUtils.getProperty("proxy.host").toString(), 
					Integer.parseInt(PropertiesUtils
							.getProperty("proxy.port").toString())));
		}
		
		// POST Form data
		if (testCase.getVerb().equalsIgnoreCase(AppConstants.HTTP_VERB_POST)
			&& (params != null && params.getForm() != null && !params.getForm().isEmpty())) {
			Form formObject = Form.form();
			List<Param> formParams = params.getForm();
			for (Param param : formParams) {
				formObject.add(param.getName(), param.getValue().toString());
			}
			request = request.bodyForm(formObject.build(), Consts.UTF_8);
		}
		
		// Header params
		if (params != null && params.getHeader() != null && !params.getHeader().isEmpty()) {
			for (Param param : params.getHeader()) {
				request = request.addHeader(param.getName(), param.getValue());
			}
		}
		
		return request.execute().returnResponse();
	}

	public String getAppSession() {
		return appSession;
	}

	public void setAppSession(String appSession) {
		this.appSession = appSession;
	}

	public String getUserSession() {
		return userSession;
	}

	public void setUserSession(String userSession) {
		this.userSession = userSession;
	}
}
