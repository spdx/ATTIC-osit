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
package com.sec.ose.osi.thread.ui_related;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.report.standard.data.ProjectInfoForIdentifyReport;
import com.sec.ose.osi.sdk.SDKInterfaceImpl;
import com.sec.ose.osi.sdk.protexsdk.codetree.CodeTreeAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReport;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReportFactory;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.IdentifyThread;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.cache.CacheableMgr;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.dialog.setting.UEProxySetting;
import com.sec.ose.osi.ui.dialog.setting.UEReportProperty;
import com.sec.ose.osi.ui.dialog.showProjectInfo.UEProjectInfo;
import com.sec.ose.osi.ui.frm.login.UELogin;
import com.sec.ose.osi.ui.frm.main.JFrmMain;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.UEComboProjectName;
import com.sec.ose.osi.ui.frm.main.identification.UEIdentifyResetComment;
import com.sec.ose.osi.ui.frm.main.identification.autoidentify.SPDXAutoIdentifyController;
import com.sec.ose.osi.ui.frm.main.identification.autoidentify.UESPDXAutoIdentify;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;
import com.sec.ose.osi.ui.frm.main.manage.UEProjectClone;
import com.sec.ose.osi.ui.frm.main.manage.UEProjectCreate;
import com.sec.ose.osi.ui.frm.main.manage.UEProtexProjectInfo;
import com.sec.ose.osi.ui.frm.main.manage.dialog.UEProtexSplit;
import com.sec.ose.osi.ui.frm.main.report.ReportMediator;
import com.sec.ose.osi.ui.frm.main.report.UEBOM;
import com.sec.ose.osi.ui.frm.main.report.UEBothBOM;
import com.sec.ose.osi.ui.frm.main.report.UEProjectName;
import com.sec.ose.osi.ui.frm.main.report.UESPDXBOM;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.ProjectSplitInfo;
import com.sec.ose.osi.util.tools.ProjectSplitUtil;

/**
 * UserCommandExecutionThread
 * @author suhyun47.kim, sjh.yoo, hankido.lee, ytaek.kim
 * 
 */
public class UserCommandExecutionThread implements Runnable {
	private static Log log = LogFactory.getLog(UserCommandExecutionThread.class);
	
	private int 				mRequestCode;	// for request identify
	private UIEntity 			mEntity;		// for additional data for handling user request filled by UI
	private UIResponseObserver 	mObserver = new DefaultUIResponseObserver();
	private UserCommandExecutionMonitorThread		mMonitorThread;
	
	private boolean				isDone = false;
	
	private SDKInterfaceImpl  mSDKInterface;
	
	public UserCommandExecutionThread(int requestCode, UIEntity entity, UIResponseObserver observer) {
		super();
		mRequestCode = requestCode;
		mEntity = entity;
		mObserver = observer;
		
		mSDKInterface   = UISDKInterfaceManager.getSDKInterface();
		
	}
	
	

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();		
		log.info(UserRequestHandler.getCommandName(mRequestCode)+" - start");
		
		isDone = false;
		
