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
package com.sec.ose.osi.data.project;

import com.sec.ose.osi.ui.frm.main.manage.StatusIcon;

/**
 * OSIProjectInfo
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class OSIProjectInfo { 
	
	private static final long NEVER_ANALYZED_BEFORE = -1;
	
	// Basic Info from Server
	private String mProjectID = null;
	private String mProjectName = null;
	
	// Additional Info from Server (loaded by thread)
	private boolean isLoaded = false;
	private String mHostName = null;
	private String mSourcePath = null;
	private long mLastAnalyzedDate = NEVER_ANALYZED_BEFORE;

	private ProjectAnalysisInfo mProjectAnalysisInfo= new ProjectAnalysisInfo();
	
	// Specific Info for Refresh (only used for OSI, no use for Protex)
	private boolean isSourcePathChange = false;
	private boolean isDeletedProject = false;
	
	// Managed Info from OSI (only used for OSI, no use for Protex)
	private boolean managed = false;
	private boolean analyzed = false; 
	private boolean analyzeTargeted = false;
	
	public OSIProjectInfo(String pProjectID, String pProjectName) {		
		this.mProjectID = pProjectID;
		this.mProjectName = pProjectName;
	}
	
	public OSIProjectInfo(String projectID, String projectName, String pHostName, String pSourcePath) {
		this.mProjectID = projectID;
		this.mProjectName = projectName;
		this.mHostName = pHostName;
		this.mSourcePath = pSourcePath;
	}
	
	public boolean isAnalyzeTarget() {
		return analyzeTargeted;
	}

	public void setAnalyzeTarget(boolean analyzeTarget) {
		this.analyzeTargeted = analyzeTarget;
	}
	
	public void updateAnalysisSuccessInfo() {
		setLastAnalyzedDate(System.currentTimeMillis());
		mProjectAnalysisInfo.setAnalysisStatus(true);
		setAnalyzed(true);
	}

	public String getProjectID() {
		return mProjectID;
	}

	public void setProjectID(String mProjectID) {
		this.mProjectID = mProjectID;
	}

	public String getProjectName() {
		return mProjectName;
	}

	public String getHostName() {
		return mHostName;
	}

	public void setHostName(String mHostName) {
		this.mHostName = mHostName;
	}

	public String getSourcePath() {
		return mSourcePath;
	}

	public void setSourcePath(String mSourcePath) {
		this.mSourcePath = mSourcePath;
	}

	public long getLastAnalyzedDate() {
		return mLastAnalyzedDate;
	}

	public void setLastAnalyzedDate(long mLastAnalyzedDate) {
		this.mLastAnalyzedDate = mLastAnalyzedDate;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public boolean isAnalyzed() {
		return this.analyzed;
	}

	public void setAnalyzed(boolean isAnalysis) {
		this.analyzed = isAnalysis;
	}
	
	public void setProjectAnalysisInfo(boolean analyzed) {
		mProjectAnalysisInfo.setAnalysisStatus(analyzed);
	}

	public ProjectAnalysisInfo getProjectAnalysisInfo() {
		return mProjectAnalysisInfo;
	}
	
	public boolean isManaged() {
		return managed;
	}

	public void setManaged(boolean pManaged) {
			this.managed = pManaged;
			if(pManaged == false) {
				setAnalyzeTarget(false);
			}
	}
	
	public void setSourcePathChange(boolean isSourcePathChange) {
		this.isSourcePathChange = isSourcePathChange;
	}

	public boolean isSourcePathChange() {
		return isSourcePathChange;
	}

	public boolean isDeleteProject() {
		return isDeletedProject;
	}

	public void setDeleteProject(boolean isDeleteProject) {
		this.isDeletedProject = isDeleteProject;
	}
	
	public int getAnalysisStatus() {
		return mProjectAnalysisInfo.getAnalysisStatus();
	}
	
	public Object getFileComp() {
		return mProjectAnalysisInfo.getFileComp();
	}
	
	public StatusIcon getStatusIcon() {
		return mProjectAnalysisInfo.getStatusIcon();
	}
	
	public int getTotalFileCount() {
		return mProjectAnalysisInfo.getTotalFileCount();
	}
	
	public boolean isLocationValid() {
		return mProjectAnalysisInfo.isLocationValid();
	}
	
	public boolean isServerProject() {
		return mProjectAnalysisInfo.isServerProject();
	}
	
}
