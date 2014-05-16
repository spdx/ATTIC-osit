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
package com.sec.ose.osi.ui.frm.main.identification.stringmatch.table;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.FolderSummary;
import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboComponentName;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboLicenseName;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.JPanStringMatchMain;

/**
 * JPanSMTableArea
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanSMTableArea extends JPanel {
	private static Log log = LogFactory.getLog(JPanSMTableArea.class);

	private static final long serialVersionUID = 1L;

	private CardLayout layout = null;

	private JScrollPane jScrollPaneForFileTable = null;
	private JScrollPane jScrollPaneForFolderTable = null;
	private JScrollPane jScrollPaneForMultipleFileTable = null;
	
	private static final String FILE_TABLE_LAYER = "File Table";
	private static final String FOLDER_TABLE_LAYER = "Folder Table";
	private static final String MULTIPLE_FILE_TABLE_LAYER = "MultipleFile Table";
	
	private JTableMatchedInfoForSM jTableMatchedInfoForSM = null;

	private JTableInfoForSMFile jTableInfoForSMFile = null;
	private JTableInfoForSMFolder jTableInfoForSMFolder = null;
	private JTableInfoForSMMultipleFile jTableInfoForSMMultipleFile = null;

	private JPanStringMatchMain jPanStringMatchMain = null;
		
	public JPanSMTableArea(JPanStringMatchMain jPanStringMatchMain) {
		super();
		this.jPanStringMatchMain = jPanStringMatchMain;
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
			jScrollPaneForFileTable.setViewportView(getJTableInfoForSMFile());
		}
		return jScrollPaneForFileTable;
	}
	
	private JScrollPane getJScrollPaneFolderTable() {
		if (jScrollPaneForFolderTable == null) {
			jScrollPaneForFolderTable = new JScrollPane();
			jScrollPaneForFolderTable.setPreferredSize(new Dimension(3, 3));
			jScrollPaneForFolderTable.setBorder(null);
			jScrollPaneForFolderTable.setViewportView(getJTableInfoForSMFolder());
		}
		return jScrollPaneForFolderTable;
	}
	
	private JScrollPane getJScrollPaneMultipleFileTable() {
		if (jScrollPaneForMultipleFileTable == null) {
			jScrollPaneForMultipleFileTable = new JScrollPane();
			jScrollPaneForMultipleFileTable.setPreferredSize(new Dimension(3, 3));
			jScrollPaneForMultipleFileTable.setBorder(null);
			jScrollPaneForMultipleFileTable.setViewportView(getJTableInfoForSMMultipleFile());
		}
		return jScrollPaneForMultipleFileTable;
	}
	
	protected JTableInfoForSMFile getJTableInfoForSMFile() {
		if (jTableInfoForSMFile == null) {
			jTableInfoForSMFile = new JTableInfoForSMFile();
		}
		jTableInfoForSMFile.addMouseListener(new StringSearchTableClickAction());
		return jTableInfoForSMFile;
	}
	
	protected JTableInfoForSMFolder getJTableInfoForSMFolder() {
		if (jTableInfoForSMFolder == null) {
			jTableInfoForSMFolder = new JTableInfoForSMFolder();
		}
		jTableInfoForSMFolder.addMouseListener(new StringSearchTableClickAction());
		return jTableInfoForSMFolder;
	}
	
	protected JTableInfoForSMMultipleFile getJTableInfoForSMMultipleFile() {
		if (jTableInfoForSMMultipleFile == null) {
			jTableInfoForSMMultipleFile = new JTableInfoForSMMultipleFile();
		}
		jTableInfoForSMMultipleFile.addMouseListener(new StringSearchTableClickAction());
		return jTableInfoForSMMultipleFile;
	}
	
	public void changeTableInfo(
			String pProjectName, 
			ArrayList<String> selectedPaths, 
			SelectedFilePathInfo selectedPathsInfo,
			int pathType) {
		
		FileSummary fileSummary = null;
		MultipleFileSummary multipleFileSummary = null; 
		FolderSummary folderSummary = null;
		int matchType = IdentificationConstantValue.STRING_MATCH_TYPE;
		String firstSelectedFilePathOfList = selectedPaths.get(0);
		String strLicenseName = ""; 
		String strComponentName = "";
		
		switch(pathType) {
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
				log.debug("SINGLE_FILE_TYPE");
				jTableMatchedInfoForSM = (JTableMatchedInfoForSM) jTableInfoForSMFile;
				layout.show(this, FILE_TABLE_LAYER);
	
				fileSummary = MatchedInfoMgr.getInstance().getFileSummary(matchType, pProjectName, firstSelectedFilePathOfList);
				jTableMatchedInfoForSM.refresh(fileSummary);
				
				if(fileSummary.getStringMatchInfoListSize() > 0) {
					jPanStringMatchMain.viewSourceCode(firstSelectedFilePathOfList, fileSummary.getMatchLines(pProjectName));
				} else {
					jPanStringMatchMain.viewSourceCode(firstSelectedFilePathOfList, null);
				}
				break;
	
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				log.debug("MULTIPLE_FILE_TYPE");
				jTableMatchedInfoForSM = (JTableMatchedInfoForSM) jTableInfoForSMMultipleFile;
				layout.show(this, MULTIPLE_FILE_TABLE_LAYER);
	
				multipleFileSummary = MatchedInfoMgr.getInstance().getMultipleFileSummary(matchType, pProjectName, selectedPaths);
				jTableMatchedInfoForSM.refresh(multipleFileSummary);
				
				jPanStringMatchMain.viewSourceCode(firstSelectedFilePathOfList, null);
				break;
	
			case SelectedFilePathInfo.FOLDER_TYPE:
			case SelectedFilePathInfo.PROJECT_TYPE:
				log.debug("FOLDER_TYPE or PROJECT_TYPE");
				jTableMatchedInfoForSM = (JTableMatchedInfoForSM) jTableInfoForSMFolder;
				layout.show(this, FOLDER_TABLE_LAYER);
	
				folderSummary = MatchedInfoMgr.getInstance().getFolderSummary(matchType, pProjectName, firstSelectedFilePathOfList);
				jTableMatchedInfoForSM.refresh(folderSummary);

				jPanStringMatchMain.viewSourceCode(firstSelectedFilePathOfList, null);
				break;
		}
		
		JComboComponentName jComboComponentNameForOpt1 = jPanStringMatchMain.getJComboBoxComponent();
		JComboLicenseName jComboLicenseNameForOpt1 = jPanStringMatchMain.getJComboLicenseNameForOpt1();
		JComboLicenseName jComboLicenseNameForOpt2 = jPanStringMatchMain.getJComboLicenseNameForOpt2();
		
		jComboComponentNameForOpt1.initComponentComboBox();
		jComboLicenseNameForOpt1.initLicenseComboBox();
		jComboLicenseNameForOpt2.initLicenseComboBox();
		
		if((pathType == SelectedFilePathInfo.SINGLE_FILE_TYPE && fileSummary.getStringMatchInfoListSize()==0) ||
		   (pathType == SelectedFilePathInfo.MULTIPLE_FILE_TYPE && multipleFileSummary.getStringMatchInforForMultipleFileSummarySize()==0) ||
		   ((pathType == SelectedFilePathInfo.FOLDER_TYPE || pathType == SelectedFilePathInfo.PROJECT_TYPE)&& folderSummary.getStringMatchInforForFolderListSize()==0)) {
			log.debug("no string match for folder or file");
			IdentifyMediator.getInstance().setOKResetButtonEnable(false);
			jPanStringMatchMain.viewSourceCode(firstSelectedFilePathOfList, null);
			return;
		} 
		
		strLicenseName = jTableMatchedInfoForSM.getSelectedLicenseName(); 
		strComponentName = jTableMatchedInfoForSM.getSelectedComponentName();
		
		if(strComponentName != null && strLicenseName != null) {
			jComboComponentNameForOpt1.setComponentName(strComponentName);
			jComboLicenseNameForOpt1.setLicenseComboBox(strLicenseName);
		}
		
		int selectedRow = jTableMatchedInfoForSM.getSelectedRow();
		IdentifyMediator.getInstance().updateUIOKResetButtonForSMCM(jTableMatchedInfoForSM, selectedRow);
			
	}

	public JTableMatchedInfoForSM getSelectedTable() {
		return jTableMatchedInfoForSM;
	}
	
	class StringSearchTableClickAction extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			setComponentLicense();
			int gSelectedStringSearchIndex = jPanStringMatchMain.getgSelectedStringSearchIndex();
			int lSelectedStringSearchIndex = jTableMatchedInfoForSM.getSelectedRow();
			if(gSelectedStringSearchIndex != lSelectedStringSearchIndex) {

				jPanStringMatchMain.updateSourceCodeView();
				gSelectedStringSearchIndex = lSelectedStringSearchIndex; 
			}
			
			int selectedRow = jTableMatchedInfoForSM.getSelectedRow();
			IdentifyMediator.getInstance().updateUIOKResetButtonForSMCM(jTableMatchedInfoForSM, selectedRow);
		}
		
		public void setComponentLicense() {

			String strLicenseName = (String)jTableMatchedInfoForSM.getValueAt(jTableMatchedInfoForSM.getSelectedRow(),3);
			String strComponentName = (String)jTableMatchedInfoForSM.getValueAt(jTableMatchedInfoForSM.getSelectedRow(),1);
			
			log.debug("StringSearchTableClickAction : strComponentName : " + strComponentName);
			
			JComboComponentName jComboComponentNameForOpt1 = jPanStringMatchMain.getJComboBoxComponent();
			JComboLicenseName jComboLicenseNameForOpt1 = jPanStringMatchMain.getJComboLicenseNameForOpt1();
			
			if(strComponentName != null && strLicenseName != null) {
				jComboComponentNameForOpt1.setComponentName(strComponentName);
				jComboLicenseNameForOpt1.setLicenseComboBox(strLicenseName);
			}
		}
	}
}
