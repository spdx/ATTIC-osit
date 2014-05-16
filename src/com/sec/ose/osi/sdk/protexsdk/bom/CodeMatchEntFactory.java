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

import java.util.Iterator;
import java.util.TreeMap;

import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * CodeMatchEntFactory
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public class CodeMatchEntFactory {
	
	public static Iterator<CodeMatchEnt> createCodeMatchEntList(
			String projectName, 
			UIResponseObserver observer
			) {
    	ReportEntityList compareCodeMatches = ReportAPIWrapper.getCompareCodeMatches(projectName, observer, true);
		TreeMap<String, CodeMatchEnt> codeMatchMap = new TreeMap<String, CodeMatchEnt>();

		if(compareCodeMatches == null) return null;
		if(compareCodeMatches.size() == 0) return null;

		for(ReportEntity entity:compareCodeMatches) {
			String fullPath =  entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.FULL_PATH);
			String codeMatchCnt = entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.CODE_MATCH_COUNT);
			String url = entity.getValue(ReportInfo.COMPARE_CODE_MATCHES.COMPARE_CODE_MATCHES_LINK);
			url = url.replaceFirst("127.0.0.1", LoginSessionEnt.getInstance().getProtexServerIP());
			CodeMatchEnt codeMatchEnt = new CodeMatchEnt(
					fullPath,
					url,
					codeMatchCnt,
					projectName
					);
			
			codeMatchMap.put(fullPath, codeMatchEnt);
		}
		
		return codeMatchMap.values().iterator();
	}
	
}