		this.mObserver.pushMessage("Preparing to request to server");
		Property prop = Property.getInstance();
		switch(mRequestCode) {
			
			case UserRequestHandler.DELETE_IDENTIFICATION_TABLE:
				if(IdentifyQueue.getInstance().size() > 0) {
					return;
				}
				
				Collection<OSIProjectInfo> infoList = OSIProjectInfoMgr.getInstance().getAllProjects();
				for(OSIProjectInfo info : infoList) {
					
					if(info.getProjectName().equals(IdentifyMediator.getInstance().getSelectedProjectName())) {
						continue;
					}
					
					if(info.isManaged() == true) {
						continue;
					}
					
					mObserver.setMessageHeader("Deleting identification table...\n");
					mObserver.pushMessageWithHeader(" > target project : "+info.getProjectName()+"\n");
					
					// Drop identification tables
					IdentificationDBManager.dropTable(info.getProjectName());
					
					// Remove memory
					ProjectDiscoveryControllerMap.removeProjectDiscoveryController(info.getProjectName());
				}
				break;
			
			case UserRequestHandler.GET_PROTEX_PROJECT_INFO:
				UEProtexProjectInfo  uap = (UEProtexProjectInfo) mEntity;
				OSIProjectInfo protexProjectInfo = UISDKInterfaceManager.getSDKInterface().getProjectInfoByName(uap.getProjectName());
				this.mObserver.setReturnValue(protexProjectInfo);
				this.mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
				
				break;

			case UserRequestHandler.LOAD_IDENTIFICATION_DATA:
				{
					UEComboProjectName uei = (UEComboProjectName) mEntity;
					
					String projectName = uei.getProjectName();
					log.info("loading project [" + projectName + "]");
					
					mObserver.setMessageHeader("Loading Identification Data ...\n");
					
					// Local Component Loading
					mObserver.pushMessageWithHeader(" > Loading local component info ...\n");
					ComponentAPIWrapper.loadLocalComponent(ProjectAPIWrapper.getProjectID(projectName), false);
					
					ProjectDiscoveryControllerMap.loadProjectDiscoveryController(projectName,this.mObserver);
					CodeTreeAPIWrapper.setCodeTree(projectName,this.mObserver);
					
					
					log.debug("Selected Project : "+projectName);

					mObserver.pushMessageWithHeader(" > Loading sql dat to memory ...\n");
					AbstractDiscoveryController stringMatchDiscovery = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE);		
					AbstractDiscoveryController codeMatchDiscovery = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE);
					AbstractDiscoveryController patternMatchDiscovery = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE);
					
					int displayedMatchedType=IdentificationConstantValue.STRING_MATCH_TYPE;
					
					if(stringMatchDiscovery.getNumOfPendingFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.STRING_MATCH_TYPE;
					} else if(codeMatchDiscovery.getNumOfPendingFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.CODE_MATCH_TYPE;
					} else if(patternMatchDiscovery.getNumOfPendingFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.PATTERN_MATCH_TYPE;
					} else if(stringMatchDiscovery.getNumOfDiscoveryFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.STRING_MATCH_TYPE;
					} else if(codeMatchDiscovery.getNumOfDiscoveryFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.CODE_MATCH_TYPE;
					} else if(patternMatchDiscovery.getNumOfDiscoveryFiles() > 0) {
						displayedMatchedType = IdentificationConstantValue.PATTERN_MATCH_TYPE;
					}
					
					mObserver.pushMessageWithHeader(" > Updating Panel ...\n");
					IdentifyMediator.getInstance().changeSelectedIdentificationPanel(displayedMatchedType);
					
					// Tree & List Identification
					mObserver.pushMessageWithHeader(" > Updating Tree and List ...\n");
					IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, displayedMatchedType);
					
					
					this.mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
				}
				break;
				
			case UserRequestHandler.SYNC_TO_SERVER:
				{
					syncToServer();
				}
				break;
				
				
			case UserRequestHandler.SYNC_FROM_SERVER:
				{
					syncFromServer();
				}
				break;

			case UserRequestHandler.GET_ALL_PROJECT_NAMES:
				{
					ArrayList<String> projectNames = UISDKInterfaceManager.getSDKInterface().getProjectNames(this.mObserver);
					this.mObserver.setReturnValue(projectNames);
				}
				break;
		
			case UserRequestHandler.SAVE_REPORT_SETTING:
				UEReportProperty ueProp = (UEReportProperty) mEntity;
				
				prop.setProperty(Property.DEFALT_REPORT_LOCATION, ueProp.getDefaultReportLocation());

				this.mObserver.setSuccessMessage("Properties have been saved successfully.");
				
				break;
				
			case UserRequestHandler.SAVE_PROXY_SETTING:
				UEProxySetting ueProxySetting = (UEProxySetting) mEntity;
				
				prop.setProperty(Property.PROXY_SERVER_IP, ueProxySetting.getProxyServerIP());
				prop.setProperty(Property.PROXY_SERVER_PORT, ueProxySetting.getProxyServerPort());
				break;
				
				
			case UserRequestHandler.LOGIN:
				UELogin ueLogin = (UELogin) mEntity;
				this.mObserver.pushMessage("Sending login request to server");

				mSDKInterface.userLogin(
						ueLogin.getUserID(), 
						ueLogin.getPassword(), 
						ueLogin.getProtexServerIP(),
						this.mObserver);

				if(this.mObserver.getResult() == UIResponseObserver.RESULT_SUCCESS) {
					IdentifyQueue.getInstance().makeBackup();
					loadMainFrame(ueLogin, this.mObserver);
				}
				break;
				
				

			case UserRequestHandler.GENERATE_BOTH_REPORT:
				generateBothReport();
				break;

			case UserRequestHandler.GENERATE_IDENTIFY_REPORT:
				generateIdentifyReport();
				break;

			case UserRequestHandler.GENERATE_SPDX_REPORT:
				generateSPDXReport();
				break;

			case UserRequestHandler.GET_BOM_LIST_FROM_SERVER:
				{
					UEProjectName ueProjetName = (UEProjectName) mEntity;
					String projectName = ueProjetName.getProjectName();
					UISDKInterfaceManager.getSDKInterface().getBOMListFromProjectName(projectName, this.mObserver);
				}
				break;
			
			case UserRequestHandler.GET_BOM_LIST_MAP_FROM_SERVER:
				{
					UEProjectName ueProjetName = (UEProjectName) mEntity;
					ArrayList<String> projectNames = ueProjetName.getProjectNames();
					UISDKInterfaceManager.getSDKInterface().getBOMListMapFromProjectNames(projectNames, this.mObserver);
				}
				break;
				
			case UserRequestHandler.PROJECT_CLONE:
				{
					UEProjectClone ueProjectClone = (UEProjectClone) mEntity;
					if(ueProjectClone.getOriginalProjectName() == null) {
						this.mObserver.setResult(UIResponseObserver.RESULT_FAIL);
						return;
					}
					String projectID = null;
					String newProjectName = ueProjectClone.getNewProjectName();
					String originalProjectName = ueProjectClone.getOriginalProjectName();
					projectID = SDKInterfaceImpl.getInstance().cloneProject(newProjectName, originalProjectName, mObserver);
					ArrayList<OSIProjectInfo> osiProjectInfoList = new ArrayList<OSIProjectInfo>();
					
					if(projectID != null) {
						String sourceLocation = ueProjectClone.getSourceLocation();
						boolean isAnalyzed = false;
						if(OSIProjectInfoMgr.getInstance().getProjectInfo(originalProjectName) != null) {
							isAnalyzed = OSIProjectInfoMgr.getInstance().getProjectInfo(originalProjectName).isAnalyzed();
						}
						OSIProjectInfo curCreateProjectInfo = createOSIProjectInfo(projectID, newProjectName, sourceLocation, isAnalyzed);
						osiProjectInfoList.add(curCreateProjectInfo);
						this.mObserver.setReturnValue(osiProjectInfoList);
						this.mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
					} else {
						this.mObserver.setResult(UIResponseObserver.RESULT_FAIL);
					}
				}
				break;
				
			case UserRequestHandler.PROJECT_CREATE:
				{
					UEProjectCreate ueProjectCreate = (UEProjectCreate) mEntity;
					String projectID = null;
					TreeMap<String, ProjectSplitInfo> mapOfAnalyzeTarget = ueProjectCreate.getMapOfAnalyzeTarget();
					String newProjectName = ueProjectCreate.getProjectName();
					ArrayList<OSIProjectInfo> osiProjectInfoList = new ArrayList<OSIProjectInfo>();
					
					if(mapOfAnalyzeTarget == null) { // one project, no source path
						this.mObserver.pushMessage("Creating project [  " + newProjectName + "  ] ...");
						projectID = SDKInterfaceImpl.getInstance().createProject(newProjectName, null, mObserver);
						if(projectID != null) {
							String sourceLocation = "";
							boolean isAnalyzed = false;
							OSIProjectInfo curCreateProjectInfo = createOSIProjectInfo(projectID, newProjectName, sourceLocation, isAnalyzed);
							osiProjectInfoList.add(curCreateProjectInfo);
							this.mObserver.setReturnValue(osiProjectInfoList);
							this.mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
						} else {
							this.mObserver.setResult(UIResponseObserver.RESULT_FAIL);
						}
						
					} else { // split or no split
						
						int curProjectNum = 0;
						int totalProjectNum = mapOfAnalyzeTarget.size();
						 
						Iterator<Map.Entry<String, ProjectSplitInfo>> iter = mapOfAnalyzeTarget.entrySet().iterator(); 
						if(iter == null) {
							return;
						}
						
						while(iter.hasNext()) {
							String strProjectName = iter.next().getKey();
							++curProjectNum;
							if(mapOfAnalyzeTarget.size() <= 1) {
								this.mObserver.pushMessage("Creating project [  " + newProjectName + "  ] ...");
							} else {
								this.mObserver.pushMessage("Creating project [  (" + curProjectNum + "/"+ totalProjectNum +") " + newProjectName + "  ] ...");
							}
							
							String sourceLocation = mapOfAnalyzeTarget.get(strProjectName).getAnalyzeTargetPath();
							projectID = SDKInterfaceImpl.getInstance().createProject(
									strProjectName, 
									sourceLocation, 
									mObserver);
							if(projectID != null) {
								boolean isAnalyzed = false;
								OSIProjectInfo curCreateProjectInfo = createOSIProjectInfo(projectID, strProjectName, sourceLocation, isAnalyzed);
								osiProjectInfoList.add(curCreateProjectInfo);
								this.mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
							} else {
								this.mObserver.setResult(UIResponseObserver.RESULT_FAIL);
							}
						}
						this.mObserver.setReturnValue(osiProjectInfoList);
					}
				}
				break;
				
			case UserRequestHandler.PROCESS_IDENTIFY:
				{
					IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
					if(thread != null)
						thread.setIsStopByUser(true);
					
					mObserver.setMessageHeader("Identify processing... \n");
					mObserver.pushMessageWithHeader(" > Update local database.\n");
	
					boolean result = ActionIdentifyOrReset.requestIdentify(mObserver, (UEIdentifyResetComment)mEntity);
					if(result) {
						mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
					} else {
						mObserver.setResult(UIResponseObserver.RESULT_FAIL);
					}
					
					thread.setIsStopByUser(false);
				}
				break;
			
			case UserRequestHandler.PROCESS_RESET:
				{
					IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
					if(thread != null)
						thread.setIsStopByUser(true);
					
					mObserver.setMessageHeader("Reset processing... \n");
					mObserver.pushMessageWithHeader(" > Update local database.\n");
					boolean result = ActionIdentifyOrReset.requestReset(mObserver, (UEIdentifyResetComment)mEntity);
					if(result) {
						mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
					} else {
						mObserver.setResult(UIResponseObserver.RESULT_FAIL);
					}
					
					thread.setIsStopByUser(false);
				}
				break;
			case UserRequestHandler.PROJECT_SPLIT:
				{
					mObserver.setMessageHeader("Assessing Project Size... \n");
					boolean result = splitProject(mObserver);
					if(result) {
						mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
						mObserver.setSuccessMessage("Complete splitting!!");
					} else {
						mObserver.setResult(UIResponseObserver.RESULT_FAIL);
						mObserver.setFailMessage("Fail splitting!!");
					}
				}
				break;
			
			case UserRequestHandler.PROJECT_INFO:
				{
					UEProjectInfo ue = (UEProjectInfo) mEntity;
					String projectName = ue.getProjectName();
					
					mObserver.setMessageHeader("Getting Project information... \n");
					boolean result = getProjectAnalysisInfo(projectName, mObserver);
					if(result) {
						mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
						mObserver.setSuccessMessage("Complete!!");
					} else {
						mObserver.setResult(UIResponseObserver.RESULT_FAIL);
						mObserver.setFailMessage("Fail!!");
					}
				}
				break;
				
			case UserRequestHandler.SPDX_AUTO_IDENTIFY:
				{
					mObserver.setMessageHeader("Ready to start SPDX Auto Identify ... \n");
					
					UESPDXAutoIdentify ue = (UESPDXAutoIdentify)mEntity;
					SPDXAutoIdentifyController controller = new SPDXAutoIdentifyController();
					AutoIdentifyResult autoIdentifyResultReport = controller.startAutoIdentifyFromSPDX(ue, mObserver);
					
					if (mObserver.getResult() != UIResponseObserver.RESULT_FAIL) {
						mObserver.setSuccessMessage(autoIdentifyResultReport.toString());
						mObserver.setResult(UIResponseObserver.RESULT_SUCCESS);
					} else {
						mObserver.setFailMessage(autoIdentifyResultReport.toString());
					}
					mObserver.setReturnValue(autoIdentifyResultReport);
				}
				break;
				
		}
		
		mObserver.pushMessage("Execution Thread - execution is completed");
		isDone = true;		
		
		long finishTime = System.currentTimeMillis();		
		log.info(UserRequestHandler.getCommandName(mRequestCode)+" - finish: ("+(finishTime-startTime)+" ms.)");

		closeAction();
		
	}

	private void syncToServer() {
		long start = System.currentTimeMillis();
		
		boolean result = IdentifyQueue.getInstance().makeBackup();
		if(result == false) {
			mObserver.setResult(UIResponseObserver.RESULT_FAIL);
			mObserver.setFailMessage("Can't make backup file");
			return;
		}
		
		Property.getInstance().setProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "false");
		while(IdentifyQueue.getInstance().size() > 0) {
			
			if(IdentifyQueue.getInstance().size() <=1) {
				Property.getInstance().setProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "true");
			} else {
				Property.getInstance().setProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "false");
			}
			
			try {
				Thread.sleep(50);
			} catch(Exception e){}
			
			mObserver.setMessageHeader(
					"Flushing items in identify queue to Protex Server - "
					+IdentifyQueue.getInstance().size()+" file(s) remains.\n");
			
			IdentifyData iData = IdentifyQueue.getInstance().firstElement();
			if(iData == null)
				break;
			
			String fullPathMessage = iData.getFilePath().toString();
			String displayedFullPath = "";
			while(fullPathMessage.length()>75) {
				displayedFullPath += fullPathMessage.substring(0,75)+"\n        ";
				fullPathMessage = fullPathMessage.substring(75);
			}
			displayedFullPath += fullPathMessage;
				
			String out = " > Identifying \""+iData.getProjectName()+"\"\n"+
			             "        "+displayedFullPath+"\n "+
			             "        (identified at "+iData.getTimeStamp()+")";
			mObserver.pushMessageWithHeader(out);
		}
		Property.getInstance().setProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "true");
		
		String title = "Sync to server - "+IdentifyMediator.getInstance().getSelectedProjectName();
		String message = "\"Sync To Server\" finished.\n"+ 
		                 "Do you want to progress \"Sync From Server\" for UI updating?";
		int yesNo = JOptionPane.showConfirmDialog(
				null,
				message,
				title,
				JOptionPane.YES_NO_OPTION);

		if(yesNo == JOptionPane.YES_OPTION) {
			long end = System.currentTimeMillis();
			log.debug("@@@     To Server TIME : " + (end - start)/1000.0);
			syncFromServer();
		}
		
	}

	private void syncFromServer() {
		long start = System.currentTimeMillis();
		mObserver.setMessageHeader("Synchronizing from Protex Server ...\n");
		mObserver.pushMessageWithHeader(" > Loading project list.\n");
		
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
		if(thread == null) {
			return;
		}
		
		boolean prevIsStopByUser = thread.getIsStopByUser();
		thread.setIsStopByUser(true);
			
		ProjectAPIWrapper.refresh();
		log.debug("STEP1. getProjectName");
		
		if( (projectName != null) && (projectName.length() != 0) ) {
			ProjectDiscoveryControllerMap.loadProjectDiscoveryControllerFromProtexServer(projectName, mObserver);
			CodeTreeAPIWrapper.refreshCodeTree(projectName, this.mObserver);
		}
		log.debug("STEP2 - refreshComboProjectName");
		IdentifyMediator.getInstance().refreshComboProjectName();
		
		log.debug("STEP3 - updateProjectList");
		if(ManageMediator.getInstance().getJPanManage() != null) {
			ManageMediator.getInstance().getJPanManage().updateProjectList();
		}
		if(ReportMediator.getInstance().getJPanBillOfMaterials() != null) {
			ReportMediator.getInstance().getJPanBillOfMaterials().clearBOMTableMgr();
		}
		
		log.debug("STEP4 - refreshIdentificationInfoForTreeListChildFrames");
		int matchType = IdentifyMediator.getInstance().getSelectedMatchType();
		IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, matchType);
		
		log.debug("STEP5 - end");
		
		this.mObserver.setSuccessMessage("Data synchronization from protex server has been completed.");
		
		thread.setIsStopByUser(prevIsStopByUser);

		long end = System.currentTimeMillis();
		log.debug("@@@ Sync From Server TIME : " + (end - start)/1000.0);
	}

	private boolean getProjectAnalysisInfo(String projectName, UIResponseObserver mObserver) {
		
		StringBuffer message = new StringBuffer();
		ProjectEntForReport projectEnt = ProjectEntForReportFactory.createProjectEnt(projectName, mObserver);
		
		if(projectEnt == null) {
			return false;
		}
		
		message.append("## General Information").append("<BR>");
		message.append("1. ProjectName : ").append(projectName).append("<BR>");
		message.append("2. Project Creator : ").append(projectEnt.getCreatedBy()).append("<BR>");
		message.append("3. Server : ").append(projectEnt.getServer()).append("<BR>");
		message.append("4. Project Description : ").append(projectEnt.getDescription()).append("<BR><BR>");
		
		message.append("## Identify Status").append("<BR>");
		message.append("5. Num of Files : ").append(projectEnt.getNumOfTotalFiles()).append("<BR>");
		message.append("6. Skipped Files : ").append(projectEnt.getNumOfSkippedFiles()).append("<BR>");
		message.append("7. Current Pending Files : ").append(projectEnt.getCurrentPendingFileNum()).append("<BR>");
		message.append("8. Last Updated : ").append(projectEnt.getLastUpdated()).append("<BR><BR>");
		
		message.append("## Scan Info").append("<BR>");
		message.append("9. Scan Started : ").append(projectEnt.getScanStarted()).append("<BR>");
		message.append("10. Analysis Release Description : ").append(projectEnt.getAnalysisProtexVersion()).append("<BR>");
		message.append("11. Analyzed From Host : ").append(projectEnt.getAnalyzedFromHost()).append("<BR>");
		message.append("12. Analyzed by : ").append(projectEnt.getAnalyzedBy()).append("<BR>");
		
		mObserver.setReturnValue(message.toString());
		return true;
	}

	private OSIProjectInfo createOSIProjectInfo(String projectID, String projectName, String sourceLocation, boolean isAnalyzed) {
		OSIProjectInfo curCreateProjectInfo = new OSIProjectInfo(projectID, projectName, System.getenv("COMPUTERNAME"),sourceLocation);
		curCreateProjectInfo.setManaged(true);
		if(isAnalyzed == true) {
			curCreateProjectInfo.setAnalyzeTarget(false);
			curCreateProjectInfo.updateAnalysisSuccessInfo();
		} else {
			curCreateProjectInfo.setAnalyzeTarget(true);
			curCreateProjectInfo.setProjectAnalysisInfo(isAnalyzed);
		}
		OSIProjectInfoMgr.getInstance().getAllProjectInfo().putProjectInfo(projectName, curCreateProjectInfo);
		return curCreateProjectInfo;
	}

	private boolean splitProject(UIResponseObserver observer) {
		UEProtexSplit ue = (UEProtexSplit) mEntity;
		if(ue == null) {
			return false;
		}
		ProjectSplitUtil projectSplitUtil = ue.getProjectSplitUtil();
		File rootLocationFile = ue.getRootLocationFile();
		projectSplitUtil.clearAnalyzeTargetMap();
		projectSplitUtil.split(rootLocationFile, observer);
		return true;
	}

	private void generateBothReport() {
		log.debug("Generate Both Report (Excel and SPDX");				
		
		Collection<String> projects = ReportMediator.getInstance().getSelectedProjectList();
		if(projects == null || projects.size() <= 0)
			return;
		
		UEBothBOM ueBoth = (UEBothBOM) mEntity;

		// SPDX
		boolean isValidProjectNames = this.isValidProjectName(ueBoth.getProjectList());
		if(isValidProjectNames == false) {
			return;
		}

		if(ueBoth.isOverwrite())
		{
			for(String fileName:ueBoth.getTargetSDPXReportFileList()) {
				File fileTemp = new File(fileName);
				if(fileTemp.exists()) 
					fileTemp.delete();
			}
		}

		// Identify Report
		ArrayList<ProjectInfoForIdentifyReport> projectInfo = new ArrayList<ProjectInfoForIdentifyReport>();
		
		for(String projectName:projects) {
			ArrayList<String> componentNames = ueBoth.getFileListUpComponentNames(projectName);
			
			ProjectInfoForIdentifyReport prjInfo = 
				new ProjectInfoForIdentifyReport(
						projectName,
						ueBoth.isOptAllFilesSelected(),
						componentNames );
			
			
			projectInfo.add(prjInfo);
		}

		
		if(ueBoth.isOverwrite()) {
			File fileTemp = new File(ueBoth.getTargetFileName());
			if(fileTemp.exists()) 
				fileTemp.delete();
			
		}

		UISDKInterfaceManager.getSDKInterface().generateIdentifyReport(
				projectInfo, 
				ueBoth.getSrcFileName(), 
				ueBoth.getTargetFileName(),
				ueBoth.isInsertCodeMatch(),
				ueBoth.getCreatorName(),
				ueBoth.getCreatorEmail(),
				ueBoth.getOrganizationName(),
				this.mObserver);
		
		UISDKInterfaceManager.getSDKInterface().generateSPDXReport(
				ueBoth.getProjectList(),
				ueBoth.getTargetSDPXReportFileList(),
				ueBoth.getSpdxReportConfiguration(),
				this.mObserver);
		
	}

	private void generateIdentifyReport() {

		log.debug("Generate Identify Report");				
		
		Collection<String> projects = ReportMediator.getInstance().getSelectedProjectList();
		if(projects == null || projects.size() <= 0)
			return;
		
		UEBOM ueIdentify = (UEBOM) mEntity;
		
		boolean isValidProjectNames = this.isValidProjectName(projects);
		if(isValidProjectNames == false) {
			return;
		}
			
		
		ArrayList<ProjectInfoForIdentifyReport> projectInfo = new ArrayList<ProjectInfoForIdentifyReport>();
		
		for(String projectName:projects) {
			ArrayList<String> componentNames = ueIdentify.getFileListUpComponentNames(projectName);
			
			ProjectInfoForIdentifyReport prjInfo = 
				new ProjectInfoForIdentifyReport(
						projectName,
						ueIdentify.isOptAllFilesSelected(),
						componentNames );
			
			
			projectInfo.add(prjInfo);
		}

		
		if(ueIdentify.isOverwrite())
		{
			File fileTemp = new File(ueIdentify.getTargetFileName());
			if(fileTemp.exists()) 
				fileTemp.delete();
			
		}
		
		UISDKInterfaceManager.getSDKInterface().generateIdentifyReport(
				projectInfo, 
				ueIdentify.getSrcFileName(), 
				ueIdentify.getTargetFileName(),
				ueIdentify.isInsertCodeMatch(),
				ueIdentify.getCreatorName(),
				ueIdentify.getCreatorEmail(),
				ueIdentify.getOrganizationName(),
				this.mObserver);
		
	}


	private void generateSPDXReport() {

		log.debug("Generate SPDX Document");				
		UESPDXBOM ueSPDX = (UESPDXBOM) mEntity;
		
		boolean isValidProjectNames = this.isValidProjectName(ueSPDX.getProjectList());
		if(isValidProjectNames == false) {
			return;
		}

		if(ueSPDX.isOverwrite())
		{
			for(String fileName:ueSPDX.getTargetSDPXReportFileList()) {
				File fileTemp = new File(fileName);
				if(fileTemp.exists()) 
					fileTemp.delete();
			}
		}

		UISDKInterfaceManager.getSDKInterface().generateSPDXReport(
				ueSPDX.getProjectList(),
				ueSPDX.getTargetSDPXReportFileList(),
				ueSPDX.getSpdxReportConfiguration(),
				this.mObserver);
	}

	private void loadMainFrame(UELogin ueLogin, UIResponseObserver observer) {

		BackgroundJobManager.getInstance().startAfterLoginTaskThread();
		
		UISharedData.getInstance().setCurrentFrame(null);
		UISharedData.getInstance().setConnectionInfo(
				ueLogin.getUserID(), ueLogin.getProtexServerIP()
				);
				
		
		observer.pushMessage("Loading main window...");
		JFrame frmMain = new JFrmMain();
		CacheableMgr.getInstance().loadFromCache();
		
		UISharedData.getInstance().setCurrentFrame(frmMain);
		
	}

	public boolean isDone() {
		return isDone;
	}
	
	public void setMonitorThread(UserCommandExecutionMonitorThread monitorThread) {
		this.mMonitorThread = monitorThread;
		
	}

