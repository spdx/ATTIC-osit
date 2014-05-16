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
package com.sec.ose.osi.ui.frm.main.identification.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * SelectedFilePathInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class SelectedFilePathInfo {
	
	private int pathType;
	public static final int INVALID_TYPE = -1;
	public static final int SINGLE_FILE_TYPE = 0x01;
	public static final int MULTIPLE_FILE_TYPE = 0x02;
	public static final int FOLDER_TYPE = 0x03;
	public static final int PROJECT_TYPE = 0x04;
	
	private ArrayList<String> selectedPath = null;
	
	public SelectedFilePathInfo() {
		if(selectedPath == null) {
			selectedPath = new ArrayList<String>();
		}
	}

	public boolean isFile() {
		if(this.pathType == SINGLE_FILE_TYPE || 
		   this.pathType == MULTIPLE_FILE_TYPE) {
			return true;
		}
		return false;
	}
	
	public void setPathType(int pathType) {
		this.pathType = pathType;
	}

	public String getSelectedPath() {
		return selectedPath.size()>0?selectedPath.get(selectedPath.size()-1):"NO SELECTED";
	}

	public ArrayList<String> getSelectedPaths() {
		return selectedPath;
	}
	
	public void setSelectedFilePathInfo( int getPathType, String selectedPath) {
		this.selectedPath.clear();
		setPathType(getPathType);
		this.selectedPath.add(selectedPath);
	}

	public void setSelectedFilePathInfo( int pathType, Collection<String> selectedPath) {
		this.selectedPath.clear();
		setPathType(pathType);
		for(String path:selectedPath) {
			if(path==null) continue;
			this.selectedPath.add(path);
		}
	}

	public String getPathTypeString() {
		
		switch(this.pathType) {
			
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				return "FILE";
				
			case SelectedFilePathInfo.FOLDER_TYPE:
				return "FOLDER";
				
			case SelectedFilePathInfo.PROJECT_TYPE:
				return "PROJECT";
		}
		
		return "";
	}

	public int getPathType() {
		return this.pathType;
	}

}
