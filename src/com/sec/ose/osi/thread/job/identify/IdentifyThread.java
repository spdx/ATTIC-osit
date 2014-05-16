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
package com.sec.ose.osi.thread.job.identify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.job.analysis.AnalysisMonitorThread;
import com.sec.ose.osi.thread.job.identify.data.CodeMatchIdentify;
import com.sec.ose.osi.thread.job.identify.data.CommonIdentify;
import com.sec.ose.osi.thread.job.identify.data.IdentifiedController;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.job.identify.data.IdentifyErrorQueue;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.job.identify.data.PatternMatchIdentify;
import com.sec.ose.osi.thread.job.identify.data.StringSearchIdentify;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;

/**
 * IdentifyThread
 * @author sjh.yoo, hankido.lee, suhyun47.kim, jae-yong.lee
 * 
 */
public class IdentifyThread extends Thread {
	private static Log log = LogFactory.getLog(IdentifyThread.class);
	
	public static final int STATUS_READY = 0;
	public static final int STATUS_EXECUTING = 1;
	
	private int threadStatus = STATUS_READY;
	private int errorCount = 0;
	private final int IDENTIFY_FAIL_ACCEPT_LIMIT = 3;

	private IdentifyData preIdentifiedData = null;

	
	private boolean isContinue = true;
	private static final int SAFE_TIME_FOR_EXIT = 5 * 1000; // 5 seconds
	
	private boolean isStopByUser = false;
	
	
	private int id;
	
