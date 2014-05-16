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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.project.codetree.discovery.FileDiscoveryPatternDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * DCPatternMatch
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class DCPatternMatch extends AbstractDiscoveryController {
	private static Log log = LogFactory.getLog(DCPatternMatch.class);

	public ArrayList<String> getPendingFileList() {
		return super.getPendingFileList(IdentificationConstantValue.PATTERN_MATCH_TYPE);
	}
	
	public ArrayList<String> getIdentifiedFileList() {
		return super.getIdentifiedFileList(IdentificationConstantValue.PATTERN_MATCH_TYPE);
	}

	public int getNumOfPendingFiles() {
		return super.getNumOfPendingFiles(IdentificationConstantValue.PATTERN_MATCH_TYPE);
	}
	
	public int getNumOfDiscoveryFiles() {
		return super.getNumOfDiscoveryFiles(IdentificationConstantValue.PATTERN_MATCH_TYPE);
	}
	
	public DCPatternMatch(String pProjectName, UIResponseObserver observer) {
		super(pProjectName);
	}
	
	public void loadFromProtexServer(
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList patternMatchesPendingFiles) {
		
		PreparedStatement prep = IdentificationDBManager.getPatternMatchPreparedStatement(projectName);

		if(patternMatchesPendingFiles == null){
			System.err.println("Not Founded PatternMatches PendingFiles.");
        } else {
        	String PatternMatchFilePath = "";
        	String PatternMatchComment = "";
	        for(ReportEntity entity:patternMatchesPendingFiles) {
	        	PatternMatchFilePath = entity.getValue(ReportInfo.PATTERN_MATCHES_PENDING_FILES.FULL_PATH);
	        	PatternMatchComment = entity.getValue(ReportInfo.PATTERN_MATCHES_PENDING_FILES.COMMENT);
        		try {
                	prep.setString(1, PatternMatchFilePath);
                	prep.setString(2, null);
                	prep.setString(3, null);
                	prep.setString(4, null);
                	prep.setString(5, String.valueOf(AbstractMatchInfo.STATUS_PENDING));
                	prep.setString(6, PatternMatchComment);
                	prep.addBatch();
    			} catch (SQLException e) {
    				log.warn(e);
		        }
	        }
            IdentificationDBManager.execute(prep);
        }

		String PatternMatchFileName = "";
        String PatternMatchComponent = "";
        String PatternMatchVersion = "";
        String PatternMatchLicense = "";
        String PatternMatchComment = ""; 
		if(identifiedFiles != null){
            for(ReportEntity tmpIdentifiedFile:identifiedFiles.getEntityList()) {
            	if(tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.DISCOVERY_TYPE).equals("Other Supported Languages") ||
            			tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.RESOLUTION_TYPE).equals("Declared")){
            		PatternMatchFileName = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.FILE_FOLDER_NAME).substring(1);
            		PatternMatchComponent = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.COMPONENT);
            		PatternMatchVersion = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.VERSION);
        			if(PatternMatchVersion == null) PatternMatchVersion = "";
            		PatternMatchLicense = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.LICENSE);
            		PatternMatchComment = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.COMMENT);
            		try {
                    	prep.setString(1, PatternMatchFileName);
                    	prep.setString(2, PatternMatchComponent);
                    	prep.setString(3, PatternMatchVersion);
                    	prep.setString(4, PatternMatchLicense);
                    	prep.setString(5, String.valueOf(AbstractMatchInfo.STATUS_IDENTIFIED));
                    	prep.setString(6, PatternMatchComment);
                    	prep.addBatch();
        			} catch (SQLException e) {
        				log.warn(e);
        			}
            	}
            }
            IdentificationDBManager.execute(prep);
        }
		
    	if(prep != null){
			try {
				prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void identifyFile(IdentifyData identifyData, UIResponseObserver mObserver) {
		
		Collection<String> pFilePaths = identifyData.getFilePath();
		String pTargetVersion = identifyData.getCurrentVersionName();
		String pComponent = identifyData.getNewComponentName(); 
		String pVersion = identifyData.getNewVersionName(); 
		String pLicense = identifyData.getNewLicenseName();
		
		if(pFilePaths == null)
			return;
		
		if(pTargetVersion.equals("Unspecified")) pTargetVersion = "";
		
		for(String path:pFilePaths) {
			
			String displayedFullPath = getDisplayedFullPath(path);
			mObserver.pushMessageWithHeader(" > filePath : " + displayedFullPath);
			IdentificationDBManager.updateIdentifyInfoForPatternMatch(
					this.projectName, 
					path, pComponent, 
					pVersion, 
					pLicense);
		}
	}

	public void resetFile(
			ArrayList<String> pFilePaths, 
			String pTargetComponent, 
			String pTargetVersion, 
			String pMatchedFile,
			UIResponseObserver mObserver, 
			int compositeType,
			String pFilePath,
			String pCurrentComponentName) {
		
		String displayedFullPath = getDisplayedFullPath(pFilePath);
		mObserver.pushMessageWithHeader(" > filePath : " + displayedFullPath);
		IdentificationDBManager.updateResetInfoForPatternMatch(this.projectName, pFilePath);
	}

	public boolean resetFile(
			String projectName,
			String curFilePath,
			List<FileDiscoveryPatternDiscovery> listFileDiscoveryPatternDiscovery) {
		boolean isRemain = false;
		for(FileDiscoveryPatternDiscovery fdpd:listFileDiscoveryPatternDiscovery) {
			if (fdpd.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION) {
				String updateSQL = "update patternmatch_"+tablePostfix+" set status='0' where path='"+fdpd.getFilePath().substring(1)+"'"; // +"' and component='"+ComponentAPIWrapper.getGlobalComponentName(fdpd.get())+"';";
				log.debug("SQL : "+updateSQL);
				IdentificationDBManager.update(updateSQL);
				isRemain = true;
			} else if (fdpd.getIdentificationStatus() == IdentificationStatus.DECLARED) {
				String updateSQL = "update patternmatch_"+tablePostfix+" set status='1' where path='"+fdpd.getFilePath().substring(1)+"'"; // +"' and component='"+ComponentAPIWrapper.getGlobalComponentName(fdpd.getMatchingComponentId())+"';";
				log.debug("SQL : "+updateSQL);
				IdentificationDBManager.update(updateSQL);
				isRemain = true;
			} 
		}
		return isRemain;
	}
}
