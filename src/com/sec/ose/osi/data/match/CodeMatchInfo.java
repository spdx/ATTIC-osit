/**
 * Copyright(C) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.data.match;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CodeMatchInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class CodeMatchInfo extends AbstractMatchInfo {
	
	public static final int USAGE_SNIPPET = 0;
	public static final int USAGE_FILE = 1;
	
	public static HashMap<Integer, String> MATCED_INFO_USAGE_FILE = new HashMap<Integer, String>();
	static {
		MATCED_INFO_USAGE_FILE.put(USAGE_SNIPPET, "File");
		MATCED_INFO_USAGE_FILE.put(USAGE_FILE, "Snippet");
	}
	
	protected ArrayList<String> paths = null;
	protected String path = "";
	protected String componentName = "";
	protected String versionName = "";
	protected String licenseName = "";
	
	protected int usage = USAGE_SNIPPET;
	protected int percentage = 0;
	protected String matchedFilePath = "";
	
	protected CodeMatchInfo(
			ArrayList<String> pPaths, 
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage, 
			String pMatchedFile) {
		this.paths = pPaths;
		this.componentName = pComponent;
		this.versionName = pVersion;
		this.licenseName = pLicense;
		this.usage = pUsage;
		this.percentage = pPercentage;
		this.matchedFilePath = pMatchedFile;
		super.setStatus(pStatus);
	}
	
	protected CodeMatchInfo(
			String pPath, 
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage, 
			String pMatchedFile) {
		this.path = pPath;
		this.componentName = pComponent;
		this.versionName = pVersion;
		this.licenseName = pLicense;
		this.usage = pUsage;
		this.percentage = pPercentage;
		this.matchedFilePath = pMatchedFile;
		super.setStatus(pStatus);
	}

	protected CodeMatchInfo(
			String pProjectName,
			ArrayList<String> pPaths, 
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage,
			String pMatchedFile, 
			String identifiedComponentName, 
			String identifiedVersionName, 
			String identifiedLicenseName) {
		
		this(	pPaths, 
				pComponent, 
				pVersion, 
				pLicense, 
				pUsage, 
				pStatus, 
				pPercentage, 
				pMatchedFile);
		super.setStatus(pStatus);
		super.setIdentifiedInfo(
				identifiedComponentName, 
				identifiedLicenseName, 
				identifiedVersionName, 
				pProjectName );
	}
	
	protected CodeMatchInfo(
			String pProjectName,
			String pPath, 
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage,
			String pMatchedFile, 
			String identifiedComponentName, 
			String identifiedVersionName, 
			String identifiedLicenseName) {
		
		this(	pPath, 
				pComponent, 
				pVersion, 
				pLicense, 
				pUsage, 
				pStatus, 
				pPercentage, 
				pMatchedFile);
		super.setStatus(pStatus);
		super.setIdentifiedInfo(
				identifiedComponentName, 
				identifiedLicenseName, 
				identifiedVersionName, 
				pProjectName );
	}
	
	public void removeIdentifiedInfo(IdentifiedInfo idenInfo) {
	}

	public ArrayList<String> getPaths() {
		return paths;
	}
	
	public String getComponentName() {
		return componentName;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public int getUsage() {
		return usage;
	}

	public int getPercentage() {
		return percentage;
	}

	public String getMatchedFile() {
		return matchedFilePath;
	}
	
	public void identify() {
		super.setStatus(STATUS_REJECT);
	}

	public String getPath() {
		return path;
	}	
}
