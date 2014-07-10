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

import com.sec.ose.osi.report.standard.data.BillOfMaterialsRow;
import com.sec.ose.osi.report.standard.data.LicenseSummaryRow;
import com.sec.ose.osi.util.Property;

/**
 * BillOfMaterialsSheetTemplate
 * @author sjh.yoo
 *
 */
public class BillOfMaterialsSheetTemplate extends ISheetTemplate {

	static int [] BillOfMaterialsWidth = {10, 40, 7, 20, 10, 55};
	int ROW_START = ROW_4;
	
	String[] criticalLicenseList = null;
	String[] majorLicenseList = null;

	public BillOfMaterialsSheetTemplate(XSSFWorkbook wb, String sheetName, int sheetColor) {
		super(wb, sheetName, sheetColor, BillOfMaterialsWidth);
		
		sheet.createFreezePane( 0, ROW_START, 0, ROW_START );	// FreezePane 3 Row

		Property prop = Property.getInstance();
		criticalLicenseList = prop.getProperty(Property.RECIPROCAL_LICENSE).split(",");
		majorLicenseList = prop.getProperty(Property.MAJOR_LICENSE).split(",");
	}

	protected void createTitle() {
		// Title
		Row row = sheet.createRow(ROW_1);
		row.setHeight((short) (43*BASE_HEIGHT));

		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:F1"));
		
		Cell cell = row.createCell(COL_A);
		cell.setCellValue(sheet.getSheetName());
		cell.setCellStyle(titleStyle);
	}

	protected void createTable() {

		short mainItemThickness = (short) (24 * BASE_HEIGHT);
		short subItemThickness = (short) (18 * BASE_HEIGHT);
		CellStyle styleMainItem = getCellStyle(ROYAL_BLUE,getFont(FONT_WHITE, (short)12,true));

		//  ROW_2
		Row row = sheet.createRow(ROW_2);
		row.setHeight(mainItemThickness);

		sheet.addMergedRegion(CellRangeAddress.valueOf("A2:F2"));
		
		Cell cell = row.createCell(COL_A);
		cell.setCellValue("Open source License Verification Result ");
		cell.setCellStyle(styleMainItem);
		
		row.createCell(COL_B).setCellStyle(styleMainItem);
		row.createCell(COL_C).setCellStyle(styleMainItem);
		row.createCell(COL_D).setCellStyle(styleMainItem);
		row.createCell(COL_E).setCellStyle(styleMainItem);
		row.createCell(COL_F).setCellStyle(styleMainItem);

		//  ROW_3
		row = sheet.createRow(ROW_3);
		row.setHeight(subItemThickness);

		CellStyle style = getCellStyle(PALE_BLUE, getFont(FONT_BLACK, (short)10, false));

		cell = row.createCell(COL_A);
		cell.setCellValue("Category");
		cell.setCellStyle(style);

		cell = row.createCell(COL_B);
		cell.setCellValue("Files");
		cell.setCellStyle(style);
		
		cell = row.createCell(COL_C);
		cell.setCellValue("Files #");
		cell.setCellStyle(style);

		cell = row.createCell(COL_D);
		cell.setCellValue("Component");
		cell.setCellStyle(style);

		cell = row.createCell(COL_E);
		cell.setCellValue("License");
		cell.setCellStyle(style);

		cell = row.createCell(COL_F);
		cell.setCellValue("Remark");
		cell.setCellStyle(style);

	}

	int curRow = ROW_START;
	public void writeBillOfMaterialsRow(ArrayList<BillOfMaterialsRow> billOfMaterialsList) {
		
		for(BillOfMaterialsRow billOfMaterialsRow:billOfMaterialsList) {
			Row row = sheet.createRow(curRow++);

			Cell cell = row.createCell(ISheetTemplate.COL_A);
			cell.setCellValue(billOfMaterialsRow.getCategory());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_B);
			cell.setCellValue(billOfMaterialsRow.getMatchedFiles());
			cell.setCellStyle(leftStyle);

			cell = row.createCell(ISheetTemplate.COL_C);
			cell.setCellValue(billOfMaterialsRow.getMatchedFileCounts());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_D);
			cell.setCellValue(billOfMaterialsRow.getComponent());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_E);
			String license = billOfMaterialsRow.getLicense();
			cell.setCellValue(license);
			CellStyle licenseCellStyle = getCellStyle(WHITE,getFont(FONT_BLACK, (short)10,false));
			for(String criticalLicense:criticalLicenseList) {
				if(license.contains(criticalLicense)) {
					licenseCellStyle.setFont(getFont(RED,(short)10,false));
				}
			}
			for(String majorLicense:majorLicenseList) {
				if(license.contains(majorLicense)) {
					licenseCellStyle.setFont(getFont(ORANGE,(short)10,false));
				}
			}
			cell.setCellStyle(licenseCellStyle);
			
			cell = row.createCell(ISheetTemplate.COL_F);
			cell.setCellValue(billOfMaterialsRow.getComment());
			cell.setCellStyle(leftStyle);

		}
		
	}

	public void writeLicenseRow(ArrayList<LicenseSummaryRow> licenseList) {
		curRow += 3;

		CellStyle styleLicenseTitle = getCellStyle(YELLOW,getFont(FONT_BLACK, (short)10,true));
		styleLicenseTitle.setAlignment(CellStyle.ALIGN_LEFT);
		CellStyle styleLicenseSum = getCellStyle(YELLOW,getFont(FONT_BLACK, (short)10,true));

		Row row = sheet.createRow(curRow++);
		
		Cell cell = row.createCell(ISheetTemplate.COL_B);
		cell.setCellValue("License State");
		cell.setCellStyle(styleLicenseTitle);

		cell = row.createCell(ISheetTemplate.COL_C);
		cell.setCellFormula("SUM(C"+(curRow+1)+":C"+(curRow+licenseList.size())+")");
		cell.setCellStyle(styleLicenseSum);
		
		for(LicenseSummaryRow licenseRow:licenseList) {
			row = sheet.createRow(curRow++);

			cell = row.createCell(ISheetTemplate.COL_B);
			cell.setCellValue(licenseRow.getLicense());
			cell.setCellStyle(leftStyle);

			cell = row.createCell(ISheetTemplate.COL_C);
			cell.setCellValue(licenseRow.getCount());
			cell.setCellStyle(normalStyle);

		}
		
	}
}
