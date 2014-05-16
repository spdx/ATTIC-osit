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
package com.sec.ose.osi.data.match;

import java.util.HashMap;

/**
 * AbstractMatchInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class AbstractMatchInfo {
	
	public static final int STATUS_UNKNOWN = -999;
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_IDENTIFIED = 1;
	public static final int STATUS_REJECT = 2;
	public static final int STATUS_DECLARED = 4;
	public static final int STATUS_IDENTIFIEDCOMMON = 10;
	
	public static final String PENDING = "Pending";
	public static final String IDENTIFIED = "Identified";
	public static final String REJECTED = "Rejected";
	public static final String DECLARED = "Declared";
	
	public static HashMap<Integer, String> MATCED_INFO_STATUS_MAP = new HashMap<Integer, String>();
	static {
		MATCED_INFO_STATUS_MAP.put(STATUS_PENDING, PENDING);
		MATCED_INFO_STATUS_MAP.put(STATUS_IDENTIFIED, IDENTIFIED);
		MATCED_INFO_STATUS_MAP.put(STATUS_REJECT, REJECTED);
		MATCED_INFO_STATUS_MAP.put(STATUS_UNKNOWN, REJECTED);
		MATCED_INFO_STATUS_MAP.put(STATUS_IDENTIFIEDCOMMON, REJECTED);
		MATCED_INFO_STATUS_MAP.put(STATUS_DECLARED, DECLARED);
	}
	
	private IdentifiedInfo identifiedInfo = null;
	private int status = STATUS_PENDING;
	
	public IdentifiedInfo getIdentifiedInfo() {
		return identifiedInfo;
	}

	
	protected void setStatus(int status) {
		this.status = status;
	}

	protected void setIdentifiedInfo(
			String componentName, 
			String licenseName, 
			String versionName,
			String protexProjectName
			) {
		
		identifiedInfo = new IdentifiedInfo(componentName, licenseName, versionName, protexProjectName);
	}
	
	public int getStatus() {
		return status;
	}

}
