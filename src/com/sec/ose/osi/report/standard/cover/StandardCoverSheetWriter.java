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
package com.sec.ose.osi.report.standard.cover;

import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.Version;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReport;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectEntForReportFactory;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.FormatUtil;

/**
 * StandardCoverSheetWriter
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class StandardCoverSheetWriter {
	private static final int PROJECT_ROW = 12;
	private static final int PROJECT_COL = 3;

	private static final int DATE_ROW = 12;
	private static final int DATE_COL =5;

	private static final int AUTHOR_ROW = 13;
	private static final int AUTHOR_COL = 3;
	
	private static final int GROUP_ROW = 14;
	private static final int GROUP_COL = 3;

	private static final int PROTEX_ROW = 15;
	private static final int PROTEX_COL = 3;
	
	public static void writeCoverSheet(
			HSSFSheet coverSheet, 
			UIResponseObserver observer, 
			String projectName,
			String creatorName,
			String creatorEmail,
			String organizationName) {
				
		if(coverSheet == null)
			return;
		
		HSSFRichTextString value = null;
		
		value = new HSSFRichTextString(projectName);
		coverSheet.getRow(PROJECT_ROW).getCell(PROJECT_COL).setCellValue(value);

		value = new HSSFRichTextString(FormatUtil.mDateFormat.format(Calendar.getInstance().getTime()));
		coverSheet.getRow(DATE_ROW).getCell(DATE_COL).setCellValue(value);

		value = new HSSFRichTextString(creatorEmail);
		coverSheet.getRow(AUTHOR_ROW).getCell(AUTHOR_COL).setCellValue(value);
		
		value = new HSSFRichTextString(organizationName);
		coverSheet.getRow(GROUP_ROW).getCell(GROUP_COL).setCellValue(value);
		
		ProjectEntForReport projectEnt = ProjectEntForReportFactory.createProjectEntforAnalysisSummary(projectName, observer);
		String version = projectEnt.getAnalysisProtexVersion();
		value = new HSSFRichTextString(version+"\n"+Version.getApplicationVersionInfo());
		coverSheet.getRow(PROTEX_ROW).getCell(PROTEX_COL).setCellValue(value);
	}
}
