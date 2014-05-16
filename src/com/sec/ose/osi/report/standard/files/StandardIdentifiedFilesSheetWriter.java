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
package com.sec.ose.osi.report.standard.files;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.report.common.ExcelStyle;
import com.sec.ose.osi.report.common.files.IdentifiedFilesSheet;
import com.sec.ose.osi.report.common.files.IdentifiedFilesSheetRow;
import com.sec.ose.osi.report.standard.StandardReportExcelFileManager;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * StandardIdentifiedFilesSheetWriter
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StandardIdentifiedFilesSheetWriter {
	private static Log log = LogFactory.getLog(IdentificationDBManager.class);
	
	private ExcelStyle style;
	private HSSFSheet mHSSFSheet;
	
	private static final int COL_PROJECT_NAME 		= 0;
	private static final int COL_FULL_PATH 			= 1;
	private static final int COL_COMPONENT 			= 2;
	private static final int COL_LICENSE 			= 3;
	private static final int COL_DISCOVERY_TYPE		= 4;
	private static final int COL_CODE_MATCH_URL 	= 5;
	private static final int COL_COMMENT 			= 6;
	private static final int NUM_OF_COL  			= 7;
	
	private static HashMap<Integer, String> cellTitle = new HashMap<Integer, String>();
	static {
		
		cellTitle.put(COL_PROJECT_NAME, "ProjectName");
		cellTitle.put(COL_FULL_PATH, "FullPath");
		cellTitle.put(COL_COMPONENT, "Component");
		cellTitle.put(COL_LICENSE, "License");
		cellTitle.put(COL_DISCOVERY_TYPE, "DiscoveryType");
		cellTitle.put(COL_CODE_MATCH_URL, "CodeMatchURL");
		cellTitle.put(COL_COMMENT, "Commment");
	}
	
	private static final short START_ROW_NUM = 2;
	private static final short ROW_HEIGHT = 4*256;
	
	public void write(
			HSSFSheet xHSSFSheet, 
			IdentifiedFilesSheet codeMatchReportSheet, 
			int startIndex,
			ExcelStyle excelStyle, 
			UIResponseObserver observer) {
		
		this.mHSSFSheet = xHSSFSheet;
		this.style = excelStyle;

		if (codeMatchReportSheet == null) {
			observer.setFailMessage("Fail on creating Excel sheet\n No data is generated.");
			return;
		}
		
		// 1. row number of actual excel sheet
		short currentRowNumber = START_ROW_NUM;
		currentRowNumber = addTitleRow(currentRowNumber);
		
		ArrayList<IdentifiedFilesSheetRow> rows = codeMatchReportSheet.getRows();
		if(rows == null) {
			observer.setFailMessage("Fail on creating Excel sheet\n No data is generated.");
			return;
		}
		
		// 2. index is row number of identified files sheet model.
		int endIndex = startIndex + StandardReportExcelFileManager.MAX_ROW_NUM_OF_IDENTIFIED_FILES_SHEET;
		endIndex = (rows.size() < endIndex)? rows.size(): endIndex;

		log.debug("write codematchsheet: start:"+startIndex+" / end:"+endIndex);		
		for(int curIndex=startIndex; curIndex<endIndex; curIndex++) {
			IdentifiedFilesSheetRow row = rows.get(curIndex);
			currentRowNumber = addDataRow(currentRowNumber, row);
			
			if(currentRowNumber % 1000 == 0) log.debug("row: cur:"+currentRowNumber+"/ "+curIndex);
		}
		
		fitColumnSize();
		
		this.mHSSFSheet.createFreezePane(0, START_ROW_NUM+1);
	}

	private void fitColumnSize() {
		this.mHSSFSheet.setColumnWidth(COL_PROJECT_NAME, 25*256);
		this.mHSSFSheet.setColumnWidth(COL_FULL_PATH, 33*256);
		this.mHSSFSheet.setColumnWidth(COL_DISCOVERY_TYPE, 13*256);
		this.mHSSFSheet.setColumnWidth(COL_CODE_MATCH_URL, 9*256);
		this.mHSSFSheet.setColumnWidth(COL_COMMENT, 24*256);
		this.mHSSFSheet.setColumnWidth(COL_COMPONENT, 30*256);
		this.mHSSFSheet.setColumnWidth(COL_LICENSE, 15*256);
		
	}

	private short addDataRow(short startRowNumber, IdentifiedFilesSheetRow row) {
		
		short currentRowNumber = startRowNumber;

		final HSSFCellStyle TEXT_STYLE = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_LEFT);
		final HSSFCellStyle LINK_STYLE = style.getStyle(ExcelStyle.STYLE_DEFALT_FRAME_ALIGN_CENTER_FONT_BLUE);
		
		HSSFRow curRow = mHSSFSheet.createRow(currentRowNumber);
		curRow.setHeight(ROW_HEIGHT);
		
		curRow.createCell(COL_PROJECT_NAME).setCellValue( new HSSFRichTextString(row.getProjectName()));
		curRow.getCell(COL_PROJECT_NAME).setCellStyle(TEXT_STYLE);
		
		curRow.createCell(COL_FULL_PATH).setCellValue( new HSSFRichTextString(row.getFullPath()));
		curRow.getCell(COL_FULL_PATH).setCellStyle(TEXT_STYLE);

		curRow.createCell(COL_COMPONENT).setCellValue( new HSSFRichTextString(row.getComponentName()));
		curRow.getCell(COL_COMPONENT).setCellStyle(TEXT_STYLE);
		
		curRow.createCell(COL_LICENSE).setCellValue( new HSSFRichTextString(row.getLicense()));
		curRow.getCell(COL_LICENSE).setCellStyle(TEXT_STYLE);
		
		curRow.createCell(COL_COMMENT).setCellValue( new HSSFRichTextString(row.getComment()));
		curRow.getCell(COL_COMMENT).setCellStyle(TEXT_STYLE);

		curRow.createCell(COL_DISCOVERY_TYPE).setCellValue( new HSSFRichTextString(row.getDiscoveryType()));
		curRow.getCell(COL_DISCOVERY_TYPE).setCellStyle(TEXT_STYLE);
		
		curRow.createCell(COL_CODE_MATCH_URL);
		
		String url = row.getUrl();
		if(url != null && (url.length() != 0) && !"null".equals(url)) {
			HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);

			link.setAddress(url);
			link.setLabel(url);
		
			
			curRow.getCell(COL_CODE_MATCH_URL).setHyperlink(link);
			curRow.getCell(COL_CODE_MATCH_URL).setCellValue(
					new HSSFRichTextString("Show Matched Code ("+row.getCodeMatchCnt()+")"));
			curRow.getCell(COL_CODE_MATCH_URL).setCellStyle(LINK_STYLE);
		} else {
			curRow.getCell(COL_CODE_MATCH_URL).setCellStyle(LINK_STYLE);
		}
		
		return ++currentRowNumber;
	}

	private short addTitleRow(short startRowNumber) {
		short currentRowNumber = startRowNumber;
		
		final HSSFCellStyle TITLE_STYLE = style.getStyle(ExcelStyle.STYLE_YELLOW_FRAME_ALIGN_CENTER);

		HSSFRow curRow = mHSSFSheet.createRow(currentRowNumber);
		curRow.setHeight(ROW_HEIGHT);
		
		for(int i=0; i<NUM_OF_COL; i++) {
			curRow.createCell(i).setCellValue( new HSSFRichTextString(cellTitle.get(i)));
			curRow.getCell(i).setCellStyle(TITLE_STYLE);
		}
		
		return ++currentRowNumber;
	}
}
