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
package com.sec.ose.osi.data.project;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.ProjectInfo;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectInfoLoaderMgrThread;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * AllProjectInfoMap
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class AllProjectInfoMap extends AbstractProjectInfoMap{
	private static Log log = LogFactory.getLog(AllProjectInfoMap.class);
	
	public AllProjectInfoMap() {}
	
	public boolean reload(UIResponseObserver observer) {
		observer.pushMessage("Try to loading project lists....");
		log.debug("Reload project list from Protex Server.");
		observer.setMessageHeader("Loading project lists from Protex Server.\n  >");
		try {
			List<ProjectInfo> piList = ProtexSDKAPIManager.getProjectAPI().getProjectsByUser(LoginSessionEnt.getInstance().getUserID());
			for(ProjectInfo pi:piList) {
				String projectID = pi.getProjectId();
				String projectName = pi.getName();
				OSIProjectInfo tmpProjectInfo = this.getProjectInfo(projectName);
				if(tmpProjectInfo == null) {
					tmpProjectInfo = new OSIProjectInfo(projectID, projectName);
					this.putProjectInfo(projectName, tmpProjectInfo);
					observer.pushMessageWithHeader(projectName);
				}
			}
		} catch (SdkFault e) {
			log.warn("getProjectsByUser() failed: " + e.getMessage());
			return false;
		}
		
		ProjectInfoLoaderMgrThread pir = new ProjectInfoLoaderMgrThread(this.getProjects());
		pir.start();
		return true;
	}
}
