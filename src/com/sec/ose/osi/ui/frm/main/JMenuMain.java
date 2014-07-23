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
package com.sec.ose.osi.ui.frm.main;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.IdentifyThread;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.ui.ApplicationCloseMgr;
import com.sec.ose.osi.ui.dialog.about.JDlgAbout;
import com.sec.ose.osi.ui.dialog.setting.JDlgSetting;
import com.sec.ose.osi.ui.dialog.showqueue.JDlgQueueInfo;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.autoidentify.JDlgImportSPDX;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;

/**
 * JMenuMain
 * @author suhyun47.kim, sjh.yoo, ytaek.kim, hankido.lee, jae-yong.lee
 * 
 */
public class JMenuMain extends JMenuBar {
	private static Log log = LogFactory.getLog(JMenuMain.class);

	private static final long serialVersionUID = 1L;

	private JMenu jMenuFile = null;
	private JMenuItem jMenuItemFileManage = null;
	private JMenuItem jMenuItemIdentify = null;
	private JMenuItem jMenuItemReport = null;
	private JMenuItem jMenuItemExit = null;

	private JMenu jMenuTool = null;
	private JMenuItem jMenuItemSyncFromServer = null;
	private JMenuItem jMenuItemSyncToServer = null;
	private JMenuItem jMenuItemSPDXAutoIdentify = null;
	
	private JMenuItem jMenuItemStopSendingIdentificationThread = null;
	private JMenuItem jMenuItemResumeSendingIdentificationThread = null;
		
	private JMenu jMenuSetting = null;
	private JMenuItem jMenuItemProjectAnalysis = null;	
	private JMenuItem jMenuItemProxyServer = null;
	private JMenuItem jMenuItemReportProperties = null;

	private JMenu jMenuHelp = null;
	private JMenuItem jMenuShowErrorQueue = null;
	private JMenuItem jMenuShowIdentifyQueue = null;
	private JMenuItem jMenuItemAbout = null;

	private JPanManageMain jPanManageMain;
	private JTabbedPaneMain jTabbedPaneMain = null;

	public JMenuMain(JTabbedPaneMain tabMain ) {
		this.jPanManageMain = tabMain.getJPanManageMain();
		this.jTabbedPaneMain = tabMain;
		initialize();
	}

