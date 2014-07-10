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
package com.sec.ose.osi.sdk.protexsdk.project;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.AnalysisCodeTreeInfo;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.AnalysisInfo;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.Tools;

/**
 * ProjectEntForReportFactory
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class ProjectEntForReportFactory {
	private static Log log = LogFactory.getLog(ProjectEntForReportFactory.class);
	
	public static ProjectEntForReport createProjectEnt(String projectName, UIResponseObserver observer) {
		return  createProjectEnt(projectName, true, observer);
	}

	public static ProjectEntForReport createProjectEnt(String projectName) {
		
	    UIResponseObserver observer = new DefaultUIResponseObserver();
		return  createProjectEnt(projectName, true, observer);
	}	
	
	public static ProjectEntForReport createProjectEnt(
			String projectName, 
			boolean countAlreadyIdentifile, 
			UIResponseObserver observer) {

		AnalysisCodeTreeInfo analysisCodeTreeInfo = null;
		AnalysisInfo analysisInfo = null; 
		ProjectEntForReport projectEnt = new ProjectEntForReport();

		String projectID = null;	
		String scanDate = null;
		long scanTime;
		String numOfSkippedFiles = null;
		long bytes;
		String createdBy = null;
		String analysisProtexVersion = null;
		String server = null;
		String license = null;
		String description = null;

		String totalFile = null;
		String pendingFile = null;
		int totalFileNum = 0;
		int originPendingFileNum = 0;
		int curPendingFileNum = 0;
		String originPendingRatio = "";
		String curPendingRatio = "";
		
		String scanStarted = "";
		String lastUpdated = "";
		String analyzedFromHost = "";
		String analyzedBy = "";

		projectID = ProjectAPIWrapper.getProjectID(projectName) ; 

		if(projectID == null) {
			return null;
		}

		projectEnt.setProjectID(projectID);

		try {
			analysisCodeTreeInfo = ProtexSDKAPIManager.getDiscoveryAPI().getLastAnalysisCodeTreeInfo(projectID);

			if( analysisCodeTreeInfo != null ){
				if(analysisCodeTreeInfo.getAnalyzedBytes() != null){
					bytes = analysisCodeTreeInfo.getAnalyzedBytes();
					projectEnt.setBytes(bytes);
				}
			}
		} catch (SdkFault e) {
			log.warn(e);
		}

		ReportEntity summary = ReportAPIWrapper.getSummary(projectName, observer, true);
		ReportEntity analysisSummary = ReportAPIWrapper.getAnalysisSummary(projectName, observer, true);
		ReportEntityList billOfMaterials = ReportAPIWrapper.getBillOfMaterials(projectName, observer, true);

		int curIdentifiedFileNum =0;
		for(ReportEntity entity: billOfMaterials){

			if( !( entity.getValue(ReportInfo.BILL_OF_MATERIALS.COMPONENT).equals(projectName)) ) {
				curIdentifiedFileNum = AbstractDiscoveryController.originPendingFileCount(projectName) - AbstractDiscoveryController.curPendingFileCount(projectName);	
			}
		}
		projectEnt.setNumOfAlreadyIdentifiedFiles(curIdentifiedFileNum);
		
		if(summary != null) {
			license = summary.getValue(ReportInfo.SUMMARY.LICENSE);
			if(license != null) projectEnt.setLicense( license );

			description =  summary.getValue(ReportInfo.SUMMARY.DESCRIPTION);
			if(description != null) projectEnt.setDescription(description);

			server =  summary.getValue(ReportInfo.SUMMARY.SERVER);
			if( server != null)	projectEnt.setServer(server);

			totalFile = summary.getValue(ReportInfo.SUMMARY.NUMBER_OF_FILES);
			if(totalFile != null) {
				totalFileNum = Tools.transStringToInteger(totalFile);
				projectEnt.setNumOfTotalFiles(totalFileNum);
			}

			pendingFile = summary.getValue(ReportInfo.SUMMARY.FILES_PENDING_IDENTIFICATION);
			if(pendingFile != null) {
				curPendingFileNum = AbstractDiscoveryController.curPendingFileCount(projectName);
				curPendingRatio = getPendingRatio(pendingFile);
				projectEnt.setCurrentPendingFileNum(curPendingFileNum);
				projectEnt.setCurrentPendingRatio(curPendingRatio);
			}
		}
		
		
		originPendingFileNum = AbstractDiscoveryController.originPendingFileCount(projectName);
		projectEnt.setOriginPendingFileNum(originPendingFileNum);
		originPendingRatio = (calculateOriginPendingRatio(totalFileNum, originPendingFileNum));
		projectEnt.setOriginPendingRatio(originPendingRatio);

		createdBy =  summary.getValue(ReportInfo.SUMMARY.PROJECT_CREATOR);
		if(createdBy != null)
			projectEnt.setCreatedBy(createdBy);

		
		if(analysisSummary != null) {
			numOfSkippedFiles = analysisSummary.getValue( ReportInfo.ANALYSIS_SUMMARY.FILES_SKIPPED);
			analysisProtexVersion = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.ANALYZED_RELEASE_DESCRIPTION );
			scanStarted = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.SCAN_STARTED);
			lastUpdated = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.LAST_UPDATED);
			analyzedFromHost = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.ANALYZED_FROM_HOST);
			analyzedBy = analysisSummary.getValue(ReportInfo.ANALYSIS_SUMMARY.ANALYZED_BY);
		}
		
		if(numOfSkippedFiles != null) {
			projectEnt.setNumOfSkippedFiles(getNumOfSkippedFiles(numOfSkippedFiles));
		}

		if(analysisProtexVersion != null) {
			projectEnt.setAnalysisProtexVersion( analysisProtexVersion );
		}

		if(scanStarted != null) {
			projectEnt.setScanStarted(scanStarted);
		}
		
		if(lastUpdated != null) {
			projectEnt.setLastUpdated(lastUpdated);
		}
		
		if(analyzedFromHost != null) {
			projectEnt.setAnalyzedFromHost(analyzedFromHost);
		}
		
		if(analyzedBy != null) {
			projectEnt.setAnalyzedBy(analyzedBy);
		}		

		try {
			analysisInfo = ProtexSDKAPIManager.getDiscoveryAPI().getLastAnalysisInfo(projectID);

			if(analysisInfo != null){
				Date startTime = analysisInfo.getAnalysisStartedDate();
				Date finishTime = analysisInfo.getAnalysisFinishedDate();

				if (startTime != null && finishTime != null){
					scanTime = finishTime.getTime() - startTime.getTime();
					if(scanTime < 0) scanTime = 0;
					projectEnt.setScanTime(scanTime);
				}else
					projectEnt.setScanTime(0);

				if (finishTime != null){		
					String dateFormat = "%1$tB %1$te, %1$tY %1$tH:%1$tM %1$Tp";	 
					scanDate = String.format(dateFormat,finishTime.getTime());
					projectEnt.setScanDate(scanDate);
				} else {
					String msg = "analysis fail"; 
					projectEnt.setScanDate(msg);
				}

			}

		} catch (SdkFault e) {
			log.warn(e);
		}

		return projectEnt;
	}
	
	private static String calculateOriginPendingRatio(int totalFileNum, int originPendingFileNum) {
		
		String result = "";
		DecimalFormat df = new DecimalFormat("##0.00%");
		String percent = df.format((double)originPendingFileNum / (double)totalFileNum );
		result = percent;
		
		return percent;
	}

	private static String getPendingRatio(String pendingFilesInfo){
		if(pendingFilesInfo != null){
			int pendingFilesIndex1 = pendingFilesInfo.indexOf("(");
		    int pendingFilesIndex2 = pendingFilesInfo.indexOf(")");
		    if( (pendingFilesIndex1 >= 0) && (pendingFilesIndex2 >= 0))
		    	return pendingFilesInfo.substring(pendingFilesIndex1+1, pendingFilesIndex2) ;
		 	 	
		}
		
		return "0%";
		
	}
	
	private static int getNumOfSkippedFiles(String skippedFiles){
		if(skippedFiles != null){
		int skippedFilesIndex = skippedFiles.indexOf("Files");  
		if(skippedFilesIndex > 0 ){
				String numOfSkippedFiles = skippedFiles.substring(0, skippedFilesIndex-1);
				numOfSkippedFiles = numOfSkippedFiles.trim();
				return Tools.transStringToInteger(numOfSkippedFiles) ;
			}
		}
		
		return 0;
	}
	
}
