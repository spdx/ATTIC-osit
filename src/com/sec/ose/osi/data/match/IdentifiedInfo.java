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
package com.sec.ose.osi.data.match;

/**
 * IdentifiedInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class IdentifiedInfo {
	
	private String componentName;
	private String licenseName;
	private String versionName;
	private String protexProjectName;
	
	public IdentifiedInfo(
							String componentName, 
							String licenseName,
							String versionName, 
							String protexProjectName ) {
		this.componentName = componentName;
		this.licenseName = licenseName;
		this.versionName = versionName;
		this.protexProjectName = protexProjectName;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getProtexProjectName() {
		return protexProjectName;
	}

	public void setProtexProjectName(String protexProjectName) {
		this.protexProjectName = protexProjectName;
	}
}
