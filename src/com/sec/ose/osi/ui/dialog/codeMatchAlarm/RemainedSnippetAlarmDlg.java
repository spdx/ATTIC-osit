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
package com.sec.ose.osi.ui.dialog.codeMatchAlarm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * RemainedSnippetAlarmDlg
 * @author hankido.lee, suhyun47.kim
 *
 */
public class RemainedSnippetAlarmDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextPane jTextPane = null;
	
	public RemainedSnippetAlarmDlg() {
		initialize();
	}
	
	private void initialize() {
		this.setSize(500, 300);
		this.setResizable(true);
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setTitle("Detecting Another Matched Snippet");
		this.setContentPane(getJContentPane());
//		this.setModalityType(ModalityType.APPLICATION_MODAL);
		WindowUtil.locateCenter(this);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.insets = new Insets(0, 20, 10, 20);
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.insets = new Insets(0, 20, 10, 20);
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(10, 20, 10, 20);
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJScrollPaneConsole(), gridBagConstraints);
		}
		return jContentPane;
	}

	private JScrollPane jScrollPaneConsole = null;
	private JScrollPane getJScrollPaneConsole() {
		if (jScrollPaneConsole == null) {
			jScrollPaneConsole = new JScrollPane();
			jScrollPaneConsole.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneConsole.setViewportView(getJTextPane());
		}
		return jScrollPaneConsole;
	}
	
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setEditable(false);
			jTextPane.setContentType("text/html");
		}
		return jTextPane;
	}
	
	synchronized public void setTextPane(String content) {
		jTextPane.setText(content); 
	}
}
