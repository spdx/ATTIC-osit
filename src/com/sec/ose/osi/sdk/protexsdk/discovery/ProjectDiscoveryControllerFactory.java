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
package com.sec.ose.osi.sdk.protexsdk.discovery;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * ProjectDiscoveryControllerFactory
 * @author suhyun47.kim
 * 
 */
public class ProjectDiscoveryControllerFactory {
	private static Log log = LogFactory.getLog(ProjectDiscoveryControllerFactory.class);
	
	public static ProjectDiscoveryController createFromProtexServer(String projectName, UIResponseObserver observer) {
		
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		observer.setMessageHeader("Composing Identification Data from Protex Server...\n");
		observer.pushMessageWithHeader(" > Init Identification Table from Protex Server.\n");
		
		ProjectDiscoveryController aProjectDiscoveryController = new ProjectDiscoveryController(projectName);
		
		// blocked here to finish loading
		loadDiscoveryDataFromProtexServer(aProjectDiscoveryController, observer);
		SettingStringSearchInformationToCodeMatchThread aSearchInformationToCodeMatchThread = 
			new SettingStringSearchInformationToCodeMatchThread (
					aProjectDiscoveryController);
		
		aSearchInformationToCodeMatchThread.start();											// non-blocked
		
		return aProjectDiscoveryController;
	}
	
	private static void loadDiscoveryDataFromProtexServer(
			ProjectDiscoveryController pProjectDiscoveryController, 
			UIResponseObserver observer) {
		// delete previous table and create new table
		String projectName = pProjectDiscoveryController.getProjectName();
		IdentificationDBManager.init(projectName);
		ReportAPIWrapper.discardAllDataForProject(projectName);
		
		log.debug("@@@ table generation start (loading from Protex Server- "+new java.util.Date());
		
		observer.setMessageHeader("Update Identification Table from Protex Server...\n");
		observer.pushMessageWithHeader(" > Generating Match Table from Protex Server.\n");
		log.debug(" > Generating Match Table.");
		
		ReportEntityList identifiedFiles = ReportAPIWrapper.getIdentifiedFiles(projectName, new DefaultUIResponseObserver(), true);
		ReportEntityList stringSearch = ReportAPIWrapper.getStringSearches(projectName, new DefaultUIResponseObserver(), true);
		ReportEntityList compareCodeMatches = ReportAPIWrapper.getCompareCodeMatches(projectName,observer, true);
//		ReportEntityList codeMatchesPrecision = ReportAPIWrapper.getCodeMatchesPrecision(projectName,observer, true);
		ReportEntityList codeMatchesPrecision = ReportAPIWrapper.precessCodeMatchesPrecision(projectName,observer, true);
		ReportEntityList patternMatchesPendingFiles = ReportAPIWrapper.getPatternMatchesPendingFiles(projectName, new DefaultUIResponseObserver(), true);
		
		ArrayList<DiscoveryControllerLoaderThread> discoveryDataLoader = new ArrayList<DiscoveryControllerLoaderThread>();
		
		discoveryDataLoader.add(
				new StringSearchDiscoveryControllerLoaderThread(
						pProjectDiscoveryController,
						observer,
						identifiedFiles, 
						stringSearch,
						compareCodeMatches,
						codeMatchesPrecision,
						patternMatchesPendingFiles
						));
		
		discoveryDataLoader.add(
				new CodeMatchDiscoveryControllerLoaderThread(
						pProjectDiscoveryController,
						observer,
						identifiedFiles, 
						stringSearch,
						compareCodeMatches,
						codeMatchesPrecision,
						patternMatchesPendingFiles
						));
		
		discoveryDataLoader.add(
				new PatternMatchDiscoveryControllerLoaderThread(
						pProjectDiscoveryController,
						observer,
						identifiedFiles, 
						stringSearch,
						compareCodeMatches,
						codeMatchesPrecision,
						patternMatchesPendingFiles
						));				
		
		try {
			// starting loaders
			for(int i=0; i<discoveryDataLoader.size(); i++) {
				discoveryDataLoader.get(i).start();
			}
			// waiting for finishing loaders.
			for(int i=0; i<discoveryDataLoader.size(); i++) {
				log.debug("@@@ DiscoveryControllerThread joining : " + discoveryDataLoader.get(i).getClass().getName());
				discoveryDataLoader.get(i).join();
			}
		} catch (Exception e) {
			log.warn(e);
		}
		log.debug("@@@ table generate end - "+new java.util.Date());
	}

