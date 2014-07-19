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
package com.sec.ose.airs.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spdx.rdfparser.InvalidSPDXAnalysisException;
import org.spdx.rdfparser.SPDXDocument;
import org.spdx.rdfparser.SPDXDocumentFactory;
import org.spdx.rdfparser.SPDXLicense;
import org.spdx.rdfparser.SPDXLicenseInfo;
import org.spdx.rdfparser.SPDXLicenseSet;
import org.spdx.rdfparser.SPDXNonStandardLicense;
import org.spdx.rdfparser.SPDXReview;

import com.sec.ose.airs.domain.autoidentify.AutoIdentifyOptions;
import com.sec.ose.airs.domain.autoidentify.SPDXFileDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXOtherLicenseDTO;
import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;
import com.sec.ose.airs.persistence.SPDXMapper;
import com.sec.ose.airs.persistence.SPDXMapperImpl;
import com.sec.ose.airs.service.protex.ProtexIdentificationInfoService;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class AutoIdentifyService {
	private static Log log = LogFactory.getLog(AutoIdentifyService.class);

	SPDXMapper mapper = new SPDXMapperImpl();
	
	// before Spring, just let it be Protex ...
	// TODO - Use Spring!!
	IdentificationInfoService iiSvc = new ProtexIdentificationInfoService();
	
	
	public SPDXPackageDTO parseSPDXDocumentContent(String content, String encoding) throws IOException, InvalidSPDXAnalysisException {
		SPDXDocument doc = null;
		
		try {
			InputStream is = new ByteArrayInputStream(content.getBytes(encoding));
			doc = SPDXDocumentFactory.creatSpdxDocument(is, null, null);
			
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		} catch (InvalidSPDXAnalysisException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return this.convertParserFormatToAIRSDB(doc);
	}
	
	
	protected String concatArray(Object[] objs) {
		String tmp = "";
		for (Object obj : objs) {
			if (!"".equals(tmp))
				tmp += SPDXFileDTO.COLUMN_DELIMITER;
			tmp += obj.toString();
		}
		
		return tmp;
	}
	
	
	public SPDXPackageDTO convertParserFormatToAIRSDB(SPDXDocument spdxDocument) {
		SPDXPackageDTO pkg = new SPDXPackageDTO();

		try {
			org.spdx.rdfparser.SPDXDocument.SPDXPackage fromPkg = spdxDocument.getSpdxPackage();	
			
			// Package convert
			pkg.setName(fromPkg.getDeclaredName());
			pkg.setVersion(fromPkg.getVersionInfo());
			pkg.setFileName(fromPkg.getFileName());
			pkg.setSupplier(fromPkg.getSupplier());
			pkg.setOriginator(fromPkg.getOriginator());
			pkg.setVerificationCode(fromPkg.getVerificationCode().getValue());
			pkg.setChecksum(fromPkg.getSha1());
			pkg.setConcludedLicense(fromPkg.getConcludedLicenses().toString());
			pkg.setAllLicensesInfoFromFiles(concatArray(fromPkg.getPackageInfo().getLicensesFromFiles()));
			pkg.setDeclaredLicense(fromPkg.getDeclaredLicense().toString());
			
			for (SPDXReview review : spdxDocument.getReviewers()) {
				if (!"".equals(pkg.getReviewDate()))
					pkg.setReviewDate(pkg.getReviewDate() + SPDXFileDTO.COLUMN_DELIMITER);
				if (!"".equals(pkg.getReviewer()))
					pkg.setReviewer(pkg.getReviewer() + SPDXFileDTO.COLUMN_DELIMITER);
				pkg.setReviewDate(pkg.getReviewDate() + review.getReviewDate());
				pkg.setReviewer(pkg.getReviewer() + review.getReviewer());
			}
			
			//pkg.setCreator(concatArray(spdxDocument.getCreatorInfo().getCreators()));
			String[] creatorHeaders = {"Person: ", "Organization: ", "Tool: "};
			for (String creator : spdxDocument.getCreatorInfo().getCreators()) {
				if (creator.toLowerCase().contains(creatorHeaders[0].toLowerCase())) {
					pkg.setPerson(creator.substring(creatorHeaders[0].length()));
				} else if (creator.toLowerCase().contains(creatorHeaders[1].toLowerCase())) {
					pkg.setOrganization(creator.substring(creatorHeaders[1].length()));
				} else if (creator.toLowerCase().contains(creatorHeaders[2].toLowerCase())) {
					pkg.setTool(creator.substring(creatorHeaders[2].length()));
				}
			}
			
			pkg.setCreated(spdxDocument.getCreatorInfo().getCreated());
			pkg.setSpdxVersion(spdxDocument.getSpdxVersion());
			
			// Files convert
			for (org.spdx.rdfparser.SPDXFile fromFile : fromPkg.getFiles()) {
				SPDXFileDTO file = new SPDXFileDTO(); 
				file.setName(fromFile.getName());
				file.setType(fromFile.getType());
				file.setChecksum(fromFile.getSha1());
				file.setComment(fromFile.getLicenseComments());
				
				// If there are other licenses, insert db.
				SPDXLicenseInfo licInfo = fromFile.getConcludedLicenses();
				// SPDXLicenseInfo type checking
				if (licInfo instanceof SPDXLicenseSet) {
					for (SPDXLicenseInfo info : ((SPDXLicenseSet)licInfo).getSPDXLicenseInfos())
						this.insertOtherLicenseIfNonStandardLicense(info, pkg);
				} else if (licInfo instanceof SPDXLicense) {
					this.insertOtherLicenseIfNonStandardLicense(licInfo, pkg);
				}
				
				file.setConcludedLicense(fromFile.getConcludedLicenses().toString());
				file.setComment(fromFile.getComment());
				
				pkg.getFileList().add(file);
			}

		} catch (InvalidSPDXAnalysisException e) {
			log.error(e.getMessage());
		}
		
		return pkg;
	}
	
	
	protected void insertOtherLicenseIfNonStandardLicense(SPDXLicenseInfo info, SPDXPackageDTO pkg) {
		if (info instanceof SPDXNonStandardLicense) {
			SPDXNonStandardLicense infoLic = (SPDXNonStandardLicense)info;
			
			// duplication check by ID
			for (SPDXOtherLicenseDTO lic : pkg.getOtherLicenseList()) {
				if (lic.getLicenseId().equals(infoLic.getId()))
					return;
			}
			
			SPDXOtherLicenseDTO lic = new SPDXOtherLicenseDTO();
			lic.setLicenseId(infoLic.getId());
			lic.setName(infoLic.getLicenseName());
			lic.setComment(infoLic.getComment());

			String extractedText = infoLic.getText();
			
			//extractedText狼 蔼捞 null老版快 贸府
			if("".equals(extractedText) || extractedText == null){
				lic.setExtractedText("");
			}else{
				lic.setExtractedText(extractedText);
			}
			
			pkg.getOtherLicenseList().add(lic);
		}
	}
	
	
	public int insertSPDXPackageData(SPDXPackageDTO pkg) {
		// TODO - TRANSACTION
		mapper.insertPackage(pkg);
		for (SPDXFileDTO file : pkg.getFileList()) {
			file.setPackageId(pkg.getId());
			mapper.insertFile(file);
		}
		for (SPDXOtherLicenseDTO lic : pkg.getOtherLicenseList()) {
			lic.setPackageId(pkg.getId());
			mapper.insertOtherLicense(lic);
		}
		
		return pkg.getId();
	}
	
	public int insertSPDXPackageDataOnlyHavingIdentificationInfo(SPDXPackageDTO pkg) {
		// TODO - TRANSACTION
		mapper.insertPackage(pkg);
		for (SPDXFileDTO file : pkg.getFileList()) {
			file.setPackageId(pkg.getId());
			
			if (iiSvc.hasIdentificationInfo(file)) {
				mapper.insertFile(file);
			}
			
		}
		for (SPDXOtherLicenseDTO lic : pkg.getOtherLicenseList()) {
			lic.setPackageId(pkg.getId());
			mapper.insertOtherLicense(lic);
		}
		
		return pkg.getId();
	}
	
	
	public List<SPDXPackageDTO> getAllPackageList() {
		return mapper.getPackageList();
	}
	
	
	public SPDXPackageDTO getPackageDetail(int id) {
		SPDXPackageDTO pkg = mapper.getPackage(id);
		pkg.setFileList(mapper.getFileList(id));
		pkg.setOtherLicenseList(mapper.getOtherLicenseList(id));
		
		return pkg;
	}
	
	public void deleteSPDXPackage(int packageId) {
		mapper.deletePackageAndFilesAndOtherLicensces(packageId);
	}
	
	
	public List<SPDXFileDTO> getFileListByComparingHashCodeAndIdentificaionInfo(List<Integer> srcPkgIdList, SPDXFileDTO targetFile, AutoIdentifyOptions opt) {
		if (opt.isOverwrite() == true) {
			if (targetFile.getIdentificationInfoList() != null && targetFile.getIdentificationInfoList().size() > 0) {
				return new ArrayList<SPDXFileDTO>();
			}
		}
		
		List<SPDXFileDTO> sameFileList = mapper.getSameFileWithChecksum(srcPkgIdList, targetFile.getChecksum());

		// ONLY ONE MATCHED
		if (sameFileList.size() == 1) {
			return sameFileList;
		
		// multiple files matched.
		} else if (sameFileList.size() > 1) {
			//////////////////////////////////////////////////
			// All files have same identify info, then AI.
			if (iiSvc.hasIdentificationInfo(sameFileList.get(0))) {
				boolean allSame = false;
				
				for (SPDXFileDTO file : sameFileList.subList(1, sameFileList.size())) {
					if (iiSvc.hasSameIdentificationInfoList(sameFileList.get(0), file)) {
						allSame = true;
					} else {
						allSame = false;
						break;
					}
				}
				
				if (allSame) {
					for(int i=sameFileList.size()-1;i>0;i--) {
						sameFileList.remove(i);
					}
					return sameFileList;
				}
			}
			
			//////////////////////////////////////////////////
			// same File Path and there's only one, then AI
			// only works in PHASE 2.
			if (opt.isIdentifyWhenExistingOnlyOneSamePathFile()) {
				int sameFilePathCount = 0;
				for (SPDXFileDTO file : sameFileList) {
					if (targetFile.getName().equals(file.getName())) {
						sameFilePathCount++;
					}
				}
				if (sameFilePathCount == 1) {
					for(int i=sameFileList.size()-1;i>0;i--) {
						sameFileList.remove(i);
					}
//					sameFileList = new ArrayList<SPDXFile>();
//					sameFileList.add(sameFile);

					return sameFileList;
				}
			}
			
			//////////////////////////////////////////////////
			// Recent file path, then AI
			// TODO - Deprecated
//			if (opt.isSelectedRecentInfo()) {
//				sameFileList = new ArrayList<SPDXFile>();
//				sameFileList.add(sameFileList.get(0));
//				
//				return sameFileList;
//			}			
		}
		
		return sameFileList;
	}
	
}
