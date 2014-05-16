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
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.tools.Tools;

/**
 * DCStringSearch
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class DCStringSearch extends AbstractDiscoveryController {
	private static Log log = LogFactory.getLog(DCStringSearch.class);
	
	public ArrayList<String> getPendingFileList() {
		return super.getPendingFileList(IdentificationConstantValue.STRING_MATCH_TYPE);
	}
	
	public ArrayList<String> getIdentifiedFileList() {
		return super.getIdentifiedFileList(IdentificationConstantValue.STRING_MATCH_TYPE);
	}

	public int getNumOfPendingFiles() {
		return super.getNumOfPendingFiles(IdentificationConstantValue.STRING_MATCH_TYPE);
	}
	
	public int getNumOfDiscoveryFiles() {
		return super.getNumOfDiscoveryFiles(IdentificationConstantValue.STRING_MATCH_TYPE);
	}
	
	public DCStringSearch(String pProjectName, UIResponseObserver observer) {
		super(pProjectName);
	}

	public void loadFromProtexServer(
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList stringSearch) {
		
	
		PreparedStatement prep = IdentificationDBManager.getStringSearchPreparedStatement(projectName);
		HashSet<String> StringSearchFileLineSet = new HashSet<String>();
		
		if(stringSearch == null){
        	System.err.println("Not Founded StringSearch.");
        	return;
		}
        
        StringBuffer stringSearchLineBuf = new StringBuffer("");
        
        if(identifiedFiles != null) {
        	for(ReportEntity tmpIdentifiedFile:identifiedFiles.getEntityList()) {
        		
            	stringSearchLineBuf.setLength(0);
            	
            	if(tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.DISCOVERY_TYPE).equals("String Search")) {
            		
            		String stringSearchFilePath = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.FILE_FOLDER_NAME).substring(1);
            		String stringSearchSearch = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.SEARCH);
            		String stringSearchComponent = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.COMPONENT);
            		String stringSearchVersion = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.VERSION);
            		String stringSearchLicense = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.LICENSE);
            		String stringSearchTotalLine = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.TOTAL_LINES);
            		String stringSearchComment = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.COMMENT);
            		String stringResolutionType = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.RESOLUTION_TYPE);
            		String stringSearchStartLine = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.FILE_LINE);
            		
            		stringSearchLineBuf.append(stringSearchFilePath);
            		stringSearchLineBuf.append(stringSearchStartLine);
            		if(stringSearchTotalLine.length()>0) {
            			int iStringSearchStartLine = Tools.transStringToInteger(stringSearchStartLine);
                        int iStringSearchTotalLine = Tools.transStringToInteger(stringSearchTotalLine);
                        int iStringSearchEndLine = iStringSearchStartLine + iStringSearchTotalLine - 1;

                        stringSearchLineBuf.append("..");
                        stringSearchLineBuf.append(iStringSearchEndLine);
            		}
            		
            		StringSearchFileLineSet.add(stringSearchLineBuf.toString());
            		if (stringSearchSearch.length() > 0) {
            			
            			String pendingStatus = String.valueOf(AbstractMatchInfo.STATUS_IDENTIFIED);
                    	if("Declared".equals(stringResolutionType))
                    		pendingStatus = String.valueOf(AbstractMatchInfo.STATUS_DECLARED);
            			
            			try {
	                    	prep.setString(1, stringSearchFilePath);
	                    	prep.setString(2, stringSearchSearch);
	                    	prep.setString(3, stringSearchComponent);
	                    	prep.setString(4, stringSearchVersion);
	                    	prep.setString(5, stringSearchLicense);
	                    	prep.setString(6, pendingStatus);
	                    	prep.setString(7, stringSearchLineBuf.toString().substring(stringSearchFilePath.length()));
	                    	prep.setString(8, stringSearchComment);
	                    	prep.addBatch();
	        			} catch (SQLException e) {
	        				log.warn(e);
	        			}
            			
            		}
            	}
            }
            IdentificationDBManager.execute(prep);
        }
        
        
        for(ReportEntity entity:stringSearch) {
        	
        	String stringSearchFilePath = entity.getValue(ReportInfo.STRING_SEARCHES.FILE);
        	String stringSearchSearch = entity.getValue(ReportInfo.STRING_SEARCHES.SEARCH);
        	String stringSearchLine = entity.getValue(ReportInfo.STRING_SEARCHES.LINE);
        	
        	if(StringSearchFileLineSet.contains(stringSearchFilePath+stringSearchLine))
        		continue;
        	
    		if(stringSearchSearch.length() > 0) {

        		try {
                	prep.setString(1, stringSearchFilePath);
                	prep.setString(2, stringSearchSearch);
                	prep.setString(3, null);
                	prep.setString(4, null);
                	prep.setString(5, null);
                	prep.setString(6, String.valueOf(AbstractMatchInfo.STATUS_PENDING));
                	prep.setString(7, stringSearchLine);
                	prep.setString(8, null); // comment
                	prep.addBatch();
    			} catch (SQLException e) {
    				log.warn(e);
    			}
    		}
        }
        IdentificationDBManager.execute(prep);
		StringSearchFileLineSet = null;
    	if(prep != null){
			try {
				prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateStringSearchIdentifyForLocalDB(String pFilePath, String pComponent, String pVersion, String pLicense) {
		pComponent = pComponent.replace("\'", "\'\'");
		String updateSQL = "update stringsearch_"+tablePostfix+" set status='1',component='"+pComponent+"',version='"+pVersion+"',license='"+pLicense+"' where path='"+pFilePath+"' and status='0';";
		log.debug("identifyStringSearch sql ~~~ : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		IdentificationDBManager.addIdentifiedFile(projectName, pFilePath, TYPE_STRING_SEARCH, pComponent, pVersion, pLicense, "");
		setStringSearchInfoForCodeMatch(pFilePath,pLicense);
	}

	public void identifyFile(IdentifyData identifyData, UIResponseObserver mObserver) {
		
		Collection<String> pFilePaths = identifyData.getFilePath();
		if(pFilePaths == null)
			return;
		
		String newComponentName = identifyData.getNewComponentName(); 
		String newVersionName = identifyData.getNewVersionName(); 
		String newLicenseName = identifyData.getNewLicenseName();
		
		for(String path:pFilePaths) {
			
			String displayedFullPath = getDisplayedFullPath(path);
			mObserver.pushMessageWithHeader(" > filePath : " + displayedFullPath);
			updateStringSearchIdentifyForLocalDB(
					path,
					newComponentName, 
					newVersionName,
					newLicenseName);
		}
	}
	
	private void setStringSearchInfoForCodeMatch(String pFilePath, String pLicense) {
		DCCodeMatch tmpCodeMatchDiscoveryController = (DCCodeMatch)ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE);
		if(tmpCodeMatchDiscoveryController == null) {
			log.warn("DCCodeMatch tmpCodeMatchDiscoveryController is null..");
			return;
		}
		tmpCodeMatchDiscoveryController.setStringSearchInformation(projectName,pFilePath,pLicense);
	}
	
	private void updateStringSearchResetForLocalDB(String pFilePath) {
		String updateSQL = "update stringsearch_"+tablePostfix+" set status='0',component=null,version=null,license=null where path='"+pFilePath+"' and status='1';";
		IdentificationDBManager.update(updateSQL);
		IdentificationDBManager.removeIdentifiedFile(projectName, pFilePath, TYPE_STRING_SEARCH, "");
		setStringSearchInfoForCodeMatch(pFilePath,"");
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
		updateStringSearchResetForLocalDB(pFilePath);
	}

	public ArrayList<String> resetFolder(String pFolderPath, String pStringSearch, String targetComponentName, String targetVersionName, UIResponseObserver mObserver, int compositeType) {
		ArrayList<String> resetTargetList = new ArrayList<String>();
		HashSet<String> hs = IdentificationDBManager.getStringSearchResetTargetFile(this.projectName, pFolderPath, pStringSearch);
		String target = "";
		mObserver.setMessageHeader("Reset processing...\n");
		if(hs == null) {
			return resetTargetList;
		}
		Iterator<String> it = hs.iterator();
		while(it.hasNext()) {
			target = it.next();
			mObserver.pushMessageWithHeader(" > filePath : " + target);
			updateStringSearchResetForLocalDB(target);
			resetTargetList.add(target);
		}
		return resetTargetList;
	}
}
