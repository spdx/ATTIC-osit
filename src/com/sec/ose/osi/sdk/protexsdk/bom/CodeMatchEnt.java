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

/**
 * CodeMatchEnt
 * @author suhyun47.kim
 * 
 */
public class CodeMatchEnt {
	private String fullPath;
	private String url;
	private String codeMatchCnt; //key	"codematchcount"	
	private String projectName;

	public String getProjectName() {
		return projectName;
	}

	protected CodeMatchEnt(
			String fullPath, 
			String url, 
			String codeMatchCnt, 
			String projectName) {
		super();
		this.fullPath = fullPath;
		this.url = url;
		this.codeMatchCnt = codeMatchCnt;
		this.projectName = projectName;
	}

	public String getCodeMatchCnt() {
		return codeMatchCnt;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getUrl() {
		return url;
	}
	
}
