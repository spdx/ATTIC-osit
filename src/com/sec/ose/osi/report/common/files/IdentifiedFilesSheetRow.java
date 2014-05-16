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
package com.sec.ose.osi.report.common.files;

/**
 * IdentifiedFilesSheetRow
 * @author suhyun47.kim 
 * 
 */
public class IdentifiedFilesSheetRow {

	private String projectName;
	private String fullPath;
	private String url;
	private String codeMatchCnt;
	private String comment;
	private String componentName;
	private String componentVersion;
	private String license;
	private String discoveryType;
	private boolean isStringSearchOnly;
	
	protected IdentifiedFilesSheetRow(
			String projectName, 
			String fullPath, 
			String url, 
			String codeMatchCnt, 
			String comment, 
			String componentName,
			String componentVersion,
			String license,
			String discoveryType,
			boolean isStringSearchOnly
			) {
		
		super();
		this.projectName = projectName;
		this.fullPath = fullPath;
		this.url = url;
		this.codeMatchCnt = codeMatchCnt;
		this.comment = comment;
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.license = license;
		this.discoveryType = discoveryType;
		this.isStringSearchOnly = isStringSearchOnly;
	}
	
	public String getCodeMatchCnt() {
		return codeMatchCnt;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getUrl() {
		return url;
	}

	public String getComment() {
		return comment;
	}

	public String getComponentName() {
		return componentName;
	}
	
	public String getComponentVersion() {
		return componentVersion;
	}

	public String getLicense() {
		return license;
	}

	public String getDiscoveryType() {
		if(this.isStringSearchOnly == true)
			return "STRING_SEARCH_ONLY";
		
		if(discoveryType == null)
			return "N/A";
		
		if(discoveryType.indexOf("GENERIC_") >= 0)
			discoveryType = discoveryType.substring(8, discoveryType.length());
		
		return discoveryType;
	}
	
}