	public IdentifyThread(int id) {
		this.id = id;
	}
	
	
	public void requestStopThread() {
		
		if(this.isContinue == false)
			return;
		
		this.isContinue = false;
		
		try {
			Thread.sleep(SAFE_TIME_FOR_EXIT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setIsStopByUser(boolean isResume) {
		this.isStopByUser = isResume;
	}
	
	public synchronized boolean getIsStopByUser() {
		return this.isStopByUser;
	}
	
	public void run() {
		log.debug("Identify Thread#"+id+" Run...");
		isContinue = true;
		
		while (isContinue) {
			
			updateJtextFieldIdentifyQueueStatusBar();


			
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				log.warn(e);
			}

			if(isStopByUser == true)			// default = false;
				continue;
			
			if(IdentifyQueue.getInstance().size() <= 0)
				continue;
			
			IdentifyData identifyData = IdentifyQueue.getInstance().firstElement();
			
			this.setStatus(STATUS_EXECUTING);
			boolean isAcceptableErrorCnt = checkErrorCountIsAcceptable(identifyData);
			if(isAcceptableErrorCnt == false) {
				this.setStatus(STATUS_READY);
				continue;
			}
			
			processIdentifyData(identifyData);
			this.setStatus(STATUS_READY);
		}
		
		this.setStatus(STATUS_READY);
	}
	
	private void updateJtextFieldIdentifyQueueStatusBar() {
		
		JTextField statusBar = UISharedData.getInstance().getjTextFieldIdentifyQueueStatusBar();
		if(statusBar == null) {
			return;
		}
			
		
		String message = UISharedData.getInstance().getConnectionInfo()  
							+" - " + IdentifyQueue.getInstance().size()
							+" item(s) are in the queue to be updated on server. ";
		
		
		int errorQueueSize = IdentifyErrorQueue.getInstance().size();
		if(errorQueueSize > 0) {
			message += " - with "+errorQueueSize+ " error(s) ";
		}
		
		if(isStopByUser == true) {
			message += " - Thread is not running.";
		} else {
			message += " - Thread is running.";
		}
		
		
		statusBar.setText(message);
	}

	private void processIdentifyData(IdentifyData identifiedData) {
		
		if(identifiedData == null) {
			return;
		}
		
		log.debug("Thread#"+id+"processIdentifyData \n"+ identifiedData.toOutPut());
		String projectName = identifiedData.getProjectName();
		
		boolean bReset = ((identifiedData.getCompositeType() & IdentificationConstantValue.RESET_TYPE) != 0)?true:false;
		
		if(IdentifiedController.getProjectStatus(projectName) != AnalysisMonitorThread.STATUS_EXECUTING) {
			IdentifiedController.setProjectStatus(projectName, AnalysisMonitorThread.STATUS_IDENTIFING);
			boolean result = false;
			
			if(bReset) {
				result = performReset(identifiedData);
			} else {
				result = performIdentification(identifiedData);
			}
			
			if(result) {
				IdentifyQueue.getInstance().dequeue();
			}
			IdentifiedController.setProjectStatus(projectName, AnalysisMonitorThread.STATUS_READY);
		}
	}

	private boolean performIdentification(IdentifyData identifyData) {
		
		String projectName = identifyData.getProjectName();
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(projectID == null) {
			projectID = UISDKInterfaceManager.getSDKInterface().getProjectID(projectName);
		}
        log.debug("Thread#"+id+" ### Identification Start - "+projectName);
		
		/** Data Processing **/
        
		// Search & Create LicenseInfo
		String newLicenseName = identifyData.getNewLicenseName();
		String newLicenseID = null;
		if(newLicenseName.length() != 0) {
			newLicenseID = LicenseAPIWrapper.getLicenseID(newLicenseName);
		} else {
			newLicenseName = "Unspecified";
			newLicenseID = "unspecified";
		}
		
		LicenseInfo identifiedLicenseInfo = new LicenseInfo();
		identifiedLicenseInfo.setName(newLicenseName);
		identifiedLicenseInfo.setLicenseId(newLicenseID);
		log.debug("Thread#"+id+"[IdentifyThread.performIdentification()] newLicenseName : " + newLicenseName);
		log.debug("Thread#"+id+"[IdentifyThread.performIdentification()] newLicenseID : " + newLicenseID);

		// Search & Create ComponentID, VersionID
		String currentComponentName = identifyData.getCurrentComponentName();
		String currentVersionName = identifyData.getCurrentVersionName();
		String currentComponentID = "";
		String currentVersionID = "";
		String matchedFile = identifyData.getMatchedFile();
		

		int type = identifyData.getCompositeType();
		boolean bFile = ((type & IdentificationConstantValue.FOLDER_TYPE) != 0)?false:true;
        

		/** Identify **/
        CommonIdentify identification = null; 
		if((type & IdentificationConstantValue.STRING_MATCH_TYPE) != 0) {
		
			/** String Search Identification **/
			identification = new StringSearchIdentify(
					identifyData,
					projectName, 
					projectID, 
					bFile, 
					currentComponentName, 
					currentComponentID, 
					currentVersionID, 
					identifyData.getComment(), 
					identifiedLicenseInfo);
			
		} else if((type & IdentificationConstantValue.CODE_MATCH_TYPE) != 0) {
		
			/** Code Match Identification **/
			boolean isSelected_ThirdForthOption = ((type & IdentificationConstantValue.STANDARD_COMPONENT_TYPE)==0);
			identification = new CodeMatchIdentify(
					identifyData,
					projectName, 
					projectID, 
					bFile,
					isSelected_ThirdForthOption,
					currentComponentName, 
					currentComponentID, 
					currentVersionID, 
					identifyData.getComment(), 
					identifiedLicenseInfo);
			
		} else if((type & IdentificationConstantValue.PATTERN_MATCH_TYPE) != 0) {
		
			/** Pattern Match Identification **/
			identification = new PatternMatchIdentify(
					identifyData,
					projectName, 
					projectID, 
					bFile, 
					currentComponentName, 
					currentComponentID, 
					currentVersionID, 
					identifyData.getComment(), 
					identifiedLicenseInfo);
					
		} else {
		
			log.warn("[ERROR] Unknown identify type : "+type);
			return false;
		}
		
		log.debug("Thread#"+id+" Identified("+identification.getInfo()+")  ==> " + currentComponentID + "#" + currentVersionID);

        /** action **/
		try {
			Collection<String> filePathCollection = identifyData.getFilePath();
			
	        for(String filePath: filePathCollection) {
	        	log.info("Thread#"+id+" # Identify : "+filePath + " ==> " + currentComponentName + ":" + currentVersionName + ":" + newLicenseName);
	        	identification.setPath(filePath);
	        	
	    		if( currentComponentName != null && currentComponentName.length() > 0 ){
	    			
	    			log.debug("Thread#"+id+"currentComponentName : "+currentComponentName+" , currentVersionName : "+currentVersionName);
	    			
	    			if( (currentVersionName != null) &&
	    				 (currentVersionName.length() > 0) && 
	    				 !(currentVersionName.toLowerCase().equals("unspecified")) ) {
	    				
	    				try {
	    					ComponentVersion componentVersion = ComponentAPIWrapper.getComponentVersionByName(currentComponentName, currentVersionName);
	    					if(componentVersion != null) {
	    						currentComponentID = componentVersion.getComponentId();
	    						currentVersionID = componentVersion.getVersionId();
	    						log.debug("Thread#"+id+"currentComponentID : "+currentComponentID+" , currentVersionID : "+currentVersionID);
	    					} else {
    							// search discovery using matched file
	    				    	PartialCodeTree partialCodeTree = null;
	    				        try {
	    				        	filePath = "/" + filePath;
	    				            partialCodeTree = ProtexSDKAPIManager.getCodeTreeAPI().getCodeTree(projectID, filePath, 0, Boolean.TRUE);
	    				        } catch (SdkFault e) {
	    				        	log.warn("getCodeTree failed() " + e.getMessage());
	    				        }
	    				        
	    				        List<CodeMatchType> precisionOnly = new ArrayList<CodeMatchType>(1);
	    				        precisionOnly.add(CodeMatchType.PRECISION);
	    				        List<CodeMatchDiscovery> discoveries = null;
	    				        try {
	    				        	discoveries = ProtexSDKAPIManager.getDiscoveryAPI().getCodeMatchDiscoveries(projectID, partialCodeTree, precisionOnly);
	    				        } catch (SdkFault e) {
	    				        	log.warn("getCodeMatchDiscoveries() failed: " + e.getMessage());
	    				        }

	    				        if (discoveries.size() != 0) {
	    		 		        	for (CodeMatchDiscovery discovery : discoveries) {
	    		 		        		if(discovery.getMatchingFileLocation().getFilePath().equals(matchedFile)) {
	    		 		        			currentComponentID = discovery.getMatchingComponentId();
	    		 		        			currentVersionID = discovery.getMatchingVersionId();
	    		 		        			break;
	    		 		        		}
	    				        	}
	    				        }
	    						
	    						if(currentComponentID == null || currentComponentID.length() == 0) {
	    							return false;
	    						}
	    						log.debug("Thread#"+id+"currentComponentID : "+currentComponentID+" , currentVersionID : "+currentVersionID);
	    					}
	    				} catch (Exception e) {
	    					log.debug(e.getMessage());
	    					if(currentComponentID == null || currentComponentID.length() == 0) {
	    						log.error("Thread#"+id+"[performIdentification] "+ currentComponentName 
	    								+" : ComponentID is null - fail to getting componentversioninfo from Server");
	    						return false;
	    					}
	    				}
	    				
	    			} else {
	    				currentComponentID = ComponentAPIWrapper.getComponentId(projectID,currentComponentName);
	    				if(currentComponentID == null) {
	    					currentComponentID = ComponentAPIWrapper.createLocalComponent(projectID, currentComponentName, newLicenseID);
	    					if(currentComponentID == null || currentComponentID.length() == 0) {
	    						log.error("Thread#"+id+"[performIdentification] "+ currentComponentName 
	    								+" : ComponentID is null - failt to create local component.");
	    						return false;
	    					}
	    				}
						log.debug("Thread#"+id+"currentComponentID : "+currentComponentID+" , currentVersionID : "+currentVersionID);
	    			}
	    		}
	    		identification.setCurrentComponentID(currentComponentID);
	    		identification.setCurrentVersionID(currentVersionID);
	    		identification.setNewComponent();
	    		
	        	identification.identify();
			}
			
	        log.info("### Identification Complete");
        } catch (SdkFault e) {
			log.error("performIdentification is fail...");
			log.error(e);
			return false;
		}
		
		return true;
	}

	private boolean performReset(IdentifyData tmpIdentifiedData) {
		String projectName = tmpIdentifiedData.getProjectName();
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(projectID == null) {
			projectID = UISDKInterfaceManager.getSDKInterface().getProjectID(projectName);
		}
        log.debug("### performReset Start - "+projectName);

		/** Data Processing **/
		String currentComponentName = tmpIdentifiedData.getCurrentComponentName();
		String currentVersionName = tmpIdentifiedData.getCurrentVersionName();
		log.debug("currentComponentName : " +currentComponentName);
		log.debug("currentVersionName : " +currentVersionName);
		
		String currentComponentID = null;
		String currentVersionID = "";

		int type = tmpIdentifiedData.getCompositeType();
		boolean bFile = true;
		
        CommonIdentify identification = null; 
		if((type & IdentificationConstantValue.STRING_MATCH_TYPE) != 0) {
			identification = new StringSearchIdentify(
					tmpIdentifiedData,
					projectName, 
					projectID, 
					bFile, 
					currentComponentName, 
					currentComponentID, 
					currentVersionID,
					"", 
					null);
		} else if((type & IdentificationConstantValue.CODE_MATCH_TYPE) != 0) {
			identification = new CodeMatchIdentify(
					tmpIdentifiedData,
					projectName, 
					projectID, 
					bFile,
					false,
					currentComponentName, 
					currentComponentID, 
					currentVersionID, 
					"", 
					null);
		} else if((type & IdentificationConstantValue.PATTERN_MATCH_TYPE) != 0) {
			identification = new PatternMatchIdentify(
					tmpIdentifiedData,
					projectName, 
					projectID, 
					bFile, 
					currentComponentName, 
					currentComponentID, 
					currentVersionID, 
					"", 
					null);
		} else {
			log.warn("[ERROR] Unknown identify type : "+type);
			return false;
		}
		
		log.debug("reset type : " + identification.getInfo());
        
        /** action **/
		try {
			Collection<String> filePath = tmpIdentifiedData.getFilePath();
			String[] filePathList = filePath.toArray(new String[filePath.size()]);
			
			for(int i=0; i<filePathList.length; i++) {
				String curFilePath = filePathList[i];
	    		log.info("reset filepath : "+curFilePath);
				identification.setPath(curFilePath);
				
				if( (currentComponentName==null) || 
						(currentComponentName.equals("null")) || 
						(currentComponentName.length() == 0) ) {
					log.debug("currentComponentName is null~~~");
				}
				if ( (currentVersionName.length() <= 0) || (currentVersionName.toLowerCase().equals("unspecified")) ) {
					currentComponentID = ComponentAPIWrapper.getComponentId(projectID, currentComponentName);
				} else {
					//ComponentVersion componentVersion = ComponentAPIWrapper.getComponentVersionByName(currentComponentName, currentVersionName, projectID, curFilePath);
					ComponentVersion componentVersion = ComponentAPIWrapper.getComponentVersionByName(currentComponentName, currentVersionName);
					if(componentVersion != null) {
						currentComponentID = componentVersion.getComponentId();
						currentVersionID = componentVersion.getVersionId();
					} else {
						currentComponentID = ComponentAPIWrapper.getComponentId(projectID, currentComponentName);
					}
				}
	    		log.info("currentComponentID : "+currentComponentID);
	    		log.info("currentVersionID : "+currentVersionID);
				identification.setCurrentComponentID(currentComponentID);
				identification.setCurrentVersionID(currentVersionID);
				identification.setNewComponent();
				
				List<CodeTreeIdentificationInfo> identifiedNodes = identification.getIdentifications();
				
				for (CodeTreeIdentificationInfo identificationInfo : identifiedNodes) {
					log.debug("List<CodeTreeIdentificationInfo> : "+identificationInfo.getName());
					List<Identification> identifications = identificationInfo.getIdentifications();
					if(identifications.size() <= 0) {
						log.warn("List<Identification> size is 0.. can't reset..");
					}
					for (Identification id : identifications) {
						log.debug("List<Identification> : " + id.getIdentifiedComponentId());
						identification.reset(id);
					}
				}
			}
	        log.info("### Reset Complete");
		} catch (SdkFault e1) {
			log.error("performIdentification is fail...");
			log.error(e1);
			return false;
		}
		return true;
	}
	
	private boolean checkErrorCountIsAcceptable(IdentifyData curIdentifiedData) {
		
		
		if(curIdentifiedData == null) {
			errorCount = 0;
			return false;
		}
			
		if(preIdentifiedData == curIdentifiedData ) {
			errorCount++;
			log.debug("errorCount: " + errorCount + "/ "+IDENTIFY_FAIL_ACCEPT_LIMIT);
			if (errorCount > IDENTIFY_FAIL_ACCEPT_LIMIT) {
				handleInvalidIdentifyData(curIdentifiedData.getProjectName());
				return false;
			}
		} else {
			preIdentifiedData = curIdentifiedData;
			errorCount = 0;
		}
		return true;
	}

	private void handleInvalidIdentifyData(String projectName) {

			log.debug("Queue Delete @@@~~");
			
			IdentifyData invalidIdentifyData = IdentifyQueue.getInstance().dequeue();
			
			IdentifyErrorQueue.getInstance().enqueue(invalidIdentifyData);			
			
			log.debug("UI, Start Local DB recover");
			ProjectAPIWrapper.refresh();
			
			if( (projectName != null) && (projectName.length() != 0) ) {
				ProjectDiscoveryControllerMap.loadProjectDiscoveryControllerFromProtexServer(projectName, null);
			}
			IdentifyMediator.getInstance().refreshComboProjectName();
			
			if(ManageMediator.getInstance().getJPanManage() == null) {
				return;
			}
			
			ManageMediator.getInstance().getJPanManage().updateProjectList();
			
			int matchType = IdentifyMediator.getInstance().getSelectedMatchType();
			
			IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, matchType);
			
			log.debug("UI, End Local DB recover");

	}
	
	public synchronized void setStatus(int status) {
		this.threadStatus = status;
	}
	
	public synchronized int getStatus() {
		return threadStatus;
	}

}