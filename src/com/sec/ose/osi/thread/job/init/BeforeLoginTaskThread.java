/**
 * Copyright(C) 2013-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.thread.job.init;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;

/**
 * BeforeLoginTaskThread
 * @author sjh.yoo, hankido.lee, suhyun47.kim
 * 
 */
public class BeforeLoginTaskThread extends Thread {
	private static Log log = LogFactory.getLog(BeforeLoginTaskThread.class);
	
	public static final int READY 	= 1;
	public static final int RUNNING = 2;
	public static final int DONE 	= 3;
	
	private int status = READY;
	
	public void run() {
		
		status = RUNNING;
		
		log.debug("BeforeLoginTask: start");
		
		log.debug("BeforeLoginTask: init initIdentificationDB");
		connectIdentificationDB();
		
		log.debug("BeforeLoginTask: init Component Information");
		ComponentAPIWrapper.init();
		
		status = DONE;
		log.debug("BeforeLoginTask: end");
		
	}
	
	public synchronized int getStatus() {
		return status;
	}
	
	private void connectIdentificationDB() {
		// DB Connection
		if(!IdentificationDBManager.connectToDB()) {
			JOptionPane.showMessageDialog(
					null, 
					"Can't connect to Identification DB",
					"disable Identification DB",
					JOptionPane.ERROR_MESSAGE
			);
			log.debug("Can't connect to Identification DB");
		}
	}
	
}