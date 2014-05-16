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
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * AbstractDiscoveryController
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public abstract class AbstractDiscoveryController {
	private static Log log = LogFactory.getLog(AbstractDiscoveryController.class);
	
	protected String projectName = null;
	protected String tablePostfix = null;
	protected ArrayList<String> cachePendingFileList = null;
	protected ArrayList<String> cacheIdentifiedFileList = null;

	public static String TYPE_STRING_SEARCH = "1";
	public static String TYPE_CODE_MATCH = "2";
	public static String TYPE_PATTERN_MATCH = "3";
	
	public AbstractDiscoveryController() {}

	
	public AbstractDiscoveryController(String pProjectName) {
		this.projectName = pProjectName;
		this.tablePostfix = IdentificationDBManager.getProjectTablePostfix(projectName);
	}

	
	public void removeIdentificationArrayListFromCache(Collection<String> removePaths) {
		for(String removePath:removePaths) {
			log.debug("removePath : "+removePath);
			cachePendingFileList.remove(removePath);
		}
	}

	public void addIdentificationArrayListFromCache(ArrayList<String> addPaths) {
		for(String addPath:addPaths) {
			if(!isPath(addPath)) {
				cachePendingFileList.add(addPath);
			}
		}
	}
	
	public boolean isPath(String addPath) {
		for(int i=0; i<cachePendingFileList.size(); i++) {
			if(addPath.equals(cachePendingFileList.get(i))) {
				return true;
			}
		}
		return false;
	}

	public int getNumOfPendingFiles(){return 0;}
	
	protected ArrayList<String> getPendingFileList(int type) {
		cachePendingFileList = IdentificationDBManager.getPendingFileList(this.projectName, type);
		return cachePendingFileList;
	}
	
	protected ArrayList<String> getIdentifiedFileList(int type) {
		cacheIdentifiedFileList = IdentificationDBManager.getIdentifiedFileList(this.projectName, type);
		return cacheIdentifiedFileList;
	}

	protected int getNumOfPendingFiles(int type) {
		getPendingFileList(type);
		if(cachePendingFileList != null) {
			return cachePendingFileList.size();
		}
		return 0;
	}
	
	public abstract int getNumOfDiscoveryFiles();
	
	protected int getNumOfDiscoveryFiles(int type) {
		
		int pendingFileCount = 0;
		int identifiedFileCount = 0;
		int discoveryFileCount = 0;
		
		getPendingFileList(type);
		if(cachePendingFileList != null) {
			pendingFileCount = cachePendingFileList.size();
		}
		
		getIdentifiedFileList(type);
		if(cacheIdentifiedFileList != null) {
			identifiedFileCount = cacheIdentifiedFileList.size();
		}
		
		discoveryFileCount = pendingFileCount + identifiedFileCount;
		
		return discoveryFileCount;
	}
	
	public ArrayList<String> refreshCahce(int type) {
		cachePendingFileList.clear();
		return getPendingFileList(type);
	}
	
	public static int originPendingFileCount(String projectName) {
		
		HashSet<String> fileSet = new HashSet<String>();		
		UIResponseObserver observer = null;
		ArrayList<String> files = new ArrayList<String>();
		
		ProjectDiscoveryControllerMap.loadProjectDiscoveryController(projectName, observer);
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}

		return fileSet.size();
	}
	
	public static int curIdentifiedFileCount(String projectName) {
		HashSet<String> fileSet = new HashSet<String>();		
		UIResponseObserver observer = null;
		ArrayList<String> files = new ArrayList<String>();
		
		ProjectDiscoveryControllerMap.loadProjectDiscoveryController(projectName, observer);
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).getIdentifiedFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		return fileSet.size();
	}
	
	public static int curPendingFileCount(String projectName) {
		HashSet<String> fileSet = new HashSet<String>();		
		UIResponseObserver observer = null;
		ArrayList<String> files = new ArrayList<String>();
		
		ProjectDiscoveryControllerMap.loadProjectDiscoveryController(projectName, observer);

		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		
		files = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).getPendingFileList();
		for(String file:files) {
			fileSet.add(file);
		}
		

		return fileSet.size();
	}
	
	protected String getDisplayedFullPath(String pFilePath) {
		String displayedFullPath = "";
		String fullPathMessage = pFilePath;
		while(fullPathMessage.length()>75) {
			displayedFullPath += fullPathMessage.substring(0,75)+"\n        ";
			fullPathMessage = fullPathMessage.substring(75);
		}
		displayedFullPath += fullPathMessage;
		return displayedFullPath;
	}
	
	public abstract ArrayList<String> getIdentifiedFileList();
	
	public abstract ArrayList<String> getPendingFileList(); 
	
	public abstract void identifyFile(IdentifyData identifyData, UIResponseObserver mObserver);

	public ArrayList<String> identifyFolder(String pFolderPath, String pTargetComponent, String pTargetVersion, String pComponent, String pVersion, String pLicense, UIResponseObserver mObserver) {return null;}

	public void resetFile(ArrayList<String> pFilePaths, String targetComponentName, String targetVersionName, String matchedFile, UIResponseObserver mObserver, int compositeType, String pFilePath, String pCurrentComponentName) {}

	public ArrayList<String> resetFolder(String pFolderPath, String pStringSearch, String targetComponentName, String targetVersionName, UIResponseObserver mObserver, int compositeType) {return null;}

}
