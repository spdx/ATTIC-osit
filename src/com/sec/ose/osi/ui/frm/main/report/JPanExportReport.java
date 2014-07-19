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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.report.SpdxAllLicenseInfoFromFileType;
import com.blackducksoftware.sdk.protex.report.SpdxFileLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxLicenseDeclarationType;
import com.blackducksoftware.sdk.protex.report.SpdxPackageLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.blackducksoftware.sdk.protex.user.User;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.frm.main.report.file_explorer.JFCFolderExplorer;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.FormatUtil;

/**
 * JPanExportReport
 * @author suhyun47.kim, sjh.yoo, ytael.kim, hankido.lee, jae-yong.lee
 * 
 */
public class JPanExportReport extends JPanel{
	private static Log log = LogFactory.getLog(JPanExportReport.class);
	
	private static final long serialVersionUID = 3865970050510146656L;
	
	private JTextField jTextFieldReportLocation = null;
	private JButton jButtonCreateExplorer = null;
	
	private JPanel jPanMain = null;
	
	private JPanel jButtonPanel = null;	
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;

	private final int CREATOR_NAME = 0;
	private final int CREATOR_EMAIL = 1;
	private final int ORGANIZATION_NAME = 2;
	private final int FIELD_COUNT = 3;
	
	private JLabel [] jLabelList = new JLabel[FIELD_COUNT];
	private boolean mandatoryField[] = new boolean[FIELD_COUNT]; 
	
	public JPanExportReport() {
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
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			
			jPanMain.add(getJPanelForCreatorInfo(), gridBagConstraints1);
			jPanMain.add(getJPanelForDocumentExportInfo(), gridBagConstraints2);
			jPanMain.add(getJPanelForReportTypeInfo(), gridBagConstraints3);
			jPanMain.add(getJButtonPanel(), gridBagConstraints4);			
		}
		
