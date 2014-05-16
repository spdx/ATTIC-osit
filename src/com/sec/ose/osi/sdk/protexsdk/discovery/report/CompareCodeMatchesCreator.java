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

import com.sec.ose.osi.data.LoginSessionEnt;

/**
 * CompareCodeMatchesCreator
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class CompareCodeMatchesCreator extends DefaultEntityListCreator {
	private static Log log = LogFactory.getLog(CompareCodeMatchesCreator.class);
	
	static final String HRefStartAttr = "ss:HRef=\"";
	static final int HRefStartAttrLength = HRefStartAttr.length();
	static final String HRefEndStr = "\"><ss:Data";
	
	protected CompareCodeMatchesCreator() {}

	protected ReportEntityList buildEntityList(BufferedReader XmlReportReader) {
		ReportEntityList reportEntityList = new ReportEntityList();
		ReportEntity Entiry = new ReportEntity();
		String tmpLine = null;
		int index = 0;
		try {
			while ((tmpLine = XmlReportReader.readLine()) != null) {
				//LogManager.debug(tmpLine);
				if(tmpLine.startsWith(ROW_END_TAG)) {
					reportEntityList.addEntity(Entiry);
					Entiry = new ReportEntity();
					index = 0;
					if( (XmlReportReader.readLine()!=null) && (XmlReportReader.readLine().equals(TABLE_END_TAG)) ) {
						break;	// read <Row ss:Index="#">
					}
				} else {
					String key = entityKeyList.get(index++);
					String value = null;
					if(index<4) {
						value = tmpLine.substring(tmpLine.indexOf(DATA_START_TAG)+DATA_START_TAG_LEN, tmpLine.indexOf(DATA_END_TAG_WITH_NS));
					} else {
						// HRef
						value = tmpLine.substring(tmpLine.indexOf(HRefStartAttr)+HRefStartAttrLength, tmpLine.indexOf(HRefEndStr)).replaceFirst("127.0.0.1", LoginSessionEnt.getInstance().getProtexServerIP());
					}
					//LogManager.debug(key + ":"+value);
					Entiry.setValue(key, value);
				}
			}
		}catch (IOException e) {
			log.warn(e);
		}
		return reportEntityList;
	}
}
