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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import java.util.HashMap;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult.MatchedFilePair;
import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;

/**
 * SPDXAutoIdentifyResult
 * @author sjh.yoo, ytaek.kim, jae-yong.lee
 * 
 */
public class SPDXAutoIdentifyResult {
	AutoIdentifyResult aiResult;
	
	public SPDXAutoIdentifyResult(AutoIdentifyResult src) {
		aiResult = src;
	}
	
	public String getSummary() {
		return aiResult.getAiCount() + " file(s) are AUTO-IDENTIFIED and added into IDENTIFY-QUEUE";
	}
	
	public String getSourceSPDXPackagesInfo() {
		StringBuffer sf = new StringBuffer();
		for (SPDXPackageDTO pkg : aiResult.getSourceSPDXPackageList()) {
			sf.append(" * SPDX File : " + pkg.getFileName() + " (" + pkg.getName() + ")\n");
			sf.append("   - Creator : ").append(pkg.getPerson()).append("\n");
			sf.append("   - Organization : ").append(pkg.getOrganization()).append("\n");
			sf.append("   - Created : ").append(pkg.getCreated()).append("\n");
		}
		return sf.toString();
	}
	
	public String getFailDetails() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for (SPDXPackageDTO pkg : aiResult.getSourceSPDXPackageList()) {
			map.put(pkg.getId(), " (" + pkg.getFileName() + "/" + pkg.getName() + ")");
		}
		
		String resultSummary = " All identification has been completed ";
		int count = 0;
		StringBuffer sf = new StringBuffer();
		for (MatchedFilePair pair :aiResult.getFailedPairList()) {
			count++;
			sf.append(" ").append(pair.getTargetFile().getName()).append("\n");
			for (SPDXFileDTO file : pair.getMatchedFileList()) {
				sf.append("    + " + file.getName() + map.get(file.getPackageId())).append("\n");
			}
		}
		if(count == 0) {
			resultSummary += "without conflict\n";
		} else {
			resultSummary += "with " + count + " conflict(s)\n";
		}
		return resultSummary + sf.toString();
	}
}
