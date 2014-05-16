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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.report.project.JPanProjectExplorer;

/**
 * JPanReportMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanReportMain extends JPanel {
	private static Log log = LogFactory.getLog(JPanReportMain.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanel = null;
	private JButton jButtonReportExport = null;
	private JSplitPane jSplitPaneMain = null;
	public JPanProjectExplorer jPanelExplorer = null;
	
	public JPanBillOfMaterials jPanBOM;	

	public JPanReportMain() {
		super();
		initialize();
		ReportMediator.getInstance().setJPanReportMain(this);
		
		this.getJPanBOM().setCurrentProject(null);
	}
	
	public void resetProjectList(){
		jPanelExplorer.updateProjectList();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(482, 200);
		this.setLayout(new GridBagLayout());		

		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.weighty = 1.0;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 10;
		gridBagConstraints1.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints1.gridy = 0;

		this.add(getJSplitPane(), gridBagConstraints11);
		this.add(getJPanelbutton(), gridBagConstraints1);
				
	}

	private JSplitPane getJSplitPane() {

		if(jSplitPaneMain == null) {
			jSplitPaneMain = new JSplitPane();
			jSplitPaneMain.setDividerLocation(200);
			jSplitPaneMain.setLeftComponent(getJPanelExplorer());
			jSplitPaneMain.setRightComponent(getJPanBOM());
			jSplitPaneMain.setDividerSize(5);
			jSplitPaneMain.setBorder(null);
		}
			
		return jSplitPaneMain;
	}
	
	
	private JPanProjectExplorer getJPanelExplorer() {

		if(jPanelExplorer == null) {
			jPanelExplorer = new JPanProjectExplorer();
			jPanelExplorer.setjPanBOM(getJPanBOM());
			jPanelExplorer.setMinimumSize(new Dimension(130,300));
		}
			
		return jPanelExplorer;
	}

	private JPanBillOfMaterials getJPanBOM() {

		if(jPanBOM == null) {
			jPanBOM = new JPanBillOfMaterials(getJPanelExplorer());
		}
			
		return jPanBOM;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelbutton() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints4.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;

			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonReportExport(), gridBagConstraints4);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReportExport() {
		if (jButtonReportExport == null) {
			jButtonReportExport = new JButton();
			jButtonReportExport.setText("Export Report");
			jButtonReportExport.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							log.debug("Report Button Click!!! ");
							eventHandler.handleEvent(EventHandler.BTN_REPORT_EXPORTS);
						}
					}
					);			
		}
		return jButtonReportExport;
	}

	private EventHandler eventHandler = new EventHandler();
	
	class EventHandler {
		protected static final int BTN_REPORT_EXPORTS = 1;
		
		protected void handleEvent(int eventCode) {
			String projectName = ReportMediator.getInstance().getFirstSelectedProjectName();
			
			
			switch(eventCode) {
				case BTN_REPORT_EXPORTS:	
				{
					boolean pass = checkIdentifyCompletion(projectName);
					if(pass == false) {
						return;
					}

					JDlgExportReport.getInstance().setVisible(true);
					
					break;				
				}	
			}
		}
	}

	private boolean checkIdentifyCompletion(String projectName) {
		
		if(IdentifyQueue.getInstance().isIdentifyCompleted(projectName) == true) {
			return true;
		}
		
		String[] buttonList = {"Yes", "No"};
		int choice = JOptionPane.showOptionDialog(
				null, 
				"Identify transactions for "+projectName+" are on updating.\n" +
				"You must wait for completing all transactions to obtain accurate report.\n" + 
				"Are you sure to generate the report anyway?\n",
				"Warning",
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.WARNING_MESSAGE, 
				null, 
				buttonList,
				"Yes");
		
		if(choice == JOptionPane.YES_OPTION) {
			return true;
		} 
		
		return false;
	}

	public UIEntity generateBothReportUIEntity() {
		HashMap<String, BOMTableModel> boms = getJPanBOM().getBOMMap();
		UEBothBOM ueBothBOM = new UEBothBOM(
				boms,
				getJPanBOM().isAllFilesSelected()
				);
		
		return ueBothBOM;
	}

	public UIEntity exportUIEntity() {
		HashMap<String, BOMTableModel> boms = getJPanBOM().getBOMMap();
		UEBOM ueBOM = new UEBOM(
				boms,
				getJPanBOM().isAllFilesSelected()
				);
		
		
		return ueBOM;
	}

	public UIEntity generateSPDXReportUIEntity() {
		UESPDXBOM ueSPDXBOM = new UESPDXBOM();
		
		
		return ueSPDXBOM;
	}

	public void setAllFilesChecked() {
		getJPanBOM().setAllFilesChecked();
	}

	public void setReportButtonEnabled(boolean enabled) {
		this.getJButtonReportExport().setEnabled(enabled);
	}

}
