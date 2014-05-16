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
package com.sec.ose.osi.ui.dialog.progress;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.ui.dialog.ProgressDisplayer;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.FormatUtil;

/**
 * JDlgProgress
 * @author suhyun47.kim, hankido.lee 
 *
 */

public class JDlgProgress extends ProgressDisplayer {
	private static Log log = LogFactory.getLog(JDlgProgress.class);
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanelContent = null;

	private JButton jButtonCancel = null;

	private JTextArea jTextAreaMessage = null;
	
	private String titlePrefix = null;
	private boolean isCancled = false;
	
	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	
	}

	public JDlgProgress() {
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setResizable(false);
		this.setModal(true);
		this.setContentPane(getJContentPane());
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				log.debug("windowClosing() - Progress Dialog"); 
				mEventHandler.handle(EventHandler.CLOSING_EVENT);
			}
		});
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
			jContentPane.add(getJPanelContent(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes jPanelContent	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelContent() {
		if (jPanelContent == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(35, 25, 0, 25);
			gridBagConstraints1.gridx = 0;
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(35, 25, 0, 25);
			gridBagConstraints2.gridy = 1;
			
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(20, 0, 20, 0);
			gridBagConstraints3.gridy = 2;
			
			
			jPanelContent = new JPanel();
			jPanelContent.setLayout(new GridBagLayout());
			jPanelContent.setPreferredSize(new Dimension(350, 150));
			jPanelContent.add(getJTextAreaMessage(), gridBagConstraints1);
			jPanelContent.add(getJButtonCancel(), gridBagConstraints3);
		}
		return jPanelContent;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("actionPerformed() - Progress Cancel Button"); 
					mEventHandler.handle(EventHandler.BTN_CANCEL);
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jTextAreaMessage	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextAreaMessage() {
		if (jTextAreaMessage == null) {
			jTextAreaMessage = new JTextArea();
			jTextAreaMessage.setEditable(false);
			jTextAreaMessage.setBackground(new Color(238, 238, 238));
			jTextAreaMessage.setText("Executing");
			jTextAreaMessage.setRows(4);
			jTextAreaMessage.setPreferredSize(new Dimension(300, 18));
			jTextAreaMessage.setDisabledTextColor(Color.BLACK);
			jTextAreaMessage.setEnabled(false);
		}
		return jTextAreaMessage;
	}
	
	public void setProgressMessage(String pMessage) {
		this.jTextAreaMessage.setText(pMessage);
	}

	public void setElapsedTime(long timeMillisec) {
		
		String timeString = FormatUtil.getElapsedTime(timeMillisec);
		String timeMessage = timeString + " elapsed";
		
		this.setTitle(titlePrefix + " - " + timeMessage);
	}
	
	public boolean isCancled() {
		return this.isCancled;
	}
	
	public void close() {
		this.setVisible(false);
		this.dispose();
	}
	
	/* 
	 * The frame needs to be displayable when you call createBufferStrategy.
	 * Also as camickr has pointed out you need to call it from the EDT.
	 * One way to ensure this is to extend JFrame and override addNotify:
	 */
	public void addNotify() {
        super.addNotify();
        // Buffer
        createBufferStrategy(2);           
    }
	
	EventHandler mEventHandler = new EventHandler();

	class EventHandler {
				
		protected static final int BTN_CANCEL 		= 1;
		protected static final int CLOSING_EVENT	= 2;
		
		protected void handle(int pEvent) {
			
			switch(pEvent) {
				
				case BTN_CANCEL:
				case CLOSING_EVENT:
					Property.getInstance().setProperty(Property.IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "true");
					
					JDlgProgress.this.isCancled = true;
					JDlgProgress.this.setVisible(false);
					JDlgProgress.this.dispose();
					break;
				
			}
			
		}
	}

}
