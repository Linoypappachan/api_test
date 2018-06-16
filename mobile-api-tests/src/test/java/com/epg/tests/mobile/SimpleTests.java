package com.epg.tests.mobile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epg.mobile.dto.CompletedTest;
import com.epg.mobile.utils.ReportingUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tests-spring.xml" })
public class SimpleTests {
	
	@Test
	public void testGetDailyAlerts() throws IOException, ParseException {
		Map<String, List<CompletedTest>> result = ReportingUtils.getDailyAlerts("05-08-2017", "07-08-2017");
		Assert.assertTrue(!result.keySet().isEmpty());
	}
}