	/**
	 * This method initializes jJMenuBarMain	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private void initialize() {
		this.add(getJMenuFile());
		this.add(getJMenuTool());
		this.add(getJMenuSetting());
		this.add(getJMenuHelp());
	}

	/**
	 * This method initializes jMenuFile	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.setText("File");
			jMenuFile.add(getJMenuItemAnalysis());
			jMenuFile.add(getJMenuItemIdentify());
			jMenuFile.add(getJMenuItemReport());
			jMenuFile.addSeparator();
			jMenuFile.add(getJMenuItemExit());

		}
		return jMenuFile;
	}

	/**
	 * This method initializes jMenuTool	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuTool() {
		if (jMenuTool == null) {
			jMenuTool = new JMenu();
			jMenuTool.setText("Tool");

			jMenuTool.add(getJMenuItemSyncFromServer());
			jMenuTool.add(getJMenuItemSyncToServer());
			jMenuTool.add(getJMenuItemSPDXAutoIdentify());

			jMenuTool.addSeparator();
			jMenuTool.add(getJMenuItemStopIdentificationThread());
			jMenuTool.add(getJMenuItemResumeIdentificationThread());
			
		}
		return jMenuTool;
	}

	private JMenuItem getJMenuItemResumeIdentificationThread() {
		if (jMenuItemResumeSendingIdentificationThread== null) {
			jMenuItemResumeSendingIdentificationThread = new JMenuItem();
			jMenuItemResumeSendingIdentificationThread.setText("Resume Sending Identification Thread");
			jMenuItemResumeSendingIdentificationThread.setEnabled(false);
			jMenuItemResumeSendingIdentificationThread.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jMenuItemResumeSendingIdentificationThread actionPerformed()");
					mEventHandler.handle(EventHandler.MAN_TOOL_RESUME_IDENTIFICATION_THREAD);
				}
			});
		}
		return jMenuItemResumeSendingIdentificationThread;
	}

	private JMenuItem getJMenuItemStopIdentificationThread() {
		if (jMenuItemStopSendingIdentificationThread== null) {
			jMenuItemStopSendingIdentificationThread = new JMenuItem();
			jMenuItemStopSendingIdentificationThread.setText("Stop Sending Identification Thread");
			jMenuItemStopSendingIdentificationThread.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jMenuItemStopSendingIdentificationThread actionPerformed()");
					mEventHandler.handle(EventHandler.MAN_TOOL_STOP_IDENTIFICATION_THREAD);
				}
			});
		}
		return jMenuItemStopSendingIdentificationThread;
	}

	private JMenu getJMenuSetting() {
		if (jMenuSetting == null) {
			jMenuSetting = new JMenu();
			jMenuSetting.setText("Setting");
			jMenuSetting.add(getJMenuItemProjectAnalysis());
			jMenuSetting.add(getJMenuItemProxyServer());
			jMenuSetting.add(getJMenuItemReportProperties());
		}
		return jMenuSetting;
	}	

	private JMenuItem getJMenuItemProxyServer() {
		if (jMenuItemProxyServer == null) {
			jMenuItemProxyServer = new JMenuItem();
			jMenuItemProxyServer.setText("Proxy");
			jMenuItemProxyServer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jMenuItemProxyServer actionPerformed()");
					mEventHandler.handle(EventHandler.MAN_SETTING_PROXY_SERVER);
				}
			});
		}
		return jMenuItemProxyServer;
	}

	/**
	 * This method initializes jMenuHelp	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuHelp() {
		if (jMenuHelp == null) {
			jMenuHelp = new JMenu();
			jMenuHelp.setText("Help");
			jMenuHelp.add(getJMenuShowIdentifyQueue());
			jMenuHelp.add(getJMenuShowErrorQueue());
			jMenuHelp.add(getJMenuItemAbout());
		}
		return jMenuHelp;
	}

	/**
	 * This method initializes jMenuItemAnalysis	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAnalysis() {
		if (jMenuItemFileManage == null) {
			jMenuItemFileManage = new JMenuItem();
			jMenuItemFileManage.setText("Manage");
			jMenuItemFileManage.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_FILE_MANAGE);
				}
			});
		}
		return jMenuItemFileManage;
	}	
	
	/**
	 * This method initializes jMenuItemProperties	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemIdentify() {
		if (jMenuItemIdentify == null) {
			jMenuItemIdentify = new JMenuItem();
			jMenuItemIdentify.setText("Identify");
			jMenuItemIdentify.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_FILE_IDENTIFY);
				}
			});
		}
		return jMenuItemIdentify;
	}
	
	/**
	 * This method initializes jMenuItemProperties	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemReport() {
		if (jMenuItemReport == null) {
			jMenuItemReport = new JMenuItem();
			jMenuItemReport.setText("Report");
			jMenuItemReport.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_FILE_REPORT);
				}
			});
		}
		return jMenuItemReport;
	}
	
	private JMenuItem getJMenuItemExit() {
		if (jMenuItemExit == null) {
			jMenuItemExit = new JMenuItem();
			jMenuItemExit.setText("Exit");
			jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_FILE_EXIT);

				}
			});
		}
		return jMenuItemExit;
	}

	/**
	 * This method initializes jMenuItemProperties	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemProjectAnalysis() {
		if (jMenuItemProjectAnalysis == null) {
			jMenuItemProjectAnalysis = new JMenuItem();
			jMenuItemProjectAnalysis.setText("Project Analysis");
			jMenuItemProjectAnalysis.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("Monitoring Interval menu click"); 
					mEventHandler.handle(EventHandler.MAN_SETTING_MONITOR_INTERVAL);
				}
			});
		}
		return jMenuItemProjectAnalysis;
	}	

	public JMenuItem getJMenuItemSyncFromServer() {
		if (jMenuItemSyncFromServer == null) {
			jMenuItemSyncFromServer = new JMenuItem();
			jMenuItemSyncFromServer.setText("Sync from Server");
			jMenuItemSyncFromServer.setEnabled(false);
			jMenuItemSyncFromServer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					log.debug("Sync from Server clicked");
					
					String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
					if(IdentifyQueue.getInstance().isIdentifyCompleted(projectName) == false) {
						
						log.debug("Identification for \""+projectName+"\" is not completed yet.");
						
						String message = "Identification for \""+projectName+"\" is not completed yet.\n"+
						  			"You should click \"sync to server\" before \"sync from server\" in this case.";
						JOptionPane.showMessageDialog(
								null, 
								message,
								"Identification is not completed",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					mEventHandler.handle(EventHandler.MAN_TOOL_SYNC_FROM_SERVER);
				}
			});
		}
		return jMenuItemSyncFromServer;
	}

	private JMenuItem getJMenuItemSyncToServer() {
		if (jMenuItemSyncToServer == null) {
			jMenuItemSyncToServer = new JMenuItem();
			jMenuItemSyncToServer.setText("Sync to Server (flush identify queue)");
			jMenuItemSyncToServer.setEnabled(true);
			jMenuItemSyncToServer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("Sync to Server clicked"); 
					
					String title = "Sync to server - "+IdentifyMediator.getInstance().getSelectedProjectName();
					String message = "OSI will flush all items in identify queue without UI update.\n"+
					                 "You are strongly suggested to click \"Sync From Server\" after completing this task.\n"+
					                 "Do you want to progress it now?";
					int yesNo = JOptionPane.showConfirmDialog(
							null,
							message,
							title,
							JOptionPane.YES_NO_OPTION);

					if(yesNo == JOptionPane.NO_OPTION)
						return;
					
					mEventHandler.handle(EventHandler.MAN_TOOL_SYNC_TO_SERVER);
				}
			});
		}
		return jMenuItemSyncToServer;
	}

	public JMenuItem getJMenuItemSPDXAutoIdentify() {
		if (jMenuItemSPDXAutoIdentify == null) {
			jMenuItemSPDXAutoIdentify = new JMenuItem();
			jMenuItemSPDXAutoIdentify.setText("Auto Identify (SPDX)");
			jMenuItemSPDXAutoIdentify.setEnabled(false);
			jMenuItemSPDXAutoIdentify.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("Auto Identify (SPDX) clicked"); 
					mEventHandler.handle(EventHandler.MAN_TOOL_AUTO_IDENTIFY_SPDX);
				}
			});
		}
		return jMenuItemSPDXAutoIdentify;
	}

	/**
	 * This method initializes jMenuItemAbout	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAbout() {
		if (jMenuItemAbout == null) {
			jMenuItemAbout = new JMenuItem();
			jMenuItemAbout.setText("About");
			jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("getJMenuItemAbout actionPerformed()"); 
					mEventHandler.handle(EventHandler.MAN_HELP_ABOUT);
				}
			});
		}
		return jMenuItemAbout;
	}

	private JMenuItem getJMenuShowErrorQueue() {
		if (jMenuShowErrorQueue == null) {
			jMenuShowErrorQueue = new JMenuItem();
			jMenuShowErrorQueue.setText("Show not identified item(s) with error.");
			jMenuShowErrorQueue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_HELP_SHOW_ERROR_QUEUE);
				}
			});
		}
		return jMenuShowErrorQueue;
	}

	private JMenuItem getJMenuShowIdentifyQueue() {
		if (jMenuShowIdentifyQueue == null) {
			jMenuShowIdentifyQueue = new JMenuItem();
			jMenuShowIdentifyQueue.setText("Show identifying item(s)");
			jMenuShowIdentifyQueue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mEventHandler.handle(EventHandler.MAN_HELP_SHOW_IDENTIFY_QUEUE);
				}
			});
		}
		return jMenuShowIdentifyQueue;
	}

	/**
	 * This method initializes jMenuItemProperties	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemReportProperties() {
		if (jMenuItemReportProperties == null) {
			jMenuItemReportProperties = new JMenuItem();
			jMenuItemReportProperties.setText("Report");
			jMenuItemReportProperties.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("getJMenuItemReportProperties actionPerformed()");
					mEventHandler.handle(EventHandler.MAN_SETTING_REPORT_PROPERTY);
				}
			});
		}
		return jMenuItemReportProperties;
	}

	private EventHandler mEventHandler = new EventHandler();
	
	class EventHandler {
		protected static final int MAN_FILE_MANAGE = 11;
		protected static final int MAN_FILE_IDENTIFY = 12;
		protected static final int MAN_FILE_REPORT = 13;
		protected static final int MAN_FILE_EXIT = 15;

		protected static final int MAN_TOOL_SYNC_FROM_SERVER = 21;
		protected static final int MAN_TOOL_AUTO_IDENTIFY_SPDX=23;
		protected static final int MAN_TOOL_SYNC_TO_SERVER = 24;
		protected static final int MAN_TOOL_STOP_IDENTIFICATION_THREAD = 25;
		protected static final int MAN_TOOL_RESUME_IDENTIFICATION_THREAD = 26;

		protected static final int MAN_TOOL_UPDATE_SPECIFIC = 28;
		protected static final int MAN_TOOL_DISABLE_SPECIFIC = 29;
		protected static final int MAN_TOOL_ENABLE_SPECIFIC = 30;
		
		protected static final int MAN_SETTING_MONITOR_INTERVAL = 31;
		protected static final int MAN_SETTING_PROXY_SERVER = 32;
		protected static final int MAN_SETTING_TEMPLATE = 33;
		protected static final int MAN_SETTING_REPORT_PROPERTY = 34;
		
		protected static final int MAN_HELP_ABOUT = 41;
		protected static final int MAN_HELP_SHOW_ERROR_QUEUE = 43;
		protected static final int MAN_HELP_SHOW_IDENTIFY_QUEUE = 44;

		protected void handle(int pCode) {
			switch(pCode) {	
				case MAN_FILE_MANAGE:
					jTabbedPaneMain.setSelectedIndex(JTabbedPaneMain.TAB_INDEX_MANAGE);
					break;
					
				case MAN_FILE_IDENTIFY:
					jTabbedPaneMain.setSelectedIndex(JTabbedPaneMain.TAB_INDEX_IDENTIFY);
					break;
					
				case MAN_FILE_REPORT:
					jTabbedPaneMain.setSelectedIndex(JTabbedPaneMain.TAB_INDEX_REPORT);
					break;
					
				case MAN_FILE_EXIT:
					ApplicationCloseMgr.getInstance().exit();
		    		break;
					
				case MAN_TOOL_SYNC_FROM_SERVER:
					UserRequestHandler.getInstance().handle(UserRequestHandler.SYNC_FROM_SERVER, null, true, false);
					break;
					
				case MAN_TOOL_SYNC_TO_SERVER:
					{
						IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
						if(thread == null)
							break;
						
						boolean prevIsStopByUser = thread.getIsStopByUser();
						thread.setIsStopByUser(false);
						UserRequestHandler.getInstance().handle(UserRequestHandler.SYNC_TO_SERVER, null, true, false);
						thread.setIsStopByUser(prevIsStopByUser);
					}
					break;
					
				case MAN_TOOL_AUTO_IDENTIFY_SPDX:
					JDlgImportSPDX.getInstance().setVisible(true);
					break;
					
				case MAN_TOOL_STOP_IDENTIFICATION_THREAD:
					{
						IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
						if(thread != null)
							thread.setIsStopByUser(true);
						
						getJMenuItemStopIdentificationThread().setEnabled(false);
						getJMenuItemResumeIdentificationThread().setEnabled(true);
					}
					break;
				
				case MAN_TOOL_RESUME_IDENTIFICATION_THREAD:
					{
						IdentifyThread thread = BackgroundJobManager.getInstance().getIdentifyThread();
						if(thread != null)
							thread.setIsStopByUser(false);
						
						getJMenuItemStopIdentificationThread().setEnabled(true);
						getJMenuItemResumeIdentificationThread().setEnabled(false);
						
					}
				break;
				
				case MAN_TOOL_UPDATE_SPECIFIC:
					UserRequestHandler.getInstance().handle(UserRequestHandler.CUSTOM_COMPONENT_UPDATE, null, true, false);
					break;
					
				case MAN_SETTING_MONITOR_INTERVAL:
					JDlgSetting.getInstance().setSelectedIndex(JDlgSetting.TAB_INDEX_MONITOR);
					JDlgSetting.getInstance().setJPanManageMain(jPanManageMain);
					JDlgSetting.getInstance().setVisible(true);
					break;
					
				case MAN_SETTING_PROXY_SERVER:
					JDlgSetting.getInstance().setSelectedIndex(JDlgSetting.TAB_INDEX_PROXY);
					JDlgSetting.getInstance().setJPanManageMain(jPanManageMain);
					JDlgSetting.getInstance().setVisible(true);
					break;
					
				case MAN_SETTING_REPORT_PROPERTY:
					JDlgSetting.getInstance().setSelectedIndex(JDlgSetting.TAB_INDEX_REPORT);
					JDlgSetting.getInstance().setJPanManageMain(jPanManageMain);
					JDlgSetting.getInstance().setVisible(true);
					break;
				
				case MAN_HELP_SHOW_ERROR_QUEUE:
					JDlgQueueInfo.getInstance().showErrorQueueInfo();
					break;
					
				case MAN_HELP_SHOW_IDENTIFY_QUEUE:
					JDlgQueueInfo.getInstance().showIdentifyQueueInfo();
					break;
					
				case MAN_HELP_ABOUT: 
					JDlgAbout.getAboutDialog().setVisible(true);
					break;	
			}
		}
	}
}
