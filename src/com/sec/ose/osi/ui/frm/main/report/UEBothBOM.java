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
import java.util.HashMap;
import java.util.Iterator;

import com.blackducksoftware.sdk.protex.report.SpdxReportConfiguration;
import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UEBothBOM
 * @author sjh.yoo, hankido.lee
 * 
 */
public class UEBothBOM implements UIEntity {
	private static final long serialVersionUID = -6145647753903097051L;
	
	// Excel
	private HashMap<String, BOMTableModel> bomTableModels = null;
	private boolean optAllFileListUpSelected;

	private String srcFileName = null;
	private String targetFileName = null;
	private boolean insertCodeMatch = false;

	// SPDX
	private ArrayList<String> projectList = new ArrayList<String>(1);
	private ArrayList<String> targetSDPXReportList = new ArrayList<String>(1);
	private boolean bOverwrite = false;
	private SpdxReportConfiguration spdxReportConfiguration = null;

	private String creatorName = null;
	private String creatorEmail = null;
	private String organizationName = null;

	public UEBothBOM(
			HashMap<String, BOMTableModel> boms,
			boolean optAllFileListUpSelected
			) {

		this.bomTableModels = boms;
		this.optAllFileListUpSelected = optAllFileListUpSelected;
	}
	
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
	public void setSpdxReportConfiguration(SpdxReportConfiguration spdxReportConfiguration) {
		this.spdxReportConfiguration = spdxReportConfiguration;
	}
	public SpdxReportConfiguration getSpdxReportConfiguration() {
		return spdxReportConfiguration;
	}

	public boolean isInsertCodeMatch() {
		return insertCodeMatch;
	}

	public void setInsertCodeMatch(boolean insertCodeMatche) {
		this.insertCodeMatch = insertCodeMatche;
	}
	
	public String getSrcFileName() {
		return srcFileName;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}
	
	public boolean isOverwrite() {
		return bOverwrite;
	}
	
	public void setOverwrite(boolean overwrite) {
		this.bOverwrite = overwrite;
	}

	public ArrayList<String> getFileListUpComponentNames(String projectName) {
		
		if(optAllFileListUpSelected == true)
			return null;
		
		ArrayList<String> fileListUpComponentNames = new ArrayList<String>();
		BOMTableModel model = bomTableModels.get(projectName);
		
		if(model == null)
			return null;
		
		Iterator<BOMTableRow> rows = model.getRows();
		if(rows == null)
			return null;

		while(rows.hasNext()) {
			BOMTableRow row = rows.next();
			if(row.isChecked() == true) {

				fileListUpComponentNames.add(row.getComponentName());
			}
		}
		
		return fileListUpComponentNames;
	}

	public Iterator<String> getSelectedProjects() {
		
		return bomTableModels.keySet().iterator();
	}

	public boolean isOptAllFilesSelected() {
		return this.optAllFileListUpSelected;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
}
