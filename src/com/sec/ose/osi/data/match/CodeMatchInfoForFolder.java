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

import com.sec.ose.osi.localdb.identification.TableCodeMatch;

/**
 * CodeMatchInfoForFolder
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class CodeMatchInfoForFolder {
	
	private String path;
	private String componentName;
	private String versionName;
	private String licenseName;
	private int usage;
	private int status;
	private int percentage;
	private String matched_file;
	private String comment;
	
	private int pendingSnippetCount;
	private int identifiedSnippetCount;
	private int declaredSnippetCount;

	private String IdentifiedComponentName = null;
	private String IdentifiedVersionName = null;
	private String IdentifiedLicenseName = null;
	
	public CodeMatchInfoForFolder(
			String pComponent, 
			String pVersion, 
			String pLicense,
			int pPendingSnippetCount,
			int pIdentifiedSnippetCount,
			int declaredSnippetCount) {
		
		this.componentName = pComponent;
		this.versionName = pVersion;
		this.licenseName = pLicense;
		this.pendingSnippetCount = pPendingSnippetCount;
		this.identifiedSnippetCount = pIdentifiedSnippetCount;
		this.declaredSnippetCount = declaredSnippetCount;
	}

	public CodeMatchInfoForFolder(
			String pComponent, 
			String pVersion, 
			String pLicense,
			int pPendingHits,
			int pIdentifiedHits,
			int pDeclaredSnippetCount,
			String identifiedComponent, 
			String identifiedVersion, 
			String identifiedLicense) {
		
		this(pComponent, pVersion, pLicense, pPendingHits, pIdentifiedHits, pDeclaredSnippetCount);
		IdentifiedComponentName = identifiedComponent;
		IdentifiedVersionName = identifiedVersion;
		IdentifiedLicenseName = identifiedLicense;
	}

	public CodeMatchInfoForFolder(
			String path, 
			String component, 
			String version,
			String license, 
			int usage, 
			int status, 
			int percentage,
			String matchedFile, 
			String comment) {
		this.path = path;
		this.componentName = component;
		this.versionName = version;
		this.licenseName = license;
		this.usage = usage;
		this.status = status;
		this.percentage = percentage;
		this.matched_file = matchedFile;
		this.comment = comment;
	}
	
	public CodeMatchInfoForFolder(
			String pPath,
			String pComponent, 
			String pVersion, 
			String pLicense,
			int pUsage, 
			int pStatus, 
			int pPercentage, 
			String pMatchedFile ) {
		
		this.path = pPath;
		this.componentName = pComponent;
		this.versionName = pVersion;
		this.licenseName = pLicense;
		this.usage = pUsage;
		this.status = pStatus;
		this.percentage = pPercentage;
		this.matched_file = pMatchedFile;
		switch(this.status) {
		case TableCodeMatch.CODE_MATCH_TABLE_STATUS_PENDING:
			this.pendingSnippetCount = 1;
			this.identifiedSnippetCount = 0;
			this.declaredSnippetCount = 0;
			break;

		case TableCodeMatch.CODE_MATCH_TABLE_STATUS_IDENTIFIED:
			this.pendingSnippetCount = 0;
			this.identifiedSnippetCount = 1;
			this.declaredSnippetCount = 0;
			break;
		case TableCodeMatch.CODE_MATCH_TABLE_STATUS_DECLARED:
			this.pendingSnippetCount = 0;
			this.identifiedSnippetCount = 0;
			this.declaredSnippetCount = 1;
			break;
		}
	}

	public String getPath() {
		return path;
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

	public int getStatus() {
		return status;
	}

	public int getPercentage() {
		return percentage;
	}

	public String getMatchedFile() {
		return matched_file;
	}

	public String getComment() {
		return comment;
	}

	public int getPendingSnippetCount() {
		return pendingSnippetCount;
	}

	public int getIdentifiedSnippetCount() {
		return identifiedSnippetCount;
	}

	public String getIdentifiedComponentName() {
		return IdentifiedComponentName;
	}

	public String getIdentifiedVersionName() {
		return IdentifiedVersionName;
	}

	public String getIdentifiedLicenseName() {
		return IdentifiedLicenseName;
	}
	
	public void increasePendingSnippetCount() {
		pendingSnippetCount++;
	}
	
	public void increaseIdentifiedSnippetCount() {
		identifiedSnippetCount++;
	}
	
	public int getDeclaredSnippetCount() {
		return declaredSnippetCount;
	}

	public void increaseDeclaredSnippetCount() {
		declaredSnippetCount++;
	}
}