	public static ProjectDiscoveryController createFromLocalDB(
			String pProjectName, 
			UIResponseObserver observer) {
		
		ProjectDiscoveryController aProjectDiscoveryController = new ProjectDiscoveryController(pProjectName);
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		observer.setMessageHeader("Loading Identification Table from Local DB...\n");
		observer.pushMessageWithHeader(" > Loading Match Table from Local DB.\n");
		log.debug(" > Loading Match Table.");
		
		aProjectDiscoveryController.setCodeMatchDiscoveryController(new DCCodeMatch(pProjectName, observer));
		aProjectDiscoveryController.setStringSearchDiscoveryController(new DCStringSearch(pProjectName, observer));
		aProjectDiscoveryController.setPatternMatchDiscoveryController(new DCPatternMatch(pProjectName, observer));
		
		
		return aProjectDiscoveryController;
	}
}

/**
 *  SettingStringSearchInformationToCodeMatchThread
 */
class SettingStringSearchInformationToCodeMatchThread extends Thread {
	private static Log log = LogFactory.getLog(SettingStringSearchInformationToCodeMatchThread.class);
	
	ProjectDiscoveryController mProjectDiscoveryController;
	public SettingStringSearchInformationToCodeMatchThread(
			ProjectDiscoveryController pProjectDiscoveryController) {
		this.mProjectDiscoveryController = pProjectDiscoveryController;
	}
	
	@Override
	public void run() {
		log.debug("@@@ SetStringSearchInformationThread start");
		String projectName = this.mProjectDiscoveryController.getProjectName();
		DCCodeMatch aDCCodeMatch = this.mProjectDiscoveryController.getCodeMatchDiscoveryController();
		
		ReportEntityList identifiedFiles = ReportAPIWrapper.getIdentifiedFiles(projectName, new DefaultUIResponseObserver(), true);
		if(identifiedFiles != null) {
			for(ReportEntity entity:identifiedFiles) {
				if(entity.getValue(ReportInfo.IDENTIFIED_FILES.DISCOVERY_TYPE).equals("String Search")) {
					String path = entity.getValue(ReportInfo.IDENTIFIED_FILES.FILE_FOLDER_NAME).substring(1);
					String licenseName = entity.getValue(ReportInfo.IDENTIFIED_FILES.LICENSE);
					aDCCodeMatch.setStringSearchInformation(projectName,path,licenseName);
				}
			}
		}
		log.debug("@@@ SetStringSearchInformationThread end");
	}
}

/**
 *  StringSearchDiscoveryControllerLoaderThread 
 *  	extends DiscoveryControllerLoaderThread 
 */

class StringSearchDiscoveryControllerLoaderThread extends DiscoveryControllerLoaderThread {
	private static Log log = LogFactory.getLog(StringSearchDiscoveryControllerLoaderThread.class);
	public StringSearchDiscoveryControllerLoaderThread(
			ProjectDiscoveryController pProjectDiscoveryController,
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList stringSearch,
			ReportEntityList compareCodeMatches,
			ReportEntityList codeMatchesPrecision,
			ReportEntityList patternMatchesPendingFiles) {
		super(  pProjectDiscoveryController, 
				observer, 
				identifiedFiles, 
				stringSearch,
				compareCodeMatches,
				codeMatchesPrecision,
				patternMatchesPendingFiles);
	
	}
	public void run() {
		
		log.debug("@@@ StringSearchDiscoveryControllerThread start");
		
		String projectName = mProjectDiscoveryController.getProjectName();
		DCStringSearch aDCStringSearch = new DCStringSearch(projectName,super.mObserver);
		aDCStringSearch.loadFromProtexServer(
				super.mObserver,
				identifiedFiles,
				stringSearch);
		
		mProjectDiscoveryController.setStringSearchDiscoveryController(aDCStringSearch);
		log.debug("@@@ StringSearchDiscoveryControllerThread end");
	}
}

/**
 *  CodeMatchDiscoveryControllerLoaderThread
 *  	extends DiscoveryControllerLoaderThread 
 */
