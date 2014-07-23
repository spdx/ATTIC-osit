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
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.util.ProxyUtil;

/**
 * JPanProxySetting
 * 
 * @author hankido.lee, sjh.yoo
 *
 */
public class JPanProxySetting extends JPanel {
	private static Log log = LogFactory.getLog(JPanProxySetting.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelValue = null;
	private JPanel jPanelButtons = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	
	private JTextField jTextFieldProxyHost = null;
	private JTextField jTextFieldProxyPort = null;
	private JTextField jTextFieldProxyBypass = null;
	
	private String proxyHost = null;
	private String proxyPort = null;
	private String proxyBypass = null;
	
	private Component mParent = null;
	
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
		proxyHost = ProxyUtil.getInstance().getProxyHost();
		proxyPort = ProxyUtil.getInstance().getProxyPort();
		proxyBypass = ProxyUtil.getInstance().getProxyBypass();
		this.getJTextFieldProxyHost().setText(proxyHost);
		this.getJTextFieldProxyPort().setText(proxyPort);
		this.getJTextFieldProxyBypass().setText(proxyBypass);
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
			jPanelValue.add(jLabelEmpty, gridBagConstraints3);

			JLabel jLabelProxyHost = new JLabel("Proxy Host : ",JLabel.RIGHT);
			GridBagConstraints gridBagConstraintsjLabelProxyHost = new GridBagConstraints();
			gridBagConstraintsjLabelProxyHost.gridx = 0;
			gridBagConstraintsjLabelProxyHost.insets = new Insets(10, 20, 5, 0);
			gridBagConstraintsjLabelProxyHost.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelProxyHost.weightx = 0.0;
			gridBagConstraintsjLabelProxyHost.weighty = 0.0;
			gridBagConstraintsjLabelProxyHost.gridy = 1;
			jPanelValue.add(jLabelProxyHost, gridBagConstraintsjLabelProxyHost);
			
			GridBagConstraints gridBagConstraintsgetJTextFieldProxyHost = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldProxyHost.gridx = 1;
			gridBagConstraintsgetJTextFieldProxyHost.weightx = 1.0;
			gridBagConstraintsgetJTextFieldProxyHost.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldProxyHost.insets = new Insets(10, 5, 5, 100);
			gridBagConstraintsgetJTextFieldProxyHost.gridwidth = 2;
			gridBagConstraintsgetJTextFieldProxyHost.gridy = 1;
			jPanelValue.add(getJTextFieldProxyHost(), gridBagConstraintsgetJTextFieldProxyHost);
			
			JLabel jLabelProxyPort = new JLabel("Proxy Port : ",JLabel.RIGHT);
			GridBagConstraints gridBagConstraintsjLabelProxyPort = new GridBagConstraints();
			gridBagConstraintsjLabelProxyPort.gridx = 0;
			gridBagConstraintsjLabelProxyPort.insets = new Insets(0, 20, 0, 0);
			gridBagConstraintsjLabelProxyPort.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelProxyPort.weighty = 0.0;
			gridBagConstraintsjLabelProxyPort.gridy = 2;
			jPanelValue.add(jLabelProxyPort, gridBagConstraintsjLabelProxyPort);
			
			GridBagConstraints gridBagConstraintsgetJTextFieldProxyPort = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldProxyPort.gridx = 1;
			gridBagConstraintsgetJTextFieldProxyPort.weightx = 1.0;
			gridBagConstraintsgetJTextFieldProxyPort.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldProxyPort.insets = new Insets(5, 5, 5, 220);
			gridBagConstraintsgetJTextFieldProxyPort.weighty = 0.0;
			gridBagConstraintsgetJTextFieldProxyPort.anchor = GridBagConstraints.CENTER;
			gridBagConstraintsgetJTextFieldProxyPort.gridy = 2;
			jPanelValue.add(getJTextFieldProxyPort(), gridBagConstraintsgetJTextFieldProxyPort);
			
			JLabel jLabelProxyBypass = new JLabel("Proxy Bypass : ",JLabel.RIGHT);
			GridBagConstraints gridBagConstraintsjLabelProxyBypass = new GridBagConstraints();
			gridBagConstraintsjLabelProxyBypass.gridx = 0;
			gridBagConstraintsjLabelProxyBypass.insets = new Insets(0, 20, 0, 0);
			gridBagConstraintsjLabelProxyBypass.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsjLabelProxyBypass.weighty = 0.0;
			gridBagConstraintsjLabelProxyBypass.gridy = 3;
			jPanelValue.add(jLabelProxyBypass, gridBagConstraintsjLabelProxyBypass);
			
			GridBagConstraints gridBagConstraintsgetJTextFieldProxyBypass = new GridBagConstraints();
			gridBagConstraintsgetJTextFieldProxyBypass.gridx = 1;
			gridBagConstraintsgetJTextFieldProxyBypass.weightx = 1.0;
			gridBagConstraintsgetJTextFieldProxyBypass.fill = GridBagConstraints.BOTH;
			gridBagConstraintsgetJTextFieldProxyBypass.insets = new Insets(5, 5, 5, 100);
			gridBagConstraintsgetJTextFieldProxyBypass.weighty = 0.0;
			gridBagConstraintsgetJTextFieldProxyBypass.anchor = GridBagConstraints.CENTER;
			gridBagConstraintsgetJTextFieldProxyBypass.gridy = 3;
			jPanelValue.add(getJTextFieldProxyBypass(), gridBagConstraintsgetJTextFieldProxyBypass);
			
			//jPanelValue.add(getJPanel(), gridBagConstraints2);
			
			
		}
		return jPanelValue;
	}

	private JTextField getJTextFieldProxyHost() {
		if (jTextFieldProxyHost == null) {
			jTextFieldProxyHost = new JTextField();
			jTextFieldProxyHost.setEditable(true);
			jTextFieldProxyHost.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_PROXY_HOST);
				}
			});
		}
		return jTextFieldProxyHost;
	}
	
	private JTextField getJTextFieldProxyPort() {
		if (jTextFieldProxyPort == null) {
			jTextFieldProxyPort = new JTextField();
			jTextFieldProxyPort.setEditable(true);
			jTextFieldProxyPort.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_PROXY_PORT);
				}
			});
		}
		return jTextFieldProxyPort;
	}

	private JTextField getJTextFieldProxyBypass() {
		if (jTextFieldProxyBypass == null) {
			jTextFieldProxyBypass = new JTextField();
			jTextFieldProxyBypass.setEditable(true);
			jTextFieldProxyBypass.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_PROXY_BYPASS);
				}
			});
		}
		return jTextFieldProxyBypass;
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
	private JLabel jLabelEmpty = null;
	class EventHandler {

		protected static final int TF_PROXY_HOST 	= 1;
		protected static final int TF_PROXY_PORT 	= 2;
		protected static final int TF_PROXY_BYPASS 	= 3;
		
		protected static final int BT_SAVE 					= 11;
		protected static final int BT_CANCEL 				= 12;
		
		protected EventHandler() {
		}
		
		protected void handle(int pEvent) {
			
			switch(pEvent) {
			
				case TF_PROXY_HOST:
				case TF_PROXY_PORT:
				case TF_PROXY_BYPASS:
					
					if (!getJTextFieldProxyHost().getText().equals(proxyHost)) {
						jButtonOK.setEnabled(true);
					} else if (!getJTextFieldProxyPort().getText().equals(proxyPort)) {
						jButtonOK.setEnabled(true);
					} else if (!getJTextFieldProxyBypass().getText().equals(proxyBypass)) {
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
					proxyHost = getJTextFieldProxyHost().getText().trim();
					proxyPort = getJTextFieldProxyPort().getText().trim();
					proxyBypass = getJTextFieldProxyBypass().getText().trim();
					ProxyUtil.getInstance().setProxyInfo(proxyHost,proxyPort,proxyBypass);
					
					log.debug("save proxy setting");
					jButtonOK.setEnabled(false);
					mParent.setVisible(false);
					break;
					
			}
		}
	}

}
