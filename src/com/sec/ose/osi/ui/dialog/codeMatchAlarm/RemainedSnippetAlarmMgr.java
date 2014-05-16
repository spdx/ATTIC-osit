/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog.codeMatchAlarm;

import java.util.Collections;
import java.util.TreeMap;

/**
 * RemainedSnippetAlarmMgr
 * @author hankido.lee, suhyun47.kim
 *
 */
public class RemainedSnippetAlarmMgr {
	
	private TreeMap<Integer, RemainedSnippetAlarmInfo> warningMessageMap = new TreeMap<Integer, RemainedSnippetAlarmInfo>(Collections.reverseOrder());
	
	private static RemainedSnippetAlarmMgr instance = null;
	
	private RemainedSnippetAlarmMgr() {}
	
	public static RemainedSnippetAlarmMgr getInstance() {
		if(instance == null) {
			instance = new RemainedSnippetAlarmMgr();
		}
		return instance;
	}
	
	int serialNum=0;
	public void showAlarmMessage(RemainedSnippetAlarmInfo data) {
		
		warningMessageMap.put(++serialNum, data);
		this.showDlg();
	}
	
	private final static int MAX_SHOW_COUNT = 20;

	private StringBuffer buffer = new StringBuffer();
	
	public String makeWarningMessages() {
		buffer.setLength(0);
		buffer.append("<HTML>");
		int showCnt = 0;
		
		for(int num : warningMessageMap.keySet()) {
			
			if(++showCnt > MAX_SHOW_COUNT){
				break;
			}
			
			RemainedSnippetAlarmInfo data = warningMessageMap.get(num);
			
			String time = data.getTime();
			String path = data.getCurFilePath();
			String componentName = data.getNewComponentName();
			
			buffer.append("<HR>");
			buffer.append("#").append(num);
			buffer.append(" (created at ").append(time).append(")");
			buffer.append("<HR>");
			buffer.append("The file <B><FONT color=red>").append(path);
			buffer.append("</FONT></B> still has another matched portion ");
			buffer.append("with other FOSS project after having identified it as ");
			buffer.append(" <B>").append(componentName).append("</B>, ");
			buffer.append("and this portion should be identfied also.<BR>");
			buffer.append("<I><FONT COLOR=BLUE>You can click another codematch snippet whose status is </FONT></I>");
			buffer.append("<B><FONT COLOR=RED> Pending (not Identfied or Rejected)</FONT></B> <I><FONT COLOR=BLUE>in the table,</FONT></I>");
			buffer.append("<I><FONT COLOR=BLUE> then click OK button or use 2nd option. </FONT> </I> <BR><BR>");
		}
		buffer.append("</HTML>");
		
		return buffer.toString();
	}
	
	private RemainedSnippetAlarmDlg refreshTableAlarmDlg = null;

	public void showDlg() {
		if(refreshTableAlarmDlg == null) {
			refreshTableAlarmDlg = new RemainedSnippetAlarmDlg();
		}
		String warningMessages = makeWarningMessages();
		new RemainedSnippetAlarmThread(refreshTableAlarmDlg, warningMessages).showDlg();
	}
	
}
