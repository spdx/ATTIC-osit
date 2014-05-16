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
package com.sec.ose.osi.sdk.protexsdk.component;

import java.util.ArrayList;

/**
 * ComponentInfo
 * @author sjh.yoo
 * 
 */
public class ComponentManager {
	private ArrayList<ComponentInfo> componentInfoList = new ArrayList<ComponentInfo>();
	
	public void addComponent(ComponentInfo info) {
		for(ComponentInfo tmp:componentInfoList) {
			if(tmp.getComponentId().equals(info.getComponentId()) && tmp.getVersionId().equals(info.getVersionId())) {
				return;
			}
		}
		componentInfoList.add(info);
	}
	
	public ArrayList<ComponentInfo> getComponentList() {
		return componentInfoList;
	}
	
	public String getComponentNameFromId(String componentId) {
		for(ComponentInfo info : componentInfoList) {
			if(componentId.equals(info.getComponentId())) {
				return info.getComponentName();
			}
		}
		return null;
	}

	public String getComponentIdFromName(String componentName) {
		for(ComponentInfo info : componentInfoList) {
			if(componentName.equals(info.getComponentName())) {
				return info.getComponentId();
			}
		}
		return null;
	}
	
	public ArrayList<ComponentInfo> getComponentVersionIdFromName(String componentName, String versionName) {
		ArrayList<ComponentInfo> componentVersionList = new ArrayList<ComponentInfo>(1);
		if(versionName != null) versionName = "";
		for(ComponentInfo info : componentInfoList) {
			if(componentName.equals(info.getComponentName()) && versionName.equals(info.getVersionName())) {
				componentVersionList.add(info);
			}
		}
		return componentVersionList;
	}
	
	/*
	 * for UI update
	 * 
	 * sjh.yoo
	 */
	public ComponentInfo getComponentVersionNameFromId(String componentId, String versionId) {
		for(ComponentInfo info : componentInfoList) {
			if(componentId.equals(info.getComponentId()) && versionId.equals(info.getVersionId())) {
				return info;
			}
		}
		return null;
	}
}
