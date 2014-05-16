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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.CodeMatchInfo;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.codematch.JPanCodeMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.tools.QueryUtil;
import com.sec.ose.osi.util.tools.Tools;

/**
 * DCCodeMatch
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class DCCodeMatch extends AbstractDiscoveryController {
	private static Log log = LogFactory.getLog(DCCodeMatch.class);
	
	public static String START_ORIGIN = "(--";
	public static String END_ORIGIN = "--)";
	public static String getOriginValue(String pTarget) {
		if(pTarget==null) return "";
		int start = pTarget.indexOf(START_ORIGIN);
		if(start >= 0) return pTarget.substring(start+START_ORIGIN.length(), pTarget.indexOf(END_ORIGIN));
		else return pTarget;
	}
	public static String getCurrentValue(String pTarget) {
		int start = pTarget.indexOf(START_ORIGIN);
		if(start >= 0) return pTarget.substring(0, start);
		else return pTarget;
	}
	public ArrayList<String> getPendingFileList() {
		return super.getPendingFileList(IdentificationConstantValue.CODE_MATCH_TYPE);
	}
	
	public ArrayList<String> getIdentifiedFileList() {
		return super.getIdentifiedFileList(IdentificationConstantValue.CODE_MATCH_TYPE);
	}

	public int getNumOfPendingFiles() {
		return super.getNumOfPendingFiles(IdentificationConstantValue.CODE_MATCH_TYPE);
	}
	
	public int getNumOfDiscoveryFiles() {
		return super.getNumOfDiscoveryFiles(IdentificationConstantValue.CODE_MATCH_TYPE);
	}
	
	public DCCodeMatch(
			String pProjectName, 
			UIResponseObserver observer) {
		
		super(pProjectName);
	}
	
	public void loadFromProtexServer(
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList compareCodeMatches,
			ReportEntityList codeMatchesPrecision) {
		
		PreparedStatement prepIdentifyTable = IdentificationDBManager.getIdentificationFilePreparedStatement(projectName);
		PreparedStatement prepCodematchTable = IdentificationDBManager.getCodeMatchPreparedStatement(projectName);
		PreparedStatement prepIdentifiedTable = IdentificationDBManager.getPreparedStatementForSettingIdentifiedFiles(projectName);

		if(compareCodeMatches == null){
        	System.err.println("Not Founded Compare CodeMatches.");
        } else {
            for(ReportEntity reportEntity:compareCodeMatches) {
                String codeMatchFilePath = reportEntity.getValue(ReportInfo.COMPARE_CODE_MATCHES.FULL_PATH);
                codeMatchFilePath = codeMatchFilePath.substring(1);
                String codeMatchURL = reportEntity.getValue(ReportInfo.COMPARE_CODE_MATCHES.COMPARE_CODE_MATCHES_LINK);
                codeMatchURL = transURL(codeMatchURL);
                
                try {
                	prepIdentifyTable.setString(1, codeMatchFilePath);
                	prepIdentifyTable.setString(2, codeMatchURL);
                	prepIdentifyTable.setString(3, "");
                	prepIdentifyTable.addBatch();
				} catch (SQLException e) {
					log.warn(e);
		        }
            }
    		IdentificationDBManager.execute(prepIdentifyTable);
        }

        String CodeMatchFileName = "";
        String CodeMatchComponent = "";
        String CodeMatchVersion = "";
        String CodeMatchLicense = "";

        int CodeMatchUsage = CodeMatchInfo.USAGE_SNIPPET;
        int CodeMatchPercentage = 0;
        String CodeMatchedFile = "";
        String CodeMatchedStatus = "";
        String CodeMatchComment = "";
        
		if(codeMatchesPrecision == null){
        	System.err.println("Not Founded CodeMatches PendingIdentification Precision.");
        } else {
            for(ReportEntity tmpPendingFile:codeMatchesPrecision) {
            	CodeMatchFileName = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.FILE);
            	CodeMatchComponent = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.COMPONENT);
                CodeMatchVersion = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.VERSION);
    			if(CodeMatchVersion == null) CodeMatchVersion = "";
                CodeMatchLicense = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.LICENSE);
                
                CodeMatchUsage = (tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.USAGE).equals("File"))?CodeMatchInfo.USAGE_FILE:CodeMatchInfo.USAGE_SNIPPET;
                CodeMatchedStatus = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.STATUS);
                String strCodeMatchPercentage = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.PRECISION);
                CodeMatchPercentage = Tools.transStringToInteger(strCodeMatchPercentage.substring(0,strCodeMatchPercentage.length()-1));
                CodeMatchedFile = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.MATCHED_FILE);
                CodeMatchComment = tmpPendingFile.getValue(ReportInfo.CODE_MATCHES_PRECISION.FILE_COMMENT);
                CodeMatchComment = CodeMatchComment.replace("<br />", "\n");
                
                try {
					prepCodematchTable.setString(1, CodeMatchFileName);
	                prepCodematchTable.setString(2, CodeMatchComponent);
	                prepCodematchTable.setString(3, CodeMatchVersion);
	                prepCodematchTable.setString(4, CodeMatchLicense);
	                prepCodematchTable.setString(5, String.valueOf(CodeMatchUsage));
	                if(CodeMatchedStatus.startsWith("Precision Match")) {
	                	prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_PENDING));
	                } else if(CodeMatchedStatus.startsWith("Identified")) {
                		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_IDENTIFIED));
	                } else if(CodeMatchedStatus.startsWith("Identified by Generic Version")) {
	                	prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_REJECT));
	                } else if(CodeMatchedStatus.startsWith("Rejected")) {
                		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_REJECT));
	                } else if(CodeMatchedStatus.startsWith("Declared")) {
                		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_DECLARED));
	                } 
	                prepCodematchTable.setInt(7, CodeMatchPercentage);
	                prepCodematchTable.setString(8, CodeMatchedFile);
	                prepCodematchTable.setString(9, CodeMatchComment);
	                prepCodematchTable.addBatch();
				} catch (SQLException e) {
					log.warn(e);
				}
            }
    		IdentificationDBManager.execute(prepCodematchTable);
        }

		if(identifiedFiles != null){
            for(ReportEntity tmpIdentifiedFile:identifiedFiles.getEntityList()) {
        		CodeMatchFileName = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.FILE_FOLDER_NAME).substring(1);
        		String DiscoveryType = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.DISCOVERY_TYPE);
        		String ResolutionType = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.RESOLUTION_TYPE);
        		if(DiscoveryType.equals("String Search")) {
        			DiscoveryType = "1";
        		} else if(DiscoveryType.equals("Code Match") && !ResolutionType.equals("Declared")) {
        			DiscoveryType = "2";
        		} else if(DiscoveryType.equals("Other Supported Languages") || ResolutionType.equals("Declared")){
        			DiscoveryType = "3";
        		} else {
        			continue;
        		}
        		CodeMatchComponent = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.COMPONENT);
        		CodeMatchVersion = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.VERSION);
    			if(CodeMatchVersion == null) CodeMatchVersion = "";
        		CodeMatchLicense = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.LICENSE);

                CodeMatchedFile = tmpIdentifiedFile.getValue(ReportInfo.IDENTIFIED_FILES.MATCHED_FILE);

                try {
                	prepIdentifiedTable.setString(1, CodeMatchFileName);
                	prepIdentifiedTable.setString(2, DiscoveryType);
                	prepIdentifiedTable.setString(3, CodeMatchComponent);
                	prepIdentifiedTable.setString(4, CodeMatchVersion);
                	prepIdentifiedTable.setString(5, CodeMatchLicense);
                	prepIdentifiedTable.setString(6, CodeMatchedFile);
                	prepIdentifiedTable.addBatch();
				} catch (SQLException e) {
					log.warn(e);
				}
            }
            IdentificationDBManager.execute(prepIdentifiedTable);
        }
		
    	try {
            if (prepIdentifiedTable != null) {
                try { prepIdentifiedTable.close(); } catch (Exception e) { log.warn(e); }
            }
            if (prepCodematchTable != null) {
                try { prepCodematchTable.close(); } catch (Exception e) { log.warn(e); }
            }
            if (prepIdentifyTable != null) {
                try { prepIdentifyTable.close(); } catch (Exception e) { log.warn(e); }
            }
        } catch (Exception e) {
        	log.warn(e);
        }
	}
	
	private String transURL(String codeMatchURL) {
		final String ip = LoginSessionEnt.getInstance().getProtexServerIP();
		final String ORG_IP = "http://127.0.0.1:80/protex/ProtexIPCompareCodeMatchesTopPage?";
		final String TO_IP = "http://"+ip+"/protex/ProtexIPCompareCodeMatchesTopPage?5=15&amp;uifsid=4";
		final String ORG_AMP = "&;";
		final String TO_AMP = "&amp;";
		codeMatchURL = codeMatchURL.replace(ORG_IP,TO_IP);
		codeMatchURL = codeMatchURL.replace(ORG_AMP,TO_AMP);
		return codeMatchURL;
	}

	
	public String getCodeMatchedURL(String pFilePath) {
		return IdentificationDBManager.getURLForIdentifyFile(this.projectName, pFilePath);
	}

	public String getStringSearchLicense(String projectName, String pFilePath) {
		return IdentificationDBManager.getStringSearchLicense(projectName, pFilePath);
	}
	
	public void setStringSearchInformation(String pProjectName, String pFilePath, String pLicense) {
		try {
			IdentificationDBManager.setStringSearchLicense(pProjectName,pFilePath,pLicense);
		} catch (Exception e) {
			log.warn(e);
		}
	}

	private void identifyCodeMatch(
				String pFilePath, 
				String curComponentNAme, 
				String curVersionName, 
				String newComponentName, 
				String newVersionName, 
				String newLicenseName) {
		String updateSQL = "UPDATE codematch_"+tablePostfix+" " +
						   "SET status='"+AbstractMatchInfo.STATUS_REJECT+"' " +
						   "WHERE path='"+pFilePath+"' AND status='"+AbstractMatchInfo.STATUS_PENDING+"';";
		log.debug("identifyCodeMatch SQL1 : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		
		updateSQL = "UPDATE codematch_"+tablePostfix+
					" SET status='"+AbstractMatchInfo.STATUS_IDENTIFIED+"'"+
					" WHERE path='"+pFilePath+"' AND component='"+curComponentNAme+"' AND version='"+curVersionName+"';";
		
		log.debug("identifyCodeMatch SQL2 : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		
		IdentificationDBManager.addIdentifiedFile(
				projectName, 
				pFilePath, 
				TYPE_CODE_MATCH, 
				newComponentName, 
				newVersionName, 
				newLicenseName, 
				curComponentNAme, 
				curVersionName);
		IdentificationDBManager.updateIdentifyInfoForCommonPatternMatchFile(this.projectName, pFilePath);
	}
	
	public void identifyFile(IdentifyData identifyData, UIResponseObserver mObserver) {
		
		Collection<String> pFilePaths = identifyData.getFilePath();
		String curComponentName = identifyData.getCurrentComponentName();
		String curVersionName = identifyData.getCurrentVersionName();
		String newComponentName = identifyData.getNewComponentName(); 
		String newVersionName = identifyData.getNewVersionName(); 
		String newLicenseName = identifyData.getNewLicenseName();
		
		if(pFilePaths == null)
			return;
		
		if(curVersionName.equals("Unspecified")) curVersionName = "";
		
		for(String path:pFilePaths) {
			
			String displayedFullPath = getDisplayedFullPath(path);			
			mObserver.pushMessageWithHeader(" > filePath : " + displayedFullPath);
			identifyCodeMatch(
					path,
					curComponentName,
					curVersionName,
					newComponentName, 
					newVersionName, 
					newLicenseName);
		}
	}

	public ArrayList<String> getUpdateTarget(String projectName, String pFolderPath, String pTargetComponent, String pTargetVersion) {
		if(pTargetVersion == null) pTargetVersion = "";
		else if(pTargetVersion.equals("Unspecified")) pTargetVersion = "";
		ArrayList<String> identifyList = IdentificationDBManager.getCodeMatchIdentifyTargetFile(projectName, pFolderPath, pTargetComponent, pTargetVersion);
		return identifyList;
	}

	public boolean updateDB(String action, ArrayList<String> fileList, List<CodeMatchDiscovery> listCodeMatchDiscovery, String newComponentName, boolean isSelected_ThirdForthOption) {
		boolean remainPending = false;
		String componentID = "";
		String componentName = "";
		String versionID = "";
		String versionName = "";
		String filePath = "";
		String baseStatus = null;
		
		log.debug(" # CodeMatchDiscovery update DB");
		
		if(action.equals("identify")) {
			baseStatus = DiscoveryAPIWrapper.STATE_PENDING;
		} else {
			baseStatus = DiscoveryAPIWrapper.STATE_REJECT;
		}
		log.debug("CodeMatchDiscovery update DB START : "+new java.util.Date());
		PreparedStatement prep = IdentificationDBManager.getCodeMatchUpdatePreparedStatement(projectName);
		PreparedStatement prep2 = IdentificationDBManager.updateIdentifiedTablePreparedStatement(projectName);
		int psCnt = 0;
		for(CodeMatchDiscovery cmd:listCodeMatchDiscovery) {
			String status = DiscoveryAPIWrapper.translateIdentficationStatus(cmd.getIdentificationStatus());
			// 1. code match table update
			if (baseStatus.equals(status)) {
				filePath = cmd.getFilePath().substring(1);
				if(fileList != null) {
					if(!fileList.contains(filePath)) continue;
				}
				componentID = cmd.getMatchingComponentId();
				versionID = cmd.getMatchingVersionId();
				if(versionID != null) {
					ComponentVersion cv = ComponentAPIWrapper.getComponentVersionById(componentID,versionID);
					if(cv != null) {
						componentName = cv.getComponentName();
						versionName = cv.getVersionName();
					}
				} else {
					componentName = ComponentAPIWrapper.getGlobalComponentName(componentID);
					versionName = "";
				}
	            try {
	            	if(prep == null) {
	            		continue;
	            	}
	            	prep.setString(1, DiscoveryAPIWrapper.translateIdentficationStatus(cmd.getIdentificationStatus()));
	            	prep.setString(2, filePath);
	            	prep.setString(3, componentName);
	            	prep.setString(4, versionName);
	            	prep.addBatch();
					psCnt++;
					if(psCnt>10000) {
						IdentificationDBManager.executeUpdateStatement(prep);
						psCnt = 0;
					}
				} catch (SQLException e) {
					log.warn(e);
		        }
				remainPending = true;
			}
			
			// 2. identified table update
			if(isSelected_ThirdForthOption==true && action.equals("identify") && status.equals(DiscoveryAPIWrapper.STATE_IDENTIFIED)) {
				IdentificationDBManager.removeIdentifiedFile(projectName, cmd.getFilePath().substring(1), TYPE_CODE_MATCH, cmd.getMatchingFileLocation().getFilePath());
				try {
					prep2.setString(1, cmd.getFilePath().substring(1)); // path
					prep2.setString(2, TYPE_CODE_MATCH); // match type
					prep2.setString(3, newComponentName); // component name
					prep2.setString(4, ""); // version name
					prep2.setString(5, ""); // license name
					prep2.setString(6, cmd.getMatchingFileLocation().getFilePath()); // matched_file
					prep2.addBatch();
				} catch (Exception e) {
					log.warn(e);
				} 
				
			}
		}
		log.debug("Code Match Update START!! (It may delay...)");
    	if(psCnt > 0) {
			IdentificationDBManager.executeUpdateStatement(prep);
    	}
    	log.debug("Code Match Update END..");
		IdentificationDBManager.executeUpdateStatement(prep2);

     	if(prep != null){
			try {
				prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	
    	if(prep2 != null){
			try {
				prep2.close();
			} catch (SQLException e) {                                    
				e.printStackTrace();
			}
		}
    	
    	log.debug("CodeMatchDiscovery update DB END : "+new java.util.Date());
		return remainPending;
	}

	private void resetCodeMatch(
			String pFilePath, 
			String pComponent, 
			String pVersion, 
			String pMatchedFile, 
			int compositeType,
			String pCurrentComponentName) {
		if(pCurrentComponentName.equals(JPanCodeMatchMain.OPTION3_DECLARED_NOT_OPEN_SOURCE) ||
		   pCurrentComponentName.equals(JPanCodeMatchMain.OPTION4_DECLARED_DO_NOT_ALARM_AGAIN) ) {
			
			String resetStatusSQL_AllComponent = "UPDATE codematch_"+tablePostfix+" " +
								"SET status='"+AbstractMatchInfo.STATUS_PENDING+"' " +
								"WHERE path='"+pFilePath+"';"; // status°¡ rejected -> pending
			log.debug("resetCodeMatch ThirdForthOption : "+resetStatusSQL_AllComponent);
			IdentificationDBManager.update(resetStatusSQL_AllComponent);
			
			String deleteSQL = "DELETE FROM identified_file_"+tablePostfix+
							   " WHERE path='"+pFilePath+"' AND type='"+TYPE_CODE_MATCH+"'";
			IdentificationDBManager.update(deleteSQL);
			return;
		}
		
		ArrayList<String> matchedFileList = IdentificationDBManager.getMatchedFileFromCodeMatch(
				pFilePath, 
				pComponent, 
				pVersion, 
				pMatchedFile, 
				tablePostfix);
		
		pComponent = QueryUtil.makeValid(pComponent);
		for(int i=0; i<matchedFileList.size(); i++) {
			String matchedFile = matchedFileList.get(i);
			IdentificationDBManager.removeIdentifiedFile(projectName, pFilePath, TYPE_CODE_MATCH, matchedFile);
		}
		
		String updateSQL = "UPDATE codematch_"+tablePostfix+" " +
							"SET status='"+AbstractMatchInfo.STATUS_PENDING+"' " +
							"WHERE path='"+pFilePath+"' AND status='"+AbstractMatchInfo.STATUS_REJECT+"';"; // status°¡ rejected -> pending
		log.debug("resetCodeMatch SQL1 : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		
		updateSQL = "UPDATE codematch_"+tablePostfix+" " +
					"SET status='"+AbstractMatchInfo.STATUS_PENDING+"'" +
					" WHERE path='"+pFilePath+"' AND component='"+pComponent+"' AND version='"+pVersion+"';";
		log.debug("resetCodeMatch SQL2 : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		
		IdentificationDBManager.updateResetInfoForCommonPatternMatchFile(this.projectName, pFilePath);
	}

	public void resetFile(
			ArrayList<String> pFilePaths, 
			String pComponent, 
			String pVersion, 
			String pMatchedFile, 
			UIResponseObserver mObserver, 
			int compositeType, 
			String pFilePath,
			String pCurrentComponentName) {
		
		if(pVersion.equals("Unspecified")) pVersion = "";
		
		String displayedFullPath = getDisplayedFullPath(pFilePath);
		mObserver.pushMessageWithHeader(" > filePath : " + displayedFullPath);
		resetCodeMatch(pFilePath,pComponent,pVersion,pMatchedFile, compositeType, pCurrentComponentName);
		
	}
	
	class DiscoveryComponentInfo {
		private String filePath = "";
		private String componentID = "";
		private IdentificationStatus representState = null;
		private int maxCount = 0;
		private HashMap<String,IdentificationStatus> version_state = new HashMap<String,IdentificationStatus>();
		private HashMap<IdentificationStatus,Integer> state_cnt = new HashMap<IdentificationStatus,Integer>(3);
		
		public DiscoveryComponentInfo(String pFilePath, String pComponentID) {
			filePath = pFilePath;
			componentID = pComponentID;
		}
		
		public String getFilePath() {
			return filePath;
		}
		
		public String getComponentID() {
			return componentID;
		}

		public boolean isMultiState() {
			return state_cnt.size()>1?true:false;
		}
		
		public void addInfo(String version,IdentificationStatus state) {
			version_state.put(version, state);
			Integer cnt = state_cnt.get(state);
			if(cnt == null) {
				state_cnt.put(state, 1);
				if(representState == null) representState = state;
			} else {
				state_cnt.put(state, cnt+1);
				if(maxCount<cnt) {
					representState = state;
					maxCount = cnt;
				}
			}
		}
		
		public String getRepresentState() {
			return DiscoveryAPIWrapper.translateIdentficationStatus(representState);
		}
		
		public ArrayList<String> getRemainVersion() {
			ArrayList<String> remainVersion = new ArrayList<String>();
			Set<String> set = version_state.keySet();
			Iterator<String> keys = set.iterator();
			String version = null;
			while(keys.hasNext()) {
				version = (String)keys.next();
				if(version_state.get(version) != representState) {
					remainVersion.add(version);
				}
			}
			return remainVersion;
		}
		public String getState(String version) {
			return DiscoveryAPIWrapper.translateIdentficationStatus(version_state.get(version));
		}
	}
}
