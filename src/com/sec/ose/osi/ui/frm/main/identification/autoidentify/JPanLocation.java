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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.util.Property;

/**
 * JPanLocation
 * @author sjh.yoo, ytaek.kim
 * 
 */
public class JPanLocation extends JPanel {
	private static final long serialVersionUID = -2323534863889500922L;
	JPanImportSPDX parent = null;
	
	private Color invalidColor = new Color(255,233,241);
	private Color validColor = new Color(220,242,255);

	public JPanLocation(JPanImportSPDX parent, String path) {
		this(parent);
		if(path != null) {
			setSPDXLocation(path);
		}
	}
	
	public JPanLocation(JPanImportSPDX parent) {
		this.parent = parent;
		this.setLayout(new GridBagLayout());

		ImageIcon del_icon = new ImageIcon(WindowUtil.class.getResource("btn_delete.gif"));
		JLabel jLabelIdentify = new JLabel(del_icon);
		jLabelIdentify.addMouseListener(
				new MouseAdapter() {
					public void mouseClicked(MouseEvent e)  {
						removeJPanSPDXLocation();
					}
				}
		);
		
		JButton jButtonCreateExplorer = new JButton();
		jButtonCreateExplorer.setText("...");
		jButtonCreateExplorer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		});
		
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints12.gridx = 1;
		gridBagConstraints12.gridy = 0;
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints13.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints13.gridx = 2;
		gridBagConstraints13.gridy = 0;

		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.insets = new Insets(0, 13, 0, 0);		//Insets(int top, int left, int bottom, int right) 
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 1;
		gridBagConstraints21.gridwidth = 3;

		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;		//Insets(int top, int left, int bottom, int right) 
		gridBagConstraints31.insets = new Insets(0, 13, 0, 0);
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.gridy = 2;
		gridBagConstraints31.gridwidth = 3;
		
		this.add(jLabelIdentify, gridBagConstraints11);
		this.add(getJTextFieldReportLocation(), gridBagConstraints12);
		this.add(getJButtonCreateExplorer(), gridBagConstraints13);
		this.add(getJLableCreatorInfo(), gridBagConstraints21);
		if(!getJLableCreatorInfo().getText().equals(CheckMsg)) {
			this.add(getJLableCreatedDateInfo(), gridBagConstraints31);
		}

	}

	protected void removeJPanSPDXLocation() {
		parent.removeJPanSPDXLocation(this);
	}

	JTextField jTextFieldReportLocation = null;
	protected JTextField getJTextFieldReportLocation() {
		if (jTextFieldReportLocation == null) {
			jTextFieldReportLocation = new JTextField();
			jTextFieldReportLocation.setPreferredSize(new Dimension(330, 22));
			jTextFieldReportLocation.setEditable(false);
			jTextFieldReportLocation.setBackground(invalidColor);
			
			jTextFieldReportLocation.setText(Property.getInstance().getProperty(Property.DEFALT_REPORT_LOCATION));
			
		}
		return jTextFieldReportLocation;
	}

	JButton jButtonCreateExplorer = null;
	private JButton getJButtonCreateExplorer() {
		if (jButtonCreateExplorer == null) {
			jButtonCreateExplorer = new JButton();
			jButtonCreateExplorer.setPreferredSize(new Dimension(30, 22));
			jButtonCreateExplorer.setText("...");
			jButtonCreateExplorer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser mFileChooser = new JFileChooser();

					mFileChooser.setMultiSelectionEnabled(true);
					mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					mFileChooser.setAcceptAllFileFilterUsed(false);
					mFileChooser.setFileFilter(new FileNameExtensionFilter("SPDX Document", "rdf"));
					
					String strDir = Property.getInstance().getProperty(Property.DEFALT_REPORT_LOCATION);
					File curDir = new File(strDir); 
					
					if(curDir.exists())
						mFileChooser.setCurrentDirectory(curDir);

					mFileChooser.setDialogTitle("Select SPDX Documents");
					int result = mFileChooser.showDialog(parent, null);
				    
					if(result == JFileChooser.APPROVE_OPTION) {
						File [] selectedFiles = mFileChooser.getSelectedFiles();
						if(selectedFiles.length > 0){
				        	addJPanSPDXLocation(selectedFiles);
						}
					}
				}
			});
		}
		return jButtonCreateExplorer;
	}
	
	private JLabel jlableCreatorInfo = null; 
	public JLabel getJLableCreatorInfo() {
		if(jlableCreatorInfo == null) {
			jlableCreatorInfo = new JLabel();
		}
		return jlableCreatorInfo;
	}

	private JLabel jlableCreatedDateInfo = null; 
	public JLabel getJLableCreatedDateInfo() {
		if(jlableCreatedDateInfo == null) {
			jlableCreatedDateInfo = new JLabel();
		}
		return jlableCreatedDateInfo;
	}

	public void setSPDXLocation(String path) {
		getJTextFieldReportLocation().setText(path);
		isValidSPDXLocation();
		parent.checkValid();
	}
	
	private boolean isValidSPDXLocation() {
		File tmpFile = new File(getJTextFieldReportLocation().getText());
		if(tmpFile.exists() && tmpFile.isFile()) {
			getJTextFieldReportLocation().setBackground(validColor);
			setCreatorInfo();
			return true;
		} else {
			getJTextFieldReportLocation().setBackground(invalidColor);
			setCreatorInfo();
			return false;
		}
	}
	
	private final String CheckMsg = "Please check your SPDX Document format";
	private void setCreatorInfo() {
		SPDXPackageDTO tmpPackageInfo = SPDXPackageInfoParser.getSPDXPackageInfo(getJTextFieldReportLocation().getText());
		if(tmpPackageInfo != null) {
			getJLableCreatorInfo().setText(tmpPackageInfo.getOrganization()+" , "+tmpPackageInfo.getPerson());
			getJLableCreatedDateInfo().setText(tmpPackageInfo.getName()+" , "+tmpPackageInfo.getCreated());
		} else {
			getJLableCreatorInfo().setText(CheckMsg);
		}
	}
	
	private boolean isNewSPDXLocation(String path) {
		return parent.isNewSPDXLocation(path);
	}

	protected void addJPanSPDXLocation(File[] list) {
		ArrayList<JPanLocation> JPanSPDXLocationList = parent.getJPanSPDXLocationList();
		setSPDXLocation(list[0].getPath());
		for(int i=1;i<list.length;i++) {
			String tmpPath = list[i].getPath();
			if(isNewSPDXLocation(tmpPath)) {
				boolean isRegister = false;
				for(JPanLocation tmpJPanLocation:JPanSPDXLocationList) {
					if(tmpJPanLocation.isValidSPDXLocation()) {
						continue;
					} else {
						tmpJPanLocation.setSPDXLocation(tmpPath);
						isRegister = true;
						break;
					}
				}
				if(!isRegister) {
					parent.addJPanSPDXLocation(tmpPath);
				}
			}
		}
	}

	public String getPath() {
		return getJTextFieldReportLocation().getText();
	}
}
