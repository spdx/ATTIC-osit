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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.util.Property;

/**
 * DefaultEntityListCreator
 * @author sjh.yoo, hankido.lee
 * 
 */
public class DefaultEntityListCreator implements ReportEntityListCreator {
	private static Log log = LogFactory.getLog(DefaultEntityListCreator.class);

	protected ArrayList<String> entityKeyList = null;
	public DefaultEntityListCreator() {}
	public ReportEntityList createReportEntityList(BufferedReader xmlReportReader) {
		// extract fields for data
		entityKeyList = buildEntityKeyListFromHTML(xmlReportReader);
		log.debug("Loading: "+entityKeyList);
		if(entityKeyList == null)
			return null;
		
		ArrayList<String> duplicationCheckFieldList=null;;
		
		String strEnabled = Property.getInstance().getProperty(Property.MINIMIZED_CODE_MATCH_PENDING_FILE_LIST_ENABLED);
		
		if(strEnabled != null &&
				"true".equals(strEnabled.toLowerCase())) { 
			
			// Checking if "CodeMatches PendingIdentification Precision" is loaded 
			if(entityKeyList != null &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.FILE) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.COMPONENT)&&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.VERSION) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.LICENSE) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.USAGE) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.PRECISION) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.MATCHED_FILE) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.SIZE) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.FILE_LINE ) &&
					entityKeyList.contains(ReportInfo.CODE_MATCHES_PRECISION.TOTAL_LINES ) 
					) {
				
				duplicationCheckFieldList = new ArrayList<String>();
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.FILE);
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.SIZE);
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.FILE_LINE);
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.TOTAL_LINES);
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.COMPONENT);
				duplicationCheckFieldList.add(ReportInfo.CODE_MATCHES_PRECISION.LICENSE);
				
				log.debug("MINIMIZED_CODE_MATCH_PENDING_FILE_LIST_ENABLED enabled");
			}
		}
		
		// data fill start
