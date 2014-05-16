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

/**
 * TableCodeMatch
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class TableCodeMatch {

	static final int PATH 			= 1;
	static final int COMPONENT 		= 2;
	static final int VERSION 		= 3;
	static final int LICENSE 		= 4;
	static final int USAGE 			= 5;		
	static final int STATUS 		= 6;
	static final int PERCENTAGE 	= 7;
	static final int MATCHED_FILE 	= 8;
	static final int COMMENT 		= 9;
	
	public static final int CODE_MATCH_TABLE_STATUS_PENDING		= 0;	
	public static final int CODE_MATCH_TABLE_STATUS_IDENTIFIED	= 1;
	public static final int CODE_MATCH_TABLE_STATUS_REJECT 		= 2;
	public static final int CODE_MATCH_TABLE_STATUS_DECLARED 	= 4;

	static StringBuffer sqlString = new StringBuffer();
	static String getQueryForCodematchlistByFile(String projectName, String filePath) {
		String table = "codematch_"+IdentificationDBManager.getProjectTablePostfix(projectName);
		
		sqlString.setLength(0);
		sqlString.append("SELECT * ");
		sqlString.append(" FROM ").append(table);
		sqlString.append(" WHERE path='").append(filePath).append("'");
		
		return sqlString.toString();
	}
	
	static String getQueryForCodeMatchlistByFolder(String projectName, String folderPath) {
		String table = "codematch_"+IdentificationDBManager.getProjectTablePostfix(projectName);
		
		StringBuffer sqlString = new StringBuffer();
		sqlString.append("SELECT * ");
		sqlString.append(" FROM ").append(table);
		sqlString.append(" WHERE path like '").append(folderPath).append("%'");
		
		return sqlString.toString();
	}
}
