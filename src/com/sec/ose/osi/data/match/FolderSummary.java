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

/**
 * FolderSummary
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class FolderSummary implements ISummaryInfo{
	
	private String fullPath;
	private ArrayList<CodeMatchInfoForFolder> mCodeMatchInforForFolderList = new ArrayList<CodeMatchInfoForFolder>();
	private ArrayList<StringMatchInfoForFolder> mStringMatchInfoForFolderList = new ArrayList<StringMatchInfoForFolder>();
	protected FolderSummary(String pFullPath) {
		this.fullPath = pFullPath;
	}
	
	protected FolderSummary(String pSearch, String pComponent, String pVersion,
			String pLicense, int pFileCount, int pPendingHits,
			int pIdentifiedHits) {
	}

	public String getPath() {
		return fullPath;
	}

	public int getCodeMatchInforForFolderListSize() {
		return mCodeMatchInforForFolderList.size();
	}

	public int getStringMatchInforForFolderListSize() {
		return mStringMatchInfoForFolderList.size();
	}

	public void addCodeMatchInforForFolderList(
			String pComponent, 
			String pVersion, 
			String pLicense,
			int pPendingHits,
			int pIdentifiedHits,
			int pDeclaredSnippetCount,
			String identifiedComponent, 
			String identifiedVersion, 
			String identifiedLicense) {
		
		if(identifiedComponent != null) {
			mCodeMatchInforForFolderList.add(
					new CodeMatchInfoForFolder(
							pComponent, 
							pVersion, 
							pLicense, 
							pPendingHits, 
							pIdentifiedHits,
							pDeclaredSnippetCount,
							identifiedComponent, 
							identifiedVersion, 
							identifiedLicense));
		} else {
			mCodeMatchInforForFolderList.add(
					new CodeMatchInfoForFolder(
							pComponent, 
							pVersion, 
							pLicense, 
							pPendingHits, 
							pIdentifiedHits,
							pDeclaredSnippetCount));
		}
	}
	
	public void addCodeMatchInforForFolderList(CodeMatchInfoForFolder codeMatchInfoForFolder) {
		mCodeMatchInforForFolderList.add(codeMatchInfoForFolder);
	}
	
	public void addStringMatchInforForFolderList(String pSearch, String pComponent, String pVersion, String pLicense, int pFileCount, int pPendingHits, int pIdentifiedHits) {
		mStringMatchInfoForFolderList.add(new StringMatchInfoForFolder(pSearch,pComponent,pVersion,pLicense,pFileCount,pPendingHits,pIdentifiedHits));
	}

	public CodeMatchInfoForFolder getCodeMatchFolderUnit(int index) {
		if(index >= mCodeMatchInforForFolderList.size()) {
			return null;
		}
		return mCodeMatchInforForFolderList.get(index);
	}
	
	public StringMatchInfoForFolder getStringSearchFolderUnit(int index) {
		if(index >= mStringMatchInfoForFolderList.size()) {
			return null;
		}
		return mStringMatchInfoForFolderList.get(index);
	}
	
	public void clear() {
		mCodeMatchInforForFolderList.clear();
		mStringMatchInfoForFolderList.clear();
	}
}
