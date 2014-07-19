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
package com.sec.ose.airs.domain.autoidentify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class SPDXPackageDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	int id;
	String name;
	String version;
	String fileName;
	String supplier;
	String originator;
	String verificationCode;
	String checksum;
	String concludedLicense;
	String allLicensesInfoFromFiles;
	String declaredLicense;
	String reviewer;
	String reviewDate;
	String created;
	String spdxVersion;
	String sourceLocation;
	String person;
	String organization;
	String tool;
	
	List<SPDXOtherLicenseDTO> otherLicenseList;
	List<SPDXFileDTO> fileList;
	
	public SPDXPackageDTO() {
		otherLicenseList = new ArrayList<SPDXOtherLicenseDTO>();
		fileList = new ArrayList<SPDXFileDTO>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getConcludedLicense() {
		return concludedLicense;
	}
	public void setConcludedLicense(String concludedLicense) {
		this.concludedLicense = concludedLicense;
	}
	public String getAllLicensesInfoFromFiles() {
		return allLicensesInfoFromFiles;
	}
	public void setAllLicensesInfoFromFiles(String allLicensesInfoFromFiles) {
		this.allLicensesInfoFromFiles = allLicensesInfoFromFiles;
	}
	public String getDeclaredLicense() {
		return declaredLicense;
	}
	public void setDeclaredLicense(String declaredLicense) {
		this.declaredLicense = declaredLicense;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getSpdxVersion() {
		return spdxVersion;
	}
	public void setSpdxVersion(String spdxVersion) {
		this.spdxVersion = spdxVersion;
	}
	public List<SPDXFileDTO> getFileList() {
		return fileList;
	}
	public void setFileList(List<SPDXFileDTO> fileList) {
		this.fileList = fileList;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<SPDXOtherLicenseDTO> getOtherLicenseList() {
		return otherLicenseList;
	}
	public void setOtherLicenseList(List<SPDXOtherLicenseDTO> otherLicenseList) {
		this.otherLicenseList = otherLicenseList;
	}
	public String getSourceLocation() {
		return sourceLocation;
	}
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getTool() {
		return tool;
	}
	public void setTool(String tool) {
		this.tool = tool;
	}
}
