package com.leekyoungil.illuminati.gatekeeper.system.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/system", produces = MediaType.TEXT_HTML_VALUE)
public class SystemViewController {

    private final String SYSTEM_VIEW_URI = "/view";
    private final String SYSTEM_VIEW_JVM_STATUS_URI = "/view/jvmStatus";
    private final String SYSTEM_VIEW_SUMMARY_URI = "/view/summary/{hostName}";

    @RequestMapping(value = SYSTEM_VIEW_URI, method = RequestMethod.GET)
    public String systemView () {
        return "system/index";
    }

    @RequestMapping(value = SYSTEM_VIEW_JVM_STATUS_URI, method = RequestMethod.GET)
    public String systemViewJvmStatus () {
        return "system/jvm/index";
    }

    @RequestMapping(value = SYSTEM_VIEW_SUMMARY_URI, method = RequestMethod.GET)
    public ModelAndView SystemViewSummary (@PathVariable String hostName) {
        ModelAndView view = new ModelAndView();
        view.setViewName("system/summary");
        view.addObject("hostName", hostName);
        return view;
    }
}
