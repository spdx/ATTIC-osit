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
package com.sec.ose.osi.report.common.files;

import java.util.ArrayList;
import java.util.HashMap;

import com.sec.ose.osi.sdk.protexsdk.bom.CodeMatchEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.IdentifiedFileEnt;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * IdentifiedFilesSheetFactory
 * @author suhyun47.kim 
 * 
 */
public class IdentifiedFilesSheetFactory {

	public static IdentifiedFilesSheet createIdentifiedFilesSheet(
			ArrayList<IdentifiedFileEnt> identifiedFiles, 
			HashMap<String, CodeMatchEnt> codeMatchMap, UIResponseObserver observer) {
		
		IdentifiedFilesSheet codeMatchReportSheet = new IdentifiedFilesSheet();
		

		for(int i=0; i<identifiedFiles.size(); i++) {
			
			IdentifiedFileEnt curFileEnt = identifiedFiles.get(i);
			String key = curFileEnt.getProjectName()+"#"+curFileEnt.getFilePath();
			String comment = curFileEnt.getFileComment();
			String componentName = curFileEnt.getComponentName();
			String componentVersion = curFileEnt.getComponentVersion();
			String projectName = curFileEnt.getProjectName();
			String fullPath = curFileEnt.getFilePath();
			String license = curFileEnt.getFileLicense();
			String discoveryType = curFileEnt.getDiscoveryType();
			boolean isStringSearchOnly = curFileEnt.isStringSearchOnly();
			
			String url = null;
			String codeMatchCnt = "";
			
			if(IdentifiedFileEnt.CODE_MATCH.equals(curFileEnt.getDiscoveryType())) {
				CodeMatchEnt      curCodeMatchEnt = codeMatchMap.get(key);
				if(curCodeMatchEnt != null) {
					url = curCodeMatchEnt.getUrl();
					codeMatchCnt = curCodeMatchEnt.getCodeMatchCnt();
				}
			}
			
			observer.pushMessageWithHeader(" > "+fullPath);
			
			IdentifiedFilesSheetRow row = new IdentifiedFilesSheetRow(
					projectName, 
					fullPath, 
					url, 
					codeMatchCnt, 
					comment, 
					componentName,
					componentVersion,
					license,
					discoveryType,
					isStringSearchOnly
					
					);
			
			codeMatchReportSheet.addRow(row);
			
		}
		
		return codeMatchReportSheet;
	}

}
