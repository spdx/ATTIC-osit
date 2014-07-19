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

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.AnalysisPhase;
import com.blackducksoftware.sdk.protex.common.AnalysisStatus;
import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.ProjectAnalysisInfo;
import com.sec.ose.osi.sdk.SDKInterfaceImpl;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.codetree.CodeTreeAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.data.IdentifiedController;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.manage.DirectoryInfo;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;

/**
 * AnalyzeExecutionThread
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class AnalyzeExecutionThread extends SwingWorker<String, String> {
	private static Log log = LogFactory.getLog(AnalyzeExecutionThread.class);
	
	ManageMediator mediator;
	
	public AnalyzeExecutionThread (
			ArrayList<OSIProjectInfo> projectLists,
			ManageMediator mediator
			) {
		this.projectList = projectLists;
		this.mediator = mediator;
	}
	
	ArrayList<OSIProjectInfo> projectList;
	private String projectName = null;
	private String projectID = null;
	private int totalFileCnt = 0;

	@Override
	public String doInBackground() throws InterruptedIOException {
		try {
			for(OSIProjectInfo projectInfo: projectList) {
				projectName = projectInfo.getProjectName();
				mediator.setProjectAnalysisStatus(projectName,ProjectAnalysisInfo.STATUS_PROCESSING);
				projectID = SDKInterfaceImpl.getInstance().getProjectID(projectName);
				
				log.debug("Analysis ProjectName : "+projectName+" , projectID : "+projectID);

				mediator.setStatusText("Checking "+ projectName + " Directory ...");
				if(checkTargetDirChanged(projectInfo)) {
					totalFileCnt = projectInfo.getTotalFileCount() + 2;	// add 1 for Root Directory , add 1 for Finish
					setProgress(0);
					mediator.setStatusText("Analyzing "+ projectName + " ...");
					mediator.setProgressBarVisible(true);
					publish("<-- Start to analyze Project ["+projectName+"] total " + totalFileCnt + " files -->");
				} else {
					mediator.setProjectAnalysisStatus(projectName,ProjectAnalysisInfo.STATUS_COMPLETE);
					publish("<-- Project ["+projectName+"] is not Changed -->");
					continue;
					
				}
				
				IdentifiedController.setProjectStatus(projectName, AnalysisMonitorThread.STATUS_EXECUTING);
				ProjectAPIWrapper.analyzeProject(projectID);
				
				publish("Start assessing "+projectInfo.getSourcePath());
				publish("Analyzing project "+projectName+" ("+projectID+")");
				
	            int totalFileCount = -1;
	            AnalysisPhase prePhase = null;
	            int PrePhasePercent = -1;
				int curProgress = 0;
		        boolean finished = false;
		        while (!finished) {
		            try {
		                Thread.sleep(100);
		            } catch (InterruptedException e) {
		                System.err.println(e.getMessage());
		            }
		            try {
		            	// TODO: if Source Location is changed, then have to change "AnalysisSourceLocation"
		            	
			            AnalysisStatus status = ProtexSDKAPIManager.getProjectAPI().getAnalysisStatus(projectID);
		            	AnalysisPhase currPhase = status.getAnalysisPhase();
		                int currPhasePercent = status.getCurrentPhasePercentCompleted();
		                if(prePhase != currPhase || PrePhasePercent != currPhasePercent) {
		                	mediator.setStatusText(currPhase + " (" + currPhasePercent + "%)");
			            	if(currPhase == AnalysisPhase.INITIALIZING || currPhase == AnalysisPhase.ASSESSING) {
			            	} else if(currPhase == AnalysisPhase.SCANNING) {
			            		curProgress = (int)(currPhasePercent * 0.5);
			            		if(totalFileCount==-1) {
			            			totalFileCount = status.getTotalFileCount();
			            			publish("Files pending identification: " + totalFileCount);
			            		}
								StringBuffer sb = new StringBuffer();
								sb.append(currPhase).append(" (").append(currPhasePercent).append("%) - ").append(status.getCurrentFile()).append(" ("+status.getAnalyzedFileCount()).append("/").append(totalFileCount).append(")");
			            		mediator.setStatusText(sb.toString());
			            	} else if(currPhase == AnalysisPhase.ANALYZING) {
			            		if(prePhase != AnalysisPhase.SCANNING) {	// After Scanning, still 100% 
			            			curProgress = 50 + (int)(currPhasePercent * 0.5);
			            		}
			            	}
							setProgress(curProgress);
			            	
		                }
		                
		                prePhase = currPhase;
		                PrePhasePercent = currPhasePercent;
		                
		                finished = status.isFinished();
		                
		            } catch (SdkFault e) {
		                System.err.println(e.getMessage());
		                e.printStackTrace();
		            }
		        }
		        curProgress = 100;
				setProgress(curProgress);
				
				if(finished) {
					
					mediator.setStatusText("("+totalFileCnt+"/"+totalFileCnt+") "+"analyzed sucessfully and will identify string search automatically.");
					setProgress(100);
					
					IdentifyMediator.getInstance().refreshComboProjectName(projectName);
					
					projectInfo.updateAnalysisSuccessInfo();

					mediator.setProjectAnalysisStatus(projectName,ProjectAnalysisInfo.STATUS_COMPLETE);
					IdentifiedController.setProjectStatus(projectName, AnalysisMonitorThread.STATUS_READY);
					projectInfo.setSourcePathChange(false);
					publish("<-- Finish project ["+projectName+"] Analyze -->");

					log.debug("refesh loaded project START...");
					ProjectDiscoveryControllerMap.loadProjectDiscoveryControllerFromProtexServer(projectName,null);
					CodeTreeAPIWrapper.refreshCodeTree(projectName, null);
					log.debug("refesh loaded project END...");
					
					mediator.showPopupFrame("Finish project ["+projectName+"] Analyze");
					
				} else {
					mediator.setProjectAnalysisStatus(projectName,ProjectAnalysisInfo.STATUS_READY);
					IdentifiedController.setProjectStatus(projectName, AnalysisMonitorThread.STATUS_READY);
					publish("<-- Error project ["+projectName+"] Analyze -->");
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
		return "Analysis Completed";
	}

	@Override
	protected void process(List<String> progress) {
		for(String tmpstr:progress) {
			mediator.appendMessageToConsole(tmpstr);
		}
	}

	@Override
	protected void done() {
		if(isCancelled()) {
			if(projectID != null) ProjectAPIWrapper.cancelAnalyzeProject(projectID);
			mediator.appendMessageToConsole("<-- Project ["+projectName+"] Analysis is Cancelled -->");
			mediator.setStatusText("Analysis is Cancelled");
		} else {
			mediator.setStatusText("Analysis is Done");
		}
		mediator.setProgressBarVisible(false);
		AnalysisMonitorThread monitor = BackgroundJobManager.getInstance().startAnalysisMonitoThread();
		monitor.setStatus(AnalysisMonitorThread.STATUS_READY);
		mediator.updateUIForFinishAnalysis();
	}
	
	private boolean checkTargetDirChanged(OSIProjectInfo projectInfo) {
		DirectoryInfo di = new DirectoryInfo(projectInfo.getSourcePath());
		long lastAnalysisDate = projectInfo.getLastAnalyzedDate();

		if(di.isFileModified(lastAnalysisDate) || projectInfo.isSourcePathChange() ) {
			projectInfo.getProjectAnalysisInfo().setTotalFileCount(di.getFileCount());
			
			if(IdentifiedController.getProjectStatus(projectName) == AnalysisMonitorThread.STATUS_IDENTIFING) {
				log.debug("\nProject ["+projectName+"] is identifing !!!\n");
				for(int i=0;i<10;i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						log.warn(e);
					}
					if(IdentifiedController.getProjectStatus(projectName) == AnalysisMonitorThread.STATUS_READY) {
						return true;
					}
				}
				return false;
			}
		} else {
			log.debug("\nProject ["+projectName+"] is not Changed !!!\n");
			return false;
		}
		return true;
	}
}