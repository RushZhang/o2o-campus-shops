package com.rush.o2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rush.o2o.entity.Area;
import com.rush.o2o.service.AreaService;

@Controller
@RequestMapping("/superadmin")
public class AreaController {
	
	Logger logger = LoggerFactory.getLogger(AreaController.class);
	
	@Autowired
	private AreaService areaService;
	
	/*
	 *    在SpringMVC中可以在Controller的某个方法上加@ResponseBody注解，表示该方法的返回结果直接写入HTTP response body中。 
	 */
	@RequestMapping(value = "/listarea", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listArea() {
		logger.info("========力哥=========");
		long startTime = System.currentTimeMillis();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Area> list = new ArrayList<Area>();
		try {
			list = areaService.getAreaList();
			modelMap.put("rows", list);
			modelMap.put("total", list.size());
		}	catch(Exception exception ) {
			exception.printStackTrace();
			modelMap.put("succuess", false);
			modelMap.put("errMsg", exception.toString());
		}
		long endTime = System.currentTimeMillis();
		logger.debug("花费时间：[{}ms]", endTime - startTime);
		logger.info("========结束=========");
		return modelMap;
	}
}
