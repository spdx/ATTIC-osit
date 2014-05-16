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
package com.sec.ose.osi.ui.frm.main.identification;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * JPanMatchTypeSelection
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanMatchTypeSelection extends JPanel {
	private static Log log = LogFactory.getLog(JPanMatchTypeSelection.class);

	private static final long serialVersionUID = 1L;
	
	private static final Color COLOR_SELECTED = new Color(215, 228, 248);
	private static final Color COLOR_UNSELECTED = new Color(238, 238, 238);
	
	private JLabel jLabelArrow1 = null;
	private JLabel jLabelArrow2 = null;
	private JButton jBtnStringSearch = null;
	private JButton jBtnCodeMatch = null;
	private JButton jBtnPatternMatch = null;
	
	private int currentSelectedMatchType = IdentificationConstantValue.STRING_MATCH_TYPE;
	
	private IdentifyMediator mIdentifyMediator;
	
	public JPanMatchTypeSelection(IdentifyMediator pIdentifyMediator) {
		this.mIdentifyMediator = pIdentifyMediator;
		init();
	}
	
	private void init(){
	
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 3;
		gridBagConstraints12.ipadx = 5;
		gridBagConstraints12.gridy = 0;
		jLabelArrow2 = new JLabel();
		jLabelArrow2.setText("¡æ");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.ipadx = 5;
		gridBagConstraints11.gridy = 0;
		jLabelArrow1 = new JLabel();
		jLabelArrow1.setText("¡æ");
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.ipadx = 10;
		gridBagConstraints8.insets = new Insets(10, 0, 10, 10);
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 4;
		gridBagConstraints7.ipadx = 5;
		gridBagConstraints7.gridy = 0;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.ipadx = 18;
		gridBagConstraints4.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints4.gridy = 0;
		
		this.setLayout(new GridBagLayout());
		this.add(getJButtonStringSearch(), gridBagConstraints8);
		this.add(getJButtonCodeMatch(), gridBagConstraints4);
		this.add(getJButtonPatternMatch(), gridBagConstraints7);
		this.add(jLabelArrow1, gridBagConstraints11);
		this.add(jLabelArrow2, gridBagConstraints12);
	}
	
	private JButton getJButtonStringSearch() {
	
		if (jBtnStringSearch == null) {
			jBtnStringSearch = new JButton();
			jBtnStringSearch.setText("String Search ( 0 / 0 )");
			jBtnStringSearch.setEnabled(true);
			jBtnStringSearch.setBackground(new Color(238, 238, 238));
			jBtnStringSearch.setFocusPainted(false);
			jBtnStringSearch.setActionCommand(""+EventHandler.BTN_STRING_SEARCH);
			jBtnStringSearch.addActionListener(handler);
		}
		return jBtnStringSearch;
	}
	
	private JButton getJButtonCodeMatch() {
		if (jBtnCodeMatch == null) {
			jBtnCodeMatch = new JButton();
			jBtnCodeMatch.setText("Code Match ( 0 / 0 )");
			jBtnCodeMatch.setEnabled(true);
			jBtnCodeMatch.setBackground(new Color(238, 238, 238));
			jBtnCodeMatch.setFocusPainted(false);
			jBtnCodeMatch.setActionCommand(""+EventHandler.BTN_CODE_MATCH);
			jBtnCodeMatch.addActionListener(handler);
		}
		return jBtnCodeMatch;
	}
	
	private JButton getJButtonPatternMatch() {
		if (jBtnPatternMatch == null) {
			jBtnPatternMatch = new JButton();
			jBtnPatternMatch.setText("Pattern Match ( 0 / 0 )");
			jBtnPatternMatch.setEnabled(true);
			jBtnPatternMatch.setBackground(new Color(238,238,238));
			jBtnPatternMatch.setFocusPainted(false);
			jBtnPatternMatch.setActionCommand(""+EventHandler.BTN_PATTERN_MATCH);
			jBtnPatternMatch.addActionListener(handler);
		}
		return jBtnPatternMatch;
	}
	

	
	EventHandler handler = new EventHandler();
	
	class EventHandler implements ActionListener {
		
		private static final int BTN_STRING_SEARCH = 1;
		private static final int BTN_CODE_MATCH = 2;
		private static final int BTN_PATTERN_MATCH = 3;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int selectedButton = -1;
			try {
				selectedButton = Integer.parseInt(e.getActionCommand());
			} catch(NumberFormatException ex) {
				
			}
			
			log.debug("String Search/Code Match/Pattern Match Button Clicked !!");
			int selectedType = IdentificationConstantValue.STRING_MATCH_TYPE;
			switch(selectedButton) {
			
				case BTN_STRING_SEARCH:
					selectedType = IdentificationConstantValue.STRING_MATCH_TYPE;
					break;
					
				case BTN_CODE_MATCH:
					selectedType = IdentificationConstantValue.CODE_MATCH_TYPE;
					break;
					
				case BTN_PATTERN_MATCH:
					selectedType = IdentificationConstantValue.PATTERN_MATCH_TYPE;
					break;
			}

			mIdentifyMediator.changeSelectedIdentificationPanel(selectedType);
		}
	}

	 void updateButtonCount(String projectName) {
		
		int ssPendingCnt = ProjectDiscoveryControllerMap
						.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE)
						.getNumOfPendingFiles();
		int cmPendingCnt = ProjectDiscoveryControllerMap
						.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE)
						.getNumOfPendingFiles();
		int pmPendingCnt = ProjectDiscoveryControllerMap
						.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE)
						.getNumOfPendingFiles();
		
		int ssDiscoveryCnt = ProjectDiscoveryControllerMap
								.getDiscoveryController(projectName, IdentificationConstantValue.STRING_MATCH_TYPE)
								.getNumOfDiscoveryFiles();
		int cmDiscoveryCnt = ProjectDiscoveryControllerMap
								.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE)
								.getNumOfDiscoveryFiles();
		int pmDiscoveryCnt = ProjectDiscoveryControllerMap
								.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE)
								.getNumOfDiscoveryFiles();

		jBtnStringSearch.setText("String Search ( " + String.valueOf(ssPendingCnt)+" / "+String.valueOf(ssDiscoveryCnt)+" )");
		jBtnCodeMatch.setText("Code Match ( " + String.valueOf(cmPendingCnt)+" / "+String.valueOf(cmDiscoveryCnt)+" )");
		jBtnPatternMatch.setText("Pattern Match ( " + String.valueOf(pmPendingCnt)+" / "+String.valueOf(pmDiscoveryCnt)+" )");

	}

	private void updateButtonColor() {
		
		jBtnStringSearch.setBackground(COLOR_UNSELECTED);
		jBtnCodeMatch.setBackground(COLOR_UNSELECTED);
		jBtnPatternMatch.setBackground(COLOR_UNSELECTED);
		
		switch(JPanMatchTypeSelection.this.currentSelectedMatchType) {
			case IdentificationConstantValue.STRING_MATCH_TYPE:
				jBtnStringSearch.setBackground(COLOR_SELECTED);
				break;
			
			case IdentificationConstantValue.CODE_MATCH_TYPE:
				jBtnCodeMatch.setBackground(COLOR_SELECTED);
				break;
			
			case IdentificationConstantValue.PATTERN_MATCH_TYPE:
				jBtnPatternMatch.setBackground(COLOR_SELECTED);
				break;
		}
		
	}

	public void setSelectedPendingType(int typeID) {
		this.currentSelectedMatchType = typeID;
		this.updateButtonColor();
	}

	public int getSelectedMatchType() {
		return this.currentSelectedMatchType;
	}
}
