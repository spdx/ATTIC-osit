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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyOptions;
import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * JPanImportSPDX
 * @author sjh.yoo, ytaek.kim, hankido.lee
 * 
 */
public class JPanImportSPDX extends JPanel{
	private static Log log = LogFactory.getLog(JPanImportSPDX.class);
	
	private static final long serialVersionUID = 1034848064468759280L;
	
	private JPanel jPanMain = null;
	private ArrayList<JPanLocation> JPanSPDXLocationList = new ArrayList<JPanLocation>(1);

	public JPanImportSPDX() {
		initialize();
	}
	
	private void initialize () {
		BorderLayout layout = new BorderLayout();
		
		this.setLayout(layout);
		this.add(new JLabel("           "), BorderLayout.NORTH);
		this.add(getJPanMain(), BorderLayout.CENTER);
	}
	
	private JPanel getJPanMain() {		
		if(jPanMain == null) {
			jPanMain = new JPanel();

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			
			jPanMain.add(getJScrollPaneImportSPDX(), gridBagConstraints1);
			
			jPanMain.add(getJPanelForOption(), gridBagConstraints2);
			jPanMain.add(getJButtonPanel(), gridBagConstraints3);
			
			addJPanSPDXLocation(null);
		}
		
		return jPanMain;
	}

	private JDlgImportSPDX jDlgImportSPDX;
	public void setParent(JDlgImportSPDX dialog) {
		this.jDlgImportSPDX = dialog;
	}
	

