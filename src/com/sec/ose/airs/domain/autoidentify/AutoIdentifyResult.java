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
package com.sec.ose.airs.domain.autoidentify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class AutoIdentifyResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	List<SPDXPackageDTO> sourceSPDXPackageList = new ArrayList<SPDXPackageDTO>();
	List<MatchedFilePair> failedPairList = new ArrayList<MatchedFilePair>();
	
	int totalFileCount = 0;
	int aiCount = 0;
	int aiError = 0;
	int multipleMatchedCount = 0;
	int noMathcedFileCount = 0;
	
	public class MatchedFilePair {
		SPDXFileDTO targetFile;
		List<SPDXFileDTO> matchedFileList;
		
		public MatchedFilePair(SPDXFileDTO targetFile, List<SPDXFileDTO> matchedFileList) {
			this.targetFile = targetFile;
			this.matchedFileList = matchedFileList;
		}
		public SPDXFileDTO getTargetFile() {
			return targetFile;
		}
		public void setTargetFile(SPDXFileDTO targetFile) {
			this.targetFile = targetFile;
		}
		public List<SPDXFileDTO> getMatchedFileList() {
			return matchedFileList;
		}
		public void setMatchedFileList(List<SPDXFileDTO> matchedFileList) {
			this.matchedFileList = matchedFileList;
		}
	}
	
	public List<SPDXPackageDTO> getSourceSPDXPackageList() {
		return sourceSPDXPackageList;
	}
	public void setSourceSPDXPackageList(List<SPDXPackageDTO> sourceSPDXPackageList) {
		this.sourceSPDXPackageList = sourceSPDXPackageList;
	}
	public List<MatchedFilePair> getFailedPairList() {
		return failedPairList;
	}
	public void setFailedPairList(List<MatchedFilePair> failedPairList) {
		this.failedPairList = failedPairList;
	}
	public int getTotalFileCount() {
		return totalFileCount;
	}
	public void setTotalFileCount(int totalFileCount) {
		this.totalFileCount = totalFileCount;
	}
	public int getAiCount() {
		return aiCount;
	}
	public void setAiCount(int aiCount) {
		this.aiCount = aiCount;
	}
	public int getAiError() {
		return aiError;
	}
	public void setAiError(int aiError) {
		this.aiError = aiError;
	}
	public int getMultipleMatchedCount() {
		return multipleMatchedCount;
	}
	public void setMultipleMatchedCount(int multipleMatchedCount) {
		this.multipleMatchedCount = multipleMatchedCount;
	}
	public int getNoMathcedFileCount() {
		return noMathcedFileCount;
	}
	public void setNoMathcedFileCount(int noMathcedFileCount) {
		this.noMathcedFileCount = noMathcedFileCount;
	}
}
