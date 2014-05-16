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
package com.sec.ose.osi.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * JDlgExitMessage
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JDlgExitMessage extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel = null;
	private JTextArea jTextArea = null;
	private final String TITLE = "Waiting For Exit";

	public JDlgExitMessage() {
		initialize();
	}

	private void initialize() {
		this.setSize(330, 130);
		this.setContentPane(getJPanel());
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		WindowUtil.locateCenter(this);
	}

	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(0, 30, 0, 0);
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.gridx = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJTextArea(), gridBagConstraints);
		}
		return jPanel;
	}

	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
			jTextArea.setBackground(new Color(238, 238, 238));
			jTextArea.setEnabled(false);
			jTextArea.setDisabledTextColor(Color.BLACK);
			jTextArea.setFont(new Font("Dialog", Font.PLAIN, 13));
		}
		return jTextArea;
	}
	
	public void setMessage(String text) {
		jTextArea.setText(text);
	}

	public void updateTimeElapsed(long timeDuration) {
		this.setTitle(TITLE+" - "+timeDuration + " second(s) elapsed.");
		
	}

}
