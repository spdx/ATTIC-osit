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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;

/**
 * BOMTableRow
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class BOMTableRow implements Serializable {
	private static Log log = LogFactory.getLog(BOMTableRow.class);
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_CHECKED_FILECNT = 10;
	private boolean checked;
	private String componentName;
	private String componentLicense;
	private int fileCount;
	
	protected BOMTableRow(String componentName, String componentLicense, int fileCount) {
		super();
		this.componentName = componentName;
		this.componentLicense = componentLicense;
		this.fileCount = fileCount;
		
		if(this.componentName == null)
			this.componentName = "";
		
		if(this.componentLicense == null)
			this.componentLicense="";
		
		int fileCnt = 0;
		try {
			fileCnt = this.fileCount;
			
		}catch(NumberFormatException ex) {
			log.warn(ex.getMessage());
			fileCnt=0;
		}

		if(fileCnt >= DEFAULT_CHECKED_FILECNT)
			setChecked(true);
	}
	
	public BOMTableRow(BOMEnt curEnt) {
		
		this(curEnt.getComponentName(),
				curEnt.getLicense(),
				curEnt.getCount()
				);
	}

	public boolean isChecked() {
		return checked;
	}

	public String getComponentLicense() {
		return componentLicense;
	}

	public String getComponentName() {
		return componentName;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
