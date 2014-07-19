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
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sec.ose.osi.report.standard.data.IdentifiedFilesRow;

/**
 * IdentifiedFilesSheetTemplate
 * @author sjh.yoo
 *
 */
public class IdentifiedFilesSheetTemplate extends ISheetTemplate {
	
	static int [] IdentifiedFilesWidth = {20, 32, 30, 12, 16, 10, 30};
	int ROW_START = ROW_3;
	
	public IdentifiedFilesSheetTemplate(XSSFWorkbook wb, String sheetName, int sheetColor) {
		super(wb, sheetName, sheetColor, IdentifiedFilesWidth);

		sheet.createFreezePane( 0, ROW_START, 0, ROW_START );	// FreezePane 3 Row
	}

	protected void createTitle() {
		// Title
		Row row = sheet.createRow(ROW_1);
		row.setHeight((short) (43*BASE_HEIGHT));

		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
		
		Cell cell = row.createCell(COL_A);
		cell.setCellValue("Identified Files");
		cell.setCellStyle(titleStyle);
	}

	protected void createTable() {
		
		short subItemThickness = (short) (35 * BASE_HEIGHT);
		CellStyle styleSubItem = getCellStyle(ROYAL_BLUE,getFont(FONT_WHITE, (short)10,true));

		//  ROW_2
		Row row = sheet.createRow(ROW_2);
		row.setHeight(subItemThickness);

		Cell cell = row.createCell(COL_A);
		cell.setCellValue("ProjectName");
		cell.setCellStyle(styleSubItem);
		
		cell = row.createCell(COL_B);
		cell.setCellValue("FullPath");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_C);
		cell.setCellValue("Component");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_D);
		cell.setCellValue("License");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_E);
		cell.setCellValue("DiscoveryType");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_F);
		cell.setCellValue("CodeMatch URL");
		cell.setCellStyle(styleSubItem);

		cell = row.createCell(COL_G);
		cell.setCellValue("Comment");
		cell.setCellStyle(styleSubItem);

	}

	int curRow = ROW_START;
	public void writeRow(
			ArrayList<IdentifiedFilesRow> identifiedFilesRowList) {

		for(IdentifiedFilesRow identifiedFilesRow:identifiedFilesRowList) {
			Row row = sheet.createRow(curRow++);

			Cell cell = row.createCell(ISheetTemplate.COL_A);
			cell.setCellValue(identifiedFilesRow.getProjectName());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_B);
			cell.setCellValue(identifiedFilesRow.getFullPath());
			cell.setCellStyle(leftStyle);

			cell = row.createCell(ISheetTemplate.COL_C);
			cell.setCellValue(identifiedFilesRow.getComponent());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_D);
			cell.setCellValue(identifiedFilesRow.getLicense());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_E);
			cell.setCellValue(identifiedFilesRow.getDiscoveryType());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(ISheetTemplate.COL_F);
			cell.setCellStyle(normalStyle);
			String value = "";
			String url = identifiedFilesRow.getUrl();
			if(url != null && (url.length() != 0) && !"null".equals(url)) {
				Hyperlink link = wb.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
			    link.setAddress(url);
				link.setLabel(url);
				
				cell.setHyperlink(link);
				cell.setCellStyle(getCellStyle(WHITE, getFont(BLUE,(short)10,false)));
				value = "Show Matched Code ("+(identifiedFilesRow.getCodeMatchCnt())+")";
			}
			cell.setCellValue(value);

			cell = row.createCell(ISheetTemplate.COL_G);
			cell.setCellValue(identifiedFilesRow.getComment());
			cell.setCellStyle(leftStyle);

		}
	}

}
