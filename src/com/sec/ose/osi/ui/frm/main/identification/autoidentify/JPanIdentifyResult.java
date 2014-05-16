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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 * JPanIdentifyResult
 * @author sjh.yoo, ytaek.kim
 * 
 */
public class JPanIdentifyResult extends JPanel{
	private static final long serialVersionUID = -6509248585419006535L;
	private JPanel jPanMain = null;

	public JPanIdentifyResult() {
		initialize();
	}
	
	private void initialize () {
		BorderLayout layout = new BorderLayout();
		
		this.setLayout(layout);
		this.add(new JLabel("           "), BorderLayout.NORTH);
		this.add(getJPanMain(), BorderLayout.CENTER);
	}

	private JDlgIdentifyResult jDlgIdentifyResult;
	public void setParent(JDlgIdentifyResult dialog) {
		this.jDlgIdentifyResult = dialog;
	}
	
	public void setAutoIdentifyResult(SPDXAutoIdentifyResult autoIdentifyResult) {
		getJLabelIdentifyResultSummary().setText(autoIdentifyResult.getSummary());
		getJTextAreaSPDXPackagesInfo().setText(autoIdentifyResult.getSourceSPDXPackagesInfo());
		getJTextAreaResultDetail().setText(autoIdentifyResult.getFailDetails());
	}

	private JPanel getJPanMain() {		
		if(jPanMain == null) {
			jPanMain = new JPanel();

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(0,0,0,10);	//Insets(int top, int left, int bottom, int right)
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.insets = new Insets(0,0,0,0);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.insets = new Insets(0,0,0,0);
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.insets = new Insets(0,0,0,0);
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			
			jPanMain.add(getJPanelForIdentifySummary(), gridBagConstraints1);
			jPanMain.add(getJPanelSPDXPackagesInfo(), gridBagConstraints2);
			jPanMain.add(getJPanelIdentifyResultDetail(), gridBagConstraints3);
			jPanMain.add(getJButtonPanel(),gridBagConstraints4);
			
		}
		
		return jPanMain;
	}
	
