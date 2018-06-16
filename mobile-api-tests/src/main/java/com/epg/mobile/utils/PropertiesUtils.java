package com.epg.mobile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);
	
	private static Properties props;
	
	static {
		try {
			loadProperties(PropertiesUtils.class.getClassLoader().getResourceAsStream("app.properties"));
		} catch (IOException e) {
			LOG.error("Failed to load properties file on startup", e);
		}
	}
	
	public static void uploadProperties(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(in, baos);
		Utils.createFile("app.properties", new ByteArrayInputStream(baos.toByteArray()));
		loadProperties(new ByteArrayInputStream(baos.toByteArray()));
	}
	
	public static void loadProperties(InputStream in) throws IOException {
		props = new Properties();
		props.load(in);
	}
	
	public static void getPropertiesFile(OutputStream out) throws IOException {
		if (props != null) {
			props.store(out, "Downloaded at : "+new Date());
		}
	}
	
	public static String getProperty(String key) {
		String result = "";
		Object obj = props.get(key);
		if (obj != null) {
			result = obj.toString();
		}
		return result;
	}
	
	public static void setProperty(String key, String value) throws IOException {
		props.setProperty(key, value);
		StringWriter sw = new StringWriter();
		props.store(sw, "Created at: "+new Date());
		Utils.createFile("app.properties", new ByteArrayInputStream(sw.toString().getBytes()));
	}

	public static Set<Entry<Object, Object>> getAllProperties() {
		return props.entrySet();
	}
}
