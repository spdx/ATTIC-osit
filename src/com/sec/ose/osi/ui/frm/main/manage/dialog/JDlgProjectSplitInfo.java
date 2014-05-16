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
package com.sec.ose.osi.ui.frm.main.manage.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.util.tools.ProjectSplitUtil;

/**
 * JDlgProjectSplitInfo
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JDlgProjectSplitInfo extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JButton jButtonOK = null;
	private JTextPane textPane = null;
	private volatile static JDlgProjectSplitInfo instance = null;
	
	private JButton jButtonCancel = null;

	private JPanel jPanelButtonArea = null;
	private boolean splitOperation = false;
	
	private JDlgProjectSplitInfo() {
		initialize();
	}
	
	public static JDlgProjectSplitInfo getInstance() {
		if(instance == null) {
			synchronized(JDlgProjectSplitInfo.class) {
				if(instance == null) {
					instance = new JDlgProjectSplitInfo();
				}
			}
		}
		return instance;
	}

	private void initialize() {
		this.setSize(750, 500);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setTitle("Folder size is too big.");
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setContentPane(getJContentPane());
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		WindowUtil.locateCenter(this);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getJPanelButtonArea(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JButton getJButtonOK() {
		
		if(jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
			jButtonOK.setPreferredSize(new Dimension(73, 28));
			jButtonOK.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							splitOperation = true;
							setVisible(false);
						}
					}
				);
		}
		
		return jButtonOK;
	}

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

	public void setText(String content) {
		textPane.setText(content);
	}

	public void showDialog() {
		String output = "";
		output = ProjectSplitUtil.toHtml();
		
		setText(output);  
		setVisible(true);
	}

	public boolean isWorking() {
		return splitOperation;
	}

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							splitOperation = false;
							setVisible(false);
						}
					}
				);
		}
		return jButtonCancel;
	}

	private JPanel getJPanelButtonArea() {
		if (jPanelButtonArea == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(10, 10, 10, 0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.insets = new Insets(10, 0, 10, 10);
			jPanelButtonArea = new JPanel();
			jPanelButtonArea.setLayout(new GridBagLayout());
			jPanelButtonArea.add(getJButtonOK(), gridBagConstraints);
			jPanelButtonArea.add(getJButtonCancel(), gridBagConstraints1);
		}
		return jPanelButtonArea;
	}
}
