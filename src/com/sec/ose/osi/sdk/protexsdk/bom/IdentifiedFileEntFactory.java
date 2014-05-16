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
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationType;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * IdentifiedFileEntFactory
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class IdentifiedFileEntFactory {
	private static Log log = LogFactory.getLog(IdentifiedFileEntFactory.class);
	
    public static ArrayList<IdentifiedFileEnt> createIdentifiedFileEntListFromReportSection(
			String projectName, 
			UIResponseObserver observer) {
    	
    	log.debug("createIdentifiedFileEntListFromReportSection");

    	ReportEntityList identifiedFilesEntList = ReportAPIWrapper.getIdentifiedFiles(projectName, observer, true);
    	ArrayList<IdentifiedFileEnt> identifiedFileList =  new ArrayList<IdentifiedFileEnt>();
    	
    	if(identifiedFilesEntList == null) return null;
		if(identifiedFilesEntList.size() == 0) return null;
		
		HashSet<String> duplicateCheckSet = new HashSet<String>();
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
			IdentifiedFileEnt fileEnt = new IdentifiedFileEnt(filePath, componentName, componentVersion, fileComment, fileLicense, projectName, discoveryType);
			identifiedFileList.add(fileEnt);
						
			String msg = "    "+ identifiedFileList.size() + " files are loaded.";    	        
			observer.pushMessage(msg);
		}
		
		return identifiedFileList;
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

    private static final String ROOT = "/";
	public static ArrayList<IdentifiedFileEnt> createIdentifiedFileEntListFromCodeTreeResult(
			String pProjectName,
			UIResponseObserver observer) {
    	ArrayList<IdentifiedFileEnt> identifiedFileList =  new ArrayList<IdentifiedFileEnt>();
    	
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
	    private ArrayList<IdentifiedFileEnt> identifiedFileList;

	    private CodeTreeApi mCodeTreeAPI = null;
	    private ProjectApi mProjectAPI = null;
	    private BomApi mBomAPI = null;
	    private IdentificationApi mIdentificationAPI = null;
	    private UIResponseObserver observer = null;
	    
		public CodeTreeWorker(String pProjectName, ArrayList<IdentifiedFileEnt> pIdentifiedFileList, UIResponseObserver observer) {
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
	
	    	        identifiedFileList.add(new IdentifiedFileEnt(filePath, mComponentName, fileComment, fileLicense, mProjectName, discoveryType.toString()));

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
}
