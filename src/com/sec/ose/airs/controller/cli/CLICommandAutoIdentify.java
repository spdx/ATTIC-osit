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
package com.sec.ose.airs.controller.cli;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

/**
 * 
 * 
 * @author ytaek.kim
 */
@Parameters(commandDescription="Auto Identify")
public class CLICommandAutoIdentify {
	@Parameter(names = "--project-id", required = false, description="project id to export")
	String projectId;
	@Parameter(names = "--project-name", required = false, description="project name to export")
	String projectName;
	@Parameter(names = "--spdx-files", description="spdx file list to import/auto-identify")
	List<String> fileList;
	
	@ParametersDelegate
	CLICommandLogin login = new CLICommandLogin();
	
	// Getter and Setter
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public List<String> getFileList() {
		return fileList;
	}
	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}
	public CLICommandLogin getLogin() {
		return login;
	}
	public void setLogin(CLICommandLogin login) {
		this.login = login;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
