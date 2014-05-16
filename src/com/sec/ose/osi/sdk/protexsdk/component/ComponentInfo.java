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

import java.io.Serializable;

/**
 * ComponentInfo
 * 
 * @author sjh.yoo
 * 
 */
public class ComponentInfo implements Serializable {

	private static final long serialVersionUID = -6488851621440597300L;
		
	private String componentId;
	private String componentName;
	private String versionId = "";
	private String versionName = "";
	private String licenseId = "unspecified";
	private String licenseName = "Unspecified";

	public ComponentInfo(String componentId, String componentName,
			String versionId, String versionName, String licenseId,
			String licenseName) {
		super();
		this.componentId = componentId;
		this.componentName = componentName;
		this.versionId = versionId;
		this.versionName = versionName;
		this.licenseId = licenseId;
		this.licenseName = licenseName;
	}
	public ComponentInfo(String componentId, String componentName,
			String versionId, String versionName) {
		super();
		this.componentId = componentId;
		this.componentName = componentName;
		this.versionId = versionId;
		this.versionName = versionName;
	}
	public ComponentInfo(String componentId, String componentName) {
		this.componentId = componentId;
		this.componentName = componentName;
	}
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}
	public String getLicenseName() {
		return licenseName;
	}
	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}
}
