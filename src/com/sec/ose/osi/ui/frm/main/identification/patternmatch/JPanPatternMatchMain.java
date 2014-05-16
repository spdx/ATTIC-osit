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
package com.sec.ose.osi.ui.frm.main.identification.patternmatch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.JPanIdentifyMain;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboComponentName;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboLicenseName;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.util.tools.FileOperator;

/**
 * JPanPatternMatchMain
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JPanPatternMatchMain extends JPanel {
	private static Log log = LogFactory.getLog(JPanPatternMatchMain.class);
	
	private static final long serialVersionUID = 1L;
	private JRadioButton rdbtnItsLibraryWhich = null;
	private JRadioButton rdbtnIKnowThe = null;
	private JRadioButton rdbtnThird = null;
	private JLabel jLabelBinding = null;
	private JLabel jLabelComponent = null;
	private JLabel jLabelLicense = null;
	private JComboBox<String> cbPmBindType = null;
	private JLabel jLabelForder = null;
	private JPanel jPanelBottom = null;
	private JPanel jPanelTop = null;
	private JScrollPane jScrollPaneFolder = null;
	private JScrollPane jScrollPanePanel = null;
	private JButton jButtonThird = null;
	private JPanel jPanelThirdTitle = null;
	
	/**
	 * This is the default constructor
	 */
	public JPanPatternMatchMain() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 0;
		gridBagConstraints41.gridwidth = 2;
		gridBagConstraints41.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints41.weighty = 0.01;
		gridBagConstraints41.weightx = 1.0;
		gridBagConstraints41.gridy = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.gridwidth = 2;
		gridBagConstraints3.weighty = 1.0;
		gridBagConstraints3.insets = new Insets(0, 20, 0, 0);
		gridBagConstraints3.gridy = 3;
		jLabelLicense = new JLabel();
		jLabelLicense.setText("License :");
		jLabelLicense.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelComponent = new JLabel();
		jLabelComponent.setText("Component :");
		jLabelComponent.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelBinding = new JLabel();
		jLabelBinding.setText("Binding Type :");
		jLabelBinding.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.setLayout(new GridBagLayout());
		this.setSize(570, 480);
		this.setBorder(BorderFactory.createTitledBorder(null, "Identification Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		this.setLocation(new Point(0, 0));
		this.add(getJScrollPaneFolder(), gridBagConstraints3);
		this.add(getJScrollPanePanel(), gridBagConstraints41);
		
		getJRadioButtonNotContain().setSelected(false);
		getJRadioButtonThird().setSelected(false);
		getJRadioButtonIKnow().setSelected(true);
		getJScrollPaneFolder().setVisible(true);
	}
	
	private JScrollPane getJScrollPanePanel(){
		if (jScrollPanePanel == null) {
			jScrollPanePanel = new JScrollPane(getJPanelTop());
			jScrollPanePanel.setBorder(null);
			jScrollPanePanel.setMinimumSize(new Dimension(100,200));
		}
		return jScrollPanePanel;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonNotContain() {
		if (rdbtnItsLibraryWhich == null) {
			rdbtnItsLibraryWhich = new JRadioButton();
			rdbtnItsLibraryWhich.setText("It is a library that does not contains any open source code.");
			rdbtnItsLibraryWhich.setFocusPainted(false);
			rdbtnItsLibraryWhich.addActionListener(new RadioButtonItsLibraryWitchAction());
		}
		return rdbtnItsLibraryWhich;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonThird() {
		if (rdbtnThird == null) {
			rdbtnThird = new JRadioButton();
			rdbtnThird.setText("I don't know the license of this file.");
			rdbtnThird.setFocusPainted(false);
			rdbtnThird.addActionListener(new RadioButtonThird());
		}
		return rdbtnThird;
	}
	
	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonIKnow() {
		if (rdbtnIKnowThe == null) {
			rdbtnIKnowThe = new JRadioButton();
			rdbtnIKnowThe.setText("I know the license for this file.");
			rdbtnIKnowThe.setFocusPainted(false);
			rdbtnIKnowThe.addActionListener(new RadioButtonIKnowAction());
		}
		return rdbtnIKnowThe;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxBinding() {
		if (cbPmBindType == null) {
			cbPmBindType = new JComboBox<String>();
			cbPmBindType.setPreferredSize(new Dimension(350, 27));
			// Bingding Type ComboBox Add
			cbPmBindType.setEditable(true);
			cbPmBindType.addItem("Static Binding Libary");		
			cbPmBindType.addItem("Dynamic Binding Library");
			cbPmBindType.addItem("Object File");
			cbPmBindType.addItem("Executable File");
			cbPmBindType.addItem("Text File");
			cbPmBindType.addItem("Compression File");
		}
		return cbPmBindType;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboLicenseName jComboLicenseNameForOpt3 = null; 
	public JComboLicenseName getJComboBoxLicenseForOpt3() {
		if (jComboLicenseNameForOpt3 == null) {
			jComboLicenseNameForOpt3 = new JComboLicenseName();
			jComboLicenseNameForOpt3.setPreferredSize(new Dimension(350, 27));
			jComboLicenseNameForOpt3.setEditable(true);
		}
		return jComboLicenseNameForOpt3;
	}

	/**
	 * This method initializes jComboBox2	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboComponentName jComboComponentNameForOpt3 = null;
	public JComboComponentName getJComboBoxComponentForOpt3() {
		if (jComboComponentNameForOpt3 == null) {
			jComboComponentNameForOpt3 = new JComboComponentName();
			jComboComponentNameForOpt3.setBounds(188, 130, 306, 21);
			jComboComponentNameForOpt3.setEditable(true);
			jComboComponentNameForOpt3.setEnabled(true);
			jComboComponentNameForOpt3.setPreferredSize(new Dimension(350, 27));
			
		}
		return jComboComponentNameForOpt3;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JLabel getJButton() {
		if (jLabelForder == null) {
			jLabelForder = new JLabel();
			jLabelForder.setPreferredSize(new Dimension(20, 20));
			jLabelForder.setBackground(new Color(238, 238, 238));
			jLabelForder.setBorder(null);
			jLabelForder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
		}
		return jLabelForder;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getJPanelBottom() {
		if (jPanelBottom == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints8.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 0.0;
			gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(10, 0, 0, 15);
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 0.0;
			gridBagConstraints5.weighty = 0.0;
			gridBagConstraints5.insets = new Insets(13, 0, 0, 5);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.insets = new Insets(10, 0, 0, 5);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			gridBagConstraints1.insets = new Insets(10, 15, 0, 5);
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(new GridBagLayout());
			jPanelBottom.add(jLabelBinding, gridBagConstraints1);
			jPanelBottom.add(jLabelComponent, gridBagConstraints4);
			jPanelBottom.add(jLabelLicense, gridBagConstraints5);
			jPanelBottom.add(getJComboBoxBinding(), gridBagConstraints6);
			jPanelBottom.add(getJComboBoxComponentForOpt3(), gridBagConstraints7);
			jPanelBottom.add(getJComboBoxLicenseForOpt3(), gridBagConstraints8);
		}
		return jPanelBottom;
	}

	private JScrollPane getJScrollPaneFolder(){
		if (jScrollPaneFolder == null) {
			jScrollPaneFolder = new JScrollPane(getJPanelBottom());
			jScrollPaneFolder.setBorder(null);
			jScrollPaneFolder.setMinimumSize(new Dimension(100,200));
		}
		return jScrollPaneFolder;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.gridx = 0;
			gridBagConstraints91.anchor = GridBagConstraints.WEST;
			gridBagConstraints91.gridy = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridwidth = 2;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.insets = new Insets(0, 40, 0, 0);
			gridBagConstraints31.gridy = 3;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.weighty = 0.0;
			gridBagConstraints9.insets = new Insets(3, 10, 0, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 4;
			gridBagConstraints2.weightx = 0.0;
			gridBagConstraints2.weighty = 0.0;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.gridwidth = 2;
			jPanelTop = new JPanel();
			jPanelTop.setLayout(new GridBagLayout());
			jPanelTop.add(getJRadioButtonNotContain(), gridBagConstraints);
			jPanelTop.add(getJRadioButtonIKnow(), gridBagConstraints2);
			jPanelTop.add(getJButton(), gridBagConstraints9);
			jPanelTop.add(getJPanelThirdTitle(), gridBagConstraints91);
		}
		return jPanelTop;
	}
	
	/**
	 * This method initializes jPanelThirdTitle	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelThirdTitle() {
		if (jPanelThirdTitle == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridx = -1;
			gridBagConstraints11.gridy = -1;
			gridBagConstraints11.weighty = 0.0;
			gridBagConstraints11.gridwidth = 1;
			jPanelThirdTitle = new JPanel();
			jPanelThirdTitle.setLayout(new GridBagLayout());
			jPanelThirdTitle.add(getJRadioButtonThird(), gridBagConstraints11);
			jPanelThirdTitle.add(getJButtonThird(), gridBagConstraints12);
		}
		return jPanelThirdTitle;
	}
	
	/**
	 * This method initializes jButtonThird	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonThird() {
		if (jButtonThird == null) {
			jButtonThird = new JButton();
			jButtonThird.setBorder(null);
			jButtonThird.setBackground(new Color(238, 238, 238));
			jButtonThird.setPreferredSize(new Dimension(20, 20));
		}
		return jButtonThird;
	}
	public void setOptionUI() {
		if(getJRadioButtonIKnow().isSelected()) {
			jLabelForder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
			if(!getJScrollPaneFolder().isVisible()) {
				getJScrollPaneFolder().setVisible(true);
			}
		} else {
			jLabelForder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
			if(getJScrollPaneFolder().isVisible()) {
				getJScrollPaneFolder().setVisible(false);
			}
		}
	}

	public JRadioButton getRdbtnThird() {
		return rdbtnThird;
	}

	public void setRdbtnThird(JRadioButton rdbtnThird) {
		this.rdbtnThird = rdbtnThird;
	}

	public JRadioButton getRdbtnItsLibraryWhich() {
		return rdbtnItsLibraryWhich;
	}

	public void setRdbtnItsLibraryWhich(JRadioButton rdbtnItsLibraryWhich) {
		this.rdbtnItsLibraryWhich = rdbtnItsLibraryWhich;
	}

	public JRadioButton getRdbtnIKnowThe() {
		return rdbtnIKnowThe;
	}

	public void setRdbtnIKnowThe(JRadioButton rdbtnIKnowThe) {
		this.rdbtnIKnowThe = rdbtnIKnowThe;
	}

	public JComboBox<String> getCbPmBindType() {
		return cbPmBindType;
	}

	public void setCbPmBindType(JComboBox<String> cbPmBindType) {
		this.cbPmBindType = cbPmBindType;
	}
	
	private String getSelectedLicenseName() {
		String licenseName = "";
		licenseName = getJComboBoxLicenseForOpt3().getCurrentLicenseName();
		return licenseName;
	}

	public void refreshUI(String projectName) {

		rdbtnItsLibraryWhich.setSelected(false);
		rdbtnThird.setSelected(false);
		rdbtnIKnowThe.setSelected(true);
		this.setOptionUI();
		
		getJComboBoxComponentForOpt3().initComponentComboBox();
		getJComboBoxComponentForOpt3().activateComponentCombo();
		
		getJComboBoxLicenseForOpt3().initLicenseComboBox();
		getJComboBoxLicenseForOpt3().activateLicenseCombo();

		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		String tmpSelectedPath = selectedPaths.getSelectedPath();

		HashMap<Integer, Integer> counterPerStatus = IdentificationDBManager.setOkCancelButtonEnabledForPatternMatch(projectName, selectedPaths.getSelectedPaths());
		int pendingCount = counterPerStatus.get(AbstractMatchInfo.STATUS_PENDING);
		int identifiedCount = counterPerStatus.get(AbstractMatchInfo.STATUS_IDENTIFIED);

		if(identifiedCount > 0) {
			IdentifyMediator.getInstance().setOKResetButtonsForPatternMatch(false, true);
		} else if(identifiedCount==0 && pendingCount > 0) {
			IdentifyMediator.getInstance().setOKResetButtonsForPatternMatch(true, false);
		} else if(identifiedCount==0 && pendingCount == 0) {
			IdentifyMediator.getInstance().setOKResetButtonsForPatternMatch(false, false);
		}
		
		boolean bFile = selectedPaths.isFile();
		if(tmpSelectedPath != null && bFile) {
			int bindingType = IdentificationConstantValue.BINDING_TYPE_STATIC;
			String strExtention = FileOperator.getExtention(tmpSelectedPath);
			if(strExtention != null) {
				if(strExtention.equalsIgnoreCase("so")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_STATIC;
				} else if(strExtention.equalsIgnoreCase("a")||strExtention.equalsIgnoreCase("dll")||strExtention.equalsIgnoreCase("jar")||strExtention.equalsIgnoreCase("lib")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_DYNAMIC;
				} else if(strExtention.equalsIgnoreCase("o")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_OBJECT;
				} else if(strExtention.equalsIgnoreCase("exe")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_EXECUTABLE;
				} else if(strExtention.equalsIgnoreCase("txt")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_TEXT;
				} else if(strExtention.equalsIgnoreCase("zip")) {
					bindingType = IdentificationConstantValue.BINDING_TYPE_COMPRESSION;
				}
			}
			cbPmBindType.setSelectedIndex(bindingType);
			cbPmBindType.setEnabled(true);
			
			
			try {
				String strComponentName = IdentificationDBManager.getComponentNameForPatternMatchFile(projectName, tmpSelectedPath);
				String strLicense = IdentificationDBManager.getLicenseForPatternMatch(projectName, tmpSelectedPath);
				getJComboBoxComponentForOpt3().setComponentName(strComponentName);
				getJComboBoxLicenseForOpt3().setLicenseComboBox(strLicense);
			} catch (Exception e) {
				log.debug(e.getMessage());
			}

		} else if(tmpSelectedPath != null && !bFile) {
			
		} else {
			IdentifyMediator.getInstance().changeFileNavigationPanel(JPanIdentifyMain.INDEX_TREE);
		}


	}
	
	/*******************************************************************************************************
	 * 		Action
	 */
	class RadioButtonItsLibraryWitchAction implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("getJRadioButtonNotContain actionPerformed()");
			if(rdbtnItsLibraryWhich.isSelected()) {
				cbPmBindType.setSelectedIndex(0);
				cbPmBindType.setEnabled(false);
				jComboComponentNameForOpt3.setEnabled(false);
				jComboLicenseNameForOpt3.setEnabled(false);
				
				log.debug("license init");
			}
			getJRadioButtonNotContain().setSelected(true);
			getJRadioButtonThird().setSelected(false);
			getJRadioButtonIKnow().setSelected(false);
			setOptionUI();
		}
	}
	
	class RadioButtonThird implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("getJRadioButtonThird actionPerformed()");
			if(rdbtnThird.isSelected()) {
				cbPmBindType.setSelectedIndex(0);
				cbPmBindType.setEnabled(false);
				jComboComponentNameForOpt3.setEnabled(false);
				jComboLicenseNameForOpt3.setEnabled(false);
				
				log.debug("license init");
			}
			getJRadioButtonNotContain().setSelected(false);
			getJRadioButtonThird().setSelected(true);
			getJRadioButtonIKnow().setSelected(false);
			setOptionUI();			
		}
	}
	
	class RadioButtonIKnowAction implements ActionListener{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("getJRadioButtonIKnow actionPerformed()");
			if(rdbtnIKnowThe.isSelected()) {
				cbPmBindType.setEnabled(true);
				jComboComponentNameForOpt3.setEnabled(true);
				jComboLicenseNameForOpt3.setEnabled(true);

				log.debug("license init");
			}
			getJRadioButtonNotContain().setSelected(false);
			getJRadioButtonThird().setSelected(false);
			getJRadioButtonIKnow().setSelected(true);
			setOptionUI();
		}
	}
	
	private String getSelectedComponentName() {
		log.debug("getSelectedComponentName");
		
		String componentName = "";
		componentName = getJComboBoxComponentForOpt3().getCurrentComponentName();
		return componentName;
	}

	public UEPatternMatch exportUIEntity(String projectName) {
		
		String currentComponentName = "";
		String newComponentName = "";
		String newLicenseName = getSelectedLicenseName();
		
		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		boolean bFile = selectedPaths.isFile();
		String selectedPath = selectedPaths.getSelectedPath();

		if(bFile) {
			currentComponentName = IdentificationDBManager.getComponentNameForPatternMatchFile(projectName, selectedPath);
		} else {
			currentComponentName = IdentificationDBManager.getComponentNameForPatternMatchResetFolder(projectName, selectedPath);
		}
		
		int status = IdentificationDBManager.getStatusForPatternMatch(projectName, selectedPath);
		
		if(rdbtnItsLibraryWhich.isSelected()){
			newComponentName = "DECLARED_PROPRIETARY_LIBRARY";
		} else if(rdbtnThird.isSelected()) {
			newComponentName = "DECLARED_THIRDPARTY_LIBRARY";
		} else if(rdbtnIKnowThe.isSelected()){
			
			if( status == AbstractMatchInfo.STATUS_PENDING &&
				getJComboBoxLicenseForOpt3().getSelectedItem() == null) {
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
	        
	        if( status == AbstractMatchInfo.STATUS_PENDING 
				&& LicenseAPIWrapper.getLicenseID(newLicenseName) == null) {
					JOptionPane.showOptionDialog(
		        			null, 
		        			"The license name ("+newLicenseName+") does not exist in Protex Server. " +
		        			"\nPlease correct the license name.",
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
        		newComponentName = (String)cbPmBindType.getSelectedItem()+"_"+IdentifyMediator.getInstance().getSelectedLicenseName();
        	}
		}
		
		if(currentComponentName == null) currentComponentName = "";
		if(newLicenseName == null) newLicenseName = "";
		UEPatternMatch xUEPatternMatch = new UEPatternMatch(
															currentComponentName,
															newComponentName, 
															newLicenseName);
		return xUEPatternMatch;
	}
	private String[] buttonOK = {"OK"};
} 
