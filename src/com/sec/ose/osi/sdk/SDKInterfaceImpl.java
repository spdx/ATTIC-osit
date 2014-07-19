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
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.report.ReportGenerator;
import com.sec.ose.osi.report.standard.data.ProjectInfoForIdentifyReport;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.authentication.AuthenticationAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * SDKInterfaceImpl
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class SDKInterfaceImpl implements SDKInterface{
	private static Log log = LogFactory.getLog(SDKInterfaceImpl.class);
	
	private volatile static SDKInterfaceImpl instance = null;
	public static SDKInterfaceImpl getInstance() {
		if(instance == null) {
			synchronized(SDKInterfaceImpl.class) {
				if(instance == null) {
					instance = new SDKInterfaceImpl();
				}
			}
		}
		return instance;
	}
	
	private SDKInterfaceImpl(){}
	
    public void userLogin(String userID,String password, String protexServerIP, UIResponseObserver observer){
    	
    	log.debug("userLogin()");
    	
    	if(observer == null)
    		observer = new DefaultUIResponseObserver();
    	
    	observer.pushMessage("Connecting to Protex server: "+protexServerIP);
        observer.pushMessage("Try log in \"" + userID + "\" \nto \"" + protexServerIP +"\"");
        
    	boolean result = AuthenticationAPIWrapper.login(userID, password, protexServerIP, observer);
    	
    	if(result == false) {
    		log.error("## Login Failed!!");
    		return;
    	} 
    	
    	log.debug("## Login Success!!");
    	observer.pushMessage("Successfully logged in \"" + userID + "\" \nto \"" + protexServerIP +"\"");
		observer.setSuccessMessage("Login Success");
		        
        // Project List Loading
        ProjectAPIWrapper.initialize(userID, observer);
    }
    
    public void generateIdentifyReport( 
    		ArrayList<ProjectInfoForIdentifyReport> projectsInfo, 
    		String sourceExcelFilename,
	    	String targeExcelFilename,
	    	boolean insertCodeMatches,
	    	String creatorName,
			String creatorEmail,
			String organizationName,
	    	UIResponseObserver observer)  {   	
    	
    	ReportGenerator rg = new ReportGenerator();
    	
    	rg.generateIdentifyReport(
					projectsInfo, 
					sourceExcelFilename, 
					targeExcelFilename, 
					creatorName,
					creatorEmail,
					organizationName,
					observer );
     }

    public void generateSPDXReport(ArrayList<String> projectNameList, ArrayList<String> targetFilePathList, SpdxReportConfiguration configuration, UIResponseObserver observer) {
    	ReportAPIWrapper.generateSPDXDocument(projectNameList, targetFilePathList, configuration, observer);
    }
    
	public ArrayList<String> getProjectNames(UIResponseObserver observer){
		
		log.debug("getProjectNames()");
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		observer.pushMessage("Get project list");
		
		ArrayList<String> projectNames = ProjectAPIWrapper.getProjectNames();
		if(projectNames == null || projectNames.size() == 0) {
			observer.setFailMessage("Loading the projects list fail\n");
			return null;
		}

		observer.setSuccessMessage("Loading the projects list successful\n");
		
		return projectNames;

	}
	
	public String getUserID() {
		
		log.debug("getUserID()");
		
		String userID = LoginSessionEnt.getInstance().getUserID();
		if(userID != null) return userID.substring(0, userID.indexOf('@'));
		return null;
	}


	public String getProjectID(String projectName) {
		return ProjectAPIWrapper.getProjectID(projectName);
	}
	
	
	public boolean isExistedProjectName(String projectName ,UIResponseObserver observer){
		
		log.debug("isExistedProjectName()");
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
        boolean result = ProjectAPIWrapper.isExistedProjectName(projectName);
        

        if(result == true) {
			return true;
        }
	
		return false;
	}

	public void getBOMListFromProjectName(
			String projectName, 
			UIResponseObserver observer) {

		observer.pushMessage("Get bill of materials for project: "+projectName);
		ArrayList<BOMEnt> boms = BOMReportAPIWrapper.getBillOfMeterialsFromProjectName(projectName, observer);
		
		observer.setReturnValue(boms);
       	observer.setSuccessMessage("ok");
		
	}
	
	public void getBOMListMapFromProjectNames(ArrayList<String> projectNames, UIResponseObserver observer) {
		
		HashMap<String, ArrayList<BOMEnt>> bomListMap = new HashMap<String, ArrayList<BOMEnt>>();
		
		for(int i=0; i<projectNames.size(); i++) {
			String projectName = projectNames.get(i);
			observer.pushMessage("["+(i+1)+"/"+projectNames.size()+"] Get bill of materials for project: \n  > "+projectName);
			observer.setMessageHeader("["+(i+1)+"/"+projectNames.size()+"] Get bill of materials for project: \n  > "+projectName);
			
			ArrayList<BOMEnt> boms = BOMReportAPIWrapper.getBillOfMeterialsFromProjectName(projectName, observer);
			bomListMap.put(projectName, boms);
		}
		
		observer.setReturnValue(bomListMap);
       	observer.setSuccessMessage("ok");
	}

	public boolean refreshProjectNames() {

		return ProjectAPIWrapper.refresh();
	}

	public OSIProjectInfo getProjectInfoByName(String projectName) {

		return ProjectAPIWrapper.getProjectInfoByName(projectName);
	}

	public String cloneProject(String newProjectName, String originalProjectName,
			UIResponseObserver observer) {
		
		return ProjectAPIWrapper.cloneProject(newProjectName, originalProjectName, observer);
	}

	public String createProject(
			String newProjectName,
			String sourcePath,
			UIResponseObserver observer) {
		return ProjectAPIWrapper.createProject(newProjectName, sourcePath, observer);
	}
	
	public void discardAllUserData() {
		ProtexSDKAPIManager.discardAllUserData();
	}

	public long getLastAnalysisDate(String projectName) {
		Date gc = ProjectAPIWrapper.getLastAnalysisDate(projectName);
		return (gc != null) ? gc.getTime() : 0;
	}

	public String getHostName(String projectName) {
		return ProjectAPIWrapper.getHostName(projectName);
	}

	public String getSourceLocation(String projectName) {
		return ProjectAPIWrapper.getSourceLocation(projectName);
	}
}