	private JScrollPane jScrollPaneImportSPDX = null;
	private JScrollPane getJScrollPaneImportSPDX() {
		if (jScrollPaneImportSPDX == null) {
			jScrollPaneImportSPDX = new JScrollPane();
			jScrollPaneImportSPDX.setPreferredSize(new Dimension(410, 200));
			jScrollPaneImportSPDX.setViewportView(getJPanelImportSPDX());
			jScrollPaneImportSPDX.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPaneImportSPDX;
	}
	
	private JPanel jPanelImportSPDX = null;
	private JPanel getJPanelImportSPDX() {
		if(jPanelImportSPDX == null) {
			jPanelImportSPDX = new JPanel();
			jPanelImportSPDX.setLayout(new GridBagLayout());
			
		}
		return jPanelImportSPDX;
	}
	private void updateImportSPDXList() {
		getJPanelImportSPDX().removeAll();
		int index = 0;
		for(index = 0;index < getJPanSPDXLocationList().size();index++) {
			JPanLocation tmpLocation = getJPanSPDXLocationList().get(index);
			if(tmpLocation != null) {
				GridBagConstraints tmpGridBagConstraints = new GridBagConstraints();
				tmpGridBagConstraints.insets = new Insets(5, 5, 0, 5);	//Insets(int top, int left, int bottom, int right) 
				tmpGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
				tmpGridBagConstraints.gridx = 0;
				tmpGridBagConstraints.gridy = index;
				
				getJPanelImportSPDX().add(tmpLocation,tmpGridBagConstraints);
			}
		}
		
		GridBagConstraints tmpGridBagConstraints = new GridBagConstraints();
		tmpGridBagConstraints.fill = GridBagConstraints.BOTH;
		tmpGridBagConstraints.gridx = 0;
		tmpGridBagConstraints.gridy = index;
		tmpGridBagConstraints.weighty = 1.0;
		getJPanelImportSPDX().add(new JLabel(),tmpGridBagConstraints);

		checkValid();
		
		getJPanelImportSPDX().updateUI();
	}
	
	public boolean isNewSPDXLocation(String path) {
		for(JPanLocation tmpJPanLocation:getJPanSPDXLocationList()) {
			if(path.equals(tmpJPanLocation.getPath())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean checkValid() {
		for(JPanLocation tmpJPanLocation:getJPanSPDXLocationList()) {
			File tmpFile = new File(tmpJPanLocation.getPath());
			if(!tmpFile.exists() || !tmpFile.isFile()) {
				getJButtonOK().setEnabled(false);
				return false;
			}
		}
		getJButtonOK().setEnabled(true);
		return true;
	}
	
	public ArrayList<JPanLocation> getJPanSPDXLocationList() {
		if(JPanSPDXLocationList == null) JPanSPDXLocationList = new ArrayList<JPanLocation>(1);
		return JPanSPDXLocationList;
	}
	
	public void addJPanSPDXLocation(String path) {
		JPanLocation newJPanLocation = new JPanLocation(this, path);
		getJPanSPDXLocationList().add(newJPanLocation);
		updateImportSPDXList();
	}

	public void removeJPanSPDXLocation(JPanLocation target) {
		if(getJPanSPDXLocationList().size() > 1) {
			getJPanSPDXLocationList().remove(target);
			updateImportSPDXList();
		}
	}
	
	/** Option Panel **/
	private JCheckBox jCheckSamePath = null;
	private JCheckBox jCheckRecentInfo = null;
	private JCheckBox jCheckOverwrite = null;

	private JPanel jPanelForOption = null;
	private JPanel getJPanelForOption() {
		if (jPanelForOption == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.insets = new Insets(0, 0, 0, 15);
			jPanelForOption = new JPanel();
			jPanelForOption.setLayout(new GridBagLayout());
			jPanelForOption.setBorder(BorderFactory.createTitledBorder(null, "Identify Option", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForOption.add(getJPanelOption(), gridBagConstraints);
		}
		return jPanelForOption;
	}
	
	private JPanel jPanelOption = null;
	private JPanel getJPanelOption() {
		if (jPanelOption == null) {
			jPanelOption = new JPanel();
			jPanelOption.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(0, 5, 0, 5);
			jPanelOption.add(getJCheckSamePath(), gridBagConstraints);

		}
		return jPanelOption;
	}

	private JCheckBox getJCheckSamePath() {
		if(jCheckSamePath == null) {
			jCheckSamePath = new JCheckBox("Use the exactly same path file info if duplicated hash files exist.");
			jCheckSamePath.setSelected(true);
		}
		return jCheckSamePath;
	}

	private JCheckBox getJCheckRecentInfo() {
		if(jCheckRecentInfo == null) {
			jCheckRecentInfo = new JCheckBox("Use SPDX document recently created if project already exists.");
			jCheckRecentInfo.setSelected(true);
		}
		return jCheckRecentInfo;
	}

	private JCheckBox getJCheckOverwrite() {
		if(jCheckOverwrite == null) {
			jCheckOverwrite = new JCheckBox("Overwrite if the file already identified.");
			jCheckOverwrite.setSelected(true);
		}
		return jCheckOverwrite;
	}

	/** Button Panel **/
	private JPanel jButtonPanel = null;	
	private JButton jButtonAdd = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JPanel getJButtonPanel() {
		if(jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(10, 0, 10, 50);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jButtonPanel.add(getJButtonAdd(), gridBagConstraints);

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(10, 50, 10, 5);
			gridBagConstraints1.gridx = 3;
			gridBagConstraints1.gridy = 0;
			jButtonPanel.add(getJButtonOK(), gridBagConstraints1);
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(10, 5, 10, 0);
			gridBagConstraints2.gridx = 4;
			gridBagConstraints2.gridy = 0;
			jButtonPanel.add(getJButtonCancel(), gridBagConstraints2);

		}
		return jButtonPanel;
	}

	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("   Add   ");
			jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addJPanSPDXLocation(null);
				}
			});
		}
		return jButtonAdd;
	}
	
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("     OK     ");
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					launchSPDXAutoIdentify();
					jDlgImportSPDX.setVisible(false);
				}
			});
			jButtonOK.setEnabled(false);
		}
		return jButtonOK;
	}
	
	private void launchSPDXAutoIdentify() {
		AutoIdentifyOptions autoIdentifyOptions = new AutoIdentifyOptions();
		autoIdentifyOptions.setIdentifyWhenExistingOnlyOneSamePathFile(getJCheckSamePath().isSelected());
		autoIdentifyOptions.setIdentifyRecentCreatedPackageInfo(getJCheckRecentInfo().isSelected());
		autoIdentifyOptions.setOverwrite(getJCheckOverwrite().isSelected());
		
		List<String> filePathList = new ArrayList<String>();		
		for(JPanLocation tmpLocation:getJPanSPDXLocationList())
			filePathList.add(tmpLocation.getPath());
		
		UESPDXAutoIdentify ue = new UESPDXAutoIdentify();
		ue.setProjectName(IdentifyMediator.getInstance().getSelectedProjectName());
		ue.setAutoIdentifyOptions(autoIdentifyOptions);
		ue.setSPDXFilePathList(filePathList);
		
		UIResponseObserver observer = UserRequestHandler.getInstance().handle(UserRequestHandler.SPDX_AUTO_IDENTIFY, ue, true, false);
		
		SPDXAutoIdentifyResult autoIdentifyResult = new SPDXAutoIdentifyResult((AutoIdentifyResult)observer.getReturnValue());
		
		// Log to file
		String autoIdentifyResultLogFilePath = Property.FOLDER_LOGS + File.separator + "OSI_AI_RESULT_" + DateUtil.getCurrentTime("%1$tY%1$tm%1$td_%1$tH%1$tM") + ".log";
		File logFile = new File(autoIdentifyResultLogFilePath);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(logFile);
			pw.println(autoIdentifyResult.getSummary());
			pw.println("-------------------------------------------------------");
			pw.println(autoIdentifyResult.getSourceSPDXPackagesInfo());
			pw.println("-------------------------------------------------------");
			pw.println(autoIdentifyResult.getFailDetails());
			pw.close();
		} catch (FileNotFoundException e) {
			log.error("Cannot write autoIdentifyResult to " + autoIdentifyResultLogFilePath + ", " + e.getMessage());
		} finally {
			if (pw != null) pw.close();
		}
		
		// show result
		JDlgIdentifyResult.getInstance().setAutoIdentifyResult(autoIdentifyResult);
		JDlgIdentifyResult.getInstance().setVisible(true);
	}
	
	private JButton getJButtonCancel() {
		if(jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jDlgImportSPDX.setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}
}
