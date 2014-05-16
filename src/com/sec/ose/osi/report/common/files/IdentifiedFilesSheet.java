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
package com.sec.ose.osi.report.common.files;

import java.util.ArrayList;

/**
 * IdentifiedFilesSheet
 * @author suhyun47.kim 
 * 
 */
public class IdentifiedFilesSheet {

	ArrayList<IdentifiedFilesSheetRow> rows = new ArrayList<IdentifiedFilesSheetRow>();
	
	public ArrayList<IdentifiedFilesSheetRow> getRows() {
		
		return rows;
	}

	public void addRow(IdentifiedFilesSheetRow row) {
		this.rows.add(row);
		
	}

	public int getNumOfRows() {
		
		if(rows != null)
			return rows.size();
		
		return 0;
	}
}
