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
package com.sec.ose.osi.thread.job.identify.data;

import java.io.Serializable;
import java.util.Collection;

import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * IdentifyData
 * @author sjh.yoo, hankido.lee
 * 
 */
public class IdentifyData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -605101004215609335L;
	
	private int compositeType = -1;		// match type, file/folder, identify/reset, standard component  information
	
	private String stringSearch = "";
	private String projectName = "";	// Project Name
	private String originComponentName = "";
	private String originVersionName = "";
	private String originLicenseName = "";
	private String currentComponentName = "";		// Component Name
	private String currentVersionName = "";		// version Name
	private String currentLicenseName = "";		// License Name
	private String newComponentName = "";	// identify new Component Name
	private String newVersionName = "";		// identify new Version Name
	private String newLicenseName = "";
	private String comment = "";
	private Collection<String> filePaths = null;		// Identify target File Path
	private String matchedFile = "";
	private String timestamp = "";
	
	public IdentifyData(
						int compositeType,
						String projectName,
						String originComponentName,
						String originVersionName,
						String originLicenseName,
						String currentComponentName,
						String currentVersionName,
						String currentLicenseName,
						String newComponentName,
						String newVersionName,
						String newLicenseName,
						String comment,
						Collection<String> filePath,
						String matchedFile,
						String stringSearch
						) {
		
		this.compositeType = compositeType;
		this.projectName = 	projectName;
		this.originComponentName = originComponentName;
		this.originVersionName = originVersionName;
		this.originLicenseName = originLicenseName;
		this.currentComponentName = currentComponentName;
		this.currentVersionName = currentVersionName;
		this.currentLicenseName = currentLicenseName;
		this.newComponentName = newComponentName;
		this.newVersionName = newVersionName;
		this.newLicenseName = newLicenseName;
		this.comment = comment;
		this.filePaths = filePath;
		this.matchedFile = matchedFile;
		this.stringSearch = stringSearch;
		this.timestamp = DateUtil.getCurrentTime("%1$tm-%1$td %1$tH:%1$tM:%1$tS");
	}
	
	public String getStringSearch() {
		return stringSearch;
	}
	
	public int getCompositeType() {
		return compositeType;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String pProjectName) {
		this.projectName = pProjectName;
	}

	public Collection<String> getFilePath() {
		return filePaths;
	}

	public int getFileCount() {
		if(filePaths == null)
			return 0;
		
		return filePaths.size();
	}
	
	public String getOriginComponentName() {
		return originComponentName;
	}

	public String getOriginVersionName() {
		return originVersionName;
	}

	public String getOriginLicenseName() {
		return originLicenseName;
	}

	public String getCurrentComponentName() {
		return currentComponentName;
	}

	public String getCurrentVersionName() {
		return currentVersionName;
	}

	public String getCurrentLicenseName() {
		return currentLicenseName;
	}

	public String getNewComponentName() {
		return newComponentName;
	}

	public String getNewVersionName() {
		return newVersionName;
	}
	
	public String getNewLicenseName() {
		return newLicenseName;
	}

	public String getComment() {
		return comment;
	}
	
	public String getMatchedFile() {
		return matchedFile;
	}

	public String toString() {
		return "project Name : "+projectName +
			"\nComponent Name : "+ currentComponentName +
			"\nLicense Name : "+ currentLicenseName;
	}

	private StringBuffer bufContent = null;
	public String toOutPut() {
		if(bufContent == null) {
			bufContent = new StringBuffer();
		}
		bufContent.setLength(0);
        bufContent.append("\r\n<ProjectName> ").append(projectName);
        bufContent.append("\r\n<FilePath(s)> ");
        for(String filePath:filePaths) {
        	bufContent.append("\r\n   +").append(filePath);
        }
        bufContent.append("\r\n<Type: ").append(analyzeCompositeType());
        bufContent.append("\r\n<StringSearch> ").append(stringSearch);
        bufContent.append("\r\n<OriginComponentName> ").append(originComponentName);
        bufContent.append("\r\n<OriginVersionName> ").append(originVersionName);
        bufContent.append("\r\n<OriginLicenseName> ").append(originLicenseName);
        bufContent.append("\r\n<CurrentComponentName> ").append(currentComponentName);
        bufContent.append("\r\n<CurrentVersionName> ").append(currentVersionName);
        bufContent.append("\r\n<CurrentLicenseName> ").append(currentLicenseName);
        bufContent.append("\r\n<NewComponentName> ").append(newComponentName);
        bufContent.append("\r\n<NewVersionName> ").append(newVersionName);
        bufContent.append("\r\n<NewLicenseName> ").append(newLicenseName);
        bufContent.append("\r\n<MatchedFile> ").append(matchedFile);
        bufContent.append("\r\n<Ccomment> \r\n").append(comment);
		
		return bufContent.toString();
	}
	
	private StringBuffer htmlBufContent = new StringBuffer();
	public String toHTMLContents() {
		if(htmlBufContent == null) {
			htmlBufContent = new StringBuffer();
		}
		htmlBufContent.setLength(0);

		htmlBufContent.append("\r\n<B>ProjectName:</B> ").append(projectName);
		htmlBufContent.append("\r\n<BR><B>FilePath(s)</B> ");
        for(String filePath:filePaths) {
        	htmlBufContent.append("\r\n<BR>   +").append(filePath);
        }
        htmlBufContent.append("\r\n<HR>");
        
        htmlBufContent.append("\r\n<B>Type:</B> ").append(analyzeCompositeType());
        htmlBufContent.append("\r\n<BR><B>StringSearch:</B> ").append(stringSearch);
        htmlBufContent.append("\r\n<BR><B>OriginComponentName:</B> ").append(originComponentName);
        htmlBufContent.append("\r\n<BR><B>OriginVersionName:</B> ").append(originVersionName);
        htmlBufContent.append("\r\n<BR><B>OriginLicenseName:</B> ").append(originLicenseName);
        htmlBufContent.append("\r\n<BR><B>CurrentComponentName:</B> ").append(currentComponentName);
        htmlBufContent.append("\r\n<BR><B>CurrentVersionName:</B> ").append(currentVersionName);
        htmlBufContent.append("\r\n<BR><B>CurrentLicenseName:</B> ").append(currentLicenseName);
        htmlBufContent.append("\r\n<BR><B>NewComponentName:</B> ").append(newComponentName);
        htmlBufContent.append("\r\n<BR><B>NewVersionName:</B> ").append(newVersionName);
        htmlBufContent.append("\r\n<BR><B>NewLicenseName:</B> ").append(newLicenseName);
        htmlBufContent.append("\r\n<BR><B>MatchedFile:</B> ").append(matchedFile);
        htmlBufContent.append("\r\n<BR><B>Comment:</B><BR> \r\n").append(comment);
       
		return htmlBufContent.toString();
	}
	
	private StringBuffer analysisCompositeType = null;
	private String analyzeCompositeType() {
		if(analysisCompositeType == null) {
			analysisCompositeType = new StringBuffer();
		}
		analysisCompositeType.setLength(0);
		
		if((compositeType & IdentificationConstantValue.FOLDER_TYPE) != 0) {
			analysisCompositeType.append("FOLDER_TYPE, ");
		} else {
			analysisCompositeType.append("FILE_TYPE, ");
		}
		
		if((compositeType & IdentificationConstantValue.STRING_MATCH_TYPE) != 0) {
			analysisCompositeType.append("STRING_MATCH_TYPE, ");
		} else if((compositeType & IdentificationConstantValue.CODE_MATCH_TYPE) != 0) {
			analysisCompositeType.append("CODE_MATCH_TYPE, ");
		} else {
			analysisCompositeType.append("PATTERN_MATCH_TYPE, ");
		}
		
		if((compositeType & IdentificationConstantValue.RESET_TYPE) != 0) {
			analysisCompositeType.append("RESET_TYPE");
		} else {
			analysisCompositeType.append("IDENTIFY_TYPE");
		}
		
		return analysisCompositeType.toString();
	}

	private StringBuffer testBufContent = null;
	public String exportCSVForm() {
		if(testBufContent == null) {
			testBufContent = new StringBuffer();
		}
		testBufContent.setLength(0);
		
		testBufContent.append(timestamp).append(",");
		testBufContent.append(projectName).append(",");
		
		testBufContent.append(currentComponentName).append(",");
		testBufContent.append(currentLicenseName).append(",");
		testBufContent.append(currentVersionName).append(",");
		testBufContent.append(newComponentName).append(",");
		testBufContent.append(newVersionName).append(",");
		testBufContent.append(newLicenseName).append(",");
		testBufContent.append(matchedFile).append(",");

		for(String filePath:filePaths) {
			testBufContent.append(filePath).append(",");
		}
		
		testBufContent.append(comment).append(",");
		
		return testBufContent.toString();
	}

	public String getTimeStamp() {
		return timestamp;
	}
}
