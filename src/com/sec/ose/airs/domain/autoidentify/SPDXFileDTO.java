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
import java.util.List;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class SPDXFileDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COLUMN_DELIMITER = "\n";
	
	int packageId;
	String name;
	String type;
	String checksum;
	String concludedLicense;
	String comment;
	List<IdentificationInfo> identificationInfoList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getPackageId() {
		return packageId;
	}
	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
	public List<IdentificationInfo> getIdentificationInfoList() {
		return identificationInfoList;
	}
	public void setIdentificationInfoList(List<IdentificationInfo> identificationInfoList) {
		this.identificationInfoList = identificationInfoList;
	}
}
