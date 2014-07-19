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

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sec.ose.osi.Version;
import com.sec.ose.osi.report.standard.data.BillOfMaterialsRow;
import com.sec.ose.osi.report.standard.data.IdentifiedFilesRow;
import com.sec.ose.osi.report.standard.data.LicenseSummaryRow;
import com.sec.ose.osi.report.standard.data.SummaryRow;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;

/**
 * SheetsManager
 * @author sjh.yoo
 *
 */
public class SheetsManager {
	
	XSSFWorkbook wb = null;
	
	public XSSFWorkbook createWorkbook() {
		wb = new XSSFWorkbook();  // or new XSSFWorkbook();
		return wb;
	}
	
	public void createCoverSheet(String projectName, String creatorName, String creatorEmail, String organizationName) {
		// generate Cover Sheet
		CoverSheetTemplate cc = new CoverSheetTemplate(wb, "Cover", IndexedColors.RED.getIndex());
		
		ReportEntity analysisSummary = ReportAPIWrapper.getAnalysisSummary(projectName, null, true);
		String version = "Unknown";
		if(analysisSummary != null) {
			version = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.ANALYZED_RELEASE_DESCRIPTION);
		}
		
		cc.writeCoverSheet(projectName, creatorName, creatorEmail, organizationName, version+"\n"+Version.getApplicationVersionInfo());
		
	}

	public void writeSummaryRow(ArrayList<SummaryRow> summaryRowList) {
		// generate Summary Sheet
		SummarySheetTemplate summarySheetTemplate = new SummarySheetTemplate(wb, "Summary", IndexedColors.RED.getIndex());
		if(summaryRowList != null)
			summarySheetTemplate.writeSummaryRow(summaryRowList);
	}

	public void writeBillOfMaterialsRow(String projectName,
			ArrayList<BillOfMaterialsRow> billOfMaterialsRowList,
			ArrayList<LicenseSummaryRow> licenseSummaryRowList) {
		
		BillOfMaterialsSheetTemplate billOfMaterialsSheetTemplate
			= new BillOfMaterialsSheetTemplate(wb, projectName, IndexedColors.ROYAL_BLUE.getIndex());
		
		if(billOfMaterialsRowList != null)
			billOfMaterialsSheetTemplate.writeBillOfMaterialsRow(billOfMaterialsRowList);
		if(licenseSummaryRowList != null)
			billOfMaterialsSheetTemplate.writeLicenseRow(licenseSummaryRowList);
	}

	/**
	 * @param allProjectIdentifiedFilesRowList
	 */
	final int MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET = 30000;
	String IDENTIFIED_FILES_SHEET_NAME = "IdentifiedFiles";
	public void writeIdentifiedFilesRow(
			ArrayList<IdentifiedFilesRow> allProjectIdentifiedFilesRowList) {
		if(allProjectIdentifiedFilesRowList == null) return;
		
		IdentifiedFilesSheetTemplate identifiedFilesSheetTemplate = null;
		
     	int numOfRows = allProjectIdentifiedFilesRowList.size();
   	 	int numOfSheets = (numOfRows / MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET) + 1;
   	 	if(numOfSheets == 1) {
   	 		identifiedFilesSheetTemplate = new IdentifiedFilesSheetTemplate(wb, IDENTIFIED_FILES_SHEET_NAME, IndexedColors.DARK_GREEN.getIndex());
   	 		identifiedFilesSheetTemplate.writeRow(allProjectIdentifiedFilesRowList);
   	 	} else {
	   	 	for(int sheetNo = 0; sheetNo < numOfSheets; sheetNo++) {
	   	 		String sheetName = IDENTIFIED_FILES_SHEET_NAME + "_" + (sheetNo+1);
	   	 		identifiedFilesSheetTemplate = new IdentifiedFilesSheetTemplate(wb, sheetName, IndexedColors.DARK_GREEN.getIndex());
	   	 		
	   	 		ArrayList<IdentifiedFilesRow> tempIdentifiedFilesRowList = new ArrayList<IdentifiedFilesRow>();
	   	 		for(int i=0;i<MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET;i++) {
	   	 			tempIdentifiedFilesRowList.add(allProjectIdentifiedFilesRowList.remove(0));
	   	 			if(allProjectIdentifiedFilesRowList.size() == 0) break;
	   	 		}
	   	 		identifiedFilesSheetTemplate.writeRow(tempIdentifiedFilesRowList);
	   	 		
	   	 	}
   	 	}
   	 	
	}

}
