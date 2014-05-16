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
package com.sec.ose.osi.ui.frm.main.manage;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEProtexProjectInfo
 * @author suhyun47.kim, hankido.lee, sjh.yoo
 * 
 */
public class UEProtexProjectInfo implements UIEntity {
	private static final long serialVersionUID = 1L;
	
	private String projectName;
	private String sourcePath;
	
	private boolean isAnalysisProject = false;
	private int AnalysisStatus = 0;

	public UEProtexProjectInfo(String projectName) {
		super();
		this.projectName = projectName;

	}

	public UEProtexProjectInfo(OSIProjectInfo projectInfo) {
		this.projectName = projectInfo.getProjectName();
		this.sourcePath = projectInfo.getSourcePath();
		
		if( projectInfo.getProjectAnalysisInfo()!=null) {
			this.isAnalysisProject = projectInfo.isAnalyzed();
			this.AnalysisStatus = projectInfo.getAnalysisStatus();
		}
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSourcePath() {
		return sourcePath;
	}
	
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public boolean isAnalysisProject() {
		return isAnalysisProject;
	}

	public int getAnalysisStatus() {
		return AnalysisStatus;
	}
}
