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
package com.sec.ose.osi.localdb.identification;

import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;

/**
 * TableIdentifiedFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class TableIdentifiedFile {
	
	static final int PATH 			= 1;
	static final int TYPE 			= 2;
	static final int COMPONENT 		= 3;
	static final int VERSION 		= 4;
	static final int LICENSE 		= 5;
	static final int MATCHED_FILE	= 6;
	
	private String 	path;
	private	String	component;
	private String	version;
	private String	license;
	private String 	matched_file;
	
	public TableIdentifiedFile(String path, int type, String component,
			String version, String license, String matchedFile) {
		super();
		this.path = path;
		this.component = component;
		this.version = version;
		this.license = license;
		matched_file = matchedFile;
	}

	public String getComponent() {
		return component;
	}

	public String getVersion() {
		return version;
	}

	public String getLicense() {
		return license;
	}
	
	public String getPath() {

		return path;
	}
	
	public String getMatchedFile() {
		return matched_file;
	}

	private static StringBuffer ilbpBuf = new StringBuffer();
	static String getQueryForIdentifiedListByFolder(
			String projectName, 
			String pPath
			) {
		
		ilbpBuf.setLength(0);
		ilbpBuf.append("SELECT * ");
		ilbpBuf.append("FROM identified_file_").append(IdentificationDBManager.getProjectTablePostfix(projectName)).append(" ");
		ilbpBuf.append("WHERE path like '").append(pPath).append("%' ");
		ilbpBuf.append(" AND type = '").append(IdentificationConstantValue.CODE_MATCH_TYPE).append("';");
		
		return ilbpBuf.toString();
	}
	
	private static StringBuffer iflBuf = new StringBuffer();
	static String getQueryForIdentifiedFileListByFile (
			String projectName, 
			String pPath) {
		
		iflBuf.setLength(0);	
		iflBuf.append("SELECT * ");
		iflBuf.append("FROM identified_file_").append(IdentificationDBManager.getProjectTablePostfix(projectName)).append(" ");
		iflBuf.append("WHERE path='").append(pPath).append("' ");
		iflBuf.append("AND type='").append(IdentificationConstantValue.CODE_MATCH_TYPE).append("' ");
		iflBuf.append(";");
		
		return iflBuf.toString();
	}

}
