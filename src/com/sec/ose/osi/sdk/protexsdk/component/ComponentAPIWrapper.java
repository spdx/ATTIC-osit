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
package com.sec.ose.osi.sdk.protexsdk.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.component.custom.CustomComponent;
import com.blackducksoftware.sdk.protex.component.standard.StandardComponent;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersionInfo;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponent;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentColumn;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentPageFilter;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentRequest;
import com.blackducksoftware.sdk.protex.util.PageFilterFactory;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.license.LicenseAPIWrapper;
import com.sec.ose.osi.util.Property;

/**
 * ComponentAPIWrapper
 * @author sjh.yoo, hankido.lee, suhyun47.kim, jae-yong.lee
 * 
 */
public class ComponentAPIWrapper {
	private static Log log = LogFactory.getLog(ComponentAPIWrapper.class);
	
	private static String COMPONENT_FILE_PATH = Property.FOLDER_DAT + File.separator + Property.FILE_COMPONENT_INFO_CACHE;

	/*
	 * CUSTOM - A Custom component, also known as Code Print.Custom Components are added and code printed by a user with the appropriate role.
	 * LOCAL - A Component local to a specific project.
	 * PROJECT - A Project that is referenced as a component here.
	 * STANDARD - A standard component from the Black Duck Protex KnowledgeBase (KB).
	 * STANDARD_MODIFIED - A standard (but modified) component from the Black Duck Protex KnowledgeBase (KB).
	 * 
	 */
	private static ComponentManager globalComponentManager = new ComponentManager();	// Standard & Custom
	private static HashMap<String,ComponentManager> localComponentManagerMap = new HashMap<String,ComponentManager>();	// projectId, ComponentInfo
	
