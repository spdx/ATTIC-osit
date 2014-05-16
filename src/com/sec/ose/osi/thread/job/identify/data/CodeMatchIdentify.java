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
package com.sec.ose.osi.thread.job.identify.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationDirective;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationType;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.ui.dialog.codeMatchAlarm.RemainedSnippetAlarmInfo;
import com.sec.ose.osi.ui.dialog.codeMatchAlarm.RemainedSnippetAlarmMgr;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.DateUtil;
import com.sec.ose.osi.util.tools.Time;

/**
 * CodeMatchIdentify
 * @author sjh.yoo, hankido.lee, suhyun47.kim, jae-yong.lee
 * 
 */
public class CodeMatchIdentify extends CommonIdentify {
	private static Log log = LogFactory.getLog(CodeMatchIdentify.class);
	
	private List<CodeMatchType> precisionOnly = new ArrayList<CodeMatchType>(1);
	private boolean isSelected_ThirdForthOption = false;
	
	public CodeMatchIdentify(
			IdentifyData pIdentifyData,
			String pProjectName,
			String pProjectID,
			boolean pIsFile,
			boolean isSelected_ThirdForthOption,
			String pCurrentComponentName, 
			String pCurrentComponentID,
			String pCurrentVersionID,
			String pComment,
			LicenseInfo pLicenseInfo ) {
		this.identifyData = pIdentifyData;
		this.projectName = pProjectName;
		this.projectID = pProjectID;
		this.bFile = pIsFile;
		this.isSelected_ThirdForthOption = isSelected_ThirdForthOption;
		this.currentComponentName = pCurrentComponentName;
		this.currentComponentID = pCurrentComponentID;
		this.currentVersionID = pCurrentVersionID;
		this.comment = pComment;
		this.identifiedLicenseInfo = pLicenseInfo;
		
		this.setBase();
	}
	
	public void setBase() {
		super.setBase();
		identifiedUsageLevel = UsageLevel.SNIPPET;
		/** Code Match **/
		precisionOnly.add(CodeMatchType.PRECISION);
	}
	
