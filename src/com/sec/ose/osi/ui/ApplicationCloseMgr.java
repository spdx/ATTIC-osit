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

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.ui.cache.CacheableMgr;

/**
 * ApplicationCloseMgr
 * @author suhyun47.kim, ytaek.kim, hankido.lee
 * 
 */
public class ApplicationCloseMgr {
	private static Log log = LogFactory.getLog(ApplicationCloseMgr.class);
	
	private static ApplicationCloseMgr instance;
	private ApplicationCloseMgr() {}
	
	public static ApplicationCloseMgr getInstance() {
		if(instance == null) {
			synchronized(ApplicationCloseMgr.class) {
				if(instance == null) {
					instance = new ApplicationCloseMgr();
				}
			}
		}
		return instance;
	}

	synchronized public void exit() {
		
		log.debug("exit() - identifyQueueSize: "+IdentifyQueue.getInstance().size());
		
		ComponentAPIWrapper.save();

		if(IdentifyQueue.getInstance().size() <= 0) {
			
			CacheableMgr.getInstance().saveToCache();
			
			UserRequestHandler.getInstance().handle(
					UserRequestHandler.DELETE_IDENTIFICATION_TABLE, 
					null, 
					true,	// progress
					false	// result
					);
			
			log.debug("OSIT EXIT...");
	    	System.exit(0);
		}

		log.debug("show message dialog to confirm exit or not");

		String[] buttonList = {"Yes", "No"};
		int choice = JOptionPane.showOptionDialog(
				null, 
				"Identification Queue is not empty.(size : "+IdentifyQueue.getInstance().size()+")\n" +
				"If you close this application with non-empty queue.\n" +
				"identification process for this queue will start again.\n" +
				"But it's not recommended. (Data loss problem)\n" + 
				"Do you really want to exit now?\n",
				"Exit",
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				buttonList,
				"Yes");
		if(choice == JOptionPane.NO_OPTION) {
			return;	// will not exit. 
		}

		log.debug("user select yes option and create thread");
				
		JDlgExitMessage dlgExitMessage = new JDlgExitMessage();
		String message = "OSI try to sync with Protex Server.\n" +
		"It takes several minutes to finish.";
		DialogDisplayerThread aDialogDiaplayerThread = new DialogDisplayerThread(message, dlgExitMessage);
		CompleteSendingThread aCompleteSendingThread = new CompleteSendingThread(aDialogDiaplayerThread);
				
		log.debug("Thread start");

		aDialogDiaplayerThread.execute();

		aCompleteSendingThread.start();

		dlgExitMessage.setVisible(true); // block
		
		CacheableMgr.getInstance().saveToCache();
		
		log.debug("OSIT EXIT...");
    	System.exit(0);
	}
}

/**
 * CompleteSendingThread
 * @author suhyun47.kim, ytaek.kim, hankido.lee
 * 
 */
class CompleteSendingThread extends Thread {
	private static Log log = LogFactory.getLog(CompleteSendingThread.class);

	private static final long TIME_LIMIT = 30 * 1000;		// 30 seconds 
	
	DialogDisplayerThread aDialogDiaplayerThread = null;
	
	public CompleteSendingThread(DialogDisplayerThread aDialogDiaplayerThread) {
		this.aDialogDiaplayerThread = aDialogDiaplayerThread;
	}

	public void run() {

		log.debug("run() start");

		boolean loop = true;
	
		int queueSize = IdentifyQueue.getInstance().size();
		log.info("Trying to Sending item to Protex Server - Identify Queue Size: "+queueSize);			
		
		long startTime = System.currentTimeMillis();
		while(loop) { // block
			
			BackgroundJobManager.getInstance().requestStopIdentifyThread();
			
			
			long endTime = System.currentTimeMillis();
			long timeDuration = endTime - startTime;

			if(timeDuration % 100 == 0)
				System.out.println("delayTime : " + timeDuration);
				
			if(timeDuration >= TIME_LIMIT) {
				log.error("TIME_LIMIT_EXCEED during completing sending items to Protex server ");
				loop = false;
				aDialogDiaplayerThread.closeDialog();
				String exitMessage = "OSI fails to sync with Protex Server.\n" +
				"Please contact to OSI Development Team to resolve this problem.";
				String[] button = {"OK"};
				JOptionPane.showOptionDialog( // block
						null, 
						exitMessage,
						"Exit",
						JOptionPane.YES_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						button,
						"OK");
				continue;
				
			} else {
			
				boolean isAllidentifyThreadStopped = BackgroundJobManager.getInstance().isAllIdentifyThreadReadyStatus();
				if(isAllidentifyThreadStopped) {
					queueSize = IdentifyQueue.getInstance().size();
					log.info("OSI succeeds to sync with Protex Server. - Identify Queue Size: "+queueSize+" / "+timeDuration+" ms.");		
					loop = false;
					aDialogDiaplayerThread.closeDialog();
				}
				
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		log.debug("run() end");
	}
}

/**
 * DialogDisplayerThread
 * @author suhyun47.kim, ytaek.kim, hankido.lee
 * 
 */
class DialogDisplayerThread extends SwingWorker<Object, Object> {
	private static Log log = LogFactory.getLog(DialogDisplayerThread.class);
	
	String message;
	long startTime;
	boolean isContinue = true;
	JDlgExitMessage dlgExitMessage = null;
	private static final long TIME_INTERVAL = 25;
	
	public DialogDisplayerThread(String message, JDlgExitMessage dlgExitMessage) {
		this.message = message;
		this.dlgExitMessage = dlgExitMessage;
	}
	
	public void updateTimeElapsed() {
		long timeDuration = (System.currentTimeMillis() - startTime) / 1000;
		dlgExitMessage.updateTimeElapsed(timeDuration);
	}

	public Object doInBackground() {
		
		startTime = System.currentTimeMillis();
		log.info("run() - start: show dialog");			

		while(isContinue) { // block
			dlgExitMessage.setMessage(message);
			this.updateTimeElapsed();
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch(InterruptedException e) {
				log.warn(e);
			}
		}
		
		log.info("run() - end: Thread completed");
		return null;
	}

	public void closeDialog() {
		log.info("closeDialog() - request to close");
		this.isContinue = false;
		dlgExitMessage.setVisible(false);
		dlgExitMessage.dispose();
	}
}
