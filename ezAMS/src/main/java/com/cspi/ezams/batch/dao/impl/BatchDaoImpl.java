package com.cspi.ezams.batch.dao.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.cspi.ezams.batch.dao.BatchDao;
import com.cspi.ezams.batch.vo.ServerInfoResultVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
public class BatchDaoImpl implements BatchDao {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "serverinfo")
	private CompositeConfiguration configuration;

	@Override
	public boolean insertFileDb(String filePath, String tempPath, String fileName, String historyFileName,List<ServerInfoResultVo> serverInfoResultList) throws Exception {
		
		JSONArray objlist = new JSONArray();
		JSONArray historyObjList = new JSONArray();
		JSONObject obj;
		int resultCnt = 0;

		if (serverInfoResultList != null) {
			resultCnt = serverInfoResultList.size();
		}

		for (int i = 0; resultCnt > 0 && i < resultCnt; i++) {

			String alarm = "";
			int alarmDisk = 0;
			int alarmProcess = 0;
			int alarmRebootTime = 0;
			int alarmSlowQuerystat = 0;
			int alarmPingState = 0;

			obj = new JSONObject();

			String strDiskInfo = "";
			int arrLCnt = serverInfoResultList.get(i).getArrLogicalDiskInfo().length;

			strDiskInfo = strDiskInfo
					+ "<table width='100%' border='0' cellpadding='5' cellspacing='1' align='center' style='border:0px gray solid;'>";
			strDiskInfo = strDiskInfo + "<tr>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>DiskDrive</b></td>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>Size</b></td>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>Used</b></td>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>Avail</b></td>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>Use%</b></td>";
			strDiskInfo = strDiskInfo + "   <td style='border:0px gray solid;'><b>ProviderName</b></td>";

			boolean isLocalDiskCntAlarm = serverInfoResultList.get(i).isLocalDiskCntFlag();
			boolean isNetworkDiskCntAlarm = serverInfoResultList.get(i).isNetworkDiskCntFlag();
			
			strDiskInfo = strDiskInfo + " <td style= 'border:0px gray solid;'>"; // <td>
			
			if (isLocalDiskCntAlarm || isNetworkDiskCntAlarm) {					// TotalCnt 에다가 Font 태그 감싸준다.
				alarmDisk++;
				strDiskInfo = strDiskInfo + "<font name='DiskAlarm'>";
			} 
			
			// Total Disk Count 
			strDiskInfo = strDiskInfo + "<b>" + "Total Disk Count : "+ (serverInfoResultList.get(i).getLocalDiskCnt()+ serverInfoResultList.get(i).getNetworkDiskCnt())+ "/" + serverInfoResultList.get(i).getDiskConfigCnt() + "</b>";
			
			if(isLocalDiskCntAlarm || isNetworkDiskCntAlarm){
				strDiskInfo = strDiskInfo + "</font>";
			}
			
			strDiskInfo = strDiskInfo + "</td></tr>";

			for (int k = 4; k < arrLCnt; k++) {
				String strUse = serverInfoResultList.get(i).getArrLogicalDiskInfo()[k] + " ".toString();
				String arrLogicalDiskInfo[] = strUse.split("@");
				
				strDiskInfo = strDiskInfo + "<tr>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrLogicalDiskInfo[0].trim()+ "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrLogicalDiskInfo[1].trim()+ "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrLogicalDiskInfo[2].trim()+ "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrLogicalDiskInfo[3].trim()+ "</td>";

				double diskUse = 0;

				double diskUseAmountOfSpot = Double.valueOf(configuration.getString("server.diskUseAmountOfSpot")).doubleValue();
				double diskUseAmountOfMarsprimeAP = Double.valueOf(configuration.getString("server.diskUseAmountOfMarsprimeAP")).doubleValue();
				double diskUseAmountOfMarsprimeDB = Double.valueOf(configuration.getString("server.diskUseAmountOfMarsprimeDB")).doubleValue();
				double diskUseAmountOfPantheon = Double.valueOf(configuration.getString("server.diskUseAmountOfPantheon")).doubleValue();
				double diskUseAmountOfSequs = Double.valueOf(configuration.getString("server.diskUseAmountOfSequs")).doubleValue();
				double diskUseAmount = 0.0;

				if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("PANTHEON") > -1) {
					diskUseAmount = diskUseAmountOfPantheon;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("MARSPRIME") > -1 && serverInfoResultList.get(i).getGroupName().toUpperCase().indexOf("DB") > -1) {
					diskUseAmount = diskUseAmountOfMarsprimeDB;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("MARSPRIME") > -1 && serverInfoResultList.get(i).getGroupName().toUpperCase().indexOf("AP") > -1) {
					diskUseAmount = diskUseAmountOfMarsprimeAP;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("SPOT") > -1) {
					diskUseAmount = diskUseAmountOfSpot;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("S-EQUS") > -1) {
					diskUseAmount = diskUseAmountOfSequs;
				} else {
					diskUseAmount = 95.0;
				}

				if (strUse.indexOf("#") > 0) {
					diskUse = Double.valueOf(strUse.substring(strUse.indexOf("#") + 1, strUse.indexOf("*") - 1)).doubleValue();
				}
				if (diskUse >= diskUseAmount) {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'><font  name = 'DiskAlarm'>" + arrLogicalDiskInfo[4].trim().replace("#", "").replace("*", "") + "</font></td>";
					alarmDisk++;
				} else {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'>" + arrLogicalDiskInfo[4].trim().replace("#", "").replace("*", "") + "</td>";
				}

				if (arrLogicalDiskInfo.length >= 6) {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'>" + arrLogicalDiskInfo[5].trim() + ".  </td>";
				}
				strDiskInfo = strDiskInfo + "</tr>";
			}

			if (isLocalDiskCntAlarm) {
				strDiskInfo = strDiskInfo + "<tr>";
				strDiskInfo = strDiskInfo + "<td align = 'center' style ='border:0px gray solid;'><font name = 'DiskAlarm'>Local Disk Count : " + serverInfoResultList.get(i).getLocalDiskCnt() + "</font></td>";
				strDiskInfo = strDiskInfo + "</tr>";
			} else {
				strDiskInfo = strDiskInfo + "<tr>";
				strDiskInfo = strDiskInfo + "<td align = 'center' style ='border:0px gray solid;'>Local Disk Count : " + serverInfoResultList.get(i).getLocalDiskCnt() + "</td>";
				strDiskInfo = strDiskInfo + "</tr>";
			}

			int arrNCnt = serverInfoResultList.get(i).getArrNetWorkDiskInfo().length;
			for (int k = 4; k < (arrNCnt / 2) + 2; k++) {
				String strUse = serverInfoResultList.get(i).getArrNetWorkDiskInfo()[k] + " ".toString();
				String arrNetWorkDiskInfo[] = strUse.split("@");
				strDiskInfo = strDiskInfo + "<tr>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrNetWorkDiskInfo[0].trim() + "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrNetWorkDiskInfo[1].trim() + "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrNetWorkDiskInfo[2].trim() + "</td>";
				strDiskInfo = strDiskInfo + "   <td style = 'border:0px gray solid;'>" + arrNetWorkDiskInfo[3].trim() + "</td>";

				double diskUse = 0;

				double diskUseAmountOfSpot = Double.valueOf(configuration.getString("server.diskUseAmountOfSpot")).doubleValue();
				double diskUseAmountOfMarsprimeAP = Double.valueOf(configuration.getString("server.diskUseAmountOfMarsprimeAP")).doubleValue();
				double diskUseAmountOfMarsprimeDB = Double .valueOf(configuration.getString("server.diskUseAmountOfMarsprimeDB")).doubleValue();
				double diskUseAmountOfPantheon = Double .valueOf(configuration.getString("server.diskUseAmountOfPantheon")).doubleValue();
				double diskUseAmountOfSequs = Double.valueOf(configuration.getString("server.diskUseAmountOfSequs")) .doubleValue();
				double diskUseAmount = 0.0;

				if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("PANTHEON") > -1) {
					diskUseAmount = diskUseAmountOfPantheon;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("MARSPRIME") > -1 && serverInfoResultList.get(i).getGroupName().toUpperCase().indexOf("DB") > -1) {
					diskUseAmount = diskUseAmountOfMarsprimeDB;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("MARSPRIME") > -1 && serverInfoResultList.get(i).getGroupName().toUpperCase().indexOf("AP") > -1) {
					diskUseAmount = diskUseAmountOfMarsprimeAP;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("SPOT") > -1) {
					diskUseAmount = diskUseAmountOfSpot;
				} else if (serverInfoResultList.get(i).getToolName().toUpperCase().indexOf("S-EQUS") > -1) {
					diskUseAmount = diskUseAmountOfSequs;
				} else {
					diskUseAmount = 95.0;
				}

				if (strUse.indexOf("#") > 0) {
					diskUse = Double.valueOf(strUse.substring(strUse.indexOf("#") + 1, strUse.indexOf("*") - 1)).doubleValue();
				}
				if (diskUse >= diskUseAmount) {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'><font  name = 'DiskAlarm'>"
							+ arrNetWorkDiskInfo[4].trim().replace("#", "").replace("*", "") + "</font></td>";
					alarmDisk++;
				} else {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'>" + arrNetWorkDiskInfo[4].trim().replace("#", "").replace("*", "") + "</td>";
				}

				if (arrNetWorkDiskInfo.length == 6) {
					strDiskInfo = strDiskInfo + "<td style='border:0px gray solid;'>" + arrNetWorkDiskInfo[5].trim() + ".  </td>";
				}
				strDiskInfo = strDiskInfo + "</tr>";
			}
			if (serverInfoResultList.get(i).getArrNetWorkDiskInfo().length >= 4) {
				if (isNetworkDiskCntAlarm) {
					strDiskInfo = strDiskInfo + "<tr>";
					strDiskInfo = strDiskInfo + "<td align = 'center' style ='border:0px gray solid;'><font name = 'DiskAlarm'>NetWork Disk Count : "+ serverInfoResultList.get(i).getNetworkDiskCnt() + "</font></td>";
					strDiskInfo = strDiskInfo + "</tr>";
				} else {
					strDiskInfo = strDiskInfo + "<tr>";
					strDiskInfo = strDiskInfo+ "<td align = 'center' style ='border:0px gray solid;'>Network Disk Count : " + serverInfoResultList.get(i).getNetworkDiskCnt() + "</td>";
					strDiskInfo = strDiskInfo + "</tr>";
				}
			}
			strDiskInfo = strDiskInfo + "</table>";

			String isFailoverFlag = serverInfoResultList.get(i).getFailoverFlag();
			if (!isFailoverFlag.equals("EMPTY")) {
				alarmDisk++;
				strDiskInfo = strDiskInfo.replace(
						"<table width='100%' border='0' cellpadding='5' cellspacing='1' align='center' style='border:0px gray solid';>",
						"<table name='failoverFlag' width='100%' border='0' cellpadding='5' cellspacing='0'>"
						);
			}

			if (serverInfoResultList.get(i).getArrLogicalDiskInfo()[0].equals("N/A") && serverInfoResultList.get(i).getArrNetWorkDiskInfo().clone()[0].equals("N/A")) {
				obj.put("diskInfo", "N/A");
			} else {
				obj.put("diskInfo", strDiskInfo);
			}

			obj.put("no", Integer.parseInt(serverInfoResultList.get(i).getServerCategory()));
			obj.put("toolName", serverInfoResultList.get(i).getToolName());
			obj.put("hostName", serverInfoResultList.get(i).getGroupName() + "<br>("+ serverInfoResultList.get(i).getHostInfo() + ")");
			obj.put("ipInfo", serverInfoResultList.get(i).getIpInfo().split(" ")[0]);
			obj.put("cpuInfo", serverInfoResultList.get(i).getCpuInfo());
			obj.put("memoryInfo", serverInfoResultList.get(i).getMemoryInfo());
			obj.put("occuredTime", serverInfoResultList.get(i).getOccuredTime());
			obj.put("slowQueryStat", serverInfoResultList.get(i).getSlowBackUp().replace("#", ""));
			obj.put("runProcess", serverInfoResultList.get(i).getRunProcess().replace("#", ""));
			obj.put("lastBootUpTime", serverInfoResultList.get(i).getLastBootUpTime().replace("#", ""));
			obj.put("pingState", serverInfoResultList.get(i).getPingState().replace("#", ""));
			obj.put("iOWriteOperationsPersec", serverInfoResultList.get(i).getiOWriteOperationsPersec());
			obj.put("pageFaultsPerSec", serverInfoResultList.get(i).getPageFaultsPerSec());
			// }
			
			alarmSlowQuerystat = serverInfoResultList.get(i).getSlowBackUp().indexOf("#");
			alarmProcess = serverInfoResultList.get(i).getRunProcess().indexOf("#");
			alarmPingState = serverInfoResultList.get(i).getPingState().indexOf("#");
			alarmRebootTime = serverInfoResultList.get(i).getLastBootUpTime().indexOf("#");
			String hostInfo = serverInfoResultList.get(i).getHostInfo();
			String ipInfo = serverInfoResultList.get(i).getIpInfo().split(" ")[0];
			String cpuInfo = serverInfoResultList.get(i).getCpuInfo();
			String memoryInfo = serverInfoResultList.get(i).getMemoryInfo();
			String runProcess = serverInfoResultList.get(i).getRunProcess();
			String logicalDiskInfo = serverInfoResultList.get(i).getArrLogicalDiskInfo()[0];
			String netWorkDiskInfo = serverInfoResultList.get(i).getArrNetWorkDiskInfo()[0];
			String lastBootUpTime = serverInfoResultList.get(i).getLastBootUpTime();
			String slowQueryStat = serverInfoResultList.get(i).getSlowBackUp();

			if (alarmProcess > 0 || alarmDisk > 0 || alarmRebootTime > 0 || alarmSlowQuerystat > 0|| alarmPingState > 0) {
				alarm = "A";
			} else if (hostInfo.equals("N/A") || ipInfo.equals("N/A") || cpuInfo.equals("N/A") || memoryInfo.equals("N/A") || runProcess.equals("N/A")|| (logicalDiskInfo.equals("N/A") && netWorkDiskInfo.equals("N/A")) || lastBootUpTime.equals("N/A")|| slowQueryStat.equals("N/A")) {
				alarm = "B";
			} else {
				alarm = "N";
			}
			obj.put("alarm", alarm);

			if (obj.get("alarm").equals("A")) {
				historyObjList.add(obj);
			}
			objlist.add(obj);
		}

		try

		{
			BufferedWriter file = new BufferedWriter(new FileWriter(tempPath + "\\" + fileName));
			
			file.write(objlist.toString());
			file.flush();
			file.close();

			BufferedReader fis = new BufferedReader(new FileReader(tempPath + "\\" + fileName));
			BufferedWriter fos = new BufferedWriter(new FileWriter(filePath + "\\" + fileName));
			int data = 0;
			while ((data = fis.read()) != -1) {
				fos.write(data);
			}
			fos.flush();
			fis.close();
			fos.close();

			File i = new File(tempPath + "\\" + fileName);
			i.delete();

			if (historyObjList.toString().length() > 10) {
				File historyFilePath = new File(filePath + "\\" + historyFileName);

				StringBuilder strBuf = new StringBuilder();
				JSONArray previousResult = new JSONArray();

				if (historyFilePath.exists()) {

					BufferedReader historyFis = new BufferedReader(new FileReader(filePath + "\\" + historyFileName));

					int intData = 0;
					while ((intData = historyFis.read()) != -1) {
						strBuf.append((char) intData);
					}
					previousResult = JSONArray.fromObject(strBuf.toString());
					historyFis.close();
				}

				JSONArray nowResult = JSONArray.fromObject(historyObjList);

				if (previousResult.toString().length() > 5) {
					nowResult.addAll(previousResult);
				}

				BufferedWriter history = new BufferedWriter(new FileWriter(tempPath + "\\" + historyFileName));
				history.write(nowResult.toString());
				history.flush();
				history.close();

				BufferedReader hFis = new BufferedReader(new FileReader(tempPath + "\\" + historyFileName));
				BufferedWriter hFos = new BufferedWriter(new FileWriter(filePath + "\\" + historyFileName));

				int hData = 0;
				while ((hData = hFis.read()) != -1) {
					hFos.write(hData);
				}
				hFis.close();
				hFos.close();

				File hl = new File(tempPath + "\\" + historyFileName);
				hl.delete();

			}

			return true;
		} catch (IOException e) {
			log.debug(e);
			return false;
		}
	}
}