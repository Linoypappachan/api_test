package com.epg.mobile.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.wagon.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	
	
	public static void createWorkspace() throws IOException {
		String outDir = PropertiesUtils.getProperty(AppConstants.OUT_DIR);
		if (StringUtils.isEmpty(outDir)) {
			PropertiesUtils.setProperty(AppConstants.OUT_DIR, System.getProperty("java.io.tmpdir")
					+AppConstants.SYSTEM_SEPARATOR
					+AppConstants.OUT_DIR_NAME);
			outDir = PropertiesUtils.getProperty(AppConstants.OUT_DIR);
		}
		File outDirFolder = new File(outDir);
		if (!outDirFolder.exists()) {				
			outDirFolder.mkdirs();
		}
	}

	public static void createFile(String fileName, InputStream in) throws IOException {
		String filePath = PropertiesUtils.getProperty(AppConstants.OUT_DIR)
				+AppConstants.SYSTEM_SEPARATOR
				+fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		StringBuilder fileData = new StringBuilder();
		List<String> lines = IOUtils.readLines(in, "UTF-8");
		for (String line : lines) {
			fileData.append(line+"\n");
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(fileData.toString());
		writer.flush();
		writer.close();
		in.close();
		LOG.info("Created file: {}", filePath);
	}


	public static Date getCurrentTimeWithReducedDuration(Date currentTime, String duration) {
		int d = 0, h = 0, m = 0;
		String[] parts = null;
		if (duration.contains("d")) {
			parts = duration.split("d");
			d = Integer.parseInt(parts[0]);
			if (parts.length > 1)
				duration = parts[1];
		}
		if (duration.contains("h")) {
			parts = duration.split("h");
			h = Integer.parseInt(parts[0]);
			if (parts.length > 1)
				duration = parts[1];
		}
		if (duration.contains("m")) {
			parts = duration.split("m");
			m = Integer.parseInt(parts[0]);
		}		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTime);
		cal.add(Calendar.DATE, -d);
		cal.add(Calendar.HOUR, -h);
		cal.add(Calendar.MINUTE, -m);
		return cal.getTime();
	}

	public static void deleteFoldersOlderThan(Date timeLimit, String outDir) throws IOException {
		File folder = new File(outDir);
		if (folder.exists() && folder.isDirectory()) {
			String[] fileNames = folder.list();
			for (String fileName : fileNames) {
				File subFolder = new File(outDir
						+System.getProperty("file.separator")+fileName);
				if (subFolder.exists() && subFolder.isDirectory() && 
						subFolder.lastModified() < timeLimit.getTime()) {
					FileUtils.deleteDirectory(subFolder);
				}
			}
		}		
	}

	public static ByteArrayOutputStream getCopyOutputStream(InputStream source) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = source.read(buffer)) > -1 ) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		return baos;
	}

	public static File getFile(String fileName) {
		String filePath = PropertiesUtils.getProperty(AppConstants.OUT_DIR)
				+AppConstants.SYSTEM_SEPARATOR
				+fileName;
		File file = new File(filePath);
		return file;
	}

	public static boolean getRandomBoolean() {
		return (Math.random()  * 10) > 5;
	}

	public static String getUtf8String(String str) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		StringWriter sw = new StringWriter();
		IoUtils.copy(bais, sw, "UTF-8");
		return sw.toString();
	}

	public static String decodeURIComponent(String s) {
		String result = "";
		try {
			result = URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			result = s;  
		}
		return result;
	}

	public static String encodeURIComponent(String s) {
		String result = "";
		try {
			result = URLEncoder.encode(s, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~")
					.replaceAll("%5", "%255")
					.replaceAll("%3A", "%253A");
		} catch (UnsupportedEncodingException e) {
			result = s;
		}
		return result;
	}

	public static void appendDataToFile(File file, String data) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.append(data);
		writer.close();
	}
}
