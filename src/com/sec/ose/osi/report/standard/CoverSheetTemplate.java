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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sec.ose.osi.util.tools.DateUtil;

/**
 * CoverSheetLayout
 * @author sjh.yoo
 *
 */
public class CoverSheetTemplate extends ISheetTemplate {
	
	static int [] CoverWidth = {8, 8, 17, 35, 11, 32, 8};

	public CoverSheetTemplate(XSSFWorkbook wb, String sheetName, int sheetColor) {
		super(wb, sheetName, sheetColor, CoverWidth);
	}

	public void writeCoverSheet(String projectName,String creatorName,
			String creatorEmail,String organizationName,String toolInfo) {
		
		sheet.getRow(ROW_10).getCell(COL_D).setCellValue(
				new XSSFRichTextString(projectName));
		sheet.getRow(ROW_10).getCell(COL_F).setCellValue(
				new XSSFRichTextString(DateUtil.getCurrentTime("%1$tY-%1$tm-%1$te (%1$ta)")));
		sheet.getRow(ROW_11).getCell(COL_D).setCellValue(
				new XSSFRichTextString(creatorEmail));
		sheet.getRow(ROW_12).getCell(COL_D).setCellValue(
				new XSSFRichTextString(organizationName));
		sheet.getRow(ROW_13).getCell(COL_D).setCellValue(
				new XSSFRichTextString(toolInfo));
	}

	protected void createTitle() {
		short lineThickness = (short) (6 * BASE_HEIGHT);
		
		// Top Line
		Row row = sheet.createRow(ROW_4);
		row.setHeight(lineThickness);
		
		XSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		setDummyTitleStyle(row,style);
		
		// Title
		row = sheet.createRow(ROW_5);
		row.setHeightInPoints(100);
		sheet.addMergedRegion(CellRangeAddress.valueOf("B5:G5"));
		
		Font font = wb.createFont();
		font.setFontHeightInPoints((short)28);
		font.setFontName("Trebuchet MS");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		setDummyTitleStyle(row,style);
		row.getCell(COL_B).setCellValue("Open Source License Verification Report");
		
		// Bottom Line
		row = sheet.createRow(ROW_6);
		row.setHeight(lineThickness);
		
		style = wb.createCellStyle();
		style.setFillForegroundColor(DARK_BLUE);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		setDummyTitleStyle(row,style);
	}
	private void setDummyTitleStyle(Row row,CellStyle style) {
		row.createCell(COL_B).setCellStyle(style);
		row.createCell(COL_C).setCellStyle(style);
		row.createCell(COL_D).setCellStyle(style);
		row.createCell(COL_E).setCellStyle(style);
		row.createCell(COL_F).setCellStyle(style);
		row.createCell(COL_G).setCellStyle(style);
	}
	
	protected void createTable() {

		Font fontBold = getFont(FONT_BLACK,(short)11,true);
		Font fontNormal = getFont(FONT_BLACK,(short)11,false);
		
		short tableThickness = (short) (37 * BASE_HEIGHT);
		short toolThickness = (short) (55 * BASE_HEIGHT);

		//  ROW_10
		Row row = sheet.createRow(ROW_10);
		row.setHeight(tableThickness);
		
		XSSFCellStyle style = getCellStyle(BORDER_LEFT|BORDER_TOP,fontBold);
		Cell cell = row.createCell(COL_C);
		cell.setCellValue("Project Name");
		cell.setCellStyle(style);
		
		style = getCellStyle(BORDER_TOP,fontNormal);
		row.createCell(COL_D).setCellStyle(style);

		style = getCellStyle(BORDER_TOP,fontBold);
		cell = row.createCell(COL_E);
		cell.setCellValue("Date");
		cell.setCellStyle(style);

		style = getCellStyle(BORDER_RIGHT|BORDER_TOP,fontNormal);
		row.createCell(COL_F).setCellStyle(style);


		//  ROW_11
		row = sheet.createRow(ROW_11);
		row.setHeight(tableThickness);

		sheet.addMergedRegion(CellRangeAddress.valueOf("D11:F11"));
		
		style = getCellStyle(BORDER_LEFT,fontBold);
		cell = row.createCell(COL_C);
		cell.setCellValue("Author");
		cell.setCellStyle(style);
		
		style = getCellStyle(0,fontNormal);
		row.createCell(COL_D).setCellStyle(style);
		row.createCell(COL_E).setCellStyle(style);

		style = getCellStyle(BORDER_RIGHT,fontNormal);
		row.createCell(COL_F).setCellStyle(style);

		//  ROW_12
		row = sheet.createRow(ROW_12);
		row.setHeight(tableThickness);

		sheet.addMergedRegion(CellRangeAddress.valueOf("D12:F12"));
		
		style = getCellStyle(BORDER_LEFT,fontBold);
		cell = row.createCell(COL_C);
		cell.setCellValue("Team");
		cell.setCellStyle(style);
		
		style = getCellStyle(0,fontNormal);
		row.createCell(COL_D).setCellStyle(style);
		row.createCell(COL_E).setCellStyle(style);

		style = getCellStyle(BORDER_RIGHT,fontNormal);
		row.createCell(COL_F).setCellStyle(style);

		//  ROW_13
		row = sheet.createRow(ROW_13);
		row.setHeight(toolThickness);

		sheet.addMergedRegion(CellRangeAddress.valueOf("D13:F13"));
		
		style = getCellStyle(BORDER_BOTTOM|BORDER_LEFT,fontBold);
		cell = row.createCell(COL_C);
		cell.setCellValue("Tool");
		cell.setCellStyle(style);
		
		style = getCellStyle(BORDER_BOTTOM,fontNormal);
		row.createCell(COL_D).setCellStyle(style);
		row.createCell(COL_E).setCellStyle(style);

		style = getCellStyle(BORDER_BOTTOM|BORDER_RIGHT,fontNormal);
		row.createCell(COL_F).setCellStyle(style);

	}

	protected XSSFCellStyle getCellStyle(int border, Font font) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);	// new line
		
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		if((border & BORDER_BOTTOM) > 0) style.setBorderBottom(CellStyle.BORDER_DOUBLE);
		else style.setBorderBottom(CellStyle.BORDER_THIN);

		if((border & BORDER_LEFT) > 0) style.setBorderLeft(CellStyle.BORDER_DOUBLE);
		else style.setBorderLeft(CellStyle.BORDER_THIN);

		if((border & BORDER_RIGHT) > 0) style.setBorderRight(CellStyle.BORDER_DOUBLE);
		else style.setBorderRight(CellStyle.BORDER_THIN);

		if((border & BORDER_TOP) > 0) style.setBorderTop(CellStyle.BORDER_DOUBLE);
		else style.setBorderTop(CellStyle.BORDER_THIN);
		
		if(font != null) style.setFont(font);
		
		return style;
	}

}
