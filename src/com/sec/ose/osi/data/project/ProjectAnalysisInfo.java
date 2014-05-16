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
 * ProjectAnalysisInfo
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class ProjectAnalysisInfo {
	
	public static final int STATUS_UNKNOWN = -1;
	public static final int STATUS_READY = 0;
	public static final int STATUS_PROCESSING = 1;
	public static final int STATUS_COMPLETE = 2;
	
	private int analysisStatus = STATUS_UNKNOWN;
	
	private Object oFileComp = null;
	private StatusIcon statusIcon = null;
	private boolean isLocationValid = true;

	private int totalFileCount = -1;
	
	private boolean isRegisteredOnServer = true;

	public ProjectAnalysisInfo() {
	}
	
	protected int getAnalysisStatus() {
		return analysisStatus;
	}

	protected Object getFileComp() {
		return oFileComp;
	}

	protected StatusIcon getStatusIcon() {
		return statusIcon;
	}
	
	protected int getTotalFileCount() {
		return totalFileCount;
	}
	
	protected boolean isLocationValid() {
		return isLocationValid;
	}
	
	protected boolean isServerProject() {
		return isRegisteredOnServer;
	}
	
	public void setAnalysisStatus(boolean isAlreadyAnalysis) {
		if(isAlreadyAnalysis) {
			setAnalysisStatus(STATUS_COMPLETE);
		} else {
			setAnalysisStatus(STATUS_READY);
		}
	}
	public void setAnalysisStatus(int pAnalysisStatus) {
		analysisStatus = pAnalysisStatus;
		if(statusIcon == null) statusIcon = new StatusIcon(pAnalysisStatus);
		statusIcon.setStatus(pAnalysisStatus);
	}
	
	public void setFileComp(Object o) {
		oFileComp = o;
	}
	
	public void setLocationValid(boolean b) {
		isLocationValid = b;
	}
	
	public void setTotalFileCount(int count) {
		totalFileCount = count;
	}
}
