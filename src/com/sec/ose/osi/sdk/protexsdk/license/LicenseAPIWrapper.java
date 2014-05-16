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
package com.sec.ose.osi.sdk.protexsdk.license;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.license.LicenseInfoColumn;
import com.blackducksoftware.sdk.protex.license.LicenseInfoPageFilter;
import com.blackducksoftware.sdk.protex.license.LicenseOriginType;
import com.blackducksoftware.sdk.protex.util.PageFilterFactory;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;

/**
 * LicenseAPIWrapper
 * @author sjh.yoo, hankido.lee
 * 
 */
public class LicenseAPIWrapper {
	private static Log log = LogFactory.getLog(LicenseAPIWrapper.class);
	
	private static ArrayList<String> allLicenseList = new ArrayList<String>();
	
	public static ArrayList<String> getAllLicenseList() {
		if(allLicenseList == null) allLicenseList = new ArrayList<String>();
		if(allLicenseList.size() == 0) allLicenseList = getLicenseList(null);
		return allLicenseList;
	}

	public static ArrayList<String> getLicenseList(String word) {
    	ArrayList<String> LicenseList = new ArrayList<String>();
		if (word == null) {
        	List<LicenseOriginType> typeFilter = new ArrayList<LicenseOriginType>();
            typeFilter.add(LicenseOriginType.STANDARD);
            typeFilter.add(LicenseOriginType.MODIFIED_STANDARD);
            typeFilter.add(LicenseOriginType.CUSTOM);
            LicenseInfoPageFilter pageFilter = PageFilterFactory.getAllRows(LicenseInfoColumn.LICENSE_NAME);
			try {
				List<LicenseInfo> licenses = ProtexSDKAPIManager.getLicenseAPI().getLicenses(typeFilter, pageFilter);
	            for(LicenseInfo licenseInfo:licenses) {
	            	licenseNameIDMap.put(licenseInfo.getName(), licenseInfo.getLicenseId());
	            	licenseIDNameMap.put(licenseInfo.getLicenseId(), licenseInfo.getName());
	            	LicenseList.add(licenseInfo.getName());
	            }
			} catch (SdkFault e) {
				e.printStackTrace();
			}
		} else {
			if(allLicenseList == null) getAllLicenseList();
			for(String licenseName:allLicenseList) {
				if(licenseName.startsWith(word)) {
					LicenseList.add(licenseName);
				}
			}
		}
    	return LicenseList;
	}
	
	private static HashMap<String, String> licenseNameIDMap = new HashMap<String, String>(); // license name : license id
	private static HashMap<String, String> licenseIDNameMap = new HashMap<String, String>(); // license name : license id
	public static String getLicenseID(String pLicenseName) {
		if(pLicenseName == null) return null;
		// 1. Memory
		if(licenseNameIDMap.containsKey(pLicenseName)) {
			return licenseNameIDMap.get(pLicenseName);
		}
		// 2. API
		String ret = null;
		try {
			if(ProtexSDKAPIManager.getLicenseAPI() != null) {
				GlobalLicense license  = ProtexSDKAPIManager.getLicenseAPI().getLicenseByName(pLicenseName);
				ret = license.getLicenseId();
			}
		} catch (SdkFault e) {
			log.error(e);
		}
		
		licenseNameIDMap.put(pLicenseName, ret);
		return ret; 
	}

	public static String getLicenseName(String pLicenseID) {
		if(pLicenseID == null) return null;
		// 1. Memory
		if(licenseIDNameMap.containsKey(pLicenseID)) {
			return licenseIDNameMap.get(pLicenseID);
		}
		// 2. API
		String ret = null;
		try {
			if(ProtexSDKAPIManager.getLicenseAPI() != null) {
				GlobalLicense license  = ProtexSDKAPIManager.getLicenseAPI().getLicenseById(pLicenseID);
				ret = license.getLicenseId();
			}
		} catch (SdkFault e) {
			log.warn(e);
		}
		return ret; 
	}
	
	public static byte[] getLicenseText(String pLicenseID) {
		try {
			if(ProtexSDKAPIManager.getLicenseAPI() != null) {
				GlobalLicense license  = ProtexSDKAPIManager.getLicenseAPI().getLicenseById(pLicenseID);
				return license.getText();
			}
		} catch (SdkFault e) {
			log.warn(e);
		}
		return null;
	}
}
