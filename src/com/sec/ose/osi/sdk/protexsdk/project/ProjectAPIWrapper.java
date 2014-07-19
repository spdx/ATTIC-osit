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
package com.sec.ose.osi.sdk.protexsdk.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.ErrorCode;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.LicenseCategory;
import com.blackducksoftware.sdk.protex.project.AnalysisSourceLocation;
import com.blackducksoftware.sdk.protex.project.AnalysisSourceRepository;
import com.blackducksoftware.sdk.protex.project.CloneOption;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectRequest;
import com.blackducksoftware.sdk.protex.project.ProjectUpdateRequest;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.AnalysisInfo;
import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.policy.PolicyCheckResult;
import com.sec.ose.osi.util.policy.ProjectNamePolicy;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * ProjectAPIWrapper
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class ProjectAPIWrapper {
	private static Log log = LogFactory.getLog(ProjectAPIWrapper.class);
	
	private ProjectAPIWrapper(){}
	
	public static boolean checkConnectToServer() {
		if(ProtexSDKAPIManager.getProjectAPI() == null) {
			JOptionPane.showMessageDialog(
					null, 
					"Can't connect server to refresh project list", 
					"Connection Error", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public static boolean refresh() {
		if(!checkConnectToServer()) return false;

		OSIProjectInfoMgr.getInstance().reload(null);

		return true;
	} 
	
	public static boolean initialize(String userID, UIResponseObserver observer) {
		if(!checkConnectToServer()) return false;
		
		OSIProjectInfoMgr.getInstance().reload(observer);
		
		return true;
	}
	
	public static OSIProjectInfo loadAnalysisInfo(OSIProjectInfo xOSIProjectInfo) {
		
		try {
			Project project = ProtexSDKAPIManager.getProjectAPI().getProjectById(xOSIProjectInfo.getProjectID());
		
			Date clastAnalyzedDate = project.getLastAnalyzedDate();
			if(clastAnalyzedDate != null) {
				xOSIProjectInfo.setLastAnalyzedDate(clastAnalyzedDate.getTime());
				xOSIProjectInfo.setAnalyzed(true);
			} else {
				xOSIProjectInfo.setLastAnalyzedDate(0);
				xOSIProjectInfo.setAnalyzed(false);
			}

			AnalysisSourceLocation asl = project.getAnalysisSourceLocation();
			if(asl != null)  {
				xOSIProjectInfo.setHostName(asl.getHostname());
				xOSIProjectInfo.setSourcePath(asl.getSourcePath());
			}
			
		} catch (SdkFault e) {
			log.warn(e);
		}
		xOSIProjectInfo.setLoaded(true);

		return xOSIProjectInfo;
	}

    public static  String createProject(
    		String newProjectName,
    		String sourcePath,
    		UIResponseObserver observer) {
    	
    	AnalysisSourceLocation newAnalysisSourceLocation = new AnalysisSourceLocation();
    	newAnalysisSourceLocation.setHostname(System.getenv("COMPUTERNAME"));
    	newAnalysisSourceLocation.setSourcePath(sourcePath);
    	newAnalysisSourceLocation.setRepository(AnalysisSourceRepository.LOCAL_PROXY);
    	
    	return createProject(newProjectName, newAnalysisSourceLocation, observer);
    }
    
    private static  String createProject(
    		String newProjectName,
    		AnalysisSourceLocation newAnalysisSourceLocation,
    		UIResponseObserver observer) {
    	
    	if(observer == null) {
    		observer = new DefaultUIResponseObserver();
    	}
    	
    	if(isExistedProjectName(newProjectName) == true) {
    		observer.setFailMessage("\""+newProjectName+"\" is already existed.");
    		log.debug("\""+newProjectName+"\" is already existed.");
    		return null;
    	}
    	
    	
    	String projectID = null;
    	ProjectRequest pRequest = new ProjectRequest();
        
    	PolicyCheckResult p = ProjectNamePolicy.checkProjectName(newProjectName); 
    	if(p.getResult() != PolicyCheckResult.PROJECT_NAME_OK) {
    		observer.setFailMessage(p.getResultMsg());
    		return null;
    	}

    	final String DESCRIPTION = "This project is created by OSI - " 
    								+ DateUtil.getCurrentTime("[%1$tY/%1$tm/%1$te(%1$ta) %1$tl:%1$tM:%1$tS %1$tp]");
    	pRequest.setName(newProjectName);
    	pRequest.setDescription(DESCRIPTION);
    	if(newAnalysisSourceLocation != null) {
    		pRequest.setAnalysisSourceLocation(newAnalysisSourceLocation);
    	}
    	
    	try{
	    	projectID = ProtexSDKAPIManager.getProjectAPI().createProject(pRequest, LicenseCategory.PROPRIETARY);
		} catch (SdkFault e) {
			log.warn(e);
			ErrorCode errorCode = e.getFaultInfo().getErrorCode();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			if(errorCode == ErrorCode.DUPLICATE_PROJECT_NAME) {
				String[] button = {"OK"};
				JOptionPane.showOptionDialog( // block
						null, 
						"The project name \""+newProjectName+"\" is already created by other user.",
						"Duplicated project name",
						JOptionPane.YES_OPTION, 
						JOptionPane.ERROR_MESSAGE, 
						null, 
						button,
						"OK");
			}
			return null;
		}
		
		if(projectID == null)
			return null;
		
		setScanIgnorePattern(projectID, ProjectNamePolicy.IGNORED_PATTERN, observer);
		
		observer.pushMessage("[ok]\n");
    	return projectID;
    	
    }

	public static String cloneProject(
			String newProjectName,
			String originalProjectName, 
			UIResponseObserver observer) {
		
		
    	if(observer == null) {
    		observer = new DefaultUIResponseObserver();
    	}

    	observer.pushMessage("Clonning project [" + newProjectName+"] from "+originalProjectName+" ...");
    		    	
    	if(isExistedProjectName(newProjectName) == true) {
    		observer.setFailMessage("\""+newProjectName+"\" is alread existed.");
    		return null;
    	}    	
    	if(isExistedProjectName(originalProjectName) == false) {
    		observer.setFailMessage("\""+originalProjectName+"\" is not existed.");
    		return null;
    	}
    	
    	String originProjectID = getProjectID(originalProjectName);
    	String newProjectID = "";
    	
//    	Boolean includeAnalysisResults = Boolean.TRUE; // Copy the results of an analysis (code tree and discoveries)
//    	Boolean includeCompletedWork = Boolean.TRUE; // Copy the Identifications performed
//    	Boolean includeAssignedUsers = Boolean.TRUE; // assign all users to the new project as well
    	Boolean linkIdentifications = Boolean.FALSE; // link identifications so that new identifications in this cloned project are copied back to the original project
		
    	List<CloneOption> analysisAndWork = new ArrayList<CloneOption>();
        analysisAndWork.add(CloneOption.ANALYSIS_RESULTS);
        analysisAndWork.add(CloneOption.COMPLETED_WORK);
        
        List<String> resetAllFulfillments = new ArrayList<String>(0); //  Includes the fulfillment status of obligations for these obligation categories (list of category IDs)
        
    	try {
    		newProjectID = ProtexSDKAPIManager.getProjectAPI().cloneProject(
    				originProjectID, 
    				newProjectName, 
    				analysisAndWork, 
    				linkIdentifications, 
    				resetAllFulfillments);
    		updateProjectDescription(newProjectID);
		} catch (SdkFault e) {
			log.warn(e);
			observer.setFailMessage(e.getMessage());
			return null;
		}
		observer.pushMessage("[ok]\n");
    	
    	return newProjectID;
	}
	
	private static void updateProjectDescription(String newProjectID) {
    	try {
    		String DESCRIPTION = "This project is cloned by OSI - " 
    				+ DateUtil.getCurrentTime("[%1$tY/%1$tm/%1$te(%1$ta) %1$tl:%1$tM:%1$tS %1$tp]");
    		
    		ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest();
    		projectUpdateRequest.setProjectId(newProjectID);
    		projectUpdateRequest.setDescription(DESCRIPTION);
    		ProtexSDKAPIManager.getProjectAPI().updateProject(projectUpdateRequest);
		} catch (SdkFault e) {
			log.warn(e);
		}
		
	}
	
	public static ArrayList<String> getProjectNames() {
		ArrayList<String> projectNames = new ArrayList<String>();
		Collection<OSIProjectInfo> lpis = OSIProjectInfoMgr.getInstance().getAllProjects();
		
		if(lpis == null) return projectNames;
		
		for(OSIProjectInfo lpi:lpis) {
			projectNames.add(lpi.getProjectName());
		}
		
		if(projectNames.size() <= 0) return projectNames;
		
		Collections.sort(projectNames);
		return projectNames;
	}

    public static void setScanIgnorePattern( 
    		String projectID,
    		String[] ignoredPatterns,
    		UIResponseObserver observer
    		){
    	
    	if(ignoredPatterns == null || ignoredPatterns.length <= 0)
    		return;
    }
    
	public static boolean isExistedProjectName(String projectName) {

		if(projectName == null)
			return false;

		Collection<OSIProjectInfo> projectInfoList = OSIProjectInfoMgr.getInstance().getAllProjects();
		
		if(projectInfoList == null) return true;
		
		for(OSIProjectInfo lpi:projectInfoList) {
			if(projectName.toLowerCase().equals(lpi.getProjectName().toLowerCase())) return true;
		}

		return false;
	}

	public static OSIProjectInfo getProjectInfoByName(String projectName) {
		
		return OSIProjectInfoMgr.getInstance().getProjectInfo(projectName);
	}

	public static Date getLastAnalysisDate(String projectName) {
		if(projectName == null) return null;
		AnalysisInfo aInfo = null;
		Date analysisStartedDate = null;
		try {
			aInfo = ProtexSDKAPIManager.getDiscoveryAPI().getLastAnalysisInfo(getProjectID(projectName));
			analysisStartedDate = aInfo.getAnalysisStartedDate();
		} catch (SdkFault e) {
			log.warn(e);
		}
		return (analysisStartedDate != null) ? analysisStartedDate : null;		
	}

	public static String getSourceLocation(String projectName) {
		OSIProjectInfo p = getProjectInfoByName(projectName);
		return (p != null) ? p.getSourcePath() : null;
	}

	public static String getHostName(String projectName) {
		OSIProjectInfo p = getProjectInfoByName(projectName);
		return (p != null) ? p.getHostName() : null;
	}
	
	public static String getProjectID(String projectName) {
		OSIProjectInfo p = getProjectInfoByName(projectName);
		return (p != null) ? p.getProjectID() : null;
	}

	public static void analyzeProject(String projectId) {
        // Start Analysis
        try {
        	ProtexSDKAPIManager.getProjectAPI().startAnalysis(projectId, Boolean.FALSE);
        } catch (SdkFault e) {
            System.err.println("startAnalysis() failed: " + e.getMessage());
            System.exit(-1);
        } catch (SOAPFaultException e) {
            System.err.println("startAnalysis() failed: " + e.getMessage());
            System.exit(-1);
        }
	}
	
	public static void cancelAnalyzeProject(String projectId) {
        // Start Analysis
        try {
        	ProtexSDKAPIManager.getProjectAPI().cancelAnalysis(projectId);
        } catch (SdkFault e) {
            System.err.println("cancelAnalysis() failed: " + e.getMessage());
            System.exit(-1);
        } catch (SOAPFaultException e) {
            System.err.println("cancelAnalysis() failed: " + e.getMessage());
            System.exit(-1);
        }
	}
}
