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
package com.sec.ose.osi.ui.frm.main.manage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.data.project.ProjectAnalysisInfo;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.analysis.AnalysisMonitorThread;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.cache.CacheablePanel;
import com.sec.ose.osi.ui.cache.UICache;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.dialog.setting.UEProjectAnalysisSetting;
import com.sec.ose.osi.ui.dialog.showProjectInfo.JDlgProjectInfo;
import com.sec.ose.osi.ui.frm.main.JTabbedPaneMain;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.manage.dialog.ConfirmDialog;
import com.sec.ose.osi.ui.frm.main.manage.dialog.JDlgProjectAdd;
import com.sec.ose.osi.ui.frm.main.manage.dialog.JDlgProjectCreate;
import com.sec.ose.osi.util.tools.DateUtil;
import com.sec.ose.osi.util.tools.ProjectSplitUtil;

/**
 * JPanManageMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanManageMain extends CacheablePanel {
	private static Log log = LogFactory.getLog(JPanManageMain.class);
	public static final int MAX_SIZE_OF_FOLDER_MB = 200;
	private AnalysisMonitorThread monitor = null;
	private JFrame frmOwner;
	private static final String STOP_AUTO_ANALYZE = "Stop monitoring project change to auto-analyze";

	/**
	 * This is the default constructor
	 */
	public JPanManageMain(final JTabbedPaneMain jTabbedPaneMain) {
		super();
		initialize();
		addCacheable();

		ManageMediator.getInstance().setJPanManage(this);
		ManageMediator.getInstance().setProjectTableModel(getProjectTableModel());
		monitor = BackgroundJobManager.getInstance().startAnalysisMonitoThread();

		this.addComponentListener(new ComponentAdapter(){
	    	public void componentResized(ComponentEvent e){
	    		if(jTabbedPaneMain.getSelectedIndex() == JTabbedPaneMain.TAB_INDEX_MANAGE) {
	    			resetTableHederSize();
	    		}
	    	}
	    });
	}
	
	public void showStaus(String text) {
		jLabelStatus.setText(text);
	}
	
	public void loadFromCache() {
		UEAnalysisPanel ueAnalysisPanel = (UEAnalysisPanel)mCache.getUIEntity(UICache.UE_SELECTED_PROJECT_INFO_IN_ANALYSIS);
		ueProjectAnalysisSetting = (UEProjectAnalysisSetting) mCache.getUIEntity(UICache.UE_MONITOR_INTERVAL);
		
		if(ueAnalysisPanel != null) {
			ArrayList<UEProtexProjectInfo> list = ueAnalysisPanel.getAllElements();
			ArrayList<OSIProjectInfo> cacheProjectsList = new ArrayList<OSIProjectInfo>();
			StringBuffer analysisTargetProject = new StringBuffer();
			for(UEProtexProjectInfo projectInfo: list) {
				
				String projectName = projectInfo.getProjectName();
				
				boolean isExisted = UISDKInterfaceManager.getSDKInterface().isExistedProjectName(projectName, null);
				if(isExisted == false) {
					log.debug("add project to analysis table: "+projectName+" - not existed");
					continue;
				}
				log.debug("add project to analysis table: "+projectName);
				if(projectInfo.isAnalysisProject()) {
					analysisTargetProject.append("["+projectInfo.getProjectName()+"] ");
				}
				
				OSIProjectInfo pInfo = UISDKInterfaceManager.getSDKInterface().getProjectInfoByName(projectName);
				if(pInfo != null) {
					cacheProjectsList.add(pInfo);
					pInfo.setManaged(true);
					pInfo.setProjectAnalysisInfo(projectInfo.isAnalysisProject());
					pInfo = ProjectAPIWrapper.loadAnalysisInfo(pInfo);
				}
			}

			if(analysisTargetProject.length() > 0) {
				writeConsoleWithTime("Current analysis target project : "+analysisTargetProject.toString());
			} else {
				writeConsoleWithTime("There is no analysis target project.");
			}

			/** Load Project advanced infomation **/
			while(cacheProjectsList.size() > 0) {
				for(int i=cacheProjectsList.size()-1;i>=0;i--) {
					if(cacheProjectsList.get(i).isLoaded()) {
						cacheProjectsList.get(i).getProjectAnalysisInfo().setAnalysisStatus(cacheProjectsList.get(i).isAnalyzed());
						setTableViewProperty(cacheProjectsList.get(i));
						cacheProjectsList.remove(i);
					}
				}
			}
			
			setProjectTableModelList();
			managedProjectTableModel.checkAnalyzeAll(jTablePjtList);
		}
		
		setProjectAnalysisSetting(ueProjectAnalysisSetting);
	}
	
	public UEProjectAnalysisSetting getUIEntityMonitoringInterval() {
		return ueProjectAnalysisSetting;
	}
	
	ManagedProjectTableModel getProjectTableModel() {
		if(this.managedProjectTableModel == null) {
			managedProjectTableModel = new ManagedProjectTableModel();
		}
		return this.managedProjectTableModel;
	}
	
	private long calcNextAnalyzeTime(int timeCycle, int timeHour, int timeMinite) {
		long timeGap = 0;
		
		Calendar FixedCale = Calendar.getInstance();
		FixedCale.set(Calendar.HOUR_OF_DAY, timeHour);
		FixedCale.set(Calendar.MINUTE, timeMinite);
		FixedCale.set(Calendar.SECOND, 0);
		FixedCale.set(Calendar.MILLISECOND, 0);
		log.debug("starting of fixed time : " + DateUtil.getFormatingTime("%1$tb/%1$te, %1$tI:%1$tM %1$tp",FixedCale.getTimeInMillis()));
		if(timeCycle > 0) {
			// week
			FixedCale.set(Calendar.DAY_OF_WEEK, timeCycle);
			if(FixedCale.getTimeInMillis() < System.currentTimeMillis()) {
				FixedCale.add(Calendar.DAY_OF_MONTH, 7);
			}
		} else {
			// daily
			if(FixedCale.getTimeInMillis() < System.currentTimeMillis()) {
				FixedCale.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		timeGap = FixedCale.getTimeInMillis() - System.currentTimeMillis();
		if(timeGap == 0) { timeGap = 24 * 60 * 60 * 1000;}
		
		return timeGap;
	}
	
	public void setProjectAnalysisSetting(UEProjectAnalysisSetting setting){
		long interval = 0;	// disable
		if(setting != null) {
			ueProjectAnalysisSetting = setting;
			
			ProjectSplitUtil.setNumOfFilesForAProject(ueProjectAnalysisSetting.getProjectSplitFileCountLimit());
			
			int selectedType = ueProjectAnalysisSetting.getSelectedType();
			switch(selectedType) {
			case UEProjectAnalysisSetting.TYPE_DISABLE :
				interval = 0;
				getJLabelMonitoringInterval().setText("* Monitoring Disabled");
				break;
			case UEProjectAnalysisSetting.TYPE_FIXED_TIME :
				int timeCycle = ueProjectAnalysisSetting.getTimeCycle();
				int timeHour = ueProjectAnalysisSetting.getTimeHour();
				int timeMinite = ueProjectAnalysisSetting.getTimeMinite();
				interval = calcNextAnalyzeTime(timeCycle,timeHour,timeMinite);
				getJLabelMonitoringInterval().setText(getMonitoringLabel(timeCycle,timeHour,timeMinite));
				break;
			default :
				interval = (long)ueProjectAnalysisSetting.getMonitorInterval()*60*60*1000;
				getJLabelMonitoringInterval().setText("* Monitoring Interval : "+ueProjectAnalysisSetting.getMonitorInterval()+" hours");
			}
		}

		if(monitor.chkStatus(AnalysisMonitorThread.STATUS_READY)) {
			setTimeInterval(interval);
		}
	}
	
	private String getMonitoringLabel(int timeCycle, int timeHour, int timeMinite) {
		StringBuffer sbCycle = new StringBuffer("* Monitoring Time : ");
		switch(timeCycle) {
		case 1:
			sbCycle.append("Every Sunday ");
			break;
		case 2:
			sbCycle.append("Every Monday ");
			break;
		case 3:
			sbCycle.append("Every Tuesday ");
			break;
		case 4:
			sbCycle.append("Every Wednesday ");
			break;
		case 5:
			sbCycle.append("Every Thursday ");
			break;
		case 6:
			sbCycle.append("Every Friday ");
			break;
		case 7:
			sbCycle.append("Every Saturday ");
			break;
		default :
			sbCycle.append("Every Day ");
			break;
		}
		if(timeHour > 11) {
			sbCycle.append("PM ");
		} else {
			sbCycle.append("AM ");
		}
		if(timeHour < 10) sbCycle.append("0");
		sbCycle.append(timeHour).append(" : ");
	
		if(timeMinite < 10) sbCycle.append("0");
		sbCycle.append(timeMinite);
		
		return sbCycle.toString();
	}

	@SuppressWarnings("deprecation")
	private void setTimeInterval(long interval) {

		if(interval <= 0) {
			monitor.suspend();
			showStaus(STOP_AUTO_ANALYZE);
			log.debug("Suspend");
			writeConsoleWithTime(STOP_AUTO_ANALYZE);
		} else {
			monitor.setTimeInterval(interval);
			monitor.setStatus(AnalysisMonitorThread.STATUS_READY);
			monitor.resume();
			writeConsoleWithTime("Next periodic analyze time is "+DateUtil.getFormatingTime("%1$tb/%1$te, %1$tI:%1$tM %1$tp",interval + System.currentTimeMillis()));
		}
	}
	
	public void saveToCache() {
		mCache.setUIEntity(UICache.UE_SELECTED_PROJECT_INFO_IN_ANALYSIS, exportUIEntity());
		mCache.setUIEntity(UICache.UE_MONITOR_INTERVAL, ueProjectAnalysisSetting);
	}
	
	public UIEntity exportUIEntity() {

		UEAnalysisPanel ueAnalysisPanel = new UEAnalysisPanel();
		for(OSIProjectInfo projectInfo: managedProjectTableModel.getManagedProjects()) {
			UEProtexProjectInfo ueProtexProjectInfo = new UEProtexProjectInfo(projectInfo);
			ueAnalysisPanel.add(ueProtexProjectInfo);
		}

		return ueAnalysisPanel;
	}

	public boolean checkFolderSize(String sourcePath) {
		DirectoryInfo di = new DirectoryInfo(sourcePath);
		return (di.checkFileSize(MAX_SIZE_OF_FOLDER_MB*1024*1024));
	}
	
	void refreshMonitorIntervalInfo() {
		setProjectAnalysisSetting(ueProjectAnalysisSetting);
	}

	class AnalyzeActionListener implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("Analyze actionPerformed()");

			ArrayList<OSIProjectInfo> projectInfoLists = getProjectTableModel().getAnalysisProjects();
			log.debug("-----------projectInfoLists---------"+projectInfoLists.size());
			if(projectInfoLists.size() == 0){
				JOptionPane.showMessageDialog(
						null, 
						"No project(s) is selected.\n" +
						"Please click the checkbox to select.",
						"Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			// TODO : Analysis of local source code is not supported in Protex 6.X
			//monitor.requestAnalysisNow();

			JOptionPane.showMessageDialog(
					null, 
					"Analysis of local source code is not supported (in current Protex SDK 6.X)\n" +
					"This function is supported since version 7\n" +
					"Please analyze via Protex Client(Web) and add project in OSIT.",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;

		}
	}

	class CancelActionListener implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("Cancel actionPerformed()");
			monitor.setStatus(AnalysisMonitorThread.STATUS_CANCELLING);
			if(monitor != null) {
				monitor.cancelAnalysis();
			}
		}
	}

	//-------------modify start-------------------------------------------------//
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanelProjectList = null;
	private JPanel jPanelButton = null;
	private JPanel jPanelConsol = null;
	private JButton jButtonAnalyze = null;
	private JButton jButtonCancel = null;
	private JButton jButtonAdd = null;
	private JButton jButtonCreate = null;
	private JTextArea jTextAreaConsole = null;
	private JProgressBar jAnalyzeProgressBar = null;
	private JLabel jLabelStatus = null;
	private JLabel jLabelMonitoringInterval = null;
	private JSplitPane jSplitPane = null;
	private JScrollPane jScrollPanePjtList = null;
	private JTable jTablePjtList = null;
	private JScrollPane jScrollPaneConsole = null;
	
	private ManagedProjectTableModel managedProjectTableModel = null;
	private UEProjectAnalysisSetting ueProjectAnalysisSetting = null; 
	
	JLabel getJLabelStatus() {
		if(jLabelStatus == null) {
			jLabelStatus = new JLabel(STOP_AUTO_ANALYZE);
		}
		return jLabelStatus;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.weighty = 1.0;
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		gridBagConstraints11.insets = new Insets(10, 10, 10, 0);
		gridBagConstraints11.ipadx = 0;
		gridBagConstraints11.ipady = 0;
		gridBagConstraints11.weightx = 1.0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints1.gridy = 0;
		this.setSize(600, 700);
		this.setLayout(new GridBagLayout());
		this.add(getJPanelButton(), gridBagConstraints1);
		this.add(getJSplitPane(), gridBagConstraints11);
	}

	/**
	 * This method initializes jPanelProjectList	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelProjectList() {
		if (jPanelProjectList == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.weightx = 0.1;
			gridBagConstraints8.insets = new Insets(0, 0, 0, 10);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.CENTER;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 10);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints.gridx = 0;
			jPanelProjectList = new JPanel();
			jPanelProjectList.setLayout(new GridBagLayout());
			jPanelProjectList.setBorder(BorderFactory.createTitledBorder(null, "Project", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelProjectList.add(getJButtonAdd(), gridBagConstraints8);
			jPanelProjectList.add(getJButtonCreate(), gridBagConstraints2);
			jPanelProjectList.add(getJScrollPanePjtList(), gridBagConstraints);
		}
		return jPanelProjectList;
	}

	/**
	 * This method initializes jPanelButton	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelButton() {
		if (jPanelButton == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(10, 10, 10, 10);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			jPanelButton = new JPanel();
			jPanelButton.setLayout(new GridBagLayout());
			jPanelButton.add(getJButtonAnalyze(), gridBagConstraints7);
			jPanelButton.add(getJButtonCancel(), gridBagConstraints3);
		}
		return jPanelButton;
	}

	/**
	 * This method initializes jPanelConsol	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelConsol() {
		if (jPanelConsol == null) {
			GridBagConstraints gridBagConstraintsForConsole = new GridBagConstraints();
			gridBagConstraintsForConsole.fill = GridBagConstraints.BOTH;
			gridBagConstraintsForConsole.weightx = 1.0;
			gridBagConstraintsForConsole.weighty = 1.0;
			gridBagConstraintsForConsole.gridwidth = 3;
			gridBagConstraintsForConsole.insets = new Insets(10, 10, 10, 10);

			GridBagConstraints gridBagConstraintsForProgressBar = new GridBagConstraints();
			gridBagConstraintsForProgressBar.fill = GridBagConstraints.BOTH;
			gridBagConstraintsForProgressBar.gridx = 0;
			gridBagConstraintsForProgressBar.gridy = 1;
			gridBagConstraintsForProgressBar.weightx = 1.0;
			gridBagConstraintsForProgressBar.gridwidth = 3;
			gridBagConstraintsForProgressBar.insets = new Insets(10, 10, 10, 10);

			GridBagConstraints gridBagConstraintsForLabelStatus = new GridBagConstraints();
			gridBagConstraintsForLabelStatus.fill = GridBagConstraints.BOTH;
			gridBagConstraintsForLabelStatus.gridx = 0;
			gridBagConstraintsForLabelStatus.gridy = 2;
			gridBagConstraintsForLabelStatus.weightx = 0.1;
			gridBagConstraintsForLabelStatus.insets = new Insets(0, 10, 10, 0);

			GridBagConstraints gridBagConstraintsForMonitoringInterfal = new GridBagConstraints();
			gridBagConstraintsForMonitoringInterfal.fill = GridBagConstraints.BOTH;
			gridBagConstraintsForMonitoringInterfal.gridx = 2;
			gridBagConstraintsForMonitoringInterfal.gridy = 2;
			gridBagConstraintsForMonitoringInterfal.weightx = 0.1;
			gridBagConstraintsForMonitoringInterfal.insets = new Insets(0, 0, 10, 10);

			jPanelConsol = new JPanel();
			jPanelConsol.setLayout(new GridBagLayout());
			jPanelConsol.setBorder(BorderFactory.createTitledBorder(null, "Console", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelConsol.add(getJScrollPaneConsole(), gridBagConstraintsForConsole);
			jPanelConsol.add(getJLabelStatus(), gridBagConstraintsForLabelStatus);
			jPanelConsol.add(getJAnalyzeProgressBar(), gridBagConstraintsForProgressBar);
			jPanelConsol.add(getJLabelMonitoringInterval(), gridBagConstraintsForMonitoringInterfal);
					}
		return jPanelConsol;
	}

	/**
	 * This method initializes jButtonAnalyze	
	 * 	
	 * @return javax.swing.JButton	
	 */
	JButton getJButtonAnalyze() {
		if (jButtonAnalyze == null) {
			jButtonAnalyze = new JButton();
			jButtonAnalyze.setText("Analyze");
			jButtonAnalyze.addActionListener(new AnalyzeActionListener());
		}
		return jButtonAnalyze;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.setEnabled(false);
			jButtonCancel.addActionListener(new CancelActionListener());
		}
		return jButtonCancel;
	}
	

	/**
	 * This method initializes jButtonSearch	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("   Add   ");
			jButtonAdd.setPreferredSize(new Dimension(75, 28));
			jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() : Add Button Clicked!");
					if(OSIProjectInfoMgr.getInstance().getAllProjects().size() > 0) {
						JDlgProjectAdd dlgProjectList = new JDlgProjectAdd(frmOwner);
						dlgProjectList.setManageMain(JPanManageMain.this);
						dlgProjectList.setSize(600, 300);
						WindowUtil.locateCenter(dlgProjectList);
						dlgProjectList.setVisible(true);
					} else {
						String[] buttonOK = {"OK"};
						JOptionPane.showOptionDialog(null, "There is no project", "Search Project", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, buttonOK, "OK");
						return;
					}
				}
			});
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButtonCreate	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCreate() {
		if (jButtonCreate == null) {
			jButtonCreate = new JButton();
			jButtonCreate.setText("Create");
			jButtonCreate.setPreferredSize(new Dimension(75, 28));
			jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() : Create Button Clicked!");
					JDlgProjectCreate dlgProjectCreate = new JDlgProjectCreate(frmOwner);
					dlgProjectCreate.setTarget(JPanManageMain.this);
					dlgProjectCreate.setSize(600, 300);
					WindowUtil.locateCenter(dlgProjectCreate);
					dlgProjectCreate.setVisible(true);
				}
			});
		}
		return jButtonCreate;
	}
	

	/**
	 * This method initializes jTextAreaConsole	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextAreaConsole() {
		if (jTextAreaConsole == null) {
			jTextAreaConsole = new JTextArea();
			jTextAreaConsole.setEditable(false);
		}
		return jTextAreaConsole;
	}

	void writeConsoleWithTime(String text) {
		jTextAreaConsole.append(DateUtil.getCurrentTime(" [%1$tb %1$te, %1$tY %1$tI:%1$tM:%1$tS %1$tp] ")+text+"\n");
		int pos = jTextAreaConsole.getText().length(); 
		jTextAreaConsole.setCaretPosition(pos);
	}
	
	JProgressBar getJAnalyzeProgressBar() {
		if (jAnalyzeProgressBar == null) {
			jAnalyzeProgressBar = new JProgressBar(0,100);
			jAnalyzeProgressBar.setVisible(false);
			jAnalyzeProgressBar.setForeground(Color.GREEN);
		}
		return jAnalyzeProgressBar;
	}
	
	/**
	 * This method initializes jScrollPaneConsole	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneConsole() {
		if (jScrollPaneConsole == null) {
			jScrollPaneConsole = new JScrollPane();
			jScrollPaneConsole.setViewportView(getJTextAreaConsole());
		}
		return jScrollPaneConsole;
	}

	/**
	 * This method initializes jLabelMonitoringInterval	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JLabel getJLabelMonitoringInterval() {
		if (jLabelMonitoringInterval == null) {
			jLabelMonitoringInterval = new JLabel();
			jLabelMonitoringInterval.setHorizontalAlignment(JLabel.RIGHT);
		}
		return jLabelMonitoringInterval;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setDividerLocation(400);
			jSplitPane.setResizeWeight(1.0D);
			jSplitPane.setTopComponent(getJPanelProjectList());
			jSplitPane.setBottomComponent(getJPanelConsol());
			jSplitPane.setDividerSize(5);
			jSplitPane.setBorder(null);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jScrollPanePjtList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPanePjtList() {
		if (jScrollPanePjtList == null) {
			jScrollPanePjtList = new JScrollPane();
			jScrollPanePjtList.setViewportView(getJTablePjtList());
//			jScrollPanePjtList.getViewport().setBackground(Color.white);
			jScrollPanePjtList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPanePjtList;
	}

	/**
	 * This method initializes jTablePjtList	
	 * 	
	 * @return javax.swing.JTable	
	 */
  	@SuppressWarnings("unchecked")
	private JTable getJTablePjtList() {
		if (jTablePjtList == null) {
			
			jTablePjtList = new JTable(getProjectTableModel());
			getProjectTableModel().setColumnWidth(jTablePjtList);
			getProjectTableModel().setColumnType(jTablePjtList);
			jTablePjtList.addMouseListener(new ProjectTableClickAction());
			
			TableRowSorter sorter = new TableRowSorter(managedProjectTableModel);  
			sorter.setComparator(ManagedProjectTableModel.COL_DELETE, new ProjectTableComparator());  
			sorter.setComparator(ManagedProjectTableModel.COL_PROJECT_NAME, new ProjectTableComparator());  
			sorter.setComparator(ManagedProjectTableModel.COL_ANALYZE_TARGET, new ProjectTableComparator());  
			sorter.setComparator(ManagedProjectTableModel.COL_ANALYZE_STATUS, new ProjectTableComparator());   
			sorter.setComparator(ManagedProjectTableModel.COL_SOURCE_LOCATION, new ProjectTableComparator());   
			sorter.toggleSortOrder(ManagedProjectTableModel.COL_PROJECT_NAME);
			jTablePjtList.setRowSorter(sorter);			
		}
		return jTablePjtList;
	}
  	
  	@SuppressWarnings("unchecked")
	class ProjectTableComparator implements Comparator{
  		
  		public int compare(Object o1, Object o2) {   
  			String s1 = null;
  			String s2 = null;
  			
  			if(o1 instanceof String){
  				s1 = (String)o1;
  				s2 = (String)o2;
  			}
  			else if(o1 instanceof StatusIcon){
  				StatusIcon icon1 = (StatusIcon)o1;
  				StatusIcon icon2 = (StatusIcon)o2;
  				
  				s1 = icon1.getStatusText();
  				s2 = icon2.getStatusText();
   			}
  			else if(o1 instanceof FileBrowser){
  				FileBrowser fb1 = (FileBrowser)o1;
  				FileBrowser fb2 = (FileBrowser)o2;
  				
  				s1 = fb1.getFileLocation();
  				s2 = fb2.getFileLocation();
   			}
  			
  			
  			if(s1 == null) s1 = "";
  			if(s2 == null) s2 = "";
  			
	       	return s1.compareTo(s2);   
      }
  	}

	class ProjectTableClickAction extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int col = jTablePjtList.getSelectedColumn();
			int targetIndex = jTablePjtList.getSelectedRow();
			
			if(col == ManagedProjectTableModel.COL_DELETE) {
				
				if(targetIndex >=0) {
					if(ConfirmDialog.showConfirmDialog(frmOwner,"Do you want to exclude this project(s) from manage tab ?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION){
						String delProject = jTablePjtList.getValueAt(targetIndex,ManagedProjectTableModel.COL_PROJECT_NAME).toString();
						managedProjectTableModel.deleteRow(delProject);
						
					}
				}
				
			} else if(col == ManagedProjectTableModel.COL_ANALYZE_TARGET){
				
				managedProjectTableModel.checkAnalyzeAll(jTablePjtList);
				
			} else if(col == ManagedProjectTableModel.COL_PROJECT_NAME) {
				
				if(e.getClickCount() != IdentificationConstantValue.MOUSE_DOUBLE_CLICK) {
					return;
				}
				String projectName = jTablePjtList.getValueAt(targetIndex,ManagedProjectTableModel.COL_PROJECT_NAME).toString();
				JDlgProjectInfo.getInstance().showInfoDlg(projectName);
				
			}
		}
	}
	

	public void setOwner(JFrame f){
		frmOwner = f;
	}

	public JFrame getOwner() {
		return frmOwner;
	}

	private void setTableViewProperty(OSIProjectInfo item){
		ProjectAnalysisInfo mpi = item.getProjectAnalysisInfo();
		FileBrowser fb = new FileBrowser();
		fb.setOwner(frmOwner);
		fb.setProjectInfo(item);
		fb.setTableModel(managedProjectTableModel);
		mpi.setFileComp(fb);
	}
	
	public void addNewProject(OSIProjectInfo item){
		//for Table View
		setTableViewProperty(item);
		managedProjectTableModel.addRow(item);
	}
	
	public Collection<OSIProjectInfo> getProjectList(){
		return this.managedProjectTableModel.getManagedProjects();
	}
	
	public void updateProjectList(){
		managedProjectTableModel.reload();
		for(OSIProjectInfo managedProject: OSIProjectInfoMgr.getInstance().getManagedProjectInfo().getProjects()) {
			setTableViewProperty(managedProject);
		}
		managedProjectTableModel.checkAnalyzeAll(jTablePjtList);
	}
	
	public void resetTableHederSize(){
		managedProjectTableModel.resetTableHaderSize(jTablePjtList, jScrollPaneConsole.getSize().width);
	}
  	
	public void setProjectTableModelList() {
		managedProjectTableModel.reload();
	}

	public void addManagedProject(OSIProjectInfo xOSIProjectInfo) {

		if(xOSIProjectInfo.isManaged() == false) {
			xOSIProjectInfo.setManaged(true);
			xOSIProjectInfo.setProjectAnalysisInfo(xOSIProjectInfo.isAnalyzed());
			updateProjectList();

			FileBrowser fb = new FileBrowser();
			fb.setProjectInfo(xOSIProjectInfo);
			fb.setTableModel(this.managedProjectTableModel);
			xOSIProjectInfo.getProjectAnalysisInfo().setFileComp(fb);
		}
	}

}
