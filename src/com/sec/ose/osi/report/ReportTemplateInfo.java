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
package com.sec.ose.osi.report;

import java.io.File;

/**
 * ReportTemplateInfo
 * @author suhyun47.kim
 * 
 */
public class ReportTemplateInfo {
	
	private File mReportTemplate;

	public ReportTemplateInfo(
			String templateName, 
			File reportTemplate,
			boolean scanReportButton,
			boolean createFromExistedButton) {
		
		super();
		mReportTemplate = reportTemplate;
	}
	
	public File getReportTemplate() {
		return mReportTemplate;
	}

}
