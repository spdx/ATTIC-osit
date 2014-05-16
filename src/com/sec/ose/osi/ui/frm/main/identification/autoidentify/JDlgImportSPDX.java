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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import com.sec.ose.osi.ui.dialog.DialogTemplate;

/**
 * JDlgImportSPDX
 * @author sjh.yoo
 * 
 */
public class JDlgImportSPDX {
	
	private volatile static JDlgImportSPDX instance = null;
	private DialogTemplate dialog;
	private JPanImportSPDX panel;

	public static JDlgImportSPDX getInstance() {
		if(instance == null) {
			synchronized(JDlgImportSPDX.class) {
				if(instance == null) {
					instance = new JDlgImportSPDX();
				}
			}
		}
		return instance;
	}

	private JDlgImportSPDX() {
		panel = new JPanImportSPDX();
		dialog = 
			new DialogTemplate(
					"Auto Identify from SPDX",
					panel,
					450,380
					
					);
		panel.setParent(this);
		
		
	}
	
	public void setVisible(boolean pVisible) {
		dialog.setVisible(pVisible);
	}
	
}