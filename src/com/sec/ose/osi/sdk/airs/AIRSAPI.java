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
package com.sec.ose.osi.sdk.airs;

import java.util.ArrayList;
import java.util.List;

import com.sec.ose.airs.domain.autoidentify.ProtexIdentificationInfo;
import com.sec.ose.airs.service.protex.AIRSProtexService;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class AIRSAPI extends AIRSProtexService {
	
	List<ProtexIdentificationInfo> infoList = new ArrayList<ProtexIdentificationInfo>();
	
	public void init(Object protexServerProxyObject) throws Exception {
		super.getProtexSDKAPIService().setProtexServerProxyObject(protexServerProxyObject);
	}
	
	protected boolean identifyRequest(String targetProjectId, ProtexIdentificationInfo info) {
		infoList.add(info);
		return true;
	}

	public List<ProtexIdentificationInfo> getInfoList() {
		return infoList;
	}
	public void setInfoList(List<ProtexIdentificationInfo> infoList) {
		this.infoList = infoList;
	}
}
