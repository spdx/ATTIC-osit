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

/**
 * StringMatchInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class StringMatchInfo extends AbstractMatchInfo {
	
	protected String stringSeachName = "";
	protected int pendingHits = 0;
	protected int identifiedHits = 0;
	
	protected StringMatchInfo() {
	}
	
	protected StringMatchInfo(String pStringSeachName, String pComponent, String pVersion, String pLicense, int pPendingHits, int pIdentifiedHits) {
		this.stringSeachName = pStringSeachName;
		pendingHits = pPendingHits;
		identifiedHits = pIdentifiedHits;
	}
	
	public StringMatchInfo(
			String pProjectName,
			String pStringSeachName, 
			String pComponent, 
			String pVersion,
			String pLicense, 
			int pPendingHits, 
			int pIdentifiedHits, 
			int pStatus, 
			String pComment) {
		
		this.stringSeachName = pStringSeachName;
		this.pendingHits = pPendingHits;
		this.identifiedHits = pIdentifiedHits;
		super.setStatus(pStatus);
		super.setIdentifiedInfo(
				pComponent, 
				pLicense, 
				pVersion, 
				pProjectName);
	}
	
	public String getStringSeachName() {
		return stringSeachName;
	}

	public int getPendingHits() {
		return pendingHits;
	}

	public int getIdentifiedHits() {
		return identifiedHits;
	}
}
