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
package com.sec.ose.osi.ui.frm.main.identification.stringmatch.table;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.IdentifiedInfo;
import com.sec.ose.osi.data.match.StringMatchInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;

/**
 * TableModelForSMFile
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class TableModelForSMFile extends MatchedInfoTableModel {
	private static final long serialVersionUID = 5754431226924156725L;

	public static final int COL_STRING_SEARCH = 0;
	public static final int COL_COMPONENT_NAME = 1;
	public static final int COL_VERSION_NAME = 2;
	public static final int COL_LICENSE_NAME = 3;
	public static final int COL_STATUS = 4;
	public static final int COL_PENDING_HITS = 5;
	public static final int COL_IDENTIFIED_HITS = 6;
	public static final int NOM_OF_COL = 7;
	
	public static final String[] columnNames  = 
		{"Search","Component Name","Version","License","Status","Pending Hits","Identified Hits"};
	private int[] columnWidth = { 80, 80, 20, 80, 40, 10, 30};
	
	private FileSummary fileSummary = null;
	
	public TableModelForSMFile() {
		setType("StringSearchFileTableModel");
	}
	public TableModelForSMFile(FileSummary pFileSummary) {
		fileSummary = pFileSummary;
	}
	
	public FileSummary getStringSearchFile() {
		return fileSummary;
	}

	public void setStringSearchFile(FileSummary pFileSummary) {
		this.fileSummary = pFileSummary;
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
		return fileSummary.getStringMatchInfoListSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if((fileSummary == null || fileSummary.getStringMatchInfoListSize() <= 0)) return null;
		
		StringMatchInfo tmpStringSearchUnit = null;
		if(fileSummary != null) {
			tmpStringSearchUnit = fileSummary.getStringSearchUnit(rowIndex);
		}
		
		if(tmpStringSearchUnit == null) {
			return null;
		}
		
		IdentifiedInfo idInfo = tmpStringSearchUnit.getIdentifiedInfo();
		switch(columnIndex){
			case COL_STRING_SEARCH:
				return tmpStringSearchUnit.getStringSeachName();
			case COL_COMPONENT_NAME:
				return idInfo.getComponentName();
			case COL_VERSION_NAME:
				return idInfo.getVersionName();
			case COL_LICENSE_NAME:
				return idInfo.getLicenseName();
			case COL_STATUS:
				if(tmpStringSearchUnit.getStatus()==StringMatchInfo.STATUS_PENDING) {
					return "Pending";
				} else if(tmpStringSearchUnit.getStatus()==StringMatchInfo.STATUS_DECLARED) {
					return "Declared";
				} else {
					return "Identified";
				}
			case COL_PENDING_HITS:
				return tmpStringSearchUnit.getPendingHits();
			case COL_IDENTIFIED_HITS:
				return tmpStringSearchUnit.getIdentifiedHits();
		}
		return null;
	}
}
