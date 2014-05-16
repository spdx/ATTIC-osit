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

import com.sec.ose.osi.util.Property;

/**
 * ReportTemplateMgr
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class ReportTemplateMgr {	
	private static final long serialVersionUID = 1L;
	public static final String STANDARD_TEMPLATE = "Standard";
	private static ReportTemplateInfo templateInfo = null;
	
	private volatile static ReportTemplateMgr instance = null;
	
	public static ReportTemplateMgr getInstance() {
		if(instance == null) {
			synchronized(ReportTemplateMgr.class) {
				if(instance == null) {
					instance = new ReportTemplateMgr();
				}
			}
		}
		return instance;
	}
	
	private static final String FOLDER_NAME = "./"+Property.FOLDER_TEMPLATE+"/";
	
	private ReportTemplateMgr() {
		File f = new File(FOLDER_NAME+"TEMPLATE_STANDARD.xls");
		if(f!= null && f.exists() == true) {
			templateInfo = new ReportTemplateInfo(
												STANDARD_TEMPLATE,
												f,
												true,
												true);
		}
	}
	
	public ReportTemplateInfo getReportTemplate() {
		return templateInfo;
	}
}
