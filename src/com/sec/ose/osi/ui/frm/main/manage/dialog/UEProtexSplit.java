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
package com.sec.ose.osi.ui.frm.main.manage.dialog;

import java.io.File;

import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.util.tools.ProjectSplitUtil;

/**
 * UEProtexSplit
 * @author sjh.yoo, hankido.lee
 * 
 */
public class UEProtexSplit implements UIEntity {
	
	private static final long serialVersionUID = 1L;
	File rootLocationFile;
	ProjectSplitUtil projectSplitUtil;

	public UEProtexSplit(File pRootLocationFile, ProjectSplitUtil pProjectSplitUtil) {
		rootLocationFile = pRootLocationFile;
		projectSplitUtil = pProjectSplitUtil;
	}
	
	public File getRootLocationFile() {
		return rootLocationFile;
	}
	
	public ProjectSplitUtil getProjectSplitUtil() {
		return projectSplitUtil;
	}
	
}
