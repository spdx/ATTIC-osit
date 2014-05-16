/**
 * Copyright(C) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.airs.domain.autoidentify.ProtexIdentificationInfo;
import com.sec.ose.airs.service.AutoIdentifyService;
import com.sec.ose.osi.data.match.CodeMatchInfo;
import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.airs.AIRSAPI;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.ActionIdentifyOrReset;
import com.sec.ose.osi.thread.ui_related.data.message.PrintStreamUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.UEIdentifyResetComment;
import com.sec.ose.osi.ui.frm.main.identification.codematch.UECodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.patternmatch.UEPatternMatch;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.UEStringMatch;

/**
 * SPDXAutoIdentifyController
 * @author sjh.yoo, ytaek.kim, jae-yong.lee
 * 
 */
public class SPDXAutoIdentifyController {
	private static Log log = LogFactory.getLog(SPDXAutoIdentifyController.class);
	AutoIdentifyService svc = new AutoIdentifyService();

	public AutoIdentifyResult startAutoIdentifyFromSPDX(UESPDXAutoIdentify ue, UIResponseObserver observer) {
		String projectName = ue.getProjectName();
		String projectId = ProjectAPIWrapper.getProjectID(projectName);

		/////////////////////////////////////////////////////////////
		// 1. get identification info from SPDX Documents
		observer.pushMessageWithHeader(" > Analyze SPDX Document(s) ...");
		AIRSAPI airsApi = new AIRSAPI();
		try {
			airsApi.init(ProtexSDKAPIManager.getMyProtexServer());
		} catch (Exception e) {
			log.error(e.getMessage());
			observer.setResult(UIResponseObserver.RESULT_FAIL);
			observer.setFailMessage("Protex SDK load failed \n" + e.getMessage());
			return null;
		}
		
		observer.pushMessageWithHeader(" > Analyze current project information ...");
		AutoIdentifyResult result = airsApi.autoIdentify(ue.getSPDXFilePathList(),projectId,new PrintStreamUIResponseObserver(observer));
		List<ProtexIdentificationInfo> protexIdentificationInfoList = airsApi.getInfoList();

		// 1-1. add component info in cache
		for(ProtexIdentificationInfo p:protexIdentificationInfoList) {
			if(!p.getComponentID().startsWith("c_")) {
				// Standard & Custom Component
				ComponentAPIWrapper.setComponentVersion(p.getComponent(), p.getComponentID(), p.getVersion(), p.getVersionID());
			}
		}
		
		///////////////////////////////////////////////////////////		
		// 2. Auto Identify!
		observer.setMessageHeader("SPDX Auto Identify processing ...\n");
		observer.pushMessageWithHeader(" > Start ...");
		identifyUsingSPDX(projectName, protexIdentificationInfoList, observer);

		observer.setResult(UIResponseObserver.RESULT_SUCCESS);
		return result;
	}

