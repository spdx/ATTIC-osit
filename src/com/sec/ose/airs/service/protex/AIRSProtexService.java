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
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spdx.rdfparser.InvalidSPDXAnalysisException;
import org.spdx.rdfparser.SPDXDocument;
import org.spdx.rdfparser.SPDXDocumentFactory;
import org.spdx.rdfparser.SPDXFile;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationDirective;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.DeclaredIdentificationRequest;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.blackducksoftware.sdk.protex.project.codetree.identification.StringSearchIdentificationRequest;
import com.blackducksoftware.sdk.protex.report.Report;
import com.sec.ose.airs.Properties;
import com.sec.ose.airs.domain.autoidentify.AutoIdentifyOptions;
import com.sec.ose.airs.domain.autoidentify.AutoIdentifyResult;
import com.sec.ose.airs.domain.autoidentify.IdentificationInfo;
import com.sec.ose.airs.domain.autoidentify.ProtexIdentificationInfo;
import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;
import com.sec.ose.airs.service.AIRSService;
import com.sec.ose.airs.service.AutoIdentifyService;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class AIRSProtexService implements AIRSService {
	private static Log log = LogFactory.getLog(AIRSProtexService.class);
	
	ProtexSDKAPIService svc = new ProtexSDKAPIService();
	
	// string literals
	final String LICENSE_DELIMITER = "AIRS_LABELMAP_LICENSEID";
	final String COMPONENT_VERSION_DELIMITER = "AIRS_LABELMAP_COMPONENTID_VERSIONID";

	// license DB maps for AUTO Identify
	private HashMap<String, String> licenseLabelMap = new HashMap<String, String>();
	private HashMap<String, String> componentVersionLabelMap = new HashMap<String, String>();
	private List<String> notUniqueComponentVersionLabelList = new ArrayList<String>();
	private HashMap<String, String> localComponentNameMap = new HashMap<String, String>();
	
	private HashMap<String, String> customLicenseIDMap = new HashMap<String, String>();
	private HashMap<String, String> localComponentIDMap = new HashMap<String, String>();
	private HashMap<String, String> customComponentIDMap = new HashMap<String, String>();
	
	public ProtexSDKAPIService getProtexSDKAPIService() {
		return svc;
	}
	
	@Override
	public void init(String protexServerIP, String userID, String password) throws Exception {
		svc.init(protexServerIP, userID, password);		
	}
	
	@Override
	public boolean export(String projectId, String targetFilePath,
			String packageName, String packageFileName,
			String organizationName, String creatorName, String creatorEmail,
			PrintStream out) throws Exception {
		out.println("Start SPDX Export - ProjectId:" + projectId);
		
		out.println(" > Generate SPDX Report Contents from server...");
        Report report = svc.getSPDXReport(projectId, packageName, packageFileName, organizationName, creatorName, creatorEmail);
        if (report == null) {
        	return false;
        }

        out.println(" > Get project Identification info from server...");
        
        HashMap<String, String> exportLicenseLabelMap = new HashMap<String, String>();
        HashMap<String, String> exportComponentVersionLabelMap = new HashMap<String, String>();
        HashMap<String, String> duplicatedExportComponentVersionLabelMap = new HashMap<String, String>();
        
		HashMap<String, List<ProtexIdentificationInfo>> identificationInfoList = this.getIdentificationInfoList(projectId, exportLicenseLabelMap, exportComponentVersionLabelMap, duplicatedExportComponentVersionLabelMap);
        
		// when empty project (no pending/identify list) 
		if (identificationInfoList == null) {
			out.println("project: " + projectId + " is empty or not analyzed yet.");
			return false;
		}
		
		//////////////////////////////////////////////////////
		// ADD Identification Information into file comment
		SPDXDocument doc; 
		try {
	        StringBuilder sb = new StringBuilder();
			BufferedReader SPDXReportReader = null;
			SPDXReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(), "UTF-8"));
			String tmpStr = null;
			while((tmpStr = SPDXReportReader.readLine()) != null) {
				sb.append(tmpStr);
				sb.append("\n");
			}
			
			out.println(" > Rebuild SPDX Report...");
			
			tmpStr = sb.toString(); sb = null; // for GC
			byte[] bytes = tmpStr.getBytes("UTF-8"); tmpStr = null; // for GC
			
			InputStream is = new ByteArrayInputStream(bytes); 
			doc = SPDXDocumentFactory.creatSpdxDocument(is, null, null);
			is.close();
			
			SPDXFile[] fileList = null;
			if(doc.getSpdxPackage() != null) {
				fileList = doc.getSpdxPackage().getFiles();
			}
			
			out.println(" > Write Identification info to SPDX Report...");

			for (Map.Entry<String, List<ProtexIdentificationInfo>> entrySet : identificationInfoList.entrySet()) {
				String identifyFilePath = entrySet.getKey();
				List<ProtexIdentificationInfo> infoList = entrySet.getValue();
				
				String fileGetName = "";
				for (SPDXFile file : fileList) {
					fileGetName = file.getName();
					if (fileGetName.equals(identifyFilePath)) {
						String infoStr = "";
						for (ProtexIdentificationInfo info : infoList) {
							infoStr += info + "\n";
						}
						file.setComment(infoStr + "\n" + file.getComment());
						break;
					}
				}
			}
		
			// Write document/AIRS version
			String ver = "\n\nSPDX-AIRS:" + Properties.AIRS_SPDX_VERSION + "/AIRS:" + Properties.VERSION + "\n\n";
			
			// Write labelmap
			sb = new StringBuilder();
			sb.append(LICENSE_DELIMITER);
			sb.append("\n");
			for (Map.Entry<String, String> entrySet : exportLicenseLabelMap.entrySet()) {
				sb.append(entrySet.getKey());
				sb.append("=");
				sb.append(entrySet.getValue());
				sb.append("\n");				
			}
			sb.append(COMPONENT_VERSION_DELIMITER);
			sb.append("\n");
			for (Map.Entry<String, String> entrySet : exportComponentVersionLabelMap.entrySet()) {
				sb.append(entrySet.getKey());
				sb.append("=");
				sb.append(entrySet.getValue());
				sb.append("\n");
			}
			for (Map.Entry<String, String> entrySet : duplicatedExportComponentVersionLabelMap.entrySet()) {
				sb.append(entrySet.getKey());
				sb.append("=");
				sb.append(entrySet.getValue());
				sb.append("\n");
				log.debug("Duplicated: " + entrySet.getKey() + "/" + entrySet.getValue());
			}
			sb.append("\n");
			
			doc.getCreatorInfo().setComment(doc.getCreatorInfo().getComment() + ver + sb.toString());
			doc.getModel().write(new FileOutputStream(targetFilePath));
			doc.getModel().close();
			
			out.println("Success exporting SPDX to " + targetFilePath);
			
		} catch(IOException e) {
			log.error(e.getMessage());
			return false;
		} catch (InvalidSPDXAnalysisException e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean exportByProjectName(String projectName,
			String targetFilePath, String packageName, String packageFileName,
			String organizationName, String creatorName, String creatorEmail,
			PrintStream out) throws Exception{
		
		return export(svc.getProjectAPI().getProjectByName(projectName).getProjectId(), targetFilePath, packageName, packageFileName, organizationName, creatorName, creatorEmail, out);
	}

	
	protected HashMap<String, List<ProtexIdentificationInfo>> getIdentificationInfoList(String projectId, HashMap<String, String> exportLicenseLabelMap, HashMap<String, String> exportComponentVersionLabelMap, HashMap<String, String> duplicatedExportComponentVersionLabelMap) {
		HashMap<String, List<ProtexIdentificationInfo>> identificationInfoFiles = svc.getIdentificationInfoList(projectId);
		// when empty project (no pending/identify list) 
		if (identificationInfoFiles == null)
			return null; 
		
		for (Map.Entry<String, List<ProtexIdentificationInfo>> entrySet : identificationInfoFiles.entrySet()) {
			String filePath = entrySet.getKey();
			
			List<ProtexIdentificationInfo> list = this.getIDsFromIdentificationResult(projectId, filePath);
			for (ProtexIdentificationInfo info : list) {
//if (!info.getFilePath().equals("libiconv-1.9.1/m4/iconv.m4"))
//	continue;
//else
//	System.out.println("gogo");
				/////////////////////////////////
				// 1. license lablemap
				/////////////////////////////////
				if (!exportLicenseLabelMap.containsKey(info.getLicense())) {
					exportLicenseLabelMap.put(info.getLicense(), info.getLicenseID());
				}
				
				///////////////////////////////////
				// 2-1. get protex Component/Version IDs
				///////////////////////////////////
				if (info.getVersionID() == null || "null".equalsIgnoreCase(info.getVersionID()) || "Unspecified".equalsIgnoreCase(info.getVersionID())) {
					info.setComponent(svc.getComponentNameByProjectIDAndComponentID(projectId, info.getComponentID()));
				} else {
					ProtexIdentificationInfo nameInfo = svc.getComponentVersionNamesWithIDs(info.getComponentID(), info.getVersionID());
					if (nameInfo == null) {
						continue;
					}
					info.setComponent(nameInfo.getComponent());
					info.setVersion(nameInfo.getVersion());
				}

				///////////////////////////////////
				// 2-2. update protex Component/Version Lable map
				///////////////////////////////////
				// couldn't find component ID ... then skip
				String key = info.getComponent() + "#" + info.getVersion();					
				String value = info.getComponentID() + "#" + info.getVersionID();
				if (!exportComponentVersionLabelMap.containsKey(key)) {
					if (!"#".equals(key)) {
						exportComponentVersionLabelMap.put(key, value);
					}
				} else {
					// check component/version name+id not unique  
					if (!value.equals(exportComponentVersionLabelMap.get(key))) { 
						duplicatedExportComponentVersionLabelMap.put(key, value);
					}
				}
			}
		}
		
		return identificationInfoFiles;
	}
	

	protected List<ProtexIdentificationInfo> getIDsFromIdentificationResult(String projectId, String filePath) {
		List<ProtexIdentificationInfo> list = new ArrayList<ProtexIdentificationInfo>();
		
		PartialCodeTree partialCodeTree = null;
		try {
			partialCodeTree = svc.getCodeTreeAPI().getCodeTree(projectId, "/" + filePath, 0, Boolean.TRUE);
			List<CodeTreeIdentificationInfo> idInfos = svc.getIdentificationAPI().getEffectiveIdentifications(projectId, partialCodeTree);

			for (CodeTreeIdentificationInfo idInfo : idInfos) {
				for (Identification id : idInfo.getIdentifications()) {
					ProtexIdentificationInfo info = new ProtexIdentificationInfo();
					info.setComponentID(id.getIdentifiedComponentId());
					info.setVersionID(id.getIdentifiedVersionId());
					info.setLicenseID(id.getIdentifiedLicenseInfo().getLicenseId());
					info.setLicense(id.getIdentifiedLicenseInfo().getName());
					list.add(info);					
				}
			}
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
		
		return list;
	}
	
	protected ProtexIdentificationInfo getIdentificationDataFromIdentificationResult(String projectId, ProtexIdentificationInfo orgInfo) {
		ProtexIdentificationInfo info = new ProtexIdentificationInfo();
		
		PartialCodeTree partialCodeTree = null;
		try {
			partialCodeTree = svc.getCodeTreeAPI().getCodeTree(projectId, "/" + orgInfo.getFilePath(), 0, Boolean.TRUE);
			List<CodeTreeIdentificationInfo> idInfos = svc.getIdentificationAPI().getEffectiveIdentifications(projectId, partialCodeTree);
			
			for (CodeTreeIdentificationInfo idInfo : idInfos) {
				for (Identification id : idInfo.getIdentifications()) {
					String name = svc.getComponentNameByProjectIDAndComponentID(projectId, id.getIdentifiedComponentId());
					if (name.equals(orgInfo.getComponent())) {
						info.setComponentID(id.getIdentifiedComponentId());
						info.setVersionID(id.getIdentifiedVersionId());
						info.setLicenseID(id.getIdentifiedLicenseInfo().getLicenseId());
						info.setLicense(id.getIdentifiedLicenseInfo().getName());
					}
				}
				if (info.getComponentID() != null) {
					break;
				} else {
					// if reported component name is different from identified component name,
					// then compare it to discovery
					ProtexIdentificationInfo discoveryInfo = this.getComponentVersionIDFromDiscovery(projectId, orgInfo.getFilePath(), orgInfo.getMatchedFile());
					for (Identification id : idInfo.getIdentifications()) {
						if (discoveryInfo.getComponentID().equals(id.getIdentifiedComponentId())) {
							info.setComponentID(id.getIdentifiedComponentId());
							info.setVersionID(id.getIdentifiedVersionId());
							info.setLicenseID(id.getIdentifiedLicenseInfo().getLicenseId());
							info.setLicense(id.getIdentifiedLicenseInfo().getName());
						}
					}
				}
			}

		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
		
		return info;
	}
	
	
	protected ProtexIdentificationInfo getComponentVersionIDFromDiscovery(String projectId, String filePath, String matchedFile) {
		ProtexIdentificationInfo info = new ProtexIdentificationInfo();

		PartialCodeTree partialCodeTree = null;
		try {
			partialCodeTree = svc.getCodeTreeAPI().getCodeTree(projectId, "/" + filePath, 0, Boolean.TRUE);

			List<CodeMatchType> precisionOnly = new ArrayList<CodeMatchType>(1);
			precisionOnly.add(CodeMatchType.PRECISION);
			List<CodeMatchDiscovery> discoveries = null;
			discoveries = svc.getDiscoveryAPI().getCodeMatchDiscoveries(projectId, partialCodeTree, precisionOnly);
			if (discoveries.size() != 0) {
				for (CodeMatchDiscovery discovery : discoveries) {
					if (discovery.getMatchingFileLocation().getFilePath().equals(matchedFile)) {
						info.setComponentID(discovery.getMatchingComponentId());
						info.setVersionID(discovery.getMatchingVersionId());
						break;
					}
				}
			}
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}

		return info;
	}
	

	
	@Override
	public AutoIdentifyResult autoIdentify(List<String> SPDXFileNameList, String targetProjectId, PrintStream out) {
		out.println("Start AutoIdentify to projectId: " + targetProjectId);
		out.println(" > Analyze SPDX Document(s) ...");
		AutoIdentifyService aiSvc = new AutoIdentifyService();
		AutoIdentifyResult aiResult = new AutoIdentifyResult();
		
		
		/////////////////////////////////////////////////////////////
		// 1. create package from spdx file and insert it to local db
		// and hold package id list
		// and Build Label map
		List<Integer> pkgIdList = new ArrayList<Integer>();
		for(String path : SPDXFileNameList) {
			out.println(" >> Read identification info from file: " + path);
			
			try {
				SPDXDocument doc = null;
				try {
					doc = SPDXDocumentFactory.creatSpdxDocument(path);
				} catch (IOException e) {
					log.error(e.getMessage());
					throw e;
				} catch (InvalidSPDXAnalysisException e) {
					log.error(e.getMessage());
					throw e;
				}

				String comment = doc.getCreatorInfo().getComment();
				
				// parsing label maps
				String[] lines = StringUtils.split(comment, "\n");
				boolean licenseLine = false;
				boolean compVerLine = false;
				
				
				for (String line : lines) {
					if (LICENSE_DELIMITER.equals(line)) {
						licenseLine = true;
						compVerLine = false;
						continue;
					} else if (COMPONENT_VERSION_DELIMITER.equals(line)) {
						compVerLine = true;
						licenseLine = false;
						continue;
					} else if ("\n".equals(line)) {
						licenseLine = compVerLine = false;
					}
					
					String[] items = StringUtils.split(line, "=");
					if (licenseLine) {
						if (!licenseLabelMap.containsKey(items[0])) {
							licenseLabelMap.put(items[0], items[1]);
						}
					} else if (compVerLine) {
						// save duplicated line
						if (!componentVersionLabelMap.containsKey(items[0])) {
							componentVersionLabelMap.put(items[0], items[1]);
						} else {
							notUniqueComponentVersionLabelList.add(line.trim());
						}
					}
				}
				for (Map.Entry<String, String> entrySet : componentVersionLabelMap.entrySet()) {
					// find not unique data in labelmap and move it to notUniqueComponentVersionLabelList
					for (String label : notUniqueComponentVersionLabelList) {
						if (entrySet.getKey().equals(StringUtils.split(label, "=")[0])) {
							notUniqueComponentVersionLabelList.add(entrySet.getKey() + "=" + entrySet.getValue());
							componentVersionLabelMap.remove(entrySet.getKey());
						}
					}
				}
				
				SPDXPackageDTO pkg = aiSvc.convertParserFormatToAIRSDB(doc);
				aiResult.getSourceSPDXPackageList().add(pkg);
				pkgIdList.add(aiSvc.insertSPDXPackageDataOnlyHavingIdentificationInfo(pkg));
				
				// FOR GC (reduce memory consumption ...)
				pkg.setFileList(null);
			} catch (Exception e) {
				log.error("SPDX Document Parsing failed: " + path + "\n" + e.getMessage());
				return null;
			}
		}
		
		out.println(" > Analyze current project information ...");
		out.println(" >> Get current project information by SPDX report generation ...");
		///////////////////////////////////////////////////////////
		// 2. Create current project's SPDX document and Pending list data
		String currentProjectSPDXContent;
		try {
			currentProjectSPDXContent = svc.getSPDXReportContentString(targetProjectId, "org", "orgFile", "sec", "system", "");
		} catch (SdkFault e1) {
			log.error(e1.getMessage());
			out.println("Error occured when creating current project's SPDX document");
			return null;
		}
		out.println(" >> Get all pending list ...");
		HashMap<String, List<IdentificationInfo>> pendingListMap = svc.getAllPendingListMap(targetProjectId);
		
		
		// parsing it, and hold it in memory
		// TODO - in phase 2, it better to go to DB.
		SPDXPackageDTO tgtPkg = null;
		try {
			// TODO - encoding
			tgtPkg = aiSvc.parseSPDXDocumentContent(currentProjectSPDXContent, "UTF-8");
		} catch (Exception e) {
			log.error("Couldn't analyze current project information.\nPlease contact admin.\n" + e.getMessage());
			return null;
		}
		
		out.println(" > Start Auto Identify processing ...\n");
		
		///////////////////////////////////////////////////////////		
		// 3. Auto Identify!
		AutoIdentifyOptions options = new AutoIdentifyOptions();
		
		String pMessage = null;
		int aiCount = 0;
		int aiError = 0;
		int multipleMatchedCount = 0;
		int noMathcedFileCount = 0;
		int totalFileCount = 0;
		int targetFileCount = tgtPkg.getFileList().size();
		for (SPDXFileDTO targetFile : tgtPkg.getFileList()) {
			totalFileCount++;
			
			out.println( "(" + totalFileCount + "/" + targetFileCount + ") " + targetFile.getName() + " checking...");
			// add identification info for overwrite option
			targetFile.setIdentificationInfoList(pendingListMap.get(targetFile.getName()));
			List<SPDXFileDTO> fileList = aiSvc.getFileListByComparingHashCodeAndIdentificaionInfo(pkgIdList, targetFile, options);

			if (fileList != null) {
				// AI success
				if (fileList.size() == 1) {
					if (this.identifyUsingSPDX(targetProjectId, fileList.get(0), targetFile, out)) {
						pMessage = targetFile.getName() + ": Auto-identified.";
						aiCount++;
					} else {
					// CANNOT Happen for now.
						log.error("Filelist has a same matched file, but no ident info. THIS SHOULDn't happen");
						pMessage = targetFile.getName() + ": has a same matched file, but no identification info";
						aiError++;
					}
				} else if (fileList.size() > 1) {
					pMessage = targetFile.getName() + ": has multiple matched files with different identification data.";
					multipleMatchedCount++;
					aiResult.getFailedPairList().add(aiResult.new MatchedFilePair(targetFile, fileList));
				// No Matched
				} else if (fileList.size() == 0){
					pMessage = targetFile.getName() + ": has no matched file";
					noMathcedFileCount++;
				}
			// AI FAIL
			} else {
				log.error("Auto-identify Failed.THIS SHOULDn't happen");
				pMessage = targetFile.getName() + ": Auto-identify Failed.";
			}
			
			out.println(" >> " + pMessage);
		}
		
		/////////////////////////////////////////////////////
		// refresh bom
		// moved from pattern match
		try {
			svc.getBomAPI().refreshBom(targetProjectId, Boolean.TRUE, Boolean.FALSE);
			out.println("BOM Refreshing (it takes some minutes) ...");
		} catch (SdkFault e) {
			log.error("error when refresh bom lastly");
		}
		/////////////////////////////////////////////////////
		
		// empty source spdx package in local db
		for (int packageId : pkgIdList)
			aiSvc.deleteSPDXPackage(packageId);
		
		aiResult.setAiCount(aiCount);
		aiResult.setAiError(aiError);
		aiResult.setMultipleMatchedCount(multipleMatchedCount);
		aiResult.setNoMathcedFileCount(noMathcedFileCount);
		aiResult.setTotalFileCount(totalFileCount);
		
		out.println("AI Finished!");
		out.println("+----------------------------------------------+");
		out.println(" Total File count : " + aiResult.getTotalFileCount());
		out.println(" Auto-identified count : " + aiResult.getAiCount());
		out.println(" same matched file, but error : " + aiError);
		out.println(" multiple matched count : " + multipleMatchedCount);
		out.println(" no matched file count : " + noMathcedFileCount);
		out.println("+----------------------------------------------+");
		
		return aiResult;
	}

	@Override
	public AutoIdentifyResult autoIdentifyByProjectName(List<String> SPDXFileNameList,
			String targetProjectName, PrintStream out) {
		try {
			return this.autoIdentify(SPDXFileNameList, svc.getProjectAPI().getProjectByName(targetProjectName).getProjectId(), out);
		} catch (SdkFault e) {
			log.error(e.getMessage());
			return null;
		}
	}

	
	@Override
	public boolean identifyUsingSPDX(String targetProjectId, SPDXFileDTO sourceSpdxFile, SPDXFileDTO targetSpdxFile, PrintStream out) {
		
		ProtexIdentificationInfoService iiServ = new ProtexIdentificationInfoService();
		List<ProtexIdentificationInfo> infoList = iiServ.extractIdentificationInfoList(sourceSpdxFile); 

		if (infoList == null || infoList.size() < 1)
			return false;
		
		out.println(" > Identifying " + targetSpdxFile.getName() );
		
		for (ProtexIdentificationInfo info : infoList) {
			log.debug(" >> Identifing: " + info);
			IdentificationInfo targetInfo = null;
			if (targetSpdxFile.getIdentificationInfoList() != null) {
				for (IdentificationInfo pendingInfo : targetSpdxFile.getIdentificationInfoList()) {
					if (pendingInfo.getMatchedFile().equals(info.getMatchedFile())) {
						targetInfo = pendingInfo;
						break;
					}
				}
			}
			if (targetInfo == null) {
				targetInfo = new IdentificationInfo();
			}
			targetInfo.setFilePath(targetSpdxFile.getName());
			
			boolean identifyResult = this.identifyRequest(targetProjectId, info, targetInfo);
			if (identifyResult == false) log.error(">>>Identify failed - " + info.getFilePath());
			if (!identifyResult)
				continue;
		}
		return true;
	}
	

	protected boolean identifyRequest(String targetProjectId, ProtexIdentificationInfo srcInfo, IdentificationInfo targetInfo) {
		
//if (!srcInfo.getFilePath().endsWith("/immodules/ibus/ibus_imcontext.c")) {
//	return false;
//}
//		

		String newLicenseId = this.licenseLabelMap.get(srcInfo.getLicense());		
		String newComponentName = srcInfo.getComponent();
		String newVersionName = srcInfo.getVersion();
		String newComponentId = "";
		String newVersionID = "";
		
		///////////////////////////////////////////////////////////
		// 1. License valid check
		///////////////////////////////////////////////////////////
		String newLicenseName = srcInfo.getLicense();
		// custom license
		if (newLicenseId == null) {
			log.error("License ID of \"" + newLicenseName + "\" doesn't exists.");
			return false;
		}
		if (newLicenseId.startsWith("c_")) {
			if (customLicenseIDMap.containsKey(srcInfo.getLicense())) {
				newLicenseId = customLicenseIDMap.get(srcInfo.getLicense());
			} else {	
				newLicenseId = svc.getLicenseIdByName(srcInfo.getLicense());
				customLicenseIDMap.put(srcInfo.getLicense(), newLicenseId);
			}
		}
		if (newLicenseName == null || newLicenseId == null) {
			log.error("License is not found: " + newLicenseId + "/" + newLicenseName);
			return false;
		}
		
		// match type
		String matchType = srcInfo.getDiscoveryType();
		if("Declared".equals(srcInfo.getResolutionType()))
			matchType = ProtexIdentificationInfo.PATTERN_MATCH;
		
		String key = srcInfo.getComponent() + "#" + srcInfo.getVersion();
		String label = this.componentVersionLabelMap.get(key);
		
		///////////////////////////////////////////////////////////
		// 2. Component Version valid check
		///////////////////////////////////////////////////////////
		if (label == null) {
			List<String> notUniqueLabelList = new ArrayList<String>();
			for (String notUniqueLabel : notUniqueComponentVersionLabelList) {
				if (key.equalsIgnoreCase(StringUtils.split(notUniqueLabel, "=")[0])) {
					notUniqueLabelList.add(notUniqueLabel);
				}
			}
			if (notUniqueLabelList.size() == 0) {
				// TODO - open-source IDB?
				log.error("ComponentVersion is not on label map");
				return false;
			// when found in notUniqueLabelList
			} else {
				// TODO - find matched ID in Discovery Pending List
				log.error("find matched ID in Discovery Pending List");
				ProtexIdentificationInfo discoveryInfo = this.getComponentVersionIDFromDiscovery(targetProjectId, targetInfo.getFilePath(), srcInfo.getMatchedFile());
				newComponentId = discoveryInfo.getComponentID();
				newVersionID = discoveryInfo.getVersionID();
			}
		} else {
			if ("".equals(ObjectUtils.toString(label))) {
				log.error("fail to find component id: " + srcInfo.getComponent() + ", SKIP this file info: " + srcInfo.toString());
				return false;
			}
	
			String[] items = StringUtils.split(label, "#");
			newComponentId = items[0];
			newVersionID = items[1];
			
			// when custom or local component
			if (newComponentId.startsWith("c_")) {
				String nonStandardComponentId = this.getNotStandardComponentIdByName(targetProjectId, newComponentName);
				if (nonStandardComponentId == null) {
					nonStandardComponentId = this.getLocalComponentIdWhenNotExsitingCreate(targetProjectId, newComponentName, newLicenseId, newLicenseName);
				}
				newComponentId = nonStandardComponentId;
				newVersionID = "";
			}
		}
		// version set when unspecified
		if ("Unspecified".equals(newVersionName) || "null".equalsIgnoreCase(newVersionName) || newVersionName == null) {
			newVersionName = "";	//TODO null? Unspecified
			newVersionID = "";
		}
		if (newVersionID == null || "null".equalsIgnoreCase(newVersionID)) {
			newVersionID = "";
		}
		LicenseInfo identifiedLicenseInfo = new LicenseInfo();
		identifiedLicenseInfo.setLicenseId(newLicenseId);
		identifiedLicenseInfo.setName(newLicenseName);
		
		// IDENTIFY
		if (ProtexIdentificationInfo.STRING_SEARCH.equals(matchType)) {
			if (newComponentId == null) {
				log.error("fail to create local component id. SKIP this file: " + srcInfo.getFilePath());
				return false;
			}
	
			List<StringSearchDiscovery> listStringSearchDiscovery = svc.getStringSearchDiscoveries(targetProjectId, targetInfo.getFilePath());
			for (StringSearchDiscovery oStringSearchDiscovery : listStringSearchDiscovery) {
				StringSearchIdentificationRequest oStringSearchIdentificationRequest = new StringSearchIdentificationRequest();
				oStringSearchIdentificationRequest.setIdentifiedComponentId(newComponentId);
				oStringSearchIdentificationRequest.setIdentifiedVersionId("");
				oStringSearchIdentificationRequest.setIdentifiedUsageLevel(UsageLevel.SNIPPET);
				oStringSearchIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);
				oStringSearchIdentificationRequest.setFolderLevelIdentification(false);
				oStringSearchIdentificationRequest.setStringSearchId(oStringSearchDiscovery.getStringSearchId());
				oStringSearchIdentificationRequest.getMatchLocations().addAll(oStringSearchDiscovery.getMatchLocations());
				svc.addStringSearchIdentification(targetProjectId, oStringSearchDiscovery.getFilePath(), oStringSearchIdentificationRequest);
	
				// TODO - pending
	//			boolean pendingExist = false;
	//			for (StringSearchMatchLocation location : oStringSearchDiscovery.getMatchLocations()) {
	//				if (location.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION) {
	//					pendingExist = true;
	//					break;
	//				}
	//			}
	//			
	//			if (pendingExist) {
	//				StringSearchIdentificationRequest oStringSearchIdentificationRequest = new StringSearchIdentificationRequest();
	//				oStringSearchIdentificationRequest.setIdentifiedComponentId(newComponentId);
	//				oStringSearchIdentificationRequest.setIdentifiedVersionId("");
	//				oStringSearchIdentificationRequest.setIdentifiedUsageLevel(UsageLevel.SNIPPET);
	//				oStringSearchIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);
	//				oStringSearchIdentificationRequest.setFolderLevelIdentification(false);
	//				oStringSearchIdentificationRequest.setStringSearchId(oStringSearchDiscovery.getStringSearchId());
	//				oStringSearchIdentificationRequest.getMatchLocations().addAll(oStringSearchDiscovery.getMatchLocations());
	//				apiSvc.addStringSearchIdentification(targetProjectId, oStringSearchDiscovery.getFilePath(), oStringSearchIdentificationRequest);
	//			}
			}
			
			
		} else if (ProtexIdentificationInfo.CODE_MATCH.equals(matchType)) {
			// TODO - modify this part ...
			String orgComponentId = null;
			String orgVersionId = null;
			if (targetInfo.getComponent() != null) {
				ProtexIdentificationInfo orgInfo = svc.getComponentVersionIDWithNames(targetInfo.getComponent(), targetInfo.getVersion());			
				if (orgInfo == null) {
					orgComponentId = svc.getComponentIDByName(targetInfo.getComponent());
				} else {
					orgComponentId = orgInfo.getComponentID();
					orgVersionId = orgInfo.getVersionID();
				}
			} else {
				orgComponentId = newComponentId;
				orgVersionId = newVersionID;
			}
			
			CodeMatchIdentificationRequest codeMatchIdentificationRequest = new CodeMatchIdentificationRequest();
			
			codeMatchIdentificationRequest.setDiscoveredVersionId(orgVersionId);
			codeMatchIdentificationRequest.setDiscoveredComponentId(orgComponentId);
			
			codeMatchIdentificationRequest.setIdentifiedVersionId(newVersionID);
			codeMatchIdentificationRequest.setIdentifiedComponentId(newComponentId);
			codeMatchIdentificationRequest.setCodeMatchIdentificationDirective(CodeMatchIdentificationDirective.SNIPPET_AND_FILE);
			codeMatchIdentificationRequest.setIdentifiedUsageLevel(UsageLevel.SNIPPET);
			codeMatchIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);
	
			return svc.addCodeMatchIdentification(targetProjectId, "/" + targetInfo.getFilePath(), codeMatchIdentificationRequest);
		} else if (ProtexIdentificationInfo.PATTERN_MATCH.equals(matchType)) {
			DeclaredIdentificationRequest oDeclaredIdentificationRequest = new DeclaredIdentificationRequest();
	
			oDeclaredIdentificationRequest.setPath(srcInfo.getFilePath());
			oDeclaredIdentificationRequest.setIdentifiedComponentId(newComponentId);
			oDeclaredIdentificationRequest.setIdentifiedVersionId(newVersionID);
			oDeclaredIdentificationRequest.setIdentifiedUsageLevel(UsageLevel.SNIPPET);
			oDeclaredIdentificationRequest.setIdentifiedLicenseInfo(identifiedLicenseInfo);
			
			return svc.addDeclaredIdentification(targetProjectId, "/" + targetInfo.getFilePath(), oDeclaredIdentificationRequest);
			
		// Exception
		} else {
			log.error("Match Type is not in CODE_MATCH, STRING_MATCH, PATTERN_MATCH:" + matchType);
			return false;
		}
		return true;
	}
	
	protected String getLocalComponentIDByName(String projectID, String componentName) {
		String key = projectID + "|" + componentName;
		if (localComponentIDMap.containsKey(key)) {
			return localComponentIDMap.get(key);
		}
		
		String componentID = svc.getLocalComponentIDByProjectIDAndComponentName(projectID, componentName);
		localComponentIDMap.put(key, componentID);
		return componentID;
	}
	
	protected String getCustomComponentIDByName(String componentName) {
		String key = componentName;
		if (customComponentIDMap.containsKey(key)) {
			return customComponentIDMap.get(key);
		}
		
		String componentID = svc.getCustomComponentIDByComponentName(componentName);
		customComponentIDMap.put(key, componentID);
        return componentID;
	}
	
	protected String getNotStandardComponentIdByName(String projectId, String componentName) {
		String componentID = this.getLocalComponentIDByName(projectId, componentName);
		if (componentID == null) {
			componentID = this.getCustomComponentIDByName(componentName);
		}
		return componentID;
	}
	
	protected String getLocalComponentIdWhenNotExsitingCreate(String projectID, String componentName, String licenseID, String licenseName) {
		String key = projectID + "|" + componentName;
		if (localComponentNameMap.containsKey(key)) {
			return localComponentNameMap.get(key);
		}

        String componentID = svc.createLocalComponent(projectID, componentName, licenseID, licenseName);
        if (componentID != null) {
        	localComponentNameMap.put(key, componentID);
        	return componentID;
        } else {
        	return null;
        }
	}
}
