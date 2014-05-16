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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.ui_related.UserRequestHandler;

/**
 * JPanProxySetting
 * 
 * @author hankido.lee
 *
 */
public class JPanProxySetting extends JPanel {
	private static Log log = LogFactory.getLog(JPanProxySetting.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelValue = null;
	private JPanel jPanelButtons = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JLabel jLabelProxyServerIP = null;
	private JLabel jLabelProxyServerPort = null;
	private JTextField jTextFieldProxyServerIP = null;
	private JTextField jTextFieldProxyServerPort = null;
	
	private Component mParent = null;
	private UEProxySetting mUEProxySetting = new UEProxySetting();
	
	public JPanProxySetting() {
		super();
		initialize();
		initValues();
	}
	
	public JPanProxySetting(Component pParent) {
		super();
		this.mParent = pParent;
		initialize();
		initValues();
	}
	
	public void setParent(Component pParent) {
		this.mParent = pParent;
	}

	private void initValues() {
		this.getJTextFieldProxyServerIP().setText(mUEProxySetting.getProxyServerIP());
		this.getJTextFieldProxyServerPort().setText(mUEProxySetting.getProxyServerPort());
	}
	
	public void setVisible(boolean pVisible) {
		if(pVisible == true) {
			initValues();
			
		}
	}

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
		gridBagConstraints7.anchor = GridBagConstraints.CENTER;
		gridBagConstraints7.weighty = 1.0;
		gridBagConstraints7.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getJPanelValue(), gridBagConstraints7);
		this.add(getJPanelButtons(), gridBagConstraints9);
	}

	private JPanel getJPanelValue() {
		if (jPanelValue == null) {
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.weightx = 0.0;
			gridBagConstraints3.gridy = 4;
			jLabelEmpty = new JLabel();
			jLabelEmpty.setText("");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 3;
			jPanelValue = new JPanel();
			jPanelValue.setLayout(new GridBagLayout());
			jPanelValue.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelValue.setPreferredSize(new Dimension(400, 200)); 
			
			jLabelProxyServerIP = new JLabel();
			jLabelProxyServerIP.setText("Server IP : ");
			jLabelProxyServerIP.setHorizontalAlignment(SwingConstants.RIGHT);
			
			GridBagConstraints gridBagConstraintsjLabelReciprocalLicense = new GridBagConstraints();
			gridBagConstraintsjLabelReciprocalLicense.gridx = 0;
			gridBagConstraintsjLabelReciprocalLicense.insets = new Insets(10, 20, 5, 0);
			gridBagConstraintsjLabelReciprocalLicense.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelReciprocalLicense.weightx = 0.0;
			gridBagConstraintsjLabelReciprocalLicense.weighty = 0.0;
			gridBagConstraintsjLabelReciprocalLicense.gridy = 1;
			GridBagConstraints gridBagConstraintsgetJTextFieldReciprocalLicense = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldReciprocalLicense.gridx = 1;
			gridBagConstraintsgetJTextFieldReciprocalLicense.weightx = 1.0;
			gridBagConstraintsgetJTextFieldReciprocalLicense.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldReciprocalLicense.insets = new Insets(10, 5, 5, 130);
			gridBagConstraintsgetJTextFieldReciprocalLicense.gridwidth = 2;
			gridBagConstraintsgetJTextFieldReciprocalLicense.gridy = 1;
			
			
			jLabelProxyServerPort = new JLabel();
			jLabelProxyServerPort.setText("Port : ");
			jLabelProxyServerPort.setHorizontalAlignment(SwingConstants.RIGHT);
			
			GridBagConstraints gridBagConstraintsjLabelMajorLicense = new GridBagConstraints();
			gridBagConstraintsjLabelMajorLicense.gridx = 0;
			gridBagConstraintsjLabelMajorLicense.insets = new Insets(0, 20, 0, 0);
			gridBagConstraintsjLabelMajorLicense.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelMajorLicense.weighty = 0.0;
			gridBagConstraintsjLabelMajorLicense.gridy = 2;
			
			GridBagConstraints gridBagConstraintsgetJTextFieldMajorLicense = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldMajorLicense.gridx = 1;
			gridBagConstraintsgetJTextFieldMajorLicense.weightx = 1.0;
			gridBagConstraintsgetJTextFieldMajorLicense.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldMajorLicense.insets = new Insets(5, 5, 5, 220);
			gridBagConstraintsgetJTextFieldMajorLicense.weighty = 0.0;
			gridBagConstraintsgetJTextFieldMajorLicense.anchor = GridBagConstraints.CENTER;
			gridBagConstraintsgetJTextFieldMajorLicense.gridy = 2;
			jPanelValue.add(jLabelProxyServerIP, gridBagConstraintsjLabelReciprocalLicense);
			jPanelValue.add(getJTextFieldProxyServerIP(), gridBagConstraintsgetJTextFieldReciprocalLicense);
			jPanelValue.add(jLabelProxyServerPort, gridBagConstraintsjLabelMajorLicense);
			jPanelValue.add(getJTextFieldProxyServerPort(), gridBagConstraintsgetJTextFieldMajorLicense);
			jPanelValue.add(getJPanel(), gridBagConstraints2);
			jPanelValue.add(jLabelEmpty, gridBagConstraints3);
			
			GridBagConstraints gridBagConstraintsgetJTextFieldAuthorName = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldAuthorName.gridx = 1;
			gridBagConstraintsgetJTextFieldAuthorName.weightx = 1.0;
			gridBagConstraintsgetJTextFieldAuthorName.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldAuthorName.insets = new Insets(5, 5, 5, 15);
			gridBagConstraintsgetJTextFieldAuthorName.gridy =4;
			
			GridBagConstraints gridBagConstraintsgetJTextFieldGroupName = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldGroupName.gridx = 1;
			gridBagConstraintsgetJTextFieldGroupName.weightx = 1.0;
			gridBagConstraintsgetJTextFieldGroupName.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldGroupName.insets = new Insets(5, 5, 5, 15);
			gridBagConstraintsgetJTextFieldGroupName.gridy = 5;
			
		}
		return jPanelValue;
	}

	private JTextField getJTextFieldProxyServerIP() {
		if (jTextFieldProxyServerIP == null) {
			jTextFieldProxyServerIP = new JTextField();
			jTextFieldProxyServerIP.setEditable(true);
			jTextFieldProxyServerIP.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_PROXYSERVERIP);
				}
			});
		}
		return jTextFieldProxyServerIP;
	}
	
	private JTextField getJTextFieldProxyServerPort() {
		if (jTextFieldProxyServerPort == null) {
			jTextFieldProxyServerPort = new JTextField();
			jTextFieldProxyServerPort.setEditable(true);
			jTextFieldProxyServerPort.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_PROXYSERVERPORT);
				}
			});
		}
		return jTextFieldProxyServerPort;
	}
	
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new GridBagLayout());
			jPanelButtons.add(getJButtonOK(), gridBagConstraints);
			jPanelButtons.add(getJButtonCancel(), gridBagConstraints1);
		}
		return jPanelButtons;
	}

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


	private EventHandler mEventHandler = new EventHandler();
	private JPanel jPanel = null;
	private JLabel jLabelEmpty = null;
	class EventHandler {

		protected static final int TF_PROXYSERVERIP 	= 1;
		protected static final int TF_PROXYSERVERPORT 	= 2;
		
		protected static final int BT_SAVE 					= 11;
		protected static final int BT_CANCEL 				= 12;
		
		protected EventHandler() {
		}
		
		protected void handle(int pEvent) {
			
			switch(pEvent) {
			
				case TF_PROXYSERVERIP:
				case TF_PROXYSERVERPORT:
					
					if (jTextFieldProxyServerIP != null &&
							jTextFieldProxyServerIP.getText().equals(mUEProxySetting.getProxyServerIP())==false
							) {
						jButtonOK.setEnabled(true);
					} else if (jTextFieldProxyServerPort != null &&
							jTextFieldProxyServerPort.getText().equals(mUEProxySetting.getProxyServerPort())==false
							) {
						jButtonOK.setEnabled(true);
					}
					
					else {
						jButtonOK.setEnabled(false);
					}
					break;

					
				case BT_CANCEL:
					initValues();
					mParent.setVisible(false);
					break;
					
					
				case BT_SAVE:
					
					UEProxySetting ue = new UEProxySetting();
					
					ue.setProxyServerIP(jTextFieldProxyServerIP.getText());
					ue.setProxyServerPort(jTextFieldProxyServerPort.getText());
					
					mUEProxySetting = ue;
					UserRequestHandler.getInstance().handle(UserRequestHandler.SAVE_PROXY_SETTING, mUEProxySetting);
					log.debug("save");
					jButtonOK.setEnabled(false);
					mParent.setVisible(false);
					break;
					
			}
		}
	}

	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}
}
