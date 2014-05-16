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

import java.util.Collection;

import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * OSIProjectInfoMgr
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class OSIProjectInfoMgr {
	
	private static final UIResponseObserver DEFAULT_UNREPORTED_OBSERVER = new DefaultUIResponseObserver();
	private static OSIProjectInfoMgr singleInstance = new OSIProjectInfoMgr();
	
	public static OSIProjectInfoMgr getInstance() {
		return singleInstance;
	}
	
	private AllProjectInfoMap allProjectInfoMap;
	private ManagedProjectInfoMap managedProjectInfoMap;
	
	private  OSIProjectInfoMgr() {
		allProjectInfoMap = new AllProjectInfoMap();
		managedProjectInfoMap = new ManagedProjectInfoMap(allProjectInfoMap);
	}
	
	public AllProjectInfoMap getAllProjectInfo() {
		return allProjectInfoMap;
	}
	
	public ManagedProjectInfoMap getManagedProjectInfo() {
		return managedProjectInfoMap;
	}
	
	public void rebuildManagedProjectInfo() {
		managedProjectInfoMap.reload();
	}

	public void reload(UIResponseObserver observer) {
		if(observer == null)
			observer = DEFAULT_UNREPORTED_OBSERVER;
		allProjectInfoMap.reload(observer);	
		managedProjectInfoMap.reload();
		
	}

	public Collection<OSIProjectInfo> getAllProjects() {
		return allProjectInfoMap.getProjects();
	}

	public OSIProjectInfo getProjectInfo(String projectName) {
		return allProjectInfoMap.getProjectInfo(projectName);
		
	}

	public void clear() {
		allProjectInfoMap.clear();
		managedProjectInfoMap.clear();
	}

	public void removeProject(String projectName) {
		allProjectInfoMap.removeProjectInfo(projectName);
		
	}
}
