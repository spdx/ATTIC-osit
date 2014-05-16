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
package com.sec.ose.osi.sdk.protexsdk.discovery.report;

/**
 * CodeMatchesPrecision
 * @author hankido.lee
 * 
 */
public class CodeMatchesPrecision {
	String table;
	int id;
	String file;
	int size;
	int fileLine;
	int totalLines;
	String component;
	String version;
	String license;
	String usage;
	String status;
	String percentage;
	String matchedFile;
	int matchedFileLine;
	String fileComment;
	String componentComment;

	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getFileLine() {
		return fileLine;
	}
	public void setFileLine(int fileLine) {
		this.fileLine = fileLine;
	}
	public int getTotalLines() {
		return totalLines;
	}
	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
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
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getMatchedFile() {
		return matchedFile;
	}
	public void setMatchedFile(String matchedFile) {
		this.matchedFile = matchedFile;
	}
	public int getMatchedFileLine() {
		return matchedFileLine;
	}
	public void setMatchedFileLine(int matchedFileLine) {
		this.matchedFileLine = matchedFileLine;
	}
	public String getFileComment() {
		return fileComment;
	}
	public void setFileComment(String fileComment) {
		this.fileComment = fileComment;
	}
	public String getComponentComment() {
		return componentComment;
	}
	public void setComponentComment(String componentComment) {
		this.componentComment = componentComment;
	}
}
