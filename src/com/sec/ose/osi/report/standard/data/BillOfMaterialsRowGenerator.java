/**
 * Copyright(C) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.report.standard.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.Tools;

/**
 * BillOfMaterialsRowGenerator
 * @author sjh.yoo
 *
 */
public class BillOfMaterialsRowGenerator {

	private static Log log = LogFactory.getLog(BillOfMaterialsRowGenerator.class);
	
	ArrayList<IdentifiedFilesRow> identifiedFilesRowList = null;
	ArrayList<LicenseSummaryRow> licenseSummaryList = null;
	
	public ArrayList<LicenseSummaryRow> getLicenseSummaryRowList() {
		return licenseSummaryList;
	}

	public ArrayList<IdentifiedFilesRow> getIdentifiedFilesRowList() {
		return identifiedFilesRowList;
	}
	
	public ArrayList<BillOfMaterialsRow> createBillOfMaterialsRowList(
			ProjectInfoForIdentifyReport curProjectInfo, 
			UIResponseObserver observer) {

		String pMessage = "";

		if(curProjectInfo == null) return null;

		String projectName = curProjectInfo.getProjectName();    	  
		String projectID = ProjectAPIWrapper.getProjectID(projectName);
		if(projectID == null) return null;

		// 2) get Bill of Materials
		String message = " > Getting [BILL OF MATERIALS] information from server.";
		observer.pushMessageWithHeader(message);

		ArrayList<BOMEnt> boms = BOMReportAPIWrapper.getBillOfMeterialsFromProjectName(projectName, observer);
		log.debug("  bom: "+boms);		
		if(boms == null) return null;

		log.debug("  bom.size(): "+boms.size());
		
		// 3) get identified file info
		pMessage = " > Getting [Identified File] information from server.";
		observer.pushMessageWithHeader(pMessage);
		
		boolean usingCodeTree = false;
		log.debug("Get identified File from Server");
		identifiedFilesRowList = getIdentifiedFiles(
				projectName,
				usingCodeTree,
				observer);
		
		if(identifiedFilesRowList == null) {
			observer.pushMessageWithHeader("Cannot obtain \"Identified Files\" information form server.");
			log.debug("Failed to get identifiedFileEnts from Report");
			return null;
		}
				
		// 4) generate Bill of Materials
		pMessage = " > Generate Bill of Materials information.";
		observer.pushMessageWithHeader(pMessage);
		
		ArrayList<BillOfMaterialsRow> billOfMaterialsRows = getBillOfMaterialsRows(
				projectName,  
				boms, 
				identifiedFilesRowList,
				curProjectInfo.isAllFileListUp(),
				curProjectInfo.getfilePathListUpComponent()
			);	

		licenseSummaryList = summarizeLicense(billOfMaterialsRows);
		
		return billOfMaterialsRows;
		
	}

	private ArrayList<IdentifiedFilesRow> getIdentifiedFiles(
			String projectName,
			boolean usingCodeTree,
			UIResponseObserver observer) {
		
		String message = " > Getting [IDENTIFIED FILES] information from server.\n";
		observer.pushMessageWithHeader(message);

		log.debug(message);

		ArrayList<IdentifiedFilesRow> fileEnt = null;

		log.debug("using code tree: "+usingCodeTree);

		if(usingCodeTree == false) {
			log.debug("   fast but need much memory");
			fileEnt = BOMReportAPIWrapper.getIdentifiedFilesFromBOMReport(projectName, observer);
		}
		else {
			log.debug("   slow but need less memory");  
			fileEnt = BOMReportAPIWrapper.getIdentifiedFilesFromCodeTree(projectName, observer);
		}
		
		return fileEnt;

	}
	
