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
package com.sec.ose.osi.thread.job.identify.data;

import java.util.HashMap;

import com.sec.ose.osi.thread.job.analysis.AnalysisMonitorThread;

/**
 * IdentifiedController
 * @author sjh.yoo, hankido.lee
 * 
 */
public class IdentifiedController {
	private static HashMap<String,Integer> curProjectStatus = new HashMap<String,Integer>();
	
	public synchronized static int getProjectStatus(String pProjectName) {
		Integer tmpInt = curProjectStatus.get(pProjectName);
		if(tmpInt == null) {
			curProjectStatus.put(pProjectName,AnalysisMonitorThread.STATUS_READY);
			tmpInt = curProjectStatus.get(pProjectName);
		}
		return tmpInt.intValue();
	}
	
	public synchronized static void setProjectStatus(String pProjectName,int status) {
		curProjectStatus.put(pProjectName,status);
	}
}
