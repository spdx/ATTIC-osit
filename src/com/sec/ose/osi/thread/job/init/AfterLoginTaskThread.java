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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;

/**
 * AfterLoginTaskThread
 * @author sjh.yoo, suhyun47.kim
 * 
 */
public class AfterLoginTaskThread extends Thread {
	private static Log log = LogFactory.getLog(AfterLoginTaskThread.class);
	
	private BeforeLoginTaskThread mBeforeLoginTaskThread;
	
	public AfterLoginTaskThread(BeforeLoginTaskThread pBeforeLoginTaskThread) {
		mBeforeLoginTaskThread = pBeforeLoginTaskThread;
	}
	
	public void run() {
		log.debug("AfterLoginTask: start");
		while(mBeforeLoginTaskThread.getStatus() != BeforeLoginTaskThread.DONE) {
			log.debug("AfterLoginTask: Waiting for BeforeLoginTaskThread complition");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		log.debug("AfterLoginTask: init loadLicense");
		LicenseAPIWrapper.getAllLicenseList();	// from API
		
		log.debug("AfterLoginTask: init IdentifyQueue");
		IdentifyQueue.getInstance();
		
		log.debug("AfterLoginTask: start IdentifyThread");
		BackgroundJobManager.getInstance().startIdentifyThread();
		
		log.debug("AfterLoginTask: end");
		
	}
	
}