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
package com.sec.ose.osi.ui.frm.main.manage;

import java.util.ArrayList;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEAnalysisPanel
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class UEAnalysisPanel implements UIEntity {
	private static final long serialVersionUID = 1L;
	
	private String monitoringInterval; 

	public String getMonitoringInterval() {
		return monitoringInterval;
	}

	public void setMonitoringInterval(String monitoringInterval) {
		this.monitoringInterval = monitoringInterval;
	}

	private ArrayList<UEProtexProjectInfo> projectList = null;

	public UEAnalysisPanel() {
		projectList = new ArrayList<UEProtexProjectInfo>();
	}

	public void add(UEProtexProjectInfo projectInfo) {
		this.projectList.add(projectInfo);
	}
	
	public ArrayList<UEProtexProjectInfo> getAllElements() {
		return projectList;
	}
}
