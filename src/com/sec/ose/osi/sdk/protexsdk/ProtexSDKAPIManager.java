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
package com.sec.ose.osi.sdk.protexsdk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_3;
import com.blackducksoftware.sdk.protex.comparison.FileComparisonApi;
import com.blackducksoftware.sdk.protex.component.custom.CustomComponentApi;
import com.blackducksoftware.sdk.protex.component.standard.StandardComponentApi;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersionApi;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.DiscoveryApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentApi;
import com.blackducksoftware.sdk.protex.report.ReportApi;
import com.blackducksoftware.sdk.protex.user.UserApi;
import com.sec.ose.osi.Version;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * ProtexSDKAPIManager
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class ProtexSDKAPIManager {
	private static Log log = LogFactory.getLog(ProtexSDKAPIManager.class);
	
	private static ProtexServerProxyV6_3 myProtexServer = null;
	private static ProjectApi projectAPI = null;
	private static UserApi userAPI = null;
    private static BomApi bomAPI = null;
    private static CodeTreeApi CodeTreeAPI = null;
    private static DiscoveryApi discoveryAPI = null;
    private static IdentificationApi identificationAPI = null;
    private static CustomComponentApi customComponentAPI = null;
    private static LocalComponentApi localComponentAPI = null;
    private static ComponentVersionApi componentVersionAPI = null;
    private static ReportApi reportAPI = null;
    private static StandardComponentApi standardComponentAPI = null;
    private static LicenseApi licenseAPI = null;
    private static FileComparisonApi fileComparisonAPI = null;

    private static Long connectionTimeout = 30 * 60 * 1000L;	// 30 min
       
    public static boolean connectToService(String userID,String password, String protexServerIP, UIResponseObserver observer){
    	myProtexServer =  new ProtexServerProxyV6_3(protexServerIP, userID, password,connectionTimeout);

 	    //connection test
 	    try {
 	    	myProtexServer.getUserApi().getCurrentUserHasServerFileAccess();
		} catch (SdkFault e) {
    		observer.setFailMessage(e.getMessage());
			log.error("Login Error SdkFault Message : " + e.getFaultInfo().getMessage());
			log.error("Login Error SdkFault ErrorCode : " + e.getFaultInfo().getErrorCode().toString());
			return false;
		} catch (RuntimeException e){
			//runtime exception error msg: Could not send Message.
			String pMessage = "The server IP provided was not valid or The server is not active. \n " +
							"You should check if the OSI SDK Version("+Version.getProtexSDKVersion()+") is matched to \n" +
							"the SDK Version of Protex Server("+protexServerIP+") \n" +
							"(You can check your protex Server SDK version after login via web browser.)";
			observer.setFailMessage(pMessage);
			log.error("Login Error RuntimeException Message : " + e);
			return false;
		}
    	return true;
    	
    }
    
	public static ProjectApi getProjectAPI() {
		if(myProtexServer == null) return null;
		if(projectAPI == null) projectAPI = myProtexServer.getProjectApi();
		return projectAPI;
	}    
	public static UserApi getUserAPI() {
		if(myProtexServer == null) return null;
		if(userAPI == null) userAPI = myProtexServer.getUserApi();
		return userAPI;
	}
	
	public static BomApi getBomAPI() {
		if(myProtexServer == null) return null;
		if(bomAPI == null) bomAPI = myProtexServer.getBomApi();
		return bomAPI;
	}

	public static CodeTreeApi getCodeTreeAPI() {
		if(myProtexServer == null) return null;
		if(CodeTreeAPI == null) CodeTreeAPI = myProtexServer.getCodeTreeApi();
		return CodeTreeAPI;
	}
	
	public static DiscoveryApi getDiscoveryAPI() {
		if(myProtexServer == null) return null;
		if(discoveryAPI == null) discoveryAPI = myProtexServer.getDiscoveryApi();
		return discoveryAPI;
	}

	public static CustomComponentApi getCustomComponentAPI() {
		if(myProtexServer == null) return null;
		if(customComponentAPI == null) customComponentAPI = myProtexServer.getCustomComponentApi();
		return customComponentAPI;
	}
	
	public static LocalComponentApi getLocalComponentAPI() {
		if(myProtexServer == null) return null;
		if(localComponentAPI == null) localComponentAPI = myProtexServer.getLocalComponentApi();
		return localComponentAPI;
	}

	public static ComponentVersionApi getComponentVersionAPI() {
		if(myProtexServer == null) return null;
		if(componentVersionAPI == null) componentVersionAPI = myProtexServer.getComponentVersionApi();
		return componentVersionAPI;
	}
	
	public static IdentificationApi getIdentificationAPI() {
		if(myProtexServer == null) return null;
		if(identificationAPI == null) identificationAPI = myProtexServer.getIdentificationApi();
		return identificationAPI;
	}
	
	public static ReportApi getReportAPI() {
		if(myProtexServer == null) return null;
		if(reportAPI == null) reportAPI = myProtexServer.getReportApi();
		return reportAPI;
	}

	public static StandardComponentApi getStandardComponentAPI() {
		if(myProtexServer == null) return null;
		if(standardComponentAPI == null) standardComponentAPI = myProtexServer.getStandardComponentApi();
		return standardComponentAPI;
	}
	
	public static LicenseApi getLicenseAPI() {
		if(myProtexServer == null) return null;
		if(licenseAPI == null) licenseAPI = myProtexServer.getLicenseApi();
		return licenseAPI;
	}
	
	public static FileComparisonApi getFileComparisonAPI() {
		if(myProtexServer == null) return null;
		if(fileComparisonAPI == null) fileComparisonAPI = myProtexServer.getFileComparisonApi();
		return fileComparisonAPI;
	}
	
	public static void discardAllUserData() {
		myProtexServer = null;
	    projectAPI = null;
	    bomAPI = null;
	    CodeTreeAPI = null;
	    discoveryAPI = null;
	    identificationAPI = null;
	    customComponentAPI = null;
	    localComponentAPI = null;
	    reportAPI = null;
	    standardComponentAPI = null;
	    licenseAPI = null;
	    fileComparisonAPI = null;
	}
	
    public static ProtexServerProxyV6_3 getMyProtexServer() {
		return myProtexServer;
	}
}