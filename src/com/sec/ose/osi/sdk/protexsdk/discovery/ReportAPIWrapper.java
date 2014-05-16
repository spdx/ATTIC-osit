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
package com.sec.ose.osi.sdk.protexsdk.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.sec.ose.osi.sdk.airs.AIRSAPI;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportFactory;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo.ReportType;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.PrintStreamUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * ReportAPIWrapper
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class ReportAPIWrapper {
	public static Log log = LogFactory.getLog(ReportAPIWrapper.class);
	
	private static String SEPERATOR = "#";
	
	private static Hashtable<String, ReportEntityList> mReportTable = new Hashtable<String, ReportEntityList>();

	protected static void discardAllData() {
		mReportTable.clear();
		System.gc();
	}
	
	protected static void discardAllDataForProject(String pProjectName) {
		log.debug( "refresh() - "+pProjectName);

		HashSet<String> removeKeySet = new HashSet<String>();
		String removeKeyword = pProjectName+SEPERATOR;
		
		for(String curKey:mReportTable.keySet()) {
			
			if(curKey.startsWith(removeKeyword)) {
				removeKeySet.add(curKey);
			}
		}
		
		for(String removeKey: removeKeySet) {
			log.debug("discardData: "+removeKey);			
			mReportTable.remove(removeKey);
		}

		System.gc();
	}

	private static ReportEntityList getReportEntityListFromLocalMemory(
			String pProjectName, 
			ReportType pReportType, 
			UIResponseObserver observer) {

		log.debug("getReportEntityList - type:" +pReportType.getType());

		if(observer == null) {
			observer = new DefaultUIResponseObserver();
		}
	
		String strReportType = pReportType.getType().toUpperCase();
		String pMessage = " > Generating ["+strReportType+"] report .\n";
		
		observer.pushMessageWithHeader(pMessage);

		
		String reportKey = pProjectName + SEPERATOR + strReportType;
		ReportEntityList tmpReportEntityList = mReportTable.get(reportKey);
		if(tmpReportEntityList == null) {
			tmpReportEntityList = getReportEntityListFromProtexServer(
					pProjectName, 
					pReportType, 
					observer);
		}
		
		return tmpReportEntityList;
	}
	
	private static ReportEntityList getReportEntityListFromProtexServer(
			String pProjectName, 
			ReportType pReportType, 
			UIResponseObserver observer) {

		log.debug("getReportEntityList - type:" +pReportType.getType());

		if(observer == null) {
			observer = new DefaultUIResponseObserver();
		}
	
		String strReportType = pReportType.getType().toUpperCase();
		String pMessage = " > Generating ["+strReportType+"] report .\n";
		
		observer.pushMessageWithHeader(pMessage);

		
		String reportKey = pProjectName + SEPERATOR + strReportType;
		ReportEntityList tmpReportEntityList = mReportTable.get(reportKey);
		
		pMessage = " > Generating ["+strReportType+"] report from Protex Server.\n";
		observer.pushMessageWithHeader(pMessage);
		
		tmpReportEntityList = ReportFactory.getReportEntityList(pProjectName, pReportType, observer);
		if(tmpReportEntityList == null) {
			tmpReportEntityList = new ReportEntityList();
		}
		
		mReportTable.put(reportKey, tmpReportEntityList);
		return tmpReportEntityList;
	}
	
	public static ReportEntity getSummary(String pProjectName, UIResponseObserver observer, boolean useServer) {
		
		ReportEntityList summaryEntityList = null;
		if(useServer == true) {
			summaryEntityList = getReportEntityListFromProtexServer(pProjectName,new ReportInfo.SUMMARY(), observer);
		} else {
			summaryEntityList = getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.SUMMARY(), observer);
		}
		
		if(summaryEntityList != null) {
			if(summaryEntityList.size() > 0) 
				return summaryEntityList.getEntityList().get(0);
		}
		return null;
	}

	public static ReportEntity getAnalysisSummary(String pProjectName,UIResponseObserver observer, boolean useServer){
		
		ReportEntityList analysisSummaryEntityList = null;
		if(useServer == true) {
			analysisSummaryEntityList = getReportEntityListFromProtexServer(pProjectName,new ReportInfo.ANALYSIS_SUMMARY(), observer);
		} else {
			analysisSummaryEntityList = getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.ANALYSIS_SUMMARY(), observer);
		}

		if(analysisSummaryEntityList != null) {
			if(analysisSummaryEntityList.size() > 0)
				return analysisSummaryEntityList.getEntityList().get(0);
		}
		return null;
	}

	public static ReportEntityList getBillOfMaterials(String pProjectName, UIResponseObserver observer, boolean useServer) {
		if(useServer == true) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.BILL_OF_MATERIALS(), observer);
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.BILL_OF_MATERIALS(), observer);
		}
	}
	
	public static ReportEntityList getIdentifiedFiles(String pProjectName, UIResponseObserver observer, boolean useServer) {
		log.debug("createIdentifiedFileEntListFromReportSection");
		if(useServer) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.IDENTIFIED_FILES(), observer);
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.IDENTIFIED_FILES(), observer);
		}
	}

	public static ReportEntityList getCompareCodeMatches(String pProjectName, UIResponseObserver observer, boolean useServer) {
		if(useServer == true) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.COMPARE_CODE_MATCHES(), observer);
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.COMPARE_CODE_MATCHES(), observer);	
		}
	}
	
	protected static ReportEntityList getPatternMatchesPendingFiles(String pProjectName, UIResponseObserver observer, boolean useServer) {
		if(useServer == true) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.PATTERN_MATCHES_PENDING_FILES(), observer);
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.PATTERN_MATCHES_PENDING_FILES(), observer);
		}
	}
	
	public static ReportEntityList getStringSearches(String pProjectName, UIResponseObserver observer, boolean useServer) {
		if(useServer == true) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.STRING_SEARCHES(), observer);
			
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.STRING_SEARCHES(), observer);
			
		}
	}

	// TEST
	public static ReportEntityList getCodeMatchesPendingIdentificationPrecision(String pProjectName, UIResponseObserver observer) {
		return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION(), observer);
	}

	public static ReportEntityList getCodeMatchesPrecision(String pProjectName, UIResponseObserver observer, boolean useServer) {
		if(useServer == true) {
			return getReportEntityListFromProtexServer(pProjectName,new ReportInfo.CODE_MATCHES_PRECISION(), observer);
		} else {
			return getReportEntityListFromLocalMemory(pProjectName,new ReportInfo.CODE_MATCHES_PRECISION(), observer);
		}
	}
	
	public static ReportEntityList precessCodeMatchesPrecision(String pProjectName, UIResponseObserver observer, boolean useServer) {
		ReportFactory.compositeCodematchTable(pProjectName, observer);
		return null;
	}
	
	public static void generateSPDXDocument(ArrayList<String> projectNameList, ArrayList<String> targetFilePathList, SpdxReportConfiguration spdxReportConfiguration, UIResponseObserver observer) {
		log.debug("Generate SPDX Report - "+new java.util.Date());

		boolean isFail = false;
		String retMsg = "";
		if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS) {
			if(observer.getSuccessMessage() != null) {
				retMsg = observer.getSuccessMessage();
			}
		} else {
			if(observer.getFailMessage() != null) {
				retMsg = observer.getFailMessage();
			}
		}
		if(retMsg.length() > 0) retMsg += "\n";
		
		// Start AIRS
		AIRSAPI AIRSApi = new AIRSAPI();
		try {
			AIRSApi.init(ProtexSDKAPIManager.getMyProtexServer());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(int i=0;i<projectNameList.size();i++) {
			String projectName = projectNameList.get(i);
			String pMessage = " Generating ["+projectName+"] SPDX Document ("+(i+1)+"/"+projectNameList.size()+").\n";
			observer.setMessageHeader(pMessage);
			
			boolean ret = false;
			String tmpRet = "";
			try {
				ret = AIRSApi.export(ProjectAPIWrapper.getProjectID(projectName), 
						targetFilePathList.get(i), 
						spdxReportConfiguration.getPackageName(), 
						spdxReportConfiguration.getPackageFileName(), 
						spdxReportConfiguration.getOrganization(), 
						spdxReportConfiguration.getCreatedBy(), 
						spdxReportConfiguration.getCreatedByEMail(), new PrintStreamUIResponseObserver(observer));
			} catch (Exception e) {
				if(e.getMessage().indexOf("spdx.cannot.compute.file.sha1")>0) {
					tmpRet += "\n      : SPDX reports require data that was not created when scanning using some earlier versions of Protex.";
					tmpRet += "\n         You will need to re-analyze all files in this project in order to run this report.";
				}
			}
			if(!ret) {
				isFail = true;
				retMsg += "\n X \""+targetFilePathList.get(i)+"\" has been created fail.";
				if(!tmpRet.equals("")) retMsg += tmpRet;
			} else {
				retMsg += "\n O \""+targetFilePathList.get(i)+"\" has been created successfully.";
			}
		}
		
		if(isFail) {
			observer.setResult(UIResponseObserver.RESULT_FAIL);
			observer.setFailMessage(retMsg);
		} else {
			observer.setResult(UIResponseObserver.RESULT_SUCCESS);
			observer.setSuccessMessage(retMsg);
		}
	}
}
