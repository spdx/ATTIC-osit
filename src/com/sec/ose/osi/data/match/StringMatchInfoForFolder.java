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

/**
 * StringMatchInfoForFolder
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StringMatchInfoForFolder {
	
	protected String path;
	protected String stringSearch;
	protected String component;
	protected String version;
	protected String license;
	protected int files;
	protected int pendingHits;
	protected int identifiedHits ;
	
	protected StringMatchInfoForFolder(
			String pSearch, 
			String pComponent,
			String pVersion, 
			String pLicense, 
			int pFileCount, 
			int pPendingHits,
			int pIdentifiedHits) {
		
		this.stringSearch = pSearch;
		this.component = pComponent;
		this.version = pVersion;
		this.license = pLicense;
		this.files = pFileCount;
		this.pendingHits = pPendingHits;
		this.identifiedHits = pIdentifiedHits;
	}
	
	public StringMatchInfoForFolder(
			String path, 
			String stringSearch, 
			String component,
			String version, 
			String license, 
			int pendingHits, 
			int identifiedHits) {
		
		this.path = path;
		this.stringSearch = stringSearch;
		this.component = component;
		this.version = version;
		this.license = license;
		this.files = 1;
		this.pendingHits = pendingHits;
		this.identifiedHits = identifiedHits;
	}

	public int getFileCount() {
		return files;
	}

	public String getStringSearchName() {
		return stringSearch;
	}

	public String getComponentName() {
		return component;
	}

	public String getVersionName() {
		return version;
	}

	public String getLicenseName() {
		return license;
	}

	public int getPendingHits() {
		return pendingHits;
	}

	public int getIdentifiedHits() {
		return identifiedHits;
	}
	
	public void increaseFileCount() {
		this.files += 1;
	}
	
	public void plusPendingHits(int count) {
		this.pendingHits += count;
	}
	
	public void plusIdentifiedHits(int count) {
		this.identifiedHits += count;
	}

	public String getPath() {
		return path;
	}
	
}
