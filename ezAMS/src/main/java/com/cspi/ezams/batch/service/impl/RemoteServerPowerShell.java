
package com.cspi.ezams.batch.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cspi.ezams.batch.vo.RemoteShellOutputVO;


class ServerGobbler implements Runnable {

	protected Log log = LogFactory.getLog(this.getClass());

	private String message;
	private BufferedReader reader;

	protected String serverCategory;
	protected String groupName;
	protected String toolName;
	protected String occuredTime;
	protected String runProcessCnt;

	public ServerGobbler(InputStream inputStream, String serverCategory, String groupName, String runProcessCnt,String toolName, String occuredTime) {

		this.serverCategory = serverCategory;
		this.groupName = groupName;
		this.runProcessCnt = runProcessCnt;
		this.toolName = toolName;
		this.occuredTime = occuredTime;

		try {
			this.reader = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
			this.message = (null != message) ? message : "";
		} catch (UnsupportedEncodingException e) {
			log.debug(e);
		}
	}

	public void run() {
		String line;
		StringBuffer strResult = new StringBuffer();

		try {
			while (null != (line = this.reader.readLine())) {
				strResult.append(line + "กั");
				//log.debug("run Result :: "+line);
				
			}
			strResult.append("&" + serverCategory + "&" + groupName + "&" + runProcessCnt + "&" + toolName + "&" + occuredTime);
			RemoteShellOutputVO.ServerInfoList.add(strResult.toString());
			this.reader.close();
			strResult.delete(0, strResult.toString().length());
		} catch (IOException e) {
			log.debug("ERROR :" + e.getMessage());
		}
	}
}

class ErrGlobbler implements Runnable {

	protected Log log = LogFactory.getLog(this.getClass());

	private String message;
	private BufferedReader reader;

	public ErrGlobbler(InputStream inputStream) {
		try {
			this.reader = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
			this.message = (null != message) ? message : "";
		} catch (UnsupportedEncodingException e) {
			log.debug(e);
		}
	}

	public void run() {
		String line;
		try {
			while (null != (line = this.reader.readLine())) {
				log.debug("SHELL ERROR:" + line);
			}
			this.reader.close();
		} catch (IOException e) {
			log.debug("ERROR: " + e.getMessage());
		}
	}
}

public class RemoteServerPowerShell {

	protected Log log = LogFactory.getLog(this.getClass());

	private ProcessBuilder pb;
	Process p;
	boolean closed = false;
	PrintWriter writer;

	public RemoteServerPowerShell(String[] commandList, String serverCategory, String groupName, String runProcessCnt,
			String toolName, String occuredTime) throws InterruptedException {
		pb = new ProcessBuilder(commandList);
		try {
			p = pb.start();
		} catch (IOException ex) {
			throw new RuntimeException("Cannot execute PowerShell.exe", ex);
		}
		writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(p.getOutputStream())));

		ServerGobbler outGobbler = new ServerGobbler(p.getInputStream(), serverCategory, groupName, runProcessCnt,toolName, occuredTime);
		ErrGlobbler errGobbler = new ErrGlobbler(p.getErrorStream());
		
		Thread outThread = new Thread(outGobbler);
		Thread errThread = new Thread(errGobbler);

		outThread.start();
		errThread.start();

	}

	public void execute(String command) {
		if (!closed) {
			writer.println(command);
			writer.flush();
		} else {
			throw new IllegalStateException("Power console has ben closed.");
		}
	}

	public void close() {
		try {
			execute("exit");
			p.waitFor();
		} catch (InterruptedException ex) {
		}
	}
}
