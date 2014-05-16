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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.identification.codematch.UECodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.JTableInfoForCMFile;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.JTableInfoForCMFolder;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.JTableInfoForCMMultipleFile;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMFile;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMFolder;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMMultipleFile;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.JTableMatchedInfoForSM;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.TableModelForSMFile;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.table.TableModelForSMFolder;
import com.sec.ose.osi.util.tools.Tools;

/**
 * JPanIdentifyResetComment
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanIdentifyResetComment extends JPanel {
	private static Log log = LogFactory.getLog(JPanIdentifyResetComment.class);
	
	private JScrollPane jScrollPaneComment = null;
	private JTextArea txtIdentificationComment = null;
	
	private JButton btnIdentificationOK = null;
	private JButton jButtonReset = null;
	
	private JPanel jPanOKCancelBtn=null; 
	
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanIdentifyResetComment() {
		
		initialize();
	}

	private void initialize() {
		
		GridBagConstraints gridBagConstraintsPanelComment = new GridBagConstraints();
		gridBagConstraintsPanelComment.fill = GridBagConstraints.BOTH;
		gridBagConstraintsPanelComment.gridx = 0;
		gridBagConstraintsPanelComment.gridy = 0;
		gridBagConstraintsPanelComment.weightx = 1.0;
		gridBagConstraintsPanelComment.weighty = 1.0;

		GridBagConstraints gridBagConstraintsOKResetBtn = new GridBagConstraints();
		gridBagConstraintsOKResetBtn.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraintsOKResetBtn.insets = new Insets(10, 10, 0, 10);
		gridBagConstraintsOKResetBtn.gridx = 1;
		gridBagConstraintsOKResetBtn.gridy = 0;

		this.setLayout(new GridBagLayout());
		this.add(getJScrollPaneComment(), gridBagConstraintsPanelComment);
		this.add(getJPanelOkResetBtn(), gridBagConstraintsOKResetBtn);
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (btnIdentificationOK == null) {
			btnIdentificationOK = new JButton();
			btnIdentificationOK.setText("OK");
			btnIdentificationOK.setPreferredSize(new Dimension(80, 28));
			btnIdentificationOK.setFocusPainted(false);
			btnIdentificationOK.setActionCommand(ButtonActions.OK_BUTTON.name());
			btnIdentificationOK.addActionListener(new JPanIdentificationButtonAction());
		}
		return btnIdentificationOK;
	}
	
	
	
	private Component getJPanelOkResetBtn() {
		if(jPanOKCancelBtn == null) {
			
			jPanOKCancelBtn = new JPanel();
			
			GridBagConstraints gridBagConstraintsOKBtn = new GridBagConstraints();
			gridBagConstraintsOKBtn.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsOKBtn.insets = new Insets(0, 0, 10, 0);
			gridBagConstraintsOKBtn.gridx = 0;
			gridBagConstraintsOKBtn.gridy = 0;
			
			GridBagConstraints gridBagConstraintsResetBtn = new GridBagConstraints();
			gridBagConstraintsResetBtn.gridx = 0;
			gridBagConstraintsResetBtn.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraintsResetBtn.insets = new Insets(0, 0, 10, 0);
			gridBagConstraintsResetBtn.gridy = 1;

			jPanOKCancelBtn.setLayout(new GridBagLayout());
			jPanOKCancelBtn.add(getJButtonOK(), gridBagConstraintsOKBtn);
			jPanOKCancelBtn.add(getJButtonReset(), gridBagConstraintsResetBtn);
		}
		return jPanOKCancelBtn;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneComment() {
		if (jScrollPaneComment == null) {
			jScrollPaneComment = new JScrollPane();
			jScrollPaneComment.setBorder(
					BorderFactory.createTitledBorder(null, 
					"File Comment", 
					TitledBorder.DEFAULT_JUSTIFICATION, 
					TitledBorder.DEFAULT_POSITION,
					new Font("Dialog", Font.BOLD, 12), 
					new Color(51, 51, 51)));
			jScrollPaneComment.setViewportView(getJTextAreaComment());
		}
		return jScrollPaneComment;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	JTextArea getJTextAreaComment() {
		if (txtIdentificationComment == null) {
			txtIdentificationComment = new JTextArea();
		}
		return txtIdentificationComment;
	}

	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReset() {
		if (jButtonReset == null) {
			jButtonReset = new JButton();
			jButtonReset.setText("Reset");
			jButtonReset.setPreferredSize(new Dimension(80, 28));
			jButtonReset.setFocusPainted(false);
			jButtonReset.setToolTipText("");
			jButtonReset.setActionCommand(ButtonActions.RESET_BUTTON.name());
			jButtonReset.addActionListener(new JPanIdentificationButtonAction());
		}
		return jButtonReset;
	}
	
	public void setResetButtonToolTip(int type) {
		if(type == IdentificationConstantValue.STRING_MATCH_TYPE) {
			jButtonReset.setToolTipText("This function is not available since protex SDK 6.2.2.");
		} else {
			jButtonReset.setToolTipText("");
		}
	}
	
	public void setOKResetButtonEnable(boolean enable) {
		getJButtonOK().setEnabled(enable);
		getJButtonReset().setEnabled(enable);
	
	}
	
	synchronized public void updateUIOKResetButtonForSMCM(
			JTableMatchedInfo tableMatchedInfo,
			int selectedRow,
			int matchType,
			SelectedFilePathInfo selectedFilePathInfo) {
		
		switch(matchType) {
			case IdentificationConstantValue.STRING_MATCH_TYPE:
			{
				JTableMatchedInfoForSM stringMatchedInfoForSM = (JTableMatchedInfoForSM) tableMatchedInfo;
				if(stringMatchedInfoForSM.getRowCount() <= 0) {
					getJButtonOK().setEnabled(false);
					getJButtonReset().setEnabled(false);
					break;
				}
				if(selectedFilePathInfo.getPathType() == SelectedFilePathInfo.SINGLE_FILE_TYPE ||
				   selectedFilePathInfo.getPathType() == SelectedFilePathInfo.MULTIPLE_FILE_TYPE ) {
					String status = String.valueOf(stringMatchedInfoForSM.getValueAt(selectedRow, TableModelForSMFile.COL_STATUS));
					if(status.equals("Pending")) {
						getJButtonOK().setEnabled(true);
						getJButtonReset().setEnabled(false);
					} else if(status.equals("Identified")) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false); 
					} else {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false);
					}
					
				} else {
					int pendingHits = Tools.transStringToInteger(String.valueOf(stringMatchedInfoForSM.getValueAt(selectedRow, TableModelForSMFolder.COL_PENDING_HITS)));
					int identifiedHits = Tools.transStringToInteger(String.valueOf(stringMatchedInfoForSM.getValueAt(selectedRow, TableModelForSMFolder.COL_IDENTIFIED_HITS)));
					
					getJButtonOK().setEnabled(false);
					getJButtonReset().setEnabled(false);
					
					if(pendingHits > 0) {
						getJButtonOK().setEnabled(true);
					}
					if(identifiedHits > 0) {
						getJButtonReset().setEnabled(false); 
					}
				}
			}
			break;
			
			case IdentificationConstantValue.CODE_MATCH_TYPE:
			{
				if(selectedFilePathInfo.getPathType() == SelectedFilePathInfo.SINGLE_FILE_TYPE) {
					JTableInfoForCMFile jTableInfoForCMFile = (JTableInfoForCMFile) tableMatchedInfo;
					if(jTableInfoForCMFile.getRowCount()<=0) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false);
						break;
					}
					try {
						if(jTableInfoForCMFile.getValueAt(selectedRow, TableModelForCMFile.COL_STATUS) == null)
							break;
					} catch(java.lang.IndexOutOfBoundsException e) {
						break;
					}
					
					String status = String.valueOf(jTableInfoForCMFile.getValueAt(selectedRow, TableModelForCMFile.COL_STATUS));
					if(status.equals("Pending")) {
						getJButtonOK().setEnabled(true);
						getJButtonReset().setEnabled(false);
					} else if(status.equals("Identified") || status.equals("Declared")) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(true);
					} else {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false);
					}
					
				} else if(selectedFilePathInfo.getPathType() == SelectedFilePathInfo.MULTIPLE_FILE_TYPE) {
					JTableInfoForCMMultipleFile jTableInfoForCMMultipleFile = (JTableInfoForCMMultipleFile) tableMatchedInfo;
					if(jTableInfoForCMMultipleFile.getRowCount()<=0) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false);
						break;
					}
					getJButtonOK().setEnabled(false);
					getJButtonReset().setEnabled(false);
					
					if(selectedRow == -1) {
						break;
					}
					
					int pendingHits = Tools.transStringToInteger(String.valueOf(jTableInfoForCMMultipleFile.getValueAt(selectedRow, TableModelForCMMultipleFile.COL_PENDING)));
					int identifiedHits = Tools.transStringToInteger(String.valueOf(jTableInfoForCMMultipleFile.getValueAt(selectedRow, TableModelForCMMultipleFile.COL_IDENTIFIED)));
					String status = String.valueOf(jTableInfoForCMMultipleFile.getValueAt(selectedRow, TableModelForCMMultipleFile.COL_STATUS));
					if(pendingHits > 0) {
						getJButtonOK().setEnabled(true);
					}
					if(identifiedHits > 0) {
						getJButtonReset().setEnabled(true);
					}
					if(status.equals("Declared")) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(true);
					}
				} else {
					JTableInfoForCMFolder jTableInfoForCMFolder = (JTableInfoForCMFolder) tableMatchedInfo;
					if(jTableInfoForCMFolder.getRowCount()<=0) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(false);
						break;
					}
					
					getJButtonOK().setEnabled(false);
					getJButtonReset().setEnabled(false);
					
					if(selectedRow == -1) {
						break;
					}
					
					int pendingHits = Tools.transStringToInteger(String.valueOf(jTableInfoForCMFolder.getValueAt(selectedRow, TableModelForCMFolder.COL_PENDING)));
					int identifiedHits = Tools.transStringToInteger(String.valueOf(jTableInfoForCMFolder.getValueAt(selectedRow, TableModelForCMFolder.COL_IDENTIFIED)));
					String status = String.valueOf(jTableInfoForCMFolder.getValueAt(selectedRow, TableModelForCMFolder.COL_STATUS));
					
					if(pendingHits > 0) {
						getJButtonOK().setEnabled(true);
					}
					if(identifiedHits > 0) {
						getJButtonReset().setEnabled(true);
					}
					if(status.equals("Declared")) {
						getJButtonOK().setEnabled(false);
						getJButtonReset().setEnabled(true);
					}
				}
			}
			break;
		}
		
	}
	
	private static final long serialVersionUID = 1L;

	private enum ButtonActions {
		OK_BUTTON,
		RESET_BUTTON,

	}
	
	class JPanIdentificationButtonAction implements ActionListener {
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			
			log.debug("JPanIdentification Button Action...");
			
			String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
			String comment = txtIdentificationComment.getText();
			if(comment.length() <= 0){
				JOptionPane.showMessageDialog(
						null, 
						"Comment area must be completed.", 
						"Pending identification", 
						JOptionPane.ERROR_MESSAGE
						 );
			}
			UIEntity matchedInfo = null;
			
			int matchType = IdentifyMediator.getInstance().getSelectedMatchType();
			switch(matchType) { 
				case IdentificationConstantValue.STRING_MATCH_TYPE:
					matchedInfo = IdentifyMediator.getInstance().getJPanStringMatch().exportUIEntity(projectName);
					break;
					
				case IdentificationConstantValue.CODE_MATCH_TYPE:
					matchedInfo = IdentifyMediator.getInstance().getJPanCodeMatch().exportUIEntity(projectName);
					
					UECodeMatch ue = (UECodeMatch)matchedInfo;
					if(ue == null) {
						return;
					}
					String status = ue.getStatus();
					if(status.equals("Declared")) {
						matchType = IdentificationConstantValue.PATTERN_MATCH_TYPE;
						matchedInfo = IdentifyMediator.getInstance().getJPanPatternMatch().exportUIEntity(projectName);
					}
					break;
				case IdentificationConstantValue.PATTERN_MATCH_TYPE:
					matchedInfo = IdentifyMediator.getInstance().getJPanPatternMatch().exportUIEntity(projectName);
					break;
			}
			
			if(matchedInfo == null)
				return;
			
			UEIdentifyResetComment ue = new UEIdentifyResetComment(
					projectName, 
					IdentifyMediator.getInstance().getSelectedFilePathInfo(), 
					matchType, 
					matchedInfo,
					comment);
	
			
			if (e.getActionCommand().equals(ButtonActions.OK_BUTTON.name())) {
				
				log.debug("OK Button Clicked !!");
				log.debug("#####################OK Clicked: "+ue.toString());
				UserRequestHandler.getInstance().handle(UserRequestHandler.PROCESS_IDENTIFY, ue, true, false);
				
			} else if (e.getActionCommand().equals(ButtonActions.RESET_BUTTON.name())) {
				log.debug("#####################RESET Clicked: "+ue.toString());

				UserRequestHandler.getInstance().handle(UserRequestHandler.PROCESS_RESET, ue, true, false);
				
			} 
			
		}
	}
	
	public void setOKResetButtonsForPatternMatch(boolean okButton, boolean resetButton) {
		getJButtonOK().setEnabled(okButton);
		getJButtonReset().setEnabled(resetButton);
		
	}
}
