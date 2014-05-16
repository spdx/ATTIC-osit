/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog.setting;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEProjectAnalysisSetting
 * @author sjh.yoo, hankido.lee
 *
 */
public class UEProjectAnalysisSetting implements UIEntity {
	public static final int TYPE_DISABLE = 0;
	public static final int TYPE_12_HOUR = 1;
	public static final int TYPE_24_HOUR = 2;
	public static final int TYPE_48_HOUR = 3;
	public static final int TYPE_USER_DEFINED = 4;
	public static final int TYPE_FIXED_TIME = 5;
	
	private static final long serialVersionUID = 87892386148806150L;
	private int selectedType = 0;
	private int monitorInterval = 0;
	private int timeCycle = 0;
	private int timeHour = 0;
	private int timeMinite = 0;
	private int projectSplitFileCountLimit = 1000;

	public UEProjectAnalysisSetting() {}
	
	public int getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(int selectedType) {
		this.selectedType = selectedType;
	}

	public int getMonitorInterval() {
		return monitorInterval;
	}

	public void setMonitorInterval(int monitorInterval) {
		this.monitorInterval = monitorInterval;
	}

	public int getTimeCycle() {
		return timeCycle;
	}

	public void setTimeCycle(int timeCycle) {
		this.timeCycle = timeCycle;
	}

	public int getTimeHour() {
		return timeHour;
	}

	public void setTimeHour(int timeHour) {
		this.timeHour = timeHour;
	}

	public int getTimeMinite() {
		return timeMinite;
	}

	public void setTimeMinite(int timeMinite) {
		this.timeMinite = timeMinite;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getProjectSplitFileCountLimit() {
		return projectSplitFileCountLimit;
	}

	public void setProjectSplitFileCountLimit(int projectSplitFileNum) {
		this.projectSplitFileCountLimit = projectSplitFileNum;
	}

	
}
