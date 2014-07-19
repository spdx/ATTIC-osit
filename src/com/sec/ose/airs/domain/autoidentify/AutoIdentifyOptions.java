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

import java.io.Serializable;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class AutoIdentifyOptions implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean identifyWhenExistingOnlyOneSamePathFile = false;
	private boolean identifyRecentCreatedPackageInfo = false;
	private boolean overwrite = false;
		
	public boolean isIdentifyWhenExistingOnlyOneSamePathFile() {
		return identifyWhenExistingOnlyOneSamePathFile;
	}
	public void setIdentifyWhenExistingOnlyOneSamePathFile(boolean identifyWhenExistingOnlyOneSamePathFile) {
		this.identifyWhenExistingOnlyOneSamePathFile = identifyWhenExistingOnlyOneSamePathFile;
	}
	public boolean isIdentifyRecentCreatedPackageInfo() {
		return identifyRecentCreatedPackageInfo;
	}
	public void setIdentifyRecentCreatedPackageInfo(boolean identifyRecentCreatedPackageInfo) {
		this.identifyRecentCreatedPackageInfo = identifyRecentCreatedPackageInfo;
	}
	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	
}
