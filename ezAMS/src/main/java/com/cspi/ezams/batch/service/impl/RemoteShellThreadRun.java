package com.cspi.ezams.batch.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cspi.ezams.batch.vo.ServerInfoVO;

public class RemoteShellThreadRun extends Thread {

	protected Log log = LogFactory.getLog(this.getClass());

	protected int seq;
	protected String remoteIp;
	protected String serverCategory;
	protected String groupName;
	protected String serverIp;
	protected String serverId;
	protected String serverPassWd;
	protected String serverCheckInfo;
	protected String runProcess;
	protected String runProcessCnt;
	protected String toolName;
	protected String occuredTime;
	protected String failoverConfigFlag;
	protected String taskSlowBackUpPath;
	protected String slowBackUpPath;
	protected int processDownTimeout;
	protected int rebootTimeout;
	protected int failoverTimeout;

	public RemoteShellThreadRun(int seq, String remoteIp, String slowBackUpPath, String taskSlowBackUpPath,int processDownTimeout, int rebootTimeout, int failoverTimeout, ServerInfoVO serverInfoVO) {

		this.seq = seq;
		this.remoteIp = remoteIp.replace("^", "");
		this.serverCategory = serverInfoVO.getServerCategory();
		this.groupName = serverInfoVO.getGroupName();
		this.serverIp = serverInfoVO.getServerIp();
		this.serverId = serverInfoVO.getServerId();
		this.serverPassWd = serverInfoVO.getServerPassWd();
		this.serverCheckInfo = serverInfoVO.getServerCheckInfo();
		this.runProcess = serverInfoVO.getRunProcess();
		this.runProcessCnt = serverInfoVO.getRunProcessCnt();
		this.toolName = serverInfoVO.getToolName();
		this.occuredTime = serverInfoVO.getOccuredTime();
		this.failoverConfigFlag = serverInfoVO.getFailoverConfigFlag();
		this.rebootTimeout = rebootTimeout;
		this.processDownTimeout = processDownTimeout;
		this.failoverTimeout = failoverTimeout;
		this.taskSlowBackUpPath = taskSlowBackUpPath;
		this.slowBackUpPath = slowBackUpPath;

	}

