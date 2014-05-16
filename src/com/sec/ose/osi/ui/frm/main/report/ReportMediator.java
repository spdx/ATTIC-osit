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
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.ui.frm.main.report.project.JPanProjectExplorer;

/**
 * ReportMediator
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class ReportMediator {
	private static Log log = LogFactory.getLog(ReportMediator.class);
	
	private volatile static ReportMediator instance = null;

	public static ReportMediator getInstance() {
		if(instance == null) {
			synchronized(ReportMediator.class) {
				if(instance == null) {
					instance = new ReportMediator();
				}
			}
		}
		return instance;
	}
	private JPanProjectExplorer jPanProjectExplorer;
	
	private JPanBillOfMaterials jPanBillOfMaterials;
	private JPanReportMain jPanReportMain;
	private ReportMediator(){}

	public String getFirstSelectedProjectName() {
		
		Object[] selectedProjects = jPanProjectExplorer.getAllSelectedProjects();
		if(selectedProjects == null || selectedProjects.length < 1)
			return "";
		
		if(selectedProjects[0] == null)
			return "";
		
		return selectedProjects[0].toString();
	}

	public JPanBillOfMaterials getJPanBillOfMaterials() {
		return jPanBillOfMaterials;
	}

	public JPanProjectExplorer getJPanProjectExplorer() {
		return this.jPanProjectExplorer;
	}

	public JPanReportMain getJPanReportMain() {
		return jPanReportMain;
	}

	public Collection<String> getSelectedProjectList() {
		
		Object[] selectedProjects = jPanProjectExplorer.getAllSelectedProjects();
		if(selectedProjects == null || selectedProjects.length < 1)
			return null;
		
		log.debug("getSelectedProjectList()");		
		ArrayList<String> selected = new ArrayList<String>();
		for(Object projectName:selectedProjects) {
			if(projectName == null) continue;

			selected.add(projectName.toString());
			log.debug("  >> selected: "+projectName.toString());
		}
		
		
		return selected;
	}

	public void setJPanBillOfMaterials(JPanBillOfMaterials jPanBillOfMaterials) {
		this.jPanBillOfMaterials = jPanBillOfMaterials;
	}

	public void setJPanProjectExplorer(JPanProjectExplorer jPanProjectExplorer) {
		this.jPanProjectExplorer = jPanProjectExplorer;
	}

	public void setJPanReportMain(JPanReportMain jPanReportMain) {
		this.jPanReportMain = jPanReportMain;
	}

	public void setReportButtonEnabled(boolean enabled) {
		jPanReportMain.setReportButtonEnabled(enabled);
	}

}
