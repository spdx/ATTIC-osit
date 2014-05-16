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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ManagedProjectInfoMap
 * @author suhyun47.kim, ytaek.kim
 * 
 */
public class ManagedProjectInfoMap extends AbstractProjectInfoMap {
	private static Log log = LogFactory.getLog(ManagedProjectInfoMap.class);

	private AllProjectInfoMap allProjectInfoMap;
	
	public ManagedProjectInfoMap(AllProjectInfoMap allProjectInfoMap) {
		this.allProjectInfoMap = allProjectInfoMap;
	}
	
	void reload() {

		log.debug("reload()");
		
		this.clear();
		
		for(OSIProjectInfo tempProjectInfo:allProjectInfoMap.getProjects()) {
			if(tempProjectInfo.isManaged() && !tempProjectInfo.isDeleteProject()) {
				this.putProjectInfo(tempProjectInfo.getProjectName(), tempProjectInfo);				
				log.debug("insert:"+tempProjectInfo.getProjectName());
			}
		}
	}
	
	public void removeProjectInfo(String projectName) {
		
		OSIProjectInfo xOSIProjectInfo = allProjectInfoMap.getProjectInfo(projectName);
		if(xOSIProjectInfo != null) {
			xOSIProjectInfo.setManaged(false);
			super.removeProjectInfo(projectName);
		}
	}
	
	public void putProjectInfo(String projectName, OSIProjectInfo projectInfo) {
		allProjectInfoMap.putProjectInfo(projectName, projectInfo);
		super.putProjectInfo(projectName, projectInfo);
	}
}
