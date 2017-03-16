package com.cspi.ezams.batch.vo;

public class ServerInfoVO {

	private String serverCategory;
	private String toolName;
	private String groupName;
	private String serverIp;
	private String serverId;
	private String serverPassWd;
	private String serverCheckInfo;
	private String runProcess;
	private String runProcessCnt;
	private String occuredTime;
	private String failoverConfigFlag;
	private int localDiskCnt;
	private int networkDiskCnt;
	private String taskSlowBackUpPath;
	private String slowBackUpPath;

	public String getServerCategory() {
		return serverCategory;
	}

	public void setServerCategory(String serverCategory) {
		this.serverCategory = serverCategory;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerPassWd() {
		return serverPassWd;
	}

	public void setServerPassWd(String serverPassWd) {
		this.serverPassWd = serverPassWd;
	}

	public String getServerCheckInfo() {
		return serverCheckInfo;
	}

	public void setServerCheckInfo(String serverCheckInfo) {
		this.serverCheckInfo = serverCheckInfo;
	}

	public String getRunProcess() {
		return runProcess;
	}

	public void setRunProcess(String runProcess) {
		this.runProcess = runProcess;
	}

	public String getRunProcessCnt() {
		return runProcessCnt;
	}

	public void setRunProcessCnt(String runProcessCnt) {
		this.runProcessCnt = runProcessCnt;
	}

	public String getOccuredTime() {
		return occuredTime;
	}

	public void setOccuredTime(String occuredTime) {
		this.occuredTime = occuredTime;
	}

	public String getFailoverConfigFlag() {
		return failoverConfigFlag;
	}

	public void setFailoverConfigFlag(String failoverConfigFlag) {
		this.failoverConfigFlag = failoverConfigFlag;
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

	public String getTaskSlowBackUpPath() {
		return taskSlowBackUpPath;
	}

	public void setTaskSlowBackUpPath(String taskSlowBackUpPath) {
		this.taskSlowBackUpPath = taskSlowBackUpPath;
	}

	public String getSlowBackUpPath() {
		return slowBackUpPath;
	}

	public void setSlowBackUpPath(String slowBackUpPath) {
		this.slowBackUpPath = slowBackUpPath;
	}

	@Override
	public String toString() {
		return "ServerInfoVO [serverCategory=" + serverCategory + ", toolName=" + toolName + ", groupName=" + groupName
				+ ", serverIp=" + serverIp + ", serverId=" + serverId + ", serverPassWd=" + serverPassWd
				+ ", serverCheckInfo=" + serverCheckInfo + ", runProcess=" + runProcess + ", runProcessCnt="
				+ runProcessCnt + ", occuredTime=" + occuredTime + ", failoverConfigFlag=" + failoverConfigFlag
				+ ", localDiskCnt=" + localDiskCnt + ", networkDiskCnt=" + networkDiskCnt + ", taskSlowBackUpPath="
				+ taskSlowBackUpPath + ", slowBackUpPath=" + slowBackUpPath + ", getServerCategory()="
				+ getServerCategory() + ", getToolName()=" + getToolName() + ", getGroupName()=" + getGroupName()
				+ ", getServerIp()=" + getServerIp() + ", getServerId()=" + getServerId() + ", getServerPassWd()="
				+ getServerPassWd() + ", getServerCheckInfo()=" + getServerCheckInfo() + ", getRunProcess()="
				+ getRunProcess() + ", getRunProcessCnt()=" + getRunProcessCnt() + ", getOccuredTime()="
				+ getOccuredTime() + ", getFailoverConfigFlag()=" + getFailoverConfigFlag() + ", getLocalDiskCnt()="
				+ getLocalDiskCnt() + ", getNetworkDiskCnt()=" + getNetworkDiskCnt() + ", getTaskSlowBackUpPath()="
				+ getTaskSlowBackUpPath() + ", getSlowBackUpPath()=" + getSlowBackUpPath() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	

}
