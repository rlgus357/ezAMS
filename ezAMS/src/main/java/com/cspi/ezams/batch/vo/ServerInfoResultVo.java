package com.cspi.ezams.batch.vo;

public class ServerInfoResultVo {

	private String ipInfo;
	private String hostInfo;
	private String cpuInfo;
	private String memoryInfo;
	private String diskInfo;
	private String arrLogicalDiskInfo[];
	private String arrNetWorkDiskInfo[];

	private String runProcess;
	private String lastBootUpTime;
	private String runProcessDownFlag;
	private String failoverFlag;
	
	private boolean localDiskCntFlag;
	private boolean networkDiskCntFlag;
	private int localDiskCnt;
	private int networkDiskCnt;
	private int diskConfigCnt;
	
	private String slowBackUp;
	private String pingState;
	private String iOWriteOperationsPersec;
	private String pageFaultsPerSec;

	private String serverCategory;
	private String groupName;
	private String toolName;
	private String occuredTime;

	public String getIpInfo() {
		return ipInfo;
	}

	public void setIpInfo(String ipInfo) {
		this.ipInfo = ipInfo;
	}

	public String getHostInfo() {
		return hostInfo;
	}

	public void setHostInfo(String hostInfo) {
		this.hostInfo = hostInfo;
	}

	public String getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}

	public String getMemoryInfo() {
		return memoryInfo;
	}

	public void setMemoryInfo(String memoryInfo) {
		this.memoryInfo = memoryInfo;
	}

	public String getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(String diskInfo) {
		this.diskInfo = diskInfo;
	}

	public String[] getArrLogicalDiskInfo() {
		return arrLogicalDiskInfo;
	}

	public void setArrLogicalDiskInfo(String[] arrLogicalDiskInfo) {
		this.arrLogicalDiskInfo = arrLogicalDiskInfo;
	}

	public String[] getArrNetWorkDiskInfo() {
		return arrNetWorkDiskInfo;
	}

	public void setArrNetWorkDiskInfo(String[] arrNetWorkDiskInfo) {
		this.arrNetWorkDiskInfo = arrNetWorkDiskInfo;
	}

	public String getRunProcess() {
		return runProcess;
	}

	public void setRunProcess(String runProcess) {
		this.runProcess = runProcess;
	}

	public String getLastBootUpTime() {
		return lastBootUpTime;
	}

	public void setLastBootUpTime(String lastBootUpTime) {
		this.lastBootUpTime = lastBootUpTime;
	}

	public String getRunProcessDownFlag() {
		return runProcessDownFlag;
	}

	public void setRunProcessDownFlag(String runProcessDownFlag) {
		this.runProcessDownFlag = runProcessDownFlag;
	}

	public String getFailoverFlag() {
		return failoverFlag;
	}

	public void setFailoverFlag(String failoverFlag) {
		this.failoverFlag = failoverFlag;
	}

	public boolean isLocalDiskCntFlag() {
		return localDiskCntFlag;
	}

	public void setLocalDiskCntFlag(boolean localDiskCntFlag) {
		this.localDiskCntFlag = localDiskCntFlag;
	}

	public boolean isNetworkDiskCntFlag() {
		return networkDiskCntFlag;
	}

	public void setNetworkDiskCntFlag(boolean networkDiskCntFlag) {
		this.networkDiskCntFlag = networkDiskCntFlag;
	}

	public int getLocalDiskCnt() {
		return localDiskCnt;
	}

	public void setLocalDiskCnt(int localDiskCnt) {
		this.localDiskCnt = localDiskCnt;
	}

	public int getNetworkDiskCnt() {
		return networkDiskCnt;
	}

	public void setNetworkDiskCnt(int networkDiskCnt) {
		this.networkDiskCnt = networkDiskCnt;
	}

	public String getSlowBackUp() {
		return slowBackUp;
	}

	public void setSlowBackUp(String slowBackUp) {
		this.slowBackUp = slowBackUp;
	}

	public String getPingState() {
		return pingState;
	}

	public void setPingState(String pingState) {
		this.pingState = pingState;
	}

	public String getiOWriteOperationsPersec() {
		return iOWriteOperationsPersec;
	}

	public void setiOWriteOperationsPersec(String iOWriteOperationsPersec) {
		this.iOWriteOperationsPersec = iOWriteOperationsPersec;
	}

	public String getPageFaultsPerSec() {
		return pageFaultsPerSec;
	}

	public void setPageFaultsPerSec(String pageFaultsPerSec) {
		this.pageFaultsPerSec = pageFaultsPerSec;
	}

	public String getServerCategory() {
		return serverCategory;
	}

	public void setServerCategory(String serverCategory) {
		this.serverCategory = serverCategory;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getOccuredTime() {
		return occuredTime;
	}

	public void setOccuredTime(String occuredTime) {
		this.occuredTime = occuredTime;
	}

	public int getDiskConfigCnt() {
		return diskConfigCnt;
	}

	public void setDiskConfigCnt(int diskConfigCnt) {
		this.diskConfigCnt = diskConfigCnt;
	}

}
