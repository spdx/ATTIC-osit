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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.manage.ManageMediator;

/**
 * JComboProjectName
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JComboProjectName extends JComboBox<String> {
	private static Log log = LogFactory.getLog(JComboProjectName.class);
	
	private final String DIVIDER_LINE = "-----------------------------------------------------------------"; 
	private String preProjectName="";

	private static final long serialVersionUID = 1L;

	// Comment & Confirm
	private String[] buttonOK = {"OK"};

	public JComboProjectName() {
		
		this.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE); 
		this.setEditable(true);
		this.setPreferredSize(new Dimension(400, 27));
		this.addActionListener( 
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						long start = System.currentTimeMillis();
						actionOnProjectSelectedonProjectComboBox();
						long end = System.currentTimeMillis();
						log.debug("[JComboProjectName.addActionListener()] Project Loding TIME : " + (end - start)/1000.0);
					}
				}
		);
		
		final JTextField editor = (JTextField)this.getEditor().getEditorComponent();
		editor.addKeyListener(new KeyAdapter() {
			
			public void keyReleased(KeyEvent e) {
				
				char ch = e.getKeyChar();
				int count = 0;
				String newProjectName = editor.getText();
				
				if(ch != KeyEvent.VK_ENTER && ch != KeyEvent.VK_BACK_SPACE && (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch))) {
					return;
				}
				
				if(newProjectName.length() <=0 && ch == KeyEvent.VK_BACK_SPACE) {
					count++;
					if(count>=2){
						String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
						((JTextField)JComboProjectName.this.getEditor().getEditorComponent()).setText(projectName);
						count = 0;
						return;
					}
				}
				if(JComboProjectName.this.getComponentCount() > 0){
					JComboProjectName.this.removeAllItems();
            	}
				
				JComboProjectName.this.addItem(newProjectName);
        		try {
        			Collection<OSIProjectInfo> ProjectsInfo = OSIProjectInfoMgr.getInstance().getAllProjects();
        			
    	        	String tmpProName = null;
    	        	boolean bAnalysis = false;
        	        if(newProjectName.length() > 0){ 
        	        	for(OSIProjectInfo projectInfo: ProjectsInfo) {
        	        		tmpProName = projectInfo.getProjectName();
        	        		bAnalysis = projectInfo.isAnalyzed();
        	        		if(tmpProName.toLowerCase().contains(newProjectName.toLowerCase()) 
        	        				&& bAnalysis) JComboProjectName.this.addItem(tmpProName);
        	        	}
        	        	if(JComboProjectName.this.getItemCount() <= 1) { 
        	        		JComboProjectName.this.removeAllItems();
        	        		JOptionPane.showOptionDialog(
        	        				null, 
        	        				"There is no project.", 
        	        				"Project Filter", 
        	        				JOptionPane.OK_OPTION, 
        	        				JOptionPane.ERROR_MESSAGE, 
        	        				null, 
        	        				buttonOK, 
        	        				"OK");
        	        	}
        	        } else {
        	        	for(OSIProjectInfo projectInfo: ProjectsInfo) {
        	    			if (projectInfo.isManaged() == true && projectInfo.isAnalyzed()){
        	    				JComboProjectName.this.addItem(projectInfo.getProjectName());
        	    			}
        	    		}
        	        	JComboProjectName.this.addItem(DIVIDER_LINE);
        	    		for(OSIProjectInfo projectInfo: ProjectsInfo) {
        	    			if (projectInfo.isManaged() == false && projectInfo.isAnalyzed()){
        	    				JComboProjectName.this.addItem(projectInfo.getProjectName());
        	    			}
        	    		}	
        	    		((JTextField)JComboProjectName.this.getEditor().getEditorComponent()).setText("");
        	        }
    			} catch (Exception e1) {
    				log.warn(e1.getMessage());
    			}
        		
    			JComboProjectName.this.hidePopup();
    	        if(newProjectName.length() > 0) JComboProjectName.this.showPopup();
			}	
		});
	
	}

	private void actionOnProjectSelectedonProjectComboBox() {

		String projectName = getSelectedProjectName();
		((JTextField)this.getEditor().getEditorComponent()).setText(projectName);  

		if ( (this.getSelectedItem() == null) || 
			 (this.getSelectedItem().toString().length() == 0) || 
			 (this.getSelectedItem().equals(DIVIDER_LINE)) ) {
			return;
		}

		projectName = this.getSelectedItem().toString();
		OSIProjectInfo xOSIProjectInfo = OSIProjectInfoMgr.getInstance().getProjectInfo(projectName);
		
		if (xOSIProjectInfo == null ||	preProjectName.equals(projectName))
			return;
		
		// Identification Data Loading
		boolean result = loadControllerssNCodeTreeForProject(projectName);
		
		if(result==false) {

			this.setSelectedItem(preProjectName);
			return;
		}
		
		ManageMediator.getInstance().addManagedProject(xOSIProjectInfo);
		
		IdentifyMediator.getInstance().setEnabledJMenuItemSynchFromServer();
		IdentifyMediator.getInstance().setEnabledJMenuItemSPDXAutoIdentify();
		IdentifyMediator.getInstance().setEnabledAutoIdentifyButton();
		
		
		refreshProjectComboBox();
		

		
		preProjectName = projectName;
		
	}

	public void refreshComboBox(boolean force) {
		if(force) {
			preProjectName = "";
		}
		refreshProjectComboBox();
	}
	
	public void refreshComboBox(String pProjectName) {
		
		String projectName = getSelectedProjectName();
		log.debug("projectName : "+projectName+" , pProjectName : "+pProjectName);
		if(projectName.equals(pProjectName)) {
			preProjectName = "";
		}
		refreshProjectComboBox();
	}

	String getSelectedProjectName() {
		if(this.getSelectedItem() != null)
			return this.getSelectedItem().toString();
		return "";
	}
	
	public void refreshProjectComboBox() {
		
		String projectName = getSelectedProjectName();
		
		this.removeAllItems();
		Collection<OSIProjectInfo> ProjectsInfo = OSIProjectInfoMgr.getInstance().getAllProjects();
		this.addItem("");
		for(OSIProjectInfo projectInfo:ProjectsInfo){
			if (projectInfo.isManaged() == true && projectInfo.isAnalyzed()){
				this.addItem(projectInfo.getProjectName());
			}
		}
		this.addItem(DIVIDER_LINE);
		for(OSIProjectInfo projectInfo:ProjectsInfo){
			if (projectInfo.isManaged() == false && projectInfo.isAnalyzed()){
				this.addItem(projectInfo.getProjectName());
			}
		}	
		
		if( (projectName.length() != 0) && (OSIProjectInfoMgr.getInstance().getProjectInfo(projectName) != null) ) {
			this.setSelectedItem(projectName);	
		} else {
			this.setSelectedIndex(0);

		}
	}

	private boolean loadControllerssNCodeTreeForProject(String projectName) {
		
		// Identification Data Loading
		UEComboProjectName uIdentificationData = new UEComboProjectName(projectName);
		UIResponseObserver observer = UserRequestHandler.getInstance().handle(
				UserRequestHandler.LOAD_IDENTIFICATION_DATA, 
				uIdentificationData, 
				true,	// progress
				false	// result
				);
		
		if(observer.getResult() == UIResponseObserver.RESULT_SUCCESS) {
			log.debug("Identification Data Loading Success!!");
			return true;
		}
		
		else 
			return false;
	}

	
}
