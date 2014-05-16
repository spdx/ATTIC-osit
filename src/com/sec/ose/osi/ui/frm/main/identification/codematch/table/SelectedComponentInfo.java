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
package com.sec.ose.osi.ui.frm.main.identification.codematch.table;

/**
 * SelectedComponentInfo
 * @author hankido.lee
 * 
 */
public class SelectedComponentInfo {
	private String componentNameForHoldingPointer="";
	private String versionNameForHoldingPointer="";
	private String matchedFileForHoldingPointer="";
	
	private int type;
	public static final int TYPE_FOLDER = 1;
	public static final int TYPE_FILE = 2;

	public void setHoldingPointerValue(
			String pComponentNameForHoldingPointer,
			String pVersionNameForHoldingPointer,
			String pMatchedFileForHoldingPointer,
			int type) {
		
		componentNameForHoldingPointer = pComponentNameForHoldingPointer;
		versionNameForHoldingPointer = pVersionNameForHoldingPointer;
		matchedFileForHoldingPointer = pMatchedFileForHoldingPointer;
		this.type = type;
	}


	public int getHoldingTableType() {
		return type;
	}

	public String getComponentNameForHoldingPointer() {
		return componentNameForHoldingPointer;
	}

	public String getVersionNameForHoldingPointer() {
		return versionNameForHoldingPointer;
	}

	public String getMatchedFileForHoldingPointer() {
		return matchedFileForHoldingPointer;
	}
}