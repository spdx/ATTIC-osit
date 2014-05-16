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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.airs.service.protex.AIRSProtexService;
import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.JPanIdentifyMain;
import com.sec.ose.osi.ui.frm.main.identification.UEIdentifyResetComment;
import com.sec.ose.osi.ui.frm.main.identification.codematch.UECodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.UEStringMatch;

/**
 * ActionIdentifyOrReset
 * @author sjh.yoo, hankido.lee, jae-yong.lee
 * 
 */
public class ActionIdentifyOrReset {
	private static Log log = LogFactory.getLog(AIRSProtexService.class);
	
	public static boolean requestIdentify(UIResponseObserver mObserver, UEIdentifyResetComment ueIdentification) {
		log.debug("Request Identify");
		
		int matchedType = ueIdentification.getMatchType();
		String projectName = ueIdentification.getProjectName();
		ArrayList<String> selectedPaths = ueIdentification.getSelectedPath();
		
		int curCount = 0;
		int selectedPathsSize = selectedPaths.size();
		for(String path:selectedPaths) {
			IdentifyData identifyData = null;
			int compositeType = matchedType;
			
			if(ueIdentification.isFile()==false) {		// folder
				compositeType = matchedType | IdentificationConstantValue.FOLDER_TYPE;
				if(path.equals("") == false) {
					path +="/";
				}
			}
			
			ArrayList<String> pendingFileListForIdentify = null ;
			switch(matchedType) {
	
				case IdentificationConstantValue.STRING_MATCH_TYPE:
					UEStringMatch ueStringMatch = (UEStringMatch) ueIdentification.getUeMatched();
					if(ueStringMatch == null) {
						return false;
					}
					pendingFileListForIdentify = IdentificationDBManager.getStringMatchFileList(
							projectName, 
							path, 
							ueStringMatch.getStringSearch(),
							AbstractMatchInfo.STATUS_PENDING);
					break;
					
				case IdentificationConstantValue.CODE_MATCH_TYPE:
					UECodeMatch ueCodeMatch = (UECodeMatch) ueIdentification.getUeMatched();
					pendingFileListForIdentify = IdentificationDBManager.getCodeMatchFileList(
							projectName, 
							path,
							ueCodeMatch.getOriginComponentName(),
							ueCodeMatch.getOriginVersionName(),
							ueCodeMatch.getOriginLicenseName(),
							AbstractMatchInfo.STATUS_PENDING);
					
					if(pendingFileListForIdentify.size() == 0 &&
							ueCodeMatch.getCurrentVersionName().equals("Unspecified")
							) {
						pendingFileListForIdentify = IdentificationDBManager.getCodeMatchFileList(
								projectName, 
								path,
								ueCodeMatch.getOriginComponentName(),
								"",
								ueCodeMatch.getOriginLicenseName(),
								AbstractMatchInfo.STATUS_PENDING);
					}
	
					break;
					
				case IdentificationConstantValue.PATTERN_MATCH_TYPE:
					pendingFileListForIdentify = IdentificationDBManager.getPatternMatchFileList(
							projectName, 
							path,
							AbstractMatchInfo.STATUS_PENDING);
					break;
			}
			
			int pendingFileListForIdentifySize = pendingFileListForIdentify.size();
			for(String curPath: pendingFileListForIdentify) {
				++curCount;
				identifyData = IdentifyMediator.getInstance().createIdentifyData(
						projectName,
						matchedType,
						compositeType, 
						curPath,
						ueIdentification.getUeMatched(),
						ueIdentification.getComment()
						);
				
				if(identifyData == null)
					return false;
				
				if(matchedType == IdentificationConstantValue.STRING_MATCH_TYPE && 
						(identifyData.getNewLicenseName() == null 
						 || identifyData.getNewLicenseName().equals("")) ) {
					return false;
				}

				IdentifyQueue.getInstance().enqueue(identifyData);
				
				if(selectedPathsSize > 1) { // JList selection
					mObserver.setMessageHeader("Identify processing...("+curCount+"/"+selectedPathsSize+")\n"); 
				} else { // JTree selection
					mObserver.setMessageHeader("Identify processing...("+curCount+"/"+pendingFileListForIdentifySize+")\n"); 
				}
				
				AbstractDiscoveryController discoveryController = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, matchedType);
				discoveryController.identifyFile(identifyData, mObserver);
			}
		}
		IdentifyMediator.getInstance().updateUIFrameWithAdditionalIdentifiedFiles(projectName, ueIdentification.getSelectedPath());
		MatchedInfoMgr.getInstance().loadIdentifiedFilesInfoToMemory(projectName);
		IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, matchedType);
		if(IdentifyMediator.getInstance().getIndexOfTreeOrList() == JPanIdentifyMain.INDEX_LIST) {
			IdentifyMediator.getInstance().getJListMatchedFile().changeSelectedIndex(IdentificationConstantValue.IDENTIFY_TYPE);
		}
		return true;
	}
	
	public static boolean requestReset(UIResponseObserver mObserver, UEIdentifyResetComment ueIdentification) {
		int selectedMatchType = ueIdentification.getMatchType();
		String projectName = ueIdentification.getProjectName();
		
		ArrayList<String> selectedPaths = ueIdentification.getSelectedPath();
		ArrayList<String> resetFileListForUIupdate = new ArrayList<String>();
		
		int curCount = 0;
		int selectedPathsSize = selectedPaths.size();
		for(String path:selectedPaths) {
			
			int compositeType = selectedMatchType | IdentificationConstantValue.RESET_TYPE;
			if(ueIdentification.isFile()==false) {
				compositeType |= IdentificationConstantValue.FOLDER_TYPE;
				
				if(path.equals("") == false) {
					path += "/";
				}
			}
			
			ArrayList<String> identifiedFileListForReset = null ;
			switch(selectedMatchType) {
	
				case IdentificationConstantValue.STRING_MATCH_TYPE:
					UEStringMatch ueStringMatch = (UEStringMatch) ueIdentification.getUeMatched();
					identifiedFileListForReset = IdentificationDBManager.getStringMatchFileList(
							projectName, 
							path, 
							ueStringMatch.getStringSearch(),
							AbstractMatchInfo.STATUS_IDENTIFIED);
					break;
					
				case IdentificationConstantValue.CODE_MATCH_TYPE:
					UECodeMatch ueCodeMatch = (UECodeMatch) ueIdentification.getUeMatched();
					identifiedFileListForReset = IdentificationDBManager.getCodeMatchFileList(
							projectName, 
							path, 
							ueCodeMatch.getOriginComponentName(), 
							ueCodeMatch.getOriginVersionName(), 
							ueCodeMatch.getOriginLicenseName(),
							AbstractMatchInfo.STATUS_IDENTIFIED);
					
					if(identifiedFileListForReset.size() == 0 &&
							ueCodeMatch.getOriginVersionName().equals("Unspecified")
							) {
						identifiedFileListForReset = IdentificationDBManager.getCodeMatchFileList(
								projectName, 
								path, 
								ueCodeMatch.getOriginComponentName(), 
								"", 
								ueCodeMatch.getOriginLicenseName(),
								AbstractMatchInfo.STATUS_IDENTIFIED);
					}
	
					break;
					
				case IdentificationConstantValue.PATTERN_MATCH_TYPE:
					identifiedFileListForReset = IdentificationDBManager.getPatternMatchFileList(
							projectName, 
							path,
							AbstractMatchInfo.STATUS_IDENTIFIED);
					break;
			}
			
			int identifiedFileListForResetSize = identifiedFileListForReset.size();
			for(String filePath : identifiedFileListForReset) {
				++curCount;
				IdentifyData resetData = IdentifyMediator.getInstance().createResetData(projectName, compositeType, filePath, ueIdentification.getUeMatched());
				if(resetData == null)
					return false;
				
				IdentifyQueue.getInstance().enqueue(resetData);
				
				if(selectedPathsSize > 1) { // JList selection
					mObserver.setMessageHeader("Reset processing...("+curCount+"/"+selectedPathsSize+")\n");
				} else { // JTree selection
					mObserver.setMessageHeader("Reset processing...("+curCount+"/"+identifiedFileListForResetSize+")\n");
				}
				ProjectDiscoveryControllerMap.getDiscoveryController(projectName, selectedMatchType)
											.resetFile(
													ueIdentification.getSelectedPath(), 
													resetData.getOriginComponentName(), 
													resetData.getOriginVersionName(),
													resetData.getMatchedFile(),
													mObserver,
													resetData.getCompositeType(),
													filePath,
													resetData.getCurrentComponentName());
				resetFileListForUIupdate.add(path);
			} 
		}
		IdentifyMediator.getInstance().resetUIFrame(resetFileListForUIupdate);
		MatchedInfoMgr.getInstance().loadIdentifiedFilesInfoToMemory(projectName);
		IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, selectedMatchType);
		if(IdentifyMediator.getInstance().getIndexOfTreeOrList() == JPanIdentifyMain.INDEX_LIST) {
			IdentifyMediator.getInstance().getJListMatchedFile().changeSelectedIndex(IdentificationConstantValue.RESET_TYPE);
		}
		return true;
	}
	
}
