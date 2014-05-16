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
package com.sec.ose.osi.util.tools;

/**
 * ProjectSplitInfo
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class ProjectSplitInfo {
	
	public String toString() {
		return projectName+"              <<"+numOfFiles+" , "+analyzeTargetPath+", ";
	}
	
	String projectName;
	String analyzeTargetPath;

	private int numOfFiles;

	public ProjectSplitInfo(
			String projectName, 
			String path,
			int numOfFiles) {
		super();
		this.projectName = projectName;
		this.analyzeTargetPath = path;
		this.numOfFiles=numOfFiles;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getAnalyzeTargetPath() {
		return analyzeTargetPath;
	}

	public int getNumOfFiles() {
		return numOfFiles;
	}
	
}
