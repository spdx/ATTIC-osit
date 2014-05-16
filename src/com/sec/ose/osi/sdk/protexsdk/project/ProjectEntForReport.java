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
package com.sec.ose.osi.sdk.protexsdk.project;

/**
 * ProjectEntForReport
 * @author suhyun47.kim,hankido.lee, 
 * 
 */
public class ProjectEntForReport {
	
	private String projectID;
	private String projectName;
	
	private String scanDate;
	private String scanDuration;
	private int    originPendingFileNum;
	private int    currentPendingFileNum;
	private String originPendingRatio;
	private String currentPendingRatio;
	
	private int numOfTotalFiles;
	private int numOfSkippedFiles;

	private long bytes;
	
	private int numOfAlreadyIdentifiedFiles;

	private String createdBy;
	private String analysisProtexVersion;
	private String server;
	
	private String license;
	private String description;
	
	private String scanStarted;
	private String lastUpdated;
	private String analyzedFromHost;
	private String analyzedBy;
	
	public String getScanStarted() {
		return scanStarted;
	}

	public void setScanStarted(String scanStarted) {
		this.scanStarted = scanStarted;
	}
	
	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getAnalyzedFromHost() {
		return analyzedFromHost;
	}

	public void setAnalyzedFromHost(String analyzedFromHost) {
		this.analyzedFromHost = analyzedFromHost;
	}

	public String getAnalyzedBy() {
		return analyzedBy;
	}

	public void setAnalyzedBy(String analyzedBy) {
		this.analyzedBy = analyzedBy;
	}
	
	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getScanDate() {
		return scanDate;
	}

	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}

	public String getScanDuration() {
		return scanDuration;
	}

	public void setScanDuration(String scanDuration) {
		this.scanDuration = scanDuration;
	}

	public int getCurrentPendingFileNum() {
		return currentPendingFileNum;
	}

	public void setCurrentPendingFileNum(int currentPendingFileNum) {
		this.currentPendingFileNum = currentPendingFileNum;
	}

	public String getCurrentPendingRatio() {
		return currentPendingRatio;
	}

	public void setCurrentPendingRatio(String pendingRatio) {
		this.currentPendingRatio = pendingRatio;
	}

	public int getNumOfTotalFiles() {
		return numOfTotalFiles;
	}

	public void setNumOfTotalFiles(int numOfTotalFiles) {
		this.numOfTotalFiles = numOfTotalFiles;
	}

	public int getNumOfSkippedFiles() {
		return numOfSkippedFiles;
	}

	public void setNumOfSkippedFiles(int numOfSkippedFiles) {
		this.numOfSkippedFiles = numOfSkippedFiles;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public int getNumOfAlreadyIdentifiedFiles() {
		return numOfAlreadyIdentifiedFiles;
	}

	public void setNumOfAlreadyIdentifiedFiles(int numOfAlreadyIdentifiedFiles) {
		this.numOfAlreadyIdentifiedFiles = numOfAlreadyIdentifiedFiles;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getAnalysisProtexVersion() {
		return analysisProtexVersion;
	}

	public void setAnalysisProtexVersion(String analysisProtexVersion) {
		this.analysisProtexVersion = analysisProtexVersion;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
	public String getCurrentPendingInformation() {
		int curPendingRatio = (int)((double)currentPendingFileNum /
				(double)(currentPendingFileNum + numOfAlreadyIdentifiedFiles) * 100);
		
		if(curPendingRatio == 0 && currentPendingFileNum != 0) {
			curPendingRatio = 1;
		}
		
		return currentPendingFileNum + " (" + curPendingRatio + "%)";
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getOriginPendingFileNum() {
		return originPendingFileNum;
	}

	public void setOriginPendingFileNum(int originPendingFileNum) {
		this.originPendingFileNum = originPendingFileNum;
	}

	public String getOriginPendingRatio() {
		return originPendingRatio;
	}

	public void setOriginPendingRatio(String originPendingRatio) {
		this.originPendingRatio = originPendingRatio;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(projectName).append(",");
		buf.append(this.currentPendingFileNum).append(",");
		buf.append(this.numOfTotalFiles).append(",");
		buf.append(createdBy).append(","); 
		buf.append(server).append(",");
		buf.append(license).append(",");
		buf.append(bytes).append(",");
		
		return buf.toString();
	}

}
