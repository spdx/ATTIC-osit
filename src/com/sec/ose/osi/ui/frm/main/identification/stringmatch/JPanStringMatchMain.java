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
package com.sec.ose.osi.ui.frm.main.identification.stringmatch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.SDKInterface;
import com.sec.ose.osi.ui.ConstantValue;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboComponentName;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboLicenseName;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.JPanSMTableArea;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.JTableMatchedInfoForSM;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.TableModelForSMFile;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.TableModelForSMFolder;
import com.sec.ose.osi.util.tools.FileOperator;
import com.sec.ose.osi.util.tools.Tools;

/**
 * JPanStringMatchMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanStringMatchMain extends JPanel {
	private static Log log = LogFactory.getLog(JPanStringMatchMain.class);
	
	private static final long serialVersionUID = 1L;
	private SDKInterface protexSDK = UISDKInterfaceManager.getSDKInterface();
	private JRadioButton opt1ThisFileIs = null;
	private JRadioButton opt2ThisFileContains = null;
	private JLabel jLabelLicenseForOpt1 = null;
	private JLabel jLabelRepresentativeLicenseForOpt2 = null;
	private JSplitPane jSplitPaneCodeAndTable = null;
	private JPanel jPanelSourceCode = null;
	private JScrollPane jScrollPaneSourceCode = null;
	private JTextPane jTextPaneSourceCodeNum = null;
	private JTextPane jTextPaneSourceCode = null;
	private StyledDocument docSourceCode = null;
	private JPanel jPanelBottom = null;
	
	private JScrollPane jScrollPanelOptions = null;
	
	private JPanel jPanelThisFileIsFolderTitle = null;
	private JPanel jPanelLicenseFolder = null;
	private JPanel jPanelRepreLicenFolder = null;
	private JPanel jPanelThisFileContainsFolderTitle = null;
	private JLabel jLabelLicenseFolder = null; 
	private JLabel jLabelRepreLicenFolder = null; 
	private JLabel jLabelComponentForOpt1 = null;

	private JLabel jLabelNoticeForOpt1 = null;
	private JPanel jPanel2 = null;

	private String viewFileName = "";
	private SourceViewTask task = null;
	private boolean isWorkingSourceViewTask = false;

	private final String STR_FILE_NOT_FOUND = "No source code available.";
	
	/**
	 * This is the default constructor
	 */
	public JPanStringMatchMain() {
		super();
		initialize();
		reset();
	}
	
	// parameter String matchedFilePath
	public void refreshUI(
			String projectName,
			ArrayList<String> paths, 
			SelectedFilePathInfo selectedPathsInfo) {
		
		if(paths.size() <= 0) return;
		String selectedFilePath = paths.get(0);
		log.debug("refreshUI: "+selectedFilePath);

		opt1ThisFileIs.setSelected(true);
		jPanelLicenseFolder.setVisible(true);
		opt2ThisFileContains.setSelected(false);
		jPanelRepreLicenFolder.setVisible(false);
		
		jComboComponentNameForOpt1.setEnabled(true);
		jComboLicenseNameForOpt1.initLicenseComboBox();
		jComboLicenseNameForOpt1.setEnabled(true);
		jComboLicenseNameForOpt2.setEnabled(false);
		
		int pathType = selectedPathsInfo.getPathType();
		
		jPanelTableCardLayout.changeTableInfo(
				projectName, 
				paths, 
				selectedPathsInfo,
				pathType);
		repaint();
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.anchor = GridBagConstraints.WEST;
		gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.gridwidth = 2;
		gridBagConstraints12.weightx = 0.0;
		gridBagConstraints12.gridy = 1;
		GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
		gridBagConstraints23.fill = GridBagConstraints.BOTH;
		gridBagConstraints23.weighty = 1.0;
		gridBagConstraints23.gridwidth = 1;
		gridBagConstraints23.weightx = 1.0;
		jLabelComponentForOpt1 = new JLabel();
		jLabelComponentForOpt1.setText("(Option) Component :");
		jLabelComponentForOpt1.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelLicenseForOpt1 = new JLabel();
		jLabelLicenseForOpt1.setText("License :");
		jLabelLicenseForOpt1.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelRepresentativeLicenseForOpt2 = new JLabel();
		jLabelRepresentativeLicenseForOpt2.setText("Representative License :");
		jLabelRepresentativeLicenseForOpt2.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelNoticeForOpt1 = new JLabel();
		jLabelNoticeForOpt1.setText("Note) If you don't choose a component, it will be identified by \"DECLARED_STRING_MATCH_LICENSE_\" + \"License Name\".");
		jLabelNoticeForOpt1.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.setSize(570, 480);
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder(null, "Identification Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));

		// matched code view & license match info table
		this.add(getJSplitPaneCodeAndTable(), gridBagConstraints23);
		
		// option
		this.add(getJScrollPanelOptions(), gridBagConstraints12);
	}
	
	private void reset() {
		getJRadioButtonThisFileIs().setSelected(true);
		getJRadioButtonThisFileContains().setSelected(false);
		setOptionUI();
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonThisFileIs() {
		if (opt1ThisFileIs == null) {
			opt1ThisFileIs = new JRadioButton();
			opt1ThisFileIs.setText("This file is under exact one license.");
			opt1ThisFileIs.setFocusPainted(false);
			opt1ThisFileIs.addActionListener(new RadioThisFileIsAction());
		}
		return opt1ThisFileIs;
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonThisFileContains() {
		if (opt2ThisFileContains == null) {
			opt2ThisFileContains = new JRadioButton();
			opt2ThisFileContains.setText("This file contains more than one license text");
			opt2ThisFileContains.setFocusPainted(false);
			opt2ThisFileContains.addActionListener(new RadioThisFileContainsAction());
		}
		return opt2ThisFileContains;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPaneCodeAndTable() {
		if (jSplitPaneCodeAndTable == null) {
			jSplitPaneCodeAndTable = new JSplitPane();
			jSplitPaneCodeAndTable.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPaneCodeAndTable.setDividerLocation(150);
			jSplitPaneCodeAndTable.setDividerSize(5);
			jSplitPaneCodeAndTable.setResizeWeight(1.0D);
			jSplitPaneCodeAndTable.setTopComponent(getJPanelSourceCode());
			jSplitPaneCodeAndTable.setBottomComponent(getJTableCardLayout());
		}
		return jSplitPaneCodeAndTable;
	}
	
	private JPanSMTableArea jPanelTableCardLayout = null;

	private JPanel getJTableCardLayout() {
		if(jPanelTableCardLayout == null) {
			jPanelTableCardLayout = new JPanSMTableArea(this);
		}
		return jPanelTableCardLayout;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelSourceCode() {
		if (jPanelSourceCode == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.weightx = 1.0;
			jPanelSourceCode = new JPanel();
			jPanelSourceCode.setLayout(new GridBagLayout());
			jPanelSourceCode.add(getJScrollPaneSourceCode(), gridBagConstraints5);
		}
		return jPanelSourceCode;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneSourceCode() {
		if (jScrollPaneSourceCode == null) {
			jScrollPaneSourceCode = new JScrollPane();
			jScrollPaneSourceCode.setBorder(BorderFactory.createTitledBorder(null, "Source Code", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			
			jScrollPaneSourceCode.getVerticalScrollBar().setUnitIncrement(ConstantValue.CODE_SCROLL_SIZE);

			JPanel SourceCodePanel = new JPanel();
			SourceCodePanel.setLayout(new BorderLayout());
			SourceCodePanel.add(getJTextPaneSourceCodeNum(), BorderLayout.WEST);
			SourceCodePanel.add(getJTextPaneSourceCode(), BorderLayout.CENTER);
			jScrollPaneSourceCode.setViewportView(SourceCodePanel);
		}
		return jScrollPaneSourceCode;
	}

	public JTextPane getJTextPaneSourceCode() {
		if (jTextPaneSourceCode == null) {
			jTextPaneSourceCode = new JTextPane();
			jTextPaneSourceCode.setEditable(false);
			jTextPaneSourceCode.setAutoscrolls(false);

			docSourceCode = jTextPaneSourceCode.getStyledDocument();
		}
		return jTextPaneSourceCode;
	}

	private JTextPane getJTextPaneSourceCodeNum() {
		if (jTextPaneSourceCodeNum == null) {
			jTextPaneSourceCodeNum = new JTextPane();
			jTextPaneSourceCodeNum.setEditable(false);
			jTextPaneSourceCodeNum.setEnabled(false);
			jTextPaneSourceCodeNum.setAutoscrolls(false);
		}
		return jTextPaneSourceCodeNum;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelBottom() {
		if (jPanelBottom == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.gridy = 2;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.insets = new Insets(0, 20, 0, 0);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(0, 24, 0, 0);
			gridBagConstraints8.gridy = 4;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridy = 3;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.anchor = GridBagConstraints.WEST;
			gridBagConstraints61.insets = new Insets(0, 110, 0, 0);
			gridBagConstraints61.gridy = 1;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.anchor = GridBagConstraints.WEST;
			gridBagConstraints51.gridy = 0;
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(new GridBagLayout());
			jPanelBottom.add(getJPanelThisFileIsFolderTitle(), gridBagConstraints51);
			jPanelBottom.add(getJPanelLicenseFolder(), gridBagConstraints61);
			jPanelBottom.add(getJPanelThisFileContainsFolderTitle(), gridBagConstraints7);
			jPanelBottom.add(getJPanelRepreLicenFolder(), gridBagConstraints8);
			jPanelBottom.add(getJPanel2(), gridBagConstraints18);
		}
		return jPanelBottom;
	}

	private JScrollPane getJScrollPanelOptions(){
			
		
		if (jScrollPanelOptions == null) {
			jScrollPanelOptions = new JScrollPane(getJPanelBottom());
			jScrollPanelOptions.setBorder(null);
			jScrollPanelOptions.setMinimumSize(new Dimension(100,150));
		}
		return jScrollPanelOptions;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelThisFileIsFolderTitle() {
		if (jPanelThisFileIsFolderTitle == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.insets = new Insets(3, 10, 0, 0);
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(3, 0, 0, 0);
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			jPanelThisFileIsFolderTitle = new JPanel();
			jPanelThisFileIsFolderTitle.setLayout(new GridBagLayout());
			jPanelThisFileIsFolderTitle.add(getJRadioButtonThisFileIs(), gridBagConstraints);
			jPanelThisFileIsFolderTitle.add(getJButtonLicenseFolder(), gridBagConstraints9);
		}
		return jPanelThisFileIsFolderTitle;
	}

	public JPanel getJPanelLicenseFolder() {
		if (jPanelLicenseFolder == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.gridwidth = 1;
			gridBagConstraints13.weightx = 1.0;
			
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new Insets(0, 0, 5, 3);
			gridBagConstraints11.gridy = 0;

			
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints4.gridx = -1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.gridwidth = 1;
			gridBagConstraints4.weightx = 0.0;
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 3);
			gridBagConstraints2.gridx = -1;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.weightx = 0.0;
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			jPanelLicenseFolder = new JPanel();
			jPanelLicenseFolder.setLayout(new GridBagLayout());
			jPanelLicenseFolder.setPreferredSize(new Dimension(403, 66));
			jPanelLicenseFolder.add(jLabelLicenseForOpt1, gridBagConstraints2);
			jPanelLicenseFolder.add(getJComboLicenseNameForOpt1(), gridBagConstraints4);
			jPanelLicenseFolder.add(jLabelComponentForOpt1, gridBagConstraints11);
			jPanelLicenseFolder.add(getJComboBoxComponent(), gridBagConstraints13);
		}
		return jPanelLicenseFolder;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getJPanelRepreLicenFolder() {
		if (jPanelRepreLicenFolder == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints6.gridx = -1;
			gridBagConstraints6.gridy = -1;
			gridBagConstraints6.weightx = 0.0;
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.insets = new Insets(0, 0, 0, 3);
			gridBagConstraints3.gridx = -1;
			gridBagConstraints3.gridy = -1;
			gridBagConstraints3.weightx = 0.0;
			gridBagConstraints3.fill = GridBagConstraints.NONE;
			jPanelRepreLicenFolder = new JPanel();
			jPanelRepreLicenFolder.setLayout(new GridBagLayout());
			jPanelRepreLicenFolder.setPreferredSize(new Dimension(489, 33));
			jPanelRepreLicenFolder.add(jLabelRepresentativeLicenseForOpt2, gridBagConstraints3);
			jPanelRepreLicenFolder.add(getJComboLicenseNameForOpt2(), gridBagConstraints6);
		}
		return jPanelRepreLicenFolder;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelThisFileContainsFolderTitle() {
		if (jPanelThisFileContainsFolderTitle == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(3, 0, 0, 0);
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			gridBagConstraints1.weightx = 0.0;
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.insets = new Insets(3, 10, 0, 0);
			gridBagConstraints10.gridy = 0;
			jPanelThisFileContainsFolderTitle = new JPanel();
			jPanelThisFileContainsFolderTitle.setLayout(new GridBagLayout());
			jPanelThisFileContainsFolderTitle.add(getJRadioButtonThisFileContains(), gridBagConstraints1);
			jPanelThisFileContainsFolderTitle.add(getJButtonRepreLicen(), gridBagConstraints10);
		}
		return jPanelThisFileContainsFolderTitle;
	}
	
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {

		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridy = 1;
			gridBagConstraints15.gridx = 0;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = -1;
			gridBagConstraints14.gridy = -1;
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(jLabelNoticeForOpt1, gridBagConstraints14);
		}
		return jPanel2;
	}
	

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JLabel getJButtonLicenseFolder() {
		if (jLabelLicenseFolder == null) {
			jLabelLicenseFolder = new JLabel();
			jLabelLicenseFolder.setPreferredSize(new Dimension(20, 20));
			jLabelLicenseFolder.setBackground(new Color(238, 238, 238));
			jLabelLicenseFolder.setBorder(null);
			jLabelLicenseFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
		}
		return jLabelLicenseFolder;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JLabel getJButtonRepreLicen() {
		if (jLabelRepreLicenFolder == null) {
			jLabelRepreLicenFolder = new JLabel();
			jLabelRepreLicenFolder.setPreferredSize(new Dimension(20, 20));
			jLabelRepreLicenFolder.setBackground(new Color(238, 238, 238));
			jLabelRepreLicenFolder.setBorder(null);
			jLabelRepreLicenFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
		}
		return jLabelRepreLicenFolder;
	}
	
	private void setOptionUI() {
		getJPanelLicenseFolder().setVisible(getJRadioButtonThisFileIs().isSelected());
		getJPanel2().setVisible(getJRadioButtonThisFileIs().isSelected());
		getJPanelRepreLicenFolder().setVisible(getJRadioButtonThisFileContains().isSelected());
		if(getJRadioButtonThisFileIs().isSelected()) {
			jLabelLicenseFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
			jLabelRepreLicenFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
			if(getJRadioButtonThisFileIs().isSelected())
				return;
			getJSplitPaneCodeAndTable().setSize(getJSplitPaneCodeAndTable().getWidth(), getJSplitPaneCodeAndTable().getHeight()-100);
		} else {
			jLabelLicenseFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
			jLabelRepreLicenFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
			if(getJRadioButtonThisFileContains().isSelected())
				return;
			getJSplitPaneCodeAndTable().setSize(getJSplitPaneCodeAndTable().getWidth(), getJSplitPaneCodeAndTable().getHeight()+100);
		}
			
	}
	
	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboLicenseName jComboLicenseNameForOpt2 = null;
	public JComboLicenseName getJComboLicenseNameForOpt2() {
		if (jComboLicenseNameForOpt2 == null) {
			jComboLicenseNameForOpt2 = new JComboLicenseName();
			jComboLicenseNameForOpt2.setPreferredSize(new Dimension(350, 27));
			jComboLicenseNameForOpt2.setEditable(true);
			jComboLicenseNameForOpt2.setEnabled(false);
		}
		return jComboLicenseNameForOpt2;
	}
	
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboLicenseName jComboLicenseNameForOpt1 = null;
	public JComboLicenseName getJComboLicenseNameForOpt1() {
		if (jComboLicenseNameForOpt1 == null) {
			jComboLicenseNameForOpt1 = new JComboLicenseName();
			jComboLicenseNameForOpt1.setPreferredSize(new Dimension(350, 27));
			jComboLicenseNameForOpt1.setEditable(true);
			jComboLicenseNameForOpt1.setEnabled(true);
		}
		return jComboLicenseNameForOpt1;
	}
	
	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboComponentName jComboComponentNameForOpt1 = null; 
	public JComboComponentName getJComboBoxComponent() {
		if (jComboComponentNameForOpt1 == null) {
			jComboComponentNameForOpt1 = new JComboComponentName();
			jComboComponentNameForOpt1.setPreferredSize(new Dimension(350, 27));
			jComboComponentNameForOpt1.setEditable(true);
		}
		return jComboComponentNameForOpt1;
	}

	public JRadioButton getRdbtnThisFileIs() {
		return opt1ThisFileIs;
	}

	public void setRdbtnThisFileIs(JRadioButton rdbtnThisFileIs) {
		this.opt1ThisFileIs = rdbtnThisFileIs;
	}

	public JRadioButton getRdbtnThisFileContains() {
		return opt2ThisFileContains;
	}

	public void setRdbtnThisFileContains(JRadioButton rdbtnThisFileContains) {
		this.opt2ThisFileContains = rdbtnThisFileContains;
	}

	Vector<Integer> matchedLines = null;
	HashMap<String,Vector<String>> matchedLineMap = null;	
			// key: searchName  , value: machedLines 
	
	public void viewSourceCode(String fileName, HashMap<String,Vector<String>> pMatchedLineHashMap) {
		
		log.debug("viewSourceCode: "+fileName);	

		gSelectedStringSearchIndex = 0;
		if(isAvailableExtention(FileOperator.getExtention(fileName))) {
			if(!viewFileName.equals(fileName)) {
				viewFileName = fileName;
				matchedLineMap = pMatchedLineHashMap;
				updateSourceCodeView();
			}
		} else {
			getJTextPaneSourceCodeNum().setText("");
			getJTextPaneSourceCode().setText(STR_FILE_NOT_FOUND);
		}
	}
	
	static Vector<String> NOT_AVAILABLE_EXTENSION = new Vector<String>();
	static {
		NOT_AVAILABLE_EXTENSION.add("jar");
		NOT_AVAILABLE_EXTENSION.add("exe");
		NOT_AVAILABLE_EXTENSION.add("dll");
		NOT_AVAILABLE_EXTENSION.add("so");
		NOT_AVAILABLE_EXTENSION.add("a");
		NOT_AVAILABLE_EXTENSION.add("class");
	}
		
	private boolean isAvailableExtention(String extention) {
		if(extention == null) return false;

		
		if(NOT_AVAILABLE_EXTENSION.contains(extention.toLowerCase())) {
			return false;
		}
		return true;
	}

	public void updateSourceCodeView() {
		
		log.debug("updateSourceCodeView() - isWorkingSourceViewTask: "+isWorkingSourceViewTask);	

		if(isWorkingSourceViewTask) {
			if(task != null) {
				task.cancel(true);
			}
			task = null;
		}
		isWorkingSourceViewTask = true;
		
		if(matchedLineMap != null) {
			setStringSearchList(); 
		} else {
			matchedLines = null;
		}
		task = new SourceViewTask(viewFileName);
		task.execute();
	}

	private void setStringSearchList() {
		
		log.debug("setStringSearchList");
		
		int startLine = -1;
		int endLine = -1;
		
		JTableMatchedInfoForSM jTableMatchedInfoForSM = jPanelTableCardLayout.getSelectedTable();
		
		String selectedSearchName = jTableMatchedInfoForSM.getSelectedSearchName();
		Vector<String> strMatchedLines = null;
		if(matchedLineMap != null) {
			strMatchedLines = matchedLineMap.get(selectedSearchName);
		}
		
		if(strMatchedLines!=null) {
			matchedLines = new Vector<Integer>();
			int cnt=0;			
			for(String matchLine:strMatchedLines) {
				log.debug(">>>>>> matchLine: "+matchLine+" - "+cnt++ +"/"+strMatchedLines.size());				
				matchLine = matchLine.replaceAll(",","");
				log.debug(">>> matchLine - after replace: "+matchLine);				
				int split = matchLine.indexOf("..");
				if(split > 0) {
					startLine = Tools.transStringToInteger(matchLine.substring(0,split));
					endLine = Tools.transStringToInteger(matchLine.substring(split+2));
					log.debug(">>> start: "+startLine);
					log.debug(">>> end: "+endLine);
					for(int i=startLine;i<=endLine;i++) {
						matchedLines.addElement(i);
					}
				} else {
					matchedLines.addElement(Tools.transStringToInteger(matchLine));
				}
			}
		}
	}

	class SourceViewTask extends SwingWorker<String, String> {
		int curLine = 1;
		boolean isComplete = false;
		
		Vector<Integer> startPos = new Vector<Integer>();
		Vector<Integer> endPos = new Vector<Integer>(); 
		
		StringBuffer sbNum = new StringBuffer();
		StringBuffer sbText = new StringBuffer();
		String displayedFileName;
		
		SourceViewTask(String displayedFileName) {
			log.debug("instructor called: "+displayedFileName);
			this.displayedFileName = displayedFileName;
		}
		
		@Override
		public String doInBackground() {
			
			log.debug("doInBackground - start");

			String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
			File mySourceCode = new File(protexSDK.getSourceLocation(projectName)+File.separator+displayedFileName);
			FileReader fr = null;
			try {
				getJTextPaneSourceCode().setText("");
				fr = new FileReader(mySourceCode);
				BufferedReader br = new BufferedReader(fr);
				String tmpStr = null;
				while((tmpStr = br.readLine()) != null) {
					publish(tmpStr);
				}
				isComplete = true;
			} catch (Exception e) {
				log.warn(e.getMessage());
				getJTextPaneSourceCodeNum().setText("");
				getJTextPaneSourceCode().setText(STR_FILE_NOT_FOUND);
	        } finally {
	        	try {
					if(fr != null) { fr.close(); }
				} catch (Exception e) {
					log.warn(e);
				}
	        }
			return "";
		}
		
		@Override
		protected void process(List<String> progress) {
			for(String tmpStr:progress) {
				setStringSearchList(curLine);
				sbNum.append(String.valueOf(curLine)+"\n");
				sbText.append(tmpStr+"\n");
				curLine++;
			}
			setStringSearchList(curLine);
		}
		
		boolean preMatching = false;
		
		private void setStringSearchList(int line) {
			boolean isMatching = false;
			if(matchedLines == null) return;
			for(int matchLine:matchedLines) {
				if(matchLine == line) {
					isMatching = true;
					if(!preMatching) {
						startPos.addElement(sbText.length());
					}
					break;
				}
			}
			if(!isMatching && preMatching) {
				endPos.addElement(sbText.length());
			}
			preMatching = isMatching;
		}
		

		@Override
		protected void done() {
			if(isDone() && isComplete) {
				getJTextPaneSourceCode().setText(sbText.toString());
				getJTextPaneSourceCodeNum().setText(sbNum.toString());

				MutableAttributeSet attr = new SimpleAttributeSet();
				StyleConstants.setBackground( attr, new Color(255, 255, 180));
				MutableAttributeSet defaultAttr = new SimpleAttributeSet();
				
				docSourceCode.setCharacterAttributes(0, docSourceCode.getLength(), defaultAttr, true);
				if(startPos.size() > endPos.size()) endPos.add(docSourceCode.getLength());
				for(int i=0; i<startPos.size();i++) {
					int offset = startPos.get(i);
					int length = endPos.get(i) - offset;
					docSourceCode.setCharacterAttributes(offset, length, attr, true);
				}
				if(matchedLines == null) {
					getJTextPaneSourceCode().setCaretPosition(0);
				} else if(startPos.size() > 0) {
					getJTextPaneSourceCode().setCaretPosition(startPos.get(0));
				}
			} else {
				log.debug("not done");
				getJTextPaneSourceCodeNum().setText("");
				getJTextPaneSourceCode().setText(STR_FILE_NOT_FOUND);
			}
			isWorkingSourceViewTask = false;
		}
	}

	private int gSelectedStringSearchIndex = 0;
	public int getgSelectedStringSearchIndex() {
		return gSelectedStringSearchIndex;
	}

	public String getSelectedComonentName() {
		
		String componentName = "";
		
		if(opt1ThisFileIs.isSelected()) {
			componentName = getJComboBoxComponent().getCurrentComponentName();
			if(componentName.length() == 0)
				componentName = "DECLARED_STRING_MATCH_LICENSE_"+IdentifyMediator.getInstance().getSelectedLicenseName();
		} else if(opt2ThisFileContains.isSelected()){
			componentName = 
				"DECLARED_STRING_MATCH_MULTIPLE_LICENSE_"+
				getJComboBoxComponent().getCurrentComponentName();
		}
		
		return componentName;
	}

	private String getSelectedLicenseName() {
		String licenseName = "";
		
		if(opt1ThisFileIs.isSelected()) {
			licenseName = getJComboLicenseNameForOpt1().getCurrentLicenseName();
		} else if(opt2ThisFileContains.isSelected()){
			licenseName = getJComboLicenseNameForOpt2().getCurrentLicenseName();
		}
		
		return licenseName;
	}

	private String getSelectedComponentName() {
		
		log.debug("getSelectedComponentName");
		
		String componentName = "";
		componentName = getJComboBoxComponent().getCurrentComponentName();
		return componentName;
	}


	public String getNewComponentName() {
		return getSelectedComonentName();
	}

	public UEStringMatch exportUIEntity(String projectName) {
		
		String stringSearch = "";
		String componentName = "";
		String versionName = "";
		String licenseName = "";
		String newComponentName = "";
		String newVersionName = "";
		String newLicenseName = getSelectedLicenseName();
		
		log.debug("exportUIEntity - ");
		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		JTableMatchedInfoForSM jTableMatchedInfoForSM = jPanelTableCardLayout.getSelectedTable();

		int row = jTableMatchedInfoForSM.getSelectedRow();

		int pathType = selectedPaths.getPathType();
		switch(pathType) {
		case SelectedFilePathInfo.SINGLE_FILE_TYPE:
			{
				stringSearch = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_STRING_SEARCH));
				componentName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_COMPONENT_NAME));
				versionName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_VERSION_NAME));
				licenseName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_LICENSE_NAME));
			}
			break;
		case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
			{
			}
			break;
		case SelectedFilePathInfo.FOLDER_TYPE:
		case SelectedFilePathInfo.PROJECT_TYPE:
			{
				stringSearch = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFolder.COL_STRING_SEARCH));
				componentName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFolder.COL_COMPONENT_NAME));
				versionName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFolder.COL_VERSION_NAME));
				licenseName = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_LICENSE_NAME));
			}
			break;
		}
		
		if(stringSearch.equals("null")) stringSearch = "";
		if(componentName.equals("null")) componentName = "";
		if(versionName.equals("null")) versionName = "";
		if(licenseName.equals("null")) licenseName = "";
		String status = String.valueOf(jTableMatchedInfoForSM.getValueAt(row, TableModelForSMFile.COL_STATUS));
		if(opt1ThisFileIs.isSelected() ) {
			if( ("Pending".equals(status)) &&
				(getSelectedLicenseName() == null || getSelectedLicenseName().length() <= 0) ) {
	        	JOptionPane.showOptionDialog(
	        			null, 
	        			"\"License\" Field must be non-empty.", 
	        			"Pending identification", 
	        			JOptionPane.OK_OPTION, 
	        			JOptionPane.ERROR_MESSAGE, 
	        			null, 
	        			buttonOK, 
	        			"OK");
	        	return null;
			}
			
			if((getSelectedComponentName()).length() > 0){
				newComponentName = getSelectedComponentName();
			} else {	
				newComponentName = "DECLARED_STRING_MATCH_LICENSE_" + newLicenseName;					
			}
		} else if(opt2ThisFileContains.isSelected()){
			if( ("Pending".equals(status)) &&
				(getSelectedLicenseName() == null || getSelectedLicenseName().length() <= 0) ) {
	        	JOptionPane.showOptionDialog(
	        			null, 
	        			"\"Representative License\" Field must be non-empty.", 
	        			"Pending identification", 
	        			JOptionPane.OK_OPTION, 
	        			JOptionPane.ERROR_MESSAGE, 
	        			null, 
	        			buttonOK, 
	        			"OK");
	        	return null;
			}
			newComponentName = "DECLARED_STRING_MATCH_MULTIPLE_LICENSE_"+newLicenseName; 
		}
		log.debug("exportUIEntity - newComponentName : "+ newComponentName);
		
		
		UEStringMatch xUEStringMatch = new UEStringMatch(
				stringSearch,
				componentName,
				versionName,
				licenseName,
				newComponentName,
				newVersionName,
				newLicenseName);

		return xUEStringMatch;
	}
	private String[] buttonOK = {"OK"};

	class RadioThisFileIsAction implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("ThisFileIs Radio Select actionPerformed()");
			
			if(opt1ThisFileIs.isSelected()) {
				int selectedIndex = jComboLicenseNameForOpt2.getSelectedIndex();
				jComboLicenseNameForOpt1.setEnabled(true);
				jComboLicenseNameForOpt2.setEnabled(false);
				if(selectedIndex>=0) {
					jComboLicenseNameForOpt1.setLicenseComboBox(selectedIndex);
				} else {
					IdentifyMediator.getInstance().setSelectedLicenseName("");
					log.debug("라이센스 초기화");
				}
			}

			opt1ThisFileIs.setSelected(true);
			opt2ThisFileContains.setSelected(false);
			setOptionUI();
			

			log.debug("1. "+ getJComboBoxComponent().getCurrentComponentName());
		}
	}

	class RadioThisFileContainsAction implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("ThisFileContains Radio Select actionPerformed()");
			
			if(opt2ThisFileContains.isSelected()) {
				int selectedRow = jComboLicenseNameForOpt1.getSelectedIndex();
				jComboLicenseNameForOpt1.setEnabled(false);
				jComboLicenseNameForOpt2.setEnabled(true);
				if(selectedRow>=0) {
					jComboLicenseNameForOpt1.setLicenseComboBox(selectedRow);
				} else {
					IdentifyMediator.getInstance().setSelectedLicenseName("");
					log.debug("init license");
				}
			}
			
			opt1ThisFileIs.setSelected(false);
			opt2ThisFileContains.setSelected(true);
			setOptionUI();
			
						
			log.debug("1. "+ getJComboBoxComponent().getCurrentComponentName());
		}
	}
	
}
