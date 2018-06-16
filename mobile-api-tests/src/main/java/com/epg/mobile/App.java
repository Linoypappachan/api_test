package com.epg.mobile;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@ImportResource("classpath*:spring-context-config.xml")
@Configuration
public class App extends SpringBootServletInitializer
{
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }
    public static void main(String[] args ) {
    	LOG.info("Starting app @ " + new Date());
    	SpringApplication.run(App.class, args);
    }
}
