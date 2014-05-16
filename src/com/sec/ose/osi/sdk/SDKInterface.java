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
package com.sec.ose.osi.sdk;

import java.util.ArrayList;
import java.util.Collection;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.report.common.ProjectInfoForIdentifyReport;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * SDKInterface
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public interface SDKInterface {
	public void userLogin(
			String username,
			String password, 
			String blackDuckServerIP,
			UIResponseObserver observer);

	public ArrayList<String> getProjectNames(UIResponseObserver oberver);
	 
	/**
	 * @param projectInfo
	 * @param sourceExcelFilename	// the file name to be base
	 *                                 if the parameter value is null
	 *                                 this method generates excel files from template defined by application
	 *                                 
	 * @param targetExcelFilename	// the file name to be generated.
	 * @param insertCodeMatches
	 * @param oberver
	 */
	public void generateIdentifyReport(
			Collection<ProjectInfoForIdentifyReport> projectInfo,  
	    	String sourceExcelFilename,
	    	String targeExcelFilename,
	    	boolean insertCodeMatches,
	    	String creatorName,
			String creatorEmail,
			String organizationName,
	    	UIResponseObserver oberver
	    	);
	
	public boolean isExistedProjectName(
			String projectName,
			UIResponseObserver observer);
	
	public void getBOMListFromProjectName(
			String projectName, 
			UIResponseObserver observer);

	public void getBOMListMapFromProjectNames(
			ArrayList<String> projectNames, 
			UIResponseObserver observer);

	public boolean refreshProjectNames();
	
	public OSIProjectInfo getProjectInfoByName(String ProjectName);
	
	public String getUserID();
	
	public String getProjectID(String projectName);

	public String cloneProject(
			String newProjectName, 
			String originalProjectName,
			UIResponseObserver observer);
	
	public void discardAllUserData();

	public long getLastAnalysisDate(String projectName);
	
	public String getSourceLocation(String projectName);
	
	public String getHostName(String projectName);

	public String createProject(
			String projectName, 
			String sourcePath,
			UIResponseObserver object);

}