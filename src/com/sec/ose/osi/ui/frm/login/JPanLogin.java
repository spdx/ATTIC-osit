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
package com.sec.ose.osi.ui.frm.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.cache.CacheablePanel;
import com.sec.ose.osi.ui.cache.UICache;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.ProxyUtil;

/**
 * JPanLogin
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanLogin extends CacheablePanel {
	private static Log log = LogFactory.getLog(JPanLogin.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelUserInfo = null;
	private JTextField jTextFieldUser = null;
	private JPasswordField jPasswordField = null;
	private JTextField jTextFieldServerIP = null;

	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JButton jButtonAdvanced = null;
	
	private JPanel jPanelForProxyInfo = null;
	private JPanel jPanelProxyInfo = null;
	private JTextField jTextFieldProxyHost = null;
	private JTextField jTextFieldProxyPort = null;
	private JTextField jTextFieldProxyBypass = null;
	
	private JPanel jPanelForUserInfo = null;
	private JPanel jPanelForOkReset = null;
	private JPanel jPanelBase = null;
	
	private JFrame loginFrame = null;
	
	/**
	 * This is the default constructor
	 */
	public JPanLogin(JFrame pLoginFrame) {
		super();
		this.loginFrame = pLoginFrame;
		initialize();
		loadFromCache();
	}

	public void saveToCache() {
		
		UIEntity login = exportUIEntity();
		mCache.setUIEntity(UICache.UE_LOGIN, login);
	}

	public void loadFromCache() {
		UELogin login = (UELogin) mCache.getUIEntity(UICache.UE_LOGIN);
		if(login == null) {
			return;
		}

		getJTextFieldUser().setText(login.getUserID());
		getJTextFieldServerIP().setText(login.getProtexServerIP());
		
		mEventHandler.handle(EventHandler.LOAD_FROM_CACHE);
	}
	
	public void setFocus() {
				
		// Set Focus
		if(getJTextFieldUser().getText().length() == 0) {
			getJTextFieldUser().requestFocusInWindow();
		} else if(jPasswordField.getPassword().length == 0) {
			getJPasswordField().requestFocusInWindow();
		} else if(jTextFieldServerIP.getText().length() == 0) {
			getJTextFieldServerIP().requestFocusInWindow();
		}
	}

	public UIEntity exportUIEntity() {
		UELogin login = new UELogin(
				getJTextFieldUser().getText().trim(),
				new String(jPasswordField.getPassword()),
				getJTextFieldServerIP().getText().trim()
			);

		return login;

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.setSize(496, 320);
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);		
		this.add(getJPanelBase(), gridBagConstraints);

		// PROXY
		getJTextProxyHost().setText(ProxyUtil.getInstance().getProxyHost());
		getJTextProxyPort().setText(ProxyUtil.getInstance().getProxyPort());
		getJTextProxyBypass().setText(ProxyUtil.getInstance().getProxyBypass());
		
	}

	/**
	 * This method initializes jPanelUserInfo	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelUserInfo() {
		
		if (jPanelUserInfo == null) {
			
			jPanelUserInfo = new JPanel();
			jPanelUserInfo.setLayout(new GridBagLayout());
			
			// User ID
			JLabel jLabelUser = new JLabel("User ID:");
			jLabelUser.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelUser.setText("User ID :");
			jLabelUser.setEnabled(true);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(0, 10, 0, 0);
			jPanelUserInfo.add(jLabelUser, gridBagConstraints);

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			jPanelUserInfo.add(getJTextFieldUser(), gridBagConstraints1);
			
			// Password
			JLabel jLabelPwd = new JLabel();
			jLabelPwd.setText("Password :");
			jLabelPwd.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new Insets(0, 10, 0, 0);
			jPanelUserInfo.add(jLabelPwd, gridBagConstraints2);
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			jPanelUserInfo.add(getJPasswordField(), gridBagConstraints3);
			
			// Protex Server IP
			JLabel jLabelServer = new JLabel();
			jLabelServer.setText("Protex Server IP :");
			jLabelServer.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelServer.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new Insets(0, 10, 5, 0);
			jPanelUserInfo.add(jLabelServer, gridBagConstraints11);
			
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new Insets(5, 5, 10, 5);
			
			jPanelUserInfo.add(getJTextFieldServerIP(), gridBagConstraints21);
		}
		return jPanelUserInfo;
	}

	/**
	 * This method initializes jTextFieldUser	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUser() {
		if (jTextFieldUser == null) {
			jTextFieldUser = new JTextField();
			
			jTextFieldUser.setPreferredSize(new Dimension(200, 22));
			jTextFieldUser.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_USER_ID);
				}
			});
			jTextFieldUser.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						mEventHandler.handle(EventHandler.ENTER_KEY_TYTPED);
					}
				}
			});
			jTextFieldUser.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jTextFieldUser.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jTextFieldUser.select(0, 0);
	            }
			});
		}
		return jTextFieldUser;
	}

	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setPreferredSize(new Dimension(200, 22));
			jPasswordField.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.PTF_USER_PASSWORD);
				}
			});
			jPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						mEventHandler.handle(EventHandler.ENTER_KEY_TYTPED);
					}
				}
			});
			jPasswordField.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jPasswordField.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jPasswordField.select(0, 0);
	            }
			});
		}
		return jPasswordField;
	}
	
	public void setJPasswordField(String content) {
		jPasswordField.setText(content);
	}

	/**
	 * This method initializes jTextFieldServerIP	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldServerIP() {
		if (jTextFieldServerIP == null) {
			jTextFieldServerIP = new JTextField();
			jTextFieldServerIP.setPreferredSize(new Dimension(200, 22));
			
			jTextFieldServerIP.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.TF_SERVER_IP);
				}
			});
			jTextFieldServerIP.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						mEventHandler.handle(EventHandler.ENTER_KEY_TYTPED);
					}
				}
			});
			jTextFieldServerIP.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jTextFieldServerIP.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jTextFieldServerIP.select(0, 0);
	            }
			});
		}
		return jTextFieldServerIP;
	};
	
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelForUserInfo() {
		if (jPanelForUserInfo == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			//gridBagConstraints5.insets = new Insets(10, 10, 0, 20);
			jPanelForUserInfo = new JPanel();
			jPanelForUserInfo.setLayout(new GridBagLayout());
			jPanelForUserInfo.setBorder(BorderFactory.createTitledBorder(null, "User Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForUserInfo.add(getJPanelUserInfo(), gridBagConstraints5);
		}
		return jPanelForUserInfo;
	}


	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelForOkReset() {
		if (jPanelForOkReset == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.insets = new Insets(5, 0, 7, 0);
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 1;
			GridBagConstraints gridBagConstraintsAdvanced = new GridBagConstraints();
			gridBagConstraintsAdvanced.insets = new Insets(20, 0, 0, 0);
			gridBagConstraintsAdvanced.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsAdvanced.gridx = 0;
			gridBagConstraintsAdvanced.gridy = 3;
			jPanelForOkReset = new JPanel();
			jPanelForOkReset.setLayout(new GridBagLayout());
			jPanelForOkReset.add(getJButtonOK(), gridBagConstraints15);
			jPanelForOkReset.add(getJButtonCancel(), gridBagConstraints12);
			jPanelForOkReset.add(getJButtonAdvanced(), gridBagConstraintsAdvanced);
		}
		return jPanelForOkReset;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
			jButtonOK.setPreferredSize(new Dimension(100, 28));
			jButtonOK.setEnabled(true);
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() - BtnLogin");
					
					String userInfo = getJTextFieldUser().getText().trim();
					getJTextFieldUser().setText(userInfo);
					
					mEventHandler.handle(EventHandler.BTN_LOGIN);
					
				}
			});
			jButtonOK.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						String userInfo = getJTextFieldUser().getText().trim();
						getJTextFieldUser().setText(userInfo);
						mEventHandler.handle(EventHandler.ENTER_KEY_TYTPED);
					}
				}
			});
			
		}
		return jButtonOK;
	}


	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.setEnabled(true);
			jButtonCancel.setPreferredSize(new Dimension(100, 28));
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.BTN_CANCEL);
				}
			});
			jButtonCancel.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						mEventHandler.handle(EventHandler.BTN_CANCEL);
					}
				}
			});
		}
		return jButtonCancel;
	}	

	private JButton getJButtonAdvanced() {
		if (jButtonAdvanced == null) {
			jButtonAdvanced = new JButton();
			jButtonAdvanced.setText("Advanced..");
			jButtonAdvanced.setEnabled(true);
			//jButtonAdvanced.setFocusable(false);
			jButtonAdvanced.setPreferredSize(new Dimension(100, 28));
			jButtonAdvanced.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.BTN_ADVANCED);
				}
			});
			jButtonAdvanced.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						mEventHandler.handle(EventHandler.BTN_ADVANCED);
					}
				}
			});
		}
		return jButtonAdvanced;
	}
	
	/****************
	 *  Proxy Info
	 ***************/

	private JPanel getJPanelForProxyInfo() {
		if (jPanelForProxyInfo == null) {
			GridBagConstraints gridBagConstraintsProxyInfo = new GridBagConstraints();
			gridBagConstraintsProxyInfo.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsProxyInfo.gridx = -1;
			gridBagConstraintsProxyInfo.gridy = -1;
			gridBagConstraintsProxyInfo.gridwidth = 1;
			gridBagConstraintsProxyInfo.anchor = GridBagConstraints.CENTER;
			gridBagConstraintsProxyInfo.weightx = 1.0;
			gridBagConstraintsProxyInfo.weighty = 0.0;
			//gridBagConstraints5.insets = new Insets(10, 10, 0, 20);
			jPanelForProxyInfo = new JPanel();
			jPanelForProxyInfo.setLayout(new GridBagLayout());
			jPanelForProxyInfo.setBorder(BorderFactory.createTitledBorder(null, "Proxy Setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForProxyInfo.add(getJPanelProxyInfo(), gridBagConstraintsProxyInfo);
		}
		return jPanelForProxyInfo;
	}
	
	private JPanel getJPanelProxyInfo() {
		
		if (jPanelProxyInfo == null) {
			
			jPanelProxyInfo = new JPanel();
			jPanelProxyInfo.setLayout(new GridBagLayout());
			
			// Proxy Host
			JLabel jLabelProxyHost = new JLabel("Proxy Host :");
			jLabelProxyHost.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(0, 10, 0, 0);
			jPanelProxyInfo.add(jLabelProxyHost, gridBagConstraints);

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.weightx = 0.5;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			jPanelProxyInfo.add(getJTextProxyHost(), gridBagConstraints1);
			
			// Proxy Port
			JLabel jLabelProxyPort = new JLabel("Port :");
			jLabelProxyPort.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new Insets(0, 10, 0, 0);
			jPanelProxyInfo.add(jLabelProxyPort, gridBagConstraints2);
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.gridx = 3;
			gridBagConstraints3.weightx = 0.5;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			jPanelProxyInfo.add(getJTextProxyPort(), gridBagConstraints3);
			
			// Proxy Bypass
			JLabel jLabelProxyBypass = new JLabel();
			jLabelProxyBypass.setText("     Proxy Bypass :");
			jLabelProxyBypass.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelProxyBypass.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new Insets(0, 10, 5, 0);
			jPanelProxyInfo.add(jLabelProxyBypass, gridBagConstraints11);
			
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridwidth = 3;
			//gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new Insets(5, 5, 10, 5);
			
			jPanelProxyInfo.add(getJTextProxyBypass(), gridBagConstraints21);
		}
		return jPanelProxyInfo;
	}

	private JTextField getJTextProxyHost() {
		if (jTextFieldProxyHost == null) {
			jTextFieldProxyHost = new JTextField();
			jTextFieldProxyHost.setPreferredSize(new Dimension(130, 22));
			
			jTextFieldProxyHost.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jTextFieldProxyHost.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jTextFieldProxyHost.select(0, 0);
	            }
			});
		}
		return jTextFieldProxyHost;
	};

	private JTextField getJTextProxyPort() {
		if (jTextFieldProxyPort == null) {
			jTextFieldProxyPort = new JTextField();
			jTextFieldProxyPort.setPreferredSize(new Dimension(50, 22));
			
			jTextFieldProxyPort.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jTextFieldProxyPort.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jTextFieldProxyPort.select(0, 0);
	            }
			});
		}
		return jTextFieldProxyPort;
	};

	private JTextField getJTextProxyBypass() {
		if (jTextFieldProxyBypass == null) {
			jTextFieldProxyBypass = new JTextField();
			jTextFieldProxyBypass.setPreferredSize(new Dimension(200, 22));
			
			jTextFieldProxyBypass.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	            	jTextFieldProxyBypass.selectAll();
	            }
	            @Override
	            public void focusLost(FocusEvent e) {
	            	jTextFieldProxyBypass.select(0, 0);
	            }
			});
		}
		return jTextFieldProxyBypass;
	};
	
	/*********
	 * Base
	 **********/
	private JPanel getJPanelBase() {
		if (jPanelBase == null) {
			GridBagConstraints gridBagConstraintsUserInfo = new GridBagConstraints();
			gridBagConstraintsUserInfo.gridx = 0;
			gridBagConstraintsUserInfo.gridy = 0;
			gridBagConstraintsUserInfo.weightx = 1.0;
			gridBagConstraintsUserInfo.insets = new Insets(0, 10, 0, 10);
			gridBagConstraintsUserInfo.fill = GridBagConstraints.HORIZONTAL;

			GridBagConstraints gridBagConstraintsOkReset = new GridBagConstraints();
			gridBagConstraintsOkReset.gridx = 1;
			gridBagConstraintsOkReset.gridy = 0;
			gridBagConstraintsOkReset.weighty = 1.0;
			gridBagConstraintsOkReset.insets = new Insets(0, 0, 0, 10);
			gridBagConstraintsOkReset.fill = GridBagConstraints.BOTH;

			jPanelBase = new JPanel();
			jPanelBase.setLayout(new GridBagLayout());
			jPanelBase.add(getJPanelForUserInfo(), gridBagConstraintsUserInfo);
			jPanelBase.add(getJPanelForOkReset(), gridBagConstraintsOkReset);
		}
		return jPanelBase;
	}

	private EventHandler mEventHandler = new EventHandler();
	class EventHandler {

		protected static final int CHBOX_REMEMBER_ID_ONLY 		= 1;
		protected static final int LOAD_FROM_CACHE 				= 2;
		protected static final int TF_USER_ID 					= 3;
		protected static final int PTF_USER_PASSWORD 			= 4;
		protected static final int TF_SERVER_IP 				= 5;
		protected static final int ENTER_KEY_TYTPED 			= 6;
		protected static final int BTN_LOGIN					= 7; 
		protected static final int BTN_CANCEL					= 8;
		protected static final int BTN_ADVANCED					= 9;
		
		public void handle(int action) {

			switch(action) {
			
				case CHBOX_REMEMBER_ID_ONLY:
					break;
					
				case LOAD_FROM_CACHE:
				case TF_USER_ID :
				case PTF_USER_PASSWORD:
				case TF_SERVER_IP:
					if(jTextFieldUser.getText().length() == 0  ||
					   jPasswordField.getPassword().length == 0 ||
					   jTextFieldServerIP.getText().length() == 0
					   ) {
						jButtonOK.setEnabled(false);
					}
					else {
						jButtonOK.setEnabled(true);
					}
					break;
					
				
				case ENTER_KEY_TYTPED:
					if(jTextFieldUser.getText().length() == 0  ||
					   jPasswordField.getPassword().length == 0 ||
					   jTextFieldServerIP.getText().length() == 0
					   ) {
						setFocus();
						break;
					}
					// go through intentionally

					
				case BTN_LOGIN:
					tryLogin();
					break;
					
				case BTN_CANCEL:
					System.exit(0);
					break;
				
				case BTN_ADVANCED:

					if(getJPanelBase().getComponentCount() == 2) {
						GridBagConstraints gridBagConstraintsProxyInfo = new GridBagConstraints();
						gridBagConstraintsProxyInfo.gridx = 0;
						gridBagConstraintsProxyInfo.gridy = 1;
						gridBagConstraintsProxyInfo.weightx = 1.0;
						gridBagConstraintsProxyInfo.insets = new Insets(0, 10, 18, 10);
						gridBagConstraintsProxyInfo.fill = GridBagConstraints.HORIZONTAL;
						getJPanelBase().add(getJPanelForProxyInfo(), gridBagConstraintsProxyInfo);
						loginFrame.setSize(520, 300);
					} else {
						getJPanelBase().remove(getJPanelForProxyInfo());
						loginFrame.setSize(520, 190);
					}
					
					break;
			}
		}

		private void tryLogin() {
			UELogin ueLogin = (UELogin) mCache.getUIEntity(UICache.UE_LOGIN);
			if( ueLogin!= null && !getJTextFieldUser().getText().equals(ueLogin.getUserID()) )
				mCache.setClear();
			saveToCache();
			
			UIEntity ueLlogin = exportUIEntity();

			// set Proxy
			String proxyHost = getJTextProxyHost().getText().trim();
			String proxyPort = getJTextProxyPort().getText().trim();
			String proxyBypass = getJTextProxyBypass().getText().trim();
			ProxyUtil.getInstance().setProxyInfo(proxyHost, proxyPort, proxyBypass);
	    	
			loginFrame.setVisible(false);
 
			UIResponseObserver observer = UserRequestHandler.getInstance().handle(
					UserRequestHandler.LOGIN, 
					ueLlogin, 
					true, 
					false); // blocking
			
			if(observer.getResult() != UIResponseObserver.RESULT_SUCCESS) {

				JOptionPane.showMessageDialog(
						null, 
						observer.getFailMessage(),
						"Login failed",
						JOptionPane.WARNING_MESSAGE);
				loginFrame.setVisible(true);
				jPasswordField.setText("");
				jPasswordField.requestFocus();
				
			} else {
				loginFrame.setVisible(false);
				UISharedData.getInstance().getCurrentFrame().setVisible(true);
			}
			
		}
	}
}