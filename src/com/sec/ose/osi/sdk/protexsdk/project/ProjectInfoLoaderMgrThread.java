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
package com.sec.ose.osi.sdk.protexsdk.project;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;

/**
 * ProjectInfoLoaderMgrThread
 * @author sjh.yoo, suhyun47.kim
 * 
 */
public class ProjectInfoLoaderMgrThread extends Thread {
	private static Log log = LogFactory.getLog(ProjectInfoLoaderMgrThread.class);
	
	private final int THREAD_POOL_SIZE = 10;
	private ProjectInfoLoaderThread[] mLoaderThreads = null;
	private Collection<OSIProjectInfo> toBeLoadedProjects;
	
	public ProjectInfoLoaderMgrThread(Collection<OSIProjectInfo> toBeLoadedProjects) {
		this.toBeLoadedProjects = toBeLoadedProjects;
		mLoaderThreads = new ProjectInfoLoaderThread[THREAD_POOL_SIZE];
	}
	
	synchronized private ProjectInfoLoaderThread getReadyThread(OSIProjectInfo project) {
		
		while(true) {
			for(int i=0;i<THREAD_POOL_SIZE;i++) {
				if(mLoaderThreads[i] == null || mLoaderThreads[i].isDone()) {
					mLoaderThreads[i] = new ProjectInfoLoaderThread(project);
					return mLoaderThreads[i];
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.warn(e);
			}
		}
	}

	public void run() {
		ArrayList<OSIProjectInfo> projectInfoList = new ArrayList<OSIProjectInfo>(toBeLoadedProjects);
		
		for(int i=0; i<projectInfoList.size(); i++) {
	    	OSIProjectInfo project = projectInfoList.get(i);   
			if(!project.isLoaded()) {
				ProjectInfoLoaderThread projectInfoLoader = getReadyThread(project);
				projectInfoLoader.start();
			}
	    }
	}
}
