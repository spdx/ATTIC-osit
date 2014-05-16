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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.IdentifyThread;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.ui.frm.main.report.project.JPanProjectExplorer;
import com.sec.ose.osi.ui.frm.main.report.project.ProjectListModel;

/**
 * JPanBillOfMaterials
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanBillOfMaterials extends JPanel {
	private static Log log = LogFactory.getLog(JPanBillOfMaterials.class);

	// ui section
	private static final long serialVersionUID = 1L;

	private JPanel jPanelTable = null;
	private JPanel jPanelBOMWrapper = null;
	private JScrollPane jScrollPaneBOM = null;
	private JTable jTableBOM = null;
	private JPanel jPanelCheckAll;
	private JCheckBox jCheckBoxCheckAll;
	private JLabel jLabelCheckAll;
	private JPanel jPanelOptionWrapper = null;

	private BOMTableModelMgr oBOMTableModelMgr;
	private final HashMap<String, Boolean> RADIO_MAP = new HashMap<String, Boolean>();
								// key:project name , value: displayAllComponent

	public JPanBillOfMaterials(JPanProjectExplorer jPanProjectExplorer) {
		super();
		
		oBOMTableModelMgr = new BOMTableModelMgr();
		ReportMediator.getInstance().setJPanBillOfMaterials(this);
		initialize();		
	}

	public void clearBOMTableMgr() {
		this.oBOMTableModelMgr.clear();
		this.RADIO_MAP.clear();
		this.setRadioAllFilesForAll();
		this.setCurrentProject(null);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(339, 218);
		this.setLayout(new BorderLayout());
		this.add(getJPanelBOMWrapper(), BorderLayout.CENTER);

	}
	
	public void setAllFilesChecked() {
		eventHandler.handleEvent(EventHandler.RADIO_ALL_FILES);
	}

	private JPanel getJPanelOptionWrapper() {
		if (jPanelOptionWrapper == null) {
			jPanelOptionWrapper = new JPanel();
			jPanelOptionWrapper.setLayout(new BorderLayout());
			jPanelOptionWrapper.add(getJPanelSelectedProjectLabel(), BorderLayout.NORTH);
			jPanelOptionWrapper.add(getJPanelOptions(), BorderLayout.WEST);
		}
		return jPanelOptionWrapper;
	}
	
	private JPanel jPanelLabelSelectedProject = null;
	private JLabel jLabelSelectedProjectTitle = null;
	private JPanel getJPanelSelectedProjectLabel() {
		if(jPanelLabelSelectedProject == null) {
			jPanelLabelSelectedProject = new JPanel();
			jPanelLabelSelectedProject.setLayout(new BorderLayout());
			jLabelSelectedProjectTitle = new JLabel();
			jLabelSelectedProjectTitle.setText("  No Project is selected for report generation. "); 
			jPanelLabelSelectedProject.add(jLabelSelectedProjectTitle, BorderLayout.NORTH);
		}
		return jPanelLabelSelectedProject;
	}

	StringBuffer displayedText = new StringBuffer("");
	public void setProjectListTextInfo(Object[] projectNameList) {
		
		if(projectNameList == null || projectNameList.length ==0) {
			jLabelSelectedProjectTitle.setText("  No Project is selected for report generation. ");
			return;
		}
		displayedText.setLength(0);
		
		for(int i=0; i<projectNameList.length; i++) {
			displayedText.append(projectNameList[i]);
			if(i != projectNameList.length-1) {
				displayedText.append(", ");
			}
		}
		
		jLabelSelectedProjectTitle.setText("  Report will be generated for following "+ projectNameList.length +" project(s) : " + displayedText);
		jLabelSelectedProjectTitle.setToolTipText(displayedText.toString());
	}
	
	/**
	 * This method initializes jPanelOptions	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel jPanelOptions = null;
	private JLabel jLabelSelectedComponent = null;
	private JLabel jLabelAllFiles = null;
	private JRadioButton jRadioAllFiles = null;
	private JRadioButton jRadioSelectedComponent = null;

	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints3.gridy = 1;
			jLabelSelectedComponent = new JLabel();
			jLabelSelectedComponent.setText("Display files for the selected components");
			jLabelSelectedComponent.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							eventHandler.handleEvent(EventHandler.RADIO_SELECTED_COMPONENT);
							
						}
					}
				);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(0, 10, 10, 0);
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(10, 0, 10, 0);
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridy = 0;
			jLabelAllFiles = new JLabel();
			jLabelAllFiles.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							eventHandler.handleEvent(EventHandler.RADIO_ALL_FILES);
							
						}
					}
				);

			jLabelAllFiles.setText("Display all files for all components");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints.gridy = 0;
			jPanelOptions = new JPanel();
			jPanelOptions.setLayout(new GridBagLayout());
			jPanelOptions.add(getJRadioAllFilesForAll(), gridBagConstraints);
			jPanelOptions.add(jLabelAllFiles, gridBagConstraints1);
			jPanelOptions.add(getJRadioAllFilesForSelectedComponent(), gridBagConstraints2);
			jPanelOptions.add(jLabelSelectedComponent, gridBagConstraints3);
		}
		return jPanelOptions;
	}

	/**
	 * This method initializes jPanelTable	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelTable() {
		if (jPanelTable == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(10, 10, 0, 10);
			gridBagConstraints1.gridx = 0;
			jPanelTable = new JPanel();
			jPanelTable.setLayout(new GridBagLayout());
			jPanelTable.add(getJScrollPaneBOM(), gridBagConstraints1);			
		}
		return jPanelTable;
	}
	
	/**
	 * This method initializes jRadioAllFiles	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	public JRadioButton getJRadioAllFilesForAll() {
		if (jRadioAllFiles == null) {
			jRadioAllFiles = new JRadioButton();
			jRadioAllFiles.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed()"); 
					eventHandler.handleEvent(EventHandler.RADIO_ALL_FILES);
				}
			});
		}
		return jRadioAllFiles;
	}
	
	public boolean isAllFilesSelected(){
		return getJRadioAllFilesForAll().isSelected();
	}
	/**
	 * This method initializes jRadioSelectedComponent	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	public JRadioButton getJRadioAllFilesForSelectedComponent() {
		if (jRadioSelectedComponent == null) {
			jRadioSelectedComponent = new JRadioButton();
			jRadioSelectedComponent.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed()");
					eventHandler.handleEvent(EventHandler.RADIO_SELECTED_COMPONENT);
				}
			});
		}
		return jRadioSelectedComponent;
	}

	/**
	 * This method initializes jPanelBOMWrapper	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelBOMWrapper() {
		if (jPanelBOMWrapper == null) {
			jPanelBOMWrapper = new JPanel();
			jPanelBOMWrapper.setLayout(new BorderLayout());
			jPanelBOMWrapper.add(getJPanelOptionWrapper(), BorderLayout.NORTH);
			jPanelBOMWrapper.add(getJPanelTable(), BorderLayout.CENTER);
			jPanelBOMWrapper.add(getJPanelCheckAll(), BorderLayout.SOUTH);
		}
		return jPanelBOMWrapper;
	}

	private JPanel getJPanelCheckAll() {

		if (jPanelCheckAll == null) {
			jPanelCheckAll = new JPanel (new FlowLayout(FlowLayout.LEFT));
			jPanelCheckAll.add(getJCheckBoxCheckAll());
			jPanelCheckAll.add(getJLabelCheckAll());
			jPanelCheckAll.add(new JLabel("               "), new JLabel("               ").getName());
			
		}
		return jPanelCheckAll;
	}

	private JLabel getJLabelCheckAll() {
		
		if(jLabelCheckAll == null) {
			jLabelCheckAll = new JLabel("Check all components.");
		}
		return jLabelCheckAll;
	}

	public JCheckBox getJCheckBoxCheckAll() {

		if(jCheckBoxCheckAll == null) {
			jCheckBoxCheckAll = new JCheckBox();
			jCheckBoxCheckAll.setSelected(false);
			jCheckBoxCheckAll.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								eventHandler.handleEvent(EventHandler.CHECK_ALL_CHECKBOX);
							}
						}
					);
		}
		return jCheckBoxCheckAll;
	}

	/**
	 * This method initializes jScrollPaneBOM	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneBOM() {
		if (jScrollPaneBOM == null) {
			jScrollPaneBOM = new JScrollPane();
			jScrollPaneBOM.setViewportView(getJTableBOM());
		}
		return jScrollPaneBOM;
	}

	public void setCurrentProject(String projectName) {
		log.debug("--------------JPanBOM.setCurrentProject(), projectName:"+projectName);

		JTable bomTable = this.getJTableBOM();
		
		if(projectName == null) {
			BOMTableModel model = oBOMTableModelMgr.getDeaultBOMTableModel();
			bomTable.setModel(model);
			model.setColumnWidth(bomTable);
			return;
		}
		
		BOMTableModel model = oBOMTableModelMgr.getBOMTableModel(projectName, this.getJCheckBoxCheckAll());
		bomTable.setModel(model);
		model.setColumnWidth(bomTable);
	}
	
	/**
	 * This method initializes jTableBOM	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableBOM() {
		if (jTableBOM == null) {
			jTableBOM = new JTable();
		}
		return jTableBOM;
	}

	private EventHandler eventHandler = new EventHandler();
			// key: projectName  value: allFileDisplayed radio
	
	class EventHandler {
		protected static final int CHECK_ALL_CHECKBOX = 0;
		protected static final int RADIO_SELECTED_COMPONENT = 2;
		protected static final int RADIO_ALL_FILES = 3;

		public void handleEvent(int eventCode) {
			
			BOMTableModel model = null;
			JPanProjectExplorer jPanProjectExplorer = ReportMediator.getInstance().getJPanProjectExplorer();
			
			switch(eventCode) {
				case CHECK_ALL_CHECKBOX:
			
					model = (BOMTableModel) getJTableBOM().getModel();
					boolean curCheckeValue = getJCheckBoxCheckAll().isSelected();
					model.setAllCheck(curCheckeValue);
					getJTableBOM().updateUI();
					break;
					
				case RADIO_ALL_FILES:
					setRadioAllFilesForAll();
					

					RADIO_MAP.put(jPanProjectExplorer.getJListManagedProjects().getSelectedValue().toString(), true);
					
					setCurrentProject(jPanProjectExplorer.getJListManagedProjects().getSelectedValue().toString());
					setEnabled(false);
					break;
					
				case RADIO_SELECTED_COMPONENT:
					if(jPanProjectExplorer.getJListManagedProjects().getSelectedValue() == null){
						setRadioAllFilesForAll();
						return;
					}
					
					if(IdentifyQueue.getInstance().size() > 0) {
						IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
						if(thread == null)
							break;
						
						boolean prevIsStopByUser = thread.getIsStopByUser();
						thread.setIsStopByUser(false);
						UserRequestHandler.getInstance().handle(UserRequestHandler.SYNC_TO_SERVER, null, true, false);
						thread.setIsStopByUser(prevIsStopByUser);
					}

					RADIO_MAP.put(jPanProjectExplorer.getJListManagedProjects().getSelectedValue().toString(), false);
					
					setRadioAllFilesForSelectedComponent();
					String projectName = jPanProjectExplorer.getJListManagedProjects().getSelectedValue().toString();
					model =oBOMTableModelMgr.getBOMTableModel(projectName, getJCheckBoxCheckAll());
					
					if(model.isAllBOMChecked() == true) {
						 getJCheckBoxCheckAll().setSelected(true);
					} else {
						 getJCheckBoxCheckAll().setSelected(false);
					}

					setCurrentProject(projectName);
					
					setEnabled(true);
					break;
			}
		}
	}

	protected HashMap<String, BOMTableModel> getBOMMap() {
	
		JPanProjectExplorer jPanProjectExplorer = ReportMediator.getInstance().getJPanProjectExplorer();
		HashMap<String, BOMTableModel> bomMap = new HashMap<String, BOMTableModel>();
		ProjectListModel listModel = jPanProjectExplorer.getJListManagedProjects().getProjectListModel();
		int cntItem = listModel.getSize();
		for(int i=0; i<cntItem; i++) {
			String projectName = ""+listModel.getElementAt(i).toString();
			if(projectName.length() == 0)
				continue;
			
			BOMTableModel model = oBOMTableModelMgr.getBOMTableModel(projectName);
			bomMap.put(projectName, model);
		}
		return bomMap;
	}

	public void setRadioAllFilesForAll(){
		getJRadioAllFilesForAll().setSelected(true);
		getJRadioAllFilesForSelectedComponent().setSelected(false);
	}
	
	public void setRadioAllFilesForSelectedComponent(){
		getJRadioAllFilesForAll().setSelected(false);
		getJRadioAllFilesForSelectedComponent().setSelected(true);
	}
	
	public void setEnabled(boolean enabled) {

		this.getJTableBOM().setEnabled(enabled);
		this.getJCheckBoxCheckAll().setEnabled(enabled);
		
		if(enabled == false) {
			this.setCurrentProject(null);
		}
		
	}

	public void removeRadioInfoForProject(String projectName) {
		RADIO_MAP.remove(projectName);
	}

	public void refresh(String projectName) {
		
		if(RADIO_MAP.containsKey(projectName) == false) {
			RADIO_MAP.put(projectName, true);
		}
		
		boolean curRadioAllFileChecked = RADIO_MAP.get(projectName);
			
		if(curRadioAllFileChecked) {
			this.setRadioAllFilesForAll();
			this.setCurrentProject(null);
		}
		else {
			this.setRadioAllFilesForSelectedComponent();
			this.setCurrentProject(projectName);
		}
	
	}
	
	public void setReportUIEnabled(Object[] projectNameList) {
		boolean enabled = false;
		if(projectNameList == null || projectNameList.length == 0) {
			enabled = false;
		} else {
			enabled = true;
		}
		this.jRadioAllFiles.setEnabled(enabled);
		this.jRadioSelectedComponent.setEnabled(enabled);
		this.jCheckBoxCheckAll.setEnabled(enabled);
		ReportMediator.getInstance().setReportButtonEnabled(enabled);
	}
}
