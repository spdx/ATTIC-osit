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
package com.sec.ose.osi.ui.frm.main.identification.autoidentify;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.airs.domain.autoidentify.SPDXPackageDTO;

/**
 * SPDXPackageInfoParser
 * @author sjh.yoo, ytaek.kim, hankido.lee
 * 
 */
public class SPDXPackageInfoParser {
	private static Log log = LogFactory.getLog(SPDXPackageInfoParser.class);
	private static final String CreationInfoTag = "http://spdx.org/rdf/terms#CreationInfo"; 
	private static final String PackageNameTag = "<name>"; 
	
	public static SPDXPackageDTO getSPDXPackageInfo(String filePath) {
		SPDXPackageDTO tempSPDXPackageInfo = null;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String tmp = null;
			boolean fCreatorSection = false;
			boolean fPackageNameSection = false;
			while((tmp = br.readLine()) != null) {
				if(tmp.contains(SPDXPackageInfoParser.CreationInfoTag)) {
					fCreatorSection = true;
					if(tempSPDXPackageInfo == null) tempSPDXPackageInfo = new SPDXPackageDTO();
					while((tmp = br.readLine()) != null) {
						if(tmp.contains("Person:")) {
							tmp = tmp.substring(tmp.indexOf("Person:")+"Person: ".length(),tmp.indexOf("</creator>"));
							tempSPDXPackageInfo.setPerson(tmp);
						} else if(tmp.contains("Organization:")) {
							tmp = tmp.substring(tmp.indexOf("Organization:")+"Organization: ".length(),tmp.indexOf("</creator>"));
							tempSPDXPackageInfo.setOrganization(tmp);
						} else if(tmp.contains("<created>")) {
							tmp = tmp.substring(tmp.indexOf("<created>")+"<created>".length(),tmp.indexOf("</created>"));
							tempSPDXPackageInfo.setCreated(tmp);
						} else if(tmp.contains("</rdf:Description>")) {
							break;
						}
					}
				}
				if( (tmp != null) && (tmp.contains(SPDXPackageInfoParser.PackageNameTag)) ) {
					fPackageNameSection = true;
					if(tempSPDXPackageInfo == null) tempSPDXPackageInfo = new SPDXPackageDTO();
					tmp = tmp.substring(tmp.indexOf("<name>")+"<name>".length(),tmp.indexOf("</name>"));
					tempSPDXPackageInfo.setName(tmp);
				}
				if(fCreatorSection && fPackageNameSection) {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.debug(e);
				}
			}
		}
		
		
		return tempSPDXPackageInfo;
	}
}
