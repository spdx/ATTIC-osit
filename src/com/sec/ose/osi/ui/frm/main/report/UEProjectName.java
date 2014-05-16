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
package com.sec.ose.osi.ui.frm.main.report;

import java.util.ArrayList;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEProjectName
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UEProjectName  implements UIEntity {
	private ArrayList<String> projectNames;
	private static final long serialVersionUID = 1L;

	public UEProjectName(String projectName) {
		projectNames = new ArrayList<String>();
		projectNames.add(projectName);
	}

	public UEProjectName(ArrayList<String> projectNames) {
		this.projectNames = projectNames;
	}

	public ArrayList<String> getProjectNames() {
		return projectNames;
	}

	public String getProjectName() {

		if(projectNames.size() < 1)
			return null;
		else
			return projectNames.get(0);
	}

}
