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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;

/**
 * JComboLicenseName
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JComboLicenseName extends JComboBox<String>{
	private static Log log = LogFactory.getLog(JComboLicenseName.class);
	
	private static final long serialVersionUID = 1L;
	
	public JComboLicenseName() {
		final JTextField editor;
		this.initLicenseComboBox();

		this.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(isEnabled()) {
					if(e.getActionCommand().equals("comboBoxChanged") || e.getActionCommand().equals("comboBoxEdited")){
						if(getSelectedIndex() > 0){
							setSelectedIndex(getSelectedIndex());
							IdentifyMediator.getInstance().setSelectedLicenseName(String.valueOf(getSelectedItem()));
							log.debug("selected license name : " + IdentifyMediator.getInstance().getSelectedLicenseName());
						} else {
							IdentifyMediator.getInstance().setSelectedLicenseName("");
						}
					}
				}
			}
		});
		editor = (JTextField)this.getEditor().getEditorComponent();
		editor.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e)
			{
				char ch = e.getKeyChar();

				if(ch != KeyEvent.VK_ENTER && ch != KeyEvent.VK_BACK_SPACE && (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)))
					return;
				if(ch == KeyEvent.VK_ENTER) {
					hidePopup();
					return;
				}

				String str = editor.getText();

				if(getComponentCount() > 0){
					removeAllItems();
				}

				addItem(str);
				try {
					String tmpLicense = null;
					ArrayList<String> AllLicenseList = new ArrayList<String>();
					AllLicenseList = LicenseAPIWrapper.getAllLicenseList();
					if(str.length() > 0){ 
						for(int i=0;i<AllLicenseList.size();i++) {
							tmpLicense = AllLicenseList.get(i);
							if(tmpLicense.toLowerCase().startsWith(str.toLowerCase())) addItem(tmpLicense);
						}
					} else {
						for(int i=0;i<AllLicenseList.size();i++) {
							addItem(AllLicenseList.get(i));
						}
					}
				} catch (Exception e1) {
					log.warn(e1.getMessage());
				}

				hidePopup();
				if(str.length() > 0) showPopup();
			}
		});

		editor.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if(editor.getText().length() > 0) showPopup();
			}
			public void focusLost(FocusEvent e) {
				hidePopup();
			}
		});
	}
	
	public void initLicenseComboBox() {
		if(this.getComponentCount() > 0){
			this.removeAllItems();
		}
		this.addItem("");
		ArrayList<String> AllLicenseList = LicenseAPIWrapper.getAllLicenseList();
		if(AllLicenseList != null) {
			for(int i=0;i<AllLicenseList.size();i++) {
				this.addItem(AllLicenseList.get(i));
			}
		}
		((JTextField)this.getEditor().getEditorComponent()).setText("");
	}

	public void setLicenseComboBox(String pLicenseName) {
		setSelectedItem(pLicenseName);
	}
	
	public void setLicenseComboBox(int index) {
		setSelectedIndex(index);
	}

	public String getCurrentLicenseName() {

		String licenseName = "";
		if(getSelectedItem() != null) {			
			licenseName = getSelectedItem().toString();
		}
		return licenseName;
		
	}

	public void activateLicenseCombo() {
		this.setEnabled(true);
	}
}
