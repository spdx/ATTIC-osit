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
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.report.common.ReportExcelFileManager;
import com.sec.ose.osi.report.standard.cover.StandardCoverSheetWriter;
import com.sec.ose.osi.report.standard.summary.StandardSummarySheetRow;
import com.sec.ose.osi.report.standard.summary.StandardSummarySheetWriter;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.Tools;

/**
 * StandardReportExcelFileManager
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class StandardReportExcelFileManager extends ReportExcelFileManager {
	private static Log log = LogFactory.getLog(StandardReportExcelFileManager.class);
	
	public StandardReportExcelFileManager  (
			String srcFilePath, 
			String destFilePath, 
			UIResponseObserver observer,
			String creatorName,
			String creatorEmail,
			String organizationName,
			String projectName)throws IOException  {
		
		super(
			srcFilePath, 
			destFilePath, 
			observer);
		
		// cover
		HSSFSheet coverSheet = getCoverSheet();
		StandardCoverSheetWriter.writeCoverSheet(
				coverSheet, 
				observer, 
				projectName,
				creatorName,
				creatorEmail,
				organizationName);
	}

    public HSSFSheet getIdentifySheetForProject(String projectname)  {
    	
    	HSSFSheet identifySheet = createIdentifyReportSheet(projectname);
    	
    	return identifySheet;
    }
    
	private static final String IDENTIFY_TEMPLETE_SHEET_NAME = "identify_template";
	
	private HSSFSheet createIdentifyReportSheet(String projectname) {
		// clone sheet .
		int cloneSheetIdx = mWorkBook.getSheetIndex(IDENTIFY_TEMPLETE_SHEET_NAME);
		HSSFSheet projectsSheet = mWorkBook.cloneSheet(cloneSheetIdx);
		if(!mWorkBook.isSheetVeryHidden(cloneSheetIdx)) mWorkBook.setSheetHidden(cloneSheetIdx, 2);
		
		// project name check .
		String sheetName = getUniqueSheetNameForProjectName(projectname);
	   
		int sheetIndex = mWorkBook.getSheetIndex(projectsSheet);
		mWorkBook.setSheetName(sheetIndex , sheetName);
		mWorkBook.setSheetOrder(sheetName, mWorkBook.getNumberOfSheets()-1);

		return projectsSheet;
	}

	public boolean removeSheetForOverwrite(String projectName)
	{
		// 1. Trim the projectName
		StringBuffer sheetName = new StringBuffer("");
		String strTemp = "";
		String newProjectName = projectName.replace("-", "_").replace(" ", "_");
		StringTokenizer st = new StringTokenizer(newProjectName, "_");
		try {
			String year = st.nextToken();
			Tools.transStringToInteger(year);
		
			if(year.length() == 2)
				while(st.hasMoreTokens()) {
					sheetName.append( st.nextToken()+"_" );
				} try {
					strTemp = sheetName.substring(0, sheetName.length()-1);
					sheetName.setLength(0);
					sheetName.append(strTemp); // remove last "_"	
				} catch(StringIndexOutOfBoundsException ex) {
					log.warn(ex.getMessage());
					JOptionPane.showMessageDialog(null, 
							"\""+projectName+"\" is not following the suggested naming rule.\n"+
							"You must modify your project name to satisfiy naming rule to earn OSI Report\n"+
							" ex) YY_Projectname (10_SampleProject)\n",
							"Report will not be generated.",
							JOptionPane.ERROR_MESSAGE
						);
				}
		} 
		catch(NumberFormatException e) {
			log.warn(e);
		}
		catch(NoSuchElementException e) {
			log.warn(e);
		}
		
		String curSheetName = sheetName.toString();
		
		for(int index=4; index<mWorkBook.getNumberOfSheets(); index++)
		{
			// 2. check the Sheet is duplicated.
			String exist_sheet = mWorkBook.getSheetName(index);	
			
			log.debug("test: "+exist_sheet + " for "+ curSheetName);
			if(exist_sheet.contains(curSheetName) == true) {
				log.debug("removing: "+exist_sheet);				
				// 3. delete the exist sheet
				mWorkBook.removeSheetAt(index);
				index--;
			}		
		}

		return true;
	}
	
	private static final int MAX_SHEET_NAME_LENGTH = 30;

	private String getUniqueSheetNameForProjectName(String projectName) {
		StringBuffer sheetName = new StringBuffer("");
		String strTemp = "";
		String newProjectName = projectName.replace("-", "_").replace(" ", "_");
		StringTokenizer st = new StringTokenizer(newProjectName, "_");
		try {
			String year = st.nextToken();
			Tools.transStringToInteger(year);			// NumberFormatException can be occurred.
			
			if(year.length() == 2)
			while(st.hasMoreTokens()) {
				sheetName.append( st.nextToken()+"_" );
			} 
			
			strTemp = sheetName.substring(0, sheetName.length()-1);
			sheetName.setLength(0);
			sheetName.append(strTemp); // remove last "_"	

		} catch(Exception e) {
			log.warn(e.getMessage());
			JOptionPane.showMessageDialog(null, 
					"\""+projectName+"\" is not following the suggested naming rule.\n"+
					"You must modify your project name to satisfiy naming rule to earn CRG Report\n"+
					" ex) YY_Projectname (10_SampleProject)\n",
					"Report will not be generated.",
					JOptionPane.ERROR_MESSAGE
					);
			sheetName.setLength(0);
			sheetName.append(projectName);
		}
		
		// sheet length
		if (sheetName.length() > MAX_SHEET_NAME_LENGTH) {
			strTemp = sheetName.substring(0, MAX_SHEET_NAME_LENGTH-1);
			sheetName.setLength(0);
			sheetName.append(strTemp);
		}
		
		// unique
		
		boolean isExisted = isExistedSheetName(sheetName.toString());
	
		int     id = 1;
		
		String orignalSheetName = sheetName.toString();
		while(isExisted == true) {
			sheetName.setLength(0);
			sheetName.append(orignalSheetName + id++);
			isExisted = isExistedSheetName(sheetName.toString());
		}

		log.debug("sheetName: "+sheetName);
		
		String revisedSheetName = sheetName.toString().replace(" ", "_");
		return revisedSheetName;
	}

	private static final String SUMMARY_SHEET_NAME = "Summary";

	public void writeScanSummarySheet(
			ArrayList<StandardSummarySheetRow> summarySheetRows) {
		
		HSSFSheet summarySheet = mWorkBook.getSheet(SUMMARY_SHEET_NAME);
		StandardSummarySheetWriter.write(summarySheet, summarySheetRows, this.mExcelStyle);
	}

	public static final int MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET = 30000;	// max row number is 65535, but limit of short is near 32000
	private int numOfIdentifiedFilesSheet=0;

	public HSSFSheet createIdentifiedFilesSheet() {

		numOfIdentifiedFilesSheet++;
		String sheetName = IDENTIFIED_FILES_SHEET_NAME+"_"+numOfIdentifiedFilesSheet;
		HSSFSheet xHSSFSheet = this.mWorkBook.createSheet(sheetName);
		mWorkBook.setSheetOrder(sheetName, this.mWorkBook.getNumberOfSheets()-1);
		
		return xHSSFSheet;
	}

	public static final String IDENTIFIED_FILES_SHEET_NAME = "IdentifiedFiles";
	
}
