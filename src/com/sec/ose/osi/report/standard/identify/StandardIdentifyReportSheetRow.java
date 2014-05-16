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
package com.sec.ose.osi.report.standard.identify;

/**
 * StandardIdentifyReportSheetRow
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StandardIdentifyReportSheetRow {
	private String category;
	private String matchedFiles;
	private Integer matchedFileCounts;
	private String component;
	private String license;
	private String version;
	private String comment;

	protected StandardIdentifyReportSheetRow(
			String category,
			String matchedFiles, 
			Integer matchedFileCounts,
			String component, 
			String version, 
			String license,
			String comment) {

		this.category = category;
		this.matchedFiles = matchedFiles;
		this.matchedFileCounts = matchedFileCounts;
		this.component = component;
		this.version = version;
		this.license = license;
		this.comment = comment;
	}

	/**
	 * Gets the category value for this CategoryHolder.
	 * 
	 * @return category
	 */
	public String getCategory() {
		
		if(this.category == null)
			return "";
		
		return category.trim();
	}

	/**
	 * Gets the matchedFiles value for this CategoryHolder.
	 * 
	 * @return matchedFiles
	 */
	public String getMatchedFiles() {
		if(this.matchedFiles == null)
			return "";
		
		return matchedFiles.trim();
	}

	/**
	 * Gets the matchedFileCounts value for this CategoryHolder.
	 * 
	 * @return matchedFileCounts
	 */
	public Integer getMatchedFileCounts() {
		return matchedFileCounts;
	}

	/**
	 * Gets the component value for this CategoryHolder.
	 * 
	 * @return component
	 */
	public String getComponent() {
		
		if(this.component == null)
			return "";
		
		return component.trim();
	}

	/**
	 * Gets the license value for this CategoryHolder.
	 * 
	 * @return license
	 */
	public String getComponentLicense() {
		if( (license == null) || (license.length() == 0) )
			return "N/A";
		
		return license.trim();
	}

	/**
	 * Gets the version value for this CategoryHolder.
	 * 
	 * @return version
	 */
	public String getVersion() {
		if(version == null)
			return "";
		
		return version.trim();
	}

	public String getComment() {
		
		if(comment == null)
			return "";
		
		return comment.trim();
	}

}
