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
package com.sec.ose.osi.ui.frm.main.identification.stringmatch;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEStringMatch
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UEStringMatch implements UIEntity{

	private static final long serialVersionUID = 1L;
	
	private String stringSearch;
	private String currentComponentName;
	private String currentVersionName;
	private String currentLicenseName;
	private String newComponentName;
	private String newVersionName;
	private String newLicenseName;

	public UEStringMatch(
			String stringSearch,
			String currentComponentName,
			String currentVersionName,
			String currentLicenseName,
			String newComponentName,
			String newVersionName,
			String newLicenseName) {

		this.stringSearch = stringSearch;
		this.currentComponentName = currentComponentName;
		this.currentVersionName = currentVersionName;
		this.currentLicenseName = currentLicenseName;
		this.newComponentName = newComponentName;
		this.newVersionName = newVersionName;
		this.newLicenseName = newLicenseName;

	}

	public String getStringSearch() {
		return stringSearch;
	}

	public String getCurrentComponentName() {
		return currentComponentName;
	}

	public String getCurrentVersionName() {
		return currentVersionName;
	}

	public String getCurrentLicenseName() {
		return currentLicenseName;
	}

	public String getNewComponentName() {
		return newComponentName;
	}

	public String getNewVersionName() {
		return newVersionName;
	}

	public String getNewLicenseName() {
		return newLicenseName;
	}

}
