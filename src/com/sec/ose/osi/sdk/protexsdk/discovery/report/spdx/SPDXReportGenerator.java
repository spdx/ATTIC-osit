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
package com.sec.ose.osi.sdk.protexsdk.discovery.report.spdx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.SpdxAllLicenseInfoFromFileType;
import com.blackducksoftware.sdk.protex.report.SpdxFileLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxLicenseDeclarationType;
import com.blackducksoftware.sdk.protex.report.SpdxPackageLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.blackducksoftware.sdk.protex.report.SpdxReportFormat;
import com.blackducksoftware.sdk.protex.user.User;
import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * SPDXReportGenerator
 * @author sjh.yoo
 * 
 */
public class SPDXReportGenerator {
	private static Log log = LogFactory.getLog(SPDXReportGenerator.class);
	public static int GEN_ERROR_CODE = 1;
	public static int SHA_ERROR_CODE = 2;
	
	public int generator(String projectName, String targetFilePath, UIResponseObserver observer) {
		log.debug("generate SPDX Document : "+projectName);
		try {
			String pMessage = " > Load SPDX Document from Server.\n";
			if(observer != null) {
				observer.pushMessageWithHeader(pMessage);
			}
			
			// 1. select project
			String projectId = ProjectAPIWrapper.getProjectID(projectName);
	        
	        // 2. setup report configuration (below are full config that we can setup)
	        SpdxReportConfiguration spdxReportConfiguration = new SpdxReportConfiguration();
	        
			/** Spec. 2 SPDX DOCUMENT INFORMATION **/
	        // Unused
//	        spdxReportConfiguration.setDocumentComment("");
			
			/** Spec. 3 CREATION INFORMATION **/
	        spdxReportConfiguration.setOrganization(LoginSessionEnt.getInstance().getOrganization());
	        spdxReportConfiguration.setOrganizationEmail("Unknown");

			try {
				User userInfo = ProtexSDKAPIManager.getUserAPI().getUserByEmail(LoginSessionEnt.getInstance().getUserID());
		        spdxReportConfiguration.setCreatedBy(userInfo.getFirstName() + " " + userInfo.getLastName());
			} catch (SdkFault e1) {
				e1.printStackTrace();
				spdxReportConfiguration.setCreatedBy("Unknown");
			}
	        spdxReportConfiguration.setCreatedByEMail(LoginSessionEnt.getInstance().getUserID());
	        
	        /** Spec. 4 PACKAGE INFORMATION **/
	        spdxReportConfiguration.setPackageName(projectName);
	        spdxReportConfiguration.setPackageVersion("");
	        spdxReportConfiguration.setPackageFileName("");
	        spdxReportConfiguration.setPackageLicenseDeclarationType(SpdxLicenseDeclarationType.NO_ASSERTION);
	        spdxReportConfiguration.setAllLicenseInfoFromFilesType(SpdxAllLicenseInfoFromFileType.NO_ASSERTION);
	        spdxReportConfiguration.setPackageLicenseConclusionType(SpdxPackageLicenseConclusionType.NO_ASSERTION);

	        // Unused
	        spdxReportConfiguration.setLicenseComment("");	// Spec 4. Comments On License (not available, protex error??)
	        spdxReportConfiguration.setPackageDownloadUrl("");	// Spec 4. Package Download Location
	        spdxReportConfiguration.setPackageSourceInformation("");	// Spec 4. Source Information	// (not available, protex error??)
	        spdxReportConfiguration.setCopyright("");	// Spec 4. Copyright Text
	        spdxReportConfiguration.setPackageDescription("");	// Spec 4. Package Detailed Description
	        
	        /** Spec. 7 REVIEW INFORMATION **/
	        
	        /** DEFAULT SETTING **/
	        spdxReportConfiguration.setFileLicenseConclusionType(SpdxFileLicenseConclusionType.NO_ASSERTION);	// COMPONENT_IDENTIFIED_LICENSES | NO_ASSERTION
	        System.out.println(LoginSessionEnt.getInstance().getProtexServerUrl());
	        spdxReportConfiguration.setProtexUrl(LoginSessionEnt.getInstance().getProtexServerUrl());

	        /** I don't know what it is **/
	        // Unused
	        spdxReportConfiguration.setReportComment("");
	        
	        // 3. setup report format
	        SpdxReportFormat reportFormat = SpdxReportFormat.RDF;	// HTML | RDF | RDF_AND_XLS_WRAPPED_IN_ZIP | XLS
	        
	        // 4. generate SPDX report
	        Report report = ProtexSDKAPIManager.getReportAPI().generateSpdxReport(projectId, spdxReportConfiguration, reportFormat);
	        
	        // file write
			pMessage = " > Save SPDX Document.\n";
			if(observer != null) {
				observer.pushMessageWithHeader(pMessage);
			}
			BufferedReader SPDXReportReader = null;
			BufferedWriter SPDXReportWriter = null;
			if(report != null && report.getFileContent() != null) {
				try {
					SPDXReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));
					SPDXReportWriter = new BufferedWriter(new FileWriter(targetFilePath));
					
					String tmpStr = null;
					while((tmpStr = SPDXReportReader.readLine()) != null) {
						SPDXReportWriter.write(tmpStr+"\n");
					}
					SPDXReportWriter.flush();
				} catch(IOException e) {
					e.printStackTrace();
				} finally {
					try {
						SPDXReportReader.close();
						SPDXReportWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return 0;
		} catch (SdkFault e) {
			e.printStackTrace();
		}
		return 1;
	}
}
