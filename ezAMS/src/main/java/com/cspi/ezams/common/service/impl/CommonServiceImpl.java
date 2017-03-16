package com.cspi.ezams.common.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cspi.ezams.common.service.CommonService;
import com.cspi.ezams.common.dao.CommonDAO;

@Service
public class CommonServiceImpl implements CommonService {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "serverinfo")
	private CompositeConfiguration configuration;

	@Autowired
	private CommonDAO commonDAO;

	@Override
	public boolean userCheck(String userId, String userPw) throws Exception {
		return commonDAO.userCheck(userId, userPw);
	}

	@Override
	public JSONArray selectFileDb(String folderPath, String fileName, String searchColumn, String searchKeyword)
			throws Exception {
		return commonDAO.selectFileDb(folderPath, fileName, searchColumn, searchKeyword);
	}

	@Override
	public boolean selectFileExit() throws Exception {

		boolean rtn;

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatHH = new SimpleDateFormat("HH");
		
		String tmpDate = format.format(date);
		String tmpHour = formatHH.format(date);

		String filePath = configuration.getString("server.fileDB") + tmpDate ;
		String fileName = tmpDate +tmpHour + ".json";
		
		File existsFile = new File(filePath + "\\" + fileName);

		rtn = existsFile.exists();

		return rtn;
	}
}
