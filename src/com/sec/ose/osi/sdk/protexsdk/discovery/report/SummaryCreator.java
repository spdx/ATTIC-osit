/**
 * Copyright(C) 2013-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.sdk.protexsdk.discovery.report;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SummaryCreator
 * @author sjh.yoo, hankido.lee
 * 
 */
public class SummaryCreator implements ReportEntityListCreator {
	private static Log log = LogFactory.getLog(SummaryCreator.class);
	
	public ReportEntityList createReportEntityList(BufferedReader XmlReportReader) {
		ReportEntityList reportEntityList = new ReportEntityList();
		ReportEntity reportEntity = new ReportEntity();
		String tmpLine = null;
		try {
			while((tmpLine = XmlReportReader.readLine()) != null) {
				tmpLine = tmpLine.trim();
				if(tmpLine.startsWith(HTML_DATA_TABLE_START_TAG)) {
					XmlReportReader.readLine();XmlReportReader.readLine();XmlReportReader.readLine();
					XmlReportReader.readLine();		// reportType
					while((tmpLine = XmlReportReader.readLine()) != null) {
						tmpLine = tmpLine.trim();
						if(tmpLine.startsWith(HTML_DATA_TABLE_END_TAG)) {
							break;
						} else if(tmpLine.startsWith(HTML_DATA_ROW_START_TAG)) {
							XmlReportReader.readLine();
							String key = XmlReportReader.readLine();
							XmlReportReader.readLine();XmlReportReader.readLine();
							String value = XmlReportReader.readLine();
							System.out.println(key+" ## "+value);
							reportEntity.setValue(key, value);
						}
					}
					break;
				}
			}
			
		}catch (IOException e) {
			log.warn(e);
		}
		
		reportEntityList.addEntity(reportEntity);
		return reportEntityList;
	}
}
