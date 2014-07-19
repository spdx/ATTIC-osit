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
package com.sec.ose.airs.service.protex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sec.ose.airs.domain.autoidentify.ProtexIdentificationInfo;
import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.service.IdentificationInfoService;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class ProtexIdentificationInfoService implements IdentificationInfoService {
	private final String delimiter = "\n";
	private final String listDelimiter = "\n\n";
	
	@Override
	public List<ProtexIdentificationInfo> extractIdentificationInfoList(SPDXFileDTO spdxFile) {
		String str = this.extractIdentificationInfoStringFromComment(spdxFile.getComment());
		
		if (str == null) {
			return new ArrayList<ProtexIdentificationInfo>();
		}
		
		List<ProtexIdentificationInfo> list = new ArrayList<ProtexIdentificationInfo>();
		for (String infoStr : str.split(delimiter)) {
			list.add(new ProtexIdentificationInfo(infoStr));
		}
		
		return list;
	}

	@Override
	public boolean hasIdentificationInfo(SPDXFileDTO spdxFile) {
		String str = this.extractIdentificationInfoStringFromComment(spdxFile.getComment());
		
		if (str == null) return false;
		else return true;
	}

	@Override
	public boolean hasSameIdentificationInfoList(SPDXFileDTO srcFile, SPDXFileDTO tgtFile) {
		String src = this.extractIdentificationInfoStringFromComment(srcFile.getComment());
		String tgt = this.extractIdentificationInfoStringFromComment(tgtFile.getComment());
		
		String[] srcArr, tgtArr;
		String srcStr = "", tgtStr = "";
		
		srcArr = src.split(delimiter);
		tgtArr = tgt.split(delimiter);
		Arrays.sort(srcArr);
		Arrays.sort(tgtArr);
		
		for (String line : srcArr)
			srcStr += new ProtexIdentificationInfo(line).toKeyStringForComparison();
		for (String line : tgtArr)
			tgtStr += new ProtexIdentificationInfo(line).toKeyStringForComparison();
		
		return srcStr.equals(tgtStr);
	}
	
	public String extractIdentificationInfoStringFromComment(String comment) {
		// minimum validation
		if (comment.contains(listDelimiter) && StringUtils.countMatches(comment, ProtexIdentificationInfo.SPDX_IDENTIFIED_DELIMETER) > 6 )
			return comment.substring(0, comment.indexOf(listDelimiter)).trim();
		return null;
	}
}
