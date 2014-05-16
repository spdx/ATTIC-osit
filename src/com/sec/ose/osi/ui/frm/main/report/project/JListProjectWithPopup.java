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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;
import com.sec.ose.osi.ui.frm.main.manage.dialog.JDlgProjectAdd;
import com.sec.ose.osi.ui.frm.main.report.JPanBillOfMaterials;
import com.sec.ose.osi.ui.frm.main.report.JPanReportMain;
import com.sec.ose.osi.ui.frm.main.report.ReportMediator;

/**
 * JListProjectWithPopup
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JListProjectWithPopup extends AbstractJListProject {
	private static Log log = LogFactory.getLog(JListProjectWithPopup.class);
	private static final long serialVersionUID = -2277801141968086661L;

	public JListProjectWithPopup() {
		super(new ProjectListModel());
		initialize();
	}

	private void initialize() {
		this.addListSelectionListener(new FileSelectionListener());
		this.addMouseListener(
				new MouseAdapter() {
					@SuppressWarnings("deprecation")
					public void mouseClicked(MouseEvent e)  {
						if(e.getButton() == MouseEvent.BUTTON3) {
							log.debug("JLIstProjectWithPop.mouseClicked() : MouseEvent.BUTTON3");
							/// clicked right button
							mEventHandler.handle(EventHandler.RIGHT_BTN_CLICKED);

							if( JListProjectWithPopup.this.getSelectedValues() != null			// existed selected item
								&& JListProjectWithPopup.this.getSelectedValues().length > 0
								) {	
								log.debug("button3");
								mPopup.show(
										e.getComponent(),
										e.getX(),
										e.getY()
										);
							}
						}
					}
				}
				);
	}
	
	private EventHandler mEventHandler = new EventHandler();
	
	class EventHandler {
		protected static final int RIGHT_BTN_CLICKED = 1;
		protected static final int POP_DELETE = 2;
		protected static final int POP_AUTO_DECLARE = 3;
		protected static final int POP_GENERATE_SCANNING_SUMMARY_SHEET = 4;
		protected static final int POP_ADD_PROJECT = 5;
		
		protected void handle(int pCode) {
			switch(pCode) {
			
				case RIGHT_BTN_CLICKED:
					break;
					
				case POP_DELETE:
					JPanBillOfMaterials jPanBillOfMaterials = ReportMediator.getInstance().getJPanBillOfMaterials();
					String curSelectedProject = ReportMediator.getInstance().getJPanProjectExplorer().getCurSelectedProject();
					jPanBillOfMaterials.removeRadioInfoForProject(curSelectedProject);
					deleteSelectedItems();

					jPanBillOfMaterials.setRadioAllFilesForAll();
					jPanBillOfMaterials.setCurrentProject(null);
					
					break;
					
				case POP_ADD_PROJECT:
					if(OSIProjectInfoMgr.getInstance().getAllProjects().size() > 0) {
						JPanManageMain jPanManageMain = ManageMediator.getInstance().getJPanManage();
						JPanReportMain jPanReportMain = ReportMediator.getInstance().getJPanReportMain();
						JDlgProjectAdd dlgProjectList = new JDlgProjectAdd(jPanManageMain.getOwner());
						dlgProjectList.setManageMain(jPanManageMain);
						dlgProjectList.setReportMain(jPanReportMain);
						dlgProjectList.setSize(600, 300);
						WindowUtil.locateCenter(dlgProjectList);
						dlgProjectList.setVisible(true);
					} else {
						String[] buttonOK = {"OK"};
						JOptionPane.showOptionDialog(null, "There is no project", "Search Project", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonOK, "OK");
						return;
					}
					break;
					
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteSelectedItems() {
		Object[] selectedItems = this.getSelectedValues();
		log.debug("deleteSelectedItems: num: "+selectedItems.length);
		
		if(selectedItems == null || selectedItems.length <= 0) return;

		log.debug("try to delete: "+selectedItems);		
		ProjectListModel selectedModel = this.getProjectListModel();

		ArrayList<String> deleteProjectNames = new ArrayList<String>();
				
		for(int i=0; i<selectedItems.length; i++) {
			deleteProjectNames.add(selectedItems[i].toString());
			boolean refresh = false;
			if(i==selectedItems.length-1)
				refresh = true;
			selectedModel.removeElement(selectedItems[i], refresh);
		}

		Collection<OSIProjectInfo> allManagedprojectList = OSIProjectInfoMgr.getInstance().getManagedProjectInfo().getProjects();	

		for(OSIProjectInfo xProjectInfo:allManagedprojectList) {
			if(deleteProjectNames.contains(xProjectInfo.getProjectName())) {
				xProjectInfo.setManaged(false);
				log.debug("delected: "+xProjectInfo.getProjectName());		
			}
		}
		OSIProjectInfoMgr.getInstance().rebuildManagedProjectInfo();
		
		
	}
	
	private MenuPopup mPopup = new MenuPopup();

	class MenuPopup extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public MenuPopup() {
			
			JMenuItem jMenuItemAdd = new JMenuItem("Add project");
			JMenuItem jMenuItemDelete = new JMenuItem("Remove from list");
			
			jMenuItemAdd.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.POP_ADD_PROJECT);
						}
					}
			);
			
			jMenuItemDelete.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.POP_DELETE);
						}
					}
			);
			
			this.add(jMenuItemAdd);
			this.add(jMenuItemDelete);
		}
	}
	
	class FileSelectionListener implements ListSelectionListener {
		
		private String preSelect = "";
		
		// List Selection
		public void valueChanged(ListSelectionEvent e) {
			String curSelectedProject = "";
			
			if(JListProjectWithPopup.this.getSelectedValue()!=null) {
				curSelectedProject = JListProjectWithPopup.this.getSelectedValue().toString();
			}

			if(!preSelect.equals(curSelectedProject)) {
				
				log.debug("Selected project changed: "+ curSelectedProject);
				
				JPanBillOfMaterials jPanBillOfMaterials = ReportMediator.getInstance().getJPanBillOfMaterials();
				jPanBillOfMaterials.refresh(curSelectedProject);
				preSelect = curSelectedProject;
			}
			
		}
	}
}
