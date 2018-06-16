package com.epg.mobile.service;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.epg.mobile.testxml.dto.TestCase;

@Service
public class TestDataQueue {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestDataQueue.class);
	
	@Autowired
	private ApplicationContext appContext;
	
	private static TestDataQueue _this;
	
	@PostConstruct
	public void init() {
		_this = appContext.getBean(TestDataQueue.class);
	}
	
	private Queue<TestCase> queue = new LinkedList<TestCase>();
	
	public void enqueue(TestCase tc) {
		synchronized(this) {
			this.queue.add(tc);
			LOG.info("Added {} test case to the queue", tc.getId());
		}
	}
	
	public TestCase dequeue() {
		TestCase tc = null;
		synchronized(this) {
			tc = this.queue.poll();
			LOG.info("Removed {} test case from the queue", tc.getId());
		}
		return tc;
	}
	
	public static TestDataQueue getObject() {
		return _this;
	}
}