//	@SuppressWarnings("deprecation")
	public void cancel() {
		closeAction();
	}
	
	private void closeAction() {
		if(this.mMonitorThread != null) {
			mMonitorThread.setIsContinue(false);
		}
	}
	
	private boolean isValidProjectName(Collection<String> projectList) {
		
		for(String projectName: projectList) {

			String modifiedProjectName = projectName.replace("[", "_");
			modifiedProjectName = modifiedProjectName.replace("]", "_");
			modifiedProjectName = modifiedProjectName.replace("/", "_");
			modifiedProjectName = modifiedProjectName.replace("|", "_");
			modifiedProjectName = modifiedProjectName.replace(":", "_");
			modifiedProjectName = modifiedProjectName.replace(",", "_");
			modifiedProjectName = modifiedProjectName.replace("?", "_");
			modifiedProjectName = modifiedProjectName.replace(";", "_");
			modifiedProjectName = modifiedProjectName.replace("\\", "_");
	
			if(modifiedProjectName.equals(projectName) == false) {
				JOptionPane.showMessageDialog(
						null, 
						"\""+projectName + "\" is not valid project name.\n"+
						"If project name in protex contains any characters in"+
						"\"[]?:;/\\,\"  \n"+
						"Any report is not generated.\n"+
						"You should remove characters above.",
						"Report will not be generated",
						JOptionPane.WARNING_MESSAGE
						);
				return false;
			}
		}

		return true;
	}
}
