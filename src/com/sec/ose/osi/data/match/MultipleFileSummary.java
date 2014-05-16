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

/**
 * MultipleFileSummary
 * @author hankido.lee
 * 
 */
public class MultipleFileSummary implements ISummaryInfo {
	
	private ArrayList<String> paths = null;
	private ArrayList<StringMatchInfo> mStringMatchInfoList = new ArrayList<StringMatchInfo>();
	private ArrayList<CodeMatchInfo> mCodeMatchInfoList = new ArrayList<CodeMatchInfo>();
	private ArrayList<StringMatchInfoForFolder> mStringMatchInfoForMultipleFileSummary = new ArrayList<StringMatchInfoForFolder>();
	private ArrayList<CodeMatchInfoForFolder> mCodeMatchInforForMultipleFileSummary = new ArrayList<CodeMatchInfoForFolder>();

	protected MultipleFileSummary(ArrayList<String> pPaths) {
		this.paths = pPaths;
	}
	
	public ArrayList<String> getPaths() {
		return paths;
	}
	
	public String getFirstPath() {
		return paths.get(0);
	}
	
	public Collection<CodeMatchInfo> getCodeMatchInfoList() {
		return mCodeMatchInfoList;
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
										paths, 
										pComponent, 
										pVersion, 
										pLicense, 
										pUsage, 
										pStatus, 
										pPercentage, 
										pMatchedFileForOriginalComponent ));
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
										paths, 
										pComponent, 
										pVersion, 
										pLicense, 
										pUsage, 
										pStatus, 
										pPercentage, 
										pMatchedFile, 
										iComponent, 
										iVersion, 
										iLicense ));
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
	
	public void addStringMatchInforForMultipleFileSummary(
			String pSearch, 
			String pComponent, 
			String pVersion, 
			String pLicense, 
			int pFileCount, 
			int pPendingHits, 
			int pIdentifiedHits) {
		
		mStringMatchInfoForMultipleFileSummary.add(
				new StringMatchInfoForFolder(
						pSearch,
						pComponent,
						pVersion,
						pLicense,
						pFileCount,
						pPendingHits,
						pIdentifiedHits));
	}

	public int getStringMatchInforForMultipleFileSummarySize() {
		return mStringMatchInfoForMultipleFileSummary.size();
	}

	public StringMatchInfoForFolder getStringSearchFolderUnit(int rowIndex) {
		return mStringMatchInfoForMultipleFileSummary.get(rowIndex);
	}

	public int getCodeMatchInforForMultipleFileSummarySize() {
		return mCodeMatchInforForMultipleFileSummary.size();
	}
	
	public void addCodeMatchInforForMultipleFileSummary (
			String pComponent, 
			String pVersion, 
			String pLicense,
			int pPendingSnippetCount,
			int pIdentifiedSnippetCount,
			int pDeclaredSnippetCount,
			String identifiedComponent, 
			String identifiedVersion, 
			String identifiedLicense) {
		
		if(identifiedComponent != null) {
			mCodeMatchInforForMultipleFileSummary.add(
					new CodeMatchInfoForFolder(
							pComponent, 
							pVersion, 
							pLicense, 
							pPendingSnippetCount, 
							pIdentifiedSnippetCount,
							pDeclaredSnippetCount,
							identifiedComponent, 
							identifiedVersion, 
							identifiedLicense));
		} else {
			mCodeMatchInforForMultipleFileSummary.add(
					new CodeMatchInfoForFolder(
							pComponent, 
							pVersion, 
							pLicense, 
							pPendingSnippetCount, 
							pIdentifiedSnippetCount,
							pDeclaredSnippetCount));
		}
	}

	public CodeMatchInfoForFolder getCodeMatchMultipleFileUnit(int rowIndex) {
		if(rowIndex >= mCodeMatchInforForMultipleFileSummary.size()) {
			return null;
		}
		return mCodeMatchInforForMultipleFileSummary.get(rowIndex);
	}
	
}
