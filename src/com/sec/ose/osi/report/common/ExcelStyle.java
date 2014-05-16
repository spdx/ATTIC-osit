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
package com.sec.ose.osi.report.common;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * ExcelStyle
 * @author suhyun47.kim, jae-yong.lee 
 * 
 */
public class ExcelStyle {

	// fonts 
	public static final int DEFAULT_FONT					= 0;
	public static final int FONT_BLACK						= 1;
	public static final int FONT_BLUE						= 2;
	public static final int FONT_RED						= 3;
	public static final int FONT_ORANGE						= 4;
	public static final int FONT_FILE						= 5;
	public static final int FONT_GREEN						= 6;
	public static final int FONT_GREEN_SMALL				= 7;
	public static final int FONT_BLACK_BOLD					= 8;
	
	// frame and fonts 
	public static final int DEFAULT_STYLE						= 100;
	public static final int STYLE_DEFAULT_FRAME_ALIGN_CENTER 	= 101;
	public static final int STYLE_DEFAULT_FRAME_ALIGN_LEFT		= 102;
	public static final int STYLE_DEFAULT_FRAME_ALIGN_RIGHT 	= 103;
	
	public static final int STYLE_DEFALT_FRAME_ALIGN_CENTER_FONT_BLUE 		= 201;
	public static final int STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_RED 		= 202;
	public static final int STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_ORANGE 	= 203;
	public static final int STYLE_DEFAILT_FRANE_ALIGN_LEFT_FONT_FILE 		= 204;
	
	public static final int STYLE_YELLOW_FRAME_ALIGN_LEFT 			= 104;
	public static final int STYLE_YELLOW_FRAME_ALIGN_CENTER 		= 105;
	public static final int STYLE_YELLOW_FRAME_ALIGN_RIGHT_YELLOW 	= 106;
	public static final int STYLE_YELLOW_FRAME_ALIGN_RIGHT_YELLOW_BOLD = 107;
	public static final int STYLE_YELLOW_FRAME_ALIGN_LEFT_BOLD 			= 108;
	
	public static final int STYLE_GREEN_COMMENT_ALIGN_LEFT = 300;
	public static final int STYLE_GREEN_COMMENT_ALIGN_LEFT_SMALL = 301;
	public static final int STYLE_BLACK_SUMMARY_ALIGN_LEFT = 303;
	
	private static final short FONT_SIZE=10;
	private static final short COMMENT_FONT_SIZE = 11;
	private static final String FONT_NAME = "Calibri";
	private static final short FILE_FONT_SIZE=9;
	
	private HSSFWorkbook myWorkbook;
	private HashMap<Integer, HSSFCellStyle> mStyleMap;
	private HashMap<Integer, HSSFFont> mFontMap;
	
	protected ExcelStyle(HSSFWorkbook myWorkbook) {
		this.myWorkbook = myWorkbook;
		initialize();
	}
	
	private void initialize() {
		mFontMap = new HashMap<Integer, HSSFFont>() ;
		mFontMap.put(DEFAULT_FONT, createFont(FONT_NAME, HSSFColor.BLACK.index, FONT_SIZE));
		mFontMap.put(FONT_BLACK, createStyledFont(FONT_NAME, HSSFColor.BLACK.index, FONT_SIZE));
		mFontMap.put(FONT_BLACK_BOLD, createStyledBoldFont(FONT_NAME, HSSFColor.BLACK.index, FONT_SIZE));
		mFontMap.put(FONT_BLUE, createFont(FONT_NAME, HSSFColor.BLUE.index, FONT_SIZE));
		mFontMap.put(FONT_GREEN, createStyledFont(FONT_NAME, HSSFColor.GREEN.index, COMMENT_FONT_SIZE));
		mFontMap.put(FONT_GREEN_SMALL, createStyledFont(FONT_NAME, HSSFColor.GREEN.index, FONT_SIZE));
		mFontMap.put(FONT_FILE, createFont(FONT_NAME, HSSFColor.BLACK.index, FILE_FONT_SIZE));
		mFontMap.put(FONT_RED, createFont(FONT_NAME, HSSFColor.RED.index, FONT_SIZE));
		mFontMap.put(FONT_ORANGE, createFont(FONT_NAME, HSSFColor.ORANGE.index, FONT_SIZE));
		
		mStyleMap = new HashMap<Integer, HSSFCellStyle> ();
		mStyleMap.put(
				DEFAULT_STYLE, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT));
		