	public void setNewComponent() {
		
		newComponentName = identifyData.getNewComponentName();
		String newVersionName = identifyData.getNewVersionName();
		String currentVersionName = identifyData.getCurrentVersionName();
		if(identifiedLicenseInfo == null) return;
		String newLicenseID = identifiedLicenseInfo.getLicenseId();
		log.debug("currentComponentName : "+currentComponentName+" , currentVersionName : "+currentVersionName);
		log.debug("newComponentName : "+newComponentName+" , newVersionName : "+newVersionName);
		if(currentComponentName.equals(newComponentName) && currentVersionName.equals(newVersionName)) {
			newComponentID = currentComponentID;
			newVersionID = currentVersionID;
		} else {
			if ( (newVersionName.length() == 0) || (newVersionName.toLowerCase().equals("unspecified")) ) {
				newComponentID = ComponentAPIWrapper.getComponentId(projectID, newComponentName);
				if ( newComponentID == null) {
					newComponentID = ComponentAPIWrapper.createLocalComponent(projectID, newComponentName, newLicenseID);
				}
			} else {
				ComponentVersion newComponentVersion = ComponentAPIWrapper.getComponentVersionByName(newComponentName,newVersionName);
				if(newComponentVersion != null) {
					newComponentID = newComponentVersion.getComponentId();
					newVersionID = newComponentVersion.getVersionId();
				} else {

					// search discovery using matched file
			    	PartialCodeTree partialCodeTree = null;
			        try {
			        	String filePath = "/" + identifyData.getFilePath();
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
	 		        		if(discovery.getMatchingFileLocation().getFilePath().equals(identifyData.getMatchedFile())) {
	 		        			newComponentID = discovery.getMatchingComponentId();
	 		        			newVersionID = discovery.getMatchingVersionId();
	 		        			break;
	 		        		}
			        	}
			        }
					
					if(newComponentID == null || newComponentID.length() == 0) {
						newComponentID = ComponentAPIWrapper.getComponentId(projectID, newComponentName);
					}
				}
			}
		}
		log.debug("newComponentID : "+newComponentID+" , newVersionID : "+newVersionID);
	}
	
	
	private int skippedCnt=0;
	private static final int SYNCHRONOUS_INTERVAL=1000;
	
	@Override
	public void identify() throws SdkFault {
		
		CodeMatchIdentificationRequest oCodeMatchIdentificationRequest = getCodeMatchIdentificationRequest(
				newComponentID, 
				newVersionID, 
				currentComponentID, 
				currentVersionID, 
				identifiedLicenseInfo,
				identifiedUsageLevel);
		
		BomRefreshMode refreshMode = BomRefreshMode.SKIP;
		
		String optionRefreshMode = Property.getInstance().getProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH);
		if("true".equals(optionRefreshMode.toLowerCase())) {
			refreshMode = BomRefreshMode.SYNCHRONOUS;
		}
		else if(++skippedCnt % SYNCHRONOUS_INTERVAL == 0) {
			refreshMode = BomRefreshMode.ASYNCHRONOUS;
			skippedCnt=0;
		}
		
		long startTime = Time.startTime("addCodeMatchIdentification");
		ProtexSDKAPIManager.getIdentificationAPI().addCodeMatchIdentification(
				projectID, 
				"/" + curFilePath, 
				oCodeMatchIdentificationRequest,
				refreshMode);
		log.debug("BOM Refresh Mode : " + refreshMode.name());
		Time.endTime("addCodeMatchIdentification", startTime);

		if(isSelected_ThirdForthOption) {
			List<CodeMatchDiscovery> listCodeMatchDiscovery = null;
			boolean isRemain = true;
			while(isRemain) {
				isRemain = false;
				listCodeMatchDiscovery = ProtexSDKAPIManager.getDiscoveryAPI().getCodeMatchDiscoveries(projectID, fileOnlyTree, precisionOnly);
				for(CodeMatchDiscovery tmpCodeMatchDiscovery:listCodeMatchDiscovery) {
					if (tmpCodeMatchDiscovery.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION) {
						isRemain = true;
	    				oCodeMatchIdentificationRequest = getCodeMatchIdentificationRequest(
	    						newComponentID, 
	    						newVersionID, 
	    						tmpCodeMatchDiscovery.getMatchingComponentId() , 
	    						tmpCodeMatchDiscovery.getMatchingVersionId(), 
	    						identifiedLicenseInfo, identifiedUsageLevel);
	    				
						ProtexSDKAPIManager.getIdentificationAPI().addCodeMatchIdentification(
								projectID, "/" + curFilePath, 
								oCodeMatchIdentificationRequest, 
								refreshMode);
						
						log.debug("remain Identified("+getInfo()+") : "+ curFilePath + " ==> " + currentComponentID + "#" + currentVersionID);
						break;
					}
				}
			}
		}
		
		this.updateComment();
		this.updateDBnUI("identify");
	}

	@Override
	public void reset(Identification id) throws SdkFault {
		BomRefreshMode refreshMode = BomRefreshMode.SKIP;
		
		String optionRefreshMode = Property.getInstance().getProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH);
		if("true".equals(optionRefreshMode.toLowerCase())) {
			refreshMode = BomRefreshMode.SYNCHRONOUS;
		}
		else if(++skippedCnt % SYNCHRONOUS_INTERVAL == 0) {
			refreshMode = BomRefreshMode.ASYNCHRONOUS;
			skippedCnt=0;
		}
		
		if(id.getType() != IdentificationType.CODE_MATCH) return;
		String currentComponentIdFromProtex = "";
		String currentComponentNameFromProtex = "";
		currentComponentIdFromProtex = id.getIdentifiedComponentId();
		currentComponentNameFromProtex = ComponentAPIWrapper.getComponentName(projectID, currentComponentIdFromProtex);

		log.debug("selecting component id : " + currentComponentID);
		log.debug("targeting component id : " + currentComponentIdFromProtex);
		log.debug("selecting component name : " + currentComponentName);
		log.debug("targeting component name : " + currentComponentNameFromProtex);
		
		if(currentComponentID == null) {
			long startTime = Time.startTime("removeCodeMatchIdentification");
			ProtexSDKAPIManager.getIdentificationAPI().removeCodeMatchIdentification(
					projectID, 
					(CodeMatchIdentification) id,
					refreshMode);
			log.debug("BOM Refresh Mode : " + refreshMode.name());
			Time.endTime("removeCodeMatchIdentification", startTime);
			log.debug("reset("+getInfo()+") "+curFilePath+" complete");
			this.updateDBnUI("reset");
			return;
		}	
		
		if(	currentComponentID.equals(currentComponentIdFromProtex) ||
		    currentComponentName.equals(currentComponentNameFromProtex)	) {
			
			long startTime = Time.startTime("removeCodeMatchIdentification");
			ProtexSDKAPIManager.getIdentificationAPI().removeCodeMatchIdentification(
					projectID, 
					(CodeMatchIdentification) id,
					refreshMode);
			log.debug("BOM Refresh Mode : " + refreshMode.name());
			Time.endTime("removeCodeMatchIdentification", startTime);
			log.debug("reset("+getInfo()+") "+curFilePath+" complete");
		}
		
		this.updateDBnUI("reset");
	}
	
	@Override
	public void updateDBnUI(String action) throws SdkFault {
		
		String optionRefreshMode = Property.getInstance().getProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH);
		if("true".equals(optionRefreshMode.toLowerCase()) == false) {
			return;
		}
		
		/** Code Match **/
		long startTime = Time.startTime("getCodeMatchDiscoveries");
		List<CodeMatchDiscovery> listCodeMatchDiscovery = ProtexSDKAPIManager.getDiscoveryAPI().getCodeMatchDiscoveries(projectID, fileOnlyTree, precisionOnly);
		Time.endTime("getCodeMatchDiscoveries", startTime);
		
		DCCodeMatch cdc = ((DCCodeMatch)ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE));
		
		ArrayList<String> newFileList = null;
		if(!bFile) {
			ComponentVersion cv = ComponentAPIWrapper.getComponentVersionById(currentComponentID, currentVersionID);
			if(cv != null) {
				newFileList = cdc.getUpdateTarget(projectName, curFilePath,cv.getComponentName(),cv.getVersionName());
			}
		}
		boolean isRemain = cdc.updateDB(action, newFileList, listCodeMatchDiscovery, newComponentName, isSelected_ThirdForthOption);
		listCodeMatchDiscovery = null;
		log.debug("isRemain : "+isRemain);
		
		String projectIDFromServer = ProjectAPIWrapper.getProjectID(projectName);
		
		if(isRemain && projectID != null && projectID.equals(projectIDFromServer)) {
			IdentifyMediator.getInstance().refreshIdentificationInfoForSnippetRefresh(projectName, this.curFilePath, IdentificationConstantValue.CODE_MATCH_TYPE);

			AbstractDiscoveryController controller  = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE);
			ArrayList<String> pendingFilePathList = controller.getPendingFileList();
			String projectNameByUI = IdentifyMediator.getInstance().getSelectedProjectName();
			if( (pendingFilePathList.contains(curFilePath) == true) && 
				(action.equals("identify") == true) &&
				(isSelected_ThirdForthOption == false) &&
				(projectNameByUI != null) &&
				(projectNameByUI.equals(projectName)) ) {
				
				RemainedSnippetAlarmMgr.getInstance().showAlarmMessage(
						new RemainedSnippetAlarmInfo(
						DateUtil.getCurrentTime("%1$tm-%1$td %1$tH:%1$tM:%1$tS"), 
						curFilePath, 
						newComponentName));
				
			}
		}
	}

	private CodeMatchIdentificationRequest getCodeMatchIdentificationRequest(
			String componentID, 
			String versionID, 
			String newComponentID, 
			String newVersionID, 
			LicenseInfo identifiedLicenseInfo, 
			UsageLevel identifiedUsageLevel) {
		
		CodeMatchIdentificationRequest codeMatchIdentificationRequest = new CodeMatchIdentificationRequest();
		
		codeMatchIdentificationRequest.setDiscoveredComponentId(newComponentID);
		codeMatchIdentificationRequest.setDiscoveredVersionId(newVersionID);
		codeMatchIdentificationRequest.setIdentifiedComponentId(componentID);
		codeMatchIdentificationRequest.setIdentifiedVersionId(versionID);
		codeMatchIdentificationRequest.setCodeMatchIdentificationDirective(CodeMatchIdentificationDirective.SNIPPET_AND_FILE);

		codeMatchIdentificationRequest.setIdentifiedUsageLevel(identifiedUsageLevel);
		codeMatchIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);

		return codeMatchIdentificationRequest;
	}
	@Override
	public String getInfo() {
		return "Code Match";
	}
	
}
