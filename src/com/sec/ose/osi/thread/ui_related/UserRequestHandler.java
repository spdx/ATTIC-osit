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

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.ui.dialog.ProgressDisplayer;
import com.sec.ose.osi.ui.dialog.progress.ProgressDictionary;
import com.sec.ose.osi.ui.dialog.progress.ProgressDisplayerFactory;

/**
 * UserRequestHandler
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UserRequestHandler {
	private static Log log = LogFactory.getLog(UserRequestHandler.class);
	public static final int SYNC_FROM_SERVER		 	= 10;
	public static final int SYNC_TO_SERVER 				= 11;
	
	public static final int GET_ALL_PROJECT_NAMES 		= 21;
	public static final int SPDX_AUTO_IDENTIFY = 22;
	
	public static final int LOGIN 						= 102;
	public static final int GENERATE_BOTH_REPORT		= 103;
	public static final int GENERATE_IDENTIFY_REPORT 	= 104;
	public static final int GENERATE_SPDX_REPORT		= 105;
	
	
	public static final int EXPORT_ALL_BOM_LIST 		= 501;
	public static final int EXPORT_ALL_PROJECT_LIST 	= 502;
	
	public static final int LOAD_LICENSE				= 601;
	public static final int GET_PROTEX_PROJECT_INFO 	= 701;
	
	public static final int GET_BOM_LIST_FROM_SERVER 	= 1001;
	public static final int GET_BOM_LIST_MAP_FROM_SERVER = 1002;
	
	public static final int LOAD_IDENTIFICATION_DATA = 2000;
	
	public static final int PROJECT_SPLIT = 3002;
	public static final int PROJECT_CLONE = 3003;
	public static final int PROJECT_CREATE = 3004;
	
	public static final int PROCESS_IDENTIFY = 4001;
	public static final int PROCESS_RESET = 4002;
	
	public static final int PROJECT_INFO = 6000;
	
	public static final int DELETE_IDENTIFICATION_TABLE = 8000;
	
	public static final int CUSTOM_COMPONENT_UPDATE = 9003;
	
	public static String getCommandName(int command) {
		if(COMMAND_NAME.containsKey(command)) {
			return COMMAND_NAME.get(command);
		}
		return "UNKNOWN";
	}
	
	private static HashMap<Integer, String> COMMAND_NAME =  new HashMap<Integer, String>();
	static {
		COMMAND_NAME.put(SYNC_FROM_SERVER, "SYNC_FROM_SERVER");
		COMMAND_NAME.put(SYNC_TO_SERVER, "SYNC_TO_SERVER");
		COMMAND_NAME.put(GET_ALL_PROJECT_NAMES, "GET_ALL_PROJECT_NAMES");

		COMMAND_NAME.put(LOGIN, "LOGIN");
		COMMAND_NAME.put(GENERATE_IDENTIFY_REPORT, "GENERATE_IDENTIFY_REPORT");
		COMMAND_NAME.put(GENERATE_SPDX_REPORT, "GENERATE_SPDX_REPORT");

		COMMAND_NAME.put(EXPORT_ALL_BOM_LIST, "EXPORT_ALL_BOM_LIST");
		COMMAND_NAME.put(EXPORT_ALL_PROJECT_LIST, "");

		COMMAND_NAME.put(LOAD_LICENSE, "LOAD_LICENSE");
		COMMAND_NAME.put(GET_PROTEX_PROJECT_INFO, "GET_PROTEX_PROJECT_INFO");

		COMMAND_NAME.put(GET_BOM_LIST_FROM_SERVER, "GET_BOM_LIST_FROM_SERVER");
		COMMAND_NAME.put(GET_BOM_LIST_MAP_FROM_SERVER, "GET_BOM_LIST_MAP_FROM_SERVER");

		COMMAND_NAME.put(LOAD_IDENTIFICATION_DATA, "LOAD_IDENTIFICATION_DATA");
		COMMAND_NAME.put(PROJECT_CREATE, "PROJECT_CREATE");
		COMMAND_NAME.put(PROJECT_CLONE, "PROJECT_CLONE");
		COMMAND_NAME.put(PROJECT_SPLIT, "PROJECT_SPLIT");


		COMMAND_NAME.put(PROCESS_IDENTIFY, "PROCESS_IDENTIFY");
		COMMAND_NAME.put(PROCESS_RESET, "PROCESS_RESET");
		
		COMMAND_NAME.put(PROJECT_INFO, "PROJECT_INFO");
		COMMAND_NAME.put(DELETE_IDENTIFICATION_TABLE, "DELETE_IDENTIFICATION_TABLE");
		COMMAND_NAME.put(CUSTOM_COMPONENT_UPDATE, "CUSTOM_COMPONENT_UPDATE");
		
	}
	
	private volatile static UserRequestHandler instance = null;
	private UserRequestHandler() {}

	public static UserRequestHandler getInstance() {
		if(instance == null) {
			synchronized(UserRequestHandler.class) {
				if(instance == null) {
					instance = new UserRequestHandler();
				}
			}
		}
		return instance;
	}
	
	public void handle(int pRequestCode, UIEntity pEntity) {
		 handle(pRequestCode, pEntity, false, false);
	}


	public UIResponseObserver handle(int pRequestCode, UIEntity pEntity, boolean pDisplayProgress, boolean pShowSuccessResult) {

		final int NUMBER_THREADS = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
		
		UIResponseObserver observer = new DefaultUIResponseObserver();
		UserCommandExecutionThread    xExecutionThread = new UserCommandExecutionThread(pRequestCode, pEntity, observer);

		// Option 1:
		//      in case: pDisplayProgress == true
		// 		need to display progress dialog
		
		
		if(pDisplayProgress == true) {
			
			// 1-1. create Progress Dialog

			
			ProgressDisplayer progressDisplayer = ProgressDisplayerFactory.getProgressDisplayer(
					UISharedData.getInstance().getCurrentFrame(),
					pRequestCode);

			// 1-2. Execute Monitor Thread
			UserCommandExecutionMonitorThread xMonitorThread = new UserCommandExecutionMonitorThread(progressDisplayer, observer);
			xExecutionThread.setMonitorThread(xMonitorThread);
			
			// 1-3. execute

			executorService.execute(xMonitorThread);
			executorService.execute(xExecutionThread);
			
			progressDisplayer.setVisible(true);		// block here
			
			// 1-5. close remained thread
			
			if(progressDisplayer.isCancled()==true && 
			   xExecutionThread.isDone() == false) {
				log.debug("canceled");
				xExecutionThread.cancel();
				
				log.debug("isDone: "+xExecutionThread.isDone());
				xExecutionThread = null;
				observer.setFailMessage("canceled");
				
				executorService.shutdown();
				return observer;
			}
		}
		
		// Option 2:
		//		in case: pDisplayProgress == false
		// 		no need to display progress dialog
		
		else {
			// 2-1. execute
			executorService.execute(xExecutionThread);

			// 2-2. waiting for completing execution
			while(xExecutionThread.isDone() == false) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.warn(e);
				}
			}
		}
		
		// Mandatory STEP
		// 		display result
		
		int result = observer.getResult();
		
		// opt1. the execution result is "SUCCESS"
		if(result == UIResponseObserver.RESULT_SUCCESS) {
			
			if(pShowSuccessResult == true) {
				JOptionPane.showMessageDialog(
						UISharedData.getInstance().getCurrentFrame(), 
						observer.getSuccessMessage(), 
						ProgressDictionary.getSuccessTitle(pRequestCode),
						JOptionPane.INFORMATION_MESSAGE);
			}
		} 
		// opt2. if the execution result is "FAIL" or else
		else {
			if(pShowSuccessResult == true) {
				JOptionPane.showMessageDialog(
						UISharedData.getInstance().getCurrentFrame(), 
						observer.getFailMessage(), 
						ProgressDictionary.getErrorTitle(pRequestCode),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		executorService.shutdown();
		return observer;
	}
}
