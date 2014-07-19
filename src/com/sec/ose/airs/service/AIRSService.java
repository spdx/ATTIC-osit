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
package com.sec.ose.airs.service;

import java.io.PrintStream;
import java.util.List;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;

/**
 * 
 * 
 * @author ytaek.kim
 */
public interface AIRSService {
	
	public void init(String protexServerIP, String userID, String password) throws Exception;
	
	public boolean export(String projectId, String targetFilePath, String packageName, String packageFileName, String organizationName, String creatorName, String creatorEmail, PrintStream out) throws Exception;
	public boolean exportByProjectName(String projectName, String targetFilePath, String packageName, String packageFileName, String organizationName, String creatorName, String creatorEmail, PrintStream out) throws Exception;
	
	public AutoIdentifyResult autoIdentify(List<String> SPDXFileNameList, String targetProjectId, PrintStream out);
	public AutoIdentifyResult autoIdentifyByProjectName(List<String> SPDXFileNameList, String targetProjectName, PrintStream out);
	
	public boolean identifyUsingSPDX(String targetProjectId, SPDXFileDTO sourceSpdxFile, SPDXFileDTO targetSpdxFile, PrintStream out);
}
