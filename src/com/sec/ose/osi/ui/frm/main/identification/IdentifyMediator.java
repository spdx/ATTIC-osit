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

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.thread.job.identify.data.IdentifyData;
import com.sec.ose.osi.thread.job.identify.data.IdentifyDataFactory;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.JMenuMain;
import com.sec.ose.osi.ui.frm.main.identification.codematch.JPanCodeMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.codematch.UECodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.patternmatch.JPanPatternMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.patternmatch.UEPatternMatch;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.JPanStringMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.UEStringMatch;

/**
 * IdentifyMediator
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class IdentifyMediator {
	private static Log log = LogFactory.getLog(IdentifyMediator.class);
	
	public static final String STR_PROJECT = "PROJECT";
	public static final String STR_FOLDER = "FOLDER";
	public static final String STR_FILE = "FILE";
	
	private JPanStringMatchMain jPanStringMatch;
	private JPanCodeMatchMain jPanCodeMatch;
	private JPanPatternMatchMain jPanPatternMatch;
	
	private JPanMatchTypeSelection jPanPendingTypeSelection;
	private JComboProjectName jComboProjectName;
	private JListMatchedFiles jListMatchedFiles;
	private JTabbedPane jTabbedPaneFileNavigation;
	private JTreeAllFiles jTreeAllFiles;
	private JPanIdentifyResetComment jPanIdentifyResetComment;
	
	private SelectedFilePathInfo selectedFilePathInfo = new SelectedFilePathInfo(); 
	
	private volatile static IdentifyMediator instance = null;
	private String selectedLicenseName = "";

	public static IdentifyMediator getInstance() {
		if(instance == null) {
			synchronized(IdentifyMediator.class) {
				if(instance == null) {
					instance = new IdentifyMediator();
				}
			}
		}
		return instance;
	}

	private IdentifyMediator(){}

	public void setjTabbedPaneFileNavigation(JTabbedPane jTabbedPaneFileNavigation) {
		this.jTabbedPaneFileNavigation = jTabbedPaneFileNavigation;
	}

	public SelectedFilePathInfo getSelectedFilePathInfo() {
		
		return selectedFilePathInfo;
	}

	public void setjListMatchedFiles(JListMatchedFiles jListMatchedFiles) {
		this.jListMatchedFiles = jListMatchedFiles;
	}

	public JListMatchedFiles getJListMatchedFile() {
		return jListMatchedFiles;
	}

	public void setjTreeAllFiles(JTreeAllFiles jTreeAllFiles) {
		this.jTreeAllFiles = jTreeAllFiles;
	}

	public void refreshJListMatchedFiles(ArrayList<String> pendingFilePathList,
			ArrayList<String> identifiedFilePathList) {
		
		this.jListMatchedFiles.setIdentificationList(
				pendingFilePathList,
				identifiedFilePathList);
	}

	public void setSelectedFile(int pathType, String filePath) {
		selectedFilePathInfo.setSelectedFilePathInfo(pathType, filePath);
		jPanIdentifyMain.setPathTextInfo(getSelectedProjectName(), getSelectedFilePathInfo());
	}

	public void setSelectedFiles(int pathType, Collection<String> filePath) {
		if(filePath == null || filePath.size() < 1 )
			return;
		
		selectedFilePathInfo.setSelectedFilePathInfo(pathType, filePath);
		jPanIdentifyMain.setPathTextInfo(getSelectedProjectName(), getSelectedFilePathInfo());
	}
	
	public JPanStringMatchMain getJPanStringMatch() {
		return jPanStringMatch;
	}

	public void setPnStringSearch(JPanStringMatchMain pnStringSearch) {
		this.jPanStringMatch = pnStringSearch;
	}

	public void setJComboProjectName(JComboProjectName jComboProjectName) {
		this.jComboProjectName = jComboProjectName;
	}
	
	public void setJPendingTypeSelection(JPanMatchTypeSelection jPanPendingTypeSelection) {
		this.jPanPendingTypeSelection = jPanPendingTypeSelection;
	}

	public JPanCodeMatchMain getJPanCodeMatch() {
		return jPanCodeMatch;
	}

	public void setJPanCodeMatch(JPanCodeMatchMain pnCodeMatch) {
		this.jPanCodeMatch = pnCodeMatch;
	}

	public JPanPatternMatchMain getJPanPatternMatch() {
		return jPanPatternMatch;
	}

	public void setJPanPatternMatch(JPanPatternMatchMain pnPatternMatch) {
		this.jPanPatternMatch = pnPatternMatch;
	}
	
	// panel
	private JPanIdentifyMain jPanIdentifyMain = null;

	
	public JPanIdentifyMain getjPanPendingIdentification() {
		return jPanIdentifyMain;
	}

	public void setjPanPendingIdentification(
			JPanIdentifyMain jPanPendingIdentification) {
		jPanIdentifyMain = jPanPendingIdentification;
	}

	public void refreshComboProjectName() {
		if(jComboProjectName == null) {
			return;
		}
		jComboProjectName.refreshProjectComboBox();
	}

	public void refreshComboProjectName(String projectName) {
		this.jComboProjectName.refreshComboBox(projectName);
		
	}

	public IdentifyData createIdentifyData(
			String projectName,
			int matchedType,
			int compositeType, 
			String filePath,
			UIEntity ueMatched,
			String comment) {
		
		IdentifyData identifiedData = null;
			
		ArrayList<String> filePathList = new ArrayList<String>();
		filePathList.add(filePath);
		
		if(ueMatched instanceof UEStringMatch) {
			UEStringMatch xUEStringMatch = (UEStringMatch) ueMatched;
			if(xUEStringMatch==null)
				return null;
			identifiedData = IdentifyDataFactory.createStringMatchIdentify(
					projectName,
					compositeType, 
					filePathList, 
					xUEStringMatch, 
					comment);
			
		} else if(ueMatched instanceof UECodeMatch) {
			UECodeMatch xUECodeMatch = (UECodeMatch) ueMatched;
			if(xUECodeMatch== null)
				return null;
			identifiedData = IdentifyDataFactory.createCodeMatchIdentify(
					projectName,
					compositeType, 
					filePathList,
					xUECodeMatch, 
					comment);
			
		} else if(ueMatched instanceof UEPatternMatch) {
			UEPatternMatch xUEPatternMatch = (UEPatternMatch) ueMatched;
			if(xUEPatternMatch==null) 
				return null;
			identifiedData = IdentifyDataFactory.createPatternMatchIdentify(
					projectName,
					compositeType, 
					filePathList,
					xUEPatternMatch,
					comment);
		}
		
		IdentificationDBManager.writeDBComment(
				projectName,
				matchedType, 
				filePathList.get(0), 
				comment);
		
		return identifiedData;
	}

	public IdentifyData createResetData(
			String projectName,
			int compositeType, 
			String filePath,
			UIEntity ueMatched) {
		IdentifyData tmpResetData = null;
		
		ArrayList<String> filePathList = new ArrayList<String>();
		filePathList.add(filePath);
		
		if(ueMatched instanceof UEStringMatch) {
			UEStringMatch xUEStringMatch = (UEStringMatch) ueMatched;
			tmpResetData = IdentifyDataFactory.createStringMatchReset(projectName, compositeType, filePathList, xUEStringMatch);
		} else if(ueMatched instanceof UECodeMatch) {
			UECodeMatch xUECodeMatch = (UECodeMatch) ueMatched;
			tmpResetData = IdentifyDataFactory.createCodeMatchReset(projectName, compositeType, filePathList, xUECodeMatch);
		} else if(ueMatched instanceof UEPatternMatch) {
			UEPatternMatch xUEPatternMatch = (UEPatternMatch) ueMatched;
			tmpResetData = IdentifyDataFactory.createPatternMatchReset(projectName, compositeType, filePathList, xUEPatternMatch);
		}
		
		return tmpResetData;
	}

	public void changeSelectedIdentificationPanel(int selectedPendingType) {
		
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		if(jPanIdentifyMain == null) {
			return;
		}
		jPanIdentifyMain.changeSelectedIdentificationPanel(selectedPendingType);
		jPanPendingTypeSelection.setSelectedPendingType(selectedPendingType);
		jPanIdentifyResetComment.setResetButtonToolTip(selectedPendingType);
		
		if (previousSelectedPendingType != selectedPendingType) {
			IdentifyMediator.getInstance().refreshIdentificationInfoForTreeListChildFrames(projectName, null, selectedPendingType);
			previousSelectedPendingType = selectedPendingType;
		}
	}
	private int previousSelectedPendingType = IdentificationConstantValue.STRING_MATCH_TYPE;

	public void updateJPanPendingTypeSelectionButtonCount(String projectName) {
		if(jPanPendingTypeSelection == null) {
			return;
		}
		jPanPendingTypeSelection.updateButtonCount(projectName);
	}

	public String getSelectedProjectName() {
		if(jComboProjectName == null)
			return "";
		return jComboProjectName.getSelectedProjectName();
	}

	public void setSelectedProjectName(Object obj) {
		if(jComboProjectName == null) {
			jComboProjectName = new JComboProjectName();
		}
		jComboProjectName.setSelectedItem(obj);
	}

	public int getSelectedMatchType() {
		if(jPanPendingTypeSelection == null)
			return IdentificationConstantValue.STRING_MATCH_TYPE;
		return jPanPendingTypeSelection.getSelectedMatchType();
	}

	public void refreshIdentificationInfoForTreeListChildFrames(
			String selectedProjectName, 
			String selectedPath,
			int selectedMatchType) {
		
		if(IdentifyMediator.getInstance().getSelectedProjectName().length() <= 0) {
			return;
		}
		
		String projectNameFromUI = IdentifyMediator.getInstance().getSelectedProjectName();
		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		String pathFromUI = selectedPaths.getSelectedPath();
		
		int matchTypeFromUI = IdentifyMediator.getInstance().getSelectedMatchType();
		if(selectedProjectName.equals(projectNameFromUI) == false) return;
		if(selectedMatchType != matchTypeFromUI) return;
		if((selectedPath != null) && selectedPath.equals(pathFromUI) == false) return;
		
		MatchedInfoMgr.getInstance().loadIdentifiedFilesInfoToMemory(selectedProjectName);
		
		log.debug("projectName: "+selectedProjectName);
		log.debug("matchTypeFromUI: "+matchTypeFromUI);
		
		AbstractDiscoveryController controller  = ProjectDiscoveryControllerMap.getDiscoveryController(selectedProjectName, matchTypeFromUI);
		ArrayList<String> pendingFileList = controller.getPendingFileList();
		
		log.debug("### Start Identify UI Update");
		
		updateJPanPendingTypeSelectionButtonCount(selectedProjectName);

		ArrayList<String> identifiedFileList = MatchedInfoMgr.getInstance().getIdentifiedFilePathListByCurrentMatchType();

		// List Update
		JListMatchedFiles jListMatchedFile = IdentifyMediator.getInstance().getJListMatchedFile();
		if(jListMatchedFile == null) {
			return;
		}
		jListMatchedFile.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		refreshJListMatchedFiles( pendingFileList,
								  identifiedFileList);

		jListMatchedFiles.setSelectPointer(getSelectedFilePathInfo().getSelectedPath());
		
		// update tree
		IdentifyMediator.getInstance().updateJTreeAllFiles(pendingFileList, identifiedFileList);
		
		// Identification info Update
		refreshChildFrames(selectedProjectName);
		
		log.debug("### End Identify UI Update");
		
	}

	public void refreshIdentificationInfoForSnippetRefresh (
			String selectedProjectName, 
			String selectedPath,
			int selectedMatchType) {
		
		if(IdentifyMediator.getInstance().getSelectedProjectName().length() <= 0) {
			return;
		}
		
		String projectNameFromUI = IdentifyMediator.getInstance().getSelectedProjectName();
		int matchTypeFromUI = IdentifyMediator.getInstance().getSelectedMatchType();
		if(selectedProjectName.equals(projectNameFromUI) == false) return;
		if(selectedMatchType != matchTypeFromUI) return;
		
		MatchedInfoMgr.getInstance().loadIdentifiedFilesInfoToMemory(selectedProjectName);
		
		
		log.debug("projectName: "+selectedProjectName);
		log.debug("matchTypeFromUI: "+matchTypeFromUI);
		
		AbstractDiscoveryController controller  = ProjectDiscoveryControllerMap.getDiscoveryController(selectedProjectName, matchTypeFromUI);
		ArrayList<String> pendingFileList = controller.getPendingFileList();
		
		log.debug("### Start Identify UI Update");
		
		updateJPanPendingTypeSelectionButtonCount(selectedProjectName);

		ArrayList<String> identifiedFileList = MatchedInfoMgr.getInstance().getIdentifiedFilePathListByCurrentMatchType();

		// List Update
		JListMatchedFiles jListMatchedFile = IdentifyMediator.getInstance().getJListMatchedFile();
		jListMatchedFile.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		refreshJListMatchedFiles( pendingFileList,
								  identifiedFileList);

		jListMatchedFiles.setSelectPointer(getSelectedFilePathInfo().getSelectedPath());
		
		// update tree
		IdentifyMediator.getInstance().updateJTreeAllFiles(pendingFileList, identifiedFileList);
		
		// Identification info Update
		refreshChildFrames(selectedProjectName);
		
		log.debug("### End Identify UI Update");
		
	}

	public void setOpenCodeMatchSourceView(int pathType) { 
		
		jPanIdentifyMain.setOpenCodeMatchSourceView(pathType);
	}
	public void changeFileNavigationPanel(int tabIdx) {
		jTabbedPaneFileNavigation.setSelectedIndex(tabIdx);
		
	}

	public void refreshChildFrames(String projectName) {
		jPanIdentifyMain.refreshChildFrameForMatchedType(projectName);
	}

	public void setJPanIdentifyResetComment(
			JPanIdentifyResetComment jPanIdentifyResetComment) {
		this.jPanIdentifyResetComment = jPanIdentifyResetComment; 
		
	}

	public void updateUIFrameWithAdditionalIdentifiedFiles(String pProjectName, Collection<String> additionalIdentifiedFilePaths) {
		
		if(IdentifyMediator.getInstance().getSelectedProjectName().length() <= 0) {
			return;
		}
		
		for(String filePath:additionalIdentifiedFilePaths) {
			log.debug("updateTargetFiles : "+filePath);
		}
		
		int selectedPendingType = IdentifyMediator.getInstance().getSelectedMatchType();
		String projectName = pProjectName;
		log.debug("getJTabbedPane().getSelectedIndex() : "+jTabbedPaneFileNavigation.getSelectedIndex());
		
		ProjectDiscoveryControllerMap.getDiscoveryController(projectName, selectedPendingType)
			.removeIdentificationArrayListFromCache(additionalIdentifiedFilePaths);
		
		updateJPanPendingTypeSelectionButtonCount(pProjectName);

		if(selectedPendingType == IdentificationConstantValue.CODE_MATCH_TYPE) {
			ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).refreshCahce(IdentificationConstantValue.PATTERN_MATCH_TYPE);
		} else if(selectedPendingType == IdentificationConstantValue.PATTERN_MATCH_TYPE) {
			ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).refreshCahce(IdentificationConstantValue.CODE_MATCH_TYPE);
		}
		
		
		jTreeAllFiles.markPendingFileToIdentified(additionalIdentifiedFilePaths);
		jPanIdentifyMain.refreshUI();
	}

	public void resetUIFrame(ArrayList<String> additionalResetFilePathList) {
		
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		
		int selectedPendingType= getSelectedMatchType();
		ProjectDiscoveryControllerMap.getDiscoveryController(projectName, selectedPendingType)
			.addIdentificationArrayListFromCache(additionalResetFilePathList);

		if(selectedPendingType == IdentificationConstantValue.CODE_MATCH_TYPE) {
			ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.PATTERN_MATCH_TYPE).refreshCahce(IdentificationConstantValue.PATTERN_MATCH_TYPE);
		} else if(selectedPendingType == IdentificationConstantValue.PATTERN_MATCH_TYPE) {
			ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE).refreshCahce(IdentificationConstantValue.CODE_MATCH_TYPE);
		}
		
		IdentifyMediator.getInstance().updateJPanPendingTypeSelectionButtonCount(projectName);
		
		jTreeAllFiles.markIdentifiedToPending(additionalResetFilePathList);
		jPanIdentifyMain.refreshUI();
		
	}

	public void updateJTreeAllFiles(
		ArrayList<String> pendingFileList,
		ArrayList<String> identifiedFileList ) {
	jTreeAllFiles.updateTreeUI(pendingFileList, identifiedFileList);
	
	}

	public void setComment(String comment) {
		jPanIdentifyResetComment.getJTextAreaComment().setText(comment);
	}

	public void setOKResetButtonEnable(boolean enable) {
		jPanIdentifyResetComment.setOKResetButtonEnable(enable);
		
	}

	synchronized public void updateUIOKResetButtonForSMCM(JTableMatchedInfo tableMatchedInfo, int selectedRow) {
		
		jPanIdentifyResetComment.updateUIOKResetButtonForSMCM(
				tableMatchedInfo,
				selectedRow,
				getSelectedMatchType(),
				getSelectedFilePathInfo() );
		
	}

	public void setFilePointerToSelectedFile(String filePath) {
		
		int selectedTab = jTabbedPaneFileNavigation.getSelectedIndex();
		
		switch(selectedTab) {
		
			case JPanIdentifyMain.INDEX_TREE:
				jTreeAllFiles.setSelectedFile(filePath, null, null);
				break;
				
			case JPanIdentifyMain.INDEX_LIST:
				jListMatchedFiles.setSelectedFile(filePath);
				break;
			
		}
		
	}

	JMenuMain jMenuMain = null;
	public void setEnabledJMenuItemSynchFromServer() {
		if(jMenuMain == null) {
			return;
		}
		jMenuMain.getJMenuItemSyncFromServer().setEnabled(true);
		
	}

	public void setJMenuMain(JMenuMain jMenuMain) {
		this.jMenuMain = jMenuMain;
	}

	public void setDividerLocationOfjSplitPaneCodeTableComment(int height) {
		jPanIdentifyMain.getJSplitPaneCodeTableComment().setDividerLocation(height);
	}

	public String getComment() {
		return jPanIdentifyResetComment.getJTextAreaComment().getText();
	}

	public void setHorizontalScrollBarValue(int value) {
		jPanIdentifyMain.getJScrollPaneTree().getHorizontalScrollBar().setValue(value);
	}

	public ArrayList<String> getStringSearchPendingFileList() {
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		int pendingType = IdentifyMediator.getInstance().getSelectedMatchType();
		AbstractDiscoveryController controller  = ProjectDiscoveryControllerMap.getDiscoveryController(projectName, pendingType);
		ArrayList<String> pendingFilePathList = controller.getPendingFileList();
		return pendingFilePathList;
	}	

	public int getIndexOfTreeOrList() {
		return jTabbedPaneFileNavigation.getSelectedIndex();
	}

	public void setOKResetButtonsForPatternMatch(boolean okButton, boolean resetButton) {
		jPanIdentifyResetComment.setOKResetButtonsForPatternMatch(okButton, resetButton);
	}

	public String getSelectedLicenseName() {
		return selectedLicenseName;
	}

	public void setSelectedLicenseName(String selectedLicenseName) {
		this.selectedLicenseName = selectedLicenseName;
	}

	private JButton autoIdentifyButton = null;
	public void setAutoIdentifyButton(JButton autoIdentifyButton) {
		this.autoIdentifyButton = autoIdentifyButton;
	}

	public void setEnabledAutoIdentifyButton() {
		if(autoIdentifyButton == null) {
			return;
		}
		autoIdentifyButton.setEnabled(true);
	}

	public void setEnabledJMenuItemSPDXAutoIdentify() {
		if(jMenuMain == null) {
			return;
		}
		
		jMenuMain.getJMenuItemSPDXAutoIdentify().setEnabled(true);
	}
}
