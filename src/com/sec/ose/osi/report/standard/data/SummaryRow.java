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
package com.sec.ose.osi.report.standard.data;

import java.text.DecimalFormat;


/**
 * SummaryData
 * @author sjh.yoo
 *
 */
public class SummaryRow {

	private String classification;
	private String projectName;
	private String scanDate;
	private long scanTime;	// scan duration
	private int pendingFileCount;
	private int totalFileCount;
	private int exceptionalFileCount;
	private long bytes;
	private int totalIdentifiedFileCount;
	private int currentPendingFileCount;

	/**
	 * @param classification
	 * @param projectName
	 * @param scanDate
	 * @param scanTime
	 * @param pendingFileCount
	 * @param totalFileCount
	 * @param exceptionalFileCount
	 * @param bytes
	 * @param totalIdentifiedFileCount
	 * @param currentPendingFileCount
	 */
	public SummaryRow(String classification, String projectName, 
			String scanDate, long scanTime, int pendingFileCount,
			int totalFileCount, int exceptionalFileCount, long bytes,
			int totalIdentifiedFileCount, int currentPendingFileCount) {
		super();
		this.classification = classification;
		this.projectName = projectName;
		this.scanDate = scanDate;
		this.scanTime = scanTime;
		this.pendingFileCount = pendingFileCount;
		this.totalFileCount = totalFileCount;
		this.exceptionalFileCount = exceptionalFileCount;
		this.bytes = bytes;
		this.totalIdentifiedFileCount = totalIdentifiedFileCount;
		this.currentPendingFileCount = currentPendingFileCount;
	}
	
	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}
	/**
	 * @param classification the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the scanDate
	 */
	public String getScanDate() {
		return scanDate;
	}
	/**
	 * @param scanDate the scanDate to set
	 */
	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}
	/**
	 * @return the scanTime
	 */
	public long getScanTime() {
		return scanTime;
	}
	/**
	 * @param scanTime the scanTime to set
	 */
	public void setScanTime(long scanTime) {
		this.scanTime = scanTime;
	}
	/**
	 * @return the pendingFileCount
	 */
	public int getPendingFileCount() {
		return pendingFileCount;
	}
	/**
	 * @param pendingFileCount the pendingFileCount to set
	 */
	public void setPendingFileCount(int pendingFileCount) {
		this.pendingFileCount = pendingFileCount;
	}
	/**
	 * @return the pendingPercent
	 */
	public String getPendingPercent() {
		DecimalFormat df = new DecimalFormat("##0.00%");
		return  df.format((double)pendingFileCount / (double)totalFileCount );
	}
	/**
	 * @return the totalFileCount
	 */
	public int getTotalFileCount() {
		return totalFileCount;
	}
	/**
	 * @param totalFileCount the totalFileCount to set
	 */
	public void setTotalFileCount(int totalFileCount) {
		this.totalFileCount = totalFileCount;
	}
	/**
	 * @return the exceptionalFileCount
	 */
	public int getExceptionalFileCount() {
		return exceptionalFileCount;
	}
	/**
	 * @param exceptionalFileCount the exceptionalFileCount to set
	 */
	public void setExceptionalFileCount(int exceptionalFileCount) {
		this.exceptionalFileCount = exceptionalFileCount;
	}
	/**
	 * @return the bytes
	 */
	public long getBytes() {
		return bytes;
	}
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	/**
	 * @return the totalIdentifiedFileCount
	 */
	public int getTotalIdentifiedFileCount() {
		return totalIdentifiedFileCount;
	}
	/**
	 * @param totalIdentifiedFileCount the totalIdentifiedFileCount to set
	 */
	public void setTotalIdentifiedFileCount(int totalIdentifiedFileCount) {
		this.totalIdentifiedFileCount = totalIdentifiedFileCount;
	}
	/**
	 * @return the currentPendingFileCount
	 */
	public int getCurrentPendingFileCount() {
		return currentPendingFileCount;
	}

	/**
	 * @param currentPendingFileCount the currentPendingFileCount to set
	 */
	public void setCurrentPendingFileCount(int currentPendingFileCount) {
		this.currentPendingFileCount = currentPendingFileCount;
	}
}