		return jPanMain;
	}

	private JDlgExportReport jDlgExportReport;
	
	public void setParent(JDlgExportReport dialog) {
		this.jDlgExportReport = dialog;
	}
	
	/** Select Report export location **/
	private JPanel jPanelForDocumentExportInfo = null;
	private JPanel getJPanelForDocumentExportInfo() {
		if (jPanelForDocumentExportInfo == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(0, 0, 10, 5);	//  margin // top, left, bottom, right
			jPanelForDocumentExportInfo = new JPanel();
			jPanelForDocumentExportInfo.setLayout(new GridBagLayout());
			jPanelForDocumentExportInfo.setBorder(BorderFactory.createTitledBorder(null, "Report Export Location", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForDocumentExportInfo.add(getJPanelDocumentExportInfo(), gridBagConstraints5);
		}
		return jPanelForDocumentExportInfo;
	}
	
	private JPanel jPanelDocumentExportInfo = null;
	private JPanel getJPanelDocumentExportInfo() {
		if (jPanelDocumentExportInfo == null) {
			jPanelDocumentExportInfo = new JPanel();
			jPanelDocumentExportInfo.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
			jPanelDocumentExportInfo.add(getJTextFieldReportLocation(), gridBagConstraints1);
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			jPanelDocumentExportInfo.add(getJButtonCreateExplorer(), gridBagConstraints2);
		}
		return jPanelDocumentExportInfo;
	}
	
	protected JTextField getJTextFieldReportLocation() {
		if (jTextFieldReportLocation == null) {
			jTextFieldReportLocation = new JTextField();
			jTextFieldReportLocation.setPreferredSize(new Dimension(330, 22));
			jTextFieldReportLocation.setEditable(false);
			jTextFieldReportLocation.setBackground(Color.WHITE);
			
			jTextFieldReportLocation.setText(Property.getInstance().getProperty(Property.DEFALT_REPORT_LOCATION));
		}
		return jTextFieldReportLocation;
	}

	private JButton getJButtonCreateExplorer() {
		if (jButtonCreateExplorer == null) {
			jButtonCreateExplorer = new JButton();
			jButtonCreateExplorer.setText("...");
			jButtonCreateExplorer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.BTN_CREATE_EXPLORER);
				}
			});
		}
		return jButtonCreateExplorer;
	}

	/** Select Report type **/
	private JPanel jPanelForReportTypeInfo = null;
	private JPanel getJPanelForReportTypeInfo() {
		if (jPanelForReportTypeInfo == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(0, 10, 5, 150);	// margin // top, left, bottom, right
			jPanelForReportTypeInfo = new JPanel();
			jPanelForReportTypeInfo.setLayout(new GridBagLayout());
			jPanelForReportTypeInfo.setBorder(BorderFactory.createTitledBorder(null, "Report Type", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForReportTypeInfo.add(getJPanelReportTypeInfo(), gridBagConstraints5);
		}
		return jPanelForReportTypeInfo;
	}
	
	private JPanel jPanelReportTypeInfo = null;
	private JPanel getJPanelReportTypeInfo() {
		if (jPanelReportTypeInfo == null) {
			jPanelReportTypeInfo = new JPanel();
			jPanelReportTypeInfo.setLayout(new GridBagLayout());

			JLabel jLabelBoth = new JLabel();
			jLabelBoth.setText("Generate Both Report (Identify and SPDX)");
			jLabelBoth.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mEventHandler.handle(EventHandler.BTN_BOTH_REPORT);
						}
					}
			);

			JLabel jLabelIdentify = new JLabel();
			jLabelIdentify.setText("Generate Identify Report (*.xlsx)");
			jLabelIdentify.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mEventHandler.handle(EventHandler.BTN_IDENTIFY_REPORT);
						}
					}
			);

			JLabel jLabelSPDX = new JLabel();
			jLabelSPDX.setText("Generate SPDX Report (*.rdf)");
			jLabelSPDX.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mEventHandler.handle(EventHandler.BTN_SPDX_REPORT);
						}
					}
			);

			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 1;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.insets = new Insets(0, 0, 0, 70);
			gridBagConstraints22.gridx = 1;
			gridBagConstraints22.gridy = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridy = 2;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints32.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints32.gridx = 1;
			gridBagConstraints32.gridy = 2;

			jPanelReportTypeInfo.add(getJRadioButtonBoth(), gridBagConstraints11);
			jPanelReportTypeInfo.add(jLabelBoth, gridBagConstraints12);
			jPanelReportTypeInfo.add(getJRadioButtonIdentify(), gridBagConstraints21);
			jPanelReportTypeInfo.add(jLabelIdentify, gridBagConstraints22);
			jPanelReportTypeInfo.add(getJRadioButtonSPDX(), gridBagConstraints31);
			jPanelReportTypeInfo.add(jLabelSPDX, gridBagConstraints32);

			selectedBothReport();
		}
		return jPanelReportTypeInfo;
	}

	private void selectedBothReport() {
		this.getJRadioButtonBoth().setSelected(true);
		this.getJRadioButtonIdentify().setSelected(false);
		this.getJRadioButtonSPDX().setSelected(false);
	}

	private void selectedIdentifyReport() {
		this.getJRadioButtonBoth().setSelected(false);
		this.getJRadioButtonIdentify().setSelected(true);
		this.getJRadioButtonSPDX().setSelected(false);
	}

	private void selectedSPDXReport() {
		this.getJRadioButtonBoth().setSelected(false);
		this.getJRadioButtonIdentify().setSelected(false);
		this.getJRadioButtonSPDX().setSelected(true);
	}

	JRadioButton jRadioButtonBoth = null;
	private JRadioButton getJRadioButtonBoth() {
		if (jRadioButtonBoth == null) {
			jRadioButtonBoth = new JRadioButton();
			jRadioButtonBoth.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.BTN_BOTH_REPORT);
						}
					}
					);
			
		}
		return jRadioButtonBoth;
	}

	JRadioButton jRadioButtonIdentify = null;
	private JRadioButton getJRadioButtonIdentify() {
		if (jRadioButtonIdentify == null) {
			jRadioButtonIdentify = new JRadioButton();
			jRadioButtonIdentify.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.BTN_IDENTIFY_REPORT);
						}
					}
					);
			
		}
		return jRadioButtonIdentify;
	}

	JRadioButton jRadioButtonSPDX = null;
	private JRadioButton getJRadioButtonSPDX() {
		if (jRadioButtonSPDX == null) {
			jRadioButtonSPDX = new JRadioButton();
			jRadioButtonSPDX.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.BTN_SPDX_REPORT);
						}
					}
					);
			
		}
		return jRadioButtonSPDX;
	}
	
	/** Button **/
	private JPanel getJButtonPanel() {
		if(jButtonPanel == null) {
			jButtonPanel = new JPanel();			
			jButtonPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(10, 200, 10, 5);
			gridBagConstraints1.gridx = 3;
			gridBagConstraints1.gridy = 2;
			jButtonPanel.add(getJButtonOK(), gridBagConstraints1);
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(10, 5, 10, 0);
			gridBagConstraints2.gridx = 4;
			gridBagConstraints2.gridy = 2;
			jButtonPanel.add(getJButtonCancel(), gridBagConstraints2);

		}
		return jButtonPanel;
	}
	
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("   OK   ");
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					String dirPath = jTextFieldReportLocation.getText();
					File dir = new File(dirPath);
					if(dir.exists() == false) {
		        		int choice = JOptionPane.showOptionDialog(
		        				null, 
		        				"\""+dir+"\" is not existed folder. Do you want to create it now?",
		        				"Alarm", 
		        				JOptionPane.YES_NO_OPTION, 
		        				JOptionPane.QUESTION_MESSAGE, 
		        				null, 
		        				null,
		        				null);
		        		if(choice == JOptionPane.NO_OPTION) {
		        			return;
		        		}
		        		dir.mkdirs();
		        	}
					
					if(getJRadioButtonIdentify().isSelected()) {
						mEventHandler.handle(EventHandler.BTN_OK_IDENTIFY);
					} else if(getJRadioButtonSPDX().isSelected()) {
						mEventHandler.handle(EventHandler.BTN_OK_SPDX);
					} else if(getJRadioButtonBoth().isSelected()) {
						mEventHandler.handle(EventHandler.BTN_OK_BOTH);
					}
				}
			});
		}
		return jButtonOK;
	}

	private JButton getJButtonCancel() {

		if(jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mEventHandler.handle(EventHandler.BTN_CANCEL);
						}
					}
					);
			
		}
		return jButtonCancel;
	}
	
	/** Creator Infomation UI **/
	private JTextField getJTextFieldCreatorName() {
		if (jTextFieldCreatorName == null) {
			jTextFieldCreatorName = new JTextField();
			
			jTextFieldCreatorName.setPreferredSize(new Dimension(200, 22));
			jTextFieldCreatorName.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.CHECK_BLANK);
				}
			});
			jTextFieldCreatorName.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						if(jTextFieldCreatorName.getText().length() == 0) {
							jTextFieldCreatorName.requestFocusInWindow();
						} else {
							getJTextFieldCreatorEmail().requestFocusInWindow();
						}
					}
				}
			});
			
			try {
				User userInfo = ProtexSDKAPIManager.getUserAPI().getUserByEmail(LoginSessionEnt.getInstance().getUserID());
				jTextFieldCreatorName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
			} catch (SdkFault e1) {
				e1.printStackTrace();
			}
		}
		return jTextFieldCreatorName;
	}

	private JTextField getJTextFieldCreatorEmail() {
		if (jTextFieldCreatorEmail == null) {
			jTextFieldCreatorEmail = new JTextField();
			
			jTextFieldCreatorEmail.setPreferredSize(new Dimension(200, 22));
			jTextFieldCreatorEmail.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						if(jTextFieldCreatorEmail.getText().length() == 0) {
							jTextFieldCreatorEmail.requestFocusInWindow();
						} else {
							getJTextFieldOrganizationName().requestFocusInWindow();
						}
					}
				}
			});
			jTextFieldCreatorEmail.setText(LoginSessionEnt.getInstance().getUserID());
		}
		return jTextFieldCreatorEmail;
	}

	private JTextField getJTextFieldOrganizationName() {
		if (jTextFieldOrganizationName == null) {
			jTextFieldOrganizationName = new JTextField();
			
			jTextFieldOrganizationName.setPreferredSize(new Dimension(200, 22));
			jTextFieldOrganizationName.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					mEventHandler.handle(EventHandler.CHECK_BLANK);
				}
			});
			jTextFieldOrganizationName.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						if(jTextFieldOrganizationName.getText().length() == 0) {
							jTextFieldOrganizationName.requestFocusInWindow();
						}
					}
				}
			});
			jTextFieldOrganizationName.setText(LoginSessionEnt.getInstance().getOrganization());
		}
		
		return jTextFieldOrganizationName;
	}

	private JPanel jPanelForCreatorInfo = null;
	private JPanel jPanelCreatorInfo = null;
	
	private JTextField jTextFieldCreatorName = null;
	private JTextField jTextFieldCreatorEmail = null;
	private JTextField jTextFieldOrganizationName = null;

	private JPanel getJPanelForCreatorInfo() {
		if (jPanelForCreatorInfo == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(0, 10, 10, 43);	// margin // top, left, bottom, right
			jPanelForCreatorInfo = new JPanel();
			jPanelForCreatorInfo.setLayout(new GridBagLayout());
			jPanelForCreatorInfo.setBorder(BorderFactory.createTitledBorder(null, "Creator Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelForCreatorInfo.add(getJPanelCreatorInfo(), gridBagConstraints5);
		}
		return jPanelForCreatorInfo;
	}
	
	private JPanel getJPanelCreatorInfo() {
		if (jPanelCreatorInfo == null) {
			jPanelCreatorInfo = new JPanel();
			jPanelCreatorInfo.setLayout(new GridBagLayout());

			jLabelList[CREATOR_NAME] = new JLabel("* Creator Name:",JLabel.RIGHT);
			mandatoryField[CREATOR_NAME] = true;
			jLabelList[CREATOR_EMAIL] = new JLabel("* Creator Email:",JLabel.RIGHT);
			mandatoryField[CREATOR_EMAIL] = true;
			jLabelList[ORGANIZATION_NAME] = new JLabel("* Organization Name:",JLabel.RIGHT);
			mandatoryField[ORGANIZATION_NAME] = true;
			
			// Creator Name
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = CREATOR_NAME;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(0, 5, 0, 0);
			jPanelCreatorInfo.add(jLabelList[CREATOR_NAME], gridBagConstraints);

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = CREATOR_NAME;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
			jPanelCreatorInfo.add(getJTextFieldCreatorName(), gridBagConstraints1);
			
			// Creator Email
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = CREATOR_EMAIL;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
			jPanelCreatorInfo.add(jLabelList[CREATOR_EMAIL], gridBagConstraints2);
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = CREATOR_EMAIL;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(5, 5, 0, 5);
			jPanelCreatorInfo.add(getJTextFieldCreatorEmail(), gridBagConstraints3);

			// Organization Name
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridy = ORGANIZATION_NAME;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(0, 5, 0, 0);
			jPanelCreatorInfo.add(jLabelList[ORGANIZATION_NAME], gridBagConstraints4);

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = ORGANIZATION_NAME;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new Insets(5, 5, 0, 5);
			jPanelCreatorInfo.add(getJTextFieldOrganizationName(), gridBagConstraints5);
		}
		return jPanelCreatorInfo;
	}

	private EventHandler mEventHandler = new EventHandler();
	class EventHandler {

		protected static final int PANEL_CREATED = 0;
		protected static final int BTN_CREATE_EXPLORER = 1;
		protected static final int BTN_OK_IDENTIFY = 2;
		protected static final int BTN_OK_SPDX = 3;
		protected static final int BTN_OK_BOTH = 4;
		protected static final int BTN_CANCEL = 10;
		protected static final int CHECK_BLANK = 11;
		
		protected static final int BTN_BOTH_REPORT = 20;
		protected static final int BTN_IDENTIFY_REPORT = 21;
		protected static final int BTN_SPDX_REPORT = 22;
		
		public void handle(int pCode) {
			switch (pCode) {
				case BTN_CREATE_EXPLORER:
					JFCFolderExplorer folderExplorer = JFCFolderExplorer.getInstance();
					int result = folderExplorer.showSaveDialog(jTextFieldReportLocation.getText());
									
					if( result == JFileChooser.APPROVE_OPTION) {
						String reportDirName = folderExplorer.getSelectedFile().getPath();
						File reportDir = new File(reportDirName);
			        	if(reportDir.exists() == false) {
			        		int choice = JOptionPane.showOptionDialog(
			        				null, 
			        				"\""+reportDir+"\" is not existed folder. Do you want to create it now?",
			        				"Alarm", 
			        				JOptionPane.YES_NO_OPTION, 
			        				JOptionPane.QUESTION_MESSAGE, 
			        				null, 
			        				null,
			        				null);
			        		if (choice == JOptionPane.YES_OPTION) {
			        			reportDir.mkdirs();
			        		} else {
						    	return;
			        		}
			        	}
			        	getJTextFieldReportLocation().setText(reportDir.getPath());
			
					}
					
					break;

				case BTN_OK_BOTH:
					generateBothFile();
					break;
					
				case BTN_OK_IDENTIFY:
					generateIdentifyFile();
					break;
					
				case BTN_OK_SPDX:
					generateSPDXDocument();
					break;
					
				case BTN_CANCEL:
					jDlgExportReport.setVisible(false);
					break;
					
				case CHECK_BLANK:
					for(int i=0;i<mandatoryField.length;i++) {
						if(mandatoryField[i]) jLabelList[i].setForeground(new Color(255,0,0));
						else  jLabelList[i].setForeground(new Color(0,0,0));
					}
					
					if((mandatoryField[CREATOR_NAME] && getJTextFieldCreatorName().getText().length() == 0) ||
						(mandatoryField[CREATOR_EMAIL] && getJTextFieldCreatorEmail().getText().length() == 0) ||
						(mandatoryField[ORGANIZATION_NAME] && getJTextFieldOrganizationName().getText().length() == 0)
					   ) {
						getJButtonOK().setEnabled(false);
					} else {
						getJButtonOK().setEnabled(true);
					}
					break;

				case BTN_BOTH_REPORT:
					selectedBothReport();
					mandatoryField[ORGANIZATION_NAME] = true;
					handle(CHECK_BLANK);
					break;
					
				case BTN_IDENTIFY_REPORT:
					selectedIdentifyReport();
					mandatoryField[ORGANIZATION_NAME] = false;
					handle(CHECK_BLANK);
					break;

				case BTN_SPDX_REPORT:
					selectedSPDXReport();
					mandatoryField[ORGANIZATION_NAME] = true;
					handle(CHECK_BLANK);
					break;
			}
		}
		
		private void generateBothFile() {
			String reportedFilePath = getJTextFieldReportLocation().getText().trim();
			if(reportedFilePath.lastIndexOf(File.separator) != reportedFilePath.length()-1) {
				reportedFilePath += File.separator;
			}
			String prevReportedFilePath = reportedFilePath;
			String projectName = ReportMediator.getInstance().getFirstSelectedProjectName();
			reportedFilePath += getReportFileNameFromProjectName(projectName);
			
			// check if target file name is empty
			if( (reportedFilePath == null) || (reportedFilePath.length() == 0) ) {
				JOptionPane.showMessageDialog(
						null,
						"Report file location must be filled.",
						"Select target file name",
						JOptionPane.ERROR_MESSAGE
						);
				return;
			}

			UEBothBOM ueBothBOM = (UEBothBOM) ReportMediator.getInstance().getJPanReportMain().generateBothReportUIEntity();
			ueBothBOM.setSrcFileName(null);
			ueBothBOM.setTargetFileName(reportedFilePath);
			
			ueBothBOM.setOverwrite(true);
			ueBothBOM.setInsertCodeMatch(true);
			
			ueBothBOM.setProjectList(ReportMediator.getInstance().getSelectedProjectList());
			
			ueBothBOM.setCreatorName(getJTextFieldCreatorEmail().getText());
			ueBothBOM.setCreatorEmail(getJTextFieldCreatorEmail().getText());
			ueBothBOM.setOrganizationName(getJTextFieldOrganizationName().getText());
			
			SpdxReportConfiguration spdxReportConfiguration = new SpdxReportConfiguration();

			ArrayList<String> reportFilePathList = new ArrayList<String>(1);
			for(String tmpProjectName:ReportMediator.getInstance().getSelectedProjectList()) {
				String tmpFileName = getSPDXDocumentFileNameFromProjectName(tmpProjectName);
				reportFilePathList.add(prevReportedFilePath + tmpFileName);
		        spdxReportConfiguration.setPackageName(tmpProjectName);	// packages.name
		        // <name>testPackageName</name>
		    	spdxReportConfiguration.setPackageFileName(tmpFileName);	// packages.fileName
		        // <packageFileName>testPackageFileName</packageFileName>
			}
			ueBothBOM.setTargetSDPXReportFileList(reportFilePathList);
			
	        ueBothBOM.setSpdxReportConfiguration(getSPDXReportConfiguration(spdxReportConfiguration));

			// making parameter to pass to SDKInterface
			jDlgExportReport.setVisible(false);
			
			// call SDKInterface
			UIResponseObserver observer = UserRequestHandler.getInstance().handle(
					UserRequestHandler.GENERATE_BOTH_REPORT, 
					ueBothBOM, 
					true, 
					false);

			// check if user want to open file now.
			String msg = null;
			int msgType = JOptionPane.INFORMATION_MESSAGE;
			if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS) {
				msg = observer.getSuccessMessage();
				msgType = JOptionPane.INFORMATION_MESSAGE;
			} else {
				msg = observer.getFailMessage();
				msgType = JOptionPane.ERROR_MESSAGE;
			}
			JOptionPane.showMessageDialog(
					null, 
					msg+"\n",
					"Export SPDX Document",
					msgType
					);
		}
		
		private void generateIdentifyFile() {
			String reportedFilePath = getJTextFieldReportLocation().getText().trim();
			if(reportedFilePath.lastIndexOf(File.separator) != reportedFilePath.length()-1) {
				reportedFilePath += File.separator;
			}
			String projectName = ReportMediator.getInstance().getFirstSelectedProjectName();
			reportedFilePath += getReportFileNameFromProjectName(projectName);
			
			// check if target file name is empty
			if( (reportedFilePath == null) || (reportedFilePath.length() == 0) ) {
				JOptionPane.showMessageDialog(
						null,
						"Report file location must be filled.",
						"Select target file name",
						JOptionPane.ERROR_MESSAGE
						);
				return;
			}

			// making parameter to pass to SDKInterface
			jDlgExportReport.setVisible(false);
			
			UEBOM ueBOM = (UEBOM) ReportMediator.getInstance().getJPanReportMain().exportUIEntity();
			ueBOM.setSrcFileName(null);
			ueBOM.setTargetFileName(reportedFilePath);
			
			ueBOM.setOverwrite(true);
			ueBOM.setInsertCodeMatch(true);
			
			ueBOM.setCreatorName(getJTextFieldCreatorName().getText());
			ueBOM.setCreatorEmail(getJTextFieldCreatorEmail().getText());
			ueBOM.setOrganizationName(getJTextFieldOrganizationName().getText());
			
			// call SDKInterface
			UIResponseObserver observer = UserRequestHandler.getInstance().handle(
					UserRequestHandler.GENERATE_IDENTIFY_REPORT, 
					ueBOM, 
					true, 
					false);
			
			// check if user want to open file now.
			if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS) {

				String msg = observer.getSuccessMessage();
				msg = "\""+reportedFilePath+"\" has been created successfully.";
				

				int resultConfirm = JOptionPane.showConfirmDialog(
						UISharedData.getInstance().getCurrentFrame(), 
						msg+"\nDo you want to open it now?\n\n",
						"Open File",
						JOptionPane.YES_NO_OPTION
						);
				
				if(resultConfirm == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().open( new File(reportedFilePath) );
					} catch (IOException e) {
						log.warn(e);
						String message = e.getMessage();
						
						JOptionPane.showMessageDialog(
								UISharedData.getInstance().getCurrentFrame(), 
								message,
								"Cannot Open File",
								JOptionPane.ERROR_MESSAGE
								);
					}
				}
				
			} 
		}
		
		private void generateSPDXDocument() {
			String prevReportedFilePath = getJTextFieldReportLocation().getText().trim();
			if(prevReportedFilePath.lastIndexOf(File.separator) != prevReportedFilePath.length()-1) {
				prevReportedFilePath += File.separator;
			}

			UESPDXBOM ueSPDXBOM = (UESPDXBOM) ReportMediator.getInstance().getJPanReportMain().generateSPDXReportUIEntity();

			ueSPDXBOM.setProjectList(ReportMediator.getInstance().getSelectedProjectList());
				
			SpdxReportConfiguration spdxReportConfiguration = new SpdxReportConfiguration();

			ArrayList<String> reportFilePathList = new ArrayList<String>(1);
			for(String tmpProjectName:ReportMediator.getInstance().getSelectedProjectList()) {
				String tmpFileName = getSPDXDocumentFileNameFromProjectName(tmpProjectName);
				reportFilePathList.add(prevReportedFilePath + tmpFileName);
		        spdxReportConfiguration.setPackageName(tmpProjectName);	// packages.name
		        // <name>testPackageName</name>
		    	spdxReportConfiguration.setPackageFileName(tmpFileName);	// packages.fileName
		        // <packageFileName>testPackageFileName</packageFileName>
			}
			ueSPDXBOM.setTargetSDPXReportFileList(reportFilePathList);
			
			ueSPDXBOM.setSpdxReportConfiguration(getSPDXReportConfiguration(spdxReportConfiguration));

			// making parameter to pass to SDKInterface
			jDlgExportReport.setVisible(false);

			// call SDKInterface
			UIResponseObserver SPDXObserver = UserRequestHandler.getInstance().handle(
					UserRequestHandler.GENERATE_SPDX_REPORT, 
					ueSPDXBOM, 
					true, 
					false);
			
			// check if user want to open file now.
			String msg = null;
			int msgType = JOptionPane.INFORMATION_MESSAGE;
			if(SPDXObserver.getResult() == UIResponseObserver.RESULT_SUCCESS) {
				msg = SPDXObserver.getSuccessMessage();
				msgType = JOptionPane.INFORMATION_MESSAGE;
			} else {
				msg = SPDXObserver.getFailMessage();
				msgType = JOptionPane.ERROR_MESSAGE;
			}
			JOptionPane.showMessageDialog(
					null, 
					msg+"\n",
					"Export SPDX Document",
					msgType
			);
		}
		

		private String getReportFileNameFromProjectName(String protexProjectName) {
			

			
			String reportFileName = protexProjectName
									+ "_"
									+ FormatUtil.getTimeIdentifier()
									+ ".xlsx"
									;

			reportFileName = reportFileName.replace(" ", "_");
			return reportFileName;
		}

		private String getSPDXDocumentFileNameFromProjectName(String protexProjectName) {
			

			
			String reportFileName = protexProjectName
									+ "_"
									+ FormatUtil.getTimeIdentifier()
									+ ".rdf"
									;

			reportFileName = reportFileName.replace(" ", "_");
			return reportFileName;
		}

		private SpdxReportConfiguration getSPDXReportConfiguration(SpdxReportConfiguration src) {
			SpdxReportConfiguration spdxReportConfiguration = src;
			
			/** Spec. 3 CREATION INFORMATION **/
	        spdxReportConfiguration.setOrganization(getJTextFieldOrganizationName().getText());
	        spdxReportConfiguration.setCreatedBy(getJTextFieldCreatorName().getText());
	        String creatorEmail = getJTextFieldCreatorEmail().getText().trim();
	        if(!creatorEmail.equals("")) {
		        if(creatorEmail.contains(" ") || !creatorEmail.contains("@")) {
					JOptionPane.showMessageDialog(
							null, 
							"Must be an email address\n",
							"Creator Email Format Error",
							JOptionPane.ERROR_MESSAGE
							);
					getJTextFieldCreatorEmail().requestFocusInWindow();
		        	return null;
		        }
	        }
	        spdxReportConfiguration.setCreatedByEMail(creatorEmail);			
	        /** Spec. 4 PACKAGE INFORMATION **/

	        spdxReportConfiguration.setPackageLicenseDeclarationType(SpdxLicenseDeclarationType.PROJECT_DECLARED_LICENSE);	// PROJECT_DECLARED_LICENSE | NO_ASSERTION	// packages.concludedLicense
	        spdxReportConfiguration.setAllLicenseInfoFromFilesType(SpdxAllLicenseInfoFromFileType.IDENTIFIED_PLUS_STRING_SEARCH_MATCH_LICENSES);	// IDENTIFIED_PLUS_STRING_SEARCH_MATCH_LICENSES | NO_ASSERTION		// packages.allLicensesInfoFromFiles
	        spdxReportConfiguration.setPackageLicenseConclusionType(SpdxPackageLicenseConclusionType.COMPONENT_IDENTIFIED_LICENSES);	// COMPONENT_IDENTIFIED_LICENSES | NO_ASSERTION	// packages.declaredLicense

	        // Unused
	        spdxReportConfiguration.setLicenseComment("");	// Spec 4. Comments On License (not available, protex error??)
	        spdxReportConfiguration.setPackageDownloadUrl("");	// Spec 4. Package Download Location
	        spdxReportConfiguration.setPackageSourceInformation("");	// Spec 4. Source Information	// (not available, protex error??)
	        spdxReportConfiguration.setCopyright("");	// Spec 4. Copyright Text
	        spdxReportConfiguration.setPackageDescription("");	// Spec 4. Package Detailed Description
	        
	        /** DEFAULT SETTING **/
	        spdxReportConfiguration.setFileLicenseConclusionType(SpdxFileLicenseConclusionType.COMPONENT_IDENTIFIED_LICENSES);	// COMPONENT_IDENTIFIED_LICENSES | NO_ASSERTION
	        System.out.println(LoginSessionEnt.getInstance().getProtexServerUrl());
	        spdxReportConfiguration.setProtexUrl(LoginSessionEnt.getInstance().getProtexServerUrl());

	        spdxReportConfiguration.setReportComment("");
	        
	        return spdxReportConfiguration;
		}
		
	}
}
