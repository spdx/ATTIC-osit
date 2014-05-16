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
package com.sec.ose.osi.ui.dialog.setting;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.Tools;

/**
 * JPanProjectAnalysisSetting
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 *
 */
public class JPanProjectAnalysisSetting extends JPanel {
	private static Log log = LogFactory.getLog(JPanProjectAnalysisSetting.class);
	
	private static final long serialVersionUID = -6456823491974978305L;

	UEProjectAnalysisSetting ueProjectAnalysis = null;
	
	private JRadioButton jRadioButtonDisable = null;
	private JRadioButton jRadioButton12Hour = null;
	private JRadioButton jRadioButton24Hour = null;
	private JRadioButton jRadioButton48Hour = null;
	private JRadioButton jRadioButtonUserDefined = null;
	private JRadioButton jRadioButtonTimeSet = null;
	
	private JPanel jIntMainPanel = null;
	private JPanel jIntRadioTopPanel = null;
	private JPanel jIntRadioBottomPanel = null;
	private JTextField jTextFieldUserHour = null;	
	private JLabel jLabelUserHour = null;
	
	private JLabel jLabelTimeHour = null;
	private JLabel jLabelTimeMinite = null;
	private JPanel jPanel = null;

	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private Component mParent = null;
	private JPanManageMain analPanel = null; 

	private JPanel jInnerPanel = null;
	private JComboBox<String> jComboBoxTimeCycle = null;
	private JComboBox<String> jComboBoxTimeHour = null;
	private JComboBox<String> jComboBoxTimeMinite = null;
	private JTextField jTextFieldProjectSplitFileCountLimit = null;
	private JLabel jLabelPojectSplitFileCountLimit = null;
	private JLabel jLabel = null;

	private JPanel jPanelProjectSplit = null;

	public JPanProjectAnalysisSetting() {
		super();
		initialize();
	}
	
	public JPanProjectAnalysisSetting(Component pParent) {
		super();
		this.mParent = pParent;
		initialize();
	}

	public void setJPanManageMain(JPanManageMain p){   
		analPanel = p;
		load(analPanel.getUIEntityMonitoringInterval());
	}

	public void setParent(Component pParent) {
		this.mParent = pParent;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
		gridBagConstraints71.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints71.weightx = 1.0;
		gridBagConstraints71.weighty = 1.0;
		gridBagConstraints71.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints71.fill = GridBagConstraints.BOTH;
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		gridBagConstraints14.gridx = -1;
		gridBagConstraints14.anchor = GridBagConstraints.NORTH;
		gridBagConstraints14.insets = new Insets(10, 10, 0, 10);
		gridBagConstraints14.gridy = -1;
		this.setLayout(new GridBagLayout());
		this.add(getJIntMainPanel(), gridBagConstraints71);
		this.add(getJPanel(), gridBagConstraints14);
	}

	private void load(UEProjectAnalysisSetting ueProjectAnalysisSetting) {
		int selectedType = UEProjectAnalysisSetting.TYPE_DISABLE;
		if(ueProjectAnalysisSetting != null) {
			ueProjectAnalysis = ueProjectAnalysisSetting;
			selectedType = ueProjectAnalysisSetting.getSelectedType();
		} else {
			ueProjectAnalysis = new UEProjectAnalysisSetting();
			ueProjectAnalysis.setProjectSplitFileCountLimit(Property.getInstance().getDefaultNumOfFilesUpperLimit());
		}
		
		jTextFieldProjectSplitFileCountLimit.setText(String.valueOf(ueProjectAnalysis.getProjectSplitFileCountLimit()));
		
		JRadioButton selectedJRadioButton = jRadioButtonDisable;
		switch(selectedType) {
		case UEProjectAnalysisSetting.TYPE_DISABLE :
			selectedJRadioButton = jRadioButtonDisable;
			break;
		case UEProjectAnalysisSetting.TYPE_12_HOUR :
			selectedJRadioButton = jRadioButton12Hour;
			break;
		case UEProjectAnalysisSetting.TYPE_24_HOUR :
			selectedJRadioButton = jRadioButton24Hour;
			break;
		case UEProjectAnalysisSetting.TYPE_48_HOUR :
			selectedJRadioButton = jRadioButton48Hour;
			break;
		case UEProjectAnalysisSetting.TYPE_USER_DEFINED :
			selectedJRadioButton = jRadioButtonUserDefined;
			jTextFieldUserHour.setText(String.valueOf(ueProjectAnalysisSetting.getMonitorInterval()));
			break;
		case UEProjectAnalysisSetting.TYPE_FIXED_TIME :
			selectedJRadioButton = jRadioButtonTimeSet;

			jComboBoxTimeCycle.setSelectedIndex(ueProjectAnalysisSetting.getTimeCycle());
			jComboBoxTimeHour.setSelectedIndex(ueProjectAnalysisSetting.getTimeHour());
			jComboBoxTimeMinite.setSelectedIndex(ueProjectAnalysisSetting.getTimeMinite());
			break;
		}

		setUI(selectedJRadioButton);
	}
	

