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

import java.util.ArrayList;

/**
 * ProjectInfoForIdentifyReport
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class ProjectInfoForIdentifyReport {   
	
	private  String projectName;
	private  ArrayList<String> filePathListUpComponent;
	private  boolean allFileListUp;
	
	public ProjectInfoForIdentifyReport(
			String projectName,
			boolean allFileListUp,
			ArrayList<String> filePathListUpComponent) {
		
		this.allFileListUp = allFileListUp;
		this.projectName = projectName;
		this.filePathListUpComponent = filePathListUpComponent;
	}

	public boolean isAllFileListUp() {
		return allFileListUp;
	}
	
	public  String getProjectName() {
		return projectName;
	}
	public  void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public  ArrayList<String> getfilePathListUpComponent() {
		return filePathListUpComponent;
	}
	
}