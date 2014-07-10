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

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ISheetTemplate
 * @author sjh.yoo
 *
 */
public abstract class ISheetTemplate {
	private int BASE_WIDTH = 300;
	protected int BASE_HEIGHT = 20;
	private short DEFAULT_HEIGHT = (short)(16.5 * BASE_HEIGHT);

	protected static XSSFColor ROYAL_BLUE = new XSSFColor(new java.awt.Color(79, 129, 189));
	protected static XSSFColor DARK_BLUE = new XSSFColor(new java.awt.Color(23, 55, 93));
	protected static XSSFColor LIGHT_RED = new XSSFColor(new java.awt.Color(230, 185, 184));
	protected static XSSFColor DARK_RED = new XSSFColor(new java.awt.Color(149, 55, 53));
	protected static XSSFColor PALE_BLUE = new XSSFColor(new java.awt.Color(153, 204, 255));
	protected static XSSFColor BLUE = new XSSFColor(new java.awt.Color(0, 0, 255));
	protected static XSSFColor RED = new XSSFColor(new java.awt.Color(255, 0, 0));
	protected static XSSFColor BLACK = new XSSFColor(new java.awt.Color(0, 0, 0));
	protected static XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255));
	protected static XSSFColor YELLOW = new XSSFColor(new java.awt.Color(255, 255, 102));
	protected static XSSFColor ORANGE = new XSSFColor(new java.awt.Color(255, 102, 0));
	
	protected static short FONT_WHITE = IndexedColors.WHITE.getIndex();
	protected static short FONT_BLACK = IndexedColors.BLACK.getIndex();
	
	protected static short COL_A = 0,COL_B = 1,COL_C = 2,COL_D = 3,COL_E = 4,COL_F = 5,
				COL_G = 6,COL_H = 7,COL_I = 8,COL_J = 9,COL_K = 10;

	protected static short ROW_1 = 0,ROW_2 = 1,ROW_3 = 2,ROW_4 = 3,ROW_5 = 4,ROW_6 = 5,
				ROW_7 = 6,ROW_8 = 7, ROW_9 = 8, ROW_10 = 9, ROW_11 = 10,
				ROW_12 = 11,ROW_13 = 12;

	protected int BORDER_BOTTOM = 0x01;
	protected int BORDER_LEFT = 0x02;
	protected int BORDER_RIGHT = 0x04;
	protected int BORDER_TOP = 0x08;

	XSSFWorkbook wb = null;
	XSSFSheet sheet = null;
	XSSFCellStyle titleStyle = null;
	XSSFCellStyle normalStyle = null;
	XSSFCellStyle leftStyle = null;

	public ISheetTemplate(XSSFWorkbook wb, String sheetName, int sheetColor, int[] coverWidth) {
		this.wb = wb;
		this.sheet = wb.createSheet(sheetName);
		sheet.setTabColor(sheetColor);

		setLayout(coverWidth);
		createTitle();
		createTable();
	}

	protected abstract void createTitle();

	protected abstract void createTable();

	private void setLayout(int[] coverWidth) {
		// Set WIDTH
		for(int i=0;i<coverWidth.length;i++) {
			sheet.setColumnWidth( i, coverWidth[i] * BASE_WIDTH);
		}
		
		// Set default height
		sheet.setDefaultRowHeight(DEFAULT_HEIGHT);

		// set Title Style
		titleStyle = wb.createCellStyle();
		titleStyle.setFont(getFont(DARK_BLUE, (short)16,true));
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		// set normal Style
		normalStyle = getCellStyle(WHITE, getFont(FONT_BLACK,(short)10,false));
		
		// set left Style
		leftStyle = getCellStyle(WHITE, getFont(FONT_BLACK,(short)10,false));
		leftStyle.setAlignment(CellStyle.ALIGN_LEFT);
	}

	/**
	 * @param color
	 * @param size
	 * @param bold
	 * @return Font
	 */
	protected XSSFFont getFont(XSSFColor color, short size, boolean bold) {
		XSSFFont font = wb.createFont();
		font.setFontHeightInPoints(size);
		font.setFontName("Tahoma");
		font.setColor(color);
		if(bold) font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		return font;
	}

	protected XSSFFont getFont(int color, short size, boolean bold) {
		XSSFFont font = wb.createFont();
		font.setFontHeightInPoints(size);
		font.setFontName("Tahoma");
		font.setColor((short)color);
		if(bold) font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		return font;
	}

	/**
	 * @param color
	 * @param font
	 * @return CellStyle
	 */
	protected XSSFCellStyle getCellStyle(XSSFColor color, Font font) {
		XSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(color);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);	// new line
		
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		if(font != null) style.setFont(font);
		
		return style;
	}

}
