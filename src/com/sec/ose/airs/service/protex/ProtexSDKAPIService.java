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
package com.sec.ose.airs.service.protex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.StringSearchPatternOriginType;
import com.blackducksoftware.sdk.protex.component.custom.CustomComponentApi;
import com.blackducksoftware.sdk.protex.component.standard.StandardComponent;
import com.blackducksoftware.sdk.protex.component.standard.StandardComponentApi;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersionApi;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.DiscoveryApi;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.DeclaredIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.StringSearchIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentApi;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentRequest;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.ReportApi;
import com.blackducksoftware.sdk.protex.report.ReportFormat;
import com.blackducksoftware.sdk.protex.report.ReportSection;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.sdk.protex.report.ReportTemplateRequest;
import com.blackducksoftware.sdk.protex.report.SpdxAllLicenseInfoFromFileType;
import com.blackducksoftware.sdk.protex.report.SpdxFileLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxLicenseDeclarationType;
import com.blackducksoftware.sdk.protex.report.SpdxPackageLicenseConclusionType;
import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.blackducksoftware.sdk.protex.report.SpdxReportFormat;
import com.blackducksoftware.sdk.protex.user.UserApi;
import com.sec.ose.airs.domain.autoidentify.IdentificationInfo;
import com.sec.ose.airs.domain.autoidentify.ProtexIdentificationInfo;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class ProtexSDKAPIService {
	private static Log log = LogFactory.getLog(ProtexSDKAPIService.class);
	private static Long connectionTimeout = 30 * 60 * 1000L;
	
	public static final String IDENTIFY_REPORT_STRINGS[] = {
		"Resolution Type",
		"Discovery Type",
		"File/Folder",
		"Component",
		"Version",
		"License",
		"Usage",
		"%",
		"Matched File",
		"Search"
	};
	
	// TODO - Use spring!!
	String protexServerProxyClassName = "com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_3";
	Class<?> protexServerProxyClass = null;
	Object protexServerProxyObject = null;
	
	String protexServerIP = null;
	String userID = null;
	String password = null;
	
	private ProjectApi projectAPI = null;
	private UserApi userAPI = null;
	private ReportApi reportAPI = null;
	private LicenseApi licenseAPI = null;
	private BomApi bomAPI = null;
	private DiscoveryApi discoveryAPI = null;
	private IdentificationApi identificationAPI = null;
    private CodeTreeApi codeTreeAPI = null;
	private ComponentVersionApi componentVersionAPI = null;
	private CustomComponentApi customComponentAPI = null;
	private LocalComponentApi localComponentAPI = null;
	private StandardComponentApi standardComponentAPI = null;
//    private static FileComparisonApi fileComparisonAPI = null;
	
	private void setAPIs() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (protexServerProxyObject != null) {
			projectAPI = (ProjectApi)(protexServerProxyClass.getMethod("getProjectApi").invoke(protexServerProxyObject));
			userAPI = (UserApi)(protexServerProxyClass.getMethod("getUserApi").invoke(protexServerProxyObject));
			reportAPI = (ReportApi)(protexServerProxyClass.getMethod("getReportApi").invoke(protexServerProxyObject));
			licenseAPI = (LicenseApi)(protexServerProxyClass.getMethod("getLicenseApi").invoke(protexServerProxyObject));
			discoveryAPI = (DiscoveryApi)(protexServerProxyClass.getMethod("getDiscoveryApi").invoke(protexServerProxyObject));
			bomAPI = (BomApi)(protexServerProxyClass.getMethod("getBomApi").invoke(protexServerProxyObject));
			codeTreeAPI = (CodeTreeApi)(protexServerProxyClass.getMethod("getCodeTreeApi").invoke(protexServerProxyObject));
			componentVersionAPI = (ComponentVersionApi)(protexServerProxyClass.getMethod("getComponentVersionApi").invoke(protexServerProxyObject));
			identificationAPI = (IdentificationApi)(protexServerProxyClass.getMethod("getIdentificationApi").invoke(protexServerProxyObject));
			standardComponentAPI = (StandardComponentApi)(protexServerProxyClass.getMethod("getStandardComponentApi").invoke(protexServerProxyObject));
			customComponentAPI = (CustomComponentApi)(protexServerProxyClass.getMethod("getCustomComponentApi").invoke(protexServerProxyObject));
			localComponentAPI = (LocalComponentApi)(protexServerProxyClass.getMethod("getLocalComponentApi").invoke(protexServerProxyObject));
		}
	}
	
	private boolean testConnection() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SdkFault {
		if (protexServerProxyObject != null) {
	 	    userAPI.getCurrentUserHasServerFileAccess();
	 	    return true;
		}
		return false;	
	}
	
    public void init(String protexServerIP, String userID, String password) throws Exception {
		try {
			protexServerProxyClass = Class.forName(protexServerProxyClassName);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
    	
    	Class<?>[] parameterTypes = {String.class, String.class, String.class, Long.class};
    	Object[] args = {protexServerIP, userID, password, connectionTimeout};
    	
    	protexServerProxyObject = protexServerProxyClass.getConstructor(parameterTypes).newInstance(args);
    	this.setAPIs();
    	
    	try {
    		this.testConnection();
    	} catch (Exception e) {
    		log.error(e);
    		throw e;
    	}
    	
    	this.protexServerIP = protexServerIP;
    	this.userID = userID;
    	this.password = password;
    }
    
	public void login(String ip, String id, String pw) throws Exception {
		this.init(ip, id, pw);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	// AUTO Identify 
	/////////////////////////////////////////////////////////////////////////////////
	public String getSPDXReportContentString(String projectId, String packageName, String packageFileName, String organizationName, String creatorName, String creatorEmail) throws SdkFault {
        Report report = getSPDXReport(projectId, packageName, packageFileName, organizationName, creatorName, creatorEmail);
        if (report == null) {
        	return null;
        }

        
        StringBuilder sb = new StringBuilder();
        try {
        	BufferedReader SPDXReportReader = null;
			SPDXReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));

			String tmpStr = null;
			while((tmpStr = SPDXReportReader.readLine()) != null) {
				sb.append(tmpStr);
				sb.append("\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
		
		return sb.toString();
	}
	
	public Report getSPDXReport(String projectId, String packageName, String packageFileName, String organizationName, String creatorName, String creatorEmail) throws SdkFault {
        // 2. setup report configuration (below are full config that we can setup)
		SpdxReportConfiguration spdxReportConfiguration = getSPDXReportConfiguration(packageName, packageFileName, organizationName, creatorName, creatorEmail, protexServerIP);
        SpdxReportFormat reportFormat = SpdxReportFormat.RDF;	// HTML | RDF | RDF_AND_XLS_WRAPPED_IN_ZIP | XLS
        
        return reportAPI.generateSpdxReport(projectId, spdxReportConfiguration, reportFormat);
	}
	
	public List<StringSearchDiscovery> getStringSearchDiscoveries(String projectID, String path) {
		CodeTreeNode tmpCodeTreeNode = new CodeTreeNode();
		tmpCodeTreeNode.setNodeType(CodeTreeNodeType.FILE);
		tmpCodeTreeNode.setName(path);
		
		PartialCodeTree fileOnlyTree = new PartialCodeTree();
		fileOnlyTree.setParentPath("/");
        fileOnlyTree.getNodes().add(tmpCodeTreeNode);
		
		List<StringSearchPatternOriginType> searchOnly = new ArrayList<StringSearchPatternOriginType>(1);
		searchOnly.add(StringSearchPatternOriginType.STANDARD);
		searchOnly.add(StringSearchPatternOriginType.CUSTOM);
		
		try {
			return this.discoveryAPI.getStringSearchDiscoveries(projectID, fileOnlyTree, searchOnly);
		} catch (SdkFault e) {
			log.error(e.getMessage());
		}
		
		return null;	
	}
	
	public boolean addStringSearchIdentification(String projectId, String path, StringSearchIdentificationRequest req) {
		BomRefreshMode refreshMode = BomRefreshMode.ASYNCHRONOUS;
		
		try {
			this.identificationAPI.addStringSearchIdentification(projectId, path, req, refreshMode);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean addCodeMatchIdentification(String projectId, String path, CodeMatchIdentificationRequest req) {
		BomRefreshMode refreshMode = BomRefreshMode.ASYNCHRONOUS;
		
		try {
			this.identificationAPI.addCodeMatchIdentification(projectId, path, req, refreshMode);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean addDeclaredIdentification(String projectId, String path, DeclaredIdentificationRequest req) {
		BomRefreshMode refreshMode = BomRefreshMode.ASYNCHRONOUS;
		
		try {
			this.identificationAPI.addDeclaredIdentification(projectId, path, req, refreshMode);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	// AUTO Identify END
	/////////////////////////////////////////////////////////////////////////////////
	
	public HashMap<String, List<IdentificationInfo>> getAllPendingListMap(String projectId) {
		HashMap<String, List<IdentificationInfo>> pendingListMap = new HashMap<String, List<IdentificationInfo>>();
		Report report = this.getAllPendingListReport(projectId);
		
		BufferedReader XmlReportReader = null;
		if(report != null && report.getFileContent() != null) {
			try {
				XmlReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));
				// didn't use XML SAX Parser, because
				// 1. blackduck-XML format parsing error
				// 2. XML parser might be more inefficient in performance(speed/memory)  

				// move to data point
				while (!XmlReportReader.readLine().trim().startsWith("<tbody"));
				while (!XmlReportReader.readLine().trim().startsWith("<tbody"));
				
				// Read data lines
				// "File", 0
				// "Matched File",10
				//
				// HEADER:
				// 0	        <th nowrap="nowrap">File</th>
				// 1	        <th nowrap="nowrap">Size</th>
				// 2            <th nowrap="nowrap">File Line</th>
				// 3            <th nowrap="nowrap">Total Lines</th>
				// 4            <th nowrap="nowrap">Component</th>
				// 5            <th nowrap="nowrap">Version</th>
				// 6            <th nowrap="nowrap">License</th>
				// 7            <th nowrap="nowrap">Usage</th>
				// 8            <th nowrap="nowrap">Status</th>
				// 9            <th nowrap="nowrap">%</th>
				// 10           <th nowrap="nowrap">Matched File</th>
				// 11           <th nowrap="nowrap">Matched File Line</th>
				// 12           <th nowrap="nowrap">File Comment</th>
				// 13           <th nowrap="nowrap">Component Comment</th>
				
				// FORMAT:
				//				 <tr >
				//			     	 <td >
				//			     blowfish.c
				//			     </td>
				//			     	 <td align='right'>
				String line = XmlReportReader.readLine().trim();
				int rowNum = 0;
				while (line.startsWith("<tr")) {
					rowNum = 0;
					String key = null;
					IdentificationInfo info = new IdentificationInfo();
					
					// skip to data 
					line = XmlReportReader.readLine().trim();
					while (line.startsWith("<td")) {
						// read next line (data)
						line = XmlReportReader.readLine().trim();

						switch(rowNum) {
							case (0):
							{
								key = StringEscapeUtils.unescapeXml(line);
								break;
							}
							case (4):
							{
								info.setComponent(StringEscapeUtils.unescapeXml(line));
								break;
							}
							case (5):
							{
								info.setVersion(StringEscapeUtils.unescapeXml(line));
								break;
							}
							case (6):
							{
								info.setLicense(StringEscapeUtils.unescapeXml(line));
								break;
							}
							case (10):
							{
								info.setMatchedFile(StringEscapeUtils.unescapeXml(line));
								break;
							}
						}
						rowNum++;
						// </td>
						line = XmlReportReader.readLine();
						// find <td
						line = XmlReportReader.readLine().trim();
						
						if (line.equals("</tr>")) {
							break;
						}
					}

					if (!pendingListMap.containsKey(key)) {
						pendingListMap.put(key, new ArrayList<IdentificationInfo>());
					}
					pendingListMap.get(key).add(info);
					
					line = XmlReportReader.readLine().trim();
				}
				
			} catch(IOException e) {
				log.error("[ERROR] XML fileIO failed: " + e.getMessage());
				return null;
			}
		}
		
		return pendingListMap;
	}
	
	
	
	
	public Report getAllPendingListReport(String projectId) {		
		ReportTemplateRequest reportTemplate = new ReportTemplateRequest();

		reportTemplate.setTitle("Code Matches - Precision");
		reportTemplate.setForced(Boolean.TRUE);
		
		ReportSection section = new ReportSection();
		section.setLabel("Code Matches - Precision");
		section.setSectionType(ReportSectionType.CODE_MATCHES_PRECISION);
		reportTemplate.getSections().add(section);
		
		try {
			return this.reportAPI.generateAdHocProjectReport(projectId, reportTemplate, ReportFormat.HTML);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	private SpdxReportConfiguration getSPDXReportConfiguration(String packageName, String packageFileName, String organizationName, String creatorName, String creatorEmail, String protexUrl) {
		SpdxReportConfiguration spdxReportConfiguration = new SpdxReportConfiguration();

        // <name>testPackageName</name>
        // <packageFileName>testPackageFileName</packageFileName>
        spdxReportConfiguration.setPackageName(packageName);	// packages.name
    	spdxReportConfiguration.setPackageFileName(packageFileName);	// packages.fileName
		
		/** Spec. 2 SPDX DOCUMENT INFORMATION **/
    	spdxReportConfiguration.setDocumentComment("");
		
		/** Spec. 3 CREATION INFORMATION **/
        spdxReportConfiguration.setOrganization(organizationName);
//        spdxReportConfiguration.setOrganizationEmail("");
        spdxReportConfiguration.setCreatedBy(creatorName);
        spdxReportConfiguration.setCreatedByEMail(creatorEmail);          
        // <creator>Organization: testOrganization (testOrganizationEmail)</creator>
        // <creator>Tool: Black Duck Protex - version 6.2.0</creator>
        // <creator>Person: testCreatedBy (testCreatedByEMail)</creator>
        // AUTO : packages.created
        // <created>2013-01-23T14:03:54Z</created>

        spdxReportConfiguration.setCreatorComment("");
		
        /** Spec. 4 PACKAGE INFORMATION **/
//        spdxReportConfiguration.setPackageVersion(getJTextFieldPackageVersion().getText());	// packages.version
        // <versionInfo>testPackageVersion</versionInfo>

        // AUTO : packages.verificationCode
        // AUTO : packages.checksum
        spdxReportConfiguration.setPackageLicenseDeclarationType(SpdxLicenseDeclarationType.PROJECT_DECLARED_LICENSE);	// PROJECT_DECLARED_LICENSE | NO_ASSERTION	// packages.concludedLicense
        spdxReportConfiguration.setAllLicenseInfoFromFilesType(SpdxAllLicenseInfoFromFileType.IDENTIFIED_PLUS_STRING_SEARCH_MATCH_LICENSES);	// IDENTIFIED_PLUS_STRING_SEARCH_MATCH_LICENSES | NO_ASSERTION		// packages.allLicensesInfoFromFiles
        spdxReportConfiguration.setPackageLicenseConclusionType(SpdxPackageLicenseConclusionType.COMPONENT_IDENTIFIED_LICENSES);	// COMPONENT_IDENTIFIED_LICENSES | NO_ASSERTION	// packages.declaredLicense

        // Unused
        spdxReportConfiguration.setLicenseComment("");	// Spec 4. Comments On License (not availabel, protex error??)
        spdxReportConfiguration.setPackageDownloadUrl("");	// Spec 4. Package Download Location
        spdxReportConfiguration.setPackageSourceInformation("");	// Spec 4. Source Information	// (not availabel, protex error??)
        spdxReportConfiguration.setCopyright("");	// Spec 4. Copyright Text
        spdxReportConfiguration.setPackageDescription("");	// Spec 4. Package Detailed Description
        

        /** DEFAULT SETTING **/
        spdxReportConfiguration.setFileLicenseConclusionType(SpdxFileLicenseConclusionType.COMPONENT_IDENTIFIED_LICENSES);	// COMPONENT_IDENTIFIED_LICENSES | NO_ASSERTION
//        spdxReportConfiguration.setProtexUrl(LoginSessionEnt.getInstance().getProtexServerUrl());
        spdxReportConfiguration.setProtexUrl("http://127.0.0.1/");

        /** I don't know what it is **/
        // Unused
        spdxReportConfiguration.setReportComment("");
        
        return spdxReportConfiguration;
	}
	
	public HashMap<String, List<ProtexIdentificationInfo>> getIdentificationInfoList(String projectId) {
		HashMap<String, List<ProtexIdentificationInfo>> identificationInfoFiles = new HashMap<String, List<ProtexIdentificationInfo>>();
		
		ReportTemplateRequest reportTemplateRequest = new ReportTemplateRequest();
		reportTemplateRequest.setTitle("Identified Files");
		reportTemplateRequest.setForced(Boolean.TRUE);
		
		ReportSection section = new ReportSection();
		section.setLabel("Summary");
		section.setSectionType(ReportSectionType.IDENTIFIED_FILES);
		reportTemplateRequest.getSections().add(section);

		Report report = null;
		try {
			report = this.getReportAPI().generateAdHocProjectReport(projectId, reportTemplateRequest, ReportFormat.XLS);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}

		BufferedReader XmlReportReader = null;
		if(report != null && report.getFileContent() != null) {
			try {
				XmlReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));
				// didn't use XML SAX Parser, because
				// 1. blackduck-XML format parsing error
				// 2. XML parser might be more inefficient in performance(speed/memory)  

				// move to data point
				while (!XmlReportReader.readLine().startsWith("<Row ss:Index=\"2\""));	// Top Disclaimer ����
				while (!XmlReportReader.readLine().startsWith("<Row ss:Index=\"2\""));	// move to data point

				
				ArrayList<Integer> seq = new ArrayList<Integer>();
				String line = XmlReportReader.readLine();
				
				// read index in seq[]
				// seq[0] has Report_strings info on the first line
				int rowNum = 1;
				while (!line.startsWith("</Row")) {
					String content = line.replaceAll("\\<.*?\\>", "");
					if (!"".equals(content)) {
						for (int fi = 0; fi < ProtexSDKAPIService.IDENTIFY_REPORT_STRINGS.length; fi++) {
							if (ProtexSDKAPIService.IDENTIFY_REPORT_STRINGS[fi].equals(content)) {
								seq.add(fi);
								break;
							}
						}
						if (seq.size() < rowNum) {
							seq.add(-1);
						}
					}
					line = XmlReportReader.readLine();
					rowNum++;
				}
				
				// Read data lines
				// "Resolution Type", 0
				// "Discovery Type",1
				// "File/Folder",2
				// "Component",3
				// "Version",4
				// "License",5
				// "Usage",6
				// "%",7
				// "Matched File",8
				// "Search"9
				while (!line.startsWith("</Table")) {
					rowNum = 0;
					ProtexIdentificationInfo info = new ProtexIdentificationInfo();

					// skip to data lines
					do {
						line = XmlReportReader.readLine();
						
						// when empty project (no pending/identify list)
						if ("</Table>".equals(line)) {
							return null;
						}
						
					} while (!line.startsWith("<Cel"));
					
					while (!line.startsWith("</Row")) {
						String content = line.replaceAll("\\<.*?\\>", "");

						String replaceCharEmpty = StringEscapeUtils.unescapeXml(content);
						content = replaceCharEmpty;

						switch(seq.get(rowNum)) {
							case (0):
							{
								info.setResolutionType(content);
								break;
							}
							case (1):
							{
								info.setDiscoveryType(content);
								break;
							}
							case (2):
							{
								info.setFilePath(content.substring(1));
								break;
							}
							case (3):
							{
								info.setComponent(content);
								break;
							}
							case (4):
							{
								info.setVersion(content);
								break;
							}
							case (5):
							{
								info.setLicense(content);
								break;
							}
							case (6):
							{
								info.setUsage(content);
								break;
							}
							case (7):
							{
								info.setPercentage(content);
								break;
							}
							case (8):
							{
								info.setMatchedFile(content);
								break;
							}
							case (9):
							{
								info.setSearch(content);
								break;
							}
						}
						rowNum++;
						line = XmlReportReader.readLine();
					}
					
					if (!"".equals(ObjectUtils.toString(info.getLicense()))) {
						// INSERT
						if (!identificationInfoFiles.containsKey(info.getFilePath())) {
							identificationInfoFiles.put(info.getFilePath(), new ArrayList<ProtexIdentificationInfo>(1));						
						}
						// Remove IdentificationInfo duplication
						boolean infoExist = false;
						List<ProtexIdentificationInfo> infoList = identificationInfoFiles.get(info.getFilePath());
						for (ProtexIdentificationInfo item : infoList) {
							if (item.toString().equals(info.toString())) {
								infoExist = true;
								break;
							}
						}
						if (!infoExist) {
							identificationInfoFiles.get(info.getFilePath()).add(info);
						}
						
					}
					
					line = XmlReportReader.readLine();
				}
				
			} catch(IOException e) {
				log.error("[ERROR] XML fileIO failed: " + e.getMessage());
				return null;
			}
		}
		
		//System.exit(0);		
		return identificationInfoFiles;
	}
	

	public void setProtexServerProxyObject(Object protexServerProxyObject) throws Exception {
		try {
			protexServerProxyClass = Class.forName(protexServerProxyClassName);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
		this.protexServerProxyObject = protexServerProxyObject;
		this.setAPIs();
	}

	
	
	public String getLicenseIdByName(String licenseName) {
		try {
			return this.getLicenseAPI().getLicenseByName(licenseName).getLicenseId();
		} catch (SdkFault e) {
			log.warn(e.getMessage());
		} catch (NullPointerException e) {
			log.warn(e.getMessage());
		}
		return null;
	}
	
	public String getComponentNameByProjectIDAndComponentID(String projectID, String ComponentID) {
		try {
			return this.getProjectAPI().getComponentById(projectID, ComponentID).getName();
		} catch (SdkFault e) {
			log.warn(e.getMessage());
		} catch (NullPointerException e) {
			log.warn(e.getMessage());
		}
		return null;	
	}
	
	public String getLocalComponentIDByProjectIDAndComponentName(String projectID, String componentName) {
		try {
			return this.getLocalComponentAPI().getLocalComponentByName(projectID, componentName).getComponentId();
		} catch (SdkFault e) {
			log.warn(e.getMessage());
		} catch (NullPointerException e) {
			log.info(e.getMessage());
		}
		return null;
	}
	
	public String getCustomComponentIDByComponentName(String componentName) {
		try {
			return this.getCustomComponentAPI().getCustomComponentByName(componentName).getComponentId();	        
		} catch (SdkFault e) {
			log.warn(e.getMessage());
		} catch (NullPointerException e) {
			log.info(e.getMessage());
		}
		return null;
	}
	
	public String getComponentIDByName(String componentName) {
		try {
			StandardComponent sc = null;
			sc = this.getStandardComponentAPI().getStandardComponentByName(componentName);
			if (sc != null) {
				return sc.getComponentId();
			} else {
				return null;
			}
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
	}
	public ProtexIdentificationInfo getComponentVersionIDWithNames(String componentName, String versionName) {
		try {
			ComponentVersion cv = this.getComponentVersionAPI().getComponentVersionByName(componentName, versionName);
			if (cv != null) {
				ProtexIdentificationInfo info = new ProtexIdentificationInfo();
				info.setComponentID(cv.getComponentId());
				info.setVersionID(cv.getVersionId());
				
				return null;
			} else {
				return null;
			}
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public ProtexIdentificationInfo getComponentVersionNamesWithIDs(String componentID, String versionID) {
		try {
			ComponentVersion cv = this.getComponentVersionAPI().getComponentVersionById(componentID, versionID);
			if (cv != null) {
				ProtexIdentificationInfo info = new ProtexIdentificationInfo();
				info.setComponent(cv.getComponentName());
				info.setVersion(cv.getVersionName());
				
				return info;
			} else {
				return null;
			}
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public String createLocalComponent(String projectID, String componentName, String licenseID, String licenseName) {
		try {
			LocalComponentRequest localComponentRequest = new LocalComponentRequest();
	        localComponentRequest.setContextProjectId(projectID);
	        localComponentRequest.setName(componentName);
	        localComponentRequest.setLicenseText(licenseName.getBytes());
	        localComponentRequest.setBasedOnLicenseId(licenseID);
	        return this.getLocalComponentAPI().createLocalComponent(localComponentRequest);
		} catch (SdkFault e) {
			log.warn("createLocalComponent() failed: " + e.getMessage());
			return null;
		}
	}
	
	
	
	
	
	// getter and setter
	public ProjectApi getProjectAPI() {
		return projectAPI;
	}

	public void setProjectAPI(ProjectApi projectAPI) {
		this.projectAPI = projectAPI;
	}

	public UserApi getUserAPI() {
		return userAPI;
	}

	public void setUserAPI(UserApi userAPI) {
		this.userAPI = userAPI;
	}

	public ReportApi getReportAPI() {
		return reportAPI;
	}

	public void setReportAPI(ReportApi reportAPI) {
		this.reportAPI = reportAPI;
	}

	public LicenseApi getLicenseAPI() {
		return licenseAPI;
	}

	public void setLicenseAPI(LicenseApi licenseAPI) {
		this.licenseAPI = licenseAPI;
	}

	public BomApi getBomAPI() {
		return bomAPI;
	}

	public void setBomAPI(BomApi bomAPI) {
		this.bomAPI = bomAPI;
	}

	public DiscoveryApi getDiscoveryAPI() {
		return discoveryAPI;
	}

	public void setDiscoveryAPI(DiscoveryApi discoveryAPI) {
		this.discoveryAPI = discoveryAPI;
	}

	public IdentificationApi getIdentificationAPI() {
		return identificationAPI;
	}

	public void setIdentificationAPI(IdentificationApi identificationAPI) {
		this.identificationAPI = identificationAPI;
	}

	public ComponentVersionApi getComponentVersionAPI() {
		return componentVersionAPI;
	}

	public void setComponentVersionAPI(ComponentVersionApi componentVersionAPI) {
		this.componentVersionAPI = componentVersionAPI;
	}

	public CustomComponentApi getCustomComponentAPI() {
		return customComponentAPI;
	}

	public void setCustomComponentAPI(CustomComponentApi customComponentAPI) {
		this.customComponentAPI = customComponentAPI;
	}

	public LocalComponentApi getLocalComponentAPI() {
		return localComponentAPI;
	}

	public void setLocalComponentAPI(LocalComponentApi localComponentAPI) {
		this.localComponentAPI = localComponentAPI;
	}

	public StandardComponentApi getStandardComponentAPI() {
		return standardComponentAPI;
	}

	public void setStandardComponentAPI(StandardComponentApi standardComponentAPI) {
		this.standardComponentAPI = standardComponentAPI;
	}

	public CodeTreeApi getCodeTreeAPI() {
		return this.codeTreeAPI;
	}

	public void setCodeTreeAPI(CodeTreeApi codeTreeAPI) {
		this.codeTreeAPI = codeTreeAPI;
	}
}
