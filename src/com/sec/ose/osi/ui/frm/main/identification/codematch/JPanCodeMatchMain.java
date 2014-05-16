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
package com.sec.ose.osi.ui.frm.main.identification.codematch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.common.Snippet;
import com.blackducksoftware.sdk.protex.comparison.FileComparisonApi;
import com.blackducksoftware.sdk.protex.comparison.ProtexFile;
import com.blackducksoftware.sdk.protex.comparison.ProtexFileSourceType;
import com.blackducksoftware.sdk.protex.comparison.RelatedSnippets;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.SDKInterface;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.JPanCMTableArea;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMFile;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMFolder;
import com.sec.ose.osi.ui.frm.main.identification.codematch.table.TableModelForCMMultipleFile;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboComponentName;
import com.sec.ose.osi.ui.frm.main.identification.common.JComboLicenseName;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;
import com.sec.ose.osi.util.ProxyUtil;
import com.sec.ose.osi.util.tools.FileOperator;

/**
 * JPanCodeMatchMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanCodeMatchMain extends JPanel {
	private static Log log = LogFactory.getLog(JPanCodeMatchMain.class);

	private final String WEB_BROWSER_PATH 	= "C:\\Program Files\\Internet Explorer\\iexplore.exe";
	
	private static final String NO_SOURCE_CODE_NOT_EXIST_IN_LOCAL = "No source code available in local PC.";
	private static final String NO_SOURCE_CODE_NOT_EXIST_IN_SERVER = "No source code available in Protex Server.";
	private SDKInterface protexSDK = UISDKInterfaceManager.getSDKInterface();
	private final static int FOLDERPANEL_HEIGHT = 80;
	private final static int JSCROLLPANEBOTTOM_HEIGHT_FOR_FOLDERDOWN = 160; 
	private final static int JSCROLLPANEBOTTOM_HEIGHT_FOR_FOLDERUP = 230;

	private static final long serialVersionUID = 1L;
	private JRadioButton rdbtnOpt1IConform = null;
	private JRadioButton rdbtnOpt2ICannotFind = null;
	private JRadioButton rdbtnOpt3Internal = null;
	private JRadioButton rdbtnOpt4NotAlarm = null;
	private JScrollPane jScrollLabel = null;
	private JLabel jLabelComponent = null;
	private JLabel jLabelLicense = null;
	private JSplitPane jSplitPane = null;
	private JPanel jPanelBottom = null;
	private JSplitPane jSplitPaneSourceCode = null;
	private JComboLicenseName cbCmLicense = null;
	private JPanCMCodeView jPanMatchedSourceViewLeft = null;
	private JPanCMCodeView jPanMatchedSourceViewRight = null;
	private JPanel jPanelViewBtn = null;
	private JButton jButtonView = null;
	private JLabel jLabelStringSearchIdentifyInfo = null;
	private JComboComponentName cbComponent = null;
	private JPanel jPanelFolder = null;
	private JPanel jPanelFolderTitle = null;
	private JLabel jLabelFolder = null;
	private JScrollPane jScrollPaneBottom = null;
	
	private JPanCMTableArea jPanelTableCardLayout = null;
	
	public final static String OPTION3_DECLARED_NOT_OPEN_SOURCE = "DECLARED_NOT_OPEN_SOURCE";
	public final static String OPTION4_DECLARED_DO_NOT_ALARM_AGAIN = "DECLARED_DO_NOT_ALARM_AGAIN";
	
	/**
	 * This is the default constructor
	 */
	public JPanCodeMatchMain() {
		super();
		initialize();
		reset();
	}
	
	
	private int pathType;

	public void refreshUI(String pProjectName, 
			ArrayList<String> selectedPaths, 
			SelectedFilePathInfo selectedPathsInfo) {
		
		int matchType = IdentificationConstantValue.CODE_MATCH_TYPE; 
		this.resetNavigator();

		String StringSearchLicense = (
				(DCCodeMatch)ProjectDiscoveryControllerMap.getDiscoveryController(
						pProjectName, 
						matchType)).getStringSearchLicense(pProjectName, selectedPaths.get(0));
		
		if( (StringSearchLicense != null) && (StringSearchLicense.length() != 0) ) {
			jLabelStringSearchIdentifyInfo.setText("This file is identified as " + StringSearchLicense + " by String Search");
		} else {
			jLabelStringSearchIdentifyInfo.setText("No license information is identified.(from String Search)");
		}
		
		this.pathType = selectedPathsInfo.getPathType();
		
		jPanelTableCardLayout.changeTableInfo(
				pProjectName, 
				selectedPaths, 
				selectedPathsInfo,
				pathType);
		
		switch(pathType) {
		
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
				log.debug("SINGLE_FILE_TYPE");
				viewSourceCode(selectedPaths.get(0));
				jButtonView.setEnabled(true);
				break;
				
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				log.debug("MULTIPLE_FILE_TYPE");
				jButtonView.setEnabled(false);
				break;
				
			case SelectedFilePathInfo.FOLDER_TYPE:
			case SelectedFilePathInfo.PROJECT_TYPE:
				log.debug("FOLDER_TYPE or PROJECT_TYPE");
				jButtonView.setEnabled(false);
				break;
		}
		
		reset();
		repaint();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
		gridBagConstraints15.gridx = 1;
		gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints15.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 1;
		jLabelLicense = new JLabel();
		jLabelLicense.setText("License :");
		jLabelLicense.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelComponent = new JLabel();
		jLabelComponent.setText("Component :");
		jLabelComponent.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.setSize(570, 480);
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder(null, "Identification Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		this.add(getJSplitPane(), gridBagConstraints);
		this.add(getJScrollLabel(), gridBagConstraints15);

		getJPanMatchedSourceViewLeft().addScrollAdjustmentObserver(getJPanMatchedSourceViewRight());
		getJPanMatchedSourceViewRight().addScrollAdjustmentObserver(getJPanMatchedSourceViewLeft());

	}

	private void reset() {
		getJPanelFolder().setVisible(false);
		getJRadioButtonOpt1().setSelected(true);
		getJRadioButtonOpt2().setSelected(false);
		getJRadioButtonOpt3().setSelected(false);
		getJRadioButtonOpt4().setSelected(false);
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	public JRadioButton getJRadioButtonOpt1() {
		if (rdbtnOpt1IConform == null) {
			rdbtnOpt1IConform = new JRadioButton();
			rdbtnOpt1IConform.setText("I confirm the open source usage.");
			rdbtnOpt1IConform.setFocusPainted(false);
			rdbtnOpt1IConform.addActionListener(new RadioActionOpt1());
		}
		return rdbtnOpt1IConform;
	}

	/**
	 * This method initializes jRadioButton2	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonOpt2() {
		if (rdbtnOpt2ICannotFind == null) {
			rdbtnOpt2ICannotFind = new JRadioButton();
			rdbtnOpt2ICannotFind.setText("I confirm the open source usage, but I cannot find original component name from the list above.");
			rdbtnOpt2ICannotFind.setFocusPainted(false);
			rdbtnOpt2ICannotFind.addActionListener(new RadioActionOpt2());
		}
		return rdbtnOpt2ICannotFind;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonOpt3() {
		if (rdbtnOpt3Internal == null) {
			rdbtnOpt3Internal = new JRadioButton();
			rdbtnOpt3Internal.setText("Internal/proprietary code (no open source used)");
			rdbtnOpt3Internal.setFocusPainted(false);
			rdbtnOpt3Internal.addActionListener(new RadioActionOpt3());

		}
		return rdbtnOpt3Internal;
	}
	
	/**
	 * This method initializes jRadioButton3	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonOpt4() {
		if (rdbtnOpt4NotAlarm == null) {
			rdbtnOpt4NotAlarm = new JRadioButton();
			rdbtnOpt4NotAlarm.setText("Do not display alarm on this file again (not recommended)");
			rdbtnOpt4NotAlarm.setFocusPainted(false);
			rdbtnOpt4NotAlarm.addActionListener(new RadioActionOpt4());
		}
		return rdbtnOpt4NotAlarm;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	public JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setDividerSize(5);
			jSplitPane.setResizeWeight(1.0D);
			jSplitPane.setTopComponent(getJSplitPaneSourceCode());
			jSplitPane.setBottomComponent(getJScrollPaneBottom());
			jSplitPane.setDividerLocation(150);
			jSplitPane.setBorder(null);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanelBottom == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(0, 30, 0, 0);
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.weighty = 1.0;
			gridBagConstraints9.gridwidth = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(0, 0, 3, 0);
			gridBagConstraints4.gridwidth = 1;
			gridBagConstraints4.gridy = 5;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(3, 0, 0, 0);
			gridBagConstraints2.gridwidth = 1;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridy = 4;
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(new GridBagLayout());
			jPanelBottom.add(getJRadioButtonOpt3(), gridBagConstraints1);
			jPanelBottom.add(getJRadioButtonOpt1(), gridBagConstraints2);
			jPanelBottom.add(getJRadioButtonOpt4(), gridBagConstraints4);
			jPanelBottom.add(getJPanelTableForCardLayout(), gridBagConstraints9);
			jPanelBottom.add(getJPanelFolder(), gridBagConstraints5);
			jPanelBottom.add(getJPanelFolderTitle(), gridBagConstraints11);

			this.addComponentListener(new ComponentAdapter(){
				public void componentResized(ComponentEvent e){
					log.debug("code match source code divider resized...");
					jSplitPaneSourceCode.setDividerLocation(jSplitPaneSourceCode.getSize().width / 2);
				}
			});

		}
		return jPanelBottom;
	}

	private JPanel getJPanelTableForCardLayout() {
		if(jPanelTableCardLayout == null) {
			jPanelTableCardLayout = new JPanCMTableArea(this);
		}
		return jPanelTableCardLayout;
	}
	

	private JScrollPane getJScrollPaneBottom(){
		if (jScrollPaneBottom == null) {
			jScrollPaneBottom = new JScrollPane(getJPanel());
			jScrollPaneBottom.setBorder(null);
			jScrollPaneBottom.setMinimumSize(new Dimension(0, JSCROLLPANEBOTTOM_HEIGHT_FOR_FOLDERDOWN));
		}
		return jScrollPaneBottom;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JSplitPane getJSplitPaneSourceCode() {
		if (jSplitPaneSourceCode == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.weighty = 1.0;
			gridBagConstraints14.weightx = 1.0;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.weighty = 1.0;
			gridBagConstraints17.weightx = 1.0;
			jSplitPaneSourceCode = new JSplitPane();
			jSplitPaneSourceCode.setBorder(BorderFactory.createTitledBorder(null, "Source Code", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jSplitPaneSourceCode.setDividerSize(5);
			jSplitPaneSourceCode.setLeftComponent(getJPanMatchedSourceViewLeft());
			jSplitPaneSourceCode.setRightComponent(getJPanMatchedSourceViewRight());

			PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent changeEvent) {
					String propertyName = changeEvent.getPropertyName();
					if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
						if(getSimilarSnippets() != null) {
							setNavigator();
						}
					}
				}
			};

			jSplitPaneSourceCode.addPropertyChangeListener(propertyChangeListener);
		}
		return jSplitPaneSourceCode;
	}

	private void setNavigator() {
		jButtonSnippets.clear();
		getJPanMatchedSourceViewLeft().getJPanelNavigator().removeAll();
		getJButtonSnippets();
		setJButtonSnippets();
		setCurPosition();
		getJPanMatchedSourceViewLeft().getJPanelNavigator().repaint();
		getJPanMatchedSourceViewLeft().repaint();
		getJSplitPaneSourceCode().repaint();
	}

	public void resetNavigator() {
		jButtonSnippets.clear();
		getJPanMatchedSourceViewLeft().getJPanelNavigator().removeAll();
		getJPanMatchedSourceViewLeft().getJPanelNavigator().repaint();
		getJPanMatchedSourceViewLeft().repaint();
		getJSplitPaneSourceCode().repaint();
	}

	int selectSnippetNum = 0;
	private void setCurPosition() {
		if(totalLine != 0) {
			log.debug("setCurPosition selectSnippetNum : " + selectSnippetNum);
			JLabel jLabelPosition = null;
			int curPosY = 0;
			if(totalLine > 0 && getSimilarSnippets().size() > 0) {
				curPosY = getSimilarSnippets().get(selectSnippetNum).getLeftSnippet().getFirstLine() * getJSplitPaneSourceCode().getHeight() / totalLine;
			}
			jLabelPosition = new JLabel();
			jLabelPosition.setPreferredSize(new Dimension(15, 15));
			jLabelPosition.setLocation(new Point(35, curPosY));
			jLabelPosition.setSize(new Dimension(15, 15));
			jLabelPosition.setText("¢¸");
			getJPanMatchedSourceViewLeft().getJPanelNavigator().add(jLabelPosition, null);
		}
	}
	
	public void resetSelectSnippetNum() {
		selectSnippetNum = 0;
	}

	ArrayList<JButton> jButtonSnippets = new ArrayList<JButton>();
	private void getJButtonSnippets() {
		int y = 0;
		int height = 0;
		if(getSimilarSnippets() == null) {
			log.debug("getSimilarSnippets()_null_return");
			return;
		}
		for(int i=0; i<getSimilarSnippets().size(); i++) {
			if(totalLine != 0) {
				y = getSimilarSnippets().get(i).getLeftSnippet().getFirstLine() * getJSplitPaneSourceCode().getHeight() / totalLine;
				height = (getSimilarSnippets().get(i).getLeftSnippet().getLastLine()-getSimilarSnippets().get(i).getLeftSnippet().getFirstLine())* getJSplitPaneSourceCode().getHeight() / totalLine;
			}
			JButton jButton = new JButton();
			jButton.addActionListener(new ButtonListener());
			jButton.setBackground(new Color(255, 255, 180));
			jButton.setBounds(new Rectangle(1, y, 35, height)); // new Rectangle(int x, int y, int width, int height)
			jButtonSnippets.add(jButton);
		}
	}

	private void setJButtonSnippets() {
		for(int i=0; i<getSimilarSnippets().size(); i++) {
			getJPanMatchedSourceViewLeft().getJPanelNavigator().add(jButtonSnippets.get(i), null);
		}
	}


	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboLicenseName getJComboBoxLicense() {
		if (cbCmLicense == null) {
			cbCmLicense = new JComboLicenseName();
			cbCmLicense.setEnabled(false);
			cbCmLicense.setEditable(true);
			cbCmLicense.setPreferredSize(new Dimension(350, 27));
		}
		return cbCmLicense;
	}
	

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	public JPanCMCodeView getJPanMatchedSourceViewLeft() {
		if (jPanMatchedSourceViewLeft == null) {
			jPanMatchedSourceViewLeft = new JPanCMCodeView("LEFT");

		}
		return jPanMatchedSourceViewLeft;
	}

	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	public JPanCMCodeView getJPanMatchedSourceViewRight() {
		if (jPanMatchedSourceViewRight == null) {
			jPanMatchedSourceViewRight = new JPanCMCodeView("RIGHT");

		}
		return jPanMatchedSourceViewRight;
	}


	private JScrollPane getJScrollLabel(){
		if (jScrollLabel == null) {
			jScrollLabel = new JScrollPane(getJPanelViewBtn());
			jScrollLabel.setBorder(null);
			jScrollLabel.setPreferredSize(new Dimension(100,40));
			jScrollLabel.setMinimumSize(new Dimension(100,40));
		}
		return jScrollLabel;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelViewBtn() {
		if (jPanelViewBtn == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.anchor = GridBagConstraints.EAST;
			gridBagConstraints18.fill = GridBagConstraints.NONE;
			gridBagConstraints18.insets = new Insets(0, 0, 10, 10);
			gridBagConstraints18.weightx = 1.0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints16.gridy = 0;
			jLabelStringSearchIdentifyInfo = new JLabel();
			jLabelStringSearchIdentifyInfo.setText("This file is identified as (StringSearchLicense) by String Search");
			jPanelViewBtn = new JPanel();
			jPanelViewBtn.setLayout(new GridBagLayout());
			jPanelViewBtn.add(getJButtonView(), gridBagConstraints18);
			jPanelViewBtn.add(jLabelStringSearchIdentifyInfo, gridBagConstraints16);
		}
		return jPanelViewBtn;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonView() {
		if (jButtonView == null) {
			jButtonView = new JButton();
			jButtonView.setText("View");
			jButtonView.setFocusPainted(false);
			jButtonView.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("View Button Click ~~~");
					try {
						String osName = System.getProperty("os.name");
						String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
						
						DCCodeMatch xCodeMatchDiscoveryController = 
							(DCCodeMatch)		
							ProjectDiscoveryControllerMap.getDiscoveryController(projectName, IdentificationConstantValue.CODE_MATCH_TYPE);
			
						String selectedPath = IdentifyMediator.getInstance().getSelectedFilePathInfo().getSelectedPath();
						String codeMatchUrl = xCodeMatchDiscoveryController.getCodeMatchedURL(selectedPath);
						
						Runtime rt = Runtime.getRuntime();
						if(osName.startsWith("Windows")) {
							// windows
							try {
								String[] b = {WEB_BROWSER_PATH, codeMatchUrl};
								Runtime.getRuntime().exec(b);
							} catch (Exception e1) {
								log.warn(e1);
							}
						} else if(osName.startsWith("Linux")) {
							String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",	"netscape","opera","links","lynx"};
							StringBuffer cmd = new StringBuffer();
							for (int i=0; i<browsers.length; i++) {
								cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + codeMatchUrl + "\" ");
							}
							System.out.println("code view button : "+ cmd.toString());
							rt.exec(new String[] { "sh", "-c", cmd.toString() });
						}
					} catch (IOException e1) {
						log.warn(e1.getMessage());
					}
				}
			});
		}
		return jButtonView;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxComponent() {
		if (cbComponent == null) {
			cbComponent = new JComboComponentName();
			cbComponent.setPreferredSize(new Dimension(350, 27));
			cbComponent.setEnabled(false);
			cbComponent.setEditable(true);

		}
		return cbComponent;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getJPanelFolder() {
		if (jPanelFolder == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridwidth = 1;
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 0.0;
			gridBagConstraints8.insets = new Insets(0, 10, 0, 3);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(5, 0, 5, 10);
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			GridBagConstraints gridBagConstraints66 = new GridBagConstraints();
			gridBagConstraints66.anchor = GridBagConstraints.WEST;
			gridBagConstraints66.insets = new Insets(5, 0, 5, 10);
			gridBagConstraints66.gridx = 2;
			gridBagConstraints66.gridy = -1;
			gridBagConstraints66.weightx = 1.0;
			gridBagConstraints66.fill = GridBagConstraints.VERTICAL;
			
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.EAST;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 0.0;
			gridBagConstraints10.insets = new Insets(0, 0, 0, 3);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
			
			jPanelFolder = new JPanel();
			jPanelFolder.setLayout(new GridBagLayout());			
			jPanelFolder.add(jLabelComponent, gridBagConstraints8);
			jPanelFolder.add(getJComboBoxComponent(), gridBagConstraints6);
			
			jPanelFolder.add(jLabelLicense, gridBagConstraints10);
			jPanelFolder.add(getJComboBoxLicense(), gridBagConstraints7);
		}
		return jPanelFolder;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelFolderTitle() {
		if (jPanelFolderTitle == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints12.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = -1;
			gridBagConstraints3.gridy = -1;
			gridBagConstraints3.gridwidth = 1;
			jPanelFolderTitle = new JPanel();
			jPanelFolderTitle.setLayout(new GridBagLayout());
			jPanelFolderTitle.add(getJRadioButtonOpt2(), gridBagConstraints3);
			jPanelFolderTitle.add(getJButtonFolder(), gridBagConstraints12);
		}
		return jPanelFolderTitle;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JLabel getJButtonFolder() {
		if (jLabelFolder == null) {
			jLabelFolder = new JLabel();
			jLabelFolder.setPreferredSize(new Dimension(20, 20));
			jLabelFolder.setBackground(new Color(238, 238, 238));
			jLabelFolder.setBorder(null);
			jLabelFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
		}
		return jLabelFolder;
	}


	private void setOptionUI() {
		if(getJRadioButtonOpt2().isSelected()) {
			jLabelFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_up.png")));
			if(!getJPanelFolder().isVisible()) {
				getJPanelFolder().setVisible(true);
				if( jPanelBottom.getSize().height > FOLDERPANEL_HEIGHT * 2 ) {
					jPanelBottom.setSize(jPanelBottom.getSize().width, jPanelBottom.getSize().height - FOLDERPANEL_HEIGHT);
				} else {
					log.debug("\"I have used open source\" option... divider resized...");
					getJSplitPane().setDividerLocation(getJSplitPane().getDividerLocation() - FOLDERPANEL_HEIGHT);
				}
			}
			jScrollPaneBottom.setMinimumSize(new Dimension(0, JSCROLLPANEBOTTOM_HEIGHT_FOR_FOLDERUP));
		} else {
			jLabelFolder.setIcon(new ImageIcon(WindowUtil.class.getResource("fold_down.png")));
			if(getJPanelFolder().isVisible()) {
				getJPanelFolder().setVisible(false);
				jPanelBottom.setSize(jPanelBottom.getSize().width, jPanelBottom.getSize().height + FOLDERPANEL_HEIGHT);
			}
			jScrollPaneBottom.setMinimumSize(new Dimension(0, JSCROLLPANEBOTTOM_HEIGHT_FOR_FOLDERDOWN));
		}
	}


	/*******************************************************************************************************
	 * 		Action
	 */	
	class RadioActionOpt1 implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("IHaveUsed Radio Select actionPerformed()");
			if(rdbtnOpt1IConform.isSelected()) {
				cbComponent.setEnabled(false);
				cbCmLicense.setEnabled(false);
				
				JTableMatchedInfo jTableMatchedInfo = jPanelTableCardLayout.getSelectedTable();
				
				String selectedLicenseName = DCCodeMatch.getOriginValue((String)jTableMatchedInfo.getValueAt(jTableMatchedInfo.getSelectedRow(),2));
				if(jTableMatchedInfo.getSelectedRow()>=0) {
					IdentifyMediator.getInstance().setSelectedLicenseName(selectedLicenseName);
				} else {
					IdentifyMediator.getInstance().setSelectedLicenseName("");
				}
				log.debug("RadioActionOpt1 selectedLicenseName : " + IdentifyMediator.getInstance().getSelectedLicenseName());
			}
			getJRadioButtonOpt3().setSelected(false);
			getJRadioButtonOpt1().setSelected(true);
			getJRadioButtonOpt2().setSelected(false);
			getJRadioButtonOpt4().setSelected(false);
			setOptionUI();
		}
	}

	class RadioActionOpt2 implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("ICannotFind Radio Select actionPerformed()");
			if(rdbtnOpt2ICannotFind.isSelected()) {
				cbComponent.initComponentComboBox();
				cbCmLicense.initLicenseComboBox();
				cbComponent.setEnabled(true);
				cbCmLicense.setEnabled(true);
				IdentifyMediator.getInstance().setSelectedLicenseName("");
				log.debug("license init");
			}
			getJRadioButtonOpt3().setSelected(false);
			getJRadioButtonOpt1().setSelected(false);
			getJRadioButtonOpt2().setSelected(true);
			getJRadioButtonOpt4().setSelected(false);
			setOptionUI();
		}
	}

	class RadioActionOpt3 implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("ThisFileIs Radio Select actionPerformed()");
			if(rdbtnOpt3Internal.isSelected()) {
				cbComponent.setEnabled(false);

				IdentifyMediator.getInstance().setSelectedLicenseName("");
				log.debug("license init");
			}

			getJRadioButtonOpt3().setSelected(true);
			getJRadioButtonOpt1().setSelected(false);
			getJRadioButtonOpt2().setSelected(false);
			getJRadioButtonOpt4().setSelected(false);
			setOptionUI();
		}
	}

	class RadioActionOpt4 implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			log.debug("DoNotAlarm Radio Select actionPerformed()");
			if(rdbtnOpt4NotAlarm.isSelected()) {
				cbComponent.setEnabled(false);
				cbCmLicense.setEnabled(false);

				IdentifyMediator.getInstance().setSelectedLicenseName("");
				log.debug("license init");
			}
			getJRadioButtonOpt3().setSelected(false);
			getJRadioButtonOpt1().setSelected(false);
			getJRadioButtonOpt2().setSelected(false);
			getJRadioButtonOpt4().setSelected(true);
			setOptionUI();
		}
	}

	private int prevSelectedCodeMatchTableIndex = 0;

	public int getPrevSelectedCodeMatchTableIndex() {
		return prevSelectedCodeMatchTableIndex;
	}

	/** Source View **/

	private String currentFileName = "";
	private OpenSourceViewTask task = null;
	private List<RelatedSnippets> similarSnippets = null;
	private boolean isWorkingSourceViewTask = false;

	public List<RelatedSnippets> getSimilarSnippets() {
		return similarSnippets;
	}

	static HashSet<String> NOT_AVAILABLE_EXTENSION = new HashSet<String>();
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

	public void viewSourceCode(String fileName) {
		prevSelectedCodeMatchTableIndex = 0;
		if(!currentFileName.equals(fileName)) {
			currentFileName = fileName;
			if(isWorkingSourceViewTask) {
				task.cancel(true);
			}					
			similarSnippets = null;	
			if(isAvailableExtention(FileOperator.getExtention(fileName))) {
				setMySourceCode(currentFileName);
				JTableMatchedInfo jTableMatchedInfo = jPanelTableCardLayout.getSelectedTable();
				if(jTableMatchedInfo.getSelectedRow() >= 0) {
					updateSourceCodeView();
				} else {
					getJPanMatchedSourceViewRight().clear();
				}
			} else {
				getJPanMatchedSourceViewLeft().clear();
				getJPanMatchedSourceViewRight().clear();
			}
		}
	}

	private void setMySourceCode(String fileName) {
		sbMySourceNum.delete(0, sbMySourceNum.length());
		sbMySourceText.delete(0, sbMySourceText.length());

		log.debug("setMySourceCode: "+fileName);

		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		File mySourceCode = new File(protexSDK.getSourceLocation(projectName)+File.separator+fileName);

		log.debug("mySourceCode: "+mySourceCode.getAbsolutePath());

		getJPanMatchedSourceViewLeft().clear();
		if(mySourceCode.exists()) {
			FileReader fr = null;
			try {
				fr = new FileReader(mySourceCode);
				BufferedReader br = new BufferedReader(fr);
				String tmpStr = null;
				int curLine = 1;
				while((tmpStr = br.readLine()) != null) {
					sbMySourceNum.append(String.valueOf(curLine)+"\n");
					sbMySourceText.append(tmpStr+"\n");
					curLine++;
				}

				getJPanMatchedSourceViewLeft().setText(
						sbMySourceText.toString(), 
						sbMySourceNum.toString());
			} catch (Exception e) {
				log.warn(e.getMessage());
				getJPanMatchedSourceViewLeft().clear();
				getJPanMatchedSourceViewLeft().setText(
						mySourceCode.getAbsolutePath() + "\n" + NO_SOURCE_CODE_NOT_EXIST_IN_LOCAL);
	        } finally {
	        	try {
					if(fr != null) { fr.close(); }
				} catch (Exception e) {
					log.warn(e);
				}
	        }
		} else {
			getJPanMatchedSourceViewLeft().setText(
					mySourceCode.getAbsolutePath() + "\n" + NO_SOURCE_CODE_NOT_EXIST_IN_LOCAL);
		}

		getJPanMatchedSourceViewLeft().clearStyle();

	}

	public void updateSourceCodeView() {
		if(isWorkingSourceViewTask) {
			task.cancel(true);
			task = null;
		}
		isWorkingSourceViewTask = true;
		similarSnippets = null;
		task = new OpenSourceViewTask();
		task.execute();
	}

	private Vector<Integer> startMySourceMatchPos = new Vector<Integer>();
	private Vector<Integer> endMySourceMatchPos = new Vector<Integer>();
	
	private Vector<Integer> startServerSourceMatchPos = new Vector<Integer>();
	private Vector<Integer> endServerSourceMatchPos = new Vector<Integer>();

	private StringBuffer sbMySourceNum = new StringBuffer();
	private StringBuffer sbMySourceText = new StringBuffer();

	private boolean preMyMatching = false;
	private void setMySourceMatchLine(int line,int curTextSize) {
		if(similarSnippets == null) return;
		boolean isMatching = false;
		for (RelatedSnippets similarSnippet: similarSnippets) {
			Snippet leftSnippet = similarSnippet.getLeftSnippet();
			if(line >= leftSnippet.getFirstLine() && line <= leftSnippet.getLastLine()) {
				isMatching = true;
				if(!preMyMatching) {
					startMySourceMatchPos.addElement(curTextSize);
				}
				break;
			}
		}
		if(!isMatching && preMyMatching) {
			endMySourceMatchPos.addElement(curTextSize);
		}
		preMyMatching = isMatching;
	}

	private boolean preServerMatching = false;
	private void setServerSourceMatchLine(int line, int curTextSize) {
		if(similarSnippets == null) return;
		boolean isMatching = false;
		for (RelatedSnippets similarSnippet: similarSnippets) {
			Snippet rightSnippet = similarSnippet.getRightSnippet();
			if(line >= rightSnippet.getFirstLine() && line <= rightSnippet.getLastLine()) {
				isMatching = true;
				if(!preServerMatching) {
					startServerSourceMatchPos.addElement(curTextSize);
				}
				break;
			}
		}
		if(!isMatching && preServerMatching) {
			endServerSourceMatchPos.addElement(curTextSize);
		}
		preServerMatching = isMatching;
	}

	private int totalLine = 0;
	private void markMySourceSnippet() {
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setBackground( attr, new Color(255, 255, 180));

		startMySourceMatchPos.clear();
		endMySourceMatchPos.clear();

		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		File mySourceCode = new File(protexSDK.getSourceLocation(projectName)+File.separator+currentFileName);
		if(mySourceCode.exists()) {
			FileReader fr = null;
			try {
				fr = new FileReader(mySourceCode);
				BufferedReader br = new BufferedReader(fr);
				String tmpStr = null;
				int curLine = 1;
				int curTextSize = 0;
				while((tmpStr = br.readLine()) != null) {
					setMySourceMatchLine(curLine,curTextSize);
					curTextSize += tmpStr.length()+1;
					curLine++;
				}
				totalLine = curLine - 1;
				setMySourceMatchLine(curLine,curTextSize);
			} catch (Exception e) {
				log.warn(e);
	        } finally {
	        	try {
					if(fr != null) { fr.close(); }
				} catch (Exception e) {
					log.warn(e);
				}
	        }

			getJPanMatchedSourceViewLeft().clearStyle();

			if(startMySourceMatchPos.size() > endMySourceMatchPos.size()) 
				endMySourceMatchPos.add(
						getJPanMatchedSourceViewLeft()
						.getStyledDocumentForSourcePane().getLength()
				);

			int snippetCnt = startMySourceMatchPos.size();
			if(snippetCnt > 0) {
				for(int i=0; i<snippetCnt;i++) {
					int offset = startMySourceMatchPos.get(i);
					int length = endMySourceMatchPos.get(i) - offset;
					getJPanMatchedSourceViewLeft()
					.getStyledDocumentForSourcePane().setCharacterAttributes(offset, length, attr, true);
				}
				getJPanMatchedSourceViewLeft()
				.getJTextPaneSourceView()
				.setCaretPosition(startMySourceMatchPos.get(0));
			}
		}
	}

	class OpenSourceViewTask extends SwingWorker<String, String> {
		int curLine = 1;
		boolean isComplete = false;

		Vector<Integer> startOpenSourceMatchPos = new Vector<Integer>();
		Vector<Integer> endOpenSourceMatchPos = new Vector<Integer>(); 

		StringBuffer sbOpenSourceNum = new StringBuffer();
		StringBuffer sbOpenSourceText = new StringBuffer();

		MutableAttributeSet attr = new SimpleAttributeSet();
		OpenSourceViewTask() {
			StyleConstants.setBackground( attr, new Color(255, 255, 180));
		}

		@Override
		public String doInBackground() {
			getJPanMatchedSourceViewRight().getJTextPaneLineNumber().setText("");
			getJPanMatchedSourceViewRight().getJTextPaneSourceView().setText("Now Loading...");
			getJPanMatchedSourceViewRight().clearStyle();
			InputStream inputStream = null;
			try {
				List<CodeMatchType> precisionOnly = new ArrayList<CodeMatchType>(1);
				precisionOnly.add(CodeMatchType.PRECISION);

				PartialCodeTree fileOnlyTree = new PartialCodeTree();
				fileOnlyTree.setParentPath("/");
				CodeTreeNode tmpCodeTreeNode = new CodeTreeNode();
				fileOnlyTree.getNodes().add(tmpCodeTreeNode);
				tmpCodeTreeNode.setNodeType(CodeTreeNodeType.FILE);
				tmpCodeTreeNode.setName(currentFileName);

				JTableMatchedInfo jTableMatchedInfo = jPanelTableCardLayout.getSelectedTable();
				String componentName = jTableMatchedInfo.getValueAt(jTableMatchedInfo.getSelectedRow(), TableModelForCMFile.COL_COMPONENT_NAME).toString();
				componentName = DCCodeMatch.getOriginValue(componentName);
				log.debug("File view componentName : "+componentName);
				String matchedFile = jTableMatchedInfo.getValueAt(jTableMatchedInfo.getSelectedRow(), TableModelForCMFile.COL_MATCHED_FILE).toString();
				String componentID = ComponentAPIWrapper.getGlobalComponentId(componentName);

				// 1. Source Code Compare
				ProtexFile leftFile = new ProtexFile();
				String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
				String projectID = ProjectAPIWrapper.getProjectID(projectName);
				leftFile.setComponentOrProjectId(projectID);
				leftFile.setFilePath("/"+currentFileName);
				leftFile.setFileType(ProtexFileSourceType.PROJECT);

				ProtexFile rightFile = new ProtexFile();
				rightFile.setComponentOrProjectId(componentID);
				rightFile.setFilePath(matchedFile);
				log.debug("matchedFile : "+matchedFile);
				rightFile.setFileType(ProtexFileSourceType.STANDARD_COMPONENT);

				FileComparisonApi fileComparisonApi = ProtexSDKAPIManager.getFileComparisonAPI();
				similarSnippets = fileComparisonApi.getFileSimilarities(leftFile, rightFile);

				if(similarSnippets != null && similarSnippets.size() > 0) {
					log.debug("similarSnippets exist...");
					getJPanMatchedSourceViewLeft().setBaseline(similarSnippets.get(0).getLeftSnippet().getFirstLine());
					getJPanMatchedSourceViewRight().setBaseline(similarSnippets.get(0).getRightSnippet().getFirstLine());
				}

				// 2. My Source Code Repaint
				getJPanMatchedSourceViewLeft().getJTextPaneSourceView().setCaretPosition(
						getJPanMatchedSourceViewLeft().getStyledDocumentForSourcePane().getLength()
				);
				markMySourceSnippet();

				// 3. Open Source Code Paint
				startServerSourceMatchPos.clear();
				endServerSourceMatchPos.clear();
				
				String orgFileLocation = fileComparisonApi.getFileUrl(rightFile);
				String fileLocation = orgFileLocation.replace("127.0.0.1:80", LoginSessionEnt.getInstance().getProtexServerIP());

				log.debug("right source file location URL : "+fileLocation);
	
				URL url = new URL(fileLocation);
				URLConnection conn = null;
				
				Proxy proxy = ProxyUtil.getInstance().getProxy();
				if(proxy == null)
		        	conn = (HttpURLConnection) url.openConnection();
		        else
		        	conn = (HttpURLConnection) url.openConnection(proxy);
				
	        
				inputStream = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				int curLine = 1;
				int curTextSize = 0;
				String tmpStr = null;
				while((tmpStr = br.readLine()) != null) {
					publish(tmpStr);
					setServerSourceMatchLine(curLine,curTextSize);
					curTextSize += tmpStr.length()+1;
					curLine++;
				}
				setServerSourceMatchLine(curLine,curTextSize);
				isComplete = true;

				// 4. Code Navigator composite
				setNavigator();

			} catch (Exception e1) {
				log.warn(e1.getMessage());
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return "";
		}


		boolean preMatching = false;
		private void setOpenSourceMatchLine(int line) {
			boolean isMatching = false;
			if(similarSnippets == null) return;
			for (RelatedSnippets similarSnippet: similarSnippets) {
				Snippet rightSnippet = similarSnippet.getRightSnippet();
				if(line >= rightSnippet.getFirstLine() && line <= rightSnippet.getLastLine()) {
					isMatching = true;
					if(!preMatching) {
						startOpenSourceMatchPos.addElement(sbOpenSourceText.length());
					}
					break;
				}
			}
			if(!isMatching && preMatching) {
				endOpenSourceMatchPos.addElement(sbOpenSourceText.length());
			}
			preMatching = isMatching;
		}

		@Override
		protected void process(List<String> progress) {
			for(String tmpStr:progress) {
				setOpenSourceMatchLine(curLine);
				sbOpenSourceNum.append(String.valueOf(curLine)+"\n");
				sbOpenSourceText.append(tmpStr+"\n");
				curLine++;
			}
			setOpenSourceMatchLine(curLine);
		}

		@Override
		protected void done() {
			if(isDone() && isComplete) {
				getJPanMatchedSourceViewRight().setText(
						sbOpenSourceText.toString(),
						sbOpenSourceNum.toString()
				);

				getJPanMatchedSourceViewRight().clearStyle();

				if(startOpenSourceMatchPos.size() > endOpenSourceMatchPos.size()) 
					endOpenSourceMatchPos.add(
							getJPanMatchedSourceViewRight().getStyledDocumentForSourcePane().getLength()
					);

				for(int i=0; i<startOpenSourceMatchPos.size();i++) {
					int offset = startOpenSourceMatchPos.get(i);
					int length = endOpenSourceMatchPos.get(i) - offset;
					getJPanMatchedSourceViewRight()
					.getStyledDocumentForSourcePane()
					.setCharacterAttributes(offset, length, attr, true);
				}
				if(startOpenSourceMatchPos.size() > 0)
					getJPanMatchedSourceViewRight()
					.getJTextPaneSourceView()
					.setCaretPosition(startOpenSourceMatchPos.get(0));
			} else {
				getJPanMatchedSourceViewRight().clear();
				getJPanMatchedSourceViewRight().setText(NO_SOURCE_CODE_NOT_EXIST_IN_SERVER);

			}
			isWorkingSourceViewTask = false;
		}
	}

	class ButtonListener implements ActionListener {

		public ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) 
		{ 
			JPanCMCodeView jPanLeft = getJPanMatchedSourceViewLeft().getSiblingSourceView();
			if(getSimilarSnippets() != null) {
				for(int i=0; i<getSimilarSnippets().size(); i++) {
					if(e.getSource() == jButtonSnippets.get(i)) {
						log.debug("SnippetButton has been clicked : " + i);

						if(jPanLeft != null) {
							int pos = jPanLeft.getJScrollPane().getVerticalScrollBar().getMaximum();
							jPanLeft.getJScrollPane().getVerticalScrollBar().setValue(pos);
							getJPanMatchedSourceViewLeft().getJTextPaneSourceView().setCaretPosition(startMySourceMatchPos.get(i));
						}

						selectSnippetNum = i;
						setNavigator();
					}
				}
			}
		}
	}
	
	public UECodeMatch exportUIEntity(String projectName) {
		
		String tmpComponentName = ""; 
		String tmpVersionName = "";
		String tmpLicenseName = "";
		
		String originComponentName = "";
		String originVersionName = "";
		String originLicenseName = "";
		
		String currentComponentName = "";
		String currentVersionName = "";
		String currentLicenseName = "";
		
		String newComponentName = "";
		String newVersionName = "";
		String newLicenseName = "";
		
		String matchedFile = "";
		String comment = IdentifyMediator.getInstance().getComment();
		
		int compositeType = IdentificationConstantValue.CODE_MATCH_TYPE;
		int row = 0;
		JTableMatchedInfo jTableMatchedInfo = jPanelTableCardLayout.getSelectedTable();
		String status = "";
		
		switch(pathType) {
			case SelectedFilePathInfo.SINGLE_FILE_TYPE:
				{
					row = jTableMatchedInfo.getSelectedRow();
					tmpComponentName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_COMPONENT_NAME)); 
					tmpVersionName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_VERSION_NAME));
					tmpLicenseName =String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_LICENSE_NAME));
					matchedFile = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_MATCHED_FILE));
					status = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_STATUS));
				}
				break;
			case SelectedFilePathInfo.MULTIPLE_FILE_TYPE:
				{
					row = jTableMatchedInfo.getSelectedRow();
					tmpComponentName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMMultipleFile.COL_COMPONENT_NAME)); 
					tmpVersionName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMMultipleFile.COL_VERSION_NAME));
					tmpLicenseName =String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMMultipleFile.COL_LICENSE_NAME));
					status = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMMultipleFile.COL_STATUS));
					compositeType |= IdentificationConstantValue.FOLDER_TYPE;
				}
				break;
			case SelectedFilePathInfo.FOLDER_TYPE:
			case SelectedFilePathInfo.PROJECT_TYPE:
				{
					row = jTableMatchedInfo.getSelectedRow();
					tmpComponentName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFolder.COL_COMPONENT_NAME)); 
					tmpVersionName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFolder.COL_VERSION_NAME));
					tmpLicenseName =String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFolder.COL_LICENSE_NAME));
					status = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFolder.COL_STATUS));
					compositeType |= IdentificationConstantValue.FOLDER_TYPE;
				}
				break;
		}
		
		currentComponentName = DCCodeMatch.getCurrentValue(tmpComponentName);
		currentVersionName = DCCodeMatch.getCurrentValue(tmpVersionName);
		currentLicenseName = DCCodeMatch.getCurrentValue(tmpLicenseName); 
		
		originComponentName = DCCodeMatch.getOriginValue(tmpComponentName);
		originVersionName = DCCodeMatch.getOriginValue(tmpVersionName);
		originLicenseName = DCCodeMatch.getOriginValue(tmpLicenseName);
		
		if(rdbtnOpt1IConform.isSelected()) {
			compositeType |= IdentificationConstantValue.STANDARD_COMPONENT_TYPE;
			newComponentName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_COMPONENT_NAME));
			newLicenseName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_LICENSE_NAME));
			newVersionName = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_VERSION_NAME));
			if(newVersionName.equals("Unspecified")) newVersionName = "";
			
		} else if(rdbtnOpt2ICannotFind.isSelected()){
			status = String.valueOf(jTableMatchedInfo.getValueAt(row, TableModelForCMFile.COL_STATUS));
			if( ("Pending".equals(status)) &&
				(IdentifyMediator.getInstance().getSelectedLicenseName().length() == 0) ) {
	        	JOptionPane.showOptionDialog(
	        			null, 
	        			"Select License.", 
	        			"Pending identification", 
	        			JOptionPane.OK_OPTION, 
	        			JOptionPane.ERROR_MESSAGE, 
	        			null, 
	        			buttonOK, 
	        			"OK");
	        	return null;
	        }
			
			if( ("Pending".equals(status)) &&
				(cbComponent.getSelectedItem() == null) ) {
				JOptionPane.showOptionDialog(
						null, 
						"Component must be completed.", 
						"Pending identification", 
						JOptionPane.OK_OPTION, 
						JOptionPane.ERROR_MESSAGE, 
						null, 
						buttonOK, "OK");
				return null;
			} else {
				newComponentName = String.valueOf(cbComponent.getSelectedItem());
				newLicenseName = String.valueOf(cbCmLicense.getSelectedItem());
				
			}
			compositeType |= IdentificationConstantValue.STANDARD_COMPONENT_TYPE;
			
		} else if(rdbtnOpt3Internal.isSelected()) {
			newComponentName = OPTION3_DECLARED_NOT_OPEN_SOURCE;
			
		} else if(rdbtnOpt4NotAlarm.isSelected()){
			newComponentName = OPTION4_DECLARED_DO_NOT_ALARM_AGAIN;
		}
		
		log.debug("[JPanCodeMatchMain.exportUIEntity()] newComponentName : "+newComponentName);
		log.debug("[JPanCodeMatchMain.exportUIEntity()] newVersionName : "+newVersionName);
		log.debug("[JPanCodeMatchMain.exportUIEntity()] newLicenseName : "+newLicenseName);
	
		UECodeMatch xUECodeMatch = new UECodeMatch(
				originComponentName,
				originVersionName,
				originLicenseName,
				currentComponentName,
				currentVersionName,
				currentLicenseName,
				newComponentName, 
				newVersionName,
				newLicenseName, 
				matchedFile, 
				compositeType,
				comment,
				status);
		return xUECodeMatch;
	}
	private String[] buttonOK = {"OK"};
	

}
