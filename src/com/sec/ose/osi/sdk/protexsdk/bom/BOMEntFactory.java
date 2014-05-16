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
package com.sec.ose.osi.sdk.protexsdk.bom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.Component;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntity;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportEntityList;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * BOMEntFactory
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public class BOMEntFactory {
	private static Log log = LogFactory.getLog(BOMEntFactory.class);

	public static ArrayList<BOMEnt> createBOMEntList(String pProjectName, boolean containDefaultComponent, UIResponseObserver observer) {
		String mComponentID = "";
		String mComponentName = "";
		String mComponentVersion = "";
		String mLicenseName = "";
		String mHasDeclaredLicenseConflict = "";
		StringBuffer mComponentComment = new StringBuffer("");
		String mFileCountIdentified = "";
		
    	ReportEntityList BillOfMaterialList = ReportAPIWrapper.getBillOfMaterials(pProjectName, observer, true);
		ArrayList<BOMEnt> bomEntList = new ArrayList<BOMEnt>();
		
		if(BillOfMaterialList == null) return null;
		if(BillOfMaterialList.size() == 0) return null;
		
		int cnt=0;
		for(ReportEntity entity:BillOfMaterialList) {
			if(++cnt % 100 == 0)
			log.debug("entity#"+cnt);

			mComponentComment.setLength(0);
			mComponentName = entity.getValue(ReportInfo.BILL_OF_MATERIALS.COMPONENT);
			mComponentVersion = entity.getValue(ReportInfo.BILL_OF_MATERIALS.VERSION);

			// get license name
        	mLicenseName = entity.getValue(ReportInfo.BILL_OF_MATERIALS.LICENSE);
            if (mLicenseName.contains("[") && !mLicenseName.contains("template")) {
				int startIndex = mLicenseName.indexOf("[");
				mLicenseName = mLicenseName.substring(0, startIndex);
				mLicenseName = mLicenseName.trim();
			}

			if(entity.getValue(ReportInfo.BILL_OF_MATERIALS.LICENSE_CONFLICT).contains("Declared License Conflict")) {
				mHasDeclaredLicenseConflict = "true";
			} else {
				mHasDeclaredLicenseConflict = "false";
			}
			
			// get Component Comment
        	String componentComment = entity.getValue(ReportInfo.BILL_OF_MATERIALS.COMMENT);
        	if(componentComment != null && componentComment.length() > 0) {
        		mComponentComment.append("<"+mComponentName+" - "+mComponentVersion+">\n"+componentComment);
        	}
        	
			mFileCountIdentified = entity.getValue(ReportInfo.BILL_OF_MATERIALS.NUM_OF_CODE_MATCH);
			
			if(mFileCountIdentified.equals("0")){
				mFileCountIdentified = entity.getValue(ReportInfo.BILL_OF_MATERIALS.NUM_OF_SERACH);
			}

            bomEntList.add(new BOMEnt(pProjectName, mComponentName, mComponentID, 
            		mComponentVersion, mLicenseName, mHasDeclaredLicenseConflict, mComponentComment.toString(), mFileCountIdentified));
            if(observer != null) {
            	observer.pushMessageWithHeader(" > "+mComponentName+" - "+mComponentVersion+" : "+mLicenseName);
            }
		}
		return bomEntList;
	}
	
	public static ArrayList<BOMEnt> createBOMEntListFromBOMAPI(String pProjectName, boolean containDefaultComponent, UIResponseObserver observer) {
		String mComponentID = "";
		String mComponentName = "";
		String mComponentVersion = "";
		String mLicenseName = "";
		String mHasDeclaredLicenseConflict = "";
		StringBuffer mComponentComment = new StringBuffer("");
		Integer mFileCountIdentified = 0;
		ArrayList<BOMEnt> bomEntList = new ArrayList<BOMEnt>();
		
		String mProjectID;
        List<BomComponent> bomComponents = null;
    	ProjectApi mProjectAPI = ProtexSDKAPIManager.getProjectAPI();
    	BomApi mBomAPI = ProtexSDKAPIManager.getBomAPI();
    	try {
    		mProjectID = ProjectAPIWrapper.getProjectID(pProjectName);

            bomComponents = mBomAPI.getBomComponents(mProjectID);
            if(bomComponents.size() <= 0) return null;

            for (BomComponent bomComponent : bomComponents) {

    			// get component ID
            	mComponentID = bomComponent.getComponentId();
            	
    			// get component name
    	        Component mComponent = mProjectAPI.getComponentById(mProjectID, mComponentID);
    	        if(mComponent == null) continue;
    	        mComponentName = mComponent.getName();
    			if(containDefaultComponent == false && pProjectName.equals(mComponentName)) {
    				continue;
    			}

    			// get component version
    	        mComponentVersion = bomComponent.getBomVersionName();
    	        
    	        // get Declared License Conflict
    	        mHasDeclaredLicenseConflict = bomComponent.isHasDeclaredLicenseConflict().toString();

    	        // get Component License Conflict - current not use
//    	        mHasComponentLicenseConflict = bomComponent.isHasComponentLicenseConflict().toString();
    	        
    			// get license name
    	        LicenseInfo licenseInfo = bomComponent.getLicenseInfo();
    	        if(licenseInfo == null) mLicenseName = "";
    	        else mLicenseName = parseFileLicense(licenseInfo.getName());

    			// get Identified file count
    	        mFileCountIdentified = bomComponent.getFileCountIdentified();
    			
    			// get Component Comment
            	mComponentComment.append(mBomAPI.getComponentComment(mProjectID, mComponentID, bomComponent.getVersionId()));
            	if(mComponentComment.toString().length() > 0) {
            		mComponentComment.append("<"+mComponentName+" - "+mComponentVersion+">\n"+mComponentComment);
            	}
                
                bomEntList.add(new BOMEnt(pProjectName, mComponentName, mComponentID, 
                		mComponentVersion, mLicenseName, mHasDeclaredLicenseConflict, mComponentComment.toString(), mFileCountIdentified.toString()));
                observer.pushMessageWithHeader(" > "+mComponentName+" - "+mComponentVersion+" : "+mLicenseName);
                
            }
    	} catch (SdkFault e) {
    		log.warn(e);
    		observer.setFailMessage("FAILE");
    		return null;
    	}

		return bomEntList;
	}

	private static String parseFileLicense(String mLicenseName) {
		if(mLicenseName != null) {
			if (mLicenseName.contains("[") && !mLicenseName.contains("template")) {
				int startIndex = mLicenseName.indexOf("[");
				mLicenseName = mLicenseName.substring(0, startIndex).trim();
			}
        }
		
		return mLicenseName;
	}
}

