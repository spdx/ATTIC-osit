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
package com.sec.ose.osi.ui.frm.main.manage;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DirectoryInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class DirectoryInfo {
	private static Log log = LogFactory.getLog(DirectoryInfo.class);
	
	private File sourcePath = null;
    private long totalSize = 0L;
    private long limit = 0L;
    private int totalFileCount = -1;
	
	public DirectoryInfo(String path) {
		sourcePath = new File(path);
	}
	
	public long getTotalFileSize() {
		totalSize = 0L;
		if(sourcePath.exists()) {
			sumDirSize(sourcePath);
		}
		return totalSize;
	}

	private void sumDirSize(File file)
	{
		if(file == null || !file.exists()) {
			return;
		}

		if(file.isDirectory()) {
			String[] children = file.list();
			if(children != null) {
				for(String child:children) {
					sumDirSize(new File(file, child));
				}
			}
		} else {
			  totalSize += (long)file.length();
		}
	}

	public boolean checkFileSize(long pLimit) {
		this.limit = pLimit;
		checkDirSize(sourcePath);
		return (totalSize < limit);
	}

	private void checkDirSize(File file)
	{
		if(totalSize > limit) {
			return;
		} else {
			if(file.isDirectory()) {
				String[] children = file.list();
				if(children != null) {
					for(String child:children) {
						checkDirSize(new File(file, child));
					}
				}
			} else {
				totalSize += (long)file.length();
			}
		}
	}

	long baseDate = 0;
	public boolean isFileModified(long pBaseDate) {
		totalFileCount = -1;
		log.debug(pBaseDate);
		this.baseDate = pBaseDate;
		if(sourcePath.exists()) {
			checkDirModified(sourcePath);
		}
		return isModify;
	}

	boolean isModify = false;
	private void checkDirModified(File file)
	{
		totalFileCount++;
		if(file.lastModified() > baseDate) {
			isModify = true;
		}
		if(file.isDirectory()) {
			String[] children = file.list();
			if(children != null) {
				for(String child:children) {
					checkDirModified(new File(file, child));
				}
			}
		}
	}
	
	public int getFileCount() {
		return totalFileCount;
	}
}
