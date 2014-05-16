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
package com.sec.ose.osi.thread.job.identify.data;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.Identification;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;

/**
 * CommonIdentify
 * @author sjh.yoo, hankido.lee
 * 
 */
public abstract class CommonIdentify {
	private static Log log = LogFactory.getLog(CommonIdentify.class);
	
	protected UsageLevel identifiedUsageLevel = UsageLevel.FILE;
	protected CodeTreeNode tmpCodeTreeNode = new CodeTreeNode();
	protected PartialCodeTree fileOnlyTree = new PartialCodeTree();

	protected IdentifyData identifyData = null;
	protected String projectName = null;
	protected String projectID = null;
	
	protected boolean bFile = false;
	protected String curFilePath = null;
	protected String comment = null;
	protected String currentComponentName = null;
	protected String currentComponentID = null;
	protected String currentVersionID = null;
	
	protected String newComponentName = null;
	protected String newComponentID = null;
	protected String newVersionID = null;
	protected LicenseInfo identifiedLicenseInfo = null;

	public void setBase() {
		/** Common **/
        fileOnlyTree.setParentPath("/");
        fileOnlyTree.getNodes().add(tmpCodeTreeNode);
    	tmpCodeTreeNode.setNodeType(CodeTreeNodeType.FILE);
	}
	
	public void setPath(String curFilePath) {
		this.curFilePath  = curFilePath;
    	tmpCodeTreeNode.setName(curFilePath);
	}

	public String getInfo() {
		return "Unknown";
	}
	
	public void updateComment() throws SdkFault {
    	try {
    		if(curFilePath.length() <= 0) {
    			ProtexSDKAPIManager.getCodeTreeAPI().updateFileOrFolderComment(projectID, "/", comment);
    		} else {
    			ProtexSDKAPIManager.getCodeTreeAPI().updateFileOrFolderComment(projectID, curFilePath, comment);
    		}
		} catch (SdkFault e) {
			log.warn("[ERROR] Can't update comment");
			throw e;
		}
	}
	
	public void setCurrentComponentID(String currentComponentID) {
		this.currentComponentID = currentComponentID;
	}

	public void setCurrentVersionID(String currentVersionID) {
		this.currentVersionID = currentVersionID;
	}
	
	abstract public void setNewComponent();
	
	abstract public void identify() throws SdkFault;
	abstract public void reset(Identification id) throws SdkFault;
	abstract public void updateDBnUI(String action) throws SdkFault;
	public List<CodeTreeIdentificationInfo> getIdentifications() throws SdkFault {
        return ProtexSDKAPIManager.getIdentificationAPI().getAppliedIdentifications(projectID,fileOnlyTree);
	}
}