	public static void init() {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(COMPONENT_FILE_PATH)));
			ComponentInfo componentInfoTmp = null;
			while((componentInfoTmp = (ComponentInfo)ois.readObject()) != null) {
				globalComponentManager.addComponent(componentInfoTmp);
			}
			ois.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (ClassNotFoundException e) {
			return;
		}
	}
	
	public static void save() {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(COMPONENT_FILE_PATH)));
			for(ComponentInfo tmp:globalComponentManager.getComponentList()) {
				oos.writeObject(tmp);
			}
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public static ArrayList<String> getComponentList(String projectId) {
		ArrayList<String> componentList = new ArrayList<String>();

		// Standard & Custom
		for(ComponentInfo compInfo:globalComponentManager.getComponentList()) {
			componentList.add(compInfo.getComponentName());
		}
		// Local
		ComponentManager LocalComponentManager = localComponentManagerMap.get(projectId);
		if(LocalComponentManager != null) {
			for(ComponentInfo compInfo:LocalComponentManager.getComponentList()) {
				componentList.add(compInfo.getComponentName());
			}
		}
		
		return componentList;
	}

	public static ArrayList<String> getComponentList(String projectId, String subString) {
		ArrayList<String> componentList = new ArrayList<String>();
		subString = subString.toLowerCase();
		for(String componentName:getComponentList(projectId)) {
			if(componentName != null && componentName.toLowerCase().startsWith(subString)) {
				componentList.add(componentName);
			}
		}
		return componentList;
	}
	

	public static void loadLocalComponent(String projectId, boolean force) {
		// 1. check "loadedLocalComponentProject"
		if(!force) {
			if(localComponentManagerMap.containsKey(projectId)) return;
		} else {
			localComponentManagerMap.remove(projectId);
		}
		
		// 2. Load Local Component
		try {
			ComponentManager localComponentManager = new ComponentManager();
			LocalComponentPageFilter pageFilter = PageFilterFactory.getAllRows(LocalComponentColumn.COMPONENT_NAME);
			List<LocalComponent> components = ProtexSDKAPIManager.getLocalComponentAPI().getLocalComponents(projectId, pageFilter);
            for (LocalComponent component : components) {
            	ComponentInfo info = new ComponentInfo(component.getComponentId(), component.getName());
            	info.setLicenseId(component.getLicenseId());
            	localComponentManager.addComponent(info);
            }
            localComponentManagerMap.put(projectId, localComponentManager);
		} catch (SdkFault e) {
			log.warn(e);
		}
		
		// TODO: if there is local component info in ComponentInfoCache, when updating this info? "SyncFromServer"?
	}

	public synchronized static String createLocalComponent(String projectId, String componentName, String licenseId) {
		try {
			// get License Info
			String licenseName = "Unspecified";
	    	LocalComponentRequest localComponentRequest = new LocalComponentRequest();
			if(licenseId != null && licenseId.length() != 0) {
				licenseName = LicenseAPIWrapper.getLicenseName(licenseId);
			} else {
				licenseName = "Unspecified";	// Checked Protex KDB
				licenseId = "unspecified";		// Checked Protex KDB
			}
	        localComponentRequest.setContextProjectId(projectId);
	        localComponentRequest.setName(componentName);
	        localComponentRequest.setBasedOnLicenseId(licenseId);
	        //localComponentRequest.setLicenseText(LicenseAPIWrapper.getLicenseText(licenseId));
	        String componentId = ProtexSDKAPIManager.getLocalComponentAPI().createLocalComponent(localComponentRequest);

			log.debug("### Create Local Component ["+componentName+"] Based On ["+licenseName+"] License");
			
			// save cache
			ComponentManager LocalComponentManager = localComponentManagerMap.get(projectId);
			if(LocalComponentManager == null) {
				LocalComponentManager = new ComponentManager();
			}
			LocalComponentManager.addComponent(new ComponentInfo(componentId,componentName,"","",licenseId,licenseName));
			localComponentManagerMap.put(projectId, LocalComponentManager);
	        return componentId;
		} catch (SdkFault e) {
			log.warn("createLocalComponent() failed: " + e.getMessage());
		}
		return null;
	}

	/**
	 * get component name from standard -> custom -> local  
	 * 
	 * sjh.yoo
	 */
	public synchronized static String getComponentName(String projectID, String componentId) {
		String componentName = getGlobalComponentName(componentId);
		return (componentName != null) ? componentName : getLocalComponentName(projectID, componentId);
	}

	/**
	 * this method is public for searching discovery. (discovery can not be local component)
	 * 
	 * sjh.yoo
	 */
	public synchronized static String getGlobalComponentName(String componentId) {
		String componentName = null;
		if(componentId != null) {
			
			// 1. cache
			componentName = globalComponentManager.getComponentNameFromId(componentId);
			if(componentName == null) {
				
				// 2. Standard Component API
				StandardComponent standardComponent = null;
				try {
					standardComponent = ProtexSDKAPIManager.getStandardComponentAPI().getStandardComponentById(componentId);
				} catch (SdkFault e1) {
					log.debug("There is no " + componentId + " in Standard Component");
				}
				if(standardComponent != null) {
					componentName = standardComponent.getName();
					List<LicenseInfo> licenseInfoList = standardComponent.getLicenses();
					if(licenseInfoList.size() > 0) {
						globalComponentManager.addComponent(new ComponentInfo(componentId,componentName,"","",
															licenseInfoList.get(0).getLicenseId(),licenseInfoList.get(0).getName()));
					} else {
						globalComponentManager.addComponent(new ComponentInfo(componentId,componentName));
					}
				} else {
					
					// 3. Custom Component API
					CustomComponent customComponent = null;
					try {
						customComponent = ProtexSDKAPIManager.getCustomComponentAPI().getCustomComponentById(componentId);
					} catch (SdkFault e2) {
						log.debug("There is no " + componentId + " in Custom Component");
					}
		        	if(customComponent != null) {
		        		componentName = customComponent.getName();
						List<LicenseInfo> licenseInfoList = customComponent.getLicenses();
						if(licenseInfoList.size() > 0) {
							globalComponentManager.addComponent(new ComponentInfo(componentId,componentName,
																licenseInfoList.get(0).getLicenseId(),licenseInfoList.get(0).getName()));
						} else {
							globalComponentManager.addComponent(new ComponentInfo(componentId,componentName));
						}
		        	}
				}
			}
		}
		return componentName;
	}

	private synchronized static String getLocalComponentName(String projectId, String componentId) {
		String componentName = null;
		if(componentId != null) {
			ComponentManager localComponentManager = localComponentManagerMap.get(projectId);
			if(localComponentManager != null) {
					componentName = localComponentManager.getComponentNameFromId(componentId);
			} else {
				log.debug("There is no " + componentId + " in Local Component");
			}
		}
		return componentName;
	}

	public synchronized static String getComponentId(String projectId, String componentName) {
		String componentId = getGlobalComponentId(componentName);
		return (componentId != null) ? componentId : getLocalComponentId(projectId, componentName);
	}

	//private static HashMap<String, String> componentMap = new HashMap<String, String>(); // component name : component id
	public synchronized static String getGlobalComponentId(String componentName) {
		String componentId = null;
		if(componentName != null) {
			
			// 1. cache
			componentId = globalComponentManager.getComponentIdFromName(componentName);
			if(componentId == null) {
				
				// 2. Standard Component API
				StandardComponent standardComponent = null;
				try {
					standardComponent = ProtexSDKAPIManager.getStandardComponentAPI().getStandardComponentByName(componentName);
				} catch (SdkFault e1) {
					log.debug("There is no " + componentName + " in Standard Component");
				}
				if(standardComponent != null) {
					componentId = standardComponent.getComponentId();
					List<LicenseInfo> licenseInfoList = standardComponent.getLicenses();
					if(licenseInfoList.size() > 0) {
						globalComponentManager.addComponent(new ComponentInfo(componentId,componentName,"","",
															licenseInfoList.get(0).getLicenseId(),licenseInfoList.get(0).getName()));
					} else {
						globalComponentManager.addComponent(new ComponentInfo(componentId,componentName));
					}
				} else {
					
					// 3. Custom Component API
					CustomComponent customComponent = null;
					try {
						customComponent = ProtexSDKAPIManager.getCustomComponentAPI().getCustomComponentByName(componentName);
					} catch (SdkFault e2) {
						log.debug("There is no " + componentName + " in Custom Component");
					}
		        	if(customComponent != null) {
		        		componentId = customComponent.getComponentId();
						List<LicenseInfo> licenseInfoList = customComponent.getLicenses();
						if(licenseInfoList.size() > 0) {
							globalComponentManager.addComponent(new ComponentInfo(componentId,componentName,
																licenseInfoList.get(0).getLicenseId(),licenseInfoList.get(0).getName()));
						} else {
							globalComponentManager.addComponent(new ComponentInfo(componentId,componentName));
						}
		        	}
				}
			}
		}
		return componentId;
	}
	
	public synchronized static String getLocalComponentId(String projectId, String componentName) {
		String componentId = null;
		if(componentName != null) {
			ComponentManager localComponentManager = localComponentManagerMap.get(projectId);
			if(localComponentManager != null) {
					componentId = localComponentManager.getComponentIdFromName(componentName);
			} else {
				log.debug("There is no " + componentName + " in Local Component");
			}
		}
		return componentId;
	}

	public static ComponentVersion getComponentVersionByName(String componentName, String versionName) {
		if(componentName == null || componentName.length() <=0) 
			return null;
		if(versionName == null || versionName.equals("Unspecified")) 
			versionName = "";
		
		ComponentVersion cv = new ComponentVersion();
		cv.setComponentName(componentName);
		cv.setVersionName(versionName);
		
		// 1. cache
		ArrayList<ComponentInfo> componentInfoList = globalComponentManager.getComponentVersionIdFromName(componentName,versionName);
		if(componentInfoList.size() == 1) {
			cv.setComponentId(componentInfoList.get(0).getComponentId());
			cv.setVersionId(componentInfoList.get(0).getVersionId());
			return cv;
		} else if(componentInfoList.size() == 0) {
			try {
				String componentId = getGlobalComponentId(componentName);
				if(componentId != null) {
					List<ComponentVersionInfo> componentVersionInfoList = 
						ProtexSDKAPIManager.getComponentVersionAPI().getComponentVersions(componentId);
					for(ComponentVersionInfo cvInfo:componentVersionInfoList) {
						ComponentInfo info = new ComponentInfo(cvInfo.getComponentId(),cvInfo.getComponentName(), cvInfo.getVersionId(), cvInfo.getVersionName());
						List<LicenseInfo> licenseInfo = cvInfo.getLicenses();
						if(licenseInfo.size() > 0) {
							info.setLicenseId(licenseInfo.get(0).getLicenseId());
							info.setLicenseName(licenseInfo.get(0).getName());
						}
						globalComponentManager.addComponent(info);
						if(componentName.equals(info.getComponentName()) && versionName.equals(info.getVersionName())) {
							componentInfoList.add(info);
						}
					}
					if(componentInfoList.size()==0) {
						// There is no component info in ComponentVersionAPI , There is info in StandardComponentAPI.
						ComponentInfo info = new ComponentInfo(componentId,componentName);
						globalComponentManager.addComponent(info);
						componentInfoList.add(info);
					}
				}
			} catch (SdkFault e) {
				e.printStackTrace();
			}
		}
		
		if(componentInfoList.size() == 1) {
			cv.setComponentId(componentInfoList.get(0).getComponentId());
			cv.setVersionId(componentInfoList.get(0).getVersionId());
			return cv;
		} else if(componentInfoList.size() > 1) {
			// Need matched file info
			return null;
		}
		
		return null;
	}

	public static ComponentVersion getComponentVersionById(String componentId, String versionId) {
		if(componentId == null || componentId.length() <=0) 
			return null;
		if(versionId == null || versionId.equals("Unspecified")) 
			versionId = "";
		
		ComponentVersion cv = new ComponentVersion();
		cv.setComponentId(componentId);
		cv.setVersionId(versionId);
		
		// 1. cache
		ComponentInfo componentInfo = globalComponentManager.getComponentVersionNameFromId(componentId,versionId);
		if(componentInfo != null) {
			cv.setComponentName(componentInfo.getComponentName());
			cv.setVersionName(componentInfo.getVersionName());
			return cv;
		} else {
			try {
				String componentName = null;
				List<ComponentVersionInfo> componentVersionInfoList = 
					ProtexSDKAPIManager.getComponentVersionAPI().getComponentVersions(componentId);
				for(ComponentVersionInfo cvInfo:componentVersionInfoList) {
					ComponentInfo info = new ComponentInfo(cvInfo.getComponentId(),cvInfo.getComponentName(), cvInfo.getVersionId(), cvInfo.getVersionName());
					List<LicenseInfo> licenseInfo = cvInfo.getLicenses();
					if(licenseInfo.size() > 0) {
						info.setLicenseId(licenseInfo.get(0).getLicenseId());
						info.setLicenseName(licenseInfo.get(0).getName());
					}
					globalComponentManager.addComponent(info);
					if(componentId.equals(info.getComponentId()) && versionId.equals(info.getVersionId())) {
						componentName = info.getComponentName();
						cv.setComponentName(componentName);
						cv.setVersionName(info.getVersionName());
						return cv;
					}
				}

				if(componentName == null) {
					// There is no component info in ComponentVersionAPI , There is info in StandardComponentAPI.
					componentName = getGlobalComponentName(componentId);
					if(componentName != null) {
						ComponentInfo info = new ComponentInfo(componentId,componentName);
						globalComponentManager.addComponent(info);
						cv.setComponentName(componentName);
						cv.setVersionName("");
						return cv;
					}
				}
			} catch (SdkFault e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void setComponentVersion(String componentName, String componentId, String versionName, String versionId) {
		// Standard or Custom Component

		// 1. check dup in cache
		ComponentInfo componentInfo = globalComponentManager.getComponentVersionNameFromId(componentId,versionId);
		if(componentInfo != null) return;

		// 2. add component info
		ComponentInfo info = new ComponentInfo(componentId,componentName);
		info.setVersionId(versionId);
		info.setVersionName(versionName);
		globalComponentManager.addComponent(info);
	}
}