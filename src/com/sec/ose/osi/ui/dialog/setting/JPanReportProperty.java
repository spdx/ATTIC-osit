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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.ui.frm.main.report.file_explorer.JFCFolderExplorer;
import com.sec.ose.osi.util.Property;

/**
 * JPanReportProperty
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 *
 */
public class JPanReportProperty extends JPanel {
	private static Log log = LogFactory.getLog(JPanReportProperty.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelValue = null;
	private JPanel jPanelButtons = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JLabel jLabelDefaultReportLocation = null;
	private JLabel jLabelReciprocalLicense = null;
	private JLabel jLabelMajorLicense = null;
	private JTextField jTextFieldReciprocalLicense = null;
	private JTextField jTextFieldMajorLicense = null;
	
	private JPanel jPanelDefaultReportLocation = null;
	private JTextField jTextFieldDefaultReportLocation = null;
	private JButton jButtonFileExplorer = null;
	
	private String defaultReportLocation = null;
	private String reciprocalLicense = null;
	private String majorLicense = null;

	private Component mParent = null;
	
	public JPanReportProperty() {
		super();
		initialize();
		initValues();
	}
	
	public JPanReportProperty(Component pParent) {
		super();
		this.mParent = pParent;
		initialize();
		initValues();
	}

	private void initValues() {
		Property prop = Property.getInstance();
		defaultReportLocation = prop.getProperty(Property.DEFALT_REPORT_LOCATION);
		reciprocalLicense = prop.getProperty(Property.RECIPROCAL_LICENSE);
		majorLicense = prop.getProperty(Property.MAJOR_LICENSE);
		this.getJTextFieldDefaultReportLocation().setText(defaultReportLocation);
		this.getJTextFieldReciprocalLicense().setText(reciprocalLicense);
		this.getJTextFieldMajorLicense().setText(majorLicense);
	}
	
	public void setParent(Component pParent) {
		this.mParent = pParent;
	}
	
	public void setVisible(boolean pVisible) {
		if(pVisible == true) {
			initValues();
			
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 2;
		gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints9.gridwidth = 2;
		gridBagConstraints9.insets = new Insets(10, 10, 0, 10);
		gridBagConstraints9.anchor = GridBagConstraints.NORTH;
		gridBagConstraints9.gridy = 0;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.fill = GridBagConstraints.BOTH;
		gridBagConstraints7.gridwidth = 2;
		gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.weighty = 1.0;
		gridBagConstraints7.anchor = GridBagConstraints.CENTER;
		gridBagConstraints7.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getJPanelValue(), gridBagConstraints7);
		this.add(getJPanelButtons(), gridBagConstraints9);
	}

	/**
	 * This method initializes jPanelValue	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelValue() {
		if (jPanelValue == null) {
			
			jPanelValue = new JPanel();
			jPanelValue.setLayout(new GridBagLayout());
			jPanelValue.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelValue.setPreferredSize(new Dimension(400, 200)); 
			
			// Default Report Location

			jLabelDefaultReportLocation = new JLabel();
			jLabelDefaultReportLocation.setText("Default Report Location:");
			jLabelDefaultReportLocation.setHorizontalAlignment(SwingConstants.RIGHT);

			GridBagConstraints gridBagConstraintsjLabelDefaultReportLocation = new GridBagConstraints();
			gridBagConstraintsjLabelDefaultReportLocation.gridx = 0;
			gridBagConstraintsjLabelDefaultReportLocation.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelDefaultReportLocation.insets = new Insets(5, 0, 0, 0);
			gridBagConstraintsjLabelDefaultReportLocation.gridy = 1;
			
			GridBagConstraints gridBagConstraintsjpanelDefaultReportLocation = new GridBagConstraints();
			gridBagConstraintsjpanelDefaultReportLocation.fill = GridBagConstraints.BOTH;
			gridBagConstraintsjpanelDefaultReportLocation.gridy = 1;
			gridBagConstraintsjpanelDefaultReportLocation.weightx = 1.0;
			gridBagConstraintsjpanelDefaultReportLocation.insets = new Insets(10, 5, 5, 15);
			gridBagConstraintsjpanelDefaultReportLocation.gridx = 1;
			
			// reciprocal license
			
			jLabelReciprocalLicense = new JLabel();
			jLabelReciprocalLicense.setText("Reciprocal License (RED):");
			jLabelReciprocalLicense.setHorizontalAlignment(SwingConstants.RIGHT);
			
			GridBagConstraints gridBagConstraintsjLabelReciprocalLicense = new GridBagConstraints();
			gridBagConstraintsjLabelReciprocalLicense.gridx = 0;
			gridBagConstraintsjLabelReciprocalLicense.insets = new Insets(0, 15, 0, 0);
			gridBagConstraintsjLabelReciprocalLicense.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelReciprocalLicense.gridy = 2;
			jPanelValue.add(jLabelDefaultReportLocation, gridBagConstraintsjLabelDefaultReportLocation);
			jPanelValue.add(getJPanelDefaultReportLocation(), gridBagConstraintsjpanelDefaultReportLocation);
			jPanelValue.add(jLabelReciprocalLicense, gridBagConstraintsjLabelReciprocalLicense);

			GridBagConstraints gridBagConstraintsgetJTextFieldReciprocalLicense = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldReciprocalLicense.gridx = 1;
			gridBagConstraintsgetJTextFieldReciprocalLicense.weightx = 1.0;
			gridBagConstraintsgetJTextFieldReciprocalLicense.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldReciprocalLicense.insets = new Insets(5, 5, 5, 15);
			gridBagConstraintsgetJTextFieldReciprocalLicense.gridy = 2;
			jPanelValue.add(getJTextFieldReciprocalLicense(), gridBagConstraintsgetJTextFieldReciprocalLicense);
			
			
			// major license
			
			jLabelMajorLicense = new JLabel();
			jLabelMajorLicense.setText("Major License (ORANGE):");
			jLabelMajorLicense.setHorizontalAlignment(SwingConstants.RIGHT);
			
			GridBagConstraints gridBagConstraintsjLabelMajorLicense = new GridBagConstraints();
			gridBagConstraintsjLabelMajorLicense.gridx = 0;
			gridBagConstraintsjLabelMajorLicense.insets = new Insets(0, 15, 0, 0);
			gridBagConstraintsjLabelMajorLicense.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelMajorLicense.gridy = 3;
			jPanelValue.add(jLabelMajorLicense, gridBagConstraintsjLabelMajorLicense);
			
			GridBagConstraints gridBagConstraintsgetJTextFieldMajorLicense = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldMajorLicense.gridx = 1;
			gridBagConstraintsgetJTextFieldMajorLicense.weightx = 1.0;
			gridBagConstraintsgetJTextFieldMajorLicense.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldMajorLicense.insets = new Insets(5, 5, 5, 15);
			gridBagConstraintsgetJTextFieldMajorLicense.gridy = 3;
			jPanelValue.add(getJTextFieldMajorLicense(), gridBagConstraintsgetJTextFieldMajorLicense);

			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.weightx = 0.0;
			gridBagConstraints3.gridy = 4;
			JLabel jLabelEmpty = new JLabel();
			jLabelEmpty.setText("");
			jPanelValue.add(jLabelEmpty, gridBagConstraints3);
		}
		return jPanelValue;
	}

	private JTextField getJTextFieldReciprocalLicense() {
		if (jTextFieldReciprocalLicense == null) {
			jTextFieldReciprocalLicense = new JTextField();
			jTextFieldReciprocalLicense.setEditable(true);
			jTextFieldReciprocalLicense.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_RECIPROCAL);
				}
			});
		}
		return jTextFieldReciprocalLicense;
	}
	
	private JTextField getJTextFieldMajorLicense() {
		if (jTextFieldMajorLicense == null) {
			jTextFieldMajorLicense = new JTextField();
			jTextFieldMajorLicense.setEditable(true);
			jTextFieldMajorLicense.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_MAJOR);
				}
			});
		}
		return jTextFieldMajorLicense;
	}
	
	/**
	 * This method initializes jPanelButtons	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 0;
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new GridBagLayout());
			jPanelButtons.add(getJButtonOK(), gridBagConstraints1);
			jPanelButtons.add(getJButtonCancel(), gridBagConstraints2);
		}
		return jPanelButtons;
	}

	/**
	 * This method initializes jButtonSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
			jButtonOK.setPreferredSize(new Dimension(73, 28));
			jButtonOK.setEnabled(false);
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() - Save"); 
					mEventHandler.handle(EventHandler.BT_SAVE);
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() - Cancel"); 
					mEventHandler.handle(EventHandler.BT_CANCEL);
				}
				

			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jPanelDefaultReportLocation	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelDefaultReportLocation() {
		if (jPanelDefaultReportLocation == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = 0;
			jPanelDefaultReportLocation = new JPanel();
			jPanelDefaultReportLocation.setLayout(new GridBagLayout());
			jPanelDefaultReportLocation.add(getJTextFieldDefaultReportLocation(), gridBagConstraints4);
			jPanelDefaultReportLocation.add(getJButtonDefaultReportLocation(), gridBagConstraints5);
		}
		return jPanelDefaultReportLocation;
	}

	/**
	 * This method initializes jTextFieldLocation	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDefaultReportLocation() {
		if (jTextFieldDefaultReportLocation == null) {
			jTextFieldDefaultReportLocation = new JTextField();
			jTextFieldDefaultReportLocation.setEditable(false);
			jTextFieldDefaultReportLocation.setPreferredSize(new Dimension(170, 22));
			jTextFieldDefaultReportLocation.setBackground(Color.white);
			jTextFieldDefaultReportLocation.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					log.debug("caretUpdate() - Default Report Location"); 
					mEventHandler.handle(EventHandler.TF_REPORT);
				}
			});
		}
		return jTextFieldDefaultReportLocation;
	}

	/**
	 * This method initializes jButtonFileExplorer	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDefaultReportLocation() {
		if (jButtonFileExplorer == null) {
			jButtonFileExplorer = new JButton();
			jButtonFileExplorer.setText("...");
			jButtonFileExplorer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() - File Explorer"); 
					mEventHandler.handle(EventHandler.BT_DEFAULT_REPORT_LOCATION);
				}
			});
		}
		return jButtonFileExplorer;
	}
	
	private EventHandler mEventHandler = new EventHandler();
	
	class EventHandler {

		protected static final int TF_LOCAL			= 1;
		protected static final int TF_REPORT		= 2;
		protected static final int TF_AUTHOR 		= 3;
		protected static final int TF_GROUP 		= 4;
		protected static final int TF_MAJOR 		= 5;
		protected static final int TF_RECIPROCAL 	= 6;


		
		protected static final int BT_SAVE 					= 11;
		protected static final int BT_CANCEL 				= 12;
		protected static final int BT_DEFAULT_REPORT_LOCATION	= 13;
		
		
		protected EventHandler() {

		}
		
		protected void handle(int pEvent) {
			
			switch(pEvent) {
			
				case BT_DEFAULT_REPORT_LOCATION:
					String strCurDir = getJTextFieldDefaultReportLocation().getText();
					
					JFCFolderExplorer explorer = JFCFolderExplorer.getInstance();
					int dialogResult = explorer.showSaveDialog(strCurDir);
					
					if(dialogResult == JFCFolderExplorer.APPROVE_OPTION) {

						File selected = explorer.getSelectedFile();
						
						if(selected.exists() == false) {
			        		int choice = JOptionPane.showOptionDialog(
			        				null, 
			        				"\""+selected+"\" is not existed folder. Do you want to create it now?",
			        				"Alarm", 
			        				JOptionPane.YES_NO_OPTION, 
			        				JOptionPane.QUESTION_MESSAGE, 
			        				null, 
			        				null,
			        				null);
			        		if (choice == JOptionPane.YES_OPTION) {
			        			selected.mkdirs();
			        		} else {
						    	return;
			        		}
			        	}
						
						getJTextFieldDefaultReportLocation().setText(selected.getPath());
						
					}
					
					break;
					
				case TF_LOCAL:
				case TF_REPORT:
				case TF_AUTHOR:
				case TF_GROUP:
				case TF_MAJOR:
				case TF_RECIPROCAL:
					
					if(!getJTextFieldDefaultReportLocation().getText().equals(defaultReportLocation)) {
						jButtonOK.setEnabled(true);
					} else if (!getJTextFieldReciprocalLicense().getText().equals(reciprocalLicense)) {
						jButtonOK.setEnabled(true);
					} else if (!getJTextFieldMajorLicense().getText().equals(majorLicense)) {
						jButtonOK.setEnabled(true);
					} else {
						jButtonOK.setEnabled(false);
					}
					break;

				case BT_CANCEL:
					initValues();
					mParent.setVisible(false);
					break;
					
				case BT_SAVE:
					defaultReportLocation = getJTextFieldDefaultReportLocation().getText().trim();
					reciprocalLicense = getJTextFieldReciprocalLicense().getText().trim();
					majorLicense = getJTextFieldMajorLicense().getText().trim();
					Property.getInstance().setProperty(Property.DEFALT_REPORT_LOCATION, defaultReportLocation);
					Property.getInstance().setProperty(Property.RECIPROCAL_LICENSE, reciprocalLicense);
					Property.getInstance().setProperty(Property.MAJOR_LICENSE, majorLicense);
					
					log.debug("save report setting");
					
					jButtonOK.setEnabled(false);
					mParent.setVisible(false);
					break;
					
			}
		}
	}

}
