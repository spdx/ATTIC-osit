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
package com.sec.ose.osi.thread.ui_related;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.dialog.ProgressDisplayer;

/**
 * UserCommandExecutionMonitorThread
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class UserCommandExecutionMonitorThread extends SwingWorker<Object, Object> {
	private static Log log = LogFactory.getLog(UserCommandExecutionMonitorThread.class);
	
	private static final long TIME_INTERVAL = 25;
	
	private ProgressDisplayer mProgressDisplayer;
	private UIResponseObserver mObserver;
	
	private long startTime;
	private boolean isContinue = true;
	
	public UserCommandExecutionMonitorThread(ProgressDisplayer dlgProgress, UIResponseObserver pObserver) {
		super();
		mProgressDisplayer = dlgProgress;
		mObserver = pObserver;
	}

	long cnt=0;
	final long PRINT_TERM = 500 / TIME_INTERVAL;
	
	@Override
	protected Object doInBackground() throws Exception {

		String message = "";
		startTime = System.currentTimeMillis();
		
		while(isContinue == true) {
			
			// 0. precondition checking
			if(mObserver == null ||	mProgressDisplayer == null )
				break;
			
			
			// 1. get message from observer
			// 2. calculate elapsed time 
			cnt++;
			if(mObserver.hasMoreMessage()) {
				message = mObserver.popMessage();
				
				this.mProgressDisplayer.setProgressMessage(message);
			}

			long elapsedTime = (System.currentTimeMillis() - startTime);
			
			// 3. make message displayed at JDlgProgress 
			
			
			this.mProgressDisplayer.setElapsedTime(elapsedTime);
			
			// 4. sleep
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch(InterruptedException e) {
				log.warn(e);
			}
			

		}
		return null;
	}
	
	@Override
	protected void done() {
		super.done();
		
		while(mObserver.hasMoreMessage()) {
			String message = mObserver.popMessage();
			this.mProgressDisplayer.setProgressMessage(message);
		}
		
		if(this.mProgressDisplayer != null) {
			this.mProgressDisplayer.close();
			this.mProgressDisplayer = null;
		}
	}
	
	public void setIsContinue(boolean pIsContinue) {
		this.isContinue = pIsContinue;
	}
}
