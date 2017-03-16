package com.cspi.ezams.common.service;

import net.sf.json.JSONArray;

public interface CommonService {

	public JSONArray selectFileDb(String folderPath, String fileName, String searchColumn, String searchKeyword) throws Exception;

	public boolean selectFileExit() throws Exception;

	public boolean userCheck(String userId, String userPw) throws Exception;
}
