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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.FileDiscoveryPatternDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.DeclaredIdentification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.DeclaredIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationType;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCPatternMatch;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * PatternMatchIdentify
 * @author sjh.yoo, hankido.lee, suhyun47.kim, jae-yong.lee
 * 
 */
public class PatternMatchIdentify extends CommonIdentify {
	private static Log log = LogFactory.getLog(PatternMatchIdentify.class);
	
	public PatternMatchIdentify(
			IdentifyData pIdentifyData,
			String pProjectName,
			String pProjectID,
			boolean pIsFile,
			String pCurrentComponentName, 
			String pCurrentComponentID,
			String pCurrentVersionID,
			String pComment,
			LicenseInfo pLicenseInfo ) {
		this.identifyData = pIdentifyData;
		this.projectName = pProjectName;
		this.projectID = pProjectID;
		this.bFile = pIsFile;
		this.currentComponentName = pCurrentComponentName;
		this.currentComponentID = pCurrentComponentID;
		this.currentVersionID = pCurrentVersionID;
		this.comment = pComment;
		this.identifiedLicenseInfo = pLicenseInfo;
		
		this.setBase();
	}
	
	public void setBase() {
		super.setBase();
		identifiedUsageLevel = UsageLevel.COMPONENT;
	}
	
	public void setNewComponent() {
		
		String newComponentName = identifyData.getNewComponentName();
		String newVersionName = identifyData.getNewVersionName();
		if(identifiedLicenseInfo == null) return;
		String newLicenseID = identifiedLicenseInfo.getLicenseId();
		log.debug("newComponentName : "+newComponentName);
		log.debug("newVersionName : "+newVersionName);
		
		//ComponentVersion newComponentVersion = ComponentAPIWrapper.getComponentVersionByName(newComponentName,newVersionName, projectID, "");
		ComponentVersion newComponentVersion = ComponentAPIWrapper.getComponentVersionByName(newComponentName,newVersionName);
		if(newComponentVersion != null) {
			newComponentID = newComponentVersion.getComponentId();
			newVersionID = newComponentVersion.getVersionId();
		} else {
			newComponentID = ComponentAPIWrapper.getComponentId(projectID, newComponentName);
		}
		
		if ( newComponentID == null) {
			newComponentID = ComponentAPIWrapper.createLocalComponent(projectID, newComponentName, newLicenseID);
		}
	
		log.debug( "pattern match identify - newComponentID : " + newComponentID);
	}

	@Override
	public void identify() throws SdkFault {
		DeclaredIdentificationRequest oDeclaredIdentificationRequest = 
			getDeclaredIdentificationRequest( 
					curFilePath, 
					newComponentID, 
					newVersionID, 
					identifiedUsageLevel, 
					identifiedLicenseInfo);
		
		ProtexSDKAPIManager.getIdentificationAPI().addDeclaredIdentification(
				projectID, 
				curFilePath, 
				oDeclaredIdentificationRequest, 
				BomRefreshMode.SKIP);
		log.debug("Identified(PatternMatch) ผ๖วเ2 : " + curFilePath + " ==> " + oDeclaredIdentificationRequest.getIdentifiedComponentId() + "#" + oDeclaredIdentificationRequest.getIdentifiedVersionId());

		this.updateComment();
		this.updateDBnUI("identify");
	}

	@Override
	public void reset(Identification id) throws SdkFault {
		if(id.getType() != IdentificationType.DECLARATION) return;
		ProtexSDKAPIManager.getIdentificationAPI().removeDeclaredIdentification(projectID, (DeclaredIdentification) id, BomRefreshMode.SYNCHRONOUS);
		log.debug("reset("+getInfo()+") "+curFilePath+" complete");
		
		this.updateDBnUI("reset");
	}

	@Override
	public void updateDBnUI(String action) throws SdkFault {
		List<FileDiscoveryPatternDiscovery> listFileDiscoveryPatternDiscovery = ProtexSDKAPIManager.getDiscoveryAPI().getFileDiscoveryPatternDiscoveries(projectID, fileOnlyTree);
		boolean isRemain = ((DCPatternMatch)ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE)).resetFile(projectName, curFilePath, listFileDiscoveryPatternDiscovery);
		
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(isRemain && projectID != null && projectID.equals(projectID)) {
			IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, this.curFilePath, IdentificationConstantValue.PATTERN_MATCH_TYPE);
		}
	}

	private DeclaredIdentificationRequest getDeclaredIdentificationRequest( 
			String absolutePath, 
			String componentID, 
			String componentVersionID, 
			UsageLevel usageLevel, 
			LicenseInfo licenseInfo) {
		
		DeclaredIdentificationRequest oDeclaredIdentificationRequest = new DeclaredIdentificationRequest();

		oDeclaredIdentificationRequest.setPath(absolutePath);
		oDeclaredIdentificationRequest.setIdentifiedComponentId(componentID);
		oDeclaredIdentificationRequest.setIdentifiedVersionId(componentVersionID);
		oDeclaredIdentificationRequest.setIdentifiedUsageLevel(usageLevel);
		oDeclaredIdentificationRequest.setIdentifiedLicenseInfo(licenseInfo);
		

		return oDeclaredIdentificationRequest;
	}
	
	@Override
	public String getInfo() {
		return "Pattern Match";
	}

}
