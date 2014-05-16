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
package com.sec.ose.osi.ui.frm.main.identification;

import java.util.ArrayList;

import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;

/**
 * UEIdentifyResetComment
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UEIdentifyResetComment implements UIEntity {
	private static final long serialVersionUID = 1L;
	
	private String projectName;
	private String comment;
	
	private int		matchType;
	private UIEntity ueMatched;
	private ArrayList<String> selectedPath;
	private int	pathType;

	@SuppressWarnings("unchecked")
	public UEIdentifyResetComment(
			String projectName, 
			SelectedFilePathInfo selectedFilePathInfo,
			int matchType,
			UIEntity ueMatched,
			String comment) {
		
		super();

		this.projectName = projectName;
		this.comment = comment;
		this.matchType = matchType;
		this.ueMatched = ueMatched;
		this.selectedPath = (ArrayList<String>)selectedFilePathInfo.getSelectedPaths().clone();
		this.pathType = selectedFilePathInfo.getPathType();
	}

	public String getComment() {
		return comment;
	}

	public int getMatchType() {
		return matchType;
	}

	public int getPathType() {
		return pathType;
	}

	public String getProjectName() {
		return projectName;
	}

	public ArrayList<String> getSelectedPath() {
		return selectedPath;
	}

	public UIEntity getUeMatched() {
		return ueMatched;
	}
	
	private StringBuffer buf = new StringBuffer();
	public String toString() {
		buf.setLength(0);
		buf.append("project: ").append(projectName);
		buf.append("/ matchtype: ").append(matchType);
		buf.append("/ filetype: ").append(pathType);
		buf.append("/ selected: ").append(selectedPath);
		buf.append("/ ueMatched: ").append(ueMatched);
		
		return buf.toString();
	}

	public boolean isFile() {
		
		if(pathType == SelectedFilePathInfo.SINGLE_FILE_TYPE ||
		   pathType == SelectedFilePathInfo.MULTIPLE_FILE_TYPE) {
			return true;
		}
		
		return false;
	}

	
}
