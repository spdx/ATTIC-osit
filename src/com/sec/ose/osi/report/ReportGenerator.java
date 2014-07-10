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
package com.sec.ose.osi.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sec.ose.osi.report.standard.SheetsManager;
import com.sec.ose.osi.report.standard.data.BillOfMaterialsRow;
import com.sec.ose.osi.report.standard.data.BillOfMaterialsRowGenerator;
import com.sec.ose.osi.report.standard.data.IdentifiedFilesRow;
import com.sec.ose.osi.report.standard.data.LicenseSummaryRow;
import com.sec.ose.osi.report.standard.data.ProjectInfoForIdentifyReport;
import com.sec.ose.osi.report.standard.data.SummaryRowGenerator;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * ReportGenerator
 * @author sjh.yoo
 *
 */
public class ReportGenerator {

	/**
	 * @param projectsInfo
	 * @param sourceExcelFilename
	 * @param targeExcelFilename
	 * @param creatorName
	 * @param creatorEmail
	 * @param organizationName
	 * @param observer
	 */
	public void generateIdentifyReport(
			ArrayList<ProjectInfoForIdentifyReport> projectsInfo,
			String sourceExcelFilename, String targeExcelFilename,
			String creatorName, String creatorEmail, String organizationName,
			UIResponseObserver observer) {
		
		SheetsManager reportSheetManager = new SheetsManager();
		
		String ProjectName = projectsInfo.get(0).getProjectName();
		for(int i=1;i<projectsInfo.size();i++) {
			ProjectInfoForIdentifyReport pInfo = projectsInfo.get(i);
			ProjectName += "\n"+pInfo.getProjectName();
		}
		XSSFWorkbook wb = reportSheetManager.createWorkbook();
		try {
			
			// 1. create cover sheet
			reportSheetManager.createCoverSheet(ProjectName, creatorName, creatorEmail, organizationName);
			
			// 2. create Summary Sheet
			SummaryRowGenerator summaryRowGen = new SummaryRowGenerator(projectsInfo, observer);
			reportSheetManager.writeSummaryRow(summaryRowGen.createSummaryRow());
			
			// 3. create Bill of Materials Row
			ArrayList<IdentifiedFilesRow> allProjectIdentifiedFilesRowList = new ArrayList<IdentifiedFilesRow>();
			for(ProjectInfoForIdentifyReport projectInfo:projectsInfo) {
				BillOfMaterialsRowGenerator billOfMaterialsRowGen = new BillOfMaterialsRowGenerator();
				ArrayList<BillOfMaterialsRow> BillOfMaterialsRowList 
					= billOfMaterialsRowGen.createBillOfMaterialsRowList(projectInfo, observer);
				ArrayList<LicenseSummaryRow> licenseSummaryRowList = billOfMaterialsRowGen.getLicenseSummaryRowList();
				ArrayList<IdentifiedFilesRow> tmpIdentifiedFilesRow = billOfMaterialsRowGen.getIdentifiedFilesRowList();
				if(tmpIdentifiedFilesRow != null) allProjectIdentifiedFilesRowList.addAll(tmpIdentifiedFilesRow);
				
				reportSheetManager.writeBillOfMaterialsRow(projectInfo.getProjectName(),BillOfMaterialsRowList,licenseSummaryRowList);
			}

			// 4. create Identified Files Row
			reportSheetManager.writeIdentifiedFilesRow(allProjectIdentifiedFilesRowList);
			
			// 5. write file
		    FileOutputStream fileOut = new FileOutputStream(targeExcelFilename);
		    wb.write(fileOut);
		    fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