		mStyleMap.put(
				STYLE_DEFAULT_FRAME_ALIGN_CENTER, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_CENTER));

		mStyleMap.put(
				STYLE_DEFAULT_FRAME_ALIGN_LEFT, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT));

				
		mStyleMap.put(
				STYLE_DEFAULT_FRAME_ALIGN_RIGHT, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_RIGHT));

		mStyleMap.put(
				STYLE_YELLOW_FRAME_ALIGN_LEFT, 
				createColoredCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFColor.YELLOW.index
						));
		
		mStyleMap.put(
				STYLE_YELLOW_FRAME_ALIGN_CENTER, 
				createColoredCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFColor.YELLOW.index
						));
		
		mStyleMap.put(
				STYLE_YELLOW_FRAME_ALIGN_RIGHT_YELLOW, 
				createColoredCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_RIGHT,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFColor.YELLOW.index
						));
		
		mStyleMap.put(
				STYLE_YELLOW_FRAME_ALIGN_RIGHT_YELLOW_BOLD, 
				createColoredCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_RIGHT,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFColor.YELLOW.index,
						this.mFontMap.get(FONT_BLACK_BOLD)
						));
		
		mStyleMap.put(
				STYLE_YELLOW_FRAME_ALIGN_LEFT_BOLD, 
				createColoredCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFColor.YELLOW.index,
						this.mFontMap.get(FONT_BLACK_BOLD)
						));
		
		mStyleMap.put(
				STYLE_DEFALT_FRAME_ALIGN_CENTER_FONT_BLUE, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_BLUE)
						));

		mStyleMap.put(
				STYLE_DEFAILT_FRANE_ALIGN_LEFT_FONT_FILE, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_FILE)
						));			
		mStyleMap.put(
				STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_RED, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_RED)
						));
		mStyleMap.put(
				STYLE_DEFAILT_FRANE_ALIGN_CENTER_FONT_ORANGE, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_ORANGE)
						));	
		mStyleMap.put(STYLE_GREEN_COMMENT_ALIGN_LEFT, 
				createCellStyle(
						HSSFCellStyle.BORDER_NONE, 
						HSSFColor.WHITE.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_GREEN)
						));
		
		mStyleMap.put(STYLE_GREEN_COMMENT_ALIGN_LEFT_SMALL, 
				createCellStyle(
						HSSFCellStyle.BORDER_NONE, 
						HSSFColor.WHITE.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_GREEN_SMALL)
						));
		
		mStyleMap.put(STYLE_BLACK_SUMMARY_ALIGN_LEFT, 
				createCellStyle(
						HSSFCellStyle.BORDER_THIN, 
						HSSFColor.BLACK.index, 
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER ,
						this.mFontMap.get(FONT_BLACK)
						));

	}
	
	public HSSFFont getFont(int pType) {
		HSSFFont font = mFontMap.get(pType);
		if(font == null)
			font = mFontMap.get(DEFAULT_FONT);
		
		return font;
	}

	public HSSFCellStyle getStyle(int pType) {
		HSSFCellStyle style = mStyleMap.get(pType);
		if(style == null)
			style = mStyleMap.get(DEFAULT_FONT);
		
		return style;
	}
	
	private HSSFFont createFont(String fontName, short fontColor, short fontSize) {
		HSSFFont font = myWorkbook.createFont();

		font.setFontName(fontName);
		font.setColor(fontColor);
		font.setFontHeightInPoints(fontSize);

		return font;
	}
	
	private HSSFFont createStyledFont(String fontName, short fontColor, short fontSize) {
		HSSFFont font = myWorkbook.createFont();

		font.setFontName(fontName);
		font.setColor(fontColor);
		font.setFontHeightInPoints(fontSize);
		font.setFontName("Calibri");

		return font;
	}
	
	private HSSFFont createStyledBoldFont(String fontName, short fontColor, short fontSize) {
		HSSFFont font = myWorkbook.createFont();

		font.setFontName(fontName);
		font.setColor(fontColor);
		font.setFontHeightInPoints(fontSize);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("Calibri");

		return font;
	}
	
	private HSSFCellStyle createCellStyle(short borderType, short borderColor, short alignment) {
		return  createCellStyle(borderType, borderColor, alignment, HSSFCellStyle.VERTICAL_CENTER);
	}
	
	private HSSFCellStyle createCellStyle(short borderType, short borderColor, short alignment, short verticalAlignment, HSSFFont font) {
		HSSFCellStyle style = myWorkbook.createCellStyle();
		
		style.setAlignment(alignment);
		style.setVerticalAlignment(verticalAlignment);

		style.setBorderBottom(borderType);
		style.setBorderLeft(borderType);
		style.setBorderRight(borderType);
		style.setBorderTop(borderType);
		
		style.setBottomBorderColor(borderColor);
		style.setLeftBorderColor(borderColor);
		style.setRightBorderColor(borderColor);
		style.setTopBorderColor(borderColor);
		style.setWrapText(true);
		
		style.setFont(font);
		
		return style;
	}
	
	private HSSFCellStyle createCellStyle(short borderType, short borderColor, short alignment, short verticalAlignment) {
		return createCellStyle(borderType, borderColor, alignment, verticalAlignment, this.getFont(DEFAULT_FONT));	
	}
	
	private HSSFCellStyle createColoredCellStyle(short borderType, short borderColor, short alignment, short verticalAlignment, short cellColor) {
		HSSFCellStyle style =   createCellStyle(borderType, borderColor, alignment, HSSFCellStyle.VERTICAL_CENTER);
		
		style.setFillForegroundColor(cellColor);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		return style;
	}
	
	private HSSFCellStyle createColoredCellStyle(short borderType, short borderColor, short alignment, short verticalAlignment, short cellColor, HSSFFont font) {
		HSSFCellStyle style =   createCellStyle(borderType, borderColor, alignment, HSSFCellStyle.VERTICAL_CENTER, font);
		
		style.setFillForegroundColor(cellColor);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		return style;
	}
	
}
