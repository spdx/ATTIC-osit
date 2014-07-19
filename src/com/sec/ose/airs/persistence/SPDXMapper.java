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
package com.sec.ose.airs.persistence;

import java.util.List;

import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXOtherLicenseDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;

/**
 * 
 * 
 * @author ytaek.kim
 */
public interface SPDXMapper {
	public int insertPackage(SPDXPackageDTO pkg);
	
	public int insertFile(SPDXFileDTO file);
	public int insertOtherLicense(SPDXOtherLicenseDTO lic);
	
	public List<SPDXPackageDTO> getPackageList();
	public List<SPDXFileDTO> getFileList(int packageId);
	public List<SPDXOtherLicenseDTO> getOtherLicenseList(int packageId);
	
	public SPDXPackageDTO getPackage(int id);
	public int deletePackageAndFilesAndOtherLicensces(int id);
	
	public List<SPDXFileDTO> getSameFileWithChecksum(List<Integer> packageIdList, String checksum);
}
