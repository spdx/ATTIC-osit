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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sec.ose.osi.report.standard.data.SummaryRow;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * SummarySheetTemplate
 * @author sjh.yoo
 *
 */
public class SummarySheetTemplate extends ISheetTemplate {

	static int [] SummaryWidth = {15, 40, 10, 11, 10, 10, 10, 10, 10, 10, 10};
	int ROW_START = ROW_4;
	
	public SummarySheetTemplate(XSSFWorkbook wb, String sheetName, int sheetColor) {
		super(wb, sheetName, sheetColor, SummaryWidth);

		sheet.createFreezePane( 0, ROW_START, 0, ROW_START );	// FreezePane 3 Row
		//sheet.setZoom( 85,100 );	// 85%
	}

	protected void createTitle() {
		// Title
		Row row = sheet.createRow(ROW_1);
		row.setHeight((short) (43*BASE_HEIGHT));

		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:K1"));
		
		Cell cell = row.createCell(COL_A);
		cell.setCellValue("Summary");
		cell.setCellStyle(titleStyle);
	}

	protected void createTable() {

		short mainItemBorderThickness = (short) (24 * BASE_HEIGHT);
		short subItemBorderThickness = (short) (45 * BASE_HEIGHT);
		CellStyle styleMainItem = getCellStyle(ROYAL_BLUE,getFont(FONT_WHITE, (short)12,true));
		CellStyle styleSubItem = getCellStyle(ROYAL_BLUE,getFont(FONT_WHITE, (short)10,false));
		
		//  ROW_2
		Row row = sheet.createRow(ROW_2);
		row.setHeight(mainItemBorderThickness);

		sheet.addMergedRegion(CellRangeAddress.valueOf("A2:A3"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("B2:B3"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("C2:I2"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("J2:K2"));
		
		Cell cell = row.createCell(COL_A);
		cell.setCellValue("Classification");
		cell.setCellStyle(styleMainItem);
		
		cell = row.createCell(COL_B);
		cell.setCellValue("Project Name");
		cell.setCellStyle(styleMainItem);

		cell = row.createCell(COL_C);
		cell.setCellValue("Source Code Scan Result");
		cell.setCellStyle(styleMainItem);

		row.createCell(COL_D).setCellStyle(styleMainItem);
		row.createCell(COL_E).setCellStyle(styleMainItem);
		row.createCell(COL_F).setCellStyle(styleMainItem);
		row.createCell(COL_G).setCellStyle(styleMainItem);
		row.createCell(COL_H).setCellStyle(styleMainItem);
		row.createCell(COL_I).setCellStyle(styleMainItem);

		// Identify Result
		
		CellStyle style = getCellStyle(LIGHT_RED, getFont(DARK_RED,(short)12,true));
		
		cell = row.createCell(COL_J);
		cell.setCellValue("Identify Result");
		cell.setCellStyle(style);
		
		row.createCell(COL_K).setCellStyle(style);


		//  ROW_3
		row = sheet.createRow(ROW_3);
		row.setHeight(subItemBorderThickness);
		
		cell = row.createCell(COL_A);
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_B);
		cell.setCellStyle(styleSubItem);
		
		cell = row.createCell(COL_C);
		cell.setCellValue("Scan Date");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_D);
		cell.setCellValue("Scan time\n(HH:MM:SS)");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_E);
		cell.setCellValue("Pending Files");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_F);
		cell.setCellValue("Pending\n(%)");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_G);
		cell.setCellValue("Total Files");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_H);
		cell.setCellValue("Exceptional Files");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_I);
		cell.setCellValue("Bytes");
		cell.setCellStyle(styleSubItem);
		
		
		// Identify Result
		
		style = getCellStyle(LIGHT_RED, getFont(DARK_RED,(short)10,true));
		
		cell = row.createCell(COL_J);
		cell.setCellValue("Total Identified Files");
		cell.setCellStyle(style);
		
		cell = row.createCell(COL_K);
		cell.setCellValue("Current Pending Files");
		cell.setCellStyle(style);

	}
	

	/**
	 * write summary info
	 * 
	 * @param summaryRowList
	 */
	public void writeSummaryRow(ArrayList<SummaryRow> summaryRowList) {
		CellStyle rightStyle = getCellStyle(WHITE, getFont(FONT_BLACK,(short)10,false));
		rightStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		
		int curRow = ROW_START;
		for(SummaryRow summaryRow:summaryRowList) {
			Row row = sheet.createRow(curRow++);

			Cell cell = row.createCell(ISheetTemplate.COL_A);
			cell.setCellValue(summaryRow.getClassification());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_B);
			cell.setCellValue(summaryRow.getProjectName());
			cell.setCellStyle(leftStyle);

			cell = row.createCell(ISheetTemplate.COL_C);
			cell.setCellValue(summaryRow.getScanDate());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_D);
			cell.setCellValue(DateUtil.translateTimeFormatToColon(summaryRow.getScanTime()));
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_E);
			cell.setCellValue(summaryRow.getPendingFileCount());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_F);
			cell.setCellValue(summaryRow.getPendingPercent());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_G);
			cell.setCellValue(summaryRow.getTotalFileCount());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_H);
			cell.setCellValue(summaryRow.getExceptionalFileCount());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_I);
			cell.setCellValue(summaryRow.getBytes());
			cell.setCellStyle(rightStyle);

			cell = row.createCell(ISheetTemplate.COL_J);
			cell.setCellValue(summaryRow.getTotalIdentifiedFileCount());
			cell.setCellStyle(normalStyle);

			int percent = (summaryRow.getCurrentPendingFileCount()*100 / summaryRow.getPendingFileCount());
			cell = row.createCell(ISheetTemplate.COL_K);
			cell.setCellValue(summaryRow.getCurrentPendingFileCount() + " ("+percent+"%)");
			cell.setCellStyle(normalStyle);
		}
		
	}

}