//		ReportEntityList entityList = buildEntityList(xmlReportReader,entityKeyList,duplicationCheckFieldList);
		ReportEntityList entityList = buildEntityListFromHTML(xmlReportReader,entityKeyList,duplicationCheckFieldList); 
		
		return entityList;
	}
	
	public ArrayList<String> buildEntityKeyListFromHTML(BufferedReader htmlReport) {
		ArrayList<String> entityKeyList = new ArrayList<String>();
		String tmpLine = null;
		try {
			while((tmpLine = htmlReport.readLine()) != null) {
				tmpLine = tmpLine.trim();
				if(tmpLine.startsWith(HTML_DATA_TABLE_START_TAG)) {
					htmlReport.readLine();htmlReport.readLine();htmlReport.readLine();
					htmlReport.readLine();	// reportType
					htmlReport.readLine();htmlReport.readLine();htmlReport.readLine();
					while((tmpLine = htmlReport.readLine()) != null) {
						tmpLine = tmpLine.trim();
						if(tmpLine.startsWith("<th nowrap=\"nowrap\">")) {
							entityKeyList.add(tmpLine.substring(tmpLine.indexOf("<th nowrap=\"nowrap\">")+"<th nowrap=\"nowrap\">".length(), tmpLine.indexOf("</th>")));
						}
						if(tmpLine.startsWith("</tr>")) {
							break;
						}
					}
				}
				if(tmpLine.startsWith("</thead>")) {
					break;
				}
			}
		}catch (IOException e) {
			log.warn(e);
		}
		return entityKeyList;
	}
	
	protected ReportEntityList buildEntityListFromHTML(
			BufferedReader htmlReportReader, 
			ArrayList<String>entityKeyList,
			ArrayList<String>duplicationCheckingField
			) {
		ReportEntityList reportEntityList = new ReportEntityList();
		ReportEntity reportEntity = null;
		String tmpLine = null;
		
		if (duplicationCheckingField== null) {
			duplicationCheckingField = new ArrayList<String>();
		}
		
		int insertedCnt=0;

		try {
			StringBuffer tmpValue = new StringBuffer("");
			while((tmpLine = htmlReportReader.readLine()) != null) {
				tmpLine = tmpLine.trim();
				if(tmpLine.startsWith("<tr ")) {
					reportEntity = new ReportEntity();	
					int index = 0;
					while((tmpLine = htmlReportReader.readLine()) != null) {
						tmpLine = tmpLine.trim();
						if(tmpLine.startsWith("<td ")) {
							while((tmpLine = htmlReportReader.readLine()) != null) {
								tmpLine = tmpLine.trim();
								if(tmpLine.startsWith("</td>")) {
									String key = entityKeyList.get(index);
									String value = "";
									
									if(key.equals(ReportInfo.COMPARE_CODE_MATCHES.COMPARE_CODE_MATCHES_LINK)) {
										value = extractURL(tmpValue);
									} else {
										value = removeTag(tmpValue);
									}
									reportEntity.setValue(key, value);
									tmpValue.setLength(0);
									++index;
									break;
								}
								tmpValue.append(tmpLine);
							}
						}
						if(tmpLine.startsWith("</tr>")) {
							if(hasNoData(entityKeyList, index)) {
								break;
							}
							reportEntityList.addEntity(reportEntity);
							insertedCnt++;
							if(insertedCnt % 10000 == 0) {
								log.debug("buildEntityList insertedCnt: "+insertedCnt);
							}
							break;
						}
					}
				}
				if(tmpLine.startsWith(HTML_DATA_TABLE_END_TAG)) {
					break;
				}
			}
		} catch (IOException e) {
			log.warn(e);
			String[] buttonOK = {"OK"};
			JOptionPane.showOptionDialog(null, "Out Of Memory Error", "Java heap space", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttonOK, "OK");
		}
		log.debug("buildEntityList insertedCnt finally : "+insertedCnt);
		return reportEntityList;
	}
	
	private String extractURL(StringBuffer tmpValue) {
	
		String result = "";
		String text = tmpValue.toString();
		
	    Pattern p = Pattern.compile("<a[^>]* href=\"([^\"]*)\"");
	    Matcher m = p.matcher(text);
	    
	    if (m.find() == true) {
	    	result = m.group(1);
	    	log.debug("Matched: " + result);
	    } else {
	      	log.debug("No match.");
	    }
	    
		return result;
	}
	
	private String removeTag(StringBuffer tmpValue) {
		return tmpValue.toString().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	private boolean hasNoData(ArrayList<String> entityKeyList, int index) {
		return index < entityKeyList.size();
	}
	
	public ArrayList<String> buildEntityKeyList(BufferedReader XmlReportReader) {
		String tmpLine = null;
		ArrayList<String> entityKeyList = new ArrayList<String>();
		try {
			while ((tmpLine = XmlReportReader.readLine()) != null) {
				if(tmpLine.startsWith(ROW_END_TAG)) {
					tmpLine = XmlReportReader.readLine();	// read <Row ss:Index="#">
					if(tmpLine.startsWith(TABLE_END_TAG))
						return null;
					else break;
				} else {
					tmpLine = tmpLine.substring(tmpLine.indexOf(DATA_START_TAG)+DATA_START_TAG_LEN, tmpLine.indexOf(DATA_END_TAG));
					entityKeyList.add(tmpLine);		
				}
			}
		}catch (IOException e) {
			log.warn(e);
		}		
		return entityKeyList;
	}
	
	protected ReportEntityList buildEntityList(
			BufferedReader XmlReportReader, 
			ArrayList<String>entityKeyList,
			ArrayList<String>duplicationCheckingField
			) {
		ReportEntityList reportEntityList = new ReportEntityList();
		ReportEntity reportEntity = new ReportEntity();
		String tmpLine = null;
		StringBuffer value = new StringBuffer();
		int index = 0;
		
		if (duplicationCheckingField== null) {
			duplicationCheckingField = new ArrayList<String>();
		}
		boolean entityDuplicationCheck = (duplicationCheckingField.size() > 0)? true: false;
		HashSet<String> entityDuplicationCheckKeySet = new HashSet<String>();
		String duplicationCheckString="";
		
		int totalCnt=0;
		int insertedCnt=0;

		try {
			while ((tmpLine = XmlReportReader.readLine()) != null) {
			
			totalCnt++;
			if(totalCnt % 10000 == 0) {
				log.debug("buildEntityList cnt: "+totalCnt+", insertedCnt: "+insertedCnt);
			}
			if(totalCnt > Property.getInstance().getMaxNumOfReportEntity()) {
	
				log.error("Report Entity is larger than MAX_NUM_OF_REPORT_ENTITY: " + Property.getInstance().getMaxNumOfReportEntity());
				JOptionPane.showMessageDialog(null,
						"[OUT OF MEMORY] Project loading has been failed.\n"
						+"Please, Reanalyze this project with smallar files.\n" 
						+"to reduce the size of project.\n",
						"Program Exit - Project size is too big", 
						JOptionPane.ERROR_MESSAGE);
						System.exit(0);
				}

				if(tmpLine.startsWith(ROW_END_TAG)) {

					if(entityDuplicationCheck == true) {
						if(entityDuplicationCheckKeySet.contains(duplicationCheckString) == false) {
										reportEntityList.addEntity(reportEntity);
										insertedCnt++;
						}
						
						entityDuplicationCheckKeySet.add(duplicationCheckString);
						duplicationCheckString="";
					}
					else {
						reportEntityList.addEntity(reportEntity);
						insertedCnt++;
					}
					
					reportEntity = new ReportEntity();
					index = 0;
					if(XmlReportReader.readLine().equals(TABLE_END_TAG)) {
						break;	// read <Row ss:Index="#">
					}
				} else {
					int startIndex = tmpLine.indexOf(DATA_START_TAG);
					if(startIndex>0) {
						tmpLine = tmpLine.substring(startIndex+DATA_START_TAG_LEN);
					}
					int endIndex = tmpLine.indexOf(DATA_END_TAG_WITH_NS);
					if(endIndex>=0) {
						value.append(tmpLine.substring(0, endIndex));
						if(entityDuplicationCheck == true) {
							String currentKey = entityKeyList.get(index);
							if(duplicationCheckingField.contains(currentKey))
								duplicationCheckString += value.toString()+"-";
						}
						reportEntity.setValue(entityKeyList.get(index), value.toString());
						index++;
						value = new StringBuffer();
							
					} else {
						value.append(tmpLine);						
					}
			
				}

			}
		}catch (IOException e) {
			log.warn(e);
			String[] buttonOK = {"OK"};
			JOptionPane.showOptionDialog(null, "Out Of Memory Error", "Java heap space", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttonOK, "OK");
		}
		log.debug("total buildEntityList cnt: "+totalCnt+", inserted cnt: "+insertedCnt);
		return reportEntityList;
	}
	
	
}
