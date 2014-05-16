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
package com.sec.ose.osi.thread.job.identify.data;

import java.util.Collection;

import com.sec.ose.osi.ui.frm.main.identification.codematch.UECodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.patternmatch.UEPatternMatch;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.UEStringMatch;

/**
 * IdentifyDataFactory
 * @author hankido.lee, suhyun47.kim
 * 
 */
public class IdentifyDataFactory {
	
	public static IdentifyData createStringMatchIdentify(
			String projectName,
			int pCompositeType, 
			Collection<String> filePath, 
			UEStringMatch xUEStringMatch,
			String comment) {

		int compositeType = pCompositeType;
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		String currentComponentName = xUEStringMatch.getCurrentComponentName();
		String currentVersionName = xUEStringMatch.getCurrentVersionName();
		String currentLicenseName = xUEStringMatch.getCurrentLicenseName();
		String newComponentName = xUEStringMatch.getNewComponentName();
		String newVersionName = xUEStringMatch.getNewVersionName();
		String newLicenseName = xUEStringMatch.getNewLicenseName();
		String matchedFile = "";
		String stringSearch = xUEStringMatch.getStringSearch();
		
		return new IdentifyData(
				compositeType,
				projectName,
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch);
	}
	
	public static IdentifyData createStringMatchReset(
			String projectName,
			int pCompositeType, 
			Collection<String> filePath, 
			UEStringMatch xUEStringMatch) {

		int compositeType = pCompositeType;
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		String currentComponentName = xUEStringMatch.getCurrentComponentName();
		String currentVersionName = xUEStringMatch.getCurrentVersionName();
		String currentLicenseName = xUEStringMatch.getCurrentLicenseName();
		String newComponentName = "";
		String newVersionName = "";
		String newLicenseName = "";
		String comment = "";
		String matchedFile = "";
		String stringSearch = "";
		
		return new IdentifyData(
				compositeType,
				projectName,
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch
				);
	}
	
	public static IdentifyData createCodeMatchIdentify(
			String projectName,
			int pMatchType, 
			Collection<String> filePath,
			UECodeMatch xUECodeMatch,
			String comment) {
		int compositeType = pMatchType | xUECodeMatch.getCompositeType();
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		String currentComponentName = xUECodeMatch.getCurrentComponentName();
		String currentVersionName = xUECodeMatch.getCurrentVersionName();
		String currentLicenseName = xUECodeMatch.getCurrentLicenseName();
		String newComponentName = xUECodeMatch.getNewComponentName();
		String newVersionName = xUECodeMatch.getNewVersionName();
		String newLicenseName = xUECodeMatch.getNewLicenseName();
		String matchedFile = xUECodeMatch.getMatchedFile();
		String stringSearch = "";
		
		return new IdentifyData(
				compositeType,
				projectName,	
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch);
	}
	
	public static IdentifyData createCodeMatchReset(
			String projectName,
			int pCompositeType, 
			Collection<String> filePath,
			UECodeMatch xUECodeMatch) {
		
		int compositeType = pCompositeType | xUECodeMatch.getCompositeType();
		String originComponentName = xUECodeMatch.getOriginComponentName();
		String originVersionName = xUECodeMatch.getOriginVersionName();
		String originLicenseName = xUECodeMatch.getOriginLicenseName();
		String currentComponentName = xUECodeMatch.getCurrentComponentName();
		String currentVersionName = xUECodeMatch.getCurrentVersionName();
		String currentLicenseName = xUECodeMatch.getCurrentLicenseName();
		String newComponentName = "";
		String newVersionName = "";
		String newLicenseName = "";
		String comment = "";
		String matchedFile = xUECodeMatch.getMatchedFile();
		String stringSearch = "";
		
		return new IdentifyData(
				compositeType,
				projectName,
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch);
	}
	
	public static IdentifyData createPatternMatchIdentify(
			String projectName,
			int pCompositeType,
			Collection<String> filePath,
			UEPatternMatch xUEPatternMatch,
			String comment) {
		
		if(xUEPatternMatch == null)
			return null;
		
		int compositeType = pCompositeType;
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		String currentComponentName = xUEPatternMatch.getCurrentComponentName();
		String currentVersionName = "";
		String currentLicenseName = "";
		String newComponentName = xUEPatternMatch.getNewComponentName();
		String newVersionName = "";
		String newLicenseName = xUEPatternMatch.getNewLicenseName();
		String matchedFile = "";
		String stringSearch = "";
		
		return new IdentifyData(
				compositeType,
				projectName,
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch);
	}
	
	public static IdentifyData createPatternMatchReset(
			String projectName,
			int pCompositeType, 
			Collection<String> filePath,
			UEPatternMatch xUEPatternMatch) {
		
		int compositeType = pCompositeType;
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		String currentComponentName = xUEPatternMatch.getCurrentComponentName();
		String currentVersionName = "";
		String currentLicenseName = "";
		String newComponentName = xUEPatternMatch.getNewComponentName();
		String newVersionName = "";
		String newLicenseName = xUEPatternMatch.getNewLicenseName();
		String comment = "";
		String matchedFile = "";
		String stringSearch = "";
		
		return new IdentifyData(
				compositeType,
				projectName,
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName,
				newVersionName,
				newLicenseName,
				comment,
				filePath,
				matchedFile,
				stringSearch);
	}
}
