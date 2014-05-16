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
package com.sec.ose.osi.util.tools;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

import com.sec.ose.osi.thread.ui_related.data.message.DefaultUIResponseObserver;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.Property;

/**
 * ProjectSplitUtil
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class ProjectSplitUtil {
	
	private static TreeMap<String, ProjectSplitInfo> analyzeTargetMap = new TreeMap<String, ProjectSplitInfo>();
						// key : project name, value : ProjectSplitInfo
	private String mProjectNamePrefix;
	private static String mRootLocation;
	private UIResponseObserver observer;
	private static int numOfFilesForAProject = Property.getInstance().getDefaultNumOfFilesUpperLimit();

	public ProjectSplitUtil(String pPrefixProjectName, String pRootLocation) {
		this.mProjectNamePrefix = pPrefixProjectName;
		setRootLocation(pRootLocation);
	}

	private static void setRootLocation(String pRootLocation) {
		mRootLocation = pRootLocation;
	}

	public static TreeMap<String, ProjectSplitInfo> getAnalyzeTargetMap() {
		return analyzeTargetMap;
	}

	public void clearAnalyzeTargetMap() {
		analyzeTargetMap.clear();
	}

	public String generateProjectName(String curPath) {
		
		String xProjectName = "";
		String moduleName = curPath.substring(curPath.indexOf(mRootLocation) + mRootLocation.length());
		moduleName = moduleName.replace(File.separator, "_");
		
		xProjectName = mProjectNamePrefix + moduleName;
		return xProjectName;
	}

	public static String toHtml() {

		StringBuffer sf = new StringBuffer();
		
		sf.append("The number of files for the folder \""+mRootLocation+"\" is more than limitation ("+numOfFilesForAProject+"). <br/>");
		sf.append("The following projects in the table will be created. <br/>");
		sf.append("(You can change this limitation: Setting > Project Analysis) <br/><br/><br/>");
		sf.append("<table border='0' width='100%'>"); //bordercolor='#ffffff'
		sf.append("<tr align='center' bgcolor='#6495ed'>"); //#F6F6F6
		sf.append("<td>");
		sf.append("Number");
		sf.append("</td>");
		sf.append("<td>");
		sf.append("Project Name");
		sf.append("</td>");
		sf.append("<td>");
		sf.append("Path");
		sf.append("</td>");
		sf.append("<td>");
		sf.append("File Count");
		sf.append("</td>");
		sf.append("</tr>");
		
		int number = 0;
		String bgcolor = "";
		for(String key : analyzeTargetMap.keySet()) {
			ProjectSplitInfo projectSplitInfo = analyzeTargetMap.get(key);
			if(number%2 == 0) {
				bgcolor = "bgcolor=#e6e6fa";
			} else {
				bgcolor = "bgcolor=#fff0f5";
			}
			sf.append("<tr "+bgcolor+">");
			sf.append("<td align='center'>");
			sf.append(++number);
			sf.append("</td>");
			sf.append("<td>");
			sf.append(projectSplitInfo.getProjectName());
			sf.append("</td>");
			sf.append("<td>");
			sf.append(projectSplitInfo.getAnalyzeTargetPath());
			sf.append("</td>");
			sf.append("<td align='center'>");
			sf.append(projectSplitInfo.getNumOfFiles());
			sf.append("</td>");
			sf.append("</tr>");
		}
		sf.append("</table>");
		return sf.toString();
	}

	public void split(File rootLocation, UIResponseObserver pObserver) {
		
		observer = pObserver;
		if(observer == null)
			observer = new DefaultUIResponseObserver();
		
		boolean result = checkNeedToSplit(rootLocation, pObserver);

		
		if(result == false) {
			String projectName = generateProjectName(rootLocation.getAbsolutePath());
			analyzeTargetMap.put(projectName, new ProjectSplitInfo(projectName, rootLocation.getAbsolutePath(), getNumOfFiles(rootLocation) ));
			return;
		}
		
		int fileCnt=0;
		int dirCnt=0;
		
		if(rootLocation.listFiles() == null) {
			return;
		}
		
		for(File f: rootLocation.listFiles()) {
			if(f.isDirectory()) {
				split(f, pObserver);
				dirCnt++;
			}
			else {
				fileCnt++;
			}
		}
		
		if(fileCnt >0) {
			String projectName = generateProjectName(rootLocation.getAbsolutePath());
			
			if(dirCnt > 0)
				projectName += Property.LOCAL_OPTION_POSTFIX;
			analyzeTargetMap.put(projectName, new ProjectSplitInfo(projectName, rootLocation.getAbsolutePath(), fileCnt ));
		}
		
	}

	public boolean checkNeedToSplit(File dirPath, UIResponseObserver observer) {
		
		int totalFileCnt = getNumOfFiles(dirPath);

		if(observer != null) {
			observer.pushMessageWithHeader(">path : ..." + dirPath.getAbsolutePath()+" << FileCnt:"+totalFileCnt);
		}
		
		System.out.println("dirPath : " + dirPath);
		System.out.println("totalFileCnt : " + totalFileCnt);
		if(totalFileCnt >  numOfFilesForAProject)
			return true;
		else
			return false;
		

	}

 	private static HashMap<String, Integer> _File_CNT_CACHE = new HashMap<String, Integer>();
	private int getNumOfFiles(File dirPath) {
		
		if( _File_CNT_CACHE.containsKey(dirPath.getAbsolutePath())) {
			return _File_CNT_CACHE.get(dirPath.getAbsolutePath());
		}
		
		int fileCnt = 0;
		
		File[] files = dirPath.listFiles();
		
		if(files == null) {
			return fileCnt;
		}
		
		for(File f: files) {
			if(f.isFile()) {
				fileCnt++;
			}
			else {
				File curDirPath = f;
				fileCnt += getNumOfFiles(curDirPath);
			}
			if(fileCnt > numOfFilesForAProject) 
				return fileCnt;

		}
		
		_File_CNT_CACHE.put(dirPath.getAbsolutePath(), fileCnt);
		System.out.println(dirPath.getAbsolutePath()+", filecnt:"+fileCnt);
		return fileCnt;
	}

	public static void setNumOfFilesForAProject(int numOfFilesForAProject) {
		ProjectSplitUtil.numOfFilesForAProject = numOfFilesForAProject;
	}
	
}
