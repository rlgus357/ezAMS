package com.cspi.ezams.common.dao;

import net.sf.json.JSONArray;

public interface CommonDAO {

	public boolean userCheck(String userId, String userPw) throws Exception;

	public JSONArray selectFileDb(String folderPath, String fileName, String searchColumn, String searchKeyword) throws Exception;

}