	/**
	 * This method initializes jRadioButtonDisable	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonDisable() {
		if (jRadioButtonDisable == null) {
			jRadioButtonDisable = new JRadioButton();
			jRadioButtonDisable.setText("Disable");
			jRadioButtonDisable.addActionListener(new RadioButtonAction());
		}
		return jRadioButtonDisable;
	}

	/**
	 * This method initializes jRadioButton12Hour	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton12Hour() {
		if (jRadioButton12Hour == null) {
			jRadioButton12Hour = new JRadioButton();
			jRadioButton12Hour.setText("12Hour");
			jRadioButton12Hour.addActionListener(new RadioButtonAction());
		}
		return jRadioButton12Hour;
	}

	/**
	 * This method initializes jRadioButton24Hour	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton24Hour() {
		if (jRadioButton24Hour == null) {
			jRadioButton24Hour = new JRadioButton();
			jRadioButton24Hour.setText("24Hour");
			jRadioButton24Hour.addActionListener(new RadioButtonAction());
		}
		return jRadioButton24Hour;
	}
	
	/**
	 * This method initializes jRadioButton48Hour	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton48Hour() {
		if (jRadioButton48Hour == null) {
			jRadioButton48Hour = new JRadioButton();
			jRadioButton48Hour.setText("48Hour");

			jRadioButton48Hour.addActionListener(new RadioButtonAction());
		}
		return jRadioButton48Hour;
	}

	/**
	 * This method initializes jRadioButtonUserHour	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonUserHour() {
		if (jRadioButtonUserDefined == null) {
			jRadioButtonUserDefined = new JRadioButton();
			jRadioButtonUserDefined.setText("User defined : ");

			jRadioButtonUserDefined.addActionListener(new RadioButtonAction());
		}
		return jRadioButtonUserDefined;
	}

	/**
	 * This method initializes jRadioButtonTimeSet	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonTimeSet() {
		if (jRadioButtonTimeSet == null) {
			jRadioButtonTimeSet = new JRadioButton();
			jRadioButtonTimeSet.setText("Fixed time :");
			jRadioButtonTimeSet.addActionListener(new RadioButtonAction());
		}
		return jRadioButtonTimeSet;
	}

	/**
	 * This method initializes jTextFieldUserHour
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUserHour() {
		if (jTextFieldUserHour == null) {
			jTextFieldUserHour = new JTextField();         
			jTextFieldUserHour.setPreferredSize(new Dimension(80, 22));
			jTextFieldUserHour.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					char c = e.getKeyChar();
					if (!((Character.isDigit(c) ||
							(c == KeyEvent.VK_BACK_SPACE) || 
							(c == KeyEvent.VK_DELETE)
						  ))) 
					{
						getToolkit().beep();
						e.consume();
					}
				}
			});
	
		}
		return jTextFieldUserHour;
	}

	/**
	 * This method initializes jIntRadioTopPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJIntRadioTopPanel() {
		if (jIntRadioTopPanel == null) {

			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.gridy = 2;
			
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 8;
			jLabelUserHour = new JLabel();
			jLabelUserHour.setText(" Hour (more than 12 hours)");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 8;
			gridBagConstraints4.weightx = 0.0;
			gridBagConstraints4.weighty = 0.0;
			gridBagConstraints4.ipadx = 0;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 8;
			gridBagConstraints10.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 7;
			gridBagConstraints9.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 6;
			gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 5;
			gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = -1;
			gridBagConstraints3.gridy = 4;
			gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 5;
			gridBagConstraints6.insets = new Insets(0, 0, 0, 0);			
			jIntRadioTopPanel = new JPanel();
			jIntRadioTopPanel.setLayout(new GridBagLayout());
			jIntRadioTopPanel.add(getJRadioButtonDisable(), gridBagConstraints3);
			jIntRadioTopPanel.add(getJRadioButton12Hour(), gridBagConstraints5);
			jIntRadioTopPanel.add(getJRadioButton24Hour(), gridBagConstraints7);
			jIntRadioTopPanel.add(getJRadioButton48Hour(), gridBagConstraints9);
			jIntRadioTopPanel.add(getJRadioButtonUserHour(), gridBagConstraints10);
			jIntRadioTopPanel.add(getJTextFieldUserHour(), gridBagConstraints4);
			jIntRadioTopPanel.add(jLabelUserHour, gridBagConstraints);
		}
		return jIntRadioTopPanel;
	}

	/**
	 * This method initializes jIntRadioBottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJIntRadioBottomPanel() {
		if (jIntRadioBottomPanel == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 5;
			gridBagConstraints22.gridy = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridx = 4;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 3;
			gridBagConstraints19.gridy = 0;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints18.gridy = 0;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.gridx = 2;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.insets = new Insets(0, 0, 1, 0);
			gridBagConstraints15.gridy = 0;
			jLabelTimeMinite = new JLabel();
			jLabelTimeMinite.setText("");
			jLabelTimeHour = new JLabel();
			jLabelTimeHour.setText(" : ");
			jIntRadioBottomPanel = new JPanel();
			jIntRadioBottomPanel.setLayout(new GridBagLayout());
			jIntRadioBottomPanel.add(getJRadioButtonTimeSet(), gridBagConstraints15);
			jIntRadioBottomPanel.add(getJComboBoxTimeCycle(), gridBagConstraints13);
			jIntRadioBottomPanel.add(getJComboBoxTimeHour(), gridBagConstraints18);
			jIntRadioBottomPanel.add(jLabelTimeHour, gridBagConstraints19);
			jIntRadioBottomPanel.add(getJComboBoxTimeMinite(), gridBagConstraints21);
			jIntRadioBottomPanel.add(jLabelTimeMinite, gridBagConstraints22);
		}
		return jIntRadioBottomPanel;
	}

	/**
	 * This method initializes jIntMainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJIntMainPanel() {
		if (jIntMainPanel == null) {

			GridBagConstraints gridBagConstraintsSplit = new GridBagConstraints();
			gridBagConstraintsSplit.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraintsSplit.weighty = 1.0;
			gridBagConstraintsSplit.insets = new Insets(5, 5, 0, 5);
			gridBagConstraintsSplit.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsSplit.weightx = 1.0;
			
			GridBagConstraints gridBagConstraintsMonitor = new GridBagConstraints();
			gridBagConstraintsMonitor.anchor = GridBagConstraints.WEST;
			gridBagConstraintsMonitor.weighty = 1.0;
			gridBagConstraintsMonitor.insets = new Insets(5, 5, 0, 5);
			gridBagConstraintsMonitor.gridx = 0;
			gridBagConstraintsMonitor.gridy = 1;
			gridBagConstraintsMonitor.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsMonitor.weightx = 1.0;

			jIntMainPanel = new JPanel();
			jIntMainPanel.setLayout(new GridBagLayout());
			jIntMainPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jIntMainPanel.add(getJPanelProjectSplit(), gridBagConstraintsSplit);
			jIntMainPanel.add(getJPanelMonitorInterval(), gridBagConstraintsMonitor);
		}
		return jIntMainPanel;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.weighty = 0.0;
			gridBagConstraints17.weightx = 0.0;
			gridBagConstraints17.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints17.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.anchor = GridBagConstraints.NORTH;
			gridBagConstraints16.weighty = 0.0;
			gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.weightx = 0.0;
			gridBagConstraints16.gridy = 1;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonOK(), gridBagConstraints17);
			jPanel.add(getJButtonCancel(), gridBagConstraints16);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
			jButtonOK.setPreferredSize(new Dimension(73, 28));
			jButtonOK.addActionListener(new OKButtonAction());
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mParent.setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jInnerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelMonitorInterval() {
		if (jInnerPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints12.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints8.gridx = -1;
			gridBagConstraints8.gridy = -1;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			jInnerPanel = new JPanel();
			jInnerPanel.setLayout(new GridBagLayout());
			jInnerPanel.setBorder(BorderFactory.createTitledBorder(null, "Monitor Interval", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jInnerPanel.add(getJIntRadioTopPanel(), gridBagConstraints8);
			jInnerPanel.add(getJIntRadioBottomPanel(), gridBagConstraints12);
		}
		return jInnerPanel;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxTimeCycle() {
		if (jComboBoxTimeCycle == null) {
			String[] data = {"Daily","Every Sunday","Every Monday","Every Tuesday","Every Wednesday","Every Thursday","Every Friday","Every Saturday"};
			jComboBoxTimeCycle = new JComboBox<String>(data);
			jComboBoxTimeCycle.setPreferredSize(new Dimension(130, 27));
		}
		return jComboBoxTimeCycle;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxTimeHour() {
		if (jComboBoxTimeHour == null) {
			String[] data = {"00","01","02","03","04","05","06","07","08","09","10",
					"11","12","13","14","15","16","17","18","19","20","21","22","23"};			
			jComboBoxTimeHour = new JComboBox<String>(data);
			jComboBoxTimeHour.setPreferredSize(new Dimension(50, 27));
		}
		return jComboBoxTimeHour;
	}

	/**
	 * This method initializes jComboBox2	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxTimeMinite() {
		if (jComboBoxTimeMinite == null) {
			String[] data = {"00","01","02","03","04","05","06","07","08","09","10",
					"11","12","13","14","15","16","17","18","19","20",
					"21","22","23","24","25","26","27","28","29","30",
					"31","32","33","34","35","36","37","38","39","40",
					"41","42","43","44","45","46","47","48","49","50",
					"51","52","53","54","55","56","57","58","59"};			
			jComboBoxTimeMinite = new JComboBox<String>(data);
			jComboBoxTimeMinite.setPreferredSize(new Dimension(50, 27));
		}
		return jComboBoxTimeMinite;
	}	

	private class OKButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			int projectSplitFileNum = Tools.transStringToInteger(getJTextFieldProjectSplitFileCountLimit().getText());
			if(projectSplitFileNum < Property.getInstance().getMinNumOfFilesUpperLimit() || projectSplitFileNum > Property.getInstance().getMaxNumOfFilesUpperLimit()){
				JOptionPane.showMessageDialog(
						null, 
						"The range of \"Max number of files for a Project\" should be between "+Property.getInstance().getMinNumOfFilesUpperLimit()+" and "+Property.getInstance().getMaxNumOfFilesUpperLimit(),
						"Setup Confirm",
						JOptionPane.ERROR_MESSAGE);	
				return;
			}
			
			int selectedType = UEProjectAnalysisSetting.TYPE_DISABLE;
			int interval = 0;
			if(jRadioButton12Hour.isSelected()){
				selectedType = UEProjectAnalysisSetting.TYPE_12_HOUR;
				interval = 12;
			} else if(jRadioButton24Hour.isSelected()){
				selectedType = UEProjectAnalysisSetting.TYPE_24_HOUR;
				interval = 24;
			} else if(jRadioButton48Hour.isSelected()){
				selectedType = UEProjectAnalysisSetting.TYPE_48_HOUR;
				interval = 48;
			} else if(jRadioButtonUserDefined.isSelected()){
				selectedType = UEProjectAnalysisSetting.TYPE_USER_DEFINED;
				String userIntervalStr = jTextFieldUserHour.getText().trim();

				if(userIntervalStr.length() <= 0){
					JOptionPane.showMessageDialog(
							null, 
							"Enter the user define of the time.",
							"Setup Confirm",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
				
				try {
					interval = Tools.transStringToInteger(userIntervalStr);
				} catch (Exception e1) {
					log.warn(e1.getMessage());
					JOptionPane.showMessageDialog(
							null, 
							"Enter only digits.",
							"Setup Confirm",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
			
				if(interval < 12 || interval > 999){
					JOptionPane.showMessageDialog(
							null, 
							"Enter three digits or less (more than 12 hours).",
							"Setup Confirm",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
			} else if(jRadioButtonTimeSet.isSelected()){
				selectedType = UEProjectAnalysisSetting.TYPE_FIXED_TIME;
				ueProjectAnalysis.setTimeCycle(jComboBoxTimeCycle.getSelectedIndex());
				ueProjectAnalysis.setTimeHour(jComboBoxTimeHour.getSelectedIndex());
				ueProjectAnalysis.setTimeMinite(jComboBoxTimeMinite.getSelectedIndex());
			}
			
			ueProjectAnalysis.setSelectedType(selectedType);
			ueProjectAnalysis.setMonitorInterval(interval);
			ueProjectAnalysis.setProjectSplitFileCountLimit(projectSplitFileNum);
			analPanel.setProjectAnalysisSetting(ueProjectAnalysis);
			
			mParent.setVisible(false);
		}
	}
	
	private class RadioButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton selectedJRadioButton = (JRadioButton)e.getSource();
			log.debug("Selected "+selectedJRadioButton.getText());
			setUI(selectedJRadioButton);
		}
	}
	private void setUI(JRadioButton selectedJRadioButton) {
		jRadioButtonDisable.setSelected(false);
		jRadioButton12Hour.setSelected(false);
		jRadioButton24Hour.setSelected(false);
		jRadioButton48Hour.setSelected(false);
		jRadioButtonUserDefined.setSelected(false);
		jRadioButtonTimeSet.setSelected(false);
		
		selectedJRadioButton.setSelected(true);

		if(jRadioButtonUserDefined.isSelected()){
			getJTextFieldUserHour().setEnabled(true);
			getJTextFieldUserHour().setSelectionStart(0);
			getJTextFieldUserHour().setSelectionEnd(getJTextFieldUserHour().getText().length());
			getJTextFieldUserHour().requestFocusInWindow();
			
			getJComboBoxTimeCycle().setEnabled(false);
			getJComboBoxTimeHour().setEnabled(false);
			getJComboBoxTimeMinite().setEnabled(false);
		} else if(jRadioButtonTimeSet.isSelected()){
			getJTextFieldUserHour().setEnabled(false);
			
			getJComboBoxTimeCycle().setEnabled(true);
			getJComboBoxTimeHour().setEnabled(true);
			getJComboBoxTimeMinite().setEnabled(true);
		} else {
			getJTextFieldUserHour().setEnabled(false);
			
			getJComboBoxTimeCycle().setEnabled(false);
			getJComboBoxTimeHour().setEnabled(false);
			getJComboBoxTimeMinite().setEnabled(false);
		}
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldProjectSplitFileCountLimit() {
		if (jTextFieldProjectSplitFileCountLimit == null) {
			jTextFieldProjectSplitFileCountLimit = new JTextField();
			jTextFieldProjectSplitFileCountLimit.setPreferredSize(new Dimension(80, 22));
		}
		return jTextFieldProjectSplitFileCountLimit;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelProjectSplit() {
		if (jPanelProjectSplit == null) {
			jPanelProjectSplit = new JPanel();
			jPanelProjectSplit.setLayout(new GridBagLayout());
			jPanelProjectSplit.setBorder(BorderFactory.createTitledBorder(null, "Project Split", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			
			GridBagConstraints gridBagConstraintsLabelFileCountLimit = new GridBagConstraints();
			gridBagConstraintsLabelFileCountLimit.gridx = 0;
			gridBagConstraintsLabelFileCountLimit.ipadx = 0;
			gridBagConstraintsLabelFileCountLimit.insets = new Insets(0, 5, 0, 0);
			gridBagConstraintsLabelFileCountLimit.gridwidth = 2;
			gridBagConstraintsLabelFileCountLimit.gridy = 1;
			jLabelPojectSplitFileCountLimit = new JLabel();
			jLabelPojectSplitFileCountLimit.setText("Max Number of Files for a project :");
			GridBagConstraints gridBagConstraintsTextFieldSplit = new GridBagConstraints();
			gridBagConstraintsTextFieldSplit.fill = GridBagConstraints.VERTICAL;
			gridBagConstraintsTextFieldSplit.gridy = 1;
			gridBagConstraintsTextFieldSplit.weightx = 0.0;
			gridBagConstraintsTextFieldSplit.insets = new Insets(0, 5, 0, 0);
			gridBagConstraintsTextFieldSplit.anchor = GridBagConstraints.WEST;
			gridBagConstraintsTextFieldSplit.gridx = 2;
			GridBagConstraints gridBagConstraintsLabelSplitRange = new GridBagConstraints();
			gridBagConstraintsLabelSplitRange.gridx = 3;
			gridBagConstraintsLabelSplitRange.insets = new Insets(0, 5, 0, 0);
			gridBagConstraintsLabelSplitRange.weightx = 1.0;
			gridBagConstraintsLabelSplitRange.anchor = GridBagConstraints.WEST;
			gridBagConstraintsLabelSplitRange.gridy = 1;
			jLabel = new JLabel();
			jLabel.setText(
					"( "
					+Property.getInstance().getMinNumOfFilesUpperLimit()
					+" ~ "
					+Property.getInstance().getMaxNumOfFilesUpperLimit()
					+" )" );
			jPanelProjectSplit.add(jLabelPojectSplitFileCountLimit, gridBagConstraintsLabelFileCountLimit);
			
			jPanelProjectSplit.add(getJTextFieldProjectSplitFileCountLimit(), gridBagConstraintsTextFieldSplit);
			jPanelProjectSplit.add(jLabel, gridBagConstraintsLabelSplitRange);
		}
		return jPanelProjectSplit;
	}

}
