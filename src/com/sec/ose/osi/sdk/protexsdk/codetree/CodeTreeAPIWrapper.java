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
package com.sec.ose.osi.sdk.protexsdk.codetree;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.bom.IdentifiedFileEntFactory;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISDKInterfaceManager;

/**
 * CodeTreeAPIWrapper
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class CodeTreeAPIWrapper {
	private static Log log = LogFactory.getLog(IdentifiedFileEntFactory.class);
	
	private static HashMap<String,PartialCodeTree> codeTreeMap = new HashMap<String,PartialCodeTree>();

	public static PartialCodeTree getCodeTree(String projectName) {
		return codeTreeMap.get(projectName);
	}
	
	public static PartialCodeTree setCodeTree(String projectName, UIResponseObserver observer) {
		String ROOT = "/";
		String projectID = UISDKInterfaceManager.getSDKInterface().getProjectID(projectName);

		String pMessage = " > Generating Code Tree from server.\n";
		observer.pushMessageWithHeader(pMessage);
		
		try {
			PartialCodeTree codeTree = getCodeTree(projectName);
			if(codeTree == null) {
				codeTree = ProtexSDKAPIManager.getCodeTreeAPI().getCodeTreeByNodeTypes(projectID, ROOT, CodeTreeUtilities.INFINITE_DEPTH, Boolean.TRUE, CodeTreeUtilities.ALL_CODE_TREE_NODE_TYPES);
				codeTreeMap.put(projectName, codeTree);
			}
			return codeTree;
		} catch (SdkFault e) {
			log.warn(e);
		}
		return null;
	}

	public static void refreshCodeTree(String projectName, UIResponseObserver observer) {
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		if(codeTreeMap.containsKey(projectName)) {
			codeTreeMap.remove(projectName);
			setCodeTree(projectName, observer);
		}
	}
}
