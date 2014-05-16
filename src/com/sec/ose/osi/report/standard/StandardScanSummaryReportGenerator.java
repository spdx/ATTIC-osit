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
package com.sec.ose.osi.report.standard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.report.standard.summary.StandardSummarySheetRow;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReport;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReportFactory;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.Tools;

/**
 * StandardScanSummaryReportGenerator
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StandardScanSummaryReportGenerator {
	private static Log log = LogFactory.getLog(StandardScanSummaryReportGenerator.class);
	
	StandardSummarySheetRow TotalSheetRow = new StandardSummarySheetRow();
	
	public static void writeSummarySheet(
			Collection<String> projectNames,
			String savedFileLocation,
			UIResponseObserver observer,
			StandardReportExcelFileManager reportGenerator) {
		
		ArrayList<StandardSummarySheetRow> summarySheetRows = new ArrayList<StandardSummarySheetRow>();
		
		Iterator<String> projects = projectNames.iterator();
		
		String sheetName = null;
		try {
			
			int numOfProject = projectNames.size();
			int index=0;
			
			while(projects.hasNext()) {
		
				index++;
				
				String projectName = projects.next();
				if(ProjectAPIWrapper.isExistedProjectName(projectName) ==false)
					continue;
			
				String msgHead = "["+index+"/"+numOfProject+"] Working on "+projectName + "\n";
				observer.setMessageHeader(msgHead);
				
				sheetName = projectName;
				StandardSummarySheetRow sheetRow = null;
				sheetRow = createSummaryRow(sheetName, observer);
				if(sheetRow != null)
					summarySheetRows.add(sheetRow);
			}
			
			sheetName = "Total";
			StandardSummarySheetRow totalRow = createTotalRow(summarySheetRows, observer);
			if(totalRow != null)
				summarySheetRows.add(totalRow);	
			
		} catch (RemoteException e) {
			log.warn(e);
			String message =
				"Error on generation summary at \"" + sheetName +"\"\n"+ 
				e.getMessage();
			observer.setFailMessage(message);
			return;
		}
		
		observer.pushMessage("writing file...");
		try {
			reportGenerator.writeScanSummarySheet(summarySheetRows);
		} catch (Exception e) {
			log.warn(e);
			String message = "Fail to open file: \n"+e.getMessage();
			observer.setFailMessage(message);
			return;
		} 
		
		observer.setSuccessMessage("\""+ savedFileLocation + "\" has been generated successfully.");
		
	}

	private static StandardSummarySheetRow createSummaryRow(String projectName, UIResponseObserver observer) throws RemoteException {

		StandardSummarySheetRow sheetRow = new StandardSummarySheetRow();

		observer.pushMessageWithHeader(" > Getting project information\n");
		ProjectEntForReport projectEnt = ProjectEntForReportFactory.createProjectEnt(projectName, observer);
		
		if(projectEnt == null) {
			log.debug("project: "+projectName+ " is null ");
			return null;
		}
		
		observer.pushMessageWithHeader(" > Writing...");
		
		sheetRow.setValue(StandardSummarySheetRow.PROJECT_NAME, projectName);
		sheetRow.setValue(StandardSummarySheetRow.SCAN_DATE, String.valueOf(projectEnt.getScanDate()));
		sheetRow.setValue(StandardSummarySheetRow.SCAN_DURATION, projectEnt.getScanDuration());
		sheetRow.setValue(StandardSummarySheetRow.PENDING_FILE_COUNT, String.valueOf(projectEnt.getOriginPendingFileNum()) );
		sheetRow.setValue(StandardSummarySheetRow.PENDING_FILE_PERCENT, projectEnt.getOriginPendingRatio());
		sheetRow.setValue(StandardSummarySheetRow.TOTAL_FILE_COUNT, String.valueOf( projectEnt.getNumOfTotalFiles()) );
		sheetRow.setValue(StandardSummarySheetRow.SKIPPED_FILE_COUNT, String.valueOf(projectEnt.getNumOfSkippedFiles()) );
		sheetRow.setValue(StandardSummarySheetRow.TOTAL_BYTES, String.valueOf(projectEnt.getBytes()) );
		sheetRow.setValue(StandardSummarySheetRow.IDENTIFIED_FILE_COUNT, String.valueOf( projectEnt.getNumOfAlreadyIdentifiedFiles()) );
		sheetRow.setValue(StandardSummarySheetRow.CURRENT_PENDING_FILE_COUNT, projectEnt.getCurrentPendingInformation());
		
		log.debug(sheetRow+"\n\n\n----");
		return sheetRow;
	}

	private static StandardSummarySheetRow createTotalRow(ArrayList<StandardSummarySheetRow> summarySheetRows, UIResponseObserver observer) {

		StandardSummarySheetRow sheetRow = new StandardSummarySheetRow();

		observer.pushMessage("Caculating total...");
		Iterator<StandardSummarySheetRow> iterator = summarySheetRows.iterator();
		
		int	totalScanDurationSeconds=0;
		
		int	totalPendingFiles=0;
		int	totalTotalFiles=0;
		int	totalSkippedFiles=0;	
		long totalBytes=0;
		int totalIdentifiedFiles=0;
		int totalCurPendingFiles=0;
		
		
		while(iterator.hasNext()) {
			StandardSummarySheetRow curRow = iterator.next();
			
			totalScanDurationSeconds += convertDurationStringtoSecondsInt(curRow.getValue(StandardSummarySheetRow.SCAN_DURATION));
			totalPendingFiles 	+= Tools.transStringToInteger(curRow.getValue(StandardSummarySheetRow.PENDING_FILE_COUNT));
			totalTotalFiles 	+= Tools.transStringToInteger(curRow.getValue(StandardSummarySheetRow.TOTAL_FILE_COUNT));
			totalSkippedFiles 	+= Tools.transStringToInteger(curRow.getValue(StandardSummarySheetRow.SKIPPED_FILE_COUNT));
			totalBytes 		+= Tools.transStringToInteger(curRow.getValue(StandardSummarySheetRow.TOTAL_BYTES));
			totalIdentifiedFiles += Tools.transStringToInteger(curRow.getValue(StandardSummarySheetRow.IDENTIFIED_FILE_COUNT));
			System.out.println(curRow.getValue(StandardSummarySheetRow.CURRENT_PENDING_FILE_COUNT));
			StringTokenizer st = new StringTokenizer(curRow.getValue(StandardSummarySheetRow.CURRENT_PENDING_FILE_COUNT), " ");
			totalCurPendingFiles += Tools.transStringToInteger(st.nextToken());
		
		}
		
		sheetRow.setValue(StandardSummarySheetRow.PROJECT_NAME, "TOTAL");
		sheetRow.setValue(StandardSummarySheetRow.SCAN_DATE, "-");
		sheetRow.setValue(StandardSummarySheetRow.SCAN_DURATION,convertSecondsIntToDurationString(totalScanDurationSeconds));
		sheetRow.setValue(StandardSummarySheetRow.PENDING_FILE_COUNT, String.valueOf(totalPendingFiles));	
		
		String totalPendingPercentage = null;
		if(totalPendingFiles == 0)
		 totalPendingPercentage = "(0%)";
		else if(totalPendingFiles >0 )
		 totalPendingPercentage = "(" + String.format("%.2f", totalPendingFiles*100.0/totalTotalFiles)+ "%)";
		
		sheetRow.setValue(StandardSummarySheetRow.PENDING_FILE_PERCENT, totalPendingPercentage);
		sheetRow.setValue(StandardSummarySheetRow.TOTAL_FILE_COUNT, String.valueOf(totalTotalFiles));
		sheetRow.setValue(StandardSummarySheetRow.SKIPPED_FILE_COUNT, String.valueOf(totalSkippedFiles));
		sheetRow.setValue(StandardSummarySheetRow.TOTAL_BYTES, String.valueOf(totalBytes));
		sheetRow.setValue(StandardSummarySheetRow.IDENTIFIED_FILE_COUNT, String.valueOf(totalIdentifiedFiles));
		
		int totalPendingRatio = (int)((double)totalCurPendingFiles / (double)(totalIdentifiedFiles + totalCurPendingFiles) * 100);
		if(totalPendingRatio == 0 && totalCurPendingFiles != 0) {
			totalPendingRatio = 1;
		}
		
		sheetRow.setValue(StandardSummarySheetRow.CURRENT_PENDING_FILE_COUNT, totalCurPendingFiles + 
				" (" + totalPendingRatio + "%)");
		return sheetRow;
	}
	
	private static int convertDurationStringtoSecondsInt(String tempDuration) {
		int temp_hour 	= 0;
		int temp_minute = 0;
		int temp_second = 0;
		
		if(tempDuration == null)
			return 0;
		
	    StringTokenizer st = new StringTokenizer(tempDuration, ":");
	    try {
	    	
	    	temp_hour = Tools.transStringToInteger(st.nextToken());
	    	temp_minute = Tools.transStringToInteger(st.nextToken());
	    	temp_second = Tools.transStringToInteger(st.nextToken());
	    	
	    } catch(NumberFormatException e) {
	    	log.warn(e);
	    	return 0;
	    } catch(Exception e) {
	    	log.warn(e);
	    	return 0;
	    }
	    
	    int totalSeconds = (temp_hour * 60  * 60)+
	                       (temp_minute * 60) 
	                       + temp_second;
	    
	    return totalSeconds;
	    
	}

	private static String convertSecondsIntToDurationString(int tempDuration) {
		int temp_hour 	= tempDuration / (60*60);
		int temp_minute = (tempDuration % (60*60) ) / 60;
		int temp_second = tempDuration % 60;
		
		String str_hour = ""+temp_hour;
		String str_min = ""+temp_minute;
		String str_sec = ""+temp_second;
		
		if(str_sec.length()<2)
			str_sec = "0" + str_sec;
		if(str_min.length()<2)
			str_min = "0" + str_min;
		if(str_hour.length()<2)
			str_hour = "0" + str_hour;
		
		
		String totalDuration = str_hour + ":" + str_min + ":" + str_sec;
		return totalDuration;
	}
}

