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

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXOtherLicenseDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class SPDXMapperImpl implements SPDXMapper {
	SqlSessionFactory factory = AIRSSessionFactory.getInstance();
	
	@Override
	public int insertPackage(SPDXPackageDTO pkg) {
		SqlSession session = factory.openSession(true);
		int result;
		
		try {
			result = session.insert("insertPackage", pkg);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public int insertFile(SPDXFileDTO file) {
		SqlSession session = factory.openSession(true);
		int result;
		
		try {
			result = session.insert("insertFile", file);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public int insertOtherLicense(SPDXOtherLicenseDTO lic) {
		SqlSession session = factory.openSession(true);
		int result;
		
		try {
			result = session.insert("insertOtherLicense", lic);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public List<SPDXPackageDTO> getPackageList() {
		SqlSession session = factory.openSession(true);
		List<SPDXPackageDTO> result;
		
		try {
			result = session.selectList("getPackageList");
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public List<SPDXFileDTO> getFileList(int packageId) {
		SqlSession session = factory.openSession(true);
		List<SPDXFileDTO> result;
		
		try {
			result = session.selectList("getFileList", packageId);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public List<SPDXOtherLicenseDTO> getOtherLicenseList(int packageId) {
		SqlSession session = factory.openSession(true);
		List<SPDXOtherLicenseDTO> result;
		
		try {
			result = session.selectList("getOtherLicenseList", packageId);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public SPDXPackageDTO getPackage(int id) {
		SqlSession session = factory.openSession(true);
		SPDXPackageDTO result;
		
		try {
			result = session.selectOne("getPackage", id);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public int deletePackageAndFilesAndOtherLicensces(int id) {
		// TODO cascade 걸어야함!!!!
		SqlSession session = factory.openSession(true);
		int result;
		
		try {
			result = session.delete("deletePackageAndFilesAndOtherLicensces", id);
		} finally {
			session.close();
		}
		
		return result;
	}

	@Override
	public List<SPDXFileDTO> getSameFileWithChecksum(List<Integer> packageIdList,
			String checksum) {
		SqlSession session = factory.openSession(true);
		List<SPDXFileDTO> fileList = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("packageIdList", packageIdList);
		map.put("checksum", checksum);
		
		try {
			fileList = session.selectList("getSameFileWithChecksum", map);
		} finally {
			session.close();
		}
		
		return fileList;
	}
}
