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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.sec.ose.osi.report.ReportTemplateInfo;
import com.sec.ose.osi.report.ReportTemplateMgr;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.FileOperator;
import com.sec.ose.osi.util.tools.FormatUtil;

/**
 * ReportExcelFileManager
 * @author suhyun47.kim, sjh.yoo 
 * 
 */
public class ReportExcelFileManager {
	private static Log log = LogFactory.getLog(ReportExcelFileManager.class);
	
	protected static final String COVER_SHEET_NAME = "Cover";
	
	
	protected HSSFWorkbook 		mWorkBook;
	protected ExcelStyle 		mExcelStyle = null;
	
	
	private FileOutputStream 	mFileOutputStream;
	private FileInputStream 	mFileInputStream;
	
	private File mWriteFile;
	private File tempReadFile;
	private File tempWriteFile;

	public ReportExcelFileManager(
			String srcFilePath, 
			String destFilePath, 
			UIResponseObserver observer)  {

		POIFSFileSystem readFileSystem = null;
		
		try {
			readFileSystem = openExcelFile(srcFilePath, destFilePath);
			mWorkBook = new HSSFWorkbook(readFileSystem);
//			this.observer = observer;
			this.mExcelStyle = new ExcelStyle(mWorkBook);
			

		}catch (IOException e) {
				log.warn(e);
		}
        

		

	}
	
	public void close() throws IOException {
		
log.debug("Write Excel File - start");		
		try {
			mWorkBook.write(mFileOutputStream);
			mFileOutputStream.flush();
			
			mFileInputStream.close();
			mFileOutputStream.close();
			
			FileOperator.copyFile(tempWriteFile, mWriteFile);
log.debug("Write Excel File - done");
		}
		catch(IOException e) {
			log.warn(e);
			throw e;
		}
		finally {
			
			if(tempReadFile != null && tempReadFile.exists()) {
				tempReadFile.delete();
			}
			tempWriteFile.delete();
		}
			

	}
	
	protected boolean isExistedSheetName(String sheetName) {
		
		int index = mWorkBook.getSheetIndex(sheetName);
		if(index == -1)
			return false;

		return true;
	}
	
	private POIFSFileSystem openExcelFile(String srcFilepath, String destFilepath)  throws IOException {
		
		// make temp write file object
		mWriteFile = new File(destFilepath);
		tempWriteFile = new File("~write~"+mWriteFile.getName()+".tmp");

		// make read file object
        File readFile = null;
        if(srcFilepath != null) {
        	readFile = new File(srcFilepath);
        }
        else {
//			String reportTemplateName = LoginSessionEnt.getInstance().getReportTemplateName();
        	ReportTemplateInfo templateInfo = ReportTemplateMgr.getInstance().getReportTemplate();
//			ReportTemplateInfo templateInfo = ReportTemplateMap.getInstance().getReportTemplate(reportTemplateName);
			if(templateInfo == null)
				throw new NullPointerException();
				
			readFile = templateInfo.getReportTemplate();
log.debug("read excel file path: " + readFile.getPath());			
        	
        }
        
        // make temp read file object if needed
        if(srcFilepath != null && srcFilepath.equals(destFilepath)) {
        	tempReadFile = new File("~read~"+readFile.getName()+".tmp");
        	
        	FileOperator.copyFile(readFile, tempReadFile);
        	mFileInputStream = new FileInputStream(tempReadFile);
        }
        else {
        	mFileInputStream = new FileInputStream(readFile);
        }
        
		
		
		mFileOutputStream = new FileOutputStream(tempWriteFile);
		POIFSFileSystem readFileSystem = new POIFSFileSystem(mFileInputStream);
		return readFileSystem;
	}

    public HSSFSheet getCoverSheet() { 
    	if(mWorkBook == null)
    		return null;
    	
    	HSSFSheet coverSheet = mWorkBook.getSheet(COVER_SHEET_NAME);
    	return coverSheet;
    }

	public static String getReportFileNameFromProjectName(String protexProjectName) {
		

		
		String reportFileName = protexProjectName
								+ "_"
								+ FormatUtil.getTimeIdentifier()
								+ ".xls"
								;

		reportFileName = reportFileName.replace(" ", "_");
		return reportFileName;
	}

	public static String getSPDXDocumentFileNameFromProjectName(String protexProjectName) {
		

		
		String reportFileName = protexProjectName
								+ "_"
								+ FormatUtil.getTimeIdentifier()
								+ ".rdf"
								;

		reportFileName = reportFileName.replace(" ", "_");
		return reportFileName;
	}

	public ExcelStyle getExcelStyle() {
		return mExcelStyle;
	}

}
