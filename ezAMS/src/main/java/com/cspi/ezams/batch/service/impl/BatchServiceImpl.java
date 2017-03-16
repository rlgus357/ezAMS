package com.cspi.ezams.batch.service.impl;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cspi.ezams.batch.service.BatchService;
import com.cspi.ezams.batch.dao.BatchDao;
import com.cspi.ezams.batch.vo.ServerInfoVO;
import com.cspi.ezams.batch.vo.RemoteShellOutputVO;
import com.cspi.ezams.batch.vo.ServerInfoResultVo;

@Service
public class BatchServiceImpl implements BatchService {

	/** log **/
	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "serverinfo")
	private CompositeConfiguration configuration;

	@Autowired
	private BatchDao batchDao;

	@Override
	public boolean startBatch() throws Exception {

		/** 데이터가 들어갈 폴더 생성 **/
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatHour = new SimpleDateFormat("yyyyMMddHH");
		// 12.30 추가 히스토리 추가를 위한 발생시간 추가
		SimpleDateFormat formatMinute = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		String tmpTime = formatMinute.format(date);

		String tmpDate = format.format(date);
		String tmpHour = formatHour.format(date);

		String filePath = configuration.getString("server.fileDB") + "\\" + tmpDate;
		String tempPath = configuration.getString("server.fileDB") + "\\" + tmpDate + "\\temp";
		String fileName = tmpHour + ".json";
		String historyFileName = "error_history_log_" + tmpDate + ".json";

		File makeFolder = new File(filePath);
		if (!makeFolder.exists()) {
			makeFolder.mkdir();
		}

		File tempFolder = new File(tempPath);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}

		/** 원격 서버의 리소스 등 정보를 가져옴 (쓰레드로 실행) **/
		List<ServerInfoVO> serverInfoList = new ArrayList<ServerInfoVO>();

		String strfile = configuration.getString("server.configPath");

		String arrfile[] = strfile.split("⊥");

		for (int i = 0; i < arrfile.length; i++) {

			String strxml = arrfile[i];
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(strxml);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(is);

			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String expression = "//*/server";
			NodeList cols = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
			
			
			for (int idx = 0; idx < cols.getLength(); idx++) {
				String serverCategory = cols.item(idx).getAttributes().item(0).getTextContent();
				
				/** 서버 툴네임 **/
				expression = "//*[@category=" + serverCategory + "]/toolName";
				String toolName = xpath.compile(expression).evaluate(document);

				/** 서버 그룹핑 네임 **/
				expression = "//*[@category=" + serverCategory + "]/groupName";
				String groupName = xpath.compile(expression).evaluate(document);

				/** 서버아이피 **/
				expression = "//*[@category=" + serverCategory + "]/serverIp";
				String serverIp = xpath.compile(expression).evaluate(document);

				/** 서버아이디 **/
				expression = "//*[@category=" + serverCategory + "]/serverId";
				String serverId = xpath.compile(expression).evaluate(document);

				/** 서버패스워드 **/
				expression = "//*[@category=" + serverCategory + "]/serverPassWd";
				String serverPassWd = xpath.compile(expression).evaluate(document);

				/** 서버 확인 리소스 **/
				expression = "//*[@category=" + serverCategory + "]/serverCheckInfo";
				String serverCheckInfo = xpath.compile(expression).evaluate(document);

				/** 서버 확인 프로세스 **/
				expression = "//*[@category=" + serverCategory + "]/runProcess";
				String runProcess = xpath.compile(expression).evaluate(document);

				/** 서버 확인 프로세스 카운트 **/
				expression = "//*[@category=" + serverCategory + "]/runProcessCnt";
				String runProcessConfigCnt = xpath.compile(expression).evaluate(document);

				/** 페일오버 확인 **/
				expression = "//*[@category=" + serverCategory + "]/failoverConfigFlag";
				String failoverConfigFlag = xpath.compile(expression).evaluate(document);

				/** 서버로컬디스크갯수 **/
				expression = "//*[@category=" + serverCategory + "]/localDiskCnt";
				String localDiskCnt = xpath.compile(expression).evaluate(document);

				/** 서버 네트워크 디스크 갯수 **/
				expression = "//*[@category=" + serverCategory + "]/networkDiskCnt";
				String networkDiskCnt = xpath.compile(expression).evaluate(document);

				ServerInfoVO serverInfo = new ServerInfoVO();
				
				serverInfo.setToolName(toolName);
				serverInfo.setServerCategory(serverCategory);
				serverInfo.setGroupName(groupName);
				serverInfo.setServerIp(serverIp);
				serverInfo.setServerId(serverId);
				serverInfo.setServerPassWd(serverPassWd);
				serverInfo.setServerCheckInfo(serverCheckInfo);
				serverInfo.setRunProcess(runProcess);
				serverInfo.setRunProcessCnt(runProcessConfigCnt);
				serverInfo.setOccuredTime(tmpTime);
				serverInfo.setFailoverConfigFlag(failoverConfigFlag);
				serverInfo.setLocalDiskCnt(Integer.parseInt(localDiskCnt));
				serverInfo.setNetworkDiskCnt(Integer.parseInt(networkDiskCnt));

				serverInfoList.add(serverInfo);
				
			}
		}
		
		String remoteIp = configuration.getString("server.remoteIp").trim();

		String slowBackupPath = configuration.getString("server.slowBackUpPath");
		String taskSlowBackupPath = configuration.getString("server.taskSlowBackUpPath");

		int processDownTimeout = Integer.parseInt(configuration.getString("server.processDownTimeout"));
		int rebootTimeout = Integer.parseInt(configuration.getString("server.rebootTimeout"));
		int failoverTimeout = Integer.parseInt(configuration.getString("server.failoverTimeout"));

		int serverInfoCnt = serverInfoList.size();
		int threadCnt = Integer.parseInt(configuration.getString("server.threadCnt"));

		int groupCnt = (serverInfoCnt / threadCnt) + 1;
		int lastNodeCnt = serverInfoCnt % threadCnt;

		log.debug("main start.");
		String startTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
		log.debug("시작 시간 :: " + startTime);

		long time1 = System.currentTimeMillis();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		int totalCnt = 0;
		for (int i = 1; i <= groupCnt; i++) {
			int nodeCnt = threadCnt;
			if (i == groupCnt) { 
				nodeCnt = lastNodeCnt;
			}

			for (int j = 0; j < nodeCnt; j++) { 
				RemoteShellThreadRun shellThread = new RemoteShellThreadRun(totalCnt, remoteIp, slowBackupPath,taskSlowBackupPath, processDownTimeout, rebootTimeout, failoverTimeout,serverInfoList.get(totalCnt));
				shellThread.start();
				threads.add(shellThread);
				totalCnt++;
			}

			for (int j = 0; j < threads.size(); j++) {
				Thread t = threads.get(j);

				try {
					t.join(Integer.parseInt(configuration.getString("server.threadTimeout")) * 1000);

				} catch (Exception e) {
					log.debug(e);
				}
			}
			threads.clear();
		}

		long time2 = System.currentTimeMillis();

		String endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

		log.debug("main end.");
		log.debug("종료시간 :: " + endTime);
		log.debug("쓰레드 수행시간 :: " + (time2 - time1) / 1000.0);

		long time3 = System.currentTimeMillis();
		String parsingStartTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

		log.debug("parsing Start.");
		log.debug("시작 시간 :: " + parsingStartTime);

		/**
		 * 여기까지 쓰레드 아래부턴 쓰레드 결과 값 쓰레드 이므로 순서대로 같이 나오진 않는다.
		 **/

		List<ServerInfoResultVo> ServerInfoResultList = new ArrayList<ServerInfoResultVo>();
		String strResult = null;
		 
		
		for (int i = 0; RemoteShellOutputVO.ServerInfoList != null
				&& i < RemoteShellOutputVO.ServerInfoList.size(); i++) {
			strResult = RemoteShellOutputVO.ServerInfoList.get(i);
			String arrResult[] = strResult.split("\\&"); 
			String arrResource[] = strResult.substring(0, (arrResult[0].length() - 1)).split("⊥");
				/*
			for (int j = 0; j < arrResult.length; j++) {
				log.debug("arrResult [" + j + "] ::"+ arrResult[j]);
			}
				*/
			ServerInfoResultVo serverInfoResultVo = new ServerInfoResultVo();
			for (int j = 0; j < arrResource.length; j++) {
				switch (arrResource[j].split("\\^")[0]) {
				// 2016. 8. 2 N/A 값 찍지 않음. (HostName , ip, cpu, memory)
				case "ipInfo":
					if (arrResource[j].length() > 1) {
						serverInfoResultVo.setIpInfo(arrResource[j].split("\\^")[1]);
					} else {
						serverInfoResultVo.setIpInfo("N/A" + " " + "..");

					}
					break;
				case "hostInfo":
					if (arrResource[j].length() > 1) {
						serverInfoResultVo.setHostInfo(arrResource[j].split("\\^")[1]);
					} else {
						serverInfoResultVo.setHostInfo("N/A");

					}
					break;
				case "cpuInfo":
					if (arrResource[j].length() > 1) {
						serverInfoResultVo.setCpuInfo(arrResource[j].split("\\^")[1]);
					} else {
						serverInfoResultVo.setCpuInfo("N/A");

					}
					break;
				case "memoryInfo":
					if (arrResource[j].length() > 1) {
						serverInfoResultVo.setMemoryInfo(arrResource[j].split("\\^")[1]);
					} else {
						serverInfoResultVo.setMemoryInfo("N/A");

					}
					break;
				case "Disk":
					break;
				}
			}

			/** 디스크 정보 **/
			serverInfoResultVo.setDiskInfo(arrResult[1].replaceAll("⊥", "<br>"));   

			String arrLogicalDiskInfo[] = null;
			String arrNetWorkDiskInfo[] = null;
			int localDiskCnt = 0;
			int networkDiskCnt = 0;
			if (arrResult[1].length() > 1) {
				arrLogicalDiskInfo = arrResult[1].substring(0, (arrResult[1].length() - 1)).split("⊥");
			} else {
				arrLogicalDiskInfo = new String[1];
				arrLogicalDiskInfo[0] = "N/A";
			}

			if (arrResult[2].length() > 1) {
				arrNetWorkDiskInfo = arrResult[2].substring(0, (arrResult[2].length() - 1)).split("⊥");
			} else {
				arrNetWorkDiskInfo = new String[1];
				arrNetWorkDiskInfo[0] = "N/A";
			}

			// ####################################
			// 2016. 04. 12 추가
			for (String strLogicalDiskInfo : arrLogicalDiskInfo) {
				if (strLogicalDiskInfo.indexOf(":)") > -1) {
					localDiskCnt++;
				}
			}
			for (String strNetWorkDiskInfo : arrNetWorkDiskInfo) {
				if (strNetWorkDiskInfo.indexOf(":)") > -1) {
					networkDiskCnt++;
				}
			}
			for (int j = 0; j < serverInfoList.size(); j++) {
				int logicalDiskConfigCnt = 0;
				int networkDiskConfigCnt = 0;

				if (serverInfoList.get(j).getServerCategory().equals(arrResult[16])) {
					logicalDiskConfigCnt = serverInfoList.get(j).getLocalDiskCnt();
					networkDiskConfigCnt = serverInfoList.get(j).getNetworkDiskCnt();
					int totalDiskCnt = 0;
					totalDiskCnt = logicalDiskConfigCnt + networkDiskConfigCnt;
					
					if (arrLogicalDiskInfo[0].equals("N/A")) {
						serverInfoResultVo.setLocalDiskCntFlag(false);
					} else {
						if (logicalDiskConfigCnt == localDiskCnt) {
							serverInfoResultVo.setLocalDiskCntFlag(false);
						} else {
							serverInfoResultVo.setLocalDiskCntFlag(true);
						}
					}
					
					
					if (arrNetWorkDiskInfo.clone()[0].equals("N/A")) {
						// 2016. 5.13 추가. 네트워크가 하나도 없을경우 제대로 체크하지 못하여 추가
						// #######################
						networkDiskConfigCnt = 0; // 기존의 설정값 무시함.
						totalDiskCnt = logicalDiskConfigCnt + networkDiskConfigCnt;
						// #######################
						serverInfoResultVo.setNetworkDiskCntFlag(false);
					} else {
						if (networkDiskCnt > 0 && networkDiskConfigCnt == ((networkDiskCnt / 2) + 1)) {
							serverInfoResultVo.setNetworkDiskCntFlag(false);
						} else {
							serverInfoResultVo.setNetworkDiskCntFlag(true);
						}
					}

					serverInfoResultVo.setDiskConfigCnt(totalDiskCnt);
					serverInfoResultVo.setLocalDiskCnt(localDiskCnt);
					if (networkDiskCnt > 0) {
						serverInfoResultVo.setNetworkDiskCnt(((networkDiskCnt / 2) + 1));
					} else {
						serverInfoResultVo.setNetworkDiskCnt(0);
					}
				}
			} 

			// ##########################################
			serverInfoResultVo.setArrLogicalDiskInfo(arrLogicalDiskInfo);
			serverInfoResultVo.setArrNetWorkDiskInfo(arrNetWorkDiskInfo);
			// ##########################################

			// 2016. 04.19 s-equs SlowQueryBackUp Check 추가
			String strSlowQueryBackup = "";
			if (arrResult[9].replaceAll("⊥", "").equals("True") && 
				arrResult[10].replaceAll("⊥", "").equals("True") && 
				arrResult[11].replaceAll("⊥", "").equals("True") && 
				arrResult[12].replaceAll("⊥", "").equals("True")) {
				if (arrResult[9].length() > 1 && arrResult[9].replaceAll("⊥", "").trim().equals("True")) {
					strSlowQueryBackup += "<b>" + taskSlowBackupPath + "<b><br>";
					strSlowQueryBackup += "SlowQueryBackUp(1)<br>";
				} else {
					strSlowQueryBackup += "<b>" + taskSlowBackupPath + "<b><br>";
					strSlowQueryBackup += "<font Name = 'processDownCntFlag'>SlowQueryBackUp(0)</font><br>#";
				}

				if (arrResult[10].length() > 1 && arrResult[10].replaceAll("⊥", "").trim().equals("True")) {
					strSlowQueryBackup += "<b>" + taskSlowBackupPath + "<b><br>";
					strSlowQueryBackup += "SlowQueryBackUp.bat(1)<br>";
				} else {
					strSlowQueryBackup += "<b>" + taskSlowBackupPath + "<b><br>";
					strSlowQueryBackup += "<font Name = 'processDownCntFlag'>SlowQueryBackUp.bat(0)</font><br>#";
				}

				if (arrResult[11].length() > 1 && arrResult[11].replaceAll("⊥", "").trim().equals("True")) {
					strSlowQueryBackup += "SlowQueryBackUp.exe(1)<br>";
				} else {
					strSlowQueryBackup += "<font Name = 'processDownCntFlag'>SlowQueryBackUp.exe(0)</font><br>#";
				}

				if (arrResult[12].length() > 1 && arrResult[12].replaceAll("⊥", "").trim().equals("True")) {
					strSlowQueryBackup += "SlowQueryOffOn.sql(1)<br>";
				} else {
					strSlowQueryBackup += "<font Name = 'processDownCntFlag'>SlowQueryOffOn.sql(0)</font><br>#";
				}

				serverInfoResultVo.setSlowBackUp(strSlowQueryBackup);
			} else {
				if (arrResult[19].indexOf("S-EQUS") > 0) {
					serverInfoResultVo.setSlowBackUp("N/A");
				} else {
					serverInfoResultVo.setSlowBackUp(" ");
				}
			}

			/**
			 * 실행중인 프로세스 확인
			 **/
			if (arrResult[3].length() > 1) {
				String arrAllProcess[] = arrResult[3].substring(0, (arrResult[3].length() - 1)).split("⊥");

				/** arrResult [5] 카테고리 **/
				String arrRunProcess[] = null;
				for (int j = 0; j < serverInfoList.size(); j++) {
					if (serverInfoList.get(i).getServerCategory().equals(arrResult[16])) {
						arrRunProcess = serverInfoList.get(j).getRunProcess().split("⊥");
					}
				}

				/** arrResult [7] 카테고리 **/
				String[] arrRunProcessConfigCnt = null;
				arrRunProcessConfigCnt = arrResult[18].split("⊥");
				String strRunProcessCheck = "";

				for (int j = 0; j < arrRunProcess.length; j++) {
					int processCnt = 0;
					if (arrRunProcess[j].trim().isEmpty() != true) {
						for (int k = 0; k < arrAllProcess.length; k++) {
							if (arrAllProcess[k].trim().equals(arrRunProcess[j].trim())) {
								processCnt++;
							}
						}

						String arrProcessDown[] = null;
						
						
						if (arrResult[5].length() > 1 && arrRunProcess[j].trim().length() > 1) {
							arrProcessDown = arrResult[5].split("⊥");
							for (String strProcessDown : arrProcessDown) {
								if (strProcessDown.length() > 1) {
 
									if (strProcessDown.trim().toUpperCase().indexOf(arrRunProcess[j].trim().toUpperCase()) > -1) {
										if (arrRunProcess[j].equals("SmartLogTransfer") || arrRunProcess[j].equals("DataTrancefer") || arrRunProcess[j].equals("TamDataDetector")) {
											if (j == (arrRunProcess.length - 1)) {
												strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessAbnomalFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#";
											} else {
												strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessAbnomalFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#" + "<br>";
											}
										} else {
											if (j == (arrRunProcess.length - 1)) {
												strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessAbnomalFlag'>" + arrRunProcess[j].trim() + "(" + processCnt + "/"+ Integer.parseInt(arrRunProcessConfigCnt[j]) + ")"+ "</font>#";
											} else {
												strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessAbnomalFlag'>" + arrRunProcess[j].trim() + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")" + "</font>#" + "<br>";
											}
										}
									} else {
										if (arrRunProcess[j].equals("smartLogTransfer") || arrRunProcess[j].equals("DataTrancefer")) {
											if (j == (arrRunProcess.length - 1)) {
												strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")";
											} else {
												strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")" + "<br>";
											}
										} else if (arrRunProcess[j].equals("TMDataDetector")) {
											if (processCnt == 0) {
												if (j == (arrRunProcess.length - 1)) {
													strRunProcessCheck = strRunProcessCheck + "<font Name = 'processDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#";
												} else {
													strRunProcessCheck = strRunProcessCheck + "<font Name = 'processDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#" + "<br>";
												}
											} else {
												if (j == (arrRunProcess.length - 1)) {
													strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")";
												} else {
													strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")" + "<br>";
												}
											}
										} else {
											if (processCnt > Integer.parseInt(arrRunProcessConfigCnt[j])) {
												if (j == (arrRunProcess.length - 1)) {
													strRunProcessCheck = strRunProcessCheck+ "<font Name = 'ProcessUpCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#";
												} else {
													strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessUpCntFlag'>" + arrRunProcess[j]+ "(" + processCnt + "/"+ Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#"+ "<br>";
												}
											} else if (processCnt < Integer.parseInt(arrRunProcessConfigCnt[j])) {
												if (j == (arrRunProcess.length - 1)) {
													strRunProcessCheck = strRunProcessCheck+ "<font Name = 'ProcessDownCntFlag'>" + arrRunProcess[j]+ "(" + processCnt + "/"+ Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#";
												} else {
													strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#"+ "<br>";
												}
											} else {
												if (j == (arrRunProcess.length - 1)) {
													strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")";
												} else {
													strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")" + "<br>";
												}
											}
										}
									}
								}
							}
						} else {
							if (arrRunProcess[j].equals("smartLogTransfer") || arrRunProcess[j].equals("DataTrancefer")) {
								if (j == (arrRunProcess.length - 1)) {
									strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")";
								} else {
									strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")" + "<br>";
								}
							} else if (arrRunProcess[j].equals("TMDataDetector")) {
								if (processCnt == 0) {
									if (j == (arrRunProcess.length - 1)) {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'processDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#";
									} else {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'processDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + ")</font>#" + "<br>";
									}
								} else {
									if (j == (arrRunProcess.length - 1)) {
										strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")";
									} else {
										strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + ")" + "<br>";
									}
								}
							} else {
								if (processCnt > Integer.parseInt(arrRunProcessConfigCnt[j])) {
									if (j == (arrRunProcess.length - 1)) {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessUpCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#";
									} else {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessUpCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#" + "<br>";
									}
								} else if (processCnt < Integer.parseInt(arrRunProcessConfigCnt[j])) {
									if (j == (arrRunProcess.length - 1)) {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#";
									} else {
										strRunProcessCheck = strRunProcessCheck + "<font Name = 'ProcessDownCntFlag'>" + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")</font>#" + "<br>";
									}
								} else {
									if (j == (arrRunProcess.length - 1)) {
										strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")";
									} else {
										strRunProcessCheck = strRunProcessCheck + arrRunProcess[j] + "(" + processCnt + "/" + Integer.parseInt(arrRunProcessConfigCnt[j]) + ")" + "<br>";
									}
								}
							}
						}
					}
					// 여기까지 arrRunProcess[배열]도는 곳임
				}
				serverInfoResultVo.setRunProcess(strRunProcessCheck);
			} else {
				serverInfoResultVo.setRunProcess("N/A");
			}

			/** 서버 재부팅 시간 확인 **/
			if (arrResult[4].length() > 1) {
				if (arrResult[7].replaceAll("⊥", "").equals("True")) {
					arrResult[4] = "<font Name = 'rebootFlag'>" + arrResult[4].replaceAll("⊥", "") + "</font>#";
				}
				if (arrResult[8].length() > 1) {
					serverInfoResultVo.setLastBootUpTime(arrResult[4].replaceAll("⊥", "") + "("+ Integer.parseInt(arrResult[8].replaceAll("⊥", "")) + "일)");
				} else {
					serverInfoResultVo.setLastBootUpTime(arrResult[4].replaceAll("⊥", "") + "(N/A)");
				}
			} else {
				serverInfoResultVo.setLastBootUpTime("N/A");
			}
				
			if (arrResult[6].length() > 1) {
				serverInfoResultVo.setFailoverFlag(arrResult[6].replaceAll("⊥", " ").trim());
			} else {
				serverInfoResultVo.setFailoverFlag("N/A");
			}

			serverInfoResultVo.setServerCategory(arrResult[16]);
			serverInfoResultVo.setGroupName(arrResult[17]);

			// 추가 12.30 히스토리 추가를 위한
			serverInfoResultVo.setToolName(arrResult[19]);
			serverInfoResultVo.setOccuredTime(arrResult[20]);

			// 2016. 10. 20
			// ping check
			if (arrResult[13].length() > 1) {
				if (arrResult[13].replaceAll("⊥", "").equals("True")) {// 성공
					serverInfoResultVo.setPingState("pass");
				} else { // ping fail
					arrResult[13] = "fail#";
					serverInfoResultVo.setPingState(arrResult[13]);
				}
			} else if (arrResult[19].indexOf("S-EQUS") > 1) {
				arrResult[13] = "Null";
				serverInfoResultVo.setPingState(arrResult[12]);
			} else {
				arrResult[13] = "fail";
				serverInfoResultVo.setPingState(arrResult[13]);
			}
			// [//2016.10.20]

			// [//2016.10.29]
			serverInfoResultVo.setiOWriteOperationsPersec(arrResult[14].replaceAll("⊥", ""));
			serverInfoResultVo.setPageFaultsPerSec(arrResult[15].replaceAll("⊥", ""));
			// [//2016.10.29]

			ServerInfoResultList.add(serverInfoResultVo);
		}

		boolean rtn = batchDao.insertFileDb(filePath, tempPath, fileName, historyFileName, ServerInfoResultList);

		long time4 = System.currentTimeMillis();
		String parsingEndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

		log.debug("parsing end.");
		log.debug("종료 시간 :: " + parsingEndTime);
		log.debug("파싱 수핸 시간 :: " + (time4 - time3) / 1000.0);

		RemoteShellOutputVO.ServerInfoList.clear();

		System.gc();

		return rtn;

	}
}
