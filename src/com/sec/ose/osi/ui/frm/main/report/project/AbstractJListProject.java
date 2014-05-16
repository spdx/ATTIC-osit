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
package com.sec.ose.osi.ui.frm.main.report.project;


import javax.swing.JList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractJListProject
 * @author suhyun47.kim, hankido.lee
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJListProject extends JList {
	private static Log log = LogFactory.getLog(AbstractJListProject.class);
	private static final long serialVersionUID = 1L;
	private ProjectListModel mListModel = null;

	public AbstractJListProject() {
		
		this(new ProjectListModel());
	}

	public AbstractJListProject(ProjectListModel model) {
		super();
		
		this.mListModel = model;
		this.setModel(mListModel);
	}

	public boolean isExistedElement(String projectName) {
		log.debug("mlist: "+mListModel);		
		return mListModel.isExistedElements(projectName);
		
	}

	public void addElement(String pProjectName) {
		mListModel.addElement(pProjectName);
		
		this.updateUI();
	}

	public ProjectListModel getProjectListModel() {
		return this.mListModel;
	}

	public void setProjectListModel(ProjectListModel model) {
		this.mListModel = model;
		super.setModel(model);
		this.updateUI();
	}	
}