	public void run() {
		log.debug(this.seq + " thread start.");
  
		try { 
			String[] arrCheckInfo = serverCheckInfo.split("⊥");
			String[] commandList ={"powershell.exe", "-NoExit", "-Command", "-"};
			RemoteServerPowerShell pc = new RemoteServerPowerShell(commandList, serverCategory, groupName,runProcessCnt, toolName, occuredTime);

			/*
			 * 
			pc.execute("Set-Item WSMan:₩₩localhost₩₩Client₩₩TrustedHosts * -Force");
			pc.execute("$MyPassword = '" + serverPassWd + "' | ConvertTo - SecureString");
			pc.execute("$ObjectTypeName = 'System.Management.Automation.PsCredential'");
			pc.execute("$MyCredential = New-Object -TypeName $ObjectTypeName -ArgumentList '" + serverId+ "', $MyPassword");
		
			
			ORIGINAL ::>>invoke-command -ComputerName '" + serverIp+ "' -Credential $MyCredential -command
			CHANGE  ::>>>invoke-command -Command
			DELETE ::>>> -ComputerName  '" + serverIp+ "' -Credential $MyCredential 
			//propose :: Local Testing
			 */			

			for (int i = 0; i < arrCheckInfo.length; i++) {
				switch (arrCheckInfo[i]) {
				case "Ip":
					if (groupName.substring(0, 1).equals("N")) {
						pc.execute("$ServerIpInfo = invoke-command -command{ Get-WmiObject -Class Win32_NetworkAdapterConfiguration -Filter 'IPEnabled = True' | select IPAddress | select -Last 1 }");
					} else {
						pc.execute("$ServerIpInfo = invoke-Command -Command{ Get-WmiObject -class Win32_NetworkAdapterConfiguration -Filter 'IPEnabled = True' | select IPAddress | select -First 1 }");
					}
					pc.execute("'ipInfo^' + $ServerIpInfo.IPAddress");
					break;
				case "HostName":
					pc.execute("$ServerHostName = invoke-command -command{hostName}");
					pc.execute("'hostInfo^' + $ServerHostName");
					break;
				case "Cpu":
					pc.execute("$ServerCpuInfo = invoke-command -command{Get-WmiObject win32_processor | Measure-Object -property LoadPercentage -Average | Select Average}");
					pc.execute("'cpuInfo^'+ $ServerCpuInfo.Average + '%'");
					break;
				case "Memory":
					pc.execute("$ServerMemoryInfo = invoke-command -command{gwmi -class win32_operatingsystem | Select-Object @{Name = 'MemoryUsage'; Expression = { (( $_.TotalVisibleMemorySize - $_.FreePhysicalMemory)*100/ $_.TotalVisibleMemorySize) -as[int]}}}");
					pc.execute("'memoryInfo^' + $ServerMemoryInfo.MemoryUsage + '%'");
					break;
				case "Disk":
					pc.execute("'&'");
					pc.execute("invoke-command -command{Get-WmiObject win32_LogicalDisk -Filter 'DriveType=3 or DriveType=4' | select DeviceID, DriveType,ProviderName, FreeSpace, Size} |Format-Table -AutoSize @{Label='DiskDrive';Expression={$_.VolumeName + '(' + $_.DeviceID + ')'}}, @{Label='Size';Expression={($_.Size/1gb) -as[int]};FormatString='@{0:N1}G@'}, @{Label='Used' ; Expression = {( $_.Size/1gb) - ( $_.FreeSpace/1gb) -as[int]};FormatString='{0:N1}G@'}, @{Label='Avail';Expression ={($_.FreeSpace/1gb) -as[int]};FormatString='{0:N1}G@'}, @{Label='Use%';Expression={(($_.Size/1gb)-($_.FreeSpace/1gb))/($_.Size/1gb)*100 -as[int]}; FormatString='#{0:N1}%*@'}, @{Label='Provider' ; Expression={$_.ProviderName}};");
					pc.execute("'&'");
					pc.execute("invoke-command -Command{get-wmiobject win32_MappedLogicalDisk | select DeviceID, DriveType, ProviderName, FreeSpace, Size} | Format-Table -AutoSize @{Label='DiskDrive';Expression={$_.VolumeName + '(' + $_.DeviceID + ')'}}, @{Label='Size';Expression={($_.Size/1gb) -as[int]};FormatString='@{0:N1}G@'}, @{Label='Used';Expression= {($_.Size/1gb) - ($_.FreeSpace/1gb) -as[int]};FormatString='{0:N1}G@'}, @{Label='Avail';Expression ={($_.FreeSpace/1gb) -as[int]};FormatString='{0:N1}G@'}, @{Label='Use%';Expression={(($_.Size/1gb)-($_.FreeSpace/1gb))/($_.Size/1gb)*100 -as[int]};FormatString='#{0:N1}%*@'}, @{Label='ProviderName';Expression={$_.ProviderName}}");
					break;
				}
			}
			
			/** 실행중인 프로세스 확인 **/
			pc.execute("'&'");
			pc.execute("invoke-command -Command{get-process | select ProcessName}");

			/** 서버 재부팅 시간 확인 **/
			pc.execute("'&'");
			pc.execute("$ServerBootInfo = invoke-command -Command{Get-WmiObject Win32_operatingsystem | select csname, @{Label = 'LastBootUpTime';Expression={$_.ConverttoDateTime($_.lastbootuptime)}}}");
			pc.execute("$ServerBootInfo.LastBootUpTime");

			/** 서버 프로세스 다운 확인 **/
			pc.execute("'&'");
			pc.execute("$abnomalEventMessage = invoke-command -command{ get-eventLog -LogName Application* -After (get-date).addMinutes(-'"+ processDownTimeout+ "') -EntryType Error -Message *.exe* -ErrorAction SilentlyContinue | Select-String -inputObject {-split $_.message} -pattern '.exe' }");
			pc.execute("if([String]::IsNullOrEmpty($abnomalEventMessage)){}else{$abnomalProcessMessage = $abnomalEventMessage -split '.exe'}");
			pc.execute("if([String]::IsNullOrEmpty($abnomalProcessMessage)){}else{$abnomalProcessName = $abnomalProcessMessage[0] -split ':'}");
			pc.execute("if([String]::IsNullOrEmpty($abnomalProcessName)){}else{$abnomalProcessName[1]}");

			/** 서버 페일오버 한시간 이내 확인 **/
			pc.execute("'&'");
			if (failoverConfigFlag.equals("Y")) {
				pc.execute("$failoverflag = invoke-command -command{get-winEvent -filterHashTable @{LogName = 'System'; ProviderName='Microsoft-Windows-FailoverClustering';Level=2; StartTime=(get-date).addMinutes(-'"+ failoverTimeout + "')} -ErrorAction SilentlyContinue | select TimeCreated }");
			}
			pc.execute("if([String]::IsNullOrEmpty($failoverflag)){'EMPTY'}else{$failoverflag}");

			/** 서버 재부팅 시간 30분이내 확인 **/
			pc.execute("'&'");
			pc.execute("invoke-command -command{ ((get-date) - (get-wmiobject -class win32_operatingSystem).converttodateTime((get-wmiobject -class win32_operatingSystem).lastbootuptime)).TotalMinutes -lt '"
					+ rebootTimeout + "'}");

			/** 서버 재부팅 시간 경과일 계산 **/
			pc.execute("'&'");
			pc.execute("invoke-Command -command{ ((get-date) - (get-wmiobject -class win32_operatingSystem).converttodateTime((get-wmiobject -class win32_operatingSystem).lastbootuptime)).Days}");

			/** S-Sequs 이벤트 스케줄러 확인 **/
			pc.execute("'&'");
			if (toolName.indexOf("S-EQUS") > -1) {
				pc.execute("invoke-command -command{Test-Path -Path '" + taskSlowBackUpPath + "SlowQueryBackUp'}");
			}

			/** S-SEQUS SlowQuery BackUp 설정 필수 파일 존재 확인 **/
			pc.execute("'&'");
			if (toolName.indexOf("S-EQUS") > -1) {
				pc.execute("invoke-command -command{Test-Path -Path '" + taskSlowBackUpPath + "SlowQueryBackUp.bat'}");
			}
			/** S-SEQUS SlowQuery BackUp 설정파일 존재 확인 **/
			pc.execute("'&'");
			if (toolName.indexOf("S-EQUS") > -1) {
				pc.execute("invoke-command -command{Test-Path -Path '" + taskSlowBackUpPath + "SlowQueryBackUp.exe'}");
			}

			/** S-SEQUS SlowQuery BackUp 설정파일 존재 확인 **/
			pc.execute("'&'");
			if (toolName.indexOf("S-EQUS") > -1) {
				pc.execute("invoke-command -command{Test-Path -Path '" + taskSlowBackUpPath + "SlowQueryOffOn.sql'}");
			}
			/** S-EQUS 제외한 나머지 Ping Check **/
			pc.execute("'&'");
			if (toolName.indexOf("S-SEQUS") > -1) {
				pc.execute("invoke-command -Command{'test-connection -Quiet -count 3 -BufferSize 32 -ComputerName '" + serverIp + "' ");
			}

			/** DISK IO **/
			pc.execute("'&'");
			pc.execute("invoke-command -Command{ (Get-WmiObject Win32_PerfFormattedData_PerfProc_Process | Where-Object { $_.Name -eq '_Total' }).IOWriteOperationsPersec}");

			/** page fault **/
			pc.execute("'&'");
			pc.execute("invoke-command -Command{(Get-WmiObject Win32_PerfFormattedData_PerfOS_memory).PageFaultsPerSec}");

			/** 원격 접속 한 서버 연결 끊기 **/
			pc.close();

		} catch (Exception e) {
			log.debug(e.toString());
		}
		log.debug(this.seq + " thread end.");
	}
}