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
package com.sec.ose.airs.domain.autoidentify;

import java.io.Serializable;

/**
 * 
 * 
 * @author sjh.yoo, ytaek.kim
 */
public class IdentificationInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// original file
	protected String filePath; 
	
	// matched file info
	protected String matchedFile;
	protected String component;
	protected String version;
	protected String license;
	protected String spdxLicense;
	
	// getter and setter
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getMatchedFile() {
		return matchedFile;
	}
	public void setMatchedFile(String matchedFile) {
		this.matchedFile = matchedFile;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getSpdxLicense() {
		return spdxLicense;
	}
	public void setSpdxLicense(String spdxLicense) {
		this.spdxLicense = spdxLicense;
	}
}
