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
 * JDlgIdentifyResult
 * @author sjh.yoo, ytaek.kim, jae-yong.lee
 * 
 */
public class JDlgIdentifyResult {

	private volatile static JDlgIdentifyResult instance = null;
	private DialogTemplate dialog;
	private JPanIdentifyResult panel;

	public static JDlgIdentifyResult getInstance() {
		if(instance == null) {
			synchronized(JDlgIdentifyResult.class) {
				if(instance == null) {
					instance = new JDlgIdentifyResult();
				}
			}
		}
		return instance;
	}

	private JDlgIdentifyResult() {
		panel = new JPanIdentifyResult();
		dialog = 
			new DialogTemplate(
					"Result of Auto Identify from SPDX",
					panel,
					600,670
					
					);
		panel.setParent(this);
	}
	
	public void setVisible(boolean pVisible) {
		dialog.setVisible(pVisible);
	}
	
	public void setAutoIdentifyResult(SPDXAutoIdentifyResult autoIdentifyResult) {
		panel.setAutoIdentifyResult(autoIdentifyResult);
	}
	
}