	/** Identify Summary **/
	private JPanel jPanelForIdentifySummary = null; 
	private JPanel getJPanelForIdentifySummary() {
		if (jPanelForIdentifySummary == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(0, 10, 5, 5);
			jPanelForIdentifySummary = new JPanel();
			jPanelForIdentifySummary.setLayout(new GridBagLayout());
			jPanelForIdentifySummary.setBorder(BorderFactory.createTitledBorder(null, "Summary", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 13), new Color(60, 60, 60)));
			jPanelForIdentifySummary.add(getJPanelCreatorInfo(), gridBagConstraints5);
		}
		return jPanelForIdentifySummary;
	}

	private JPanel jPanelIdentifySummary = null; 
	private JPanel getJPanelCreatorInfo() {
		if (jPanelIdentifySummary == null) {
			jPanelIdentifySummary = new JPanel();
			jPanelIdentifySummary.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(0,0,0,10);	//Insets(int top, int left, int bottom, int right)
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			
			jPanelIdentifySummary.add(getJLabelIdentifyResultSummary(), gridBagConstraints1);
		}
		return jPanelIdentifySummary;
	}
			
	private JLabel jLabelIdentifyResultSummary = null;
	private JLabel getJLabelIdentifyResultSummary() {
		if(jLabelIdentifyResultSummary == null) {
			jLabelIdentifyResultSummary = new JLabel("TEST", JLabel.LEFT);
			jLabelIdentifyResultSummary.setPreferredSize(new Dimension(518, 22));
			jLabelIdentifyResultSummary.setForeground(Color.BLUE);
		}
		return jLabelIdentifyResultSummary;
		
	}

	/** SPDX Packages Info **/
	private JPanel jPanelSPDXPackagesInfo = null;
	private JPanel getJPanelSPDXPackagesInfo() {
		if(jPanelSPDXPackagesInfo == null) {
			jPanelSPDXPackagesInfo = new JPanel();
			jPanelSPDXPackagesInfo.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.insets = new Insets(0,0,0,0);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			
			JLabel jLabelSPDXPackagesInfo = new JLabel("SPDX Packages Info",JLabel.LEFT);

			jPanelSPDXPackagesInfo.add(jLabelSPDXPackagesInfo, gridBagConstraints1);
			jPanelSPDXPackagesInfo.add(getJScrollPaneSPDXPackagesInfo(), gridBagConstraints2);
		}
		return jPanelSPDXPackagesInfo;
	}

	private JScrollPane jScrollPaneSPDXPackagesInfo = null;
	private JScrollPane getJScrollPaneSPDXPackagesInfo() {
		if (jScrollPaneSPDXPackagesInfo == null) {
			jScrollPaneSPDXPackagesInfo = new JScrollPane();
			jScrollPaneSPDXPackagesInfo.setPreferredSize(new Dimension(550, 150));
			jScrollPaneSPDXPackagesInfo.setViewportView(getJTextAreaSPDXPackagesInfo());
			jScrollPaneSPDXPackagesInfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPaneSPDXPackagesInfo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneSPDXPackagesInfo.setAutoscrolls(false);
		}
		return jScrollPaneSPDXPackagesInfo;
	}

	private JTextArea jTextAreaSPDXPackagesInfo = null;
	private JTextArea getJTextAreaSPDXPackagesInfo() {
		if (jTextAreaSPDXPackagesInfo == null) {
			jTextAreaSPDXPackagesInfo = new JTextArea();
			jTextAreaSPDXPackagesInfo.setEditable(false);
		}
		return jTextAreaSPDXPackagesInfo;
	}
	
	
	/** Identify Result Detail **/
	private JPanel jPanelIdentifyResultDetail = null;
	private JPanel getJPanelIdentifyResultDetail() {
		if(jPanelIdentifyResultDetail == null) {
			jPanelIdentifyResultDetail = new JPanel();
			jPanelIdentifyResultDetail.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.insets = new Insets(0,0,0,0);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			
			JLabel jLabelIdentifyResultDetail = new JLabel("Conflict Log",JLabel.LEFT);

			jPanelIdentifyResultDetail.add(jLabelIdentifyResultDetail, gridBagConstraints1);
			jPanelIdentifyResultDetail.add(getJScrollPaneIdentifyResultDetail(), gridBagConstraints2);
		}
		return jPanelIdentifyResultDetail;
	}

	private JScrollPane jScrollPaneIdentifyResultDetail = null;
	private JScrollPane getJScrollPaneIdentifyResultDetail() {
		if (jScrollPaneIdentifyResultDetail == null) {
			jScrollPaneIdentifyResultDetail = new JScrollPane();
			jScrollPaneIdentifyResultDetail.setPreferredSize(new Dimension(550, 300));
			jScrollPaneIdentifyResultDetail.setViewportView(getJTextAreaResultDetail());
			jScrollPaneIdentifyResultDetail.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPaneIdentifyResultDetail.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneIdentifyResultDetail.setAutoscrolls(false);
		}
		return jScrollPaneIdentifyResultDetail;
	}

	private JTextArea jTextAreaResultDetail = null;
	private JTextArea getJTextAreaResultDetail() {
		if (jTextAreaResultDetail == null) {
			jTextAreaResultDetail = new JTextArea();
			jTextAreaResultDetail.setEditable(false);
		}
		return jTextAreaResultDetail;
	}
	
	/** Button Panel **/
	private JPanel jButtonPanel = null;	
	private JButton jButtonOK = null;
	private JPanel getJButtonPanel() {
		if(jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(10, 0, 10, 0);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			jButtonPanel.add(getJButtonOK(), gridBagConstraints1);
			
		}
		return jButtonPanel;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("     OK     ");
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jDlgIdentifyResult.setVisible(false);
				}
			});
			jButtonOK.setEnabled(true);
		}
		return jButtonOK;
	}
	
}
