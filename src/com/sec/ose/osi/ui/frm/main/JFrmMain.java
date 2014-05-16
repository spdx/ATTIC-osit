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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sec.ose.osi.Version;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.cache.CacheableMgr;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;

/**
 * JFrmMain
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JFrmMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanelTabbedPaneWrapper = null;
	private JTextField jTextFieldIdentifyQueueStatusBar = null;
	private JMenuMain jJMenuMain = null;
	JTabbedPaneMain jTabbedPaneMain = null;
	
	/**
	 * This is the default constructor
	 */
	public JFrmMain() {
		super();
		initialize();
		
		this.setResizable(true);
		this.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						CacheableMgr.getInstance().saveToCache();
					}
				}
		);
		WindowUtil.locateCenter(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setMinimumSize(new Dimension(1150, 780));
		this.setJMenuBar(getJMenuBarMain());

		this.setContentPane(getJContentPane());
		this.setTitle(Version.getApplicationVersionInfo());
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());

		this.add(getTabbedPaneWrapper(), BorderLayout.CENTER);
		this.add(new JLabel("       "), BorderLayout.EAST);
		this.add(new JLabel("       "), BorderLayout.WEST);
		this.getJTextFieldIdentifyQueueStatusBar().setText("Connection on " + UISharedData.getInstance().getConnectionInfo());
		UISharedData.getInstance().setjTextFieldIdentifyQueueStatusBar(getJTextFieldIdentifyQueueStatusBar());
		
		IdentifyMediator.getInstance().setJMenuMain(getJMenuBarMain());
	}
	
	/**
	 * This method initializes jJMenuBarMain	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuMain getJMenuBarMain() {
		if (jJMenuMain == null) {
			jJMenuMain = new JMenuMain(getJTabbedPaneMain());
		}
		return jJMenuMain;
	}
	
	private JPanel getTabbedPaneWrapper() {

		if(jPanelTabbedPaneWrapper == null) {
			jPanelTabbedPaneWrapper = new JPanel();
			jPanelTabbedPaneWrapper.setLayout(new BorderLayout());
			jPanelTabbedPaneWrapper.add(getJTabbedPaneMain(), BorderLayout.CENTER);
			jPanelTabbedPaneWrapper.add(new JLabel(" "), BorderLayout.SOUTH);
		}
		
		return jPanelTabbedPaneWrapper;
	}

	private JTabbedPaneMain getJTabbedPaneMain() {
		if(jTabbedPaneMain == null) {
			jTabbedPaneMain = new JTabbedPaneMain();
		}
		return jTabbedPaneMain; 
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTextFieldIdentifyQueueStatusBar(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextFieldStatusBar	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldIdentifyQueueStatusBar() {
		if (jTextFieldIdentifyQueueStatusBar == null) {
			jTextFieldIdentifyQueueStatusBar = new JTextField();
			jTextFieldIdentifyQueueStatusBar.setEnabled(false);
			jTextFieldIdentifyQueueStatusBar.setEditable(false);
			jTextFieldIdentifyQueueStatusBar.setDisabledTextColor(Color.GRAY);
		}
		return jTextFieldIdentifyQueueStatusBar;
	}

}
