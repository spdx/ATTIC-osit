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

/**
 * StandardIdentifyReportSheet
 * @author suhyun47.kim
 * 
 */
public class StandardIdentifyReportSheet {

	private String projectName;
	private ArrayList<StandardIdentifyReportSheetRow> mIdentifiedRows;
	private ArrayList<StandardIdentifyReportSheetRow> mStringSearchOnlyRows;
	private StandardLicenseSummary licenseSummary;
	
	protected StandardIdentifyReportSheet(
			String projectName, 
			ArrayList<StandardIdentifyReportSheetRow> identifiedRows, 
			ArrayList<StandardIdentifyReportSheetRow> stringSearchOnlyRows,
			StandardLicenseSummary licenseSummary) {
		super();
		this.projectName = projectName;
		this.mIdentifiedRows = identifiedRows;
		this.mStringSearchOnlyRows = stringSearchOnlyRows;
		this.licenseSummary = licenseSummary;
	}
	
	public StandardLicenseSummary getLicenseSummary() {
		return licenseSummary;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public ArrayList<StandardIdentifyReportSheetRow> getIdentifiedRows() {
		return mIdentifiedRows;
	}
	
	public ArrayList<StandardIdentifyReportSheetRow> getStringSearchOnlyRows() {
		return mStringSearchOnlyRows;
	}
	
}
