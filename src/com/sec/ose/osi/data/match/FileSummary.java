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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.localdb.identification.MatchedLine;

/**
 * FileSummary
 * @author suhyun47.kim, sjh.yoo, hankido.lee, ytaek.kim
 * 
 */
public class FileSummary implements ISummaryInfo{
	
	private ArrayList<StringMatchInfo> mStringMatchInfoList = new ArrayList<StringMatchInfo>();
	private ArrayList<CodeMatchInfo> mCodeMatchInfoList = new ArrayList<CodeMatchInfo>();
	private String fullPath = "";
	protected FileSummary(String pFullPath) {
		this.fullPath = pFullPath;
	}	
	
	public String getFullPath() {
		return fullPath;
	}
	
	public Collection<CodeMatchInfo> getCodeMatchInfoList() {
		return mCodeMatchInfoList;
	}

	public Collection<StringMatchInfo> getStringMatchInfoList() {
		return mStringMatchInfoList;
	}
	
	public void addCodeMatchInfoList(
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage, 
			String pMatchedFileForOriginalComponent) {
				mCodeMatchInfoList.add(
					new CodeMatchInfo(
						fullPath, 
						pComponent, 
						pVersion, 
						pLicense, 
						pUsage, 
						pStatus, 
						pPercentage, 
						pMatchedFileForOriginalComponent
					)
				);
	}
	
	public void addCodeMatchInfoListWithIdentifiedInfo(
			String pProjectName,
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pUsage, 
			int pStatus, 
			int pPercentage, 
			String pMatchedFile, 
			String iComponent, 
			String iVersion, 
			String iLicense) {
		
				mCodeMatchInfoList.add(
					new CodeMatchInfo(
						pProjectName,
						fullPath, 
						pComponent, 
						pVersion, 
						pLicense, 
						pUsage, 
						pStatus, 
						pPercentage, 
						pMatchedFile, 
						iComponent, 
						iVersion, 
						iLicense
					)
				);
	}
	
	public CodeMatchInfo getCodeMatchFileUnit(int index) {
		if(index >= mCodeMatchInfoList.size() || index <0)
			return null;
		return (CodeMatchInfo)mCodeMatchInfoList.get(index);
	}
	
	public void addStringSearchInfoList(
										String pProjectName,
										String pSearch, 
										String pComponent, 
										String pVersion, 
										String pLicense, 
										int pPendingHits, 
										int pIdentifiedHits, 
										int pStatus, 
										String pComment) {
		
		mStringMatchInfoList.add(new StringMatchInfo(
														pProjectName,
														pSearch,
														pComponent,
														pVersion,
														pLicense,
														pPendingHits,
														pIdentifiedHits,
														pStatus,
														pComment ));
	}
	
	public StringMatchInfo getStringSearchUnit(int index) {
		if(index >= mStringMatchInfoList.size() || index < 0)
			return null;
		return (StringMatchInfo)mStringMatchInfoList.get(index);
	}
	
	public int getStringMatchInfoListSize() {
		return mStringMatchInfoList.size();
	}
	
	public int getCodeMatchInfoListSize() {
		return mCodeMatchInfoList.size();
	}
	
	public void clear() {
		mStringMatchInfoList.clear();
		mCodeMatchInfoList.clear();
	}

	public HashMap<String,Vector<String>> getMatchLines(String projectName) {
		MatchedLine oMatchedLine = IdentificationDBManager.getStringSearchMatchedLines(projectName, fullPath);
		if(oMatchedLine != null) {
			HashMap<String,Vector<String>> MatchedLines = oMatchedLine.getMatchedLines();
			return MatchedLines;
		}
		return null;
	}
}
