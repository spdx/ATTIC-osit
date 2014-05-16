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
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * BOMReportAPIWrapper
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class BOMReportAPIWrapper {
	private static Log log = LogFactory.getLog(BOMReportAPIWrapper.class);
	
	private BOMReportAPIWrapper() {}

    public static ArrayList<BOMEnt> getBillOfMeterialsFromProjectName(String projectName, UIResponseObserver observer) {
       	return getBillOfMeterialsFromProjectName(projectName, false, observer);

    }

    public static ArrayList<BOMEnt> getBillOfMeterialsFromProjectName(String projectName, boolean containDefaultComponent, UIResponseObserver observer) {
       	return BOMEntFactory.createBOMEntList(projectName, containDefaultComponent, observer);
    }
    
    public static ArrayList<IdentifiedFileEnt> getIdentifiedFilesFromBOMReport(String projectName, UIResponseObserver observer) {
    	
    	ArrayList<IdentifiedFileEnt> identifiedFiles = null;
   		identifiedFiles = markUpStringSearchOnlySymbols(IdentifiedFileEntFactory.createIdentifiedFileEntListFromReportSection(projectName,observer));
   		
   		log.debug("start Garbage collector in BOMReportAPIWrapper");   		
   		System.gc();
   		log.debug("finish Garbage collector in BOMReportAPIWrapper");
      		
    	return identifiedFiles;

    }

    public static ArrayList<IdentifiedFileEnt> getIdentifiedFilesFromCodeTree(String projectName, UIResponseObserver observer) {
    	log.debug("getIdentifiedFilesFromCodeTree");     	
    	ArrayList<IdentifiedFileEnt> identifiedFiles = null;
   		identifiedFiles = markUpStringSearchOnlySymbols(IdentifiedFileEntFactory.createIdentifiedFileEntListFromCodeTreeResult(projectName,observer));
    	return identifiedFiles;

    }

    public static Iterator<CodeMatchEnt> getCodeMatches(String projectName, UIResponseObserver observer) {
    	return CodeMatchEntFactory.createCodeMatchEntList(projectName, observer);
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

	private static ArrayList<IdentifiedFileEnt> markUpStringSearchOnlySymbols(ArrayList<IdentifiedFileEnt> fileEntList) {
		if(fileEntList == null || fileEntList.size() < 1)
			return null;
		
		TreeMap<String, Boolean> detectStringSearchOnly = new TreeMap<String, Boolean>();	// key = license#fullpath
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		
		while(itr.hasNext()) {
			IdentifiedFileEnt ent = itr.next();
			String filePath = ent.getFilePath();
			String license = ent.getFileLicense();
			String discoveryType = ent.getDiscoveryType();
			
			if(discoveryType == null)
				continue;
			
			String key = license+"#"+filePath;
			
			// 1. StringSearchOnly
			boolean isStringSearch;
			if(discoveryType.equals(IdentifiedFileEnt.STRING_SEARCH)) {
				isStringSearch = true;
			} else {
				isStringSearch = false;
			}
			
			// 2. detectStringSearchOnly
			if (detectStringSearchOnly.containsKey(key) == true) {
				boolean isPrevious = detectStringSearchOnly.get(key);
				detectStringSearchOnly.put(key, isStringSearch && isPrevious);
			} else {
				detectStringSearchOnly.put(key, isStringSearch);
			}
			
		}

		// 2. StringSearchOnly markup
		
		itr = fileEntList.iterator();
		while(itr.hasNext()) {
			IdentifiedFileEnt ent = itr.next();
			String filePath = ent.getFilePath();
			String license = ent.getFileLicense();
			String key = license+"#"+filePath;
			
		
			if(detectStringSearchOnly.get(key) == true) {
				ent.setStringSearchOnly(true);
			}else {
				ent.setStringSearchOnly(false);
			}
		}
		 
		return fileEntList;
	}
}
