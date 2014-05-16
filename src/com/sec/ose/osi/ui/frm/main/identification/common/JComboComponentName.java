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
package com.sec.ose.osi.ui.frm.main.identification.common;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;

/**
 * JComboComponentName
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JComboComponentName extends JComboBox<String>{
	private static Log log = LogFactory.getLog(JComboComponentName.class);
	
	private static final long serialVersionUID = 1L;
	
	public JComboComponentName() {
		init();	
	}

	public String getCurrentComponentName() {
		if(getSelectedIndex()>0)
			return String.valueOf(getSelectedItem());
		else 
			return "";
	}

	private void init() {

		final JTextField tfComponentName = (JTextField)this.getEditor().getEditorComponent();

		tfComponentName.addKeyListener(
				new KeyAdapter() {
					public void keyReleased(KeyEvent e)	{
						char ch = e.getKeyChar();
						if(ch != KeyEvent.VK_ENTER 
								&& ch != KeyEvent.VK_BACK_SPACE 
								&& ch != KeyEvent.VK_SPACE 
								&& (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)))
							return;
						
						if(ch == KeyEvent.VK_ENTER) {
				
							JComboComponentName.this.hidePopup();
							return;
				}
				// 1. save string
				String keyword = tfComponentName.getText();//.trim();
				updateComboBoxList(keyword);

			}
		});

		tfComponentName.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if(tfComponentName.getText().length() > 0) showPopup();
			}
			public void focusLost(FocusEvent e) {
				hidePopup();
			}
		});


	}
	
	private void updateComboBoxList(String keyword) {
		// 2. Init
		if(getComponentCount() > 0){
			removeAllItems();
		}
		hidePopup();

		// 3. add
		String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(keyword.length() > 0) {
			addItem(keyword);
			for(String tmpComponent:ComponentAPIWrapper.getComponentList(projectID, keyword)) {
				addItem(tmpComponent);
			}
			showPopup();
		}
	}
	
	public void setComponentName(String pComponentName) {
		log.debug("setComponentName() - pComponentName : " + pComponentName);
		JTextField editor = (JTextField)this.getEditor().getEditorComponent();
		if(this != null) {
			editor.setText(pComponentName);
		}
	}
	
	public static void updateComponentName(String strName) {
	}

	public void initComponentComboBox() {
		if(this.getComponentCount() > 0){
			this.removeAllItems();
    	}
		((JTextField)this.getEditor().getEditorComponent()).setText("");
	}

	public void activateComponentCombo() {
		this.initComponentComboBox();
		this.setEnabled(true);

	}

}
