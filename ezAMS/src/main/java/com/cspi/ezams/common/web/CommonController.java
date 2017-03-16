
package com.cspi.ezams.common.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.cspi.ezams.common.service.CommonService;
import com.cspi.ezams.common.vo.CommonVO;

@Controller
public class CommonController {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "serverinfo")
	private CompositeConfiguration configuration;

	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/login.do")
	public String login(@RequestParam(value = "UserId", required = true) String userId,
			@RequestParam(value = "UserPw", required = true) String userPw, Model model) throws Exception {

		String strMessage = null;
		String returnPage = null;

		boolean result = commonService.userCheck(userId, userPw);
		
		if (result) {
			returnPage = "serverInfoList";
		} else {
			strMessage = "아이디 혹은 비밀번호가 잘못되었습니다.";
			model.addAttribute("message", strMessage);
			returnPage = "index";
			log.debug("Login Fail returnPage :: "+ returnPage);
		}
		return returnPage;
	}

	@RequestMapping(value = "/common/selectServerInfo.do", method = RequestMethod.POST)
	public void selectServerInfo(@ModelAttribute("CommonVO") CommonVO commonVO, HttpServletRequest request,HttpServletResponse response, ModelMap model) throws Exception {
		
		JSONArray json = null;
		
		String folderPath = configuration.getString("server.fileDB") + commonVO.getSearchDate().replace("/","" );
		String fileName = "";
		if (commonVO.getSelectTime().length() == 1) {
			fileName = commonVO.getSearchDate().replace("/", "") + "0" + commonVO.getSelectTime() + ".json";
		} else {
			fileName = commonVO.getSearchDate().replace("/", "") + commonVO.getSelectTime() + ".json";
		} 

		File filePath = new File(folderPath +"\\"+ fileName);
		
		if (filePath.exists()) {
			json = commonService.selectFileDb(folderPath, fileName, commonVO.getSearchColumn(),commonVO.getSearchKeyword());
		}
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/common/history.do", method = RequestMethod.POST)
	public void selectHistoryServerInfo(@ModelAttribute("CommonVO") CommonVO commonVO, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception {

		JSONArray json = null;

		String folderPath = configuration.getString("server.fileDB") + "₩₩" + commonVO.getSearchDate().replace("/", "");
		String fileName = "";
		if (commonVO.getSelectTime().length() == 1) {
			fileName = "error_history_log_" + commonVO.getSearchDate().replace("/", "") + ".json";
		} else {
			fileName = "error_history_log_" + commonVO.getSearchDate().replace("/", "") + ".json";
		}

		File filePath = new File(folderPath + "\\" + fileName);

		if (filePath.exists()) {
			json = commonService.selectFileDb(folderPath, fileName, commonVO.getSearchColumn(),
					commonVO.getSearchKeyword());
		}

		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/common/selectInterval.do", method = RequestMethod.POST)
	public void selectInterval(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		boolean rtn;

		rtn = commonService.selectFileExit();

		PrintWriter writer;
		obj.put("rtn", rtn);

		//String endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
		
		
		//log.debug("시간 ::" + endTime);

		writer = response.getWriter();
		writer.print(obj);
		writer.close();
	}
}
