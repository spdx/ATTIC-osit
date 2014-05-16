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
package com.sec.ose.osi.ui.dialog.showProjectInfo;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * JDlgProjectInfo
 * @author hankido.lee
 *
 */
public class JDlgProjectInfo extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTextPane textPane = null;
	
	private volatile static JDlgProjectInfo instance = null;
	
	/**
	 * @param owner
	 */
	private JDlgProjectInfo() {
		initialize();
	}
	
	public static JDlgProjectInfo getInstance() {
		if(instance == null) {
			synchronized(JDlgProjectInfo.class) {
				if(instance == null) {
					instance = new JDlgProjectInfo();
				}
			}
		}
		return instance;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 450);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setTitle("Error Queue Contents");
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setContentPane(getJContentPane());
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		WindowUtil.locateCenter(this);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(null);
			jScrollPane.setViewportView(getTextPane());
		}
		return jScrollPane;
	}

	private JTextPane getTextPane() {
		if (textPane == null) {
			textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setContentType("text/html");
			textPane.setMargin(new Insets(15,15,15,15));
			textPane.setPreferredSize(new Dimension(300,200));
		}
		return textPane;
	}

	public void showInfoDlg(String projectName) {
		
		UIResponseObserver observer = null;
		String title = "Project Information";
		
		UEProjectInfo ue = new UEProjectInfo(projectName);
		observer = UserRequestHandler.getInstance().handle(
				UserRequestHandler.PROJECT_INFO, 
				ue, 
				true, 
				false);
		if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS ) {
			showDialog(title, (String)observer.getReturnValue());
		} else {
			showDialog(title, "There is no project information.");
		} 
		
	}

	public void showDialog(String title, String message) {
		this.setTitle(title);
		getTextPane().setText(message);
		setVisible(true);
	}
}
