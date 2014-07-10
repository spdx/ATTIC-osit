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
package com.sec.ose.osi.report.standard.data;

/**
 * BillOfMaterialsRow
 * @author sjh.yoo
 *
 */
public class BillOfMaterialsRow {
	private String category;
	private String matchedFiles;
	private int matchedFileCounts;
	private String component;
	private String license;
	private String comment;
	
	/**
	 * @param category
	 * @param matchedFiles
	 * @param matchedFileCounts
	 * @param component
	 * @param license
	 * @param comment
	 */
	public BillOfMaterialsRow(String category, String matchedFiles,
			int matchedFileCounts, String component, String license,
			String comment) {
		super();
		this.category = category;
		this.matchedFiles = matchedFiles;
		this.matchedFileCounts = matchedFileCounts;
		this.component = component;
		this.license = license;
		this.comment = comment;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		if(category == null) return "";
		return category.trim();
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the matchedFiles
	 */
	public String getMatchedFiles() {
		if(matchedFiles == null) return "";
		return matchedFiles.trim();
	}
	/**
	 * @param matchedFiles the matchedFiles to set
	 */
	public void setMatchedFiles(String matchedFiles) {
		this.matchedFiles = matchedFiles;
	}
	/**
	 * @return the matchedFileCounts
	 */
	public int getMatchedFileCounts() {
		return matchedFileCounts;
	}
	/**
	 * @param matchedFileCounts the matchedFileCounts to set
	 */
	public void setMatchedFileCounts(int matchedFileCounts) {
		this.matchedFileCounts = matchedFileCounts;
	}
	/**
	 * @return the component
	 */
	public String getComponent() {
		if(component == null) return "";
		return component.trim();
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
		if( (license == null) || (license.length() == 0) ) return "N/A";
		return license.trim();
	}
	/**
	 * @param license the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		if(comment == null) return "";		
		return comment.trim();
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
