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
package com.sec.ose.osi.report.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.report.common.files.IdentifiedFilesSheet;
import com.sec.ose.osi.report.common.files.IdentifiedFilesSheetFactory;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.bom.CodeMatchEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.IdentifiedFileEnt;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * IdentifyReportGenerator
 * @author suhyun47.kim, sjh.yoo, hankido.lee 
 * 
 */
public abstract class IdentifyReportGenerator {
	
	private static Log log = LogFactory.getLog(IdentifyReportGenerator.class);
	
	public abstract void generateIdentifyReport(
			Collection<ProjectInfoForIdentifyReport> projectsInfo, 
			String sourceExcelFilename, 
			String targeExcelFilename,
			boolean insertIdentifiedFiles,
			String creatorName,
			String creatorEmail,
			String organizationName,
			UIResponseObserver observer
			);

	protected ArrayList<IdentifiedFileEnt> getIdentifiedFiles(
			ArrayList<IdentifiedFileEnt> globalIdentifiedFiles,
			String projectName,
			ArrayList<BOMEnt> boms,
			boolean usingCodeTree,
			UIResponseObserver observer) {
		
		String message = " > Getting [IDENTIFIED FILES] information from server.\n";
		observer.pushMessageWithHeader(message);

		log.debug(message);

		ArrayList<IdentifiedFileEnt> fileEnt = null;

		log.debug("using code tree: "+usingCodeTree);

		if(usingCodeTree == false) {
			log.debug("   fast but need much memory");
			fileEnt = BOMReportAPIWrapper.getIdentifiedFilesFromBOMReport(projectName, observer);
		}
		else {
			log.debug("   slow but need less memory");  
			fileEnt = BOMReportAPIWrapper.getIdentifiedFilesFromCodeTree(projectName, observer);
		}
		
		if(fileEnt == null) {
			observer.pushMessageWithHeader("Cannot obtain \"Identified Files\" information form server.");
		} else {
			Iterator<IdentifiedFileEnt> itr = fileEnt.iterator();
			while(itr.hasNext()) {
				IdentifiedFileEnt curEnt = itr.next();
				globalIdentifiedFiles.add(curEnt);
			}
		}
		
		return fileEnt;

	}
	
	protected static IdentifiedFilesSheet createIdentifiedFilesSheet(
			Collection<ProjectInfoForIdentifyReport> projectsInfo, 
			ArrayList<IdentifiedFileEnt> identifiedFiles, 
			UIResponseObserver observer) {

		String pMessage = "";

		HashMap<String, CodeMatchEnt> codeMatchMap = new HashMap<String, CodeMatchEnt>();

		// 2) generate CodeMatchReport sheet
		int i=0;
		for(ProjectInfoForIdentifyReport projectInfo: projectsInfo) {


			String projectName = projectInfo.getProjectName();
			String msgHead = "Creating Code match sheet: - ["+(++i)+"/"+ projectsInfo.size()+"] "+projectName+"\n";

			pMessage = msgHead+" > Getting [CodeMatches] information from server.\n";
			observer.pushMessage(pMessage);

			Iterator<CodeMatchEnt> itrCodeMatches = BOMReportAPIWrapper.getCodeMatches(projectName,observer);
			if(itrCodeMatches == null)
				continue;

			while(itrCodeMatches.hasNext()) {
				CodeMatchEnt curEnt = itrCodeMatches.next();
				String key = projectName+"#"+curEnt.getFullPath();
				codeMatchMap.put(key, curEnt);
			}

		}
		IdentifiedFilesSheet identifiedFilesSheet = 
			IdentifiedFilesSheetFactory.createIdentifiedFilesSheet(identifiedFiles, codeMatchMap, observer);

		return identifiedFilesSheet;

	}

}
