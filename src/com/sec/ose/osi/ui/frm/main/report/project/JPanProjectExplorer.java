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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;
import com.sec.ose.osi.ui.frm.main.report.JPanBillOfMaterials;
import com.sec.ose.osi.ui.frm.main.report.ReportMediator;

/**
 * JPanProjectExplorer
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanProjectExplorer extends JPanel {
	private static Log log = LogFactory.getLog(JPanProjectExplorer.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelSelectedPart = null;
	private JScrollPane jScrollPaneProjectList = null;
	private JListProjectWithPopup jListManagedProjects = null;
	private JPanBillOfMaterials jPanBOM = null;

	/**
	 * This is the default constructor
	 */
	public JPanProjectExplorer() {
		super();
		
		ReportMediator.getInstance().setJPanProjectExplorer(this);
		initialize();

		updateUI();
	}

	public void updateProjectList(){
		
		Collection<OSIProjectInfo> allManagedprojectList;	
		if( ManageMediator.getInstance().getJPanManage() == null)
			allManagedprojectList = new ArrayList<OSIProjectInfo>();
		else 
			allManagedprojectList = ManageMediator.getInstance().getJPanManage().getProjectList();
		
		log.debug("updateProjectList , size: "+allManagedprojectList.size());
		ProjectListModel selectedModel = getJListManagedProjects().getProjectListModel();
		selectedModel.clear();
		
		if(allManagedprojectList.size() <= 0) {
			selectedModel.refreshJList();
			getjPanBOM().setReportUIEnabled(null);
			return;
		}

		for(OSIProjectInfo tmpOSIProjectInfo:  allManagedprojectList) {
			log.debug("getProjectName() : "+tmpOSIProjectInfo.getProjectName());
			if (tmpOSIProjectInfo.isAnalyzed() == true){
				selectedModel.addElement(tmpOSIProjectInfo.getProjectName(), false);
			}
		}
		selectedModel.refreshJList();
		
		if(jListManagedProjects.getSelectedValue() == null) {
			jListManagedProjects.setSelectedIndex(0);
		}
		
		if(selectedModel.getSize() <= 0) {
			getjPanBOM().setReportUIEnabled(null);
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(1);
		borderLayout.setVgap(1);
		this.setLayout(borderLayout);
		this.add(getJPanelSelectedPart(), BorderLayout.CENTER);
		
	}

	private JPanel getJPanelSelectedPart() {			
		if (jPanelSelectedPart == null) {
			jPanelSelectedPart = new JPanel();
			jPanelSelectedPart.setBorder(new TitledBorder("Selected Project(s)"));
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setVgap(10);
			jPanelSelectedPart.setLayout(borderLayout);
			jPanelSelectedPart.add(getJScrollPaneProjectList(), BorderLayout.CENTER);
	
		}
		return jPanelSelectedPart;
	}

	/**
	 * This method initializes jScrollPaneProjectList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneProjectList() {
		if (jScrollPaneProjectList == null) {
			jScrollPaneProjectList = new JScrollPane();
			jScrollPaneProjectList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPaneProjectList.setViewportView(getJListManagedProjects());
		}
		return jScrollPaneProjectList;
	}

	/**
	 * This method initializes jListSelectedProjects	
	 * 	
	 * @return javax.swing.JList	
	 */
	public JListProjectWithPopup getJListManagedProjects() {
		if (jListManagedProjects == null) {
			jListManagedProjects = new JListProjectWithPopup();
			jListManagedProjects.addListSelectionListener(new ListSelectionListener(){
				@SuppressWarnings("deprecation")
				@Override
				public void valueChanged(ListSelectionEvent e) {
					getjPanBOM().setProjectListTextInfo(jListManagedProjects.getSelectedValues());
					getjPanBOM().setReportUIEnabled(jListManagedProjects.getSelectedValues());
				}
			});
		}
		return jListManagedProjects;
	}

	public void setjPanBOM(JPanBillOfMaterials jPanBOM) {
		this.jPanBOM = jPanBOM;
	}
	
	public JPanBillOfMaterials getjPanBOM() {
		return jPanBOM;
	}

	@SuppressWarnings("deprecation")
	public Object[] getAllSelectedProjects() {
		return this.getJListManagedProjects().getSelectedValues();
	}

	public String getCurSelectedProject() {
		
		if(this.getJListManagedProjects().getSelectedValue()!=null)
			return this.getJListManagedProjects().getSelectedValue().toString();
		
		else return null;
	}
}
