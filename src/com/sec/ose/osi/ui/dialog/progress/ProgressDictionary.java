/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog.progress;

import java.awt.Dimension;
import java.util.HashMap;

import com.sec.ose.osi.thread.ui_related.UserRequestHandler;

/**
 * ProgressDictionary
 * @author suhyun47.kim, hankido.lee, sjh.yoo
 *
 */
public class ProgressDictionary {

	public static final int DEFAULT		= 0;
	private static HashMap<Integer, Dimension> 	sProgressSize = new HashMap<Integer, Dimension>();
	
	private static HashMap<Integer, String> 	sProgressTitle = new HashMap<Integer, String>();
	private static HashMap<Integer, String> 	sErrorTitle = new HashMap<Integer, String>();
	private static HashMap<Integer, String> 	sSuccessTitle = new HashMap<Integer, String>();
	static {
		
		// DEFAULT 
		sProgressSize.put(DEFAULT, new Dimension(550, 250));
		sProgressTitle.put(DEFAULT, "Progress");
		
		sErrorTitle.put(DEFAULT, "Error");
		sSuccessTitle.put(DEFAULT, "Completed");
		
		sProgressTitle.put(UserRequestHandler.GENERATE_IDENTIFY_REPORT, "Generate Identify Report");
		sProgressTitle.put(UserRequestHandler.GENERATE_SPDX_REPORT, "Generate SPDX Document");
		
		
		// SAVE_PROPERTY
		
		
		// LOGIN
		sProgressTitle.put(UserRequestHandler.LOGIN, "Connecting...");
		sProgressSize.put(UserRequestHandler.LOGIN, new Dimension(400,180));
		sErrorTitle.put(UserRequestHandler.LOGIN, "Fail to login");
	}
	
	public static String getErrorTitle(int pCode) {
		
		String returnValue = sErrorTitle.get(pCode);
		
		if(returnValue != null)
			return returnValue;
		else
			return sErrorTitle.get(DEFAULT);
	}
	
	public static String getJDlgProgressTitle(int pCode) {
		
		String returnValue = sProgressTitle.get(pCode);
		
		if(returnValue != null)
			return returnValue;
		else
			return sProgressTitle.get(DEFAULT);
	}
	
	public static Dimension getJDlgProgressSize(int pCode) {
		Dimension returnValue = sProgressSize.get(pCode);
		
		if(returnValue != null)
			return returnValue;
		else
			return sProgressSize.get(DEFAULT);
	}

	public static String getSuccessTitle(int pCode) {
		String returnValue = sSuccessTitle.get(pCode);
		
		if(returnValue != null)
			return returnValue;
		else
			return sSuccessTitle.get(DEFAULT);
	}
}
