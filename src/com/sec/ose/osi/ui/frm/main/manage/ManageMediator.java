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

import com.sec.ose.osi.data.project.OSIProjectInfo;

/**
 * ManageMediator
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class ManageMediator {

	private JPanManageMain jPanManageMain;
	private ManagedProjectTableModel projectTableModel;
	private volatile static ManageMediator instance = null;
	
	private ManageMediator() {}

	public static ManageMediator getInstance() {
		if(instance == null) {
			synchronized(ManageMediator.class) {
				if(instance == null) {
					instance = new ManageMediator();
				}
			}
		}
		return instance;
	}
	
	public void setJPanManageMain(JPanManageMain pJPanManageMain) {
		this.jPanManageMain = pJPanManageMain;
	}

	public void setJPanManage(JPanManageMain mJPanAnalysis) {
		this.jPanManageMain = mJPanAnalysis;
	}

	public void setProjectTableModel(ManagedProjectTableModel projectTableModel) {
		this.projectTableModel = projectTableModel;
	}

	public void setProjectAnalysisStatus(String projectName, int status) {
		projectTableModel.setProjectAnalysisStatus(projectName,status);
	}

	public void setStatusText(String statusText) {
		jPanManageMain.getJLabelStatus().setText(statusText);
	}

	public void appendMessageToConsole(String text) {
		jPanManageMain.writeConsoleWithTime(text);
	}

	public void setProgressBarVisible(boolean visible) {
		jPanManageMain.getJAnalyzeProgressBar().setVisible(visible);
	}

	public ArrayList<OSIProjectInfo> getAnalysisProjects() {
		
		return projectTableModel.getAnalysisProjects();
	}

	public void updateUIForStartAnalysis() {
		jPanManageMain.getJButtonAnalyze().setEnabled(false);
		jPanManageMain.getJButtonCancel().setEnabled(true);
		jPanManageMain.getJAnalyzeProgressBar().setValue(0);
	}

	public void updateUIForFinishAnalysis() {
		jPanManageMain.refreshMonitorIntervalInfo();
		jPanManageMain.getJButtonAnalyze().setEnabled(true);
		jPanManageMain.getJButtonCancel().setEnabled(false);
	}

	public void setProgressValue(Integer newValue) {
		jPanManageMain.getJAnalyzeProgressBar().setValue(newValue);
	}

	public void showPopupFrame(String message) {
//		PopupFrame pf = new PopupFrame(message);
	}

	public JPanManageMain getJPanManage() {
		return jPanManageMain;
	}

	public void addManagedProject(OSIProjectInfo xOSIProjectInfo) {
		if(jPanManageMain == null) {
			return;
		}
		jPanManageMain.addManagedProject(xOSIProjectInfo);
	}

	public void refreshMonitorInterval() {
		jPanManageMain.refreshMonitorIntervalInfo();
	}
}
