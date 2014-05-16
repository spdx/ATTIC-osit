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
package com.sec.ose.osi.ui;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * UISharedData
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class UISharedData {

	private volatile static UISharedData instance = null;
	
	private UISharedData(){}

	public static UISharedData getInstance() {
		if(instance == null) {
			synchronized(UISharedData.class) {
				if(instance == null) {
					instance = new UISharedData();
				}
			}
		}
		return instance;
	}

	private JFrame curFrame;
	private JTextField jTextFieldIdentifyQueueStatusBar;
	
	
	private String mUserID = null;
	private String mServerIP = null;
	private String mReportTemplateName = null;

	public String getReportTemplateName() {
		return mReportTemplateName;
	}

	public void setReportTemplateName(String reportTemplateName) {
		mReportTemplateName = reportTemplateName;
	}

	public String getConnectionInfo() {
		return mUserID + "@" + mServerIP;
	}

	public void setConnectionInfo(String userID, String serverIP) {
		mUserID = userID;
		mServerIP = serverIP;
	}

	public JFrame getCurrentFrame() {
		return curFrame;
	}
	public void setCurrentFrame(JFrame curFrame) {
		this.curFrame = curFrame;
	}

	public String getUserID() {
		return this.mUserID;
	}

	public JTextField getjTextFieldIdentifyQueueStatusBar() {
		return jTextFieldIdentifyQueueStatusBar;
	}

	public void setjTextFieldIdentifyQueueStatusBar(JTextField jTextFieldStatusBar) {
		this.jTextFieldIdentifyQueueStatusBar = jTextFieldStatusBar;
	}

}
