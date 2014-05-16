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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.ui.cache.CacheablePanel;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.frm.main.identification.autoidentify.JDlgImportSPDX;
import com.sec.ose.osi.ui.frm.main.identification.codematch.JPanCodeMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.ui.frm.main.identification.patternmatch.JPanPatternMatchMain;
import com.sec.ose.osi.ui.frm.main.identification.stringmatch.JPanStringMatchMain;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * JPanIdentifyMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanIdentifyMain extends CacheablePanel {
	private static Log log = LogFactory.getLog(JPanIdentifyMain.class);
	
	private static final long serialVersionUID = 1L;
	private JSplitPane jSplitPaneMain = null;
	private JPanel jPanelMainRight = null;
	private JSplitPane jSplitPaneCodeTableComment = null;
	
	private JScrollPane jScrollPaneLabelPath = null;
	
	private JPanel jPanelProjectCombo = null;
	private JLabel jLabelProject = null;
	private JPanMatchTypeSelection jPanPendingTypeSelection = null;
	
	private JComboProjectName jComoboProjectName = null;

	private JPanIdentifyResetComment jPanIdentifyResetComment = null;
	private JTabbedPane jTabbedPaneFileNavigation = null;
	private JListMatchedFiles jListMatchedFiles = null;
	private JTreeAllFiles jTreeAllFiles = null;
	
	private JScrollPane jScrollPaneJList = null;
	private JScrollPane jScrollPaneJTree = null;

	// panel
	private JPanStringMatchMain jPanStringMatchMain = null;
	private JPanCodeMatchMain jPanCodeMatchMain = null;
	private JPanPatternMatchMain jPanPatternMatchMain = null;

	public static final int INDEX_TREE = 0;
	public static final int INDEX_LIST = 1;

	static final int SELECTED_PROJECT = 0x01;
	static final int SELECTED_FOLDER = 0x02;
	static final int SELECTED_FILE = 0x03;

	/**
	 * This is the default constructor
	 */
	public JPanIdentifyMain() {
		
		super();
		
		IdentifyMediator.getInstance().setJPanCodeMatch(getJPanCodeMatchMain());
		IdentifyMediator.getInstance().setPnStringSearch(getJPanStringMatchMain());
		IdentifyMediator.getInstance().setJPanPatternMatch( getJPanPatternMatchMain());
		IdentifyMediator.getInstance().setJPendingTypeSelection(JPanPendingTypeSelection());
		IdentifyMediator.getInstance().setjPanPendingIdentification(this);
		IdentifyMediator.getInstance().setJComboProjectName(getJComboProject());
		IdentifyMediator.getInstance().setjTabbedPaneFileNavigation(getJTabbedPaneFileNavigation());
		IdentifyMediator.getInstance().setjTreeAllFiles(getJTreeAllFiles());
		IdentifyMediator.getInstance().setjListMatchedFiles(getJListMatchedFiles());
		IdentifyMediator.getInstance().setJPanIdentifyResetComment(getJPanIdentifyResetComment());
		
		initialize();
	}
	
	private JPanStringMatchMain getJPanStringMatchMain() {
		if (jPanStringMatchMain == null) {
			jPanStringMatchMain = new JPanStringMatchMain();	
		}
		return jPanStringMatchMain;
	}
	
	private JPanCodeMatchMain getJPanCodeMatchMain() {
		if(jPanCodeMatchMain == null) {
			jPanCodeMatchMain = new JPanCodeMatchMain();
		}
		return jPanCodeMatchMain;
	}
	
	private JPanPatternMatchMain getJPanPatternMatchMain() {
		if(jPanPatternMatchMain == null) {
			jPanPatternMatchMain = new JPanPatternMatchMain();
		}
		return jPanPatternMatchMain;
	}
	
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.weightx = 0.7;
		gridBagConstraints.insets = new Insets(10, 300, 0, 0);	//(int top, int left, int bottom, int right)
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 5;
		gridBagConstraints7.gridy = 0;
		gridBagConstraints7.weightx = 0.3;
		gridBagConstraints7.insets = new Insets(10, 0, 0, 90);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.gridwidth = 6;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.BOTH;
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.weighty = 1.0;
		gridBagConstraints5.gridwidth = 6;
		this.setSize(920, 700);
		this.setLayout(new GridBagLayout());
		this.add(getJPanelProjectCombo(), gridBagConstraints);
		this.add(getJPanelAutoIdentify(), gridBagConstraints7);
		this.add(JPanPendingTypeSelection(), gridBagConstraints3);
		this.add(getJSplitPaneMain(), gridBagConstraints5);
	}

	private JPanel jPanelAutoIdentify = null;
	public JPanel getJPanelAutoIdentify() {
		if (jPanelAutoIdentify == null) {
			jPanelAutoIdentify = new JPanel();
			JButton autoIdentifyButton = new JButton("Auto Identify from SPDX");
			autoIdentifyButton.setEnabled(false);
			autoIdentifyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JDlgImportSPDX.getInstance().setVisible(true);
				}
			});
			jPanelAutoIdentify.add(autoIdentifyButton);
			
			IdentifyMediator.getInstance().setAutoIdentifyButton(autoIdentifyButton);
		}
		return jPanelAutoIdentify;
	}
	
	private int tempSize = 0;
	private int preType = SelectedFilePathInfo.SINGLE_FILE_TYPE;
	
	void setOpenCodeMatchSourceView(int type) {
		
		log.debug("[JPanIdentifyMain.setOpenCodeMatchSourceView()] Code Match Source View resized...");
		
		switch(type) {
		
			case SelectedFilePathInfo.PROJECT_TYPE:
			case SelectedFilePathInfo.FOLDER_TYPE:
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				tempSize = getJPanCodeMatchMain().getJSplitPane().getHeight();
				getJPanCodeMatchMain().getJSplitPaneSourceCode().setVisible(false);
				getJPanCodeMatchMain().getJSplitPane().setDividerLocation(tempSize);
				break;
				
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
				if(preType == SelectedFilePathInfo.SINGLE_FILE_TYPE) {
					tempSize = getJPanCodeMatchMain().getJSplitPane().getDividerLocation();
				}else {
					tempSize = getJPanCodeMatchMain().getJSplitPane().getHeight() / 2;
				}
				getJPanCodeMatchMain().getJSplitPaneSourceCode().setVisible(true);
				getJPanCodeMatchMain().getJSplitPane().setDividerLocation(tempSize);
				break;
		}

		preType = type;
	}

	public void refreshChildFrameForMatchedType(String projectName) {
		
		if(IdentifyMediator.getInstance().getSelectedFilePathInfo() == null)
			return;
		
		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		int selectedPendingType = IdentifyMediator.getInstance().getSelectedMatchType();
		
		ArrayList<String> paths = selectedPaths.getSelectedPaths();
		if(paths == null) {
			getJTabbedPaneFileNavigation().setSelectedIndex(INDEX_TREE);
			getJTabbedPaneFileNavigation().repaint(); // list, tree viewer update
		}
		
		log.debug("[JPanIdentifyMain.refreshChildFrameForMatchedType()] executing...");
		
		switch(selectedPendingType) {
		
			case IdentificationConstantValue.STRING_MATCH_TYPE:
				getJPanStringMatchMain().refreshUI(
						projectName,
						paths, 
						selectedPaths);
				break;

			case IdentificationConstantValue.CODE_MATCH_TYPE:
				getJPanCodeMatchMain().refreshUI(
						projectName, 
						paths, 
						selectedPaths);

				break;
				
			case IdentificationConstantValue.PATTERN_MATCH_TYPE:
				getJPanPatternMatchMain().refreshUI(projectName);
				break;
		}
				
		String comment = getComment(projectName, selectedPendingType, selectedPaths);
		IdentifyMediator.getInstance().setComment(comment);
	}
		
	private StringBuffer commentBuf = new StringBuffer();
	private String getComment(String projectName, int selectedPendingType, SelectedFilePathInfo selectedPaths) {

		commentBuf.setLength(0);
		
		String prevComment = IdentificationDBManager.getComment(projectName, selectedPendingType, selectedPaths.getSelectedPath());
		
		if ( (prevComment!=null) && (prevComment.length() > 0) ) {
			commentBuf.append(prevComment).append("\n");
		}
		
		commentBuf.append(DateUtil.getCurrentTime("[%1$tY/%1$tm/%1$te(%1$ta) %1$tl:%1$tM:%1$tS %1$tp"));
		commentBuf.append(" by ");
		commentBuf.append(LoginSessionEnt.getInstance().getUserID());
		commentBuf.append("]\n");
		
		return commentBuf.toString();
	}	

	public void refreshUI() {
		
		getJTabbedPaneFileNavigation().repaint();
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		refreshChildFrameForMatchedType(projectName);
	}
		
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPaneMain() {
		if (jSplitPaneMain == null) {
			jSplitPaneMain = new JSplitPane();
			jSplitPaneMain.setDividerLocation(300);
			jSplitPaneMain.setDividerSize(5);
			jSplitPaneMain.setRightComponent(getJPanelMainRight());
			jSplitPaneMain.setLeftComponent(getJTabbedPaneFileNavigation());
		}
		return jSplitPaneMain;
	}
	
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneLabelPath() {
		if (jScrollPaneLabelPath == null) {
			jScrollPaneLabelPath = new JScrollPane();
			jScrollPaneLabelPath.setBorder(null);
			jScrollPaneLabelPath.setPreferredSize(new Dimension(20, 20));
			jScrollPaneLabelPath.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
			jScrollPaneLabelPath.setViewportView(getJLabelSelectedPath());
		}
		return jScrollPaneLabelPath;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelMainRight() {
		if (jPanelMainRight == null) {
			jPanelMainRight = new JPanel();
			jPanelMainRight.setLayout(new BorderLayout());
			jPanelMainRight.add(getJScrollPaneLabelPath(), BorderLayout.NORTH);
			jPanelMainRight.add(getJSplitPaneCodeTableComment(), BorderLayout.CENTER);
		}
		return jPanelMainRight;
	}

	JTextField jtfSelectedPath = null;
	JTextField getJLabelSelectedPath() {
		if (jtfSelectedPath == null) {
			jtfSelectedPath = new JTextField("", JLabel.LEFT);
			jtfSelectedPath.setEditable(false);
			jtfSelectedPath.setPreferredSize(new Dimension(100,25));
		}
		return jtfSelectedPath;
	}

	/**
	 * This method initializes jSplitPane1	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	JSplitPane getJSplitPaneCodeTableComment() {
		if (jSplitPaneCodeTableComment == null) {
			jSplitPaneCodeTableComment = new JSplitPane();
			jSplitPaneCodeTableComment.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPaneCodeTableComment.setTopComponent(getJPanStringMatchMain());
			jSplitPaneCodeTableComment.setDividerSize(5);
			jSplitPaneCodeTableComment.setResizeWeight(1.0D);
			jSplitPaneCodeTableComment.setContinuousLayout(false);
			jSplitPaneCodeTableComment.setBottomComponent(getJPanIdentifyResetComment());
			jSplitPaneCodeTableComment.setDividerLocation(440);
			jSplitPaneCodeTableComment.setBorder(null);
		}
		return jSplitPaneCodeTableComment;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelProjectCombo() {
		if (jPanelProjectCombo == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 0;
			jLabelProject = new JLabel();
			jLabelProject.setText("Project :");
			jPanelProjectCombo = new JPanel();
			jPanelProjectCombo.setLayout(new GridBagLayout());
			jPanelProjectCombo.setPreferredSize(new Dimension(1000, 37));
			jPanelProjectCombo.add(jLabelProject, gridBagConstraints2);
			jPanelProjectCombo.add(getJComboProject(), gridBagConstraints1);
		}
		return jPanelProjectCombo;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanMatchTypeSelection JPanPendingTypeSelection() {
		if (jPanPendingTypeSelection == null) {
			jPanPendingTypeSelection = new JPanMatchTypeSelection(IdentifyMediator.getInstance());
		}
		return jPanPendingTypeSelection;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */

	private JComboProjectName getJComboProject() {
		if (jComoboProjectName == null) {
			jComoboProjectName = new JComboProjectName();
		}
		return jComoboProjectName;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanIdentifyResetComment getJPanIdentifyResetComment() {
		if (jPanIdentifyResetComment == null) {
			jPanIdentifyResetComment = new JPanIdentifyResetComment();
		}
		return jPanIdentifyResetComment;
	}
	
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	public JTabbedPane getJTabbedPaneFileNavigation() {
		
		if (jTabbedPaneFileNavigation == null) {
			jTabbedPaneFileNavigation = new JTabbedPane();
			jTabbedPaneFileNavigation.addTab("Tree", null, getJScrollPaneTree(), null);
			jTabbedPaneFileNavigation.addTab("List", null, getJScrollPaneList(), null);
			jTabbedPaneFileNavigation.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			jTabbedPaneFileNavigation.addChangeListener(new ChangeListener() {
				
				public void stateChanged(ChangeEvent e) {
					JTabbedPane pane = (JTabbedPane)e.getSource();
					int selectedTab = pane.getSelectedIndex();
					log.debug("[JPanIdentifyMain.getJTabbedPaneFileNavigation()]  selectedTab : " + selectedTab);
					
					SelectedFilePathInfo selectedFilePathInfo = IdentifyMediator.getInstance().getSelectedFilePathInfo();
					String selectedPath = selectedFilePathInfo.getSelectedPath();
					log.debug("[JPanIdentifyMain.getJTabbedPaneFileNavigation()]  selectedPath : " + selectedPath);
					IdentifyMediator.getInstance().setFilePointerToSelectedFile(selectedPath);
					
					actForTab(selectedTab);
					String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
					refreshChildFrameForMatchedType(projectName);
					
				}

			});
		}
		return jTabbedPaneFileNavigation;
	}

	public void repaint() {
		super.repaint();
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JListMatchedFiles getJListMatchedFiles() {
		if (jListMatchedFiles == null) {
			jListMatchedFiles = new JListMatchedFiles();
		}
		return jListMatchedFiles;
	}
	
	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	public JTreeAllFiles getJTreeAllFiles() {
		if(jTreeAllFiles == null) {
			
			jTreeAllFiles = new JTreeAllFiles();
			
		}
		return jTreeAllFiles;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneList() {
		if (jScrollPaneJList == null) {
			jScrollPaneJList = new JScrollPane();
			jScrollPaneJList.setViewportView(getJListMatchedFiles());
		}
		return jScrollPaneJList;
	}
	
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	public JScrollPane getJScrollPaneTree() {
		if (jScrollPaneJTree == null) {
			jScrollPaneJTree = new JScrollPane();
			jScrollPaneJTree.setViewportView(getJTreeAllFiles());
		}
		return jScrollPaneJTree;
	}
	
	public void loadFromCache() {
		
	}

	public void saveToCache() {
		
	}

	public UIEntity exportUIEntity() {
		return null;
	}
	
	void changeSelectedIdentificationPanel(int selectedPendingType) {
		
		jSplitPaneCodeTableComment.setDividerLocation((jSplitPaneCodeTableComment.getDividerLocation()));
		switch(selectedPendingType) {
			case IdentificationConstantValue.STRING_MATCH_TYPE:
				jSplitPaneCodeTableComment.setTopComponent(getJPanStringMatchMain());
				break;
		
			case IdentificationConstantValue.CODE_MATCH_TYPE:
				jSplitPaneCodeTableComment.setTopComponent(getJPanCodeMatchMain());
				break;
				
			case IdentificationConstantValue.PATTERN_MATCH_TYPE:
				jSplitPaneCodeTableComment.setTopComponent(getJPanPatternMatchMain());
				break;
				
		}

	}
	
	public void actForTab(int selectedTab) {
		switch(selectedTab) {
		
			case INDEX_TREE:
				IdentifyMediator.getInstance().setHorizontalScrollBarValue(0); 
				break;
				
			case INDEX_LIST:
				IdentifyMediator.getInstance().setOpenCodeMatchSourceView(SelectedFilePathInfo.SINGLE_FILE_TYPE);
				break;
				
		}
	}
	
	public void setPathTextInfo(String projectName, SelectedFilePathInfo selectedPathInfo) {
		
		String typeString = selectedPathInfo.getPathTypeString();
		String displayedText = "   "+typeString+" : /";
		
		if(selectedPathInfo.getPathType() == SelectedFilePathInfo.PROJECT_TYPE) {
			displayedText += projectName;
		} else {
			displayedText += projectName+"/"+selectedPathInfo.getSelectedPath();
		}
		
		getJLabelSelectedPath().setText(displayedText);
		getJLabelSelectedPath().setToolTipText(displayedText);
	}

}