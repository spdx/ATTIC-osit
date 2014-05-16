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
package com.sec.ose.osi.data.match;

import java.util.ArrayList;
import java.util.HashMap;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * MatchedInfoMgr
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class MatchedInfoMgr {
	
	private static HashMap <String, FileSummary> fileSummaryMap = new HashMap <String, FileSummary>();
	private static HashMap <String, FolderSummary> folderSummaryMap = new HashMap <String, FolderSummary>();
	private HashMap<Integer, ArrayList<String>> identifiedFilesMap = null;
	private volatile static MatchedInfoMgr instance = null;

	public static MatchedInfoMgr getInstance() {
		if(instance == null) {
			synchronized(MatchedInfoMgr.class) {
				if(instance == null) {
					instance = new MatchedInfoMgr();
				}
			}
		}
		return instance;
	}
	
	public FileSummary getFileSummary(int matchType, String curProjectName, String path) {
		
		FileSummary fileSummary = null;
		if( !fileSummaryMap.containsKey(path) ) {
			fileSummaryMap.put(path, new FileSummary(path));
		}
		
		fileSummary = fileSummaryMap.get(path);
		fileSummary.clear();
		if(matchType == IdentificationConstantValue.STRING_MATCH_TYPE) {
			IdentificationDBManager.updateStringSearchInfoForSingleFile(curProjectName, path, fileSummary);
		} else if(matchType == IdentificationConstantValue.CODE_MATCH_TYPE) {
			IdentificationDBManager.updateCodeMatchInfoForSingleFile(curProjectName, fileSummary);
		}
		
		return fileSummaryMap.get(path); 
	}
	
	public MultipleFileSummary getMultipleFileSummary(
			int matchType,
			String pProjectName, 
			ArrayList<String> selectedPaths ) {
		
		MultipleFileSummary multipleFileSummary = new MultipleFileSummary(selectedPaths);
		
		if(matchType == IdentificationConstantValue.STRING_MATCH_TYPE) {
			IdentificationDBManager.updateStringSearchForMultiFileFolderView(pProjectName, selectedPaths, multipleFileSummary);
		} else if(matchType == IdentificationConstantValue.CODE_MATCH_TYPE) {
			IdentificationDBManager.updateCodeMatchForMultiFileFolderView(pProjectName, multipleFileSummary);
		}
		
		return multipleFileSummary; 
	}
	
	public FolderSummary getFolderSummary(
			int matchType, 
			String curProjectName, 
			String folderPath) {
		
		FolderSummary folderSummary = null;
		if( !folderSummaryMap.containsKey(folderPath) ) {
			folderSummaryMap.put(folderPath, new FolderSummary(folderPath));
		}
		
		folderSummary = folderSummaryMap.get(folderPath);
		folderSummary.clear();
		if(matchType == IdentificationConstantValue.STRING_MATCH_TYPE) {
			IdentificationDBManager.updateStringSearchFolderSummary(curProjectName, folderPath, folderSummary);
		} else if(matchType == IdentificationConstantValue.CODE_MATCH_TYPE) {
			IdentificationDBManager.updateCodeMatchFolderSummary(curProjectName, folderPath, folderSummary);
		}
		
		return folderSummaryMap.get(folderPath); 
	}
	
	public void loadIdentifiedFilesInfoToMemory(String projectName) {
		if(IdentifyMediator.getInstance().getSelectedProjectName().length() <= 0) {
			return;
		}
		identifiedFilesMap = IdentificationDBManager.loadIdentifiedFilesInfoToMemory(projectName);
	}

	public ArrayList<String> getIdentifiedFilePathListByCurrentMatchType() {
		return identifiedFilesMap.get(IdentifyMediator.getInstance().getSelectedMatchType());
		
	}
	
	public void resetForLogout() {
		if(fileSummaryMap != null) fileSummaryMap.clear();
		if(folderSummaryMap != null) folderSummaryMap.clear();
		if(identifiedFilesMap != null) identifiedFilesMap.clear();
	}
}
