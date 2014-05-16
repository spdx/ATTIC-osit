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
package com.sec.ose.osi.sdk.protexsdk.discovery;

/**
 * ProjectDiscoveryController
 * @author sjh.yoo, suhyun47.kim
 * 
 */
public class ProjectDiscoveryController {
	
	private String mProjectName = null;
	private DCStringSearch mDCStringSearch = null;
	private DCCodeMatch mDCCodeMatch = null;
	private DCPatternMatch mDCPatternMatch = null;

	protected ProjectDiscoveryController(String pProjectName) {
		mProjectName = pProjectName;
	}

	public DCStringSearch getStringSearchDiscoveryController() {
		return mDCStringSearch;
	}

	public DCCodeMatch getCodeMatchDiscoveryController() {
		return mDCCodeMatch;
	}

	public DCPatternMatch getPatternMatchDiscoveryController() {
		return mDCPatternMatch;
	}

	public String getProjectName() {

		return mProjectName;
	}

	protected void setStringSearchDiscoveryController(
			DCStringSearch pDCStringSearch) {
		this.mDCStringSearch = pDCStringSearch;
		
	}
	
	protected void setCodeMatchDiscoveryController(
			DCCodeMatch pDCCodeMatch) {
		mDCCodeMatch = pDCCodeMatch;
		
	}
	
	protected void setPatternMatchDiscoveryController(
			DCPatternMatch pDCPatternMatch) {
		mDCPatternMatch = pDCPatternMatch;
		
	}
}
