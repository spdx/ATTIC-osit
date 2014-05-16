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
package com.sec.ose.osi.ui.frm.main.identification.codematch.table;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.CodeMatchInfo;
import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.IdentifiedInfo;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;

/**
 * TableModelForCMFile
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class TableModelForCMFile extends MatchedInfoTableModel {
	private static final long serialVersionUID = 1159440560147099736L;
	
	public static final int COL_COMPONENT_NAME = 0;
	public static final int COL_VERSION_NAME = 1;
	public static final int COL_LICENSE_NAME = 2;
	public static final int COL_USAGE = 3;
	public static final int COL_STATUS = 4;
	public static final int COL_PERCENTAGE = 5;
	public static final int COL_MATCHED_FILE = 6;
	public static final int NUM_OF_COL = 7;
	
	private static final String[] columnNames  = 
		{"Component","Version","License","Usage","Status","%","Matched File"};
	private int[] columnWidth = { 80, 20, 80, 40, 40, 10, 100};
	
	private FileSummary fileSummary = null;

	public TableModelForCMFile() {
		setType("CodeMatchFileTableModel");
	}

	public void setCodeMatchFile(FileSummary pFileSummary) {
		this.fileSummary = pFileSummary;
	}
	
	public void setCodeMatchFile(MultipleFileSummary pMultipleFileSummary) {
		this.fileSummary = null;
	}
	
	public TableModelForCMFile(FileSummary pFileSummary) {
		fileSummary = pFileSummary;
		setType("CodeMatchFileTableModel");
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    public void setColumnWidth(JTable table){
    	TableColumnModel tcm = table.getColumnModel();
    	for(int i=0; i<columnWidth.length; i++){
    		tcm.getColumn(i).setPreferredWidth(columnWidth[i]);
    	}
    }
	@Override
	public int getRowCount() {
		if(fileSummary == null) return 0;
		return fileSummary.getCodeMatchInfoListSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( (fileSummary == null || fileSummary.getCodeMatchInfoListSize()<=0) ) {
			return null;
		}
		
		CodeMatchInfo tmpCodeMatchUnit = null;
		tmpCodeMatchUnit = fileSummary.getCodeMatchFileUnit(rowIndex);
		
		if(tmpCodeMatchUnit == null)
			return null;
		
		IdentifiedInfo newIdentifiedInfo = tmpCodeMatchUnit.getIdentifiedInfo();
			switch(columnIndex){
				case COL_COMPONENT_NAME:
					if(tmpCodeMatchUnit.getComponentName() != null && newIdentifiedInfo != null && !tmpCodeMatchUnit.getComponentName().equals(newIdentifiedInfo.getComponentName())) {
						return newIdentifiedInfo.getComponentName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getComponentName() + DCCodeMatch.END_ORIGIN; 
					}
					return tmpCodeMatchUnit.getComponentName();
				case COL_VERSION_NAME:
					if(tmpCodeMatchUnit.getVersionName() != null && newIdentifiedInfo != null && !tmpCodeMatchUnit.getVersionName().equals(newIdentifiedInfo.getVersionName())) {
						if(tmpCodeMatchUnit.getVersionName().length() == 0) return "Unspecified" + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getVersionName() + DCCodeMatch.END_ORIGIN;
						return newIdentifiedInfo.getVersionName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getVersionName() + DCCodeMatch.END_ORIGIN; 
					}
					if(tmpCodeMatchUnit.getVersionName().length() == 0) return "Unspecified";
					return tmpCodeMatchUnit.getVersionName();
				case COL_LICENSE_NAME:
					if(tmpCodeMatchUnit.getLicenseName() != null && newIdentifiedInfo != null && !tmpCodeMatchUnit.getLicenseName().equals(newIdentifiedInfo.getLicenseName())) {
						return newIdentifiedInfo.getLicenseName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getLicenseName() + DCCodeMatch.END_ORIGIN; 
					}
					return tmpCodeMatchUnit.getLicenseName();
				case COL_USAGE:
					return CodeMatchInfo.MATCED_INFO_USAGE_FILE.get(tmpCodeMatchUnit.getUsage());
				case COL_STATUS:
					return AbstractMatchInfo.MATCED_INFO_STATUS_MAP.get(tmpCodeMatchUnit.getStatus());
				case COL_PERCENTAGE:
					return tmpCodeMatchUnit.getPercentage();
				case COL_MATCHED_FILE:
					return tmpCodeMatchUnit.getMatchedFile();
			}
		return null;
	}
}
