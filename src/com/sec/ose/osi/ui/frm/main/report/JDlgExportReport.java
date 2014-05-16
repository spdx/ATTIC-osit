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
package com.sec.ose.osi.ui.frm.main.report;

import com.sec.ose.osi.ui.dialog.DialogTemplate;

/**
 * JDlgExportReport
 * @author sjh.yoo
 * 
 */
public class JDlgExportReport {

	private volatile static JDlgExportReport instance = null;
	private DialogTemplate dialog;
	private JPanExportReport panel;

	public static JDlgExportReport getInstance() {
		if(instance == null) {
			synchronized(JDlgExportReport.class) {
				if(instance == null) {
					instance = new JDlgExportReport();
				}
			}
		}
		return instance;
	}
	
	private JDlgExportReport() {
		
		panel = new JPanExportReport();
		dialog = 
			new DialogTemplate(
					"Generate Report",
					panel,
					440,400
					
					);
		panel.setParent(this);
		
		
	}
	
	public void setVisible(boolean pVisible) {
		dialog.setVisible(pVisible);
	}
}
