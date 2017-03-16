package com.cspi.ezams.batch.dao;

import java.util.List;

import com.cspi.ezams.batch.vo.ServerInfoResultVo;


public interface BatchDao {
	
	public boolean insertFileDb(String filePath, String tempPath, String fileName, String historyFileName, List<ServerInfoResultVo> serverInfoResultList) throws Exception;
}
