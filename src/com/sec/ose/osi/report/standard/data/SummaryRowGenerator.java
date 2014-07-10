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
package com.sec.ose.osi.report.standard.data;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReport;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReportFactory;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * SummaryRowGenerator
 * @author sjh.yoo
 *
 */
public class SummaryRowGenerator {

	private static Log log = LogFactory.getLog(SummaryRowGenerator.class);
	
	ArrayList<ProjectInfoForIdentifyReport> projectsInfo;
	UIResponseObserver observer;

	/**
	 * @param projectsInfo
	 * @param creatorName
	 * @param creatorEmail
	 * @param organizationName
	 * @param observer
	 */
	public SummaryRowGenerator(ArrayList<ProjectInfoForIdentifyReport> projectsInfo,
			UIResponseObserver observer) {
		super();
		this.projectsInfo = projectsInfo;
		this.observer = observer;
	}
	
	public ArrayList<SummaryRow> createSummaryRow() {
		
		ArrayList<SummaryRow> summarySheetRows = new ArrayList<SummaryRow>(2);
		String projectName = "";

		try {
			
			int numOfProject = projectsInfo.size();
			int index=0;
			for(ProjectInfoForIdentifyReport projectInfo:projectsInfo) {
				projectName = projectInfo.getProjectName();
				if(!ProjectAPIWrapper.isExistedProjectName(projectName))
					continue;
				
				String msgHead = "["+(++index)+"/"+numOfProject+"] Working on "+projectName + "\n";
				observer.setMessageHeader(msgHead);
				
				SummaryRow summaryRow = createSummaryRow(projectName, observer);
				if(summaryRow != null) summarySheetRows.add(summaryRow);
			}

			projectName = "Total";	// for Error
			SummaryRow totalRow = createTotalRow(summarySheetRows, observer);
			if(totalRow != null) summarySheetRows.add(totalRow);	
			
		} catch (RemoteException e) {
			log.warn(e);
			String message =
				"Error on generation summary at \"" + projectName +"\"\n"+ 
				e.getMessage();
			observer.setFailMessage(message);
			return null;
		}

		return summarySheetRows;
		
	}

	private static SummaryRow createSummaryRow(String projectName, UIResponseObserver observer) throws RemoteException {

		observer.pushMessageWithHeader(" > Getting project information\n");
		ProjectEntForReport projectEnt = ProjectEntForReportFactory.createProjectEnt(projectName, observer);
		
		if(projectEnt == null) {
			log.debug("project: "+projectName+ " is null ");
			return null;
		}
		
		observer.pushMessageWithHeader(" > Writing...");

		SummaryRow summaryRow = new SummaryRow("", projectName, 
				projectEnt.getScanDate(), projectEnt.getScanTime(),
				projectEnt.getOriginPendingFileNum(), projectEnt.getNumOfTotalFiles(),
				projectEnt.getNumOfSkippedFiles(), projectEnt.getBytes(),
				projectEnt.getNumOfAlreadyIdentifiedFiles(),
				projectEnt.getCurrentPendingFileNum()
			);

		return summaryRow;
	}

	private static SummaryRow createTotalRow(ArrayList<SummaryRow> summarySheetRows, UIResponseObserver observer) {

		observer.pushMessage("Caculating total...");

		long totalScanDurationSeconds = 0;
		
		int	totalPendingFiles = 0;
		int	totalTotalFiles = 0;
		int	totalSkippedFiles = 0;	
		long totalBytes = 0;
		int totalIdentifiedFiles = 0;
		int totalCurPendingFiles = 0;
		
		for(SummaryRow summaryRow:summarySheetRows) {
			totalScanDurationSeconds += summaryRow.getScanTime();
			totalPendingFiles 	+= summaryRow.getPendingFileCount();
			totalTotalFiles 	+= summaryRow.getTotalFileCount();
			totalSkippedFiles 	+= summaryRow.getExceptionalFileCount();
			totalBytes 		+= summaryRow.getBytes();
			totalIdentifiedFiles += summaryRow.getTotalIdentifiedFileCount();
			totalCurPendingFiles += summaryRow.getCurrentPendingFileCount();
		
		}
		
		SummaryRow summaryRow = new SummaryRow("", "TOTAL", 
				"-", totalScanDurationSeconds, totalPendingFiles, 
				totalTotalFiles, totalSkippedFiles, totalBytes, 
				totalIdentifiedFiles, totalCurPendingFiles
			);

		
		return summaryRow;
	}
	
}
