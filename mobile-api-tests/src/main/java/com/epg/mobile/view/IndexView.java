package com.epg.mobile.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexView {
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String welcome (ModelMap model) {
		return "index";
	}
}
