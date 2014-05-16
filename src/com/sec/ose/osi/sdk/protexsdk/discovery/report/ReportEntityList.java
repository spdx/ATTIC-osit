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
package com.sec.ose.osi.sdk.protexsdk.discovery.report;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ReportEntityList
 * @author sjh.yoo
 * 
 */
public class ReportEntityList implements Iterable<ReportEntity>{
	private ArrayList<ReportEntity> mReportEntityList = new ArrayList<ReportEntity>();
	
	public ReportEntityList() {}

	public int size() {
		return mReportEntityList.size();
	}
	
	public void addEntity(ReportEntity pReportEntity) {
		mReportEntityList.add(pReportEntity);
	}
	
	public ArrayList<ReportEntity> getEntityList() {
		return mReportEntityList;
	}
	
	public Iterator<ReportEntity> iterator() {
		return mReportEntityList.iterator();
	}
}