	private ArrayList<BillOfMaterialsRow> getBillOfMaterialsRows(
			String projectName,
			ArrayList<BOMEnt> boms, 
			ArrayList<IdentifiedFilesRow> IdentifiedFilesRowList,
			boolean isAllFileListUp,
			ArrayList<String> filePathListUpComponent) {
		
		ArrayList<BillOfMaterialsRow> BillOfMaterialsRowList = new ArrayList<BillOfMaterialsRow>();
		
		HashMap<String, ArrayList<IdentifiedFilesRow>> fileEntMap = toComponentFileEntHashMap(IdentifiedFilesRowList);
		
		ArrayList<String> keyListForDup = new ArrayList<String>();
		for(int i=0; i<boms.size(); i++) {
			BOMEnt curBOM = boms.get(i);

			// 0. gen key (component name + license name)
			String componentName = curBOM.getComponentName().trim();
			log.debug(projectName+"'s BoM info " + " ("+i+"/"+boms.size()+") : "+componentName);
			if(componentName.equals(projectName)) continue;
			
			String componentLicense = curBOM.getLicense().trim();
			String key = componentName + "#" + componentLicense;
			if(keyListForDup.contains(key)) continue;
			else keyListForDup.add(key);
			
			// 1. extract component info
			String componentVersion = curBOM.getComponentVersion(); 
			String componentComment = curBOM.getComment();

			String matchedFiles = "";
			Integer matchedFileCounts = 0;
			StringBuffer componentCommentBuf = new StringBuffer("");
			StringBuffer fileCommentBuf = new StringBuffer("");

			if( (componentComment != null) && 
				(componentComment.length() != 0) && 
				(componentComment.equals("null")==false) && 
				(componentComment.equals("null\nnull")==false) &&
				(componentComment.equals("null<"+componentName+" - "+componentVersion+">\nnull")==false) )
			{
				componentCommentBuf.append("[Compoment Comment]\n"+componentComment+"\n\n");
			}

			log.debug("- Component Comment: "+componentCommentBuf.toString());
			
			BillOfMaterialsRow sheetRow = null;
			
			//	 2. extract file info - component
			log.debug("- get FileInfo start");
			if(fileEntMap.containsKey(key) == true) {
				ArrayList<IdentifiedFilesRow> fileEntList = fileEntMap.get(key); 
				log.debug("- File # : "+fileEntList.size());
				ArrayList<String> categorySet = getCategorySet(fileEntList);
				
				if(categorySet == null)
					continue;
				
				// 3. Top folder
				for(String topFolder:categorySet) {
					
					ArrayList<IdentifiedFilesRow> fileEntListForRow = getFileEntListForRow(topFolder, fileEntList);
					if(fileEntListForRow != null) {
						// matchedFileCount
						matchedFileCounts = fileEntListForRow.size();
		
						// comments
						fileCommentBuf.setLength(0);
						for(IdentifiedFilesRow curFileEnt:fileEntListForRow) {
							curFileEnt.setLicense(componentLicense);
							String fileComment = curFileEnt.getComment();
							if( (fileComment != null) && (fileComment.length() != 0) ) {
								fileCommentBuf.append("## "+curFileEnt.getFullPath()+"\n"+fileComment+"\n\n");
								
							}
						}
						
						// fileList
						if(isAllFileListUp == true || 
								(filePathListUpComponent != null && filePathListUpComponent.contains(componentName)) ) {
							matchedFiles = getFullPaths(fileEntListForRow);
						} else {
							matchedFiles = getFileCountForFolders(fileEntListForRow);
						}
					}

					sheetRow = new BillOfMaterialsRow(
							topFolder,
							matchedFiles, 
							matchedFileCounts,
							componentName, 
							componentLicense,
							componentCommentBuf.toString()+fileCommentBuf.toString());
					
					BillOfMaterialsRowList.add(sheetRow);
				}
				log.debug("- getFileInfo end");
			} else {
				log.debug("- getFileInfo ent - no file info");

				sheetRow = new BillOfMaterialsRow(
						"",
						matchedFiles, 
						matchedFileCounts,
						componentName, 
						componentLicense,
						componentCommentBuf.toString());

				BillOfMaterialsRowList.add(sheetRow);
			}
		} // end of for
		
		log.debug("IdentifiedRowSize: " +BillOfMaterialsRowList.size());
		if( BillOfMaterialsRowList.size()==0) {
			BillOfMaterialsRow xSheetRow = new BillOfMaterialsRow(
					"",
					"No component is identified for this project.", 
					0,
					"", 
					"",
					"");

			BillOfMaterialsRowList.add(xSheetRow);
		}
		
		return BillOfMaterialsRowList;
	}
		
	private ArrayList<IdentifiedFilesRow> getFileEntListForRow(String topFolder, ArrayList<IdentifiedFilesRow> fileEntList) {

		if(fileEntList == null || fileEntList.size() == 0)
			return null;
		
		if(topFolder == null)
			return null;
		
		ArrayList<IdentifiedFilesRow> set = new ArrayList<IdentifiedFilesRow>();
		
		for(IdentifiedFilesRow ent:fileEntList) {
			String root = getRoot(ent.getFullPath());
			
			if(root.equals(topFolder)) {
				set.add(ent);
			}
		}
		
		return set;
	
	}

