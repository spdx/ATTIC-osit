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
package com.sec.ose.osi.ui.frm.main.identification.codematch.table;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.FolderSummary;
import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.codematch.JPanCodeMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;

/**
 * JPanCMTableArea
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JPanCMTableArea extends JPanel {
	private static Log log = LogFactory.getLog(JPanCMTableArea.class);

	private static final long serialVersionUID = 1L;

	private CardLayout layout = null;
	
	private JScrollPane jScrollPaneForFileTable = null;
	private JScrollPane jScrollPaneForFolderTable = null;
	private JScrollPane jScrollPaneForMultipleFileTable = null;
	
	private static final String FILE_TABLE_LAYER = "File Table";
	private static final String FOLDER_TABLE_LAYER = "Folder Table";
	private static final String MULTIPLE_FILE_TABLE_LAYER = "MultipleFile Table";
	
	private JTableMatchedInfo jTableMatchedInfo = null;
	
	private JTableInfoForCMFile jTableInfoForCMFile = null;
	private JTableInfoForCMFolder jTableInfoForCMFolder = null;
	private JTableInfoForCMMultipleFile jTableInfoForCMMultipleFile = null;
	
	private JPanCodeMatchMain jPanCodeMatchMain = null;

	public JPanCMTableArea(JPanCodeMatchMain jPanCodeMatchMain) {
		super();
		this.jPanCodeMatchMain = jPanCodeMatchMain;
		initialize();
	}

	private void initialize() {
		layout = new CardLayout();
		this.setLayout(layout);
		this.add(getJScrollPaneFileTable(), FILE_TABLE_LAYER);
		this.add(getJScrollPaneFolderTable(), FOLDER_TABLE_LAYER);
		this.add(getJScrollPaneMultipleFileTable(), MULTIPLE_FILE_TABLE_LAYER);
	}

	private JScrollPane getJScrollPaneFileTable() {
		if (jScrollPaneForFileTable == null) {
			jScrollPaneForFileTable = new JScrollPane();
			jScrollPaneForFileTable.setPreferredSize(new Dimension(3, 3));
			jScrollPaneForFileTable.setBorder(null);
			jScrollPaneForFileTable.setViewportView(getJTableInfoForCMFile());
		}
		return jScrollPaneForFileTable;
	}

	private JScrollPane getJScrollPaneFolderTable() {
		if (jScrollPaneForFolderTable == null) {
			jScrollPaneForFolderTable = new JScrollPane();
			jScrollPaneForFolderTable.setPreferredSize(new Dimension(3, 3));
			jScrollPaneForFolderTable.setBorder(null);
			jScrollPaneForFolderTable.setViewportView(getJTableInfoForCMFolder());
		}
		return jScrollPaneForFolderTable;
	}

	private JScrollPane getJScrollPaneMultipleFileTable() {
		if (jScrollPaneForMultipleFileTable == null) {
			jScrollPaneForMultipleFileTable = new JScrollPane();
			jScrollPaneForMultipleFileTable.setPreferredSize(new Dimension(3, 3));
			jScrollPaneForMultipleFileTable.setBorder(null);
			jScrollPaneForMultipleFileTable.setViewportView(getJTableInfoForCMMultipleFile());
		}
		return jScrollPaneForMultipleFileTable;
	}

	protected JTableInfoForCMFile getJTableInfoForCMFile() {
		if (jTableInfoForCMFile == null) {
			jTableInfoForCMFile = new JTableInfoForCMFile();
			jTableInfoForCMFile.addKeyListener( 
					new KeyAdapter() {
						public void keyReleased(java.awt.event.KeyEvent e) {
							keyReleasedAction(jTableInfoForCMFile);
						}
					}
			);

			jTableInfoForCMFile.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mouseClickedAction(jTableInfoForCMFile, e);
						}
					}
			);
		}
		return jTableInfoForCMFile;
	}

	protected JTableInfoForCMFolder getJTableInfoForCMFolder() {
		if (jTableInfoForCMFolder == null) {
			jTableInfoForCMFolder = new JTableInfoForCMFolder();
			jTableInfoForCMFolder.addKeyListener( 
					new KeyAdapter() {
						public void keyReleased(java.awt.event.KeyEvent e) {
							keyReleasedAction(jTableInfoForCMFolder);
						}
					}
			);

			jTableInfoForCMFolder.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mouseClickedAction(jTableInfoForCMFolder, e);
						}
					}
			);
		}
		return jTableInfoForCMFolder;
	}

	protected JTableInfoForCMMultipleFile getJTableInfoForCMMultipleFile() {
		if (jTableInfoForCMMultipleFile == null) {
			jTableInfoForCMMultipleFile = new JTableInfoForCMMultipleFile();
			jTableInfoForCMMultipleFile.addKeyListener( 
				new KeyAdapter() {
					public void keyReleased(java.awt.event.KeyEvent e) {
						keyReleasedAction(jTableInfoForCMMultipleFile);
					}
				}
			);
				
			jTableInfoForCMMultipleFile.addMouseListener(
					new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							mouseClickedAction(jTableInfoForCMMultipleFile, e);
						}
					}
			);
		}
		return jTableInfoForCMMultipleFile;
	}

	private void keyReleasedAction(JTableMatchedInfo jTableMatchedInfo) {
		
		jPanCodeMatchMain.resetNavigator();
		jPanCodeMatchMain.resetSelectSnippetNum();
		JRadioButton rdbtnOpt1IConform = jPanCodeMatchMain.getJRadioButtonOpt1();
		int prevSelectedCodeMatchTableIndex = jPanCodeMatchMain.getPrevSelectedCodeMatchTableIndex();

		int selectedRow = jTableMatchedInfo.getSelectedRow();
		
		IdentifyMediator.getInstance().updateUIOKResetButtonForSMCM(jTableMatchedInfo, selectedRow);

		if(rdbtnOpt1IConform.isSelected()) {
			String selectedLicenseName = (String)jTableMatchedInfo.getValueAt(selectedRow,2);
			selectedLicenseName = DCCodeMatch.getOriginValue(selectedLicenseName);
			IdentifyMediator.getInstance().setSelectedLicenseName(selectedLicenseName);
			log.debug("selectedLicenseName by keyboard : " + selectedLicenseName);
		}

		int lSelectedCodeMatchTableIndex = jTableMatchedInfo.getSelectedRow();
		if(prevSelectedCodeMatchTableIndex != lSelectedCodeMatchTableIndex) {
			if(jTableMatchedInfo.getSelectedRow() >= 0 && jTableMatchedInfo.getModel().getColumnCount() > 5) {
				jPanCodeMatchMain.updateSourceCodeView();
			}
			prevSelectedCodeMatchTableIndex = lSelectedCodeMatchTableIndex; 
		}
		return;
	}

	private void mouseClickedAction(JTableMatchedInfo jTableMatchedInfo, MouseEvent e) {
		
		jPanCodeMatchMain.resetNavigator();
		jPanCodeMatchMain.resetSelectSnippetNum();
		JRadioButton rdbtnOpt1IConform = jPanCodeMatchMain.getJRadioButtonOpt1();
		int prevSelectedCodeMatchTableIndex = jPanCodeMatchMain.getPrevSelectedCodeMatchTableIndex();

		int selectedRow = jTableMatchedInfo.getSelectedRow();
		
		IdentifyMediator.getInstance().updateUIOKResetButtonForSMCM(jTableMatchedInfo, selectedRow);

		if(e.getClickCount() != IdentificationConstantValue.MOUSE_DOUBLE_CLICK) {

			if(rdbtnOpt1IConform.isSelected()) {
				String selectedLicenseName = (String)jTableMatchedInfo.getValueAt(selectedRow,2);
				selectedLicenseName = DCCodeMatch.getOriginValue(selectedLicenseName);
				IdentifyMediator.getInstance().setSelectedLicenseName(selectedLicenseName);
				log.debug("selectedLicenseName by mouse selection : " + selectedLicenseName);
			}

			int lSelectedCodeMatchTableIndex = jTableMatchedInfo.getSelectedRow();
			if(prevSelectedCodeMatchTableIndex != lSelectedCodeMatchTableIndex) {
				if(jTableMatchedInfo.getSelectedRow() >= 0 && jTableMatchedInfo.getModel().getColumnCount() > 5) {
					jPanCodeMatchMain.updateSourceCodeView();
				}
				prevSelectedCodeMatchTableIndex = lSelectedCodeMatchTableIndex; 
			}
			return;
			
		}
	}

	public void changeTableInfo(
			String pProjectName, 
			ArrayList<String> selectedPaths, 
			SelectedFilePathInfo selectedPathsInfo,
			int pathType) {
		
		FileSummary fileSummary = null;
		MultipleFileSummary multipleFileSummary = null; 
		FolderSummary folderSummary = null;
		int matchType = IdentificationConstantValue.CODE_MATCH_TYPE;
		
		switch(pathType) {
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
				log.debug("SINGLE_FILE_TYPE");
				jTableMatchedInfo = (JTableMatchedInfo) jTableInfoForCMFile;
				layout.show(this, FILE_TABLE_LAYER);
	
				fileSummary = MatchedInfoMgr.getInstance().getFileSummary(matchType, pProjectName, selectedPaths.get(0));
				jTableMatchedInfo.refresh(fileSummary);
				break;
	
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				log.debug("MULTIPLE_FILE_TYPE");
				jTableMatchedInfo = (JTableMatchedInfo) jTableInfoForCMMultipleFile;
				layout.show(this, MULTIPLE_FILE_TABLE_LAYER);
	
				multipleFileSummary = MatchedInfoMgr.getInstance().getMultipleFileSummary(matchType, pProjectName, selectedPaths);
				jTableMatchedInfo.refresh(multipleFileSummary);
				break;
	
			case SelectedFilePathInfo.FOLDER_TYPE:
			case SelectedFilePathInfo.PROJECT_TYPE:
				log.debug("FOLDER_TYPE or PROJECT_TYPE");
				jTableMatchedInfo = (JTableMatchedInfo) jTableInfoForCMFolder;
				layout.show(this, FOLDER_TABLE_LAYER);
	
				folderSummary = MatchedInfoMgr.getInstance().getFolderSummary(matchType, pProjectName, selectedPaths.get(0));
				jTableMatchedInfo.refresh(folderSummary);
				break;
		}
		
		int selectedRow = jTableMatchedInfo.getSelectedRow();
		IdentifyMediator.getInstance().updateUIOKResetButtonForSMCM(jTableMatchedInfo, selectedRow);
	}

	public JTableMatchedInfo getSelectedTable() {
		return jTableMatchedInfo;
	}
}
