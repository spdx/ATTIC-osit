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
package com.sec.ose.osi.ui.frm.main.manage.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;
import com.sec.ose.osi.ui.frm.main.manage.UEProjectClone;
import com.sec.ose.osi.ui.frm.main.manage.UEProjectCreate;
import com.sec.ose.osi.ui.frm.main.report.file_explorer.JFCFolderExplorer;
import com.sec.ose.osi.util.policy.PolicyCheckResult;
import com.sec.ose.osi.util.policy.ProjectNamePolicy;
import com.sec.ose.osi.util.tools.ProjectSplitInfo;
import com.sec.ose.osi.util.tools.ProjectSplitUtil;

/**
 * JDlgProjectCreate
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JDlgProjectCreate extends JDialog {
	private static Log log = LogFactory.getLog(JDlgProjectCreate.class);
	
	private static final long serialVersionUID = 1L;
	private JLabel jLabelNewProjectName = null;
	private JTextField jTextFieldNewProjectName = null;
	private JButton jButtonCheck = null;
	private JCheckBox jCheckBoxClonedFrom = null;
	private JComboBox<String> jComboBoxClonedFrom = null;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JTextField jTextFieldSourceLocation = null;
	private JButton jButtonBrowse = null;
	private JPanel jPanelPjtName = null;
	private JPanel jPanelSourceLocation = null;
	private JPanel jPanelButton = null;
	
	private JPanManageMain jPanManageMain;
	private JFrame frame = null;
	private String sDefaultPath = System.getProperty("user.home");
	private String strPath = "";

	/**
	 * This is the default constructor
	 */
	public JDlgProjectCreate(JFrame f) {
		super(f,"Create Project",true);
		this.frame = f;
		this.setResizable(false);
        this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		initialize();
	}
	
	public void setTarget(JPanManageMain p){
		jPanManageMain = p;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.weightx = 0.1;
		gridBagConstraints5.weighty = 0.0;
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints5.gridy = 1;
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 0;
		gridBagConstraints41.weightx = 0.1;
		gridBagConstraints41.anchor = GridBagConstraints.CENTER;
		gridBagConstraints41.insets = new Insets(0, 10, 20, 0);
		gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints41.gridy = 0;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.gridheight = 2;
		gridBagConstraints3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints3.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints3.gridy = 0;
		this.setSize(600, 250);
		this.setLayout(new GridBagLayout());
		this.add(getJPanelButton(), gridBagConstraints3);
		this.add(getJPanelPjtName(), gridBagConstraints41);
		this.add(getJPanelSourceLocation(), gridBagConstraints5);
		
		getJTextFieldSourceLocation().setText(strPath);
		getJTextFieldSourceLocation().setToolTipText(strPath);
	}

	/**
	 * This method initializes jPanelPjtName	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelPjtName() {
		if (jPanelPjtName == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints.ipadx = 4;
			gridBagConstraints.gridy = 0;
			jLabelNewProjectName = new JLabel();
			jLabelNewProjectName.setText("New Project Name :");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.CENTER;
			gridBagConstraints7.insets = new Insets(10, 10, 10, 0);
			gridBagConstraints7.gridwidth = 1;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 0.1;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.ipadx = 0;
			gridBagConstraints6.insets = new Insets(10, 10, 10, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.insets = new Insets(10, 10, 0, 10);
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 0.1;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints1.gridx = 1;
			jPanelPjtName = new JPanel();
			jPanelPjtName.setLayout(new GridBagLayout());
			jPanelPjtName.setPreferredSize(new Dimension(500, 200));
			jPanelPjtName.setBorder(BorderFactory.createTitledBorder(null, "Project Name", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelPjtName.add(getJTextFieldNewProjectName(), gridBagConstraints1);
			jPanelPjtName.add(getJButtonCheck(), gridBagConstraints2);
			jPanelPjtName.add(getJCheckBoxClonedFrom(), gridBagConstraints6);
			jPanelPjtName.add(getJComboBoxClonedFrom(), gridBagConstraints7);
			jPanelPjtName.add(jLabelNewProjectName, gridBagConstraints);
		}
		return jPanelPjtName;
	}

	/**
	 * This method initializes jPanelSourceLocation	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelSourceLocation() {
		if (jPanelSourceLocation == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.BOTH;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new Insets(10, 10, 10, 0);
			gridBagConstraints10.anchor = GridBagConstraints.CENTER;
			gridBagConstraints10.gridx = 0;
			jPanelSourceLocation = new JPanel();
			jPanelSourceLocation.setLayout(new GridBagLayout());
			jPanelSourceLocation.setBorder(BorderFactory.createTitledBorder(null, "Source Location", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelSourceLocation.setPreferredSize(new Dimension(500, 200));
			jPanelSourceLocation.add(getJTextFieldSourceLocation(), gridBagConstraints10);
			jPanelSourceLocation.add(getJButtonBrowse(), gridBagConstraints11);
		}
		return jPanelSourceLocation;
	}
	
	private JTextField getJTextFieldSourceLocation() {
		if (jTextFieldSourceLocation == null) {
			jTextFieldSourceLocation = new JTextField();
			jTextFieldSourceLocation.setPreferredSize(new Dimension(200, 22));
			jTextFieldSourceLocation.setEditable(false);
		}
		return jTextFieldSourceLocation;
	}
	
	/**
	 * This method initializes jButtonBrowse	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonBrowse() {
		if (jButtonBrowse == null) {
			jButtonBrowse = new JButton();
			jButtonBrowse.setText("Browse");
			jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jButtonBrowse.actionPerformed()"); 
					eventHandler.handleEvent(EventHandler.BTN_BROWSE);
				}
			});
		}
		return jButtonBrowse;
	}

	private JPanel getJPanelButton() {
		if (jPanelButton == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.insets = new Insets(0, 10, 10, 10);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 10, 10, 10);
			gridBagConstraints4.gridy = 1;
			jPanelButton = new JPanel();
			jPanelButton.setLayout(new GridBagLayout());
			jPanelButton.add(getJButtonOK(), gridBagConstraints12);
			jPanelButton.add(getJButtonCancel(), gridBagConstraints4);
		}
		return jPanelButton;
	}

	
	/**
	 * This method initializes jTextFieldNewProjectName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldNewProjectName() {
		if (jTextFieldNewProjectName == null) {
			jTextFieldNewProjectName = new JTextField();
			jTextFieldNewProjectName.setText(ProjectNamePolicy.getProjectNamePrefix());
			jTextFieldNewProjectName.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					setButtonState();
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						eventHandler.handleEvent(EventHandler.BTN_CHECK);
					} else {
						jTextFieldNewProjectName.setToolTipText(jTextFieldNewProjectName.getText());
					}
				}
			});
		}
		return jTextFieldNewProjectName;
	}
	
	private void setButtonState() {
		int length = jTextFieldNewProjectName.getText().length();
		if(length > 0) {
			getJButtonCheck().setEnabled(true);
			getJButtonOK().setEnabled(false);
		} else {
			getJButtonCheck().setEnabled(false);
			getJButtonOK().setEnabled(false);
		}
	}

	/**
	 * This method initializes jButtonCheck	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCheck() {
		if (jButtonCheck == null) {
			jButtonCheck = new JButton();
			jButtonCheck.setText("Check");
			jButtonCheck.setEnabled(false);
			jButtonCheck.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jButtonCheck.actionPerformed()");
					eventHandler.handleEvent(EventHandler.BTN_CHECK);
				}
			});
		}
		return jButtonCheck;
	}

	
	
	/**
	 * This method initializes jCheckBoxClonedFrom	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBoxClonedFrom() {
		if (jCheckBoxClonedFrom == null) {
			jCheckBoxClonedFrom = new JCheckBox();
			jCheckBoxClonedFrom.setText("Cloned From:");
			jCheckBoxClonedFrom.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jCheckBoxClonedFrom.actionPerformed()");
					eventHandler.handleEvent(EventHandler.OPT_CLONED_FROM);
				}
			});
		}
		return jCheckBoxClonedFrom;
	}

	/**
	 * This method initializes jComboBoxClonedFrom	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxClonedFrom() {
		if (jComboBoxClonedFrom == null) {
			jComboBoxClonedFrom = new JComboBox<String>();
			jComboBoxClonedFrom.setRenderer(new ComboToopTip());
			jComboBoxClonedFrom.setEditable(true);
			jComboBoxClonedFrom.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jComboBoxClonedFrom.actionPerformed()"); 
					eventHandler.handleEvent(EventHandler.COMBO_CLONED_FROM);
				}
			});
			refresh(jComboBoxClonedFrom);
			
			final JTextField editor;
			editor = (JTextField)jComboBoxClonedFrom.getEditor().getEditorComponent();
	        editor.addKeyListener(new KeyAdapter() {
	    	    public void keyReleased(KeyEvent e)
	    	    {
	    	        char ch = e.getKeyChar();
	    	        
	    	        if(ch != KeyEvent.VK_ENTER && ch != KeyEvent.VK_BACK_SPACE && (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)))
	    	            return;
	    	        if(ch == KeyEvent.VK_ENTER) {
	    	        	jComboBoxClonedFrom.hidePopup();
	    		        return;
	    	        }
	    	        
	    	        String str = editor.getText();
	        
	        		if(jComboBoxClonedFrom.getComponentCount() > 0){
	        			jComboBoxClonedFrom.removeAllItems();
	            	}

	        		jComboBoxClonedFrom.addItem(str);
	        		try {
	    	        	String tmpProjectName = null;
	        	        if(str.length() > 0){ 
	        	        	for(int i=0;i<names.size();i++) {
	        	        		tmpProjectName = names.get(i);
	        	        		if(tmpProjectName.toLowerCase().startsWith(str.toLowerCase())) jComboBoxClonedFrom.addItem(tmpProjectName);
	        	        	}
	        	        } else {
	        	        	for(int i=0;i<names.size();i++) {
	        	        		jComboBoxClonedFrom.addItem(names.get(i));
	        	        	}
	        	        }
	    			} catch (Exception e1) {
	    				log.warn(e1.getMessage());
	    			}
	        		
	    			jComboBoxClonedFrom.hidePopup();
	    	        if(str.length() > 0) jComboBoxClonedFrom.showPopup();
	    	    }
			});
	        
	        editor.addFocusListener(new FocusAdapter() {
	        	public void focusGained(FocusEvent e) {
	        		if(editor.getText().length() > 0) jComboBoxClonedFrom.showPopup();
	        	}
	        	public void focusLost(FocusEvent e) {
	        		jComboBoxClonedFrom.hidePopup();
	        	}
	        });
		}
		return jComboBoxClonedFrom;
	}

	class ComboToopTip extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5152705354251799364L;
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JComponent comp = (JComponent)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null) {
				comp.setToolTipText(String.valueOf(value));
			} else {
				comp.setToolTipText(null);
			}
			return comp;
		}
	}

	/**
	 * This method initializes jButtonOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setEnabled(false);
			jButtonOK.setText("   OK   ");
			jButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jButtonOK.actionPerformed()");
					eventHandler.handleEvent(EventHandler.BTN_OK);
				}
			});
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("jButtonCancel.actionPerformed()");
					eventHandler.handleEvent(EventHandler.BTN_CANCEL);
				}
			});
		}
		return jButtonCancel;
	}

	ArrayList<String> names = UISDKInterfaceManager.getSDKInterface().getProjectNames(null);
	private void refresh(JComboBox<String> jComboBoxClonedFrom) {
		jComboBoxClonedFrom.removeAllItems();
		
		if(names != null) {
			jComboBoxClonedFrom.addItem("");
			for(String name:names) {
				jComboBoxClonedFrom.addItem(name);
			}
		}
		((JTextField)jComboBoxClonedFrom.getEditor().getEditorComponent()).setText("");
	}
	
	private void checkProjectName(String projectName) {
		projectName = projectName.trim();
		
		boolean isPossibleToInsert = true;

		log.debug("-------checkProjectName.projectName----"+projectName+":--");
		if (projectName.length() == 0) {
			JOptionPane.showMessageDialog(
					null,
					"Input Projecct Name!",
					"ProjectName",
					JOptionPane.ERROR_MESSAGE);
			
			isPossibleToInsert = false;
		}
		else{

			PolicyCheckResult result = ProjectNamePolicy.checkProjectName(projectName);
			if(result.getResult() != PolicyCheckResult.PROJECT_NAME_OK) {
				JOptionPane.showMessageDialog(
						null, 
						result.getResultMsg(), 
						"Invalid project name", 
						JOptionPane.ERROR_MESSAGE);
				
				isPossibleToInsert = false;
			}
			else if(UISDKInterfaceManager.getSDKInterface().isExistedProjectName(projectName, null) == true) {
				JOptionPane.showMessageDialog(
						null, 
						"\""+projectName+"\" is already exists on the Protex Server.\nYou should use other name.", 
						"Duplicated ProjectName", 
						JOptionPane.ERROR_MESSAGE);
				
				isPossibleToInsert = false;
			}
		
		}
		
		if(isPossibleToInsert == true) {
			JOptionPane.showMessageDialog(
					null, 
					"You can use the name of \""+projectName+"\".", 
					"ProjectName", 
					JOptionPane.INFORMATION_MESSAGE);
			
			jButtonCheck.setEnabled(false);
			jButtonOK.setEnabled(true);
			jButtonBrowse.requestFocusInWindow();
			
		}
		else {
			jButtonCheck.setEnabled(true);
			jButtonOK.setEnabled(false);
			jTextFieldNewProjectName.setSelectionStart(0);
			jTextFieldNewProjectName.setSelectionEnd(jTextFieldNewProjectName.getText().length());
			jLabelNewProjectName.requestFocusInWindow();

		}
	}

	EventHandler eventHandler = new EventHandler();
	class EventHandler {
		public static final int BTN_CHECK = 1;
		public static final int BTN_OK = 2;
		public static final int BTN_CANCEL = 3;
		public static final int OPT_CLONED_FROM = 4;
		public static final int COMBO_CLONED_FROM = 5;
		public static final int BTN_BROWSE = 8;
		
		public void handleEvent(int eventCode) {
			jTextFieldNewProjectName.setText(jTextFieldNewProjectName.getText().trim());
			String projectName = jTextFieldNewProjectName.getText();
			
			switch(eventCode) {			
				case BTN_CHECK :
					
					jTextFieldNewProjectName.setToolTipText(projectName);
					checkProjectName(projectName);
					break;					
					
				case BTN_OK :
					
					String rootLocation = getJTextFieldSourceLocation().getText();
					createCloneProject(rootLocation, projectName);
					break;
					
				case BTN_CANCEL :
					getJCheckBoxClonedFrom().setSelected(false);
					getJTextFieldNewProjectName().setText("");
					getJTextFieldNewProjectName().setToolTipText("");
					
					setVisible(false);
					break;
					
				case OPT_CLONED_FROM :
					String projectNameOrg = null;
					if(getJComboBoxClonedFrom().getItemCount() < 1) return;
					
					if(getJCheckBoxClonedFrom().isSelected() == true) {
						String clonedProjectName = getJComboBoxClonedFrom().getSelectedItem().toString();
						OSIProjectInfo prjInfo = OSIProjectInfoMgr.getInstance().getProjectInfo(clonedProjectName);
						if(prjInfo == null) {
							JOptionPane.showMessageDialog(null, 
									""+"\"" + clonedProjectName + "\" is a project that does not exist."
									);
							jCheckBoxClonedFrom.setSelected(false);
							jTextFieldNewProjectName.setText("");							
							return;
						}
						String projectNamePrefix = clonedProjectName+"_Cloned";
						int i=1;
						projectNameOrg = projectNamePrefix;
						while(UISDKInterfaceManager.getSDKInterface().isExistedProjectName(projectNameOrg, null)) {
							projectNameOrg = projectNamePrefix+i++;
						}
						
						getJTextFieldNewProjectName().setText(projectNameOrg);
						getJTextFieldNewProjectName().setToolTipText(projectNameOrg);
						getJTextFieldSourceLocation().setText(prjInfo.getSourcePath());
						getJTextFieldSourceLocation().setToolTipText(prjInfo.getSourcePath());
						jButtonCheck.setEnabled(false);
						jButtonOK.setEnabled(true);
					} 
					else {
						jButtonCheck.setEnabled(true);
						jButtonOK.setEnabled(false);
						jTextFieldNewProjectName.setSelectionStart(0);
						jTextFieldNewProjectName.setSelectionEnd(jTextFieldNewProjectName.getText().length());
						jTextFieldNewProjectName.requestFocusInWindow();
						getJTextFieldSourceLocation().setText("");
						getJTextFieldSourceLocation().setToolTipText("");
					}
					break;
					
					
				case COMBO_CLONED_FROM :
					if(getJComboBoxClonedFrom().getSelectedItem() == null)
						return;
					String clonedProjectName = getJComboBoxClonedFrom().getSelectedItem().toString();
					jComboBoxClonedFrom.setToolTipText(clonedProjectName);
					OSIProjectInfo prjInfo = OSIProjectInfoMgr.getInstance().getProjectInfo(clonedProjectName);
					if(prjInfo != null) {
						getJTextFieldSourceLocation().setText(prjInfo.getSourcePath());
						getJTextFieldSourceLocation().setToolTipText(prjInfo.getSourcePath());
					}
					break;
				
				case BTN_BROWSE:
					JFCFolderExplorer explorer = JFCFolderExplorer.getInstance();
					String sFileLoc = getJTextFieldSourceLocation().getText();
					JFileChooser chooser = explorer.getJFileChooser();
					
					if( (sFileLoc == null) || (sFileLoc.length() == 0) ){
						chooser.setCurrentDirectory(new java.io.File(sDefaultPath));
					}else{
						chooser.setCurrentDirectory(new java.io.File(sFileLoc));
					}
					int result = explorer.showBrowser(frame);
					if(result == JFileChooser.APPROVE_OPTION){
						strPath = chooser.getSelectedFile().getAbsolutePath();
						getJTextFieldSourceLocation().setText(strPath);
						getJTextFieldSourceLocation().setToolTipText(strPath);
					}
					break;

			}
		}

	}

	@SuppressWarnings("unchecked")
	private void createCloneProject(String rootLocation, String projectName) {
		UIResponseObserver observer = null;
		if(getJCheckBoxClonedFrom().isSelected() == true) { // clone
			UEProjectClone uProjectClone = null;
			String clonedFromProjectName = getJComboBoxClonedFrom().getSelectedItem().toString();
			uProjectClone = new UEProjectClone(projectName, clonedFromProjectName, rootLocation, jPanManageMain);
			observer = UserRequestHandler.getInstance().handle(
					UserRequestHandler.PROJECT_CLONE, 
					uProjectClone, 
					true,	// progress
					false	// result
					);
			
		} else { // create
			TreeMap<String, ProjectSplitInfo> mapOfAnalyzeTarget = null;
			UEProjectCreate ueProjectCreate = null;
			
			if(rootLocation.length() == 0) { // one creation, no source path
				
				ueProjectCreate = new UEProjectCreate(projectName, mapOfAnalyzeTarget);
				observer = UserRequestHandler.getInstance().handle(
						UserRequestHandler.PROJECT_CREATE, 
						ueProjectCreate, 
						true,	// progress
						false	// result
						);
				
			} else { // split or no split
				
				File rootLocationFile = new File(rootLocation);
				ProjectSplitUtil projectSplitUtil = new ProjectSplitUtil(projectName, rootLocation);
				UEProtexSplit ue = new UEProtexSplit(rootLocationFile, projectSplitUtil);
				observer = UserRequestHandler.getInstance().handle(
						UserRequestHandler.PROJECT_SPLIT, 
						ue, 
						true, 
						false);
				if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS ) {
					mapOfAnalyzeTarget = ProjectSplitUtil.getAnalyzeTargetMap();
				} else {
					return;
				}

				int size = mapOfAnalyzeTarget.size();
				if(size > 1) {
					JDlgProjectSplitInfo.getInstance().showDialog();
					boolean isWorking = JDlgProjectSplitInfo.getInstance().isWorking();
	
					if(isWorking == false) {
						return;
					}
				}
					
				ueProjectCreate = new UEProjectCreate(projectName, mapOfAnalyzeTarget);
				observer = UserRequestHandler.getInstance().handle(
						UserRequestHandler.PROJECT_CREATE, 
						ueProjectCreate, 
						true,	// progress
						false	// result
				);
			}
		}
		
		if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS) {
			
			ArrayList<OSIProjectInfo> osiProjectInfoList = (ArrayList<OSIProjectInfo>) observer.getReturnValue();
			for(OSIProjectInfo pf : osiProjectInfoList) {
				jPanManageMain.addNewProject(pf);
			}
			
			jTextFieldNewProjectName.setText("");
			jTextFieldNewProjectName.setToolTipText("");
			jCheckBoxClonedFrom.setSelected(false);
			setVisible(false);
		} else {
			jTextFieldNewProjectName.setSelectionStart(0);
			jTextFieldNewProjectName.setSelectionEnd(jTextFieldNewProjectName.getText().length());
			jTextFieldNewProjectName.requestFocusInWindow();
		}
		
	}
	
} 
