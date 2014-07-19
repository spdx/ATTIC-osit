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
package com.sec.ose.osi.report.standard.data;

/**
 * IdentifiedFileEnt
 * @author sjh.yoo
 * 
 */
public class IdentifiedFilesRow {
	private String projectName;
	private String fullPath;
	private String component;
	private String license;
	private String discoveryType;
	private String url;
	private String codeMatchCnt;
	private String comment;
	
	/**
	 * @param projectName
	 * @param fullPath
	 * @param component
	 * @param license
	 * @param discoveryType
	 * @param url
	 * @param codeMatchCnt
	 * @param comment
	 */
	public IdentifiedFilesRow(String projectName, String fullPath,
			String component, String license, String discoveryType,
			String comment) {
		super();
		this.projectName = projectName;
		this.fullPath = fullPath;
		this.component = component;
		this.license = license;
		this.discoveryType = discoveryType;
		this.comment = comment;
	}
	
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the fullPath
	 */
	public String getFullPath() {
		return fullPath;
	}
	/**
	 * @param fullPath the fullPath to set
	 */
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	/**
	 * @return the component
	 */
	public String getComponent() {
		return component;
	}
	/**
	 * @param component the component to set
	 */
	public void setComponent(String component) {
		this.component = component;
	}
	/**
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}
	/**
	 * @param license the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}
	/**
	 * @return the discoveryType
	 */
	public String getDiscoveryType() {
		return discoveryType;
	}
	/**
	 * @param discoveryType the discoveryType to set
	 */
	public void setDiscoveryType(String discoveryType) {
		this.discoveryType = discoveryType;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the codeMatchCnt
	 */
	public String getCodeMatchCnt() {
		return codeMatchCnt;
	}
	/**
	 * @param codeMatchCnt the codeMatchCnt to set
	 */
	public void setCodeMatchCnt(String codeMatchCnt) {
		this.codeMatchCnt = codeMatchCnt;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
