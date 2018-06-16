package com.epg.mobile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.epg.mobile.service.FTLService;
import com.epg.mobile.utils.PropertiesUtils;
import com.epg.mobile.utils.Utils;

@Component
public class AppContextEventListener implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(AppContextEventListener.class);
	
	@Autowired
	private FTLService ftlService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			Utils.createWorkspace();
			// load properties
			File propertiesFile = Utils.getFile("app.properties");
			if (propertiesFile.exists()) {
				PropertiesUtils.loadProperties(new FileInputStream(propertiesFile));
			} else {
				// saving same properties file present in the build
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PropertiesUtils.getPropertiesFile(baos);
				PropertiesUtils.uploadProperties(new ByteArrayInputStream(baos.toByteArray()));
			}
			// load notification templates
			ftlService.loadAllTemplates();			
		} catch(Exception e) {
			LOG.error("Error occured while creating temp folder", e);
		}
	}

}
