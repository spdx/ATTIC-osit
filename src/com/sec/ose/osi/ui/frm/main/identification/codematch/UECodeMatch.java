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
package com.sec.ose.osi.ui.frm.main.identification.codematch;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UECodeMatch
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class UECodeMatch implements UIEntity{

	private static final long serialVersionUID = 1L;

	private String matchedFile;
	private String originComponentName;
	private String originVersionName;
	private String originLicenseName;
	private String currentComponentName;
	private String currentVersionName;
	private String currentLicenseName;
	private String newComponentName;
	private String newVersionName;
	private String newLicenseName;
	private int compositeType;
	private String comment;
	private String status;

	public UECodeMatch(
			String originComponentName,
			String originVersionName,
			String originLicenseName,
			String currentComponentName,
			String currentVersionName,
			String currentLicenseName,  
			String newComponentName,
			String newVersionName,
			String newLicenseName,
			String matchedFile, 
			int compositeType,
			String comment,
			String status) {
		super();
		this.originComponentName = originComponentName;
		this.originVersionName = originVersionName;
		this.originLicenseName = originLicenseName;
		this.currentComponentName = currentComponentName;
		this.currentVersionName = currentVersionName;
		this.currentLicenseName = currentLicenseName;
		this.newComponentName = newComponentName;
		this.newVersionName = newVersionName;
		this.newLicenseName = newLicenseName;
		this.matchedFile = matchedFile;
		this.compositeType = compositeType;
		this.comment = comment;
		this.status = status;
	}

	public String getOriginComponentName() {
		return originComponentName;
	}

	public String getOriginVersionName() {
		return originVersionName;
	}

	public String getOriginLicenseName() {
		return originLicenseName;
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

	public String getMatchedFile() {
		return matchedFile;
	}

	public int getCompositeType() {
		return compositeType;
	}

	public String getComment() {
		return comment;
	}

	public String getStatus() {
		return status;
	}
	
}
