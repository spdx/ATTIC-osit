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
import com.blackducksoftware.sdk.protex.common.StringSearchPatternOriginType;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationType;
import com.blackducksoftware.sdk.protex.project.codetree.identification.StringSearchIdentification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.StringSearchIdentificationRequest;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.util.Property;

/**
 * StringSearchIdentify
 * @author sjh.yoo, hankido.lee, suhyun47.kim, jae-yong.lee
 * 
 */
public class StringSearchIdentify extends CommonIdentify {
	private static Log log = LogFactory.getLog(StringSearchIdentify.class);
	
	private List<StringSearchPatternOriginType> searchOnly = new ArrayList<StringSearchPatternOriginType>(1);
	
	public StringSearchIdentify(
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
		identifiedUsageLevel = UsageLevel.SNIPPET;
		/** String Search **/
		searchOnly.add(StringSearchPatternOriginType.STANDARD);
		searchOnly.add(StringSearchPatternOriginType.CUSTOM);
	}
	
	public void setNewComponent() {
		String newVersionName = identifyData.getNewVersionName();
		String newComponentName = identifyData.getNewComponentName();
		if(identifiedLicenseInfo == null) return;
		String newLicenseID = identifiedLicenseInfo.getLicenseId();
		log.debug("newComponentName : "+newComponentName+" , newVersionName : "+newVersionName);
		if ( (newVersionName.length() == 0) || (newVersionName.toLowerCase().equals("unspecified")) ) {
			newComponentID = ComponentAPIWrapper.getComponentId(projectID, newComponentName);
			if ( newComponentID == null) {
				newComponentID = ComponentAPIWrapper.createLocalComponent(projectID, newComponentName, newLicenseID);
			}
		} else {
			//ComponentVersion newComponentVersion = ComponentAPIWrapper.getComponentVersionByName(newComponentName,newVersionName, projectID, "");
			ComponentVersion newComponentVersion = ComponentAPIWrapper.getComponentVersionByName(newComponentName,newVersionName);
			if(newComponentVersion != null) {
				newComponentID = newComponentVersion.getComponentId();
				newVersionID = newComponentVersion.getVersionId();
			} else {
				newComponentID = ComponentAPIWrapper.getComponentId(projectID, newComponentName);
			}
		}
		
		log.debug("newComponentID : "+newComponentID+" , newVersionID : "+newVersionID);
	}
	
	@Override
	public void identify() throws SdkFault {
		
		BomRefreshMode refreshMode = BomRefreshMode.ASYNCHRONOUS;
		
		String optionRefreshMode = Property.getInstance().getProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH);
		if("true".equals(optionRefreshMode.toLowerCase())) {
			refreshMode = BomRefreshMode.SYNCHRONOUS;
		}
		
		List<StringSearchDiscovery> listStringSearchDiscovery = ProtexSDKAPIManager.getDiscoveryAPI().getStringSearchDiscoveries(projectID, fileOnlyTree, searchOnly);
		for (StringSearchDiscovery oStringSearchDiscovery : listStringSearchDiscovery) {
			int matchLocationCount = oStringSearchDiscovery.getMatchLocations().size();
			for(int i=0; i<matchLocationCount; i++) {
				IdentificationStatus identificationStatus = oStringSearchDiscovery.getMatchLocations().get(i).getIdentificationStatus();
				if(identificationStatus == IdentificationStatus.PENDING_IDENTIFICATION) {
					StringSearchIdentificationRequest oStringSearchIdentificationRequest = getStringSearchIdentificationRequest(newComponentID, newVersionID, identifiedUsageLevel, identifiedLicenseInfo, false, oStringSearchDiscovery);
					ProtexSDKAPIManager.getIdentificationAPI().addStringSearchIdentification(
							projectID, 
							oStringSearchDiscovery.getFilePath(), 
							oStringSearchIdentificationRequest,
							refreshMode);
							//BomRefreshMode.SYNCHRONOUS);
					break;
				}
			}
		}
		
		this.updateComment();
		this.updateDBnUI("identify");
	}

	@Override
	public void reset(Identification id) throws SdkFault {
		
		BomRefreshMode refreshMode = BomRefreshMode.ASYNCHRONOUS;
		
		String optionRefreshMode = Property.getInstance().getProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH);
		if("true".equals(optionRefreshMode.toLowerCase())) {
			refreshMode = BomRefreshMode.SYNCHRONOUS;
		}

		
		if(id.getType() != IdentificationType.STRING_SEARCH) return;
		String identifiedVersionId = id.getIdentifiedVersionId();
		if(identifiedVersionId == null) {
			identifiedVersionId = "";
		}

		if( (currentVersionID.equals(identifiedVersionId)) || (identifiedVersionId.length() <= 0) ) {
			ProtexSDKAPIManager.getIdentificationAPI().removeStringSearchIdentification(
					projectID, 
					(StringSearchIdentification) id,
					refreshMode);
			log.debug("reset("+getInfo()+") "+curFilePath+" complete");
		}
		this.updateDBnUI("reset");
	}

	@Override
	public void updateDBnUI(String action) throws SdkFault {
	}

	private StringSearchIdentificationRequest getStringSearchIdentificationRequest(String componentID, String VersionID, UsageLevel identifiedUsageLevel, LicenseInfo identifiedLicenseInfo, boolean isFolderLevelIdentification, StringSearchDiscovery discovery) {
		StringSearchIdentificationRequest oStringSearchIdentificationRequest = new StringSearchIdentificationRequest();

		oStringSearchIdentificationRequest.setIdentifiedComponentId(componentID);
		oStringSearchIdentificationRequest.setIdentifiedVersionId(VersionID); 
		oStringSearchIdentificationRequest.setIdentifiedUsageLevel(identifiedUsageLevel);
		oStringSearchIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);
		oStringSearchIdentificationRequest.setFolderLevelIdentification(isFolderLevelIdentification);
		oStringSearchIdentificationRequest.setStringSearchId(discovery.getStringSearchId());
		oStringSearchIdentificationRequest.getMatchLocations().addAll(discovery.getMatchLocations());

		return oStringSearchIdentificationRequest;
	}

	@Override
	public String getInfo() {
		return "String Search";
	}

}
