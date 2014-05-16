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
package com.sec.ose.osi.report.standard.summary;

import java.util.TreeMap;

/**
 * StandardSummarySheetRow
 * @author suhyun47.kim
 * 
 */
public class StandardSummarySheetRow {

	public static final int PROJECT_NAME 				= 0;
	public static final int SCAN_DATE 					= 1;
	public static final int SCAN_DURATION 				= 2;
	public static final int PENDING_FILE_COUNT			= 3;
	public static final int PENDING_FILE_PERCENT		= 4;
	public static final int TOTAL_FILE_COUNT			= 5;
	public static final int SKIPPED_FILE_COUNT			= 6;
	public static final int TOTAL_BYTES					= 7;
	public static final int IDENTIFIED_FILE_COUNT		= 8;
	public static final int CURRENT_PENDING_FILE_COUNT 	= 9;
	public static final int NUM_OF_FIELDS				= 10;
	
	public String toString() {
		String result="";
		
		result = 
			"name " + values.get(PROJECT_NAME)
			+"\ndate: "+values.get(SCAN_DATE)
			+"\nduration: "+values.get(SCAN_DURATION)
			+"\npending count: "+values.get(PENDING_FILE_COUNT)
			+"\npending percent: "+values.get(PENDING_FILE_PERCENT)
			+"\ntotal count: "+values.get(TOTAL_FILE_COUNT)
			+"\nskipped: "+values.get(SKIPPED_FILE_COUNT)
			+"\nbytes: "+values.get(TOTAL_BYTES)
			+"\nidentified: "+values.get(IDENTIFIED_FILE_COUNT)
			+"\ncurrent pending count(identify percent): "+values.get(CURRENT_PENDING_FILE_COUNT);
		
		return result;
		
	}
	
	private TreeMap<Integer, String> values;
	
	public StandardSummarySheetRow() {
		values = new TreeMap<Integer, String>();
	}
	
	public void setValue(int key, String value) {
		this.values.put(key, value);
	}
	
	public String getValue(int key) {
		
		return values.get(key);
	}
}
