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
package com.sec.ose.osi.report.standard.summary;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.sec.ose.osi.report.common.ExcelStyle;

/**
 * StandardSummarySheetWriter
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StandardSummarySheetWriter {
	private static Log log = LogFactory.getLog(StandardSummarySheetWriter.class);
	
	private static final int SUMMARY_SHEET_START_ROW = 3;
	private static final int SUMMARY_SHEET_START_COL = 1;

	public static void write(HSSFSheet summarySheet, ArrayList<StandardSummarySheetRow> summarySheetRows, ExcelStyle excelStyle) {
	
		
		Iterator<StandardSummarySheetRow> itr = summarySheetRows.iterator();
		if(itr == null) {
			return;
		}
		
		int rowCnt = 0;
		while(itr.hasNext()) {
			
			int curRow = SUMMARY_SHEET_START_ROW + rowCnt++;
			HSSFRow curHSSFRow = summarySheet.createRow(curRow);//row create
			
			StandardSummarySheetRow row = itr.next();
			for(int i=0; i<StandardSummarySheetRow.NUM_OF_FIELDS; i++){
				
				curHSSFRow.createCell(SUMMARY_SHEET_START_COL+i);
				curHSSFRow.getCell(SUMMARY_SHEET_START_COL+i).setCellStyle(excelStyle.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_CENTER));
				
				switch(i) {
					case StandardSummarySheetRow.PENDING_FILE_COUNT:
					case StandardSummarySheetRow.TOTAL_FILE_COUNT:
					case StandardSummarySheetRow.SKIPPED_FILE_COUNT:
					case StandardSummarySheetRow.TOTAL_BYTES:
					case StandardSummarySheetRow.IDENTIFIED_FILE_COUNT:
						try {
							double dblValue = Double.parseDouble(row.getValue(i));
							curHSSFRow.getCell(SUMMARY_SHEET_START_COL+i).setCellValue(dblValue);
							break;
						} catch(NumberFormatException ex) {
							log.warn(ex.getMessage());
						}
						// go through
						
					default:
						HSSFRichTextString value = new HSSFRichTextString(row.getValue(i));
						curHSSFRow.getCell(SUMMARY_SHEET_START_COL+i).setCellValue(value);
					
				}
			}
			
			curHSSFRow.getCell(1).setCellStyle(excelStyle.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_LEFT));
			curHSSFRow.getCell(8).setCellStyle(excelStyle.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_RIGHT));
					
			curHSSFRow.createCell(0);
			curHSSFRow.getCell(0).setCellStyle(excelStyle.getStyle(ExcelStyle.STYLE_DEFAULT_FRAME_ALIGN_CENTER));
		}
	}
}
