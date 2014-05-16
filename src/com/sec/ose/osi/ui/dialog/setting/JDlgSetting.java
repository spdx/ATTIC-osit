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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;

/**
 * JDlgSetting
 * 
 * @author hankido.lee
 *
 */
public class JDlgSetting extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;
	
	private JPanProjectAnalysisSetting mJPanMonitoring = null;
	private JPanProxySetting mJPanProxy = null;
	private JPanReportProperty mJPanReport = null;
	private JScrollPane jScrollPaneMonitoring = null;
	private JScrollPane jScrollPaneProxy = null;
	private JScrollPane jScrollPaneReport = null;
	private volatile static JDlgSetting instance = null;
	
	public static final int TAB_INDEX_MONITOR = 0;
	public static final int TAB_INDEX_PROXY = 1;
	public static final int TAB_INDEX_REPORT = 2;
	
	private static final String TAB_TITLE_MONITOR = "Project Analysis";
	private static final String TAB_TITLE_PROXY = "Proxy Server";
	private static final String TAB_TITLE_REPORT = "Report";
	
	public static JDlgSetting getInstance() {
		if(instance == null) {
			synchronized(JDlgSetting.class) {
				if(instance == null) {
					instance = new JDlgSetting();
				}
			}
		}
		return instance;
	}
	
	private JDlgSetting() {
		initialize();
	}

	private void initialize() {
		this.setSize(540, 320);
		this.setModal(true);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setTitle("Setting");
		WindowUtil.locateCenter(this);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 503;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(0, 0, 0, 0);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJTabbedPane(), gridBagConstraints);
		}
		return jContentPane;
	}

	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.insertTab(TAB_TITLE_MONITOR, null, getJScrollPaneMonitoring(), null, TAB_INDEX_MONITOR);
			jTabbedPane.insertTab(TAB_TITLE_PROXY, null, getJScrollPaneProxy(), null, TAB_INDEX_PROXY);
			jTabbedPane.insertTab(TAB_TITLE_REPORT, null, getJScrollPaneReport(), null, TAB_INDEX_REPORT);
		}
		
		return jTabbedPane;
	}
	
	private JScrollPane getJScrollPaneMonitoring() {
		if (jScrollPaneMonitoring == null) {
			mJPanMonitoring = new JPanProjectAnalysisSetting(this);
			jScrollPaneMonitoring = new JScrollPane(mJPanMonitoring);
			jScrollPaneMonitoring.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneMonitoring.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneMonitoring.setBorder(null);
		}
		return jScrollPaneMonitoring;
	}

	private JScrollPane getJScrollPaneProxy() {
		if (jScrollPaneProxy == null) {
			mJPanProxy = new JPanProxySetting(this);
			jScrollPaneProxy = new JScrollPane(mJPanProxy);
			jScrollPaneProxy.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneProxy.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneProxy.setBorder(null);
		}
		return jScrollPaneProxy;
	}

	private JScrollPane getJScrollPaneReport() {
		if (jScrollPaneReport == null) {
			mJPanReport = new JPanReportProperty(this);
			jScrollPaneReport = new JScrollPane(mJPanReport);
			jScrollPaneReport.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneReport.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneReport.setBorder(null);
		}
		return jScrollPaneReport;
	}

	public void setJPanManageMain(JPanManageMain pManageMain) {
		mJPanMonitoring.setJPanManageMain(pManageMain);
	}
	
	public void setSelectedIndex(int tabIndexMonitor) {
		jTabbedPane.setSelectedIndex(tabIndexMonitor);
		
	}
}
