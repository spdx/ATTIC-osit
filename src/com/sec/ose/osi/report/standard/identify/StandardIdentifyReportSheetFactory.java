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
package com.sec.ose.osi.report.standard.identify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.sdk.protexsdk.bom.IdentifiedFileEnt;

/**
 * StandardIdentifyReportSheetFactory
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class StandardIdentifyReportSheetFactory {
	private static Log log = LogFactory.getLog(IdentificationDBManager.class);
	
	public static final String STRING_SEARCH_ONLY_SYMBOL = "STRING_SEARCH_ONLY";

	public static StandardIdentifyReportSheet createIdentifyReportSheet(
			String projectName,  
			ArrayList<BOMEnt> boms, 
			ArrayList<IdentifiedFileEnt> fileEnt,
			boolean isAllFileListUp,
			ArrayList<String> filePathListUpComponent,
			boolean usingCodeTree
			) {		
          
		StandardLicenseSummary licenseSummary = null;
	
		ArrayList<StandardIdentifyReportSheetRow> identifiedRows = getIdentifiedRows(
				projectName,  
				boms, 
				fileEnt,
				isAllFileListUp,
				filePathListUpComponent
			);	
		
		ArrayList<StandardIdentifyReportSheetRow> stringSearchOnlyRows = null;
		if(usingCodeTree == false) {
			stringSearchOnlyRows = getStringSerachOnlyRows(fileEnt);
		} 
		
		licenseSummary = summarizeLicense(identifiedRows);
		
		StandardIdentifyReportSheet xIdentifyReportSheet = 
			new StandardIdentifyReportSheet(
					projectName, 
					identifiedRows, 
					stringSearchOnlyRows,
					licenseSummary);
		 
		return xIdentifyReportSheet;
	}

	private static ArrayList<StandardIdentifyReportSheetRow> getIdentifiedRows(
			String projectName,
			ArrayList<BOMEnt> boms, 
			ArrayList<IdentifiedFileEnt> fileEnt,
			boolean isAllFileListUp,
			ArrayList<String> filePathListUpComponent) {
		
		TreeMap<String, StandardIdentifyReportSheetRow> identifiedRows = new TreeMap<String,StandardIdentifyReportSheetRow>();
		
		HashMap<String, TreeSet<IdentifiedFileEnt>> fileEntMap = toComponentFileEntHashMap(fileEnt);
		
		for(int i=0; i<boms.size(); i++) {
			BOMEnt curBOM = boms.get(i);
			log.debug("getIdentifiedRows(): "+projectName+" / bom: "+curBOM.getComponentName() + " ("+i+"/"+boms.size()+")");

			String componentName = curBOM.getComponentName().trim();
			if(componentName.equals(projectName)) continue;
			
			// 1. extract component info
			String componentVersion = curBOM.getComponentVersion(); 
			String componentLicense = curBOM.getLicense();
			String componentComment = curBOM.getComment();
			String mComponentName = curBOM.getComponentName();
			String mComponentVersion = curBOM.getComponentVersion();

			String category = "";
			String matchedFiles = "";
			Integer matchedFileCounts = 0;
			StringBuffer componentCommentBuf = new StringBuffer("");
			StringBuffer fileCommentBuf = new StringBuffer("");

			if( (componentComment != null) && 
				(componentComment.length() != 0) && 
				(componentComment.equals("null")==false) && 
				(componentComment.equals("null\nnull")==false) &&
				(componentComment.equals("null<"+mComponentName+" - "+mComponentVersion+">\nnull")==false) )
			{
				componentCommentBuf.append("[Compoment Comment]\n"+componentComment+"\n\n");
			}

			log.debug("\tgetComponentInfo: "+componentCommentBuf);			
			
			StandardIdentifyReportSheetRow xSheetRow = null;
			
			//	 2. 
			log.debug("\tgetFileInfo start");			
			if(fileEntMap.containsKey(componentName) == true) {
				
				TreeSet<IdentifiedFileEnt> fileEntList = fileEntMap.get(componentName); 
				log.debug("\tgetFileInfo start - numOfFile: "+fileEntList.size());					
				TreeSet<String> categorySet = getCategorySet(fileEntList);
				
				if(categorySet == null)
					continue;
				
				Iterator<String> itrTopFolder = categorySet.iterator();
				if(itrTopFolder == null)
					continue;
				
				// 3. Top folder
				while(itrTopFolder.hasNext()) {
					String topFolder = itrTopFolder.next();
					
					TreeSet<IdentifiedFileEnt> fileEntListForRow = getFileEntListForRow(topFolder, fileEntList);
					if(fileEntListForRow != null) {
						// matchedFileCount
						matchedFileCounts = fileEntListForRow.size();
		
						// comments
						Iterator<IdentifiedFileEnt> itr = fileEntListForRow.iterator();
						fileCommentBuf.setLength(0);
						while(itr.hasNext()) {
							IdentifiedFileEnt curFileEnt= itr.next();
							curFileEnt.setLicense(componentLicense);
							String fileComment = curFileEnt.getFileComment();
							if( (fileComment != null) && (fileComment.length() != 0) ) {
								fileCommentBuf.append("## "+curFileEnt.getFilePath()+"\n"+fileComment+"\n\n");
								
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

					category = topFolder;

					xSheetRow = new StandardIdentifyReportSheetRow(
							category,
							matchedFiles, 
							matchedFileCounts,
							componentName, 
							componentVersion, 
							componentLicense,
							componentCommentBuf.toString()+fileCommentBuf.toString());
					
					identifiedRows.put(topFolder+componentName.toLowerCase(),xSheetRow);
				}
				log.debug("\tgetFileInfo end");
			} else {
				log.debug("\tgetFileInfo ent - no file info");

				xSheetRow = new StandardIdentifyReportSheetRow(
						category,
						matchedFiles, 
						matchedFileCounts,
						componentName, 
						componentVersion, 
						componentLicense,
						componentCommentBuf.toString());
				identifiedRows.put(componentName.toLowerCase(),xSheetRow);
			}
		} // end of for
		
		log.debug("IdentifiedRowSize: " +identifiedRows.size());
		if( identifiedRows.size()==0) {
			StandardIdentifyReportSheetRow xSheetRow = new StandardIdentifyReportSheetRow(
					"",
					"No component is identified for this project.", 
					0,
					"", 
					"", 
					"",
					"");
			identifiedRows.put("",xSheetRow);
		}
		
		return  new ArrayList<StandardIdentifyReportSheetRow>(identifiedRows.values());
	}

	private static ArrayList<StandardIdentifyReportSheetRow> 
			getStringSerachOnlyRows(
					ArrayList<IdentifiedFileEnt> fileEntList
					) {

		ArrayList<StandardIdentifyReportSheetRow> stringSearchOnlyRows = new ArrayList<StandardIdentifyReportSheetRow>();
		StandardIdentifyReportSheetRow xSheetRow = null;
		
		if(fileEntList == null) {
			return null;
		}
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		while(itr.hasNext()) {
			IdentifiedFileEnt ent = itr.next();
			if(ent.isStringSearchOnly() == true) {
				
				String filePath = ent.getFilePath();
				String componentLicense = ent.getFileLicense();
				String category = getRoot(filePath);
				String comment = ent.getFileComment();
				String componentName = ent.getComponentName();
				
				xSheetRow = new StandardIdentifyReportSheetRow(
						category,
						filePath, 
						1, //matchedFileCounts,
						componentName, //STRING_SEARCH_ONLY_SYMBOL, //componentName, 
						"", //componentVersion 
						componentLicense,
						comment);
				
				stringSearchOnlyRows.add(xSheetRow);
			}
			
		}
		return stringSearchOnlyRows;
	}
		
	private static TreeSet<IdentifiedFileEnt> getFileEntListForRow(String topFolder, TreeSet<IdentifiedFileEnt> fileEntList) {

		if(fileEntList == null || fileEntList.size() == 0)
			return null;
		
		if(topFolder == null)
			return null;
		
		TreeSet<IdentifiedFileEnt> set = new TreeSet<IdentifiedFileEnt>();
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		while(itr.hasNext()) {
		
			IdentifiedFileEnt ent = itr.next();
			String root = getRoot(ent.getFilePath());
			
			if(root.equals(topFolder)) {
				set.add(ent);
			}
		}
		
		return set;
	
	}

	private static TreeSet<String> getCategorySet(TreeSet<IdentifiedFileEnt> fileEntList) {
		if(fileEntList == null || fileEntList.size() == 0)
			return null;

		TreeSet<String> categorySet = new TreeSet<String>();		// path, value
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		while(itr.hasNext()) {
		
			IdentifiedFileEnt ent = itr.next();
			String root = getRoot(ent.getFilePath());
			categorySet.add(root);
		}
		
		return categorySet;
	}
	
	private static String getRoot(String path) {
		
		if(path == null)
			return "";
		
		if(path.indexOf("/") < 0)
			return "/";
		
		StringTokenizer st = new StringTokenizer(path, "/");
		String root = st.nextToken();
		
		return root+"/";
	}

	private static StandardLicenseSummary summarizeLicense(ArrayList<StandardIdentifyReportSheetRow> rows) {
		
		StandardLicenseSummary summary = new StandardLicenseSummary();
		for(int i=0; i<rows.size(); i++) {
			StandardIdentifyReportSheetRow row = rows.get(i);
			String componentLicense = row.getComponentLicense();
			Integer fileCnt = row.getMatchedFileCounts();
			
			summary.addFileCntForLicense(componentLicense, fileCnt);
		}
		
		
		return summary;
	}

	private static String getFullPaths(TreeSet<IdentifiedFileEnt> fileEntList) {
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		StringBuffer paths = new StringBuffer("");
		
		while(itr.hasNext()) {
			paths.append( itr.next().getFilePath()+"\n" );

		}
		
		if(paths.length()>0) {
			String strTmpe = paths.substring(0, paths.length()-1);
			paths.setLength(0);
			paths.append(strTmpe);
		}
		
		
		return paths.toString();
	}
	
	private static String getFileCountForFolders(TreeSet<IdentifiedFileEnt> fileEntList) {

		TreeMap<String, Integer> map = new TreeMap<String, Integer>();		// parent path, value
		
		if(fileEntList == null || fileEntList.size() == 0)
			return "<None>";
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		while(itr.hasNext()) {
		
			IdentifiedFileEnt ent = itr.next();
			String parentPath = ent.getParentPath();
			if(parentPath == null)
				parentPath = "";
			

			if(map.containsKey(parentPath) == false) {
				map.put(parentPath, 0);
			}


			map.put(parentPath, map.get(parentPath)+1);
		}
		
		String msg = "";
		
		Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator(); 
		if(iter == null) {
			return msg;
		}
		
		while(iter.hasNext()) {
			String path = iter.next().getKey();		
			Integer numOfFiles = map.get(path);
			
			msg+= (path + "\\ ("+numOfFiles+" files)\n");
		}
		msg = msg.replace("\\", "/");
		
		if(msg.length()>0) {
			msg = msg.substring(0, msg.length()-1);
		}
		
		return msg;
	}

	private static HashMap<String, TreeSet<IdentifiedFileEnt>> 
		toComponentFileEntHashMap(ArrayList<IdentifiedFileEnt> fileEntList) {
		
		HashMap<String, TreeSet<IdentifiedFileEnt>> hashMap = new HashMap<String, TreeSet<IdentifiedFileEnt>>();
		
		if(fileEntList == null || fileEntList.size() < 1)
			return hashMap;
		
		Iterator<IdentifiedFileEnt> itr = fileEntList.iterator();
		while(itr.hasNext()) {
			IdentifiedFileEnt ent = itr.next();

			
			String componentName = ent.getComponentName();
			if(componentName != null)
				componentName = componentName.trim();
			
			if(hashMap.containsKey(componentName) == false) {
				TreeSet<IdentifiedFileEnt> fileEntSet = new TreeSet<IdentifiedFileEnt>();
				hashMap.put(componentName, fileEntSet);
			}
			hashMap.get(componentName).add(ent);
			
		}
		
		return hashMap;
	}
}

