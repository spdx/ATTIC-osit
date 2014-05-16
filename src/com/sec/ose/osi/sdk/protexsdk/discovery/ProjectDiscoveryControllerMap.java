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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.Property;

/**
 * ProjectDiscoveryControllerMap
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class ProjectDiscoveryControllerMap {
	private static Log log = LogFactory.getLog(ProjectDiscoveryControllerMap.class);
	
	private ProjectDiscoveryControllerMap() {}
	
	private static HashMap<String, ProjectDiscoveryController> mProjectControllers = new HashMap<String, ProjectDiscoveryController>(2);
																					// key: project_name - value: DiscoveryControllerContainer
	public static AbstractDiscoveryController getDiscoveryController(
			String pProjectName, 
			int pCompositeType) {
		
		ProjectDiscoveryController aProjectDiscoveryController = mProjectControllers.get(pProjectName);
		if(aProjectDiscoveryController == null) {
			aProjectDiscoveryController = createProjectDiscoveryControllerFromLocalDB(pProjectName, new DefaultUIResponseObserver());
		}
		
		if( (pCompositeType & IdentificationConstantValue.STRING_MATCH_TYPE) !=0) {
			return aProjectDiscoveryController.getStringSearchDiscoveryController();
		}
		else if( (pCompositeType & IdentificationConstantValue.CODE_MATCH_TYPE) != 0) {
			return aProjectDiscoveryController.getCodeMatchDiscoveryController();
		}
		else if( (pCompositeType & IdentificationConstantValue.PATTERN_MATCH_TYPE) != 0) {
			return aProjectDiscoveryController.getPatternMatchDiscoveryController();
		}
		
		return null;
	}
	
	public static void loadProjectDiscoveryController(
			String pProjectName, 
			UIResponseObserver observer) {
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		log.debug("loadProjectDiscoveryController() - "+pProjectName);
		observer.pushMessageWithHeader("> Load Project Discovery Controller");

		ProjectDiscoveryController aDiscoveryController = mProjectControllers.get(pProjectName);
		if(aDiscoveryController != null)		// discovery controller for the given projectName is already loaded in memory
			return;
		
		
		boolean loadDiscoveryDataFromServer = false;
		String strLoadDiscoveryDataFromServer = Property.getInstance().getProperty(Property.DISCOVERY_DATA_ALWAYS_RELOAD_WHEN_SELECT_PRROJECT);
		loadDiscoveryDataFromServer = "true".equals(strLoadDiscoveryDataFromServer.toLowerCase())? true: false;

		log.debug("loadDiscoveryDataFromServer - "+loadDiscoveryDataFromServer);
		
		
		if(loadDiscoveryDataFromServer == false	&&
				IdentificationDBManager.isExistedProject(pProjectName)
				) {
			
			log.debug("loadDiscoveryDataFromServer - "+loadDiscoveryDataFromServer);
			observer.pushMessageWithHeader("> Loading data from local database");
			
			createProjectDiscoveryControllerFromLocalDB(pProjectName, observer);
		}
		else {
		
			createProjectDiscoveryControllerFromProtexServer(pProjectName,observer);
		}
		
	}
	
	public static void loadProjectDiscoveryControllerFromProtexServer(
			String projectName, 
			UIResponseObserver observer) {
		
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		log.debug("refresh() - "+projectName);

		ReportAPIWrapper.discardAllDataForProject(projectName);
		mProjectControllers.remove(projectName);
		createProjectDiscoveryControllerFromProtexServer(projectName, observer);

	}
	
	private static ProjectDiscoveryController createProjectDiscoveryControllerFromLocalDB(
			String pProjectName, 
			UIResponseObserver observer) {
		
		log.debug("createProjectDiscoveryControllerFromLocalDB");	
		
		ProjectDiscoveryController aProjectDiscoveryController = 
			ProjectDiscoveryControllerFactory.createFromLocalDB(pProjectName, observer);
		
		mProjectControllers.put(pProjectName, aProjectDiscoveryController);
//		tmpProjectDiscoveryController.setStringSearchInformationToCodeMatchThread();
		
		return aProjectDiscoveryController;

		
	}

	private static ProjectDiscoveryController createProjectDiscoveryControllerFromProtexServer(
			String pProjectName, 
			UIResponseObserver observer) {

		ProjectDiscoveryController aProjectDiscoveryController = 
			ProjectDiscoveryControllerFactory.createFromProtexServer(pProjectName, observer);
		
		mProjectControllers.put(pProjectName, aProjectDiscoveryController);
		
		return aProjectDiscoveryController;
	}
	
	public static void clear() {
		mProjectControllers.clear();
	}
	
	public static void removeProjectDiscoveryController(String projectName) {
		mProjectControllers.remove(projectName);
	}
	
}
