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
package com.sec.ose.osi.thread.job;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.job.analysis.AnalysisMonitorThread;
import com.sec.ose.osi.thread.job.identify.IdentifyThread;
import com.sec.ose.osi.thread.job.identify.data.IdentifyQueue;
import com.sec.ose.osi.thread.job.init.AfterLoginTaskThread;
import com.sec.ose.osi.thread.job.init.BeforeLoginTaskThread;

/**
 * BackgroundJobManager
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class BackgroundJobManager {
	private static Log log = LogFactory.getLog(BackgroundJobManager.class);
	
	private final int NUM_OF_IDENTIFY_THREAD = 1;
	
	private BeforeLoginTaskThread mBeforeLoginTaskThread = null;
	private AfterLoginTaskThread mAfterLoginTaskThread = null;

	private ArrayList<IdentifyThread> mIdentifyThreadGroup=null;
	
	private AnalysisMonitorThread mAnalysisMonitorThread = null;
	private volatile static BackgroundJobManager instance = null;
	
	private BackgroundJobManager() {
	}
	
	public static BackgroundJobManager getInstance() {
		if(instance == null) {
			synchronized(BackgroundJobManager.class) {
				if(instance == null) {
					instance = new BackgroundJobManager();
				}
			}
		}
		return instance;
	}
	
	public BeforeLoginTaskThread startBeforeLoginTaskThread() {
		if(mBeforeLoginTaskThread == null) {
			mBeforeLoginTaskThread = new BeforeLoginTaskThread();
			mBeforeLoginTaskThread.start();
		}
		
		return mBeforeLoginTaskThread;
		
	}

	public void startAfterLoginTaskThread() {
		if(mAfterLoginTaskThread == null) {
			if(mBeforeLoginTaskThread == null)
				mBeforeLoginTaskThread = startBeforeLoginTaskThread();
			
			mAfterLoginTaskThread = new AfterLoginTaskThread(mBeforeLoginTaskThread);
			mAfterLoginTaskThread.start();
		}
		
	}
	
	public void startIdentifyThread() {
		
		mIdentifyThreadGroup = new ArrayList<IdentifyThread>();
		for(int id=0; id<NUM_OF_IDENTIFY_THREAD; id++) {
			IdentifyThread identifyThread = new IdentifyThread(id);
			identifyThread.start();
			mIdentifyThreadGroup.add(identifyThread);
		}
	}
	
	public AnalysisMonitorThread startAnalysisMonitoThread() {
		if(mAnalysisMonitorThread == null) {
			mAnalysisMonitorThread = new AnalysisMonitorThread();
			mAnalysisMonitorThread.start();
		}
		
		return mAnalysisMonitorThread;
	}
	
	public void stopJoinAllBackgroudThread() {
		
		mBeforeLoginTaskThread = null;
		mAfterLoginTaskThread = null;
		
		if(mIdentifyThreadGroup != null) {
			for(IdentifyThread identifyThread:mIdentifyThreadGroup) {
				identifyThread.requestStopThread();
			}
		}
		else {
			log.error("mIdentifyThread is null - identifyQueueSize:"+IdentifyQueue.getInstance().size());
		}
		
		mIdentifyThreadGroup = null;
		
		mAnalysisMonitorThread.stopThread();

		mAnalysisMonitorThread = null;
	}
	
	public void restartAllBackgroundThread() {
		log.info("restartAllBackgroundThread : start");
		stopJoinAllBackgroudThread();
		startIdentifyThread();
		startAnalysisMonitoThread();
		log.info("restartAllBackgroundThread : end");
	}

	public void requestStopIdentifyThread() {
		if(mIdentifyThreadGroup != null) {
			for(IdentifyThread identifyThread:mIdentifyThreadGroup) {
				identifyThread.requestStopThread();
			}
		}
	}

	public boolean isAllIdentifyThreadReadyStatus() {
		for(IdentifyThread identifyThread:mIdentifyThreadGroup) {
			if( identifyThread.getStatus() != IdentifyThread.STATUS_READY)
				return false;
		}
		
		return true;
	}

	public IdentifyThread getIdentifyThread() {
		if(mIdentifyThreadGroup.size() == 1)
			return mIdentifyThreadGroup.get(0);
		
		return null;
	}
}