	private ArrayList<String> getCategorySet(ArrayList<IdentifiedFilesRow> fileEntList) {
		if(fileEntList == null || fileEntList.size() == 0)
			return null;

		ArrayList<String> categoryList = new ArrayList<String>();
		
		for(IdentifiedFilesRow ent:fileEntList) {
			String root = getRoot(ent.getFullPath());
			if(!categoryList.contains(root))
				categoryList.add(root);
		}
		
		return categoryList;
	}
	
	private String getRoot(String path) {
		
		if(path == null)
			return "";
		
		if((path.indexOf("/") < 0) || (path.lastIndexOf("/")==0))
			return "/";
		
		StringTokenizer st = new StringTokenizer(path, "/");
		String root = st.nextToken();
		
		return root+"/";
	}

	private ArrayList<LicenseSummaryRow> summarizeLicense(ArrayList<BillOfMaterialsRow> rows) {

		HashMap<String,Integer> licenseMap = new HashMap<String,Integer>();
		for(BillOfMaterialsRow row:rows) {
			String componentLicense = row.getLicense();
			Integer curCnt = licenseMap.get(componentLicense);
			if(curCnt == null) {
				curCnt = 0;
			}
			licenseMap.put(componentLicense, curCnt + row.getMatchedFileCounts());
		}
		ArrayList<String> sortedLicenseKey = Tools.sortByValue(licenseMap);
		
		ArrayList<LicenseSummaryRow> licenseSummary = new ArrayList<LicenseSummaryRow>();
		for(String licenseKey:sortedLicenseKey) {
			licenseSummary.add(new LicenseSummaryRow(licenseKey,licenseMap.get(licenseKey)));
		}
		
		return licenseSummary;
	}

	private String getFullPaths(ArrayList<IdentifiedFilesRow> fileEntList) {
		
		StringBuffer paths = new StringBuffer("");
		
		for(IdentifiedFilesRow ent:fileEntList) {
			paths.append( ent.getFullPath()+"\n" );
		}
		
		if(paths.length()>0) {
			return paths.substring(0, paths.length()-1);
		}
		
		return "";
	}
	
	private String getFileCountForFolders(ArrayList<IdentifiedFilesRow> fileEntList) {

		TreeMap<String, Integer> map = new TreeMap<String, Integer>();		// parent path, value
		
		if(fileEntList == null || fileEntList.size() == 0)
			return "<None>";
		
		for(IdentifiedFilesRow ent:fileEntList) {
			String parentPath = (new File(ent.getFullPath())).getParent();
			if(parentPath == null)
				parentPath = "";

			if(map.containsKey(parentPath) == false) {
				map.put(parentPath, 0);
			}

			map.put(parentPath, map.get(parentPath)+1);
		}
		
		if(map.size() == 0) return "";
		if(map.size() == 1) return ("("+map.get(map.firstKey())+" files)\n");

		String msg = "";
		for(String path:map.keySet()) {
			msg += path;
			if(!path.endsWith("/")) msg += "/ ";
			msg+= "("+ map.get(path)+" files)\n";
		}
		msg = msg.replace("\\", "/");
		
		if(msg.length()>0) {
			return msg.substring(0, msg.length()-1);
		}
		
		return "";
	}

	/**
	 * key : component name + license name
	 * value : identified file list
	 * 
	 * @param fileEntList
	 * @return
	 */
	private HashMap<String, ArrayList<IdentifiedFilesRow>> 
		toComponentFileEntHashMap(ArrayList<IdentifiedFilesRow> fileEntList) {
		
		HashMap<String, ArrayList<IdentifiedFilesRow>> hashMap = new HashMap<String, ArrayList<IdentifiedFilesRow>>();
		
		if(fileEntList == null || fileEntList.size() < 1)
			return hashMap;
		
		for(IdentifiedFilesRow ent:fileEntList) {			
			String componentName = ent.getComponent();
			String licenseName = ent.getLicense();
			if(componentName != null) componentName = componentName.trim();
			if(licenseName != null) licenseName = licenseName.trim();
			String key = componentName+"#"+licenseName;
			
			if(hashMap.containsKey(key) == false) {
				ArrayList<IdentifiedFilesRow> fileEntSet = new ArrayList<IdentifiedFilesRow>();
				hashMap.put(key, fileEntSet);
			}
			hashMap.get(key).add(ent);
			
		}
		
		return hashMap;
	}

}
