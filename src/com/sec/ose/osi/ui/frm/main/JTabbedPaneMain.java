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

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.JPanIdentifyMain;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;
import com.sec.ose.osi.ui.frm.main.report.JPanReportMain;

/**
 * JTabbedPaneMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JTabbedPaneMain extends JTabbedPane {

	private static final String TAB_TITLE_MANAGE = "Manage";
	private static final String TAB_TITLE_IDENTIFY = "Identify";
	private static final String TAB_TITLE_REPORT = "Report";
	
	public static final int TAB_INDEX_MANAGE = 0;
	public static final int TAB_INDEX_IDENTIFY = 1;
	public static final int TAB_INDEX_REPORT = 2;
	
	private static final long serialVersionUID = 1L;

	private JPanManageMain jPanManageMain;
	private JPanReportMain jPanReportMain;
	private JPanIdentifyMain jPanIdentifyMain;

	/**
	 * This is the default constructor
	 */
	public JTabbedPaneMain() {
		super();
		initialize();
	}

	public JPanManageMain getJPanManageMain(){
		return jPanManageMain;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(550, 380);
		
		jPanManageMain = new JPanManageMain(this);
		jPanIdentifyMain = new JPanIdentifyMain();
		jPanReportMain = new JPanReportMain();
		
		this.insertTab(TAB_TITLE_MANAGE, null, jPanManageMain, null, TAB_INDEX_MANAGE);
		this.insertTab(TAB_TITLE_IDENTIFY, null, jPanIdentifyMain, null, TAB_INDEX_IDENTIFY);
		this.insertTab(TAB_TITLE_REPORT, null, jPanReportMain, null, TAB_INDEX_REPORT);
		
		this.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
					JTabbedPane pane = (JTabbedPane)e.getSource();
					
					int tabIndex = pane.getSelectedIndex();

					switch(tabIndex) {
						case JTabbedPaneMain.TAB_INDEX_MANAGE:
							jPanManageMain.setProjectTableModelList();
							jPanManageMain.resetTableHederSize();
							break;
						case JTabbedPaneMain.TAB_INDEX_IDENTIFY:
							IdentifyMediator.getInstance().refreshComboProjectName();
							break;
						case JTabbedPaneMain.TAB_INDEX_REPORT:
							jPanReportMain.resetProjectList();
							break;
					}
			}
		});
	}
}