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
package com.sec.ose.airs.domain.autoidentify;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class ProtexIdentificationInfo extends IdentificationInfo {
	public static final String CODE_MATCH = "Code Match";
	public static final String STRING_SEARCH = "String Search";
	public static final String PATTERN_MATCH = "Other Supported Languages";
	
	private static final long serialVersionUID = -36332124711925868L;
	
	public static final String SPDX_IDENTIFIED_DELIMETER = "|||";
	private static final int SPDX_IDENTIFIED_INFO_NUM = 10;	// number of fields in IdentificationInfo
	
	String resolutionType;
	String discoveryType;
	String usage;
	String percentage;
	String search;
	
	String versionID;
	String licenseID;
	String componentID;
	
	public ProtexIdentificationInfo() {}

	public ProtexIdentificationInfo(String resolutionType, String discoveryType,
			String filePath, String component, String version, String license,
			String usage, String percentage, String matchedFile, String search) {
		super();
		this.resolutionType = resolutionType;
		this.discoveryType = discoveryType;
		this.filePath = filePath;
		this.component = component;
		this.version = version;
		this.license = license;
		this.usage = usage;
		this.percentage = percentage;
		this.matchedFile = matchedFile;
		this.search = search;
	}
	
	public ProtexIdentificationInfo(String info) {
		super();
		this.setInfoFromString(info);
	}
	
	public String toString() {
		return resolutionType + SPDX_IDENTIFIED_DELIMETER +
		discoveryType + SPDX_IDENTIFIED_DELIMETER +
		filePath + SPDX_IDENTIFIED_DELIMETER +
		component + SPDX_IDENTIFIED_DELIMETER +
		version + SPDX_IDENTIFIED_DELIMETER +
		license + SPDX_IDENTIFIED_DELIMETER +
		usage + SPDX_IDENTIFIED_DELIMETER +
		percentage + SPDX_IDENTIFIED_DELIMETER +
		matchedFile + SPDX_IDENTIFIED_DELIMETER +
		search
		;
	}
	
	public void setInfoFromString(String identifiedFile) {
		String[] identifiedFileInfo = identifiedFile.split("\\|\\|\\|",SPDX_IDENTIFIED_INFO_NUM);
		resolutionType = identifiedFileInfo[0];
		discoveryType = identifiedFileInfo[1];
		filePath = identifiedFileInfo[2];
		component = identifiedFileInfo[3];
		version = identifiedFileInfo[4];
		license = identifiedFileInfo[5];
		usage = identifiedFileInfo[6];
		percentage = identifiedFileInfo[7];
		matchedFile = identifiedFileInfo[8];
		search = identifiedFileInfo[9];
	}

	public String toKeyStringForComparison() {
		return 
			resolutionType + SPDX_IDENTIFIED_DELIMETER +
			discoveryType + SPDX_IDENTIFIED_DELIMETER +
			SPDX_IDENTIFIED_DELIMETER +			// key 
			component + SPDX_IDENTIFIED_DELIMETER +
			version + SPDX_IDENTIFIED_DELIMETER +
			license + SPDX_IDENTIFIED_DELIMETER +
			usage + SPDX_IDENTIFIED_DELIMETER +
			percentage + SPDX_IDENTIFIED_DELIMETER +
			matchedFile + SPDX_IDENTIFIED_DELIMETER +
			search
		;
	}
	
	public String getResolutionType() {
		return resolutionType;
	}
	public void setResolutionType(String resolutionType) {
		this.resolutionType = resolutionType;
	}
	public String getDiscoveryType() {
		return discoveryType;
	}
	public void setDiscoveryType(String discoveryType) {
		this.discoveryType = discoveryType;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}

	public String getVersionID() {
		return versionID;
	}
	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}
	public String getLicenseID() {
		return licenseID;
	}
	public void setLicenseID(String licenseID) {
		this.licenseID = licenseID;
	}
	public String getComponentID() {
		return componentID;
	}
	public void setComponentID(String componentID) {
		this.componentID = componentID;
	}
	
}