	public boolean identifyUsingSPDX(String projectName, List<ProtexIdentificationInfo> infoList, UIResponseObserver observer) {
		if (infoList == null || infoList.size() < 1)
			return false;

		int flowCnt = 0;
		int totalCount = infoList.size();
		
		for (ProtexIdentificationInfo info : infoList) {
			// Identify targetFile by identification info.
			
			String targetFile = info.getFilePath(); 
			String pPreMessage = "> "+ targetFile + " file identifing.. ";
			observer.pushMessageWithHeader(pPreMessage + "(" + (flowCnt++) + "/" + (totalCount) + ")");

			UIEntity matchedInfo = null;
			SelectedFilePathInfo selectedPaths = new SelectedFilePathInfo();
			selectedPaths.setSelectedFilePathInfo(SelectedFilePathInfo.SINGLE_FILE_TYPE, targetFile);
			String newComponentName = info.getComponent();
			String newVersionName = (info.getVersion()!=null)?info.getVersion():"";
			String newLicenseName = info.getLicense();
			
			int matchType = this.getIdentificationConstantValueByDiscoveryType(info.getDiscoveryType());
			if("Declared".equals(info.getResolutionType()))
				matchType = IdentificationConstantValue.PATTERN_MATCH_TYPE;
			
			switch(matchType) {
				case IdentificationConstantValue.STRING_MATCH_TYPE:
					{
						String stringSearch = info.getSearch();
						String componentName = "";
						String versionName = "";
						String licenseName = "";
						
						UEStringMatch xUEStringMatch = new UEStringMatch(
								stringSearch,
								componentName,
								versionName,
								licenseName,
								newComponentName,
								newVersionName,
								newLicenseName);
						
						matchedInfo = xUEStringMatch;
						break;
					}
				case IdentificationConstantValue.CODE_MATCH_TYPE:
					{
						String originComponentName = "";
						String originVersionName = "";
						String originLicenseName = "";
						String status = "";
						
						String matchedFile = info.getMatchedFile();
						String comment = IdentifyMediator.getInstance().getComment();
						
						int compositeType = IdentificationConstantValue.CODE_MATCH_TYPE;
						compositeType |= IdentificationConstantValue.STANDARD_COMPONENT_TYPE;

						FileSummary fileSummary = MatchedInfoMgr.getInstance().getFileSummary(matchType, projectName, targetFile);
						for (CodeMatchInfo codeMatchInfo : fileSummary.getCodeMatchInfoList()) {
							if (codeMatchInfo.getMatchedFile().equals(info.getMatchedFile())) {
								originComponentName = codeMatchInfo.getComponentName();
								originVersionName = codeMatchInfo.getVersionName();
								originLicenseName = codeMatchInfo.getLicenseName();
								status = String.valueOf(codeMatchInfo.getStatus());
								break;
							}
						}
						// Error handling
						if ((originComponentName + originVersionName + originLicenseName).length() == 0) {
							log.error("couldn't find original(local) code-matched data");
							continue;
						}
						
						if(newVersionName.equals("Unspecified")) newVersionName = "";
						
						UECodeMatch xUECodeMatch = new UECodeMatch(
								originComponentName,
								originVersionName,
								originLicenseName,
								originComponentName, //currentComponentName,
								originVersionName, //currentVersionName,
								originLicenseName, //currentLicenseName,
								newComponentName, 
								newVersionName,
								newLicenseName, 
								matchedFile, 
								compositeType,
								comment,
								status);
						
						matchedInfo = xUECodeMatch;
						break;
					}
				case IdentificationConstantValue.PATTERN_MATCH_TYPE:
					{
						String currentComponentName = "";
						currentComponentName = IdentificationDBManager.getComponentNameForPatternMatchFile(projectName, selectedPaths.getSelectedPath());
						
						if(currentComponentName == null) currentComponentName = "";
						if(newLicenseName == null) newLicenseName = "";
						UEPatternMatch xUEPatternMatch = new UEPatternMatch(
																			currentComponentName,
																			newComponentName, 
																			newLicenseName);
						matchedInfo = xUEPatternMatch;
						break;
					}
			}
			
			if(matchedInfo == null) {
				log.error("identifyBySPDXFileComment - no matchedInfo.");
				return false;
			}
			
			UEIdentifyResetComment ue = new UEIdentifyResetComment(
					projectName, 
					selectedPaths, 
					matchType, 
					matchedInfo,
					"");

			boolean result = ActionIdentifyOrReset.requestIdentify(observer, ue);

			if (!result) {
				log.error("identifyBySPDXFileComment Error - identification info: " + info.toString());
			}
		}
		
		return true;
	}	
	
	public int getIdentificationConstantValueByDiscoveryType(String discoveryType) {
		if ("Code Match".equals(discoveryType))
			return IdentificationConstantValue.CODE_MATCH_TYPE;
		else if ("String Search".equals(discoveryType))
			return IdentificationConstantValue.STRING_MATCH_TYPE;
		else if ("Other Supported Languages".equals(discoveryType))
			return IdentificationConstantValue.PATTERN_MATCH_TYPE;
		else {
			log.error("Unexpected IdentificationInfo discoveryType value: " + discoveryType);
			return -1;
		}
	}
	
	public String getLicenseNameFromLocalDB(String licenceId) {
		return LicenseAPIWrapper.getLicenseName(licenceId);
	}
}
