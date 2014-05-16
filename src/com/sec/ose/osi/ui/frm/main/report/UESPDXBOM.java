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
package com.sec.ose.osi.ui.frm.main.report;

import java.util.ArrayList;
import java.util.Collection;

import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UESPDXBOM
 * @author sjh.yoo
 * 
 */
public class UESPDXBOM implements UIEntity {
	
	private static final long serialVersionUID = 4678486311421216016L;

	private ArrayList<String> projectList = new ArrayList<String>(1);
	private ArrayList<String> targetSDPXReportList = new ArrayList<String>(1);
	private boolean bOverwrite = false;
	private SpdxReportConfiguration spdxReportConfiguration = null;
	
	public ArrayList<String> getTargetSDPXReportFileList() {
		return targetSDPXReportList;
	}
	public void setTargetSDPXReportFileList(Collection<String> reportFileList) {
		targetSDPXReportList = (ArrayList<String>)reportFileList;
	}
	public ArrayList<String> getProjectList() {
		return projectList;
	}
	public void setProjectList(Collection<String> selectedProjectList) {
		projectList = (ArrayList<String>)selectedProjectList;
	}
	public boolean isOverwrite() {
		return bOverwrite;
	}
	public void setOverwrite(boolean bOverwrite) {
		this.bOverwrite = bOverwrite;
	}
	public void setSpdxReportConfiguration(SpdxReportConfiguration spdxReportConfiguration) {
		this.spdxReportConfiguration = spdxReportConfiguration;
	}
	public SpdxReportConfiguration getSpdxReportConfiguration() {
		return spdxReportConfiguration;
	}
	
}
