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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

/**
 * JPanLogin
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanLogin extends CacheablePanel {
	private static Log log = LogFactory.getLog(JPanLogin.class);
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelUserInfo = null;
	private JPanel jPanelCheckBox = null;
	private JPanel jPanelServer = null;
	private JLabel jLabelUser = null;
	private JTextField jTextFieldUser = null;
	private JLabel jLabelPwd = null;
	private JPasswordField jPasswordField = null;
	private JLabel jLabelServer = null;
	
	private JCheckBox jCheckBoxRememberIDOnly = null;
	private JLabel jLabelRememberID = null;
	
	private JTextField jTextFieldServerIP = null;

	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	
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
		
		getJTextFieldServerIP().setText(login.getProtexServerIP());
		getJCheckBoxRememberIDOnly().setSelected(login.isRememberIDOnly());
		
		if(login.isRememberIDOnly() == true) {
			jTextFieldUser.setText(login.getUserID());
		}
		
		mEventHandler.handle(EventHandler.LOAD_FROM_CACHE);
	}
	
	public void setFocus() {
				
		// Set Focus
		if(jTextFieldUser.getText().length() == 0) {
			jTextFieldUser.requestFocusInWindow();
		} else if(jPasswordField.getPassword().length == 0) {
			jPasswordField.requestFocusInWindow();
			log.debug("Request Focus for PWD: "+jPasswordField.isFocusOwner());

		} else if(jTextFieldServerIP.getText().length() == 0) {
			jTextFieldServerIP.requestFocusInWindow();
		}
	}

	public UIEntity exportUIEntity() {
		UELogin login = new UELogin(
				getJTextFieldUser().getText().trim(),
				new String(jPasswordField.getPassword()),
				getJTextFieldServerIP().getText().trim(),
				getJCheckBoxRememberIDOnly().isSelected()
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
			jLabelUser = new JLabel("User ID:");
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
			jLabelPwd = new JLabel();
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
			jLabelServer = new JLabel();
			jLabelServer.setText("Protex Server IP :");
			jLabelServer.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelServer.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.insets = new Insets(0, 10, 0, 0);
			jPanelUserInfo.add(jLabelServer, gridBagConstraints11);
			
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new Insets(0, 5, 0, 0);
			
			jPanelUserInfo.add(getJPanelServer(), gridBagConstraints21);
		}
		return jPanelUserInfo;
	}

	/**
	 * This method initializes jPanelCheckBox	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCheckBox() {
		if (jPanelCheckBox == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			jPanelCheckBox = new JPanel();
			jPanelCheckBox.setLayout(new GridBagLayout());
			jPanelCheckBox.add(getJCheckBoxRememberIDOnly(), gridBagConstraints6);
			jPanelCheckBox.add(getJLabelRememberID(), gridBagConstraints7);
		}
		return jPanelCheckBox;
	}

	/**
	 * This method initializes jPanelServer	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelServer() {
		if (jPanelServer == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(5, 0, 5, 5);
			gridBagConstraints4.gridx = 0;
			jPanelServer = new JPanel();
			jPanelServer.setLayout(new GridBagLayout());
			jPanelServer.add(getJTextFieldServerIP(), gridBagConstraints4);
		}
		return jPanelServer;
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
		}
		return jTextFieldServerIP;
	};
	
	/**
	 * This method initializes jCheckBoxRemberID	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBoxRememberIDOnly() {
		if (jCheckBoxRememberIDOnly == null) {
			jCheckBoxRememberIDOnly = new JCheckBox();
			jCheckBoxRememberIDOnly.setSelected(true);

			jCheckBoxRememberIDOnly.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					log.debug("itemStateChanged() - remember ID");
					mEventHandler.handle(EventHandler.CHBOX_REMEMBER_ID_ONLY);
			
				}
			});
		}
		return jCheckBoxRememberIDOnly;
	}
	
	private JLabel getJLabelRememberID() {
		if (jLabelRememberID == null) {
			jLabelRememberID = new JLabel();
			jLabelRememberID.setText("Remember ID");

			jLabelRememberID.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					getJCheckBoxRememberIDOnly().setSelected(!getJCheckBoxRememberIDOnly().isSelected());
					log.debug("MouseEvent() - remember ID");
					mEventHandler.handle(EventHandler.CHBOX_REMEMBER_ID_ONLY);
			
				}
			});
		}
		return jLabelRememberID;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelForUserInfo() {
		if (jPanelForUserInfo == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(10, 85, 15, 70);
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(10, 10, 0, 20);
			jPanelForUserInfo = new JPanel();
			jPanelForUserInfo.setLayout(new GridBagLayout());
			jPanelForUserInfo.setBorder(BorderFactory.createTitledBorder(null, "User Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForUserInfo.add(getJPanelUserInfo(), gridBagConstraints5);
			jPanelForUserInfo.add(getJPanelCheckBox(), gridBagConstraints10);
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
			gridBagConstraints15.insets = new Insets(18, 0, 10, 0);
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 1;
			jPanelForOkReset = new JPanel();
			jPanelForOkReset.setLayout(new GridBagLayout());
			jPanelForOkReset.add(getJButtonOK(), gridBagConstraints15);
			jPanelForOkReset.add(getJButtonCancel(), gridBagConstraints12);
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
			jButtonCancel.setFocusable(false);
			jButtonCancel.setPreferredSize(new Dimension(100, 28));
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.BTN_CANCEL);		
				}
			});
		}
		return jButtonCancel;
	}	

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelBase() {
		if (jPanelBase == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.insets = new Insets(0, 10, 10, 10);
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.weighty = 1.0;
			gridBagConstraints17.gridwidth = 1;
			gridBagConstraints17.gridy = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.CENTER;
			gridBagConstraints14.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints14.weighty = 1.0;
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = GridBagConstraints.NORTH;
			gridBagConstraints13.insets = new Insets(0, 0, 10, 10);
			jPanelBase = new JPanel();
			jPanelBase.setLayout(new GridBagLayout());
			jPanelBase.add(getJPanelForUserInfo(), gridBagConstraints14);
			jPanelBase.add(getJPanelForOkReset(), gridBagConstraints13);
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
			}
		}

		private void tryLogin() {
			UELogin ueLogin = (UELogin) mCache.getUIEntity(UICache.UE_LOGIN);
			if( ueLogin!= null && !getJTextFieldUser().getText().equals(ueLogin.getUserID()) )
				mCache.setClear();
			saveToCache();
			
			UIEntity ueLlogin = exportUIEntity();					
			
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