class CodeMatchDiscoveryControllerLoaderThread extends DiscoveryControllerLoaderThread {
	private static Log log = LogFactory.getLog(CodeMatchDiscoveryControllerLoaderThread.class);
	public CodeMatchDiscoveryControllerLoaderThread(
			ProjectDiscoveryController pProjectDiscoveryController,
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList stringSearch,
			ReportEntityList compareCodeMatches,
			ReportEntityList codeMatchesPrecision,
			ReportEntityList patternMatchesPendingFiles) {
		super(  pProjectDiscoveryController, 
				observer, 
				identifiedFiles, 
				stringSearch,
				compareCodeMatches,
				codeMatchesPrecision,
				patternMatchesPendingFiles);
	}
	
	public void run() {
		
		log.debug("@@@ CodeMatchDiscoveryControllerThread start");
		
		String projectName = mProjectDiscoveryController.getProjectName();
		DCCodeMatch aDCCodeMatch = new DCCodeMatch(projectName,super.mObserver);
		aDCCodeMatch.loadFromProtexServer(
				super.mObserver,
				identifiedFiles,
				compareCodeMatches,
				codeMatchesPrecision);
		mProjectDiscoveryController.setCodeMatchDiscoveryController(aDCCodeMatch);
		
		log.debug("@@@ CodeMatchDiscoveryControllerThread end");
	}
}


/**
 *  PatternMatchDiscoveryControllerLoaderThread
 *  	extends DiscoveryControllerLoaderThread 
 */
class PatternMatchDiscoveryControllerLoaderThread extends DiscoveryControllerLoaderThread {
	private static Log log = LogFactory.getLog(PatternMatchDiscoveryControllerLoaderThread.class);
	public PatternMatchDiscoveryControllerLoaderThread(
			ProjectDiscoveryController pProjectDiscoveryController,
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList stringSearch,
			ReportEntityList compareCodeMatches,
			ReportEntityList codeMatchesPrecision,
			ReportEntityList patternMatchesPendingFiles) {
		super(  pProjectDiscoveryController, 
				observer, 
				identifiedFiles, 
				stringSearch,
				compareCodeMatches,
				codeMatchesPrecision,
				patternMatchesPendingFiles);
	}
	
	public void run() {
		
		log.debug("@@@ PatternMatchDiscoveryControllerThread start");
		
		String projectName = super.mProjectDiscoveryController.getProjectName();
		DCPatternMatch aDCPatternMatch = new DCPatternMatch(projectName,super.mObserver);
		aDCPatternMatch.loadFromProtexServer(
				super.mObserver,
				identifiedFiles,
				patternMatchesPendingFiles);
		mProjectDiscoveryController.setPatternMatchDiscoveryController(aDCPatternMatch);
		
		
		log.debug("@@@ PatternMatchDiscoveryControllerThread end");
	}
}
	
/**
 *  PatternMatchDiscoveryControllerLoaderThread
 *  	extends DiscoveryControllerLoaderThread 
 */
	
class DiscoveryControllerLoaderThread extends Thread {
	ProjectDiscoveryController mProjectDiscoveryController;
	UIResponseObserver mObserver;
	ReportEntityList identifiedFiles = null;
	ReportEntityList stringSearch = null;
	ReportEntityList compareCodeMatches = null;
	ReportEntityList codeMatchesPrecision = null;
	ReportEntityList patternMatchesPendingFiles = null;
	
	public DiscoveryControllerLoaderThread(
			ProjectDiscoveryController pProjectDiscoveryController,
			UIResponseObserver observer,
			ReportEntityList identifiedFiles,
			ReportEntityList stringSearch,
			ReportEntityList compareCodeMatches,
			ReportEntityList codeMatchesPrecision,
			ReportEntityList patternMatchesPendingFiles) {
		this.mProjectDiscoveryController = pProjectDiscoveryController;
		this.mObserver = observer;
		this.identifiedFiles = identifiedFiles;
		this.stringSearch = stringSearch;
		this.compareCodeMatches = compareCodeMatches;
		this.codeMatchesPrecision = codeMatchesPrecision;
		this.patternMatchesPendingFiles = patternMatchesPendingFiles;
	}
}