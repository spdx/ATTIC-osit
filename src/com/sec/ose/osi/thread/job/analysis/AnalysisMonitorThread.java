/**
 * Copyright(C) 2013-2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package com.sec.ose.osi.thread.job.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * AnalysisMonitorThread
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class AnalysisMonitorThread extends Thread {
	private static Log log = LogFactory.getLog(AnalysisMonitorThread.class);
	
	public static final int STATUS_READY = 0;
	public static final int STATUS_EXECUTING = 1;
	public static final int STATUS_CANCELLING = 2;
	public static final int STATUS_IDENTIFING = 3;
	public static final int NUM_OF_STATUS = 4;
	
	private final int SLEEP_INTERVAL = 1000; 
	
	private int analysisStatus = STATUS_READY;
	
	private long timeInterval = Long.MAX_VALUE;
	private long nextAnalysisTime = 0;
	private AnalyzeExecutionThread mAnalyzeTask = null;
	
	public AnalysisMonitorThread() {
		log.debug("Create Analysis Monitor Thread");
		setNextAnalysisTime();
	}
	
	public void setTimeInterval(long pTimeInterval) {
		timeInterval = pTimeInterval;
		setNextAnalysisTime();
	}
	
	private void setNextAnalysisTime() {
		if(timeInterval>0) {
			nextAnalysisTime = timeInterval + System.currentTimeMillis();
		}
	}
	
	public long getRemainTime() {
		return nextAnalysisTime - System.currentTimeMillis();
	}
	
	private boolean isContinue = true;
	public void stopThread() {
		this.isContinue = false;
	}
	
	public void run() {
		log.debug("Start Analysis Monitor");
		isContinue = true;
		while(isContinue) {
			
			try{
				sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				log.warn(e);
			}
			
			int analysisStatus = getStatus();
			
			switch (analysisStatus) {
			
				case STATUS_CANCELLING:
				case STATUS_IDENTIFING:
					break;
				
				case STATUS_EXECUTING:
					if(this.mAnalyzeTask == null || this.mAnalyzeTask.isDone() == true) {
						this.setStatus(STATUS_READY);
						break;
					}
					
					System.out.print(".");
					break;

				case STATUS_READY :
					if(getRemainTime() <= 0) {
						
						ArrayList<OSIProjectInfo> projectInfoLists = ManageMediator.getInstance().getAnalysisProjects();
						log.debug("STATUS_READY - projectInfoLists.size() : "+projectInfoLists.size());
						
						if(projectInfoLists != null && projectInfoLists.size() > 0){
							startAnalysis();
						} else {
							ManageMediator.getInstance().appendMessageToConsole("It is analysis time now, but there is no analysis target project.");
							ManageMediator.getInstance().refreshMonitorInterval();
						}
						
					} else {
						ManageMediator.getInstance().setStatusText("Remaining Time: "+DateUtil.translateTimeFormatToColon(getRemainTime()));
					}
					
					break;
			}

		}
	}
	
	public synchronized int getStatus() {
		return analysisStatus;
	}
	
	public synchronized void setStatus(int analysisStatus) {
		if(analysisStatus == STATUS_READY) setNextAnalysisTime();
		this.analysisStatus = analysisStatus;
	}
	
	public synchronized boolean chkStatus(int chkStat) {
		return (analysisStatus == chkStat);
	}
	
	public void requestAnalysisNow() {
		startAnalysis();
	}
	
	private void startAnalysis() {
		
		setStatus(STATUS_EXECUTING);
		log.debug("AnalysisMonitorThread STATUS : "+getStatus());
		
		ManageMediator.getInstance().updateUIForStartAnalysis();
		ManageMediator.getInstance().setStatusText("analyzing...");
		
		mAnalyzeTask = new AnalyzeExecutionThread(
				ManageMediator.getInstance().getAnalysisProjects(),
				ManageMediator.getInstance()
						);
		
		mAnalyzeTask.addPropertyChangeListener(
					new PropertyChangeListener() {
			         public  void propertyChange(PropertyChangeEvent evt) {
			             if ("progress".equals(evt.getPropertyName())) {
			            	 ManageMediator.getInstance().setProgressValue((Integer)evt.getNewValue());
			             }
			         }
			    });
				
		mAnalyzeTask.execute();
	}

	public void cancelAnalysis() {
		if(mAnalyzeTask != null) {
			mAnalyzeTask.cancel(true);
		}
	}
}
