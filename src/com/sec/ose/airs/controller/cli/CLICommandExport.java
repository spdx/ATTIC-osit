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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

/**
 * 
 * 
 * @author ytaek.kim
 */
@Parameters(commandDescription="- SPDX Export")
public class CLICommandExport {
	@Parameter(names = "--project-id", required = false, description="project id to export")
	String projectId;
	@Parameter(names = "--project-name", required = false, description="project name to export")
	String projectName;
	@Parameter(names = {"-o", "--export-file"}, description="export file name")
	String filePath;
	
	@Parameter(names = "--pkg-name", description="package name to be descripted in spdx file")
	String packageName = "package";
	@Parameter(names = "--pkg-file-name", description="package file name to be descripted in spdx file")
	String packageFileName = "package file";
	@Parameter(names = "--org-name", description="organization name to be descripted in spdx file")
	String organizationName = "organization";
	@Parameter(names = "--creator-name", description="creator name to be descripted in spdx file")
	String creatorName = "creator";
	@Parameter(names = "--creator-email", description="creator email to be descripted in spdx file")
	String creatorEmail = "creator-email";
	
	@ParametersDelegate
	CLICommandLogin login = new CLICommandLogin();
	
	
	// Getter and Setter
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPackageFileName() {
		return packageFileName;
	}
	public void setPackageFileName(String packageFileName) {
		this.packageFileName = packageFileName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getCreatorEmail() {
		return creatorEmail;
	}
	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
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
