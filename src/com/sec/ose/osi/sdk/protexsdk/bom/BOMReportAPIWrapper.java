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
package com.sec.ose.osi.sdk.protexsdk.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.Component;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationType;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.report.standard.data.IdentifiedFilesRow;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * BOMReportAPIWrapper
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class BOMReportAPIWrapper {
	private static Log log = LogFactory.getLog(BOMReportAPIWrapper.class);

	private static final String CODE_MATCH = "Code Match"; 
	
	private BOMReportAPIWrapper() {}

    public static ArrayList<BOMEnt> getBillOfMeterialsFromProjectName(String projectName, UIResponseObserver observer) {
       	return getBillOfMeterialsFromProjectName(projectName, false, observer);
    }

    public static ArrayList<BOMEnt> getBillOfMeterialsFromProjectName(String projectName, boolean containDefaultComponent, UIResponseObserver observer) {
       	return BOMEntFactory.createBOMEntList(projectName, containDefaultComponent, observer);
    }
    
    public static ArrayList<IdentifiedFilesRow> getIdentifiedFilesFromBOMReport(String projectName, UIResponseObserver observer) {

    	log.debug("createIdentifiedFilewRowList");
    	
		String msgHead = "Creating Identified Files Sheet: - "+projectName+"\n";
		observer.setMessageHeader(msgHead);
		
		// get Identified Files
		String pMessage = msgHead+" > Getting [Identified Files] information from server.\n";
		observer.pushMessage(pMessage);
    	ReportEntityList identifiedFilesEntList = ReportAPIWrapper.getIdentifiedFiles(projectName, observer, true);
    	if(identifiedFilesEntList == null) return null;
		if(identifiedFilesEntList.size() == 0) return null;
		
		// get Compare Code Matches
		pMessage = msgHead+" > Getting [Compare Code Matches] information from server.\n";
		observer.pushMessage(pMessage);
    	ReportEntityList compareCodeMatches = ReportAPIWrapper.getCompareCodeMatches(projectName, observer, true);
		if(compareCodeMatches == null) return null;
		if(compareCodeMatches.size() == 0) return null;

		log.debug("create IdentifiedFilesRow");
		HashSet<String> duplicateCheckSet = new HashSet<String>();
		ArrayList<IdentifiedFilesRow> IdentifiedFilesRowList = new ArrayList<IdentifiedFilesRow>(identifiedFilesEntList.size());
		for(ReportEntity entity:identifiedFilesEntList) {
			
			String filePath = entity.getValue(ReportInfo.IDENTIFIED_FILES.FILE_FOLDER_NAME);
			String componentName = entity.getValue(ReportInfo.IDENTIFIED_FILES.COMPONENT);
			String componentVersion = entity.getValue(ReportInfo.IDENTIFIED_FILES.VERSION);
			String fileComment = entity.getValue(ReportInfo.IDENTIFIED_FILES.COMMENT);
			String fileLicense = parseFileLicense(entity.getValue(ReportInfo.IDENTIFIED_FILES.LICENSE));
			String discoveryType = entity.getValue(ReportInfo.IDENTIFIED_FILES.DISCOVERY_TYPE);

			if(componentName.equals("") && componentVersion.equals("") && fileLicense.equals("")) {
				continue;
			}
			
			String duplicateCheck = filePath + componentName + fileLicense;
			if(duplicateCheckSet.contains(duplicateCheck) == true) {
				continue;
			}

			duplicateCheckSet.add(duplicateCheck);
			IdentifiedFilesRowList.add(new IdentifiedFilesRow(projectName, filePath, componentName, fileLicense, discoveryType, fileComment));
		}
		

		log.debug("update code match info");
		HashMap<String,CodeMatchEnt> codeMatchInfoMap = new HashMap<String,CodeMatchEnt>(compareCodeMatches.size());
		for(ReportEntity entity:compareCodeMatches) {
			String fullPath =  entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.FULL_PATH);
			String codeMatchCnt = entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.CODE_MATCH_COUNT);
			String url = entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.COMPARE_CODE_MATCHES_LINK);
			
			url = url.replaceFirst("127.0.0.1", LoginSessionEnt.getInstance().getProtexServerIP());
			
			codeMatchInfoMap.put(fullPath, new CodeMatchEnt(fullPath, codeMatchCnt, url));
		}
		for(IdentifiedFilesRow fileEnt:IdentifiedFilesRowList) {
			CodeMatchEnt codeMatchEnt = codeMatchInfoMap.get(fileEnt.getFullPath());
			if(CODE_MATCH.equals(fileEnt.getDiscoveryType())) {
				fileEnt.setUrl(codeMatchEnt.url);
				fileEnt.setCodeMatchCnt(codeMatchEnt.codeMatchCnt);
			}
		}

		String msg = IdentifiedFilesRowList.size() + " files are loaded.";
		observer.pushMessage(msg);
		log.debug(msg);

   		
   		log.debug("start Garbage collector in BOMReportAPIWrapper");   		
   		System.gc();
   		log.debug("finish Garbage collector in BOMReportAPIWrapper");
      		
    	return IdentifiedFilesRowList;

    }

	private static String parseFileLicense(String protexValue) {
		if(protexValue != null) {
			if (protexValue.contains("template")) {
				// noting to do
			} 
            else if (protexValue.contains("[")) {
				int startIndex = protexValue.indexOf("[");
				protexValue = protexValue.substring(0, startIndex).trim();
			}
        }
		return protexValue;
	}

	public static ArrayList<IdentifiedFilesRow> getIdentifiedFilesFromCodeTree(String projectName, UIResponseObserver observer) {
    	log.debug("getIdentifiedFilesFromCodeTree");
    	return createIdentifiedFileEntListFromCodeTreeResult(projectName,observer);

    }

	private static ArrayList<IdentifiedFilesRow> createIdentifiedFileEntListFromCodeTreeResult(
			String pProjectName,
			UIResponseObserver observer) {
    	ArrayList<IdentifiedFilesRow> identifiedFileList =  new ArrayList<IdentifiedFilesRow>();
    	
    	log.debug("createIdentifiedFileEntListFromCodeTreeResult");	
    	
        try {
        	CodeTreeWorker worker = new CodeTreeWorker(pProjectName, identifiedFileList, observer);
        	worker.treeWalk(ROOT);
        } catch (SdkFault e) {
	        System.err.println("getCodeTree() failed: " + e.getMessage());
	        log.warn("getCodeTree() failed: " + e.getMessage());
            return null;
        }

		if(identifiedFileList.size() == 0)
			return null;
			
		return identifiedFileList;
	}

	static class CodeTreeWorker {
	    private String mProjectID = null;
	    private String mProjectName = null;
	    private ArrayList<IdentifiedFilesRow> identifiedFileList;

	    private CodeTreeApi mCodeTreeAPI = null;
	    private ProjectApi mProjectAPI = null;
	    private BomApi mBomAPI = null;
	    private IdentificationApi mIdentificationAPI = null;
	    private UIResponseObserver observer = null;
	    
		public CodeTreeWorker(String pProjectName, ArrayList<IdentifiedFilesRow> pIdentifiedFileList, UIResponseObserver observer) {
			log.debug(" createed");

			mProjectName = pProjectName;
			mProjectID = ProjectAPIWrapper.getProjectID(pProjectName);
			identifiedFileList = pIdentifiedFileList;

	    	mProjectAPI = ProtexSDKAPIManager.getProjectAPI();
	    	mBomAPI = ProtexSDKAPIManager.getBomAPI();
	    	mCodeTreeAPI = ProtexSDKAPIManager.getCodeTreeAPI();
	    	mIdentificationAPI = ProtexSDKAPIManager.getIdentificationAPI();
	    	this.observer = observer;
		}
		
	    public void treeWalk(String parentPath) throws SdkFault {
	    	log.debug("treeWalk: "+parentPath);

	        PartialCodeTree thisLevel = mCodeTreeAPI.getCodeTree(mProjectID, parentPath, CodeTreeUtilities.DIRECT_CHILDREN, Boolean.FALSE);
	        // Deal with all the sub-folders first

	        for (CodeTreeNode node : thisLevel.getNodes()) {
	        	
	            if ((node.getNodeType() == CodeTreeNodeType.EXPANDED_ARCHIVE) || (node.getNodeType() == CodeTreeNodeType.FOLDER)) {
	            	treeWalk(constructPath(parentPath, node.getName()));
	            }
	        }
	        
	        doWork(thisLevel);
	    }
	
	    private void doWork(PartialCodeTree thisLevel) throws SdkFault {
	        // do the work here
	        if (thisLevel.getNodes().size() == 0) {
	            return;
	        }
	        PartialCodeTree fileNodes = new PartialCodeTree();
	        fileNodes.setParentPath(thisLevel.getParentPath());
	        for (CodeTreeNode node : thisLevel.getNodes()) {
	            if (node.getNodeType() == CodeTreeNodeType.FILE) {
	                fileNodes.getNodes().add(node);
	            }
	        }
	        if (fileNodes.getNodes().size() == 0) {
	            return;
	        }
	        
	        List<CodeTreeIdentificationInfo> idInfos = mIdentificationAPI.getEffectiveIdentifications(mProjectID,fileNodes);
	        for (CodeTreeIdentificationInfo idInfo : idInfos) {
	            String filePath = constructPath(thisLevel.getParentPath(), idInfo.getName());
	        	for (Identification id : idInfo.getIdentifications()) {
	        		String mComponentID = id.getIdentifiedComponentId();
	    	        Component mComponent = mProjectAPI.getComponentById(mProjectID, mComponentID);
	    	        if(mComponent == null) continue;
	    	        String mComponentName = mComponent.getName();
	    	        String fileComment = mCodeTreeAPI.getFileOrFolderComment(mProjectID, filePath);
	
	    	        String fileLicense = "";
	    	        LicenseInfo mLicenseInfo = id.getIdentifiedLicenseInfo();
	    	        if(mLicenseInfo != null) fileLicense = parseFileLicense(mLicenseInfo.getName());
	    	        
	    	        IdentificationType discoveryType = id.getType();
	    	        
	    	        String mIndetifyURL = mBomAPI.getIdentifyBomUrl(mProjectID,filePath);
	    	        log.debug("-- " + filePath + " : " + mIndetifyURL);
	
	    	        identifiedFileList.add(new IdentifiedFilesRow(filePath, mComponentName, fileComment, fileLicense, mProjectName, discoveryType.toString()));

					String msg = "    "+ identifiedFileList.size() + " files are loaded.";    	        
					observer.pushMessage(msg);
					log.debug(msg);
	        	}
	        }
	    }

	    private final String FS = "/";
	    private String constructPath(String parentPath, String nodeName) {
	        String p = ((parentPath.length() == 0) || (parentPath.endsWith(FS) || (nodeName.length() == 0)) ? parentPath
	                : parentPath + FS)
	                + nodeName;
	        return p;
	    }
	}

    private static final String ROOT = "/";
    public static ArrayList<String> getPendingFileList(
    		String projectName,
    		UIResponseObserver observer) {
    	ArrayList<String> pendingList = new ArrayList<String>();
    	log.debug(projectName);
    	String projectId = ProjectAPIWrapper.getProjectID(projectName);
    	PartialCodeTree root = null;
        try {
            root = ProtexSDKAPIManager.getCodeTreeAPI().getCodeTree(projectId, ROOT, 0, Boolean.TRUE);
        } catch (SdkFault e) {
        	log.warn("getCodeTree failed() " + e.getMessage());
        }
        // Check for valid return
        if (root == null) {
            System.err.println("getCodeTree returned Unexpected value  '" + projectId + "'");
            return null;
        }
        
        List<CodeMatchType> precisionOnly = new ArrayList<CodeMatchType>(1);
        precisionOnly.add(CodeMatchType.PRECISION);
        List<CodeMatchDiscovery> discoveries = null;
        try {
        	discoveries = ProtexSDKAPIManager.getDiscoveryAPI().getCodeMatchDiscoveries(projectId, root, precisionOnly);
        } catch (SdkFault e) {
        	log.warn("getCodeMatchDiscoveries() failed: " + e.getMessage());
        }
        // Check for valid return
        if (discoveries == null) {
            System.err.println("Invalid returne from getCodeMatchDiscoveries() '" + projectId + "'");
            return null;
        }
        
        if (discoveries.size() != 0) {
        	for (CodeMatchDiscovery discovery : discoveries) {
        		if ((discovery.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION)
                        && !pendingList.contains(discovery.getFilePath())) {
        			log.debug(discovery.getFilePath());
        			pendingList.add(discovery.getFilePath());
        		}
        	}
        }

    	return pendingList;
    }

}

class CodeMatchEnt {
	CodeMatchEnt(String fullPath, String codeMatchCnt, String url) {
		this.fullPath = fullPath;
		this.codeMatchCnt = codeMatchCnt;
		this.url = url;
	}
	String fullPath = null;
	String codeMatchCnt = null;
	String url = null;
}
