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
package com.sec.ose.osi.util.policy;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * ProjectNamePolicy
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class ProjectNamePolicy {
	public static final String NOT_PERMITTED_SPECIAL_CHARACTER_SET ="~!@#$%^&*+.?<>[]{}-=\\/";
	public static final String[] IGNORED_PATTERN = 
		new String[]{
			"**/*.bmp",
			"**/*.gif",
			"**/*.jpg",
			"**/*.png",
			"**/*.wav",
			"**/*.mp3",
			"**/*.txt"};

	public static PolicyCheckResult checkProjectName(String projectName) {
		
		PolicyCheckResult ret = new PolicyCheckResult();
		
		ArrayList<String> tokenResult = new ArrayList<String>();
		
		String year = "";
		String delimiters = "_";
		
		StringTokenizer st = new StringTokenizer(projectName, delimiters);
		while(st.hasMoreTokens()) {
			tokenResult.add(st.nextToken());
		}
		
		if(tokenResult.size() >= 1) {
			year = tokenResult.get(0);
		} else {
			ret.setResult(PolicyCheckResult.UNKNOWN);
			ret.setResultMsg(
				"\""+projectName+"\" is not following the suggested naming rule.\n"+
				"You must modify your project name.\n"+
				" ex) YY_Projectname_LoginID (14_SampleProject_ID)\n" );
			return ret;
		}
		
		String specialCharSet = getSpecialCharacterSetInProjectName(projectName);
		if(specialCharSet != null) {
			ret.setResult(PolicyCheckResult.PROJECT_NAME_FAIL_WITH_SPECIAL_CHARACTER);
			ret.setResultMsg("Special character \"" + specialCharSet +"\" can not be used at project name\n");
			return ret;
		}
			
		if(getTwoNumYear().equals(year) == false) {
			ret.setResult(PolicyCheckResult.PROJECT_NAME_FAIL_WITH_YEAR);
			ret.setResultMsg(
				"\""+projectName+"\" is not following the suggested naming rule.\n"+
				"You must modify current YEAR of your project name.\n"+
				" ex) YY_Projectname_LoginID (14_SampleProject_ID)\n" );
			return ret;
		}
		
		if(checkProjectPrefixAlreadyExists(projectName) == true) {
			ret.setResult(PolicyCheckResult.PROJECT_NAME_FAIL_WITH_PREFIX);
			ret.setResultMsg(
					"\""+projectName+"\" prefix is already used.\n" );
			return ret;
		}
		
		ret.setResult(PolicyCheckResult.PROJECT_NAME_OK);
		ret.setResultMsg("OK");
		return ret;
	}
	
	private static boolean checkProjectPrefixAlreadyExists(String prefix) {

		if(ProjectAPIWrapper.getProjectNames() == null) return false;
		
		for(String strProjectName: ProjectAPIWrapper.getProjectNames()) {
			if(strProjectName.toLowerCase().contains(prefix.toLowerCase()+"_")) {
				return true;
			}
		}

		return false;
	}

	public static String getProjectNamePrefix() {
		
		String twoNumYear = getTwoNumYear();
		String loginEmail = LoginSessionEnt.getInstance().getUserID();
		String refinedID = getRefinedID(loginEmail);
		
		String ret = twoNumYear+"_"+refinedID+"_";
		
		ret = ret.replace("-", "_");
		
		return ret;
	}

	public static String getRefinedID(String loginEmail) {
		
		String id = loginEmail;
		String emailDomain = "@samsung.com";
		
		int divider = loginEmail.indexOf(emailDomain);
		
		if(divider > 0)
			id = loginEmail.substring(0, divider);
		
		
		for(int i=0; i < ProjectNamePolicy.NOT_PERMITTED_SPECIAL_CHARACTER_SET.length() ; i++){
        	String specialChar = ProjectNamePolicy.NOT_PERMITTED_SPECIAL_CHARACTER_SET.substring(i, i+1);
        	if (id.contains(specialChar)) {
        		id = id.replace(specialChar,"");
        	}
        }
		return id;
	}

	public static String getTwoNumYear() {
		String year = DateUtil.getCurrentTime("%1$tY");
		String twoNumYear = year.substring( 2, 4 );
		return twoNumYear;
	}

	private static String getSpecialCharacterSetInProjectName(String projectName){
		
		 
		StringBuffer specialCharSet = new StringBuffer("");

        for(int i=0; i < NOT_PERMITTED_SPECIAL_CHARACTER_SET.length() ; i++){

        	String specialChar = NOT_PERMITTED_SPECIAL_CHARACTER_SET.substring(i, i+1);
        
        	if (projectName.contains(specialChar)) {
        		specialCharSet.append(specialChar);
        	}
        }
        
		if(specialCharSet.length() == 0)
			return null;
		
		return specialCharSet.toString();
	}
	
}
