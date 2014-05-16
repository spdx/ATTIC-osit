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
import java.util.TreeMap;

/**
 * AbstractProjectInfoMap
 * @author suhyun47.kim, hankido.lee
 * 
 */
public abstract class AbstractProjectInfoMap {
	
	private TreeMap<String, OSIProjectInfo> mProjectsInfoMap = new TreeMap<String, OSIProjectInfo>();

	public void putProjectInfo(String projectName, OSIProjectInfo projectInfo) {
		mProjectsInfoMap.put(projectName, projectInfo);
	}
	
	public void clear() {
		mProjectsInfoMap.clear();
	}
	
	public OSIProjectInfo getProjectInfo(int index) {
		if(index < -1 || index >= this.size())
			return null;

		Object[] projectInfoList =  mProjectsInfoMap.values().toArray();
		return (OSIProjectInfo)projectInfoList[index];
	}
	
	public OSIProjectInfo getProjectInfo(String projectName) {
		if(projectName == null)
			return null;
		return mProjectsInfoMap.get(projectName);
	}
	
	public Collection<OSIProjectInfo> getProjects() {
		return mProjectsInfoMap.values();
	}
	
	public void removeProjectInfo(String projectName) {
		mProjectsInfoMap.remove(projectName);
	}
	
	public int size() {
		return mProjectsInfoMap.size();
	}
	
}
