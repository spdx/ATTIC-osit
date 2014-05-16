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
package com.sec.ose.osi.report.standard.identify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.report.common.ExcelStyle;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.Property;

/**
 * StandardIdentifySheetWriter
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class StandardIdentifySheetWriter {

	private static final int COL_CATEGORY 			= 0;
	private static final int COL_MATCHED_FILE 		= 1;
	private static final int COL_FILE_COUNT 		= 2;
	private static final int COL_COMPONENT 			= 3;
	private static final int COL_LICENSE 			= 4;
	private static final int COL_RESULT				= 5;
	private static final int NUM_OF_COL  			= 6;

	private static final short START_ROW_NUM = 3;
	
	private HSSFSheet mHSSFSheet;
	private StandardIdentifyReportSheet identifyReportSheet;
	private ExcelStyle style;

	private static final int    MAX_CELL_LENGTH_LIMIT = 5000;
	private static final String CELL_LENGTH_LIMIT_MESSAGE = 
		"WARNING!!\n"+
		"<Some part of this cell is not reported in this excel file.\n"+
		"  See the \"IdentifiedFiles\" sheet for whole file informations.\n"+
		"  You need to check \"Insert Identified Files\" to see the sheet.>\n\n";
	
	private String[] red_colored_license = null;
	private String[] orange_colored_license = null;				

	public StandardIdentifySheetWriter() {
		super();
		
		Property prop = Property.getInstance();
		String critical_license =prop.getProperty(Property.RECIPROCAL_LICENSE);
		String major_license =prop.getProperty(Property.MAJOR_LICENSE);
		String[] critical_values = null; 
		if(critical_license != null)
			critical_values = critical_license.split(",");
		
		String[] major_values = null;
		if(major_license != null)
			major_values = major_license.split(",");
		
		red_colored_license = critical_values;
		orange_colored_license = major_values;
	}

	public void write(HSSFSheet xHSSFSheet, StandardIdentifyReportSheet identifyReportSheet, ExcelStyle style, UIResponseObserver observer) {
		
		this.mHSSFSheet = xHSSFSheet;
		this.identifyReportSheet = identifyReportSheet;
		this.style = style;
		
		// project name
		HSSFRow curRow = mHSSFSheet.getRow(0);
		HSSFRichTextString newTitle = new HSSFRichTextString(identifyReportSheet.getProjectName());
		curRow.getCell(1).setCellValue(newTitle);
		
		// change row name
		curRow = mHSSFSheet.getRow(1);
				
		short currentRowNumber = START_ROW_NUM;
		currentRowNumber = writeRows(currentRowNumber, identifyReportSheet.getIdentifiedRows());
		
		currentRowNumber++;
		if(identifyReportSheet.getStringSearchOnlyRows() != null) {
			currentRowNumber = writeStringSearchOnlyTitle(currentRowNumber);
			currentRowNumber = writeRows(currentRowNumber, identifyReportSheet.getStringSearchOnlyRows());
		}
		
		currentRowNumber = writeLicenseSummary(currentRowNumber);
		
		fitColumnSize();
		
		this.mHSSFSheet.createFreezePane(0, START_ROW_NUM);
		
	}

	private short writeStringSearchOnlyTitle(short currentRowNumber) {

		HSSFRow curRow = mHSSFSheet.createRow(currentRowNumber);
		currentRowNumber++;
		
		final HSSFCellStyle TITLE = style.getStyle(ExcelStyle.STYLE_YELLOW_FRAME_ALIGN_LEFT);
		for(int colNum=0; colNum<NUM_OF_COL; colNum++) {
			
			curRow.createCell(colNum);
			curRow.getCell(colNum).setCellStyle(TITLE);
		}
		
		String title = "";
		title = "STRING SEARCH ONLY\n (identified license is not identical between <string search> and <code match>)";
		curRow.getCell(COL_MATCHED_FILE).setCellValue(new HSSFRichTextString(title));
		
		
		return currentRowNumber;
	}

	private short createSummaryTitleRow(short startRowNumber, String title, int value) {

		short currentRowNumber = startRowNumber;
		
		final HSSFCellStyle SUMMARY = style.getStyle(ExcelStyle.STYLE_YELLOW_FRAME_ALIGN_RIGHT_YELLOW);
		final HSSFCellStyle SUMMARY_TITLE = style.getStyle(ExcelStyle.STYLE_YELLOW_FRAME_ALIGN_LEFT);

		HSSFRow curRow = mHSSFSheet.createRow(currentRowNumber);
		
		curRow.createCell(1).setCellValue( new HSSFRichTextString(title));
		curRow.getCell(1).setCellStyle(SUMMARY_TITLE);
		curRow.createCell(2);
		if(value != 0)
			curRow.getCell(2).setCellValue(value);
		curRow.getCell(2).setCellStyle(SUMMARY);
		
		return ++currentRowNumber;
	}
	
	private short createSummaryRow(short startRowNumber, String title, int value) {

		short currentRowNumber = startRowNumber;
		
		final HSSFCellStyle RIGHT = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_RIGHT);
		final HSSFCellStyle LEFT = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_LEFT);

		HSSFRow curRow = mHSSFSheet.createRow(currentRowNumber);
		
		curRow.createCell(1).setCellValue( new HSSFRichTextString(title));
		curRow.getCell(1).setCellStyle(LEFT);
		curRow.createCell(2);
		if(value != 0)
			curRow.getCell(2).setCellValue( value);
		curRow.getCell(2).setCellStyle(RIGHT);
		
		return ++currentRowNumber;
	}
	
	private short writeLicenseSummary(short startRowNumber) {
		
		short currentRowNumber = (short) (startRowNumber + 3);
		currentRowNumber = createSummaryTitleRow(currentRowNumber, "Management State", 0);
		currentRowNumber = createSummaryRow(currentRowNumber, "Delete/Not Use", 0);
		currentRowNumber = createSummaryRow(currentRowNumber, "Modify", 0);
		currentRowNumber = createSummaryRow(currentRowNumber, "Open", 0);
		currentRowNumber = createSummaryRow(currentRowNumber, "No Problem", 0);
		

		StandardLicenseSummary summary = identifyReportSheet.getLicenseSummary();
		currentRowNumber = createSummaryTitleRow(currentRowNumber, "License State", summary.getTotalFileCnt());
		
		Iterator<String> itr = summary.getLicenseListByFileCntAscendingOrder();
		while(itr.hasNext()) {
			String licenseName = itr.next();
			currentRowNumber = createSummaryRow(currentRowNumber, licenseName, summary.getFileCntForLicense(licenseName));
		}
		
		return ++currentRowNumber;
	}

	private short writeRows(short startRowNumber, ArrayList<StandardIdentifyReportSheetRow> rows) {
		
		short currentRowNumber = startRowNumber;

		if(rows==null)
			return currentRowNumber;
		
		HashSet<String> checkDuplicated = new HashSet<String>(); 
		
		for (int i = 0; i < rows.size(); i++) {

			StandardIdentifyReportSheetRow currentReportItem = rows.get(i);
			
			String key = currentReportItem.getComponentLicense()+"#"+currentReportItem.getMatchedFiles();
			if(checkDuplicated.contains(key))
				continue;
			
			checkDuplicated.add(key);
			
			HSSFRow curHSSFRow = mHSSFSheet.createRow(currentRowNumber);	
			curHSSFRow = fillRow(curHSSFRow, currentReportItem);
			curHSSFRow = applyStyle(curHSSFRow, currentReportItem);

			currentRowNumber++;
		}
		
		return currentRowNumber;
	}

	private HSSFRow fillRow(HSSFRow pHSSFRow, StandardIdentifyReportSheetRow currentReportItem) {
		
		for(int colNum=0; colNum<NUM_OF_COL; colNum++) {

			String strValue = null;
			switch(colNum) {
				case COL_CATEGORY:
					strValue = currentReportItem.getCategory();
					break;
					
				case COL_MATCHED_FILE:
					strValue = currentReportItem.getMatchedFiles();
					break;
					
				case COL_FILE_COUNT:
					int currentMatchedFilesCount= currentReportItem.getMatchedFileCounts();
					if (currentMatchedFilesCount < 0) {
						currentMatchedFilesCount = 0;
					}
					strValue = ""+currentMatchedFilesCount;
					break;
					
				case COL_COMPONENT:
					strValue = currentReportItem.getComponent();
					break;
					
				case COL_LICENSE:
					strValue = currentReportItem.getComponentLicense();
					break;

				case COL_RESULT:
					strValue = currentReportItem.getComment();
					break;
			}
			
			if(strValue != null) {
				if(strValue.length() > MAX_CELL_LENGTH_LIMIT) {
					strValue = CELL_LENGTH_LIMIT_MESSAGE + strValue.substring(0, MAX_CELL_LENGTH_LIMIT);
				}
				HSSFRichTextString cellValue = new HSSFRichTextString(strValue);
				pHSSFRow.createCell(colNum).setCellValue(cellValue);
			} else {
				pHSSFRow.createCell(colNum);
			}
		}
		
		
		return pHSSFRow;
	}
	
	private HSSFCellStyle getStyleForLicenseType(String curLicenseType) {
		if(curLicenseType == null)
			return style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_CENTER);
			
		if(red_colored_license != null) {
			for(int i=0; i<red_colored_license.length; i++) {
				if(curLicenseType.contains(red_colored_license[i])) {
					return style.getStyle(ExcelStyle.STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_RED);
				}
			}
		}

		if(orange_colored_license != null) {
			for(int i=0; i<orange_colored_license.length; i++) {
				if(curLicenseType.contains(orange_colored_license[i])) {
					return style.getStyle(ExcelStyle.STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_ORANGE);
				}
			}
		}
		
		return style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_CENTER);
		
	}

	private HSSFRow applyStyle(HSSFRow pHSSFRow, StandardIdentifyReportSheetRow currentReportItem) {
		
		final HSSFCellStyle LEFT = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_LEFT);
		final HSSFCellStyle RIGHT = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_RIGHT);
		final HSSFCellStyle CENTER = style.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_CENTER);
		final HSSFCellStyle MATCHED_FILES = style.getStyle(ExcelStyle.STYLE_DEFAILT_FRANE_ALIGN_LEFT_FONT_FILE);

		for(int colNum=0; colNum<NUM_OF_COL; colNum++) {
			
			HSSFCellStyle style = LEFT;
			switch(colNum) {
			
				case COL_FILE_COUNT:
					style  = RIGHT;
					break;
				
				case COL_COMPONENT:
					style = CENTER;
					break;
					
				case COL_LICENSE:
					String currentLicense = currentReportItem.getComponentLicense();		
					style = getStyleForLicenseType(currentLicense);
					break;
					
				case COL_MATCHED_FILE:
					style = MATCHED_FILES;
					break;
			}
			
			pHSSFRow.getCell(colNum).setCellStyle(style);
			
			
		}

		
		return pHSSFRow;
	}

	private void fitColumnSize() {
		final double WEIGHT = 284.9;
		
		this.mHSSFSheet.setColumnWidth(COL_CATEGORY, (int)(7.67*WEIGHT));
		this.mHSSFSheet.setColumnWidth(COL_MATCHED_FILE, (int)(40.22*WEIGHT));
		this.mHSSFSheet.setColumnWidth(COL_FILE_COUNT, (int)(5*WEIGHT));
		this.mHSSFSheet.setColumnWidth(COL_COMPONENT, (int)(15.56*WEIGHT));
		this.mHSSFSheet.setColumnWidth(COL_LICENSE, (int)(8.56*WEIGHT));
		this.mHSSFSheet.setColumnWidth(COL_RESULT, (int)(50*WEIGHT));
	}


}