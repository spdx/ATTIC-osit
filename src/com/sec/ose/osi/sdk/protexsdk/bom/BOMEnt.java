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
package com.sec.ose.osi.sdk.protexsdk.bom;

import java.util.StringTokenizer;

import com.sec.ose.osi.util.tools.Tools;

/**
 * BOMEnt
 * @author suhyun47.kim
 * 
 */
public class BOMEnt {

	private String mProjectName;
	private String mComponentName;
	private String mComponentID;
	private String mComponentVersion;
	private String mLicense;
	private String mHasConflictWithDeclaredLicense;
	private String mComment;
	private int mCount;
	
	protected BOMEnt(String projectName, String componentName, String componentID, String componentVersion, String license, String hasConflictWithDeclaredLicense, String comment, String countString) {
		super();
		mProjectName = projectName;
		mComponentName = componentName;
		mComponentID = componentID;
		mComponentVersion = componentVersion;
		mLicense = license;
		mHasConflictWithDeclaredLicense = hasConflictWithDeclaredLicense;
		mComment = comment;
		mCount = Tools.transStringToInteger(countString);

		if(componentID == null || componentID.equals("null"))
			componentID = componentName;
	}

	public String toString() {
	
		StringBuffer buf = new StringBuffer();

		buf.append(mComponentName).append(",");
		buf.append(mComponentID).append(",");
		buf.append(mComponentVersion).append(",");
		buf.append(mLicense).append(",");
		buf.append(mCount).append(",");
		buf.append(mProjectName).append(",");
		
		StringTokenizer st = new StringTokenizer (mProjectName, "_");
		while(st.hasMoreTokens()) {
			buf.append(st.nextToken()).append(",");	
		}
		
		return buf.toString();
		
	}
	
	public String getComment() {
		return mComment;
	}

	public String getComponentID() {
		return mComponentID;
	}

	public int getCount() {
		return mCount;
	}

	public String getHasConflictWithDeclaredLicense() {
		return mHasConflictWithDeclaredLicense;
	}

	public String getComponentName() {
		return mComponentName;
	}
	
	public String getComponentVersion() {
		return mComponentVersion;
	}
	
	public String getLicense() {
		return mLicense;
	}
	
	public String getProjectName() {
		return mProjectName;
	}
}
