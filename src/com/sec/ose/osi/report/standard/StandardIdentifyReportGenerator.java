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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.report.common.ExcelStyle;
import com.sec.ose.osi.report.common.IdentifyReportGenerator;
import com.sec.ose.osi.report.common.ProjectInfoForIdentifyReport;
import com.sec.ose.osi.report.common.files.IdentifiedFilesSheet;
import com.sec.ose.osi.report.standard.files.StandardIdentifiedFilesSheetWriter;
import com.sec.ose.osi.report.standard.identify.StandardIdentifyReportSheet;
import com.sec.ose.osi.report.standard.identify.StandardIdentifyReportSheetFactory;
import com.sec.ose.osi.report.standard.identify.StandardIdentifySheetWriter;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.bom.IdentifiedFileEnt;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.report.ReportMediator;

/**
 * StandardIdentifyReportGenerator
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class StandardIdentifyReportGenerator extends IdentifyReportGenerator {
	private static Log log = LogFactory.getLog(StandardIdentifyReportGenerator.class);
	
	public void generateIdentifyReport(
			Collection<ProjectInfoForIdentifyReport> projectsInfo, 
			String sourceExcelFilename, 
			String targeExcelFilename,
			boolean insertIdentifiedFiles,
			String creatorName,
			String creatorEmail,
			String organizationName,
			UIResponseObserver observer
			
			) {
	
	   	 
 	   	 if(projectsInfo.size() < 1)
	   		 return;
	   	 
	   	 if(observer == null)
	   		 observer = new DefaultUIResponseObserver();
	   	 
	   	 
		// 1) open excel file
	   	 StandardReportExcelFileManager xReportFileManager = null;
	   	 String curProjectName = projectsInfo.iterator().next().getProjectName();
	   	 try {
	    		observer.setMessageHeader("Creating report cover \n");
				xReportFileManager = new StandardReportExcelFileManager(
														sourceExcelFilename ,
														targeExcelFilename, 
														observer,
														creatorName,
														creatorEmail,
														organizationName,
														curProjectName);
				
	   	 } catch (IOException e) {
				String message = "Cannot open file:\n" + e.getMessage();
				log.warn(message);
				observer.setFailMessage(message);
				return;
	   	 }
	   	ExcelStyle excelStyle = xReportFileManager.getExcelStyle();
	   	

	 	// 1.5) Summary Tab
	   	StandardScanSummaryReportGenerator.writeSummarySheet(
	   			ReportMediator.getInstance().getSelectedProjectList(), 
				targeExcelFilename, 
				observer,
				xReportFileManager);
	   	
	 	
	   	// 2) make arraylist for identified files information
	   	ArrayList<IdentifiedFileEnt> identifiedFiles = new ArrayList<IdentifiedFileEnt>();
	 	
	 	log.debug("Write Excel File to: "+targeExcelFilename);
		log.debug("    basedOn: "+sourceExcelFilename);
		
	   	// 3) write identify sheets
	   	int i=0;
	 	for(ProjectInfoForIdentifyReport curProjectInfo: projectsInfo) {
	 		String projectName = curProjectInfo.getProjectName();
	 		
	 		log.debug("    >> insert project: "+projectName);

    		String msgHead = "Creating identify report: - ["+(++i)+"/"+projectsInfo.size()+"] "+projectName+"\n";
    		observer.setMessageHeader(msgHead);
    		
    		StandardIdentifyReportSheet identifyReportSheet =  createIdentifySheet(
		   			curProjectInfo, 
		   			identifiedFiles,
					observer);
    		
    		if(identifyReportSheet == null) continue;
    		
    		HSSFSheet xHSSFSheet = xReportFileManager.getIdentifySheetForProject(projectName);
    		
    		StandardIdentifySheetWriter writer = new StandardIdentifySheetWriter();
    		
    		writer.write(xHSSFSheet, identifyReportSheet, excelStyle, observer);
	 	}
    	
	 	// 4) write identifiedFiles sheet
	 	
	 	if( insertIdentifiedFiles == true) {
	   	 	observer.pushMessage("Creating \"IdentifiedFiles\" sheet\n > Writing Excel File.");
			
	     	IdentifiedFilesSheet identifiedFilesSheet = 
	     		createIdentifiedFilesSheet(
	     				projectsInfo,
	     				identifiedFiles, 
	     				observer);

	     	int numOfRows = identifiedFilesSheet.getNumOfRows();
	   	 	int numOfSheets = (numOfRows / StandardReportExcelFileManager.MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET) + 1;


	   	 	for(int sheetNo = 0; sheetNo < numOfSheets; sheetNo++) {
	   	 	
		     	HSSFSheet xHSSFSheets = 
		     		xReportFileManager.createIdentifiedFilesSheet();
		     	StandardIdentifiedFilesSheetWriter writer = new StandardIdentifiedFilesSheetWriter();
		     	
		     	int startIndex = sheetNo*StandardReportExcelFileManager.MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET;
		    	writer.write(
		    			xHSSFSheets, 
		    			identifiedFilesSheet,
		    			startIndex,
		    			excelStyle, 
		    			observer);
	   	 	}
	 	}
		
	   	// 5) close the Report file
    	try {
			xReportFileManager.close();
			String message = "\""+targeExcelFilename+"\" has been created successfully." ;
			observer.setSuccessMessage(message);
		} catch (IOException e) {
			String message = "Cannot opne file:\n" + e.getMessage();
			log.warn(message);
			observer.setFailMessage(message);
			return;
		}
	}

	public StandardIdentifyReportSheet createIdentifySheet(
			ProjectInfoForIdentifyReport curProjectInfo, 
			ArrayList<IdentifiedFileEnt> globalIdentifiedFiles,
			UIResponseObserver observer) {

		String pMessage = "";


		if(curProjectInfo == null)
			return null;

		String projectName = curProjectInfo.getProjectName();    	  
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(projectID == null)
			return null;

		// 2) get Bill of Materials
		String message = " > Getting [BILL OF MATERIALS] information from server.";
		observer.pushMessageWithHeader(message);

		ArrayList<BOMEnt> boms = BOMReportAPIWrapper.getBillOfMeterialsFromProjectName(projectName, observer);
		log.debug("  bom: "+boms);		
		if(boms == null)
			return null;

		log.debug("  bom.size(): "+boms.size());
		
		// 3) get identified file info
		boolean usingCodeTree = false;
		log.debug("Get identifiedFileEnts from Report");
		ArrayList<IdentifiedFileEnt> fileEnt = getIdentifiedFiles(
				globalIdentifiedFiles,
				projectName,
				boms,
				usingCodeTree,
				observer);
		
		if(fileEnt == null) {
			log.debug("Failed to get identifiedFileEnts from Report");
			return null;
		}
				
		// 4)
		pMessage = " > Writing Excel File.\n";
		observer.pushMessageWithHeader(pMessage);
		StandardIdentifyReportSheet identifyReportSheet= 
			StandardIdentifyReportSheetFactory.createIdentifyReportSheet(
					projectName, 
					boms, 
					fileEnt,
					curProjectInfo.isAllFileListUp(),
					curProjectInfo.getfilePathListUpComponent(),
					usingCodeTree);

		return identifyReportSheet;
	}
}
