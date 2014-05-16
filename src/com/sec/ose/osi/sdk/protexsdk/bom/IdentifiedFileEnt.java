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

import java.io.File;

/**
 * IdentifiedFileEnt
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public class IdentifiedFileEnt implements Comparable<IdentifiedFileEnt> {
	public final static String STRING_SEARCH = "String Search";
	public final static String CODE_MATCH = "Code Match";	
	
	private String componentName;
	private String componentVersion;
	private String filePath;
	private String fileComment;
	private String fileLicense;
	private String projectName;
	private String discoveryType;
	
	private boolean isStringSearchOnly;		// this value is setted by BOMReportAPIWrapper
	
	public String getFileLicense() {
		return fileLicense;
	}

	public String getProjectName() {
		return projectName;
	}

	protected IdentifiedFileEnt(String filePath, String componentName,  String componentVersion, String fileComment, String fileLicense, String projectName, String discoveryType) {
		super();
		this.filePath = filePath;
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.fileLicense = fileLicense;
		this.fileComment = fileComment;
		this.projectName = projectName;
		this.discoveryType = discoveryType;		
		
	}
	
	public IdentifiedFileEnt(String fullPath, String componentName, String componentVersion, String fileComment, String projectName, String discoveryType) {
		
		this(fullPath, componentName, componentVersion, fileComment, "", projectName, discoveryType);
	}

	public String getComponentName() {
		return componentName;
	}
	
	public String getComponentVersion() {
		return componentVersion;
	}
	
	public String getFileComment() {
		return fileComment;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getParentPath() {
		return (new File(filePath)).getParent();
	}
	
	public String toString() {
		
		String msg = "[identifiedFile] "+filePath+", "+componentName+", "+discoveryType;
		
		return msg;
	}

	public int compareTo(IdentifiedFileEnt arg0) {

		if(this.filePath == null || arg0 == null)
			return -1;
			
		String filePath = arg0.getFilePath();
		
		return this.filePath.compareTo(filePath);
	}

	public void setLicense(String componentLicense) {
		this.fileLicense = componentLicense;
	}

	public String getDiscoveryType() {
		
		return discoveryType;
	}

	public boolean isStringSearchOnly() {
		return isStringSearchOnly;
	}

	public void setStringSearchOnly(boolean isStringSearchOnly) {
		this.isStringSearchOnly = isStringSearchOnly;
	}
	
}
